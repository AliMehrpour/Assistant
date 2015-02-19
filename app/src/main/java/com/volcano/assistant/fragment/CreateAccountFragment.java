// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.volcano.assistant.Intents;
import com.volcano.assistant.R;
import com.volcano.assistant.model.Account;
import com.volcano.assistant.model.AccountFieldValue;
import com.volcano.assistant.model.SubCategory;
import com.volcano.assistant.model.SubCategoryField;
import com.volcano.assistant.util.BitmapUtils;
import com.volcano.assistant.util.LogUtils;
import com.volcano.assistant.util.Utils;
import com.volcano.assistant.widget.CircleDrawable;
import com.volcano.assistant.widget.IconizedEditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Create account fragment
 */
public class CreateAccountFragment extends AbstractFragment {

    private SubCategory mSelectedSubCategory;
    private String mSelectedSubCategoryId;
    private final ArrayList<SubCategoryField> mFields = new ArrayList<>();
    private boolean mInitialized = false;

    private IconizedEditText mAccountTitle;
    private TextView mCategoryText;
    private ImageView mCategoryImage;
    private SubCategoryListFragment mSubCategoryListFragment;
    private FrameLayout mSubCategoryListLayout;
    private LinearLayout mFieldLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_create_account, container, false);

        mAccountTitle = (IconizedEditText) view.findViewById(R.id.text_account_title);
        mFieldLayout = (LinearLayout) view.findViewById(R.id.layout_fields);
        mCategoryText = (TextView) view.findViewById(R.id.text_category);
        mCategoryImage = (ImageView) view.findViewById(R.id.image_category);
        mSubCategoryListLayout = (FrameLayout) view.findViewById(R.id.layout_sub_category_list);
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
        mSubCategoryListFragment.setCategorySelectedListener(new SubCategoryListFragment.OnSubCategorySelectedListener() {
            @Override
            public void onSubCategorySelected(SubCategory subCategory) {
                mInitialized = true;
                mSubCategoryListLayout.setVisibility(View.GONE);
                mFieldLayout.setVisibility(View.VISIBLE);
                if (mSelectedSubCategory == null || mSelectedSubCategory != subCategory) {
                    emptyFields();
                    setSubCategory(subCategory);
                    mAccountTitle.setVisibility(View.VISIBLE);
                    mAccountTitle.requestFocus();
                    loadFields();
                }
            }
        });

        if (savedInstanceState != null) {
            mSelectedSubCategoryId = savedInstanceState.getString(Intents.KEY_SUB_CATEGORY_ID);
            SubCategory.getInBackground(mSelectedSubCategoryId, new GetCallback<SubCategory>() {
                @Override
                public void done(SubCategory subCategory, ParseException e) {
                    setSubCategory(subCategory);
                    loadFields();
                }
            });

            mAccountTitle.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(Intents.KEY_INITIALIZED, mInitialized);
        outState.putString(Intents.KEY_SUB_CATEGORY_ID, mSelectedSubCategoryId);
    }

    /**
     * Set category of account
     * @param categoryId The categoryId
     * @param categoryColor The category color
     */
    public void setCategoryId(String categoryId, String categoryColor) {
        mSubCategoryListLayout.setVisibility(View.VISIBLE);
        mSubCategoryListFragment.setCategoryId(categoryId);
        mCategoryImage.setImageDrawable(new CircleDrawable(categoryColor, CircleDrawable.STROKE));
    }

    /**
     * Save the account
     */
    public void save() {
        if (valid()) {
            final Account account = new Account();
            account.setTitle(mAccountTitle.getText().toString());
            account.setCreateDate(new Date());
            account.setSubCategory(mSelectedSubCategory);

            account.save(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        final int size = mFields.size();
                        for (int i = 0; i < size; i++) {
                            final IconizedEditText fieldEditText = (IconizedEditText) mFieldLayout.getChildAt(i);

                            AccountFieldValue value = new AccountFieldValue();
                            value.setAccount(account);
                            value.setField(mFields.get(i).getField());
                            value.setValue(fieldEditText.getText().toString());
                            value.save(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        LogUtils.LogI(TAG, "New account successfully saved");
                                        final Activity activity = getActivity();
                                        if (activity != null) {
                                            startActivity(Intents.getMainIntent(true));
                                        }
                                    }
                                    else {
                                        LogUtils.LogI(TAG, "Save account failed", e);
                                        Utils.showToast(R.string.toast_save_account_failed);
                                    }
                                }
                            });
                        }
                    }
                    else {
                        LogUtils.LogI(TAG, "Save account failed", e);
                        Utils.showToast(R.string.toast_save_account_failed);
                    }
                }
            });
        }
        else {
            Utils.showToast(R.string.toast_save_account_validation);
        }
    }

    private void setSubCategory(SubCategory subCategory) {
        mSelectedSubCategory = subCategory;
        mSelectedSubCategoryId = subCategory.getObjectId();
        mCategoryText.setText(mSelectedSubCategory.getName());

        if (mSelectedSubCategory.hasIcon()) {
            mCategoryImage.setImageDrawable(getResources().getDrawable(BitmapUtils.getDrawableIdentifier(getActivity(), subCategory.getIconName())));
        }
        else {
            mCategoryImage.setImageDrawable(new CircleDrawable(subCategory.getCategory().getColor(), CircleDrawable.FILL));
        }
    }

    private boolean valid() {
        return !TextUtils.isEmpty(mAccountTitle.getText()) && mFields.size() > 0;
    }

    private void loadFields() {
        SubCategoryField.getFieldBySubCategory(mSelectedSubCategory).findInBackground(
                new FindCallback<SubCategoryField>() {
                    @Override
                    public void done(List<SubCategoryField> subCategoryFields, ParseException e) {
                        if (e == null) {
                            // PopulateFields
                            mFields.clear();
                            mFields.addAll(subCategoryFields);
                            final int size = mFields.size();
                            for (int i = 0; i < size; i++) {
                                final SubCategoryField field = mFields.get(i);
                                final IconizedEditText fieldEditText = new IconizedEditText(getActivity());
                                fieldEditText.setId(field.hashCode());
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
                                mFieldLayout.addView(fieldEditText);
                            }
                        }
                    }
                });
    }

    private void emptyFields() {
        mFieldLayout.removeAllViews();
    }
}