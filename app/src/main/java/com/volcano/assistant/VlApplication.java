// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * A Custom application class
 */
public final class VlApplication extends Application {

    private static VlApplication sInstance;
    private static int sResumeCount = 0;

    public VlApplication() {
        super();
        sInstance = this;
    }

    public static VlApplication getInstance() {
        return sInstance;
    }

    @SuppressWarnings("UnusedDeclaration")
    public static boolean isCurrentApp() {
        return sResumeCount > 0;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Managers.initalize();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
                sResumeCount++;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                sResumeCount--;
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }
        });
    }
}

