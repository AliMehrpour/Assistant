// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.volcano.assistant.Intents;
import com.volcano.assistant.Managers;
import com.volcano.assistant.R;
import com.volcano.assistant.VlApplication;
import com.volcano.assistant.fragment.PasscodeFragment.Mode;
import com.volcano.assistant.util.Utils;

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

        Preference aboutPref = findPreference(getString(R.string.preference_other_about_key));
        aboutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle(getString(R.string.label_about_dialog_title))
                .setCancelable(false)
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                LayoutInflater inflater = LayoutInflater.from(getActivity());
                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_about, null);

                TextView textVersion = (TextView) layout.findViewById(R.id.text_app_version);
                textVersion.setText(Utils.getAppVersionName());

                alertDialog.setView(layout);
                AlertDialog alert = alertDialog.create();
                alert.show();
                return false;
            }
        });

        final Preference reportBugPref = findPreference(getString(R.string.preference_other_report_bug_key));
        reportBugPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    startActivity(Intent.createChooser(Intents.getEmailIntentSubjectTo(
                                    String.format(getString(R.string.email_bug_report_subject),
                                            getString(R.string.app_name), Utils.getAppVersionName()),
                                    new String[]{getString(R.string.app_bug_email_address)}),
                            getString(R.string.email_sending)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Utils.showToast(getString(R.string.toast_report_bug_email_client_unavailable));
                }
                return false;
            }
        });

        final Preference rateAppPref = findPreference(getString(R.string.preference_other_rate_key));
        rateAppPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final String appPackageName = VlApplication.getInstance().getPackageName();
                //final String appPackageName= "tv.clippit.android";
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.app_url_store) + appPackageName)));
                }
                return false;
            }
        });

        final Preference sharing = findPreference(getString(R.string.preference_other_share_key));
        sharing.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(Intent.createChooser(Intents.getEmailIntentSubjectBody(
                        getString(R.string.email_share_subject),
                        getString(R.string.email_share_body)), getString(R.string.label_share_choose_app)));
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