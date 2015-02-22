// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.activity;

import android.content.Intent;
import android.os.Bundle;

import com.volcano.assistant.Intents;
import com.volcano.assistant.Managers;
import com.volcano.assistant.R;
import com.volcano.assistant.fragment.PasscodeFragment;
import com.volcano.assistant.fragment.PasscodeFragment.Mode;

/**
 * Used in order to set or change passcode or unlock application if passcode is enabled.<br />
 * Set the mode in the intent as Intents.EXTRA_MODE. it's value is
 * {@link com.volcano.assistant.fragment.PasscodeFragment.Mode}
 */
public class PasscodeActivity extends AbstractActivity {

    private Mode mPasscodeMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);

        mPasscodeMode = (Mode) getIntent().getSerializableExtra(Intents.EXTRA_MODE);
        final PasscodeFragment passcodeFragment = (PasscodeFragment) getFragmentManager().findFragmentById(R.id.fragment_passcode);
        passcodeFragment.setPasscodeMode(mPasscodeMode);
    }

    @Override
    public void onBackPressed() {
        if (mPasscodeMode == Mode.UNLOCK) {
            Managers.getApplicationLockManager().getApplicationLock().forcePasscodeLock();
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
