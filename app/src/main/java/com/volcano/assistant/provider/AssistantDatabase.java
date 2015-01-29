// Copyright (c) 2015 Volcano. All rights reserved.

package com.volcano.assistant.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.volcano.assistant.util.LogUtils;

/**
 * Created by alimehrpour on 12/29/14.
 */
public class AssistantDatabase extends SQLiteOpenHelper {
    private static final String TAG = LogUtils.makeLogTag(AssistantDatabase.class);

    private static final String DATABASE_NAME = "assistant.db";
    private static final int DATABASE_VERSION = 1;

    private Context mContext;

    interface Tables {
        String CATEGORY           = "category";
        String ATTRIBUTE          = "attribute";
        String CATEGORY_ATTRIBUTE = "category_attribute";
    }

    public AssistantDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtils.LogI(TAG, "onCreate() database");

        db.execSQL("CREATE TABLE " + Tables.CATEGORY + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + AssistantContract.CategoryColumns.CATEGORY_ID + " TEXT NOT NULL,"
                + AssistantContract.CategoryColumns.CATEGORY_NAME + " TEXT NOT NULL,"
                + AssistantContract.CategoryColumns.CATEGORY_DESCRIPTION + " TEXT,"
                + " UNIQUE (" + AssistantContract.CategoryColumns.CATEGORY_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("INSERT INTO " + Tables.CATEGORY + "(" + AssistantContract.Category.CATEGORY_ID + "," + AssistantContract.Category.CATEGORY_NAME + "," + AssistantContract.Category.CATEGORY_DESCRIPTION + ") VALUES ("
                    + "'1', 'Mobile Account', 'All accounts in mobile'" + ")");
        db.execSQL("INSERT INTO " + Tables.CATEGORY + "(" + AssistantContract.Category.CATEGORY_ID + "," + AssistantContract.Category.CATEGORY_NAME + "," + AssistantContract.Category.CATEGORY_DESCRIPTION + ") VALUES ("
                    + "'2', 'Desktop Account', 'All accounts in desktop'" + ")");
        db.execSQL("INSERT INTO " + Tables.CATEGORY + "(" + AssistantContract.Category.CATEGORY_ID + "," + AssistantContract.Category.CATEGORY_NAME + "," + AssistantContract.Category.CATEGORY_DESCRIPTION + ") VALUES ("
                    + "'3', 'Credit Account', 'All credit accounts'" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
