// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.volcano.esecurebox.Intents;
import com.volcano.esecurebox.Managers;
import com.volcano.esecurebox.R;
import com.volcano.esecurebox.backend.ParseManager;
import com.volcano.esecurebox.model.User;
import com.volcano.esecurebox.util.LogUtils;
import com.volcano.esecurebox.util.SoftKeyboardUtils;
import com.volcano.esecurebox.util.Utils;

/**
 * Sign up activity
 */
public class SignupActivity extends AbstractActivity {

    public static final int MODE_CREATE = 0;
    public static final int MODE_UPDATE = 1;

    private EditText mEmailEdit;
    private TextView mEmailErrorText;
    private EditText mNameEdit;
    private TextView mNameErrorText;
    private EditText mPasswordEdit;
    private TextView mPasswordErrorText;
    private LinearLayout mProgressLayout;
    private TextView mProfileSubmit;
    private EditText mUsernameEdit;
    private TextView mUsernameErrorText;
    private int mExtraMode;
    private User mUser ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mUsernameEdit = (EditText) findViewById(R.id.edit_username);
        mUsernameErrorText = (TextView) findViewById(R.id.text_username);
        mNameEdit = (EditText) findViewById(R.id.edit_name);
        mNameErrorText = (TextView) findViewById(R.id.text_name);
        mPasswordEdit = (EditText) findViewById(R.id.edit_password);
        mPasswordErrorText = (TextView) findViewById(R.id.text_password);
        mEmailEdit = (EditText) findViewById(R.id.edit_email);
        mEmailErrorText = (TextView) findViewById(R.id.text_email);
        mProgressLayout = (LinearLayout) findViewById(R.id.layout_progress);
        mProfileSubmit = (TextView) findViewById(R.id.button_submit);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            mExtraMode = bundle.getInt(Intents.EXTRA_MODE);
            if(mExtraMode == MODE_CREATE) {
                mProfileSubmit.setText(getString(R.string.button_signup));
            }
            // Edit profile mode
            else {
                mProfileSubmit.setText(getString(R.string.button_edit_profile));
                mPasswordEdit.setVisibility(View.GONE);
                mEmailEdit.setEnabled(false);
                mUser = Managers.getAccountManager().getCurrentUser();
                mNameEdit.setText(mUser.getName());
                mUsernameEdit.setText(mUser.getUsername());
                mEmailEdit.setText(mUser.getEmail());
            }
        }

        mProfileSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mExtraMode == MODE_CREATE) {
                    Signup();
                }
                // Edit Profile mode
                else {
                    update();
                }
            }
        });

        mUsernameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    isUsernameValid();
                }
            }
        });

        mNameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    isNameValid();
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

        mEmailEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    isEmailValid();
                }
            }
        });
    }

    private void Signup() {
        if (isValid()) {
            SoftKeyboardUtils.hideSoftKeyboard(this);
            enable(false);

            final String username = mUsernameEdit.getText().toString();
            final String name = mNameEdit.getText().toString();
            final String password = mPasswordEdit.getText().toString();
            final String email = mEmailEdit.getText().toString();

            Managers.getAccountManager().signup(username, name, password, email,
                    new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                LogUtils.LogI(TAG, "Sign up successful");
                                InitializeData();
                            }
                            else {
                                if (e.getCode() == ParseException.USERNAME_TAKEN) {
                                    mUsernameErrorText.setText(e.getMessage());
                                    mUsernameErrorText.setVisibility(View.VISIBLE);
                                    mUsernameEdit.requestFocus();
                                }
                                else if (e.getCode() == ParseException.EMAIL_TAKEN) {
                                    mEmailErrorText.setText(e.getMessage());
                                    mEmailErrorText.setVisibility(View.VISIBLE);
                                    mEmailEdit.requestFocus();
                                }
                                else if (e.getCode() == ParseException.INVALID_EMAIL_ADDRESS) {
                                    mEmailErrorText.setText(e.getMessage());
                                    mEmailErrorText.setVisibility(View.VISIBLE);
                                    mEmailEdit.requestFocus();
                                }
                                else {
                                    Utils.showToast(R.string.toast_signup_failed);
                                }

                                enable(true);
                                LogUtils.LogE(TAG, "Sign up failed", e);
                            }
                        }
                    });
        }
    }

    private void enable(boolean enable) {
        mProgressLayout.setVisibility(enable ? View.GONE : View.VISIBLE);
        mUsernameEdit.setEnabled(enable);
        mNameEdit.setEnabled(enable);
        mPasswordEdit.setEnabled(enable);
        mEmailEdit.setEnabled(enable);
        mProfileSubmit.setEnabled(enable);
    }

    private boolean isValid() {
        mUsernameErrorText.setVisibility(View.GONE);
        mNameErrorText.setVisibility(View.GONE);
        mPasswordErrorText.setVisibility(View.GONE);
        mEmailErrorText.setVisibility(View.GONE);

        if (mExtraMode == MODE_CREATE) {
            return (isNameValid() && isUsernameValid() && isPasswordValid() && isEmailValid());
        }
        // Edit profile mode
        else {
            return (isNameValid() && isUsernameValid());
        }
    }

    private boolean isUsernameValid() {
        boolean isValid = true;
        if (TextUtils.isEmpty(mUsernameEdit.getText())) {
            mUsernameErrorText.setVisibility(View.VISIBLE);
            isValid = false;
        }
        else {
            mUsernameErrorText.setVisibility(View.GONE);
        }
        return isValid;
    }

    private boolean isPasswordValid() {
        boolean isValid = true;
        if (mPasswordEdit.getText().toString().trim().length() <
                getResources().getInteger(R.integer.min_password_length) ) {
            mPasswordErrorText.setVisibility(View.VISIBLE);
            isValid = false;
        }
        else {
            mPasswordErrorText.setVisibility(View.GONE);
        }
        return isValid;
    }

    private boolean isNameValid() {
        boolean isValid = true;
        if (TextUtils.isEmpty(mNameEdit.getText())) {
            mNameErrorText.setVisibility(View.VISIBLE);
            isValid = false;
        }
        else {
            mNameErrorText.setVisibility(View.GONE);
        }
        return isValid;
    }

    private boolean isEmailValid() {
        String email = mEmailEdit.getText().toString().trim();
        boolean isValid = true;
        if (TextUtils.isEmpty(mEmailEdit.getText())) {
            mEmailErrorText.setText(getResources().getString(R.string.error_email_not_empty));
            mEmailErrorText.setVisibility(View.VISIBLE);
            isValid = false;
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailErrorText.setText(getResources().getString(R.string.error_email_invalid));
            mEmailErrorText.setVisibility(View.VISIBLE);
            isValid = false;
        }
        else {
            mEmailErrorText.setVisibility(View.GONE);
        }
        return isValid;
    }

    private void InitializeData() {
        ParseManager.InitializeData(new ParseManager.OnDataInitializationListener() {
            @Override
            public void onInitilize(boolean successful) {
                if (successful) {
                    setResult(RESULT_OK);
                    finish();
                }
                else {
                    enable(true);
                    LogUtils.LogE(TAG, "Initialization failed");
                    Utils.showToast(R.string.toast_initialize_failed);
                }
            }
        });
    }

    private void update() {
        if (isValid()) {
            SoftKeyboardUtils.hideSoftKeyboard(this);
            enable(false);
            mProgressLayout.setVisibility(View.VISIBLE);

            mUser.setName(mNameEdit.getText().toString());
            mUser.setUsername(mUsernameEdit.getText().toString());
            mUser.save(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        LogUtils.LogI(TAG, "Update profile successful");
                        Utils.showToast(getString(R.string.toast_edit_profile_success));
                        finish();
                    }
                    else {
                        if (e.getCode() == ParseException.USERNAME_TAKEN) {
                            mUsernameErrorText.setText(e.getMessage());
                            mUsernameErrorText.setVisibility(View.VISIBLE);
                            mUsernameEdit.requestFocus();
                        }
                        else {
                            Utils.showToast(getString(R.string.toast_edit_profile_unSuccess));
                        }
                        mProgressLayout.setVisibility(View.GONE);
                        enable(true);
                    }
                }
            });
        }
    }
}
