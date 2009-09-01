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
package com.aptana.ide.server.configuration.ui;

import com.aptana.ide.server.core.IAbstractConfiguration;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.internal.ApacheServerTypeDelegate;

/**
 * @author Pavel Petrochenko
 *
 */
public class ApacheServerCompositeUpdater
{

	private IAbstractConfiguration configuration;
	private ApacheServerComposite apacheServerComposite;
	
	
	/**
	 * @param configuration
	 * @param apacheServerComposite
	 */
	public ApacheServerCompositeUpdater(IAbstractConfiguration configuration,
			ApacheServerComposite apacheServerComposite)
	{
		super();
		this.configuration = configuration;
		this.apacheServerComposite = apacheServerComposite;
	}

	/**
	 * 
	 */
	public void updateServer()
	{
		setServerName(apacheServerComposite.getServerName());
		setServerPath(apacheServerComposite.getServerPath());
		setServerDescription(apacheServerComposite.getServerDescription());
		setStopApache(apacheServerComposite.getApacheStop());
		setStartApache(apacheServerComposite.getApacheStart());
		setRestartApache(apacheServerComposite.getApacheRestart());
		setDocumentRoot(apacheServerComposite.getDocumentRoot());
		setEtcHosts(apacheServerComposite.getEtcHostsPath());
		getConfiguration().setStringAttribute(ApacheServerTypeDelegate.HOSTNAME, apacheServerComposite.getApacheHost());
		
	}
	
	/**
	 * @see com.aptana.ide.server.configuration.ui.ServerDialog#updateData()
	 */
	public void updateData()
	{
		this.apacheServerComposite.setServerPath(getConfiguration().getStringAttribute(IServer.KEY_PATH));
		this.apacheServerComposite.setServerName(getConfiguration().getStringAttribute(IServer.KEY_NAME));
		this.apacheServerComposite.setServerDescription(getConfiguration().getStringAttribute(IServer.KEY_DESCRIPTION));		
		apacheServerComposite.setApacheStart(getStartApache());		
		apacheServerComposite.setApacheRestart(getRestartApache());
		apacheServerComposite.setApacheStop((getStopApache()));
		apacheServerComposite.setDocumentRoot(getDocumentRoot());
		apacheServerComposite.setEtcHostsPath(getEtcHosts());
		apacheServerComposite.setApacheHost(getConfiguration().getStringAttribute(ApacheServerTypeDelegate.HOSTNAME));
	}

	/**
	 * @return configuration
	 */
	public IAbstractConfiguration getConfiguration()
	{
		return configuration;
	}

	private void setEtcHosts(String etcHostsPath)
	{
		getConfiguration().setStringAttribute(ApacheServerTypeDelegate.ETCHOSTS, etcHostsPath);
	}

	private void setDocumentRoot(String root)
	{
		getConfiguration().setStringAttribute(IServer.KEY_DOCUMENT_ROOT, root);
	}

	private void setRestartApache(String apacheRestart)
	{
		getConfiguration().setStringAttribute(ApacheServerTypeDelegate.RESTARTAPACHE, apacheRestart);
	}

	private void setStartApache(String apacheStart)
	{
		getConfiguration().setStringAttribute(ApacheServerTypeDelegate.STARTAPACHE, apacheStart);
	}
	
	private void setStopApache(String apacheStop)
	{
		getConfiguration().setStringAttribute(ApacheServerTypeDelegate.STOPAPACHE, apacheStop);
	}
	
	private void setServerDescription(String serverDescription)
	{
		getConfiguration().setStringAttribute(IServer.KEY_DESCRIPTION, serverDescription);
	}

	private void setServerPath(String serverPath)
	{
		getConfiguration().setStringAttribute(IServer.KEY_PATH, serverPath);
	}

	private void setServerName(String serverName)
	{
		getConfiguration().setStringAttribute(IServer.KEY_NAME, serverName);
	}
	
	private String getEtcHosts()
	{
		return getConfiguration().getStringAttribute(ApacheServerTypeDelegate.ETCHOSTS);
	}

	private String getDocumentRoot()
	{
		return getConfiguration().getStringAttribute(IServer.KEY_DOCUMENT_ROOT);
	}

	private String getStopApache()
	{
		return getConfiguration().getStringAttribute(ApacheServerTypeDelegate.STOPAPACHE);
	}

	private String getRestartApache()
	{
		return getConfiguration().getStringAttribute(ApacheServerTypeDelegate.RESTARTAPACHE);
	}

	private String getStartApache()
	{
		return getConfiguration().getStringAttribute(ApacheServerTypeDelegate.STARTAPACHE);
	}
}
