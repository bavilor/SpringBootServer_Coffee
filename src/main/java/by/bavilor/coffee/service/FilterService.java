package by.bavilor.coffee.service;

import by.bavilor.coffee.controller.CryptoController;
import by.bavilor.coffee.entity.Order;
import by.bavilor.coffee.entity.User;
import by.bavilor.coffee.filter.RequestWrapper;
import by.bavilor.coffee.filter.ResponseWrapper;
import com.google.gson.Gson;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bosak on 6/1/2018.
 */
@Component
public class FilterService {

    @Autowired
    private CryptoController cryptoController;
    @Autowired
    private UserService userService;

    private final int RSA_BLOCK_SIZE = 256;

    public FilterService(){}

    //Return request wrapper
    public RequestWrapper getRequestWrapper(HttpServletRequest request, byte[] decrDataBytes){
        return new RequestWrapper(request,decrDataBytes);
    }

    //Return response wrapper
    public ResponseWrapper getResponseWrapper(HttpServletResponse response) throws Exception{
        return new ResponseWrapper(response);
    }

    //Restore user public key
    public PublicKey decodeUPK(String UPKString) throws Exception{
        byte[] UPKb64;
        try{
            UPKb64 = new Gson().fromJson(UPKString, byte[].class);
        }catch (Exception e){
            UPKb64 = (new Gson().fromJson(UPKString, String.class)).getBytes();
        }

        byte[] UPKbytes = Base64.decode(UPKb64);

        return cryptoController.restorePublicKey(UPKbytes);
    }

    //Form response
    public byte[] returnGETResponse(PublicKey publicKey, byte[] productListBytes) throws Exception{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        SecretKey secretKey = cryptoController.getSecretKey();
        byte[] iv = cryptoController.getIV();

        byte[] encrProductListBytes = cryptoController.encryptData(productListBytes, secretKey, iv);
        byte[] wrappedSecretKey = cryptoController.encryptSecretKey(publicKey, secretKey);
        byte[] encrIV = cryptoController.encryptIV(publicKey, iv);

        outputStream.write(wrappedSecretKey);
        outputStream.write(encrIV);
        outputStream.write(encrProductListBytes);

        byte[] b64data = Base64.encode(outputStream.toByteArray());
        outputStream.close();
        return b64data;
    }

    //Base64 encode server public key bytes
    public byte[] encodeServerPublicKey(byte[] byteServerPublicKey){
        return Base64.encode(byteServerPublicKey);
    }

    //Read encrypted data
    public byte[] readEncrData(HttpServletRequest request) throws Exception{
        InputStream is = request.getInputStream();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return buffer.toByteArray();
    }

    //Decrypt data
    public byte[] decryptData(byte[] b64Data) throws Exception{
        byte[] data = Base64.decode(b64Data);

        byte[] encrAES = getByteArray(0, RSA_BLOCK_SIZE, data);
        byte[] encrIV = getByteArray(RSA_BLOCK_SIZE, RSA_BLOCK_SIZE * 2, data);
        byte[] encrData = getByteArray(RSA_BLOCK_SIZE * 2, data.length, data);

        SecretKey secretKey = cryptoController.restoreSecretKey(encrAES);
        byte[] iv = cryptoController.decryptIV(encrIV);
        return cryptoController.decryptOrder(encrData, secretKey, iv);
    }

    //Create new user
    public int createUser(PublicKey publicKey){
        return userService.createUser(publicKey);
    }

    //Get all orders
    public byte[] getAllOrders(PublicKey freshPublicKey, byte[] ordersBytes) throws Exception{
        List<User> users = userService.getAllUsers();
        List<Order> orders;
        Map<String, List<Order>> map = new Gson().fromJson(getStringFromBytes(ordersBytes), Map.class);
        Gson g = new Gson();
        PublicKey publicKey;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for(User u : users) {
            orders = map.get(u.getPublicRSAKey());

            publicKey = cryptoController.restorePublicKey(
                    Base64.decode(
                            u.getPublicRSAKey()));

            SecretKey secretKey = cryptoController.getSecretKey();
            byte[] iv = cryptoController.getIV();

            byte[] encrOrdersBytes = cryptoController.encryptData(g.toJson(orders).getBytes(), secretKey, iv);
            byte[] length = Integer.toString(encrOrdersBytes.length).getBytes();
            byte[] wrappedSecretKey = cryptoController.encryptSecretKey(publicKey, secretKey);
            byte[] encrIV = cryptoController.encryptIV(publicKey, iv);
            byte[] encrLength = cryptoController.encryptIV(freshPublicKey, length);

            outputStream.write(wrappedSecretKey);
            outputStream.write(encrIV);
            outputStream.write(encrLength);
            outputStream.write(encrOrdersBytes);
        }
        byte[] b64data = Base64.encode(outputStream.toByteArray());
        outputStream.close();
        return b64data;
    }

    //Decrypt data with sign
    public byte[] decryptDataWithSign(byte[] encrb64Data, PublicKey publicKey, PublicKey pssPublicKey) throws Exception{
        byte[] encrData = Base64.decode(getStringFromBytes(encrb64Data));

        byte[] sign = getByteArray(encrData.length - 256, encrData.length, encrData);

        PublicKey signKey;
        if(pssPublicKey == null){
            signKey = publicKey;
        }else{
            signKey = pssPublicKey;
        }

        if(cryptoController.checkSign(sign, "key", signKey)){
            byte[] byteSecretKey = getByteArray(0, 256, encrData);
            byte[] byteIV = cryptoController.decryptIV(getByteArray(256, 512, encrData));
            byte[] byteUpdateOrder = getByteArray(512, encrData.length - 256, encrData);

            SecretKey secretKey = cryptoController.restoreSecretKey(byteSecretKey);
            byte[] decrUpdateList = cryptoController.decryptOrder(byteUpdateOrder, secretKey, byteIV);

            return decrUpdateList;
        }else{
            System.out.println("Sign error");
            return null;
        }
    }

    //Encode public server key
    public byte[] encodePublicKey(byte[] exportedPublicKey){
        return Base64.encode(exportedPublicKey);
    }

    //Use to separate response on key bytes and data bytes
    private byte[] getByteArray(int start, int end, byte[] data){
        int numOfIter = end - start;
        byte[] bytes = new byte[numOfIter];

        for(int i = 0; i < numOfIter; i++){
            bytes[i] = data[i + start];
        }
        return bytes;
    }

    //Convert byte to string
    private String getStringFromBytes(byte[] data){
        String s = "";
        for (byte b : data){
            s += (char) b;
        }
        return s;
    }

}
