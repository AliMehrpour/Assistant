// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.model;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.volcano.assistant.util.LogUtils;

import java.util.List;

/**
 * A Field in a {@link Category}
 */
@ParseClassName("Field")
@SuppressWarnings("UnusedDeclaration")
public class Field extends ParseObject {
    private static final String TAG = LogUtils.makeLogTag(SubCategory.class);

    public final static int TYPE_STRING             = 1;
    public final static int TYPE_STRING_MULTILINE   = 2;
    public final static int TYPE_DATE               = 3;
    public final static int TYPE_TIME               = 4;
    public final static int TYPE_PASSWORD           = 5;
    public final static int TYPE_URL                = 6;
    public final static int TYPE_PHONE              = 7;
    public final static int TYPE_ENUM               = 8;

    private final static String ICON_NAME   = "iconName";
    private final static String NAME        = "name";
    private final static String TYPE        = "type";

    public static ParseQuery<Field> getQuery() {
        return ParseQuery.getQuery(Field.class)
                .fromLocalDatastore();
    }

    /**
     * This method only should call for getting fields for first time and exclusively called by
     * {@link com.volcano.assistant.backend.ParseManager#InitializeData(com.volcano.assistant.backend.ParseManager.OnDataInitializationListener)}
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

    public String getName() {
        return getString(NAME);
    }

    public void setName(String name) {
        put(NAME, name);
    }

    public int getType() {
        return getInt(TYPE);
    }

    public void setType(int type) {
        put(TYPE, type);
    }

    public String getIconName() {
        return getString(ICON_NAME);
    }

    public void setIconUrl(String iconName) {
        put(ICON_NAME, iconName);
    }
}
