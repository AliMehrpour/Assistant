package com.volcano.esecurebox.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.SaveCallback;
import com.volcano.esecurebox.Managers;
import com.volcano.esecurebox.R;
import com.volcano.esecurebox.VlApplication;
import com.volcano.esecurebox.model.Field;
import com.volcano.esecurebox.util.Utils;
import com.volcano.esecurebox.widget.RobotoEditText;
import com.volcano.esecurebox.widget.RobotoTextView;

import java.util.List;

/**
 * Provides field list which can be selected and returned by listener
 */
public final class AddFieldDialogFragment extends android.app.DialogFragment {
    private RobotoTextView mCancelButton;
    private RobotoTextView mChooseButton;
    private RobotoTextView mCreateNewButton;
    private RobotoEditText mSearchEdit;
    private FieldListFragment mFieldListFragment;

    private OnFieldSelectedListener mFieldSelectedListener;

    public interface OnFieldSelectedListener {
        void onFieldSelected(List<Field> fields);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_dialog_add_field, container, false);

        mCancelButton = (RobotoTextView) view.findViewById(R.id.button_cancel);
        mChooseButton = (RobotoTextView) view.findViewById(R.id.button_choose);
        mCreateNewButton = (RobotoTextView) view.findViewById(R.id.button_create_new_field);
        mSearchEdit = (RobotoEditText) view.findViewById(R.id.edit_search);
        mFieldListFragment = (FieldListFragment) getFragmentManager().findFragmentById(R.id.fragment_field_list);

        if (mFieldListFragment == null && Utils.hasJellyBeanApi()) {
            mFieldListFragment = new FieldListFragment();
            getFragmentManager().beginTransaction().add(R.id.layout_field_list, mFieldListFragment).commit();
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSearchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mFieldListFragment.loadFields(s.toString());
            }
        });

        mCancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                mFieldSelectedListener.onFieldSelected(null);
                mFieldListFragment.clearSelected();
            }
        });

        mChooseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                mFieldSelectedListener.onFieldSelected(mFieldListFragment.getSelectedFields());
                mFieldListFragment.clearSelected();
            }
        });

        mCreateNewButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewFieldDialog();
            }
        });

        mSearchEdit.setText(null);
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // the content
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        try {
            getFragmentManager().beginTransaction()
                    .remove(mFieldListFragment)
                    .commitAllowingStateLoss();
        }
        catch (Exception ignored) {
        }
    }

    public void setOnFieldSelectedListener(OnFieldSelectedListener listener) {
        mFieldSelectedListener = listener;
    }

    private void showNewFieldDialog() {
        final LinearLayout layout = (LinearLayout) View.inflate(getActivity(), R.layout.view_create_new_field, null);
        final EditText nameEdit = (RobotoEditText) layout.findViewById(R.id.edit_name);
        final AppCompatSpinner typeSpinner = (AppCompatSpinner) layout.findViewById(R.id.spinner_field_type);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.label_new_item))
                .setView(layout)
                .setPositiveButton(R.string.button_create_uppercase, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        final String name = nameEdit.getText().toString();
                        if (!TextUtils.isEmpty(name)) {
                            final Field field = new Field();
                            field.setName(nameEdit.getText().toString());
                            field.setType(typeSpinner.getSelectedItemPosition() + 1);
                            field.setUser(Managers.getAccountManager().getCurrentUser());
                            field.save(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        mFieldListFragment.addNewField(field);
                                        dialog.dismiss();
                                    }
                                    else {
                                        Toast.makeText(VlApplication.getInstance(), R.string.toast_new_item_save_failed, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(VlApplication.getInstance(), R.string.toast_new_item_name_should_filled, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.button_cancel_uppercase, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .create();

        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        dialog.show();
    }

    private void hideKeyboard() {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchEdit.getWindowToken(), 0);
    }
}