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
package com.aptana.ide.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import com.aptana.ide.core.db.AptanaDB;
import com.aptana.ide.core.io.sync.SyncManager;

/**
 * The main plugin class to be used in the desktop.
 */
public class AptanaCorePlugin extends Plugin
{
	/**
	 * ID
	 */
	public static final String ID = "com.aptana.ide.core"; //$NON-NLS-1$
	
	// The shared instance.
	private static AptanaCorePlugin plugin;

	/**
	 * This method is called upon plug-in activation
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void start(BundleContext context) throws Exception
	{
		plugin = this;
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void stop(BundleContext context) throws Exception
	{
		AptanaDB.getInstance().shutdown();
		
		super.stop(context);
		
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return Plugin
	 */
	public static AptanaCorePlugin getDefault()
	{
		return plugin;
	}


	/**
	 * Writes and state info that needs to be persisted.
	 * 
	 * @param file
	 */
	public void writeState(File file)
	{
		String state = SyncManager.getSyncManager().toSerializableString();

		Writer output = null;
		try
		{
			file.createNewFile();
			output = new BufferedWriter(new FileWriter(file));
			output.write(state);
		}
		catch (IOException e)
		{
			IdeLog.logError(this, StringUtils.format(Messages.AptanaCorePlugin_Serialization_CreateFailed, file.getAbsolutePath()));
		}
		finally
		{
			if (output != null)
			{
				try
				{
					output.close();
				}
				catch (IOException e)
				{
					IdeLog.logError(this, StringUtils.format(Messages.AptanaCorePlugin_Serialization_CloseFailed, file.getAbsolutePath()));
				}
			}
		}
	}

}
