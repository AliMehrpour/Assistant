// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Account
 */
@ParseClassName("Account")
public class Account extends ParseObject {
    private static final String TITLE       = "title";
    private static final String CATEGORY    = "category";

    public String getTitle() {
        return getString(TITLE);
    }

    public void setTitle(String title) {
        put(TITLE, title);
    }

    public Category getCategory() {
        return (Category) getParseObject(CATEGORY);
    }

    public void setCategory(Category category) {
        put(CATEGORY, category);
    }

}
