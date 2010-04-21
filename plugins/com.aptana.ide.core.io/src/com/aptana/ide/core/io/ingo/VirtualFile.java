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
package com.aptana.ide.core.io.ingo;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Base implementation of IVirtualFile.
 * 
 * @author Ingo Muschenetz
 */
public abstract class VirtualFile implements IVirtualFile
{

	/**
	 * @see IVirtualFile#isLocal()
	 */
	public boolean isLocal()
	{
		return false;
	}

	/**
	 * @see IVirtualFile#getFiles()
	 */
	public IVirtualFile[] getFiles() throws ConnectionException, IOException
	{
		return getFiles(false, true);
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#getTimeStamp()
	 */
	public String getTimeStamp()
	{
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
		return df.format(new Date(getModificationMillis()));
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#setTimeStamp(java.lang.String)
	 */
	public void setTimeStamp(String timeStamp)
	{
		// Does nothing by default
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#getStream(Client)
	 */
	public InputStream getStream(Client client) throws ConnectionException, VirtualFileManagerException, IOException
	{
		InputStream input = getStream();
		client.streamGot(input);
		return input;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#putStream(InputStream)
	 */
	public void putStream(InputStream input) throws ConnectionException, VirtualFileManagerException, IOException
	{
		putStream(input, null);
	}

	/**
	 * Remove a duplicate file from the list of files.
	 * 
	 * @param files
	 *            list of original files
	 * @param toRemove
	 *            file to remove
	 * @return the list of files, filtered
	 */
	public static IVirtualFile[] removeDuplicateFile(IVirtualFile[] files, IVirtualFile toRemove)
	{
		List<IVirtualFile> filteredSources = new ArrayList<IVirtualFile>();

		if (files != null)
		{
			IVirtualFile element;
			for (int i = 0; i < files.length; i++)
			{
				element = files[i];

				if (element.getParentFile() == null
						|| element.getParentFile().getAbsolutePath().equals(toRemove.getAbsolutePath()) == false)
				{
					filteredSources.add(element);
				}
			}
		}

		return filteredSources.toArray(new IVirtualFile[filteredSources.size()]);
	}

	/**
	 * Re-parents this list of files under the new manager. This function should be replaced by an "import" function
	 * 
	 * @param manager
	 * @param files
	 * @return IVirtualFile[]
	 * @deprecated
	 */
	public static IVirtualFile[] reparentFiles(IVirtualFileManager manager, IVirtualFile[] files)
	{
		List<IVirtualFile> newFiles = new ArrayList<IVirtualFile>();
		IVirtualFile file;
		IVirtualFile newFile;

		for (int i = 0; i < files.length; i++)
		{
			file = files[i];

			if (file.isDirectory())
			{
				newFile = manager.createVirtualDirectory(file.getAbsolutePath());
			}
			else
			{
				newFile = manager.createVirtualFile(file.getAbsolutePath());
			}

			newFiles.add(newFile);
		}

		return newFiles.toArray(new IVirtualFile[newFiles.size()]);
	}

	/**
	 * Creates a list of all parent directories of the current file (or directory)
	 * 
	 * @param file
	 * @param sourceManager
	 * @return IVirtualFile[]
	 */
	public static IVirtualFile[] getParentDirectories(IVirtualFile file, IVirtualFileManager sourceManager)
	{
		List<IVirtualFile> parentDirs = new ArrayList<IVirtualFile>();

		if (sourceManager.containsFile(file))
		{
			IVirtualFile currentFile = file;

			while (currentFile != null)
			{
				if (currentFile.equals(sourceManager.getBaseFile()))
				{
					break;
				}

				parentDirs.add(0, currentFile); // add at beginning of list, as we want most "distant" folder first
				currentFile = currentFile.getParentFile();
			}
		}

		return parentDirs.toArray(new IVirtualFile[parentDirs.size()]);
	}
}
