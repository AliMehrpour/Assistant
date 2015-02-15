package com.volcano.assistant;

import android.content.Intent;

import com.volcano.assistant.activity.CreateAccountActivity;
import com.volcano.assistant.activity.MainActivity;
import com.volcano.assistant.activity.SigninActivity;
import com.volcano.assistant.activity.SignupActivity;

/**
 * Contain various application-wide Intent constants
 */
public final class Intents {

    public static final String INTENT_NAMESPACE = Intents.class.getPackage().getName();
    public static final String ACTION_LOGIN_RESET   = INTENT_NAMESPACE + ".login_reset";

    public static final String EXTRA_CATEGORY_COLOR     = INTENT_NAMESPACE + "category_color";
    public static final String EXTRA_CATEGORY_ID        = INTENT_NAMESPACE + "category_id";
    public static final String EXTRA_RESET              = INTENT_NAMESPACE + "reset";

    public static final String KEY_INITIALIZED      = "initialized";
    public static final String KEY_POSITION         = "position";
    public static final String KEY_SUB_CATEGORY_ID  = "sub_category_id";

    public static final int REQUEST_CODE_SIGNUP_LOGIN   = 1;

    public static Intent getMainIntent(boolean reset) {
        final Intent intent = new Intent(VlApplication.getInstance(), MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(EXTRA_RESET, reset);

        return intent;
    }

    public static Intent getCreateAccountIntent(String categoryId, String color) {
        return new Intent(VlApplication.getInstance(), CreateAccountActivity.class)
                .putExtra(EXTRA_CATEGORY_ID, categoryId)
                .putExtra(EXTRA_CATEGORY_COLOR, color);
    }

    public static Intent getSigninIntent() {
        return new Intent(VlApplication.getInstance(), SigninActivity.class);
    }

    public static Intent getSignupIntent() {
        return new Intent(VlApplication.getInstance(), SignupActivity.class);
    }

}
