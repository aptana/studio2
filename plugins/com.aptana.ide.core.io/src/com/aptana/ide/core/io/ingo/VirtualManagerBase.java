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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.aptana.ide.core.AptanaCorePlugin;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.preferences.IPreferenceConstants;

/**
 * @author Kevin Lindsey
 */
public abstract class VirtualManagerBase implements IVirtualFileManager //, IPropertyChangeListener
{
	ProtocolManager _protocolManager;
	private String _nickName = StringUtils.EMPTY;
	private String _id;
	private boolean _hidden;
	private boolean _transient;
	private IVirtualFileManagerEventHandler _eventHandler;
	private List<FileTransferListener> _fileTransferListeners;
	private long _timeOffset;
	private boolean _timeOffsetIsCached;
	private boolean _calculateOffset;
	private List<IVirtualFile> _cloakedFiles = new ArrayList<IVirtualFile>();
	private List<String> _cloakedFileExpressions = new ArrayList<String>();
	
	private int _initialPoolSize;
	private int _maxPoolSize;
	private VirtualFileManagerGroup _managerGroup;

	/**
	 * VirtualManagerBase
	 * 
	 * @param protocolManager
	 */
	public VirtualManagerBase(ProtocolManager protocolManager)
	{
		this._protocolManager = protocolManager;
		this._id = String.valueOf(Long.MAX_VALUE * Math.random());
		this._hidden = false;
		this._transient = false;
		this._timeOffset = 0;
		this._timeOffsetIsCached = false;
		this._initialPoolSize = 4;
		this._maxPoolSize = 8;

//		try
//		{
//			IPreferenceStore store = getPreferenceStore();
//			if (store != null)
//			{
//				store.addPropertyChangeListener(this);
//			}
//			updateCloakExpressions();
//		}
//		catch (Exception ex)
//		{
//			IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.VirtualManagerBase_UnableToAddPropertyListener);
//		}
	}

	/**
	 * getEventHandler
	 * 
	 * @return ISyncEventHandler
	 */
	public IVirtualFileManagerEventHandler getEventHandler()
	{
		return this._eventHandler;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#isEditable()
	 */
	public boolean isEditable()
	{
		return true;
	}

	/**
	 * setEventHandler
	 * 
	 * @param eventHandler
	 */
	public void setEventHandler(IVirtualFileManagerEventHandler eventHandler)
	{
		this._eventHandler = eventHandler;
	}

	/**
	 * fireGetFilesEvent
	 * 
	 * @param path
	 * @return Returns true if the file listing should continue; otherwise, it is aborted.
	 */
	protected boolean fireGetFilesEvent(String path)
	{
		boolean result = true;

		if (this._eventHandler != null)
		{
			result = this._eventHandler.getFilesEvent(this, path);
		}

		return result;
	}

	/**
	 * @throws IOException
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getFiles(com.aptana.ide.core.io.IVirtualFile)
	 */
	public IVirtualFile[] getFiles(IVirtualFile file) throws ConnectionException, IOException
	{
		return this.getFiles(file, false, true);
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getNickName()
	 */
	public String getNickName()
	{
		return this._nickName;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#setNickName(java.lang.String)
	 */
	public void setNickName(String nickName)
	{
		this._nickName = nickName;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getProtocolManager()
	 */
	public ProtocolManager getProtocolManager()
	{
		return this._protocolManager;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o)
	{
		if (!(o instanceof IVirtualFileManager))
		{
			return -1;
		}
		IVirtualFileManager fm = (IVirtualFileManager) o;
		String label = getDescriptiveLabel();
		if (label == null || fm == null)
		{
			return -1;
		}
		return label.compareTo(fm.getDescriptiveLabel());
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getId()
	 */
	public String getId()
	{
		return this._id;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#setId(long)
	 */
	public void setId(String id)
	{
		this._id = id;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#isHidden()
	 */
	public boolean isHidden()
	{
		return this._hidden;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#setHidden(boolean)
	 */
	public void setHidden(boolean hidden)
	{
		this._hidden = hidden;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#isTransient()
	 */
	public boolean isTransient()
	{
		return this._transient;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#setTransient(boolean)
	 */
	public void setTransient(boolean transient1)
	{
		this._transient = transient1;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getDescriptiveLabel()
	 */
	public String getDescriptiveLabel()
	{
		String name = StringUtils.EMPTY;
		if (getBasePath() != null && !StringUtils.EMPTY.equals(getBasePath())
				&& !getBasePath().equals(getFileSeparator()))
		{
			name = StringUtils.format("{0} ({1})", new String[] { getNickName(), getBasePath() }); //$NON-NLS-1$
		}
		else
		{
			name = getNickName();
		}
		return name;
	}

	/**
	 * Add a listener for file transfer log events.
	 * 
	 * @param listener
	 */
	public void addFileTransferListener(FileTransferListener listener)
	{
		if (_fileTransferListeners == null)
		{
			_fileTransferListeners = new ArrayList<FileTransferListener>();
		}
		this._fileTransferListeners.add(listener);
	}

	/**
	 * Remove a listener for file transfer log events.
	 * 
	 * @param listener
	 */
	public void removeFileTransferListener(FileTransferListener listener)
	{
		if (_fileTransferListeners != null)
		{
			this._fileTransferListeners.remove(listener);
		}
	}

	/**
	 * An event that contains transfer logging info as files are transfered.
	 * 
	 * @param logInfo
	 */
	protected void fireFileTransferEvent(String logInfo)
	{
		if (_fileTransferListeners != null)
		{
			for (int i = 0; i < _fileTransferListeners.size(); i++)
			{
				_fileTransferListeners.get(i).addText(logInfo);
			}
		}
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#putStream(java.io.InputStream, IVirtualFile)
	 */
	public void putStream(InputStream input, IVirtualFile targetFile, IFileProgressMonitor monitor) throws ConnectionException,
			VirtualFileManagerException, IOException
	{
		// TODO: Fixme
		//putStream(input, targetFile, null);
	}
	
	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#putStream(java.io.InputStream, IVirtualFile)
	 */
	public void putStream(InputStream input, IVirtualFile targetFile) throws ConnectionException,
			VirtualFileManagerException, IOException
	{
		//putStream(input, targetFile, null);
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#putFile(IVirtualFile, IVirtualFile)
	 */
	public void putFile(IVirtualFile sourceFile, IVirtualFile targetFile) throws ConnectionException,
			VirtualFileManagerException, IOException
	{
		//putFile(sourceFile, targetFile, null);
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#putFile(IVirtualFile, IVirtualFile, IFileProgressMonitor)
	 */
	public void putFile(IVirtualFile sourceFile, final IVirtualFile targetFile, final IFileProgressMonitor monitor) throws ConnectionException,
			VirtualFileManagerException, IOException
	{
		sourceFile.getStream(new IVirtualFile.Client()
		{

			public void streamGot(InputStream input) throws ConnectionException,
					VirtualFileManagerException, IOException
			{
				putStream(input, targetFile, monitor);
			}
			
		});
	}

	/**
	 * Sets the time offset
	 * 
	 * @param timeOffset
	 */
	public void setTimeOffset(long timeOffset)
	{
		this._timeOffset = timeOffset;
		this._timeOffsetIsCached = true;
	}

	/**
	 * Set if we auto-calculate the server time offset
	 * 
	 * @param calculateOffset
	 */
	public void setAutoCalculateServerTimeOffset(boolean calculateOffset)
	{
		this._calculateOffset = calculateOffset;
	}

	/**
	 * Do we auto-calculate the server time offset
	 * 
	 * @return boolean
	 */
	public boolean isAutoCalculateServerTimeOffset()
	{
		return this._calculateOffset;
	}

	/**
	 * Resets the time offset cache
	 */
	public void resetTimeOffsetCache()
	{
		this._timeOffsetIsCached = false;
	}

	/**
	 * getTimeOffset
	 * 
	 * @return long
	 * @throws ConnectionException
	 */
	public long getTimeOffset() throws ConnectionException
	{
		if (!this._calculateOffset)
		{
			return 0;
		}

		if (this._timeOffsetIsCached == false)
		{
			String tempFileBase = "timestamp"; //$NON-NLS-1$
			String tempFileExtension = ".tmp"; //$NON-NLS-1$
			String tempFileName = tempFileBase + tempFileExtension;

			try
			{
				// create temp file
				File tempFile = File.createTempFile(tempFileBase, tempFileExtension);
				FileInputStream input = new FileInputStream(tempFile);
				long localModificationDate = tempFile.lastModified();
				String fullPath;

				if (this.getBasePath() != null && this.getBasePath().equals(this.getFileSeparator()))
				{
					fullPath = this.getFileSeparator() + tempFileName;
				}
				else
				{
					fullPath = this.getBasePath() + this.getFileSeparator() + tempFileName;
				}

				IVirtualFile vFile = this.createVirtualFile(fullPath);

				this.putStream(input, vFile);

				IVirtualFile vParent = vFile.getParentFile();

				// temporarily act like we've cached the time offset to avoid an infinite recursion in the call to
				// getFiles
				this._timeOffsetIsCached = true;
				IVirtualFile[] files = this.getFiles(vParent, false, true);
				this._timeOffsetIsCached = false;

				if (files.length > 0)
				{
					IVirtualFile file;
					for (int i = 0; i < files.length; i++)
					{
						file = files[i];

						if (file.getName().equals(vFile.getName()))
						{
							if (file.getModificationMillis() > 0)
							{
								this._timeOffset = file.getModificationMillis() - localModificationDate;
							}
							else
							{
								this._timeOffset = 0;
							}

							this._timeOffsetIsCached = true;

							break;
						}
					}
				}

				this.deleteFile(vFile);
			}
			catch (IOException e)
			{
				IdeLog.logError(AptanaCorePlugin.getDefault(), Messages.VirtualManagerBase_GetTimeOffsetError, e);
			}
		}

		return this._timeOffset;
	}

	/**
	 * getCloakedFiles
	 * 
	 * @return IVirtualFile[]
	 */
	public IVirtualFile[] getCloakedFiles()
	{
		return _cloakedFiles.toArray(new IVirtualFile[0]);
	}

	/**
	 * getCloakedFileExpressions
	 * 
	 * @return String[]
	 */
	public String[] getCloakedFileExpressions()
	{
		return _cloakedFileExpressions.toArray(new String[0]);
	}

	/**
	 * setCloakedFiles
	 * 
	 * @param files
	 */
	public void setCloakedFiles(IVirtualFile[] files)
	{
		_cloakedFiles.clear();
		_cloakedFiles.addAll(Arrays.asList(files));
	}

	/**
	 * Sets a file as cloaked
	 * 
	 * @param file
	 */
	public void addCloakedFile(IVirtualFile file)
	{
		_cloakedFiles.add(file);
	}

	/**
	 * Remove a file as cloaked
	 * 
	 * @param file
	 */
	public void removeCloakedFile(IVirtualFile file)
	{
		_cloakedFiles.remove(file);
	}

	/**
	 * Sets an expression to cloak all files that match the expression
	 * 
	 * @param fileExpression
	 */
	public void addCloakExpression(String fileExpression)
	{
		_cloakedFileExpressions.add(fileExpression);
	}

	/**
	 * Removes an expression to cloak all files that match the expression
	 * 
	 * @param fileExpression
	 */
	public void removeCloakExpression(String fileExpression)
	{
		_cloakedFileExpressions.remove(fileExpression);
	}

	/**
	 * Removes all cloaked expressions
	 */
	public void removeAllCloakExpressions()
	{
		_cloakedFileExpressions.clear();
	}

	/**
	 * is a file cloaked?
	 * 
	 * @param file
	 * @return boolean
	 */
	public boolean isFileCloaked(IVirtualFile file)
	{
		boolean result = false;
		
		if (this._cloakedFiles.contains(file))
		{
			result = true;
		}
		else
		{
			String separator = this.getFileSeparator();
			
			if ("\\".equals(separator)) //$NON-NLS-1$
			{
				separator = "\\\\"; //$NON-NLS-1$
			}
			
			synchronized (this._cloakedFileExpressions)
			{
				for (String pattern : this._cloakedFileExpressions)
				{
					if (pattern.contains(separator))
					{
						if (file.getRelativePath().matches(pattern))
						{
							result = true;
							break;
						}
					}
					else
					{
						if (file.getName().matches(pattern))
						{
							result = true;
							break;
						}
					}
				}
			}
		}
		
		return result;
	}

	/**
	 * Serialize a list of files
	 * 
	 * @param items
	 * @return IVirtualFile[]
	 */
	protected IVirtualFile[] deserializeCloakedFiles(String items)
	{
		String[] files = items.split(ISerializableSyncItem.FILE_DELIMITER);
		List<IVirtualFile> al = new ArrayList<IVirtualFile>();
		for (int i = 0; i < files.length; i++)
		{
			if (files[i].length() > 0) {
				al.add(createVirtualFile(files[i]));
			}
		}
		return al.toArray(new IVirtualFile[al.size()]);
	}

	/**
	 * De-serialize a list of files
	 * 
	 * @param files
	 * @return String
	 */
	protected String serializeCloakedFiles(IVirtualFile[] files)
	{
		List<String> newFiles = new ArrayList<String>();
		for (int i = 0; i < files.length; i++)
		{
			newFiles.add(files[i].getAbsolutePath());
		}

		return StringUtils.join(ISerializableSyncItem.FILE_DELIMITER, newFiles.toArray(new String[0]));
	}

//	/**
//	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
//	 */
//	public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event)
//	{
//		String property = event.getProperty();
//
//		if (IPreferenceConstants.PREF_GLOBAL_SYNC_CLOAKING_EXTENSIONS.equals(property))
//		{
//			updateCloakExpressions();
//		}
//	}

	/**
	 * Updating the cloaking expressions
	 */
	private void updateCloakExpressions()
	{
		// clear our current list of cloaking expressions
		this.removeAllCloakExpressions();
		
		// grab the preference store
//		IPreferenceStore store = getPreferenceStore();
//		
//		if (store != null)
//		{
//			// grab the list of cloaking expressions
//			String expressions = store.getString(IPreferenceConstants.PREF_GLOBAL_SYNC_CLOAKING_EXTENSIONS);
//			
//			if (expressions != null && expressions.length() > 0)
//			{
//				String[] array = expressions.split(";"); //$NON-NLS-1$
//				
//				for (String expression : array)
//				{
//					String regexString = this.cloakExpressionToRegex(expression);
//					
//					this.addCloakExpression(regexString);
//				}
//			}
//		}
	}
	
	/**
	 * cloakExpressionToRegex
	 *
	 * @param expression
	 * @return
	 */
	private String cloakExpressionToRegex(String expression)
	{
		String result = null;
		
		if (expression != null)
		{
			if (expression.startsWith("/") && expression.endsWith("/"))  //$NON-NLS-1$//$NON-NLS-2$
			{
				result = expression.substring(1, expression.length() - 1);
			}
			else
			{
				String separator = this.getFileSeparator();
				
				if (expression.contains("/")) //$NON-NLS-1$
				{
					if (separator.equals("/") == false) //$NON-NLS-1$
					{
						if (separator.equals("\\")) //$NON-NLS-1$
						{
							expression = expression.replaceAll("/", "\\\\\\\\"); //$NON-NLS-1$ //$NON-NLS-2$
						}
						else
						{
							expression = expression.replaceAll("/", separator); //$NON-NLS-1$
						}
					}
				}
				else if (expression.contains("\\")) //$NON-NLS-1$
				{
					if (separator.equals("\\") == false) //$NON-NLS-1$
					{
						expression = expression.replaceAll("\\\\", separator); //$NON-NLS-1$
					}
					else
					{
						expression = expression.replaceAll("\\\\", "\\\\\\\\"); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				
				// escape all '.' characters which aren't followed by '*'
				result = expression.replaceAll("\\.(?=[^\\*])", "\\\\."); //$NON-NLS-1$//$NON-NLS-2$
				
				// convert all '*' characters that are not preceded by '.' to ".*"
				result = "(?i)" + result.replaceAll("(?<!\\.)\\*", ".*"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
		
		return result;
	}

	/**
	 * Returns the local preference store
	 * 
	 * @return IPreferenceStore
	 */
//	protected abstract IPreferenceStore getPreferenceStore();

	/**
	 * Returns the expression used to cloak items of this type
	 * 
	 * @param element
	 * @return Returns the string used to cloak items of this type
	 */
	public static String getFileTypeCloakExpression(IVirtualFile element)
	{
		if (element.isDirectory())
		{
			return element.getName();
		}
		else
		{
			String extension = element.getExtension();
			if (extension != null && !StringUtils.EMPTY.equals(extension))
			{
				return "*" + element.getExtension(); //$NON-NLS-1$
			}
			return element.getName();
		}
	}

	/**
	 * Is the current file manager valid (meaning that the base path is valid)
	 * 
	 * @return boolean
	 */
	public boolean isValid()
	{
		return true;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#cloneManager()
	 */
	public IVirtualFileManager cloneManager()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#cancel()
	 */
	public void cancel()
	{
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#setPoolSizes(int, int)
	 */
	public void setPoolSizes(int initialSize, int maxSize)
	{
		this._initialPoolSize = initialSize;
		this._maxPoolSize = maxSize;
	}

	public VirtualFileManagerGroup getManagerGroup()
	{
	    return this._managerGroup;
	}

	public void setManagerGroup(VirtualFileManagerGroup group)
	{
	    this._managerGroup = group;
	}

	/**
	 * Gets the initial number of connection pools to use.
	 * 
	 * @return the initial number of connection pools
	 */
	protected int getInitialPoolSize()
	{
		return this._initialPoolSize;
	}

	/**
	 * Gets the maximum number of connection pools to use.
	 * 
	 * @return the maximum number of connection pools
	 */
	protected int getMaxPoolSize()
	{
		return this._maxPoolSize;
	}

}