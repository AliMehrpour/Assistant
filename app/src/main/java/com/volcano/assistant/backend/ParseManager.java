// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.backend;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.volcano.assistant.VlApplication;
import com.volcano.assistant.model.Account;
import com.volcano.assistant.model.AccountFieldValue;
import com.volcano.assistant.model.Category;
import com.volcano.assistant.model.Field;
import com.volcano.assistant.model.SubCategory;
import com.volcano.assistant.model.SubCategoryField;
import com.volcano.assistant.model.User;
import com.volcano.assistant.util.LogUtils;

import java.util.Date;
import java.util.List;

/**
 * Manager for all Parse library stuffs
 */
public final class ParseManager {
    private static final String TAG = LogUtils.makeLogTag(ParseManager.class);

    private static int INITIALIZE_DATA_TIME_OUT_MILLIS  = 20 * 1000; // 20 seconds

    private static boolean isCategoriesPinned = false;
    private static boolean isSubCategoriesPinned = false;
    private static boolean isFieldsPinned = false;
    private static boolean isSubCategoryFieldsPinned = false;
    private static boolean isCurrentUserPinned = false;

    private ParseRequestManager mRequestManager;

    public interface Listener {
        /**
         * Called when a successful response received
         */
        public void onResponse();

        /**
         * Called when an error has been occurred
         */
        public void onErrorResponse(ParseException e);
    }

    public interface OnDataInitializationListener {
        public void onInitilize(boolean successful);
    }

    private static final String APPLICATION_ID = "pBtCshXLPqwVBXhlNq6zLBTXUdMI8nNqblfASxNT";
    private static final String CLIENT_KEY     = "EtvsJB26jRBPGnkZalvIbevtQZUTcEFknZuiesvq";

    public ParseManager() {
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Category.class);
        ParseObject.registerSubclass(SubCategory.class);
        ParseObject.registerSubclass(SubCategoryField.class);
        ParseObject.registerSubclass(Field.class);
        ParseObject.registerSubclass(Account.class);
        ParseObject.registerSubclass(AccountFieldValue.class);
        ParseCrashReporting.enable(VlApplication.getInstance());
        Parse.enableLocalDatastore(VlApplication.getInstance());
        Parse.initialize(VlApplication.getInstance(), APPLICATION_ID, CLIENT_KEY);

        mRequestManager = new ParseRequestManager();
    }

    public static void InitializeData(final OnDataInitializationListener listener) {
        if (isLocalDatabaseActive()) {
            final Date startTime = new Date();
            // Pin categories
            Category.pinAllInBackground(new FindCallback<Category>() {
                @Override
                public void done(List<Category> categories, ParseException e) {
                    isCategoriesPinned = e == null;
                }
            });

            // Pin sub categories
            SubCategory.pinAllInBackground(new FindCallback<SubCategory>() {
                @Override
                public void done(List<SubCategory> subCategories, ParseException e) {
                    LogUtils.LogI(TAG, "Pinning sub categories.");
                    isSubCategoriesPinned = e == null;
                }
            });

            // Pin fields
            Field.pinAllInBackground(new FindCallback<Field>() {
                @Override
                public void done(List<Field> fields, ParseException e) {
                    isFieldsPinned = e == null;
                }
            });

            // Pin sub category fields
            SubCategoryField.pinAllInBackground(new FindCallback<SubCategoryField>() {
                @Override
                public void done(List<SubCategoryField> subCategoryFields, ParseException e) {
                    isSubCategoryFieldsPinned = e == null;
                }
            });

            // Pin current user
            User.pinAllInBackground(new FindCallback<User>() {
                @Override
                public void done(List<User> users, ParseException e) {
                    isCurrentUserPinned = e == null;
                }
            });

            // Check initialize has been done or not
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        final Date now = new Date();
                        final long elapsedMillis = now.getTime() - startTime.getTime();
                        final boolean result = isCategoriesPinned && isSubCategoriesPinned && isFieldsPinned &&
                                isSubCategoryFieldsPinned && isCurrentUserPinned;

                        if (result || elapsedMillis >= INITIALIZE_DATA_TIME_OUT_MILLIS) {
                            final float elapsedSeconds = elapsedMillis / 1000;
                            if (result) {
                                LogUtils.LogI(TAG, "Initialization finished in " + elapsedSeconds + " seconds");
                                listener.onInitilize(true);
                                break;
                            }
                            else {
                                LogUtils.LogI(TAG, "Initialization failed after " + elapsedSeconds + " seconds");
                                listener.onInitilize(false);
                                break;
                            }
                        }

                    }
                }
            };

            new Thread(runnable).start();
        }
        else {
            listener.onInitilize(true);
        }
    }

    /**
     * @return True if local database is active, otherwise false
     */
    public static boolean isLocalDatabaseActive() {
        return false;
    }

    /**
     * Get the {@link com.volcano.assistant.backend.ParseRequestManager}
     * @return The ParseRequestManager
     */
    public ParseRequestManager getRequestManager() {
        return mRequestManager;
    }
}