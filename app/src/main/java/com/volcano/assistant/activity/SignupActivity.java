package com.volcano.assistant.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseException;
import com.volcano.assistant.Managers;
import com.volcano.assistant.R;
import com.volcano.assistant.backend.ParseManager;
import com.volcano.assistant.util.LogUtils;
import com.volcano.assistant.util.Utils;

/**
 * Sign up activity by ALI
 * validation is done by SHERRY
 */
public class SignupActivity extends AbstractActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private EditText mEmailEdit;
    private TextView mEmailErrorText;
    private EditText mNameEdit;
    private TextView mNameErrorText;
    private EditText mPasswordEdit;
    private TextView mPasswordErrorText;
    private LinearLayout mProgressLayout;
    private TextView mSignupButton;
    private EditText mUsernameEdit;
    private TextView mUsernameErrorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mSignupButton = (TextView) findViewById(R.id.button_signup);
        mUsernameEdit = (EditText) findViewById(R.id.edit_username);
        mUsernameErrorText = (TextView) findViewById(R.id.text_username);
        mNameEdit = (EditText) findViewById(R.id.edit_name);
        mNameErrorText = (TextView) findViewById(R.id.text_name);
        mPasswordEdit = (EditText) findViewById(R.id.edit_password);
        mPasswordErrorText = (TextView) findViewById(R.id.text_password);
        mEmailEdit = (EditText) findViewById(R.id.edit_email);
        mEmailErrorText = (TextView) findViewById(R.id.text_email);
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
                    isUserNameValid();
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
                if (!hasFocus && mPasswordEdit.getText().toString().length() != 0) {
                    isPasswordValid();
                }
            }
        });

        mEmailEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String email = mEmailEdit.getText().toString().trim();
                if (!hasFocus && email.length() != 0) {
                    isEmailValid();
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
                    if (e.getCode() == ParseException.USERNAME_TAKEN) {
                        mUsernameErrorText.setText(e.getMessage());
                        mUsernameErrorText.setVisibility(View.VISIBLE);
                    }
                    else if (e.getCode() == ParseException.EMAIL_TAKEN) {
                        mEmailErrorText.setText(e.getMessage());
                        mEmailErrorText.setVisibility(View.VISIBLE);
                    }
                    else if (e.getCode() == ParseException.INVALID_EMAIL_ADDRESS) {
                        mEmailErrorText.setText(e.getMessage());
                        mEmailErrorText.setVisibility(View.VISIBLE);
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
        mUsernameErrorText.setVisibility(View.GONE);
        mNameErrorText.setVisibility(View.GONE);
        mPasswordErrorText.setVisibility(View.GONE);
        mEmailErrorText.setVisibility(View.GONE);
        return (isUserNameValid() && isNameValid() && isPasswordValid() && isEmailValid());
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

    private boolean isUserNameValid() {
        boolean isValid = true;
        if (TextUtils.isEmpty(mUsernameEdit.getText())) {
            isValid = false;
            mUsernameErrorText.setVisibility(View.VISIBLE);
            mUsernameEdit.requestFocus();
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
            mPasswordEdit.requestFocus();
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
            isValid = false;
            mNameErrorText.setVisibility(View.VISIBLE);
            mNameEdit.requestFocus();
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
            isValid = false;
            mEmailErrorText.setText(getResources().getString(R.string.error_sign_up_email));
            mEmailErrorText.setVisibility(View.VISIBLE);
            mEmailEdit.requestFocus();
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailErrorText.setText(getResources().getString(R.string.error_sign_up_email_character));
            isValid = false;
            mEmailErrorText.setVisibility(View.VISIBLE);
            mEmailEdit.requestFocus();
        }
        else {
            mEmailErrorText.setVisibility(View.GONE);
        }
        return isValid;
    }
}
