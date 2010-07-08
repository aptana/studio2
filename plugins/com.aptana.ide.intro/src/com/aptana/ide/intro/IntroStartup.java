/**
 * Copyright (c) 2005-2010 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.intro;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.MutexJobRule;
import com.aptana.ide.core.ui.BaseTimingStartup;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.DialogUtils;
import com.aptana.ide.intro.preferences.FeatureDescriptor;
import com.aptana.ide.intro.preferences.FeatureRegistry;
import com.aptana.ide.intro.preferences.IPreferenceConstants;
import com.aptana.ide.server.portal.PortalPlugin;
import com.aptana.ide.update.FeatureUtil;
import com.aptana.ide.update.manager.IPlugin;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class IntroStartup extends BaseTimingStartup {

    /**
     * IGNORE
     */
    public static final String IGNORE = "ignore.initial.my.aptana"; //$NON-NLS-1$

    @Override
    public String getStartupName() {
        return Messages.IntroStartup_Name;
    }

    /**
     * @see org.eclipse.ui.IStartup#earlyStartup()
     */
    public void startup() {
        showMyAptana();
        installFeatures();
        startupDone();
    }

    /**
     * Show the My Aptana start page.
     */
    protected void showMyAptana() {
        IPreferenceStore store = IntroPlugin.getDefault().getPreferenceStore();

        boolean shownPreviously = store.getBoolean(IPreferenceConstants.SHOWN_PREVIOUSLY)
                || "true".equals(System.getProperty(IGNORE)); //$NON-NLS-1$
        if (!shownPreviously) {
            store.setValue(IPreferenceConstants.SHOWN_PREVIOUSLY, true);
            showStartupPage();
            return;
        }

        String showStartPage = store.getString(IPreferenceConstants.SHOW_STARTPAGE_ON_STARTUP);
        boolean portalPreviouslyOpened = PortalPlugin
                .getDefault()
                .getPreferenceStore()
                .getBoolean(
                        com.aptana.ide.server.portal.preferences.IPreferenceConstants.MY_APTANA_PREVIOUSLY_OPENED);
        if (showStartPage.equals(IPreferenceConstants.ALWAYS_SHOW) || portalPreviouslyOpened) {
            showStartupPage();
            return;
        }
        if (showStartPage.equals(IPreferenceConstants.NEVER_SHOW)) {
            return;
        }
        // this has a side effect of updating the feature change list store
        boolean changed = FeatureChangeManager.getManager().areFeaturesChanged();
        if (changed) {
            IdeLog.logInfo(IntroPlugin.getDefault(), "Features Changed"); //$NON-NLS-1$
            List<FeatureChange> changeList = FeatureChangeManager.getManager()
                    .getFeatureChangeList();
            if (changeList != null && changeList.size() > 0) {
                showStartupPage();
            }
        } else {
            IdeLog.logInfo(IntroPlugin.getDefault(), "Unchanged feature store"); //$NON-NLS-1$
        }
    }

    private void showStartupPage() {
        UIJob job = new UIJob("Showing Startup Page") { //$NON-NLS-1$

            public IStatus runInUIThread(IProgressMonitor monitor) {
                IWorkbenchPart activePart = null;
                IWorkbenchPage page = null;
                IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                if (window != null) {
                    page = window.getActivePage();
                    activePart = page.getActivePart();
                }
                IPreferenceStore prefs = IntroPlugin.getDefault().getPreferenceStore();
                String editorId = prefs.getString(IPreferenceConstants.INTRO_EDITOR_ID);
                IEditorPart editorPart = CoreUIUtils.openEditor(editorId, false);
                if (editorPart == null) {
                    // falls back to the default
                    editorId = prefs.getDefaultString(IPreferenceConstants.INTRO_EDITOR_ID);
                    prefs.setValue(IPreferenceConstants.INTRO_EDITOR_ID, editorId);
                    CoreUIUtils.openEditor(editorId, false);
                }
                // makes the active part re-grab the focus
                if (activePart != null) {
                    page.activate(activePart);
                }
                return Status.OK_STATUS;
            }

        };
        job.schedule(1000);
    }

    /**
     * Install any missing features
     */
    protected void installFeatures() {
        final IPreferenceStore store = IntroPlugin.getDefault().getPreferenceStore();
        boolean check = store.getBoolean(IPreferenceConstants.INSTALL_PRO_AND_REQUIRED_FEATURES);
        if (!check) {
            return;
        }

        List<FeatureDescriptor> features = new ArrayList<FeatureDescriptor>();
        List<IPlugin> installedFeatures = FeatureUtil.getInstalledFeatures();
        String[] installedFeatureIds = new String[installedFeatures.size()];
        int index = 0;
        for (IPlugin feature : installedFeatures) {
            installedFeatureIds[index++] = feature.getId();
        }

        // Using the list of installed and ignored features, filter out
        // possible install items that were previously ignored, or would
        // conflict with currently installed items.
        String[] ignored = store.getString(IPreferenceConstants.IGNORE_INSTALL_FEATURES).split(","); //$NON-NLS-1$
        final List<FeatureDescriptor> featuresToInstall = new ArrayList<FeatureDescriptor>();
        for (Iterator<FeatureDescriptor> iterator = features.iterator(); iterator.hasNext();) {
            FeatureDescriptor featureDescriptor = iterator.next();
            if (!FeatureRegistry.isFeatureIgnored(featureDescriptor.getId(), ignored)
                    && !FeatureRegistry.doesFeatureConflict(featureDescriptor, installedFeatureIds)) {
                featuresToInstall.add(featureDescriptor);
            }
        }

        // Don't show dialog if user has previously opted not to see if
        // again
        if (featuresToInstall.size() > 0
                && !store.getString(IPreferenceConstants.HIDE_DIALOG_INSTALL_PROMPT).equals(
                        MessageDialogWithToggle.NEVER)) {
            String tmp_str = ""; //$NON-NLS-1$
            for (FeatureDescriptor featureDesc : featuresToInstall) {
                tmp_str += "\n\t" + featureDesc.getName(); //$NON-NLS-1$
            }
            final String str_features = tmp_str;
            UIJob job = new UIJob(Messages.IntroStartup_Job_InstallFeatures) {
                public IStatus runInUIThread(IProgressMonitor monitor) {
                    int returnCode = DialogUtils.openIgnoreMessageDialogConfirm(Display
                            .getCurrent().getActiveShell(), Messages.IntroStartup_InstallTitle,
                            MessageFormat
                                    .format(Messages.IntroStartup_InstallMessage, str_features),
                            store, IPreferenceConstants.HIDE_DIALOG_INSTALL_PROMPT);

                    if (returnCode == MessageDialog.CANCEL) {
                        return Status.OK_STATUS;
                    }
                    new FeatureInstallJob(featuresToInstall).schedule(0);
                    return Status.OK_STATUS;
                }
            };
            job.setRule(MutexJobRule.getInstance());
            job.schedule(20000);
        }

        final List<FeatureDescriptor> featuresToUpdate = FeatureRegistry
                .gatherInstalledRequiredFeatures();
        if (featuresToUpdate.size() > 0) {
            final StringBuilder featureToUpdateStr = new StringBuilder();
            for (FeatureDescriptor featureDesc : featuresToUpdate) {
                featureToUpdateStr.append("\n\t" + featureDesc.getName()); //$NON-NLS-1$
            }
            UIJob job = new UIJob(Messages.IntroStartup_Job_UpdateFeatures) {
                public IStatus runInUIThread(IProgressMonitor monitor) {
                    boolean returnCode = MessageDialog.openConfirm(Display.getCurrent()
                            .getActiveShell(), Messages.IntroStartup_UpdateTitle, MessageFormat
                            .format(Messages.IntroStartup_UpdateMessage, featureToUpdateStr
                                    .toString()));
                    if (!returnCode) {
                        return Status.OK_STATUS;
                    }

                    new FeatureInstallJob(featuresToUpdate).schedule(0);
                    return Status.OK_STATUS;
                }
            };
            job.setRule(MutexJobRule.getInstance());
            job.schedule(20000);
        }
    }
}
