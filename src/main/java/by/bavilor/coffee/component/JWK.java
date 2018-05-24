package by.bavilor.coffee.component;

import com.google.gson.Gson;


import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

/**
 * Created by bosak on 5/22/2018.
 */
public class JWK {

    private String e;
    private String n;
    private String jwk;


    private JWK(){}
    public JWK(String jwk){
        this.jwk = jwk;
    }

    public PublicKey restorePublicKey(){
        try{
            byte[] exponentBytes = Base64.getUrlDecoder().decode(e);
            byte[] modulusBytes = Base64.getUrlDecoder().decode(n);

            BigInteger publicExponent = new BigInteger(1, exponentBytes);
            BigInteger modulus = new BigInteger(1, modulusBytes);

            RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, publicExponent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA","BC");
            return keyFactory.generatePublic(spec);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String getExponent() {
        return e;
    }

    public void setExponent(String e) {
        this.e = e;
    }

    public String getModulus() {
        return n;
    }

    public void setModulus(String n) {
        this.n = n;
    }

    public String getJwk() {
        return jwk;
    }

    public void setJwk(String jwk) {
        this.jwk = jwk;
    }
}
