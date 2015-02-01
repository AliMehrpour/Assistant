// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.util;

import android.os.Build;
import android.widget.Toast;

import com.volcano.assistant.VlApplication;

/**
 * Application-wide utilities
 */
public class Utils {

    /**
     * @return True if SDK is greater than JellyBean
     */
    public static boolean hasJellyBeanApi() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * @return True if SDK is greater than Lollipop
     */
    public static boolean hasLollipopApi() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * Show toast message
     * @param id The if of string asset
     */
    public static void showToast(int id) {
        showToast(VlApplication.getInstance().getString(id));
    }

    /**
     * Show toast message
     * @param text The message
     */
    public static void showToast(CharSequence text) {
        Toast.makeText(VlApplication.getInstance(), text, Toast.LENGTH_SHORT).show();
    }

}
