package com.aptana.ide.documentation.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.aptana.ide.core.ui.CoreUIUtils;

public class ReleaseNotesAction implements IWorkbenchWindowActionDelegate {

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		CoreUIUtils
				.openBrowserURL("http://aptana.com/docs/index.php/ReleaseNotes"); //$NON-NLS-1$
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
