// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.activity;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.volcano.esecurebox.ConfigManager;
import com.volcano.esecurebox.Intents;
import com.volcano.esecurebox.Managers;
import com.volcano.esecurebox.R;
import com.volcano.esecurebox.VlApplication;
import com.volcano.esecurebox.backend.AccountManager;
import com.volcano.esecurebox.fragment.AccountListFragment;
import com.volcano.esecurebox.fragment.NavigationFragment;
import com.volcano.esecurebox.fragment.NavigationFragment.NavigationListener;
import com.volcano.esecurebox.model.Category;
import com.volcano.esecurebox.util.BitmapUtils;
import com.volcano.esecurebox.util.PrefUtils;
import com.volcano.esecurebox.util.Utils;
import com.volcano.esecurebox.widget.FloatingActionButton;
import com.volcano.esecurebox.widget.FloatingActionMenu;
import com.volcano.esecurebox.widget.FloatingActionMenu.OnFloatingActionsMenuUpdateListener;

import java.util.ArrayList;

public class MainActivity extends AbstractActivity {

    private BroadcastReceiver mLoginResetReceiver;
    private FloatingActionMenu mCreateAccountMenu;
    private NavigationFragment mNavigationFragment;
    private AccountListFragment mAccountListFragment;
    private View mTransparentBackground;

    private String mCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionbar(toolbar);
        setTitle(R.string.app_name);
        toolbar.setNavigationIcon(R.drawable.icon_drawer);

        final FragmentManager fragmentManager = getFragmentManager();
        mAccountListFragment = (AccountListFragment) fragmentManager.findFragmentById(R.id.fragment_account_list);
        mNavigationFragment = (NavigationFragment) getFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        mCreateAccountMenu = (FloatingActionMenu) findViewById(R.id.menu_create_account);
        mTransparentBackground = findViewById(R.id.background_transparent);
        mNavigationFragment.setup(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        mNavigationFragment.setNavigationListener(new NavigationListener() {
            @Override
            public void onCategorySelected(String categoryId, String title) {
                setTitle(title);
                loadAccounts(categoryId);
                mCreateAccountMenu.collapse();
            }
        });

        if (!Managers.getAccountManager().isLoggedIn()) {
            startActivityForResult(Intents.getSigninIntent(), Intents.REQUEST_CODE_SIGNIN);
        }
        else {
            if (savedInstanceState != null) {
                loadAccounts(savedInstanceState.getString(Intents.KEY_CATEGORY_ID));
            }
            else {
                loadAccounts(PrefUtils.getPref(PrefUtils.PREF_NAVIGATOR_LAST_CATEGORY, ""));
            }
            mNavigationFragment.loadNavigationItems();
            loadFloatingMenuCategories();
        }

        mCreateAccountMenu.setOnFloatingActionsMenuUpdateListener(new OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                mTransparentBackground.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                mTransparentBackground.setVisibility(View.GONE);
            }

            @Override
            public void onMenuIsEmptyOnExpanding() {
                mCreateAccountMenu.collapse();
                loadFloatingMenuCategories();
                Utils.showToast(R.string.toast_category_for_create_account_unavailable);
            }
        });

        mLoginResetReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Managers.getAccountManager().isLoggedIn()) {
                    mNavigationFragment.loadNavigationItems();
                    loadAccounts(PrefUtils.getPref(PrefUtils.PREF_NAVIGATOR_LAST_CATEGORY, ""));
                    loadFloatingMenuCategories();
                }
            }
        };
        AccountManager.registerLoginResetReceiver(this, mLoginResetReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Intents.REQUEST_CODE_SIGNIN) {
            if (resultCode != RESULT_OK) {
                finish();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        loadAccounts(mCategoryId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(Intents.KEY_CATEGORY_ID, mCategoryId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.action_setting){
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        else if (id == R.id.action_signout) {
            new AlertDialogWrapper.Builder(this)
                    .setMessage(R.string.alert_signout)
                    .setTitle(R.string.label_sign_out)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(R.string.label_yes_uppercase, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Managers.getAccountManager().signout();
                            startActivityForResult(Intents.getSigninIntent(), Intents.REQUEST_CODE_SIGNIN);
                        }
                    }).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mLoginResetReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mLoginResetReceiver);
        }
    }

    private void loadFloatingMenuCategories() {
        if (!mCreateAccountMenu.hasMenuItems()) {
            final ArrayList<Category> mCategories = ConfigManager.getCategories();
            final int size = mCategories.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    final Category category = mCategories.get(i);
                    final FloatingActionButton menu = new FloatingActionButton(VlApplication.getInstance());
                    menu.setTitle(category.getName());
                    menu.setType(FloatingActionButton.TYPE_MINI);
                    menu.setIcon(BitmapUtils.getDrawableIdentifier(VlApplication.getInstance(), category.getIconName()));
                    menu.setTag(R.id.category, category);
                    menu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCreateAccountMenu.toggle();

                            final Category category = (Category) menu.getTag(R.id.category);
                            startActivity(Intents.getCreateAccountIntent(category.getObjectId(), category.getColor()));
                        }
                    });
                    mCreateAccountMenu.addMenuItem(menu);
                }
            }
            else {
                ConfigManager.refreshCategories(new ConfigManager.RefreshCategoryCallback() {
                    @Override
                    public void onRefreshComplete(boolean isSuccessful) {
                        if (isSuccessful) {
                            loadFloatingMenuCategories();
                        }
                        else {
                            Utils.showToast(R.string.toast_category_for_create_account_unavailable);
                        }
                    }
                });
            }
        }
    }

    private void loadAccounts(String categoryId) {
        mCategoryId = categoryId;
        mAccountListFragment.loadAccounts(categoryId);
    }
}
