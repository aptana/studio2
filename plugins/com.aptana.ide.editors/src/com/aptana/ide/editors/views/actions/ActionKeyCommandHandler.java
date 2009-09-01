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
package com.aptana.ide.editors.views.actions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;

/**
 * @author Paul Colton
 */
public class ActionKeyCommandHandler implements IHandler
{
	/**
	 * @see org.eclipse.core.commands.IHandler#addHandlerListener(org.eclipse.core.commands.IHandlerListener)
	 */
	public void addHandlerListener(IHandlerListener handlerListener)
	{
	}

	/**
	 * @see org.eclipse.core.commands.IHandler#dispose()
	 */
	public void dispose()
	{
	}

	/**
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		// TODO: update to 3.1.2 to resolve this
		// TODO: event.getParameter("action");
//		String actionName = "/JavaScript/Toggle Comments"; //$NON-NLS-1$
//
//		IWorkbenchPart part = CoreUIUtils.getViewInternal("com.aptana.ide.js.ui.views.actionsView", null); //$NON-NLS-1$
//
//		if (part != null)
//		{
//			ActionsView actionsView = (ActionsView) part;
//
//			if (actionsView != null)
//			{
//				actionsView.fireAction(actionName);
//			}
//		}

		return null;
	}

	/**
	 * @see org.eclipse.core.commands.IHandler#isEnabled()
	 */
	public boolean isEnabled()
	{
		return true;
	}

	/**
	 * @see org.eclipse.core.commands.IHandler#isHandled()
	 */
	public boolean isHandled()
	{
		return true;
	}

	/**
	 * @see org.eclipse.core.commands.IHandler#removeHandlerListener(org.eclipse.core.commands.IHandlerListener)
	 */
	public void removeHandlerListener(IHandlerListener handlerListener)
	{
	}
}
