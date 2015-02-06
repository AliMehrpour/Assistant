// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * A Sub Category
 */
@SuppressWarnings("UnusedDeclaration")
@ParseClassName("SubCategory")
public class SubCategory extends ParseObject {

    private static final String NAME        = "name";
    private static final String ORDER       = "order";
    private static final String ICON_NAME   = "iconName";

    public static ParseQuery<SubCategory> getQuery() {
        //noinspection UnnecessaryLocalVariable
        final ParseQuery<SubCategory> query = ParseQuery.getQuery(SubCategory.class);
        return query;
    }

    public String getName() {
        return getString(NAME);
    }

    public void setName(String name) {
        put(NAME, name);
    }
}
