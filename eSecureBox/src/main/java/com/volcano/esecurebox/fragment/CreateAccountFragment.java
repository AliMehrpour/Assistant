// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.volcano.esecurebox.Intents;
import com.volcano.esecurebox.Managers;
import com.volcano.esecurebox.R;
import com.volcano.esecurebox.model.Account;
import com.volcano.esecurebox.model.AccountFieldValue;
import com.volcano.esecurebox.model.Field;
import com.volcano.esecurebox.model.FieldTypeValue;
import com.volcano.esecurebox.model.SubCategory;
import com.volcano.esecurebox.model.SubCategoryField;
import com.volcano.esecurebox.util.BitmapUtils;
import com.volcano.esecurebox.util.LogUtils;
import com.volcano.esecurebox.util.Utils;
import com.volcano.esecurebox.widget.CircleDrawable;
import com.volcano.esecurebox.widget.FloatingLabeledEditText;
import com.volcano.esecurebox.widget.RobotoEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Create account fragment
 */
public class CreateAccountFragment extends AbstractFragment {

    private ContentSource mContentSource;
    private Account mAccount;
    private ArrayList<AccountFieldValue> mAccountFieldValues = new ArrayList<>();
    private final ArrayList<SubCategoryField> mFields = new ArrayList<>();
    private SubCategory mSelectedSubCategory;
    private String mSelectedSubCategoryId;
    private int mSavedFieldCount = 0;

    private RobotoEditText mAccountTitle;
    private RelativeLayout mSubCategoryLayout;
    private TextView mSubCategoryText;
    private ImageView mSubCategoryImage;
    private SubCategoryListFragment mSubCategoryListFragment;
    private FrameLayout mSubCategoryListLayout;
    private LinearLayout mFieldLayout;
    private FrameLayout mProgressLayout;

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
        mFieldLayout = (LinearLayout) view.findViewById(R.id.layout_fields);
        mSubCategoryLayout = (RelativeLayout) view.findViewById(R.id.layout_sub_category);
        mSubCategoryText = (TextView) view.findViewById(R.id.text_sub_category);
        mSubCategoryImage = (ImageView) view.findViewById(R.id.image_sub_category);
        mSubCategoryListLayout = (FrameLayout) view.findViewById(R.id.layout_sub_category_list);
        mProgressLayout = (FrameLayout) view.findViewById(R.id.layout_progress);
        mSubCategoryListFragment = (SubCategoryListFragment) getFragmentManager().findFragmentById(R.id.fragment_sub_category_list);
        if (mSubCategoryListFragment == null && Utils.hasJellyBeanApi()) {
            mSubCategoryListFragment = new SubCategoryListFragment();
            getFragmentManager().beginTransaction().add(R.id.layout_sub_category_list, mSubCategoryListFragment).commit();
        }

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
                mFieldLayout.setVisibility(View.VISIBLE);
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
            addCancellingRequest(SubCategory.getInBackground(mSelectedSubCategoryId, new GetCallback<SubCategory>() {
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
            }));

            mAccountTitle.setVisibility(View.VISIBLE);
            mSubCategoryText.setVisibility(View.VISIBLE);
            // TODO: do restore state of load account
        }
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
                                final FloatingLabeledEditText fieldEditText = (FloatingLabeledEditText) mFieldLayout.getChildAt(i);

                                final AccountFieldValue value = new AccountFieldValue();
                                final SubCategoryField field = mFields.get(i);
                                value.setAccount(mAccount);
                                value.setField(field.getField());
                                value.setValue(fieldEditText.getText());
                                value.setOrder(field.getOrder());
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
                                final FloatingLabeledEditText fieldEditText = (FloatingLabeledEditText) mFieldLayout.getChildAt(i);

                                final AccountFieldValue value = mAccountFieldValues.get(i);
                                value.setValue(fieldEditText.getText());
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
        else {
            Utils.showToast(R.string.toast_account_save_validation);
        }
    }

    /**
     * Delete loaded account with related fields
     */
    public void delete() {
        if (mAccount != null) {
            new AlertDialogWrapper.Builder(getActivity())
                    .setTitle(getString(R.string.label_delete))
                    .setMessage(R.string.alert_delete_account)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(R.string.label_yes_uppercase, new DialogInterface.OnClickListener() {
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

        if (mSelectedSubCategory.hasIcon()) {
            mSubCategoryImage.setImageDrawable(getResources().getDrawable(BitmapUtils.getDrawableIdentifier(getActivity(), subCategory.getIconName())));
        }
        else {
            mSubCategoryImage.setImageDrawable(new CircleDrawable(subCategory.getCategory().getColor(), CircleDrawable.FILL));
        }
    }

    private boolean valid() {
        return !TextUtils.isEmpty(mAccountTitle.getText()) &&
                (mContentSource == ContentSource.BY_CATEGORY ? mFields.size() > 0 : mAccountFieldValues.size() > 0);
    }

    private void loadFieldsBySubCategory() {
        mProgressLayout.setVisibility(View.VISIBLE);
        mListener.OnEnableActions(false);
        final ParseQuery query = SubCategoryField.getFieldBySubCategory(mSelectedSubCategory);
        addCancellingRequest(query);
        //noinspection unchecked
        query.findInBackground(
                new FindCallback<SubCategoryField>() {
                    @Override
                    public void done(List<SubCategoryField> subCategoryFields, ParseException e) {
                        if (e == null) {
                            // PopulateFields
                            mFieldLayout.setVisibility(View.INVISIBLE);
                            mFields.clear();
                            mFields.addAll(subCategoryFields);
                            final int size = mFields.size();
                            for (int i = 0; i < size; i++) {
                                final SubCategoryField field = mFields.get(i);
                                final FloatingLabeledEditText fieldEditText = new FloatingLabeledEditText(getActivity());
                                final String iconName = field.getField().getIconName();
                                if (iconName != null) {
                                    fieldEditText.setIcon(getResources().getDrawable(BitmapUtils.getDrawableIdentifier(getActivity(), iconName)));
                                }
                                else {
                                    fieldEditText.setIcon(new CircleDrawable(Color.TRANSPARENT, CircleDrawable.FILL,
                                            field.getField().getName().substring(0, 1), getResources().getColor(R.color.theme_primary)));
                                }
                                if (!TextUtils.isEmpty(field.getDefaultValue())) {
                                    fieldEditText.setText(field.getDefaultValue());
                                }
                                fieldEditText.setHint(field.getField().getName());
                                fieldEditText.setFormatType(field.getField().getFormat());
                                if (field.getField().getFormat() == Field.FORMAT_ENUM) {
                                    addCancellingRequest(FieldTypeValue.getValueByField(field.getField(), new FindCallback<FieldTypeValue>() {
                                        @Override
                                        public void done(List<FieldTypeValue> fieldTypeValues, ParseException e) {
                                            if (e == null) {
                                                final ArrayList<String> values = new ArrayList<>();
                                                for (FieldTypeValue value : fieldTypeValues) {
                                                    values.add(value.getValue());
                                                }
                                                fieldEditText.setPossibleValues(values);
                                            }
                                            else {
                                                LogUtils.LogE(TAG, "Load field values failed");
                                            }
                                        }
                                    }));
                                }
                                mFieldLayout.addView(fieldEditText);
                            }

                            mProgressLayout.setVisibility(View.GONE);
                            mAccountTitle.setVisibility(View.VISIBLE);
                            mFieldLayout.setVisibility(View.VISIBLE);
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
        addCancellingRequest(Account.getFirstInBackground(accountId, new GetCallback<Account>() {
            @Override
            public void done(Account account, ParseException e) {
                if (e == null) {
                    mAccount = account;
                    setSubCategory(account.getSubCategory());
                    mAccountTitle.setText(account.getTitle());
                    addCancellingRequest(AccountFieldValue.findInBackground(account, new FindCallback<AccountFieldValue>() {
                        @Override
                        public void done(List<AccountFieldValue> accountFieldValues, ParseException e) {
                            if (e == null) {
                                mAccountFieldValues.clear();
                                mAccountFieldValues.addAll(accountFieldValues);

                                final int size = mAccountFieldValues.size();
                                for (int i = 0; i < size; i++) {
                                    final AccountFieldValue value = mAccountFieldValues.get(i);
                                    final FloatingLabeledEditText fieldEditText = new FloatingLabeledEditText(getActivity());
                                    fieldEditText.setHint(value.getField().getName());
                                    fieldEditText.setText(value.getValue());
                                    fieldEditText.setIcon(new CircleDrawable(Color.TRANSPARENT, CircleDrawable.FILL,
                                            value.getField().getName().substring(0, 1), getResources().getColor(R.color.theme_primary)));
                                    fieldEditText.setFormatType(value.getField().getFormat());

                                    mFieldLayout.addView(fieldEditText);
                                }

                                mListener.OnEnableActions(true);
                                mProgressLayout.setVisibility(View.GONE);
                                mSubCategoryLayout.setVisibility(View.VISIBLE);
                                mAccountTitle.setVisibility(View.VISIBLE);
                                mFieldLayout.setVisibility(View.VISIBLE);
                            }
                            else {
                                setErrorState();
                            }
                        }
                    }));
                }
                else {
                    setErrorState();
                }
            }
        }));
    }

    private void emptyFields() {
        mFieldLayout.removeAllViews();
    }

    private void setErrorState() {
        LogUtils.LogE(TAG, "Load account/fields failed");
        mProgressLayout.setVisibility(View.GONE);
        mFieldLayout.setVisibility(View.GONE);
        Utils.showToast(mContentSource == ContentSource.BY_ACCOUNT ?
                R.string.toast_account_load_failed : R.string.toast_account_create_failed);

        final Activity activity = getActivity();
        if (activity != null) {
            getActivity().finish();
        }
    }
}