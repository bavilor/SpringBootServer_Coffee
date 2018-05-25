package by.bavilor.coffee.service;

import by.bavilor.coffee.component.JWK;
import by.bavilor.coffee.controller.CryptoController;
import by.bavilor.coffee.crypto.KeyGen;
import by.bavilor.coffee.entity.User;
import com.google.gson.Gson;
import org.bouncycastle.util.encoders.Base64;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.security.PublicKey;
import java.security.SecureRandom;
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
        public void addUser(String jsonPublicKey, String sessionID) throws Exception{
        try{
            JWK jwk = new Gson().fromJson(jsonPublicKey, JWK.class);
            System.out.println("Web client");
            userService.addUser(jwk.restorePublicKey(), sessionID);
        }catch (Exception e){
            System.out.println("Desktop client");
            userService.addUser(cryptoController.restoreUserPublicKey(jsonPublicKey), sessionID);
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

        SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] iv = new byte[16];
        randomSecureRandom.nextBytes(iv);

        byte[] byteKey = cryptoController.encryptSecretKey(publicKey, secretKey);
        byte[] byteIV = cryptoController.encryptIV(publicKey, iv);
        byte[] data = cryptoController.ecnryptList(productService.getJsonProductList(), secretKey, iv);

        outputStream.write(byteKey);
        outputStream.write(byteIV);
        outputStream.write(data);

        return outputStream.toByteArray();
    }

    //Register new order
    public void registerOrder(String session, byte[] byte64Data) throws Exception{
        byte[] byteData = Base64.decode(byte64Data);
        byte[] byteSecretKey = getByteArray(0, 256, byteData);
        byte[] byteIV = cryptoController.decryptIV(getByteArray(256, 512, byteData));
        byte[] byteOrder = getByteArray(512, byteData.length, byteData);

        SecretKey secretKey = cryptoController.decryptSecretKey(byteSecretKey);
        userService.registerOrder(cryptoController.decryptOrder(byteOrder, secretKey, byteIV), session);
    }

    //Send order
    public byte[] sendOrder(String session) throws Exception{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PublicKey publicKey = userService.getPublicKeyBySession(session);
        SecretKey secretKey = keyGen.generateSecretKey();

        SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] iv = new byte[16];
        randomSecureRandom.nextBytes(iv);

        byte[] byteKey = cryptoController.encryptSecretKey(publicKey, secretKey);
        byte[] byteIV = cryptoController.encryptIV(publicKey, iv);
        byte[] data = cryptoController.ecnryptList(userService.orderToJson(session), secretKey, iv);

        outputStream.write(byteKey);
        outputStream.write(byteIV);
        outputStream.write(data);

        return outputStream.toByteArray();
    }

    //Update order ВЫ
    public void updateOrder(String session, byte[] byte64Data) throws Exception{
        byte[] byteData = Base64.decode(byte64Data);
        byte[] sign = getByteArray(byteData.length - 256, byteData.length, byteData);

        if(cryptoController.checkSign(sign, session, userService.getPublicKeyBySession(session))){
            byte[] byteSecretKey = getByteArray(0, 256, byteData);
            byte[] byteIV = cryptoController.decryptIV(getByteArray(256, 512, byteData));
            byte[] byteUpdateOrder = getByteArray(256, byteData.length - 256, byteData);

            SecretKey secretKey = cryptoController.decryptSecretKey(byteSecretKey);
            userService.updateOrder(cryptoController.decryptOrder(byteUpdateOrder, secretKey, byteIV), session);
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
