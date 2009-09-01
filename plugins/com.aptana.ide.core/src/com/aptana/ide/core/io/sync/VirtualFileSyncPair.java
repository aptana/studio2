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
package com.aptana.ide.core.io.sync;

import java.io.IOException;
import java.io.InputStream;

import com.aptana.ide.core.io.ConnectionException;
import com.aptana.ide.core.io.IVirtualFile;
import com.aptana.ide.core.io.VirtualFileManagerException;

/**
 * @author Kevin Lindsey
 */
public class VirtualFileSyncPair
{
	public static final int Direction_None = 0;
	public static final int Direction_ClientToServer = 1;
	public static final int Direction_ServerToClient = 2;

	private String _relativePath;
	private IVirtualFile _sourceFile;
	private IVirtualFile _destinationFile;
	private int _syncState;
	private int _syncDirection = Direction_None;

	/**
	 * SyncItem
	 * 
	 * @param sourceFile
	 * @param destinationFile
	 * @param relativePath
	 * @param syncState
	 */
	public VirtualFileSyncPair(IVirtualFile sourceFile, IVirtualFile destinationFile, String relativePath, int syncState)
	{
		this._sourceFile = sourceFile;
		this._destinationFile = destinationFile;
		this._relativePath = relativePath;
		this._syncState = syncState;
	}

	/**
	 * getClientFile
	 * 
	 * @return IVirtualFile
	 */
	public IVirtualFile getSourceFile()
	{
		return this._sourceFile;
	}

	/**
	 * getClientInputStream
	 * 
	 * @return InputStream
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 * @throws IOException
	 */
	public InputStream getSourceInputStream() throws ConnectionException, VirtualFileManagerException, IOException
	{
		InputStream result = null;

		if (this._sourceFile != null && this._sourceFile.isFile())
		{
			result = this._sourceFile.getFileManager().getStream(this._sourceFile);
		}

		return result;
	}

	/**
	 * setClientFile
	 * 
	 * @param sourceFile
	 */
	public void setSourceFile(IVirtualFile sourceFile)
	{
		this._sourceFile = sourceFile;
	}

	/**
	 * getServerFile
	 * 
	 * @return IVirtualFile
	 */
	public IVirtualFile getDestinationFile()
	{
		return this._destinationFile;
	}

	/**
	 * getServerInputStream
	 * 
	 * @return InputStream
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 * @throws IOException
	 */
	public InputStream getDestinationInputStream() throws ConnectionException, VirtualFileManagerException, IOException
	{
		InputStream result = null;

		if (this._destinationFile != null && this._destinationFile.isFile())
		{
			result = this._destinationFile.getFileManager().getStream(this._destinationFile);
		}

		return result;
	}

	/**
	 * setServerFile
	 * 
	 * @param destinationFile
	 */
	public void setDestinationFile(IVirtualFile destinationFile)
	{
		this._destinationFile = destinationFile;
	}

	/**
	 * getRelativePath
	 * 
	 * @return String
	 */
	public String getRelativePath()
	{
		return this._relativePath;
	}

	/**
	 * getSyncState
	 * 
	 * @return int
	 */
	public int getSyncState()
	{
		return this._syncState;
	}

	/**
	 * setSyncState
	 * 
	 * @param syncState
	 */
	public void setSyncState(int syncState)
	{
		this._syncState = syncState;
	}

	/**
	 * getSyncDirection
	 * @return int
	 */
	public int getSyncDirection()
	{
		return _syncDirection;
	}
	
	/**
	 * 
	 * @param direction
	 */
	public void setSyncDirection(int direction)
	{
		this._syncDirection = direction;
	}
	
	/**
	 * Am I a folder?
	 * 
	 * @return boolean
	 */
	public boolean isDirectory()
	{
		if (getSyncState() == SyncState.IncompatibleFileTypes)
		{
			return false;
		}
		else
		{
			if (getSourceFile() != null && getSourceFile().isDirectory())
			{
				return true;
			}
			else
			{
				return (getDestinationFile() != null && getDestinationFile().isDirectory());
			}
		}
	}
}