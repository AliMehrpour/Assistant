// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.model;

import android.text.TextUtils;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.volcano.esecurebox.backend.ParseManager;
import com.volcano.esecurebox.util.LogUtils;

import java.util.List;

/**
 * A Sub Category
 */
@ParseClassName("SubCategory")
public class SubCategory extends ParseObject {
    private static final String TAG = LogUtils.makeLogTag(SubCategory.class);

    private static final String NAME        = "name";
    private static final String ORDER       = "order";
    private static final String ICON_NAME   = "iconName";
    public static final String CATEGORY     = "category";

    public static ParseQuery findInBackground(Category category, final FindCallback<SubCategory> callback) {
        final ParseQuery<SubCategory> query = getQuery();
        query.whereEqualTo(CATEGORY, category);
        query.findInBackground(new FindCallback<SubCategory>() {
            @Override
            public void done(List<SubCategory> categories, ParseException e) {
                callback.done(categories, e);
            }
        });

        return query;
    }

    public static ParseQuery getInBackground(String subCategoryId, final GetCallback<SubCategory> callback) {
        final ParseQuery<SubCategory> query = getQuery();
        query.whereEqualTo("objectId", subCategoryId);
        query.include(CATEGORY);
        query.getFirstInBackground(new GetCallback<SubCategory>() {
            @Override
            public void done(SubCategory subCategory, ParseException e) {
                callback.done(subCategory, e);
            }
        });

        return query;
    }

    public static ParseQuery<SubCategory> getQuery() {
        final ParseQuery<SubCategory> query = ParseQuery.getQuery(SubCategory.class);
        query.orderByAscending(ORDER);
        if (ParseManager.isLocalDatabaseActive()) {
            query.fromLocalDatastore();
        }

        return query;
    }

    /**
     * This method only should call for getting sub categories for first time and exclusively called by
     * {@link com.volcano.esecurebox.backend.ParseManager#InitializeData(com.volcano.esecurebox.backend.ParseManager.OnDataInitializationListener)}
     * @param callback The callback
     */
    public static void pinAllInBackground(final FindCallback<SubCategory> callback) {
        final ParseQuery<SubCategory> query = ParseQuery.getQuery(SubCategory.class);
        query.findInBackground(new FindCallback<SubCategory>() {
            @Override
            public void done(List<SubCategory> subCategories, ParseException e) {
                try {
                    if (e == null) {
                        // Save in local database
                        ParseObject.pinAll(subCategories);
                        LogUtils.LogI(TAG, String.format("pinned %d sub categories on local database", subCategories.size()));
                    }
                    callback.done(subCategories, e);
                } catch (ParseException e1) {
                    LogUtils.LogE(TAG, "pinning sub categories failed", e1);
                    callback.done(subCategories, e1);
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

    public boolean hasIcon() {
        return !TextUtils.isEmpty(getIconName());
    }

    public Category getCategory() {
        return (Category) getParseObject(CATEGORY);
    }
}
