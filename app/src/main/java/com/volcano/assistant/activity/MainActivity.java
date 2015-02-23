// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.activity;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.volcano.assistant.Intents;
import com.volcano.assistant.Managers;
import com.volcano.assistant.R;
import com.volcano.assistant.VlApplication;
import com.volcano.assistant.backend.AccountManager;
import com.volcano.assistant.fragment.AccountListFragment;
import com.volcano.assistant.fragment.NavigationFragment;
import com.volcano.assistant.model.Category;
import com.volcano.assistant.util.BitmapUtils;
import com.volcano.assistant.util.PrefUtils;
import com.volcano.assistant.widget.FloatingActionButton;
import com.volcano.assistant.widget.FloatingActionMenu;

import java.util.List;

public class MainActivity extends AbstractActivity {

    private AccountManager.LoginResetReceiver mLoginResetReceiver;
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
        mNavigationFragment.setNavigationListener(new NavigationFragment.NavigationListener() {
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
                loadAccounts(PrefUtils.getNavigatorLastCategory());
            }
            mNavigationFragment.loadNavigationItems();
            loadFloatingMenuCategories();
        }

        mCreateAccountMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                mTransparentBackground.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                mTransparentBackground.setVisibility(View.GONE);
            }
        });
        mLoginResetReceiver = new AccountManager.LoginResetReceiver() {
            @Override
            public void onReset() {
                if (Managers.getAccountManager().isLoggedIn()) {
                    loadAccounts(PrefUtils.getNavigatorLastCategory());
                    mNavigationFragment.loadNavigationItems();
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
            new AlertDialog.Builder(this)
                    .setMessage(R.string.alert_signout)
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Managers.getAccountManager().signout();
                            startActivityForResult(Intents.getSigninIntent(), Intents.REQUEST_CODE_SIGNIN);
                        }
                    })
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mLoginResetReceiver != null) {
            unregisterReceiver(mLoginResetReceiver);
        }
    }

    private void loadFloatingMenuCategories() {
        if (!mCreateAccountMenu.hasMenuItems()) {
            addCancellingRequest(Category.findInBackground(new FindCallback<Category>() {
                @Override
                public void done(final List<Category> categories, ParseException e) {
                    if (e == null) {
                        final int size = categories.size();
                        for (int i = 0; i < size; i++) {
                            final Category category = categories.get(i);
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
                }
            }));
        }
    }

    private void loadAccounts(String categoryId) {
        mCategoryId = categoryId;
        mAccountListFragment.loadAccounts(categoryId);
    }
}
