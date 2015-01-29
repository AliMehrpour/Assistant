package com.volcano.assistant.activity;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.volcano.assistant.R;
import com.volcano.assistant.util.BitmapUtils;
import com.volcano.assistant.util.LogUtils;

/**
 * Each activity should extends this class
 */
public class AbstractActivity extends ActionBarActivity {

    protected final String TAG = LogUtils.makeLogTag(getClass().getName());

    protected void showToast(int resId) {
        showToast(getString(resId));
    }

    protected void showToast(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setTitle(CharSequence title) {
        setTitle(title);
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

    public void setToolbarColor(String color) {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final int realColor = Color.parseColor(String.format("#%s", color));
        if (toolbar != null) {
            toolbar.setBackgroundColor(realColor);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(BitmapUtils.darkenColor(realColor, 0.8f));
        }
    }
}
