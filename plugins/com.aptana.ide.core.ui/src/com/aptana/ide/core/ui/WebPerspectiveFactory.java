/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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
 * with certain other free and open source software ("FOSS") code and certain additional terms
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
package com.aptana.ide.core.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.registry.PerspectiveDescriptor;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.core.MutexJobRule;
import com.aptana.ide.core.ui.preferences.IPreferenceConstants;

/**
 * @author Spike Washburn
 */
public class WebPerspectiveFactory implements IPerspectiveFactory {

    private static final float MINIMUM_VIEW_WIDTH = 0.5f;
    private static final float MAXIMUM_VIEW_HEIGHT = 0.7f;
    private static final float MINIMUM_VIEW_HEIGHT = 0.2f;

    private static final String FILE_WIZARD_ID = "file_wizards"; //$NON-NLS-1$
    private static final String TAG_NEW_FILE_WIZARD = "new-file-wizard"; //$NON-NLS-1$
    private static final String TAG_UNTITLED_FILE_WIZARD = "untitled-file-wizard"; //$NON-NLS-1$
    private static final String ATTR_WIZARD_ID = "wizard-id"; //$NON-NLS-1$

    /**
     * VERSION
     * 
     * NOTE: Update this when the perspective layout changes for a new release
     */
    public static final int VERSION = 65;

    /**
     * PERSPECTIVE_ID
     */
    public static final String PERSPECTIVE_ID = "com.aptana.ide.js.ui.WebPerspective"; //$NON-NLS-1$

    /**
     * RAILS_PERSPECTIVE_ID
     */
    public static final String RAILS_PERSPECTIVE_ID = "org.radrails.rails.ui.PerspectiveRails"; //$NON-NLS-1$

    /**
     * RUBY_PERSPECTIVE_ID
     */
    public static final String RUBY_PERSPECTIVE_ID = "org.rubypeople.rdt.ui.PerspectiveRuby"; //$NON-NLS-1$

    private static List<Runnable> resettingHandlers;

    /**
     * addResetHandler
     * 
     * @param handler
     */
    public static void addResettingHandler(Runnable handler) {
        if (resettingHandlers == null) {
            resettingHandlers = new ArrayList<Runnable>();
        }

        resettingHandlers.add(handler);
    }

    /**
     * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
     */
    public void createInitialLayout(IPageLayout layout) {
        defineActions(layout);
        defineLayout(layout);
    }

    /**
     * defineLayout
     * 
     * @param layout
     */
    private void defineLayout(IPageLayout layout) {
        IFolderLayout leftTop = layout.createFolder(
                "leftTop", IPageLayout.LEFT, MINIMUM_VIEW_HEIGHT, IPageLayout.ID_EDITOR_AREA); //$NON-NLS-1$
        leftTop.addView("com.aptana.ide.ui.io.fileExplorerView"); //$NON-NLS-1$
        leftTop.addPlaceholder("com.aptana.ide.ui.io.fileExplorerView:*"); //$NON-NLS-1$
        leftTop.addView("com.aptana.ide.js.ui.views.profilesView"); //$NON-NLS-1$

        IFolderLayout left = layout.createFolder(
                "left", IPageLayout.BOTTOM, MINIMUM_VIEW_WIDTH, "leftTop"); //$NON-NLS-1$ //$NON-NLS-2$
        left.addView("com.aptana.ide.documentation.TutorialView"); //$NON-NLS-1$
        left.addView(IPageLayout.ID_OUTLINE);

        IPlaceholderFolderLayout right = layout.createPlaceholderFolder(
                "right", IPageLayout.RIGHT, MAXIMUM_VIEW_HEIGHT, IPageLayout.ID_EDITOR_AREA); //$NON-NLS-1$

        right.addPlaceholder("com.aptana.ide.samples.SamplesView"); //$NON-NLS-1$

        IPlaceholderFolderLayout rightBottom = layout.createPlaceholderFolder(
                "rightBottom", IPageLayout.BOTTOM, MAXIMUM_VIEW_HEIGHT, "right"); //$NON-NLS-1$ //$NON-NLS-2$
        rightBottom.addPlaceholder("org.eclipse.eclipsemonkey.views.ScriptsView"); //$NON-NLS-1$
        rightBottom.addPlaceholder("com.aptana.ide.scripting.SnippetsView"); //$NON-NLS-1$

        IPlaceholderFolderLayout bottom = layout.createPlaceholderFolder(
                "bottom", IPageLayout.BOTTOM, MAXIMUM_VIEW_HEIGHT, IPageLayout.ID_EDITOR_AREA); //$NON-NLS-1$
        bottom.addPlaceholder("com.aptana.ide.js.ui.views.problemsView"); //$NON-NLS-1$
        bottom.addPlaceholder("org.eclipse.ui.console.ConsoleView"); //$NON-NLS-1$
        bottom.addPlaceholder("com.aptana.ide.server.ui.serversView"); //$NON-NLS-1$
        bottom.addPlaceholder("com.aptana.ide.ui.ViewPlugins"); //$NON-NLS-1$
        bottom.addPlaceholder("com.aptana.ide.documentation.jquery.visualjquery"); //$NON-NLS-1$
        bottom.addPlaceholder("com.aptana.ide.js.ui.views.GenericScriptableView:*"); //$NON-NLS-1$
        bottom.addPlaceholder("com.aptana.ide.logging.LogView"); //$NON-NLS-1$
        bottom.addPlaceholder("com.aptana.ide.syncing.ui.views.FTPManagerView"); //$NON-NLS-1$
        bottom.addPlaceholder("com.aptana.ide.syncing.views.SyncManagerView"); //$NON-NLS-1$
    }

    /**
     * Gets the default set of wizard shortcuts
     * 
     * @return String[]
     */
    private static String[] getWizardShortcuts() {
        List<String> ids = getFileWizardShortcuts();

        return ids.toArray(new String[ids.size()]);
    }

    /**
     * Gets the default set of project-based wizard shortcuts
     * 
     * @return String[]
     */
    public static List<String> getProjectWizardShortcuts() {
        List<String> ids = new ArrayList<String>();
        addFromExtension(ids, TAG_NEW_FILE_WIZARD);
        // sort items
        Collections.sort(ids);

        // add file and folder to the end of the list
        ids.add("org.eclipse.ui.wizards.new.file"); //$NON-NLS-1$
        ids.add("org.eclipse.ui.wizards.new.folder"); //$NON-NLS-1$

        return ids;
    }

    /**
     * Gets the default set of file-based wizard shortcuts
     * 
     * @return String[]
     */
    public static List<String> getFileWizardShortcuts() {
        List<String> ids = new ArrayList<String>();
        addFromExtension(ids, TAG_UNTITLED_FILE_WIZARD);
        // sort items
        Collections.sort(ids);

        return ids;
    }

    /**
     * addFromExtension
     * 
     * @param ids
     * @param elementName
     */
    private static void addFromExtension(List<String> ids, String elementName) {
        IExtensionRegistry registry = Platform.getExtensionRegistry();

        if (registry != null) {
            IExtensionPoint extensionPoint = registry.getExtensionPoint(CoreUIPlugin.ID,
                    FILE_WIZARD_ID);

            if (extensionPoint != null) {
                IExtension[] extensions = extensionPoint.getExtensions();

                for (int i = 0; i < extensions.length; i++) {
                    IExtension extension = extensions[i];
                    IConfigurationElement[] elements = extension.getConfigurationElements();

                    for (int j = 0; j < elements.length; j++) {
                        IConfigurationElement element = elements[j];

                        if (element.getName().equals(elementName)) {
                            String id = element.getAttribute(ATTR_WIZARD_ID);

                            if (id != null) {
                                ids.add(id);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * defineActions
     * 
     * @param layout
     */
    private void defineActions(IPageLayout layout) {
        String[] wizards = getWizardShortcuts();
        for (int i = 0; i < wizards.length; i++) {
            layout.addNewWizardShortcut(wizards[i]);
        }

        layout.addActionSet("com.aptana.ide.server.ui.launchActionSet"); //$NON-NLS-1$
        layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);

        // Add "show views".
        layout.addShowViewShortcut("com.aptana.ide.scripting.SnippetsView"); //$NON-NLS-1$
        layout.addShowViewShortcut("com.aptana.ide.ui.io.fileExplorerView"); //$NON-NLS-1$
        layout.addShowViewShortcut("com.aptana.ide.js.ui.views.profilesView"); //$NON-NLS-1$
        layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
        layout.addShowViewShortcut("com.aptana.ide.js.ui.views.problemsView"); //$NON-NLS-1$
        layout.addShowViewShortcut("org.eclipse.ui.console.ConsoleView"); //$NON-NLS-1$
        layout.addShowViewShortcut(IPageLayout.ID_BOOKMARKS);
        layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
        layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
        layout.addShowViewShortcut("om.aptana.ide.syncing.ui.views.FTPManagerView"); //$NON-NLS-1$
        layout.addShowViewShortcut("com.aptana.ide.syncing.views.SyncManagerView"); //$NON-NLS-1$
        layout.addShowViewShortcut("com.aptana.ide.js.docgen.views.visualscriptdoc"); //$NON-NLS-1$
        layout.addShowViewShortcut("com.aptana.ide.logging.LogView"); //$NON-NLS-1$
        layout.addShowViewShortcut("com.aptana.ide.server.ui.serversView"); //$NON-NLS-1$
    }

    /**
     * removeResetHandler
     * 
     * @param handler
     */
    public static void removeResettingHandler(Runnable handler) {
        resettingHandlers.remove(handler);
    }

    /**
     * Resets the current perspective.
     * 
     * @param page
     */
    public static void resetPerspective(final IWorkbenchPage page) {
        if (Display.getCurrent() == null) {
            return;
        }

        final Shell shell = Display.getCurrent().getActiveShell();
        final IPreferenceStore p = CoreUIPlugin.getDefault().getPreferenceStore();
        if (p.getBoolean(IPreferenceConstants.WEB_PERSPECTIVE_RESET_PERSPECTIVE)) {
            return;
        }

        UIJob job = new UIJob("Resetting Aptana perspective") { //$NON-NLS-1$

            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                if (shell != null) {
                    p.setValue(IPreferenceConstants.WEB_PERSPECTIVE_RESETTING_PERSPECTIVE, true);

                    boolean ret = MessageDialog.openQuestion(CoreUIUtils.getActiveShell(),
                            Messages.WebPerspectiveFactory_UpdatePerspectiveTitle,
                            Messages.WebPerspectiveFactory_UpdatePerspectiveConfirmation);
                    p.setValue(IPreferenceConstants.WEB_PERSPECTIVE_LAST_VERSION,
                            WebPerspectiveFactory.VERSION);
                    if (!ret) {
                        return Status.OK_STATUS;
                    }

                    p.setValue(IPreferenceConstants.WEB_PERSPECTIVE_RESET_PERSPECTIVE, true);

                    // fire all resetting handlers
                    if (resettingHandlers != null) {
                        for (int i = 0; i < resettingHandlers.size(); i++) {
                            resettingHandlers.get(i).run();
                        }
                    }

                    page.resetPerspective();
                }
                return Status.OK_STATUS;
            }

        };
        job.setRule(MutexJobRule.getInstance());
        job.setSystem(true);
        job.schedule();
    }

    /**
     * Is the perspective one of the aptana derived perspectives
     * 
     * @param desc
     * @return - true if an aptana perspective
     */
    public static boolean isValidAptanaPerspective(IPerspectiveDescriptor desc) {
        if (WebPerspectiveFactory.RAILS_PERSPECTIVE_ID.equals(desc.getId())
                || WebPerspectiveFactory.RUBY_PERSPECTIVE_ID.equals(desc.getId())
                || WebPerspectiveFactory.PERSPECTIVE_ID.equals(desc.getId())) {
            return true;
        }

        if (desc instanceof PerspectiveDescriptor) {
            PerspectiveDescriptor pd = (PerspectiveDescriptor) desc;
            return pd.getOriginalId().equals(WebPerspectiveFactory.PERSPECTIVE_ID)
                    || pd.getOriginalId().equals(WebPerspectiveFactory.RUBY_PERSPECTIVE_ID)
                    || pd.getOriginalId().equals(WebPerspectiveFactory.RAILS_PERSPECTIVE_ID);
        }
        return false;
    }

    /**
     * Is the current perspective a descendant perspective of the original
     * perspective
     * 
     * @param desc
     * @return - true if same or descendant
     */
    public static boolean isSameOrDescendantPerspective(IPerspectiveDescriptor desc) {
        if (desc.getId().equals(WebPerspectiveFactory.PERSPECTIVE_ID)) {
            return true;
        }

        if (desc instanceof PerspectiveDescriptor) {
            PerspectiveDescriptor pd = (PerspectiveDescriptor) desc;
            return pd.getOriginalId().equals(WebPerspectiveFactory.PERSPECTIVE_ID);
        }
        return false;
    }
}
