// Copyright (c) 2015 Volcano. All rights reserved.

package com.volcano.assistant.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.volcano.assistant.VlApplication;

/**
 * Utility class for default shared preferences
 */
public final class PrefUtils {

    public static String PREF_USER_LEARNED_NAVIGATOR = "user_learned_navigator";

    public static boolean exists(String key) {
        return getPrefs().contains(key);
    }

    public static boolean getPref(String key, boolean defaultValue) {
        return getPrefs().getBoolean(key, defaultValue);
    }

    public static String getPref(String key, String defaultValue) {
        return getPrefs().getString(key, defaultValue);
    }

    private static SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(VlApplication.getInstance());
    }

    public static void remove(String key) {
        final SharedPreferences.Editor editor = getPrefs().edit();
        editor.remove(key);
        editor.commit();
    }

    public static void setPref(String key, boolean value) {
        final SharedPreferences.Editor editor = getPrefs().edit();
        editor.putBoolean(key, value);
        editor.apply();
        //editor.commit();
    }

    public static void setPref(String key, String value) {
        final SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(key, value);
        editor.apply();
        //editor.commit();
    }

    public static void markUserLearnNavigator() {
        setPref(PREF_USER_LEARNED_NAVIGATOR, true);
    }

    public static boolean wasUserLearnNavigator() {
        return getPref(PREF_USER_LEARNED_NAVIGATOR, false);
    }

    /* public static int getPref(String key, int defaultValue) {
        return getPrefs().getInt(key, defaultValue);
    }

    public static void setPref(String key, int value) {
        final SharedPreferences.Editor editor = getPrefs().edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static long getPref(String key, long defaultValue) {
        return getPrefs().getLong(key, defaultValue);
    }

    public static void setPref(String key, long value) {
        final SharedPreferences.Editor editor = getPrefs().edit();
        editor.putLong(key, value);
        editor.commit();
    } */
}
