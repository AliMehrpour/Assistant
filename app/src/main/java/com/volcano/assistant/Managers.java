package com.volcano.assistant;

import com.volcano.assistant.backend.AccountManager;
import com.volcano.assistant.backend.ParseManager;

/**
 * Container class for various global managers.
 */
public final class Managers {

    private static ParseManager sParseManager;
    private static AccountManager sAccountManager;

    public static void initalize() {
        sParseManager = new ParseManager();
        sAccountManager = new AccountManager();
    }

    public static ParseManager getParseManager() {
        return sParseManager;
    }

    public static AccountManager getAccountManager() {
        return sAccountManager;
    }
}
