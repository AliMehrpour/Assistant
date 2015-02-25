// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

/**
 * A utility class for working with bitmaps, colors and pictures
 */
public final class BitmapUtils {

    /**
     * Make a color darker
     * @param argb The main color
     * @param value Float value [0...1]
     * @return The darker color code
     */
    public static int getDarkenColor(int argb, float value) {
        return adjustColorBrightness(argb, value);
    }

    /**
     * Make a color lighter
     * @param argb The main color
     * @param value Float value [0...1]
     * @return The lighter color code
     */
    public static int getLightenColor(int argb, float value) {
        return adjustColorBrightness(argb, 1 + value);
    }

    private static int adjustColorBrightness(int argb, float factor) {
        final float[] hsv = new float[3];
        Color.colorToHSV(argb, hsv);

        hsv[2] = Math.min(hsv[2] * factor, 1f);

        return Color.HSVToColor(Color.alpha(argb), hsv);
    }

    /**
     * Get color code from a string
     * @param color The hex color string
     * @return The color code
     */
    public static int getColor(String color) {
        return Color.parseColor(String.format("#%s", color));
    }

    /**
     * Get an identifier by string name
     * @param context The context
     * @param name The identifier name
     * @return The identifier
     */
    public static int getDrawableIdentifier(Context context, String name)
    {
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }

    /**
     * @param color The color
     * @return A {@link android.graphics.drawable.ColorDrawable}
     */
    public static ColorDrawable getColorDrawablr(int color) {
        return new ColorDrawable(color);
    }

}
