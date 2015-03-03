// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.volcano.esecurebox.R;
import com.volcano.esecurebox.VlApplication;
import com.volcano.esecurebox.util.BitmapUtils;
import com.volcano.esecurebox.util.RobotoUtils;

/**
 * A circle drawable with two styles: Filled or Stroked
 */
public class CircleDrawable extends Drawable {

    private static final int STROKE_WIDTH   = 12;

    public static final int FILL    = 0;
    public static final int STROKE  = 1;

    private float mRadius;
    private float mCenterX;
    private float mCenterY;
    private Paint mPaint;
    private int mStyle;
    private int mColor;
    private int mTextColor;
    private String mText;

    public CircleDrawable() {
        this(Color.RED, STROKE, null, Color.WHITE);
    }

    public CircleDrawable(int color, int style) {
        this(color, style, null, Color.WHITE);
    }

    public CircleDrawable(String color, int style) {
        this(BitmapUtils.getColor(color), style, null, Color.WHITE);
    }

    public CircleDrawable(String color, int style, String text) {
        this(BitmapUtils.getColor(color), style, text, Color.WHITE);
    }

    public CircleDrawable(String color, int style, String text, String textColor) {
        this(BitmapUtils.getColor(color), style, text, BitmapUtils.getColor(textColor));
    }

    public CircleDrawable(int color, int style, String text, int textColor) {
        mTextColor = textColor;
        mColor = color;
        mStyle = style;
        mText = text;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mRadius = Math.min(bounds.exactCenterX(), bounds.exactCenterY()) - (mStyle == STROKE ? STROKE_WIDTH / 2 : 0);
        mCenterX = bounds.exactCenterX();
        mCenterY = bounds.exactCenterY();
    }
    @Override
    public void draw(Canvas canvas) {
        mPaint.setStyle(Paint.Style.values()[mStyle]);
        mPaint.setStrokeWidth(STROKE_WIDTH);
        mPaint.setColor(mColor);
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);

        if (mText != null) {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mTextColor);
            mPaint.setTextSize(VlApplication.getInstance().getResources().getDimension(R.dimen.text_size_25));
            mPaint.setStrokeWidth(1);
            mPaint.setTypeface(RobotoUtils.obtainTypeface(RobotoUtils.ROBOTO_REGULAR));
            final float measuredTextSize = mPaint.measureText(mText) / 2;
            canvas.drawText(mText, mCenterX - measuredTextSize, mCenterY - ((mPaint.descent() + mPaint.ascent()) / 2), mPaint);
        }
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    /**
     * Set style for filling drawable
     * @param style One of {@link com.volcano.esecurebox.widget.CircleDrawable#FILL} or
     *              {@link com.volcano.esecurebox.widget.CircleDrawable#STROKE}
     */
    public void setStyle(int style) {
        mStyle = style;
        mPaint.setStyle(Paint.Style.values()[mStyle]);
    }

    /**
     * Set the paint's color to fill drawable
     * @param color The new color
     */
    public void setColor(String color) {
        mColor = BitmapUtils.getColor(color);
        mPaint.setColor(mColor);
    }
}
