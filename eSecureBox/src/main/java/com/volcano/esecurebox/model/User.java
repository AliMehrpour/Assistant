package com.volcano.esecurebox.model;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.volcano.esecurebox.util.LogUtils;

import java.util.List;

/**
 * Represent a user object
 */
public final class User extends ParseUser {
    private static final String TAG = LogUtils.makeLogTag(SubCategory.class);

    private static final String NAME = "name";

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
}
