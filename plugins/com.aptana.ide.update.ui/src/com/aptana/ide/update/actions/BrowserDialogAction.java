package com.aptana.ide.update.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.actions.SubmitBugDialog;
import com.aptana.ide.update.preferences.IPreferenceConstants;
import com.aptana.ide.update.ui.BrowserDialog;
import com.aptana.ide.update.ui.UpdateUIActivator;

public class BrowserDialogAction implements IWorkbenchWindowActionDelegate{

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		
	}

	public void run(IAction action) {
		BrowserDialog dialog = new BrowserDialog(CoreUIUtils.getActiveShell(),
				UpdateUIActivator.getDefault().getPreferenceStore().getString(IPreferenceConstants.ANNOUNCEMENT_URL_PREFIX) + "announce.php", 450, 450);
		dialog.open();
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

}
