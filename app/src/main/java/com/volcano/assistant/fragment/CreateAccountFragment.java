// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.SaveCallback;
import com.volcano.assistant.R;
import com.volcano.assistant.activity.AbstractActivity;
import com.volcano.assistant.model.Account;
import com.volcano.assistant.model.AccountFieldValue;
import com.volcano.assistant.model.Category;
import com.volcano.assistant.model.Field;
import com.volcano.assistant.util.BitmapUtils;
import com.volcano.assistant.util.LogUtils;
import com.volcano.assistant.widget.CircleDrawable;
import com.volcano.assistant.widget.IconedEditText;

import java.util.List;

/**
 * Create account fragment
 */
public class CreateAccountFragment extends AbstractFragment {

    private Category mSelectedCategory;
    private List<Field> mFields;

    private IconedEditText mAccountTitle;
    private TextView mCategoryText;
    private ImageView mCategoryImage;
    private CategoryListFragment mCategoryListFragment;
    private FrameLayout mCategoryListLayout;
    private LinearLayout mFieldLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_create_account, container, false);

        mAccountTitle = (IconedEditText) view.findViewById(R.id.text_account_title);
        mFieldLayout = (LinearLayout) view.findViewById(R.id.layout_fields);
        mCategoryText = (TextView) view.findViewById(R.id.text_category);
        mCategoryImage = (ImageView) view.findViewById(R.id.image_category);
        mCategoryListLayout = (FrameLayout) view.findViewById(R.id.layout_category_list);
        mCategoryListFragment = (CategoryListFragment) getFragmentManager().findFragmentById(R.id.fragment_account_list);
        if (mCategoryListFragment == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mCategoryListFragment = new CategoryListFragment();
            getFragmentManager().beginTransaction().add(R.id.layout_category_list, mCategoryListFragment).commit();
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCategoryImage.setBackground(new CircleDrawable());
        mCategoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFieldLayout.setVisibility(View.INVISIBLE);
                mCategoryListLayout.setVisibility(View.VISIBLE);
                mCategoryListFragment.setSelectedCategory(mSelectedCategory);

            }
        });

        mCategoryListFragment.setCategorySelectedListener(new CategoryListFragment.OnCategorySelectedListener() {
            @Override
            public void onCategorySelected(Category category) {
                mCategoryListLayout.setVisibility(View.GONE);
                mFieldLayout.setVisibility(View.VISIBLE);
                if (mSelectedCategory == null || mSelectedCategory != category) {
                    emptyFields();

                    mSelectedCategory = category;

                    mCategoryImage.setBackground(new CircleDrawable(mSelectedCategory.getColor(), CircleDrawable.FILL));
                    mCategoryText.setText(mSelectedCategory.getName());

                    ((AbstractActivity) getActivity()).setToolbarColor(mSelectedCategory.getColor());
                    mAccountTitle.setVisibility(View.VISIBLE);
                    mAccountTitle.requestFocus();
                    loadFields();
                }
            }
        });
    }

    public void save() {
        if (valid()) {
            final Account account = new Account();
            account.setTitle(mAccountTitle.getText().toString());
            account.setCategory(mSelectedCategory);
            account.saveInBackground();
            final int size = mFields.size();
            for (int i = 0; i < size; i++) {
                final IconedEditText fieldEditText = (IconedEditText) mFieldLayout.getChildAt(i);

                AccountFieldValue value = new AccountFieldValue();
                value.setAccount(account);
                value.setField(mFields.get(i));
                value.setValue(fieldEditText.getText().toString());
                value.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        LogUtils.LogI(TAG, "new account successfully saved.");
                    }
                });
            }
        }
    }

    private boolean valid() {
        return true;
    }

    private void loadFields() {
        mFields = mSelectedCategory.getFields();

        // PopulateFields
        final int size = mFields.size();
        for (int i = 0; i < size; i++) {
            final Field field = mFields.get(i);
            final IconedEditText fieldEditText = new IconedEditText(getActivity());
            final String iconName = field.getIconName();
            if (iconName != null) {
                fieldEditText.setIcon(getResources().getDrawable(BitmapUtils.getDrawableIdentifier(getActivity(), iconName)));
            }
            else {
                fieldEditText.setmIndicatorText(field.getName().substring(0, 1));
            }
            fieldEditText.setHint(field.getName());
            mFieldLayout.addView(fieldEditText);
        }
    }

    private void emptyFields() {
        mFieldLayout.removeAllViews();
    }
}