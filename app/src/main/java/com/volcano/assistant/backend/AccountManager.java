package com.volcano.assistant.backend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.volcano.assistant.Intents;
import com.volcano.assistant.VlApplication;
import com.volcano.assistant.model.User;

/**
 * Manager handles all operation for users
 */
public class AccountManager {

    public static final int ERROR_CODE_USERNAME_EXIST = 202;
    public static final int ERROR_CODE_EMAIL_EXIST = 203;

    public boolean isLoggedIn() {
        return ParseUser.getCurrentUser() != null;
    }

    public User getCurrentUser() {
        return (User) ParseUser.getCurrentUser();
    }

    public void signin(String username, String password, final ParseManager.Listener listener) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    broadcastLoginReset();
                    listener.onResponse();
                }
                else {
                    listener.onErrorResponse(e);
                }
            }
        });
    }

    public void signout() {
        broadcastLoginReset();
        ParseUser.logOut();
    }

    public void signup(String username, String name, String password, String email, final ParseManager.Listener listener) {
        final User user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setPassword(password);
        user.setEmail(email);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    broadcastLoginReset();
                    listener.onResponse();
                }
                else {
                    listener.onErrorResponse(e);
                }
            }
        });
    }

    /**
     * Register a broadcast receiver to get notified when the login status of user has been changed
     */
    public static Intent registerLoginResetReceiver(Context context, LoginResetReceiver receiver) {
        final IntentFilter filter = new IntentFilter(Intents.ACTION_LOGIN_RESET);
        return context.registerReceiver(receiver, filter);
    }

    /**
     * Used to send a broadcast when login status changed
     */
    public void broadcastLoginReset() {
        VlApplication.getInstance().sendBroadcast(new Intent(Intents.ACTION_LOGIN_RESET));
    }

    /**
     * Abstract class that handle unparceling of login reset broadcast
     */
    public static abstract class LoginResetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            onReset();
        }

        public abstract void onReset();
    }
}
