/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

import com.aptana.ide.core.preferences.IPreferenceConstants;

/**
 * Utility for logging IDE messages.
 * 
 * @author Spike Washburn
 */
public final class IdeLog
{

	private static Map<Plugin, List<Status>> earlyMessageCache = new HashMap<Plugin, List<Status>>();
	private static boolean caching = true;

	public static int OFF = 0;
	public static int ERROR = 1;
	public static int WARNING = 2;
	public static int INFO = 3;

	/**
	 * Flushes any cached logging messages
	 */
	public static void flushCache()
	{
		caching = false;
		Iterator<Plugin> iter = earlyMessageCache.keySet().iterator();
		while (iter.hasNext())
		{
			Plugin plugin = iter.next();
			List<Status> messages = earlyMessageCache.get(plugin);
			for (int i = 0; i < messages.size(); i++)
			{
				Status status = (Status) messages.get(i);
				if (status.getSeverity() == IStatus.ERROR || (isAptanaDebugging(status.getSeverity()) || plugin.isDebugging()))
				{
					log(plugin, status.getSeverity(), status.getMessage(), status.getException());
				}
			}
		}
		earlyMessageCache.clear();
	}

	/**
	 * Private constructor not to be used.
	 */
	private IdeLog()
	{

	}

	/**
	 * Logs an error
	 * 
	 * @param plugin
	 * @param message
	 *            The message to log
	 */
	public static void logError(Plugin plugin, String message)
	{
		log(plugin, IStatus.ERROR, message, null);
	}
	
	/**
	 * Get whether aptana is in debug mode
	 * 
	 * @return - true if debugging
	 */
	public static boolean isAptanaDebugging(int debugLevel)
	{
		if (caching)
		{
			return true;
		}
		else if (Platform.inDebugMode())
		{
			return true;
		}
		else if (AptanaCorePlugin.getDefault() != null)
		{
            int statusPreference = Platform.getPreferencesService().getInt(
                    AptanaCorePlugin.ID, IPreferenceConstants.PREF_DEBUG_LEVEL,
                    OFF, null);

			if(statusPreference == OFF)
			{
				return false;
			}

			return statusPreference >= getStatusLevel(debugLevel);				
		}
		else
		{
			return false;
		}
	}

	/**
	 * Converts the IStatus level into something we get.
	 * @param status
	 * @return
	 */
	private static int getStatusLevel(int status)
	{
		switch(status)
		{
			case IStatus.INFO:
			{
				return INFO;
			}
			case IStatus.WARNING:
			{
				return WARNING;
			}
			case IStatus.ERROR:
			{
				return ERROR;
			}
			default:
			{
				return OFF;
			}
		}
	}
	
	/**
	 * Logs an error
	 * 
	 * @param plugin
	 * @param message
	 * @param th
	 */
	public static void logError(Plugin plugin, String message, Throwable th)
	{
		log(plugin, IStatus.ERROR, message, th);
	}

	/**
	 * Logs a warning
	 * 
	 * @param plugin
	 * @param message
	 * @param th
	 * @deprecated use logImportant instead
	 */
	public static void logWarning(Plugin plugin, String message, Throwable th)
	{
		if (isAptanaDebugging(IStatus.WARNING) || (plugin != null && plugin.isDebugging()))
		{
			log(plugin, IStatus.WARNING, message, th);
		}
	}

	/**
	 * Logs a warning
	 * 
	 * @param plugin
	 * @param message
	 *            The message to log
	 * @deprecated use logImportant instead
	 */
	public static void logWarning(Plugin plugin, String message)
	{
		if (isAptanaDebugging(IStatus.WARNING) || (plugin != null && plugin.isDebugging()))
		{
			log(plugin, IStatus.WARNING, message, null);
		}
	}
	
	/**
	 * Logs a warning
	 * 
	 * @param plugin
	 * @param message
	 * @param th
	 */
	public static void logImportant(Plugin plugin, String message, Throwable th)
	{
		if (isAptanaDebugging(IStatus.WARNING) || (plugin != null && plugin.isDebugging()))
		{
			log(plugin, IStatus.WARNING, message, th);
		}
	}

	/**
	 * Logs a warning
	 * 
	 * @param plugin
	 * @param message
	 *            The message to log
	 */
	public static void logImportant(Plugin plugin, String message)
	{
		if (isAptanaDebugging(IStatus.WARNING) || (plugin != null && plugin.isDebugging()))
		{
			log(plugin, IStatus.WARNING, message, null);
		}
	}

	/**
	 * Logs an error
	 * 
	 * @param plugin
	 * @param message
	 */
	public static void logInfo(Plugin plugin, String message)
	{
		if (isAptanaDebugging(IStatus.INFO) || (plugin != null && plugin.isDebugging()))
		{
			log(plugin, IStatus.INFO, message, null);
		}
	}

	/**
	 * Logs an error
	 * 
	 * @param plugin
	 * @param message
	 * @param th
	 */
	public static void logInfo(Plugin plugin, String message, Throwable th)
	{
		if (isAptanaDebugging(IStatus.INFO) || (plugin != null && plugin.isDebugging()))
		{
			log(plugin, IStatus.INFO, message, th);
		}
	}

	/**
	 * Logs a warning
	 * 
	 * @param plugin
	 * @param message
	 *            The message to log
	 * @param name
	 * @param suffix
	 * @param fileContent
	 */
	public static void logInfoToFile(Plugin plugin, String message, String name, String suffix, String fileContent)
	{
		if (isAptanaDebugging(IStatus.WARNING) || plugin.isDebugging())
		{
			File f = null;
			try
			{
				f = File.createTempFile(name, suffix);
				FileUtils.writeStringToFile(fileContent, f);
				log(plugin, IStatus.WARNING, message + Messages.IdeLog_File_Written_To + f.getAbsolutePath(), null);
			}
			catch (IOException e)
			{
				log(plugin, IStatus.WARNING, message + Messages.IdeLog_Unable_To_Write_Temporary_File, null);
			}
		}
	}

	// /**
	// * Times an item
	// *
	// * @param plugin
	// * @param message
	// * @param th
	// */
	// public static PerformanceStats logPerformance(Plugin plugin, String eventName, Object blameObject)
	// {
	// PerformanceStats stats = PerformanceStats.getStats(plugin.getBundle().getSymbolicName() + "/trace/perf",
	// blameObject);
	// stats.startRun(eventName);
	// return stats;
	// }

	/**
	 * Logs an error
	 * 
	 * @param plugin
	 * @param status
	 * @param message
	 * @param th
	 */
	public static void log(Plugin plugin, int status, String message, Throwable th)
	{
		if (plugin == null)
		{
			// CHECKSTYLE:OFF
			System.err.println(message); // CHECKSTYLE:ON
			if (th != null)
			{
				// CHECKSTYLE:OFF
				th.printStackTrace(); // CHECKSTYLE:ON
			}
			return;
		}

		String tempMessage = StringUtils.format("(Build {0}) {1} {2}", new String[] { PluginUtils.getPluginVersion(plugin), //$NON-NLS-1$
				getLabel(status), message });
		if (!PluginUtils.isPluginLoaded(plugin))
		{
			// CHECKSTYLE:OFF
			System.err.println(tempMessage); // CHECKSTYLE:ON
			return;
		}
		Status logStatus = new Status(status, plugin.getBundle().getSymbolicName(), IStatus.OK, tempMessage, th);
		if (caching)
		{
			List<Status> statusMessages = null;
			if (earlyMessageCache.containsKey(plugin))
			{
				statusMessages = earlyMessageCache.get(plugin);

			}
			else
			{
				statusMessages = new ArrayList<Status>();
				earlyMessageCache.put(plugin, statusMessages);
			}
			statusMessages.add(logStatus);
		}
		else
		{
			plugin.getLog().log(logStatus);
		}
		if (status == IStatus.ERROR && isAptanaDebugging(IStatus.ERROR))
		{
			// dump the error to stderr so the devteam knows it happened
			// TODO: we should create a debug-mode flag that sets a custom
			// logger for all plugins.
			// CHECKSTYLE:OFF
			System.err.println(tempMessage); // CHECKSTYLE:ON

			if (th != null)
			{
				// CHECKSTYLE:OFF
				th.printStackTrace(); // CHECKSTYLE:ON
			}
		}
	}

	/**
	 * 
	 * @param status
	 * @return
	 */
	private static String getLabel(int status)
	{
		switch(status)
		{
			case IStatus.INFO:
			{
				return Messages.IdeLog_INFO;
			}
			case IStatus.WARNING:
			{
				return Messages.IdeLog_IMPORTANT;
			}
			case IStatus.ERROR:
			{
				return Messages.IdeLog_ERROR;
			}
			default:
			{
				return Messages.IdeLog_UNKNOWN;
			}
		}
	}
}
