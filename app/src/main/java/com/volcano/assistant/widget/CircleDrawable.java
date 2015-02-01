// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;

import com.volcano.assistant.util.BitmapUtils;

/**
 * A circle drawable with two styles: Filled or Stroked
 */
public class CircleDrawable extends ShapeDrawable {

    private static final int STROKE_WIDTH   = 12;

    public static final int STROKE  = 1;
    public static final int FILL    = 2;

    private float mRadius;
    private float mCenterX;
    private float mCenterY;
    private Paint mPaint;
    private int mStyle;
    private int mColor;

    public CircleDrawable() {
        this(Color.RED, STROKE);
    }

    public CircleDrawable(String color, int style) {
        this(BitmapUtils.getColor(color), style);
    }

    public CircleDrawable(int color, int style) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mColor = color;
        mStyle = style;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.values()[mStyle]);
        mPaint.setStrokeWidth(STROKE_WIDTH);
        mPaint.setColor(mColor);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mRadius = Math.min(bounds.exactCenterX(), bounds.exactCenterY());
        mCenterX = bounds.exactCenterX();
        mCenterY = bounds.exactCenterY();
    }
    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(mCenterX, mCenterY, mRadius * .75f, mPaint);
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
     * @param style One of {@link com.volcano.assistant.widget.CircleDrawable#FILL} or
     *              {@link com.volcano.assistant.widget.CircleDrawable#STROKE}
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
