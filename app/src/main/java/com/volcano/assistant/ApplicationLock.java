package com.volcano.assistant;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.volcano.assistant.activity.PasscodeActivity;
import com.volcano.assistant.fragment.PasscodeFragment;
import com.volcano.assistant.util.LogUtils;

import java.util.Date;

/**
 * Application lock manager
 */
public class ApplicationLock implements Application.ActivityLifecycleCallbacks {
    private static final int DEFAULT_TIMEOUT = 2000; // 2 Seconds
    private Date mLostFocusDate;
    private boolean mLockEnabled = true;

    private static ApplicationLock sInstance;
    public static ApplicationLock getInstance() {
        if (sInstance == null) {
            sInstance = new ApplicationLock();
        }
        return sInstance;
    }

    public static void enableAppLock(Application app) {
        app.unregisterActivityLifecycleCallbacks(getInstance());
        app.registerActivityLifecycleCallbacks(getInstance());
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        LogUtils.LogI("Application", "Activity " + activity.getClass().getName() + " resumed.");
        if (!Managers.getAccountManager().isPasscodeEnable()) {
            return;
        }

        if (activity.getClass() == PasscodeActivity.class) {
            return;
        }

        if (!mLockEnabled) {
            mLockEnabled = true;
            return;
        }

        if (mustShowUnlockScreen()) {
            mLockEnabled = false;
            VlApplication.getInstance().startActivity(Intents.getPasscodeIntent(PasscodeFragment.MODE_PASSCODE_UNLOCK));
            //return;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activity.getClass() == PasscodeActivity.class) {
            return;
        }

        mLostFocusDate = new Date();
        LogUtils.LogI("Application", "Activity " + activity.getClass().getName() + " paused. Time=" + mLostFocusDate);
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    /**
     * Force to reset last time application has been shown
     */
    public void forcePasscodeLock() {
        mLostFocusDate = null;
    }

    private boolean mustShowUnlockScreen() {
        if (mLostFocusDate == null) {
            return true;
        }

        final Date date = new Date();
        final long nowMillis = date.getTime();
        final long lostFocusMillis = mLostFocusDate.getTime();
        // Make sure changing clock on the device doesn't by-pass PIN lock
        final int passedSeconds = (int) (nowMillis - lostFocusMillis);
        LogUtils.LogI("Application", "Passed seconds=" + passedSeconds);
        if (passedSeconds >= DEFAULT_TIMEOUT) {
            mLostFocusDate = null;
            return true;
        }

        return false;
    }
}