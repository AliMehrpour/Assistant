// Copyright (c) 2015 Volcano. All rights reserved.

package com.volcano.assistant.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.volcano.assistant.Intents;
import com.volcano.assistant.Managers;
import com.volcano.assistant.R;
import com.volcano.assistant.model.Category;
import com.volcano.assistant.model.User;
import com.volcano.assistant.util.BitmapUtils;
import com.volcano.assistant.util.PrefUtils;
import com.volcano.assistant.widget.CircleDrawable;
import com.volcano.assistant.widget.RobotoTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Navigation layout shown in MainActivity
 */
public final class NavigationFragment extends AbstractFragment {

    private ListView mDrawerListView;
    private DrawerLayout mDrawerLayout;
    private View mFragmentContainer;

    private ActionBarDrawerToggle mDrawerToggle;
    private int mCurrentSelectedPosition;
    private boolean mUserLearnNavigator;

    private final ArrayList<NavigationItem> mNavigationItems = new ArrayList<>();
    private NavigationAdapter mAdapter = new NavigationAdapter();

    private NavigationListener mListener;

    /**
     * Interface to be implemented to notifies which item selected
     */
    public interface NavigationListener {
        /**
         * Called when a navigation item selected
         */
        public void onCategorySelected(String categoryId, String title);
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

        final RobotoTextView usernameText = (RobotoTextView) view.findViewById(R.id.text_username);
        final RobotoTextView emailText = (RobotoTextView) view.findViewById(R.id.text_email);
        mDrawerListView = (ListView) view.findViewById(R.id.list_navigation);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
                final NavigationItem item = (NavigationItem) mDrawerListView.getItemAtPosition(position);
                mListener.onCategorySelected(item.id, item.title);
            }
        });
        mAdapter = new NavigationAdapter();
        mDrawerListView.setAdapter(mAdapter);

        if (Managers.getAccountManager().isLoggedIn()) {
            final User user = Managers.getAccountManager().getCurrentUser();
            usernameText.setText(user.getName());
            emailText.setText(user.getEmail());
        }

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
     * @param fragmentId The fragment id
     * @param drawerLayout The drawer layout
     * @param toolbar The toolbar
     */
    public void setup(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        mFragmentContainer = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        mDrawerLayout.setDrawerShadow(R.drawable.shadow_drawer, GravityCompat.START);
        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.navigation_statusbar_color));

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout,
                toolbar, R.string.hint_navigation_open, R.string.hint_navigation_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
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
     * @param l The listener
     */
    public void setNavigationListener(NavigationListener l) {
        mListener = l;
    }

    /**
     * Load navigation items
     */
    public void loadNavigationItems() {
        Category.findInBackground(new FindCallback<Category>() {
            @Override
            public void done(List<Category> categories, ParseException e) {
                final int size = categories.size();
                for (int i = 0; i < size; i++) {
                    final Category category = categories.get(i);

                    final NavigationItem item = new NavigationItem();
                    item.id = category.getObjectId();
                    item.color = category.getColor();
                    item.title = category.getName();
                    item.counter = 0;

                    mNavigationItems.add(item);
                }

                mAdapter.notifyDataSetChanged();
                selectItem(mCurrentSelectedPosition);
            }
        });
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        mAdapter.resetCheck();
        if (mDrawerListView != null) {
            mAdapter.setChecked(mCurrentSelectedPosition, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainer);
        }
    }

    private class NavigationAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mNavigationItems.size();
        }

        @Override
        public NavigationItem getItem(int position) {
            return mNavigationItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NavigationListItem view;

            if (convertView != null) {
                view = (NavigationListItem) convertView;
            }
            else {
                view = new NavigationListItem(getActivity());
            }

            view.setNavigationItem(getItem(position));

            return view;
        }

        public void setChecked(int pos, boolean checked) {
            if (mNavigationItems.size() > 0) {
                mNavigationItems.get(pos).checked = checked;
                notifyDataSetChanged();
            }
        }

        public void resetCheck() {
            final int size = mNavigationItems.size();
            for (int i = 0; i < size; i++) {
                mNavigationItems.get(i).checked = false;
            }
            this.notifyDataSetChanged();
        }
    }

    private class NavigationListItem extends RelativeLayout {
        private ImageView mIcon;
        private RobotoTextView mTitleText;
        private RobotoTextView mCounterText;

        public NavigationListItem(Context context) {
            super(context);
            View.inflate(context, R.layout.list_item_navigation, this);

            mIcon = (ImageView) findViewById(R.id.image_icon);
            mTitleText = (RobotoTextView) findViewById(R.id.text_title);
            mCounterText = (RobotoTextView) findViewById(R.id.text_counter);
        }

        public void setNavigationItem(NavigationItem item) {
            mIcon.setBackground(new CircleDrawable(BitmapUtils.getColor(item.color), CircleDrawable.FILL));
            mTitleText.setText(item.title);
            if (item.counter > 0) {
                mCounterText.setText((item.counter > 99) ? getString(R.string.label_ninety_nine_plus) : item.counter + "");
                mCounterText.setVisibility(View.VISIBLE);
            }
            else {
                mCounterText.setText(null);
                mCounterText.setVisibility(View.INVISIBLE);
            }

            if (item.checked) {
                setBackgroundColor(getResources().getColor(R.color.grey_5));
            }
            else {
                setBackgroundColor(getResources().getColor(android.R.color.transparent));
            }
        }
    }

    private class NavigationItem {
        public String id;
        public String title;
        public String color;
        public int counter;
        public boolean checked;
    }
}
