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
package com.aptana.ide.pathtools.preferences;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.ide.pathtools.Activator;

/**
 * @author schitale
 *
 */
public class PathtoolsPreferences extends AbstractPreferenceInitializer {
	
	public static final String FOLDER_EXPLORE_COMMAND_KEY = "folderExploreCommand"; //$NON-NLS-1$
	public static final String FILE_EXPLORE_COMMAND_KEY = "fileExploreCommand"; //$NON-NLS-1$

	static String defaultFolderExploreCommand = ""; //$NON-NLS-1$
	static String defaultFileExploreCommand = ""; //$NON-NLS-1$

	public static final String SHELL_ON_FOLDER_COMMAND_KEY = "shellOnfolderCommand"; //$NON-NLS-1$
	public static final String SHELL_ON_FILE_COMMAND_KEY = "shellOnFileCommand"; //$NON-NLS-1$

	static String defaultShellOnFolderCommand = ""; //$NON-NLS-1$
	static String defaultShellOnFileCommand = ""; //$NON-NLS-1$

	static String defaultFolderCommands = ""; //$NON-NLS-1$
	static String defaultFileCommands = ""; //$NON-NLS-1$
	
	public static final String FILE_PATH = "{path}"; //$NON-NLS-1$
	public static final String FILE_PARENT_PATH = "{parent-path}"; //$NON-NLS-1$
	public static final String FILE_NAME= "{name}"; //$NON-NLS-1$
	public static final String FILE_PARENT_NAME = "{parent-name}"; //$NON-NLS-1$
	public static final String FILE_PATH_SLASHES = "{path-slashes}"; //$NON-NLS-1$
	public static final String FILE_PARENT_PATH_SLASHES = "{parent-path-slashes}"; //$NON-NLS-1$
	public static final String FILE_PATH_BACKSLASHES = "{path-backslashes}"; //$NON-NLS-1$
	public static final String FILE_PARENT_PATH_BACKSLASHES = "{parent-path-backslashes}"; //$NON-NLS-1$
	
	public static final String LAST_COPY_PATH_FORMAT = "lastCopyPathFormat"; //$NON-NLS-1$
	static final String defaultLLastCopyPathFormat = FILE_PATH;
	
	static {
		if (Platform.OS_MACOSX.equals(Platform.getOS())) {
			defaultFolderExploreCommand = "/usr/bin/open -a /System/Library/CoreServices/Finder.app \"" //$NON-NLS-1$
					+ FILE_PATH + "\""; //$NON-NLS-1$
			defaultFileExploreCommand = "/usr/bin/open -a /System/Library/CoreServices/Finder.app \"" //$NON-NLS-1$
					+ FILE_PARENT_PATH + "\""; //$NON-NLS-1$
			defaultShellOnFolderCommand = "/usr/bin/open -a /Applications/Utilities/Terminal.app"; //$NON-NLS-1$
			defaultShellOnFileCommand = "/usr/bin/open -a /Applications/Utilities/Terminal.app"; //$NON-NLS-1$
		} else if (Platform.OS_WIN32.equals(Platform.getOS())) {
			defaultFolderExploreCommand = "cmd /C start explorer /select,/e, \"" //$NON-NLS-1$
					+ FILE_PATH + "\""; //$NON-NLS-1$
			defaultFileExploreCommand = "cmd /C start explorer /select,/e, \"" //$NON-NLS-1$
					+ FILE_PATH + "\""; //$NON-NLS-1$
			defaultShellOnFolderCommand = "cmd /K start cd /D \"" //$NON-NLS-1$
					+ FILE_PATH + "\""; //$NON-NLS-1$
			defaultShellOnFileCommand = "cmd /K start cd /D \"" //$NON-NLS-1$
					+ FILE_PARENT_PATH + "\""; //$NON-NLS-1$
		} else if (Platform.OS_LINUX.equals(Platform.getOS())) {
			if (new File("/usr/bin/nautilus").exists()) { //$NON-NLS-1$
				defaultFolderExploreCommand = "/usr/bin/nautilus \"" //$NON-NLS-1$
						+ FILE_PATH + "\""; //$NON-NLS-1$
				defaultFileExploreCommand = "/usr/bin/nautilus \"" //$NON-NLS-1$
						+ FILE_PARENT_PATH + "\""; //$NON-NLS-1$
			} else if (new File("/usr/bin/konqueror").exists()) { //$NON-NLS-1$
				defaultFolderExploreCommand = "/usr/bin/konqueror \"" //$NON-NLS-1$
					+ FILE_PATH + "\""; //$NON-NLS-1$
				defaultFileExploreCommand = "/usr/bin/konqueror \"" //$NON-NLS-1$
					+ FILE_PARENT_PATH + "\""; //$NON-NLS-1$
			}
			defaultShellOnFolderCommand = "gnome-terminal --working-directory=\"" //$NON-NLS-1$
					+ FILE_PATH + "\""; //$NON-NLS-1$
			defaultShellOnFileCommand = "gnome-terminal --working-directory=\"" //$NON-NLS-1$
					+ FILE_PARENT_PATH + "\""; //$NON-NLS-1$
		}
	}

	@Override
	public void initializeDefaultPreferences() {
		Activator activator = Activator.getDefault();
		if (Platform.OS_MACOSX.equals(Platform.getOS())) {
			try {
				URL entry = activator.getBundle().getEntry("/scripts/cdterminal.scpt"); //$NON-NLS-1$
				if (entry != null) {
					String cdterminalDotScpt = FileLocator.toFileURL(entry).getFile();
					if (cdterminalDotScpt != null) {
						defaultShellOnFolderCommand = "/usr/bin/osascript \"" + cdterminalDotScpt + "\" \"" + FILE_PATH + "\""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						defaultShellOnFileCommand = "/usr/bin/osascript \"" + cdterminalDotScpt + "\" \"" + FILE_PARENT_PATH + "\""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
				}
			} catch (IOException el) {
			}
		}
		
		IEclipsePreferences node = new DefaultScope().getNode(Activator.getDefault().getBundle().getSymbolicName());
		node.put(FOLDER_EXPLORE_COMMAND_KEY,
				defaultFolderExploreCommand);
		node.put(FILE_EXPLORE_COMMAND_KEY, defaultFileExploreCommand);
		node.put(SHELL_ON_FOLDER_COMMAND_KEY, defaultShellOnFolderCommand);
		node.put(SHELL_ON_FILE_COMMAND_KEY, defaultShellOnFileCommand);
		node.put(LAST_COPY_PATH_FORMAT, defaultLLastCopyPathFormat);
	}

}
