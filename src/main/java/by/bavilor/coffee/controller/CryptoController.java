package by.bavilor.coffee.controller;

import by.bavilor.coffee.crypto.Decrypt;
import by.bavilor.coffee.crypto.Encrypt;
import by.bavilor.coffee.crypto.KeyGen;
import by.bavilor.coffee.entity.Order;
import by.bavilor.coffee.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
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

    //Restore public key
    public PublicKey restorePublicKey(byte[] pkBytes) throws Exception{
        KeyFactory keyFactory  = KeyFactory.getInstance("RSA", "BC");
        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(pkBytes);
        return keyFactory.generatePublic(keySpecX509);
    }

    //Return secret key
    public SecretKey getSecretKey() throws Exception{
        return keyGen.generateSecretKey();
    }

    //Return iv
    public byte[] getIV() throws Exception{
        return keyGen.generateIV();
    }

    //Encrypt string
    public byte[] encryptData(byte[] data, SecretKey secretKey, byte[] iv) throws Exception{
        return encrypt.encryptData(data, secretKey, iv);
    }

    //Wrap AES key by RSA
    public byte[] encryptSecretKey(PublicKey publicKey, SecretKey secretKey) throws Exception{
        return encrypt.encryptSecretKey(publicKey, secretKey);
    }

    //Encrypt IV by public key
    public byte[] encryptIV(PublicKey publicKey, byte[] iv) throws Exception{
        return encrypt.encryptIV(publicKey, iv);
    }

    //Get byte server public key
    public byte[] getServerPublicKey(){
        return keyGen.getPublicKey().getEncoded();
    }

    //Decrypt IV
    public byte[] decryptIV(byte[] iv) throws Exception{
        return decrypt.decryptIV(iv);
    }

    public SecretKey restoreSecretKey(byte[] secretKeyBytes) throws Exception{
        return decrypt.restoreSecretKey(secretKeyBytes);
    }

    //Decrypt order list
    public byte[] decryptOrder(byte[] byteOrder, SecretKey secretKey, byte[] byteIV) throws Exception{
        return decrypt.decryptOrder(byteOrder, secretKey, byteIV);
    }





   /*


    //Restore user public key
    public PublicKey restoreUserPublicKey(String b64UPK) throws Exception{
        byte[] bKey = Base64.decode(b64UPK);


    }

    //Decrypt session ID by private server key
    public String decryptSessionID(String jsonSession) throws Exception{
        byte[] bytes;
        try{
            bytes = new Gson().fromJson(jsonSession, byte[].class);
        }catch (Exception e){
            bytes = Base64.decode(jsonSession);
        }

        return decrypt.decryptUserSession(bytes);
    }





    //Decrypt aes key
    public SecretKey restoreSecretKey(byte[] byteSecretkey) throws Exception{
       return decrypt.restoreSecretKey(byteSecretkey);
    }



    //Check sign for update methods
    public boolean checkSign(byte[] sign, String session, PublicKey publicKey, String client) throws Exception   {

        if(client.equals("web")){
            Signature signature = Signature.getInstance("SHA256withRSA/PSS", "BC");
            PSSParameterSpec pssSpec = new PSSParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), 128, 1);

            signature.setParameter(pssSpec);
            signature.initVerify(publicKey);
            signature.update(session.getBytes());

            return signature.verify(sign);
        }else{
            Signature signature = Signature.getInstance("SHA256withRSA");

            signature.initVerify(publicKey);
            signature.update(session.getBytes());

            return signature.verify(sign);
        }

    }



    //Decrypt IV


    //Restore public PSS key
    public PublicKey restorePssKey(byte[] byteKey) throws Exception{
        KeyFactory keyFactory  = KeyFactory.getInstance("RSA", "BC");
        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(byteKey);
        return keyFactory.generatePublic(keySpecX509);
    }*/
}
