// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.volcano.esecurebox.Managers;
import com.volcano.esecurebox.R;
import com.volcano.esecurebox.analytics.MixpanelManager;
import com.volcano.esecurebox.model.Field;
import com.volcano.esecurebox.model.FieldTypeValue;
import com.volcano.esecurebox.model.SubCategory;
import com.volcano.esecurebox.security.PasswordGenerator;
import com.volcano.esecurebox.util.BitmapUtils;
import com.volcano.esecurebox.util.LogUtils;
import com.volcano.esecurebox.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains a Floating Edit Text with two action available
 */
public final class FieldCell extends LinearLayout {
    private final String TAG = LogUtils.makeLogTag(FieldCell.class.getSimpleName());

    private static final int ACTION_GENERATE_PASSWORD = 1;
    private static final int ACTION_SHOW_LIST         = 2;
    private static final int ACTION_VISIBLE_PASSWORD  = 3;

    private ImageView mIcon;
    private RobotoTextView mHintTextView;
    private FormattedEditText mEditText;
    private ImageView mAction1Button;
    private ImageView mAction2Button;
    private View mDividerLine;

    private final ArrayList<String> mListItems = new ArrayList<>();
    private boolean mVisiblePassword = false;
    private int mFormatType;
    private int mAction1;
    private int mAction2;

    public FieldCell(Context context) {
        this(context, null);
    }

    public FieldCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FieldCell(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_field_cell, this, true);

        mIcon = (ImageView) findViewById(R.id.icon);
        mHintTextView = (RobotoTextView) findViewById(R.id.text_hint);
        mEditText = (FormattedEditText) findViewById(R.id.edittext);
        mAction1Button = (ImageView) findViewById(R.id.button_action_1);
        mAction2Button = (ImageView) findViewById(R.id.button_action_2);
        mDividerLine = findViewById(R.id.divider_line);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FieldCell);

        final boolean showIcon = a.getBoolean(R.styleable.FieldCell_fleShowIcon, true);
        if (!showIcon) {
            mIcon.setVisibility(View.GONE);
            mEditText.setPadding(getResources().getDimensionPixelSize(R.dimen.margin_16), 0, 0, 0);
            mHintTextView.setPadding(getResources().getDimensionPixelSize(R.dimen.margin_16), 0, 0, 0);
        }

        //mHintTextView.setAlpha(0);
        mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                onFocusChanged(hasFocus);
            }
        });
        setHint(a.getString(R.styleable.FieldCell_fleHint));
        mHintTextView.setAlpha(1f);
        ObjectAnimator.ofFloat(mHintTextView, "alpha", 1f, 0.5f).start();

        a.recycle();

        mAction1Button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAction1 == ACTION_VISIBLE_PASSWORD) {
                    toggleEyeButton();
                }
                else if (mAction1 == ACTION_SHOW_LIST && mListItems.size() > 0) {
                    new AlertDialogWrapper.Builder(getContext())
                            .setTitle(mHintTextView.getText())
                            .setItems(mListItems.toArray(new CharSequence[mListItems.size()]),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mEditText.setText(mListItems.get(which));
                                        }
                                    })
                            .show();
                }
            }
        });

        mAction2Button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAction2 == ACTION_GENERATE_PASSWORD) {
                    Managers.getMixpanelManager().track(MixpanelManager.EVENT_GENERATE_PASSWORD);

                    final boolean isPassword = mFormatType == FormattedEditText.FORMAT_PASSWORD;
                    final boolean isNumberPassword = mFormatType == FormattedEditText.FORMAT_PASSWORD_NUMBER;

                    mEditText.setText(
                            new PasswordGenerator().generate(isPassword ? PasswordGenerator.PASSWORD_LENGTH_DEFAULT : PasswordGenerator.PASSWORD_LENGTH_NUMBER,
                            isPassword || isNumberPassword, isPassword, isPassword));
                }
            }
        });
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (!enabled) {
            mEditText.setKeyListener(null);
            mEditText.setFocusable(false);
            showMenuOnLongClick();
            setDividerLineVisibility(View.INVISIBLE);
        }
    }

    /**
     * @return The field value
     */
    public String getValue() {
        return mEditText.getText().toString().trim();
    }

    /**
     * Set the field
     * @param field The field
     * @param value The value
     */
    public void setField(Field field, String value) {
        setField(field, value, false);
    }

    /**
     * Set the field
     * @param field The field
     * @param value The value
     * @param readonly True if field is readonly, false otherwise
     */
    public void setField(Field field, String value, boolean readonly) {
        setIcon(field.getIconName(), field.getName().charAt(0), getResources().getColor(R.color.grey_1));
        setText(value);
        setHint(field.getName());

        if (readonly) {
            setEnabled(false);
        }
        else {
            setFormatType(field.getFormat());

            if (field.getFormat() == Field.FORMAT_ENUM) {
                FieldTypeValue.getValueByField(FieldCell.class, field, new FindCallback<FieldTypeValue>() {
                    @Override
                    public void done(List<FieldTypeValue> fieldTypeValues, ParseException e) {
                        if (e == null) {
                            final ArrayList<String> values = new ArrayList<>();
                            for (FieldTypeValue value : fieldTypeValues) {
                                values.add(value.getValue());
                            }
                            setListItems(values);
                        }
                        else {
                            LogUtils.LogE(TAG, "Load field values failed");
                        }
                    }
                });
            }
        }
    }

    /**
     * Set the subcategory
     * @param subCategory The sub category
     */
    public void setSubCategory(SubCategory subCategory) {
        setEnabled(false);
        setHint(getResources().getString(R.string.label_category));
        setText(subCategory.getName());
        setIcon(subCategory.getIconName(), null, BitmapUtils.getColor(subCategory.getCategory().getColor()));
    }

    private void setText(String text) {
        mEditText.setText(text);
    }

    private void setHint(String hint) {
        mEditText.setHint(hint);
        mHintTextView.setText(hint);
    }

    private void setIcon(Drawable drawable) {
        mIcon.setVisibility(View.VISIBLE);
        mIcon.setImageDrawable(drawable);
    }

    private void setIcon(String iconName, Character ch, int color) {
        int resourceId = 0;
        if (iconName != null) {
            resourceId = BitmapUtils.getDrawableIdentifier(getContext(), iconName);
        }

        if (resourceId != 0) {
            //noinspection deprecation
            setIcon(getResources().getDrawable(resourceId));
        }
        else if (ch != null) {
            setIcon(new CircleDrawable(Color.TRANSPARENT, CircleDrawable.FILL, ch.toString(), color));
        }
        else {
            setIcon(new CircleDrawable(color, CircleDrawable.FILL));
        }
    }

    private void setFormatType(int formatType) {
        mFormatType = formatType;

        mEditText.setFormatType(formatType);
        setEyeAction();
        setGeneratePasswordAction();
        setListAction();
    }

    private void setDividerLineVisibility(int visibility) {
        mDividerLine.setVisibility(visibility);
    }

    private void setListItems(ArrayList<String> items) {
        mListItems.clear();
        mListItems.addAll(items);
    }

    private void onFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            ObjectAnimator.ofFloat(mHintTextView, "alpha", 0.5f, 1f).start();
        }
        else {
            mHintTextView.setAlpha(1f);
            ObjectAnimator.ofFloat(mHintTextView, "alpha", 1f, 0.5f).start();
        }
    }

    @SuppressWarnings("deprecation")
    private void setEyeAction() {
        final boolean isPassword = mFormatType == FormattedEditText.FORMAT_PASSWORD || mFormatType == FormattedEditText.FORMAT_PASSWORD_NUMBER;
        if (isPassword) {
            final boolean isFocusable = mEditText.isFocusable();
            final boolean isEmpty = TextUtils.isEmpty(mEditText.getText());
            if (isFocusable || !isEmpty) {
                mAction1 = ACTION_VISIBLE_PASSWORD;

                mAction1Button.setImageDrawable(getResources().getDrawable(R.drawable.icon_eye_open));
                mAction1Button.setVisibility(VISIBLE);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void toggleEyeButton()  {
        if (mVisiblePassword) {
            final int lastSelection = mEditText.getSelectionStart();
            mEditText.setFormatType(mFormatType);
            mAction1Button.setImageDrawable(getResources().getDrawable(R.drawable.icon_eye_open));
            mEditText.setSelection(lastSelection);
            mVisiblePassword = false;
        }
        else {
            final int lastSelection = mEditText.getSelectionStart();
            mEditText.setFormatType(mEditText.reverseFormatType(mFormatType));
            mAction1Button.setImageDrawable(getResources().getDrawable(R.drawable.icon_eye_closed));
            mEditText.setSelection(lastSelection);
            mVisiblePassword = true;
        }
    }

    @SuppressWarnings("deprecation")
    private void setGeneratePasswordAction() {
        if ((mFormatType == FormattedEditText.FORMAT_PASSWORD ||
                mFormatType == FormattedEditText.FORMAT_PASSWORD_NUMBER) && mEditText.isFocusable()) {
            mAction2 = ACTION_GENERATE_PASSWORD;

            mAction2Button.setImageDrawable(getResources().getDrawable(R.drawable.icon_generate_password));
            mAction2Button.setVisibility(VISIBLE);
        }
    }

    @SuppressWarnings("deprecation")
    private void setListAction() {
        if (mFormatType == FormattedEditText.FORMAT_ENUM) {
            mAction1 = ACTION_SHOW_LIST;

            mAction1Button.setImageDrawable(getResources().getDrawable(R.drawable.icon_list));
            mAction1Button.setVisibility(VISIBLE);
        }
    }

    private void showMenuOnLongClick() {
        mEditText.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!mEditText.isFocusable() && !TextUtils.isEmpty(mEditText.getText())) {
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
