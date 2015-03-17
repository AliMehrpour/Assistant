// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.volcano.esecurebox.model.Category;
import com.volcano.esecurebox.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration manager
 */
public class ConfigManager {
    private static final String TAG = LogUtils.makeLogTag(ConfigManager.class);
    public static final boolean IS_DOGFOOD_BUILD = false;

    // Assume connected to a mobile network of unknown type
    private final ConnectivityManager mConnMgr;
    private boolean mConnected;
    private int mConnectionType = ConnectivityManager.TYPE_MOBILE;
    private int mMobileNetworkType = TelephonyManager.NETWORK_TYPE_UNKNOWN;

    private static final ArrayList<Category> mCategories = new ArrayList<>();
    private static boolean mFetchCategoryInProgress = false;

    /**
     * Callback interface to be notified after an on demand category refresh
     */
    public interface RefreshCategoryCallback {
        /**
         * @param isSuccessful True if categories were successfully refreshed
         */
        public void onRefreshComplete(boolean isSuccessful);
    }

    public ConfigManager() {
        final Context context = VlApplication.getInstance();
        mConnMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        context.registerReceiver(new ConnectivityReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /**
     * @return Categories
     */
    public static ArrayList<Category> getCategories() {
        return mCategories;
    }

    /**
     * Refresh categories on demand
     * @param callback The {@link RefreshCategoryCallback}
     */
    public static void refreshCategories(final RefreshCategoryCallback callback) {
        if (!mFetchCategoryInProgress) {
            mFetchCategoryInProgress = true;
            Category.findInBackground(null, new FindCallback<Category>() {
                @Override
                public void done(List<Category> categories, ParseException e) {
                    mFetchCategoryInProgress = false;

                    if (e == null) {
                        mCategories.clear();
                        mCategories.addAll(categories);
                        callback.onRefreshComplete(true);
                    }
                    else {
                        callback.onRefreshComplete(false);
                    }
                }
            });
        }
    }

    /**
     * @return True if network connectivity exist, otherwise false
     */
    public boolean isConnected() {
        return mConnected;
    }

    /**
     * @return True if network is fast, otherwise false
     */
    public boolean isNetworkFast() {
        boolean isFast = false;

        if (mConnectionType == ConnectivityManager.TYPE_WIFI ||
                mConnectionType == ConnectivityManager.TYPE_ETHERNET ||
                mConnectionType == ConnectivityManager.TYPE_WIMAX ||
                (mConnectionType == ConnectivityManager.TYPE_MOBILE &&
                        (mMobileNetworkType == TelephonyManager.NETWORK_TYPE_EVDO_0 ||
                                mMobileNetworkType == TelephonyManager.NETWORK_TYPE_HSPAP ||
                                mMobileNetworkType == TelephonyManager.NETWORK_TYPE_LTE))) {
            isFast = true;
        }

        return isFast;
    }

    /**
     * @return Return last connected network info.
     */
    public String getNetworkInfo() {
        return String.format("Network Info: Connected = %b, Type = %s, Subtype = %s", mConnected, mConnectionType, mMobileNetworkType);
    }

    /**
     * @return The query time out of Parse Queries
     */
    public int getQueryTimeOut() {
        return !mConnected || isNetworkFast() ? 5000 : 10000;
    }

    private class ConnectivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final NetworkInfo netInfo = mConnMgr.getActiveNetworkInfo();
            final boolean connected = (netInfo != null && netInfo.isConnected());
            final int connectionType = (connected ? netInfo.getType() : -1);    // There is no "no connection" type, so use -1
            final int connectionSubType = (connected ? netInfo.getSubtype() : TelephonyManager.NETWORK_TYPE_UNKNOWN);

            if (connected != mConnected || connectionType != mConnectionType || connectionSubType != mMobileNetworkType) {
                Log.i(TAG, String.format("Network change: connected = %b, type = %d, subtype = %d", connected, connectionType, connectionSubType));

                mConnected = connected;
                mConnectionType = connectionType;
                mMobileNetworkType = connectionSubType;
            }
        }
    }
}
