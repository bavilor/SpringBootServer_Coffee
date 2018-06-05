package by.bavilor.coffee.crypto;

import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;

/**
 * Created by bosak on 5/3/2018.
 */
@Component
public class KeyGen {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public KeyGen() {}

    //Generate secret key
    public void generateAsyncKeys() throws Exception{
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
        keyPairGenerator.initialize(2048);
        KeyPair keyPairClient = keyPairGenerator.generateKeyPair();

        privateKey = keyPairClient.getPrivate();
        publicKey = keyPairClient.getPublic();
    }

    //Generate secret key
    public SecretKey generateSecretKey() throws Exception{
        KeyGenerator keyGeneratorAESClient = KeyGenerator.getInstance("AES", "BC");
        keyGeneratorAESClient.init(128);

        return keyGeneratorAESClient.generateKey();
    }

    //Generate iv
    public byte[] generateIV() throws Exception{
        byte[] iv = new byte[16];
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

        random.nextBytes(iv);

        return iv;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }



}
