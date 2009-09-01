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
package com.aptana.ide.server.ui.views.actions;

import java.util.WeakHashMap;

import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleListener;
import org.eclipse.ui.console.TextConsole;

import com.aptana.ide.server.ServerCore;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.IServerManagerListener;
import com.aptana.ide.server.core.ServerManagerEvent;
import com.aptana.ide.server.ui.ServerUIPlugin;

/**
 * @author Pavel Petrochenko
 */
public class OpenConsole extends Action implements ISelectionChangedListener
{
	private static final String TITLE = Messages.OpenConsole_TITLE;
	private static WeakHashMap<IServer, IConsole> consoles = new WeakHashMap<IServer, IConsole>();

	static
	{
		ConsolePlugin.getDefault().getConsoleManager().addConsoleListener(new IConsoleListener()
		{

			public void consolesAdded(IConsole[] consoles)
			{

				for (IConsole c : consoles)
				{
					if (c instanceof TextConsole)
					{
						final TextConsole cm = (TextConsole) c;
						IProcess attribute = (IProcess) cm.getAttribute(IDebugUIConstants.ATTR_CONSOLE_PROCESS);
						if (attribute != null)
						{
							for (IServer s : ServerCore.getServerManager().getServers())
							{
								IProcess[] processes = s.getProcesses();
								for (IProcess m : processes)
								{
									if (m != null)
									{
										if (m.equals(attribute))
										{
											OpenConsole.consoles.put(s, cm);
										}
									}
								}
							}
						}
					}
				}
			}

			public void consolesRemoved(IConsole[] consoles)
			{

			}

		});
		ServerCore.getServerManager().addServerManagerListener(new IServerManagerListener()
		{

			public void serversChanged(ServerManagerEvent event)
			{
				for (IConsole m : ConsolePlugin.getDefault().getConsoleManager().getConsoles())
				{
					if (m instanceof TextConsole)
					{
						TextConsole cs = (TextConsole) m;
						IProcess attribute = (IProcess) cs.getAttribute(IDebugUIConstants.ATTR_CONSOLE_PROCESS);
						for (IServer s : ServerCore.getServerManager().getServers())
						{
							IProcess[] processes = s.getProcesses();
							for (IProcess ma : processes)
							{
								if (ma != null)
								{
									if (ma.equals(attribute))
									{
										OpenConsole.consoles.put(s, cs);
									}
								}
							}
						}
					}
				}
			}

		});
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event)
	{
		ISelection selection = event.getSelection();
		if (selection.isEmpty())
		{
			setEnabled(false);
		}
		if (selection instanceof StructuredSelection)
		{
			StructuredSelection ss = (StructuredSelection) selection;
			if (ss.size() == 1)
			{
				Object firstElement = ss.getFirstElement();
				if (firstElement instanceof IServer)
				{
					server = (IServer) firstElement;
					ICanOpenConsole adapter = (ICanOpenConsole) server.getAdapter(ICanOpenConsole.class);					
					setEnabled(adapter!=null||consoles.get(server) != null);
					return;
				}
			}
		}
		setEnabled(false);
	}

	/**
	 * 
	 */
	protected IServer server;

	/**
	 * @param provider
	 */
	public OpenConsole(ISelectionProvider provider)
	{
		super(TITLE);
		this.setToolTipText(TITLE);
		provider.addSelectionChangedListener(this);
		this.setImageDescriptor(ServerUIPlugin.getImageDescriptor("/icons/server/console_view.gif")); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run()
	{
		if (server != null)
		{
			ICanOpenConsole adapter = (ICanOpenConsole) server.getAdapter(ICanOpenConsole.class);
			if (adapter!=null)
			{
				adapter.openConsole();
			}
			IConsole console2 = consoles.get(server);
			if (console2 != null)
			{
				ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console2);
			}
			
		}
		super.run();
	}
}
