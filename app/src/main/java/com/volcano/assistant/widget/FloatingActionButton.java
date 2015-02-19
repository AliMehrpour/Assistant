package com.volcano.assistant.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.volcano.assistant.R;
import com.volcano.assistant.util.BitmapUtils;
import com.volcano.assistant.util.Utils;

/**
 * A Floating Action Button
 */
public class FloatingActionButton extends ImageButton {

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_MINI   = 1;

    int mColorNormal;
    int mColorPressed;
    int mColorDisabled;
    private boolean mStrokeVisible;
    private Drawable mIconDrawable;
    private String mTitle;
    private int mType;
    private int mIcon;

    private float mCircleSize;
    private float mShadowRadius;
    private float mShadowOffset;
    private int mDrawableSize;

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

    private void init(Context context, AttributeSet attrs) {
        mColorNormal = getColor(R.color.fab_normal);
        mColorPressed = getColor(R.color.fab_pressed);
        mColorDisabled = getColor(android.R.color.darker_gray);
        mShadowRadius = getDimension(R.dimen.fab_shadow_radius);
        mShadowOffset = getDimension(R.dimen.fab_shadow_offset);
        mStrokeVisible = true;
        mType = TYPE_NORMAL;

        if (attrs != null) {
            initAttrs(context, attrs);
        }

        updateCircleSize();
        updateDrawableSize();
        updateBackground();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionButton);
        if (attr != null) {
            try {
                mColorNormal = attr.getColor(R.styleable.FloatingActionButton_fabColorNormal, getColor(R.color.fab_normal));
                mColorPressed = attr.getColor(R.styleable.FloatingActionButton_fabColorPressed, getColor(R.color.fab_pressed));
                mColorPressed = attr.getColor(R.styleable.FloatingActionButton_fabColorDisabled, getColor(R.color.fab_disable));
                mStrokeVisible = attr.getBoolean(R.styleable.FloatingActionButton_fabStrokeVisible, true);
                mType = attr.getInt(R.styleable.FloatingActionButton_fabType, TYPE_NORMAL);
                mTitle = attr.getString(R.styleable.FloatingActionButton_fabTitle);
                mIcon = attr.getResourceId(R.styleable.FloatingActionButton_fabIcon, 0);
            }
            finally {
                attr.recycle();
            }
        }
    }

    /**
     * Set the button type. it cab be one of {@link com.volcano.assistant.widget.FloatingActionButton#TYPE_NORMAL} or
     *                                       {@link com.volcano.assistant.widget.FloatingActionButton#TYPE_NORMAL}
     * @param type The type
     */
    public void setType(int type) {
        if (type != TYPE_NORMAL && type != TYPE_MINI) {
            throw new IllegalArgumentException("Use allows type values. see documentation");
        }

        if (mType != type) {
            mType = type;
            updateCircleSize();
            updateDrawableSize();
            updateBackground();
        }
    }

    /**
     * Set the icon resource as button background
     * @param icon The icon
     */
    public void setIcon(@DrawableRes int icon) {
        if (mIcon != icon) {
            mIcon = icon;
            mIconDrawable = null;
            updateBackground();
        }
    }

    /**
     * Set the drawable as button drawable
     * @param iconDrawable The drawablr
     */
    public void setIconDrawable(@NonNull Drawable iconDrawable) {
        if (mIconDrawable != iconDrawable) {
            mIconDrawable = iconDrawable;
            mIcon = 0;
            updateBackground();
        }
    }

    /**
     * Set the button title
     * @param title The string title
     */
    public void setTitle(String title) {
        mTitle = title;
        final RobotoTextView label = getLabelView();
        if (label != null) {
            label.setText(title);
        }
    }

    /**
     * Return button title
     * @return The title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     *
     * @param colorNormal
     * @param lighten
     */
    public void setColorNormal(String colorNormal, boolean lighten) {
        if (lighten) {
            mColorNormal = lightenColor(BitmapUtils.getColor(colorNormal));
        }
        else {
            mColorNormal = BitmapUtils.getColor(colorNormal);
        }

        updateBackground();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mDrawableSize, mDrawableSize);
    }

    private void updateCircleSize() {
        mCircleSize = getDimension(mType == TYPE_NORMAL? R.dimen.fab_size_normal : R.dimen.fab_size_mini);
    }

    private void updateDrawableSize() {
        mDrawableSize = (int) (mCircleSize + 2 * mShadowRadius);
    }

    void updateBackground() {
        final float strokeWidth = getDimension(R.dimen.fab_stroke_width);
        final float halfStrokeWidth = strokeWidth / 2;

        final LayerDrawable layerDrawable = new LayerDrawable(
                new Drawable[] {
                   getResources().getDrawable(mType == TYPE_NORMAL ? R.drawable.shadow_fab_normal : R.drawable.shadow_fab_mini),
                   createFillDrawable(strokeWidth),
                   createOuterStrokeDrawable(strokeWidth),
                   getIconDrawable()
                });

        final int iconOffset = (int) (mCircleSize - getDimension(R.dimen.fab_icon_size)) / 2;
        final int circleInsetHorizontal = (int) mShadowRadius;
        final int circleInsetTop = (int) (mShadowRadius - mShadowOffset);
        final int circleInsetBottom = (int) (mShadowRadius + mShadowOffset);

        // Set inset for shadow drawable
        layerDrawable.setLayerInset(1,
                circleInsetHorizontal, circleInsetTop,
                circleInsetHorizontal, circleInsetBottom);

        // Set inset for fill drawable
        layerDrawable.setLayerInset(2,
                (int) (circleInsetHorizontal - halfStrokeWidth),
                (int) (circleInsetTop - halfStrokeWidth),
                (int) (circleInsetHorizontal - halfStrokeWidth),
                (int) (circleInsetBottom - halfStrokeWidth));

        // Set inset for outer drawable
        layerDrawable.setLayerInset(3,
                circleInsetHorizontal + iconOffset,
                circleInsetTop + iconOffset,
                circleInsetHorizontal + iconOffset,
                circleInsetBottom + iconOffset);

        setBackgroundCompat(layerDrawable);
    }

    private StateListDrawable createFillDrawable(float strokeWidth) {
        final StateListDrawable drawable  = new StateListDrawable();
        drawable.addState(new int[] { -android.R.attr.state_enabled }, createCircleDrawable(mColorDisabled, strokeWidth));
        drawable.addState(new int[] { android.R.attr.state_pressed }, createCircleDrawable(mColorPressed, strokeWidth));
        drawable.addState(new int[] { }, createCircleDrawable(mColorNormal, strokeWidth));
        return drawable;
    }

    private Drawable createCircleDrawable(int color, float strokeWidth) {
        int alpha = Color.alpha(color);
        int opaqueColor = opaque(color);

        ShapeDrawable fillDrawable = new ShapeDrawable(new OvalShape());

        final Paint paint = fillDrawable.getPaint();
        paint.setAntiAlias(true);
        paint.setColor(opaqueColor);

        final Drawable[] layers = { fillDrawable, createInnerStrokesDrawable(opaqueColor, strokeWidth) };

        final LayerDrawable drawable = alpha == 255 || !mStrokeVisible
                ? new LayerDrawable(layers)
                : new TranslucentLayerDrawable(alpha, layers);

        int halfStrokeWidth = (int) (strokeWidth / 2f);
        drawable.setLayerInset(1, halfStrokeWidth, halfStrokeWidth, halfStrokeWidth, halfStrokeWidth);

        return drawable;
    }

    private Drawable createInnerStrokesDrawable(final int color, float strokeWidth) {
        if (!mStrokeVisible) {
            return new ColorDrawable(Color.TRANSPARENT);
        }

        final ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());

        final int bottomStrokeColor = darkenColor(color);
        final int bottomStrokeColorHalfTransparent = halfTransparent(bottomStrokeColor);
        final int topStrokeColor = lightenColor(color);
        final int topStrokeColorHalfTransparent = halfTransparent(topStrokeColor);

        final Paint paint = shapeDrawable.getPaint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        shapeDrawable.setShaderFactory(new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                return new LinearGradient(width / 2, 0, width / 2, height,
                        new int[] { topStrokeColor, topStrokeColorHalfTransparent, color, bottomStrokeColorHalfTransparent, bottomStrokeColor },
                        new float[] { 0f, 0.2f, 0.5f, 0.8f, 1f },
                        Shader.TileMode.CLAMP
                );
            }
        });

        return shapeDrawable;
    }

    private Drawable createOuterStrokeDrawable(float strokeWidth) {
        final ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());

        final Paint paint = shapeDrawable.getPaint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setAlpha(opacityToAlpha(0.02f));

        return shapeDrawable;
    }

    Drawable getIconDrawable() {
        if (mIconDrawable != null) {
            return mIconDrawable;
        }
        else if (mIcon != 0) {
            return getResources().getDrawable(mIcon);
        }
        else {
            return new ColorDrawable(Color.TRANSPARENT);
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private void setBackgroundCompat(Drawable drawable) {
        if (Utils.hasJellyBeanApi()) {
            setBackground(drawable);
        } else {
            setBackgroundDrawable(drawable);
        }
    }

    int getColor(@ColorRes int id) {
        return getResources().getColor(id);
    }

    int getDimension(@DimenRes int id) {
        return getResources().getDimensionPixelSize(id);
    }

    RobotoTextView getLabelView() {
        return (RobotoTextView) getTag(R.id.fab_label);
    }

    private int opacityToAlpha(float opacity) {
        return (int) (255f * opacity);
    }

    private int darkenColor(int argb) {
        return adjustColorBrightness(argb, 0.8f);
    }

    private int lightenColor(int argb) {
        return adjustColorBrightness(argb, 1.2f);
    }

    private int adjustColorBrightness(int argb, float factor) {
        final float[] hsv = new float[3];
        Color.colorToHSV(argb, hsv);

        hsv[2] = Math.min(hsv[2] * factor, 1f);

        return Color.HSVToColor(Color.alpha(argb), hsv);
    }

    private int halfTransparent(int argb) {
        return Color.argb(
                Color.alpha(argb) / 2,
                Color.red(argb),
                Color.green(argb),
                Color.blue(argb)
        );
    }

    private int opaque(int argb) {
        return Color.rgb(
                Color.red(argb),
                Color.green(argb),
                Color.blue(argb)
        );
    }

    private static class TranslucentLayerDrawable extends LayerDrawable {
        private final int mAlpha;

        public TranslucentLayerDrawable(int alpha, Drawable... layers) {
            super(layers);
            mAlpha = alpha;
        }

        @Override
        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            canvas.saveLayerAlpha(bounds.left, bounds.top, bounds.right, bounds.bottom, mAlpha, Canvas.ALL_SAVE_FLAG);
            super.draw(canvas);
            canvas.restore();
        }
    }
}
