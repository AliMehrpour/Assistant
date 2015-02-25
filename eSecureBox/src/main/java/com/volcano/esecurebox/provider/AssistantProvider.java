// Copyright (c) 2015 Volcano. All rights reserved.

package com.volcano.esecurebox.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.volcano.esecurebox.util.LogUtils;

/**
 * Provider that stores {@link com.volcano.esecurebox.provider.AssistantContract} data
 */
public class AssistantProvider extends ContentProvider {
    private final static String TAG = LogUtils.makeLogTag(AssistantProvider.class);

    private AssistantDatabase mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int CATEGORY = 100;
    private static final int CATEGORY_ID = 101;

    /**
     * Build and return a {@link UriMatcher} that catches all variations supported by this provider
     */
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AssistantContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "category", CATEGORY);
        matcher.addURI(authority, "category/#", CATEGORY_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new AssistantDatabase(getContext());
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        // Check columns

        final int matcher = sUriMatcher.match(uri);
        switch (matcher) {
            case CATEGORY :
                builder.setTables(AssistantDatabase.Tables.CATEGORY);
                break;
            case CATEGORY_ID:
                builder.setTables(AssistantDatabase.Tables.CATEGORY);
                builder.appendWhere(AssistantContract.Category.CATEGORY_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new UnsupportedOperationException("Unknown query uri: " + uri);
        }

        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CATEGORY:
                return AssistantContract.Category.CONTENT_TYPE;
            case CATEGORY_ID:
                return AssistantContract.Category.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        LogUtils.LogV(TAG, "Insert(uri=" + uri + ", values=" + values.toString() + ")");

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CATEGORY:
                db.insertOrThrow(AssistantDatabase.Tables.CATEGORY, null, values);
                // Notify
                return AssistantContract.Category.buildCategoryUri(values.getAsString(AssistantContract.Category.CATEGORY_ID));
            default:
                throw new UnsupportedOperationException("Unknown insert uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
