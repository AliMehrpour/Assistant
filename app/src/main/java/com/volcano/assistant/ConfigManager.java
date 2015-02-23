// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.volcano.assistant.model.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration manager
 */
public class ConfigManager {

    private static final ArrayList<Category> mCategories = new ArrayList<>();

    public static final boolean IS_DOGFOOD_BUILD = false;

    /**
     * Callback interface to be notified after an on demand category refresh
     */
    public interface RefreshCategoryCallback {
        /**
         * @param isSuccessful True if categories were successfully refreshed
         */
        public void onRefreshComplete(boolean isSuccessful);
    }

    /**
     * @return Categories
     */
    public static ArrayList<Category> getCategories() {
        return mCategories;
    }

    /**
     * Refresh categories on demand
     * @param callback The {@link RefreshCategoryCallback}
     */
    public static void refreshCategories(final RefreshCategoryCallback callback) {
        Category.findInBackground(new FindCallback<Category>() {
            @Override
            public void done(List<Category> categories, ParseException e) {
                if (e == null) {
                    mCategories.clear();
                    mCategories.addAll(categories);
                    callback.onRefreshComplete(true);
                }
                else {
                    callback.onRefreshComplete(false);
                }
            }
        });
    }
}
