package com.rrkim.ipcamserver;

import com.rrkim.ipcamserver.module.auth.dto.RSAKeyPair;
import com.rrkim.ipcamserver.module.auth.RSAKeyPairGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.NoSuchAlgorithmException;

@SpringBootTest
class IpcamServerApplicationTests {

    @Test
    void contextLoads() throws NoSuchAlgorithmException {
        RSAKeyPair keyPair = RSAKeyPairGenerator.createKeyPair();
        System.out.println("RSA Public Key: " + keyPair.getPublicKey());
        System.out.println("RSA Private Key: " + keyPair.getPrivateKey());
    }

}
