// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.util;

import android.util.Base64;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Security utilities
 */
public class SecurityUtils {
    private static final String TAG = LogUtils.makeLogTag(SecurityUtils.class);

    /**
     * Encrypt a clear text via DES method
     * @param encryptionSecret The encryption secret
     * @param clearText The clear text
     * @return encrypted text
     */
    public static String encrypt(String encryptionSecret, String clearText) {
        try {
            final DESKeySpec keySpec = new DESKeySpec(encryptionSecret.getBytes("UTF-8"));
            final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            final SecretKey key = keyFactory.generateSecret(keySpec);

            final Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.encodeToString(cipher.doFinal(clearText.getBytes("UTF-8")), Base64.DEFAULT);
        }
        catch (Exception e) {
            return clearText;
        }
    }

    /**
     * Decrypt a encrypted text
     * @param encryptionSecret The encryption secret
     * @param encryptedText The encrypted text
     * @return decrypted text——
     */
    public static String decrypt(String encryptionSecret, String encryptedText) {
        try {
            final DESKeySpec keySpec = new DESKeySpec(encryptionSecret.getBytes("UTF-8"));
            final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            final SecretKey key = keyFactory.generateSecret(keySpec);

            final byte[] encryptedWithoutB64 = Base64.decode(encryptedText, Base64.DEFAULT);
            final Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            final byte[] plainTextPwdBytes = cipher.doFinal(encryptedWithoutB64);
            return new String(plainTextPwdBytes);
        }
        catch (Exception e) {
            return encryptedText;
        }
    }

    /**
     * Get MD5Hash of a input string
     * @param input The input string
     * @return The MD5Hash string
     */
    public static String getMd5Hash(String input) {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            final byte[] messageDigest = md.digest(input.getBytes());
            final BigInteger number = new BigInteger(1, messageDigest);

            String md5 = number.toString(16);
            while (md5.length() < 32) {
                md5 = "0" + md5;
            }

            return md5;
        }
        catch (NoSuchAlgorithmException e) {
            LogUtils.LogE(TAG, e.getLocalizedMessage());
            return null;
        }
    }
}
