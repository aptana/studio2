/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain Eclipse Public Licensed code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.subscription;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.IWorkbenchActivitySupport;
import org.eclipse.ui.progress.UIJob;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.model.AuthUtils;
import com.aptana.ide.core.model.IModelListener;
import com.aptana.ide.core.model.IModifiableObject;
import com.aptana.ide.core.model.user.AptanaUser;
import com.aptana.ide.core.model.user.User;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.subscription.model.SubscriptionService;

public class SubscriptionManager {

    private static final String SUBSCRIPTION_URL = "http://staging-aptanalivestaging.aptanacloud.com/subscriptions.xml"; //$NON-NLS-1$
    private static final String SECURE_PREF_NODE_SERVICES = SubscriptionPlugin.PLUGIN_ID
            + "/services"; //$NON-NLS-1$
    private static final String SECURE_PREF_NODE_USER = SubscriptionPlugin.PLUGIN_ID
            + "/user"; //$NON-NLS-1$
    private static final String PREF_LAST_USER = "LastSignedInUser"; //$NON-NLS-1$

    private static final long DAY_TIME = 1000 * 60 * 60 * 24;
    // make the default 30 days for requiring user to sign in
    private static final int ALLOWED_DAY = 30;
    // grace period of 7 days before the activities are truly turned off
    private static final int GRACE_PERIOD = 7;

    private static final Set<String> EMPTY_SET = new HashSet<String>();

    private static SubscriptionManager fManager = null;

    private static IWorkbenchActivitySupport activitySupport = PlatformUI
            .getWorkbench().getActivitySupport();;

    private User fUser;
    private String fCurrentUsername;

    private IModelListener fModelListener = new IModelListener() {

        public void modelChanged(IModifiableObject object) {
            if (object instanceof User) {
                if (fUser.hasCredentials()) {
                    // saves the current user
                    fCurrentUsername = fUser.getUsername();
                    getPreferenceStore().setValue(PREF_LAST_USER,
                            fCurrentUsername);
                    // updates the subscriptions
                    update();
                } else {
                    // user signed out
                    // stores the time for the 30-day check
                    ISecurePreferences prefs = getUserSecurePreferences();
                    try {
                        prefs.putLong(fCurrentUsername, (new Date()).getTime(),
                                true);
                    } catch (StorageException e) {
                        logError(e);
                    }
                }
            }
        }
    };

    public static SubscriptionManager getInstance() {
        if (fManager == null) {
            fManager = new SubscriptionManager();
        }
        return fManager;
    }

    public void init() {
        fUser.addListener(fModelListener);
    }

    public void shutdown() {
        fUser.removeListener(fModelListener);
    }

    public void update() {
        try {
            // access the latest remote content for the current user
            URL url = getURL(SUBSCRIPTION_URL);
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            connection.setConnectTimeout(1000);
            connection.addRequestProperty("Cache-Control", "no-cache"); //$NON-NLS-1$ //$NON-NLS-2$
            connection.addRequestProperty("Authorization", AuthUtils //$NON-NLS-1$
                    .getAuthorizationHeader(fUser.getUsername(), fUser
                            .getPassword()));

            InputStream in = connection.getInputStream();
            StringBuilder text = new StringBuilder();
            byte[] bytes = new byte[1024];
            BufferedInputStream input = new BufferedInputStream(in);
            int bytesRead;
            while ((bytesRead = input.read(bytes)) != -1) {
                text.append(new String(bytes, 0, bytesRead));
            }
            // parses the content
            String content = text.toString();
            setEnabledActivityIds(parseXML(content));

            // saves the latest content
            ISecurePreferences prefs = getServicesSecurePreferences();
            prefs.put(fUser.getUsername(), content, true);
        } catch (IOException e) {
            // Failed to grab the remote content; falls back to the cached
            // version
            setEnabledActivityIds(parseCache(fUser.getUsername()));
        } catch (StorageException e) {
            logError(e);
        }
    }

    private SubscriptionManager() {
        fUser = AptanaUser.getSignedInUser();

        if (fUser.hasCredentials()) {
            // user is already signed in
            fCurrentUsername = fUser.getUsername();
            update();
        } else {
            fCurrentUsername = getPreferenceStore().getString(PREF_LAST_USER);
            // finds out when was the last time the previous user was logged in
            ISecurePreferences prefs = getUserSecurePreferences();
            try {
                long lastSignedOutTime = prefs.getLong(fCurrentUsername, 0);
                if (lastSignedOutTime == 0) {
                    // never signed in; turns off all Live activities
                    setEnabledActivityIds(new HashSet<String>());
                } else {
                    Set<String> activityIds = parseCache(fCurrentUsername);
                    if (activityIds.isEmpty()) {
                        // user has not subscribed to any Live features, so no
                        // need to warn
                        return;
                    }

                    long currentTime = (new Date()).getTime();
                    if (currentTime - lastSignedOutTime >= (ALLOWED_DAY + GRACE_PERIOD)
                            * DAY_TIME) {
                        // grace period is passed; turns off all Live activities
                        setEnabledActivityIds(new HashSet<String>());
                    } else if (currentTime - lastSignedOutTime >= ALLOWED_DAY
                            * DAY_TIME) {
                        // warns the user to log in or otherwise the Live
                        // activities will be turned off
                        showWarning();
                    } else {
                        setEnabledActivityIds(activityIds);
                    }
                }
            } catch (StorageException e) {
                logError(e);
            }
        }
    }

    private Set<String> parseXML(String content) {
        try {
            XMLReader reader = SAXParserFactory.newInstance().newSAXParser()
                    .getXMLReader();
            SubscriptionContentHandler handler = new SubscriptionContentHandler();
            reader.setContentHandler(handler);
            reader.parse(new InputSource(new StringReader(content)));

            final Set<String> activityIds = new HashSet<String>();
            SubscriptionService[] services = handler.getServices();
            String[] ids;
            for (SubscriptionService service : services) {
                ids = service.getActivityIds();
                for (String id : ids) {
                    activityIds.add(id);
                }
            }
            return activityIds;
        } catch (SAXException e) {
            logError(e);
        } catch (ParserConfigurationException e) {
            logError(e);
        } catch (IOException e) {
            logError(e);
        }
        return EMPTY_SET;
    }

    private Set<String> parseCache(String username) {
        ISecurePreferences prefs = getServicesSecurePreferences();
        try {
            String content = prefs.get(username, null);
            if (content != null) {
                return parseXML(content);
            }
        } catch (StorageException se) {
            logError(se);
        }
        return EMPTY_SET;
    }

    /**
     * Specify a set of activities to be enabled.
     * 
     * @param ids
     *            the set of activity ids being enabled
     */
    private void setEnabledActivityIds(final Set<String> ids) {
        Job job = new UIJob("Enabling Live Activities") { //$NON-NLS-1$

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                activitySupport.setEnabledActivityIds(ids);
                return Status.OK_STATUS;
            }

        };
        job.setSystem(true);
        job.schedule();
    }

    private static void showWarning() {
        CoreUIUtils.getDisplay().asyncExec(new Runnable() {

            public void run() {
                MessageDialog.openWarning(CoreUIUtils.getActiveShell(),
                        Messages.SubscriptionManager_WarningTitle,
                        MessageFormat.format(
                                Messages.SubscriptionManager_WarningMessage,
                                ALLOWED_DAY));
            }

        });
    }

    private static ISecurePreferences getServicesSecurePreferences() {
        ISecurePreferences root = SecurePreferencesFactory.getDefault();
        ISecurePreferences node = root.node(SECURE_PREF_NODE_SERVICES);
        return node;
    }

    private static ISecurePreferences getUserSecurePreferences() {
        ISecurePreferences root = SecurePreferencesFactory.getDefault();
        ISecurePreferences node = root.node(SECURE_PREF_NODE_USER);
        return node;
    }

    private static IPreferenceStore getPreferenceStore() {
        return SubscriptionPlugin.getDefault().getPreferenceStore();
    }

    private static URL getURL(String location) throws MalformedURLException {
        try {
            return new URL(location);
        } catch (MalformedURLException e) {
            return (new File(location)).toURI().toURL();
        }
    }

    private static void logError(Exception e) {
        IdeLog.logError(SubscriptionPlugin.getDefault(), e.getMessage(), e);
    }
}
