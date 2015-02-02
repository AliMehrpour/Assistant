// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.fragment;

import android.app.Fragment;

import com.volcano.assistant.util.LogUtils;

/**
 * An abstract fragment that all fragment should inherit from
 */
public class AbstractFragment extends Fragment {

    protected final String TAG = LogUtils.makeLogTag(getClass().getName());
}
