package com.aptana.ide.intro.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.aptana.ide.core.ui.CoreUIUtils;

public class AptanaURLAction implements IWorkbenchWindowActionDelegate {

    private static final String HOME_URL = "http://www.aptana.com"; //$NON-NLS-1$

    public void dispose() {
    }

    public void init(IWorkbenchWindow window) {
    }

    public void run(IAction action) {
        CoreUIUtils.openBrowserURL(HOME_URL);
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }

}
