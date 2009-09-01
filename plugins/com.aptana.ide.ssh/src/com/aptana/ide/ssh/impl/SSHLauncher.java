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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Platform;

import com.aptana.ide.ssh.Activator;
import com.aptana.ide.ssh.impl.preferences.SSHPreferences;
import com.aptana.ide.ssh.spi.ISSHLauncher;

public class SSHLauncher implements ISSHLauncher {

	public SSHLauncher() {
	}

	public void launchSSH(String host) {
		launchSSH(host, System.getProperty("user.name")); //$NON-NLS-1$
	}

	public void launchSSH(String host, String user) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put(HOST, host);
		parameters.put(USER, user);
		launchSSH(parameters);
	}

	public void launchSSH(Map<String, String> parameters) {
		String userAtHost = parameters.get(USER_AT_HOST);
		if (userAtHost == null) {
				parameters.put(USER_AT_HOST, parameters.get(USER) + "@" + parameters.get(HOST)); //$NON-NLS-1$
		}
		
		if (Platform.OS_MACOSX.equals(Platform.getOS())) {
			String terminalDotApp = parameters.get(TERMINAL_DOT_SCPT);
			if (terminalDotApp == null) {
				parameters.put(TERMINAL_DOT_SCPT, Activator.getDefault().getTerminalDotScpt());
			}
		}
		
		if (Platform.OS_WIN32.equals(Platform.getOS())) {
			String puttyDotExe = parameters.get(PUTTY_DOT_EXE);
			if (puttyDotExe == null) {
				puttyDotExe = Activator.getDefault().getPuttyDotExe();
				if (puttyDotExe == null) {
					puttyDotExe = "putty.exe"; //$NON-NLS-1$
				}
				parameters.put(PUTTY_DOT_EXE, puttyDotExe);
			}
		}
		
		CommandLauncher.launch(
				Utilities.formatCommand(SSHPreferences.getSSHCommand(), parameters));
	}

}
