// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.volcano.esecurebox.R;
import com.volcano.esecurebox.VlApplication;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Application-wide utilities
 */
public final class Utils {
    private static final String TAG = LogUtils.makeLogTag(Utils.class);

    /**
     * Copy a text to clipboard
     * @param label The label
     * @param text The text
     */
    public static void copyToClipboard(String label, String text) {
        final ClipboardManager clipboard = (ClipboardManager) VlApplication.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
        final ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
    }

    /**
     * @return Application version name
     */
    public static String getAppVersionName() {
        try {
            final VlApplication app = VlApplication.getInstance();
            return app.getPackageManager().getPackageInfo(app.getPackageName(), 0).versionName;
        }
        catch (PackageManager.NameNotFoundException e) {
            LogUtils.LogE(TAG, "Can't get package info");
            throw new RuntimeException(e);
        }
    }

    /**
     * @param year The year
     * @param month The month
     * @param day The day
     * @return An string represents date from given info
     */
    public static String getCompleteDate(int year, int month, int day) {
        return getCompleteNumber(day) + "/" + getCompleteNumber(month) + "/" + year;
    }

    /**
     * Append a zero to first of number, if it is less than 10, otherwise return the number
     * @param number The number
     */
    public static String getCompleteNumber(int number) {
        String completeNumber;
        if (number < 9) {
            completeNumber = "0" + number;
        }
        else {
            completeNumber = Integer.toString(number);
        }

        return completeNumber;
    }

    /**
     * Get display size
     * @param context The context
     * @return The {@link Point} object contains width and height of device screen
     */
    public static Point getDisplaySize(Context context) {
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return new Point(metrics.widthPixels, metrics.heightPixels);
    }

    /**
     * Return suitable format of Date
     * @param date The date
     * @return Formatted Date
     */
    @SuppressLint("SimpleDateFormat")
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
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
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
     * Launch an email client
     * @param activity An Activity context for launching the email client
     * @param recipients A {@link java.util.List} of recipients
     * @param subject The subject
     */
    public static void launchEmailClient(Activity activity, List<String> recipients, String subject) {
        launchEmailClient(activity, recipients, subject, null);
    }

    /**
     * Launch an email client
     * @param activity An Activity context for launching the email client
     * @param recipients A {@link List} of recipients
     * @param subject The subject
     * @param body The body
     */
    public static void launchEmailClient(Activity activity, List<String> recipients, String subject, String body) {
        final Intent intent = new Intent(Intent.ACTION_SENDTO)
                .setData(Uri.parse("mailto:"))
                .putExtra(Intent.EXTRA_EMAIL, recipients.toArray(new String[recipients.size()]))
                .putExtra(Intent.EXTRA_SUBJECT, subject)
                .putExtra(Intent.EXTRA_TEXT, body);

        try {
            activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.email_choose_app_title)));
        }
        catch (ActivityNotFoundException e) {
            Log.e(TAG, "Not found any email app on your device");
            showToast(R.string.toast_load_email_client_failed);
        }
    }

    /**
     * Launch play store client. If play store client isn't installed on device or disabled, open
     * play store in web browser
     */
    public static void launchPlayStore() {
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setData(Uri.parse(String.format("market://details?id=%s", VlApplication.getInstance().getPackageName())));

        if (intent.resolveActivity(VlApplication.getInstance().getPackageManager()) != null) {
            VlApplication.getInstance().startActivity(intent);
        }
        else {
            intent.setData(Uri.parse(String.format("https://play.google.com/store/apps/details?id=%s", VlApplication.getInstance().getPackageName())));
            VlApplication.getInstance().startActivity(intent);
        }
    }

    /**
     * Launch an email client
     * @param activity An Activity context for launching the email client
     * @param subject The subject
     * @param body The body
     */
    public static void launchShareClient(Activity activity, String subject, String body) {
        final Intent intent = new Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_SUBJECT, subject)
                .putExtra(Intent.EXTRA_TEXT, body);

        try {
            activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.label_share_choose_app)));
        }
        catch (ActivityNotFoundException e) {
            Log.e(TAG, "Not found any share app on your device");
            showToast(R.string.toast_load_share_client_failed);
        }
    }

    /**
     * Show toast message
     * @param id The if of string asset
     */
    public static void showToast(int id) {
        showToast(VlApplication.getInstance().getString(id));
    }

    /**
     * Parse given string to {@link Calendar} object. date format should be like "25/07/2017" or "07/2017"
     * @param string The date string
     */
    public static Calendar parseDate(String string) {
        final Calendar date = Calendar.getInstance();

        if (TextUtils.isEmpty(string)) {
            return date;
        }

        int year;
        int month;
        int day;

        final String[] dateParts = string.split("/");
        final int length = dateParts.length;
        if (length == 3) {
            day = Integer.parseInt(dateParts[0]);
            month = Integer.parseInt(dateParts[1]);
            year = Integer.parseInt(dateParts[2]);
            date.set(year < 1900 ? date.get(Calendar.YEAR) - 1900 : year,
                    month < 1 || month > 12 ? date.get(Calendar.MONTH) : month,
                    day < 1 || day > 31 ? date.get(Calendar.DAY_OF_MONTH) : day);
        }
        else if (length == 2) {
            month = Integer.parseInt(dateParts[0]);
            year = Integer.parseInt(dateParts[1]);
            date.set(year < 1900 ? date.get(Calendar.YEAR) - 1900 : year,
                    month < 1 || month > 12 ? date.get(Calendar.MONTH) : month, 1);
        }
        else if (length == 1) {
            year = Integer.parseInt(dateParts[0]);
            date.set(year < 1900 ? date.get(Calendar.YEAR) - 1900 : year, 0, 1);
        }

        return date;
    }

    /**
     * Show toast message
     * @param text The message
     */
    public static void showToast(CharSequence text) {
        Toast.makeText(VlApplication.getInstance(), text, Toast.LENGTH_SHORT).show();
    }
}
