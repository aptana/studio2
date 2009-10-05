package com.aptana.ide.documentation.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.documentation.DocumentationPlugin;

public class ReleaseNotesAction implements IWorkbenchWindowActionDelegate
{

	public void dispose()
	{
	}

	public void init(IWorkbenchWindow window)
	{
	}

	public void run(IAction action)
	{
		String releaseNotesUrl = System.getProperty(DocumentationPlugin.RELEASE_NOTES_URL_SYSTEM_PROPERTY,
				"http://www.aptana.org/tools/studio/releasenotes/"); //$NON-NLS-1$
		CoreUIUtils.openBrowserURL(releaseNotesUrl);
	}

	public void selectionChanged(IAction action, ISelection selection)
	{
	}

}
