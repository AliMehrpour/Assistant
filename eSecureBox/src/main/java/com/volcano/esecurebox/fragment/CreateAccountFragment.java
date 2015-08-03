// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.volcano.esecurebox.Intents;
import com.volcano.esecurebox.Managers;
import com.volcano.esecurebox.R;
import com.volcano.esecurebox.model.Account;
import com.volcano.esecurebox.model.AccountFieldValue;
import com.volcano.esecurebox.model.Field;
import com.volcano.esecurebox.model.SubCategory;
import com.volcano.esecurebox.model.SubCategoryField;
import com.volcano.esecurebox.util.BitmapUtils;
import com.volcano.esecurebox.util.LogUtils;
import com.volcano.esecurebox.util.Utils;
import com.volcano.esecurebox.widget.CircleDrawable;
import com.volcano.esecurebox.widget.FieldCell;
import com.volcano.esecurebox.widget.RobotoEditText;
import com.volcano.esecurebox.widget.RobotoTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Create account fragment
 */
public class CreateAccountFragment extends AbstractFragment {

    private ContentSource mContentSource;
    private Account mAccount;
    private SubCategory mSelectedSubCategory;
    private final ArrayList<AccountFieldValue> mAccountFieldValues = new ArrayList<>();
    private final ArrayList<SubCategoryField> mFields = new ArrayList<>();
    private String mSelectedSubCategoryId;
    private int mSavedFieldCount = 0;

    private FragmentManager mFragmentManager;
    private RobotoEditText mAccountTitle;
    private RelativeLayout mSubCategoryLayout;
    private TextView mSubCategoryText;
    private ImageView mSubCategoryImage;
    private SubCategoryListFragment mSubCategoryListFragment;
    private FrameLayout mSubCategoryListLayout;
    private LinearLayout mFieldsLayout;
    private ScrollView mFieldsScrollView;
    private FrameLayout mProgressLayout;
    private RobotoTextView mAddFieldButton;
    
    private OnEnableActionsListener mListener;

    public interface OnEnableActionsListener {
        public void OnEnableActions(boolean enable);
    }

    public enum ContentSource {
        BY_CATEGORY,
        BY_ACCOUNT
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_create_account, container, false);

        mAccountTitle = (RobotoEditText) view.findViewById(R.id.text_account_title);
        mFieldsLayout = (LinearLayout) view.findViewById(R.id.layout_fields);
        mSubCategoryLayout = (RelativeLayout) view.findViewById(R.id.layout_sub_category);
        mSubCategoryText = (TextView) view.findViewById(R.id.text_sub_category);
        mSubCategoryImage = (ImageView) view.findViewById(R.id.image_sub_category);
        mSubCategoryListLayout = (FrameLayout) view.findViewById(R.id.layout_sub_category_list);
        mProgressLayout = (FrameLayout) view.findViewById(R.id.layout_progress);
        mFragmentManager = getFragmentManager();
        mSubCategoryListFragment = (SubCategoryListFragment) mFragmentManager.findFragmentById(R.id.fragment_sub_category_list);
        if (mSubCategoryListFragment == null && Utils.hasJellyBeanApi()) {
            mSubCategoryListFragment = new SubCategoryListFragment();
            mFragmentManager.beginTransaction().add(R.id.layout_sub_category_list, mSubCategoryListFragment).commit();
        }
        mAddFieldButton = (RobotoTextView) view.findViewById(R.id.button_add_field);
        mFieldsScrollView = (ScrollView) view.findViewById(R.id.scroll_view_fields);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSubCategoryListFragment.setDefaultColorStyle(CircleDrawable.FILL);
        mSubCategoryListFragment.setSubCategoryListener(new SubCategoryListFragment.OnSubCategoryListener() {
            @Override
            public void onSubCategoriesLoadFailed() {
                setErrorState();
            }

            @Override
            public void onSubCategoriesEmpty() {
                setErrorState();
            }

            @Override
            public void onSubCategorySelected(SubCategory subCategory) {
                mSubCategoryListLayout.setVisibility(View.GONE);
                mFieldsLayout.setVisibility(View.VISIBLE);
                if (mSelectedSubCategory == null || mSelectedSubCategory != subCategory) {
                    emptyFields();
                    setSubCategory(subCategory);
                    mAccountTitle.requestFocus();
                    loadFieldsBySubCategory();
                }
            }
        });

        if (savedInstanceState != null) {
            mSelectedSubCategoryId = savedInstanceState.getString(Intents.KEY_SUB_CATEGORY_ID);
            SubCategory.getInBackground(this, mSelectedSubCategoryId, new GetCallback<SubCategory>() {
                @Override
                public void done(SubCategory subCategory, ParseException e) {
                    if (e == null) {
                        setSubCategory(subCategory);
                        loadFieldsBySubCategory();
                    }
                    else {
                        setErrorState();
                    }
                }
            });

            mAccountTitle.setVisibility(View.VISIBLE);
            mSubCategoryText.setVisibility(View.VISIBLE);

            // TODO: do restore state of load account
        }

        mAddFieldButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final AddFieldDialog addFieldDialog = new AddFieldDialog();
                addFieldDialog.show(mFragmentManager, "fragment_add_field");
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(Intents.KEY_SUB_CATEGORY_ID, mSelectedSubCategoryId);
    }

    /**
     * Set {@link OnEnableActionsListener}
     * @param listener The listener
     */
    public void setOnEnableActionsListener(OnEnableActionsListener listener) {
        mListener = listener;
    }

    /**
     * Set category of account
     * @param categoryId The categoryId
     * @param categoryColor The category color
     */
    public void setCategoryId(String categoryId, String categoryColor) {
        mContentSource = ContentSource.BY_CATEGORY;
        mSubCategoryLayout.setVisibility(View.VISIBLE);
        mSubCategoryListLayout.setVisibility(View.VISIBLE);
        mSubCategoryListFragment.setCategoryId(categoryId);
        mSubCategoryImage.setImageDrawable(new CircleDrawable(categoryColor, CircleDrawable.STROKE));
    }

    /**
     * Set accountId and load account
     * @param accountId The accountId
     */
    public void setAccountId(String accountId) {
        mContentSource = ContentSource.BY_ACCOUNT;
        mSubCategoryListLayout.setVisibility(View.GONE);
        loadFieldsByAccount(accountId);
    }

    /**
     * Save the account
     */
    public void save() {
        if (valid()) {
            if (mContentSource == ContentSource.BY_CATEGORY) {
                mProgressLayout.setVisibility(View.VISIBLE);
                mListener.OnEnableActions(false);

                mAccount = new Account();
                mAccount.setTitle(mAccountTitle.getText().toString());
                mAccount.setSubCategory(mSelectedSubCategory);
                mAccount.setUser(Managers.getAccountManager().getCurrentUser());
                mAccount.save(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            LogUtils.LogI(TAG, "New account successfully saved");

                            mSavedFieldCount = 0;
                            final int size = mFields.size();
                            for (int i = 0; i < size; i++) {
                                final FieldCell fieldCell = (FieldCell) mFieldsLayout.getChildAt(i);

                                final AccountFieldValue value = new AccountFieldValue();
                                final SubCategoryField field = mFields.get(i);
                                value.setAccount(mAccount);
                                value.setField(field.getField());
                                value.setValue(fieldCell.getValue());
                                value.setOrder(i + 1); // Set order based on user input
                                value.save(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            mSavedFieldCount++;
                                            checkDone();
                                        }
                                        else {
                                            LogUtils.LogI(TAG, "Save account failed", e);
                                            Utils.showToast(R.string.toast_account_save_failed);
                                            delete();
                                        }
                                    }
                                });
                            }
                        }
                        else {
                            LogUtils.LogI(TAG, "Save account failed", e);
                            Utils.showToast(R.string.toast_account_save_failed);
                            mProgressLayout.setVisibility(View.GONE);
                            mListener.OnEnableActions(true);
                        }
                    }
                });
            }
            else {
                mProgressLayout.setVisibility(View.VISIBLE);
                mListener.OnEnableActions(false);

                mAccount.setTitle(mAccountTitle.getText().toString());
                mAccount.save(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            LogUtils.LogI(TAG, "account updated successfully");

                            mSavedFieldCount = 0;
                            final int size = mAccountFieldValues.size();
                            for (int i = 0; i < size; i++) {
                                final FieldCell fieldCell = (FieldCell) mFieldsLayout.getChildAt(i);

                                final AccountFieldValue value = mAccountFieldValues.get(i);
                                value.setValue(fieldCell.getValue());
                                value.save(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            mSavedFieldCount++;
                                            checkDone();
                                        }
                                        else {
                                            LogUtils.LogI(TAG, "Update account failed", e);
                                            Utils.showToast(R.string.toast_account_update_failed);
                                            mProgressLayout.setVisibility(View.GONE);
                                            mListener.OnEnableActions(true);
                                        }
                                    }
                                });
                            }
                        }
                        else {
                            LogUtils.LogI(TAG, "Update account failed", e);
                            Utils.showToast(R.string.toast_account_update_failed);
                            mProgressLayout.setVisibility(View.GONE);
                            mListener.OnEnableActions(true);
                        }
                    }
                });

            }
        }
    }

    /**
     * Delete loaded account with related fields
     */
    public void delete() {
        if (mAccount != null) {
            new AlertDialogWrapper.Builder(getActivity())
                    .setMessage(R.string.alert_delete_account)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(R.string.button_delete_uppercase, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mProgressLayout.setVisibility(View.VISIBLE);
                            mAccount.remove(mAccountFieldValues, new DeleteCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Utils.showToast(R.string.toast_account_delete_successful);
                                        mProgressLayout.setVisibility(View.GONE);
                                        if (getActivity() != null) {
                                            getActivity().finish();
                                            startActivity(Intents.getMainIntent());
                                        }
                                    }
                                    else {
                                        LogUtils.LogE(TAG, "Delete account failed");
                                        Utils.showToast(R.string.toast_account_delete_failed);
                                        mProgressLayout.setVisibility(View.GONE);
                                        mListener.OnEnableActions(true);
                                    }
                                }
                            });
                        }
                    })
                    .show();
        }
    }

    private void checkDone() {
        if (mSavedFieldCount == (mContentSource == ContentSource.BY_CATEGORY ?
                mFields.size() : mAccountFieldValues.size())) {
            mProgressLayout.setVisibility(View.GONE);
            if (getActivity() != null) {
                getActivity().finish();
                startActivity(Intents.getMainIntent());
            }
        }
    }

    private void setSubCategory(SubCategory subCategory) {
        mSelectedSubCategory = subCategory;
        mSelectedSubCategoryId = subCategory.getObjectId();
        mSubCategoryText.setText(mSelectedSubCategory.getName());

        boolean hasIcon = false;
        if (subCategory.hasIcon()) {
            final int resourceId = BitmapUtils.getDrawableIdentifier(getActivity(), subCategory.getIconName());
            if (resourceId != 0) {
                mSubCategoryImage.setImageDrawable(getResources().getDrawable(resourceId));
                hasIcon = true;
            }
        }
        if (!hasIcon) {
            mSubCategoryImage.setImageDrawable(new CircleDrawable(subCategory.getCategory().getColor(), CircleDrawable.FILL));
        }
    }

    private boolean valid() {
        boolean valid = true;

        if (TextUtils.isEmpty(mAccountTitle.getText())) {
            valid = false;
            Utils.showToast(R.string.toast_account_save_validation_title_empty);
        }
        else if ((mContentSource == ContentSource.BY_CATEGORY ? mFields.size() : mAccountFieldValues.size()) == 0) {
            valid = false;
            Utils.showToast(R.string.toast_account_save_validation_no_field);
        }

        return valid;
    }

    private void loadFieldsBySubCategory() {
        mProgressLayout.setVisibility(View.VISIBLE);
        mListener.OnEnableActions(false);
        SubCategoryField.getFieldBySubCategory(this, mSelectedSubCategory,
                new FindCallback<SubCategoryField>() {
                    @Override
                    public void done(List<SubCategoryField> subCategoryFields, ParseException e) {
                        if (e == null) {
                            // PopulateFields
                            mFieldsLayout.setVisibility(View.INVISIBLE);
                            mFields.clear();
                            mFields.addAll(subCategoryFields);
                            final int size = mFields.size();
                            for (int i = 0; i < size; i++) {
                                createFieldLayout(mFields.get(i));
                            }

                            mProgressLayout.setVisibility(View.GONE);
                            mAccountTitle.setVisibility(View.VISIBLE);
                            mFieldsLayout.setVisibility(View.VISIBLE);
                            mAddFieldButton.setVisibility(View.VISIBLE);
                            mListener.OnEnableActions(true);
                        }
                        else {
                            setErrorState();
                        }
                    }
                });
    }

    private void loadFieldsByAccount(String accountId) {
        mProgressLayout.setVisibility(View.VISIBLE);
        mListener.OnEnableActions(false);
        Account.getFirstInBackground(this, accountId, new GetCallback<Account>() {
            @Override
            public void done(Account account, ParseException e) {
                if (e == null) {
                    mAccount = account;
                    setSubCategory(account.getSubCategory());
                    mAccountTitle.setText(account.getTitle());
                    AccountFieldValue.findInBackground(this, account, new FindCallback<AccountFieldValue>() {
                        @Override
                        public void done(List<AccountFieldValue> accountFieldValues, ParseException e) {
                            if (e == null) {
                                mAccountFieldValues.clear();
                                mAccountFieldValues.addAll(accountFieldValues);

                                final int size = mAccountFieldValues.size();
                                for (int i = 0; i < size; i++) {
                                    final AccountFieldValue value = mAccountFieldValues.get(i);
                                    final FieldCell fieldCell = new FieldCell(getActivity());
                                    fieldCell.setField(value.getField(), value.getValue());
                                    mFieldsLayout.addView(fieldCell);
                                }

                                mListener.OnEnableActions(true);
                                mProgressLayout.setVisibility(View.GONE);
                                mSubCategoryLayout.setVisibility(View.VISIBLE);
                                mAccountTitle.setVisibility(View.VISIBLE);
                                mFieldsLayout.setVisibility(View.VISIBLE);
                                mAddFieldButton.setVisibility(View.VISIBLE);
                            }
                            else {
                                setErrorState();
                            }
                        }
                    });
                }
                else {
                    setErrorState();
                }
            }
        });
    }

    private void createFieldLayout(SubCategoryField field) {
        final FieldCell fieldCell = new FieldCell(getActivity());
        fieldCell.setField(field.getField(), field.getDefaultValue());
        mFieldsLayout.addView(fieldCell);
    }

    private void emptyFields() {
        mFieldsLayout.removeAllViews();
    }

    private void setErrorState() {
        LogUtils.LogE(TAG, "Load account/fields failed");
        mProgressLayout.setVisibility(View.GONE);
        mFieldsLayout.setVisibility(View.GONE);
        Utils.showToast(mContentSource == ContentSource.BY_ACCOUNT ?
                R.string.toast_account_load_failed : R.string.toast_account_create_failed);

        final Activity activity = getActivity();
        if (activity != null) {
            getActivity().finish();
        }
    }

    private void addMoreField(List<Field> fields) {
        for(final Field field : fields) {
            final SubCategoryField subCategoryField = new SubCategoryField(mSelectedSubCategory, field);
            mFields.add(subCategoryField);
            createFieldLayout(subCategoryField);
        }

        scrollToDown();
    }

    private void scrollToDown() {
        // TODO: Check why it doesn't work or use ListView instead!
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                mFieldsScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }).start();*/
    }

    public class AddFieldDialog extends DialogFragment {

        private RobotoTextView mCancelButton;
        private RobotoTextView mChooseButton;
        private RobotoEditText mSearchEdit;
        private FieldListFragment mFragment;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_Assistant);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.dialog_field_list, container);

            mCancelButton = (RobotoTextView) view.findViewById(R.id.button_cancel);
            mChooseButton = (RobotoTextView) view.findViewById(R.id.button_choose);
            mSearchEdit = (RobotoEditText) view.findViewById(R.id.edit_search);
            mFragment = (FieldListFragment) getFragmentManager().findFragmentById(R.id.fragment_field_list);

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
                    mFragment.loadFields(s.toString());
                }
            });

            mCancelButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            mChooseButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    addMoreField(mFragment.getSelectedFields());
                }
            });
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            if (mFragment != null)
                getFragmentManager().beginTransaction().remove(mFragment).commit();
        }
    }
}