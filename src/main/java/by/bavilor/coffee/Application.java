package by.bavilor.coffee;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;


/**
 * Created by bosak on 4/19/2018.
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args){
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        SpringApplication.run(Application.class, args);
    }
}
