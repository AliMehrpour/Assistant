package com.volcano.assistant.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseException;
import com.volcano.assistant.Managers;
import com.volcano.assistant.R;
import com.volcano.assistant.backend.AccountManager;
import com.volcano.assistant.backend.ParseManager;
import com.volcano.assistant.util.LogUtils;
import com.volcano.assistant.util.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sign up activity by ALI
 * validation is done by SHERRY
 */
public class SignupActivity extends AbstractActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private TextView mSignupButton;
    private EditText mUsernameEdit;
    private TextView mUsernameText;
    private EditText mNameEdit;
    private TextView mNameText;
    private EditText mPasswordEdit;
    private TextView mPasswordText;
    private EditText mEmailEdit;
    private TextView mEmailText;
    private LinearLayout mProgressLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mSignupButton = (TextView) findViewById(R.id.button_signup);
        mUsernameEdit = (EditText) findViewById(R.id.edit_username);
        mUsernameText = (TextView) findViewById(R.id.text_username);
        mNameEdit = (EditText) findViewById(R.id.edit_name);
        mNameText = (TextView) findViewById(R.id.text_name);
        mPasswordEdit = (EditText) findViewById(R.id.edit_password);
        mPasswordText = (TextView) findViewById(R.id.text_password);
        mEmailEdit = (EditText) findViewById(R.id.edit_email);
        mEmailText = (TextView) findViewById(R.id.text_email);
        mProgressLayout = (LinearLayout) findViewById(R.id.layout_progress);

        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Signup();
            }
        });

        mUsernameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!TextUtils.isEmpty(mUsernameEdit.getText())) {
                        mUsernameText.setVisibility(View.GONE);
                    }
                }
            }
        });

        mNameEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!TextUtils.isEmpty(mNameEdit.getText())) {
                        mNameText.setVisibility(View.GONE);
                    }
                }
            }
        });

        mPasswordEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && mPasswordEdit.getText().toString().length() !=0) {
                    if (mPasswordEdit.getText().toString().trim().length() <
                            getResources().getInteger(R.integer.min_password_length) ) {
                        mPasswordText.setText(getResources().getString(R.string.label_sign_up_sign_in_password_character));
                        mPasswordText.setVisibility(View.VISIBLE);
                    }
                    else {
                        mPasswordText.setVisibility(View.GONE);
                    }
                }
            }
        });

        mEmailEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String email = mEmailEdit.getText().toString().trim();
                if (!hasFocus && email.length() != 0) {
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        mEmailText.setText(getResources().getString(R.string.label_sign_up_email_character));
                        mEmailText.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    mEmailText.setVisibility(View.GONE);
                }
            }
        });
    }

    private void Signup() {
        if (valid()) {
            Utils.hideKeyboard(this);
            enable(false);

            final String username = mUsernameEdit.getText().toString();
            final String name = mNameEdit.getText().toString();
            final String password = mPasswordEdit.getText().toString();
            final String email = mEmailEdit.getText().toString();

            Managers.getAccountManager().signup(username, name, password, email, new ParseManager.Listener() {
                @Override
                public void onResponse() {
                    LogUtils.LogI(TAG, "Sign up successful");
                    InitializeData();
                }

                @Override
                public void onErrorResponse(ParseException e) {
                    if (e.getCode() == AccountManager.ERROR_CODE_USERNAME_EXIST) {
                        mUsernameText.setText(e.getMessage());
                        mUsernameText.setVisibility(View.VISIBLE);
                    }
                    else if (e.getCode() == AccountManager.ERROR_CODE_EMAIL_EXIST) {
                        mEmailText.setText(e.getMessage());
                        mEmailText.setVisibility(View.VISIBLE);
                    }
                    else {
                        Utils.showToast(R.string.toast_signup_failed);
                    }
                    enable(true);
                    LogUtils.LogE(TAG, "Sign up failed", e);
                }
            });
        }
    }

    private boolean valid() {
        mUsernameText.setVisibility(View.GONE);
        mNameText.setVisibility(View.GONE);
        mPasswordText.setVisibility(View.GONE);
        mEmailText.setVisibility(View.GONE);
        String email = mEmailEdit.getText().toString().trim();
        boolean validation = true;

        if (TextUtils.isEmpty(mUsernameEdit.getText())) {
            mUsernameText.setText(getResources().getString(R.string.label_sign_up_sign_in_username));
            validation = false;
            mUsernameText.setVisibility(View.VISIBLE);
        }
        else if (TextUtils.isEmpty(mNameEdit.getText())) {
            mNameText.setText(getResources().getString(R.string.label_sign_up_name));
            validation = false;
            mNameText.setVisibility(View.VISIBLE);
        }
        else if (mPasswordEdit.getText().toString().trim().length() <
                getResources().getInteger(R.integer.min_password_length) ) {
            mPasswordText.setText(getResources().getString(R.string.label_sign_up_sign_in_password_character));
            validation = false;
            mPasswordText.setVisibility(View.VISIBLE);
        }
        else if (TextUtils.isEmpty(mEmailEdit.getText())) {
            mEmailText.setText(getResources().getString(R.string.label_sign_up_email));
            validation = false;
            mEmailText.setVisibility(View.VISIBLE);
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailText.setText(getResources().getString(R.string.label_sign_up_email_character));
            validation = false;
            mEmailText.setVisibility(View.VISIBLE);
        }
        return validation;
    }

    private void enable(boolean enable) {
        mUsernameEdit.setEnabled(enable);
        mNameEdit.setEnabled(enable);
        mPasswordEdit.setEnabled(enable);
        mEmailEdit.setEnabled(enable);
        mSignupButton.setEnabled(enable);
        mProgressLayout.setVisibility(enable ? View.GONE : View.VISIBLE);
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

}
