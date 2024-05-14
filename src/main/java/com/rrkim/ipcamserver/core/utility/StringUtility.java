package com.rrkim.ipcamserver.core.utility;

import java.util.Base64;

public class StringUtility {

    public static String encodeBase64(String text) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(text.getBytes());

        return new String(encodedBytes);
    }

    public static String decodeBase64(String text) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(text.getBytes());

        return new String(decodedBytes);
    }
}
