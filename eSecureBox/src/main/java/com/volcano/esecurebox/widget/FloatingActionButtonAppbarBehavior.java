package com.volcano.esecurebox.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.CoordinatorLayout.Behavior;
import android.util.AttributeSet;
import android.view.View;

/**
 * Defines fab behavior when user scroll the list and coordinator layout has been changed
 */
@SuppressWarnings("unused")
public final class FloatingActionButtonAppBarBehavior extends Behavior<FloatingActionButton> {

    public FloatingActionButtonAppBarBehavior(Context context, AttributeSet attrs) {
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        final Rect rect = new Rect();
        dependency.getGlobalVisibleRect(rect);

        child.setTranslationY(dependency.getTop());

        if (child.getHeight() + 50 > rect.height()) {
            child.setTranslationX(-500); // Just goes out of screen
        }
        else {
            child.setTranslationX(0);
        }

        return true;
    }
}