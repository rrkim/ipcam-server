package com.rrkim.ipcamserver;

import com.rrkim.ipcamserver.core.device.DeviceIdProvider;
import com.rrkim.ipcamserver.core.file.service.FileService;
import com.rrkim.ipcamserver.module.auth.service.IdentificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@SpringBootTest
public class IpcamServerApplicationTests {
    @Autowired
    IdentificationService identificationService;
    @Autowired
    FileService fileService;

    @Test
    void contextLoads() throws Exception {
        byte[] sharedKey = identificationService.getEncryptedSharedKey();
        String encodedSharedKey = Base64.getEncoder().encodeToString(sharedKey);

        String publicKeyString = fileService.readFileByDataStream("keys/private.key");
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key publicKey = keyFactory.generatePrivate(keySpec);

        Cipher ciper = Cipher.getInstance("RSA");
        ciper.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decrypted = ciper.doFinal(Base64.getDecoder().decode(encodedSharedKey));

        System.out.println(Base64.getEncoder().encodeToString(decrypted));
        System.out.println(Base64.getEncoder().encodeToString(sharedKey));
    }

    @Test
    void RSATest() throws Exception {
        // make sure camera identity(RSA key pair) is present
        identificationService.createCameraIdentity();

        // target text
        String targetString = "hello world";

        // keyFactory
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Cipher cipher = Cipher.getInstance("RSA");

        // encrypt target text with public key
        String publicKeyString = fileService.readFileByDataStream("keys/public.key");
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        Key publicKey = keyFactory.generatePublic(keySpec);

        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encrypted = cipher.doFinal(targetString.getBytes());

        // decrypt encrypted sequence with private key
        String privateKeyString = fileService.readFileByDataStream("keys/private.key");
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        Key privateKey = keyFactory.generatePrivate(privateKeySpec);

        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] decrypted = cipher.doFinal(encrypted);

        // verify original string and decrypted string are same
        for(int i = 0; i < decrypted.length; i++) {
            assert decrypted[i] == targetString.getBytes()[i];
        }
    }
}
