/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
package com.aptana.ide.server.core.launch.http;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.WorkbenchHelper;
import com.aptana.ide.server.core.HttpServerLaunchConfiguration;

/**
 * AbstractHttpServerLaunchConfigurationDelegate
 */
public abstract class AbstractHttpServerLaunchConfigurationDelegate implements ILaunchConfigurationDelegate
{
	/**
	 * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public abstract void launch(ILaunchConfiguration configuration, String mode, ILaunch launch,
			IProgressMonitor monitor) throws CoreException;

	/**
	 * showStartPage
	 *
	 * @param baseUrl
	 * @param configuration
	 * @return String
	 * @throws IOException
	 * @throws CoreException
	 */
	protected String showStartPage(String baseUrl, ILaunchConfiguration configuration) throws IOException,
			CoreException
	{
		HttpServerLaunchConfiguration config = new HttpServerLaunchConfiguration();
		config.load(configuration);

		if (baseUrl.endsWith("/")) //$NON-NLS-1$
		{
			baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
		}

		// Launch a browser
		String browserExe = config.getBrowserExe();

		String serverUrl = baseUrl;
		if (config.getStartActionType() == HttpServerLaunchConfiguration.START_ACTION_SPECIFIC_PAGE)
		{
			String resourcePath = config.getStartPagePath();
			if (resourcePath != null)
			{
				IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(resourcePath));
				if (resource != null)
				{
					serverUrl = baseUrl + resource.getFullPath().toPortableString();
				}
			}
		}
		else if (config.getStartActionType() == HttpServerLaunchConfiguration.START_ACTION_START_URL)
		{
			serverUrl = config.getStartPageUrl();
		}
		else if (config.getStartActionType() == HttpServerLaunchConfiguration.START_ACTION_CURRENT_PAGE)
		{
			// get the URL for the active editor from the main UI (note that this has to be done
			// with a syncExec
			// since launches occur on a non-UI thread.
			final String fBaseUrl = baseUrl;
			final StringBuffer activeEditorUrl = new StringBuffer();
			Display.getDefault().syncExec(new Runnable()
			{
				public void run()
				{
					IEditorInput editorInput = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
							.getActiveEditor().getEditorInput();
					if (editorInput != null && editorInput instanceof FileEditorInput)
					{
						FileEditorInput fileInput = (FileEditorInput) editorInput;
						IResource resource = fileInput.getFile();
						if (resource != null)
						{
							activeEditorUrl.append(fBaseUrl);
							activeEditorUrl.append(resource.getFullPath().toPortableString());
						}
					}
					else if (editorInput != null && editorInput instanceof IPathEditorInput)
					{
						IPathEditorInput fileInput = (IPathEditorInput) editorInput;
						IPath path = fileInput.getPath();
						if (path != null)
						{
							activeEditorUrl.append("file://"); // fBaseUrl); //$NON-NLS-1$
							activeEditorUrl.append(path.toPortableString());
						}
					}
				}
			});
			if (activeEditorUrl.length() > 0)
			{
				serverUrl = activeEditorUrl.toString();
			}
		}

		if (browserExe != null && !browserExe.equals(StringUtils.EMPTY) && new File(browserExe).exists())
		{
			if (System.getProperty("os.name").startsWith("Mac OS") == true) //$NON-NLS-1$ //$NON-NLS-2$
			{
				Runtime.getRuntime().exec(new String[] { "open", browserExe, serverUrl }); //$NON-NLS-1$
			}
			else
			{
				// Runtime.getRuntime().exec(browserExe + " \"" + serverUrl + "\"");
				Runtime.getRuntime().exec(new String[] { browserExe, serverUrl });
			}
		}
		else
		{
			WorkbenchHelper.launchBrowser(serverUrl);
		}
		/*
		 * } else handlePreviewPageNotSpecified();
		 */

		return serverUrl;
	}

	/*
	 * private void handlePreviewPageNotSpecified() throws CoreException { ServerCorePlugin.logError("No default web
	 * preview page set", null); final IStatus warning = new Status(IStatus.INFO,
	 * ServerCorePlugin.getDefault().getBundle().getSymbolicName(), IStatus.OK, "You have not specified a startup page
	 * to launch. \r\n\r\nYou can right click a file in the navigator to set the startup page.", null); //throw new
	 * CoreException(warning); getStandardDisplay().asyncExec(new Runnable() { public void run() {
	 * ErrorDialog.openError(null, "HTTP Server Launch", null, warning); } }); }
	 */

	/*
	 * private static Display getStandardDisplay() { Display display = Display.getCurrent(); if (display == null) {
	 * display = Display.getDefault(); } return display; }
	 */
}
