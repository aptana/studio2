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
package com.aptana.ide.debug.test;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;

import com.aptana.ide.debug.core.model.IJSDebugTarget;

/**
 * @author Max Stepanov
 */
public class AutomatedTestController implements IDebugEventSetListener
{
	private static AutomatedTestController instance = null;
	private MessageConsole console;
	private TestSession session;

	/**
	 * AutomatedTestController
	 */
	private AutomatedTestController()
	{
	}

	/**
	 * getInstance
	 * 
	 * @return AutomatedTestController
	 */
	public static AutomatedTestController getInstance()
	{
		if (instance == null)
		{
			instance = new AutomatedTestController();
		}
		return instance;
	}

	/**
	 * start
	 */
	public void start()
	{
		DebugPlugin.getDefault().addDebugEventListener(this);
	}

	/**
	 * stop
	 */
	public void stop()
	{
		DebugPlugin.getDefault().removeDebugEventListener(this);
	}

	private void showConsole()
	{
		if (console == null)
		{
			console = new MessageConsole("Automated Debug Console", null);
		}
		IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
		consoleManager.addConsoles(new IConsole[] { console });
		consoleManager.showConsoleView(console);
	}

	/**
	 * @see org.eclipse.debug.core.IDebugEventSetListener#handleDebugEvents(org.eclipse.debug.core.DebugEvent[])
	 */
	public void handleDebugEvents(DebugEvent[] events)
	{
		for (int i = 0; i < events.length; ++i)
		{
			DebugEvent event = events[i];

			Object source = event.getSource();
			switch (event.getKind())
			{
				case DebugEvent.CREATE:
					if (source instanceof IJSDebugTarget)
					{
						showConsole();
						session = new TestSession((IJSDebugTarget) source, console.newMessageStream());
					}
					break;
				case DebugEvent.TERMINATE:
					if (session != null && session.getDebugTarget() == source)
					{
						session = null;
					}
					break;
				default:
					break;
			}
		}
	}
}
