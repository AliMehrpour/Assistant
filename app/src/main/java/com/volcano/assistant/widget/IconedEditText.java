package com.volcano.assistant.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.volcano.assistant.R;

/**
 * Created by alimehrpour on 1/19/15.
 */
public class IconedEditText extends RelativeLayout {

    private EditText mEditText;
    private ImageView mIcon;

    public IconedEditText(Context context) {
        this(context, null);
    }

    public IconedEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconedEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_edittext_image, this, true);

        mEditText = (EditText) findViewById(R.id.edittext);
        mIcon = (ImageView) findViewById(R.id.image_indicator);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconedEditText);
        try {
            final boolean showIcon = a.getBoolean(R.styleable.IconedEditText_showIcon, true);
            if (!showIcon) {
                mIcon.setVisibility(View.GONE);
                mEditText.setPadding(getResources().getDimensionPixelSize(R.dimen.margin_16), 0, 0, 0);
            }

            final String hint = a.getString(R.styleable.IconedEditText_hint);
            setHint(hint);
        }
        finally {
            a.recycle();
        }

    }

    public EditText getEditText() {
        return mEditText;
    }

    public void setHint(CharSequence hint) {
        mEditText.setHint(hint);
    }

    public void setmIcon(Drawable drawable) {
        mIcon.setImageDrawable(drawable);
    }
}
