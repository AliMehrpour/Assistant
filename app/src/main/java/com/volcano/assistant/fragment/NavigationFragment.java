// Copyright (c) 2015 Volcano. All rights reserved.

package com.volcano.assistant.fragment;

import android.app.Fragment;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.volcano.assistant.Intents;
import com.volcano.assistant.R;
import com.volcano.assistant.provider.AssistantContract;
import com.volcano.assistant.utils.LogUtils;
import com.volcano.assistant.utils.PrefUtils;

import java.util.List;

/**
 * Navigation layout shown in MainActivity
 */
public final class NavigationFragment extends AbstractFragment {

    private ListView mDrawerListView;
    private DrawerLayout mDrawerLayout;
    private View mFragmentContainer;

    private int mCurrentSelectedPosition;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean mUserLearnNavigator;

    private NavigationListener mListener;

    /**
     * Interface to be implemented to notifies which item selected
     */
    public interface NavigationListener {
        /**
         * Called when a navigation item selected
         * @param position
         */
        public void onNavigationItemSelected(int position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserLearnNavigator = PrefUtils.wasUserLearnNavigator();

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(Intents.KEY_POSITION);
        }

        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_navigation, container, false);

        mDrawerListView = (ListView) view.findViewById(R.id.list_navigation);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        fillNavigationItems();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Intents.KEY_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Initialize navigation drawer
     * @param fragmentId
     * @param drawerLayout
     * @param toolbar
     */
    public void setup(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        mFragmentContainer = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.navigation_statusbar_color));

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout,
                toolbar, R.string.hint_navigation_open, R.string.hint_navigation_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnNavigator) {
                    mUserLearnNavigator = false;
                    PrefUtils.markUserLearnNavigator();
                }
            }
        };

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer
        if (!mUserLearnNavigator) {
            mDrawerLayout.openDrawer(mFragmentContainer);
        }
    }

    /**
     * Set {@link NavigationListener}
     * @param l
     */
    public void setNavigationListener(NavigationListener l) {
        mListener = l;
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainer);
        }
        if (mListener != null) {
            mListener.onNavigationItemSelected(position);
        }
    }

    private void fillNavigationItems() {
        /*
        final String[] projection = new String[] { AssistantContract.Category.CATEGORY_ID, AssistantContract.Category.CATEGORY_NAME};
        final Cursor cursor = getActivity().getContentResolver().query(AssistantContract.Category.CONTENT_URI, projection, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            LogUtils.LogI("DATA", cursor.getString(1));
            cursor.moveToNext();
        }
        */

        final String[] accounts = new String[5];

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Category");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                final int size = parseObjects.size();
                for (int i = 0; i < size; i++) {
                   accounts[i] = parseObjects.get(i).getString("category_name");
                }

                mDrawerListView.setAdapter(new ArrayAdapter<>(
                        getActivity(),
                        R.layout.list_item_navigation,
                        R.id.text_category_name,
                        accounts));

                mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
            }
        });
    }
}
