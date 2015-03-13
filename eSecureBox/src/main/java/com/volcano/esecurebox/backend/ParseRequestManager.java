// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.backend;

import com.parse.ParseQuery;
import com.volcano.esecurebox.util.LogUtils;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Manage Parse requests
 */
public class ParseRequestManager {
    private static final String TAG = LogUtils.makeLogTag(ParseRequestManager.class);

    private final LimitedQueue<MyParseQuery> mRequestQueue = new LimitedQueue<>(20);

    /**
     * Add a {@link com.parse.ParseQuery} to queue with specified tag
     * @param tag The tag
     * @param query Tah query
     */
    public void addRequest(Object tag, ParseQuery query) {
        LogUtils.LogD(TAG, String.format("Add query: %d [X] %s", mRequestQueue.size(), query.getClassName() + "@" + query.hashCode()));
        mRequestQueue.add(new MyParseQuery(query, tag));
    }

    /**
     * Cancel all queries with specified tag
     * @param tag The tag
     */
    public void cancelAll(final Object tag) {
        final ArrayList<MyParseQuery> deleteCandidates = new ArrayList<>();

        final int size = mRequestQueue.size();
        for (int i = 0; i < size; i++) {
            final MyParseQuery query = mRequestQueue.get(i);
            if (query.tag.equals(tag)) {
                deleteCandidates.add(query);
                LogUtils.LogD(TAG, String.format("Cancel query: %d [X] %s", i, query.query.getClassName() + "@" + query.query.hashCode()));
            }
        }

        for (final MyParseQuery query : deleteCandidates) {
            mRequestQueue.remove(query);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Done in a separated because sometimes android raise error when do network
                    // stuff on main thread
                    query.query.cancel();
                }
            }).start();
        }
    }

    /**
     * Remove query from queue
     * @param removedQuery The query
     */
    public void remove(ParseQuery removedQuery) {

        final int size = mRequestQueue.size();
        for (int i = 0; i < size; i++) {
            final MyParseQuery query = mRequestQueue.get(i);
            if (query.query == removedQuery) {
                mRequestQueue.remove(query);
                LogUtils.LogD(TAG, String.format("Remove query: %d [X] %s", i, query.query.getClassName() + "@" + query.query.hashCode()));
            }
        }
    }

    private static class MyParseQuery {
        public final ParseQuery query;
        public final Object tag;

        public MyParseQuery(ParseQuery query, Object tag) {
            this.query = query;
            this.tag = tag;
        }
    }

    public class LimitedQueue<E> extends LinkedList<E> {
        private int limit;

        public LimitedQueue(int limit) {
            this.limit = limit;
        }

        @Override
        public boolean add(E o) {
            super.add(o);
            while (size() > limit) { super.remove(); }
            return true;
        }
    }
}
