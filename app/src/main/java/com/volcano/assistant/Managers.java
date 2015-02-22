// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.assistant;

import com.volcano.assistant.backend.AccountManager;
import com.volcano.assistant.security.ApplicationLockManager;
import com.volcano.assistant.backend.ParseManager;

/**
 * Container class for various global managers.
 */
public final class Managers {

    private static ParseManager sParseManager;
    private static AccountManager sAccountManager;
    private static ApplicationLockManager sApplicationLockManager;

    public static void initalize() {
        sParseManager = new ParseManager();
        sAccountManager = new AccountManager();
        sApplicationLockManager = new ApplicationLockManager();
    }

    public static ParseManager getParseManager() {
        return sParseManager;
    }

    public static AccountManager getAccountManager() {
        return sAccountManager;
    }

    public static ApplicationLockManager getApplicationLockManager() {
        return sApplicationLockManager ;
    }
}
