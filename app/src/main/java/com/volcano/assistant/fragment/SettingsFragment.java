// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.volcano.assistant.Intents;
import com.volcano.assistant.Managers;
import com.volcano.assistant.R;
import com.volcano.assistant.fragment.PasscodeFragment.Mode;

/**
 * Settings fragment
 */
public class SettingsFragment extends PreferenceFragment {

    @SuppressWarnings("FieldCanBeLocal")
    private Preference mPasscodeEnablePref;
    private Preference mPasscodeChangePref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        mPasscodeEnablePref = findPreference(getString(R.string.preference_passcode_enable_key));
        mPasscodeEnablePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(Intents.getPasscodeIntent(
                        Managers.getApplicationLockManager().getApplicationLock().isPasscodeEnable() ?
                        Mode.DISABLE : Mode.ENABLE));
                return false;
            }
        });

        mPasscodeChangePref = findPreference(getString(R.string.preference_passcode_change_key));
        mPasscodeChangePref.setIntent(Intents.getPasscodeIntent(PasscodeFragment.Mode.CHANGE));
    }

    @Override
    public void onResume() {
        super.onResume();

        final boolean passcodeEnabled = Managers.getApplicationLockManager().getApplicationLock().isPasscodeEnable();
        mPasscodeEnablePref.setTitle(getString(passcodeEnabled ? R.string.preference_passcode_disable_title : R.string.preference_passcode_enable_title));
        mPasscodeChangePref.setEnabled(passcodeEnabled);
    }
}