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
package com.aptana.ide.update.preferences;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.update.ui.UpdateUIActivator;

/**
 * @author Pavel Petrochenko
 * 
 */
public final class AptanaUpdatePatchProxy
{
	
	private AptanaUpdatePatchProxy(){
		
	}

	/**
	 * @return true if Aptana search extension is installed
	 */
	public static boolean hasAptanaUpdate()
	{
		try
		{
			AptanaUpdatePatchProxy.class.getClassLoader().loadClass(
					"com.aptana.update.core.extension.ThreadLimitManager"); //$NON-NLS-1$
			return true;
		} catch (ClassNotFoundException e)
		{
			return false;
		}
	}

	/**
	 * @return max quanty of threads that are downloading features
	 */
	public static int getMaxFeatureDownloadingThreads()
	{
		try
		{
			Class<?> proxy = AptanaUpdatePatchProxy.class
					.getClassLoader()
					.loadClass(
							"com.aptana.update.core.extension.ThreadLimitManager"); //$NON-NLS-1$
			try
			{
				return (Integer) proxy
						.getMethod(
								"getMaxFeatureDownloadingThreads", new Class[0]).invoke(null); //$NON-NLS-1$

			} catch (Exception e)
			{
				logError(e);
				return 0;
			}
		} catch (ClassNotFoundException e)
		{
			return 0;
		}
	}

	private static void logError(Exception e)
	{
		IdeLog.logError(UpdateUIActivator.getDefault(),
				"Error while interacting with aptana update extension", e); //$NON-NLS-1$
	}

	/**
	 * @return pause time
	 */
	public static int getPauseTime()
	{
		try
		{
			Class<?> proxy = AptanaUpdatePatchProxy.class
					.getClassLoader()
					.loadClass(
							"com.aptana.update.core.extension.ThreadLimitManager"); //$NON-NLS-1$
			try
			{
				return (Integer) proxy
						.getMethod("getPauseTime", new Class[0]).invoke(null); //$NON-NLS-1$

			} catch (Exception e)
			{
				logError(e);
				return 0;
			}
		} catch (ClassNotFoundException e)
		{
			return 0;
		}
	}

	/**
	 * @param selection
	 */
	public static void setMaxFeatureDownloadingThreads(int selection)
	{
		try
		{
			Class<?> proxy = AptanaUpdatePatchProxy.class
					.getClassLoader()
					.loadClass(
							"com.aptana.update.core.extension.ThreadLimitManager"); //$NON-NLS-1$
			try
			{
				proxy
						.getMethod(
								"setMaxFeatureDownloadingThreads", new Class[] { int.class }).invoke(null, selection); //$NON-NLS-1$

			} catch (Exception e)
			{
				logError(e);
			}
		} catch (ClassNotFoundException e)
		{

		}
	}

	/**
	 * @param selection
	 */
	public static void setMaxLoadingThreads(int selection)
	{
		try
		{
			Class<?> proxy = AptanaUpdatePatchProxy.class
					.getClassLoader()
					.loadClass(
							"com.aptana.update.core.extension.ThreadLimitManager"); //$NON-NLS-1$
			try
			{
				proxy
						.getMethod(
								"setMaxLoadingThreads", new Class[] { int.class }).invoke(null, selection); //$NON-NLS-1$

			} catch (Exception e)
			{
				logError(e);
			}
		} catch (ClassNotFoundException e)
		{

		}
	}

	/**
	 * @param selection
	 */
	public static void setPauseTime(int selection)
	{
		try
		{
			Class<?> proxy = AptanaUpdatePatchProxy.class
					.getClassLoader()
					.loadClass(
							"com.aptana.update.core.extension.ThreadLimitManager"); //$NON-NLS-1$
			try
			{
				proxy
						.getMethod("setPauseTime", new Class[] { int.class }).invoke(null, selection); //$NON-NLS-1$

			} catch (Exception e)
			{
				logError(e);
			}
		} catch (ClassNotFoundException e)
		{

		}
	}
}
