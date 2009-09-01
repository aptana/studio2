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
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.swing.filechooser.FileSystemView;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.ConnectionException;
import com.aptana.ide.core.io.IFileProgressMonitor;
import com.aptana.ide.core.io.IVirtualFile;
import com.aptana.ide.core.io.IVirtualFileManager;
import com.aptana.ide.core.io.VirtualFile;
import com.aptana.ide.core.io.VirtualFileManagerException;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.ImageUtils;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class CompressedFile extends VirtualFile implements ILocalFile
{

	/**
	 * filesys
	 */
	protected static FileSystemView filesys;

	static
	{
		if (CoreUIUtils.onMacOSX == false)
		{
			filesys = FileSystemView.getFileSystemView();
		}
	}

	private long permissions = 0;
	private File file;
	private LocalFileManager manager;
	private ZipFile zippedFile;
	private Image image = null;

	/**
	 * LocalFile
	 * 
	 * @param manager
	 * @param file
	 */
	public CompressedFile(LocalFileManager manager, File file)
	{
		this.file = file;
		this.manager = manager;
		try
		{
			this.zippedFile = new ZipFile(this.file);
		}
		catch (ZipException e)
		{
		}
		catch (IOException e)
		{
		}
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#canRead()
	 */
	public boolean canRead()
	{
		boolean result = false;

		if (this.file != null)
		{
			result = this.file.canRead();
		}
		return result;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#canWrite()
	 */
	public boolean canWrite()
	{
		boolean result = false;

		if (this.file != null)
		{
			result = this.file.canWrite();
		}
		return result;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#delete()
	 */
	public boolean delete() throws ConnectionException, VirtualFileManagerException
	{
		boolean result = false;

		if (this.file != null)
		{
			result = this.file.delete();
		}
		return result;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#exists()
	 */
	public boolean exists() throws ConnectionException
	{
		if (this.file != null)
		{
			return this.file.exists();
		}
		return false;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#getAbsolutePath()
	 */
	public String getAbsolutePath()
	{
		try
		{
			return this.file.getCanonicalPath();
		}
		catch (IOException e)
		{
			return this.file.getAbsolutePath();
		}
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
		String path = this.file.getName();
		int lastDot = path.lastIndexOf("."); //$NON-NLS-1$
		if (lastDot > -1 && lastDot < path.length() - 2)
		{
			result = path.substring(lastDot);
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
		Map<String, IVirtualFile> vfs = new HashMap<String, IVirtualFile>();
		if (zippedFile != null)
		{
			List<ZipEntry> entries = new ArrayList<ZipEntry>();
			Enumeration files = zippedFile.entries();
			while (files.hasMoreElements())
			{
				ZipEntry entry = (ZipEntry) files.nextElement();
				entries.add(entry);
			}
			Collections.sort(entries, new Comparator<ZipEntry>()
			{

				public int compare(ZipEntry o1, ZipEntry o2)
				{
					return o1.getName().compareToIgnoreCase(o2.getName());
				}

			});
			for (ZipEntry entry : entries)
			{
				String name = entry.getName();
				String[] paths = name.split("/"); //$NON-NLS-1$
				if (paths.length == 1)
				{
					CompressedFileEntry cfe = new CompressedFileEntry(entry, manager, this);
					cfe.setName(paths[0]);
					if (includeCloakedFiles || !cfe.isCloaked())
					{
						vfs.put(cfe.getName(), cfe);
					}
				}
				else if (paths.length > 1)
				{
					CompressedFileEntry parentEntry = (CompressedFileEntry) vfs.get(paths[0]);
					if (parentEntry == null)
					{
						parentEntry = new CompressedFileEntry(null, manager, this);
						parentEntry.setName(paths[0]);
						if (includeCloakedFiles || !parentEntry.isCloaked())
						{
							vfs.put(parentEntry.getName(), parentEntry);
						}
					}
					for (int i = 1; i < paths.length - 1; i++)
					{
						CompressedFileEntry newParentEntry = parentEntry.getEntry(paths[i]);
						if (newParentEntry == null)
						{
							newParentEntry = new CompressedFileEntry(null, manager, this);
							newParentEntry.setName(paths[i]);
							parentEntry.addEntry(newParentEntry);
						}
						parentEntry = newParentEntry;
					}
					if (parentEntry != null)
					{
						CompressedFileEntry cfe = new CompressedFileEntry(entry, manager, this);
						cfe.setName(paths[paths.length - 1]);
						if (includeCloakedFiles || !cfe.isCloaked())
						{
							parentEntry.addEntry(cfe);
						}
					}
				}
			}
		}
		return vfs.values().toArray(new IVirtualFile[0]);
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#getGroup()
	 */
	public String getGroup()
	{
		return ""; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#getImage()
	 */
	public Image getImage()
	{
		if (image == null)
		{
			return ImageUtils.getIcon(this.file, null);
		}
		else
		{
			return image;
		}
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#getModificationMillis()
	 */
	public long getModificationMillis()
	{
		return this.file.lastModified();
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#getName()
	 */
	public String getName()
	{
		String fileName = this.file.getName();
		if (!CoreUIUtils.onMacOSX)
		{
			// previous version forgot to understand that a system display name will also
			// hide file extensions, which causes all manner of problems. Here, we limit
			// it to just the files we know will be a problem.
			if (fileName == null || StringUtils.EMPTY.equals(fileName) || !filesys.isFileSystem(this.file))
			{
				String tempFileName = filesys.getSystemDisplayName(this.file);
				if (tempFileName != null)
				{
					fileName = tempFileName;
				}
			}
		}

		return fileName;

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
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#getPath()
	 */
	public String getPath()
	{
		return this.file.getPath();
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#getPermissions()
	 */
	public long getPermissions()
	{
		return this.permissions;
	}

	/**
	 * @see IVirtualFile#getRelativePath()
	 */
	public String getRelativePath()
	{
		String basePath = ""; //$NON-NLS-1$
		if (this.getFileManager().getBasePath() != null)
		{
			basePath = this.getFileManager().getBaseFile().getAbsolutePath();
		}

		if (basePath.length() <= this.getAbsolutePath().length())
		{
			return this.getAbsolutePath().substring(basePath.length());
		}
		else
		{
			return this.getAbsolutePath();
		}
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#getSize()
	 */
	public long getSize()
	{
		if (this.file != null)
		{
			return this.file.length();
		}
		return 0;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#getStream()
	 */
	public InputStream getStream() throws ConnectionException, VirtualFileManagerException
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#hasFiles()
	 */
	public boolean hasFiles()
	{
		return zippedFile != null && zippedFile.size() > 0;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#isDirectory()
	 */
	public boolean isDirectory()
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#isFile()
	 */
	public boolean isFile()
	{
		return zippedFile == null || zippedFile.size() == 0;
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
		this.image = image;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#setModificationMillis(long)
	 */
	public void setModificationMillis(long modificationTime) throws IOException, ConnectionException
	{
		if (this.file != null)
		{
			this.file.setLastModified(modificationTime);
		}
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
		this.permissions = permissions;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#editProperties(org.eclipse.swt.widgets.Shell)
	 */
	public void editProperties(Shell shell)
	{
		InfoDialog nld = new InfoDialog(shell);
		nld.setItem(this);
		nld.open();
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
		if (o instanceof LocalFile)
		{
			return this.getName().compareToIgnoreCase(((LocalFile) o).getName());
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
		return this.file;
	}

	/**
	 * @return the zippedFile
	 */
	public ZipFile getCompressedFile()
	{
		return zippedFile;
	}

}
