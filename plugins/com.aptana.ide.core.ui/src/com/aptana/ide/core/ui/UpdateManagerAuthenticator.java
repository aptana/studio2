/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ide.core.ui;

import java.net.Authenticator;
import java.net.InetAddress;
import java.net.PasswordAuthentication;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.update.internal.ui.security.Authentication;
import org.eclipse.update.internal.ui.security.UserValidationDialog;

/**
 * Update Manager Authenticator Sadly there can only be one registered per VM
 */
public class UpdateManagerAuthenticator extends Authenticator
{

	/**
	 * @see Authenticator#getPasswordAuthentication()
	 */
	protected PasswordAuthentication getPasswordAuthentication()
	{
		// String protocol = getRequestingProtocol();
		InetAddress address = getRequestingSite(); // can be null;
		// int port = getRequestingPort();
		String prompt = getRequestingPrompt(); // realm or message, not documented that can be null
		// String scheme = getRequestingScheme(); // not documented that can be null

		// get the host name from the address since #getRequestingHost
		// is not available in the foundation 1.0 class libraries
		String hostString = null;
		if (address != null)
		{
			hostString = address.getHostName();
		}
		if (hostString == null)
		{
			hostString = ""; //$NON-NLS-1$
		}
		String promptString = prompt;
		if (prompt == null)
		{
			promptString = ""; //$NON-NLS-1$
		}

		Authentication auth = UserValidationDialogExtension.getAuthentication(hostString, promptString);
		if (auth != null)
			return new PasswordAuthentication(auth.getUser(), auth.getPassword().toCharArray());
		else
			return null;
	}

	private static class UserValidationDialogExtension extends UserValidationDialog 
	{
		protected UserValidationDialogExtension(Shell parentShell, String host, String message)
		{
			super(parentShell, host, message);
		}
		
		/**
		 * Gets user and password from a user. May be called from any thread
		 * 
		 * @return UserAuthentication that contains the userid and the password or
		 *         <code>null</code> if the dialog has been cancelled
		 */
		public static Authentication getAuthentication(final String host,
				final String message) {
			class UIOperation implements Runnable {
				public Authentication authentication;
				public void run() {
					authentication = UserValidationDialogExtension.askForAuthentication(
							host, message);
				}
			}

			UIOperation uio = new UIOperation();
			if (Display.getCurrent() != null) {
				uio.run();
			} else {
				Display.getDefault().syncExec(uio);
			}
			return uio.authentication;
		}
		/**
		 * Gets user and password from a user Must be called from UI thread
		 * 
		 * @return UserAuthentication that contains the userid and the password or
		 *         <code>null</code> if the dialog has been cancelled
		 */
		protected static Authentication askForAuthentication(String host,
				String message) {
			UserValidationDialog ui = new UserValidationDialogExtension(Display.getCurrent().getActiveShell(), host, message); 
			ui.open();
			return ui.getAuthentication();
		}
	}

}
