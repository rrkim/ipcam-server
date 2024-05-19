package com.rrkim.ipcamserver.core.utility;

import java.util.Base64;

public class StringUtility {

    public static String encodeBase64(byte[] bytes) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(bytes);

        return new String(encodedBytes);
    }

    public static String encodeBase64(String text) {
        return encodeBase64(text.getBytes());
    }

    public static String decodeBase64(String text) {
        byte[] decodedBytes = decodeBase64Bytes(text);
        return new String(decodedBytes);
    }

    public static byte[] decodeBase64Bytes(String text) {
        Base64.Decoder decoder = Base64.getDecoder();

        return decoder.decode(text.getBytes());
    }
}
