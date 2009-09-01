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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.GregorianCalendar;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.browser.WorkbenchBrowserSupport;
import org.osgi.framework.Bundle;

import com.aptana.ide.core.IdeLog;

/**
 * @author Paul Colton
 */
public abstract class AbstractPerspectiveFactory
{

	/**
	 * Protected constructor for utility class.
	 */
	protected AbstractPerspectiveFactory()
	{

	}

	/**
	 * showUIHelpPage
	 * 
	 * @param fullURL
	 * @return URL
	 */
	public static URL findPage(String fullURL)
	{
		IPath p = new Path(fullURL);

		Bundle bundle = Platform.getBundle("com.aptana.ide.core.ui"); //$NON-NLS-1$

		String rootURL = p.lastSegment();
		p = p.removeLastSegments(1);
		String baseURL = p.toPortableString();

		try
		{
			// extracts the folder onto disk
			URL folderURL = FileLocator.find(bundle, new Path(baseURL), null);
			FileLocator.toFileURL(folderURL);

			URL fileURL = FileLocator.find(bundle, new Path(baseURL + File.separatorChar + rootURL), null);
			URL contentURL = FileLocator.toFileURL(fileURL);
			return contentURL;
		}
		catch (IOException e)
		{
			return null;
		}
	}

	/**
	 * showUIHelpPage
	 * 
	 * @param id
	 */
	public static void showUIHelpPage(String id)
	{
		// String rootURL = null;
		//
		// String baseUrl = "content/uimap/"; //$NON-NLS-1$
		// if (id.equals(WebPerspectiveFactory.PERSPECTIVE_ID))
		// {
		// rootURL = "uimap.htm"; //$NON-NLS-1$
		// }
		// else
		// {
		// return;
		// }
		//
		// try
		// {
		// IWebBrowser b = WorkbenchBrowserSupport.getInstance().createBrowser(IWorkbenchBrowserSupport.AS_EDITOR,
		// BROWSER_ID, Messages.AbstractPerspectiveFactory_OverviewTitle,
		// Messages.AbstractPerspectiveFactory_OverviewMessage);
		// Bundle bundle = Platform.getBundle("com.aptana.ide.core.ui"); //$NON-NLS-1$
		//
		// // extracts the folder onto disk
		// URL folderURL = FileLocator.find(bundle, new Path(baseUrl), null);
		// FileLocator.toFileURL(folderURL);
		//
		// URL fileUrl = FileLocator.find(bundle, new Path(baseUrl + rootURL), null);
		// URL contentURL = FileLocator.toFileURL(fileUrl);
		//
		// b.openURL(contentURL);
		// }
		// catch (PartInitException e)
		// {
		// IdeLog.logError(CoreUIPlugin.getDefault(), Messages.AbstractPerspectiveFactory_ErrorInitializingUiHelpPage,
		// e);
		// }
		// catch (Exception e)
		// {
		// IdeLog.logError(CoreUIPlugin.getDefault(), Messages.AbstractPerspectiveFactory_ErrorInitializingUiHelpPage,
		// e);
		// }
	}

	/**
	 * showReleaseNotes
	 */
	public static void showReleaseNotes()
	{
		String baseUrl = "content/release_notes/"; //$NON-NLS-1$
		String rootURL = "release_notes.html"; //$NON-NLS-1$
		long lastModified = getAptanaLastModified();

		try
		{
			IWebBrowser b = WorkbenchBrowserSupport
					.getInstance()
					.createBrowser(
							IWorkbenchBrowserSupport.AS_EDITOR,
							"release_notes", Messages.AbstractPerspectiveFactory_AptanaReleaseNotes, Messages.AbstractPerspectiveFactory_AptanaReleaseNotes); //$NON-NLS-1$
			Bundle bundle = Platform.getBundle("com.aptana.ide.core.ui"); //$NON-NLS-1$
			bundle.getLastModified();

			// extracts the folder onto disk
			URL folderURL = FileLocator.find(bundle, new Path(baseUrl), null);
			FileLocator.toFileURL(folderURL);

			URL fileUrl = FileLocator.find(bundle, new Path(baseUrl + rootURL), null);
			URL contentURL = FileLocator.toFileURL(fileUrl);
			URL newURL = new URL(contentURL.toExternalForm() + "?lm=" + lastModified); //$NON-NLS-1$
			b.openURL(newURL);
		}
		catch (PartInitException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.AbstractPerspectiveFactory_ErrorInitializingUiHelpPage,
					e);
		}
		catch (Exception e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.AbstractPerspectiveFactory_ErrorInitializingUiHelpPage,
					e);
		}
	}

	/**
	 * The time in millis since this was last modified.
	 * 
	 * @return long
	 */
	public static long getAptanaLastModified()
	{
		File f = CoreUIUtils.getPluginFile(CoreUIPlugin.getDefault());
		if (f == null)
		{
		    return new GregorianCalendar().getTimeInMillis();
		}
		return f.lastModified();
	}

}
