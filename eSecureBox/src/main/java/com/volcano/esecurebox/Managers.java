// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox;

import com.volcano.esecurebox.backend.AccountManager;
import com.volcano.esecurebox.security.ApplicationLockManager;
import com.volcano.esecurebox.backend.ParseManager;

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