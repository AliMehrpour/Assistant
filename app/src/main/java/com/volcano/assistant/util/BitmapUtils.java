// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.util;

import android.content.Context;
import android.graphics.Color;

/**
 * A Utility class for work with bitmaps and generally picutres
 */
public final class BitmapUtils {

    /**
     * Make a color darker
     * @param color The main color
     * @param value Value [0...1]
     * @return The darker color code
     */
    public static int darkenColor(int color, float value) {
        final float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= value;
        return Color.HSVToColor(hsv);
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
}
