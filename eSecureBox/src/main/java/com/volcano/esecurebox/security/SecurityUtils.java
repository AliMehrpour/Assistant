// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.security;

import android.text.TextUtils;
import android.util.Base64;

import com.volcano.esecurebox.util.LogUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Security utilities
 */
public class SecurityUtils {
    private static final String TAG = LogUtils.makeLogTag(SecurityUtils.class);

    private static final String AES_SECRET  = "abcdefghijklmnop";
    private static final String DES_SECRET  = "abcdefghijklmnop";

    private static Cipher mAESCipher;
    private static SecretKeySpec mAESSecretKey;

    private static Cipher mDESCipher;
    private static SecretKey mDESSecretKey;

    /**
     * Encryption algorithms
     * DES - DES algorithm <br />
     * AES_ECB - AES_ECB algorithm
     */
    public enum EncryptionAlgorithm {
        DES,
        AES_ECB
    }

    static {
        initializeAES();
        initializeDES();
    }

    /**
     * Encrypt a clear text
     * @param algorithm The {@link EncryptionAlgorithm}
     * @param clearText The clear text
     * @return encrypted text
     */
    public static String encrypt(EncryptionAlgorithm algorithm, String clearText) {
        if (clearText == null || TextUtils.isEmpty(clearText)) {
            return clearText;
        }

        try {
            if (algorithm == EncryptionAlgorithm.DES) {
                mDESCipher.init(Cipher.ENCRYPT_MODE, mDESSecretKey);
                return encodeToString(mDESCipher.doFinal(getBytes(clearText)));
            }
            else if (algorithm == EncryptionAlgorithm.AES_ECB) {
                mAESCipher.init(Cipher.ENCRYPT_MODE, mAESSecretKey);
                return encodeToString(mAESCipher.doFinal(getBytes(clearText)));
            }
        }
        catch (Exception e) {
            LogUtils.LogE(TAG, "Error in encryption", e);
        }

        return clearText;
    }

    /**
     * Decrypt an encrypted text
     * @param algorithm The {@link EncryptionAlgorithm}
     * @param encryptedText The encrypted text
     * @return decrypted text
     */
    public static String decrypt(EncryptionAlgorithm algorithm, String encryptedText) {
        if (encryptedText == null || TextUtils.isEmpty(encryptedText)) {
            return encryptedText;
        }

        try {
            if (algorithm == EncryptionAlgorithm.DES) {
                mDESCipher.init(Cipher.DECRYPT_MODE, mDESSecretKey);
                return new String(mDESCipher.doFinal(decodeToByte(encryptedText)));
            }
            else if (algorithm == EncryptionAlgorithm.AES_ECB) {
                mAESCipher.init(Cipher.DECRYPT_MODE, mAESSecretKey);
                return new String(mAESCipher.doFinal(decodeToByte(encryptedText)));
            }
        }
        catch (Exception e) {
            LogUtils.LogE(TAG, "Error in decryption", e);
        }

        return encryptedText;
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
            LogUtils.LogE(TAG, "No algorithm for encryption", e);
        }

        return input;
    }

    private static void initializeAES() {
        try {
            // Set secret key
            byte[] key = getBytes(AES_SECRET);
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit
            mAESSecretKey = new SecretKeySpec(key, "AES");

            // Set cipher
            mAESCipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        }
        catch (Exception e) {
            LogUtils.LogE(TAG, "Initialize AES failed", e);
        }
    }

    private static void initializeDES() {
        try {
            final DESKeySpec keySpec = new DESKeySpec(getBytes(DES_SECRET));
            final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            mDESSecretKey = keyFactory.generateSecret(keySpec);
            mDESCipher = Cipher.getInstance("DES");
        }
        catch (Exception e) {
            LogUtils.LogE(TAG, "Initialize DES failed", e);
        }
    }

    private static byte[] getBytes(String string) {
        try {
            return string.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            LogUtils.LogE(TAG, "Unsupported encoding", e);
        }
        return null;
    }

    private static String encodeToString(byte[] input) {
        return Base64.encodeToString(input, Base64.DEFAULT);
    }

    private static byte[] decodeToByte(String input) {
        return Base64.decode(input, Base64.DEFAULT);
    }

}