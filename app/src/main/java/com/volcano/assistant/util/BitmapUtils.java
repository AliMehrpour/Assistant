package com.volcano.assistant.util;

import android.graphics.Color;

/**
 * Created by alimehrpour on 1/13/15.
 */
public final class BitmapUtils {

    /**
     * Make a color darker
     * @param color The main color
     * @param value Value [0...1]
     * @return
     */
    public static int darkenColor(int color, float value) {
        final float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= value;
        return Color.HSVToColor(hsv);
    }

    public static int getColor(String color) {
        return Color.parseColor(String.format("#%s", color));
    }
}
