package com.aptana.ide.ui.io.navigator.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;

import com.aptana.ide.core.ui.PartListenerAdapter;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ide.ui.io.navigator.INavigatorDecorator;

public class NavigatorDecoratorLoader {

    private static final String NAVIGATOR_ID = "com.aptana.ide.ui.io.fileExplorerView"; //$NON-NLS-1$

    private static final String EXTENSION_NAME = "decorator"; //$NON-NLS-1$
    private static final String EXTENSION_POINT = IOUIPlugin.PLUGIN_ID
            + "." + EXTENSION_NAME; //$NON-NLS-1$
    private static final String CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$

    private static final IPartListener partListener = new PartListenerAdapter() {

        @Override
        public void partOpened(IWorkbenchPart part) {
            if (part instanceof IViewPart) {
                IViewPart viewPart = (IViewPart) part;
                if (NAVIGATOR_ID.equals(viewPart.getSite().getId())) {
                    IConfigurationElement[] elements = Platform
                            .getExtensionRegistry()
                            .getConfigurationElementsFor(EXTENSION_POINT);
                    for (IConfigurationElement element : elements) {
                        if (!EXTENSION_NAME.equals(element.getName())) {
                            continue;
                        }

                        String className = element
                                .getAttribute(CLASS_ATTRIBUTE);
                        if (className != null) {
                            try {
                                Object client = element
                                        .createExecutableExtension(CLASS_ATTRIBUTE);
                                if (client instanceof INavigatorDecorator) {
                                    Tree tree = ((CommonNavigator) viewPart)
                                            .getCommonViewer().getTree();
                                    ((INavigatorDecorator) client)
                                            .addDecorator(tree);
                                }
                            } catch (CoreException e) {
                                // ignores the exception
                            }
                        }
                    }
                }
            }
        }
    };

    public static void init() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.getDisplay().syncExec(new Runnable() {

            public void run() {
                workbench.getActiveWorkbenchWindow().getPartService()
                        .addPartListener(partListener);
            }

        });
    }

    private NavigatorDecoratorLoader() {
    }
}
