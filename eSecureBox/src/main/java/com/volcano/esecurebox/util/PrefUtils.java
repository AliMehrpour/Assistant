// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.volcano.esecurebox.VlApplication;

/**
 * Utility class for default shared preferences
 */
public final class PrefUtils {

    public static String PREF_NAVIGATOR_LAST_CATEGORY   = "navigator_last_category";
    public static String PREF_NAVIGATOR_USER_LEARNED    = "navigator_user_learned";

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
        editor.apply();
    }

    public static void setPref(String key, boolean value) {
        final SharedPreferences.Editor editor = getPrefs().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void setPref(String key, String value) {
        final SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(key, value);
        editor.apply();
    }
}
