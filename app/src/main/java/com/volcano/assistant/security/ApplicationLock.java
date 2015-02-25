// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.security;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.volcano.assistant.Intents;
import com.volcano.assistant.R;
import com.volcano.assistant.activity.PasscodeActivity;
import com.volcano.assistant.fragment.PasscodeFragment.Mode;
import com.volcano.assistant.util.PrefUtils;
import com.volcano.assistant.security.SecurityUtils.EncryptionAlgorithm;

import java.util.Date;

/**
 * Application lock manager
 */
public class ApplicationLock implements Application.ActivityLifecycleCallbacks {
    private static final int DEFAULT_TIMEOUT    = 2 * 1000; // 2 Seconds
    private static final int EXTENDED_TIMEOUT   = 60 * 1000; // 60 Seconds
    private static final String SALT = "eScureBoxPasswordSalt";  // TODO: define in BuildConfig

    private Application mApplication;
    private Date mLostFocusDate;
    private int mLockTimeout = DEFAULT_TIMEOUT;

    public ApplicationLock(Application application) {
        mApplication = application;
    }

    /**
     * Enable this application lock
     */
    public void enable() {
        mApplication.unregisterActivityLifecycleCallbacks(this);
        mApplication.registerActivityLifecycleCallbacks(this);
    }

    /**
     * Disable this application lock
     */
    public void disable() {
        mApplication.unregisterActivityLifecycleCallbacks(this);
    }

    /**
     * Force to reset last time application has been shown
     */
    public void forcePasscodeLock() {
        mLostFocusDate = null;
    }

    /**
     * Set user's passcode. if passcode will be null, it will removed
     * @param passcode The passcode
     */
    public void setPasscode(String passcode) {
        if (passcode == null) {
            PrefUtils.remove(mApplication.getString(R.string.preference_passcode));
            disable();
        }
        else {
            passcode = SALT + passcode + SALT;
            passcode = SecurityUtils.encrypt(EncryptionAlgorithm.DES, passcode);
            PrefUtils.setPref(mApplication.getString(R.string.preference_passcode), passcode);
            mLostFocusDate = new Date();
            enable();
        }
    }

    /**
     * @param passcode The passcode
     * @return True if provided passcode is equal saved passcode
     */
    public boolean verifyPasscode(String passcode) {
        String storedPasscode = "";

        if (PrefUtils.exists(mApplication.getString(R.string.preference_passcode))) {
            storedPasscode = PrefUtils.getPref(mApplication.getString(R.string.preference_passcode), "");
            storedPasscode = SecurityUtils.decrypt(EncryptionAlgorithm.DES, storedPasscode);
            passcode = SALT + passcode + SALT;
        }

        if (passcode.equalsIgnoreCase(storedPasscode)) {
            mLostFocusDate = new Date();
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * @return True if passcode is enabled
     */
    public boolean isPasscodeEnable() {
        return PrefUtils.exists(mApplication.getString(R.string.preference_passcode));
    }

    /**
     * There are situations where an activity will start a different application with an intent.
     * In these situations call this method right before leaving the app.
     */
    public void setOneTimeTimeout() {
        mLockTimeout = EXTENDED_TIMEOUT;
    }

    private boolean mustShowUnlockScreen() {

        if (!isPasscodeEnable()) {
            return false;
        }

        // first startup or when we forced to show the passcode
        if (mLostFocusDate == null) {
            return true;
        }

        final int currentTimeout = mLockTimeout;
        mLockTimeout = DEFAULT_TIMEOUT;
        final Date date = new Date();
        final long nowMillis = date.getTime();
        final long lostFocusMillis = mLostFocusDate.getTime();
        // Make sure changing clock on the device doesn't by-pass PIN lock
        final int passedSeconds = (int) (nowMillis - lostFocusMillis);
        if (passedSeconds >= currentTimeout) {
            mLostFocusDate = null;
            return true;
        }

        return false;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activity.getClass() == PasscodeActivity.class) {
            return;
        }

        if (mustShowUnlockScreen()) {
            mApplication.startActivity(Intents.getPasscodeIntent(Mode.UNLOCK));
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activity.getClass() == PasscodeActivity.class &&
                activity.getIntent().getSerializableExtra(Intents.EXTRA_MODE) == Mode.UNLOCK) {
            return;
        }

        mLostFocusDate = new Date();
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

}