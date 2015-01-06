package com.volcano.assistant.activity;

import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.volcano.assistant.utils.LogUtils;

/**
 * Each activity should extends this class
 */
public class AbstractActivity extends ActionBarActivity {

    protected final String TAG = LogUtils.makeLogTag(getClass().getName());

    protected void showToast(int id) {
        showToast(getString(id));
    }

    protected void showToast(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}
