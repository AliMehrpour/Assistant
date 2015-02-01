// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * A Category
 */
@ParseClassName("Category")
public class Category extends ParseObject {

    private static final String COLOR           = "color";
    private static final String DESCRIPTION     = "description";
    private static final String IS_PRIMARY      = "isPrimary";
    private static final String NAME            = "name";
    private static final String FIELDS          = "fields";

    public static ParseQuery<Category> getQuery() {
        final ParseQuery<Category> query = ParseQuery.getQuery(Category.class);
        query.include(FIELDS);
        return query;
    }

    public String getName() {
        return getString(NAME);
    }

    public void setName(String name) {
        put(NAME, name);
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getDescription() {
        return getString(DESCRIPTION);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setDescription(String description) {
        put(DESCRIPTION, description);
    }

    public String getColor() {
        return getString(COLOR);
    }

    public void setColor(String color) {
        put(COLOR, color);
    }

    @SuppressWarnings("UnusedDeclaration")
    public boolean isPrimary() {
        return getBoolean(IS_PRIMARY);
    }

    public List<Field> getFields() {
        return getList(FIELDS);
    }
}
