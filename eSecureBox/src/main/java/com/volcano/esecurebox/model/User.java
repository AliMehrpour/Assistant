// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.model;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.volcano.esecurebox.backend.ParseManager;
import com.volcano.esecurebox.util.LogUtils;

import java.util.List;

/**
 * Represent a user object
 */
public final class User extends ParseUser {
    private static final String TAG = LogUtils.makeLogTag(SubCategory.class);

    private static final String NAME = "name";
    private static final String ENCRYPTION_SALT = "encryptionSalt";
    private static final String ENCRYPTION_IV   = "encryptionIv";

    /**
     * This method only should call for getting fields for first time and exclusively called by
     * {@link com.volcano.esecurebox.backend.ParseManager#InitializeData(com.volcano.esecurebox.backend.ParseManager.OnDataInitializationListener)}
     * @param callback The callback
     */
    public static void pinAllInBackground(final FindCallback<User> callback) {
        final ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> users, ParseException e) {
                try {
                    if (e == null) {
                        // Save in local database
                        ParseObject.pinAll(users);
                        LogUtils.LogI(TAG, String.format("pinned %d user on local database", users.size()));
                    }
                    callback.done(users, e);
                } catch (ParseException e1) {
                    LogUtils.LogE(TAG, "pinning user failed", e1);
                    callback.done(users, e1);
                }
            }
        });
    }

    public void setName(String name) {
        put(NAME, name);
    }

    public String getName() {
        return getString(NAME);
    }

    public void setEncryptionSalt(String salt) {
        put(ENCRYPTION_SALT, salt);
    }

    public String getEncryptionSalt() {
        return getString(ENCRYPTION_SALT);
    }

    public void setEncryptionIv(String iv) {
        put(ENCRYPTION_IV, iv);
    }

    public String getEncryptionIv() {
        return getString(ENCRYPTION_IV);
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
