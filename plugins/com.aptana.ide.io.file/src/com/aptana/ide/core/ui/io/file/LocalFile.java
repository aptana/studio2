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
package com.aptana.ide.core.ui.io.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.filechooser.FileSystemView;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.IFileProgressMonitor;
import com.aptana.ide.core.io.IVirtualFile;
import com.aptana.ide.core.io.IVirtualFileManager;
import com.aptana.ide.core.io.VirtualFile;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.ImageUtils;

/**
 * @author Robin Debreuil
 */
public class LocalFile extends VirtualFile implements ILocalFile
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
	private File _file;
	private LocalFileManager _manager;
	private long _permissions = 0;

	private Image _image = null;

	/**
	 * LocalFile
	 * 
	 * @param manager
	 * @param file
	 */
	public LocalFile(LocalFileManager manager, File file)
	{
		this._file = file;
		this._manager = manager;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#canRead()
	 */
	public boolean canRead()
	{
		boolean result = false;

		if (this._file != null)
		{
			result = this._file.canRead();
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#canWrite()
	 */
	public boolean canWrite()
	{
		boolean result = false;

		if (this._file != null)
		{
			result = this._file.canWrite();
		}

		return result;
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
	 * @see com.aptana.ide.core.io.IVirtualFile#delete()
	 */
	public boolean delete()
	{
		return this._file.delete();
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
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0)
	{
		boolean result = false;

		if (arg0 instanceof LocalFile)
		{
			String s = ((LocalFile) arg0).getAbsolutePath();

			if (s.equals(this.getAbsolutePath()))
			{
				result = true;
			}
		}
		return result;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#exists()
	 */
	public boolean exists()
	{
		return this._file.exists();
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#getAbsolutePath()
	 */
	public String getAbsolutePath()
	{
		try
		{
			return this._file.getCanonicalPath();
		}
		catch (IOException e)
		{
			return this._file.getAbsolutePath();
		}
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#getCreationMillis()
	 */
	public long getCreationMillis()
	{
		return 0;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#getExtension()
	 */
	public String getExtension()
	{
		String result = ""; //$NON-NLS-1$
		String path = this._file.getName();
		int lastDot = path.lastIndexOf("."); //$NON-NLS-1$
		if (lastDot > -1 && lastDot < path.length() - 2)
		{
			result = path.substring(lastDot);
		}
		return result;
	}

	/**
	 * getFile
	 * 
	 * @return File
	 */
	public File getFile()
	{
		return this._file;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#getFileManager()
	 */
	public IVirtualFileManager getFileManager()
	{
		return this._manager;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#getFiles()
	 */
	public IVirtualFile[] getFiles() throws IOException
	{
		return this._manager.getFiles(this);
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#getFiles(boolean, boolean)
	 */
	public IVirtualFile[] getFiles(boolean recurse, boolean includeCloakedFiles) throws IOException
	{
		return this._manager.getFiles(this, recurse, includeCloakedFiles);
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#getGroup()
	 */
	public String getGroup()
	{
		// TODO: Implement
		return ""; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#getImage()
	 */
	public Image getImage()
	{
		if (_image == null)
		{
			return ImageUtils.getIcon(this._file, null);
		}
		else
		{
			return _image;
		}
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#getModificationMillis()
	 */
	public long getModificationMillis()
	{
		return this._file.lastModified();
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#getName()
	 */
	public String getName()
	{
		String fileName = this._file.getName();
		if (!CoreUIUtils.onMacOSX)
		{
			// previous version forgot to understand that a system display name will also
			// hide file extensions, which causes all manner of problems. Here, we limit
			// it to just the files we know will be a problem.
			if (fileName == null || StringUtils.EMPTY.equals(fileName) || !filesys.isFileSystem(this._file))
			{
				String tempFileName = filesys.getSystemDisplayName(this._file);
				if (tempFileName != null)
				{
					fileName = tempFileName;
				}
			}
		}

		return fileName;

	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#getOwner()
	 */
	public String getOwner()
	{
		// TODO: Implement
		return null;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#getParentFile()
	 */
	public IVirtualFile getParentFile()
	{
		IVirtualFile result = null;
		File parent = (!CoreUIUtils.onMacOSX) ? filesys.getParentDirectory(this._file) : this._file.getParentFile();
		if (parent != null)
		{
			result = new LocalFile(this._manager, parent);
		}
		return result;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#getPath()
	 */
	public String getPath()
	{
		return this._file.getPath();
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#getPermissions()
	 */
	public long getPermissions()
	{
		return this._permissions;
	}

	/**
	 * @see IVirtualFile#getRelativePath()
	 */
	public String getRelativePath()
	{
		String basePath = ""; //$NON-NLS-1$
		String path = this.getFileManager().getBasePath();
		if (path != null)
		{
			basePath = this.getFileManager().getBaseFile().getAbsolutePath();
			if (path.equals(LocalProtocolManager.FileSystemRoots))
			{
				basePath = basePath.substring(0, basePath.length() - path.length());
			}
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
	 * getSize
	 * 
	 * @return long
	 */
	public long getSize()
	{
		return this._file.length();
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#getStream()
	 */
	public InputStream getStream()
	{
		return this._manager.getStream(this);
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#hasFiles()
	 */
	public boolean hasFiles()
	{
		return hasFiles(false);
	}

	/**
	 * hasFiles
	 * 
	 * @param includeCloakedFiles
	 * @return boolean
	 */
	public boolean hasFiles(boolean includeCloakedFiles)
	{
		return isDirectory();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return this.getAbsolutePath().hashCode();
	}

	/**
	 * @see IVirtualFile#isCloaked()
	 */
	public boolean isCloaked()
	{
		return this._manager.isFileCloaked(this);
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#isDirectory()
	 */
	public boolean isDirectory()
	{
		if (CoreUIUtils.runningOnWindows && (this._file.getName().endsWith(".zip") //$NON-NLS-1$
				|| this._file.getName().endsWith(".rar"))) //$NON-NLS-1$
		{
			return false;
		}
		else
		{
			return this._file.isDirectory();
		}
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#isFile()
	 */
	public boolean isFile()
	{
		return this._file.isFile();
	}

	/**
	 * @see IVirtualFile#isLocal()
	 */
	public boolean isLocal()
	{
		return true;
	}

	/**
	 * @see com.aptana.ide.core.io.VirtualFile#isLink()
	 */
	public boolean isLink()
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#putStream(java.io.InputStream, IFileProgressMonitor)
	 */
	public void putStream(InputStream input, IFileProgressMonitor monitor) throws IOException
	{
		this._manager.putStream(input, this, monitor);
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#rename(java.lang.String)
	 */
	public boolean rename(String newName)
	{
		return this._manager.renameFile(this, newName);
	}

	/**
	 * @see IVirtualFile#setCloaked(boolean)
	 */
	public void setCloaked(boolean cloak)
	{
		if (cloak)
		{
			this._manager.addCloakedFile(this);
		}
		else
		{
			this._manager.removeCloakedFile(this);
		}
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#setGroup(java.lang.String)
	 */
	public void setGroup(String group)
	{
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#setImage(org.eclipse.swt.graphics.Image)
	 */
	public void setImage(Image image)
	{
		_image = image;
	}

	/**
	 * package local method to reset the internal file, needed for 'rename'.
	 * 
	 * @param file
	 */
	void setInternalFile(File file)
	{
		this._file = file;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#setModificationMillis(long)
	 */
	public void setModificationMillis(long modificationTime)
	{
		this._file.setLastModified(modificationTime);
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#setOwner(java.lang.String)
	 */
	public void setOwner(String owner)
	{
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFile#setPermissions(long)
	 */
	public void setPermissions(long permissions)
	{
		this._permissions = permissions;
	}
}
