/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
package com.aptana.ide.scripting.views;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mozilla.javascript.Scriptable;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.scripting.ScriptingPlugin;

/**
 * @author Paul Colton
 */
public class ProblemsView extends ScriptableView
{
	/**
	 * Fields
	 */
	private static final long serialVersionUID = 5003323167511939611L;
	private static String ID = "com.aptana.ide.js.ui.views.problemsView"; //$NON-NLS-1$

	/*
	 * Constructors
	 */

	/**
	 * ProblemsView
	 * 
	 * @param scope
	 * @param view
	 */
	public ProblemsView(Scriptable scope, IWorkbenchPart view)
	{
		super(scope, view, StringUtils.EMPTY);
	}

	/*
	 * Methods
	 */

	/**
	 * @see com.aptana.ide.scripting.views.ScriptableView#getView()
	 */
	public IWorkbenchPart getView()
	{
		IWorkbenchPart result = super.getView();

		if (result == null)
		{
			result = Views.getViewInternal(ID);

			updateListeners(result, null);
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.scripting.views.ScriptableView#getClassName()
	 */
	public String getClassName()
	{
		return "ProblemsView"; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.scripting.views.ScriptableView#showView(boolean)
	 */
	public void showView(boolean makeVisible)
	{
		final IWorkbench workbench = PlatformUI.getWorkbench();
		Display display = workbench.getDisplay();

		display.syncExec(new Runnable()
		{
			public void run()
			{
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

				try
				{
					page.showView(ID);
				}
				catch (PartInitException e)
				{
					IdeLog.logError(ScriptingPlugin.getDefault(), Messages.ProblemsView_Error, e);
				}
			}
		});
	}
}
