// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.volcano.esecurebox.R;
import com.volcano.esecurebox.util.Utils;

/**
 * Floating label edit text
 */
public class FloatingLabeledEditText extends LinearLayout {

    private ImageView mIcon;
    private RobotoTextView mHintTextView;
    private FormattedEditText mEditText;
    private ImageView mEyeButton;
    private View mDividerLine;

    private boolean mVisiblePassword = false;
    private int mFormatType;

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

        mIcon = (ImageView) findViewById(R.id.icon);
        mHintTextView = (RobotoTextView) findViewById(R.id.text_hint);
        mEditText = (FormattedEditText) findViewById(R.id.edittext);
        mEyeButton = (ImageView) findViewById(R.id.button_eye);
        mDividerLine = findViewById(R.id.divider_line);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FloatingLabeledEditText);
        try {
            final boolean showIcon = a.getBoolean(R.styleable.FloatingLabeledEditText_fleShowIcon, true);
            if (!showIcon) {
                mIcon.setVisibility(View.GONE);
                mEditText.setPadding(getResources().getDimensionPixelSize(R.dimen.margin_16), 0, 0, 0);
                mHintTextView.setPadding(getResources().getDimensionPixelSize(R.dimen.margin_16), 0, 0, 0);
            }

            final Drawable background = a.getDrawable(R.styleable.FloatingLabeledEditText_fleBackground);
            if (background != null) {
                setHintBackground(background);
            }

            //mHintTextView.setAlpha(0);
            setEditText(mEditText);
            setHint(a.getString(R.styleable.FloatingLabeledEditText_fleHint));
            mHintTextView.setAlpha(1f);
            ObjectAnimator.ofFloat(mHintTextView, "alpha", 1f, 0.5f).start();
        }
        finally {
            a.recycle();
        }

        mEyeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEyeButton();
            }
        });
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (!enabled) {
            mEditText.setKeyListener(null);
            mEditText.setFocusable(false);
            showMenuOnLongClick();
        }
    }

    public String getText() {
        return mEditText.getText().toString().trim();
    }

    public void setText(String text) {
        mEditText.setText(text);
    }

    public void setHint(String hint) {
        mEditText.setHint(hint);
        mHintTextView.setText(hint);
    }

    /**
     * Set icon
     * @param drawable The darwable
     */
    public void setIcon(Drawable drawable) {
        mIcon.setVisibility(View.VISIBLE);
        mIcon.setImageDrawable(drawable);
    }

    /**
     * Set format type
     * @param formatType See {@link com.volcano.esecurebox.widget.FormattedEditText} for possible values
     */
    public void setFormatType(int formatType) {
        mEditText.setFormatType(formatType);
        mFormatType = formatType;
        setEyeButtonVisibility();
    }

    /**
     * Set Divider line visibility. use one of {@link android.view.View#VISIBLE} or {@link android.view.View#INVISIBLE}
     * or {@link android.view.View#GONE}
     * @param visibility The visibility
     */
    public void setDividerLineVisibility(int visibility) {
        mDividerLine.setVisibility(visibility);
    }

    private void setHintBackground(Drawable background) {
        if (Utils.hasJellyBeanApi()) {
            mHintTextView.setBackground(background);
        }
        else {
            //noinspection deprecation
            mHintTextView.setBackgroundDrawable(background);
        }
    }

    private void onFocusChanged(boolean hasFocus) {
        if (hasFocus && mHintTextView.getVisibility() == VISIBLE) {
            ObjectAnimator.ofFloat(mHintTextView, "alpha", 0.5f, 1f).start();
        }
        else if (mHintTextView.getVisibility() == VISIBLE) {
            mHintTextView.setAlpha(1f);
            ObjectAnimator.ofFloat(mHintTextView, "alpha", 1f, 0.5f).start();
        }
    }

    private void setEyeButtonVisibility() {
        if (mFormatType == FormattedEditText.FORMAT_PASSWORD) {
            if (!mEditText.getText().toString().trim().isEmpty()) {
                mEyeButton.setVisibility(VISIBLE);
            }
            else {
                mEyeButton.setVisibility(GONE);
            }
        }
    }

    private void toggleEyeButton() {
        if (mVisiblePassword) {
            final int lastSelection = mEditText.getSelectionStart();
            mEditText.setFormatType(FormattedEditText.FORMAT_PASSWORD);
            mEyeButton.setImageDrawable(getResources().getDrawable(R.drawable.icon_action_eye_open));
            mEditText.setSelection(lastSelection);
            mVisiblePassword = false;
        }
        else {
            final int lastSelection = mEditText.getSelectionStart();
            mEditText.setFormatType(FormattedEditText.FORMAT_STRING);
            mEyeButton.setImageDrawable(getResources().getDrawable(R.drawable.icon_action_eye_closed));
            mEditText.setSelection(lastSelection);
            mVisiblePassword = true;
        }
    }

    private void showMenuOnLongClick() {
        mEditText.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isEnabled() && !TextUtils.isEmpty(mEditText.getText())) {
                    v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

                    new AlertDialogWrapper.Builder(getContext())
                            .setItems(R.array.array_field_actions, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Utils.copyToClipboard(null, mEditText.getText().toString());
                                    Utils.showToast(R.string.toast_copy_to_clipboard);
                                }
                            })
                            .show();
                }
                return true;
            }
        });

    }

    private void setEditText(FormattedEditText editText) {
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
                //setShowHint(!TextUtils.isEmpty(s));
                setEyeButtonVisibility();
            }
        });

        /*
        if (!TextUtils.isEmpty(mEditText.getText())) {
            mHintTextView.setVisibility(VISIBLE);
        }
        */

        mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                onFocusChanged(hasFocus);
            }
        });
    }

    @SuppressWarnings("UnusedDeclaration")
    private void setShowHint(final boolean show) {
        mHintTextView.setVisibility(show ? VISIBLE : INVISIBLE);
        mHintTextView.setAlpha(show ? 1 : 0);

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
                 mHintTextView.setAlpha(show ? 1 : 0);
             }

             @Override
             public void onAnimationStart(Animator animation) {
                 super.onAnimationStart(animation);
                 mHintTextView.setVisibility(VISIBLE);
             }
        });
        animation.start();
    }
}
