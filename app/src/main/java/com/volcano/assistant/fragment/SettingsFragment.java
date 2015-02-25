// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.volcano.assistant.Intents;
import com.volcano.assistant.Managers;
import com.volcano.assistant.R;
import com.volcano.assistant.fragment.PasscodeFragment.Mode;
import com.volcano.assistant.util.Utils;

import java.util.Collections;

/**
 * Settings fragment
 */
@SuppressWarnings("FieldCanBeLocal")
public class SettingsFragment extends PreferenceFragment {

    private Preference mPasscodeEnablePref;
    private Preference mPasscodeChangePref;
    private Preference mAboutPref;
    private Preference mReportBugPref;
    private Preference mRatePref;
    private Preference mSharePref;

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

        mAboutPref = findPreference(getString(R.string.preference_other_about_key));
        mAboutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final LinearLayout layout = (LinearLayout) View.inflate(getActivity(), R.layout.dialog_about, null);
                final TextView textVersion = (TextView) layout.findViewById(R.id.text_app_version);
                textVersion.setText(Utils.getAppVersionName());

                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.label_about_dialog_title))
                        .setView(layout)
                        .setNegativeButton(R.string.button_close, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();

                return false;
            }
        });

        mReportBugPref = findPreference(getString(R.string.preference_other_report_bug_key));
        mReportBugPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Utils.launchEmailClient(getActivity(),
                        Collections.singletonList(getString(R.string.email_address_bug)),
                        String.format(getString(R.string.email_subject_bug_report), getString(R.string.app_name), Utils.getAppVersionName()));
                return false;
            }
        });

        mRatePref = findPreference(getString(R.string.preference_other_rate_key));
        mRatePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Utils.launchPlayStore();
                return false;
            }
        });

        mSharePref = findPreference(getString(R.string.preference_other_share_key));
        mSharePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Utils.launchShareClient(getActivity(),
                        String.format(getString(R.string.share_subject), getString(R.string.app_name), Utils.getAppVersionName()),
                        getString(R.string.share_body));
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        final boolean passcodeEnabled = Managers.getApplicationLockManager().getApplicationLock().isPasscodeEnable();
        mPasscodeEnablePref.setTitle(getString(passcodeEnabled ? R.string.preference_passcode_disable_title : R.string.preference_passcode_enable_title));
        mPasscodeChangePref.setEnabled(passcodeEnabled);
    }
}