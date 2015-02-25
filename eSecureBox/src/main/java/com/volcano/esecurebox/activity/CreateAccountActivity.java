// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.activity;

import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.volcano.esecurebox.Intents;
import com.volcano.esecurebox.R;
import com.volcano.esecurebox.fragment.CreateAccountFragment;
import com.volcano.esecurebox.util.BitmapUtils;
import com.volcano.esecurebox.util.SoftKeyboardUtils;
import com.volcano.esecurebox.widget.RobotoTextView;

/**
 * Create account activity
 */
public class CreateAccountActivity extends AbstractActivity {

    private CreateAccountFragment mFragment;
    private MenuItem mSaveMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionbar(toolbar);

        final Intent intent = getIntent();
        final String categoryColor = intent.getStringExtra(Intents.EXTRA_CATEGORY_COLOR);
        final String categoryId = intent.getStringExtra(Intents.EXTRA_CATEGORY_ID);
        final String accountId = intent.getStringExtra(Intents.EXTRA_ACCOUNT_ID);

        setToolbarColor(categoryColor);
        final RobotoTextView deleteText = (RobotoTextView) findViewById(R.id.text_delete);
        deleteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.delete();
            }
        });
        mFragment = (CreateAccountFragment) getFragmentManager().findFragmentById(R.id.fragment_create_account);
        mFragment.setOnEnableActionsListener(new CreateAccountFragment.OnEnableActionsListener() {
            @Override
            public void OnEnableActions(boolean enable) {
                if (mSaveMenu != null) {
                    mSaveMenu.setEnabled(enable);
                }
                deleteText.setEnabled(enable);
            }
        });

        if (savedInstanceState == null) {
            if (categoryId != null) {
                mFragment.setCategoryId(categoryId, categoryColor);
                setTitle(R.string.label_new_account);
                deleteText.setVisibility(View.GONE);
            }
            else {
                mFragment.setAccountId(accountId);
                setTitle(R.string.label_edit_account);
                deleteText.setVisibility(View.VISIBLE);

                final int normalColor = BitmapUtils.getColor(categoryColor);
                final StateListDrawable stateListDrawable = new StateListDrawable();
                stateListDrawable.addState(new int[] { -android.R.attr.state_enabled }, BitmapUtils.getColorDrawablr(getResources().getColor(android.R.color.darker_gray)));
                stateListDrawable.addState(new int[] { android.R.attr.state_pressed }, BitmapUtils.getColorDrawablr(BitmapUtils.getLightenColor(normalColor, .2f)));
                stateListDrawable.addState(new int[] { }, BitmapUtils.getColorDrawablr(normalColor));
                deleteText.setBackground(stateListDrawable);
            }
        }

        final SoftKeyboardUtils softKeyboardHelper = new SoftKeyboardUtils(findViewById(R.id.root));
        softKeyboardHelper.addSoftKeyboardStateListener(new SoftKeyboardUtils.OnSoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeight) {
                if (categoryId == null) {
                    deleteText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSoftKeyboardClosed() {
                if (categoryId == null) {
                    deleteText.setVisibility(View.VISIBLE);
                }
            }
        });
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
            case R.id.action_save:
                mFragment.save();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
