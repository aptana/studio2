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
package com.aptana.ide.core.io;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Paul Colton
 */
public interface IVirtualFile extends Comparable
{

	public static interface Client
	{
		public void streamGot(InputStream input) throws ConnectionException, 
				VirtualFileManagerException, IOException;
	}

	/**
	 * editProperties
	 * 
	 * @param shell
	 */
	void editProperties(Shell shell);

	/**
	 * Get the creation time of this file in milliseconds
	 * 
	 * @return long
	 */
	long getCreationMillis();

	/**
	 * Determines if this virtual file represents a directory
	 * 
	 * @return Returns true if this virtual file is a directory
	 */
	boolean isDirectory();

	/**
	 * Determines if this virtual file represents a file
	 * 
	 * @return Returns true if this virtual file is a file
	 */
	boolean isFile();

	/**
	 * Determines if this virtual file represents a symbolic link
	 * 
	 * @return Returns true if this virtual file is a symbolic link
	 */
	boolean isLink();

	/**
	 * Get the extension for this virtual file
	 * 
	 * @return Returns the file extension including the leading '.'
	 */
	String getExtension();

	/**
	 * Set the file as cloaked or not
	 * 
	 * @param cloak
	 */
	void setCloaked(boolean cloak);

	/**
	 * Is this file cloaked?
	 * 
	 * @return boolean
	 */
	boolean isCloaked();

	/**
	 * Is this file local?
	 * 
	 * @return boolean
	 */
	boolean isLocal();

	/**
	 * Retrieve a list of files contained by this virtual file
	 * 
	 * @return Returns an array of virtual files. This will return an empty array if this virtual file contains no other
	 *         files
	 * @throws ConnectionException
	 * @throws IOException
	 */
	IVirtualFile[] getFiles() throws ConnectionException, IOException;

	/**
	 * Retrieve a list of files contained by this virtual file
	 * 
	 * @param recurse
	 *            Do we recurse through sub-directories?
	 * @param includeCloakedFiles
	 *            Do we include cloaked files in the list?
	 * @return Returns an array of virtual files. This will return an empty array if this virtual file contains no other
	 *         files
	 * @throws ConnectionException
	 * @throws IOException
	 */
	IVirtualFile[] getFiles(boolean recurse, boolean includeCloakedFiles) throws ConnectionException, IOException;

	/**
	 * Determines if this virtual file contains files
	 * 
	 * @return Returns true if this virtual file is a directory containing other files
	 */
	boolean hasFiles();

	/**
	 * Get the path, filename and extension of this virtual file
	 * 
	 * @return Returns the full path, name, and extension for this file
	 */
	String getAbsolutePath();

	/**
	 * Get the file manager that this file belongs to
	 * 
	 * @return Returns this file's owning file manager
	 */
	IVirtualFileManager getFileManager();

	/**
	 * Get the group to which this file belongs
	 * 
	 * @return This file's group name
	 */
	String getGroup();

	/**
	 * Change this file's group name
	 * 
	 * @param group
	 */
	void setGroup(String group);

	/**
	 * getImage
	 * 
	 * @return Image
	 */
	Image getImage();

	/**
	 * setImage
	 * 
	 * @param image
	 */
	void setImage(Image image);

	/**
	 * Get the modification time of this file in milliseconds
	 * 
	 * @return long
	 */
	long getModificationMillis();

	/**
	 * Gets the raw time stamp for the file
	 * 
	 * @return - time string
	 */
	String getTimeStamp();

	/**
	 * Sets the raw time stamp
	 * 
	 * @param timeStamp
	 */
	void setTimeStamp(String timeStamp);

	/**
	 * Sets the modification time of this file in milliseconds
	 * 
	 * @param modificationTime
	 * @throws IOException
	 * @throws ConnectionException
	 */
	void setModificationMillis(long modificationTime) throws IOException, ConnectionException;

	/**
	 * Get the filename and extension of this virtual file. This should exclude the parent directory of the file
	 * 
	 * @return Returns this virtual file's name and extension
	 */
	String getName();

	/**
	 * Get the owner of this file
	 * 
	 * @return This file's group name
	 */
	String getOwner();

	/**
	 * Change this file's owner
	 * 
	 * @param owner
	 */
	void setOwner(String owner);

	/**
	 * Retrieve the virtual file that contains file file
	 * 
	 * @return Returns this file's parent file
	 */
	IVirtualFile getParentFile();

	/**
	 * Doesn't include filename and extension. Includes trailing file separator
	 * 
	 * @return Returns the path to this virtual file
	 */
	String getPath();

	/**
	 * Set the file permissions on this file
	 * 
	 * @param permissions
	 *            The new file permission settings
	 */
	void setPermissions(long permissions);

	/**
	 * Get the file permissions for this file
	 * 
	 * @return Returns the file permissions settings for this file
	 */
	long getPermissions();

	/**
	 * getSize
	 * 
	 * @return long
	 */
	long getSize();

	/**
	 * Get an input stream for the contents of this virtual file
	 * 
	 * @return Returns an InputStream of null
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 * @throws IOException
	 */
	InputStream getStream() throws ConnectionException, VirtualFileManagerException, IOException;

	/**
	 * Gets an input stream for the contents of this virutal file, possibly asynchronously
	 * 
	 * @param client
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 */
	InputStream getStream(Client client) throws ConnectionException, VirtualFileManagerException, IOException;

	/**
	 * Put the content of the specified input stream into this virtual file. This will create a new file if it does not
	 * already exist. This will replace the entire content of the file if it does exist
	 * 
	 * @param input
	 *            The input stream from which to retrieve data to place into this virtual file
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 * @throws IOException
	 */
	void putStream(InputStream input) throws ConnectionException, VirtualFileManagerException, IOException;

	/**
	 * Put the content of the specified input stream into this virtual file. This will create a new file if it does not
	 * already exist. This will replace the entire content of the file if it does exist
	 * 
	 * @param input
	 *            The input stream from which to retrieve data to place into this virtual file
	 * @param monitor
	 * 			  the monitor that handles the progress information
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 * @throws IOException
	 */
	void putStream(InputStream input, IFileProgressMonitor monitor) throws ConnectionException, 
			VirtualFileManagerException, IOException;

	/**
	 * Determine if this virtual file is readable given the current set of permissions
	 * 
	 * @return Returns true if this virtual file is readable
	 */
	boolean canRead();

	/**
	 * Determine if this virtual file is writable given the current set of permissions
	 * 
	 * @return Returns true if this virtual file is writable
	 */
	boolean canWrite();

	/**
	 * Remove this virtual file from the file manager
	 * 
	 * @return Returns true if the file was successfully deleted
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 */
	boolean delete() throws ConnectionException, VirtualFileManagerException;

	/**
	 * Determine if this virtual file exists in it's file system
	 * 
	 * @return Returns true if the file exists
	 * @throws ConnectionException
	 */
	boolean exists() throws ConnectionException;

	/**
	 * Rename this file
	 * 
	 * @param newName
	 *            The new name for this file
	 * @return Returns true if the rename was successful
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 */
	boolean rename(String newName) throws ConnectionException, VirtualFileManagerException;

	/**
	 * Returns a path relative to the parent virtual file manager
	 * 
	 * @return A relative path, or "/" if it is the root.
	 */
	String getRelativePath();
}
