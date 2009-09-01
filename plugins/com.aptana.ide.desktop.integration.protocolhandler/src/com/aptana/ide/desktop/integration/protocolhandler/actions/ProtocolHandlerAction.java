package com.aptana.ide.desktop.integration.protocolhandler.actions;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.aptana.ide.desktop.integration.protocolhandler.ProtocolHandlerStartup;

public class ProtocolHandlerAction implements IWorkbenchWindowActionDelegate {

    private IWorkbenchWindow window;

    public void dispose() {
    }

    public void init(IWorkbenchWindow window) {
        this.window = window;
    }

    public void run(IAction action) {
        if (Platform.OS_WIN32.equals(Platform.getOS())) {
            ProtocolHandlerStartup.registerOnWindows();
        } else if (Platform.OS_LINUX.equals(Platform.getOS())) {
            ProtocolHandlerStartup.registerOnLinux();
        } else if (Platform.OS_MACOSX.equals(Platform.getOS())) {
            MessageDialog.openInformation(this.window.getShell(),
                    Messages.ProtocolHandlerAction_MacDialogTitle, Messages.ProtocolHandlerAction_MacDialogMessage);

            ProtocolHandlerStartup.registerOnMac();
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }

}
