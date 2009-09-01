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
package com.aptana.ide.intro.actions;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.wizards.IWizardDescriptor;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.ui.AptanaNavigator;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.wizards.BaseWizard;
import com.aptana.ide.intro.IntroPlugin;
import com.aptana.ide.server.jetty.comet.CometClient;
import com.aptana.ide.server.jetty.comet.CometConstants;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class OpenWizardClient extends CometClient
{

	/**
	 * WIZARD_CHANNEL
	 */
	public static final String WIZARD_CHANNEL = "/portal/wizard"; //$NON-NLS-1$

	/**
	 * DATA
	 */
	public static final String DATA = "data"; //$NON-NLS-1$

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getResponse(java.lang.String, java.lang.Object)
	 */
	protected Object getResponse(String toChannel, Object request)
	{
		if (WIZARD_CHANNEL.equals(toChannel))
		{
			if (request instanceof Map)
			{
				Map requestData = (Map) request;
				if (requestData.containsKey(CometConstants.ID) && requestData.containsKey(CometConstants.REQUEST))
				{
					final String id = requestData.get(CometConstants.ID).toString();
					final String type = requestData.get(CometConstants.REQUEST).toString();
					final Object data = requestData.get(DATA);
					UIJob job = new UIJob(Messages.OpenWizardClient_Job_OpenWizard)
					{

						public IStatus runInUIThread(IProgressMonitor monitor)
						{
							try
							{
								if (id != null)
								{
									IWizardDescriptor descriptor = null;
									if (ParameterConstants.IMPORT_WIZARD_TYPE.equals(type))
									{
										descriptor = PlatformUI.getWorkbench().getImportWizardRegistry().findWizard(id);
									}
									else if (ParameterConstants.EXPORT_WIZARD_TYPE.equals(type))
									{
										descriptor = PlatformUI.getWorkbench().getExportWizardRegistry().findWizard(id);
									}
									else if (ParameterConstants.NEW_WIZARD_TYPE.equals(type))
									{
										descriptor = PlatformUI.getWorkbench().getNewWizardRegistry().findWizard(id);
									}
									if (descriptor != null)
									{
										IWorkbenchWizard wizard = descriptor.createWizard();
										IStructuredSelection selectionToPass = StructuredSelection.EMPTY;
										IWorkbenchPart[] views = CoreUIUtils.getViewsInternal(AptanaNavigator.ID);
										if (views != null && views.length == 1)
										{
											if (views[0] instanceof AptanaNavigator)
											{
												ISelection selection = ((AptanaNavigator) views[0]).getTreeViewer()
														.getSelection();
												if (selection instanceof IStructuredSelection)
												{
													selectionToPass = (IStructuredSelection) selection;
												}

											}
										}
										wizard.init(IntroPlugin.getDefault().getWorkbench(), selectionToPass);
										if (data != null && wizard instanceof BaseWizard)
										{
											((BaseWizard) wizard).setInitializationData(data);
										}
										if (wizard instanceof IWizard)
										{
											WizardDialog dialog = new WizardDialog(Display.getDefault()
													.getActiveShell(), (IWizard) wizard);
											dialog.create();
											if (wizard.getPageCount() > 0)
											{
												dialog.open();
											}
										}
									}
								}
							}
							catch (Exception e)
							{
								IdeLog.logInfo(IntroPlugin.getDefault(), Messages.OpenWizardClient_INF_ErrorLaunchWizard, e);
							}
							return Status.OK_STATUS;
						}

					};
					job.schedule();
				}
			}

		}
		return null;
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getSubscriptionIDs()
	 */
	protected String[] getSubscriptionIDs()
	{
		return new String[] { WIZARD_CHANNEL };
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getID(java.lang.String)
	 */
	protected String getID(String msgId)
	{
		return WIZARD_CHANNEL;
	}

}
