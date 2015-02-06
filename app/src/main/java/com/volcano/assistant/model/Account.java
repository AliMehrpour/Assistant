// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Account
 */
@ParseClassName("Account")
public class Account extends ParseObject {
    private static final String TITLE           = "title";
    private static final String SUB_CATEGORY    = "subCategory";

    public static ParseQuery<Account> getQuery() {
        final ParseQuery<Account> query = ParseQuery.getQuery(Account.class);
        query.include(SUB_CATEGORY);
        query.orderByAscending("title");
        return query;
    }

    public String getTitle() {
        return getString(TITLE);
    }

    public void setTitle(String title) {
        put(TITLE, title);
    }

    @SuppressWarnings("UnusedDeclaration")
    public SubCategory getSubCategory() {
        return (SubCategory) getParseObject(SUB_CATEGORY);
    }

    public void setCategory(SubCategory subCategory) {
        put(SUB_CATEGORY, subCategory);
    }

}
