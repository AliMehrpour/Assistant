// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A Field in a {@link Category}
 */
@ParseClassName("Field")
@SuppressWarnings("UnusedDeclaration")
public class Field extends ParseObject {
    public final static int TYPE_STRING     = 1;
    public final static int TYPE_DATE       = 2;
    public final static int TYPE_TIME       = 3;
    public final static int TYPE_PASSWORD   = 4;
    public final static int TYPE_URL        = 5;
    public final static int TYPE_PHONE      = 6;
    public final static int TYPE_ENUM       = 7;

    private final static String DESCRIPTION = "description";
    private final static String ICON_NAME   = "iconName";
    private final static String ICON_URL    = "iconUrl";
    private final static String NAME        = "name";
    private final static String TYPE        = "type";
    private final static String USER        = "user";

    public static ParseQuery<Field> getQuery() {
        return ParseQuery.getQuery(Field.class);
    }

    public String getName() {
        return getString(NAME);
    }

    public void setName(String name) {
        put(NAME, name);
    }

    public String getDescription() {
        return getString(DESCRIPTION);
    }

    public void setDescription(String description) {
        put(DESCRIPTION, description);
    }

    public int getType() {
        return getInt(TYPE);
    }

    public void setType(int type) {
        put(TYPE, type);
    }

    public User getUser() {
        return (User) getParseObject(USER);
    }

    public URL getIconUrl() {
        try {
            return new URL(getString(ICON_URL));
        }
        catch (MalformedURLException ex) {
            return null;
        }
    }

    public void setIconUrl(URL iconUrl) {
        put(ICON_URL, iconUrl);
    }

    public String getIconName() {
        return getString(ICON_NAME);
    }

    public void setIconUrl(String iconName) {
        put(ICON_NAME, iconName);
    }
}
