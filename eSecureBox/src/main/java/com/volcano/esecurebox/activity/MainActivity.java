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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

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
import com.volcano.esecurebox.widget.VlSearchView;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AbstractActivity {

    private BroadcastReceiver mLoginResetReceiver;
    private FloatingActionMenu mCreateAccountMenu;
    private NavigationFragment mNavigationFragment;
    private View mTransparentBackground;

    private FrameLayout mAccountListLayout;
    private FrameLayout mAccountListSearchLayout;
    private AccountListFragment mAccountListFragment;
    private AccountListFragment mAccountListSearchFragment;

    private String mCategoryId;

    private VlSearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFinishIfNotLoggedIn = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);
        setTitle(R.string.app_name);
        toolbar.setNavigationIcon(R.drawable.icon_drawer);

        mAccountListLayout = (FrameLayout) findViewById(R.id.layout_account_list);
        mAccountListSearchLayout = (FrameLayout) findViewById(R.id.layout_account_list_search);

        final FragmentManager fragmentManager = getFragmentManager();
        mAccountListFragment = (AccountListFragment) fragmentManager.findFragmentById(R.id.fragment_account_list);
        mAccountListSearchFragment = (AccountListFragment) fragmentManager.findFragmentById(R.id.fragment_account_list_search);

        mCreateAccountMenu = (FloatingActionMenu) findViewById(R.id.menu_create_account);
        mTransparentBackground = findViewById(R.id.background_transparent);

        mNavigationFragment = (NavigationFragment) getFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        mNavigationFragment.setup(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        mNavigationFragment.setNavigationListener(new NavigationListener() {
            @Override
            public void onCategorySelected(String categoryId, String title) {
                setTitle(title);
                mCreateAccountMenu.collapse();

                if (!mCategoryId.equals(categoryId)) {
                    loadAccounts(categoryId);
                }
            }
        });

        if (!Managers.getAccountManager().isLoggedIn()) {
            startActivityForResult(Intents.getSigninIntent(), Intents.REQUEST_CODE_SIGNIN);
        }
        else {
            String categoryId = null;
            if (savedInstanceState != null) {
                categoryId = savedInstanceState.getString(Intents.KEY_CATEGORY_ID);
            }

            if (TextUtils.isEmpty(categoryId)) {
                categoryId = PrefUtils.getPref(PrefUtils.PREF_NAVIGATOR_LAST_CATEGORY, "");
            }

            loadAccounts(categoryId);
            mNavigationFragment.loadNavigationItems();
            loadFloatingMenuCategories();
        }

        mCreateAccountMenu.collapse();
        mCreateAccountMenu.setOnFloatingActionsMenuUpdateListener(new OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                mTransparentBackground.setVisibility(VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                mTransparentBackground.setVisibility(GONE);
            }

            @Override
            public void onMenuIsEmptyOnExpanding() {
                loadFloatingMenuCategories();
                if (ConfigManager.getCategories().size() == 0) {
                    Utils.showToast(R.string.toast_category_try_get_category);
                }
                else {
                    mCreateAccountMenu.expand();
                }
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

        mSearchView = (VlSearchView) menu.findItem(R.id.menu_search).getActionView();
        mSearchView.setQueryHint("Hint!!");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!mSearchView.isIconified()) {
                    // Load filter account
                }

                return false;
            }
        });

        mSearchView.setOnSearchViewListener(new VlSearchView.SearchViewListener() {
            @Override
            public void onExpanded() {
                mCreateAccountMenu.setVisibility(GONE);
                mAccountListLayout.setVisibility(GONE);
                mAccountListSearchLayout.setVisibility(VISIBLE);
            }

            @Override
            public void onCollapsed() {
                mCreateAccountMenu.setVisibility(VISIBLE);
                mAccountListLayout.setVisibility(VISIBLE);

                mAccountListSearchLayout.setVisibility(GONE);
                mAccountListSearchFragment.clear();
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.menu_setting){
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        else if (id == R.id.menu_sign_out) {
            new AlertDialogWrapper.Builder(this)
                    .setMessage(R.string.alert_signout)
                    .setTitle(R.string.label_sign_out)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(R.string.button_signout_uppercase, new DialogInterface.OnClickListener() {
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

        Managers.getMixpanelManager().flush();

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
                            Managers.getMixpanelManager().trackCreateItemEvent(category.getName());
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
                            Utils.showToast(R.string.toast_category_create_account_unavailable);
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
