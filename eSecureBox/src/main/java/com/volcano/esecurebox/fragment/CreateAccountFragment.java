// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
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
import com.volcano.esecurebox.widget.FieldCell.OnFieldSwipeListener;
import com.volcano.esecurebox.widget.RobotoEditText;
import com.volcano.esecurebox.widget.RobotoTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Create account fragment
 */
public class CreateAccountFragment extends AbstractFragment {

    private final ArrayList<SubCategoryField> mFields = new ArrayList<>();
    private SubCategory mSelectedSubCategory;
    private Pair<Object, FieldCell> mLastRemovedFieldCell;
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
    private FrameLayout mProgressLayout;
    private RobotoTextView mAddFieldButton;
    private ScrollView mFieldsScrollView;
    private CoordinatorLayout mSnackbarLayout;

    private final OnFieldSwipeListener mSwipeListener = new OnFieldSwipeListener() {
        @Override
        public void onSwiped(final FieldCell fieldCell) {
            final int index = Integer.parseInt(fieldCell.getTag().toString());
            mLastRemovedFieldCell = new Pair<Object, FieldCell>(mFields.get(index), fieldCell);
            mFields.remove(index);
            mFieldsScrollView.requestDisallowInterceptTouchEvent(false);

            final Snackbar snackbar = Snackbar.make(mSnackbarLayout, R.string.snackbar_text_field_deleted, Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.YELLOW)
                    .setAction(R.string.snackbar_action_undo, new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final SubCategoryField removedSubCategoryField = (SubCategoryField) mLastRemovedFieldCell.first;
                            final FieldCell removedCell = mLastRemovedFieldCell.second;
                            final int index = Integer.parseInt(removedCell.getTag().toString());
                            mFieldsLayout.addView(removedCell, index);
                            mFields.add(index, removedSubCategoryField);
                            removedCell.resetPosition();
                            mLastRemovedFieldCell = null;
                        }
                    });

            final View view = snackbar.getView();
            final TextView text = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            text.setTextColor(Color.WHITE);
            snackbar.show();
        }

        @Override
        public void onSwipeStarted() {
            mFieldsScrollView.requestDisallowInterceptTouchEvent(true);
        }

        @Override
        public void onSwipeCanceled() {
            mFieldsScrollView.requestDisallowInterceptTouchEvent(false);
        }
    };

    private OnEnableActionsListener mListener;

    /**
     * Interface to containing activities have to implement to be notified of actions on account
     */
    public interface OnEnableActionsListener {
        void OnEnableActions(boolean enable);
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
        mSnackbarLayout = (CoordinatorLayout) view.findViewById(R.id.layout_snackbar);

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
                    removeFields();
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
        mSubCategoryLayout.setVisibility(View.VISIBLE);
        mSubCategoryListLayout.setVisibility(View.VISIBLE);
        mSubCategoryListFragment.setCategoryId(categoryId);
        mSubCategoryImage.setImageDrawable(new CircleDrawable(categoryColor, CircleDrawable.STROKE));
    }

    /**
     * Save the account
     */
    public void save() {
        if (valid()) {
            mProgressLayout.setVisibility(View.VISIBLE);
            mListener.OnEnableActions(false);

            final Account account = new Account();
            account.setTitle(mAccountTitle.getText().toString());
            account.setSubCategory(mSelectedSubCategory);
            account.setUser(Managers.getAccountManager().getCurrentUser());
            account.save(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        LogUtils.LogI(TAG, "New account successfully saved");

                        mSavedFieldCount = 0;
                        final ArrayList<AccountFieldValue> accountFieldValues = new ArrayList<>();
                        final int size = mFields.size();
                        for (int i = 0; i < size; i++) {
                            final FieldCell fieldCell = (FieldCell) mFieldsLayout.getChildAt(i);

                            final AccountFieldValue value = new AccountFieldValue();
                            final SubCategoryField field = mFields.get(i);
                            value.setAccount(account);
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

                                        account.remove(accountFieldValues, new DeleteCallback() {
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
                                }
                            });
                            accountFieldValues.add(value);
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
    }

    private void checkDone() {
        if (mSavedFieldCount == mFields.size()) {
            mProgressLayout.setVisibility(View.GONE);
            if (getActivity() != null) {
                startActivity(Intents.getMainIntent());
                getActivity().finish();
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
                mSubCategoryImage.setImageDrawable(BitmapUtils.getDrawable(resourceId));
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
        else if (mFields.size() == 0) {
            valid = false;
            Utils.showToast(R.string.toast_account_save_validation_no_field);
        }

        return valid;
    }

    private void loadFieldsBySubCategory() {
        mProgressLayout.setVisibility(View.VISIBLE);
        mListener.OnEnableActions(false);
        SubCategoryField.getFieldBySubCategory(this, mSelectedSubCategory, new FindCallback<SubCategoryField>() {
            @Override
            public void done(List<SubCategoryField> subCategoryFields, ParseException e) {
                if (e == null) {
                    // PopulateFields
                    mFieldsLayout.setVisibility(View.INVISIBLE);
                    mFields.clear();
                    mFields.addAll(subCategoryFields);
                    final int size = mFields.size();
                    for (int i = 0; i < size; i++) {
                        addToFieldsLayout(mFields.get(i), i);
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

    private void addToFieldsLayout(SubCategoryField field, int index) {
        final FieldCell fieldCell = new FieldCell(getActivity());
        fieldCell.setField(field.getField(), field.getDefaultValue(), index);
        fieldCell.setOnSwipeListener(mSwipeListener);
        fieldCell.setSwipeEnabled(true);
        mFieldsLayout.addView(fieldCell);
    }

    private void removeFields() {
        mFieldsLayout.removeAllViews();
    }

    private void setErrorState() {
        LogUtils.LogE(TAG, "Load account/fields failed");
        mProgressLayout.setVisibility(View.GONE);
        mFieldsLayout.setVisibility(View.GONE);
        Utils.showToast(R.string.toast_account_create_failed);

        final Activity activity = getActivity();
        if (activity != null) {
            getActivity().finish();
        }
    }

    private void addMoreField(List<Field> fields) {
        for(final Field field : fields) {
            final SubCategoryField subCategoryField = new SubCategoryField(mSelectedSubCategory, field);
            addToFieldsLayout(subCategoryField, mFields.size());
            mFields.add(subCategoryField);
        }

        scrollToDown();
    }

    private void scrollToDown() {
        mFieldsScrollView.post(new Runnable() {
            @Override
            public void run() {
                mFieldsScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });    }

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