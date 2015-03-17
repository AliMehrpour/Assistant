// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.security;

import android.text.TextUtils;
import android.util.Base64;

import com.volcano.esecurebox.Managers;
import com.volcano.esecurebox.model.User;
import com.volcano.esecurebox.util.LogUtils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Security utilities
 */
public class SecurityUtils {
    private static final String TAG = LogUtils.makeLogTag(SecurityUtils.class);

    private static final String RANDOM_ALGORITHM = "SHA1PRNG";
    private static final String PBE_ALGORITHM = "PBEWithSHA256And256BitAES-CBC-BC";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int NUM_ITERATION = 1000;
    private static final int KEY_SIZE = 256;
    private static final int SALT_SIZE = 128;
    private static final int IV_SIZE = 16;

    private static Cipher mCipher;
    private static SecretKey mSecretKey;
    private static IvParameterSpec mIvSpec;

    /**
     * Initialize cryptography
     * @param password The password
     */
    public static void initializeCryptography(String password) {
        final User user = Managers.getAccountManager().getCurrentUser();
        final byte[] iv = decodeToByte(user.getEncryptionIv());

        try {
            mSecretKey = generateSecretKey(password);
            mIvSpec = new IvParameterSpec(iv);
            mCipher = Cipher.getInstance(CIPHER_ALGORITHM);
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            LogUtils.LogE(TAG, "Error in initialization cryptography", e);
        }
    }

    /**
     * Generate secret key based on password
     * @param password The password
     */
    private static SecretKey generateSecretKey(String password) {
        final User user = Managers.getAccountManager().getCurrentUser();
        final byte[] salt = decodeToByte(user.getEncryptionSalt());

        try {
            final PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt, NUM_ITERATION, KEY_SIZE);
            final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBE_ALGORITHM);
            final SecretKey tempKey = keyFactory.generateSecret(pbeKeySpec);
            return new SecretKeySpec(tempKey.getEncoded(), "AES");
        }
        catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            LogUtils.LogE(TAG, "Error in generate secret key", e);
        }

        return null;
    }

    /**
     * Encrypt a clear text with default secret key
     * @param clearText The clear text
     * @return Encrypted text
     */
    public static String encrypt(String clearText) {
        return encrypt(mSecretKey, clearText);
    }

    /**
     * Encrypt a clear text with given secret key
     * @param secretKey The secret key
     * @param clearText The clear text
     * @return Encrypted text
     */
    public static String encrypt(SecretKey secretKey, String clearText) {
        if (clearText == null || TextUtils.isEmpty(clearText)) {
            return clearText;
        }

        try {
            mCipher.init(Cipher.ENCRYPT_MODE, secretKey, mIvSpec);
            return encodeToString(mCipher.doFinal(getBytes(clearText)));
        }
        catch (Exception e) {
            LogUtils.LogE(TAG, "Error in encryption", e);
        }

        return clearText;
    }

    /**
     * Decrypt an encrypted text with default secret key
     * @param encryptedText The encrypted text
     * @return Decrypted text
     */
    public static String decrypt(String encryptedText) {
        return decrypt(mSecretKey, encryptedText);
    }

    /**
     * Decrypt an encrypted text with given secret key
     * @param secretKey The secret key
     * @param encryptedText The encrypted text
     * @return Decrypted text
     */
    public static String decrypt(SecretKey secretKey, String encryptedText) {
        if (encryptedText == null || TextUtils.isEmpty(encryptedText)) {
            return encryptedText;
        }

        try {
            mCipher.init(Cipher.DECRYPT_MODE, secretKey, mIvSpec);
            return new String(mCipher.doFinal(decodeToByte(encryptedText)));
        }
        catch (Exception e) {
            LogUtils.LogE(TAG, "Error in decryption", e);
        }

        return encryptedText;
    }

    /**
     * @return The random salt suitable for GenerateKeyFromPassword
     */
    public static String generateSalt() {
        try {
            final byte[] randomBytes = randomBytes(SALT_SIZE);
            return encodeToString(randomBytes);
        }
        catch (NoSuchAlgorithmException e) {
            LogUtils.LogE(TAG, "Error in generating salt", e);
        }
        return null;
    }

    /**
     * @return The random iv suitable for GenerateKeyFromPassword
     */
    public static String generateIv() {
        try {
            final byte[] randomBytes = randomBytes(IV_SIZE);
            return encodeToString(randomBytes);
        }
        catch (NoSuchAlgorithmException e) {
            LogUtils.LogE(TAG, "Error in generating iv", e);
        }
        return null;
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

    private static byte[] randomBytes(int length) throws NoSuchAlgorithmException {
        final SecureRandom random = SecureRandom.getInstance(RANDOM_ALGORITHM);
        final byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return bytes;
    }
}