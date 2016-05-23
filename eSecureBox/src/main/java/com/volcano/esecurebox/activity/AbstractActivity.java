// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.parse.ParseQuery;
import com.volcano.esecurebox.Managers;
import com.volcano.esecurebox.R;
import com.volcano.esecurebox.util.BitmapUtils;
import com.volcano.esecurebox.util.LogUtils;
import com.volcano.esecurebox.util.Utils;

/**
 * Each activity should extends this class
 */
public class AbstractActivity extends AppCompatActivity {
    protected final String TAG = LogUtils.makeLogTag(getClass().getName());
    protected boolean mFinishIfNotLoggedIn = true;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mFinishIfNotLoggedIn && !Managers.getAccountManager().isLoggedIn()) {
            finish();
        }
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Managers.getParseManager().getRequestManager().cancelAll(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (askToFinish()) {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void setToolbarColor(String color) {
        setToolbarColor(Color.parseColor(String.format("#%s", color)));
    }

    @SuppressLint("NewApi")
    protected void setToolbarColor(int color) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setBackgroundColor(color);
        }

        if (Utils.hasLollipopApi()) {
            getWindow().setStatusBarColor(BitmapUtils.getDarkenColor(color, 0.8f));
        }
    }

    /**
     * Set the activity title
     * @param resId The resource id
     */
    public void setTitle(int resId) {
        setTitle(getString(resId));
    }

    /**
     * @return The toolbar object
     */
    public Toolbar getToolbar() {
        return mToolbar;
    }

    protected void setTitle(String title) {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    protected void setActionBar(Toolbar toolbar) {
        mToolbar = toolbar;
        toolbar.setTitleTextAppearance(this, R.style.Toolbar_Title);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @SuppressWarnings("unused")
    protected void addCancellingRequest(ParseQuery query) {
        Managers.getParseManager().getRequestManager().addRequest(this, query);
    }

    /**
     * @return True if activity can be finished by Home Up button on Toolbar or Back key
     */
    protected boolean askToFinish() {
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return !(keyCode == KeyEvent.KEYCODE_BACK && askToFinish()) || super.onKeyDown(keyCode, event);

    }
}
