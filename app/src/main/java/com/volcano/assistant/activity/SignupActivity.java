package com.volcano.assistant.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.volcano.assistant.Managers;
import com.volcano.assistant.R;
import com.volcano.assistant.backend.ParseManager;
import com.volcano.assistant.util.LogUtils;

/**
 * Created by alimehrpour on 1/5/15.
 */
public class SignupActivity extends AbstractActivity {

    private TextView mSignupButton;
    private EditText mUsernameEdit;
    private EditText mNameEdit;
    private EditText mPasswordEdit;
    private EditText mEmailEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mSignupButton = (TextView) findViewById(R.id.button_signup);
        mUsernameEdit = (EditText) findViewById(R.id.edit_username);
        mNameEdit = (EditText) findViewById(R.id.edit_name);
        mPasswordEdit = (EditText) findViewById(R.id.edit_password);
        mEmailEdit = (EditText) findViewById(R.id.edit_email);

        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Signup();
            }
        });
    }

    private void Signup() {
        final String username = mUsernameEdit.getText().toString();
        final String name = mNameEdit.getText().toString();
        final String password = mPasswordEdit.getText().toString();
        final String email = mEmailEdit.getText().toString();

        Managers.getAccountManager().signup(username, name, password, email, new ParseManager.Listener() {
            @Override
            public void onResponse() {
                LogUtils.LogI(TAG, "Signup successful");
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onErrorResponse(ParseException e) {
                LogUtils.LogE(TAG, "Signup failed, e = " + e.toString());
                showToast(e.getMessage());
            }
        });
    }

}
