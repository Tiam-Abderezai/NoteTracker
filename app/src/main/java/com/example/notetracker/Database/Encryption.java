package com.example.notetracker.Database;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
//import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {
    private static String AES = "AES";

    public static String encTitle(String title, String passkey) throws Exception {
        SecretKeySpec key = generateKey(passkey);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(title.getBytes());
        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedValue;
    }
    public static String encDesc(String description, String passkey) throws Exception {
        SecretKeySpec key = generateKey(passkey);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(description.getBytes());
        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encryptedValue;
    }

    public static SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes , 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }

    public static String decTitle(String title, String passKey) throws Exception {
        SecretKeySpec key = generateKey(passKey);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodeValue = Base64.decode(title, Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodeValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    public static String decDesc(String description, String passKey) throws Exception {
        SecretKeySpec key = generateKey(passKey);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodeValue = Base64.decode(description.getBytes(), Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodeValue);
        String decryptedValue = new String(decValue);
//        System.out.println(decryptedValue + "TIAM");
        return decryptedValue;
    }
}
