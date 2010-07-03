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
package com.aptana.ide.editors.junit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.aptana.ide.core.CoreStrings;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.css.CSSLanguageEnvironment;
import com.aptana.ide.editor.html.HTMLLanguageEnvironment;
import com.aptana.ide.editor.js.JSFileServiceFactory;
import com.aptana.ide.editor.js.JSLanguageEnvironment;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.profiles.Profile;
import com.aptana.ide.editors.profiles.ProfileManager;

/**
 * TestUtils
 * 
 * @author Ingo Muschenetz
 */
public class TestUtils
{
	/**
	 * Protected constructor for utility class
	 */
	protected TestUtils()
	{
	}

	/**
	 * Creates a new file with the specified contents
	 * 
	 * @param name
	 * @param suffix
	 * @param contents
	 * @return Returns a new file with the specified contents.
	 */
	public static File createFileFromString(String name, String suffix, String contents)
	{
		File temp = null;

		try
		{
			// Create temp file.
			temp = File.createTempFile(name, suffix);

			// Delete temp file when program exits.
			temp.deleteOnExit();

			// Write to temp file
			BufferedWriter out = new BufferedWriter(new FileWriter(temp));
			out.write(contents);
			out.close();
		}
		catch (IOException e)
		{
		}

		return temp;
	}

	/**
	 * Creates a new file with the specified contents
	 * 
	 * @param name
	 * @param suffix
	 * @param contents
	 * @return Returns a new file with the specified contents.
	 */
	public static File createFileFromString(String name, String contents)
	{
		File temp = null;

		try
		{
			// Create temp file.
			temp = new File(name);

			// Delete temp file when program exits.
			temp.deleteOnExit();

			// Write to temp file
			BufferedWriter out = new BufferedWriter(new FileWriter(temp));
			out.write(contents);
			out.close();
		}
		catch (IOException e)
		{
		}

		return temp;
	}
	
	/**
	 * Ensures the JS, HTML and CSS environments are loaded
	 */
	public static void loadEnvironment()
	{
		UnifiedEditorsPlugin plugin = UnifiedEditorsPlugin.getDefault();
		if (plugin == null)
		{
			plugin = new UnifiedEditorsPlugin();
		}

		CSSLanguageEnvironment.SLEEP_DELAY = 0;
		CSSLanguageEnvironment.getInstance();
		JSLanguageEnvironment.SLEEP_DELAY = 0;
		JSLanguageEnvironment.getInstance();
		HTMLLanguageEnvironment.SLEEP_DELAY = 0;
		HTMLLanguageEnvironment.getInstance();

	}

	/**
	 * runEventQueue
	 */
	public static void runEventQueue()
	{
		boolean result = true;
		while (result)
		{
			Display display = PlatformUI.getWorkbench().getDisplay();
			result = display.readAndDispatch();
		}
	}

	/**
	 * Ensures the JS, HTML and CSS environments are loaded Also allows other UI events to happen while waiting.
	 */
	public static void waitForParse()
	{
		waitForParse(5000);
	}

	/**
	 * Ensures the JS, HTML and CSS environments are loaded Also allows other UI events to happen while waiting.
	 * 
	 * @param timeToWait
	 *            in milliseconds
	 */
	public static void waitForParse(int timeToWait)
	{
		for (int i = 0; i < timeToWait / 50; i++)
		{
			runEventQueue();
			try
			{
				Thread.sleep(50);
			}
			catch (InterruptedException e)
			{
				IdeLog.logError(EditorsJunitPlugin.getDefault(), "Error", e); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Gets a copy of the current ProfileManager
	 * 
	 * @return ProfileManager
	 */
	public static ProfileManager createProfileManager()
	{
		UnifiedEditorsPlugin plugin = UnifiedEditorsPlugin.getDefault();
		ProfileManager pm = plugin.getProfileManager();
		pm.addLanguageSupport(JSMimeType.MimeType, JSLanguageEnvironment.getInstance(), JSFileServiceFactory
				.getInstance());
		return pm;
	}

	/**
	 * createProfile
	 * 
	 * @param name
	 * @param path
	 * @param files
	 * @return Profile
	 */
	public static Profile createProfile(String name, String path, File[] files)
	{
		Profile profile = new Profile(name, path);
		String paths = StringUtils.EMPTY;
		String comma = StringUtils.EMPTY;
		for (int i = 0; i < files.length; i++)
		{
			File file = files[i];
			try
			{
				paths += comma + file.getCanonicalPath();
			}
			catch (IOException e)
			{
				IdeLog.logError(EditorsJunitPlugin.getDefault(), CoreStrings.ERROR, e);
			}
			comma = ","; //$NON-NLS-1$
		}
		// IPath[] ipaths = profile.stringToPaths(paths);
		// profile.load(ipaths);
		profile.load(paths.split(",")); //$NON-NLS-1$
		return profile;

	}

	/**
	 * createStaticProfile
	 * 
	 * @param name
	 * @param files
	 * @return Profile
	 */
	public static Profile createStaticProfile(String name, File[] files)
	{
		return createProfile(name, "static://" + name, files); //$NON-NLS-1$
	}

	/**
	 * createProfile
	 * 
	 * @param name
	 * @param file
	 * @param files
	 * @return Profile
	 */
	public static Profile createProfile(String name, File file, File[] files)
	{
		try
		{
			return createProfile(name, file.getCanonicalPath(), files);
		}
		catch (IOException e)
		{
			IdeLog.logError(EditorsJunitPlugin.getDefault(), "Error", e); //$NON-NLS-1$
			return null;
		}
	}

	/**
	 * find
	 * 
	 * @param array
	 * @param stringToFind
	 * @return boolean
	 */
	public static boolean find(String[] array, String stringToFind)
	{
		for (int i = 0; i < array.length; i++)
		{
			String s = array[i];
			if (s.equals(stringToFind))
			{
				return true;
			}
		}
		return false;
	}
}
