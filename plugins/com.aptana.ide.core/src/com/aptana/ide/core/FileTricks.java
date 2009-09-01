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

import java.io.*;
import java.lang.reflect.*;

/**
 * Pulled from URL http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5036988
 * 
 * @author Ingo Muschenetz
 */
public final class FileTricks
{
	private static Class<?> cShellFolder, cShellFolderManager;
	private static Constructor ctrShellFolderManager;
	private static Method mCreateShellFolder;
	private static Object sfm;

	static
	{
		try
		{
			cShellFolder = Class.forName("sun.awt.shell.ShellFolder"); //$NON-NLS-1$
			cShellFolderManager = Class.forName("sun.awt.shell.ShellFolderManager"); //$NON-NLS-1$
			ctrShellFolderManager = cShellFolderManager.getDeclaredConstructor(new Class[] {});
			ctrShellFolderManager.setAccessible(true);
			sfm = ctrShellFolderManager.newInstance(new Object[] {});
			mCreateShellFolder = cShellFolderManager.getDeclaredMethod("createShellFolder", new Class[] { File.class }); //$NON-NLS-1$
			mCreateShellFolder.setAccessible(true);
		}
		catch (Exception e)
		{
			// This will happen on a non-Sun JVM
			// IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.FileTricks_AWT_UnableToInstantiate, e);
			cShellFolder = null;
			cShellFolderManager = null;
			ctrShellFolderManager = null;
			mCreateShellFolder = null;
			sfm = null;
		}
	}

	/**
	 * Hidden constructor for utility class
	 */
	protected FileTricks()
	{

	}

	/**
	 * attemptReplaceWithShellFolder
	 * 
	 * @param actual
	 * @return File
	 */
	public static File attemptReplaceWithShellFolder(File actual)
	{
		File result = actual;
		if (cShellFolder != null && !cShellFolder.isInstance(actual))
		{
			try
			{
				result = (File) mCreateShellFolder.invoke(sfm, (new Object[] { actual }));
			}
			catch (Exception e)
			{
				IdeLog.logInfo(AptanaCorePlugin.getDefault(), Messages.FileTricks_AWT_UnableToReplaceFolder, e);
			}
		}
		return result;
	}

	/**
	 * FileSnapshot
	 */
	public static final class FileSnapshot
	{
		private static Method mGetBooleanAttributes;
		private static int BA_DIRECTORY, BA_EXISTS, BA_REGULAR, BA_HIDDEN;
		private static Object fs;

		/**
		 * isDirectory
		 */
		public final boolean isDirectory;

		/**
		 * exists
		 */
		public final boolean exists;

		/**
		 * isRegular
		 */
		public final boolean isRegular;

		/**
		 * isHidden
		 */
		public final boolean isHidden;

		/**
		 * FileSnapshot
		 * 
		 * @param f
		 */
		public FileSnapshot(File f)
		{
			boolean e, d, r, h;

			try
			{
				int ba = ((Integer) mGetBooleanAttributes.invoke(fs, new Object[] { f })).intValue();
				d = (ba & BA_DIRECTORY) != 0;
				e = (ba & BA_EXISTS) != 0;
				r = (ba & BA_REGULAR) != 0;
				h = (ba & BA_HIDDEN) != 0;
			}
			catch (Exception x)
			{
				d = f.isDirectory();
				e = d || f.exists();
				r = f.isFile();
				h = f.isHidden();
			}

			isDirectory = d;
			exists = e;
			isRegular = r;
			isHidden = h;
		}

		static
		{
			try
			{
				Class<?> cFile = Class.forName("java.io.File"); //$NON-NLS-1$
				Class<?> cFileSystem = Class.forName("java.io.FileSystem"); //$NON-NLS-1$
				mGetBooleanAttributes = cFileSystem.getDeclaredMethod("getBooleanAttributes", //$NON-NLS-1$
						new Class[] { File.class });
				Field fBA_EXISTS = cFileSystem.getDeclaredField("BA_EXISTS"); //$NON-NLS-1$
				Field fBA_REGULAR = cFileSystem.getDeclaredField("BA_REGULAR"); //$NON-NLS-1$
				Field fBA_DIRECTORY = cFileSystem.getDeclaredField("BA_DIRECTORY"); //$NON-NLS-1$
				Field fBA_HIDDEN = cFileSystem.getDeclaredField("BA_HIDDEN"); //$NON-NLS-1$
				Field fFs = cFile.getDeclaredField("fs"); //$NON-NLS-1$

				mGetBooleanAttributes.setAccessible(true);
				fFs.setAccessible(true);
				fBA_EXISTS.setAccessible(true);
				fBA_REGULAR.setAccessible(true);
				fBA_DIRECTORY.setAccessible(true);
				fBA_HIDDEN.setAccessible(true);

				BA_EXISTS = ((Integer) fBA_EXISTS.get(null)).intValue();
				BA_REGULAR = ((Integer) fBA_REGULAR.get(null)).intValue();
				BA_DIRECTORY = ((Integer) fBA_DIRECTORY.get(null)).intValue();
				BA_HIDDEN = ((Integer) fBA_HIDDEN.get(null)).intValue();
				fs = fFs.get(null);
			}
			catch (Exception e)
			{
			}
		}
	}
}
