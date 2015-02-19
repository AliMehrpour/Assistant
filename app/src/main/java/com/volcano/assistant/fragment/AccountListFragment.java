// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.volcano.assistant.Intents;
import com.volcano.assistant.R;
import com.volcano.assistant.model.Account;
import com.volcano.assistant.model.Category;
import com.volcano.assistant.util.BitmapUtils;
import com.volcano.assistant.util.Utils;
import com.volcano.assistant.widget.CircleDrawable;
import com.volcano.assistant.widget.RobotoTextView;
import com.volcano.assistant.widget.recyclerview.DividerItemDecoration;
import com.volcano.assistant.widget.recyclerview.ItemClickSupport;
import com.volcano.assistant.widget.recyclerview.RefreshingRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Account list
 */
public class AccountListFragment extends AbstractFragment {

    private RefreshingRecyclerView mRecyclerView;
    private TextView mEmptyLayout;
    private FrameLayout mProgressLayout;

    private ArrayList<Account> mAccounts = new ArrayList<>();
    private AccountAdapter mAdapter = new AccountAdapter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_account_list, container, false);

        mRecyclerView = (RefreshingRecyclerView) view.findViewById(R.id.list_account);
        mEmptyLayout = (TextView) view.findViewById(android.R.id.empty);
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
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider_line)));

        final ItemClickSupport itemClick = ItemClickSupport.addTo(mRecyclerView);
        itemClick.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                final Account account = mAccounts.get(position);
                startActivity(Intents.getEditAccountIntent(account.getObjectId(),account.getSubCategory().getCategory().getColor(), account.getTitle()));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        ItemClickSupport.removeFrom(mRecyclerView);
    }

    /**
     * Load accounts by category id
     */
    public void loadAccounts(final String categoryId) {
        mAccounts.clear();
        mAdapter.notifyDataSetChanged();
        mProgressLayout.setVisibility(View.VISIBLE);
        mEmptyLayout.setVisibility(View.GONE);
        addCancellingRequest(Category.getInBackground(categoryId, new GetCallback<Category>() {
            @Override
            public void done(Category category, ParseException e) {
                loadAccounts(category);
            }
        }));
    }

    /**
     * Load accounts
     */
    private void loadAccounts(Category category) {
        addCancellingRequest(Account.findInBackground(category, new FindCallback<Account>() {
            @Override
            public void done(List<Account> accounts, ParseException e) {
                if (e == null) {
                    mProgressLayout.setVisibility(View.GONE);
                    mAccounts.clear();
                    mAccounts.addAll(accounts);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }));
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
            holder.mDateText.setText(Utils.getTimeSpan(account.getCreateDate()));
            if (account.getSubCategory().hasIcon()) {
                holder.mIconImage.setImageDrawable(getResources().getDrawable(BitmapUtils.getDrawableIdentifier(getActivity(), account.getSubCategory().getIconName())));
            }
            else {
                final String text = account.getTitle().substring(0, 1);
                holder.mIconImage.setImageDrawable(new CircleDrawable(account.getSubCategory().getCategory().getColor(), CircleDrawable.FILL, text));
            }
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
