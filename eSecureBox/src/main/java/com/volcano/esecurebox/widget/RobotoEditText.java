// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.volcano.esecurebox.util.RobotoUtils;

/**
 * A edittext that supports roboto font
 */
public class RobotoEditText extends EditText {

    public RobotoEditText(Context context) {
        super(context);
    }

    public RobotoEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        RobotoUtils.initTypeface(this, context, attrs);
    }

    public RobotoEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        RobotoUtils.initTypeface(this, context, attrs);
    }

}
