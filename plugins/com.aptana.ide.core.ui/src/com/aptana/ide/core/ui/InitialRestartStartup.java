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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.update.internal.ui.UpdateUI;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.preferences.IPreferenceConstants;

/**
 * @author paul
 */
public class InitialRestartStartup
{
	private static final String PROP_VM = "eclipse.vm"; //$NON-NLS-1$
	private static final String PROP_VMARGS = "eclipse.vmargs"; //$NON-NLS-1$
	private static final String PROP_COMMANDS = "eclipse.commands"; //$NON-NLS-1$
	private static final String PROP_EXIT_CODE = "eclipse.exitcode"; //$NON-NLS-1$
	private static final String PROP_EXIT_DATA = "eclipse.exitdata"; //$NON-NLS-1$
	private static final String CMD_VMARGS = "-vmargs"; //$NON-NLS-1$
	private static final String NEW_LINE = "\n"; //$NON-NLS-1$

	public InitialRestartStartup()
	{
	}

	public static void start()
	{
		try
		{
			IPreferenceStore preferences = CoreUIPlugin.getDefault().getPreferenceStore();

			IdeLog.logInfo(CoreUIPlugin.getDefault(),
					StringUtils.format(Messages.InitialRestartStartup_INF_CleanPreference, preferences.getBoolean(IPreferenceConstants.PREF_CLEAN_RESTART)));

			if (preferences.getBoolean(IPreferenceConstants.PREF_CLEAN_RESTART))
			{
				preferences.setValue(IPreferenceConstants.PREF_CLEAN_RESTART, false);
				System.setProperty(PROP_EXIT_CODE, Integer.toString(24));

				String commandLine = buildCommandLine("-clean"); //$NON-NLS-1$
				if (commandLine != null)
				{
					IdeLog.logInfo(CoreUIPlugin.getDefault(), Messages.InitialRestartStartup_INF_NewCommandLine + commandLine);
					System.setProperty(PROP_EXIT_DATA, commandLine);
					restartIDE();
				}
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.InitialRestartStartup_ERR_UnableToCleanConfiguration, e);
		}

		IdeLog.flushCache();
	}

	private static void restartIDE()
	{
		UIJob job = new UIJob(Messages.InitialRestartStartup_MSG_RestartingIDE)
		{

			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				Job[] jobs = Job.getJobManager().find(Workbench.EARLY_STARTUP_FAMILY);
				if( jobs.length > 0 ) {
					for(Job job : jobs ){
						if ( job.getState() != Job.NONE) {
							schedule(100);
							return Status.OK_STATUS;
						}
					}
				}
                UpdateUI.requestRestart(true);

				return Status.OK_STATUS;
			}

		};
		job.setPriority(UIJob.INTERACTIVE);
		job.schedule();
	}

	/**
	 * Create and return a string with command line options for eclipse.exe that will launch a new workbench that is the
	 * same as the currently running one, but using the argument directory as its workspace. Pulled from
	 * OpenWorkspaceAction
	 * 
	 * @param command
	 *            Note--must be separated by \n
	 * @return a string of command line options or null on error
	 */
	private static String buildCommandLine(String commands)
	{
		String property = System.getProperty(PROP_VM);
		if (property == null)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.InitialRestartStartup_ERR_VMArgumentsNullForRestartOfIDE);
			return null;
		}

		StringBuffer result = new StringBuffer(512);
		result.append(property);
		result.append(NEW_LINE);

		// append the vmargs and commands. Assume that these already end in \n
		String vmargs = System.getProperty(PROP_VMARGS);
		if (vmargs != null)
		{
			result.append(vmargs);
		}

		// append the rest of the args, replacing or adding -data as required
		property = System.getProperty(PROP_COMMANDS);
		if (property == null)
		{
			result.append(commands);
			result.append(NEW_LINE);
		}
		else
		{
			result.append(commands);
			result.append(NEW_LINE);
			result.append(property);
		}

		// put the vmargs back at the very end (the eclipse.commands property
		// already contains the -vm arg)
		if (vmargs != null)
		{
			result.append(CMD_VMARGS);
			result.append(NEW_LINE);
			result.append(vmargs);
		}

		return result.toString();
	}
}
