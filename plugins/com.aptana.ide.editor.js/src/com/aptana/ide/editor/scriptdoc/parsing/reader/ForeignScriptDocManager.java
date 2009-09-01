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
package com.aptana.ide.editor.scriptdoc.parsing.reader;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.aptana.ide.editor.js.JSPlugin;

/**
 * @author Kevin Lindsey
 */
public class ForeignScriptDocManager
{
	private static final String FOREIGN_SCRIPTDOC_ID = "foreign_scriptdoc"; //$NON-NLS-1$
	private static final Object TAG_STYLESHEET = "stylesheet"; //$NON-NLS-1$
	private static final String ATTR_NAME = "name"; //$NON-NLS-1$
	private static final String ATTR_NAMESPACE = "namespace"; //$NON-NLS-1$
	private static final String ATTR_RESOURCE = "resource"; //$NON-NLS-1$
	
	private static ForeignScriptDocManager instance;
	private Map<String, ForeignScriptDocInfo> _infoMap;

	/**
	 * ForeignScriptDocManager
	 */
	private ForeignScriptDocManager()
	{
	}

	/**
	 * getInstance
	 * 
	 * @return
	 */
	public static ForeignScriptDocManager getInstance()
	{
		if (instance == null)
		{
			instance = new ForeignScriptDocManager();
		}

		return instance;
	}

	/**
	 * getInfo
	 *
	 * @param namespace
	 * @return
	 */
	public ForeignScriptDocInfo getInfo(String namespace)
	{
		if (this._infoMap == null)
		{
			this._infoMap = new HashMap<String, ForeignScriptDocInfo>();
			
			for (ForeignScriptDocInfo info : this.loadExtensions())
			{
				this._infoMap.put(info.namespace, info);
			}
		}
		
		return this._infoMap.get(namespace);
	}

	/**
	 * loadExtensions
	 * 
	 * @return
	 */
	private ForeignScriptDocInfo[] loadExtensions()
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		List<ForeignScriptDocInfo> frameworks = new ArrayList<ForeignScriptDocInfo>();

		if (registry != null)
		{
			IExtensionPoint extensionPoint = registry.getExtensionPoint(JSPlugin.ID, FOREIGN_SCRIPTDOC_ID);
			IExtension[] extensions = extensionPoint.getExtensions();

			for (IExtension extension : extensions)
			{
				this.processExtension(extension, frameworks);
			}
		}

		return frameworks.toArray(new ForeignScriptDocInfo[frameworks.size()]);
	}

	/**
	 * processExtension
	 *
	 * @param extension
	 * @param info
	 */
	private void processExtension(IExtension extension, List<ForeignScriptDocInfo> info)
	{
		IConfigurationElement[] elements = extension.getConfigurationElements();

		for (int i = 0; i < elements.length; i++)
		{
			IConfigurationElement element = elements[i];

			if (element.getName().equals(TAG_STYLESHEET))
			{
				String name = element.getAttribute(ATTR_NAME);
				String namespace = element.getAttribute(ATTR_NAMESPACE);
				String stylesheetPath = element.getAttribute(ATTR_RESOURCE);
				
				IExtension declaring = element.getDeclaringExtension();
				String declaringPluginID = declaring.getNamespaceIdentifier();
				Bundle bundle = Platform.getBundle(declaringPluginID);
				
				URL stylesheet = bundle.getEntry(stylesheetPath);
				
				info.add(new ForeignScriptDocInfo(name, namespace, stylesheet));
			}
		}
	}
}
