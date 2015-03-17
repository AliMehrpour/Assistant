// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.util;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import com.volcano.esecurebox.R;
import com.volcano.esecurebox.VlApplication;

import java.util.LinkedList;
import java.util.List;

/**
 * Helper class that handle soft keyboard states and notify it
 */
@SuppressWarnings("UnusedDeclaration")
public class SoftKeyboardUtils implements ViewTreeObserver.OnGlobalLayoutListener {

    private float mMaxHeigthDiff;
    private final List<OnSoftKeyboardStateListener> listeners = new LinkedList<>();
    private final View mActivityRootView;
    private int mLastSoftKeyboardHeight;
    private boolean mIsSoftKeyboardOpened;

    /**
     * Interface for containing activities to implement to be notified
     * of soft keyboard states.
     */
    public interface OnSoftKeyboardStateListener {
        /**
         * Called when soft keyboard has opened
         * @param keyboardHeight The soft keyboard height
         */
        public void onSoftKeyboardOpened(int keyboardHeight);

        /**
         * Called when soft keyboard has closed
         */
        public void onSoftKeyboardClosed();
    }

    public SoftKeyboardUtils(View activityRootView) {
        this(activityRootView, false);
    }

    public SoftKeyboardUtils(View activityRootView, boolean isSoftKeyboardOpened) {
        mActivityRootView = activityRootView;
        mIsSoftKeyboardOpened = isSoftKeyboardOpened;
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        final Resources res = VlApplication.getInstance().getResources();
        // For confidence use 150% of status bar
        mMaxHeigthDiff = 1.5f * res.getDimensionPixelSize(R.dimen.status_bar_height);
        if (Utils.hasLollipopApi()) {
            final float buttonBarHeight = res.getDimensionPixelSize(R.dimen.button_bar_height_lollipop);
            mMaxHeigthDiff += buttonBarHeight;
        }
    }

    /**
     * @param isSoftKeyboardOpened True is soft keyboard is open
     */
    public void setIsSoftKeyboardOpened(boolean isSoftKeyboardOpened) {
        mIsSoftKeyboardOpened = isSoftKeyboardOpened;
    }

    /**
     * @return True if soft keyboard is open
     */
    public boolean ismIsSoftKeyboardOpened() {
        return mIsSoftKeyboardOpened;
    }

    /**
     * Default value is zero (0)
     * @return last saved keyboard height in px
     */
    public int getmLastSoftKeyboardHeight() {
        return mLastSoftKeyboardHeight;
    }

    /**
     * Add {@link OnSoftKeyboardStateListener}
     * @param listener The listener
     */
    public void addSoftKeyboardStateListener(OnSoftKeyboardStateListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove {@link OnSoftKeyboardStateListener}
     * @param listener The listener
     */
    public void removeSoftKeyboardStateListener(OnSoftKeyboardStateListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onGlobalLayout() {
        final Rect r = new Rect();
        //r will be populated with the coordinates of your view that area still visible.
        mActivityRootView.getWindowVisibleDisplayFrame(r);

        final int heightDiff = mActivityRootView.getRootView().getHeight() - (r.bottom - r.top);
        if (!mIsSoftKeyboardOpened && heightDiff > mMaxHeigthDiff) {
            mIsSoftKeyboardOpened = true;
            notifyOnSoftKeyboardOpened(heightDiff);
        }
        else if (mIsSoftKeyboardOpened && heightDiff < mMaxHeigthDiff) {
            mIsSoftKeyboardOpened = false;
            notifyOnSoftKeyboardClosed();
        }
    }

    private void notifyOnSoftKeyboardOpened(int keyboardHeight) {
        mLastSoftKeyboardHeight = keyboardHeight;

        for (OnSoftKeyboardStateListener listener : listeners) {
            if (listener != null) {
                listener.onSoftKeyboardOpened(keyboardHeight);
            }
        }
    }

    private void notifyOnSoftKeyboardClosed() {
        for (OnSoftKeyboardStateListener listener : listeners) {
            if (listener != null) {
                listener.onSoftKeyboardClosed();
            }
        }
    }

    /**
     * Hide soft keyboard
     * @param activity The activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //noinspection ConstantConditions
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}