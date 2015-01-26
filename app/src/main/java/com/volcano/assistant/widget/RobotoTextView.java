package com.volcano.assistant.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.volcano.assistant.util.RobotoUtils;

/**
 * Implementation of a {@link android.widget.TextView} with support for the Roboto fonts.
 */
public class RobotoTextView extends TextView {
    public RobotoTextView(Context context) {
        this(context, null);
    }

    public RobotoTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RobotoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (!isInEditMode()) {
            RobotoUtils.initTypeface(this, context, attrs);
        }
    }
}
