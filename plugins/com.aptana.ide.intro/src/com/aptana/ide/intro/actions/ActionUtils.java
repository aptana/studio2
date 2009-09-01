package com.aptana.ide.intro.actions;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.intro.IntroPlugin;

public class ActionUtils {

    private static final String ECLIPSE_UI_PLUGIN_ID = "org.eclipse.ui"; //$NON-NLS-1$
    private static final String ACTION_SETS = "actionSets"; //$NON-NLS-1$
    private static final String ACTION_SETS_ID = "id"; //$NON-NLS-1$
    private static final String APTANA_ACTION_SET_ID = "com.aptana.ide.intro.actionSet.MyAptanaToolbar"; //$NON-NLS-1$
    private static final String APTANA_ACTION_ID = "com.aptana.ide.intro.actions.MyAptana"; //$NON-NLS-1$

    private static final String ACTION = "action"; //$NON-NLS-1$
    private static final String ACTION_ICON = "icon"; //$NON-NLS-1$
    private static final String ACTION_LABEL = "label"; //$NON-NLS-1$
    private static final String ACTION_CLASS = "class"; //$NON-NLS-1$
    private static final String MENU = "menu"; //$NON-NLS-1$
    private static final String MENU_ID = "id"; //$NON-NLS-1$
    private static final String MENU_GROUP_MARKER = "groupMarker"; //$NON-NLS-1$
    private static final String MENU_GROUP_MARKER_NAME = "name"; //$NON-NLS-1$
    private static final String MENU_SEPARATOR = "separator"; //$NON-NLS-1$
    private static final String MENUBAR_PATH = "menubarPath"; //$NON-NLS-1$

    private static final ImageRegistry imageRegistry = new ImageRegistry();

    public static void buildMenu(Menu menu, final IWorkbenchWindow window) {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        if (registry == null) {
            return;
        }
        IExtensionPoint extension = registry.getExtensionPoint(
                ECLIPSE_UI_PLUGIN_ID, ACTION_SETS);
        if (extension == null) {
            return;
        }

        IConfigurationElement actionSets = null;
        List<IConfigurationElement> actionElements = new ArrayList<IConfigurationElement>();
        IConfigurationElement[] elements = extension.getConfigurationElements();
        for (IConfigurationElement element : elements) {
            String id = element.getAttribute(ACTION_SETS_ID);

            if (id.startsWith(APTANA_ACTION_SET_ID)) {
                if (id.equals(APTANA_ACTION_SET_ID)) {
                    // the main actionSets
                    actionSets = element;
                }
                IConfigurationElement[] childElements = element
                        .getChildren(ACTION);
                for (IConfigurationElement child : childElements) {
                    actionElements.add(child);
                }
            }
        }

        if (actionSets != null) {
            IConfigurationElement[] menuElements = actionSets.getChildren(MENU);

            for (IConfigurationElement menuElement : menuElements) {
                String menuId = menuElement.getAttribute(MENU_ID);
                IConfigurationElement[] menuChildElements = menuElement
                        .getChildren();
                for (IConfigurationElement menuChildElement : menuChildElements) {
                    if (MENU_GROUP_MARKER.equals(menuChildElement.getName())) {
                        String groupId = menuChildElement
                                .getAttribute(MENU_GROUP_MARKER_NAME);
                        String toolbarPath = menuId + "/" //$NON-NLS-1$
                                + groupId;

                        List<IConfigurationElement> actionsList = getGroupedActionConfElement(
                                actionElements, toolbarPath);
                        for (IConfigurationElement actionElement : actionsList) {
                            MenuItem item = new MenuItem(menu, SWT.PUSH);
                            item.setText(actionElement
                                    .getAttribute(ACTION_LABEL));
                            String imageKey = actionElement
                                    .getAttribute(ACTION_ICON);
                            if (imageKey != null && !"".equals(imageKey //$NON-NLS-1$
                                    .trim())) {
                                item.setImage(getIcon(imageKey));
                            }
                            final IWorkbenchWindowActionDelegate delegate;
                            try {
                                delegate = (IWorkbenchWindowActionDelegate) actionElement
                                        .createExecutableExtension(ACTION_CLASS);
                                if (delegate instanceof SignInSignOutAction) {
                                    item
                                            .setText(((SignInSignOutAction) delegate)
                                                    .getText());
                                }
                                item
                                        .addSelectionListener(new SelectionAdapter() {

                                            public void widgetSelected(
                                                    SelectionEvent e) {
                                                delegate.init(window);
                                                delegate.run(new Action() {
                                                    @Override
                                                    public String getId() {
                                                        return APTANA_ACTION_ID;
                                                    }
                                                });
                                            }

                                        });
                            } catch (CoreException e) {
                                IdeLog
                                        .logError(
                                                IntroPlugin.getDefault(),
                                                "Failed to parse the extensions for My Aptana actions",
                                                e);
                            }
                        }
                    } else if (MENU_SEPARATOR
                            .equals(menuChildElement.getName())) {
                        new MenuItem(menu, SWT.SEPARATOR);
                    }
                }
            }
        }
    }

    private static List<IConfigurationElement> getGroupedActionConfElement(
            List<IConfigurationElement> elements, String menubarPath) {
        List<IConfigurationElement> list = new ArrayList<IConfigurationElement>();

        for (IConfigurationElement element : elements) {
            if (menubarPath.equals(element.getAttribute(MENUBAR_PATH))) {
                list.add(element);
            }
        }
        return list;
    }

    private static Image getIcon(String imageKey) {
        if (imageRegistry.get(imageKey) == null) {
            ImageDescriptor imageDescriptor = AbstractUIPlugin
                    .imageDescriptorFromPlugin(IntroPlugin.PLUGIN_ID, imageKey);
            try {
                imageRegistry.put(imageKey, imageDescriptor.createImage(true));
            } catch (Exception e) {
                IdeLog.logError(IntroPlugin.getDefault(), MessageFormat.format(
                        "Failed to get icon at path {0}", imageKey), e);
            }
        }
        return imageRegistry.get(imageKey);
    }

}
