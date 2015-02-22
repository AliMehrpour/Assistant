// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nineoldandroids.view.animation.AnimatorProxy;
import com.volcano.assistant.R;
import com.volcano.assistant.util.LogUtils;
import com.volcano.assistant.util.Utils;

/**
 * Created by alimehrpour on 1/13/15.
 */
public class FloatingLabeledEditText extends LinearLayout {
    private static final String TAG = LogUtils.makeLogTag(FloatingLabeledEditText.class);

    private ImageView mIndicatorImage;
    private RobotoTextView mHintTextView;
    private RobotoEditText mEditText;
    private View mDividerLine;

    public FloatingLabeledEditText(Context context) {
        this(context, null);
    }

    public FloatingLabeledEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingLabeledEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_float_edittext, this, true);

        mIndicatorImage = (ImageView) findViewById(R.id.image_indicator);
        mHintTextView = (RobotoTextView) findViewById(R.id.text_hint);
        mEditText = (RobotoEditText) findViewById(R.id.edittext);
        mDividerLine = findViewById(R.id.divider_line);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FloatingLabeledEditText);
        try {
            final String hint = a.getString(R.styleable.FloatingLabeledEditText_floatHint);

            final Drawable background = a.getDrawable(R.styleable.FloatingLabeledEditText_floatBackground);
            if (background != null) {
                setHintBackground(background);
            }

            AnimatorProxy.wrap(mHintTextView).setAlpha(0);
            setHint(hint);
            setEditText(mEditText);
        }
        finally {
            a.recycle();
        }
    }

    @SuppressWarnings("NewApi")
    private void setHintBackground(Drawable background) {
        if (Utils.hasJellyBeanApi()) {
            mHintTextView.setBackground(background);
        }
        else {
            mHintTextView.setBackgroundDrawable(background);
        }
    }

    private void setEditText(RobotoEditText editText) {
        mEditText = editText;

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setShowHint(!TextUtils.isEmpty(s));
            }
        });
        mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                onFocusChanged(hasFocus);
            }
        });

        mHintTextView.setText(mEditText.getHint());
        if (!TextUtils.isEmpty(mEditText.getText())) {
            mHintTextView.setVisibility(VISIBLE);
        }
    }

    private void onFocusChanged(boolean hasFocus) {
        if (hasFocus && mHintTextView.getVisibility() == VISIBLE) {
            ObjectAnimator.ofFloat(mHintTextView, "alpha", 0.5f, 1f).start();
        }
        else if (mHintTextView.getVisibility() == VISIBLE) {
            AnimatorProxy.wrap(mHintTextView).setAlpha(1f);
            ObjectAnimator.ofFloat(mHintTextView, "alpha", 1f, 0.5f).start();
        }
    }

    private void setShowHint(final boolean show) {
        final AnimatorSet animation = new AnimatorSet();
        if (mHintTextView.getVisibility() == VISIBLE && !show) {
            final ObjectAnimator move = ObjectAnimator.ofFloat(mHintTextView, "translationY", 0, mHintTextView.getHeight() / 8);
            final ObjectAnimator fade = ObjectAnimator.ofFloat(mHintTextView, "alpha", 1, 0);
            animation.playTogether(move, fade);
        }
        else if (mHintTextView.getVisibility() != VISIBLE && show) {
            final ObjectAnimator move = ObjectAnimator.ofFloat(mHintTextView, "translationY", mHintTextView.getHeight() / 8, 0);
            ObjectAnimator fade;
            if (mEditText.isFocused()) {
                fade = ObjectAnimator.ofFloat(mHintTextView, "alpha", 0, 1f);
            }
            else {
                fade = ObjectAnimator.ofFloat(mHintTextView, "alpha", 0, 0.33f);
            }
            animation.playTogether(move, fade);
        }

        animation.addListener(new AnimatorListenerAdapter() {
            @Override
             public void onAnimationEnd(Animator animation) {
                 super.onAnimationEnd(animation);
                 mHintTextView.setVisibility(show ? VISIBLE : INVISIBLE);
                AnimatorProxy.wrap(mHintTextView).setAlpha(show ? 1 : 0);
             }

             @Override
             public void onAnimationStart(Animator animation) {
                 super.onAnimationStart(animation);
                 mHintTextView.setVisibility(VISIBLE);
             }
        });
        animation.start();
    }

    public void setHint(String hint) {
        mEditText.setHint(hint);
        mHintTextView.setText(hint);
    }

    public void setText(String text) {
        mEditText.setText(text);
    }

    @Override
    public void setEnabled(boolean enabled) {
        mEditText.setEnabled(enabled);
    }

    /**
     * Set Divider line visibility. use one of {@link android.view.View#VISIBLE} or {@link android.view.View#INVISIBLE}
     * or {@link android.view.View#GONE}
     * @param visibility The visibility
     */
    public void setDividerLineVisibility(int visibility) {
        mDividerLine.setVisibility(visibility);
    }

    public void setIcon(Drawable drawable) {
        mIndicatorImage.setVisibility(View.VISIBLE);
        mIndicatorImage.setImageDrawable(drawable);
    }

}
