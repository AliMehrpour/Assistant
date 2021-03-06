// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.fragment;

import android.app.Fragment;

import com.parse.ParseQuery;
import com.volcano.esecurebox.Managers;
import com.volcano.esecurebox.util.LogUtils;

/**
 * An abstract fragment that all fragment should inherit from
 */
public class AbstractFragment extends Fragment {
    protected final String TAG = LogUtils.makeLogTag(getClass().getName());

    @Override
    public void onDestroy() {
        super.onDestroy();

        Managers.getParseManager().getRequestManager().cancelAll(this);
    }

    protected void addCancellingRequest(ParseQuery query) {
        Managers.getParseManager().getRequestManager().addRequest(this, query);
    }
}
