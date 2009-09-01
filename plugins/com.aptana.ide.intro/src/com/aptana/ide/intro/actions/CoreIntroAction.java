/**
 * Copyright (c) 2005-2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.intro.actions;

import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public abstract class CoreIntroAction implements IIntroAction
{

	/**
	 * @see org.eclipse.ui.intro.config.IIntroAction#run(org.eclipse.ui.intro.IIntroSite, java.util.Properties)
	 */
	public final void run(IIntroSite site, Properties params)
	{
		try
		{
			runDelegate(site, params);
		}
		catch (Exception e)
		{
			if (params != null)
			{
				final String errorMessage = params.getProperty(ParameterConstants.ERROR_MESSAGE);
				if (errorMessage != null)
				{
					UIJob errorJob = new UIJob(Messages.CoreIntroAction_Job_ErrorRunAction)
					{

						public IStatus runInUIThread(IProgressMonitor monitor)
						{
							MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.CoreIntroAction_ERR_RunAction,
									errorMessage);
							return Status.OK_STATUS;
						}

					};
					errorJob.schedule();
				}
			}
		}
	}

	/**
	 * This method will be called first when IIntroAction.run is called. That calling method will always catch all
	 * exception and if an error message exists it will be displayed to the user.
	 * 
	 * @param site
	 * @param params
	 * @throws Exception
	 */
	public abstract void runDelegate(IIntroSite site, Properties params) throws Exception;

}
