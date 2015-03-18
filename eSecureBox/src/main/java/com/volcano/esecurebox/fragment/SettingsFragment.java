// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.volcano.esecurebox.Intents;
import com.volcano.esecurebox.Managers;
import com.volcano.esecurebox.R;
import com.volcano.esecurebox.activity.SignupActivity;
import com.volcano.esecurebox.analytics.MixpanelManager;
import com.volcano.esecurebox.fragment.PasscodeFragment.Mode;
import com.volcano.esecurebox.util.Utils;

import java.util.Collections;

/**
 * Settings fragment
 */
@SuppressWarnings("FieldCanBeLocal")
public class SettingsFragment extends PreferenceFragment {

    private Preference mEditProfilePref;
    //private Preference mChangePasswordPref;
    private Preference mPasscodeEnablePref;
    private Preference mPasscodeChangePref;
    private Preference mAboutPref;
    private Preference mFeedbackPref;
    private Preference mRatePref;
    private Preference mSharePref;

    //private AlertDialog mChangePasswordDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        mEditProfilePref = findPreference(getString(R.string.preference_general_edit_profile_key));
        mEditProfilePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivity(Intents.getSignupIntent(SignupActivity.MODE_UPDATE));
                return false;
            }
        });

        /*
        mChangePasswordPref = findPreference(getString(R.string.preference_general_change_password_key));
        mChangePasswordPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final LinearLayout layout = (LinearLayout) View.inflate(getActivity(), R.layout.dialog_change_password, null);
                final FrameLayout progressLayout = (FrameLayout) layout.findViewById(R.id.layout_progress);
                final RobotoEditText oldPasswordEdit = (RobotoEditText) layout.findViewById(R.id.edit_old_password);
                final RobotoEditText newPasswordEdit = (RobotoEditText) layout.findViewById(R.id.edit_new_password);
                final TextView oldPasswordErrorText = (RobotoTextView) layout.findViewById(R.id.text_old_password);
                final TextView newPasswordErrorText = (RobotoTextView) layout.findViewById(R.id.text_new_password);
                final int minPasswordLength = getResources().getInteger(R.integer.min_password_length);

                mChangePasswordDialog = new AlertDialogWrapper.Builder(getActivity())
                        .setTitle(getString(R.string.label_change_password_dialog_title))
                        .setView(layout)
                        .autoDismiss(false)
                        .setNegativeButton(R.string.button_cancel_uppercase, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mChangePasswordDialog.dismiss();
                            }
                        })
                        .setPositiveButton(R.string.button_submit_uppercase, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                oldPasswordErrorText.setVisibility(View.GONE);
                                newPasswordErrorText.setVisibility(View.GONE);

                                boolean isValid = true;
                                if (oldPasswordEdit.getText().toString().trim().length() < minPasswordLength) {
                                    oldPasswordErrorText.setText(getString(R.string.error_password_minimum_character));
                                    oldPasswordErrorText.setVisibility(View.VISIBLE);
                                    oldPasswordEdit.requestFocus();
                                    isValid = false;
                                }

                                if (newPasswordEdit.getText().toString().trim().length() < minPasswordLength) {
                                    newPasswordErrorText.setText(getString(R.string.error_password_minimum_character));
                                    newPasswordErrorText.setVisibility(View.VISIBLE);
                                    if (isValid) {
                                        newPasswordEdit.requestFocus();
                                    }
                                    isValid = false;
                                }

                                if (isValid) {
                                    enable(false);
                                    progressLayout.setVisibility(View.VISIBLE);

                                    Managers.getAccountManager().changePassword(oldPasswordEdit.getText().toString().trim(),
                                            newPasswordEdit.getText().toString().trim(), new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        progressLayout.setVisibility(View.GONE);

                                                        Utils.showToast(getString(R.string.toast_change_password_successful));
                                                        mChangePasswordDialog.dismiss();
                                                    }
                                                    else if (e.getCode() == ParseException.PASSWORD_MISSING) {
                                                        // Old password isn't correct
                                                        enable(true);
                                                        progressLayout.setVisibility(View.GONE);

                                                        oldPasswordErrorText.setText(getString(R.string.error_password_invalid));
                                                        oldPasswordErrorText.setVisibility(View.VISIBLE);
                                                    }
                                                    else {
                                                        progressLayout.setVisibility(View.GONE);
                                                        enable(true);
                                                        Utils.showToast(getString(R.string.toast_change_password_failed));
                                                    }
                                                }
                                            });
                                }
                            }

                            private void enable(boolean enable) {
                                if (!enable) {
                                    SoftKeyboardUtils.hideSoftKeyboard(getActivity());
                                }

                                oldPasswordEdit.setEnabled(enable);
                                newPasswordEdit.setEnabled(enable);
                            }
                        })
                        .create();
                mChangePasswordDialog.show();

                return false;
            }
        });
        */

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

                new AlertDialogWrapper.Builder(getActivity())
                        .setTitle(getString(R.string.label_about_dialog_title))
                        .setView(layout)
                        .setNegativeButton(R.string.button_close_uppercase, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                        .show();

                return false;
            }
        });

        mFeedbackPref = findPreference(getString(R.string.preference_other_feedback_key));
        mFeedbackPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Utils.launchEmailClient(getActivity(),
                        Collections.singletonList(getString(R.string.email_address_feedback)),
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
                Managers.getMixpanelManager().track(MixpanelManager.EVENT_SHARE_APP);

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