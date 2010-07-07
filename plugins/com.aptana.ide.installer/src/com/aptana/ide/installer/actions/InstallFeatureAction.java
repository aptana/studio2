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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.installer.Activator;
import com.aptana.ide.update.FeatureUtil;
import com.aptana.ide.update.manager.Plugin;
import com.aptana.ide.update.manager.PluginManagerException;

/**
 * Install a feature.
 * 
 * @author cwilliams
 */
public class InstallFeatureAction implements IObjectActionDelegate, IViewActionDelegate
{

	private Plugin[] selectedFeatures = null;

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
		if (selectedFeatures == null || selectedFeatures.length == 0)
		{
			Activator.launchWizard(false);
		}
		else
		{
			try
			{
				Activator.getDefault().getPluginManager().install(selectedFeatures, new NullProgressMonitor());
			}
			catch (PluginManagerException e)
			{
				IdeLog.logError(Activator.getDefault(), e.getMessage(), e);
				MessageDialog
						.openError(Display.getDefault().getActiveShell(),
								Messages.getString("InstallFeatureAction.ERR_TTL_Unable_install_plugin"), //$NON-NLS-1$
								Messages.getString("InstallFeatureAction.ERR_MSG_Unable_install_plugin") + selectedFeatures[0].getName()); //$NON-NLS-1$
			}
		}
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
		this.selectedFeatures = null;
		if (selection != null && !selection.isEmpty())
		{
			if (selection instanceof IStructuredSelection)
			{
				IStructuredSelection sel = (IStructuredSelection) selection;
				Object[] elements = sel.toArray();
				List<Plugin> features = new ArrayList<Plugin>();
				for (Object element : elements)
				{
					if (element instanceof Plugin)
					{
						Plugin ref = (Plugin) element;
						URL url = ref.getURL();
						String protocol = url.getProtocol();
						if (!protocol.equals("file") && //$NON-NLS-1$
								!FeatureUtil.isInstalled(ref.getId()))
						{
							features.add(ref);
						}

					}
				}
				selectedFeatures = features.toArray(new Plugin[features.size()]);
			}
		}
	}

	/**
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view)
	{
	}

}
