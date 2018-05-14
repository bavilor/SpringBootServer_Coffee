package by.bavilor.coffee.crypto;

import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by bosak on 5/3/2018.
 */
@Component
public class KeyGen {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public KeyGen() {}

    public void generateAsyncKeys() throws Exception{
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
        keyPairGenerator.initialize(2048);
        KeyPair keyPairClient = keyPairGenerator.generateKeyPair();

        privateKey = keyPairClient.getPrivate();
        publicKey = keyPairClient.getPublic();
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public SecretKey generateSecretKey() throws Exception{
        KeyGenerator keyGeneratorAESClient = KeyGenerator.getInstance("AES", "BC");
        keyGeneratorAESClient.init(128);

        return keyGeneratorAESClient.generateKey();
    }
}
