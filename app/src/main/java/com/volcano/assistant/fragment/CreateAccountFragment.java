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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.volcano.assistant.R;
import com.volcano.assistant.activity.AbstractActivity;
import com.volcano.assistant.model.Category;
import com.volcano.assistant.model.Field;
import com.volcano.assistant.widget.CircleDrawable;
import com.volcano.assistant.widget.IconedEditText;

import java.util.List;

/**
 * Created by alimehrpour on 1/9/15.
 */
public class CreateAccountFragment extends AbstractFragment {

    private Category mSelectedCategory;
    private List<Field> mFields;

    private IconedEditText mAccountName;
    private TextView mCategoryText;
    private ImageView mCategoryImage;
    private CategoryListFragment mCategoryListFragment;
    private FrameLayout mCategoryListLayout;
    private LinearLayout mFieldLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_create_account, container, false);

        mAccountName = (IconedEditText) view.findViewById(R.id.text_account_name);
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
                mCategoryListLayout.setVisibility(View.VISIBLE);
                mCategoryListFragment.setSelectedCategory(mSelectedCategory);
            }
        });

        mCategoryListFragment.setCategorySelectedListener(new CategoryListFragment.OnCategorySelectedListener() {
            @Override
            public void onCategorySelected(Category category) {
                mSelectedCategory = category;

                mCategoryImage.setBackground(new CircleDrawable(mSelectedCategory.getColor(), CircleDrawable.FILL));
                mCategoryText.setText(mSelectedCategory.getName());
                mCategoryListLayout.setVisibility(View.GONE);

                ((AbstractActivity) getActivity()).setToolbarColor(mSelectedCategory.getColor());
                mAccountName.setVisibility(View.VISIBLE);
                mAccountName.requestFocus();
                loadFields();
            }
        });

    }

    private void loadFields() {
        final ParseQuery<Field> query = Field.getQuery();
        query.findInBackground(new FindCallback<Field>() {
            @Override
            public void done(List<Field> fields, ParseException e) {
                if (e == null) {
                    mFields = fields;
                }

                populateFields();
            }
        });
    }

    private void populateFields() {
        final int size = mFields.size();
        for (int i = 0; i < size; i++) {
            final Field field = mFields.get(i);
            final IconedEditText fieldEditText = new IconedEditText(getActivity());
            fieldEditText.setHint(field.getName());
            fieldEditText.setmIcon(getResources().getDrawable(R.drawable.icon_check_blue));
            mFieldLayout.addView(fieldEditText);
        }
    }
}
