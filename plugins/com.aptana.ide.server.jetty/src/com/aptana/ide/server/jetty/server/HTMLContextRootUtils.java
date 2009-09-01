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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.server.jetty.server;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.server.core.ServerCorePlugin;

/**
 * Utils for resolving rooted urls with configured context root
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public final class HTMLContextRootUtils
{

	private HTMLContextRootUtils()
	{
		// Does nothing
	}

	/**
	 * Resolves a rooted url by considering the configured project context root
	 * 
	 * @param project
	 * @param rootedURL
	 * @return - resolved url
	 */
	public static String resolveURL(IProject project, String rootedURL)
	{
		String resolved = rootedURL;
		try
		{
			String override = project.getPersistentProperty(new QualifiedName("", //$NON-NLS-1$
					HTMLPreviewConstants.HTML_PREVIEW_OVERRIDE));
			if (HTMLPreviewConstants.TRUE.equals(override))
			{
				String contextRoot = project.getPersistentProperty(new QualifiedName("", //$NON-NLS-1$
						HTMLPreviewConstants.CONTEXT_ROOT));
				if (contextRoot != null && !contextRoot.equals("/")) //$NON-NLS-1$
				{
					if (rootedURL.startsWith("/")) //$NON-NLS-1$
					{
						resolved = contextRoot + rootedURL;
					}
				}
			}
		}
		catch (CoreException e)
		{
			IdeLog.logInfo(ServerCorePlugin.getDefault(), "Failed obtaining context root for project", e); //$NON-NLS-1$
		}
		return resolved;
	}
}
