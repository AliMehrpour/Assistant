// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.model;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.volcano.esecurebox.backend.ParseManager;
import com.volcano.esecurebox.backend.TimeoutQuery;
import com.volcano.esecurebox.util.LogUtils;

import java.util.List;

/**
 * A Field in a {@link Category}
 */
@ParseClassName("Field")
public class Field extends ParseObject {
    private static final String TAG = LogUtils.makeLogTag(SubCategory.class);

    public final static int TYPE_STRING             = 1;
    public final static int TYPE_STRING_MULTILINE   = 2;
    public final static int TYPE_DATE               = 3;
    public final static int TYPE_PASSWORD_NUMBER    = 4;   // like Pin
    public final static int TYPE_PASSWORD           = 5;
    public final static int TYPE_URL                = 6;
    public final static int TYPE_PHONE              = 7;
    public final static int TYPE_ENUM               = 8;
    public final static int TYPE_EMAIL              = 9;
    public final static int TYPE_NUMBER             = 10;

    private final static String ICON_NAME   = "iconName";
    private final static String NAME        = "name";
    private final static String TYPE        = "type";


    public static void findInBackground(Object tag, final FindCallback<Field> callback) {
        final ParseQuery<Field> query = getQuery()
                .orderByAscending(NAME);
        new TimeoutQuery<>(query).findInBackground(tag, callback);
    }

    /**
     * This method only should call for getting fields for first time and exclusively called by
     * {@link com.volcano.esecurebox.backend.ParseManager#InitializeData(com.volcano.esecurebox.backend.ParseManager.OnDataInitializationListener)}
     * @param callback The callback
     */
    public static void pinAllInBackground(final FindCallback<Field> callback) {
        final ParseQuery<Field> query = ParseQuery.getQuery(Field.class);
        query.findInBackground(new FindCallback<Field>() {
            @Override
            public void done(List<Field> fields, ParseException e) {
                try {
                    if (e == null) {
                        // Save in local database
                        ParseObject.pinAll(fields);
                        LogUtils.LogI(TAG, String.format("pinned %d fields on local database", fields.size()));
                    }
                    callback.done(fields, e);
                } catch (ParseException e1) {
                    LogUtils.LogE(TAG, "pinning fields failed", e1);
                    callback.done(fields, e1);
                }
            }
        });
    }

    public static ParseQuery<Field> getQuery() {
        final ParseQuery<Field> query = ParseQuery.getQuery(Field.class);
        if (ParseManager.isLocalDatabaseActive()) {
            query.fromLocalDatastore();
        }

        return query;
    }

    public String getName() {
        return getString(NAME);
    }

    public int getType() {
        return getInt(TYPE);
    }

    public String getIconName() {
        return getString(ICON_NAME);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Field) && (getObjectId().equals(((Field) o).getObjectId()));
    }

    @Override
    public int hashCode() {
        return getObjectId().hashCode();
    }
}
