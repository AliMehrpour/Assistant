package com.volcano.assistant.model;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.volcano.assistant.backend.ParseManager;
import com.volcano.assistant.util.LogUtils;

import java.util.List;

/**
 * A sub category field
 */
@ParseClassName("SubCategoryField")
public class SubCategoryField extends ParseObject {
    private static final String TAG = LogUtils.makeLogTag(SubCategory.class);

    private static final String SUB_CATEGORY    = "subCategory";
    private static final String FIELD           = "field";
    private static final String ORDER           = "order";
    private static final String DEFAULT_VALUE   = "defaultValue";

    public static ParseQuery<SubCategoryField> getFieldBySubCategory(SubCategory subCategory) {
        final ParseQuery<SubCategoryField> query = getQuery()
                .fromLocalDatastore()
                .whereEqualTo(SUB_CATEGORY, subCategory);
        query.include(FIELD);

        return query;
    }

    public static ParseQuery<SubCategoryField> getQuery() {
        final ParseQuery<SubCategoryField> query = ParseQuery.getQuery(SubCategoryField.class);
        query.orderByAscending(ORDER);
        if (ParseManager.isLocalDatabaseActive()) {
            query.fromLocalDatastore();
        }

        return query;
    }

    /**
     * This method only should call for getting fields for first time and exclusively called by
     * {@link com.volcano.assistant.backend.ParseManager#InitializeData(com.volcano.assistant.backend.ParseManager.OnDataInitializationListener)}
     * @param callback The callback
     */
    public static void pinAllInBackground(final FindCallback<SubCategoryField> callback) {
        final ParseQuery<SubCategoryField> query = ParseQuery.getQuery(SubCategoryField.class);
        query.findInBackground(new FindCallback<SubCategoryField>() {
            @Override
            public void done(List<SubCategoryField> subCategoryFields, ParseException e) {
                try {
                    if (e == null) {
                        // Save in local database
                        ParseObject.pinAll(subCategoryFields);
                        LogUtils.LogI(TAG, String.format("pinned %d sub category fields on local database", subCategoryFields.size()));
                    }
                    callback.done(subCategoryFields, e);
                } catch (ParseException e1) {
                    LogUtils.LogE(TAG, "pinning sub category fields failed", e1);
                    callback.done(subCategoryFields, e1);
                }
            }
        });
    }

    public Field getField() {
        return (Field) getParseObject(FIELD);
    }

    public String getDefaultValue() {
        return getString(DEFAULT_VALUE);
    }
}
