// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.widget;

import android.content.Context;
import android.support.v7.widget.SearchView;

public final class VlSearchView extends SearchView {

    private SearchViewListener mSearchViewListener;

    public interface SearchViewListener {
        void onExpanded();
        void onCollapsed();
    }

    public VlSearchView(Context context) {
        super(context);
    }

    public void setOnSearchViewListener(SearchViewListener listener) {
        mSearchViewListener = listener;
    }

    @Override
    public void onActionViewCollapsed() {
        if (mSearchViewListener != null) {
            mSearchViewListener.onCollapsed();
        }
    }

    @Override
    public void onActionViewExpanded() {
        if (mSearchViewListener != null) {
            mSearchViewListener.onExpanded();
        }
    }
}
