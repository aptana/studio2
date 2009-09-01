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

import com.aptana.ide.core.AptanaCorePlugin;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.IVirtualFileManager;

/**
 * @author Kevin Lindsey
 */
public class VirtualFileManagerSyncPair implements ISerializableSyncItem
{
	/**
	 * Upload the items
	 */
	public static final int Upload = 0;

	/**
	 * Download the items
	 */
	public static final int Download = 1;

	/**
	 * Upload and Download
	 */
	public static final int Both = 2;

	/*
	 * Fields
	 */
	private String _nickName = StringUtils.EMPTY;
	private IVirtualFileManager _sourceFileManager = null;
	private IVirtualFileManager _destinationFileManager = null;
	private long sourceId = -1;
	private long destId = -1;
	private int _syncOption = 0;
	private boolean _useCRC = false;
	private boolean _deleteRemoteFiles = false;

	/*
	 * Constructors
	 */

	/**
	 * SyncItem
	 */
	public VirtualFileManagerSyncPair()
	{
	}

	/*
	 * Methods
	 */

	/**
	 * getClientFile
	 * 
	 * @return IVirtualFileManager
	 */
	public IVirtualFileManager getSourceFileManager()
	{
		return this._sourceFileManager;
	}

	/**
	 * setClientFileManager
	 * 
	 * @param sourceFileManager
	 */
	public void setSourceFileManager(IVirtualFileManager sourceFileManager)
	{
		this._sourceFileManager = sourceFileManager;
	}

	/**
	 * getServerFileManager
	 * 
	 * @return IVirtualFileManager
	 */
	public IVirtualFileManager getDestinationFileManager()
	{
		return this._destinationFileManager;
	}

	/**
	 * setServerFileManager
	 * 
	 * @param destinationFileManager
	 */
	public void setDestinationFileManager(IVirtualFileManager destinationFileManager)
	{
		this._destinationFileManager = destinationFileManager;
	}

	/**
	 * getNickName
	 * 
	 * @return String
	 */
	public String getNickName()
	{
		return this._nickName;
	}

	/**
	 * setNickName
	 * 
	 * @param nickName
	 */
	public void setNickName(String nickName)
	{
		this._nickName = nickName;
	}

	/**
	 * getSyncOption
	 * 
	 * @return int
	 */
	public int getSyncState()
	{
		return this._syncOption;
	}

	/**
	 * setSyncOption
	 * 
	 * @param syncOption
	 */
	public void setSyncState(int syncOption)
	{
		this._syncOption = syncOption;
	}

	/**
	 * @return Returns the _deleteRemoteFiles.
	 */
	public boolean isDeleteRemoteFiles()
	{
		return _deleteRemoteFiles;
	}

	/**
	 * @param remoteFiles
	 *            The deleteRemoteFiles to set.
	 */
	public void setDeleteRemoteFiles(boolean remoteFiles)
	{
		_deleteRemoteFiles = remoteFiles;
	}

	/**
	 * @return Returns the useCRC.
	 */
	public boolean isUseCRC()
	{
		return _useCRC;
	}

	/**
	 * @return Returns true if this connection is valid
	 */
	public boolean isValid()
	{
		return getDestinationFileManager() != null && getDestinationFileManager().getBasePath() != null
				&& getSourceFileManager() != null && getSourceFileManager().getBasePath() != null
				&& getSourceFileManager().isValid() && getDestinationFileManager().isValid();
	}

	/**
	 * @param usecrc
	 *            The useCRC to set.
	 */
	public void setUseCRC(boolean usecrc)
	{
		_useCRC = usecrc;
	}

	/**
	 * @see com.aptana.ide.core.io.sync.ISerializableSyncItem#toSerializableString()
	 */
	public String toSerializableString()
	{
		try
		{
			boolean shouldSerialize = true;
			if (_sourceFileManager != null && StringUtils.EMPTY.equals(_sourceFileManager.toSerializableString()))
			{
				shouldSerialize = false;
			}
			else if (_destinationFileManager != null
					&& StringUtils.EMPTY.equals(_destinationFileManager.toSerializableString()))
			{
				shouldSerialize = false;
			}
			String result = StringUtils.EMPTY;
			if (shouldSerialize)
			{
				result += _nickName + ISerializableSyncItem.DELIMITER;

				long sourceFileId = _sourceFileManager == null ? this.sourceId : _sourceFileManager.getId();
				long destinationFileId = _destinationFileManager == null ? this.destId : _destinationFileManager
						.getId();

				result += sourceFileId + ISerializableSyncItem.DELIMITER;
				result += destinationFileId + ISerializableSyncItem.DELIMITER;
				result += _syncOption + ISerializableSyncItem.DELIMITER;
				result += _useCRC + ISerializableSyncItem.DELIMITER;
				result += _deleteRemoteFiles + ISerializableSyncItem.DELIMITER;
			}
			return result;
		}
		catch (Exception e)
		{
			IdeLog.logError(AptanaCorePlugin.getDefault(),
					Messages.VirtualFileManagerSyncPair_UnableToSerializeSyncPair, e);
		}

		return null;
	}

	/**
	 * @see com.aptana.ide.core.io.sync.ISerializableSyncItem#fromSerializableString(java.lang.String)
	 */
	public void fromSerializableString(String s)
	{
		try
		{
			String[] args = s.split(ISerializableSyncItem.DELIMITER);

			if(args.length < 3)
			{
				/*IdeLog.logError(AptanaCorePlugin.getDefault(),
						Messages.VirtualFileManagerSyncPair_UnableToLoadSyncConfiguration);*/
				return;
			}
			
			setNickName(args[0]);
			String sourceId = args[1];
			String destinationId = args[2];

			this.sourceId = Long.parseLong(sourceId);
			this.destId = Long.parseLong(destinationId);

			_sourceFileManager = this.sourceId == -1 ? null : SyncManager.getSyncManager().getVirtualFileManagerById(
					this.sourceId);
			_destinationFileManager = this.destId == -1 ? null : SyncManager.getSyncManager()
					.getVirtualFileManagerById(this.destId);

			setSyncState(Integer.parseInt(args[3]));
			setUseCRC(args[4] == "true"); //$NON-NLS-1$
			setDeleteRemoteFiles(args[5] == "true"); //$NON-NLS-1$
		}
		catch (Exception e)
		{
			IdeLog.logError(AptanaCorePlugin.getDefault(),
					Messages.VirtualFileManagerSyncPair_UnableToLoadSyncConfiguration, e);
		}
	}

	/**
	 * @see com.aptana.ide.core.io.sync.ISerializableSyncItem#getType()
	 */
	public String getType()
	{
		return "com.aptana.ide.core.io.sync.VirtualFileManagerSyncItem"; //$NON-NLS-1$
	}
}
