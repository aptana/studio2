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
package com.aptana.ide.io.ftp;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.PluginUtils;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.ConnectionException;
import com.aptana.ide.core.io.IFileProgressMonitor;
import com.aptana.ide.core.io.IPasswordListener;
import com.aptana.ide.core.io.IVirtualFile;
import com.aptana.ide.core.io.IVirtualFileManager;
import com.aptana.ide.core.io.PasswordEvent;
import com.aptana.ide.core.io.ProtocolManager;
import com.aptana.ide.core.io.VirtualFileManagerException;
import com.aptana.ide.core.io.VirtualManagerBase;
import com.aptana.ide.core.io.sync.ISerializableSyncItem;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.io.file.FilePrefUtils;
import com.aptana.ide.core.ui.syncing.SyncingConsole;
import com.aptana.ide.syncing.SyncingPlugin;
import com.aptana.ide.syncing.ftp.FtpDialogFactory;
import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPFile;
import com.enterprisedt.net.ftp.FTPMessageListener;
import com.enterprisedt.net.ftp.FTPProgressMonitor;
import com.enterprisedt.net.ftp.FTPTransferType;

/**
 * @author Kevin Lindsey
 */
public class FtpVirtualFileManager extends VirtualManagerBase implements IFtpVirtualFileManager
{
	private static final int DEFAULT_FTP_PORT = 21;

	private static Image fFTPIcon;
	private static Image fFTPDisabledIcon;

	private String _server;
	private String _user;
	private String _password;
	private String _basePath;

	private int _port;
	private boolean _passiveMode;
	private long _lastEnsuredConnection;

	private Image _image;
	private Image _disabledImage;
	private FTPClient _ftpClient;
	private List<String> serverFeatures;

	private boolean _savePassword;
	private IPasswordListener _passwordListener;

	/**
	 * static constructor
	 */
	static
	{
		ImageDescriptor imageDescriptor = SyncingPlugin.getImageDescriptor("icons/ftp.png"); //$NON-NLS-1$
		ImageDescriptor disabledImageDescriptor = SyncingPlugin.getImageDescriptor("icons/ftp_disabled.png"); //$NON-NLS-1$

		if (imageDescriptor != null)
		{
			fFTPIcon = imageDescriptor.createImage();
			fFTPDisabledIcon = disabledImageDescriptor.createImage();
		}
	}

	/**
	 * FtpFileManager
	 * 
	 * @param protocolManager
	 */
	FtpVirtualFileManager(ProtocolManager protocolManager)
	{
		super(protocolManager);

		this._port = DEFAULT_FTP_PORT;
		this._passiveMode = true;
		this._savePassword = true;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getBaseFile()
	 */
	public IVirtualFile getBaseFile()
	{
		return this.createVirtualDirectory(this._basePath);
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getBasePath()
	 */
	public String getBasePath()
	{
		return this._basePath;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#setBasePath(java.lang.String)
	 */
	public void setBasePath(String path)
	{
		if (path == null || path.equals(StringUtils.EMPTY))
		{
			throw new IllegalArgumentException(Messages.FtpVirtualFileManager_PathCannotBeEmpty);
		}

		this._basePath = path;
	}

	/**
	 * isConnected
	 * 
	 * @return Returns true if the FTP client is current connected to the server
	 */
	public boolean isConnected()
	{
		return (this._ftpClient != null && this._ftpClient.connected());
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getFiles(com.aptana.ide.core.io.IVirtualFile, boolean, boolean)
	 */
	public IVirtualFile[] getFiles(IVirtualFile file, boolean recurse, boolean includeCloakedFiles)
			throws ConnectionException, IOException
	{
		List<IVirtualFile> list = new ArrayList<IVirtualFile>();

		if (file.canRead())
		{
			this.getFiles(file.getAbsolutePath(), recurse, list, includeCloakedFiles);
		}

		return list.toArray(new IVirtualFile[list.size()]);
	}

	/**
	 * Determines if the file path is valid. For example, '.', '..', and names containing '->' are not considered valid
	 * 
	 * @param name
	 * @return boolean
	 */
	private boolean validName(String name)
	{
		return name.indexOf("->") == -1 && name.equals(".") == false && name.equals("..") == false; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * getFiles
	 * 
	 * @param path
	 * @param list
	 */
	private synchronized void getFiles(String path, boolean recurse, List<IVirtualFile> list, boolean includeCloakedFiles)
			throws ConnectionException, IOException
	{
		ensureConnection();

		if (this._ftpClient != null)
		{
			// make sure we've already calculated the server time offset otherwise we'll get
			// server-busy exceptions
			this.getTimeOffset();

			// get starting index in case we need to recurse
			int startingIndex = list.size();

			// fire event
			if (this.fireGetFilesEvent(path))
			{
				FTPFile[] files;

				try
				{
					this._ftpClient.chdir(path);
					files = this._ftpClient.dirDetails("-a");
				}
				catch (ParseException e)
				{
					Locale savedLocale = Locale.getDefault();

					if (savedLocale != Locale.US)
					{
						this._ftpClient.setParserLocale(Locale.US);

						try
						{
							this._ftpClient.chdir(path);
							files = this._ftpClient.dirDetails("-a");
						}
						catch (ParseException e2)
						{
							throw new VirtualFileManagerException(
									Messages.FtpVirtualFileManager_Unsupported_Date_Time_Format, e2);
						}
						catch (FTPException e2)
						{
							throw new VirtualFileManagerException(Messages.FtpVirtualFileManager_UnableToGetFiles, e2);
						}
						finally
						{
							// reset locale to keep it in sync with system settings
							this._ftpClient.setParserLocale(savedLocale);
						}
					}
					else
					{
						throw new VirtualFileManagerException(
								Messages.FtpVirtualFileManager_Unsupported_Date_Time_Format, e);
					}
				}
				catch (FTPException e)
				{
					throw new VirtualFileManagerException(Messages.FtpVirtualFileManager_UnableToGetFiles, e);
				}

				FTPFile file;
				for (int i = 0; i < files.length; i++)
				{
					file = files[i];

					if (file.isLink() == false)
					{
						String filePath = path + this.getFileSeparator() + file.getName();

						// correct root directory case
						if (filePath.startsWith("//")) //$NON-NLS-1$
						{
							filePath = filePath.substring(1);
						}

						// do not add not links, ".", or ".."
						if (validName(file.getName()))
						{
							// calculate file mod time
							long date = file.lastModified().getTime();
							if (file.lastModified().getSeconds() == 0) {
								if (isServerSupportsFeature("MDTM")) {
									try {
										Date lastModified = this._ftpClient.modtime(filePath);
										if (lastModified != null) {
											date = lastModified.getTime();
										}
									} catch (FTPException e) {
									}
								}
							}

							// remove server time difference
							if (date > 0)
							{
								try
								{
									date -= this.getTimeOffset();
								}
								catch (ConnectionException e1)
								{
								}
							}

							// calculate permissions
							long permissions = this.permissionStringToLong(file.getPermissions());
							String owner = file.getOwner();
							String group = file.getGroup();

							// create new virtual file
							FtpVirtualFile vFile = new FtpVirtualFile(this, filePath, permissions, date, file.isDir(),
									file.size());
							vFile.setTimeStamp(file.lastModified().toString());
							vFile.internalSetOwner(owner);
							vFile.internalSetGroup(group);

							if (includeCloakedFiles || vFile.isCloaked() == false)
							{
								// add virtual file to result list
								list.add(vFile);
							}
						}
					}
				}

				// get ending offset
				int endingIndex = list.size();

				// post-process all files from gathered from the last directory listing
				if (recurse)
				{
					IVirtualFile vFile;
					boolean addingFile;
					for (int i = startingIndex; i < endingIndex; i++)
					{
						vFile = list.get(i);
						addingFile = false;

						if (includeCloakedFiles || !vFile.isCloaked())
						{
							addingFile = true;
						}

						// recurse into directories
						if (vFile.isDirectory() && vFile.canRead() && addingFile)
						{
							this.getFiles(vFile.getAbsolutePath(), recurse, list, includeCloakedFiles);
						}
					}
				}
			}
		}
	}

	/**
	 * ensures we have a valid FTP connection
	 */
	private void ensureConnection() throws ConnectionException
	{
		if (isConnected() == false)
		{
			this.connect();
		}
		else
		{
			long elapsedMillis = System.currentTimeMillis() - this._lastEnsuredConnection;
			long timeoutMillis = (long) this._ftpClient.getTimeout();

			if (elapsedMillis > timeoutMillis)
			{
				this.disconnect();
				this.connect();
			}
		}

		this._lastEnsuredConnection = System.currentTimeMillis();
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#hasFiles(com.aptana.ide.core.io.IVirtualFile)
	 */
	public boolean hasFiles(IVirtualFile file)
	{
		return file.isDirectory();
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getFileSeparator()
	 */
	public String getFileSeparator()
	{
		return "/"; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getFileTimeString(com.aptana.ide.core.io.IVirtualFile)
	 */
	public synchronized String getFileTimeString(IVirtualFile file)
	{
		FTPFile f;
		String result = StringUtils.EMPTY;

		try
		{
			f = this._ftpClient.fileDetails(file.getAbsolutePath());
			result = f.lastModified().toString();
		}
		catch (IOException e)
		{
			IdeLog.logError(SyncingPlugin.getDefault(), StringUtils.format(
					Messages.FtpVirtualFileManager_Cannot_Get_Time_String, file.getAbsolutePath()));
		}
		catch (FTPException e)
		{
			IdeLog.logError(SyncingPlugin.getDefault(), StringUtils.format(
					Messages.FtpVirtualFileManager_Cannot_Get_Time_String, file.getAbsolutePath()));
		}
		catch (ParseException e)
		{
			IdeLog.logError(SyncingPlugin.getDefault(), StringUtils.format(
					Messages.FtpVirtualFileManager_Cannot_Get_Time_String, file.getAbsolutePath()));
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getGroup(com.aptana.ide.core.io.IVirtualFile)
	 */
	public String getGroup(IVirtualFile file)
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#setGroup(com.aptana.ide.core.io.IVirtualFile, java.lang.String)
	 */
	public void setGroup(IVirtualFile file, String groupName)
	{
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getHashString()
	 */
	public String getHashString()
	{
		StringBuffer result = new StringBuffer();

		result.append(this.getNickName() + ISerializableSyncItem.DELIMITER);
		result.append(this.getServer() + ISerializableSyncItem.DELIMITER);
		result.append(this.getBasePath() + ISerializableSyncItem.DELIMITER);
		result.append(this.getUser() + ISerializableSyncItem.DELIMITER);
		if (this.getSavePassword())
		{
			result.append(this.getPassword() + ISerializableSyncItem.DELIMITER);
		}
		else
		{
			result.append(StringUtils.EMPTY + ISerializableSyncItem.DELIMITER);
		}
		result.append(this.getPassiveMode() + ISerializableSyncItem.DELIMITER);
		result.append(this.getId() + ISerializableSyncItem.DELIMITER);
		result.append(this.isAutoCalculateServerTimeOffset() + ISerializableSyncItem.DELIMITER);
		try
		{
			result.append(this.getTimeOffset() + ISerializableSyncItem.DELIMITER);
		}
		catch (ConnectionException e)
		{
			result.append(0 + ISerializableSyncItem.DELIMITER);
		}
		result.append(this.getPort() + ISerializableSyncItem.DELIMITER);
		result.append(this.serializeCloakedFiles(getCloakedFiles()) + ISerializableSyncItem.DELIMITER);
		result.append(StringUtils.join(ISerializableSyncItem.FILE_DELIMITER, getCloakedFileExpressions())
				+ ISerializableSyncItem.DELIMITER);
		result.append(this.getSavePassword() + ISerializableSyncItem.DELIMITER);

		return result.toString();
	}

	/**
	 * @see com.aptana.ide.core.io.sync.ISerializableSyncItem#fromSerializableString(java.lang.String)
	 */
	public void fromSerializableString(String s)
	{
		String[] items = s.split(ISerializableSyncItem.DELIMITER);

		if (items.length >= 7)
		{
			setNickName(items[0]);
			setServer(items[1]);
			if(items[2] == null || "".equals(items[2])) //$NON-NLS-1$
			{
				setBasePath("/");				 //$NON-NLS-1$
			}
			else
			{
				setBasePath(items[2]);
			}
			setUser(items[3]);
			setPassword(items[4]);
			setPassiveMode(items[5].equals(Boolean.TRUE.toString()));
			setId(Long.parseLong(items[6]));
		}
		if (items.length >= 9)
		{
			setAutoCalculateServerTimeOffset(Boolean.valueOf(items[7]).booleanValue());
			setTimeOffset(Long.parseLong(items[8]));
		}
		if (items.length >= 10)
		{
			setPort(Integer.parseInt(items[9]));
		}
		if (items.length >= 11)
		{
			IVirtualFile[] files = deserializeCloakedFiles(items[10]);
			for (int i = 0; i < files.length; i++)
			{
				IVirtualFile file = files[i];
				addCloakedFile(file);
			}
		}
		if (items.length >= 12)
		{
			String[] files = items[11].split(ISerializableSyncItem.FILE_DELIMITER);
			for (int i = 0; i < files.length; i++)
			{
				String file = files[i];
				addCloakExpression(file);
			}
		}
		if (items.length == 13)
		{
			setSavePassword(Boolean.valueOf(items[12]).booleanValue());
		}
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getImage()
	 */
	public Image getImage()
	{
		if (this._image == null)
		{
			this._image = fFTPIcon;
		}

		return this._image;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#setImage(org.eclipse.swt.graphics.Image)
	 */
	public void setImage(Image image)
	{
		this._image = image;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getDisabledImage()
	 */
	public Image getDisabledImage()
	{
		if (_disabledImage == null)
		{
			return fFTPDisabledIcon;
		}
		else
		{
			return _disabledImage;
		}
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#setDisabledImage(org.eclipse.swt.graphics.Image)
	 */
	public void setDisabledImage(Image image)
	{
		_disabledImage = image;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getOwner(com.aptana.ide.core.io.IVirtualFile)
	 */
	public String getOwner(IVirtualFile file)
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#setOwner(com.aptana.ide.core.io.IVirtualFile, java.lang.String)
	 */
	public void setOwner(IVirtualFile file, String ownerName)
	{
	}

	/**
	 * @return Returns the usePassiveMode.
	 */
	public boolean getPassiveMode()
	{
		return _passiveMode;
	}

	/**
	 * @param passiveMode
	 *            The usePassiveMode to set.
	 */
	public void setPassiveMode(boolean passiveMode)
	{
		_passiveMode = passiveMode;
	}

	/**
	 * get password
	 * 
	 * @return The password used to connect to this file manager's server
	 */
	public String getPassword()
	{
		return this._password;
	}

	/**
	 * getPasswordListener
	 * 
	 * @return IPasswordListener
	 */
	public IPasswordListener getPasswordListener()
	{
		return this._passwordListener;
	}

	/**
	 * setPasswordListener
	 * 
	 * @param listener
	 */
	public void setPasswordListener(IPasswordListener listener)
	{
		this._passwordListener = listener;
	}

	/**
	 * set password
	 * 
	 * @param password
	 */
	public void setPassword(String password)
	{
		this._password = password;
	}

	/**
	 * get port
	 * 
	 * @return Returns the port.
	 */
	public int getPort()
	{
		return this._port;
	}

	/**
	 * set port
	 * 
	 * @param port
	 *            The port to set.
	 */
	public void setPort(int port)
	{
		this._port = port;
	}

	/**
	 * getSavePassword
	 * 
	 * @return boolean
	 */
	public boolean getSavePassword()
	{
		return this._savePassword;
	}

	/**
	 * setSavePassword
	 * 
	 * @param value
	 */
	public void setSavePassword(boolean value)
	{
		this._savePassword = value;
	}

	/**
	 * get server
	 * 
	 * @return The server managed by this file manager
	 */
	public String getServer()
	{
		return this._server;
	}

	/**
	 * set server
	 * 
	 * @param server
	 */
	public void setServer(String server)
	{
		this._server = server;
	}

	/**
	 * @see com.aptana.ide.core.io.sync.ISerializableSyncItem#getType()
	 */
	public String getType()
	{
		return this.getClass().getName();
	}

	/**
	 * get user
	 * 
	 * @return The user name used to connect to this file manager's server
	 */
	public String getUser()
	{
		return this._user;
	}

	/**
	 * set user
	 * 
	 * @param user
	 */
	public void setUser(String user)
	{
		this._user = user;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#connect()
	 */
	public synchronized void connect() throws ConnectionException
	{
		if (this.isConnected() == false)
		{
			this._ftpClient = new FTPClient();

			try
			{
				this._ftpClient.setMessageListener(new FTPMessageListener()
				{
					/**
					 * logCommand
					 * 
					 * @param cmd
					 */
					public void logCommand(String cmd)
					{
						if (cmd != null)
						{
							String message = cmd.substring(5);

							if (message != null && message.startsWith("PASS")) //$NON-NLS-1$
							{
								message = "PASS *****"; //$NON-NLS-1$
							}

							SyncingConsole.println("ftp> " + message + StringUtils.LINE_DELIMITER); //$NON-NLS-1$
						}
					}

					/**
					 * logReply
					 * 
					 * @param reply
					 */
					public void logReply(String reply)
					{
						SyncingConsole.println(reply + StringUtils.LINE_DELIMITER);
					}
				});

				String password = this.getPassword();

				if (this._savePassword == false && password == null && this._passwordListener != null)
				{
					String title = StringUtils.format(Messages.FtpVirtualFileManager_FTPPasswordPrompt, this
							.getNickName());
					PasswordEvent event = new PasswordEvent(title);

					this._passwordListener.getPassword(event);

					password = event.password;

					if (event.remember)
					{
						this.setPassword(password);
					}
				}

				this._ftpClient.setControlEncoding(CoreUIUtils.getFileEncoding(null)); // sets the default encoding
				this._ftpClient.setRemoteHost(this.getServer());
				this._ftpClient.setRemotePort(this.getPort());
				this._ftpClient.connect();
				this._ftpClient.user(this.getUser());
				this._ftpClient.password(password);

				if (this.getPassiveMode())
				{
					this._ftpClient.setConnectMode(FTPConnectMode.PASV);
				}
				else
				{
					this._ftpClient.setConnectMode(FTPConnectMode.ACTIVE);
				}

				this._ftpClient.setType(FTPTransferType.BINARY);
				
				try {
					String[] features = this._ftpClient.features();
					if (features != null && features.length > 0) {
						serverFeatures = new ArrayList<String>();
						for (int i = 0; i < features.length; ++i) {
							serverFeatures.add(features[i].trim());
						}
					}
				} catch (Exception e) {
				}
			}
			catch (Exception e)
			{
				if (this.isConnected())
				{
					this.disconnect();
				}

				throw new ConnectionException(StringUtils.format(Messages.FtpVirtualFileManager_UnableToConnect,
						new String[] { this.getNickName(), e.getLocalizedMessage() }), e);
			}
		}
	}
	
	protected boolean isServerSupportsFeature(String feature) {
		if (serverFeatures != null) {
			return serverFeatures.contains(feature);
		}
		return false; // assume doesn't supports be default
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#containsFile(com.aptana.ide.core.io.IVirtualFile)
	 */
	public boolean containsFile(IVirtualFile file)
	{
		Path otherPath = new Path(file.getAbsolutePath());
		Path thisPath = new Path(this.getBaseFile().getAbsolutePath());

		return thisPath.isPrefixOf(otherPath);
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#createLocalDirectory(com.aptana.ide.core.io.IVirtualFile)
	 */
	public synchronized boolean createLocalDirectory(IVirtualFile directoryFile) throws ConnectionException,
			VirtualFileManagerException
	{
		String basePath = this.getBaseFile().getAbsolutePath();
		String directoryPath = directoryFile.getAbsolutePath();
		boolean result = false;

		if (basePath.equals(directoryPath))
		{
			// assume base directories exist

			// NOTE: directoryFile.exists() will fail in FTP since we have to get the parent file and then list its
			// contents
			// to determine if the directory exists.
			result = true;
		}
		else
		{
			ensureConnection();

			try
			{
				// make directory
				if (directoryFile.exists() == false)
				{
					try
					{
						this._ftpClient.mkdir(directoryFile.getAbsolutePath());
						directoryFile.setPermissions(FilePrefUtils.getDirectoryPermission());
					}
					catch (FTPException e)
					{
						IdeLog.logWarning(SyncingPlugin.getDefault(),
								"Unexpected FTP exception during ftp folder creation ", e); //$NON-NLS-1$
					}
					catch (IOException e)
					{
						IdeLog.logWarning(SyncingPlugin.getDefault(),
								"Unexpected IO exception during ftp folder creation ", e); //$NON-NLS-1$
					}
				}

				result = true;
			}
			catch (Exception e)
			{
				throw new VirtualFileManagerException(StringUtils.format(
						Messages.FtpVirtualFileManager_UnableToCreateDirectory, directoryFile.getAbsolutePath()), e);
			}
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#createVirtualDirectory(java.lang.String)
	 */
	public IVirtualFile createVirtualDirectory(String path)
	{
		return new FtpVirtualFile(this, path, true);
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#createVirtualFile(java.lang.String)
	 */
	public IVirtualFile createVirtualFile(String path)
	{
		return new FtpVirtualFile(this, path);
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#deleteFile(com.aptana.ide.core.io.IVirtualFile)
	 */
	public synchronized boolean deleteFile(IVirtualFile file) throws ConnectionException
	{
		boolean result = false;

		ensureConnection();

		if (file != null && this._ftpClient != null)
		{
			try
			{
				if (file.isFile())
				{
					this._ftpClient.delete(file.getAbsolutePath());
				}
				else
				{
					this._ftpClient.chdir("/");
					this._ftpClient.rmdir(file.getAbsolutePath());
				}

				result = true;
			}
			catch (FTPException e)
			{
				IdeLog.logError(SyncingPlugin.getDefault(), StringUtils.format(
						Messages.FtpVirtualFileManager_UnableToDelete, file.getAbsolutePath()), e);
			}
			catch (IOException e)
			{
				IdeLog.logError(SyncingPlugin.getDefault(), StringUtils.format(
						Messages.FtpVirtualFileManager_UnableToDelete, file.getAbsolutePath()), e);
			}
		}

		return result;
	}

	/**
	 * disconnect
	 */
	public synchronized void disconnect()
	{
		if (this._ftpClient != null)
		{
			try
			{
				this._ftpClient.quit();
			}
			catch (Exception e)
			{
				// e.printStackTrace();
			}
			finally
			{
				this._ftpClient = null;
				serverFeatures = null;
			}
		}
	}

	/**
	 * @throws VirtualFileManagerException
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getStream(com.aptana.ide.core.io.IVirtualFile)
	 */
	public synchronized InputStream getStream(IVirtualFile file) throws VirtualFileManagerException
	{
		this.fireFileTransferEvent(FileUtils.NEW_LINE
				+ StringUtils.format(Messages.FtpVirtualFileManager_Downloading, file.getAbsolutePath()));
		byte[] data = null;

		try
		{
			data = this._ftpClient.get(file.getAbsolutePath());
		}
		catch (Exception e)
		{
			throw new VirtualFileManagerException(e);
		}
		finally
		{
			if (data == null)
			{
				data = new byte[0];
			}
		}

		this.fireFileTransferEvent(StringUtils.SPACE + Messages.FtpVirtualFileManager_Success);

		return new ByteArrayInputStream(data);
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#moveFile(com.aptana.ide.core.io.IVirtualFile,
	 *      com.aptana.ide.core.io.IVirtualFile)
	 */
	public boolean moveFile(IVirtualFile source, IVirtualFile destination)
	{
		return false;
	}

	/**
	 * permissionStringToLong
	 * 
	 * @param dirEntry
	 * @return long
	 */
	private long permissionStringToLong(String dirEntry)
	{
		// assume read/write
		long permissions = 0666;

		if (dirEntry != null && dirEntry.length() > 0)
		{
			permissions = 0;

			// user
			permissions |= (dirEntry.charAt(1) == 'r') ? (1 << 8) : 0;
			permissions |= (dirEntry.charAt(2) == 'w') ? (1 << 7) : 0;
			permissions |= (dirEntry.charAt(3) == 'x') ? (1 << 6) : 0;

			// group
			permissions |= (dirEntry.charAt(4) == 'r') ? (1 << 5) : 0;
			permissions |= (dirEntry.charAt(5) == 'w') ? (1 << 4) : 0;
			permissions |= (dirEntry.charAt(6) == 'x') ? (1 << 3) : 0;

			// world
			permissions |= (dirEntry.charAt(7) == 'r') ? (1 << 2) : 0;
			permissions |= (dirEntry.charAt(8) == 'w') ? (1 << 1) : 0;
			permissions |= (dirEntry.charAt(9) == 'x') ? (1 << 0) : 0;
		}

		return permissions;
	}

	/**
	 * @throws ConnectionException
	 * @throws VirtualFileManagerException
	 * @see com.aptana.ide.core.io.IVirtualFileManager#putStream(java.io.InputStream, IVirtualFile, IFileProgressMonitor monitor)
	 */
	public synchronized void putStream(InputStream input, final IVirtualFile targetFile, final IFileProgressMonitor monitor) throws ConnectionException,
			VirtualFileManagerException
	{
		if (input == null)
		{
			throw new IllegalArgumentException(Messages.FtpVirtualFileManager_InputCannotBeNull);
		}

		ensureConnection();

		if (monitor == null)
		{
			this._ftpClient.setProgressMonitor(null);
		}
		else
		{
			this._ftpClient.setProgressMonitor(new FTPProgressMonitor()
			{
				public void bytesTransferred(long bytes)
				{
					monitor.bytesTransferred(bytes);
				}

			});
		}

		try
		{
			long permissions = targetFile.exists() ? 0 : FilePrefUtils.getFilePermission();
			this._ftpClient.put(input, targetFile.getAbsolutePath());
			if (permissions > 0)
			{
			    targetFile.setPermissions(permissions);
			}
		    if (monitor != null)
            {
                monitor.done();
            }
		}
		catch (Exception e)
		{
			throw new VirtualFileManagerException(e);
		}
	}

	/**
	 * @throws VirtualFileManagerException
	 * @throws VirtualFileManagerException
	 * @see com.aptana.ide.core.io.IVirtualFileManager#putToLocalFile(com.aptana.ide.core.io.IVirtualFile, java.io.File)
	 */
	public synchronized void putToLocalFile(IVirtualFile file, File tempFile) throws ConnectionException,
			VirtualFileManagerException
	{
		if (file.canRead())
		{
			ensureConnection();

			try
			{
				this._ftpClient.get(tempFile.getAbsolutePath(), file.getAbsolutePath());
			}
			catch (Exception e)
			{
				throw new VirtualFileManagerException(e);
			}
		}
		else
		{
			throw new VirtualFileManagerException(Messages.FtpVirtualFileManager_Cannot_Read_File);
		}
	}

	/**
	 * refresh
	 */
	public void refresh()
	{
		disconnect();
		try
		{
			connect();
		}
		catch (Exception e)
		{
			IdeLog.logError(SyncingPlugin.getDefault(), Messages.FtpVirtualFileManager_UnableToRefresh, e);
		}
	}

	/**
	 * @throws VirtualFileManagerException
	 * @see com.aptana.ide.core.io.IVirtualFileManager#renameFile(com.aptana.ide.core.io.IVirtualFile, java.lang.String)
	 */
	public synchronized boolean renameFile(IVirtualFile file, String newName) throws ConnectionException,
			VirtualFileManagerException
	{
		if (newName.indexOf(this.getFileSeparator()) > -1)
		{
			throw new IllegalArgumentException(Messages.FtpVirtualFileManager_NewNameNotAPath);
		}

		boolean result = false;

		ensureConnection();

		if (this._ftpClient != null && file instanceof FtpVirtualFile)
		{
			// save original name in case rename fails
			String beforeName = file.getName();

			try
			{
				String beforePath = file.getAbsolutePath();
				String afterPath;

				// update name
				((FtpVirtualFile) file).setName(newName);

				// get full path to new name
				afterPath = file.getAbsolutePath();

				// change name
				this._ftpClient.rename(beforePath, afterPath);

				// indicate success
				result = true;
			}
			catch (Exception e)
			{
				// reset name since rename failed
				((FtpVirtualFile) file).setName(beforeName);

				throw new VirtualFileManagerException(StringUtils.format(Messages.FtpVirtualFileManager_UnableToRename,
						new String[] { file.getAbsolutePath(), newName, e.getLocalizedMessage() }));
			}
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#resolveBasePath()
	 */
	public synchronized void resolveBasePath() throws ConnectionException, VirtualFileManagerException
	{
		String fileSeparator = this.getFileSeparator();
		String basePath = (this._basePath != null && this._basePath.length() > 0) ? this._basePath : StringUtils.EMPTY;

		ensureConnection();

		try
		{
			if (this._basePath == null || this._basePath.length() == 0)
			{
				// use the current directory
				this._basePath = this._ftpClient.pwd();
			}
			else
			{
				// default to user value
				this._basePath = basePath;

				// try to go to that directory
				try
				{
					this._ftpClient.chdir(basePath);
				}
				catch (Exception e)
				{
				}

				// use the directory where we ended up
				this._basePath = this._ftpClient.pwd();
			}
		}
		catch (Exception e)
		{
			throw new VirtualFileManagerException(e);
		}

		if (this._basePath.length() > 1 && this._basePath.endsWith(fileSeparator))
		{
			this._basePath = this._basePath.substring(0, this._basePath.length() - 1);
		}
	}

	/**
	 * @see com.aptana.ide.core.io.sync.ISerializableSyncItem#toSerializableString()
	 */
	public String toSerializableString()
	{
		return getHashString();
	}

	/**
	 * setModificationMillis
	 * 
	 * @param file
	 * @param modificationTime
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public synchronized void setModificationMillis(FtpVirtualFile file, long modificationTime) throws IOException,
			ConnectionException
	{
		this.ensureConnection();

		long offset = 0;

		try
		{
			offset = this.getTimeOffset();
		}
		catch (ConnectionException e)
		{
		}

		if (!isServerSupportsFeature("MFMT")) {
			return;
		}
		try
		{
			FTPFile[] files = this._ftpClient.dirDetails(file.getAbsolutePath());

			if (files.length > 0)
			{
				this._ftpClient.setModTime(file.getAbsolutePath(), new Date(modificationTime + offset));
			}
		}
		catch (Exception e)
		{
			if (file != null)
			{
				IdeLog.logError(SyncingPlugin.getDefault(), Messages.FtpVirtualFileManager_ERR_ErrorSettingModTimeOfFile + file.getAbsolutePath(), e);
			}
			else
			{
				IdeLog.logError(SyncingPlugin.getDefault(), Messages.FtpVirtualFileManager_ERR_ErrorSettingModTime, e);
			}
		}
	}

	/**
	 * exists
	 * 
	 * @param file
	 * @return boolean
	 * @throws ConnectionException
	 */
	public synchronized boolean exists(FtpVirtualFile file) throws ConnectionException
	{
		this.ensureConnection();

		boolean result = false;

		try
		{
			if (file.isFile())
			{
				result = this._ftpClient.exists(file.getAbsolutePath());
			}
			else
			{
				IVirtualFile[] files = this.getFiles(file.getParentFile(), false, true);

				for (int i = 0; i < files.length; i++)
				{
					IVirtualFile file2 = files[i];

					if (file2.equals(file))
					{
						result = true;
						break;
					}
				}
			}
		}
		catch (Exception e)
		{
		}

		return result;
	}

	/**
	 * @see VirtualManagerBase#getPreferenceStore()
	 */
	protected IPreferenceStore getPreferenceStore()
	{
		if (PluginUtils.isPluginLoaded(CoreUIPlugin.getDefault()))
		{
			return CoreUIPlugin.getDefault().getPreferenceStore();
		}
		else
		{
			return null;
		}
	}

	/**
	 * setPermissions
	 * 
	 * @param ftpVirtualFile
	 * @param permissions
	 */
	public synchronized boolean setPermissions(FtpVirtualFile ftpVirtualFile, long permissions)
	{
		boolean result = true;

		try
		{
			ensureConnection();

			String permissionString = Long.toOctalString(permissions);

			this._ftpClient.site("chmod " + permissionString + " " + ftpVirtualFile.getAbsolutePath()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		catch (Exception e)
		{
			result = false;
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#cloneManager()
	 */
	public IVirtualFileManager cloneManager()
	{
		FtpVirtualFileManager manager = new FtpVirtualFileManager(this.getProtocolManager());
		manager.setId(getId());
		manager.setPassword(this.getPassword());
		manager.setUser(this.getUser());
		manager.setAutoCalculateServerTimeOffset(this.isAutoCalculateServerTimeOffset());
		manager.setBasePath(this.getBasePath());
		manager.setCloakedFiles(this.getCloakedFiles());
		manager.setDisabledImage(this.getDisabledImage());
		manager.setHidden(this.isHidden());
		manager.setImage(this.getImage());
		manager.setPort(this.getPort());
		manager.setServer(this.getServer());
		return manager;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#cancel()
	 */
	public void cancel()
	{
		if (isConnected())
		{
			this._ftpClient.cancelTransfer();
		}
	}

	public FtpDialogFactory getDialogFactory()
	{
		return FtpDialogFactory.getInstance();
	}
	
	public boolean supportsPublicKeyAuthentication()
	{
		return false;
	}
	
	public void setPrivateKeyFile(String keyFile)
	{
		// do nothing		
	}
	
	public String getPrivateKeyFile()
	{
		return null;
	}
}
