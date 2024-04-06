package com.rrkim.ipcamserver.module.auth;

import com.rrkim.ipcamserver.module.auth.dto.RSAKeyPair;

import java.security.*;
import java.util.Base64;
import java.util.List;

public class RSAKeyPairGenerator {

    public static RSAKeyPair createKeyPair() throws NoSuchAlgorithmException {
        // RSA 키페어 생성기 생성
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");

        // 키페어 생성기 초기화: 키페어의 크기 지정 (예: 2048비트)
        keyPairGenerator.initialize(2048);

        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

        // RSA 키페어 생성
        return new RSAKeyPair(publicKey, privateKey);
    }

    private static RSAKeyPair getPEMString(KeyPair keyPair) {

        String publicKey = getPemFormatString("PUBLIC KEY", keyPair.getPublic().getEncoded());
        String privateKey = getPemFormatString("PRIVATE KEY", keyPair.getPrivate().getEncoded());

        return new RSAKeyPair(publicKey, privateKey);
    }

    private static String getPemFormatString(String type, byte[] key) {
        String content = Base64.getEncoder().encodeToString(key);
        return String.format("-----BEGIN %s-----\n%s\n-----END %s-----", type, content, type);
    }
}