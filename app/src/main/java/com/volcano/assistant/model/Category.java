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

    private static final String NAME            = "name";
    private static final String COLOR           = "color";
    private static final String SUB_CATEGORIES  = "subCategories";

    public static ParseQuery<Category> getQuery() {
        final ParseQuery<Category> query = ParseQuery.getQuery(Category.class);
        query.include(SUB_CATEGORIES);
        return query;
    }

    public String getName() {
        return getString(NAME);
    }

    public void setName(String name) {
        put(NAME, name);
    }

    public String getColor() {
        return getString(COLOR);
    }

    public void setColor(String color) {
        put(COLOR, color);
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<SubCategory> getSubCategories() {
        return getList(SUB_CATEGORIES);
    }
}
