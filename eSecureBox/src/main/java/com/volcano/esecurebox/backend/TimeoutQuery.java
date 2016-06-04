// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.backend;

import android.os.Handler;
import android.os.Looper;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.volcano.esecurebox.Managers;
import com.volcano.esecurebox.util.LogUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A timed out {@link com.parse.ParseQuery}.<p>
 * If after a 4 seconds server doesn't get back result, cancel the query and send a {@link com.parse.ParseException}
 * with {@link com.parse.ParseException#TIMEOUT} code and empty message
 */
public final class TimeoutQuery<T extends ParseObject> {
    private static final String TAG = LogUtils.makeLogTag(TimeoutQuery.class);

    private final Object mLock = new Object();
    private final Thread mThread;
    private ParseQuery<T> mQuery;
    private FindCallback<T> mFindCallback;
    private Date mStartTime;

    public TimeoutQuery(ParseQuery<T> query) {
        mQuery = query;
        mThread = new Thread() {
            @Override
            public void run() {
                if (isInterrupted()) {
                    return;
                }

                try {
                    Thread.sleep(Managers.getConfigManager().getQueryTimeOut());
                }
                catch (InterruptedException e) {
                    return;
                }

                cancelQuery();
            }
        };
    }

    /**
     * Retrieves a list of ParseObjects that satisfy this query from the source in a background thread.
     * @param callback The callback
     */
    public void findInBackground(Object tag, final FindCallback<T> callback) {
        if (tag != null) {
            Managers.getParseManager().getRequestManager().addRequest(tag, mQuery);
        }

        mFindCallback = callback;
        mQuery.findInBackground(new FindCallback<T>() {
            @Override
            public void done(List<T> ts, ParseException e) {
                synchronized (mLock) {
                    finishQuery(e != null);
                    mFindCallback.done(ts, e);
                }
            }
        });
        mStartTime = new Date();
        mThread.start();
    }


    /**
     * Constructs a ParseObject whose id is already known by fetching data from the source in a background thread.
     * @param callback The callback
     */
    public void getInBackground(Object tag, final GetCallback<T> callback) {
        if (tag != null) {
            Managers.getParseManager().getRequestManager().addRequest(tag, mQuery);
        }

        mQuery.getFirstInBackground(new GetCallback<T>() {
            @Override
            public void done(T t, ParseException e) {
                synchronized (mLock) {
                    finishQuery(e != null);
                    callback.done(t, e);
                }
            }
        });
        mStartTime = new Date();
        mThread.start();
    }

    private void cancelQuery() {
        synchronized (mLock) {
            if (mQuery != null) {
                final long diff = (System.currentTimeMillis() - mStartTime.getTime());
                Managers.getMixpanelManager().trackFailContactServerEvent(Managers.getConfigManager().isConnected(), mQuery.getClassName(),
                        diff, Managers.getConfigManager().getNetworkInfo());
                LogUtils.LogD(TAG, String.format(Locale.getDefault(), "Query timeout: %d ms [X] %s", diff, mQuery.getClassName() + "@" + mQuery.hashCode()));
                Managers.getParseManager().getRequestManager().remove(mQuery);

                mQuery.cancel();
                mQuery = null;

                if (mFindCallback != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mFindCallback.done(Collections.<T>emptyList(), new ParseException(ParseException.TIMEOUT, ""));
                        }
                    });
                }
            }
        }
    }

    private void finishQuery(boolean error) {
        if (mQuery != null) {
            LogUtils.LogD(TAG, String.format(Locale.getDefault(), "Query finish" + (error ? " with error" : "") + ": %d ms [X] %s", (System.currentTimeMillis() - mStartTime.getTime()), mQuery.getClassName() + "@" + mQuery.hashCode()));
            Managers.getParseManager().getRequestManager().remove(mQuery);
            mQuery = null;
            mThread.interrupt();
        }
    }
}
