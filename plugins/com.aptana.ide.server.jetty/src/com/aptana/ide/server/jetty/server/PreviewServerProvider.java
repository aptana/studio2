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

import com.aptana.ide.server.ServerCore;
import com.aptana.ide.server.core.IAbstractConfiguration;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.IServerManagerListener;
import com.aptana.ide.server.core.IServerType;
import com.aptana.ide.server.core.impl.Configuration;
import com.aptana.ide.server.core.impl.servers.ServerManager;
import com.aptana.ide.server.core.model.IServerProviderDelegate;
import com.aptana.ide.server.http.HttpServer;
import com.aptana.ide.server.jetty.JettyPlugin;
import com.aptana.ide.server.jetty.preferences.IPreferenceConstants;

/**
 * @author Pavel Petrochenko
 */
public class PreviewServerProvider implements IServerProviderDelegate
{

	/**
	 * INTERNAL_PREVIEW_SERVER_ID
	 */
	public static final String INTERNAL_PREVIEW_SERVER_ID = "com.aptana.ide.html.preview.htmlPreviewServer";//$NON-NLS-1$

	/**
	 * INTERNAL_PREVIEW_SERVER_NAME
	 */
	public static final String INTERNAL_PREVIEW_SERVER_NAME = "Built-in Preview Server";//$NON-NLS-1$

	/**
	 * 
	 */
	public PreviewServerProvider()
	{

	}

	/**
	 * @see com.aptana.ide.server.core.model.IServerProviderDelegate#addServerChangeListener(com.aptana.ide.server.core.IServerManagerListener)
	 */
	public void addServerChangeListener(IServerManagerListener listener)
	{
	}

	/**
	 * @see com.aptana.ide.server.core.model.IServerProviderDelegate#getServers()
	 */
	public IServer[] getServers()
	{
		IServerType tt = ServerCore.getServerManager().getServerType(PreviewServerTypeDelegate.ID);
		IAbstractConfiguration config = new Configuration();
		config.setStringAttribute(IServer.KEY_NAME, INTERNAL_PREVIEW_SERVER_NAME);
		config.setStringAttribute(IServer.KEY_ID, INTERNAL_PREVIEW_SERVER_ID);
		config.setIntAttribute(IServer.KEY_PORT, ServerManager.findFreePort(HttpServer.getPortRange()));
		config.setStringAttribute(IServer.KEY_ASSOCIATION_SERVER_ID, INTERNAL_PREVIEW_SERVER_NAME);
		PreviewServer server = new PreviewServer(tt, config, IServer.STATE_STOPPED);
		if (JettyPlugin.getDefault().getPreferenceStore().getBoolean(IPreferenceConstants.ENABLE_BUILTIN_PREVIEW))
		{
			server.start("run", null); //$NON-NLS-1$
		}
		return new IServer[] { server };
	}

	/**
	 * @see com.aptana.ide.server.core.model.IServerProviderDelegate#isRemovable(com.aptana.ide.server.core.IServer)
	 */
	public boolean isRemovable(IServer server)
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.server.core.model.IServerProviderDelegate#removeServer(com.aptana.ide.server.core.IServer)
	 */
	public void removeServer(IServer server)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.aptana.ide.server.core.model.IServerProviderDelegate#removeServerChangeListener(com.aptana.ide.server.core.IServerManagerListener)
	 */
	public void removeServerChangeListener(IServerManagerListener listener)
	{
	}

}
