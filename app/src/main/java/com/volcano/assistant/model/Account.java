// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.model;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.volcano.assistant.backend.ParseManager;

import java.util.Date;
import java.util.List;

/**
 * Account
 */
@ParseClassName("Account")
public class Account extends ParseObject {
    private static final String TITLE           = "title";
    private static final String CREATE_DATE     = "createDate";
    private static final String SUB_CATEGORY    = "subCategory";

    public static void findInBackground(Category category, final FindCallback<Account> callback) {
        final ParseQuery<SubCategory> innerQuery = SubCategory.getQuery();
        innerQuery.whereEqualTo(SubCategory.CATEGORY, category);

        final ParseQuery<Account> query = getQuery();
        query.whereMatchesQuery(SUB_CATEGORY, innerQuery);
        query.findInBackground(new FindCallback<Account>() {
            @Override
            public void done(List<Account> accounts, ParseException e) {
                callback.done(accounts, e);
            }
        });
    }

    public static void getFirstInBackground(String accountId, final GetCallback<Account> callback) {
        final ParseQuery<Account> query = getQuery()
                .whereEqualTo("objectId", accountId);
        query.getFirstInBackground(new GetCallback<Account>() {
            @Override
            public void done(Account account, ParseException e) {
                callback.done(account, e);
            }
        });
    }

    public static ParseQuery<Account> getQuery() {
        final ParseQuery<Account> query = ParseQuery.getQuery(Account.class);
        query.orderByAscending(TITLE);
        query.include(SUB_CATEGORY);
        if (ParseManager.isLocalDatabaseActive()) {
            query.fromLocalDatastore();
        }

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

    public void setSubCategory(SubCategory subCategory) {
        put(SUB_CATEGORY, subCategory);
    }

    public Date getCreateDate() {
        //final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        //return sdf.format(get(CREATE_DATE));
        return getDate(CREATE_DATE);
    }

    public void setCreateDate(Date date) {
        put(CREATE_DATE, date);
    }

    public void save(final SaveCallback callback) {
        final SaveCallback saveCallback = new SaveCallback() {
            @Override
            public void done(ParseException e) {
                callback.done(e);
            }
        };

        if (ParseManager.isLocalDatabaseActive()) {
            pinInBackground(saveCallback);
        }
        else {
            saveInBackground(saveCallback);
        }
    }
}
