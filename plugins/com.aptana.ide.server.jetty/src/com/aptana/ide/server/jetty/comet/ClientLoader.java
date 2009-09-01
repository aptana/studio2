/**
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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
package com.aptana.ide.server.jetty.comet;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.aptana.ide.server.jetty.JettyPlugin;

import dojox.cometd.Bayeux;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class ClientLoader
{

	/**
	 * ID_ATTRIBUTE
	 */
	public static final String ID_ATTRIBUTE = "id"; //$NON-NLS-1$

	/**
	 * RESOURCE_ATTRIBUTE
	 */
	public static final String CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$

	/**
	 * PORTLET_ELEMENT
	 */
	public static final String CLIENT_ELEMENT = "client"; //$NON-NLS-1$

	/**
	 * PORTLET_EXTENSION
	 */
	public static final String PORTLET_EXTENSION = JettyPlugin.PLUGIN_ID + ".cometClient"; //$NON-NLS-1$

	private List<ICometClient> clients;

	private ClientLoader(Bayeux bayeux)
	{
		clients = new ArrayList<ICometClient>();
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
				PORTLET_EXTENSION);
		for (IConfigurationElement element : elements)
		{
			if (CLIENT_ELEMENT.equals(element.getName()))
			{
				String id = element.getAttribute(ID_ATTRIBUTE);
				String className = element.getAttribute(CLASS_ATTRIBUTE);
				if (id != null && className != null)
				{
					try
					{
						Object client = element.createExecutableExtension(CLASS_ATTRIBUTE);
						if (client instanceof ICometClient && getClient(id) == null)
						{
							ICometClient cometClient = (ICometClient) client;
							cometClient.setID(id);
							cometClient.init(bayeux);
							this.clients.add(cometClient);
						}
					}
					catch (CoreException e)
					{
					}
				}
			}
		}
	}

	/**
	 * Gets the first matching client with the given id or null if none found
	 * 
	 * @param id
	 * @return - portlet or null
	 */
	public ICometClient getClient(String id)
	{
		if (id == null)
		{
			return null;
		}
		for (ICometClient c : this.clients)
		{
			if (id.equals(c.getID()))
			{
				return c;
			}
		}
		return null;
	}

	/**
	 * Gets the clients defined via extension point
	 * 
	 * @return - array of clients
	 */
	public ICometClient[] getClients()
	{
		return this.clients.toArray(new ICometClient[0]);
	}

	/**
	 * Gets the client loader instance
	 * 
	 * @param bayeux
	 * @return - client loader
	 */
	public static ClientLoader loadClients(Bayeux bayeux)
	{
		return new ClientLoader(bayeux);
	}

	public void destroy()
	{
		if (clients != null)
		{
			clients.clear();
		}
	}
}
