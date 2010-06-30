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
package com.aptana.ide.server.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchManager;

import com.aptana.ide.server.core.IModule;
import com.aptana.ide.server.core.IModuleType;
import com.aptana.ide.server.core.IOperationListener;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.IServerManagerListener;
import com.aptana.ide.server.core.OperationCompletionEvent;
import com.aptana.ide.server.core.ServerManagerEvent;
import com.aptana.ide.server.core.impl.Configuration;
import com.aptana.ide.server.core.impl.modules.DefaultModule;
import com.aptana.ide.server.core.impl.servers.ServerManager;

import junit.framework.TestCase;

/**
 * @author Pavel Petrochenko
 */
public class ServersTest extends TestCase
{

	private final class WaitOp implements IOperationListener
	{
		private final List<OperationCompletionEvent> cm;

		private WaitOp(List<OperationCompletionEvent> cm)
		{
			this.cm = cm;
		}

		/**
		 * @see com.aptana.ide.server.core.IOperationListener#done(com.aptana.ide.server.core.OperationCompletionEvent)
		 */
		public void done(OperationCompletionEvent operation)
		{
			cm.add(operation);
		}

		/**
		 * @see com.aptana.ide.server.core.IOperationListener#isDone()
		 */
		public boolean isDone()
		{
			return false;
		}
	}

	/**
	 * test server creation and remove
	 */
	public void test0()
	{
		ServerManager.getInstance().clearAll();
		Configuration configuration = new Configuration();
		configuration.setStringAttribute(IServer.KEY_TYPE, TestServer.KEY_TYPE);
		configuration.setStringAttribute(IServer.KEY_ID, TestServer.KEY_ID);
		final List<ServerManagerEvent> events = new ArrayList<ServerManagerEvent>();
		ServerManager.getInstance().addServerManagerListener(new IServerManagerListener()
		{

			public void serversChanged(ServerManagerEvent event)
			{
				if(event.getServer().getId().equals(TestServer.KEY_ID))
				{
					events.add(event);
				}
			}

		});

		// add server
		int numServers = ServerManager.getInstance().getServers().length;

		IServer addServer = null;
		try
		{
			addServer = ServerManager.getInstance().addServer(configuration);
		}
		catch (CoreException e)
		{
			fail("Unable to get instance of ServerManager");
		}

		// ensure that the serversChanged event was only fired once
		assertEquals(1, events.size());
		
		// is the added server a "TestServer"?
		assertTrue(addServer instanceof TestServer);
		ServerManager.getInstance().reload();
		IServer[] servers = ServerManager.getInstance().getServers();
		assertEquals(numServers + 1, servers.length);

		// find TestServer in list
		IServer server = ServerManager.getInstance().findServer(TestServer.KEY_ID);
		assertNotNull("Unable to find TestServer in list after reload", server);

		// remove server
		ServerManager.getInstance().removeServer(servers[0]);
		ServerManager.getInstance().reload();
		servers = ServerManager.getInstance().getServers();
		assertEquals(2, events.size());
		assertEquals(numServers, servers.length);
	}

	/**
	 * test module creation and remove
	 */
	public void test1()
	{
		ServerManager.getInstance().clearAll();
		Configuration configuration = new Configuration();
		configuration.setStringAttribute(IServer.KEY_TYPE, TestServer.KEY_TYPE);
		configuration.setStringAttribute(IServer.KEY_ID, TestServer.KEY_ID);
		final List<ServerManagerEvent> events = new ArrayList<ServerManagerEvent>();
		final List<OperationCompletionEvent> cm = new ArrayList<OperationCompletionEvent>();
		ServerManager.getInstance().addServerManagerListener(new IServerManagerListener()
		{
			public void serversChanged(ServerManagerEvent event)
			{
				if(event.getServer().getId().equals(TestServer.KEY_ID))
				{
					events.add(event);
				}
			}
		});

		// add server
		int numServers = ServerManager.getInstance().getServers().length;

		IServer addServer = null;
		try
		{
			addServer = ServerManager.getInstance().addServer(configuration);
		}
		catch (CoreException e)
		{
			fail("Unable to get instance of ServerManager");
		}
		
		configuration = new Configuration();
		configuration.setStringAttribute(IServer.KEY_TYPE, "com.aptana.ide.server.tests.testModuleType"); //$NON-NLS-1$
		configuration.setStringAttribute(IServer.KEY_ID, "com.aptana.ide.server.tests.testType1"); //$NON-NLS-1$
		IOperationListener operationListener = new WaitOp(cm);
		addServer.configureModule(configuration, operationListener, null);
		waitCompletion(cm);
		assertEquals(2, events.size());
		assertEquals(1, cm.size());
		assertEquals(IStatus.OK, ((OperationCompletionEvent) cm.get(0)).getStatus().getSeverity());
		assertTrue(addServer instanceof TestServer);
		ServerManager.getInstance().reload();

		// we should have the same number of servers as before
		IServer[] servers = ServerManager.getInstance().getServers();		
		assertEquals(numServers + 1, servers.length);				
		
		// ensure the list contains the new server
		IServer server = ServerManager.getInstance().findServer(TestServer.KEY_ID);
		assertNotNull("Unable to find TestServer in list after reload", server);
		
		IModule[] modules = server.getModules();
		assertEquals(1, modules.length);
		assertTrue(modules[0] instanceof DefaultModule);
		IModuleType type = modules[0].getType();
		cm.clear();
		server.unconfigureModule(modules[0], operationListener, null);
		waitCompletion(cm);
		
		// reloading the servers reverts back to the default configuration
		ServerManager.getInstance().reload();

		// we should have the same number of servers as before
		servers = ServerManager.getInstance().getServers();		
		assertEquals(numServers + 1, servers.length);				

		// ensure the list contains the new server
		server = ServerManager.getInstance().findServer(TestServer.KEY_ID);
		assertNotNull("Unable to find TestServer in list after reload", server);

		modules = server.getModules();
		assertEquals(0, modules.length);
		assertEquals("com.aptana.ide.server.tests.testModuleType", type.getId()); //$NON-NLS-1$
		ServerManager.getInstance().removeServer(servers[0]);
		ServerManager.getInstance().reload();
		servers = ServerManager.getInstance().getServers();
		assertEquals(4, events.size());
		assertEquals(numServers, servers.length);
	}

	private void waitCompletion(final List<?> cm)
	{
		for (int a = 0; a < 100; a++)
		{
			if (cm.isEmpty())
			{
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					assertTrue(false);
				}
			}
			else
			{
				break;
			}
		}
	}

	/**
	 * test server start and stop
	 */
	public void test2()
	{
		ServerManager.getInstance().clearAll();
		Configuration configuration = new Configuration();
		configuration.setStringAttribute(IServer.KEY_TYPE, TestServer.KEY_TYPE);
		configuration.setStringAttribute(IServer.KEY_ID, TestServer.KEY_ID);
		final List<ServerManagerEvent> events = new ArrayList<ServerManagerEvent>();
		ServerManager.getInstance().addServerManagerListener(new IServerManagerListener()
		{

			public void serversChanged(ServerManagerEvent event)
			{
				if(event.getServer().getId().equals(TestServer.KEY_ID))
				{
					events.add(event);
				}
			}

		});

		// add server
		int numServers = ServerManager.getInstance().getServers().length;

		IServer addServer = null;
		try
		{
			addServer = ServerManager.getInstance().addServer(configuration);
		}
		catch (CoreException e)
		{
			fail("Unable to get instance of ServerManager");
		}

		assertEquals(1, events.size());
		assertTrue(addServer instanceof TestServer);
		ServerManager.getInstance().reload();
		IServer[] servers = ServerManager.getInstance().getServers();
		assertEquals(numServers + 1, servers.length);

		// ensure the list contains the new server
		IServer server = ServerManager.getInstance().findServer(TestServer.KEY_ID);
		assertNotNull("Unable to find TestServer in list after reload", server);

		List<OperationCompletionEvent> list = new ArrayList<OperationCompletionEvent>();
		server.start(ILaunchManager.RUN_MODE, new WaitOp(list), null);
		waitCompletion(list);
		assertEquals(IStatus.OK, server.canStop().getSeverity());
		assertEquals(IStatus.ERROR, server.canStart(ILaunchManager.RUN_MODE).getSeverity());
		list.clear();
		assertEquals(server.getServerState(), IServer.STATE_STARTED);
		server.start(ILaunchManager.RUN_MODE, new WaitOp(list), null);
		waitCompletion(list);
		list.clear();

		// stop the server
		server.stop(true, new WaitOp(list), null);
		waitCompletion(list);
		OperationCompletionEvent object = (OperationCompletionEvent) list.get(0);
		assertEquals(IStatus.OK, object.getStatus().getSeverity());
		list = new ArrayList<OperationCompletionEvent>();
		assertEquals(IServer.STATE_STOPPED, server.getServerState());
		
		// check to make sure the server can start
		IStatus canStart = server.canStart(ILaunchManager.RUN_MODE);
		assertEquals(IStatus.OK, canStart.getSeverity());

		// restart the server
		server.restart(ILaunchManager.RUN_MODE, new WaitOp(list), null);
		waitCompletion(list);
		object = (OperationCompletionEvent) list.get(list.size() - 1);
		assertEquals(IStatus.ERROR, object.getStatus().getSeverity());
		canStart = server.canStart(ILaunchManager.RUN_MODE);
		assertEquals(IStatus.OK, canStart.getSeverity());
		assertEquals(IServer.STATE_STOPPED, server.getServerState());
		list.clear();

		// start the server
		server.start(ILaunchManager.RUN_MODE, new WaitOp(list), null);
		waitCompletion(list);
		list.clear();
		
		// restart the server
		server.restart(ILaunchManager.RUN_MODE, new WaitOp(list), null);
		waitCompletion(list);
		object = (OperationCompletionEvent) list.get(list.size() - 1);
		assertEquals(IStatus.ERROR, object.getStatus().getSeverity());
		
		// remove the server. Ensure it's removed correctly
		ServerManager.getInstance().removeServer(server);
		ServerManager.getInstance().reload();
		servers = ServerManager.getInstance().getServers();
		assertEquals(5, events.size());
		assertEquals(numServers, servers.length);
	}
}
