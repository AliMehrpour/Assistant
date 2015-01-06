package com.volcano.assistant.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.volcano.assistant.Intents;
import com.volcano.assistant.Managers;
import com.volcano.assistant.R;
import com.volcano.assistant.backend.ParseManager;

/**
 * Created by alimehrpour on 12/31/14.
 */
public class SigninActivity extends AbstractActivity {

    private EditText mUsernameEdit;
    private EditText mPasswordEdit;
    private TextView mSigninText;
    private TextView mSignupText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mUsernameEdit = (EditText) findViewById(R.id.edit_username);
        mPasswordEdit = (EditText) findViewById(R.id.edit_password);
        mSigninText = (TextView) findViewById(R.id.text_signin);
        mSignupText = (TextView) findViewById(R.id.text_signup_email);

        mSigninText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Managers.getAccountManager().signin(mUsernameEdit.getText().toString(), mPasswordEdit.getText().toString(), new ParseManager.Listener() {
                    @Override
                    public void onResponse() {
                        setResult(RESULT_OK, new Intent());
                        finish();
                    }

                    @Override
                    public void onErrorResponse(ParseException e) {
                        showToast(e.getMessage());
                    }
                });
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
            else {
                // Nothing
            }
        }
    }


}
