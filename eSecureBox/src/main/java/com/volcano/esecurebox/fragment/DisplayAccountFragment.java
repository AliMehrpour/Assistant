// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.volcano.esecurebox.R;
import com.volcano.esecurebox.model.Account;
import com.volcano.esecurebox.model.AccountFieldValue;
import com.volcano.esecurebox.model.SubCategory;
import com.volcano.esecurebox.util.BitmapUtils;
import com.volcano.esecurebox.util.LogUtils;
import com.volcano.esecurebox.util.Utils;
import com.volcano.esecurebox.widget.FloatingLabeledEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Edit account fragment
 */
public class DisplayAccountFragment extends AbstractFragment {

    private LinearLayout mFieldLayout;
    private FrameLayout mProgress;

    private OnEnableEditListener mListener;
    private ArrayList<AccountFieldValue> mFieldValues = new ArrayList<>();

    public interface OnEnableEditListener {
        public void onEnableEdit(boolean enable);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_display_account, container, false);
        mFieldLayout = (LinearLayout) view.findViewById(R.id.layout_fields);
        mProgress = (FrameLayout) view.findViewById(android.R.id.empty);
        return view;
    }

    public void setOnEnableEditListener(OnEnableEditListener listener) {
        mListener = listener;
    }

    /**
     * Load Fields of an account for displaying
     * @param accountId The account id
     */
    public void loadFields(String accountId) {
        mProgress.setVisibility(View.VISIBLE);
        mFieldLayout.setVisibility(View.GONE);
        mListener.onEnableEdit(false);

        addCancellingRequest(Account.getFirstInBackground(accountId, new GetCallback<Account>() {
            @Override
            public void done(Account account, ParseException e) {
                if (e == null) {
                    final FloatingLabeledEditText fle = new FloatingLabeledEditText(getActivity());
                    final SubCategory subCategory = account.getSubCategory();
                    fle.setDividerLineVisibility(View.INVISIBLE);
                    fle.setEnabled(false);
                    fle.setHint(getResources().getString(R.string.label_category));
                    fle.setText(subCategory.getName());
                    fle.setIcon(subCategory.getIconName(), null, BitmapUtils.getColor(subCategory.getCategory().getColor()));
                    mFieldLayout.addView(fle);

                    addCancellingRequest(AccountFieldValue.findInBackground(account, new FindCallback<AccountFieldValue>() {
                        @Override
                        public void done(List<AccountFieldValue> accountFieldValues, ParseException e) {
                            if (e == null) {
                                mFieldValues.clear();
                                mFieldValues.addAll(accountFieldValues);

                                final int size = mFieldValues.size();
                                for (int i = 0; i < size; i++) {
                                    final AccountFieldValue value = mFieldValues.get(i);
                                    final FloatingLabeledEditText fle = new FloatingLabeledEditText(getActivity());
                                    fle.setEnabled(false);
                                    fle.setHint(value.getField().getName());
                                    fle.setText(value.getValue());
                                    fle.setDividerLineVisibility(View.INVISIBLE);
                                    fle.setFormatType(value.getField().getFormat());
                                    fle.setIcon(value.getField().getIconName(), value.getField().getName().charAt(0), getResources().getColor(R.color.theme_primary));
                                    mFieldLayout.addView(fle);
                                }

                                mProgress.setVisibility(View.GONE);
                                mFieldLayout.setVisibility(View.VISIBLE);
                                mListener.onEnableEdit(true);
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

    private void setErrorState() {
        LogUtils.LogE(TAG, "Load account failed");
        mProgress.setVisibility(View.GONE);
        mFieldLayout.setVisibility(View.GONE);
        Utils.showToast(R.string.toast_account_load_failed);

        final Activity activity = getActivity();
        if (activity != null) {
            getActivity().finish();
        }
    }
}
