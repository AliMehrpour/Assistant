package com.volcano.assistant.fragment;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.volcano.assistant.Intents;
import com.volcano.assistant.R;

/**
 * Created by Sherry on 1/30/2015 to add settings to application
 */
public class SettingFragment extends PreferenceFragment {

    CheckBoxPreference mPasscodePref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        mPasscodePref = (CheckBoxPreference) findPreference(getString(R.string.preference_passcode_key));
        mPasscodePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final boolean value = (boolean) newValue;
                if (value) {
                    startActivity(Intents.getPasscodeIntent(PasscodeFragment.MODE_PASSCODE_ENABLE));
                }
                else {
                    startActivity(Intents.getPasscodeIntent(PasscodeFragment.MODE_PASSCODE_DISABLE));
                }
                return true;
            }
        });

    }
}