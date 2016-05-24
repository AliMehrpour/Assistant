package com.volcano.esecurebox.util;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.TextView;

import com.volcano.esecurebox.VlApplication;

/**
 * Contains functions that call the appropriate SDK level function.
 */
public final class CompatUtils {
    /**
     * @see Resources#getColor(int)
     */
    public static int getColor(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return VlApplication.getInstance().getResources().getColor(id, null);
        }
        else {
            //noinspection deprecation
            return VlApplication.getInstance().getResources().getColor(id);
        }
    }

    /**
     * @see Resources#getDrawable(int)
     */
    public static Drawable getDrawable(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return VlApplication.getInstance().getResources().getDrawable(id, null);
        }
        else {
            //noinspection deprecation
            return VlApplication.getInstance().getResources().getDrawable(id);
        }
    }

    public static void setTextAppearance(TextView textview, int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textview.setTextAppearance(resId);
        }
        else {
            //noinspection deprecation
            textview.setTextAppearance(textview.getContext(), resId);
        }
    }
}
