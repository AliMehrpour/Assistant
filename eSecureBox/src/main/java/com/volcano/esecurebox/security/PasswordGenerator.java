// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.security;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 * A password generator
 */
public final class PasswordGenerator {
    private static final String sLetters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";;
    private static final String sNumbers = "0123456789";
    private static final String sSpecials = "!@#$%&*";
    private static final Random random = new Random();

    public static final int PASSWORD_LENGTH_DEFAULT = 10;
    public static final int PASSWORD_LENGTH_NUMBER  = 4;

    /**
     * Generate a random password
     * @param length The length
     * @param numberIncluded True if want generated password contains number, false otherwise
     * @param letterIncluded True if want generated password contains letter, false otherwise
     * @param specialCharacterIncluded True if want generated password contains special character, false otherwise
     * @return The random generate password
     */
    public String generate(int length, boolean numberIncluded, boolean letterIncluded, boolean specialCharacterIncluded) {
        if (length <= 0 && !(numberIncluded || letterIncluded || specialCharacterIncluded)) {
            return "";
        }
        else {
            final StringBuilder sb = new StringBuilder();
            if (numberIncluded) {
                sb.append(sNumbers);
            }
            if (letterIncluded) {
                sb.append(sLetters);
            }
            if (specialCharacterIncluded) {
                sb.append(sSpecials);
            }

            final char[] symbols = sb.toString().toCharArray();
            final char[] buf = new char[length];
            for (int idx = 0; idx < length; ++idx) {
                buf[idx] = symbols[random.nextInt(symbols.length)];
            }

            return new String(buf);
        }
    }

    private final static SecureRandom sRandom = new SecureRandom();
    private String generateSecure(int length) {
        return new BigInteger(130, sRandom).toString(16).substring(0, length);
    }
}
