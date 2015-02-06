// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.volcano.assistant.R;
import com.volcano.assistant.model.Account;
import com.volcano.assistant.util.Utils;
import com.volcano.assistant.widget.RobotoTextView;
import com.volcano.assistant.widget.recyclerview.DividerItemDecoration;
import com.volcano.assistant.widget.recyclerview.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Account list
 */
public class AccountListFragment extends AbstractFragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    private ArrayList<Account> mAccounts = new ArrayList<>();
    private AccountAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_account_list, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.layout_swipe_refresh);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_account);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSwipeRefreshLayout.setColorSchemeColors(R.color.orange, R.color.green, R.color.blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new AccountAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider_line)));
        ItemClickSupport.addTo(mRecyclerView);

        loadAccounts();
    }

    private void loadAccounts() {
        final ParseQuery<Account> query = Account.getQuery();
        query.findInBackground(new FindCallback<Account>() {
            @Override
            public void done(List<Account> accounts, ParseException e) {
                if (e == null) {
                    mAccounts = new ArrayList<>(accounts);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
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
