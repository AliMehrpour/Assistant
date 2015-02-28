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

    public final static int FORMAT_STRING             = 1;
    public final static int FORMAT_STRING_MULTILINE   = 2;
    public final static int FORMAT_DATE               = 3;
    public final static int FORMAT_TIME               = 4;
    public final static int FORMAT_PASSWORD           = 5;
    public final static int FORMAT_URL                = 6;
    public final static int FORMAT_PHONE              = 7;
    public final static int FORMAT_ENUM               = 8;

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

    private void applyFormatType() {
        switch (mFormatType) {
            case FORMAT_STRING:
                setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                setMaxLines(1);
                break;

            case FORMAT_STRING_MULTILINE:
                setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                break;

            case FORMAT_PASSWORD:
                setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                setMaxLines(1);
                break;

            case FORMAT_URL:
                setInputType(InputType.TYPE_CLASS_TEXT);
                if (!isFocusable()) {
                    Linkify.addLinks(this, Linkify.WEB_URLS);
                }
                break;

            default:
                break;
        }
    }
}
