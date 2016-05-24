package com.volcano.esecurebox.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.CoordinatorLayout.Behavior;
import android.util.AttributeSet;
import android.view.View;

import com.volcano.esecurebox.R;
import com.volcano.esecurebox.VlApplication;

/**
 * Defines fab behavior when user scroll the list and coordinator layout has been changed
 */
@SuppressWarnings("unused")
public final class FloatingActionButtonAppBarBehavior extends Behavior<FloatingActionButton> {
    final int sCollapsedToolbarHeight = VlApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.toolbar_collapse_height);

    public FloatingActionButtonAppBarBehavior(Context context, AttributeSet attrs) {
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, final FloatingActionButton child, View dependency) {
        final Rect rect = new Rect();
        dependency.getGlobalVisibleRect(rect);

        child.setTranslationY(dependency.getTop());

        if (rect.height() < sCollapsedToolbarHeight * 2) {
            child.setTranslationX(-500); // Just goes out of screen
        }
        else {
            child.setTranslationX(0);
        }

        return true;
    }
}