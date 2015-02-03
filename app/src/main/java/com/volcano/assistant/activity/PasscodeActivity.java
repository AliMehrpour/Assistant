package com.volcano.assistant.activity;

import android.content.Intent;
import android.os.Bundle;

import com.volcano.assistant.ApplicationLock;
import com.volcano.assistant.Intents;
import com.volcano.assistant.R;
import com.volcano.assistant.fragment.PasscodeFragment;

/**
 * Created by Sherry on 1/30/2015 to support application passcode Setting
 */
public class PasscodeActivity extends AbstractActivity {

    private int mPasscodeMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);
        PasscodeFragment passcodeFragment = (PasscodeFragment) getFragmentManager().findFragmentById(R.id.fragment_passcode);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            mPasscodeMode = intent.getIntExtra(Intents.EXTRA_PASSCODE_USECASE, 0);
        }
        else {
            mPasscodeMode = savedInstanceState.getInt(Intents.KEY_PASSCODE_MODE);
        }

        passcodeFragment.setPasscodeMode(mPasscodeMode);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(Intents.KEY_PASSCODE_MODE, mPasscodeMode);
    }

    @Override
    public void onBackPressed() {
        if (mPasscodeMode == PasscodeFragment.MODE_PASSCODE_UNLOCK) {
            ApplicationLock.getInstance().forcePasscodeLock();
            final Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            finish();
        }
        else {
            finish();
        }
    }
}
