package com.volcano.assistant.model;

import com.parse.ParseUser;

/**
 * Represent a user object
 */
public final class User extends ParseUser {
    private final static String NAME = "name";

    public void setName(String name) {
        put(NAME, name);
    }

    public String getName() {
        return getString(NAME);
    }
}
