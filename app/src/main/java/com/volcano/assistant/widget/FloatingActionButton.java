package com.volcano.assistant.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageButton;

import com.volcano.assistant.R;
import com.volcano.assistant.util.Utils;

/**
 * A Floating Action Button
 */
public class FloatingActionButton extends ImageButton {

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_MINI   = 1;

    private int mColorNormal;
    private int mColorPressed;
    private int mColorRipple;
    private int mType;
    private boolean mShadow;
    private int mShadowSize;
    private boolean mMarginSet;

    public FloatingActionButton(Context context) {
        this(context, null);
    }

    public FloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size = getDimension(mType == TYPE_NORMAL ? R.dimen.fab_size_normal : R.dimen.fab_size_mini);
        if (mShadow && !Utils.hasLollipopApi()) {
            size += mShadowSize * 2;
            setMarginWithoutShadow();
        }

        setMeasuredDimension(size, size);
    }

    private void init(Context context, AttributeSet attrs) {
        mShadow = true;
        mColorNormal = getColor(R.color.fab_normal);
        mColorPressed = getColor(R.color.fab_pressed);
        mColorRipple = getColor(android.R.color.white);
        mShadowSize = getDimension(R.dimen.fab_size_shadow);
        mType = TYPE_NORMAL;

        if (attrs != null) {
            initAttrs(context, attrs);
        }

        updateBackground();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionButton);
        if (attr != null) {
            try {
                mColorNormal = attr.getColor(R.styleable.FloatingActionButton_colorNormal, getColor(R.color.fab_normal));
                mColorPressed = attr.getColor(R.styleable.FloatingActionButton_colorPressed, getColor(R.color.fab_pressed));
                mColorRipple = attr.getColor(R.styleable.FloatingActionButton_colorRipple, getColor(android.R.color.white));
                mShadow = attr.getBoolean(R.styleable.FloatingActionButton_shadow, true);
                mType = attr.getInt(R.styleable.FloatingActionButton_type, TYPE_NORMAL);
            }
            finally {
                attr.recycle();
            }
        }
    }

    private void updateBackground() {
        final StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{android.R.attr.state_pressed}, createDrawble(mColorPressed));
        drawable.addState(new int[]{}, createDrawble(mColorNormal));
        setBackgroundCompat(drawable);

    }

    private Drawable createDrawble(int color) {
        final OvalShape ovalShape = new OvalShape();
        final ShapeDrawable shapeDrawable = new ShapeDrawable(ovalShape);
        shapeDrawable.getPaint().setColor(color);

        if (mShadow && !Utils.hasLollipopApi()) {
            final Drawable shadowDrawable = getResources().getDrawable(mType == TYPE_NORMAL ? R.drawable.shadow : R.drawable.shadow_mini);
            final LayerDrawable layerDrawable = new LayerDrawable(new Drawable[] {shadowDrawable, shapeDrawable});
            layerDrawable.setLayerInset(1, mShadowSize, mShadowSize, mShadowSize, mShadowSize);
            return layerDrawable;
        }
        else {
            return shapeDrawable;
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private void setBackgroundCompat(Drawable drawable) {
        if (Utils.hasLollipopApi()) {
            final float elevation;
            if (mShadow) {
                elevation = getElevation() > 0.0f ? getElevation() : getDimension(R.dimen.fab_elevation);
            }
            else {
                elevation = 0.0f;
            }
            setElevation(elevation);

            final RippleDrawable rippleDrawable = new RippleDrawable(new ColorStateList(new int[][]{{}}, new int[]{mColorRipple}), drawable, null);
            setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    final int size =  getDimension(mType == TYPE_NORMAL ? R.dimen.fab_size_normal : R.dimen.fab_size_mini);
                    outline.setOval(0, 0, size, size);
                }
            });

            setClipToOutline(true);
            setBackground(rippleDrawable);
        }
        else if (Utils.hasJellyBeanApi()) {
            setBackground(drawable);
        }
        else {
            setBackgroundDrawable(drawable);
        }
    }

    private void setMarginWithoutShadow() {
        if (!mMarginSet) {
            if (getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                final ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
                final int leftMargin = layoutParams.leftMargin - mShadowSize;
                final int topMargin = layoutParams.topMargin - mShadowSize;
                final int rightMargin = layoutParams.rightMargin - mShadowSize;
                final int bottomMargin = layoutParams.bottomMargin - mShadowSize;
                layoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);

                requestLayout();
                mMarginSet = true;
            }
        }
    }

    private int getColor(@ColorRes int id) {
        return getResources().getColor(id);
    }

    private int getDimension(@DimenRes int id) {
        return getResources().getDimensionPixelSize(id);
    }

}
