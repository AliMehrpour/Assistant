// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant;

import android.content.Intent;
import android.net.Uri;

import com.volcano.assistant.activity.CreateAccountActivity;
import com.volcano.assistant.activity.DisplayAccountActivity;
import com.volcano.assistant.activity.MainActivity;
import com.volcano.assistant.activity.PasscodeActivity;
import com.volcano.assistant.activity.SigninActivity;
import com.volcano.assistant.activity.SignupActivity;
import com.volcano.assistant.fragment.PasscodeFragment.Mode;

/**
 * Contain various application-wide Intent constants
 */
public final class Intents {

    public static final String INTENT_NAMESPACE = Intents.class.getPackage().getName();
    public static final String ACTION_LOGIN_RESET   = INTENT_NAMESPACE + ".login_reset";

    public static final String EXTRA_ACCOUNT_ID         = INTENT_NAMESPACE + "account_id";
    public static final String EXTRA_ACCOUNT_TITLE      = INTENT_NAMESPACE + "account_title";
    public static final String EXTRA_CATEGORY_COLOR     = INTENT_NAMESPACE + "category_color";
    public static final String EXTRA_CATEGORY_ID        = INTENT_NAMESPACE + "category_id";
    public static final String EXTRA_MODE               = INTENT_NAMESPACE + "mode";

    public static final String KEY_APPROVED         = "approved";
    public static final String KEY_CATEGORY_ID      = "category_id";
    public static final String KEY_PASSCODE         = "passcode";
    public static final String KEY_POSITION         = "position";
    public static final String KEY_SUB_CATEGORY_ID  = "sub_category_id";

    public static final int REQUEST_CODE_SIGNIN   = 1;
    public static final int REQUEST_CODE_SIGNUP   = 2;

    public static Intent getMainIntent() {
        return new Intent(VlApplication.getInstance(), MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    public static Intent getCreateAccountIntent(String categoryId, String color) {
        return new Intent(VlApplication.getInstance(), CreateAccountActivity.class)
                .putExtra(EXTRA_CATEGORY_ID, categoryId)
                .putExtra(EXTRA_CATEGORY_COLOR, color);
    }

    public static Intent getDisplayAccountIntent(String accountId, String color, String title) {
        return new Intent(VlApplication.getInstance(), DisplayAccountActivity.class)
                .putExtra(EXTRA_ACCOUNT_ID, accountId)
                .putExtra(EXTRA_CATEGORY_COLOR, color)
                .putExtra(EXTRA_ACCOUNT_TITLE, title);
    }

    public static Intent getEditAccountIntent(String accountId, String color) {
        return new Intent(VlApplication.getInstance(), CreateAccountActivity.class)
                .putExtra(EXTRA_ACCOUNT_ID, accountId)
                .putExtra(EXTRA_CATEGORY_COLOR, color);
    }

    public static Intent getPasscodeIntent(Mode passcodeMode) {
        return new Intent(VlApplication.getInstance(), PasscodeActivity.class)
               .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
               .putExtra(Intents.EXTRA_MODE, passcodeMode);
    }

    public static Intent getEmailIntentSubjectTo(String subject, String[] to) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        emailIntent.putExtra(Intent.EXTRA_EMAIL  ,to)
        .putExtra(Intent.EXTRA_SUBJECT, subject);
        return emailIntent;
    }

    public static Intent getEmailIntentSubjectBody(String subject, String shareBody) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
                .putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        return sharingIntent;
    }

    public static Intent getSigninIntent() {
        return new Intent(VlApplication.getInstance(), SigninActivity.class);
    }

    public static Intent getSignupIntent() {
        return new Intent(VlApplication.getInstance(), SignupActivity.class);
    }
}
