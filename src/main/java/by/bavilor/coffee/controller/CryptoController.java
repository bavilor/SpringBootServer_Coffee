package by.bavilor.coffee.controller;

import by.bavilor.coffee.component.KeyStorage;
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
import java.security.Signature;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
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
    private KeyStorage keyStorage;
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
    public byte[] getServerPublicKey() throws Exception{
        KeyPair keyPair = keyStorage.getKeyPairFromKeyStore();
        return keyPair.getPublic().getEncoded();
    }

    //Decrypt IV
    public byte[] decryptIV(byte[] iv) throws Exception{
        return decrypt.decryptIV(iv);
    }

    //Restore secret key
    public SecretKey restoreSecretKey(byte[] secretKeyBytes) throws Exception{
        return decrypt.restoreSecretKey(secretKeyBytes);
    }

    //Decrypt order list
    public byte[] decryptOrder(byte[] byteOrder, SecretKey secretKey, byte[] byteIV) throws Exception{
        return decrypt.decryptOrder(byteOrder, secretKey, byteIV);
    }

    //Check sign for update methods
    public boolean checkSign(byte[] sign, String keyWord, PublicKey publicKey) throws Exception   {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(keyWord.getBytes());
        return signature.verify(sign);
    }
}
