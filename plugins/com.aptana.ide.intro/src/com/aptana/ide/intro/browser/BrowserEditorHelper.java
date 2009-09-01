/**
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.intro.browser;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import com.aptana.ide.intro.IntroPlugin;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class BrowserEditorHelper
{

	/**
	 * Opens a browser editor configured with the parameters
	 * 
	 * @param url
	 * @param name
	 * @param image
	 * @param toolTip
	 * @param script
	 * @return - browser editor or null if editor opened was null or not an instance of CoreBrowserEditor
	 * @throws PartInitException
	 */
	public static CoreBrowserEditor openBrowserEditor(URL url, String name, ImageDescriptor image, String toolTip,
			String script) throws PartInitException
	{
		return openBrowserEditor(url, name, image, toolTip, script, false);
	}

	/**
	 * Opens a browser editor configured with the parameters
	 * 
	 * @param url
	 * @param name
	 * @param image
	 * @param toolTip
	 * @param script
	 * @param oneAllowed
	 * @return - browser editor or null if editor opened was null or not an instance of CoreBrowserEditor
	 * @throws PartInitException
	 */
	public static CoreBrowserEditor openBrowserEditor(URL url, String name, ImageDescriptor image, String toolTip,
			String script, boolean oneAllowed) throws PartInitException
	{
		String id = Long.toString(System.currentTimeMillis());
		if (oneAllowed)
		{
			String value = url.toExternalForm();
			if (!value.endsWith("?")) //$NON-NLS-1$
			{
				value += "?"; //$NON-NLS-1$
			}
			value += "hc=" + id; //$NON-NLS-1$
			try
			{
				url = new URL(value);
			}
			catch (MalformedURLException e)
			{
			}
		}
		CoreBrowserEditorInput input = new CoreBrowserEditorInput(url, SWT.NONE);
		if (oneAllowed)
		{
			input.setInputID(id);
		}
		input.setName(name);
		input.setOnlyOneAllowed(true);
		input.setToolTipText(toolTip);
		input.setImage(image);
		input.setScript(script);
		IWorkbenchWindow window = IntroPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		if (window != null)
		{
			IWorkbenchPage page = window.getActivePage();
			IEditorPart part = IDE.openEditor(page, input, CoreBrowserEditor.ID);
			if (part instanceof CoreBrowserEditor)
			{
				return (CoreBrowserEditor) part;
			}
		}
		return null;
	}

	/**
	 * Simplest method for opening browser editor a url. Uses defaults of editor.
	 * 
	 * @param url
	 * @param oneAllowed
	 * @return - browser editor or null if editor opened was null or not an instance of CoreBrowserEditor
	 * @throws PartInitException
	 */
	public static CoreBrowserEditor openBrowserEditor(URL url, boolean oneAllowed) throws PartInitException
	{
		return openBrowserEditor(url, Messages.BrowserEditorHelper_BrowserName, null, null, null, oneAllowed);
	}

	/**
	 * Simplest method for opening browser editor a url. Uses defaults of editor.
	 * 
	 * @param url
	 * @return - browser editor or null if editor opened was null or not an instance of CoreBrowserEditor
	 * @throws PartInitException
	 */
	public static CoreBrowserEditor openBrowserEditor(URL url) throws PartInitException
	{
		return openBrowserEditor(url, Messages.BrowserEditorHelper_BrowserName, null, null, null, false);
	}
}
