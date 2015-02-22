// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.security;

import android.app.Application;

/**
 * Application lock manager
 */
public class ApplicationLockManager {

    private ApplicationLock mApplicationLock;

    public ApplicationLockManager() {

    }

    public void enableApplicationLockIfAvailable(Application application) {
        mApplicationLock = new ApplicationLock(application);
        mApplicationLock.enable();
    }

    public ApplicationLock getApplicationLock() {
        return mApplicationLock;
    }

    /**
     * Convenience method used to extend the default timeout.
     * There are some situations where an activity will start a different application with an intent.
     * in these situations call this method right before leaving the app
     */
    public void setExtendedTimeout() {
        if (mApplicationLock != null) {
            mApplicationLock.setOneTimeTimeout();
        }
    }

}
