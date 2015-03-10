// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.model;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.volcano.esecurebox.backend.ParseManager;
import com.volcano.esecurebox.backend.TimedoutQuery;
import com.volcano.esecurebox.util.LogUtils;

import java.util.List;

/**
 * A Category
 */
@ParseClassName("Category")
public class Category extends ParseObject {
    private static final String TAG = LogUtils.makeLogTag(Category.class);

    private static final String NAME            = "name";
    private static final String COLOR           = "color";
    private static final String ICON_NAME       = "iconName";
    private static final String ORDER           = "order";

    public static ParseQuery findInBackground(Object tag, final FindCallback<Category> callback) {
        final ParseQuery<Category> query = getQuery();

        new TimedoutQuery<>(query).findInBackground(tag, new FindCallback<Category>() {
            @Override
            public void done(List<Category> categories, ParseException e) {
                callback.done(categories, e);
            }
        });

        return query;
    }

    public static ParseQuery getInBackground(Object tag, String categoryId, final GetCallback<Category> callback) {
        final ParseQuery<Category> query = getQuery();
        query.whereEqualTo("objectId", categoryId);
        new TimedoutQuery<>(query).getInBackground(tag, new GetCallback<Category>() {
            @Override
            public void done(Category category, ParseException e) {
                callback.done(category, e);
            }
        });

        return query;
    }

    public static ParseQuery<Category> getQuery() {
        final ParseQuery<Category> query = ParseQuery.getQuery(Category.class);
        query.orderByAscending(ORDER);
        if (ParseManager.isLocalDatabaseActive()) {
            query.fromLocalDatastore();
        }

        return query;
    }

    /**
     * This method only should call for getting categories for first time and exclusively called by
     * {@link com.volcano.esecurebox.backend.ParseManager#InitializeData(com.volcano.esecurebox.backend.ParseManager.OnDataInitializationListener)}
     * @param callback The callback
     */
    public static void pinAllInBackground(final FindCallback<Category> callback) {
        final ParseQuery<Category> query = ParseQuery.getQuery(Category.class);
        query.findInBackground(new FindCallback<Category>() {
            @Override
            public void done(List<Category> categories, ParseException e) {
                try {
                    if (e == null) {
                        // Save in local database
                        ParseObject.pinAll(categories);
                        LogUtils.LogI(TAG, String.format("pinned %d categories on local database", categories.size()));
                    }
                    callback.done(categories, e);
                } catch (ParseException e1) {
                    LogUtils.LogE(TAG, "pinning categories failed", e1);
                    callback.done(categories, e1);
                }
            }
        });
    }

    public String getName() {
        return getString(NAME);
    }

    public String getIconName() {
        return getString(ICON_NAME);
    }

    public String getColor() {
        return getString(COLOR);
    }
}
