/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.ide.core.ui.actions;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.online.OnlineDetectionService;
import com.aptana.ide.core.online.OnlineDetectionService.StatusMode;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.CoreUIUtils;

public class SubmitBugAction implements IWorkbenchWindowActionDelegate {

	private static final String URL = "https://aptana.lighthouseapp.com/projects/35272-studio/tickets/new"; //$NON-NLS-1$

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		if (OnlineDetectionService.getInstance().getStatus() == StatusMode.OFFLINE) {
			// user is offline; pops up a warning message
			showOfflineWarning();
			return;
		}
		submitBug();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	private void showOfflineWarning() {
		MessageDialog.openWarning(CoreUIUtils.getActiveShell(),
				Messages.SubmitBugAction_Offline_Title,
				Messages.SubmitBugAction_Offline_Message);
	}

	private void submitBug() {
		try {
			IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
			if (support.isInternalWebBrowserAvailable()) {
				support.createBrowser(
						IWorkbenchBrowserSupport.NAVIGATION_BAR | IWorkbenchBrowserSupport.LOCATION_BAR
								| IWorkbenchBrowserSupport.AS_EDITOR | IWorkbenchBrowserSupport.STATUS, "ReportBug", //$NON-NLS-1$
						null, // Set the name to null. That way the browser tab will display the title of page loaded in the browser.
						null).openURL(new URL(URL));
			} else {
				support.getExternalBrowser().openURL(new URL(URL));
			}
		} catch (MalformedURLException e) {
			IdeLog.logError(CoreUIPlugin.getDefault(), MessageFormat.format(
					Messages.SubmitBugAction_ERR_Access, URL), e);
		} catch (PartInitException e) {
			IdeLog.logError(CoreUIPlugin.getDefault(), e.getLocalizedMessage(), e);
		}
	}
}
