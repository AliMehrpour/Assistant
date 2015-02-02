package com.volcano.assistant.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * AcountFieldValue that contain one account object, one field object and value
 */
@ParseClassName("AccountFieldValue")
public class AccountFieldValue extends ParseObject {
    private static final String ACCOUNT     = "account";
    private static final String FIELD       = "field";
    private static final String VALUE       = "value";

    public Account getAccount() {
        return (Account) getParseObject(ACCOUNT);
    }

    public void setAccount(Account account) {
        put(ACCOUNT, account);
    }

    public Field getField() {
        return (Field) getParseObject(FIELD);
    }

    public void setField(Field field) {
        put(FIELD, field);
    }

    public String getValue() {
        return getString(VALUE);
    }

    public void setValue(String value) {
        put(VALUE, value);
    }

}
