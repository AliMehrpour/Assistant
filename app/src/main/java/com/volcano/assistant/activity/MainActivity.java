package com.volcano.assistant.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.volcano.assistant.Intents;
import com.volcano.assistant.Managers;
import com.volcano.assistant.R;
import com.volcano.assistant.backend.AccountManager;
import com.volcano.assistant.fragment.NavigationFragment;


public class MainActivity extends AbstractActivity {

    private AccountManager.LoginResetReceiver mLoginResetReceiver;

    private NavigationFragment mNavigationFragment;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.icon_drawer);
        toolbar.setTitle("Assistant");

        mNavigationFragment = (NavigationFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);

        mNavigationFragment.setup(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        mNavigationFragment.setNavigationListener(new NavigationFragment.NavigationListener() {
            @Override
            public void onNavigationItemSelected(int position) {
                final FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PlaceHolderFragment.newInstance(position + 1))
                        .commit();
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

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.label_navigation_app_accounts);
                break;
            case 2:
                mTitle = getString(R.string.label_navigation_card_accounts);
                break;
            case 3:
                mTitle = getString(R.string.label_navigation_website_accounts);
                break;
        }

        restoreActionBar();
    }

    private void restoreActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(mTitle);
    }

    /**
     * Placeholder fragment
     */
    public static class PlaceHolderFragment extends Fragment {

        public static PlaceHolderFragment newInstance(int position) {
            final PlaceHolderFragment fragment = new PlaceHolderFragment();
            final Bundle args = new Bundle();
            args.putInt(Intents.KEY_SECTION, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) getActivity()).onSectionAttached(getArguments().getInt(Intents.KEY_SECTION));
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }


    }
}
