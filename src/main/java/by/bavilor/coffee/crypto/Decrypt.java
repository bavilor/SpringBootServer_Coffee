package by.bavilor.coffee.crypto;

import by.bavilor.coffee.component.KeyStorage;
import by.bavilor.coffee.entity.Order;
import com.google.gson.Gson;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.KeyFactory;
import java.security.KeyPair;
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
    private KeyStorage keyStorage;



    public Decrypt(){}

    //Decrypt and restore a secret key
    public SecretKey restoreSecretKey(byte[] secretKey) throws Exception{
        KeyPair keyPair = keyStorage.getKeyPairFromKeyStore();
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "BC");
        cipher.init(Cipher.UNWRAP_MODE, keyPair.getPrivate());

        return (SecretKey) cipher.unwrap(secretKey,"AES", Cipher.SECRET_KEY);
    }

    //Decrypt order list
    public byte[] decryptOrder(byte[] byteOrder, SecretKey secretKey, byte[] byteIV) throws Exception{
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(byteIV));

        return cipher.doFinal(byteOrder);
    }

    //Decrypt iv
    public byte[] decryptIV(byte[] encByteIv) throws Exception{
        KeyPair keyPair = keyStorage.getKeyPairFromKeyStore();
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        return cipher.doFinal(encByteIv);
    }
}
