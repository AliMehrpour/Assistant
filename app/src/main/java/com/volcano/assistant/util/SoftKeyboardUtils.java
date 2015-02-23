// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.util;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import java.util.LinkedList;
import java.util.List;

/**
 * Helper class that handle soft keyboard states and notify it
 */
public class SoftKeyboardUtils implements ViewTreeObserver.OnGlobalLayoutListener {
    private static final int SOFT_KEYBOARD_HEIGHT = 100;

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

    private final List<OnSoftKeyboardStateListener> listeners = new LinkedList<>();
    private final View activityRootView;
    private int lastSoftKeyboardHeight;
    private boolean isSoftKeyboardOpened;

    public SoftKeyboardUtils(View activityRootView) {
        this(activityRootView, false);
    }

    public SoftKeyboardUtils(View activityRootView, boolean isSoftKeyboardOpened) {
        this.activityRootView = activityRootView;
        this.isSoftKeyboardOpened = isSoftKeyboardOpened;
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    /**
     * @param isSoftKeyboardOpened True is soft keyboard is open
     */
    public void setIsSoftKeyboardOpened(boolean isSoftKeyboardOpened) {
        this.isSoftKeyboardOpened = isSoftKeyboardOpened;
    }

    /**
     * @return True if soft keyboard is open
     */
    public boolean isSoftKeyboardOpened() {
        return isSoftKeyboardOpened;
    }

    /**
     * Default value is zero (0)
     * @return last saved keyboard height in px
     */
    public int getLastSoftKeyboardHeight() {
        return lastSoftKeyboardHeight;
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
        activityRootView.getWindowVisibleDisplayFrame(r);

        final int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
        if (!isSoftKeyboardOpened && heightDiff > SOFT_KEYBOARD_HEIGHT) {
            isSoftKeyboardOpened = true;
            notifyOnSoftKeyboardOpened(heightDiff);
        }
        else if (isSoftKeyboardOpened && heightDiff < SOFT_KEYBOARD_HEIGHT) {
            isSoftKeyboardOpened = false;
            notifyOnSoftKeyboardClosed();
        }
    }

    private void notifyOnSoftKeyboardOpened(int keyboardHeight) {
        lastSoftKeyboardHeight = keyboardHeight;

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