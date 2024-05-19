package com.rrkim.ipcamserver.core.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class ShaUtility {

    public static String getSalt() {
        SecureRandom r = new SecureRandom();
        byte[] salt = new byte[20];

        r.nextBytes(salt);

        StringBuilder sb = new StringBuilder();
        for(byte b : salt) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    public static String hash(String text, String salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        md.update((text + salt).getBytes());
        byte[] message = md.digest();

        StringBuilder sb = new StringBuilder();
        for (byte b : message) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    public static String hash(String text) throws NoSuchAlgorithmException {
        String salt = getSalt();
        return hash(text, salt);
    }
}
