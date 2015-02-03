package com.volcano.assistant.util;

import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alimehrpour on 1/6/15.
 */
public class Utils {

  /*  public static int TIME_ONLY = 1;                // ex: 16:07
    public static int TIME_ONLY_AMPM = 2;           // ex: 16:07 PM
    public static int DATE_ONLY_DMY = 3;            // ex: 02/02/2015
    public static int WEEKDAY_DATE_ONLY_DMY = 4;    // ex: Mon 02/02/2015
    public static int WEEKDAY_SHORT_MONTH_DAY = 5;  // ex: Feb 02
    public static int WEEKDAY_DAY_MONTH_YEAR = 6;   // ex: Mon, 02 Feb 2015
    public static int WEEKDAY_DAY_MONTH = 7;        // ex: Mon, 02 Feb

    public String getDateFormat(int format) {
        Date now = new Date();
        SimpleDateFormat sdfDate;
        switch (format){
            case 1:
                sdfDate = new SimpleDateFormat("HH:mm");
                break;
            case 2:
                sdfDate = new SimpleDateFormat("HH:mm a");
                break;
            case 3:
                sdfDate = new SimpleDateFormat("dd/MM/yyyy");
                break;
            case 4:
                sdfDate = new SimpleDateFormat("E dd/MM/yyyy");
                break;
            case 5:
                sdfDate = new SimpleDateFormat("MMM  dd");
                break;
            case 6:
                sdfDate = new SimpleDateFormat("EEE, dd MMM yyyy");
                break;
            case 7:
                sdfDate = new SimpleDateFormat("EEE, dd MMM");
                break;
            default:
                sdfDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
                break;
        }
        return sdfDate.format(now);
    } */

    public static String getTimeSpan(Date date){
        Date now = new Date();
        SimpleDateFormat sdfDate;
        String dateFormatted;
        sdfDate = new SimpleDateFormat("dd/MM/yyyy");
        if(sdfDate.format(date).equals(sdfDate.format(now))){
            dateFormatted = new SimpleDateFormat("HH:mm").format(date);
        } else{
            sdfDate = new SimpleDateFormat("yyyy");
            if(sdfDate.format(date).equals(sdfDate.format(now))){
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
