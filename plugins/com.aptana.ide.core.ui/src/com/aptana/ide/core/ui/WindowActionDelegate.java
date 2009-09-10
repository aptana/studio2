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
import java.io.FileOutputStream;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.preferences.IPreferenceConstants;

/**
 * @author Paul Colton
 */
public class WindowActionDelegate implements IWorkbenchWindowActionDelegate
{

	private static final String CLEAR_LOG_ID = "com.aptana.ide.core.ui.actionSet.ClearLog"; //$NON-NLS-1$
	private static final String CLEAN_CONFIG_ID = "com.aptana.ide.core.ui.actionSet.CleanConfiguration"; //$NON-NLS-1$
	private static final String VIEW_LOG_ID = "com.aptana.ide.core.ui.actionSet.ViewLog"; //$NON-NLS-1$
	private static final String LEARN_MORE_ID = "com.aptana.ide.core.ui.actionSet.LearnMore"; //$NON-NLS-1$
	private static final String LICENSE_PREFERENCE_ID = "com.aptana.ide.intro.preferences.LicensePreferencePage"; //$NON-NLS-1$

	private IWorkbenchWindow window;

	/**
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose()
	{
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window)
	{
		this.window = window;
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action)
	{
		String id = action.getId();
		if (id.equals(VIEW_LOG_ID))
		{
			String logFile = getLogFile();

			try
			{
				WorkbenchHelper.openFile(new File(logFile), PlatformUI.getWorkbench().getActiveWorkbenchWindow());
			}
			catch (Exception e)
			{
				IdeLog.logError(CoreUIPlugin.getDefault(), StringUtils.format(
						Messages.WindowActionDelegate_UnableToOpenLogFile, logFile), e);
			}
		}
		else if (id.equals(CLEAR_LOG_ID))
		{
			if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), Messages.ClearLogConfirmTitle,
					Messages.ClearLogConfirmDescription))
			{
				return;
			}
			String logFile = getLogFile();

			try
			{
				File file = new File(logFile);
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				fileOutputStream.close();
			}
			catch (Exception e)
			{
				IdeLog.logError(CoreUIPlugin.getDefault(), StringUtils.format(
						Messages.WindowActionDelegate_UnableToOpenLogFile, logFile), e);
			}
		}
		else if (id.equals("Overview.Action")) //$NON-NLS-1$
		{
			String currentPerspectiveID = WebPerspectiveFactory.PERSPECTIVE_ID;
			try
			{
				IPerspectiveDescriptor desc = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.getPerspective();
				// If it is not one of ours, then just show the default
				if (!WebPerspectiveFactory.isSameOrDescendantPerspective(desc))
				{
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							Messages.WindowActionDelegate_AptanaOverviewErrorTitle,
							Messages.WindowActionDelegate_AptanaOverviewErrorMessage);
					return;
				}
			}
			catch (Exception e)
			{
			}
		}
		else if (id.equals(CLEAN_CONFIG_ID))
		{
			if (!MessageDialog
					.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							Messages.WindowActionDelegate_CleanConfigurationTitle,
							Messages.WindowActionDelegate_CleanConfigurationDescription))
			{
				return;
			}

			IPreferenceStore prefs = CoreUIPlugin.getDefault().getPreferenceStore();
			prefs.setValue(IPreferenceConstants.PREF_CLEAN_RESTART, true);
			window.getWorkbench().restart();
		}
		else if (id.equals(LEARN_MORE_ID))
		{
			PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(Display.getDefault().getActiveShell(),
					LICENSE_PREFERENCE_ID, new String[] { LICENSE_PREFERENCE_ID }, null);
			dialog.open();
		}
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
	}

	private static String getLogFile()
	{
		return System.getProperty("osgi.logfile"); //$NON-NLS-1$
	}

}
