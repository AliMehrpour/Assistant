// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * A Field in a {@link Category}
 */
@ParseClassName("Field")
@SuppressWarnings("UnusedDeclaration")
public class Field extends ParseObject {
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
        return ParseQuery.getQuery(Field.class);
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
