// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.volcano.esecurebox.Intents;
import com.volcano.esecurebox.R;
import com.volcano.esecurebox.fragment.CreateAccountFragment;

/**
 * Create account activity
 */
public final class CreateAccountActivity extends AbstractActivity {
    private CreateAccountFragment mFragment;
    private MenuItem mSaveMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        final Intent intent = getIntent();
        final String categoryColor = intent.getStringExtra(Intents.EXTRA_CATEGORY_COLOR);
        final String categoryId = intent.getStringExtra(Intents.EXTRA_CATEGORY_ID);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);
        setToolbarColor(categoryColor);

        mFragment = (CreateAccountFragment) getFragmentManager().findFragmentById(R.id.fragment_create_account);
        mFragment.setCategoryId(categoryId, categoryColor);
        mFragment.setOnEnableActionsListener(new CreateAccountFragment.OnEnableActionsListener() {
            @Override
            public void OnEnableActions(boolean enable) {
                if (mSaveMenu != null) {
                    mSaveMenu.setEnabled(enable);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_account, menu);
        mSaveMenu = menu.findItem(R.id.action_save);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_save:
                mFragment.save();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected boolean askToFinish() {
        new AlertDialogWrapper.Builder(this)
                .setMessage(R.string.alert_cancel_create_account)
                .setNegativeButton(
                R.string.button_discard_uppercase, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton(R.string.button_keep_editing_uppercase, null)
                .show();

        return false;
    }
}
