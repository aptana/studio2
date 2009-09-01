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
package com.aptana.ide.ssh.impl.preferences;

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.ide.ssh.Activator;
import com.aptana.ide.ssh.spi.ISSHLauncher;

/**
 * @author schitale
 *
 */
public class SSHPreferences extends AbstractPreferenceInitializer {
	public static final String SSH_COMMAND = "ssh"; //$NON-NLS-1$

	private static String defaultSSHCommand = ""; //$NON-NLS-1$

	static {
		if (Platform.OS_MACOSX.equals(Platform.getOS())) {
			defaultSSHCommand = "/usr/bin/osascript "  //$NON-NLS-1$
				+ "\"{" + ISSHLauncher.TERMINAL_DOT_SCPT + "}\" " //$NON-NLS-1$ //$NON-NLS-2$
				+ "{" + ISSHLauncher.USER_AT_HOST + "}"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (Platform.OS_WIN32.equals(Platform.getOS())) {
			defaultSSHCommand = "cmd /C start " //$NON-NLS-1$
				+ "\"\\\"ssh\\\"\" " //$NON-NLS-1$
				+ "\"{" + ISSHLauncher.PUTTY_DOT_EXE + "}\" "  //$NON-NLS-1$ //$NON-NLS-2$
				+ "\"{" + ISSHLauncher.USER_AT_HOST + "}\""; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (Platform.OS_LINUX.equals(Platform.getOS())) {
			if (new File("/usr/bin/gnome-terminal").exists()) { //$NON-NLS-1$
				defaultSSHCommand = "/usr/bin/gnome-terminal -x /usr/bin/ssh " //$NON-NLS-1$
					+ "\"{" + ISSHLauncher.USER_AT_HOST + "}\""; //$NON-NLS-1$ //$NON-NLS-2$
			} else if (new File("/usr/bin/Konsole").exists()) { //$NON-NLS-1$
				defaultSSHCommand = "/usr/bin/Konsole -x /usr/bin/ssh " //$NON-NLS-1$
					+ "\"{" + ISSHLauncher.USER_AT_HOST + "}\""; //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				defaultSSHCommand = "/usr/bin/xterm -e /usr/bin/ssh "  //$NON-NLS-1$
					+ "\"{" + ISSHLauncher.USER_AT_HOST + "}\""; //$NON-NLS-1$ //$NON-NLS-2$
			}
		} else if (Platform.OS_SOLARIS.equals(Platform.getOS())) {
			defaultSSHCommand = "/usr/bin/xterm -e /usr/bin/ssh " //$NON-NLS-1$
				+ "\"{" + ISSHLauncher.USER_AT_HOST + "}\""; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	public static String getSSHCommand() {
		return Activator.getDefault().getPluginPreferences().getString(SSH_COMMAND);
	}
	
	public static void setSSHCommand(String SSHCommand) {
		Activator.getDefault().getPluginPreferences().setValue(SSH_COMMAND, SSHCommand);
	}

	public void initializeDefaultPreferences() {
		IEclipsePreferences node = new DefaultScope().getNode(Activator.getDefault().getBundle().getSymbolicName());
		node.put(SSH_COMMAND, defaultSSHCommand);
	}
}














