package com.aptana.ide.server.jetty;

import org.eclipse.core.runtime.CoreException;

import com.aptana.ide.server.core.IAbstractConfiguration;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.IServerType;
import com.aptana.ide.server.core.model.IServerTypeDelegate;

/**
 * @author Pavel Petrochenko
 */
public class JettyServerTypeDelegate implements IServerTypeDelegate
{
	
	/**
	 * ID
	 */
	public static final String ID = "com.aptana.ide.server.jetty.jettyHttpServer"; //$NON-NLS-1$


	/**
	 * KEY_SERVERID
	 */
	public static final String KEY_SERVERID = "serverId"; //$NON-NLS-1$

	/**
	 * 
	 */
	public JettyServerTypeDelegate()
	{
	}

	/**
	 * @see com.aptana.ide.server.core.model.IServerTypeDelegate#createServer(com.aptana.ide.server.core.IAbstractConfiguration,
	 *      com.aptana.ide.server.core.IServerType)
	 */
	public IServer createServer(IAbstractConfiguration configuration, IServerType type) throws CoreException
	{
		return new JettyServer(type, configuration);
	}

}
