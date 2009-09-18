package com.aptana.ide.intro.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.navigator.CommonNavigator;

import com.aptana.ide.core.ui.CoreUIUtils;

public class ClientUtils {

    private static final String NAVIGATOR_ID = "com.aptana.ide.ui.io.fileExplorerView"; //$NON-NLS-1$

    public static IStructuredSelection getNavigatorSelection() {
        IWorkbenchPart[] views = CoreUIUtils.getViewsInternal(NAVIGATOR_ID);
        if (views != null && views.length == 1) {
            if (views[0] instanceof CommonNavigator) {
                return (IStructuredSelection) ((CommonNavigator) views[0]).getCommonViewer()
                        .getSelection();
            }
        }
        return StructuredSelection.EMPTY;
    }
}
