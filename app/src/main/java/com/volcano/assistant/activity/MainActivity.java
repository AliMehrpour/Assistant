// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant.activity;

import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.volcano.assistant.Intents;
import com.volcano.assistant.Managers;
import com.volcano.assistant.R;
import com.volcano.assistant.backend.AccountManager;
import com.volcano.assistant.fragment.AccountListFragment;
import com.volcano.assistant.fragment.NavigationFragment;

public class MainActivity extends AbstractActivity {

    private AccountManager.LoginResetReceiver mLoginResetReceiver;

    @SuppressWarnings("FieldCanBeLocal")
    private NavigationFragment mNavigationFragment;
    @SuppressWarnings({ "FieldCanBeLocal", "UnusedDeclaration" })
    private AccountListFragment mAccountListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.icon_drawer);
        toolbar.setTitle("Assistant");

        final FragmentManager fragmentManager = getFragmentManager();
        mAccountListFragment = (AccountListFragment) fragmentManager.findFragmentById(R.id.fragment_account_list);
        mNavigationFragment = (NavigationFragment) getFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        mNavigationFragment.setup(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        mNavigationFragment.setNavigationListener(new NavigationFragment.NavigationListener() {
            @Override
            public void onCategorySelected(String categoryId, String title) {
                // Fill Account List Fragment
                setTitle(title);
            }
        });

        mLoginResetReceiver = new AccountManager.LoginResetReceiver() {
            @Override
            public void onReset() {
                invalidateOptionsMenu();
            }
        };
        AccountManager.registerLoginResetReceiver(this, mLoginResetReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem signinItem = menu.findItem(R.id.action_signin);
        signinItem.setVisible(!Managers.getAccountManager().isLoggedIn());

        final MenuItem signoutItem = menu.findItem(R.id.action_signout);
        signoutItem.setVisible(Managers.getAccountManager().isLoggedIn());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

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
}
