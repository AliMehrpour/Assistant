// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.volcano.esecurebox.Intents;
import com.volcano.esecurebox.R;
import com.volcano.esecurebox.model.Account;
import com.volcano.esecurebox.model.Category;
import com.volcano.esecurebox.util.BitmapUtils;
import com.volcano.esecurebox.util.CompatUtils;
import com.volcano.esecurebox.util.Utils;
import com.volcano.esecurebox.widget.CircleDrawable;
import com.volcano.esecurebox.widget.RobotoTextView;
import com.volcano.esecurebox.widget.recyclerview.DividerItemDecoration;
import com.volcano.esecurebox.widget.recyclerview.RefreshingRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Account list
 */
public class AccountListFragment extends AbstractFragment {

    private RefreshingRecyclerView mRecyclerView;
    private FrameLayout mEmptyLayout;
    private RobotoTextView mEmptyText;
    private FrameLayout mProgressLayout;

    private boolean mInitialized = false;
    private ArrayList<Account> mAccounts = new ArrayList<>();
    private AccountAdapter mAdapter = new AccountAdapter();
    private String mCategoryId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_account_list, container, false);

        mRecyclerView = (RefreshingRecyclerView) view.findViewById(R.id.list_account);
        mEmptyLayout = (FrameLayout) view.findViewById(android.R.id.empty);
        mEmptyText = (RobotoTextView) view.findViewById(R.id.text_empty);
        mProgressLayout = (FrameLayout) view.findViewById(R.id.layout_progress);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setEmptyView(mEmptyLayout);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(CompatUtils.getDrawable(R.drawable.shape_divider_line)));

        mEmptyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mInitialized) {
                    loadAccounts(mCategoryId);
                }
            }
        });
    }

    /**
     * Load accounts by category id
     */
    public void loadAccounts(final String categoryId) {
        mInitialized = false;
        mCategoryId = categoryId;
        mAccounts.clear();
        mAdapter.notifyDataSetChanged();
        mProgressLayout.setVisibility(View.VISIBLE);
        mEmptyLayout.setVisibility(View.GONE);
        Category.getInBackground(this, categoryId, new GetCallback<Category>() {
            @Override
            public void done(Category category, ParseException e) {
                if (e == null) {
                    Account.findInBackground(AccountListFragment.this, category, new FindCallback<Account>() {
                        @Override
                        public void done(List<Account> accounts, ParseException e) {
                            if (e == null) {
                                mInitialized = true;
                                mProgressLayout.setVisibility(View.GONE);
                                if (accounts.size() > 0) {
                                    mAccounts.clear();
                                    mAccounts.addAll(accounts);
                                    mAdapter.notifyDataSetChanged();
                                }
                                else {
                                    setErrorState();
                                }
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

    public void search(final String query) {

    }

    public void clear() {
        // TODO what if cancel while loading is in progress?
        mInitialized = false;
        mAccounts.clear();
        mAdapter = null;
        mCategoryId = null;
    }

    private void setErrorState() {
        mProgressLayout.setVisibility(View.GONE);
        mEmptyLayout.setVisibility(View.VISIBLE);
        mEmptyText.setText(mInitialized ? R.string.alert_no_account : R.string.alert_load_accounts);
        mEmptyText.setCompoundDrawablesWithIntrinsicBounds(0,
                mInitialized ? R.drawable.icon_account_list_empty : R.drawable.icon_no_network_large, 0, 0);
    }

    private class AccountAdapter extends RecyclerView.Adapter<AccountViewHolder> {

        @Override
        public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_account, parent, false);
            return new AccountViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AccountViewHolder holder, int position) {
            final Account account = mAccounts.get(position);
            holder.mTitleText.setText(account.getTitle());
            holder.mDateText.setText(Utils.getTimeSpan(account.getCreatedAt()));
            if (account.getSubCategory().hasIcon()) {
                holder.mIconImage.setImageDrawable(CompatUtils.getDrawable(BitmapUtils.getDrawableIdentifier(getActivity(), account.getSubCategory().getIconName())));
            }
            else {
                final String text = account.getTitle().substring(0, 1);
                holder.mIconImage.setImageDrawable(new CircleDrawable(account.getSubCategory().getCategory().getColor(), CircleDrawable.FILL, text));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(Intents.getDisplayAccountIntent(account.getObjectId(), account.getSubCategory().getCategory().getColor(), account.getTitle()));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mAccounts.size();
        }
    }

    private static class AccountViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mIconImage;
        private final RobotoTextView mTitleText;
        private final RobotoTextView mDateText;
        @SuppressWarnings("UnusedDeclaration")
        private final ImageView mFavoriteImage;

        public AccountViewHolder(View view) {
            super(view);

            mIconImage = (ImageView) view.findViewById(R.id.image_icon);
            mTitleText = (RobotoTextView) view.findViewById(R.id.text_account_title);
            mDateText = (RobotoTextView) view.findViewById(R.id.text_date);
            mFavoriteImage = (ImageView) view.findViewById(R.id.image_favorite);
        }
    }
}
