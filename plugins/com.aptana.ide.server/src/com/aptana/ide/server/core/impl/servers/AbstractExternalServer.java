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
package com.aptana.ide.server.core.impl.servers;

import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import com.aptana.ide.server.ServerCore;
import com.aptana.ide.server.core.IAbstractConfiguration;
import com.aptana.ide.server.core.IModule;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.IServerType;
import com.aptana.ide.server.core.ServerPathUtils;

/**
 * @author Pavel Petrochenko
 */
public abstract class AbstractExternalServer extends AbstractServer
{

	/**
	 * @see com.aptana.ide.server.core.IServer#isExternal()
	 */
	public boolean isExternal()
	{
		return true;
	}
	
	/**
	 * properties of this server;
	 */
	private Properties properties;

	private String path;

	private Path documentRoot;

	/**
	 * @return path
	 */
	public String getPath()
	{
		return ServerPathUtils.getFileNameByPathWithParameters(path);
	}
	
	/**
	 * Gets path parameters.
	 * @return path - path.
	 */
	public String[] getPathParameters()
	{
		return ServerPathUtils.getParameters(path);
	}

	/**
	 * @param path
	 */
	public void setPath(String path)
	{
		this.path = path;
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#reconfigure(com.aptana.ide.server.core.IAbstractConfiguration)
	 */
	@Override
	public void reconfigure(IAbstractConfiguration configuration) throws CoreException
	{
		properties = null;
		super.reconfigure(configuration);
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#installConfig(com.aptana.ide.server.core.IAbstractConfiguration)
	 */
	protected void installConfig(IAbstractConfiguration configuration)
	{
		super.installConfig(configuration);
		super.setName(configuration.getStringAttribute(IServer.KEY_NAME));
		setPath(configuration.getStringAttribute(IServer.KEY_PATH));
		properties = null;
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#storeConfiguration(com.aptana.ide.server.core.IAbstractConfiguration)
	 */
	public void storeConfiguration(IAbstractConfiguration config)
	{
		config.setStringAttribute(IServer.KEY_NAME, getName());
		config.setStringAttribute(IServer.KEY_PATH, getPath());
		super.storeConfiguration(config);
	}

	/**
	 * @param type
	 * @param configuration
	 */
	public AbstractExternalServer(IServerType type, IAbstractConfiguration configuration)
	{
		super(type, configuration);
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#restart(java.lang.String,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus restart(String mode, IProgressMonitor monitor)
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#start(java.lang.String,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus start(String mode, IProgressMonitor monitor)
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#stop(boolean,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus stop(boolean force, IProgressMonitor monitor)
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#canHaveModule(com.aptana.ide.server.core.IModule)
	 */
	public IStatus canHaveModule(IModule module)
	{
		return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, IStatus.ERROR, "modules not supported yet", null); //$NON-NLS-1$
	}


	/**
	 * @see com.aptana.ide.server.core.IServer#fetchStatistics()
	 */
	public String fetchStatistics()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getDocumentRoot()
	 */
	public IPath getDocumentRoot()
	{
		return documentRoot;
	}
	
	/**
	 * Returns the String representation of the document root, or an empty string in case the 
	 * document root was not set.
	 */
	public String getDocumentRootStr()
	{
		if(documentRoot != null)
		{
			return documentRoot.toOSString();
		}
		return ""; // $NON-NLS-1$ //$NON-NLS-1$
	}


	/**
	 * @param root The document root
	 */
	public void setDocumentRoot(String root)
	{
		if (root == null || root.trim().length() == 0)
		{
			documentRoot = null;
		}
		else
		{
			documentRoot = new Path(root);
		}
	}
	
	/**
	 * 
	 */
	protected void checkProperties()
	{
		if (properties == null)
		{
			properties = loadProperties();
		}
	}

	

	/**
	 * loads server properties
	 * 
	 * @return properties
	 */
	protected abstract Properties loadProperties();

	/**
	 * @return server configuration
	 */
	public Properties getProperties()
	{
		checkProperties();
		return properties;
	}
}
