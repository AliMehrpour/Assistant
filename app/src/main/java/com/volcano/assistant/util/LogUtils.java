// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.util;

import android.support.v7.appcompat.BuildConfig;
import android.util.Log;

import com.volcano.assistant.ConfigManager;

/**
 * Contains log utility methods
 */
public final class LogUtils {
    private static final String LOG_PREFIX = "assistant_";
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LENGTH = 23;

    public static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }

        return LOG_PREFIX + str;
    }

    /**
     * Don't use this when obfuscating class names!
     */
    public static String makeLogTag(Class cls) {
        return makeLogTag(cls.getSimpleName());
    }

    public static void LogD(final String tag, String message) {
        if (BuildConfig.DEBUG || ConfigManager.IS_DOGFOOD_BUILD || Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message);
        }
    }

    public static void LogD(final String tag, String message, Throwable cause) {
        if (BuildConfig.DEBUG || ConfigManager.IS_DOGFOOD_BUILD || Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message, cause);
        }
    }

    public static void LogV(final String tag, String message) {
        if (BuildConfig.DEBUG && Log.isLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, message);
        }
    }

    public static void LogV(final String tag, String message, Throwable cause) {
        if (BuildConfig.DEBUG && Log.isLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, message, cause);
        }
    }

    public static void LogI(final String tag, String message) {
        Log.i(tag, message);
    }

    public static void LogI(final String tag, String message, Throwable cause) {
        Log.i(tag, message, cause);
    }

    public static void LogW(final String tag, String message) {
        Log.w(tag, message);
    }

    public static void LogW(final String tag, String message, Throwable cause) {
        Log.w(tag, message, cause);
    }

    public static void LogE(final String tag, String message) {
        Log.e(tag, message);
    }

    public static void LogE(final String tag, String message, Throwable cause) {
        Log.e(tag, message, cause);
    }

    private LogUtils() {
    }
}
