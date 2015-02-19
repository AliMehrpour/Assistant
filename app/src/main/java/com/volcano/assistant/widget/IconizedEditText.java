// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.volcano.assistant.R;

/**
 * A Layout used for enter data and include an primary EditText
 * for enter usage and a image indicator or text indicator
 */
public class IconizedEditText extends RelativeLayout {

    private EditText mEditText;
    private ImageView mIcon;

    public IconizedEditText(Context context) {
        this(context, null);
    }

    public IconizedEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconizedEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_iconed_edittext, this, true);

        mEditText = (EditText) findViewById(R.id.edittext);
        mIcon = (ImageView) findViewById(R.id.image_indicator);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconizedEditText);
        try {
            final boolean showIcon = a.getBoolean(R.styleable.IconizedEditText_showIcon, true);
            if (!showIcon) {
                mIcon.setVisibility(View.GONE);
                mEditText.setPadding(getResources().getDimensionPixelSize(R.dimen.margin_16), 0, 0, 0);
            }

            final String hint = a.getString(R.styleable.IconizedEditText_hint);
            setHint(hint);
        }
        finally {
            a.recycle();
        }

    }

    public void setText(String text) {
        mEditText.setText(text);
    }

    public void setHint(CharSequence hint) {
        mEditText.setHint(hint);
    }

    public void setIcon(Drawable drawable) {
        mIcon.setImageDrawable(drawable);
    }

    public Editable getText() {
        return mEditText.getText();
    }

    public EditText getEditText() {
        return mEditText;
    }
}
