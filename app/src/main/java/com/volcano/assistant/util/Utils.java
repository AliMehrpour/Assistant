// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

import com.volcano.assistant.VlApplication;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Application-wide utilities
 */
public class Utils {
    /**
     * Return suitable format of Date
     * @param date The date
     * @return Formatted Date
     */
    public static String getTimeSpan(Date date) {
        final Date now = new Date();
        SimpleDateFormat sdfDate;
        final String dateFormatted;
        sdfDate = new SimpleDateFormat("dd/MM/yyyy");
        if (sdfDate.format(date).equals(sdfDate.format(now))) {
            dateFormatted = new SimpleDateFormat("HH:mm").format(date);
        }
        else {
            sdfDate = new SimpleDateFormat("yyyy");
            if (sdfDate.format(date).equals(sdfDate.format(now))) {
                dateFormatted = new SimpleDateFormat("MMM dd").format(date);
            }
            else {
                dateFormatted = new SimpleDateFormat("MM/dd/yyyy").format(date);
            }
        }

        return dateFormatted;
    }

    /**
     * @return True if SDK is greater than JellyBean
     */
    public static boolean hasJellyBeanApi() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    /**
     * @return True if SDK is greater than Kitkat
     */
    public static boolean hashKitkatApi() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
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

    /**
     * @return True if at least one network is available, otherwise false
     */
    public static boolean isNetworkAvailable() {
        final ConnectivityManager cm = (ConnectivityManager) VlApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo ni = cm.getActiveNetworkInfo();
        return !((ni == null) || (!ni.isConnected()));
    }
}
