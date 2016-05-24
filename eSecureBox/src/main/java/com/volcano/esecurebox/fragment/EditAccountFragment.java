// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.fragment;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.text.TextUtils;
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
import com.volcano.esecurebox.R;
import com.volcano.esecurebox.model.Account;
import com.volcano.esecurebox.model.AccountFieldValue;
import com.volcano.esecurebox.model.AccountFieldValue.Status;
import com.volcano.esecurebox.model.Field;
import com.volcano.esecurebox.model.SubCategory;
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
 * Edit account fragment
 */
public final class EditAccountFragment extends AbstractFragment {

    private final ArrayList<AccountFieldValue> mAccountFieldValues = new ArrayList<>();
    private Pair<AccountFieldValue, FieldCell> mLastRemovedFieldCell;
    private Account mAccount;
    private int mProcessedFieldCount = 0;
    private int mMaxOrder = 0;

    private RobotoEditText mAccountTitle;
    private RelativeLayout mSubCategoryLayout;
    private TextView mSubCategoryText;
    private ImageView mSubCategoryImage;
    private LinearLayout mFieldsLayout;
    private FrameLayout mProgressLayout;
    private RobotoTextView mAddFieldButton;
    private ScrollView mFieldsScrollView;
    private CoordinatorLayout mSnackbarLayout;

    private final OnFieldSwipeListener mSwipeListener = new OnFieldSwipeListener() {
        @Override
        public void onSwiped(final FieldCell fieldCell) {
            final int index = Integer.parseInt(fieldCell.getTag().toString());
            final AccountFieldValue fieldValue = mAccountFieldValues.get(index);

            if (fieldValue.getStatus() == Status.EXIST) {
                fieldValue.setStatus(Status.EXIST_REMOVED);
                mAccountFieldValues.get(index).setStatus(Status.EXIST_REMOVED);
            }
            else {
                fieldValue.setStatus(Status.ADDED_REMOVED);
                mAccountFieldValues.get(index).setStatus(Status.ADDED_REMOVED);
            }

            mLastRemovedFieldCell = new Pair<>(fieldValue, fieldCell);
            mFieldsScrollView.requestDisallowInterceptTouchEvent(false);

            final Snackbar snackbar = Snackbar.make(mSnackbarLayout, R.string.snackbar_text_field_deleted, Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.YELLOW)
                    .setAction(R.string.snackbar_action_undo, new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final AccountFieldValue removedAccountFieldValue = mLastRemovedFieldCell.first;
                            final FieldCell removedCell = mLastRemovedFieldCell.second;
                            final int index = Integer.parseInt(removedCell.getTag().toString());

                            int adjustedIndex = index;
                            for (int i = 0; i < index; i++) {
                                final Status status = mAccountFieldValues.get(i).getStatus();
                                if (status == Status.EXIST_REMOVED || status == Status.ADDED_REMOVED) {
                                    adjustedIndex--;
                                }
                            }
                            mFieldsLayout.addView(removedCell, adjustedIndex);

                            if (removedAccountFieldValue.getStatus() == Status.EXIST_REMOVED) {
                                removedAccountFieldValue.setStatus(Status.EXIST);
                                mAccountFieldValues.set(index, removedAccountFieldValue);
                            }
                            else {
                                mAccountFieldValues.add(index, removedAccountFieldValue);
                            }

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

    private OnActionsListener mListener;

    /**
     * Interface to containing activities have to implement to be notified of actions on account
     */
    public interface OnActionsListener {
        void OnEnableActions(boolean enable);
        void onVisibilityActions(int visibility);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_edit_account, container, false);

        mAccountTitle = (RobotoEditText) view.findViewById(R.id.text_account_title);
        mFieldsLayout = (LinearLayout) view.findViewById(R.id.layout_fields);
        mSubCategoryLayout = (RelativeLayout) view.findViewById(R.id.layout_sub_category);
        mSubCategoryText = (TextView) view.findViewById(R.id.text_sub_category);
        mSubCategoryImage = (ImageView) view.findViewById(R.id.image_sub_category);
        mProgressLayout = (FrameLayout) view.findViewById(R.id.layout_progress);
        mAddFieldButton = (RobotoTextView) view.findViewById(R.id.button_add_field);
        mFieldsScrollView = (ScrollView) view.findViewById(R.id.scroll_view_fields);
        mSnackbarLayout = (CoordinatorLayout) view.findViewById(R.id.layout_snackbar);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAddFieldButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddMoreFieldDialog();
            }
        });
    }

    /**
     * Set {@link OnActionsListener}
     * @param listener The listener
     */
    public void setOnEnableActionsListener(OnActionsListener listener) {
        mListener = listener;
    }

    /**
     * Set accountId and load account
     * @param accountId The accountId
     */
    public void setAccountId(String accountId) {
        loadFieldsByAccount(accountId);
    }

    /**
     * Save the account
     */
    public void save() {
        if (valid()) {
            mProgressLayout.setVisibility(View.VISIBLE);
            mListener.OnEnableActions(false);

            mAccount.setTitle(mAccountTitle.getText().toString());
            mAccount.save(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        LogUtils.LogI(TAG, "account updated successfully");
                        int j = 0; // View Counters
                        mProcessedFieldCount = 0;
                        final int size = mAccountFieldValues.size();
                        for (int i = 0; i < size; i++) {
                            final AccountFieldValue fieldValue = mAccountFieldValues.get(i);
                            final Status fieldValueStatus = fieldValue.getStatus();

                            if (fieldValueStatus == Status.EXIST || fieldValueStatus == Status.ADDED) {
                                final FieldCell fieldCell = (FieldCell) mFieldsLayout.getChildAt(j++);
                                if (fieldCell.isValueChanged()) {
                                    fieldValue.setValue(fieldCell.getValue());
                                    if (fieldValueStatus == Status.ADDED) {
                                        fieldValue.setOrder(++mMaxOrder);
                                    }
                                    fieldValue.setStatus(Status.EXIST);
                                    fieldValue.save(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                mProcessedFieldCount++;
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
                                else {
                                    mProcessedFieldCount++;
                                    checkDone();
                                }
                            }
                            else if (fieldValueStatus == Status.EXIST_REMOVED) {
                                fieldValue.remove(new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            mProcessedFieldCount++;
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
                            else {
                                mProcessedFieldCount++;
                                checkDone();
                            }
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
                                        finish();
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
        if (mProcessedFieldCount == mAccountFieldValues.size()) {
            finish();
        }
    }

    private void finish() {
        mProgressLayout.setVisibility(View.GONE);

        final Activity activity = getActivity();
        if (activity != null) {
            activity.finish();
            startActivity(Intents.getMainIntent());
        }
    }

    private boolean valid() {
        boolean valid = true;

        if (TextUtils.isEmpty(mAccountTitle.getText())) {
            valid = false;
            Utils.showToast(R.string.toast_account_save_validation_title_empty);
        }
        else if (mAccountFieldValues.size() == 0) {
            valid = false;
            Utils.showToast(R.string.toast_account_save_validation_no_field);
        }

        return valid;
    }

    private void setSubCategory(SubCategory subCategory) {
        mSubCategoryText.setText(subCategory.getName());

        boolean hasIcon = false;
        if (subCategory.hasIcon()) {
            final int resourceId = BitmapUtils.getDrawableIdentifier(getActivity(), subCategory.getIconName());
            if (resourceId != 0) {
                mSubCategoryImage.setImageDrawable(BitmapUtils.getDrawable(resourceId));
                hasIcon = true;
            }
        }
        if (!hasIcon) {
            mSubCategoryImage.setImageDrawable(
                    new CircleDrawable(subCategory.getCategory().getColor(), CircleDrawable.FILL));
        }
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
                                    final AccountFieldValue accountFieldValue = mAccountFieldValues.get(i);
                                    addToFieldsLayout(accountFieldValue, i);

                                    final int order = accountFieldValue.getOrder();
                                    if (mMaxOrder < order) {
                                        mMaxOrder = order;
                                    }
                                }

                                mSubCategoryLayout.setVisibility(View.VISIBLE);
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
                else {
                    setErrorState();
                }
            }
        });
    }

    private void setErrorState() {
        LogUtils.LogE(TAG, "Load account/fields failed");
        mProgressLayout.setVisibility(View.GONE);
        mFieldsLayout.setVisibility(View.GONE);
        Utils.showToast(R.string.toast_account_load_failed);

        final Activity activity = getActivity();
        if (activity != null) {
            getActivity().finish();
        }
    }

    private void addToFieldsLayout(AccountFieldValue fieldValue, int index) {
        final FieldCell fieldCell = new FieldCell(getActivity());
        fieldCell.setField(fieldValue.getField(), fieldValue.getValue(), index);
        fieldCell.setOnSwipeListener(mSwipeListener);
        fieldCell.setSwipeEnabled(true);
        mFieldsLayout.addView(fieldCell);
    }

    private void addMoreField(List<Field> fields) {
        for (final Field field : fields) {
            final AccountFieldValue accountFieldValue= new AccountFieldValue(mAccount, field, Status.ADDED);
            final int index = mAccountFieldValues.size();
            addToFieldsLayout(accountFieldValue, index);
            mAccountFieldValues.add(accountFieldValue);
        }

        scrollToDown();
    }

    private void scrollToDown() {
        mFieldsScrollView.post(new Runnable() {
            @Override
            public void run() {
                mFieldsScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void showAddMoreFieldDialog() {
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        final AddFieldDialogFragment fragment = new AddFieldDialogFragment();
        fragment.setOnFieldSelectedListener(new AddFieldDialogFragment.OnFieldSelectedListener() {
            @Override
            public void onFieldSelected(List<Field> fields) {
                if (fields != null) {
                    addMoreField(fields);
                }

                fragment.dismiss();
                final FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.remove(fragment).commit();
            }
        });
        fragment.show(transaction, fragment.getClass().getSimpleName());
    }
}
