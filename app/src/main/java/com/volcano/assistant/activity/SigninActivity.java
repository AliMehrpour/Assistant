// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.volcano.assistant.Intents;
import com.volcano.assistant.Managers;
import com.volcano.assistant.R;
import com.volcano.assistant.backend.ParseManager;
import com.volcano.assistant.util.Utils;

/**
 * Signin activity
 */
public class SigninActivity extends AbstractActivity {

    private EditText mUsernameEdit;
    private EditText mPasswordEdit;
    private TextView mPasswordErrorText;
    private TextView mUsernameErrorText;
    private TextView mSigninText;
    private TextView mSignupText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mUsernameEdit = (EditText) findViewById(R.id.edit_username);
        mUsernameErrorText = (TextView) findViewById(R.id.text_username);
        mPasswordEdit = (EditText) findViewById(R.id.edit_password);
        mPasswordErrorText = (TextView) findViewById(R.id.text_password);
        mSigninText = (TextView) findViewById(R.id.text_signin);
        mSignupText = (TextView) findViewById(R.id.text_signup_email);

        mSigninText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    enable(false);
                    Managers.getAccountManager().signin(mUsernameEdit.getText().toString(), mPasswordEdit.getText().toString(),
                            new ParseManager.Listener() {
                                @Override
                                public void onResponse() {
                                    setResult(RESULT_OK, new Intent());
                                    finish();
                                }

                                @Override
                                public void onErrorResponse(ParseException e) {
                                    enable(true);
                                    Utils.showToast(getResources().getString(R.string.toast_sign_in_invalid));
                                }
                            });
                }
            }
        });

        mPasswordEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    isPasswordValid();
                }
            }
        });

        mUsernameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    isUserNameValid();
                }
            }
        });

        mSignupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(Intents.getSignupIntent(), Intents.REQUEST_CODE_SIGNUP_LOGIN);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Intents.REQUEST_CODE_SIGNUP_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                finish();
            }
        }
    }

    private void enable(boolean enable){
        if (!enable) {
            Utils.hideKeyboard(this);
        }

        mUsernameEdit.setEnabled(enable);
        mPasswordEdit.setEnabled(enable);
        mSigninText.setEnabled(enable);
        mSignupText.setEnabled(enable);
    }

    private boolean isUserNameValid() {
        boolean isValid = true;
        mUsernameErrorText.setVisibility(View.GONE);

        if (TextUtils.isEmpty(mUsernameEdit.getText())) {
            isValid = false;
            mUsernameErrorText.setVisibility(View.VISIBLE);
        }

        return isValid;
    }

    private boolean isPasswordValid() {
        boolean isValid = true;
        mPasswordErrorText.setVisibility(View.GONE);

        if (mPasswordEdit.getText().toString().trim().length() <
                getResources().getInteger(R.integer.min_password_length) ) {
            mPasswordErrorText.setVisibility(View.VISIBLE);
            isValid = false;
        }

        return isValid;
    }

    private boolean isValid() {
        mUsernameErrorText.setVisibility(View.GONE);
        mPasswordErrorText.setVisibility(View.GONE);

        return (isUserNameValid() && isPasswordValid());
    }
}
