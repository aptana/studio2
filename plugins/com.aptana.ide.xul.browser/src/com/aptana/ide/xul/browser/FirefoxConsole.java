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
package com.aptana.ide.xul.browser;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.mozilla.interfaces.nsIConsoleListener;
import org.mozilla.interfaces.nsIConsoleMessage;
import org.mozilla.interfaces.nsIConsoleService;
import org.mozilla.interfaces.nsIScriptError;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.xpcom.Mozilla;

import com.aptana.ide.editors.unified.UnifiedColorManager;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class FirefoxConsole extends MessageConsole
{

	private static FirefoxConsole console;

	/**
	 * Gets the firefox console interface and creates it if it hasn't been
	 * 
	 * @return - firefox console
	 */
	public static FirefoxConsole getConsole()
	{
		if (console == null)
		{
			console = new FirefoxConsole(Messages.getString("FirefoxConsole.Firefox_Preview_Console"), Activator //$NON-NLS-1$
					.getImageDescriptor("icons/firefox_icon.png")); //$NON-NLS-1$
			console.addListener();
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
		}
		return console;
	}

	/**
	 * Firefox console constructor
	 * 
	 * @param name
	 * @param imageDescriptor
	 */
	private FirefoxConsole(String name, ImageDescriptor imageDescriptor)
	{
		super(name, imageDescriptor);
	}

	private void addListener()
	{
		nsIConsoleService service = (nsIConsoleService) Mozilla.getInstance().getServiceManager()
				.getServiceByContractID("@mozilla.org/consoleservice;1", nsIConsoleService.NS_ICONSOLESERVICE_IID); //$NON-NLS-1$
		final MessageConsoleStream warningStream = this.newMessageStream();
		warningStream.setColor(UnifiedColorManager.getInstance().getColor(new RGB(143, 91, 0)));
		final MessageConsoleStream messageStream = this.newMessageStream();
		messageStream.setColor(UnifiedColorManager.getInstance().getColor(new RGB(0, 0, 255)));
		final MessageConsoleStream errorStream = this.newMessageStream();
		errorStream.setColor(UnifiedColorManager.getInstance().getColor(new RGB(255, 0, 0)));

		service.registerListener(new nsIConsoleListener()
		{

			public nsISupports queryInterface(String uuid)
			{
				return null;
			}

			public void observe(nsIConsoleMessage aMessage)
			{
				nsIScriptError error = (nsIScriptError) aMessage.queryInterface(nsIScriptError.NS_ISCRIPTERROR_IID);
				if (error != null)
				{
					MessageConsoleStream stream = null;
					StringBuffer message = new StringBuffer(""); //$NON-NLS-1$
					long flag = error.getFlags();
					if (flag == nsIScriptError.errorFlag)
					{
						message.append(Messages.getString("FirefoxConsole.Error")); //$NON-NLS-1$
						stream = errorStream;
					}
					else if (flag == nsIScriptError.warningFlag)
					{
						message.append(Messages.getString("FirefoxConsole.Warning")); //$NON-NLS-1$
						stream = warningStream;
					}
					else if (flag == nsIScriptError.exceptionFlag)
					{
						message.append(Messages.getString("FirefoxConsole.Exception")); //$NON-NLS-1$
						stream = errorStream;
					}
					else
					{
						message.append(Messages.getString("FirefoxConsole.Message")); //$NON-NLS-1$
						stream = messageStream;
					}
					message.append(" " + error.getErrorMessage()); //$NON-NLS-1$
					message.append(Messages.getString("FirefoxConsole.File") + error.getSourceName()); //$NON-NLS-1$
					message.append(Messages.getString("FirefoxConsole.Line") + error.getLineNumber()); //$NON-NLS-1$
					message.append(Messages.getString("FirefoxConsole.Column") + error.getColumnNumber()); //$NON-NLS-1$
					stream.println(message.toString());
				}
				else
				{
					messageStream.println(Messages.getString("FirefoxConsole.Message_With_Space") + aMessage.getMessage()); //$NON-NLS-1$
				}
			}

		});
	}
}
