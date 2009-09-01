/**
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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
package com.aptana.ide.core.ui.io.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.aptana.ide.core.io.ConnectionException;
import com.aptana.ide.core.io.IFileProgressMonitor;
import com.aptana.ide.core.io.IVirtualFile;
import com.aptana.ide.core.io.IVirtualFileManager;
import com.aptana.ide.core.io.VirtualFile;
import com.aptana.ide.core.io.VirtualFileManagerException;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class CompressedFileEntry extends VirtualFile implements ILocalFile
{

	private ZipEntry entry;
	private LocalFileManager manager;
	private CompressedFile parent;
	private String name;
	private List<CompressedFileEntry> entries;

	/**
	 * Creates a new compressed file entry
	 * 
	 * @param entry
	 * @param manager
	 * @param parent
	 */
	public CompressedFileEntry(ZipEntry entry, LocalFileManager manager, CompressedFile parent)
	{
		this.entry = entry;
		this.manager = manager;
		this.parent = parent;
		this.entries = new ArrayList<CompressedFileEntry>();
		this.name = null;
	}

	/**
	 * Adds entries
	 * 
	 * @param child
	 */
	public void addEntry(CompressedFileEntry child)
	{
		this.entries.add(child);
	}

	/**
	 * Gets an entry matching the name
	 * 
	 * @param name
	 * @return - entry
	 */
	public CompressedFileEntry getEntry(String name)
	{
		for (CompressedFileEntry entry : this.entries)
		{
			if (entry.getName().equals(name))
			{
				return entry;
			}
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#canRead()
	 */
	public boolean canRead()
	{
		return true;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#canWrite()
	 */
	public boolean canWrite()
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#delete()
	 */
	public boolean delete() throws ConnectionException, VirtualFileManagerException
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#exists()
	 */
	public boolean exists() throws ConnectionException
	{
		return true;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#getAbsolutePath()
	 */
	public String getAbsolutePath()
	{
		if (entry != null)
		{
			return entry.getName();
		}
		return this.name;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#getCreationMillis()
	 */
	public long getCreationMillis()
	{
		return 0;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#getExtension()
	 */
	public String getExtension()
	{
		String result = ""; //$NON-NLS-1$
		if (this.entry != null)
		{
			String path = this.entry.getName();
			int lastDot = path.lastIndexOf("."); //$NON-NLS-1$
			if (lastDot > -1 && lastDot < path.length() - 2)
			{
				result = path.substring(lastDot);
			}
		}
		return result;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#getFileManager()
	 */
	public IVirtualFileManager getFileManager()
	{
		return this.manager;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#getFiles(boolean, boolean)
	 */
	public IVirtualFile[] getFiles(boolean recurse, boolean includeCloakedFiles) throws ConnectionException,
			IOException
	{
		return this.entries.toArray(new IVirtualFile[0]);
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#getGroup()
	 */
	public String getGroup()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#getImage()
	 */
	public Image getImage()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#getModificationMillis()
	 */
	public long getModificationMillis()
	{
		return 0;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#getName()
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Sets the name
	 * 
	 * @param name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#getOwner()
	 */
	public String getOwner()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#getParentFile()
	 */
	public IVirtualFile getParentFile()
	{
		return this.parent;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#getPath()
	 */
	public String getPath()
	{
		return getAbsolutePath();
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#getPermissions()
	 */
	public long getPermissions()
	{
		return 0;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#getRelativePath()
	 */
	public String getRelativePath()
	{
		return getAbsolutePath();
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#getSize()
	 */
	public long getSize()
	{
		if (entry != null)
		{
			return entry.getSize();
		}
		return 0;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#getStream()
	 */
	public InputStream getStream() throws ConnectionException, VirtualFileManagerException
	{
		if (entry != null && parent != null & parent.getCompressedFile() != null)
		{
			try
			{
				return parent.getCompressedFile().getInputStream(entry);
			}
			catch (IOException e)
			{
			}
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#hasFiles()
	 */
	public boolean hasFiles()
	{
		return entries != null && entries.size() > 0;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#isDirectory()
	 */
	public boolean isDirectory()
	{
		if (this.entry != null)
		{
			return this.entry.isDirectory();
		}
		return true;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#isFile()
	 */
	public boolean isFile()
	{
		return !this.isDirectory();
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#isLink()
	 */
	public boolean isLink()
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#putStream(java.io.InputStream, IFileProgressMonitor)
	 */
	public void putStream(InputStream input, IFileProgressMonitor monitor) throws ConnectionException, 
			VirtualFileManagerException, IOException
	{

	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#rename(java.lang.String)
	 */
	public boolean rename(String newName) throws ConnectionException, VirtualFileManagerException
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#setGroup(java.lang.String)
	 */
	public void setGroup(String group)
	{

	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#setImage(org.eclipse.swt.graphics.Image)
	 */
	public void setImage(Image image)
	{

	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#setModificationMillis(long)
	 */
	public void setModificationMillis(long modificationTime) throws IOException, ConnectionException
	{

	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#setOwner(java.lang.String)
	 */
	public void setOwner(String owner)
	{

	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#setPermissions(long)
	 */
	public void setPermissions(long permissions)
	{

	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#editProperties(org.eclipse.swt.widgets.Shell)
	 */
	public void editProperties(Shell shell)
	{

	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#isCloaked()
	 */
	public boolean isCloaked()
	{
		return this.manager.isFileCloaked(this);
	}

	/**
	 * @see IVirtualFile#setCloaked(boolean)
	 */
	public void setCloaked(boolean cloak)
	{
		if (cloak)
		{
			this.manager.addCloakedFile(this);
		}
		else
		{
			this.manager.removeCloakedFile(this);
		}
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o)
	{
		if (o instanceof CompressedFileEntry)
		{
			return this.getName().compareToIgnoreCase(((CompressedFileEntry) o).getName());
		}
		else
		{
			return 0;
		}
	}

	/**
	 * @see com.aptana.ide.core.ui.io.file.ILocalFile#getFile()
	 */
	public File getFile()
	{
		return null;
	}

}
