// Copyright (c) 2015 Volcano. All rights reserved.

package com.volcano.assistant.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.volcano.assistant.VlApplication;

/**
 * Utility class for default shared preferences
 */
@SuppressWarnings("UnusedDeclaration")
public final class PrefUtils {

    public static String PREF_NAVIGATOR_LAST_CATEGORY   = "navigator_last_category";
    public static String PREF_NAVIGATOR_USER_LEARNED    = "navigator_user_learned";

    public static void markUserLearnNavigator() {
        setPref(PREF_NAVIGATOR_USER_LEARNED, true);
    }

    public static boolean wasUserLearnNavigator() {
        return getPref(PREF_NAVIGATOR_USER_LEARNED, false);
    }

    public static void setNavigatorLastCategory(String categoryId) {
        setPref(PREF_NAVIGATOR_LAST_CATEGORY, categoryId);
    }

    public static String getNavigatorLastCategory() {
        return getPref(PREF_NAVIGATOR_LAST_CATEGORY, "");
    }

    private static SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(VlApplication.getInstance());
    }

    private static boolean exists(String key) {
        return getPrefs().contains(key);
    }

    private static void remove(String key) {
        final SharedPreferences.Editor editor = getPrefs().edit();
        editor.remove(key);
        editor.apply();
    }

    private static String getPref(String key, String defaultValue) {
        return getPrefs().getString(key, defaultValue);
    }

    private static void setPref(String key, String value) {
        final SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static boolean getPref(String key, boolean defaultValue) {
        return getPrefs().getBoolean(key, defaultValue);
    }

    private static void setPref(String key, boolean value) {
        final SharedPreferences.Editor editor = getPrefs().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private static int getPref(String key, int defaultValue) {
        return getPrefs().getInt(key, defaultValue);
    }

    private static void setPref(String key, int value) {
        final SharedPreferences.Editor editor = getPrefs().edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private static long getPref(String key, long defaultValue) {
        return getPrefs().getLong(key, defaultValue);
    }

    private static void setPref(String key, long value) {
        final SharedPreferences.Editor editor = getPrefs().edit();
        editor.putLong(key, value);
        editor.apply();
    }
}
