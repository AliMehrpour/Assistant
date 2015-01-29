package com.volcano.assistant.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by alimehrpour on 1/12/15.
 */
@ParseClassName("Category")
public class Category extends ParseObject {

    private static final String COLOR           = "color";
    private static final String DESCRIPTION     = "description";
    private static final String IS_PRIMARY      = "isPrimary";
    private static final String NAME            = "name";

    public static ParseQuery<Category> getQuery() {
        return ParseQuery.getQuery(Category.class);
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

    public String getColor() {
        return getString(COLOR);
    }

    public void setColor(String color) {
        put(COLOR, color);
    }

    public boolean isPrimary() {
        return getBoolean(IS_PRIMARY);
    }
}
