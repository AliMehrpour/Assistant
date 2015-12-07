// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.volcano.esecurebox.Intents;
import com.volcano.esecurebox.R;
import com.volcano.esecurebox.fragment.EditAccountFragment;
import com.volcano.esecurebox.util.BitmapUtils;
import com.volcano.esecurebox.util.SoftKeyboardUtils;
import com.volcano.esecurebox.widget.RobotoTextView;

/**
 * Edit Account Screen
 */
public final class EditAccountActivity extends AbstractActivity {

    private EditAccountFragment mFragment;
    private MenuItem mSaveMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        final RobotoTextView deleteText = (RobotoTextView) findViewById(R.id.text_delete);
        deleteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.delete();
            }
        });
        mFragment = (EditAccountFragment) getFragmentManager().findFragmentById(R.id.fragment_edit_account);
        mFragment.setOnEnableActionsListener(new EditAccountFragment.OnEnableActionsListener() {
            @Override
            public void OnEnableActions(boolean enable) {
                if (mSaveMenu != null) {
                    mSaveMenu.setEnabled(enable);
                }
                deleteText.setEnabled(enable);
            }
        });

        final Intent intent = getIntent();
        final String accountId = intent.getStringExtra(Intents.EXTRA_ACCOUNT_ID);
        final String categoryColor = intent.getStringExtra(Intents.EXTRA_CATEGORY_COLOR);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionbar(toolbar);
        setToolbarColor(categoryColor);

        mFragment.setAccountId(accountId);
        deleteText.setVisibility(View.VISIBLE);

        final int normalColor = BitmapUtils.getColor(categoryColor);
        final StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[] { -android.R.attr.state_enabled }, BitmapUtils.getColorDrawable(getResources().getColor(android.R.color.darker_gray)));
        stateListDrawable.addState(new int[] { android.R.attr.state_pressed }, BitmapUtils.getColorDrawable(BitmapUtils.getLightenColor(normalColor, .2f)));
        stateListDrawable.addState(new int[] { }, BitmapUtils.getColorDrawable(normalColor));
        BitmapUtils.setBackground(deleteText, stateListDrawable);

        final SoftKeyboardUtils softKeyboardHelper = new SoftKeyboardUtils(findViewById(R.id.root));
        softKeyboardHelper.addSoftKeyboardStateListener(new SoftKeyboardUtils.OnSoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeight) {
                deleteText.setVisibility(View.GONE);
            }

            @Override
            public void onSoftKeyboardClosed() {
                deleteText.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_account, menu);
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
                .setMessage(R.string.alert_cancel_edit_account)
                .setNegativeButton(R.string.button_discard_uppercase, new DialogInterface.OnClickListener() {
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
