package com.aptana.ide.update.portal.clients;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "com.aptana.ide.update.portal.clients.messages"; //$NON-NLS-1$

    public static String InstallPluginsClient_InstallerJob_Title;
    public static String PluginsManager_ERR_MSG_Unable_find_plugin;
    public static String PluginsManager_ERR_TTL_Unable_find_plugin;
    public static String PluginsManager_INF_MSG_Feature_selected_already_installed;
    public static String PluginsManager_TTL_Plugin_already_installed;
    public static String PluginsManager_ERR_MSG_Unable_install_plugin;
    public static String PluginsManager_ERR_TTL_Unable_install_plugin;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
