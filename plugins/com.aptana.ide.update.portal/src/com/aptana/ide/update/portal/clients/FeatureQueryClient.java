/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.ide.update.portal.clients;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.aptana.ide.server.jetty.comet.CometConstants;
import com.aptana.ide.server.jetty.comet.CometResponderClient;
import com.aptana.ide.update.FeatureUtil;

public class FeatureQueryClient extends CometResponderClient
{
	/**
	 * PLUGINS_QUERY
	 */
	public static final String PLUGINS_QUERY = "/portal/plugins/query"; //$NON-NLS-1$

	/**
	 * SVN_TEAM_PROVIDER
	 */
	public static final String SVN_TEAM_PROVIDER = "svnteamprovider"; //$NON-NLS-1$

	/**
	 * INSTALLED
	 */
	public static final String INSTALLED = "installed"; //$NON-NLS-1$

	private static final String EXTENSION_POINT = "org.eclipse.team.core.repository"; //$NON-NLS-1$
	private static final String EXTENSION_NAME = "repository"; //$NON-NLS-1$
	private static final String ATTRIBUTE_ID = "id"; //$NON-NLS-1$

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getResponse(java.lang.String, java.lang.Object)
	 */
	protected Object getResponse(String toChannel, Object request)
	{
		if (PLUGINS_QUERY.equals(toChannel))
		{
			Map requestMap = (Map) request;
			if (SVN_TEAM_PROVIDER.equals(requestMap.get(CometConstants.REQUEST)))
			{
				Map<String, Object> returnMap = new HashMap<String, Object>();
				boolean svnTeamProviderInstalled = false;
				if (isFeatureInstalled("com.aptana.ide.feature.svn")) //$NON-NLS-1$
				{
					svnTeamProviderInstalled = true;
				}
				else
				{
					IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
							EXTENSION_POINT);
					String id;
					for (IConfigurationElement element : elements)
					{
						if (element.getName().equals(EXTENSION_NAME))
						{
							id = element.getAttribute(ATTRIBUTE_ID);
							if (id != null && id.contains(".svnnature")) //$NON-NLS-1$
							{
								svnTeamProviderInstalled = true;
								break;
							}
						}
					}
				}
				returnMap.put(INSTALLED, svnTeamProviderInstalled);
				returnMap.put(CometConstants.RESPONSE, SVN_TEAM_PROVIDER);
				return returnMap;
			}
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.server.jetty.comet.CometClient#getSubscriptionIDs()
	 */
	protected String[] getSubscriptionIDs()
	{
		return new String[] { PLUGINS_QUERY };
	}

	/**
	 * Returns true if a feature with this id is enabled
	 * 
	 * @param featureId
	 * @return true if enabled, false otherwise
	 */
	private static boolean isFeatureInstalled(String featureId)
	{
		return FeatureUtil.isInstalled(featureId);
	}

}
