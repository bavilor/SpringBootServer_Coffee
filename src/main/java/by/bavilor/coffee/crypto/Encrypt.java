package by.bavilor.coffee.crypto;

import by.bavilor.coffee.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.List;

/**
 * Created by bosak on 5/7/2018.
 */
@Component
public class Encrypt {

    @Autowired
    private KeyGen keyGen;

    public Encrypt() {}

    //Encrypt list of products by use AES
    public byte[] encryptData(byte[] data, SecretKey secretKey, byte[] iv) throws Exception{
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
        return cipher.doFinal(data);
    }

    //Encrypt AES key
    public byte[] encryptSecretKey(PublicKey publicKey, SecretKey secretKey) throws Exception{
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "BC");
        cipher.init(Cipher.WRAP_MODE, publicKey);

        return cipher.wrap(secretKey);
    }

    //Encrypt iv
    public byte[] encryptIV(PublicKey publicKey, byte[] iv) throws Exception{
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(iv);
    }









    //Encrypt List<Product>
    public byte[] encryptList(String json) throws Exception{
        SecretKey secretKey = keyGen.generateSecretKey();

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        return cipher.doFinal(json.getBytes());
    }

    //Encrypt session ID
    public byte[] encryptSessionID(String session, PublicKey userPublicKey) throws Exception{
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, userPublicKey);

        return cipher.doFinal(session.getBytes());
    }






}
