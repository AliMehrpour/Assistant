// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.widget.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * A RecyclerView with pull to refresh and empty view capabilities
 */
public class RefreshingRecyclerView extends RecyclerView {
    private View mEmptyView;

    private AdapterDataObserver mObserver;

    public RefreshingRecyclerView(Context context) {
        this(context, null);
    }

    public RefreshingRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshingRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mObserver = new AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkIfEmpty();
            }
        };
    }

    /**
     * Set the empty view
     * @param v The emoty view
     */
    public void setEmptyView(@Nullable View v) {
        mEmptyView = v;
        checkIfEmpty();
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(mObserver);
        }

        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mObserver);
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (mEmptyView != null && (visibility == GONE || visibility == INVISIBLE)) {
            mEmptyView.setVisibility(GONE);
        }
        else {
            checkIfEmpty();
        }
    }

    private void checkIfEmpty() {
        final Adapter adapter = getAdapter();
        if (mEmptyView != null && adapter != null) {
            mEmptyView.setVisibility(adapter.getItemCount() == 0 ? VISIBLE : GONE);
        }
    }
}
