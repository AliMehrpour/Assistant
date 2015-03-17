// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import com.volcano.esecurebox.R;

/**
 * A floating action menu
 */
public final class FloatingActionMenu extends ViewGroup {
    private static final int ANIMATION_DURATION         = 300;
    private static final float COLLAPSED_PLUS_ROTATION  = 0f;
    private static final float EXPANDED_PLUS_ROTATION   = 90f + 45f;

    private int mButtonSpacing;
    private int mMainButtonColorNormal;
    private int mMainButtonColorPressed;
    private int mMainButtonColorDisabled;
    private int mType;
    private boolean mExpanded;
    private int mLabelsStyle;
    private int mMenuSize = 0;
    private int mLabelsMargin;
    private int mLabelsVerticalOffset;

    private AnimatorSet mExpandAnimation = new AnimatorSet().setDuration(ANIMATION_DURATION);
    private AnimatorSet mCollapseAnimation = new AnimatorSet().setDuration(ANIMATION_DURATION);
    private FloatingActionButton mMainButton;
    private RotatingDrawable mRotatingDrawable;
    private int mMaxButtonWidth;

    private OnFloatingActionsMenuUpdateListener mListener;

    public interface OnFloatingActionsMenuUpdateListener {
        void onMenuIsEmptyOnExpanding();
        void onMenuExpanded();
        void onMenuCollapsed();
    }

    public FloatingActionMenu(Context context) {
        this(context, null);
    }

    public FloatingActionMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FloatingActionMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mButtonSpacing = (int) (getResources().getDimension(R.dimen.fab_action_spacing) - getResources().getDimension(R.dimen.fab_shadow_radius));
        mLabelsMargin = 0;
        mMainButtonColorNormal = getColor(R.color.fab_normal);
        mMainButtonColorPressed = getColor(R.color.fab_pressed);
        mMainButtonColorDisabled = getColor(android.R.color.white);
        mType = FloatingActionButton.TYPE_NORMAL;
        mLabelsVerticalOffset = getDimension(R.dimen.fab_shadow_offset);

        if (attrs != null) {
            initAttrs(context, attrs);
        }

        createMainButton(context);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionMenu);
        if (attr != null) {
            try {
                mMainButtonColorNormal = attr.getColor(R.styleable.FloatingActionMenu_famColorNormal, getColor(R.color.fab_normal));
                mMainButtonColorPressed = attr.getColor(R.styleable.FloatingActionMenu_famColorPressed, getColor(R.color.fab_pressed));
                mMainButtonColorDisabled = attr.getColor(R.styleable.FloatingActionMenu_famColorDisabled, getColor(android.R.color.white));
                mType = attr.getInt(R.styleable.FloatingActionMenu_famType, FloatingActionButton.TYPE_NORMAL);
                mLabelsStyle = attr.getResourceId(R.styleable.FloatingActionMenu_famLabelStyle, 0);
            }
            finally {
                attr.recycle();
            }
        }
    }

    /**
     * Set a listener to be notified when floating menu expanded or collapsed
     * @param listener The listener
     */
    public void setOnFloatingActionsMenuUpdateListener(OnFloatingActionsMenuUpdateListener listener) {
        mListener = listener;
    }

    /**
     * Add menu item
     * @param menuItem The menu item
     */
    public void addMenuItem(FloatingActionButton menuItem) {
        addView(menuItem, mMenuSize - 1);
        mMenuSize++;

        if (mLabelsStyle != 0) {
            createLabels();
        }
    }

    /**
     * Remove menu item
     * @param menuItem The menu item
     */
    public void removeMenuItem(FloatingActionButton menuItem) {
        removeView(menuItem.getLabelView());
        removeView(menuItem);
        mMenuSize--;
    }

    /**
     * @return True if exist at least one menu item, otherwise false
     */
    public boolean hasMenuItems() {
        return mMenuSize > 1; // one item for main button
    }

    /**
     * Toggle menu
     */
    public void toggle() {
        if (mExpanded) {
            collapse();
        }
        else {
            expand();
        }
    }

    public void collapse() {
        // If call outside, collapse if it has been expanded before
        if (mExpanded) {
            mExpanded = false;
            mCollapseAnimation.start();
            mExpandAnimation.cancel();
        }

        if (mListener != null) {
            mListener.onMenuCollapsed();
        }
    }

    public void expand() {
        if (!hasMenuItems()) {
            if (mListener != null) {
                mListener.onMenuIsEmptyOnExpanding();
                return;
            }
        }

        mExpanded = true;
        mCollapseAnimation.cancel();
        mExpandAnimation.start();

        if (mListener != null) {
            mListener.onMenuExpanded();
        }
    }

    private int getColor(@ColorRes int id) {
        return getResources().getColor(id);
    }

    private int getDimension(@DimenRes int id) {
        return getResources().getDimensionPixelSize(id);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int width = 0;
        int height = 0;
        mMaxButtonWidth = 0;
        int maxLabelWidth = 0;

        for (int i = 0; i < mMenuSize; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            mMaxButtonWidth = Math.max(mMaxButtonWidth, child.getMeasuredWidth());
            height += child.getMeasuredHeight();

            final RobotoTextView label = (RobotoTextView) child.getTag(R.id.fab_label);
            if (label != null) {
                maxLabelWidth = Math.max(maxLabelWidth, label.getMeasuredWidth());
            }
            width = mMaxButtonWidth + (maxLabelWidth > 0 ? maxLabelWidth + mLabelsMargin : 0);

            height += mButtonSpacing * (getChildCount() - 1);
            height = adjustForOvershoot(height);
        }

        setMeasuredDimension(width, height);
    }

    private int adjustForOvershoot(int dimension) {
        return dimension * 12 / 10;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int mainButtonY = b - t - mMainButton.getMeasuredHeight();
        final int buttonsHorizontalCenter = r - l - mMaxButtonWidth / 2; // Ensure mMainButton is centered on the line where the buttons should be
        final int mainButtonLeft = buttonsHorizontalCenter - mMainButton.getMeasuredWidth() / 2;
        mMainButton.layout(mainButtonLeft, mainButtonY, mainButtonLeft + mMainButton.getMeasuredWidth(), mainButtonY + mMainButton.getMeasuredHeight());

        final int labelsOffset = mMaxButtonWidth / 2 + mLabelsMargin;
        final int labelsXNearButton = buttonsHorizontalCenter - labelsOffset;

        int nextY = mainButtonY - mButtonSpacing;

        for (int i = mMenuSize - 1; i >= 0; i--) {
            final View child = getChildAt(i);

            if (child == mMainButton || child.getVisibility() == GONE) continue;

            final int childX = buttonsHorizontalCenter - child.getMeasuredWidth() / 2;
            final int childY = nextY - child.getMeasuredHeight();
            child.layout(childX, childY, childX + child.getMeasuredWidth(), childY + child.getMeasuredHeight());

            final float collapsedTranslation = mainButtonY - childY;
            final float expandedTranslation = 0f;

            child.setTranslationY(mExpanded ? expandedTranslation : collapsedTranslation);
            child.setAlpha(mExpanded ? 1f : 0f);

            final LayoutParams params = (LayoutParams) child.getLayoutParams();
            params.mCollapseDir.setFloatValues(expandedTranslation, collapsedTranslation);
            params.mExpandDir.setFloatValues(collapsedTranslation, expandedTranslation);
            params.setAnimationsTarget(child);

            final View label = (View) child.getTag(R.id.fab_label);
            if (label != null) {
                final int labelLeft = labelsXNearButton - label.getMeasuredWidth();
                //noinspection UnnecessaryLocalVariable
                final int labelRight = labelsXNearButton;

                final int labelTop = childY - mLabelsVerticalOffset + (child.getMeasuredHeight() - label.getMeasuredHeight()) / 2;

                label.layout(labelLeft, labelTop, labelRight, labelTop + label.getMeasuredHeight());

                label.setTranslationY(mExpanded ? expandedTranslation : collapsedTranslation);
                label.setAlpha(mExpanded ? 1f : 0f);

                LayoutParams labelParams = (LayoutParams) label.getLayoutParams();
                labelParams.mCollapseDir.setFloatValues(expandedTranslation, collapsedTranslation);
                labelParams.mExpandDir.setFloatValues(collapsedTranslation, expandedTranslation);
                labelParams.setAnimationsTarget(label);
            }

            nextY = childY - mButtonSpacing;
        }

    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(super.generateLayoutParams(attrs));
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(super.generateLayoutParams(p));
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(super.generateDefaultLayoutParams());
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return super.checkLayoutParams(p);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (mExpanded && event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
            toggle();
            return true;
        }

        return false;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        bringChildToFront(mMainButton);
        mMenuSize = getChildCount();

        if (mLabelsStyle != 0) {
            createLabels();
        }
    }

    private void createMainButton(Context context) {
        mMainButton = new FloatingActionButton(context) {

            @Override
            void updateBackground() {
                super.mColorNormal = mMainButtonColorNormal;
                super.mColorPressed = mMainButtonColorPressed;
                super.mColorDisabled = mMainButtonColorDisabled;
                super.updateBackground();
            }

            @Override
            Drawable getIconDrawable() {
                final RotatingDrawable rotatingDrawable = new RotatingDrawable(getResources().getDrawable(R.drawable.icon_action_add));
                mRotatingDrawable = rotatingDrawable;

                final OvershootInterpolator interpolator = new OvershootInterpolator();

                final ObjectAnimator collapseAnimator = ObjectAnimator.ofFloat(rotatingDrawable, "rotation", EXPANDED_PLUS_ROTATION, COLLAPSED_PLUS_ROTATION);
                final ObjectAnimator expandAnimator = ObjectAnimator.ofFloat(rotatingDrawable, "rotation", COLLAPSED_PLUS_ROTATION, EXPANDED_PLUS_ROTATION);

                collapseAnimator.setInterpolator(interpolator);
                expandAnimator.setInterpolator(interpolator);

                mExpandAnimation.play(expandAnimator);
                mCollapseAnimation.play(collapseAnimator);

                return rotatingDrawable;
            }
        };

        mMainButton.setId(R.id.fab_expand_menu_button);
        mMainButton.setType(mType);
        mMainButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        addView(mMainButton, super.generateDefaultLayoutParams());
    }

    private void createLabels() {
        final Context context = new ContextThemeWrapper(getContext(), mLabelsStyle);

        for (int i = 0; i < mMenuSize; i++) {
            final FloatingActionButton menuItem = (FloatingActionButton) getChildAt(i);
            final String title = menuItem.getTitle();

            if (menuItem == mMainButton || title == null || menuItem.getTag(R.id.fab_label) != null) continue;

            final RobotoTextView label = new RobotoTextView(context);
            label.setText(menuItem.getTitle());
            addView(label);

            menuItem.setTag(R.id.fab_label, label);
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final SavedState savedState = new SavedState(superState);
        savedState.mExpanded = mExpanded;

        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            final SavedState savedState = (SavedState) state;
            mExpanded = savedState.mExpanded;

            if (mRotatingDrawable != null) {
                mRotatingDrawable.setRotation(mExpanded ? EXPANDED_PLUS_ROTATION : COLLAPSED_PLUS_ROTATION);
            }

            super.onRestoreInstanceState(savedState.getSuperState());
        }
        else {
            super.onRestoreInstanceState(state);
        }
    }

    public static class SavedState extends BaseSavedState {
        public boolean mExpanded;

        public SavedState(Parcelable parcel) {
            super(parcel);
        }

        @Override
        public void writeToParcel(@NonNull Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mExpanded ? 1 : 0);
        }

        private SavedState(Parcel in) {
            super(in);
            mExpanded = in.readInt() == 1;
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    private class LayoutParams extends ViewGroup.LayoutParams {
        private Interpolator sExpandInterpolator = new OvershootInterpolator();
        private Interpolator sCollapseInterpolator = new DecelerateInterpolator(3f);
        private Interpolator sAlphaExpandInterpolator = new DecelerateInterpolator();

        private ObjectAnimator mExpandDir = new ObjectAnimator();
        private ObjectAnimator mExpandAlpha = new ObjectAnimator();
        private ObjectAnimator mCollapseDir = new ObjectAnimator();
        private ObjectAnimator mCollapseAlpha = new ObjectAnimator();

        private boolean animationsSetToPlay;

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);

            mExpandDir.setInterpolator(sExpandInterpolator);
            mExpandAlpha.setInterpolator(sAlphaExpandInterpolator);
            mCollapseDir.setInterpolator(sCollapseInterpolator);
            mCollapseAlpha.setInterpolator(sCollapseInterpolator);

            mCollapseAlpha.setProperty(View.ALPHA);
            mCollapseAlpha.setFloatValues(1f, 0f);

            mExpandAlpha.setProperty(View.ALPHA);
            mExpandAlpha.setFloatValues(0f, 1f);

            mCollapseDir.setProperty(View.TRANSLATION_Y);
            mExpandDir.setProperty(View.TRANSLATION_Y);
        }

        public void setAnimationsTarget(View view) {
            mCollapseAlpha.setTarget(view);
            mCollapseDir.setTarget(view);
            mExpandAlpha.setTarget(view);
            mExpandDir.setTarget(view);

            // Now that the animations have targets, set them to be played
            if (!animationsSetToPlay) {
                mCollapseAnimation.play(mCollapseAlpha);
                mCollapseAnimation.play(mCollapseDir);
                mExpandAnimation.play(mExpandAlpha);
                mExpandAnimation.play(mExpandDir);
                animationsSetToPlay = true;
            }
        }
    }

    private static class RotatingDrawable extends LayerDrawable {
        private float mRotation;

        public RotatingDrawable(Drawable drawable) {
            super(new Drawable[] { drawable });
        }

        @SuppressWarnings("UnusedDeclaration")
        public float getRotation() {
            return mRotation;
        }

        @SuppressWarnings("UnusedDeclaration")
        public void setRotation(float rotation) {
            mRotation = rotation;
            invalidateSelf();
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.save();
            canvas.rotate(mRotation, getBounds().centerX(), getBounds().centerY());
            super.draw(canvas);
            canvas.restore();
        }
    }
}
