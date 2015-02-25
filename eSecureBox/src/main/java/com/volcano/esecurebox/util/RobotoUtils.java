// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.volcano.esecurebox.R;
import com.volcano.esecurebox.VlApplication;

/**
 * Utilities of Roboto typeface.
 */
public final class RobotoUtils {

    public final static int ROBOTO_REGULAR = 0;
    public final static int ROBOTO_MEDIUM = 1;
    public final static int ROBOTO_BLACK_ITALIC = 2;

    private final static Typeface[] mTypefaces = new Typeface[3];

    /**
     * {@link Typeface} initialization using the attributes.
     * @param textView The Roboto text view
     * @param context The context the widget is running on. through which it can
     *                access current theme, resources, etc.
     * @param attrs The attributes of XML tag that is inflating with widget.
     */
    public static void initTypeface(TextView textView, Context context, AttributeSet attrs) {
        final Typeface typeface;
        if (attrs != null) {
            TypedArray a  = context.obtainStyledAttributes(attrs, R.styleable.RobotoTextView);

            if (a.hasValue(R.styleable.RobotoTextView_robotoTypeface)) {
                int typefaceValue = a.getInt(R.styleable.RobotoTextView_robotoTypeface, ROBOTO_REGULAR);
                typeface = RobotoUtils.obtainTypeface(typefaceValue);
            }
            else {
                typeface = obtainTypeface(ROBOTO_REGULAR);
            }

            a.recycle();
        }
        else {
            typeface = obtainTypeface(ROBOTO_REGULAR);
        }

        textView.setPaintFlags(textView.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        textView.setTypeface(typeface);
    }

    /**
     * Obtain {@link Typeface}
     * @param typefaceValue The value of "robotoTypefaceValue" attribute
     * @return specify {@link Typeface}
     * @throws IllegalArgumentException if unknown "robotoTypefaceValue" attribute value.
     */
    public static Typeface obtainTypeface(int robotoTypefaceValue) {
        Typeface typeface = mTypefaces[robotoTypefaceValue];

        if (typeface == null) {
            typeface = createTypeface(robotoTypefaceValue);
            mTypefaces[robotoTypefaceValue] = typeface;
        }

        return typeface;
    }

    /**
     * Create {@link Typeface} from assets.
     * @param robotoTypefaceValue The value of "robotoTypefaceValue" attribute
     * @return Roboto {@link Typeface}
     * @throws IllegalArgumentException if unknown "robotoTypefaceValue" attribute value.
     */
    private static Typeface createTypeface(int robotoTypefaceValue) throws IllegalArgumentException {
        final String typefacePath;

        switch (robotoTypefaceValue) {
            case ROBOTO_REGULAR:
                typefacePath = "fonts/Roboto-Regular.ttf";
                break;
            case ROBOTO_MEDIUM:
                typefacePath = "fonts/Roboto-Medium.ttf";
                break;
            case ROBOTO_BLACK_ITALIC:
                typefacePath = "fonts/Roboto-BlackItalic.ttf";
                break;
            default:
                throw new IllegalArgumentException("Unknown 'robotoTypefaceValue' attribute value " + robotoTypefaceValue);
        }

        return Typeface.createFromAsset(VlApplication.getInstance().getAssets(), typefacePath);
    }
}