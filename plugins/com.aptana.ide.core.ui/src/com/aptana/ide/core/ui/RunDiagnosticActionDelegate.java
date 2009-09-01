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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class RunDiagnosticActionDelegate implements IWorkbenchWindowActionDelegate
{

	private static final String EXTENSION_NAME = "diagnosis"; //$NON-NLS-1$
	private static final String EXTENSION_POINT = CoreUIPlugin.ID + "." + EXTENSION_NAME; //$NON-NLS-1$
	private static final String CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$
	private static final String ORDINAL_ATTRIBUTE = "ordinal"; //$NON-NLS-1$
	private static final String SUBMIT_BUG_ATTRIBUTE = "submit-bug"; //$NON-NLS-1$

	private IWorkbenchWindow fWindow;

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
		fWindow = window;
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action)
	{
		final DiagnosticDialog dialog = new DiagnosticDialog(fWindow.getShell());
		dialog.open();

		Job job = new Job("Getting Diagnostic Logs") { //$NON-NLS-1$

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				final String content = getLogContent();
				CoreUIUtils.getDisplay().asyncExec(new Runnable()
				{

					public void run()
					{
						dialog.append(content);
					}

				});
				return Status.OK_STATUS;
			}

		};
		job.setSystem(true);
		job.schedule();

	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
	}

	public static String getLogContent()
	{
		return getLogContent(false);
	}

	public static String getLogContent(boolean forSubmitBug)
	{
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT);
		// sorts the extension points by the ordinal field
		Map<Integer, List<IConfigurationElement>> ordinalElements = new TreeMap<Integer, List<IConfigurationElement>>();
		List<IConfigurationElement> elementList;
		List<IConfigurationElement> otherElements = new ArrayList<IConfigurationElement>();
		String ordinalAttr;
		int ordinal;
		for (IConfigurationElement element : elements)
		{
			if (element.getName().equals(EXTENSION_NAME))
			{
				ordinalAttr = element.getAttribute(ORDINAL_ATTRIBUTE);
				if (ordinalAttr != null)
				{
					try
					{
						ordinal = Integer.parseInt(ordinalAttr);
						elementList = ordinalElements.get(ordinal);
						if (elementList == null)
						{
							elementList = new ArrayList<IConfigurationElement>();
							ordinalElements.put(ordinal, elementList);
						}
						elementList.add(element);
						continue;
					}
					catch (NumberFormatException e)
					{
					}
				}
				otherElements.add(element);
			}
		}
		List<IConfigurationElement> list = new ArrayList<IConfigurationElement>();
		for (Integer key : ordinalElements.keySet())
		{
			list.addAll(ordinalElements.get(key));
		}
		list.addAll(otherElements);

		StringBuilder content = new StringBuilder();
		String className;
		String submitBugAttr;
		for (IConfigurationElement element : list)
		{
			className = element.getAttribute(CLASS_ATTRIBUTE);
			if (className != null)
			{
				try
				{
					Object client = element.createExecutableExtension(CLASS_ATTRIBUTE);
					if (client instanceof IDiagnosticLog)
					{
						if (forSubmitBug)
						{
							submitBugAttr = element.getAttribute(SUBMIT_BUG_ATTRIBUTE);
							if (submitBugAttr != null && !Boolean.parseBoolean(submitBugAttr))
							{
								// skips the specific extension
								continue;
							}
						}
						String log = ((IDiagnosticLog) client).getLog();
						if (log != null && log.length() > 0)
						{
							content.append(log);
							content.append("\n"); //$NON-NLS-1$
						}
					}
				}
				catch (CoreException e)
				{
				}
			}
		}
		return content.toString();
	}

}
