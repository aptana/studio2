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
package com.aptana.ide.lexer;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class LexerPlugin extends Plugin
{
	// The shared instance.
	private static LexerPlugin plugin;
	private static String lexerType = "matcher"; //$NON-NLS-1$

	/**
	 * The constructor.
	 */
	public LexerPlugin()
	{
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void start(BundleContext context) throws Exception
	{
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
		super.stop(context);

		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return Returns LexerPlugin
	 */
	public static LexerPlugin getDefault()
	{
		return plugin;
	}

	/**
	 * getLexerType
	 * 
	 * @return lexer type
	 */
	public static String getLexerType()
	{
		if (lexerType == null)
		{
			lexerType = System.getProperty("aptana.lexer"); //$NON-NLS-1$ //$NON-NLS-2$

			if (lexerType == null || lexerType.length() == 0)
			{
				lexerType = "code"; //$NON-NLS-1$
			}
		}

		return lexerType;
	}

	/**
	 * Logs an error
	 * 
	 * @param message
	 *            The message to log
	 */
	public static void logError(String message)
	{
		log(IStatus.ERROR, message, null);
	}

	/**
	 * Logs an error
	 * 
	 * @param message
	 * @param th
	 */
	public static void logError(String message, Throwable th)
	{
		log(IStatus.ERROR, message, th);
	}

	/**
	 * Logs an error
	 * 
	 * @param message
	 */
	public static void logInfo(String message)
	{
		log(IStatus.INFO, message, null);
	}

	/**
	 * Logs an error
	 * 
	 * @param message
	 * @param th
	 */
	public static void logInfo(String message, Throwable th)
	{
		log(IStatus.INFO, message, th);
	}

	/**
	 * logWarning
	 * 
	 * @param message
	 */
	public static void logWarning(String message)
	{
		log(IStatus.WARNING, message, null);
	}

	/**
	 * logWarning
	 * 
	 * @param message
	 * @param th
	 */
	public static void logWarning(String message, Throwable th)
	{
		log(IStatus.WARNING, message, th);
	}

	/**
	 * Logs an error
	 * 
	 * @param statusNumber
	 * @param message
	 * @param throwable
	 */
	// CHECKSTYLE:OFF
	public static void log(int statusNumber, String message, Throwable throwable)
	{
		Plugin plugin = getDefault();

		if (plugin == null)
		{
			System.err.println(message);

			if (throwable != null)
			{
				throwable.printStackTrace();
			}
		}
		else
		{
			String expandedMessage = MessageFormat.format(
				"(Build {0}) {1}", //$NON-NLS-1$
				new Object[] { getPluginVersion(plugin), message }
			);

			if (plugin == null || plugin.getBundle() == null)
			{
				System.err.println(expandedMessage);
			}
			else
			{
				String symbolicName = plugin.getBundle().getSymbolicName();
				Status status = new Status(statusNumber, symbolicName, IStatus.OK, expandedMessage, throwable);

				plugin.getLog().log(status);

				if (statusNumber == IStatus.ERROR)
				{
					// dump the error to stderr so the devteam knows it happened
					System.err.println(expandedMessage);

					if (throwable != null)
					{
						throwable.printStackTrace();
					}
				}
			}
		}
	}

	// CHECKSTYLE:ON

	/**
	 * Retrieves the bundle version or a plugin based on its ID.
	 * 
	 * @param plugin
	 *            The plugin to retrieve from
	 * @return The version of the bundle, or null if not found.
	 */
	public static String getPluginVersion(Plugin plugin)
	{
		String result = null;

		if (plugin != null)
		{
			Bundle bundle = plugin.getBundle();

			if (bundle != null)
			{
				result = bundle.getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION).toString();
			}
		}

		return result;
	}

	/**
	 * useAsciiLexer
	 * 
	 * @return boolean
	 */
	public static boolean useAsciiLexer()
	{
		return getLexerType().equals("ascii"); //$NON-NLS-1$
	}

	/**
	 * useUnicodeLexer
	 * 
	 * @return boolean
	 */
	public static boolean useUnicodeLexer()
	{
		return getLexerType().equals("unicode"); //$NON-NLS-1$
	}

	/**
	 * useCodeBasedLexer
	 * 
	 * @return boolean
	 */
	public static boolean useCodeBasedLexer()
	{
		return getLexerType().equals("code"); //$NON-NLS-1$
	}

	/**
	 * useMatcherLexer
	 * 
	 * @return boolean
	 */
	public static boolean useMatcherLexer()
	{
		return getLexerType().equals("matcher"); //$NON-NLS-1$
	}
}
