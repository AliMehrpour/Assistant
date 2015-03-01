// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox;

import android.content.Intent;

import com.volcano.esecurebox.activity.CreateAccountActivity;
import com.volcano.esecurebox.activity.DisplayAccountActivity;
import com.volcano.esecurebox.activity.MainActivity;
import com.volcano.esecurebox.activity.PasscodeActivity;
import com.volcano.esecurebox.activity.SigninActivity;
import com.volcano.esecurebox.activity.SignupActivity;
import com.volcano.esecurebox.fragment.PasscodeFragment.Mode;

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
               .putExtra(EXTRA_MODE, passcodeMode);
    }

    public static Intent getSigninIntent() {
        return new Intent(VlApplication.getInstance(), SigninActivity.class);
    }

    public static Intent getSignupIntent(int mode) {
        Intent intent =new Intent(VlApplication.getInstance(), SignupActivity.class);
        intent.putExtra(EXTRA_MODE, mode);
        return intent;
    }
}
