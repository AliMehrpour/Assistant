// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.model;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.volcano.esecurebox.backend.ParseManager;
import com.volcano.esecurebox.backend.TimeoutQuery;

import java.util.List;

@ParseClassName("Account")
public final class Account extends ParseObject {
    public static final String SUB_CATEGORY    = "subCategory";
    private static final String TITLE           = "title";
    private static final String USER            = "user";

    public static ParseQuery findInBackground(Object tag, Category category, final FindCallback<Account> callback) {
        final ParseQuery<SubCategory> innerQuery = SubCategory.getQuery()
                .whereEqualTo(SubCategory.CATEGORY, category);

        final ParseQuery<Account> query = getQuery()
                .whereMatchesQuery(SUB_CATEGORY, innerQuery)
                .whereEqualTo(USER, ParseUser.getCurrentUser())
                .include(SUB_CATEGORY + "." + SubCategory.CATEGORY)
                .orderByAscending(TITLE);

        new TimeoutQuery<>(query).findInBackground(tag, new FindCallback<Account>() {
            @Override
            public void done(List<Account> accounts, ParseException e) {
                callback.done(accounts, e);
            }
        });

        return query;
    }

    public static ParseQuery getFirstInBackground(Object tag, String accountId, final GetCallback<Account> callback) {
        final ParseQuery<Account> query = getQuery()
                .whereEqualTo("objectId", accountId)
                .include(SUB_CATEGORY + "." + SubCategory.CATEGORY);

        new TimeoutQuery<>(query).getInBackground(tag, new GetCallback<Account>() {
            @Override
            public void done(Account account, ParseException e) {
                callback.done(account, e);
            }
        });

        return query;
    }

    public static ParseQuery<Account> getQuery() {
        final ParseQuery<Account> query = ParseQuery.getQuery(Account.class)
                .include(SUB_CATEGORY)
                .orderByAscending(TITLE);

        if (ParseManager.isLocalDatabaseActive()) {
            query.fromLocalDatastore();
        }

        return query;
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

    /**
     * Remove this object and provided AccountFiledValue objects from cloud or database
     * @param values The AccountFieldValue values
     * @param callback The callback
     */
    public void remove(final List<AccountFieldValue> values, final DeleteCallback callback) {
        final DeleteCallback deleteCallback = new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    AccountFieldValue.remove(values, callback);
                }
                else {
                    callback.done(e);
                }
            }
        };

        if (ParseManager.isLocalDatabaseActive()) {
            unpinAllInBackground(deleteCallback);
        }
        else {
            deleteInBackground(deleteCallback);
        }
    }

    public String getTitle() {
        return getString(TITLE);
    }

    public void setTitle(String title) {
        put(TITLE, title);
    }

    public SubCategory getSubCategory() {
        return (SubCategory) getParseObject(SUB_CATEGORY);
    }

    public void setSubCategory(SubCategory subCategory) {
        put(SUB_CATEGORY, subCategory);
    }

    public User getUser() {
        return (User) getParseUser(USER);
    }

    public void setUser(User user) {
        put(USER, user);
    }
}
