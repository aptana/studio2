package com.aptana.ide.pathtools;

import java.io.File;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;

import com.aptana.ide.pathtools.handlers.Utilities;
import com.aptana.ide.pathtools.preferences.PathtoolsPreferences;

public class ExplorePlacesActions implements IWorkbenchWindowPulldownDelegate2 {
	private Menu explorePlacesMenu;
	
	public void dispose() {
		if (explorePlacesMenu != null) {
			explorePlacesMenu.dispose();
		}
	}

	public void init(IWorkbenchWindow window) {}

	public void run(IAction action) {}

	public void selectionChanged(IAction action, ISelection selection) {}

	public Menu getMenu(Control parent) {
		return null;
	}
	
	public Menu getMenu(Menu parent) {
		if (explorePlacesMenu != null) {
			explorePlacesMenu.dispose();
		}
		explorePlacesMenu = new Menu(parent);
		final IPath workspaceLocation = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		MenuItem gotoWorkspace = new MenuItem(explorePlacesMenu, SWT.PUSH);
		gotoWorkspace.setText(MessageFormat.format(Messages.ExplorePlacesActions_TXT_WorkspaceFolder, workspaceLocation.toFile().getAbsolutePath()));
		gotoWorkspace.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				openFolder(workspaceLocation.toFile());
			}
		});
		MenuItem gotoWorkspaceMetadata = new MenuItem(explorePlacesMenu, SWT.PUSH);
		gotoWorkspaceMetadata.setText(MessageFormat.format(Messages.ExplorePlacesActions_TXT_WorkspaceMetadata,
                workspaceLocation.toFile().getAbsolutePath(), File.separator));
		gotoWorkspaceMetadata.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				openFolder(new File(workspaceLocation.toFile(), ".metadata")); //$NON-NLS-1$
			}
		});
		
		Location configurationLocation = Platform.getConfigurationLocation();
		if (configurationLocation != null) {
			final URL url = configurationLocation.getURL();
			if (url != null && new File(url.getFile()).exists()) {
				MenuItem gotoConfigurationFolder = new MenuItem(explorePlacesMenu, SWT.PUSH);
				gotoConfigurationFolder.setText(MessageFormat.format(Messages.ExplorePlacesActions_TXT_ConfigFolder, url.getFile()));
				gotoConfigurationFolder.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						openFolder(new File(url.getFile()));
					}
				});
			}
		}
		Location userDataLocation = Platform.getUserLocation();
		if (userDataLocation != null) {
			final URL url = userDataLocation.getURL();
			if (url != null && new File(url.getFile()).exists()) {
				MenuItem gotoUserFolder = new MenuItem(explorePlacesMenu, SWT.PUSH);
				gotoUserFolder.setText(MessageFormat.format(Messages.ExplorePlacesActions_TXT_UserDataFolder, url.getFile()));
				gotoUserFolder.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						openFolder(new File(url.getFile()));
					}
				});
			}
		}
		Location installLocation = Platform.getInstallLocation();
		if (installLocation != null) {
			final URL url = installLocation.getURL();
			if (url != null && new File(url.getFile()).exists()) {
				MenuItem gotoInstallFolder = new MenuItem(explorePlacesMenu, SWT.PUSH);
				gotoInstallFolder.setText(MessageFormat.format(Messages.ExplorePlacesActions_TXT_InstallFolder, url.getFile()));
				gotoInstallFolder.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						openFolder(new File(url.getFile()));
					}
				});
			}
		}
		return explorePlacesMenu;
	}

	private static void openFolder(File file) {
        if (file != null && file.exists() && file.isDirectory()) {
            Utilities.launch(
                    Activator.getDefault().getPreferenceStore().getString(
                            PathtoolsPreferences.FOLDER_EXPLORE_COMMAND_KEY), file);
        }
    }

}
