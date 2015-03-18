// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.widget;

import android.content.Context;
import android.text.InputType;
import android.text.util.Linkify;
import android.util.AttributeSet;

/**
 * A fotmattable edit text
 */
public final class FormattedEditText extends RobotoEditText {

    public final static int FORMAT_STRING                   = 1;
    public final static int FORMAT_STRING_MULTILINE         = 2;
    public final static int FORMAT_DATE                     = 3;
    public final static int FORMAT_PASSWORD_NUMBER          = 4;   // like Pin
    public final static int FORMAT_PASSWORD                 = 5;
    public final static int FORMAT_URL                      = 6;
    public final static int FORMAT_PHONE                    = 7;
    public final static int FORMAT_ENUM                     = 8;
    public final static int FORMAT_EMAIL                    = 9;
    public final static int FORMAT_NUMBER                   = 10;
    public final static int FORMAT_PASSWORD_NUMBER_VISIBLE  = 11;   // like Pin
    public final static int FORMAT_PASSWORD_VISIBLE         = 12;

    private int mFormatType = FORMAT_STRING;

    public FormattedEditText(Context context) {
        super(context);
        init();
    }

    public FormattedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FormattedEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        applyFormatType();
    }

    /**
     * Set the format type of this edit text
     * @param formatType Use defined constants in this class
     */
    public void setFormatType(int formatType) {
        mFormatType = formatType;
        applyFormatType();
    }

    public int reverseFormatType(int formatType) {
        if (formatType == FORMAT_PASSWORD) {
            return FORMAT_PASSWORD_VISIBLE;
        }
        else if (formatType == FORMAT_PASSWORD_NUMBER) {
            return FORMAT_PASSWORD_NUMBER_VISIBLE;
        }
        return FORMAT_STRING;
    }

    private void applyFormatType() {
        final int defaultInputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
        switch (mFormatType) {
            case FORMAT_STRING:
                setInputType(defaultInputType);
                break;

            case FORMAT_STRING_MULTILINE:
                setInputType(defaultInputType | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                break;

            case FORMAT_PASSWORD:
                setInputType(defaultInputType | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;

            case FORMAT_PASSWORD_VISIBLE:
                setInputType(defaultInputType | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                break;

            case FORMAT_PASSWORD_NUMBER:
                setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                break;

            case FORMAT_PASSWORD_NUMBER_VISIBLE:
                setInputType(InputType.TYPE_CLASS_NUMBER);
                break;

            case FORMAT_URL:
                setInputType(defaultInputType);
                if (!isFocusable()) {
                    Linkify.addLinks(this, Linkify.WEB_URLS);
                }
                break;

            case FORMAT_PHONE:
                setInputType(defaultInputType | InputType.TYPE_CLASS_PHONE);
                break;

            case FORMAT_EMAIL:
                setInputType(defaultInputType | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;

            case FORMAT_NUMBER:
                setInputType(InputType.TYPE_CLASS_NUMBER);
                break;

            default:
                break;
        }
    }
}
