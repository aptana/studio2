package com.aptana.ide.server.tests;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.ide.server.ServerCore;
import com.aptana.ide.server.core.IAbstractConfiguration;
import com.aptana.ide.server.core.IModule;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.IServerType;
import com.aptana.ide.server.core.impl.servers.AbstractServer;

/**
 * @author Pavel Petrochenko
 *
 */
public class TestServer extends AbstractServer implements IServer
{
	public static String KEY_TYPE = "com.aptana.ide.server.tests.testType";
	public static String KEY_ID = "com.aptana.ide.server.tests.testType0";
	
	/**
	 * @param type
	 * @param state
	 * @param configuration 
	 */
	public TestServer(IServerType type, int state, IAbstractConfiguration configuration)
	{
		super(type, state, configuration);
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#restart(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus restart(String mode, IProgressMonitor monitor)
	{
		throw new IllegalArgumentException();
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#start(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus start(String mode, IProgressMonitor monitor)
	{
		setServerState(IServer.STATE_STARTED);
		return Status.OK_STATUS;
	}

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#stop(boolean, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IStatus stop(boolean force, IProgressMonitor monitor)
	{
		setServerState(IServer.STATE_STOPPED);
		return Status.OK_STATUS;		
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#canHaveModule(com.aptana.ide.server.core.IModule)
	 */
	public IStatus canHaveModule(IModule module)
	{
		return new Status(IStatus.ERROR,ServerCore.PLUGIN_ID,1,"can not have modules",null); //$NON-NLS-1$
	}

	

	
	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#getConfigurationDescription()
	 */
	public String getConfigurationDescription()
	{
		return ""; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getHost()
	 */
	public String getHost()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getHostname()
	 */
	public String getHostname()
	{
		throw new UnsupportedOperationException();		
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getDocumentRoot()
	 */
	public IPath getDocumentRoot()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getPort()
	 */
	public int getPort()
	{
		return 0;
	}

	

	/**
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#fetchStatistics()
	 */
	public String fetchStatistics()
	{
		return null;
	}

}
