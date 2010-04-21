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

import java.util.Hashtable;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

import com.aptana.ide.core.AptanaCorePlugin;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;

/**
 * @author Kevin Lindsey
 */
public abstract class ProtocolManager implements Comparable
{
	private static ProtocolManager[] protocolManagers;
	//private static final IActivityManager activityManager  = PlatformUI.getWorkbench().getActivitySupport().getActivityManager();
	
	private String _fileManagerName = StringUtils.EMPTY;
	private String _displayName = StringUtils.EMPTY;
	private int _sortPriority;
	
	private boolean _hidden = false;
	private boolean _remote = false;
	private boolean _allowNew = true;
	

	private String decoratorId;

//	private static IIdentifierListener identifierListener = new IIdentifierListener()
//	{
//		public void identifierChanged(IdentifierEvent identifierEvent)
//		{
//			IIdentifier identifier = identifierEvent.getIdentifier();
//			if (identifier.isEnabled())
//			{
//				// reset the manager. It will be reloaded next time the managers are requested.
//				protocolManagers = null;
//			}
//		}
//	};
	
	/**
	 * ProtocolManager
	 */
	public ProtocolManager()
	{
	}

	/**
	 * True is this protocol manager has custom content
	 * 
	 * @return - true if custom content
	 */
	public boolean hasCustomContent()
	{
		return false;
	}

	/**
	 * Gets the custom content
	 * 
	 * @return - array of content
	 */
	public Object[] getContent()
	{
		return null;
	}

	/**
	 * Gets whether or not file managers should be added via the UI in the file explorer view
	 * 
	 * @return - true if add menu should be shown
	 */
	public boolean canAddFileManagers()
	{
		return true;
	}

	/**
	 * getDisplayName
	 * 
	 * @return Returns the name displayed in the File View
	 */
	public String getDisplayName()
	{
		return this._displayName;
	}

	/**
	 * Returns the sort priority of this protocol manager. The highest the number, the higher sort priority this manager should get (appear higher in the sorting result).
	 * 
	 * @return The sort priority for this protocol manager
	 */
	public int getSortPriority() 
	{
		return this._sortPriority;
	}
	
	/**
	 * getFileManagerName
	 * 
	 * @return String
	 */
	public String getFileManagerName()
	{
		return this._fileManagerName;
	}

	/**
	 * setDisplayName
	 * 
	 * @param displayName
	 *            display name to set
	 */
	public void setDisplayName(String displayName)
	{
		this._displayName = displayName;
	}

	/**
	 * setFileManagerName
	 * 
	 * @param fileManagerName
	 *            file manager name to set
	 */
	public void setFileManagerName(String fileManagerName)
	{
		this._fileManagerName = fileManagerName;
	}

	/**
	 * Set the sort priority for this protocol manager.
	 * 
	 * @param sortPriority
	 */
	public void setSortPriority(int sortPriority) 
	{
		this._sortPriority = sortPriority;
	}
	
	/**
	 * getFileManagers
	 * 
	 * @return Returns an array of file managers for this protocol
	 */
	public abstract IVirtualFileManager[] getFileManagers();

	/**
	 * getManagedType
	 * 
	 * @return String
	 */
	public abstract String getManagedType();

	/**
	 * getFileManager
	 * 
	 * @param absolutePath
	 *            the local path
	 * @return Returns the file manager that matches this base path
	 */
	public IVirtualFileManager getFileManager(String absolutePath)
	{
		IVirtualFileManager[] fms = getFileManagers();

		for (int i = 0; i < fms.length; i++)
		{
			IVirtualFileManager manager = fms[i];

			if (manager.getBasePath() != null && absolutePath.equals(manager.getBaseFile().getAbsolutePath()))
			{
				return manager;
			}
		}
		return null;
	}

	/**
	 * getImage
	 * 
	 * @return Image
	 */
//	public Image getImage()
//	{
//		return null;
//	}

	/**
	 * addFileManager
	 * 
	 * @param fileManager
	 */
	public void addFileManager(IVirtualFileManager fileManager)
	{
		if (fileManager == null)
		{
			throw new IllegalArgumentException(Messages.ProtocolManager_FileManagerNullError);
		}

		SyncManager.getSyncManager().addItem(fileManager);
	}

	/**
	 * removeFileManager
	 * 
	 * @param fileManager
	 */
	public void removeFileManager(IVirtualFileManager fileManager)
	{
		if (fileManager != null)
		{
			VirtualFileManagerSyncPair[] pairs = SyncManager.getSyncPairs(fileManager);

			for (int i = 0; i < pairs.length; i++)
			{
				VirtualFileManagerSyncPair pair = pairs[i];

				if (pair.getDestinationFileManager() != null && pair.getDestinationFileManager().equals(fileManager))
				{
					pair.setDestinationFileManager(null);
				}

				if (pair.getSourceFileManager() != null && pair.getSourceFileManager().equals(fileManager))
				{
					pair.setSourceFileManager(null);
				}
			}

			SyncManager.getSyncManager().removeItem(fileManager);
		}
	}

	/**
	 * Compare this {@link ProtocolManager} to another {@link ProtocolManager}.
	 * At first, compare the two by their sort priority. In case that the managers have the same sort priority, we 
	 * compare them by name (case insensitive).
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o)
	{
		if (o instanceof ProtocolManager)
		{
			// First, compare the two by priority.
			ProtocolManager other = (ProtocolManager)o;
			int priorityDiff = other._sortPriority - this._sortPriority;
			if (priorityDiff != 0) 
			{
				return priorityDiff;
			}
			// In case the priority is the same, compare by name.
			return this.getDisplayName().compareToIgnoreCase(other.getDisplayName());
		}
		else
		{
			return 0;
		}
	}

	/**
	 * createFileManager
	 * 
	 * @return IVirtualFileManager
	 */
	public abstract IVirtualFileManager createFileManager();

	/**
	 * createFileManager
	 * 
	 * @param addManager
	 *            Add the manager to the protocolManager?
	 * @return IVirtualFileManager
	 */
	public IVirtualFileManager createFileManager(boolean addManager)
	{
		return createFileManager();
	}

	/**
	 * createPropertyDialog
	 * 
	 * @param parent
	 *            the shell
	 * @param style
	 *            the SWT property style
	 * @return IVirtualFileManagerDialog
	 */
	//public abstract IVirtualFileManagerDialog createPropertyDialog(Shell parent, int style);

	/**
	 * Returns an instance of the protocol manager
	 * 
	 * @return ProtocolManager
	 */
	public abstract ProtocolManager getStaticInstance();

	/**
	 * Gets the correct protocol manager based on its hashName.
	 * 
	 * @param name
	 * @return ProtocolManager
	 */
	public static ProtocolManager getProtocolManagerByName(String name)
	{
		ProtocolManager result = null;

		if (protocolManagers == null)
		{
			getPrototcolManagers();
		}

		for (int i = 0; i < protocolManagers.length; i++)
		{
			ProtocolManager manager = protocolManagers[i];

			if (manager.getFileManagerName().equals(name))
			{
				result = manager;
				break;
			}
		}

		return result;
	}

	/**
	 * getProtocolManagerForType
	 * 
	 * @param type
	 * @return ProtocolManager
	 */
	public static ProtocolManager getProtocolManagerByType(String type)
	{
		if (protocolManagers == null)
		{
			getPrototcolManagers();
		}

		for (int i = 0; i < protocolManagers.length; i++)
		{
			ProtocolManager manager = protocolManagers[i];

			if (manager.getManagedType().equals(type))
			{
				return manager;
			}
		}

		return null;
	}

	/**
	 * Gets an array of all known protocol managers.
	 * 
	 * @return ProtocolManager[]
	 */
	public static ProtocolManager[] getPrototcolManagers()
	{
		if (protocolManagers != null)
		{
			return protocolManagers;
		}

		IExtension[] extensions = Platform.getExtensionRegistry().getExtensionPoint("com.aptana.ide.core", "protocols") //$NON-NLS-1$ //$NON-NLS-2$
				.getExtensions();

		Hashtable<String, Integer> priorityTable = new Hashtable<String, Integer>();
		Hashtable<String, ProtocolManager> found = new Hashtable<String, ProtocolManager>();

		for (int i = 0; i < extensions.length; i++)
		{
			IConfigurationElement[] configElements = extensions[i].getConfigurationElements();

			for (int j = 0; j < configElements.length; j++)
			{
				try
				{
					
					IConfigurationElement element = configElements[j];
					if (!isActivityEnabled(element)) {
						continue;
					}
					ProtocolManager pm = (ProtocolManager) configElements[j].createExecutableExtension("class"); //$NON-NLS-1$

					if (pm != null)
					{
						pm = pm.getStaticInstance();
						String displayName = element.getAttribute("displayName"); //$NON-NLS-1$
						pm.setDisplayName(displayName); //$NON-NLS-1$
						pm.setFileManagerName(element.getAttribute("fileManagerName")); //$NON-NLS-1$
						String hidden = element.getAttribute("hidden"); //$NON-NLS-1$
						String allowNew = element.getAttribute("allowNew"); //$NON-NLS-1$
						String remote = element.getAttribute("remote"); //$NON-NLS-1$
						String pri = element.getAttribute("priority"); //$NON-NLS-1$
						String sortPriority = element.getAttribute("sortPriority"); //$NON-NLS-1$
						String extensionId = element.getAttribute("id"); //$NON-NLS-1$
						String extensionPluginId = element.getNamespaceIdentifier();
						String decoratorId = element.getAttribute("decoratorId"); //$NON-NLS-1$
						
						Integer priority = 0;
						if (pri != null && !"".equals(pri)) //$NON-NLS-1$
						{
							priority = Integer.parseInt(pri);
						}
						if (sortPriority != null && !"".equals(sortPriority)) //$NON-NLS-1$
						{
							pm.setSortPriority(Integer.parseInt(sortPriority));
						}
						if (remote != null && "true".equalsIgnoreCase(remote)) //$NON-NLS-1$
						{
							pm.setRemote(true);
						}
						if (hidden != null && "true".equalsIgnoreCase(hidden)) //$NON-NLS-1$
						{
							pm.setHidden(true);
						}
						
						if (allowNew != null && "false".equalsIgnoreCase(allowNew)) //$NON-NLS-1$
						{
							pm.setAllowNew(false);
						}

						Integer oldPriority = -1;
						if (priorityTable.containsKey(displayName))
						{
							// Will be 0 or greater
							oldPriority = priorityTable.get(displayName);
						}

						// use the highest priority protocol manager
						if (oldPriority < priority)
						{
							priorityTable.put(displayName, priority);
							found.put(displayName, pm);
						}
						if (extensionId != null ){
							pm.setExtensionId(extensionId);
						}
						
						if ( extensionPluginId != null ){
							pm.setExtensionPluginId(extensionPluginId);
						}
						
						if ( decoratorId != null ){
							pm.setDecoratorId(decoratorId);
						}

					}
				}
				catch (Exception ex)
				{
					IdeLog.logError(AptanaCorePlugin.getDefault(),
							Messages.ProtocolManager_UnableToLoadProtocolManagerError, ex);
				}
			}
		}

		protocolManagers = (ProtocolManager[]) found.values().toArray(new ProtocolManager[0]);

		return protocolManagers;
	}

	/**
	 * Returns true if the element is enabled in the activities; False, otherwise.
	 * @param element an {@link IConfigurationElement}
	 * @return true if the element is enabled in the activities; False, otherwise.
	 */
	public static final boolean isActivityEnabled(IConfigurationElement element)
	{
		String extensionId = element.getAttribute("id"); //$NON-NLS-1$
		String extensionPluginId = element.getNamespaceIdentifier();
		String extensionString = null;
		if (extensionPluginId != null && extensionId != null && extensionPluginId.length() > 0
				&& extensionId.length() > 0)
		{
			extensionString = extensionPluginId + "/" + extensionId; //$NON-NLS-1$
		}
		else if (extensionPluginId != null && extensionPluginId.length() > 0)
		{
			extensionString = extensionPluginId + "/.*"; //$NON-NLS-1$
		}

		if (extensionString != null)
		{
//			final IIdentifier id = activityManager.getIdentifier(extensionString);
//			if (id != null)
//			{
//				boolean enabled = id.isEnabled();
//				if( !id.isEnabled()){
//					id.addIdentifierListener(identifierListener);
//				}
//				return enabled;
//			}
		}
		return true;
	}
	
	private void setDecoratorId(String decoratorId) {
		this.decoratorId = decoratorId;
	}
	
	public String getDecoratorId() {
		return decoratorId;
	}

	/**
	 * isHidden
	 * 
	 * @return boolean
	 */
	public boolean isHidden()
	{
		return this._hidden;
	}

	/**
	 * setHidden
	 * 
	 * @param hidden
	 */
	public void setHidden(boolean hidden)
	{
		this._hidden = hidden;
	}

	/**
	 * Is remote protocol
	 * 
	 * @return true if remote protocol, false otherwise
	 */
	public boolean isRemote()
	{
		return _remote;
	}

	/**
	 * Sets a protocol as remote
	 * 
	 * @param remote -
	 *            true if remote
	 */
	public void setRemote(boolean remote)
	{
		this._remote = remote;
	}
	

	private void setExtensionPluginId(String extensionPluginId) {
		this.extensionPluginId = extensionPluginId;
	}

	public String getExtensionPluginId() {
		return extensionPluginId;
	}


	private void setExtensionId(String extensionId) {
		this.extensionId = extensionId;
	}

	public String getExtensionId() {
		return extensionId;
	}


	/**
	 * @param _allowNew the _allowNew to set
	 */
	public void setAllowNew(boolean _allowNew)
	{
		this._allowNew = _allowNew;
	}

	/**
	 * @return the _allowNew
	 */
	public boolean isAllowNew()
	{
		return _allowNew;
	}
	
	public boolean allowNavigation()
	{
		return true;
	}

	private String extensionPluginId;
	private String extensionId;
}
