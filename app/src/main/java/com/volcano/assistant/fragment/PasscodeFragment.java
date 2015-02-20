package com.volcano.assistant.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.volcano.assistant.Managers;
import com.volcano.assistant.R;
import com.volcano.assistant.VlApplication;
import com.volcano.assistant.util.PrefUtils;
import com.volcano.assistant.util.Utils;

/**
 * Passcode fragment. used for unlock application or enable, disable or
 * change passcode
 */
public final class PasscodeFragment extends AbstractFragment {

    public static final int MODE_PASSCODE_CHANGE    = 1;
    public static final int MODE_PASSCODE_DISABLE   = 2;
    public static final int MODE_PASSCODE_ENABLE    = 3;
    public static final int MODE_PASSCODE_UNLOCK    = 4;

    private int mMode = MODE_PASSCODE_UNLOCK;
    private boolean mPassApproved = false;

    EditText mPin_1, mPin_2, mPin_3, mPin_4, mEditText_selected;
    Button mButton_0, mButton_1, mButton_2, mButton_3, mButton_4,
            mButton_5, mButton_6, mButton_7, mButton_8, mButton_9, mButton_delete;
    TextView mDescription;
    private String mFirstPasscodeEntry = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passcode, container, false);

        mPin_1 = (EditText) view.findViewById(R.id.edit_pin_1);
        mPin_2 = (EditText) view.findViewById(R.id.edit_pin_2);
        mPin_3 = (EditText) view.findViewById(R.id.edit_pin_3);
        mPin_4 = (EditText) view.findViewById(R.id.edit_pin_4);

        mEditText_selected = mPin_1;

        mPin_1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    delete_pin((EditText) v);
                }
                return false;
            }
        });

        mPin_2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    delete_pin((EditText) v);
                }
                return false;
            }
        });

        mPin_3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    delete_pin((EditText) v);
                }
                return false;
            }
        });

        mPin_4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    delete_pin((EditText) v);
                }
                return false;
            }
        });

        mButton_0= (Button) view.findViewById(R.id.button_0);
        mButton_1= (Button) view.findViewById(R.id.button_1);
        mButton_2= (Button) view.findViewById(R.id.button_2);
        mButton_3= (Button) view.findViewById(R.id.button_3);
        mButton_4= (Button) view.findViewById(R.id.button_4);
        mButton_5= (Button) view.findViewById(R.id.button_5);
        mButton_6= (Button) view.findViewById(R.id.button_6);
        mButton_7= (Button) view.findViewById(R.id.button_7);
        mButton_8= (Button) view.findViewById(R.id.button_8);
        mButton_9= (Button) view.findViewById(R.id.button_9);
        mButton_delete= (Button) view.findViewById(R.id.button_delete);

        mButton_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditText_selected(v.getTag().toString());
            }
        });

        mButton_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditText_selected(v.getTag().toString());
            }
        });

        mButton_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditText_selected(v.getTag().toString());
            }
        });

        mButton_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditText_selected(v.getTag().toString());
            }
        });

        mButton_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditText_selected(v.getTag().toString());
            }
        });

        mButton_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditText_selected(v.getTag().toString());
            }
        });

        mButton_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditText_selected(v.getTag().toString());
            }
        });

        mButton_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditText_selected(v.getTag().toString());
            }
        });

        mButton_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditText_selected(v.getTag().toString());
            }
        });

        mButton_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditText_selected(v.getTag().toString());
            }
        });

        mButton_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEditText();
            }
        });

        mDescription = (TextView) view.findViewById(R.id.text_description);

        return view;
    }

    /**
     * Set use case mode
     * @param mode One of {@link PasscodeFragment#MODE_PASSCODE_ENABLE},
     *                    {@link PasscodeFragment#MODE_PASSCODE_UNLOCK},
     *                    {@link PasscodeFragment#MODE_PASSCODE_CHANGE} or
     *                    {@link PasscodeFragment#MODE_PASSCODE_DISABLE} values
     */
    public void setPasscodeMode(int mode){
        mMode = mode;
    }

    /**
     * Set selected EditText to String text and select next EditText. in 4th EditText, passcode is enabled, unlocked, changed or disabled.
     * @param text is string must be set to selected to EditText
     */
    private void setEditText_selected(String text) {
        mEditText_selected.setText(text);
        String tag = mEditText_selected.getTag().toString();
        switch (tag){
            case "1": mEditText_selected = mPin_2;
                break;
            case "2": mEditText_selected = mPin_3;
                break;
            case "3" : mEditText_selected = mPin_4;
                break;
            case "4":
                // enter to app in case passcode to unlock. Or make passcode on. Or make passcode disable! Or change passcode.
                String pin1 = mPin_1.getText().toString();
                String pin2 = mPin_2.getText().toString();
                String pin3 = mPin_3.getText().toString();
                String pin4 = mPin_4.getText().toString();
                if (!pin1.equals("")&& !pin2.equals("") && !pin3.equals("") && !pin4.equals("")) {
                    if (mMode == MODE_PASSCODE_ENABLE) {
                        if (mFirstPasscodeEntry.equals("")) {
                            mDescription.setText(getString(R.string.label_reEnter_passcode));
                            mFirstPasscodeEntry = pin1 + pin2 + pin3 + pin4;
                            clearPinsText();
                        }
                        else {
                            String secondPasscodeEntry;
                            secondPasscodeEntry = pin1 + pin2 + pin3 + pin4;
                            if (mFirstPasscodeEntry.equals(secondPasscodeEntry)) {
                                getActivity().finish();
                                Managers.getAccountManager().enablePasscode(secondPasscodeEntry);
                                Utils.showToast(getString(R.string.toast_passcode_enabled));
                            }
                        }
                    }
                    else if (mMode == MODE_PASSCODE_UNLOCK) {
                        final String passcode = pin1 + pin2 + pin3 + pin4;
                        if (Managers.getAccountManager().isPasscodeValid(passcode)) {
                            getActivity().finish();
                        }
                        else {
                            clearPinsText();
                            Utils.showToast(getString(R.string.toast_passcode_invalid));
                       }
                    }
                    else if (mMode == MODE_PASSCODE_DISABLE) {
                        String enteredPasscode = pin1 + pin2 + pin3 + pin4;
                        final Context context = VlApplication.getInstance();
                        String passcode = PrefUtils.getPref(context.getString(R.string.preference_passcode), "");
                        if (enteredPasscode.equals(passcode)) {
                            PrefUtils.remove(getString(R.string.preference_passcode));
                            Utils.showToast(getString(R.string.toast_passcode_disabled));
                            getActivity().finish();
                        }
                        else {
                            getActivity().finish();
                            Utils.showToast(getString(R.string.toast_passcode_invalid));
                        }
                    }
                    else if (mMode == MODE_PASSCODE_CHANGE) {
                        if (!mPassApproved) {
                            String enteredPasscode = pin1 + pin2 + pin3 + pin4;
                            final Context context = VlApplication.getInstance();
                            String passcode = PrefUtils.getPref(context.getString(R.string.preference_passcode), "");
                            if (enteredPasscode.equals(passcode)) {
                                mPassApproved = true;
                                clearPinsText();
                                mDescription.setText(getString(R.string.label_Enter_newPasscode));
                            }
                            else {
                                getActivity().finish();
                                Utils.showToast(getString(R.string.toast_passcode_invalid));
                            }
                        }
                        else {
                            if (mFirstPasscodeEntry.equals("")) {
                                mFirstPasscodeEntry = pin1 + pin2 + pin3 + pin4;
                                clearPinsText();
                                mDescription.setText(getString(R.string.label_reEnter_newPasscode));
                            }
                            else {
                                String secondPasscodeEntry;
                                secondPasscodeEntry = pin1 + pin2 + pin3 + pin4;
                                if (mFirstPasscodeEntry.equals(secondPasscodeEntry)) {
                                    getActivity().finish();
                                    Managers.getAccountManager().enablePasscode(secondPasscodeEntry);
                                    Utils.showToast(getString(R.string.toast_passcode_changed));
                                }
                                else {
                                    getActivity().finish();
                                    Utils.showToast(getString(R.string.toast_passcode_matched));
                                }
                            }
                        }
                    }
                }
                break;
            default: mEditText_selected = mPin_1;
                break;
        }
        mEditText_selected.requestFocus();
    }

    /**
     * Delete Selected EditText and then Select next EditText
     */
    private void deleteEditText() {
        if(mEditText_selected.getText().toString().equals("")){
            String tag = mEditText_selected.getTag().toString();
            switch (tag) {
                case "2":
                    mEditText_selected = mPin_1;
                    break;
                case "3":
                    mEditText_selected = mPin_2;
                    break;
                case "4":
                    mEditText_selected = mPin_3;
                    break;
                case "1":
                    mEditText_selected = mPin_1;
                    break;
                default:
                    break;
            }
            mEditText_selected.requestFocus();
        }
        mEditText_selected.setText("");
    }

    /**
     * Delete data from passcode EditText and then set the listener of EditText to avoid android keyboard to open.
     * @param editText is EditText in layout for passcode
     */
    private void delete_pin(EditText editText) {
        editText.setText("");
        editText.setKeyListener(null);
    }

    /**
     * Initial pin EditTexts to data entry
     */
    private void clearPinsText() {
        mPin_1.setText("");
        mPin_2.setText("");
        mPin_3.setText("");
        mPin_4.setText("");
        mEditText_selected = mPin_1;
    }

}
