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
package com.aptana.ide.filesystem.ftp.ingo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.regex.Pattern;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.ide.core.CoreConstants;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.IFileProgressMonitor;
import com.aptana.ide.core.io.ingo.ConnectionException;
import com.aptana.ide.core.io.ingo.IVirtualFile;
import com.aptana.ide.core.io.ingo.IVirtualFileManager;
import com.aptana.ide.core.io.ingo.VirtualFile;
import com.aptana.ide.core.io.ingo.VirtualFileManagerException;

/**
 * @author Kevin Lindsey
 */
public class FtpVirtualFile //extends VirtualFile
{
//	//private static Image iconFolder;
//	private static final long DEFAULT_PERMISSIONS = 0666;
//	private static long DEFAULT_MODIFICATION_MILLIS = 0;
//	private static Pattern SEPERATOR_PATTERN = Pattern.compile("/+"); //$NON-NLS-1$
//
//	/**
//	 * static constructor
//	 */
////	static
////	{
////		IWorkbench workbench;
////
////		try
////		{
////			workbench = PlatformUI.getWorkbench();
////		}
////		catch (Exception e)
////		{
////			workbench = null;
////		}
////
////		// NOTE: workbench should only be null during unit tests
////		if (workbench != null)
////		{
////			ISharedImages sharedImages = workbench.getSharedImages();
////
////			iconFolder = sharedImages.getImage(ISharedImages.IMG_OBJ_FOLDER);
////		}
////	}
//	private FtpVirtualFileManager _manager;
//	private String _path;
//	private String _name;
//	private long _modificationMillis;
//	private long _permissions;
//	private boolean _isDirectory;
//	//private Image _image;
//	private String _owner;
//	private String _group;
//	private long _size;
//
//	private String timeStamp;
//
//	/**
//	 * FtpVirtualFile
//	 * 
//	 * @param manager
//	 * @param name
//	 */
//	FtpVirtualFile(FtpVirtualFileManager manager, String name)
//	{
//		this(manager, name, DEFAULT_PERMISSIONS, DEFAULT_MODIFICATION_MILLIS, false, 0);
//	}
//
//	/**
//	 * FtpVirtualFile
//	 * 
//	 * @param manager
//	 * @param name
//	 * @param isDirectory
//	 */
//	FtpVirtualFile(FtpVirtualFileManager manager, String name, boolean isDirectory)
//	{
//		this(manager, name, DEFAULT_PERMISSIONS, DEFAULT_MODIFICATION_MILLIS, isDirectory, 0);
//	}
//
//	/**
//	 * FtpVirtualFile
//	 * 
//	 * @param manager
//	 * @param name
//	 * @param modificationMillis
//	 */
//	FtpVirtualFile(FtpVirtualFileManager manager, String name, long modificationMillis)
//	{
//		this(manager, name, DEFAULT_PERMISSIONS, modificationMillis, false, 0);
//	}
//
//	/**
//	 * FtpVirtualFile
//	 * 
//	 * @param manager
//	 * @param name
//	 * @param modificationMillis
//	 * @param isDirectory
//	 * @param size
//	 */
//	FtpVirtualFile(FtpVirtualFileManager manager, String name, long modificationMillis, boolean isDirectory, long size)
//	{
//		this(manager, name, DEFAULT_PERMISSIONS, modificationMillis, isDirectory, size);
//	}
//
//	/**
//	 * FtpVirtualFile
//	 * 
//	 * @param manager
//	 * @param name
//	 * @param modificationMillis
//	 * @param isDirectory
//	 */
//	FtpVirtualFile(FtpVirtualFileManager manager, String name, long permissions, long modificationMillis,
//			boolean isDirectory, long size)
//	{
//		this._manager = manager;
//		this._modificationMillis = modificationMillis;
//		this._group = StringUtils.EMPTY;
//		this._owner = StringUtils.EMPTY;
//		this._permissions = permissions;
//		this._isDirectory = isDirectory;
//		this._size = size;
//
//		String tempName;
//
//		if (name == null)
//		{
//			tempName = StringUtils.EMPTY;
//		}
//		else
//		{
//			tempName = this.normalize(name);
//		}
//
//		if (this._isDirectory && tempName.endsWith("/")) //$NON-NLS-1$
//		{
//			tempName = tempName.substring(0, tempName.length() - 1);
//		}
//
//		int index = tempName.lastIndexOf('/');
//
//		if (index == -1)
//		{
//			this._path = StringUtils.EMPTY;
//			this._name = tempName;
//		}
//		else
//		{
//			this._path = tempName.substring(0, index);
//			this._name = tempName.substring(index + 1);
//		}
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#canRead()
//	 */
//	public boolean canRead()
//	{
//		return (this._permissions & 0444) != 0;
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#canWrite()
//	 */
//	public boolean canWrite()
//	{
//		return (this._permissions & 0222) != 0;
//	}
//
//	/**
//	 * @see java.lang.Comparable#compareTo(java.lang.Object)
//	 */
//	public int compareTo(Object o)
//	{
//		int result = 0;
//
//		if (o instanceof IVirtualFile)
//		{
//			String thisName = this.getName();
//			String thatName = ((IVirtualFile) o).getName();
//
//			result = thisName.compareToIgnoreCase(thatName);
//		}
//
//		return result;
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#delete()
//	 */
//	public boolean delete() throws ConnectionException
//	{
//		return this._manager.deleteFile(this);
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#editProperties(org.eclipse.swt.widgets.Shell)
//	 */
////	public void editProperties(Shell shell)
////	{
////		//InfoDialog nld = new InfoDialog(shell);
//////		nld.setItem(this);
//////		nld.open();
////	}
//
//	/**
//	 * An item is equals if the file managers are the same, and the path is the same
//	 * 
//	 * @param o
//	 * @return boolean
//	 */
//	public boolean equals(Object o)
//	{
//		boolean result = false;
//
//		if (o != null && o instanceof FtpVirtualFile)
//		{
//			FtpVirtualFile obj = (FtpVirtualFile) o;
//			boolean sameManagers = obj.getFileManager() == this.getFileManager();
//			boolean samePath = obj.getAbsolutePath().equals(this.getAbsolutePath());
//
//			result = sameManagers && samePath;
//		}
//
//		return result;
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#exists()
//	 */
//	public boolean exists() throws ConnectionException
//	{
//		return this._manager.exists(this);
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#getAbsolutePath()
//	 */
//	public String getAbsolutePath()
//	{
//		String result;
//
//		if (this._path.length() == 0)
//		{
//			if (this._name.length() == 0)
//			{
//				result = this._manager.getFileSeparator();
//			}
//			else
//			{
//				result = this._manager.getFileSeparator() + this._name;
//			}
//		}
//		else
//		{
//			result = this._path + this._manager.getFileSeparator() + this._name;
//		}
//
//		return result;
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#getCreationMillis()
//	 */
//	public long getCreationMillis()
//	{
//		return 0;
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#getExtension()
//	 */
//	public String getExtension()
//	{
//		int index = this._name.lastIndexOf('.');
//
//		if (index != -1)
//		{
//			return this._name.substring(index);
//		}
//		else
//		{
//			return StringUtils.EMPTY;
//		}
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#getFileManager()
//	 */
//	public IVirtualFileManager getFileManager()
//	{
//		return this._manager;
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#getFiles(boolean, boolean)
//	 */
//	public IVirtualFile[] getFiles(boolean recurse, boolean includeCloakedFiles) throws ConnectionException,
//			IOException
//	{
//		return this._manager.getFiles(this, recurse, includeCloakedFiles);
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#getGroup()
//	 */
//	public String getGroup()
//	{
//		return this._group;
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#getImage()
//	 */
////	public Image getImage()
////	{
////		if (this._image == null)
////		{
////			if (isDirectory() == true)
////			{
////				this._image = iconFolder;
////			}
////			else
////			{
////				this._image = null;
////			}
////		}
////
////		return this._image;
////	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#getModificationMillis()
//	 */
//	public long getModificationMillis()
//	{
//		return this._modificationMillis;
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#getName()
//	 */
//	public String getName()
//	{
//		return this._name;
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#getOwner()
//	 */
//	public String getOwner()
//	{
//		return this._owner;
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#getParentFile()
//	 */
//	public IVirtualFile getParentFile()
//	{
//		return this._manager.createVirtualDirectory(this._path);
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#getPath()
//	 */
//	public String getPath()
//	{
//		return this._path;
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#getPermissions()
//	 */
//	public long getPermissions()
//	{
//		return this._permissions;
//	}
//
//	/**
//	 * @see IVirtualFile#getRelativePath()
//	 */
//	public String getRelativePath()
//	{
//		String basePath = this.getFileManager().getBaseFile().getAbsolutePath();
//		if (basePath.length() <= this.getAbsolutePath().length())
//		{
//			return this.getAbsolutePath().substring(basePath.length());
//		}
//		else
//		{
//			return this.getAbsolutePath();
//		}
//	}
//
//	/**
//	 * getSize
//	 * 
//	 * @return long size
//	 */
//	public long getSize()
//	{
//		return _size;
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#getStream()
//	 */
//	public InputStream getStream() throws VirtualFileManagerException
//	{
//		return this._manager.getStream(this);
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.VirtualFile#getTimeStamp()
//	 */
//	public String getTimeStamp()
//	{
//		return this.timeStamp;
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#hasFiles()
//	 */
//	public boolean hasFiles()
//	{
//		return this.isDirectory();
//	}
//
//	/**
//	 * We return a hash code unique for this object
//	 * 
//	 * @return int
//	 */
//	public int hashCode()
//	{
//		// 17 is arbitrary. Taken from "Effective Java"
//		int result = CoreConstants.HASH_SEED;
//
//		// 37 is an odd prime
//		result = CoreConstants.HASH_MULTIPLIER * result + getFileManager().hashCode();
//		result = CoreConstants.HASH_MULTIPLIER * result + getAbsolutePath().hashCode();
//		return result;
//	}
//
//	/**
//	 * internalSetGroup
//	 * 
//	 * @param group
//	 */
//	void internalSetGroup(String group)
//	{
//		if (group != null)
//		{
//			this._group = group;
//		}
//	}
//
//	/**
//	 * internalSetOwner
//	 * 
//	 * @param owner
//	 */
//	void internalSetOwner(String owner)
//	{
//		if (owner != null)
//		{
//			this._owner = owner;
//		}
//	}
//
//	/**
//	 * @see IVirtualFile#isCloaked()
//	 */
//	public boolean isCloaked()
//	{
//		return this._manager.isFileCloaked(this);
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#isDirectory()
//	 */
//	public boolean isDirectory()
//	{
//		return this._isDirectory;
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#isFile()
//	 */
//	public boolean isFile()
//	{
//		return this._isDirectory == false;
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.VirtualFile#isLink()
//	 */
//	public boolean isLink()
//	{
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	/**
//	 * normalize path by removing extra file separators
//	 * 
//	 * @param path
//	 * @return normalized path string
//	 */
//	private String normalize(String path)
//	{
//		return SEPERATOR_PATTERN.matcher(path).replaceAll(this.getFileManager().getFileSeparator());
//	}
//
//	/**
//	 * @throws ConnectionException
//	 * @see com.aptana.ide.core.io.IVirtualFile#putStream(java.io.InputStream, IFileProgressMonitor)
//	 */
//	public void putStream(InputStream input, IFileProgressMonitor monitor) throws ConnectionException, 
//			VirtualFileManagerException
//	{
//		this._manager.putStream(input, this, monitor);
//	}
//
//	/**
//	 * @throws VirtualFileManagerException
//	 * @see com.aptana.ide.core.io.IVirtualFile#rename(java.lang.String)
//	 */
//	public boolean rename(String newName) throws ConnectionException, VirtualFileManagerException
//	{
//		return this._manager.renameFile(this, newName);
//	}
//
//	/**
//	 * @see IVirtualFile#setCloaked(boolean)
//	 */
//	public void setCloaked(boolean cloak)
//	{
//		if (cloak)
//		{
//			this._manager.addCloakedFile(this);
//		}
//		else
//		{
//			this._manager.removeCloakedFile(this);
//		}
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#setGroup(java.lang.String)
//	 */
//	public void setGroup(String group)
//	{
//		this._manager.setGroup(this, group);
//		this._group = group;
//	}
//
//	/**
//	 * setImage
//	 * 
//	 * @param image
//	 */
////	public void setImage(Image image)
////	{
////		this._image = image;
////	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#setModificationMillis(long)
//	 */
//	public void setModificationMillis(long modificationTime) throws IOException, ConnectionException
//	{
//		this._manager.setModificationMillis(this, modificationTime);
//		this._modificationMillis = modificationTime;
//	}
//
//	/**
//	 * setName
//	 * 
//	 * @param newName
//	 */
//	void setName(String newName)
//	{
//		this._name = newName;
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#setOwner(java.lang.String)
//	 */
//	public void setOwner(String owner)
//	{
//		this._manager.setOwner(this, owner);
//		this._owner = owner;
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.IVirtualFile#setPermissions(long)
//	 */
//	public void setPermissions(long permissions)
//	{
//		if (this._manager.setPermissions(this, permissions))
//		{
//			this._permissions = permissions;
//		}
//	}
//
//	/**
//	 * @see com.aptana.ide.core.io.VirtualFile#setTimeStamp(java.lang.String)
//	 */
//	public void setTimeStamp(String timeStamp)
//	{
//		this.timeStamp = timeStamp;
//	}
//
//	@Override
//	public IFileInfo[] childInfos(int options, IProgressMonitor monitor)
//			throws CoreException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public String[] childNames(int options, IProgressMonitor monitor)
//			throws CoreException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IFileStore[] childStores(int options, IProgressMonitor monitor)
//			throws CoreException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void copy(IFileStore destination, int options,
//			IProgressMonitor monitor) throws CoreException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void delete(int options, IProgressMonitor monitor)
//			throws CoreException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public IFileInfo fetchInfo() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IFileInfo fetchInfo(int options, IProgressMonitor monitor)
//			throws CoreException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IFileStore getChild(IPath path) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IFileStore getChild(String name) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IFileStore getFileStore(IPath path) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IFileSystem getFileSystem() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public IFileStore getParent() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public boolean isParentOf(IFileStore other) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public IFileStore mkdir(int options, IProgressMonitor monitor)
//			throws CoreException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void move(IFileStore destination, int options,
//			IProgressMonitor monitor) throws CoreException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public InputStream openInputStream(int options, IProgressMonitor monitor)
//			throws CoreException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public OutputStream openOutputStream(int options, IProgressMonitor monitor)
//			throws CoreException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void putInfo(IFileInfo info, int options, IProgressMonitor monitor)
//			throws CoreException {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public File toLocalFile(int options, IProgressMonitor monitor)
//			throws CoreException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public URI toURI() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Object getAdapter(Class adapter) {
//		// TODO Auto-generated method stub
//		return null;
//	}
}
