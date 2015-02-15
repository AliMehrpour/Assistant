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
 * Signup activity
 */
public class SignupActivity extends AbstractActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private TextView mSignupButton;
    private EditText mUsernameEdit;
    private EditText mNameEdit;
    private EditText mPasswordEdit;
    private EditText mEmailEdit;
    private LinearLayout mProgressLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mSignupButton = (TextView) findViewById(R.id.button_signup);
        mUsernameEdit = (EditText) findViewById(R.id.edit_username);
        mNameEdit = (EditText) findViewById(R.id.edit_name);
        mPasswordEdit = (EditText) findViewById(R.id.edit_password);
        mEmailEdit = (EditText) findViewById(R.id.edit_email);
        mProgressLayout = (LinearLayout) findViewById(R.id.layout_progress);

        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Signup();
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
                    LogUtils.LogI(TAG, "Signup successful");
                    InitializeData();
                }

                @Override
                public void onErrorResponse(ParseException e) {
                    enable(true);
                    LogUtils.LogE(TAG, "Signup failed", e);
                    Utils.showToast(R.string.toast_signup_failed);
                }
            });
        }
    }

    private boolean valid() {
        // TODO: do proper screen validations, show proper message and return result
        return !TextUtils.isEmpty(mUsernameEdit.getText()) && !TextUtils.isEmpty(mNameEdit.getText()) &&
                !TextUtils.isEmpty(mPasswordEdit.getText()) && !TextUtils.isEmpty(mEmailEdit.getText());
    }

    private void enable(boolean enable) {
        //TODO: enable/disable whole form
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
