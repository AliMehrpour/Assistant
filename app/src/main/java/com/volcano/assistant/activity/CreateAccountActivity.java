// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.volcano.assistant.Intents;
import com.volcano.assistant.R;
import com.volcano.assistant.fragment.CreateAccountFragment;

/**
 * Create account activity
 */
public class CreateAccountActivity extends AbstractActivity {

    private CreateAccountFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionbar(toolbar);

        final Intent intent = getIntent();
        final String categoryColor = intent.getStringExtra(Intents.EXTRA_CATEGORY_COLOR);
        final String categoryId = intent.getStringExtra(Intents.EXTRA_CATEGORY_ID);

        setToolbarColor(categoryColor);
        mFragment = (CreateAccountFragment) getFragmentManager().findFragmentById(R.id.fragment_create_account);

        if (savedInstanceState == null) {
            mFragment.setCategoryId(categoryId, categoryColor);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_account, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home :
                finish();
                return true;
            case R.id.action_save:
                mFragment.save();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
