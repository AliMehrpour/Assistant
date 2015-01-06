package com.volcano.assistant.backend;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.volcano.assistant.VlApplication;
import com.volcano.assistant.model.User;

/**
 * Manager for all Parse library stuffs
 */
public final class ParseManager {

    /**
     * General callback interface for delivering api call response
     */
    public interface Listener {
        /**
         * Called when a successful response received
         */
        public void onResponse();

        /**
         * Called when an error has been occurred
         */
        public void onErrorResponse(ParseException e);
    }

    private static final String APPLICATION_ID = "pBtCshXLPqwVBXhlNq6zLBTXUdMI8nNqblfASxNT";
    private static final String CLIENT_KEY     = "EtvsJB26jRBPGnkZalvIbevtQZUTcEFknZuiesvq";

    public ParseManager() {
        Parse.enableLocalDatastore(VlApplication.getInstance());

        ParseObject.registerSubclass(User.class);
        Parse.initialize(VlApplication.getInstance(), APPLICATION_ID, CLIENT_KEY);
    }
}
