// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.volcano.esecurebox.Intents;
import com.volcano.esecurebox.Managers;
import com.volcano.esecurebox.R;
import com.volcano.esecurebox.util.PrefUtils;
import com.volcano.esecurebox.util.SoftKeyboardUtils;
import com.volcano.esecurebox.util.Utils;

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
    private FrameLayout mProgressLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mUsernameEdit = (EditText) findViewById(R.id.edit_username);
        mUsernameErrorText = (TextView) findViewById(R.id.text_username);
        mPasswordEdit = (EditText) findViewById(R.id.edit_password);
        mPasswordErrorText = (TextView) findViewById(R.id.text_password);
        mSigninText = (TextView) findViewById(R.id.text_signin);
        mSignupText = (TextView) findViewById(R.id.text_signup_email);
        mProgressLayout = (FrameLayout) findViewById(R.id.layout_progress);

        mSigninText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    enable(false);
                    mProgressLayout.setVisibility(View.VISIBLE);
                    Managers.getAccountManager().signin(mUsernameEdit.getText().toString(),
                            mPasswordEdit.getText().toString(),
                            new LogInCallback() {
                                @Override
                                public void done(ParseUser parseUser, ParseException e) {
                                    if (e == null) {
                                        setResult(RESULT_OK);
                                        mProgressLayout.setVisibility(View.GONE);
                                        finish();
                                    }
                                    else {
                                        enable(true);
                                        mProgressLayout.setVisibility(View.GONE);
                                        Utils.showToast(getResources().getString(R.string.toast_invalid_username_or_password));
                                    }
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
                startActivityForResult(Intents.getSignupIntent(SignupActivity.MODE_CREATE), Intents.REQUEST_CODE_SIGNUP);
            }
        });

        mUsernameEdit.setText(PrefUtils.getPref(PrefUtils.PREF_LAST_USERNAME, null));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Intents.REQUEST_CODE_SIGNUP) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    private void enable(boolean enable){
        if (!enable) {
            SoftKeyboardUtils.hideSoftKeyboard(this);
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
