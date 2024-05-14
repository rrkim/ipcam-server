package com.rrkim.ipcamserver.core.utility;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AESKeyGenerator {
    public static SecretKey getAESKey() {
        KeyGenerator keyGenerator;

        try {
            // AES-256
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256, new SecureRandom());

            return keyGenerator.generateKey();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
