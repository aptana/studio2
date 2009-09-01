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
package com.aptana.ide.ssh.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Arrays;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.ssh.Activator;

/**
 * A simple external process launcher.
 * 
 * @author Sandip V. Chitale
 * 
 */
public class CommandLauncher {
	
	private static MessageConsole messageConsole;
	private static boolean firstTime = true;

	public static void launch(final String command) {
		// Launch command on a separate thread.
		new Thread(new Runnable() {
			public void run() {
				Activator activator = Activator.getDefault();
				String[] commandArray = Utilities.parseParameters(command);
				try {
					final Process process = Runtime.getRuntime().exec(commandArray);
					final MessageConsole messageConsole = getMessageConsole();
					MessageConsoleStream newMessageStream = messageConsole.newMessageStream();
					if (firstTime) {
						firstTime = false;
					} else {
						newMessageStream.println();
						newMessageStream.println();
						newMessageStream.println();
					}
					messageConsole.newMessageStream().println(command);
					UIJob uiJob = new UIJob("") { //$NON-NLS-1$
						@Override
						public IStatus runInUIThread(IProgressMonitor monitor) {
							IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
							if (activeWorkbenchWindow != null) {
								IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
								if (activePage != null) {
									new Thread(new MessageConsoleWriter(messageConsole, process.getInputStream())).start();
									new Thread(new MessageConsoleWriter(messageConsole, process.getErrorStream(), 
											Display.getCurrent().getSystemColor(SWT.COLOR_RED))).start();
								}
							}
							return Status.OK_STATUS;
						}
					};
					uiJob.schedule();
					
					int status = process.waitFor();
					if (status == 0) {
						// Good
					} else {
						String message = MessageFormat.format(
							Messages.getString("CommandLauncher.Process_Exited"), //$NON-NLS-1$
							new Object[] {
								Arrays.asList(commandArray).toString(),
								status
							}
						);
						activator.getLog().log(
								new Status(IStatus.ERROR, activator.getBundle()
										.getSymbolicName(), 0, message, null));
					}
				} catch (InterruptedException ex) {
					String message = MessageFormat.format(
						Messages.getString("CommandLauncher.Exception_While_Executing"), //$NON-NLS-1$
						new Object[] {
							Arrays.asList(commandArray).toString()
						}
					);
					activator.getLog()
							.log(
									new Status(IStatus.ERROR, activator.getBundle()
											.getSymbolicName(), 0,
											message, ex));
				} catch (IOException ioe) {
					String message = MessageFormat.format(
						Messages.getString("CommandLauncher.Exception_While_Executing"), //$NON-NLS-1$
						new Object[] {
							Arrays.asList(commandArray).toString()
						}
					);
					activator.getLog()
							.log(
									new Status(IStatus.ERROR, activator.getBundle()
											.getSymbolicName(), 0,
											message, ioe));
				}

			}
		}, "Launching - " + command).start(); //$NON-NLS-1$
	}
	
	private static MessageConsole getMessageConsole() {
		if (messageConsole == null) {
			messageConsole = new MessageConsole(Messages.getString("CommandLauncher.Launcher_Console"), null); //$NON-NLS-1$
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{messageConsole});			
		}
		return messageConsole;
	}
	
	private static class MessageConsoleWriter implements Runnable {		
		private final MessageConsole messageConsole;
		private final InputStream from;
		private final Color color;
		
		private MessageConsoleWriter(MessageConsole messageConsole, InputStream from) {
			this(messageConsole, from, null);
		}
		
		private MessageConsoleWriter(MessageConsole messageConsole, InputStream from, Color color) {
			this.messageConsole = messageConsole;
			this.from = from;
			this.color = color;
		}
		
		public void run() {
			final MessageConsoleStream messageConsoleStream = messageConsole.newMessageStream();
			if (color != null) {
				Display.getDefault().asyncExec(new Runnable() {

					public void run() {
						messageConsoleStream.setColor(color);
					}

				});
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(from));
			String output = null;
			try {
				while ((output = reader.readLine()) != null) {
					messageConsoleStream.println(output);
				}
			} catch (IOException e) {
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
				}
				try {
					messageConsoleStream.close();
				} catch (IOException e) {
				}
			}
		}		
	}

}
