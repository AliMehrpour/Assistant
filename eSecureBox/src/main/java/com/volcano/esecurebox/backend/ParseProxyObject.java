// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox.backend;

import com.parse.ParseObject;
import com.volcano.esecurebox.model.Category;
import com.volcano.esecurebox.model.SubCategory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A proxy object for create a serializable object from any
 * {@link com.parse.ParseObject} objects
 */
public class ParseProxyObject implements Serializable {
    private static final long serialVersionUID = 1L;
    private HashMap<String, Object> values = new HashMap<>();

    public ParseProxyObject(ParseObject object) {
        for(String key : object.keySet()) {
            @SuppressWarnings("rawtypes")
            final Class classType = object.get(key).getClass();
            if (classType == byte[].class || classType == String.class ||
                    classType == Integer.class || classType == Boolean.class) {
                values.put(key, object.get(key));
            }
            else if (classType == Category.class) {
                final ParseProxyObject parseCategoryObject = new ParseProxyObject((ParseObject)object.get(key));
                values.put(key, parseCategoryObject);
            }
            else if (classType == SubCategory.class) {
                final ParseProxyObject parseSubCategoryObject = new ParseProxyObject((ParseObject)object.get(key));
                values.put(key, parseSubCategoryObject);
            }
            /*else if (classType == ArrayList.class) {
                //noinspection unchecked
                final ArrayList<ParseObject> arrayList = (ArrayList<ParseObject>) object.get(key);
                final int size = arrayList .size();
                final ArrayList<ParseProxyObject> arrayPpo = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    final ParseProxyObject ppo = new ParseProxyObject(arrayList.get(i));
                    arrayPpo.add(ppo);
                }
                values.put(key, arrayList);
            }*/
        }
    }

    public HashMap<String, Object> getValues() {
        return values;
    }

    public void setValues(HashMap<String, Object> values) {
        this.values = values;
    }

    public String getString(String key) {
        if (has(key)) {
            return (String) values.get(key);
        }
        else {
            return "";
        }
    }

    public int getInt(String key) {
        if (has(key)) {
            return (Integer)values.get(key);
        }
        else {
            return 0;
        }
    }

    public Boolean getBoolean(String key) {
        if (has(key)) {
            return (Boolean)values.get(key);
        }
        else {
            return false;
        }
    }

    public byte[] getBytes(String key) {
        if (has(key)) {
            return (byte[])values.get(key);
        }
        else {
            return new byte[0];
        }
    }

    public List<ParseProxyObject> getSubCategories(String key) {
        if (has(key)) {
            final ArrayList<ParseProxyObject> arrayPpo = new ArrayList<>();
            final ArrayList arrayList = (ArrayList) values.get(key);
            final int size = arrayList.size();
            for (int i = 0; i <size; i++) {
                arrayPpo.add((ParseProxyObject) arrayList.get(i));
            }
            return arrayPpo;
        }
        else {
            return null;
        }
    }

    public Boolean has(String key) {
        return values.containsKey(key);
    }
}
