package com.volcano.assistant.util;

import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static boolean hasHoneyCombApi() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasJellyBeanApi() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasLollipopApi() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
