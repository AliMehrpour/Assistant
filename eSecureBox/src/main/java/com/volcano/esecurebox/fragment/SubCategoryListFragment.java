// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.volcano.esecurebox.R;
import com.volcano.esecurebox.model.Category;
import com.volcano.esecurebox.model.SubCategory;
import com.volcano.esecurebox.util.BitmapUtils;
import com.volcano.esecurebox.widget.CircleDrawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Sub Category list fragment
 */
public class SubCategoryListFragment extends AbstractFragment {

    private ListView mListView;
    private FrameLayout mProgressLayout;

    private SubCategoryAdapter mAdapter = new SubCategoryAdapter();
    private ArrayList<SubCategory> mSubCategories = new ArrayList<>();
    private OnSubCategoryListener mListener;
    private SubCategory mSelectedSubCategory;
    private String mCategoryId;
    private int mColorStyle = CircleDrawable.FILL;

    public interface OnSubCategoryListener {
        public void onSubCategoriesLoadFailed();
        public void onSubCategoriesEmpty();
        public void onSubCategorySelected(SubCategory subCategory);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sub_category_list, container, false);

        mListView = (ListView) view.findViewById(R.id.list_sub_category);
        mProgressLayout = (FrameLayout) view.findViewById(R.id.layout_progress);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListener != null) {
                    final SubCategory subCategory = (SubCategory) parent.getItemAtPosition(position);
                    mListener.onSubCategorySelected(subCategory);
                }
            }
        });
    }

    public void setCategoryId(String categoryId) {
        mCategoryId = categoryId;
        loadSubCategories();
    }

    public void setSubCategoryListener(OnSubCategoryListener l) {
        mListener = l;
    }

    public void setSubSelectedCategory(SubCategory subCategory) {
        mSelectedSubCategory = subCategory;
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Set default style for sub category color
     * @param style One of {@link com.volcano.esecurebox.widget.CircleDrawable#FILL} or
     *              {@link com.volcano.esecurebox.widget.CircleDrawable#STROKE} values
     */
    public void setDefaultColorStyle(int style) {
        mColorStyle = style;
    }

    private void loadSubCategories() {
        if (mProgressLayout != null) {
            mProgressLayout.setVisibility(View.VISIBLE);
        }
        mSubCategories.clear();
        addCancellingRequest(Category.getInBackground(mCategoryId, new GetCallback<Category>() {
            @Override
            public void done(Category category, ParseException e) {
                if (e == null) {
                    addCancellingRequest(SubCategory.findInBackground(category, new FindCallback<SubCategory>() {
                        @Override
                        public void done(List<SubCategory> subCategories, ParseException e) {
                            if (e == null) {
                                if (mProgressLayout != null) {
                                    mProgressLayout.setVisibility(View.GONE);
                                }
                                if (subCategories.size() > 0) {
                                    mSubCategories.addAll(subCategories);
                                    mAdapter.notifyDataSetChanged();
                                }
                                else {
                                    mListener.onSubCategoriesEmpty();
                                }
                            }
                            else {
                                mListener.onSubCategoriesLoadFailed();
                            }
                        }
                    }));
                }
                else {
                    mListener.onSubCategoriesLoadFailed();
                }
            }
        }));
    }

    private class SubCategoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mSubCategories.size();
        }

        @Override
        public SubCategory getItem(int position) {
            return mSubCategories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SubCategoryListItem view;

            if (convertView != null) {
                view = (SubCategoryListItem) convertView;
            }
            else {
                view = new SubCategoryListItem(parent.getContext());
            }

            view.setSubCategory(getItem(position));

            return view;
        }
    }

    private class SubCategoryListItem extends RelativeLayout {

        private ImageView mCategoryImage;
        private TextView mNameText;
        private ImageView mCheckImage;

        public SubCategoryListItem(Context context) {
            super(context);
            View.inflate(context, R.layout.list_item_sub_category, this);

            mCategoryImage = (ImageView) findViewById(R.id.image_category);
            mNameText = (TextView) findViewById(R.id.text_category);
            mCheckImage = (ImageView) findViewById(R.id.image_check);
        }

        public void setSubCategory(SubCategory subCategory) {

            final boolean selected = mSelectedSubCategory != null &&
                    mSelectedSubCategory.getObjectId().equals(subCategory.getObjectId());

            mCheckImage.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);

            mNameText.setText(subCategory.getName());

            boolean hasIcon = false;
            if (subCategory.hasIcon()) {
                final int resourceId = BitmapUtils.getDrawableIdentifier(getActivity(), subCategory.getIconName());
                if (resourceId != 0) {
                    mCategoryImage.setImageDrawable(getResources().getDrawable(resourceId));
                    hasIcon = true;
                }
            }

            if (!hasIcon) {
                final CircleDrawable drawable = new CircleDrawable();
                drawable.setColor(subCategory.getCategory().getColor());
                drawable.setStyle(selected || mColorStyle == CircleDrawable.FILL ? CircleDrawable.FILL : CircleDrawable.STROKE);
                mCategoryImage.setImageDrawable(drawable);
            }
        }
    }
}
