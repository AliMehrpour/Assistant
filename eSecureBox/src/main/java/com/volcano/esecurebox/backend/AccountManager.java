// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.backend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.volcano.esecurebox.Intents;
import com.volcano.esecurebox.Managers;
import com.volcano.esecurebox.VlApplication;
import com.volcano.esecurebox.model.User;
import com.volcano.esecurebox.security.SecurityUtils;
import com.volcano.esecurebox.util.PrefUtils;

/**
 * Manager handles all operation for users
 */
public class AccountManager {

    /**
     * Because we need user always enter his password, so Logout user at startup
     */
    public AccountManager() {
        signout();
    }

    public boolean isLoggedIn() {
        return ParseUser.getCurrentUser() != null;
    }

    public User getCurrentUser() {
        return (User) ParseUser.getCurrentUser();
    }

    public void signin(final String username, final String password, final LogInCallback callback) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    initialization(username, password);
                }
                callback.done(parseUser, e);
            }
        });
    }

    public void signout() {
        broadcastLoginReset();
        ParseUser.logOut();
    }

    public void signup(final String username, final String name, final String password, final String email, final SignUpCallback callback) {
        final User user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setPassword(password);
        user.setEmail(email);
        user.setEncryptionSalt(SecurityUtils.generateSalt());
        user.setEncryptionIv(SecurityUtils.generateIv());

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    initialization(username, password);
                }
                callback.done(e);
            }
        });
    }

    public void changePassword(String oldPassword, final String newPassword, final SaveCallback callback) {
        final User user = getCurrentUser();
        Managers.getAccountManager().signin(user.getUsername(),
                oldPassword,
                new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (e == null) {
                            user.setPassword(newPassword);
                            user.save(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    callback.done(e);
                                }
                            });
                        }
                        else {
                            callback.done(new ParseException(ParseException.PASSWORD_MISSING, ""));
                        }
                    }
                });
    }

    public void updateUser(String name, String username, final SaveCallback callback) {
        final User user = getCurrentUser();
        if (user.getName().equals(name) && user.getUsername().equals(username)) {
            callback.done(null);
        }
        else {
            user.setName(name);
            user.setUsername(username);
            user.save(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        broadcastLoginReset();
                    }
                    callback.done(e);
                }
            });
        }
    }

    /**
     * Register a broadcast receiver to get notified when the login status of user has been changed
     */
    public static void registerLoginResetReceiver(Context context, BroadcastReceiver receiver) {
        final IntentFilter filter = new IntentFilter(Intents.ACTION_LOGIN_RESET);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);
    }

    /**
     * Used to send a broadcast when login status changed
     */
    private void broadcastLoginReset() {
        Managers.getMixpanelManager().setIdentity(getCurrentUser() == null ? null : getCurrentUser().getObjectId());
        LocalBroadcastManager.getInstance(VlApplication.getInstance()).sendBroadcast(new Intent(Intents.ACTION_LOGIN_RESET));
    }

    private void initialization(String username, String password) {
        SecurityUtils.initializeCryptography(password);

        broadcastLoginReset();

        final String lastUsername = PrefUtils.getPref(PrefUtils.PREF_LAST_USERNAME, "");
        if (!lastUsername.equals(username)) {
            Managers.getApplicationLockManager().getApplicationLock().setPasscode(null);
            PrefUtils.setPref(PrefUtils.PREF_NAVIGATOR_USER_LEARNED, false);
        }
        PrefUtils.setPref(PrefUtils.PREF_LAST_USERNAME, username);
    }
}
