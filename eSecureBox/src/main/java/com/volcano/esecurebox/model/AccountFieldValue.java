// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.model;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.volcano.esecurebox.backend.ParseManager;
import com.volcano.esecurebox.backend.TimeoutQuery;
import com.volcano.esecurebox.security.SecurityUtils;

import java.util.List;

/**
 * AcountFieldValue that contain one account object, one field object and value
 */
@ParseClassName("AccountFieldValue")
public class AccountFieldValue extends ParseObject {
    private static final String ACCOUNT     = "account";
    private static final String FIELD       = "field";
    private static final String VALUE       = "value";
    private static final String ORDER       = "order";

    private Status mStatus = Status.EXIST;

    public enum Status {
        EXIST,
        ADDED,
        EXIST_REMOVED, // An existed object is removed
        ADDED_REMOVED // An new added object is removed
    }

    public static ParseQuery findInBackground(Object tag, Account account, final FindCallback<AccountFieldValue> callback) {
        final ParseQuery<AccountFieldValue> query = getQuery()
                .whereEqualTo(ACCOUNT, account)
                .include(FIELD)
                .orderByAscending(ORDER);
        new TimeoutQuery<>(query).findInBackground(tag, new FindCallback<AccountFieldValue>() {
            @Override
            public void done(List<AccountFieldValue> accountFieldValues, ParseException e) {
                callback.done(accountFieldValues, e);
            }
        });

        return query;
    }

    public static ParseQuery<AccountFieldValue> getQuery() {
        final ParseQuery<AccountFieldValue> query = ParseQuery.getQuery(AccountFieldValue.class);
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

    public void remove(final DeleteCallback callback) {
        final DeleteCallback deleteCallback = new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                callback.done(e);
            }
        };

        if (ParseManager.isLocalDatabaseActive()) {
            unpinAllInBackground(deleteCallback);
        }
        else {
            deleteInBackground(deleteCallback);
        }
    }

    public static void remove(List<AccountFieldValue> objects, final DeleteCallback callback) {
        final DeleteCallback deleteCallback = new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                callback.done(e);
            }
        };

        if (ParseManager.isLocalDatabaseActive()) {
            unpinAllInBackground(objects, deleteCallback);
        }
        else {
            deleteAllInBackground(objects, deleteCallback);
        }
    }

    public AccountFieldValue() {
    }

    public AccountFieldValue(Account account, Field field, Status status) {
        put(ACCOUNT, account);
        put(FIELD, field);
        mStatus = status;
    }

    public Account getAccount() {
        return (Account) getParseObject(ACCOUNT);
    }

    public void setAccount(Account account) {
        put(ACCOUNT, account);
    }

    public Field getField() {
        return (Field) getParseObject(FIELD);
    }

    public void setField(Field field) {
        put(FIELD, field);
    }

    public String getValue() {
        return SecurityUtils.decrypt(getString(VALUE));
    }

    public void setValue(String value) {
        put(VALUE, SecurityUtils.encrypt(value));
    }

    public void setOrder(int order) {
        put(ORDER, order);
    }

    public int getOrder() {
        return getInt(ORDER);
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        mStatus = status;
    }
}
