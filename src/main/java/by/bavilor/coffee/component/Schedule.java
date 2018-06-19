package by.bavilor.coffee.component;

import by.bavilor.coffee.crypto.KeyGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.KeyPair;


/**
 * Created by bosak on 4/25/2018.
 */
@Component
public class Schedule {

    @Autowired
    private KeyGen keyGen;
    @Autowired
    private KeyStorage keyStorage;

    @Scheduled(fixedDelay = 1440000 * 1000)
    public void UpdateKeys() throws Exception{
        keyStorage.writeKeyInKeyStore(keyGen.generateAsyncKeys());
        System.out.println("Keys're generated");
    }
}
