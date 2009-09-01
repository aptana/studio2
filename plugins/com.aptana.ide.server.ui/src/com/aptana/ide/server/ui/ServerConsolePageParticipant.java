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
package com.aptana.ide.server.ui;

import java.util.Arrays;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.part.IPageBookViewPage;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.server.ServerCore;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.ui.views.GenericServersView;

/**
 * @author Pavel Petrochenko
 */
public class ServerConsolePageParticipant implements IConsolePageParticipant
{

	/**
	 * @author Pavel Petrochenko
	 */
	private final class GoToServerAction extends Action
	{
		IServer srv;

		/**
		 * @param srv
		 */
		public GoToServerAction(IServer srv)
		{
			this.setToolTipText(Messages.ServerConsolePageParticipant_TITLE0);
			this.setImageDescriptor(ServerImagesRegistry.getInstance().getDescriptor(srv));
			this.srv = srv;
		}

		/**
		 * @see org.eclipse.jface.action.Action#run()
		 */
		public void run()
		{
			IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try
			{
				GenericServersView findView = (GenericServersView) activePage.showView(GenericServersView.ID);
				findView.select(srv);
			}
			catch (PartInitException e)
			{
				IdeLog.log(ServerUIPlugin.getDefault(), IStatus.ERROR,
						"Part init exception while doing go to server action", e); //$NON-NLS-1$
			}

		}
	}

	/**
	 * @see org.eclipse.ui.console.IConsolePageParticipant#activated()
	 */
	public void activated()
	{

	}

	/**
	 * @see org.eclipse.ui.console.IConsolePageParticipant#deactivated()
	 */
	public void deactivated()
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.eclipse.ui.console.IConsolePageParticipant#dispose()
	 */
	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see org.eclipse.ui.console.IConsolePageParticipant#init(org.eclipse.ui.part.IPageBookViewPage,
	 *      org.eclipse.ui.console.IConsole)
	 */
	public void init(IPageBookViewPage page, IConsole console)
	{
		IOConsole ioconsole = (IOConsole) console;
		IProcess attribute = (IProcess) ioconsole.getAttribute(IDebugUIConstants.ATTR_CONSOLE_PROCESS);
		if (attribute != null)
		{
			for (IServer srv : ServerCore.getServerManager().getServers())
			{
				IProcess[] processes = srv.getProcesses();
				if (processes != null)
				{
					if (Arrays.asList(processes).contains(attribute))
					{
						page.getSite().getActionBars().getToolBarManager().add(new GoToServerAction(srv));
					}
				}
			}
		}
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter)
	{
		return null;
	}

}
