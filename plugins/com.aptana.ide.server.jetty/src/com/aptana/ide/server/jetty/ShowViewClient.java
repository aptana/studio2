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
package com.aptana.ide.server.jetty;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.views.IRevealer;
import com.aptana.ide.server.jetty.comet.CometClient;
import com.aptana.ide.server.jetty.comet.CometConstants;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class ShowViewClient extends CometClient
{

	/**
	 * SHOW_VIEW
	 */
	public static final String SHOW_VIEW = "/portal/views/show"; //$NON-NLS-1$
	
	/**
	 * SUBID
	 */
	public static final String SUBID = "subid"; //$NON-NLS-1$
	
	private static final String CONSOLE_VIEW_ID = "org.eclipse.ui.console.ConsoleView"; //$NON-NLS-1$
	
	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getID(java.lang.String)
	 */
	protected String getID(String msgId)
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getResponse(java.lang.String, java.lang.Object)
	 */
	protected Object getResponse(String toChannel, Object request)
	{
		if (SHOW_VIEW.equals(toChannel))
		{
			if (request instanceof Map)
			{
				final Map requestData = (Map) request;

				if (requestData.containsKey(CometConstants.ID))
				{
					final String id = requestData.get(CometConstants.ID).toString();
					Object subidObj = requestData.get(SUBID);
					String subidStr = null;
					if (subidObj != null) {
					    subidStr = subidObj.toString().trim();
					}

					// Special handling of console view
					if (CONSOLE_VIEW_ID.equals(id))
					{
						// If specific console was specified - try to show it
						if (subidStr != null)
						{
							IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
							IConsole[] consoles = consoleManager.getConsoles();
							
							for (IConsole console : consoles)
							{
								if (subidStr.equals(console.getType()))
								{
									CoreUIUtils.showView(CONSOLE_VIEW_ID, null);
									
									consoleManager.showConsoleView(console);
									
									return null;
								}
							}
						}
					}

					final String subid = subidStr;
					UIJob job = new UIJob(Messages.ShowViewClient_Job_OpenView)
					{
						public IStatus runInUIThread(IProgressMonitor monitor)
						{
							try
							{
								IViewPart view = CoreUIUtils.showView(id);
								
								if (view instanceof IRevealer && subid != null && subid.length() > 0)
								{
									((IRevealer) view).revealItem(subid);
								}
							}
							catch (PartInitException e)
							{
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
		return new String[] { SHOW_VIEW };
	}

}
