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
package com.aptana.ide.update.ui;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.browser.WebBrowserEditorInput;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.Version;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.MutexJobRule;
import com.aptana.ide.core.PluginUtils;
import com.aptana.ide.core.ui.BaseTimingStartup;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.update.preferences.IPreferenceConstants;

/**
 * 
 */
@SuppressWarnings("restriction")
public class SchedulerStartup extends BaseTimingStartup {

    /**
     * The constructor.
     */
    public SchedulerStartup() {
    }

    @Override
    public String getStartupName() {
        return "SchedulerStartup"; //$NON-NLS-1$
    }

    @Override
    protected void startup() {
        scheduleCheckForReleaseMessage();
        scheduleCheckForNewsMessage();
        scheduleCheckForAnnouncements();
        startupDone();
    }

    private void scheduleCheckForReleaseMessage() {
        // Escape mechanism
        boolean doNotCheckForReleaseMessage = Boolean
                .getBoolean("DO_NOT_CHECK_FOR_RELEASE_MESSAGE"); //$NON-NLS-1$
        if (doNotCheckForReleaseMessage) {
            return;
        }

        final IPreferenceStore prefs = UpdateUIActivator.getDefault().getPreferenceStore();
        final String releaseMessageURLPrefix = prefs
                .getString(IPreferenceConstants.RELEASE_MESSAGE_URL_PREFIX);
        Job job = new Job("Check for new release message") { //$NON-NLS-1$
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    final URL releaseMessageURL = new URL(System.getProperty(
                            "RELEASE_MESSAGE_URL_OVERRIDE", releaseMessageURLPrefix //$NON-NLS-1$
                                    + getCoreUIVersion() + "/message.html")); //$NON-NLS-1$
                    URLConnection urlConnection = null;
                    try {
                        urlConnection = releaseMessageURL.openConnection();
                    } catch (IOException ioe) {
                        logError(ioe);
                    }

                    if (urlConnection instanceof HttpURLConnection) {
                        HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
                        try {
                            httpURLConnection.setConnectTimeout(1000);
                            httpURLConnection.setUseCaches(false);
                            httpURLConnection.addRequestProperty("Cache-Control", "no-cache"); //$NON-NLS-1$ //$NON-NLS-2$
                            httpURLConnection.setRequestMethod("HEAD"); //$NON-NLS-1$
                            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                                boolean showMessage = true;
                                // Check for lastModified?
                                long lastModified = httpURLConnection.getLastModified();
                                if (lastModified == 0) {
                                    // unknown
                                } else {
                                    long lastLastModified = prefs.getLong(releaseMessageURL
                                            .toString());
                                    if (lastLastModified >= lastModified) {
                                        showMessage = false;
                                    }
                                }
                                prefs.setValue(releaseMessageURL.toString(), lastModified);

                                if (showMessage) {
                                    CoreUIUtils.getDisplay().asyncExec(new Runnable() {

                                        public void run() {
                                            IWorkbenchPage page = CoreUIPlugin.getActivePage();
                                            if (page != null) {
                                                try {
                                                    page.openEditor(new WebBrowserEditorInput(
                                                            releaseMessageURL,
                                                            IWorkbenchBrowserSupport.PERSISTENT),
                                                            ExternalWebBrowserEditor.ID, false);
                                                } catch (PartInitException e) {
                                                    // Show the message in
                                                    // external browser
                                                    CoreUIUtils.openBrowserURL(releaseMessageURL
                                                            .toExternalForm());
                                                }
                                            }

                                        }
                                    });
                                }
                            }
                        } catch (IOException e) {
                            logError(e);
                        } finally {
                            // cleanup
                            httpURLConnection.disconnect();
                        }
                    }
                } catch (MalformedURLException e) {
                    logError(e);
                }

                return Status.OK_STATUS;
            }
        };
        job.setSystem(true);
        job.schedule();
    }

    private void scheduleCheckForNewsMessage() {
        // Escape mechanism
        boolean doNotCheckForReleaseMessage = Boolean.getBoolean("DO_NOT_CHECK_FOR_NEWS_MESSAGE"); //$NON-NLS-1$
        if (doNotCheckForReleaseMessage) {
            return;
        }

        final IPreferenceStore prefs = UpdateUIActivator.getDefault().getPreferenceStore();
        String newsMessageURLPrefix = prefs.getString(IPreferenceConstants.NEWS_MESSAGE_URL_PREFIX);
        // For the news we want to pass the version of studio and
        final String newsMessageBaseURL = System.getProperty("NEWS_MESSAGE_URL_OVERRIDE", //$NON-NLS-1$
                newsMessageURLPrefix + "news.php"); //$NON-NLS-1$
        Job job = new Job("Check for new news message") { //$NON-NLS-1$
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    final URL newsMessageURL = new URL(newsMessageBaseURL + getParamsString());
                    URLConnection urlConnection = null;
                    try {
                        urlConnection = newsMessageURL.openConnection();
                    } catch (IOException ioe) {
                        logError(ioe);
                    }

                    if (urlConnection instanceof HttpURLConnection) {
                        HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
                        try {
                            httpURLConnection.setConnectTimeout(1000);
                            httpURLConnection.setUseCaches(false);
                            httpURLConnection.addRequestProperty("Cache-Control", "no-cache"); //$NON-NLS-1$ //$NON-NLS-2$
                            httpURLConnection.setRequestMethod("HEAD"); //$NON-NLS-1$
                            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                // Check for lastModified?
                                boolean showMessage = true;
                                long lastModified = httpURLConnection.getLastModified();
                                if (lastModified == 0) {
                                    // unknown
                                } else {
                                    long lastLastModified = prefs.getLong(newsMessageBaseURL);
                                    if (lastLastModified >= lastModified) {
                                        showMessage = false;
                                    }
                                }
                                prefs.setValue(newsMessageBaseURL, lastModified);

                                if (showMessage) {
                                    CoreUIUtils.getDisplay().asyncExec(new Runnable() {

                                        public void run() {
                                            IWorkbenchPage page = CoreUIPlugin.getActivePage();
                                            if (page != null) {
                                                try {
                                                    page.openEditor(new WebBrowserEditorInput(
                                                            newsMessageURL,
                                                            IWorkbenchBrowserSupport.PERSISTENT),
                                                            ExternalWebBrowserEditor.ID, false);
                                                } catch (PartInitException e) {
                                                    // Show the message in
                                                    // external browser
                                                    CoreUIUtils.openBrowserURL(newsMessageURL
                                                            .toExternalForm());
                                                }
                                            }

                                        }
                                    });
                                }
                            }
                        } catch (IOException e) {
                            logError(e);
                        } finally {
                            // cleanup
                            httpURLConnection.disconnect();
                        }
                    }
                } catch (MalformedURLException e) {
                    logError(e);
                }

                return Status.OK_STATUS;
            }
        };
        job.setSystem(true);
        job.schedule();
    }

    private static final String HEADER_WIDTH = "Width"; //$NON-NLS-1$
    private static final String HEADER_HEIGHT = "Height"; //$NON-NLS-1$

    private void scheduleCheckForAnnouncements() {
        // Escape mechanism
        boolean doNotCheckForReleaseMessage = Boolean.getBoolean("DO_NOT_CHECK_FOR_NEWS_MESSAGE"); //$NON-NLS-1$
        if (doNotCheckForReleaseMessage) {
            return;
        }
        final IPreferenceStore prefs = UpdateUIActivator.getDefault().getPreferenceStore();
        boolean b_neverShow = prefs.getBoolean(IPreferenceConstants.NEVER_SHOW_ANNOUNCEMENTS);
        if (b_neverShow) {
            return;
        }

        String announcementURLPrefix = prefs
                .getString(IPreferenceConstants.ANNOUNCEMENT_URL_PREFIX);
        final String announceBaseURL = System.getProperty("ANNOUNCEMENT_URL_OVERRIDE", //$NON-NLS-1$
                announcementURLPrefix + "announce.php"); //$NON-NLS-1$
        Job job = new Job("Check for new announcement") { //$NON-NLS-1$

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    // For the announcements we want to pass the version of
                    // studio and
                    getAnnouncement(new URL(announceBaseURL + getParamsString()));
                } catch (MalformedURLException e) {
                    logError(e);
                }

                return Status.OK_STATUS;
            }

            private void getAnnouncement(final URL url) {
                URLConnection urlConnection = null;
                try {
                    urlConnection = url.openConnection();
                } catch (IOException ioe) {
                    logError(ioe);
                }
                if (urlConnection instanceof HttpURLConnection) {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
                    try {
                        httpURLConnection.setConnectTimeout(1000);
                        httpURLConnection.setUseCaches(false);
                        httpURLConnection.addRequestProperty("Cache-Control", "no-cache"); //$NON-NLS-1$ //$NON-NLS-2$
                        httpURLConnection.setRequestMethod("HEAD"); //$NON-NLS-1$

                        if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            // Check for lastModified?
                            boolean showMessage = true;
                            long lastModified = httpURLConnection.getLastModified();
                            if (lastModified == 0) {
                                // unknown
                            } else {
                                boolean neverShowThisAgain = prefs
                                        .getBoolean(IPreferenceConstants.NEVER_SHOW_THIS_ANNOUNCEMENT);
                                if (neverShowThisAgain) {
                                    long lastLastModified = prefs
                                            .getLong(IPreferenceConstants.NEVER_SHOW_THIS_ANNOUNCEMENT_LAST_LAST_MODIFIED);
                                    if (lastModified <= lastLastModified) {
                                        showMessage = false;
                                    }
                                }
                            }
                            prefs
                                    .setValue(
                                            IPreferenceConstants.NEVER_SHOW_THIS_ANNOUNCEMENT_LAST_LAST_MODIFIED,
                                            lastModified);

                            String strHeight = httpURLConnection.getHeaderField(HEADER_HEIGHT);
                            String strWidth = httpURLConnection.getHeaderField(HEADER_WIDTH);

                            final int height = (strHeight != null) ? Integer.parseInt(strHeight)
                                    : 300;
                            final int width = (strWidth != null) ? Integer.parseInt(strWidth) : 300;

                            if (showMessage) {
                                UIJob uiJob = new UIJob("Show Aptana Announcement") { //$NON-NLS-1$
                                    @Override
                                    public IStatus runInUIThread(IProgressMonitor monitor) {
                                        IWorkbenchPage page = CoreUIPlugin.getActivePage();
                                        if (page != null) {
                                            BrowserDialog dialog = new BrowserDialog(CoreUIUtils
                                                    .getActiveShell(), url.toString(), height,
                                                    width);
                                            dialog.open();
                                        }

                                        return Status.OK_STATUS;
                                    }
                                };
                                uiJob.setSystem(true);
                                uiJob.setRule(MutexJobRule.getInstance());
                                uiJob.schedule();
                            }
                        }
                    } catch (IOException e) {
                        logError(e);
                    } finally {
                        // cleanup
                        httpURLConnection.disconnect();
                    }
                }
            }
        };
        job.setSystem(true);
        job.schedule();
    }

    private static void logError(Exception e) {
        IdeLog.logInfo(UpdateUIActivator.getDefault(), e.getLocalizedMessage(), e);
    }

    /**
     * @return the version associated with com.aptana.ide.core.ui
     */
    private static String getCoreUIVersion() {
        Version version = new Version(PluginUtils.getPluginVersion(CoreUIPlugin.getDefault()));
        return MessageFormat.format("{0}.{1}.{2}.{3}", version.getMajor(), version.getMinor(), //$NON-NLS-1$ 
                version.getMicro(), version.getQualifier());
    }

    private static String getCoreUIVersionDetailsString() {
        Version version = new Version(PluginUtils.getPluginVersion(CoreUIPlugin.getDefault()));
        return MessageFormat.format(
                "coreui_major={0}&coreui_minor={1}&coreui_micro={2}&coreui_qualifier={3}", //$NON-NLS-1$ 
                version.getMajor(), version.getMinor(), version.getMicro(), version.getQualifier());
    }

    private static String getParamsString() {
        StringBuilder paramsString = new StringBuilder("?"); //$NON-NLS-1$
        paramsString.append(getCoreUIVersionDetailsString());
        paramsString.append("&appName=").append(getAppName()); //$NON-NLS-1$
        paramsString.append("&osgi_framework=") //$NON-NLS-1$
                .append(System.getProperty("osgi.framework.version")); //$NON-NLS-1$
        paramsString.append("&os=").append(Platform.getOS()); //$NON-NLS-1$
        paramsString.append("&arch=").append(Platform.getOSArch()); //$NON-NLS-1$

        return paramsString.toString();
    }

    private static String getAppName() {
        String commands = System.getProperty("eclipse.commands"); //$NON-NLS-1$
        int indexOfNameArg = commands.indexOf("-name\n"); //$NON-NLS-1$
        String subCommands = commands.substring(indexOfNameArg + 6);
        int indexOfNextNewline = subCommands.indexOf("\n"); //$NON-NLS-1$
        String nameArg = subCommands.substring(0, indexOfNextNewline);
        return nameArg;
    }
}
