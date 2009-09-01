/* ***** BEGIN LICENSE BLOCK *****
 * Version: GPL 3
 *
 * This program is Copyright (C) 2007-2008 Aptana, Inc. All Rights Reserved
 * This program is licensed under the GNU General Public license, version 3 (GPL).
 *
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by the GPL,
 * is prohibited.
 *
 * You can redistribute and/or modify this program under the terms of the GPL, 
 * as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * You may view the GPL, and Aptana's exception and additional terms in the file
 * titled license-jaxer.html in the main distribution folder of this program.
 * 
 * Any modifications to this file must keep this entire header intact.
 *
 * ***** END LICENSE BLOCK ***** */
package com.aptana.ide.editors.profiles;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editors.UnifiedEditorsPlugin;

/**
 * @author Kevin Lindsey
 */
public class ProfileFileTypeManager
{
	private static final String FILE_PROCESSOR_ID = "fileProcessor"; //$NON-NLS-1$
	private static final Object TAG_FILE_PROCESSOR = "fileProcessor"; //$NON-NLS-1$
	private static final String ATTR_EXTENSION = "extension"; //$NON-NLS-1$
	private static final String ATTR_PROCESSOR = "processor"; //$NON-NLS-1$
	
	private static ProfileFileTypeManager instance;
	private Map<String, ProfileFileTypeInfo> _infoMap;
	
	/**
	 * getInstance
	 *
	 * @return
	 */
	public static ProfileFileTypeManager getInstance()
	{
		if (instance == null)
		{
			instance = new ProfileFileTypeManager();
		}
		
		return instance;
	}

	/**
	 * ProfileFileTypeManager
	 */
	private ProfileFileTypeManager()
	{
	}
	
	/**
	 * getAllInfos
	 *
	 * @return
	 */
	public ProfileFileTypeInfo[] getAllInfos()
	{
		loadInfoMap();
		
		Collection<ProfileFileTypeInfo> infos = this._infoMap.values();
		
		return infos.toArray(new ProfileFileTypeInfo[infos.size()]);
	}

	/**
	 * getInfo
	 *
	 * @param extension
	 * @return
	 */
	public ProfileFileTypeInfo getInfo(String extension)
	{
		loadInfoMap();
		
		return this._infoMap.get(extension.toLowerCase(Locale.getDefault()));
	}
	
	/**
	 * loadExtensions
	 *
	 * @return
	 */
	private ProfileFileTypeInfo[] loadExtensions()
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		List<ProfileFileTypeInfo> processors = new ArrayList<ProfileFileTypeInfo>();

		if (registry != null)
		{
			IExtensionPoint extensionPoint = registry.getExtensionPoint(UnifiedEditorsPlugin.ID, FILE_PROCESSOR_ID);
			IExtension[] extensions = extensionPoint.getExtensions();

			for (IExtension extension : extensions)
			{
				this.processExtension(extension, processors);
			}
		}

		return processors.toArray(new ProfileFileTypeInfo[processors.size()]);
	}

	/**
	 * loadInfoMap
	 *
	 */
	private void loadInfoMap()
	{
		if (this._infoMap == null)
		{
			this._infoMap = new HashMap<String, ProfileFileTypeInfo>();
			
			for (ProfileFileTypeInfo info : this.loadExtensions())
			{
				this._infoMap.put(info.fileExtension, info);
			}
		}
	}

	/**
	 * processExtension
	 *
	 * @param extension
	 * @param processors
	 */
	private void processExtension(IExtension extension, List<ProfileFileTypeInfo> processors)
	{
		IConfigurationElement[] elements = extension.getConfigurationElements();

		for (int i = 0; i < elements.length; i++)
		{
			IConfigurationElement element = elements[i];

			if (element.getName().equals(TAG_FILE_PROCESSOR))
			{
				String fileExtension = element.getAttribute(ATTR_EXTENSION);
				IProfileFileProcessor processor = null;
				
				try
				{
					// NOTE: If we think we're going to have a lot of these, then
					// we may want to delay instantiation until the processor is
					// requested from the ProfileFileTypeInfo instance. There's
					// an InstanceCreator class that could be used for this
					processor = (IProfileFileProcessor) element.createExecutableExtension(ATTR_PROCESSOR);
				}
				catch (CoreException e)
				{
					IdeLog.logError(
						UnifiedEditorsPlugin.getDefault(),
						MessageFormat.format(
							Messages.ProfileFileTypeManager_ERR_UnableToInstantiateFileProcessorFor,
							fileExtension,
							element.getAttribute(ATTR_PROCESSOR)
						)
					);
				}
				
				processors.add(new ProfileFileTypeInfo(fileExtension, processor));
			}
		}
	}
}
