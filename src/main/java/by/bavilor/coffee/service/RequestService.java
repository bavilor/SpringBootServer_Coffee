package by.bavilor.coffee.service;

import by.bavilor.coffee.controller.CryptoController;
import by.bavilor.coffee.crypto.KeyGen;
import by.bavilor.coffee.entity.User;
import com.google.gson.Gson;
import org.bouncycastle.util.encoders.Base64;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.security.PublicKey;
import java.util.List;

/**
 * Created by bosak on 5/11/2018.
 */
@Service
public class RequestService {

    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CryptoController cryptoController;
    @Autowired
    private KeyGen keyGen;

    //Return server public key
    public byte[] getServerPublicKey(){
        System.out.println("Key was send");
        return cryptoController.getEncodedServerPublicKey();
    }

    //Set user public key
    public void addUser(String jsonPublickey, String sessionID){
        try{
            userService.addUser(cryptoController.restoreUserPublicKey(jsonPublickey), sessionID);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Return encrypted session id
    public String getEncrSessionID(String sessionID) throws Exception{
        return new Gson().toJson(cryptoController.encryptSessionID(sessionID));
    }

    //Return user find by session
    public User getUserBySession(String sessionID){
        return userService.getUserBySession(sessionID);
    }

    //Return user public key by session
    public PublicKey getPublicKeyByUserSession(String session) throws Exception{
        return userService.getPublicKeyBySession(session);
    }

    //Return price list & secret key in one array (unite with sendOrder??)
    public byte[] getPriceList(String sessionID) throws Exception{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PublicKey publicKey = userService.getPublicKeyBySession(sessionID);
        SecretKey secretKey = keyGen.generateSecretKey();

        byte[] byteKey = cryptoController.encryptSecretKey(publicKey, secretKey);
        byte[] data = cryptoController.ecnryptList(productService.getJsonProductList(), secretKey);

        outputStream.write(byteKey);
        outputStream.write(data);

        return outputStream.toByteArray();
    }

    //Register new order
    public void registerOrder(String session, byte[] byte64Data) throws Exception{
        byte[] byteData = Base64.decode(byte64Data);
        byte[] byteSecretKey = getByteArray(0, 256, byteData);
        byte[] byteOrder = getByteArray(256, byteData.length, byteData);

        SecretKey secretKey = cryptoController.decryptSecretKey(byteSecretKey);
        userService.registerOrder(cryptoController.decryptOrder(byteOrder, secretKey), session);
    }

    //Send order
    public byte[] sendOrder(String session) throws Exception{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PublicKey publicKey = userService.getPublicKeyBySession(session);
        SecretKey secretKey = keyGen.generateSecretKey();

        byte[] byteKey = cryptoController.encryptSecretKey(publicKey, secretKey);
        byte[] data = cryptoController.ecnryptList(userService.orderToJson(session), secretKey);

        outputStream.write(byteKey);
        outputStream.write(data);

        return outputStream.toByteArray();
    }

    //Update order
    public void updateOrder(String session, byte[] byte64Data) throws Exception{
        byte[] byteData = Base64.decode(byte64Data);
        byte[] sign = getByteArray(byteData.length - 256, byteData.length, byteData);

        if(cryptoController.checkSign(sign, session, userService.getPublicKeyBySession(session))){
            byte[] byteSecretKey = getByteArray(0, 256, byteData);
            byte[] byteUpdateOrder = getByteArray(256, byteData.length - 256, byteData);

            SecretKey secretKey = cryptoController.decryptSecretKey(byteSecretKey);
            userService.updateOrder(cryptoController.decryptOrder(byteUpdateOrder, secretKey), session);
            System.out.println("OK");
        }else{
            System.out.println("Sign error");
        }
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
}
