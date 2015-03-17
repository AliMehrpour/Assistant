// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.volcano.esecurebox.Intents;
import com.volcano.esecurebox.Managers;
import com.volcano.esecurebox.R;
import com.volcano.esecurebox.analytics.MixpanelManager;
import com.volcano.esecurebox.util.Utils;

/**
 * Passcode fragment
 */
public final class PasscodeFragment extends AbstractFragment {

    private EditText mPinEdit1, mPinEdit2, mPinEdit3, mPinEdit4, mCurrentEdit;
    private TextView mDescriptionText;

    private String mFirstPasscode = "";
    private Mode mMode = Mode.UNLOCK;
    private boolean mOldPasscodeApproved = false;

    /**
     * The passcode mode
     */
    public enum Mode {
        ENABLE,
        DISABLE,
        CHANGE,
        UNLOCK
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_passcode, container, false);

        mDescriptionText = (TextView) view.findViewById(R.id.text_description);

        mPinEdit1 = (EditText) view.findViewById(R.id.edit_pin_1);
        mPinEdit2 = (EditText) view.findViewById(R.id.edit_pin_2);
        mPinEdit3 = (EditText) view.findViewById(R.id.edit_pin_3);
        mPinEdit4 = (EditText) view.findViewById(R.id.edit_pin_4);
        mCurrentEdit = mPinEdit1;

        final View.OnTouchListener pinTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    clearText((EditText) v);
                }
                return false;
            }
        };
        mPinEdit1.setOnTouchListener(pinTouchListener);
        mPinEdit2.setOnTouchListener(pinTouchListener);
        mPinEdit3.setOnTouchListener(pinTouchListener);
        mPinEdit4.setOnTouchListener(pinTouchListener);

        final Button button0, button1, button2, button3, button4, button5,
                button6, button7, button8, button9, clearButton;
        button0 = (Button) view.findViewById(R.id.button_0);
        button1 = (Button) view.findViewById(R.id.button_1);
        button2 = (Button) view.findViewById(R.id.button_2);
        button3 = (Button) view.findViewById(R.id.button_3);
        button4 = (Button) view.findViewById(R.id.button_4);
        button5 = (Button) view.findViewById(R.id.button_5);
        button6 = (Button) view.findViewById(R.id.button_6);
        button7 = (Button) view.findViewById(R.id.button_7);
        button8 = (Button) view.findViewById(R.id.button_8);
        button9 = (Button) view.findViewById(R.id.button_9);

        final View.OnClickListener numberClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentText(v.getTag().toString());
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            }
        };
        button0.setOnClickListener(numberClickListener);
        button1.setOnClickListener(numberClickListener);
        button2.setOnClickListener(numberClickListener);
        button3.setOnClickListener(numberClickListener);
        button4.setOnClickListener(numberClickListener);
        button5.setOnClickListener(numberClickListener);
        button6.setOnClickListener(numberClickListener);
        button7.setOnClickListener(numberClickListener);
        button8.setOnClickListener(numberClickListener);
        button9.setOnClickListener(numberClickListener);

        clearButton = (Button) view.findViewById(R.id.button_clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCurrentEditText();
                v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            }
        });

        if (savedInstanceState != null) {
            mOldPasscodeApproved = savedInstanceState.getBoolean(Intents.KEY_APPROVED);
            mFirstPasscode = savedInstanceState.getString(Intents.KEY_PASSCODE);
            final String indexEditText = savedInstanceState.getString(Intents.KEY_POSITION);
            if (indexEditText.equals("1")) {
                mCurrentEdit = mPinEdit1;
            }
            else if (indexEditText.equals("2")) {
                mCurrentEdit = mPinEdit2;
            }
            else if (indexEditText.equals("3")) {
                mCurrentEdit = mPinEdit3;
            }
            else {
                mCurrentEdit = mPinEdit4;
            }
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(Intents.KEY_APPROVED, mOldPasscodeApproved);
        outState.putString(Intents.KEY_PASSCODE, mFirstPasscode);
        outState.putString(Intents.KEY_POSITION, mCurrentEdit.getTag().toString());
    }

    /**
     * Set mode
     * @param mode The {@link Mode}
     */
    public void setPasscodeMode(Mode mode) {
        mMode = mode;
        final boolean enteredFirstPasscode = !TextUtils.isEmpty(mFirstPasscode);
        if (mMode == Mode.CHANGE) {
            // Handle restore state
            mMode = mOldPasscodeApproved ? Mode.ENABLE : Mode.CHANGE;
            mDescriptionText.setText(getString(mOldPasscodeApproved ?
                    enteredFirstPasscode ? R.string.label_re_enter_new_passcode : R.string.label_enter_new_passcode :
                    R.string.label_enter_old_passcode));
        }
        else if (mMode == Mode.ENABLE) {
            mDescriptionText.setText(enteredFirstPasscode ? R.string.label_re_enter_passcode : R.string.label_enter_passcode);
        }
        else {
            mDescriptionText.setText(getString(R.string.label_enter_passcode));
        }
    }

    /**
     * Set selected EditText to String text and select next EditText. in 4th EditText, passcode is enabled, unlocked, changed or disabled.
     * @param text is string must be set to selected to EditText
     */
    private void setCurrentText(String text) {
        mCurrentEdit.setText(text);
        final String indexEditText = mCurrentEdit.getTag().toString();
        switch (indexEditText) {
            case "1":
                mCurrentEdit = mPinEdit2;
                break;

            case "2":
                mCurrentEdit = mPinEdit3;
                break;

            case "3" :
                mCurrentEdit = mPinEdit4;
                break;

            case "4":
                final String pin1 = mPinEdit1.getText().toString();
                final String pin2 = mPinEdit2.getText().toString();
                final String pin3 = mPinEdit3.getText().toString();
                final String pin4 = mPinEdit4.getText().toString();

                if (!TextUtils.isEmpty(pin1) && !TextUtils.isEmpty(pin2) && !TextUtils.isEmpty(pin3) && !TextUtils.isEmpty(pin4)) {
                    final String pin = pin1 + pin2 + pin3 + pin4;

                    if (mMode == Mode.ENABLE) {
                        if (TextUtils.isEmpty(mFirstPasscode)) {
                            mDescriptionText.setText(getString(mOldPasscodeApproved ? R.string.label_re_enter_new_passcode : R.string.label_re_enter_passcode));
                            mFirstPasscode = pin;
                            clearPins();
                        }
                        else if (mFirstPasscode.equals(pin)) {
                            Managers.getMixpanelManager().track(mOldPasscodeApproved ? MixpanelManager.EVENT_CHANGE_PASSCODE : MixpanelManager.EVENT_ENABLE_PASSCODE);

                            Managers.getApplicationLockManager().getApplicationLock().setPasscode(pin);
                            Utils.showToast(getString(mOldPasscodeApproved ? R.string.toast_passcode_changed : R.string.toast_passcode_enabled));
                            getActivity().finish();
                        }
                        else {
                            Utils.showToast(getString(R.string.toast_passcode_not_matched));
                            mDescriptionText.setText(getString(mOldPasscodeApproved ? R.string.label_enter_new_passcode : R.string.label_enter_passcode));
                            mFirstPasscode = "";
                            clearPins();
                        }
                    }
                    else if (mMode == Mode.DISABLE) {
                        if (Managers.getApplicationLockManager().getApplicationLock().verifyPasscode(pin)) {
                            Managers.getMixpanelManager().track(MixpanelManager.EVENT_DISABLE_PASSCODE);

                            Managers.getApplicationLockManager().getApplicationLock().setPasscode(null);
                            Utils.showToast(getString(R.string.toast_passcode_disabled));
                            getActivity().finish();
                        }
                        else {
                            Utils.showToast(getString(R.string.toast_passcode_invalid));
                            clearPins();
                        }
                    }
                    else if (mMode == Mode.CHANGE) {
                        if (!mOldPasscodeApproved) {
                            if (Managers.getApplicationLockManager().getApplicationLock().verifyPasscode(pin)) {
                                mOldPasscodeApproved = true;
                                mDescriptionText.setText(getString(R.string.label_enter_new_passcode));
                                mMode = Mode.ENABLE;
                                clearPins();
                            }
                            else {
                                Utils.showToast(getString(R.string.toast_passcode_invalid));
                                clearPins();
                            }
                        }
                    }
                    else if (mMode == Mode.UNLOCK) {
                        if (Managers.getApplicationLockManager().getApplicationLock().verifyPasscode(pin)) {
                            getActivity().finish();
                        }
                        else {
                            Utils.showToast(getString(R.string.toast_passcode_invalid));
                            clearPins();
                        }
                    }
                }
                break;

            default:
                mCurrentEdit = mPinEdit1;
                break;
        }

        mCurrentEdit.requestFocus();
    }

    /**
     * Clear selected EditText and focus next EditText
     */
    private void clearCurrentEditText() {
        if (mCurrentEdit.getText().toString().equals("")) {
            final String tag = mCurrentEdit.getTag().toString();
            switch (tag) {
                case "2":
                    mCurrentEdit = mPinEdit1;
                    break;
                case "3":
                    mCurrentEdit = mPinEdit2;
                    break;
                case "4":
                    mCurrentEdit = mPinEdit3;
                    break;
                case "1":
                    mCurrentEdit = mPinEdit1;
                    break;
                default:
                    break;
            }
            mCurrentEdit.requestFocus();
        }
        mCurrentEdit.setText("");
    }

    /**
     * Clear focused EditText's content
     * @param editText The current EditText
     */
    private void clearText(EditText editText) {
        editText.setText("");
        editText.setKeyListener(null);
    }

    /**
     * Clear all pins
     */
    private void clearPins() {
        mPinEdit1.setText("");
        mPinEdit2.setText("");
        mPinEdit3.setText("");
        mPinEdit4.setText("");
        mCurrentEdit = mPinEdit1;
    }

}
