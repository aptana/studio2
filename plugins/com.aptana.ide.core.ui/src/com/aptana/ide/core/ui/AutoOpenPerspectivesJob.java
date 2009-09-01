package com.aptana.ide.core.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.internal.Workbench;

/**
 * A job to force open a new perspective if it is registered to do so. This lets us force open Aptana perspectives, 
 * like the RadRails perspective, after first install - without the plugin needing to perform an IStartup job that would 
 * forcibly load it's plugins even when they're not being used. This only makes sense for perspectives that are dependant on Aptana.
 * 
 * @author Chris Williams
 *
 */
public class AutoOpenPerspectivesJob extends Job {

	private static final String EXTENSION_POINT = "perspectives"; //$NON-NLS-1$
	private static final String ADDED_PERSPECTIVE = CoreUIPlugin.getPluginId() + ".forced_perspective_open."; //$NON-NLS-1$

	public AutoOpenPerspectivesJob() {
		super(Messages.AutoOpenPerspectivesJob_MSG_AutomaticallyOpeningNewPerspectives);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {	
		// wait until workbench is running
		final IWorkbench wb = PlatformUI.getWorkbench();
		if (wb == null) return Status.CANCEL_STATUS;
		if (wb instanceof Workbench) {
			Workbench wb1 = (Workbench) wb;
			while (!wb1.isRunning()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// ignore
				}
			}
		}
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		List<IConfigurationElement> elements = new ArrayList<IConfigurationElement>(
				Arrays.asList(registry.getConfigurationElementsFor(CoreUIPlugin
						.getPluginId(), EXTENSION_POINT)));
		for (IConfigurationElement configurationElement : elements) {
			String id = configurationElement.getAttribute("id"); //$NON-NLS-1$
			if (openPerspective(id)) break; // can only open one new perspective(?)
		}
		return Status.OK_STATUS;
	}

	private IPreferenceStore getPreferenceStore() {
		return CoreUIPlugin.getDefault().getPreferenceStore();
	}
	
	private boolean openPerspective(final String perspectiveId) {
		if (getPreferenceStore().getBoolean(ADDED_PERSPECTIVE + perspectiveId)) return false; // already forced it open before
		Display.getDefault().asyncExec(new Runnable() {
		
			public void run() {
				final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				try {
					PlatformUI.getWorkbench().showPerspective(perspectiveId, window);
					getPreferenceStore().setValue(ADDED_PERSPECTIVE + perspectiveId, true);
				} catch (WorkbenchException e) {
					// ignore
				}		
			}
		
		});		
		return true;
	}

}
