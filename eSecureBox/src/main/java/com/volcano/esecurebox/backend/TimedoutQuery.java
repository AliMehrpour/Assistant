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

/**
 * A timed out {@link com.parse.ParseQuery}.<p>
 * If after a 4 seconds server doesn't get back result, cancel the query and send a {@link com.parse.ParseException}
 * with {@link com.parse.ParseException#TIMEOUT} code and empty message
 */
public final class TimedoutQuery<T extends ParseObject> {
    private static final String TAG = LogUtils.makeLogTag(TimedoutQuery.class);

    private final Object mLock = new Object();
    private final Thread mThread;
    private ParseQuery<T> mQuery;
    private FindCallback<T> mFindCallback;
    private Object mTag;
    private Date mStartTime;

    public TimedoutQuery(ParseQuery<T> query) {
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
            mTag = tag;
            Managers.getParseManager().getRequestManager().addRequest(tag, mQuery);
        }

        mFindCallback = callback;
        mQuery.findInBackground(new FindCallback<T>() {
            @Override
            public void done(List<T> ts, ParseException e) {
                synchronized (mLock) {
                    successQuery();
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
            mTag = tag;
            Managers.getParseManager().getRequestManager().addRequest(tag, mQuery);
        }

        mQuery.getFirstInBackground(new GetCallback<T>() {
            @Override
            public void done(T t, ParseException e) {
                synchronized (mLock) {
                    successQuery();
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
                LogUtils.LogD(TAG, String.format("Query timeout: %d ms [X] %s", (System.currentTimeMillis() - mStartTime.getTime()), mQuery.toString()));

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

    private void successQuery() {
        if (mQuery != null) {
            LogUtils.LogD(TAG, String.format("Query finish: %d ms [X] %s", (System.currentTimeMillis() - mStartTime.getTime()), mQuery.toString()));

            mThread.interrupt();
            mQuery = null;
        }
    }
}
