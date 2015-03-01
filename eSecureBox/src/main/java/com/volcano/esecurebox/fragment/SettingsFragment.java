// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.volcano.esecurebox.Intents;
import com.volcano.esecurebox.Managers;
import com.volcano.esecurebox.R;
import com.volcano.esecurebox.activity.SignupActivity;
import com.volcano.esecurebox.fragment.PasscodeFragment.Mode;
import com.volcano.esecurebox.model.User;
import com.volcano.esecurebox.util.SoftKeyboardUtils;
import com.volcano.esecurebox.util.Utils;

import java.util.Collections;

/**
 * Settings fragment
 */
@SuppressWarnings("FieldCanBeLocal")
public class SettingsFragment extends PreferenceFragment {

    private Preference mEditProfile;
    private Preference mChangePassword;
    private Preference mPasscodeEnablePref;
    private Preference mPasscodeChangePref;
    private Preference mAboutPref;
    private Preference mReportBugPref;
    private Preference mRatePref;
    private Preference mSharePref;
    private EditText mEditOldPassword;
    private EditText mEditNewPassword;
    private FrameLayout mProgressLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        mEditProfile = findPreference(getString(R.string.preference_general_profile_edit_key));
        mEditProfile.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(Intents.getSignupIntent(Intents.EXTRA_MODE , SignupActivity.MODE_UPDATE));
                return false;
            }
        });

        mChangePassword = findPreference(getString(R.string.preference_general_chang_password_key));
        mChangePassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final LinearLayout layout = (LinearLayout) View.inflate(getActivity(),
                        R.layout.dialog_change_password, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.label_change_password_dialog_title))
                        .setView(layout)
                        .setNegativeButton(R.string.button_cancel, null)
                        .setPositiveButton(R.string.button_submit, new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int id) {
                            }
                        });
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    boolean isValid = true;
                    @Override
                    public void onClick(View v) {
                        mProgressLayout = (FrameLayout) layout.findViewById(R.id.layout_progress);
                        mEditOldPassword = (EditText) layout.findViewById(R.id.edit_old_password);
                        mEditNewPassword = (EditText) layout.findViewById(R.id.edit_new_password);
                        final TextView textOldPassword = (TextView) layout.findViewById(R.id.text_old_password);
                        final TextView textNewPassword = (TextView) layout.findViewById(R.id.text_new_password);
                        textOldPassword.setText("");
                        textNewPassword.setText("");
                        textOldPassword.setVisibility(View.GONE);
                        textNewPassword.setVisibility(View.GONE);
                        final User user = Managers.getAccountManager().getCurrentUser();
                        if (mEditOldPassword.getText().toString().trim().length() <
                                getResources().getInteger(R.integer.min_password_length)) {
                            textOldPassword.setText(getString(R.string.error_password_minimum_character));
                            textOldPassword.setVisibility(View.VISIBLE);
                            mEditOldPassword.requestFocus();
                            isValid = false;
                        }
                        else {
                            isValid = true;
                        }
                        if (mEditNewPassword.getText().toString().trim().length() <
                                getResources().getInteger(R.integer.min_password_length)) {
                            textNewPassword.setText(getString(R.string.error_password_minimum_character));
                            textNewPassword.setVisibility(View.VISIBLE);
                            if(isValid) {
                                mEditNewPassword.requestFocus();
                                isValid = false;
                            }
                        }
                        else {
                            isValid = true;
                        }
                        if(isValid) {
                            enable(false);
                            mProgressLayout.setVisibility(View.VISIBLE);
                            Managers.getAccountManager().signin(user.getUsername(),
                                    mEditOldPassword.getText().toString(),
                                    new LogInCallback() {
                                        @Override
                                        public void done(ParseUser parseUser, ParseException e) {
                                            if (e == null) {
                                                //setResult(RESULT_OK);
                                                mProgressLayout.setVisibility(View.GONE);
                                                user.setPassword(mEditNewPassword.getText().toString().trim());
                                                user.save(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if (e == null) {
                                                            //LogUtils.LogI(TAG, "Update profile successful");
                                                            Utils.showToast(getString(R.string.toast_change_password_success));
                                                            dialog.dismiss();
                                                        } else {
                                                            Utils.showToast(getString(R.string.toast_edit_profile_unSuccess));
                                                            mProgressLayout.setVisibility(View.GONE);
                                                            enable(true);
                                                        }
                                                    }
                                                });
                                            } else {
                                                enable(true);
                                                mProgressLayout.setVisibility(View.GONE);
                                                textOldPassword.setText(getString(R.string.error_password_invalid));
                                                textOldPassword.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });
                        }
                    }
                });
                return false;
            }
        });

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

    private void enable(boolean enable){
        if (!enable) {
            SoftKeyboardUtils.hideSoftKeyboard(getActivity());
        }
        mEditOldPassword.setEnabled(enable);
        mEditNewPassword.setEnabled(enable);
    }
}