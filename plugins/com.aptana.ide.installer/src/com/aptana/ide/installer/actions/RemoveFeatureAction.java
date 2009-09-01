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
package com.aptana.ide.installer.actions;

import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.installer.Activator;
import com.aptana.ide.update.FeatureUtil;
import com.aptana.ide.update.manager.IPlugin;
import com.aptana.ide.update.manager.PluginManagerException;

/**
 * Remove a feature.
 * 
 * @author cwilliams
 */
public class RemoveFeatureAction implements IObjectActionDelegate, IViewActionDelegate
{

	private IViewPart view;
	private IPlugin selectedFeature;

	/**
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
	 *      org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action)
	{
		boolean okay = MessageDialog.openConfirm(view.getViewSite().getShell(), Messages
				.getString("RemoveFeatureAction.TTL_Are_you_sure"), MessageFormat.format( //$NON-NLS-1$
				Messages.getString("RemoveFeatureAction.MSG_Uninstall_feature_prompt"), selectedFeature //$NON-NLS-1$
						.getName()));
		if (!okay)
			return;

		UIJob job = new UIJob(MessageFormat.format(Messages.getString("RemoveFeatureAction.Uninstall_job_title"), //$NON-NLS-1$
				selectedFeature.getName()))
		{
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				try
				{
					return Activator.getDefault().getPluginManager().uninstall(selectedFeature, monitor);
				}
				catch (PluginManagerException e)
				{
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, e.getMessage(), e);
				}
			}
		};
		job.schedule();
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
		this.selectedFeature = null;
		if (selection != null && !selection.isEmpty())
		{
			if (selection instanceof IStructuredSelection)
			{
				IStructuredSelection sel = (IStructuredSelection) selection;
				Object element = sel.getFirstElement();
				if (element instanceof IPlugin)
				{
					IPlugin ref = (IPlugin) element;
					IPlugin ref2 = FeatureUtil.findInstalledPlugin(ref.getId());

					if (ref2 != null)
					{
						URL url = ref2.getURL();
						String protocol = url.getProtocol();
						if (protocol.equals("file")) //$NON-NLS-1$
						{
							this.selectedFeature = ref2;
						}
					}
				}
			}
		}
		action.setEnabled(selectedFeature != null);
	}

	/**
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view)
	{
		this.view = view;
	}

}
