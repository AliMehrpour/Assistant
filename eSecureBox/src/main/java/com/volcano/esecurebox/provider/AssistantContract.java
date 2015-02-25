// Copyright (c) 2015 Volcano. All rights reserved.

package com.volcano.esecurebox.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract class for interacting with {@link AssistantProvider} and
 * {@link AssistantDatabase}
 */
public final class AssistantContract {

    interface CategoryColumns {
        String CATEGORY_ID = "category_id";
        String CATEGORY_NAME = "category_name";
        String CATEGORY_DESCRIPTION = "category_description";
    }

    public static final String CONTENT_AUTHORITY = "com.volcano.esecurebox";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String BASE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.esecurebox.";
    public static final String BASE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.esecurebox.";

    private static final String PATH_CATEGORY = "category";

    public static class Category implements CategoryColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();
        public static final String CONTENT_TYPE = BASE_CONTENT_TYPE + "category";
        public static final String CONTENT_ITEM_TYPE = BASE_CONTENT_ITEM_TYPE + "category";

        public static Uri buildCategoryUri(String categoryId) {
            return  CONTENT_URI.buildUpon().appendPath(categoryId).build();
        }

        public static String getCategoryId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    private AssistantContract() {

    }
}
