// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
import com.volcano.esecurebox.util.CompatUtils;
import com.volcano.esecurebox.util.LogUtils;
import com.volcano.esecurebox.util.Utils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Contains a Floating Edit Text with two action available
 */
public final class FieldCell extends FrameLayout {
    private final String TAG = LogUtils.makeLogTag(FieldCell.class.getSimpleName());

    public final static int TYPE_STRING                  = 1;
    public final static int TYPE_STRING_MULTILINE        = 2;
    public final static int TYPE_DATE                    = 3;
    public final static int TYPE_PASSWORD_NUMBER         = 4;
    public final static int TYPE_PASSWORD                = 5;
    public final static int TYPE_URL                     = 6;
    public final static int TYPE_PHONE                   = 7;
    public final static int TYPE_ENUM                    = 8;
    public final static int TYPE_EMAIL                   = 9;
    public final static int TYPE_NUMBER                  = 10;
    public final static int TYPE_PASSWORD_NUMBER_VISIBLE = 11;
    public final static int TYPE_PASSWORD_VISIBLE        = 12;

    private static final int SWIPE_TO_REMOVE_X_THRESHOLD = 250;
    private static final int SWIPE_TO_REMOVE_MILLIS      = 400;

    private static final int ACTION_GENERATE_PASSWORD    = 1;
    private static final int ACTION_SHOW_LIST            = 2;
    private static final int ACTION_VISIBLE_PASSWORD     = 3;
    private static final int ACTION_PICK_DATE            = 4;

    private ImageView mIcon;
    private View mDividerLine;
    private RobotoEditText mEditText;
    private ImageView mAction1Button;
    private ImageView mAction2Button;
    private RobotoTextView mHintTextView;
    private RelativeLayout mViewsLayout;
    private FrameLayout.LayoutParams mLayoutParams;

    private final ArrayList<String> mListItems = new ArrayList<>();
    private boolean mVisiblePassword = false;
    private int mInputType;
    private int mAction1;
    private int mAction2;
    private boolean mReadOnly;
    private Field mField;
    private String mOriginalValue = "";
    private int mIndex;

    private int mStartX;
    private int mDelta;
    private boolean mSwipeEnabled;

    private OnFieldSwipeListener mSwipeListener;

    /**
     * Interface callback to be implemented of swipe status
     */
    public interface OnFieldSwipeListener {
        /**
         * Called when swipe has been completed
         * @param fieldCell The removed {@link FieldCell}
         */
        void onSwiped(FieldCell fieldCell);

        /**
         * Called when swipe started
         */
        void onSwipeStarted();

        /**
         * Called when swipe cancelled
         */
        void onSwipeCanceled();
    }

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

    private void init(final Context context, AttributeSet attrs) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_field_cell, this, true);

        mViewsLayout = (RelativeLayout) findViewById(R.id.layout_views);
        mIcon = (ImageView) findViewById(R.id.icon);
        mHintTextView = (RobotoTextView) findViewById(R.id.text_hint);
        mEditText = (RobotoEditText) findViewById(R.id.edit_value);
        mAction1Button = (ImageView) findViewById(R.id.button_action_1);
        mAction2Button = (ImageView) findViewById(R.id.button_action_2);
        mDividerLine = findViewById(R.id.divider_line);

        mLayoutParams = (FrameLayout.LayoutParams) mViewsLayout.getLayoutParams();

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FieldCell);
        final boolean showIcon = a.getBoolean(R.styleable.FieldCell_fleShowIcon, true);
        if (!showIcon) {
            mIcon.setVisibility(View.GONE);
            mEditText.setPadding(getResources().getDimensionPixelSize(R.dimen.margin_16), 0, 0, 0);
            mHintTextView.setPadding(getResources().getDimensionPixelSize(R.dimen.margin_16), 0, 0, 0);
        }

        mHintTextView.setAlpha(0);
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
                            .setItems(mListItems.toArray(new CharSequence[mListItems.size()]), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mEditText.setText(mListItems.get(which));
                                }
                            }).show();
                }
                else if (mAction1 == ACTION_PICK_DATE) {
                    final Calendar now = Utils.parseDate(mEditText.getText().toString());
                    final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                    mEditText.setText(Utils.getCompleteDate(year, monthOfYear, dayOfMonth));
                                }
                            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.vibrate(true);
                    datePickerDialog.setHighlightedDays(new Calendar[]{now});
                    datePickerDialog.show(((Activity) context).getFragmentManager(), "DatePickerDialog");
                }
            }
        });

        mAction2Button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAction2 == ACTION_GENERATE_PASSWORD) {
                    Managers.getMixpanelManager().track(MixpanelManager.EVENT_GENERATE_PASSWORD);

                    final boolean isPassword = (mInputType == TYPE_PASSWORD || mInputType == TYPE_PASSWORD_VISIBLE);
                    final boolean isNumberPassword = (mInputType == TYPE_PASSWORD_NUMBER || mInputType == TYPE_PASSWORD_NUMBER_VISIBLE);

                    mEditText.setText(new PasswordGenerator().generate(isPassword ? PasswordGenerator.PASSWORD_LENGTH_DEFAULT : PasswordGenerator.PASSWORD_LENGTH_NUMBER, isPassword || isNumberPassword, isPassword, isPassword));
                }
            }
        });
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public boolean onTouchEvent(MotionEvent event) {
        if (mSwipeEnabled) {
            final float X = event.getX();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mStartX = (int) X;
                    break;

                case MotionEvent.ACTION_MOVE:
                    mDelta = (int) (X - mStartX);
                    if (mDelta > 0) {
                        moveViewToRight(mDelta);
                        mSwipeListener.onSwipeStarted();
                    }
                    break;

                case MotionEvent.ACTION_OUTSIDE:
                case MotionEvent.ACTION_UP:
                    if (mDelta > SWIPE_TO_REMOVE_X_THRESHOLD) {
                        mStartX = 0;
                        mDelta = 0;

                        mViewsLayout.animate()
                                .translationX(Utils.getDisplaySize(getContext()).x)
                                .setDuration(SWIPE_TO_REMOVE_MILLIS)
                                .setInterpolator(new DecelerateInterpolator())
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        removeItself();
                                    }
                                });
                    }
                    else {
                        mStartX = 0;
                        mDelta = 0;

                        final ValueAnimator animator = ValueAnimator.ofInt(mLayoutParams.leftMargin, 0);
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                final int leftMargin = (Integer) valueAnimator.getAnimatedValue();
                                moveViewToRight(leftMargin);
                            }
                        });
                        animator.setDuration(mLayoutParams.leftMargin);
                        animator.start();
                        mSwipeListener.onSwipeCanceled();
                    }
                    break;
            }

            return true;
        }

        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (!enabled) {
            mEditText.setKeyListener(null);
            mEditText.setFocusable(false);
            setOnLongClickMenu();
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
     * @return True if orignial value has been changes, otherwise false
     */
    public boolean isValueChanged() {
        return !mEditText.getText().toString().equals(mOriginalValue);
    }

    /**
     * Set the field
     * @param field The field
     * @param value The value
     */
    public void setField(Field field, String value, int index) {
        setField(field, value, index, false);
    }

    /**
     * Set the field
     * @param field The field
     * @param value The value
     * @param readonly True if field is readonly, false otherwise
     */
    public void setField(Field field, String value, int index, boolean readonly) {
        mReadOnly = readonly;
        mField = field;
        mOriginalValue = value;
        mIndex = index;

        setEnabled(!readonly);
        setIcon(field.getIconName(), field.getName().charAt(0), CompatUtils.getColor(R.color.grey_1));
        setText(value);
        setHint(field.getName());
        setInputType(field.getType());
        setActions();
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

    /**
     * Set {@link OnFieldSwipeListener}
     * @param listener The listener
     */
    public void setOnSwipeListener(OnFieldSwipeListener listener) {
        mSwipeListener = listener;
    }

    /**
     * Reset view position
     */
    public void resetPosition() {
        mViewsLayout.setX(mLayoutParams.leftMargin);
        moveViewToRight(0);
    }

    /**
     * Enable/disable swipe to right
     * @param enable True to enable
     */
    public void setSwipeEnabled(boolean enable) {
        mSwipeEnabled = enable;
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

    private void setActions() {
        setEyeAction();

        if (!mReadOnly) {
            setGeneratePasswordAction();
            setListAction();
            setDateAction();
        }
    }

    private void setDividerLineVisibility(int visibility) {
        mDividerLine.setVisibility(visibility);
    }

    private void setEyeAction() {
        final boolean isPassword = mInputType == TYPE_PASSWORD || mInputType == TYPE_PASSWORD_NUMBER;
        if (isPassword) {
            final boolean isFocusable = mEditText.isFocusable();
            final boolean isEmpty = TextUtils.isEmpty(mEditText.getText());
            if (isFocusable || !isEmpty) {
                mAction1 = ACTION_VISIBLE_PASSWORD;

                mAction1Button.setImageResource(R.drawable.icon_eye_open);
                mAction1Button.setVisibility(VISIBLE);
            }
        }
    }

    private void toggleEyeButton()  {
        mAction1Button.setImageResource(mVisiblePassword ? R.drawable.icon_eye_open : R.drawable.icon_eye_closed);
        mVisiblePassword = !mVisiblePassword;

        final int lastSelection = mEditText.getSelectionStart();
        setInputType(reverseInputTypeVisibility(mInputType));
        mEditText.setSelection(lastSelection);
    }

    private void setGeneratePasswordAction() {
        if ((mInputType == TYPE_PASSWORD ||
                mInputType == TYPE_PASSWORD_NUMBER) && mEditText.isFocusable()) {
            mAction2 = ACTION_GENERATE_PASSWORD;

            mAction2Button.setImageResource(R.drawable.icon_generate_password);
            mAction2Button.setVisibility(VISIBLE);
        }
    }

    private void setListAction() {
        if (mInputType == TYPE_ENUM) {
            mAction1 = ACTION_SHOW_LIST;

            FieldTypeValue.getValueByField(FieldCell.class, mField, new FindCallback<FieldTypeValue>() {
                @Override
                public void done(List<FieldTypeValue> fieldTypeValues, ParseException e) {
                    if (e == null) {
                        final ArrayList<String> items = new ArrayList<>();
                        for (final FieldTypeValue value : fieldTypeValues) {
                            items.add(value.getValue());
                        }

                        mListItems.clear();
                        mListItems.addAll(items);

                        mAction1Button.setImageResource(R.drawable.icon_list);
                        mAction1Button.setVisibility(VISIBLE);
                    }
                    else {
                        LogUtils.LogE(TAG, "Load field values failed");
                        mAction1Button.setVisibility(INVISIBLE);
                    }
                }
            });
        }
    }

    private void setDateAction() {
        if (mInputType == TYPE_DATE) {
            mAction1 = ACTION_PICK_DATE;

            mAction1Button.setImageResource(R.drawable.icon_pick_date);
            mAction1Button.setVisibility(VISIBLE);
        }
    }

    private void setInputType(int inputType) {
        mInputType = inputType;

        final int defaultInputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
        switch (mInputType) {
            case TYPE_STRING:
                mEditText.setInputType(defaultInputType);
                break;

            case TYPE_STRING_MULTILINE:
                mEditText.setInputType(defaultInputType | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                break;

            case TYPE_PASSWORD:
                mEditText.setInputType(defaultInputType | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;

            case TYPE_PASSWORD_VISIBLE:
                mEditText.setInputType(defaultInputType | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                break;

            case TYPE_PASSWORD_NUMBER:
                mEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                break;

            case TYPE_PASSWORD_NUMBER_VISIBLE:
                mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;

            case TYPE_URL:
                mEditText.setInputType(defaultInputType);
                if (!mEditText.isFocusable()) {
                    Linkify.addLinks(mEditText, Linkify.WEB_URLS);
                }
                break;

            case TYPE_PHONE:
                mEditText.setInputType(defaultInputType | InputType.TYPE_CLASS_PHONE);
                break;

            case TYPE_EMAIL:
                mEditText.setInputType(defaultInputType | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;

            case TYPE_NUMBER:
                mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
        }
    }

    private int reverseInputTypeVisibility(int inputType) {
        int reverseInputType = inputType;

        if (inputType == TYPE_PASSWORD) {
            reverseInputType = TYPE_PASSWORD_VISIBLE;
        }
        else if (inputType == TYPE_PASSWORD_NUMBER) {
            reverseInputType = TYPE_PASSWORD_NUMBER_VISIBLE;
        }
        else if (inputType == TYPE_PASSWORD_VISIBLE) {
            reverseInputType = TYPE_PASSWORD;
        }
        else if (inputType == TYPE_PASSWORD_NUMBER_VISIBLE) {
            reverseInputType = TYPE_PASSWORD_NUMBER;
        }

        return reverseInputType;
    }

    private void setOnLongClickMenu() {
        mEditText.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!mEditText.isFocusable() && !TextUtils.isEmpty(mEditText.getText())) {
                    new AlertDialogWrapper.Builder(getContext()).setItems(R.array.array_field_actions, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Utils.copyToClipboard(null, mEditText.getText().toString());
                            Utils.showToast(R.string.toast_copy_to_clipboard);
                        }
                    }).show();
                }
                return true;
            }
        });

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

    private void moveViewToRight(int xLocation) {
        mLayoutParams.leftMargin = xLocation;
        mLayoutParams.rightMargin = -xLocation;
        mViewsLayout.setLayoutParams(mLayoutParams);
    }

    private void removeItself() {
        final ViewGroup parent = (ViewGroup) getParent();
        setTag(mIndex);
        parent.removeView(this);
        mSwipeListener.onSwiped(this);
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
