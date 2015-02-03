package com.volcano.assistant;

import android.app.Application;

import com.parse.ParseObject;
import com.volcano.assistant.model.Category;
import com.volcano.assistant.model.Field;

public final class VlApplication extends Application {

    private static VlApplication sInstance;

    public VlApplication() {
        super();
        sInstance = this;
    }

    public static VlApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Managers.initalize();

        ParseObject.registerSubclass(Category.class);
        ParseObject.registerSubclass(Field.class);
        ApplicationLock.enableAppLock(this);
    }
}

