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
package com.aptana.ide.server.core;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.aptana.ide.server.ServerCore;

/**
 * Server file type handlers.
 * @author Denis Denisenko
 */
public final class ServerFileTypeHandlers
{
	
	/**
	 * Preview info.
	 * @author Denis Denisenko
	 *
	 */
	public static class PreviewInfo
	{
		public String serverID;
		
		public String pathHeader;
		
		public String serverTypeID;
	}
	
	/**
	 * Preview file type handler extension point name.
	 */
	private static final String EXTENSION_POINT_NAME = "previewFileTypeHandler"; //$NON-NLS-1$
	
	/**
	 * Handler element name.
	 */
	private static final String HANDLER_ELEMENT_NAME = "handler"; //$NON-NLS-1$
	
	/**
	 * Extension attribute name.
	 */
	private static final String EXTENSION_ATTRIBUTE_NAME = "extension"; //$NON-NLS-1$
	
	/**
	 * ServerID attribute name.
	 */
	private static final String SERVER_ID_ATTRIBUTE_NAME = "serverID"; //$NON-NLS-1$
	
	/**
	 * ServerTypeID attribute name.
	 */
	private static final String SERVER_TYPE_ID_ATTRIBUTE_NAME = "serverTypeID"; //$NON-NLS-1$
	
	/**
	 * pathHeader attribute name.
	 */
	private static final String PATH_HEADER_ATTRIBUTE_NAME = "pathHeader"; //$NON-NLS-1$
	
	/**
	 * Gets server ID and path header from extensions depending on file extension.
	 * @param fileExtension - file extension.
	 * @return server ID or null.
	 */
	public static PreviewInfo getPreviewInfoByExtension(String fileExtension)
	{
		if (fileExtension == null)
		{
			return null;
		}
		
		final IExtensionRegistry registry = Platform.getExtensionRegistry();
		final IConfigurationElement[] elements = registry
				.getConfigurationElementsFor(ServerCore.PLUGIN_ID,
						EXTENSION_POINT_NAME);

		if (elements == null || elements.length == 0)
		{
			return null;
		}

		for (final IConfigurationElement element : elements)
		{
			if (HANDLER_ELEMENT_NAME.equals(element.getName()))
			{
				String extension = element.getAttribute(EXTENSION_ATTRIBUTE_NAME);
				if (!fileExtension.equals(extension))
				{
					continue;
				}
				
				String serverID = element.getAttribute(SERVER_ID_ATTRIBUTE_NAME);
				String serverTypeID = element.getAttribute(SERVER_TYPE_ID_ATTRIBUTE_NAME);
				String pathHeader = element.getAttribute(PATH_HEADER_ATTRIBUTE_NAME);
				if (serverID != null)
				{
					PreviewInfo toReturn = new PreviewInfo();
					toReturn.serverID = serverID;
					toReturn.pathHeader = pathHeader;
					toReturn.serverTypeID = serverTypeID;
					return toReturn;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Gets server ID and path header from extensions depending on file extension got from the URL specified.
	 * @param url - url.
	 * @return server ID or null.
	 */
	public static PreviewInfo getPreviewInfoFromURL(String url)
	{
		String fileExtension = getFileExtensionFromURL(url);
		return getPreviewInfoByExtension(fileExtension);
	}
	
	/**
	 * Gets file extension from URL.
	 * @param url - url.
	 * @return file extension or null.
	 */
	private static String getFileExtensionFromURL(String url)
	{
		int pointIndex = url.lastIndexOf("."); //$NON-NLS-1$
		if (pointIndex == -1)
		{
			return null;
		}
		
		if (pointIndex == url.length() - 1)
		{
			return null;
		}
		
		StringBuffer result = new StringBuffer();
		for (int i = pointIndex + 1; i < url.length(); i++)
		{
			char ch = url.charAt(i);
			if (Character.isJavaIdentifierPart(ch))
			{
				result.append(ch);
			}
			else
			{
				return result.toString();
			}
		}
		
		return result.toString();
	}
	
	/**
	 * ServerFileTypeHandlers private constructor.
	 */
	private ServerFileTypeHandlers()
	{
	}
}