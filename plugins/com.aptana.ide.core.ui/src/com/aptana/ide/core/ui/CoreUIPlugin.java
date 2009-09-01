/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.core.ui;

import java.net.Authenticator;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.update.internal.ui.UpdateUI;
import org.osgi.framework.BundleContext;

import com.aptana.ide.core.db.EventInfo;
import com.aptana.ide.core.db.EventLogger;
import com.aptana.ide.core.model.RESTServiceProvider;
import com.aptana.ide.core.online.OnlineDetectionService;
import com.aptana.ide.core.ui.install.PlatformValidatorPatcher;

/**
 * The main plugin class to be used in the desktop.
 */
public class CoreUIPlugin extends AbstractUIPlugin
{
	// The shared instance.
	private static CoreUIPlugin plugin;

	private static Map<String, Image> images = new HashMap<String, Image>();

	private AptanaAuthenticator aptanaAuth;

	/**
	 * The ID for this plug-in
	 */
	public static String ID = "com.aptana.ide.core.ui"; //$NON-NLS-1$

	/**
	 * WIZARD_EXTENSION_POINT
	 */
	public static final String WIZARD_EXTENSION_POINT = ID + ".wizard"; //$NON-NLS-1$

	private final IPartListener partListener = new PartListenerAdapter()
	{

		@Override
		public void partOpened(IWorkbenchPart part)
		{
			if (part instanceof IEditorPart)
			{
				checkPortal((IEditorPart) part, false);
				recordEditorOpening(part);
			}
		}

		@Override
		public void partClosed(IWorkbenchPart part)
		{
			if (part instanceof IEditorPart)
			{
				checkPortal((IEditorPart) part, true);
			}
		}

	};

	private final IWindowListener windowListener = new IWindowListener()
	{

		public void windowActivated(IWorkbenchWindow window)
		{
		}

		public void windowClosed(IWorkbenchWindow window)
		{
			IPartService partService = window.getPartService();
			if (partService != null)
			{
				partService.removePartListener(partListener);
			}
		}

		public void windowDeactivated(IWorkbenchWindow window)
		{
		}

		public void windowOpened(IWorkbenchWindow window)
		{
			IPartService partService = window.getPartService();
			if (partService != null)
			{
				partService.addPartListener(partListener);
			}
		}

	};

	private final RESTServiceListener serviceListener = new RESTServiceListener();

	/**
	 * This method is called upon plug-in activation
	 * 
	 * @param context
	 *            The context of the bundle within the framework
	 * @throws Exception
	 */
	public void start(BundleContext context) throws Exception
	{
		plugin = this;
		super.start(context);
		InitialRestartStartup.start();
		InitialStartup.start();
		PlatformValidatorPatcher.start();

		UpdateUI.getDefault(); // force Eclipse's Authenticator to register first so we can register after and "win"
		// TODO create some mechanism for forbidden paths contribution
		aptanaAuth = new AptanaAuthenticator();
		Authenticator.setDefault(aptanaAuth);

		Job job = new AutoOpenPerspectivesJob();
		job.setSystem(true);
		job.setPriority(Job.DECORATE);
		job.schedule(5000);

		addPartListener();
		RESTServiceProvider.addListener(serviceListener);
	}

	/**
	 * This method is called when the plug-in is stopped
	 * 
	 * @param context
	 *            The context of the bundle within the framework
	 * @throws Exception
	 */
	public void stop(BundleContext context) throws Exception
	{
		try
		{
			removePartListener();
			RESTServiceProvider.removeListener(serviceListener);
		}
		finally
		{
			super.stop(context);
			plugin = null;
		}
	}

	/**
	 * getImage
	 * 
	 * @param path
	 * @return Image
	 */
	public static Image getImage(String path)
	{
		if (images.get(path) == null)
		{
			ImageDescriptor id = getImageDescriptor(path);
			if (id == null)
			{
				return null;
			}

			Image i = id.createImage();
			images.put(path, i);

			return i;
		}
		return images.get(path);
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return An instance of CoreUIPlugin
	 */
	public static CoreUIPlugin getDefault()
	{
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path)
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
	}

	/**
	 * getPluginId
	 * 
	 * @return String
	 */
	public static String getPluginId()
	{
		return getDefault().getBundle().getSymbolicName();
	}

	/**
	 * getActivePage
	 * 
	 * @return IWorkbenchPage
	 */
	public static IWorkbenchPage getActivePage()
	{
		CoreUIPlugin plugin = getDefault();
		IWorkbenchWindow window = plugin.getWorkbench().getActiveWorkbenchWindow();
		if (window != null)
		{
			return window.getActivePage();
		}
		return null;
	}

	/**
	 * getActiveWorkbenchWindow
	 * 
	 * @return IWorkbenchWindow
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow()
	{
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	private void addPartListener()
	{
		UIJob job = new UIJob("Add part listener") //$NON-NLS-1$
		{

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				// Add actions to exiting windows.
				try
				{
					PlatformUI.getWorkbench();
				}
				catch (IllegalStateException e)
				{
					// workbench not ready yet! Re-schedule
					schedule(500);
					return Status.CANCEL_STATUS;
				}
				IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
				IPartService partService;
				for (IWorkbenchWindow workbenchWindow : workbenchWindows)
				{
					partService = workbenchWindow.getPartService();
					if (partService != null)
					{
						partService.addPartListener(partListener);
					}
				}

				// Listen on any future windows
				PlatformUI.getWorkbench().addWindowListener(windowListener);
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.setPriority(Job.SHORT);
		job.schedule();
	}

	private void removePartListener()
	{
		// Remove listener from windows.
		IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
		IPartService partService;
		for (IWorkbenchWindow workbenchWindow : workbenchWindows)
		{
			partService = workbenchWindow.getPartService();
			if (partService != null)
			{
				partService.removePartListener(partListener);
			}
		}
		PlatformUI.getWorkbench().removeWindowListener(windowListener);
	}

	private void checkPortal(IEditorPart part, boolean closed)
	{
		if ("com.aptana.ide.server.cloud.ui.PortalEditor".equals(part.getSite().getId())) //$NON-NLS-1$
		{
			OnlineDetectionService.getInstance().setDelay(closed);
		}
	}

	/**
	 * Records opening of editors for ping usage data. Only records first time a given editor was opened since last time
	 * ping data was sent/cleaned.
	 * 
	 * @param part
	 */
	private void recordEditorOpening(IWorkbenchPart part)
	{
		String keyName = "editor.opened";
		EventInfo[] events = EventLogger.getInstance().getEvents(keyName);
		String editorName = part.getClass().getName();
		if (events != null && events.length > 0)
		{
			for (EventInfo event : events)
			{
				String msg = event.getMessage();
				if (msg != null && msg.equals(editorName))
					return;
			}
		}
		EventLogger.getInstance().logEvent(keyName, editorName);
	}
}
