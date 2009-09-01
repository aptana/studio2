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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Image;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.PluginUtils;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.ConnectionException;
import com.aptana.ide.core.io.FileTransferListener;
import com.aptana.ide.core.io.IFileProgressMonitor;
import com.aptana.ide.core.io.IVirtualFile;
import com.aptana.ide.core.io.IVirtualFileManager;
import com.aptana.ide.core.io.ProtocolManager;
import com.aptana.ide.core.io.VirtualFileManagerException;
import com.aptana.ide.core.io.VirtualManagerBase;
import com.aptana.ide.core.io.sync.ISerializableSyncItem;
import com.aptana.ide.core.preferences.IPreferenceConstants;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.ImageUtils;
import com.aptana.ide.core.ui.syncing.SyncingConsole;
import com.aptana.ide.io.file.FilePlugin;

/**
 * @author Robin Debreuil
 */
public class LocalFileManager extends VirtualManagerBase
{
	private String _basePath;
	private Image _image;
	private Image _disabledImage;

	/**
	 * LocalFileManager
	 * 
	 * @param protocolManager
	 */
	public LocalFileManager(ProtocolManager protocolManager)
	{
		super(protocolManager);

		this.addFileTransferListener(new FileTransferListener()
		{
			public void addText(String eventText)
			{
				SyncingConsole.println(eventText);
			}
		});
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#createVirtualFile(java.lang.String)
	 */
	public IVirtualFile createVirtualFile(String path)
	{
		return new LocalFile(this, new File(path));
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#createLocalDirectory(IVirtualFile)
	 */
	public boolean createLocalDirectory(IVirtualFile directoryFile)
	{
		File f = new File(directoryFile.getAbsolutePath());

		return f.mkdirs();
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#createVirtualDirectory(java.lang.String)
	 */
	public IVirtualFile createVirtualDirectory(String path)
	{
		// TODO: Add support for indicating that this non-existing virtual file is a directory
		// return new LocalFile(this, new File(path), true);

		return new LocalFile(this, new File(path));
	}

	/**
	 * getBaseFile
	 * 
	 * @return IVirtualFile
	 */
	public IVirtualFile getBaseFile()
	{
		String basePath = getBasePath();
		if (basePath == null)
		{
			throw new IllegalArgumentException(Messages.LocalFileManager_BasePathCannotBeNull);
		}

		return new LocalFile(this, new File(basePath));
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
		if (path == null || path.equals("")) //$NON-NLS-1$
		{
			throw new IllegalArgumentException(Messages.LocalFileManager_pathCannotBeEmptyOrNull);
		}

		this._basePath = path;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getFiles(com.aptana.ide.core.io.IVirtualFile)
	 */
	public IVirtualFile[] getFiles(IVirtualFile file)
	{
		return this.getFiles(file, false, true);
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getFiles(com.aptana.ide.core.io.IVirtualFile, boolean, boolean)
	 */
	public IVirtualFile[] getFiles(IVirtualFile file, boolean recurse, boolean includeCloakedFiles)
	{
		IVirtualFile[] result = null;
		if (file instanceof LocalFile)
		{
			ArrayList<IVirtualFile> list = new ArrayList<IVirtualFile>();
			File f = ((LocalFile) file).getFile();
			this.getFiles(f, recurse, list, includeCloakedFiles);
			result = list.toArray(new IVirtualFile[0]);
		}
		return result;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getFileTimeString(com.aptana.ide.core.io.IVirtualFile)
	 */
	public String getFileTimeString(IVirtualFile file)
	{
		return Long.toString(file.getModificationMillis());
	}

	/**
	 * getFiles
	 * 
	 * @param file
	 * @param recurse
	 * @param list
	 */
	private void getFiles(File file, boolean recurse, List<IVirtualFile> list, boolean includeCloakedFiles)
	{
		if (file == null)
		{
			return;
		}

		// fire event
		this.fireGetFilesEvent(file.getAbsolutePath());

		File[] children = null;

		if (CoreUIUtils.runningOnMac == false)
		{
			if (file.getName().equals(LocalProtocolManager.FileSystemRoots))
			{
				children = FileSystemView.getFileSystemView().getRoots();
			}
			else
			{
				children = FileSystemView.getFileSystemView().getFiles(file, false);
			}
		}
		else
		{
			File[] kids;

			if (file.getName().equals(LocalProtocolManager.FileSystemRoots))
			{
				kids = File.listRoots();
			}
			else
			{
				kids = file.listFiles();
			}

			children = (kids == null) ? new File[0] : kids;
		}

		if (children != null)
		{
			File child;
			IVirtualFile localFile;
			boolean addingFile;
			for (int i = 0; i < children.length; i++)
			{
				child = children[i];
				if (CoreUIPlugin.getDefault().getPreferenceStore().getBoolean(
						com.aptana.ide.core.ui.preferences.IPreferenceConstants.PREF_FILE_EXPLORER_SHOW_COMPRESSED)
						&& (child.getName().endsWith(".zip") || child.getName().endsWith(".jar") || child.getName() //$NON-NLS-1$ //$NON-NLS-2$
								.endsWith(".gz"))) //$NON-NLS-1$
				{
					localFile = new CompressedFile(this, child);
				}
				else
				{
					localFile = new LocalFile(this, child);
				}

				addingFile = false;
				if (includeCloakedFiles || !localFile.isCloaked())
				{
					list.add(localFile);
					addingFile = true;
				}

				if (recurse && child.isDirectory() && addingFile)
				{
					getFiles(child, recurse, list, includeCloakedFiles);
				}
			}
		}
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#hasFiles(IVirtualFile file)
	 */
	public boolean hasFiles(IVirtualFile file)
	{
		return file.hasFiles();
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getFileSeparator()
	 */
	public String getFileSeparator()
	{
		return File.separator;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getGroup(com.aptana.ide.core.io.IVirtualFile)
	 */
	public String getGroup(IVirtualFile file)
	{
		// TODO: implement
		return null;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#setGroup(com.aptana.ide.core.io.IVirtualFile, java.lang.String)
	 */
	public void setGroup(IVirtualFile file, String groupName)
	{
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getImage()
	 */
	public Image getImage()
	{
		if (_image == null)
		{
			return ImageUtils.getFolderIcon();
		}
		else
		{
			return _image;
		}
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#setImage(org.eclipse.swt.graphics.Image)
	 */
	public void setImage(Image image)
	{
		_image = image;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getDisabledImage()
	 */
	public Image getDisabledImage()
	{
		if (_disabledImage == null)
		{
			return ImageUtils.getFolderIcon();
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
	 * @see com.aptana.ide.core.io.VirtualManagerBase#getTimeOffset()
	 */
	public long getTimeOffset() throws ConnectionException
	{
		return 0;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o)
	{
		if (o instanceof LocalFileManager)
		{
			return this.getNickName().compareToIgnoreCase(((LocalFileManager) o).getNickName());
		}
		return super.compareTo(o);
	}

	/**
	 * deleteDirectory
	 * 
	 * @param dir
	 * @return boolean
	 */
	private static boolean deleteDirectory(File dir)
	{
		if (dir.isDirectory())
		{
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++)
			{
				boolean result = deleteDirectory(new File(dir, children[i]));
				if (!result)
				{
					return false;
				}
			}
		}
		return dir.delete();
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#deleteFile(com.aptana.ide.core.io.IVirtualFile)
	 */
	public boolean deleteFile(IVirtualFile file)
	{
		boolean result = false;
		if (file == null || !(file instanceof LocalFile))
		{
			return result;
		}

		LocalFile lf = (LocalFile) file;
		File target = lf.getFile();
		if (target != null && target.exists())
		{
			result = deleteDirectory(target);
		}
		return result;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getStream(com.aptana.ide.core.io.IVirtualFile)
	 */
	public InputStream getStream(IVirtualFile file)
	{
		InputStream result = null;

		try
		{
			result = new FileInputStream(file.getAbsolutePath());
		}
		catch (FileNotFoundException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), StringUtils.format(
					Messages.LocalFileManager_UnableToCreateFileStreamForFile, file.getAbsolutePath()), e);
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#putToLocalFile(com.aptana.ide.core.io.IVirtualFile, java.io.File)
	 */
	public void putToLocalFile(IVirtualFile file, File localFile)
	{
		if (file.isLocal())
		{
			FileUtils.copy(new File(file.getAbsolutePath()), localFile);
		}
		else
		{
			try
			{
				FileUtils.pipe(file.getStream(), new FileOutputStream(localFile), false);
				localFile.setLastModified(System.currentTimeMillis());
			}
			catch (Exception e)
			{
				IdeLog.logError(FilePlugin.getDefault(), Messages.LocalFileManager_Error_copying_local_file, e);
			}
		}
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#moveFile(com.aptana.ide.core.io.IVirtualFile,
	 *      com.aptana.ide.core.io.IVirtualFile)
	 */
	public boolean moveFile(IVirtualFile source, IVirtualFile destination)
	{
		boolean result = false;
		if (source == null || destination == null || !(source instanceof LocalFile))
		{
			return result;
		}

		result = moveFile(source.getAbsolutePath(), destination.getAbsolutePath());
		return result;
	}

	// private FileFilter getFileOnlyFilter()
	// {
	// if(this._fileOnlyFilter == null)
	// {
	// this._fileOnlyFilter = new FileFilter()
	// {
	// public boolean accept(File file)
	// {
	// return !file.isDirectory();
	// }
	// };
	// }
	// return this._fileOnlyFilter;
	// }
	// private FileFilter getDirectoryOnlyFilter()
	// {
	// if(this._fileOnlyFilter == null)
	// {
	// this._fileOnlyFilter = new FileFilter()
	// {
	// public boolean accept(File file)
	// {
	// return file.isDirectory();
	// }
	// };
	// }
	// return this._directoryOnlyFilter;
	// }

	/**
	 * moveFile
	 * 
	 * @param from
	 * @param to
	 * @return boolean
	 */
	private boolean moveFile(String from, String to)
	{
		boolean result = FileUtils.copy(from, to);
		if (result == true)
		{
			File fromFile = new File(from);
			result = fromFile.delete();
		}
		return result;
	}

	/**
	 * @throws IOException
	 * @see com.aptana.ide.core.io.IVirtualFileManager#putStream(java.io.InputStream, IVirtualFile, IFileProgressMonitor)
	 */
	public void putStream(InputStream input, IVirtualFile targetFile, IFileProgressMonitor monitor) throws IOException
	{
		if (input == null)
		{
			throw new IllegalArgumentException(Messages.LocalFileManager_InputStreamCannotBeNull);
		}

		File file = new File(targetFile.getAbsolutePath());

		try
		{
			if (file.exists() || file.createNewFile())
			{
				// create output stream
				FileOutputStream output = new FileOutputStream(file);

				// copy input stream to output stream
				FileUtils.pipe(input, output, false, null, monitor);

				// close streams
				output.close();
				
				if (monitor != null)
				{
					monitor.done();
				}
			}
		}
		finally
		{
			// make sure to close input
			try
			{
				input.close();
			}
			catch (IOException e)
			{
			}
		}
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#renameFile(com.aptana.ide.core.io.IVirtualFile, java.lang.String)
	 */
	public boolean renameFile(IVirtualFile file, String newName)
	{
		boolean result = false;
		if (file == null || !(file instanceof LocalFile))
		{
			return result;
		}
		LocalFile lf = (LocalFile) file;
		File target = lf.getFile();
		File newFile = new File(newName);

		File newParent = newFile.getParentFile();
		if (newParent == null) // might be file name only, not including dir path?
		{
			File parent = target.getParentFile();
			if (parent != null)
			{
				String path = parent.getAbsolutePath() + File.separator + newName;
				newFile = new File(path);
			}
		}
		try
		{
			result = renameFile(target, newFile);
			if (result)
			{
				lf.setInternalFile(newFile);
			}
		}
		catch (IOException e)
		{
			result = false;
		}

		return result;
	}

	// private FileFilter getFileOnlyFilter()
	// {
	// if(this._fileOnlyFilter == null)
	// {
	// this._fileOnlyFilter = new FileFilter()
	// {
	// public boolean accept(File file)
	// {
	// return !file.isDirectory();
	// }
	// };
	// }
	// return this._fileOnlyFilter;
	// }
	// private FileFilter getDirectoryOnlyFilter()
	// {
	// if(this._fileOnlyFilter == null)
	// {
	// this._fileOnlyFilter = new FileFilter()
	// {
	// public boolean accept(File file)
	// {
	// return file.isDirectory();
	// }
	// };
	// }
	// return this._directoryOnlyFilter;
	// }

	/**
	 * refresh
	 */
	public void refresh()
	{
	}

	/**
	 * renameFile
	 * 
	 * @param source
	 * @param destination
	 * @return boolean
	 * @throws IOException
	 */
	private boolean renameFile(File source, File destination) throws IOException
	{
		boolean result = false;
		if (destination.isDirectory())
		{
			File parent = destination.getParentFile();

			if (parent != null && !parent.exists())
			{
				parent.mkdirs();
			}
		}

		if (source.getAbsolutePath().toLowerCase().equals(destination.getAbsolutePath().toLowerCase()))
		{
			result = source.renameTo(destination);
			return result;
		}

		if (destination.isFile() && destination.exists())
		{
			throw new IOException(Messages.LocalFileManager_UnableToRemoveExistingFile + destination);
		}
		result = source.renameTo(destination);
		return result;
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getHashString()
	 */
	public String getHashString()
	{
		String result = ""; //$NON-NLS-1$

		result += this.getNickName() + ISerializableSyncItem.DELIMITER;
		result += this.getBasePath() + ISerializableSyncItem.DELIMITER;
		result += this.getId() + ISerializableSyncItem.DELIMITER;
		result += this.isAutoCalculateServerTimeOffset() + ISerializableSyncItem.DELIMITER;
		try
		{
			result += this.getTimeOffset() + ISerializableSyncItem.DELIMITER;
		}
		catch (ConnectionException e)
		{
			result += 0 + ISerializableSyncItem.DELIMITER;
		}
		result += (this.serializeCloakedFiles(getCloakedFiles()) + ISerializableSyncItem.DELIMITER);
		result += (StringUtils.join(ISerializableSyncItem.FILE_DELIMITER, getCloakedFileExpressions()) + ISerializableSyncItem.DELIMITER);

		return result;
	}

	/**
	 * @see IVirtualFileManager#containsFile(IVirtualFile)
	 */
	public boolean containsFile(IVirtualFile file)
	{
		Path otherPath = new Path(file.getAbsolutePath());
		if (this.getBasePath() != null)
		{
			Path thisPath = new Path(this.getBaseFile().getAbsolutePath());
			return thisPath.isPrefixOf(otherPath);
		}
		else
		{
			return false;
		}
	}

	/**
	 * @see com.aptana.ide.core.io.sync.ISerializableSyncItem#getType()
	 */
	public String getType()
	{
		return this.getClass().getName();
	}

	/**
	 * @see com.aptana.ide.core.io.sync.ISerializableSyncItem#toSerializableString()
	 */
	public String toSerializableString()
	{
		return getHashString();
	}

	/**
	 * @see com.aptana.ide.core.io.sync.ISerializableSyncItem#fromSerializableString(java.lang.String)
	 */
	public void fromSerializableString(String s)
	{
		String[] items = s.split(ISerializableSyncItem.DELIMITER);

		if (items.length >= 3)
		{
			setNickName(items[0]);
			if (items[1] == null || StringUtils.EMPTY.equals(items[1]))
			{
				setBasePath("/"); //$NON-NLS-1$
			}
			else
			{
				setBasePath(items[1]);
			}
			setId(Long.parseLong(items[2]));
		}
		if (items.length >= 5)
		{
			setAutoCalculateServerTimeOffset(Boolean.valueOf(items[3]).booleanValue());
			setTimeOffset(Long.parseLong(items[4]));
		}
		if (items.length >= 7)
		{
			IVirtualFile[] files = deserializeCloakedFiles(items[5]);
			for (int i = 0; i < files.length; i++)
			{
				IVirtualFile file = files[i];
				addCloakedFile(file);
			}

			String[] files2 = items[6].split(ISerializableSyncItem.FILE_DELIMITER);
			for (int i = 0; i < files.length; i++)
			{
				String file2 = files2[i];
				addCloakExpression(file2);
			}
		}
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#getDescriptiveLabel()
	 */
	public String getDescriptiveLabel()
	{
		String label = getNickName();
		if (label == null || StringUtils.EMPTY.equals(label))
		{
			return getBasePath();
		}
		else
		{
			return label;
		}
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#connect()
	 */
	public void connect()
	{
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#disconnect()
	 */
	public void disconnect()
	{
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#resolveBasePath()
	 */
	public void resolveBasePath() throws ConnectionException, VirtualFileManagerException
	{
		if (CoreUIUtils.runningOnMac == false)
		{
			File[] root = FileSystemView.getFileSystemView().getRoots();
			if (root.length > 0)
			{
				_basePath = root[0].getAbsolutePath();
			}
		}
		else
		{
			File[] root = File.listRoots();
			if (root.length > 0)
			{
				_basePath = root[0].getAbsolutePath();
			}
		}
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#isConnected()
	 */
	public boolean isConnected()
	{
		return true;
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
	 * Adds the specified cloaking expression
	 * 
	 * @param expression
	 */
	public static void addGlobalSyncCloakExpression(String expression)
	{
		List<String> newExpressions = new ArrayList<String>();
		IPreferenceStore store = CoreUIPlugin.getDefault().getPreferenceStore();
		String editors = store.getString(IPreferenceConstants.PREF_GLOBAL_SYNC_CLOAKING_EXTENSIONS);
		if (editors != null && !"".equals(editors)) //$NON-NLS-1$
		{
			String[] array = editors.split(";"); //$NON-NLS-1$
			boolean found = false;
			String string;
			for (int i = 0; i < array.length; i++)
			{
				string = array[i];
				if (string.equals(expression))
				{
					found = true;
				}
				newExpressions.add(string);
			}
			if (!found)
			{
				newExpressions.add(expression);
			}
		}
		else
		{
			newExpressions.add(expression);
		}

		editors = StringUtils.join(";", (String[]) newExpressions.toArray(new String[0])); //$NON-NLS-1$
		store.setValue(IPreferenceConstants.PREF_GLOBAL_SYNC_CLOAKING_EXTENSIONS, editors);
		CoreUIPlugin.getDefault().savePluginPreferences();
	}

	/**
	 * Removes the specified cloaking expression
	 * 
	 * @param expression
	 */
	public static void removeGlobalSyncCloakExpression(String expression)
	{
		List<String> newExpressions = new ArrayList<String>();
		IPreferenceStore store = CoreUIPlugin.getDefault().getPreferenceStore();
		String editors = store.getString(IPreferenceConstants.PREF_GLOBAL_SYNC_CLOAKING_EXTENSIONS);
		if (editors != null && !"".equals(editors)) //$NON-NLS-1$
		{
			String[] array = editors.split(";"); //$NON-NLS-1$
			String string;
			for (int i = 0; i < array.length; i++)
			{
				string = array[i];
				if (!string.equals(expression))
				{
					newExpressions.add(string);
				}
			}
		}

		editors = StringUtils.join(";", (String[]) newExpressions.toArray(new String[0])); //$NON-NLS-1$
		store.setValue(IPreferenceConstants.PREF_GLOBAL_SYNC_CLOAKING_EXTENSIONS, editors);
		CoreUIPlugin.getDefault().savePluginPreferences();
	}

	/**
	 * Is the current file manager valid (meaning that the base path is valid)
	 * 
	 * @return boolean
	 */
	public boolean isValid()
	{
		File f = new File(getBasePath());
		return f.exists();
	}

	/**
	 * @see com.aptana.ide.core.io.IVirtualFileManager#cloneManager()
	 */
	public IVirtualFileManager cloneManager()
	{
		LocalFileManager manager = new LocalFileManager(this.getProtocolManager());
		manager.setId(getId());
		manager.setBasePath(this.getBasePath());
		manager.setCloakedFiles(this.getCloakedFiles());
		manager.setImage(this.getImage());
		manager.setDisabledImage(this.getDisabledImage());
		manager.setTransient(this.isTransient());
		return manager;
	}
}
