package com.rrkim.ipcamserver.core.utility;

import org.apache.commons.codec.DecoderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AesUtility {

    public static String encodeAesCbc(byte[] plainBytes, String privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec secretKey = new SecretKeySpec(privateKey.getBytes(), "AES");
        IvParameterSpec IV = new IvParameterSpec(privateKey.substring(0,16).getBytes());

        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, secretKey, IV);

        byte[] encryptionByte = c.doFinal(plainBytes);
        return Base64.getEncoder().encodeToString(encryptionByte);
    }


    public static byte[] decodeAesCbc(String encodedString, String privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, DecoderException, IllegalBlockSizeException, BadPaddingException {
        byte[] encryptionByte = Base64.getDecoder().decode(encodedString);

        SecretKeySpec secretKey = new SecretKeySpec(privateKey.getBytes(), "AES");
        IvParameterSpec IV = new IvParameterSpec(privateKey.substring(0,16).getBytes());

        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, secretKey, IV);

        return c.doFinal(encryptionByte);
    }
}
