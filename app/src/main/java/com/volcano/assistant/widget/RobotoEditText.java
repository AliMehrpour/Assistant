package com.volcano.assistant.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.volcano.assistant.utils.RobotoUtils;

/**
 * Created by alimehrpour on 12/31/14.
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
