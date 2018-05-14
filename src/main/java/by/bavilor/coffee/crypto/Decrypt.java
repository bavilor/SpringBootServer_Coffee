package by.bavilor.coffee.crypto;

import by.bavilor.coffee.entity.Order;
import com.google.gson.Gson;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bosak on 5/7/2018.
 */
@Component
public class Decrypt {
    @Autowired
    private KeyGen keyGen;

    public Decrypt(){}

    //decrypt user session
    public String decryptUserSession(byte[] bytes) throws Exception{
        String session = "";
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, keyGen.getPrivateKey());
        byte[] byteSessionID = cipher.doFinal(bytes);
        for(byte b : byteSessionID){
            session += (char) b;
        }
        return session;
    }

    //Decrypt and restore a secret key
    public SecretKey decryptSecretKey(byte[] secretKey) throws Exception{
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "BC");
        cipher.init(Cipher.UNWRAP_MODE, keyGen.getPrivateKey());

        return (SecretKey) cipher.unwrap(secretKey,"AES", Cipher.SECRET_KEY);
    }

    //Decrypt order list
    public List<Order> decryptOrder(byte[] byteOrder, SecretKey secretKey) throws Exception{
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decrBytes = cipher.doFinal(byteOrder);

        String s = "";
        for(byte b : decrBytes){
            s += (char) b;
        }

        Order[] orders = new Gson().fromJson(s, Order[].class);
        return Arrays.asList(orders);
    }
}
