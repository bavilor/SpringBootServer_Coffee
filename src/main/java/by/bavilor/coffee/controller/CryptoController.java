package by.bavilor.coffee.controller;

import by.bavilor.coffee.crypto.Decrypt;
import by.bavilor.coffee.crypto.Encrypt;
import by.bavilor.coffee.crypto.KeyGen;
import by.bavilor.coffee.entity.Order;
import by.bavilor.coffee.service.RequestService;
import com.google.gson.Gson;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

/**
 * Created by bosak on 5/3/2018.
 */
@Component
public class CryptoController {

    @Autowired
    private RequestService requestService;
    @Autowired
    private KeyGen keyGen;
    @Autowired
    private Decrypt decrypt;
    @Autowired
    private Encrypt encrypt;

    //Encrypt session ID
    public byte[] encryptSessionID(String session) throws Exception{
        return encrypt.encryptSessionID(session, requestService.getPublicKeyByUserSession(session));
    }

    //Get encoded server public key
    public byte[] getEncodedServerPublicKey(){
        return Base64.encode(keyGen.getPublicKey().getEncoded());
    }

    //Restore user public key
    public PublicKey restoreUserPublicKey(String jsonPublickey) throws Exception{
        byte[] bytePublicUserKey = new Gson().fromJson(jsonPublickey, byte[].class);

        KeyFactory keyFactory  = KeyFactory.getInstance("RSA", "BC");
        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.decode(bytePublicUserKey));

        return keyFactory.generatePublic(keySpecX509);
    }

    //Decrypt session ID by private server key
    public String decryptSessionID(String jsonSession) throws Exception{
        byte[] bytes = new Gson().fromJson(jsonSession, byte[].class);
        return decrypt.decryptUserSession(bytes);
    }

    //Encrypt list by use AES key
    public byte[] ecnryptList(String json, SecretKey secretKey) throws Exception{
        return encrypt.encryptListOfProducts(json, secretKey);
    }

    //Wrap AES key by RSA
    public byte[] encryptSecretKey(PublicKey publicKey, SecretKey secretKey) throws Exception{
        return encrypt.encryptSecretKey(publicKey, secretKey);
    }

    //Decrypt aes key
    public SecretKey decryptSecretKey(byte[] byteSecretkey) throws Exception{
       return decrypt.decryptSecretKey(byteSecretkey);
    }

    //Decrypt order list
    public List<Order> decryptOrder(byte[] byteOrder, SecretKey secretKey) throws Exception{
        return decrypt.decryptOrder(byteOrder, secretKey);
    }

    //Check sign for update methods
    public boolean checkSign(byte[] sign, String session, PublicKey publicKey) throws Exception   {
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initVerify(publicKey);
        signature.update(session.getBytes());
        return signature.verify(sign);
    }
}
