// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.model;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.volcano.esecurebox.backend.ParseManager;
import com.volcano.esecurebox.backend.TimeoutQuery;

import java.util.List;

/**
 * Values for fields with ENUM format type
 */
@ParseClassName("FieldTypeValue")
public class FieldTypeValue extends ParseObject {

    private final static String FIELD   = "field";
    private final static String VALUE   = "value";
    private final static String ORDER   = "order";

    public static ParseQuery getValueByField(Object tag, Field field, final FindCallback<FieldTypeValue> callback) {
        final ParseQuery<FieldTypeValue> query = getQuery().whereEqualTo(FIELD, field)
                .orderByAscending(ORDER);
        new TimeoutQuery<>(query).findInBackground(tag, new FindCallback<FieldTypeValue>() {
            @Override
            public void done(List<FieldTypeValue> fieldTypeValues, ParseException e) {
                callback.done(fieldTypeValues, e);
            }
        });

        return query;
    }

    private static ParseQuery<FieldTypeValue> getQuery() {
        final ParseQuery<FieldTypeValue> query = ParseQuery.getQuery(FieldTypeValue.class);
        if (ParseManager.isLocalDatabaseActive()) {
            query.fromLocalDatastore();
        }

        return query;
    }

    public Field getField() {
        return (Field) getParseObject(FIELD);
    }

    public String getValue() {
        return getString(VALUE);
    }

    public int getOrder() {
        return getInt(ORDER);
    }
}
