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
package com.aptana.ide.ssh;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.aptana.ide.ssh.impl.SSHLauncher;
import com.aptana.ide.ssh.spi.ISSHLauncher;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "SSH"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	
	private ISSHLauncher sshLauncher;
	
	private static String terminalDotScpt;

	private static String putty = "putty.exe"; //$NON-NLS-1$
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		try {
			URL entry = context.getBundle().getEntry("/scripts/terminal.scpt"); //$NON-NLS-1$
			if (entry != null) {
				terminalDotScpt = FileLocator.toFileURL(entry).getFile();
			}
		} catch (IOException el) {
		}
		
		Bundle[] bundles = context.getBundles();
		for (Bundle bundle : bundles)
		{
			if (bundle.getSymbolicName().equals("com.aptana.ide.libraries")) { //$NON-NLS-1$
				try {
					URL entry = bundle.getEntry("/win32/putty.exe"); //$NON-NLS-1$
					if (entry != null) {
						putty = FileLocator.toFileURL(entry).getFile().substring(1);
					}
				} catch (IOException el) {
				}
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	public String getTerminalDotScpt() {
		return terminalDotScpt;
	}
	
	public String getPuttyDotExe() {
		return putty;
	}
	
	public ISSHLauncher getSSHLauncher() {
		if (sshLauncher == null) {
			sshLauncher = new SSHLauncher();
		}
		return sshLauncher;
	}

}
