// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
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
    @SuppressWarnings("FieldCanBeLocal")
    private NavigationFragment mNavigationFragment;
    private AccountListFragment mAccountListFragment;
    private View mTransparentBackground;

    private String mCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_drawer);
        toolbar.setTitle(R.string.app_name);

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
            }
        });

        if (!Managers.getAccountManager().isLoggedIn()) { // TODO: check data has been initialized or not too
            startActivity(Intents.getSigninIntent());
        }
        else {
            mNavigationFragment.loadNavigationItems();
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
                invalidateOptionsMenu();
            }
        };
        AccountManager.registerLoginResetReceiver(this, mLoginResetReceiver);

        loadFloatingMenuCategories();

        if (savedInstanceState != null) {
            loadAccounts(PrefUtils.getNavigatorLastCategory());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra(Intents.EXTRA_RESET, false)) {
            loadAccounts(mCategoryId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem signinItem = menu.findItem(R.id.action_signin);
        signinItem.setVisible(!Managers.getAccountManager().isLoggedIn());

        final MenuItem signoutItem = menu.findItem(R.id.action_signout);
        signoutItem.setVisible(Managers.getAccountManager().isLoggedIn());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.action_signin) {
            startActivity(Intents.getSigninIntent());
            return true;
        }
        else if (id == R.id.action_signout) {
            Managers.getAccountManager().signout();
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
        // TODO: It's needed show progress or not !?
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
                                mCategoryId = category.getObjectId();
                                startActivity(Intents.getCreateAccountIntent(category.getObjectId(), category.getColor()));
                            }
                        });
                        mCreateAccountMenu.addMenuItem(menu);
                    }
                }
            }
        }));

    }

    private void loadAccounts(String categoryId) {
        PrefUtils.setNavigatorLastCategory(categoryId);
        mAccountListFragment.loadAccounts(categoryId);
    }
}
