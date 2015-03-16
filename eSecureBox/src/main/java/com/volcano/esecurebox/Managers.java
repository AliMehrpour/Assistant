// Copyright (c) 2015 Volcano. All rights reserved.
package com.volcano.esecurebox;

import com.volcano.esecurebox.analytics.MixpanelManager;
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
    private static ConfigManager sConfigManager;
    private static MixpanelManager sMixpanelManager;

    public static void initialize() {
        sParseManager = new ParseManager();
        sApplicationLockManager = new ApplicationLockManager();
        sConfigManager = new ConfigManager();
        sMixpanelManager = new MixpanelManager();
        sAccountManager = new AccountManager();
    }

    public static ParseManager getParseManager() {
        return sParseManager;
    }

    public static AccountManager getAccountManager() {
        return sAccountManager;
    }

    public static ApplicationLockManager getApplicationLockManager() {
        return sApplicationLockManager;
    }

    public static ConfigManager getConfigManager() {
        return sConfigManager;
    }

    public static MixpanelManager getMixpanelManager() {
        return sMixpanelManager;
    }

}
