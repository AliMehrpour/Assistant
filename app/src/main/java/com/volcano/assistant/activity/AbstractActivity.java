// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.volcano.assistant.R;
import com.volcano.assistant.util.BitmapUtils;
import com.volcano.assistant.util.LogUtils;
import com.volcano.assistant.util.Utils;

/**
 * Each activity should extends this class
 */
public class AbstractActivity extends ActionBarActivity {

    protected final String TAG = LogUtils.makeLogTag(getClass().getName());

    @Override
    public void onResume() {
        super.onResume();

        // TODO: temporary disabled
        // If the user is offline, let them know they are not connected
        //final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //final NetworkInfo ni = cm.getActiveNetworkInfo();
        //if ((ni == null) || (!ni.isConnected())) {
        //    Utils.showToast(R.string.toast_device_offline_message);
        //}
    }
    public void setTitle(int resId) {
        setTitle(getString(resId));
    }

    protected void setTitle(String title) {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    protected void setActionbar(Toolbar toolbar) {
        toolbar.setTitleTextAppearance(this, R.style.Toolbar_Title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @SuppressLint("NewApi")
    public void setToolbarColor(String color) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final int realColor = Color.parseColor(String.format("#%s", color));
        if (toolbar != null) {
            toolbar.setBackgroundColor(realColor);
        }

        if (Utils.hasLollipopApi()) {
            getWindow().setStatusBarColor(BitmapUtils.darkenColor(realColor, 0.8f));
        }
    }
}
