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

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;

import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.IFileProgressMonitor;

/**
 * @author Paul Colton
 */
public interface IVirtualFileManager extends IConnectionPoint, Comparable, ISerializableSyncItem
{
//	/**
//	 * getBaseFile
//	 * 
//	 * @return The IVirtualFile of the basePath
//	 */
//	IVirtualFile getBaseFile();

	/**
	 * getBasePath
	 * 
	 * @return String
	 */
	String getBasePath();

	/**
	 * setBasePath
	 * 
	 * @param path
	 */
	void setBasePath(String path);
	
	/**
	 * Returns true if this vfm is editable
	 * @return - true if editable;
	 */
	boolean isEditable();

	/**
	 * resolveBasePath
	 * 
	 * @param path
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 */
	void resolveBasePath();

	/**
	 * Returns a descriptive label for use when presenting this file manager
	 * 
	 * @return a string used by various views. Often includes the path.
	 */
	String getDescriptiveLabel();

	/**
	 * getEventHandler
	 * 
	 * @return IVirtualFileManagerEventHandler
	 */
	IVirtualFileManagerEventHandler getEventHandler();

	/**
	 * setEventHandler
	 * 
	 * @param eventHandler
	 */
	void setEventHandler(IVirtualFileManagerEventHandler eventHandler);

	/**
	 * getCloakedFiles
	 * 
	 * @return IVirtualFile[]
	 */
	IVirtualFile[] getCloakedFiles();

	/**
	 * getCloakedFileExpressions
	 * 
	 * @return String[]
	 */
	String[] getCloakedFileExpressions();

	/**
	 * setCloakedFiles
	 * 
	 * @param files
	 */
	void setCloakedFiles(IVirtualFile[] files);

	/**
	 * Sets a file as cloaked
	 * 
	 * @param file
	 */
	void addCloakedFile(IFileStore file);

	/**
	 * Sets an expression to cloak all files that match the expression
	 * 
	 * @param fileExpression
	 */
	void addCloakExpression(String fileExpression);

	/**
	 * Remove a file as cloaked
	 * 
	 * @param file
	 */
	void removeCloakedFile(IFileStore file);

	/**
	 * Removes an expression to cloak all files that match the expression
	 * 
	 * @param fileExpression
	 */
	void removeCloakExpression(String fileExpression);

	/**
	 * is a file cloaked?
	 * 
	 * @param file
	 * @return boolean
	 */
	boolean isFileCloaked(IVirtualFile file);

	/**
	 * getFiles
	 * 
	 * @param file
	 * @param includeCloakedFiles
	 *            do we include cloaked files in the list?
	 * @return IVirtualFile[]
	 * @throws ConnectionException
	 * @throws IOException
	 */
	IVirtualFile[] getFiles(IVirtualFile file) throws IOException;

	/**
	 * getFiles
	 * 
	 * @param file
	 * @param recurse
	 * @param includeCloakedFiles
	 *            do we include cloaked files in the list?
	 * @return IVirtualFile[]
	 * @throws ConnectionException
	 * @throws IOException
	 */
	IVirtualFile[] getFiles(IVirtualFile file, boolean recurse, boolean includeCloakedFiles)
			throws IOException;

	/**
	 * Determines if this virtual file contains files
	 * 
	 * @param file
	 *            The IVirtualFile to check for files against
	 * @return Returns true if this virtual file is a directory containing other files
	 * @throws ConnectionException
	 * @throws IOException
	 */
	boolean hasFiles(IVirtualFile file) throws IOException;

	/**
	 * Get the string that is used to separate directories and files within a path
	 * 
	 * @return Returns the file separator string
	 */
	String getFileSeparator();

	/**
	 * Returns the time of the file as a String.
	 * 
	 * @param file
	 * @return String
	 */
	String getFileTimeString(IVirtualFile file);

	/**
	 * getGroup
	 * 
	 * @param file
	 * @return group name
	 */
	String getGroup(IVirtualFile file);

	/**
	 * setGroup
	 * 
	 * @param file
	 * @param groupName
	 */
	void setGroup(IVirtualFile file, String groupName);

	/**
	 * Gets a string hash representation of this object.
	 * 
	 * @return String
	 */
	String getHashString();

	/**
	 * setHidden
	 * 
	 * @param hidden
	 */
	void setHidden(boolean hidden);

	/**
	 * isHidden
	 * 
	 * @return boolean
	 */
	boolean isHidden();

	/**
	 * getId
	 * 
	 * @return long
	 */
	String getId();

	/**
	 * setId
	 * 
	 * @param id
	 */
	void setId(long id);

	/**
	 * getIcon
	 * 
	 * @return Icon
	 */
//	Image getImage();

	/**
	 * getIcon
	 * 
	 * @return Icon
	 */
	//Image getDisabledImage();

	/**
	 * setImage
	 * 
	 * @param image
	 */
	//void setImage(Image image);

	/**
	 * setImage
	 * 
	 * @param image
	 */
	//void setDisabledImage(Image image);

	/**
	 * Retrieve the name to use when displaying this file manager. For example "www.myCoolWebSite.com"
	 * 
	 * @return Returns this file manager's name
	 */
	String getNickName();

	/**
	 * setNickName
	 * 
	 * @param nickName
	 */
	void setNickName(String nickName);

	/**
	 * setTransient
	 * 
	 * @param value
	 */
	void setTransient(boolean value);

	/**
	 * isTransient
	 * 
	 * @return boolean
	 */
	boolean isTransient();

	/**
	 * getOwner
	 * 
	 * @param file
	 * @return owner name
	 */
	String getOwner(IVirtualFile file);

	/**
	 * setOwner
	 * 
	 * @param file
	 * @param ownerName
	 */
	void setOwner(IVirtualFile file, String ownerName);

	/**
	 * getProtocolManager
	 * 
	 * @return Returns the protocol manager for this file manager
	 */
	ProtocolManager getProtocolManager();

	/**
	 * getTimeOffset
	 * 
	 * @return long
	 * @throws ConnectionException
	 */
	long getTimeOffset();

	/**
	 * Are we currently connected?
	 * 
	 * @return boolean
	 */
	boolean isConnected();

	/**
	 * Establish a connection with the file manager
	 * 
	 * @throws ConnectionException
	 */
	void connect();

	/**
	 * Break the connection with the file manager
	 */
	void disconnect();

	/**
	 * Does this file manager contain this particular file?
	 * 
	 * @param file
	 *            The file to check
	 * @return yes, if the file is "inside" this manager
	 */
	boolean containsFile(IVirtualFile file);

	/**
	 * Creates a new directory within the file manager's file system
	 * 
	 * @param directoryFile
	 *            The path to the new directory
	 * @return Returns trues if the directory was created successfully.
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 */
	boolean createLocalDirectory(IVirtualFile directoryFile);

	/**
	 * Creates a new virtual file for the specified directory path. Note that this does not create a directory within
	 * the file system
	 * 
	 * @param path
	 *            The path to the new virtual directory
	 * @return IVirtualFile Returns a virtual file for the new directory
	 */
	IFileStore createVirtualDirectory(String path);

	/**
	 * Creates a new virtual file for this specified path. Note that this does not create a new file within the file
	 * system
	 * 
	 * @param path
	 *            The path to the new virtual file
	 * @return IVirtualFile Returns a virtual file for this new file
	 */
	IFileStore createVirtualFile(String path);

	/**
	 * Delete the specified file or directory from the file manager
	 * 
	 * @param file
	 *            The virtual file to remove
	 * @return Returns true if the file was deleted successfully
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 */
	boolean deleteFile(IFileStore file) throws CoreException;

	/**
	 * getStream
	 * 
	 * @param file
	 * @return InputStream
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 * @throws IOException
	 */
	InputStream getStream(IVirtualFile file) throws IOException;

	/**
	 * Move the virtual file to a new location
	 * 
	 * @param source
	 *            The source virtual file to move
	 * @param destination
	 *            The destination location where to move the file
	 * @return Returns true if the file was moved successfully
	 */
	boolean moveFile(IFileStore source, IFileStore destination);

	/**
	 * putStream
	 * 
	 * @param input
	 *            The input stream containing the data to store in the remote file
	 * @param targetFile
	 *            The remote file where to store the data from the input stream
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 * @throws IOException
	 */
	void putStream(InputStream input, IVirtualFile targetFile) throws 
			IOException;

	/**
	 * putStream
	 * 
	 * @param input
	 *            The input stream containing the data to store in the remote file
	 * @param targetFile
	 *            The remote file where to store the data from the input stream
	 * @param monitor
	 * 			  the monitor that handles the progress information
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 * @throws IOException
	 */
	void putStream(InputStream input, IVirtualFile targetFile, IFileProgressMonitor monitor) throws 
			IOException;


//	/**
//	 * putFile
//	 * 
//	 * @param sourceFile
//	 * @param targetFile
//	 * @throws ConnectionException
//	 * @throws VirtualFileManagerException
//	 * @throws IOException
//	 */
//	void putFile(IVirtualFile sourceFile, IVirtualFile targetFile) throws 
//			IOException;

//	/**
//	 * putFile
//	 * 
//	 * @param sourceFile
//	 * @param targetFile
//	 * @param monitor
//	 * @throws ConnectionException
//	 * @throws VirtualFileManagerException
//	 * @throws IOException
//	 */
//	void putFile2(IVirtualFile sourceFile, IVirtualFile targetFile, IFileProgressMonitor monitor) throws 
//			VirtualFileManagerException, IOException;

	/**
	 * refresh
	 */
	void refresh();

	/**
	 * Rename a virtual file
	 * 
	 * @param file
	 *            The virtual file to rename
	 * @param newName
	 *            The new name for the file
	 * @return Returns true if the rename was successful
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 */
	boolean renameFile(IFileStore file, String newName);

	/**
	 * Sets the time offset
	 * 
	 * @param timeOffset
	 */
	void setTimeOffset(long timeOffset);

	/**
	 * Resets the time offset cache
	 */
	void resetTimeOffsetCache();

	/**
	 * Set if we auto-calculate the server time offset
	 * 
	 * @param calculateOffset
	 *            do we calculate the offset
	 */
	void setAutoCalculateServerTimeOffset(boolean calculateOffset);

	/**
	 * Do we auto-calculate the server time offset
	 * 
	 * @return boolean
	 */
	boolean isAutoCalculateServerTimeOffset();

	/**
	 * Is the current file manager valid (meaning that the base path is valid)
	 * 
	 * @return boolean
	 */
	boolean isValid();

	/**
	 * Clones the virtual file manager. If cloning is not supported the virtual file manager should just return null.
	 * 
	 * @return - clone or null
	 */
	IVirtualFileManager cloneManager();

	/**
	 * Cancels all current and pending transfers.  Has no effect if none exists.
	 */
	void cancel();

	/**
	 * Specifies the number of connection pools to use.
	 * 
	 * @param initialSize
	 *            the initial number of connection pools
	 * @param maxSize
	 *            the maximum number of connection pools
	 */
	void setPoolSizes(int initialSize, int maxSize);

	VirtualFileManagerGroup getManagerGroup();

	void setManagerGroup(VirtualFileManagerGroup group);

	/**
	 * 
	 * @param clientFile
	 * @param targetServerFile
	 * @param iFileProgressMonitor
	 */
	void putFile(IVirtualFile clientFile, IVirtualFile targetServerFile,
			IFileProgressMonitor iFileProgressMonitor);

}
