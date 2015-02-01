// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.volcano.assistant.model.Category;
import com.volcano.assistant.R;
import com.volcano.assistant.util.Utils;
import com.volcano.assistant.widget.CircleDrawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Category list fragment
 */
public class CategoryListFragment extends AbstractFragment {

    private ListView mListView;
    private FrameLayout mEmptyView;

    private CategoryAdapter mAdapter = new CategoryAdapter();
    private ArrayList<Category> mCategories = new ArrayList<>();
    private OnCategorySelectedListener mListener;
    private Category mSelectedCategory;

    public interface OnCategorySelectedListener {
        public void onCategorySelected(Category category);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_category_list, container, false);

        mListView = (ListView) view.findViewById(R.id.list_category);
        mEmptyView = (FrameLayout) view.findViewById(android.R.id.empty);

        return  view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(mEmptyView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListener != null) {
                    mListener.onCategorySelected((Category) parent.getItemAtPosition(position));
                }
            }
        });
        loadCategories();
    }

    public void setCategorySelectedListener(OnCategorySelectedListener l) {
        mListener = l;
    }

    public void setSelectedCategory(Category category) {
        mSelectedCategory = category;
        mAdapter.notifyDataSetChanged();
    }

    public void loadCategories() {

        final ParseQuery<Category> query = Category.getQuery();
        query.findInBackground(new FindCallback<Category>() {
            @Override
            public void done(List<Category> parseObjects, ParseException e) {
                if (e == null) {
                    mCategories.addAll(parseObjects);
                    mAdapter.notifyDataSetChanged();
                }
                else {
                    Utils.showToast(R.string.toast_load_category_failed);
                }
            }
        });
    }

    private class CategoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mCategories.size();
        }

        @Override
        public Category getItem(int position) {
            return mCategories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CategoryListItem view;

            if (convertView != null) {
                view = (CategoryListItem) convertView;
            }
            else {
                view = new CategoryListItem(parent.getContext());
            }

            view.setCategory(getItem(position));

            return view;
        }
    }

    private class CategoryListItem extends RelativeLayout {

        private ImageView mCategoryImage;
        private TextView mNameText;
        private ImageView mCheckImage;

        public CategoryListItem(Context context) {
            super(context);
            View.inflate(context, R.layout.list_item_category, this);

            mCategoryImage = (ImageView) findViewById(R.id.image_category);
            mNameText = (TextView) findViewById(R.id.text_category);
            mCheckImage = (ImageView) findViewById(R.id.image_check);
        }

        public void setCategory(Category category) {
            mNameText.setText(category.getName());
            final CircleDrawable drawable = new CircleDrawable();
            drawable.setColor(category.getColor());
            if (mSelectedCategory != null && mSelectedCategory.getObjectId().equals(category.getObjectId())) {
                drawable.setStyle(CircleDrawable.FILL);
                mCheckImage.setVisibility(View.VISIBLE);
            }
            else {
                drawable.setStyle(CircleDrawable.STROKE);
                mCheckImage.setVisibility(View.INVISIBLE);
            }
            mCategoryImage.setBackground(drawable);
        }
    }
}
