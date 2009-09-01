package com.aptana.ide.pathtools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Arrays;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
//import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
//import org.eclipse.ui.console.IConsoleConstants;
//import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.pathtools.handlers.Utilities;

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
								    new Thread(new MessageConsoleWriter(messageConsole, process.getErrorStream())).start();
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
						IdeLog.logImportant(activator,
								MessageFormat
                                        .format(
                                                Messages.CommandLauncher_Message_ProcessExited,
                                                Arrays.asList(commandArray).toString(), status));
					}
				} catch (InterruptedException ex) {
					IdeLog.logError(activator,
							MessageFormat.format(
                                    Messages.CommandLauncher_ERR_Exception,
                                    Arrays.asList(commandArray).toString()),
							ex);
				} catch (IOException ioe) {
					IdeLog.logError(activator,
									MessageFormat
                                            .format(
                                                    Messages.CommandLauncher_ERR_Exception,
                                                    Arrays.asList(commandArray).toString()),
									ioe);
				}
			}
		}, "Launching - " + command).start(); //$NON-NLS-1$
	}
	
	private static MessageConsole getMessageConsole() {
		if (messageConsole == null) {
			messageConsole = new MessageConsole("Path Tools Console", null); //$NON-NLS-1$
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{messageConsole});			
		}
		return messageConsole;
	}
	
	private static class MessageConsoleWriter implements Runnable {		
		private final MessageConsole messageConsole;
		private final InputStream from;
		
		private MessageConsoleWriter(MessageConsole messageConsole, InputStream from) {
			this.messageConsole = messageConsole;
			this.from = from;
		}
		
		public void run() {
			final MessageConsoleStream messageConsoleStream = messageConsole.newMessageStream();
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
