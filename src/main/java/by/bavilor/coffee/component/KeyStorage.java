package by.bavilor.coffee.component;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v1CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;

/**
 * Created by bosak on 6/8/2018.
 */
@Component
public class KeyStorage {

    //Write keys into keystore
    public void writeKeyInKeyStore(KeyPair keyPair) throws Exception{
        FileInputStream fis = null;
        FileOutputStream fos = null;
        KeyStore keyStore = KeyStore.getInstance("pkcs12");
        char[] keyStorePass = "helloServer".toCharArray();
        int sizeOfKeyStore = 0;

        try{
            fis = new FileInputStream("./KeyStore.p12");
            keyStore.load(fis, keyStorePass);

            for (Enumeration<String> e = keyStore.aliases(); e.hasMoreElements();){
                e.nextElement();
                sizeOfKeyStore++;
            }
        }catch(Exception e){
            keyStore.load(null, keyStorePass);
        }finally {
            if(fis != null){
                fis.close();
            }
        }

        KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(keyStorePass);

        X509Certificate[] chain = new X509Certificate[1];
        chain[0] = generateCertificate(keyPair);

        KeyStore.PrivateKeyEntry prEntr = new KeyStore.PrivateKeyEntry(keyPair.getPrivate(), chain);
        keyStore.setEntry(String.valueOf(sizeOfKeyStore), prEntr, protParam);

        try{
            fos = new FileOutputStream("./KeyStore.p12");
            keyStore.store(fos, keyStorePass);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(fos != null){
                fos.close();
            }
        }
    }

    //Generate certificate
    private X509Certificate generateCertificate(KeyPair keyPair) throws Exception{
        X500Name issuer = new X500Name("C=DE");
        BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
        Date validityBeginDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        Date validityEndDate = new Date(System.currentTimeMillis() + 2 * 365 * 24 * 60 * 60 * 1000);
        X500Name subject = new X500Name("C=MySubject");

        X509v1CertificateBuilder builder = new JcaX509v1CertificateBuilder(
                issuer, serialNumber, validityBeginDate, validityEndDate, subject, keyPair.getPublic()
        );
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(keyPair.getPrivate());

        return new JcaX509CertificateConverter().setProvider("BC").getCertificate(builder.build(signer));

    }

    //Find the last keypair in keystore and return it
    public KeyPair getKeyPairFromKeyStore() throws Exception {
        FileInputStream fis = null;
        KeyPair keyPair = null;
        String lastAliase = "";
        KeyStore keyStore = KeyStore.getInstance("pkcs12");
        char[] keyStorePass = "helloServer".toCharArray();

        try {
            fis = new FileInputStream("./KeyStore.p12");
            keyStore.load(fis, keyStorePass);
            for (Enumeration<String> e = keyStore.aliases(); e.hasMoreElements();){
                lastAliase = e.nextElement();
            }
            PublicKey publicKey = keyStore.getCertificate(lastAliase).getPublicKey();
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(lastAliase, keyStorePass);
            keyPair = new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        return keyPair;
    }
}
