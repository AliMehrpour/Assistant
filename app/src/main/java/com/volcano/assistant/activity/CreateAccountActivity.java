// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.activity;

import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.volcano.assistant.R;
import com.volcano.assistant.fragment.CategoryListFragment;

/**
 * Create account activity
 */
public class CreateAccountActivity extends AbstractActivity {

    private Toolbar mToolbar;
    private MenuItem mSaveMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextAppearance(this, R.style.Toolbar_Title);
        mToolbar.setPadding (0, 0, getResources().getDimensionPixelSize(R.dimen.margin_10), 0);
        
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_account, menu);
        mSaveMenu = menu.findItem(R.id.action_save);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home :
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
