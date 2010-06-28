/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.WeakHashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.help.internal.search.ProgressDistributor;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.ServerCore;
import com.aptana.ide.server.core.IAbstractConfiguration;
import com.aptana.ide.server.core.ILog;
import com.aptana.ide.server.core.IModule;
import com.aptana.ide.server.core.IModuleType;
import com.aptana.ide.server.core.IOperationListener;
import com.aptana.ide.server.core.IPublishOperation;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.IServerListener;
import com.aptana.ide.server.core.IServerType;
import com.aptana.ide.server.core.OperationCompletionEvent;
import com.aptana.ide.server.core.ServerEvent;
import com.aptana.ide.server.core.impl.modules.ModuleTypeRegistry;
import com.aptana.ide.server.internal.core.Messages;

/**
 * @author Pavel Petrochenko
 */
public abstract class AbstractServer implements IServer
{
	private static final String PUBLISH_ID = "publish"; //$NON-NLS-1$

	private static final String RECONFIGURE_ID = "reconfigure"; //$NON-NLS-1$

	/**
	 * @see com.aptana.ide.server.core.IServer#isExternal()
	 */
	public boolean isExternal()
	{
		return false;
	}

	private WeakHashMap<IProcess, Object> processes = new WeakHashMap<IProcess, Object>();

	private static final String RUNTIME_EXCEPTION_ERROR = "runtime exception during server job execution"; //$NON-NLS-1$
	/**
	 * SERVER_RESTART
	 */
	public static final String SERVER_RESTART = "serverRestart"; //$NON-NLS-1$
	/**
	 * SERVER_STOP
	 */
	public static final String SERVER_STOP = "serverStop"; //$NON-NLS-1$
	/**
	 * SERVER_START
	 */
	public static final String SERVER_START = "serverStart"; //$NON-NLS-1$

	/**
	 * SERVER_DOES_NOT_SUPPORTS_RESTARTING_IN_MODE
	 */
	protected static final String SERVER_DOES_NOT_SUPPORTS_RESTARTING_IN_MODE = Messages.AbstractServer_DOES_NOT_SUPPORTS_RESTART;

	/**
	 * SERVER_CAN_NOT_PUBLISH
	 */
	protected static final String SERVER_CAN_NOT_PUBLISH = Messages.AbstractServer_DOES_NOT_SUPPORTS_PUBLISH;

	/**
	 * SERVER_IS_NOT_RUNNING
	 */
	protected static final String SERVER_IS_NOT_RUNNING = Messages.AbstractServer_IS_NOT_RUNNING;

	/**
	 * SERVER_IS_RUNNING
	 */
	protected static final String SERVER_IS_RUNNING = Messages.AbstractServer_IS_RUNNG;

	/**
	 * SERVER_DOES_NOT_SUPPORTS_STARTING_IN_MODE
	 */
	public static final String SERVER_DOES_NOT_SUPPORTS_STARTING_IN_MODE = Messages.AbstractServer_DOES_NOT_SUPPORTS_START;
	private HashSet<IOperationListener> operationListeners = new HashSet<IOperationListener>();
	private HashSet<IServerListener> serverListeners = new HashSet<IServerListener>();

	private int state;
	private String mode;
	private String description;
	private ILaunch launch;
	private String assotiatedServers;
	private IServerType type;
	private String id;

	/**
	 * Log path
	 */
	protected IPath logPath;

	private String name;

	/**
	 * @see com.aptana.ide.server.core.IServer#getLaunch()
	 */
	public ILaunch getLaunch()
	{
		return launch;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#canDelete()
	 */
	public IStatus canDelete()
	{
		return Status.OK_STATUS;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#askStopBeforeDelete()
	 */
	public IStatus askStopBeforeDelete() {
		return Status.OK_STATUS;
	}
	
	/**
	 * @param launch
	 */
	protected void setLaunch(ILaunch launch)
	{
		this.launch = launch;
	}

	/**
	 * @param mode
	 */
	protected void setMode(String mode)
	{
		this.mode = mode;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getStreamsProxy()
	 */
	public IStreamsProxy getStreamsProxy()
	{
		if (launch == null)
		{
			return null;
		}
		IProcess[] processes = launch.getProcesses();
		if (processes.length == 0)
		{
			return null;
		}
		return processes[0].getStreamsProxy();
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getServerState()
	 */
	public int getServerState()
	{
		return state;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getName()
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 */
	protected void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @param state
	 */
	protected void setServerState(int state)
	{
		if (this.state != state)
		{
			this.state = state;
		}
	}

	private HashSet<IModule> configuredModules = new HashSet<IModule>();

	/**
	 * @param type
	 * @param state
	 * @param configuration
	 */
	public AbstractServer(IServerType type, int state, IAbstractConfiguration configuration)
	{
		this.type = type;
		this.state = state;
		this.id = ServerManager.getFreeId();
		installConfig(configuration);
	}

	/**
	 * @param type
	 * @param configuration
	 */
	public AbstractServer(IServerType type, IAbstractConfiguration configuration)
	{
		this.type = type;

		this.id = ServerManager.getFreeId();
		installConfig(configuration);
		setServerState(IServer.STATE_STOPPED);
	}

	/**
	 * call it when server changed
	 */
	protected void serverChanged()
	{
		((ServerManager) ServerCore.getServerManager()).serverChanged(this);
		fireListenersOnly();
	}

	/**
	 * 
	 */
	protected void fireListenersOnly()
	{
		ServerEvent serverEvent = new ServerEvent(this);
		for (IServerListener name : serverListeners)
		{
			name.serverChanged(serverEvent);
		}
	}

	/**
	 * @param event
	 */
	protected void operationExecuted(OperationCompletionEvent event)
	{
		for (IOperationListener ol : operationListeners)
		{
			ol.done(event);
		}
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getServerType()
	 */
	public IServerType getServerType()
	{
		return type;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getModules()
	 */
	public IModule[] getModules()
	{
		IModule[] mods = new IModule[this.configuredModules.size()];
		configuredModules.toArray(mods);
		return mods;
	}

	/**
	 * {@inheritDoc}
	 */
	public IPath getServerRoot()
	{
		// return null by default. Should be overridden by children that are able providing the root path.
		return null;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#addOperationListener(com.aptana.ide.server.core.IOperationListener)
	 */
	public synchronized void addOperationListener(IOperationListener listener)
	{
		if (listener == null)
		{
			throw new IllegalArgumentException("can not be null"); //$NON-NLS-1$
		}
		operationListeners.add(listener);
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#addServerListener(com.aptana.ide.server.core.IServerListener)
	 */
	public synchronized void addServerListener(IServerListener listener)
	{
		if (listener == null)
		{
			throw new IllegalArgumentException("can not be null"); //$NON-NLS-1$
		}
		serverListeners.add(listener);
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#removeOperationListener(com.aptana.ide.server.core.IServerListener)
	 */
	public synchronized void removeOperationListener(IServerListener listener)
	{
		operationListeners.remove(listener);
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#removeServerListener(com.aptana.ide.server.core.IServerListener)
	 */
	public synchronized void removeServerListener(IServerListener listener)
	{
		serverListeners.remove(listener);
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#canPublish()
	 */
	public synchronized IStatus canPublish()
	{
		return type.supportsPublish() ? Status.OK_STATUS : new Status(IStatus.ERROR, ServerCore.PLUGIN_ID,
				IStatus.ERROR, StringUtils.format(SERVER_CAN_NOT_PUBLISH, getName()), null);
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#canRestart(java.lang.String)
	 */
	public synchronized IStatus canRestart(String mode)
	{
		if (getServerState() == IServer.STATE_STARTING || getServerState() == IServer.STATE_STARTED)
		{
			return type.supportsRestart(mode) ? Status.OK_STATUS : new Status(IStatus.ERROR, ServerCore.PLUGIN_ID,
					IStatus.ERROR, StringUtils.format(SERVER_DOES_NOT_SUPPORTS_RESTARTING_IN_MODE, new Object[] {
							getName(), mode }), null);
		}
		else
		{
			return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, IStatus.ERROR, StringUtils.format(
					SERVER_IS_NOT_RUNNING, getName()), null);
		}
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#canStart(java.lang.String)
	 */
	public synchronized IStatus canStart(String launchMode)
	{
		if (getServerState() == IServer.STATE_STOPPED)
		{
			return type.supportsLaunchMode(launchMode) ? Status.OK_STATUS : new Status(IStatus.ERROR,
					ServerCore.PLUGIN_ID, IStatus.ERROR, StringUtils.format(SERVER_DOES_NOT_SUPPORTS_STARTING_IN_MODE,
							new Object[] { getName(), launchMode }), null);
		}
		else
		{
			return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, IStatus.ERROR, StringUtils.format(SERVER_IS_RUNNING,
					getName()), null);
		}
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getMode()
	 */
	public String getMode()
	{
		return mode;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#canStop()
	 */
	public synchronized IStatus canStop()
	{
		if (getServerState() == IServer.STATE_STARTING || getServerState() == IServer.STATE_STARTED)
		{
			return Status.OK_STATUS;
		}
		else
		{
			return new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, IStatus.ERROR, StringUtils.format(
					SERVER_IS_NOT_RUNNING, getName()), null);
		}
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#canModify()
	 */
	public IStatus canModify()
	{
		return Status.OK_STATUS;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#canModifyInStoppedStateOnly()
	 */
	public IStatus canModifyInStoppedStateOnly() {
		return Status.OK_STATUS;
	}
	
	/**
	 * @author Pavel Petrochenko
	 */
	public abstract static class ServerOperation
	{

		private final String id;

		private ProgressDistributor distributor = new ProgressDistributor();

		/**
		 * @param id
		 * @param mon
		 */
		public ServerOperation(String id, IProgressMonitor mon)
		{
			super();
			this.id = id;
			if (mon != null)
			{
				distributor.addMonitor(mon);
			}
		}

		/**
		 * @param server
		 * @param monitor
		 * @return - status
		 */
		IStatus execute(AbstractServer server, IProgressMonitor monitor)
		{
			if (monitor != null)
			{
				distributor.addMonitor(monitor);
			}
			IStatus internalExecute = internalExecute(server, distributor);
			server.serverChanged();
			distributor.done();
			return internalExecute;
		}

		/**
		 * @param server
		 * @param monitor
		 * @return - status
		 */
		protected abstract IStatus internalExecute(IServer server, IProgressMonitor monitor);

		/**
		 * @return - id
		 */
		String getId()
		{
			return id;
		}
	}

	private ISchedulingRule rule = new ISchedulingRule()
	{

		public boolean contains(ISchedulingRule rule)
		{
			return rule == this;
		}

		public boolean isConflicting(ISchedulingRule rule)
		{
			return this == rule;
		}

	};

	/**
	 * @return - scheduling rule
	 */
	public ISchedulingRule getServerOwnerRule()
	{
		return rule;
	}

	/**
	 * executes given operation as a job and notifies listeners if needed
	 * 
	 * @param operation
	 * @param listener
	 */
	protected void executeOperation(final ServerOperation operation, final IOperationListener listener)
	{
		Job runnable = new Job(getLabel(operation.getId()))
		{

			protected IStatus run(IProgressMonitor monitor)
			{
				try
				{
					try
					{
						Job.getJobManager().beginRule(rule, monitor);
						IStatus execute = operation.execute(AbstractServer.this, monitor);

						OperationCompletionEvent operationCompletionEvent = new OperationCompletionEvent(
								AbstractServer.this, operation.getId(), execute);

						operationExecuted(operationCompletionEvent);
						if (listener != null)
						{
							listener.done(operationCompletionEvent);
						}
						return execute;
					}
					catch (Exception e)
					{
						Status es = new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, IStatus.ERROR,
								RUNTIME_EXCEPTION_ERROR, e);
						IdeLog.logError(ServerCore.getDefault(), RUNTIME_EXCEPTION_ERROR, e);
						OperationCompletionEvent operationCompletionEvent = new OperationCompletionEvent(
								AbstractServer.this, operation.getId(), es);
						if (listener != null)
						{
							listener.done(operationCompletionEvent);
						}
						return es;
					}
				}
				finally
				{
					Job.getJobManager().endRule(rule);
				}
			}

		};
		runnable.schedule();
	}

	/**
	 * @param operationId
	 * @return - label
	 */
	protected String getLabel(String operationId)
	{
		if (operationId.equals(SERVER_RESTART))
		{
			return StringUtils.format(Messages.AbstractServer_RESTART_OP_LABEL, getName());
		}
		if (operationId.equals(SERVER_START))
		{
			return StringUtils.format(Messages.AbstractServer_START_OP_LABEL, getName());
		}
		if (operationId.equals(SERVER_STOP))
		{
			return StringUtils.format(Messages.AbstractServer_STOP_OP_LABEL, getName());
		}
		if (operationId.equals(PUBLISH_ID))
		{
			return StringUtils.format(Messages.AbstractServer_PUBLISH_ON_OP_LABEL, getName());
		}
		if (operationId.equals(RECONFIGURE_ID))
		{
			return StringUtils.format(Messages.AbstractServer_RECONFIGURE_OP_LABEL, getName());
		}
		else
		{
			return Messages.AbstractServer_LBL_Unknown;
		}
	}

	/**
	 * actually restarts server
	 * 
	 * @param mode
	 * @param monitor
	 * @return status
	 */
	protected abstract IStatus restart(String mode, IProgressMonitor monitor);

	/**
	 * actually starts server
	 * 
	 * @param mode
	 * @param monitor
	 * @return - status
	 */
	protected abstract IStatus start(String mode, IProgressMonitor monitor);

	/**
	 * actually stops server
	 * 
	 * @param force
	 * @param monitor
	 * @return - status
	 */
	protected abstract IStatus stop(boolean force, IProgressMonitor monitor);

	/**
	 * @see com.aptana.ide.server.core.IServer#start(java.lang.String, com.aptana.ide.server.core.IOperationListener,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void start(final String mode, IOperationListener listener, final IProgressMonitor monitor)
	{
		IStatus canStart = canStart(mode);
		if (canStart.getSeverity() != IStatus.OK)
		{
			if (listener != null)
			{
				listener.done(new OperationCompletionEvent(this, SERVER_START, canStart));
			}
			return;
		}
		executeOperation(new ServerOperation(SERVER_START, monitor)
		{

			protected IStatus internalExecute(IServer server, IProgressMonitor monitor)
			{
				return start(mode, monitor);
			}

		}, listener);
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#stop(boolean, com.aptana.ide.server.core.IOperationListener,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void stop(final boolean force, IOperationListener listener, IProgressMonitor monitor)
	{
		IStatus canStart = canStop();
		if (canStart.getSeverity() != IStatus.OK)
		{
			if (listener != null)
			{
				listener.done(new OperationCompletionEvent(this, SERVER_STOP, canStart));
			}
			return;
		}
		executeOperation(new ServerOperation(SERVER_STOP, monitor)
		{

			protected IStatus internalExecute(IServer server, IProgressMonitor monitor)
			{
				return stop(force, monitor);
			}

		}, listener);
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#restart(java.lang.String, com.aptana.ide.server.core.IOperationListener,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void restart(final String mode, IOperationListener listener, IProgressMonitor monitor)
	{
		IStatus canStart = canRestart(mode);
		if (canStart.getSeverity() != IStatus.OK)
		{
			if (listener != null)
			{
				listener.done(new OperationCompletionEvent(this, SERVER_RESTART, canStart));
			}
			return;
		}
		executeOperation(new ServerOperation(SERVER_RESTART, monitor)
		{

			protected IStatus internalExecute(IServer server, IProgressMonitor monitor)
			{
				return restart(mode, monitor);
			}

		}, listener);
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#publish(int, com.aptana.ide.server.core.IModule[],
	 *      com.aptana.ide.server.core.IOperationListener, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void publish(final int kind, final IModule[] modules, IOperationListener listener, IProgressMonitor monitor)
	{
		executeOperation(new ServerOperation(PUBLISH_ID, null)
		{

			protected IStatus internalExecute(IServer server, IProgressMonitor monitor)
			{
				for (int a = 0; a < modules.length; a++)
				{
					if (monitor.isCanceled())
					{
						return Status.OK_STATUS;
					}
					String[] publishOperationIds = modules[a].getPublishOperationIds();
					try
					{
						IPublishOperation[] operations = PublishOperationRegistry.getOperationRegistry().getOperations(
								publishOperationIds);
						for (int b = 0; b < operations.length; b++)
						{
							if (monitor.isCanceled())
							{
								return Status.OK_STATUS;
							}
							IStatus performPublish = operations[b].performPublish(AbstractServer.this, kind, modules,
									monitor);
							if (performPublish.getSeverity() == IStatus.ERROR)
							{
								return performPublish;
							}
						}
					}
					catch (CoreException e)
					{
						return e.getStatus();
					}
				}
				return Status.OK_STATUS;
			}

		}, null);
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#reconfigure(com.aptana.ide.server.core.IAbstractConfiguration)
	 */
	public void reconfigure(final IAbstractConfiguration configuration) throws CoreException
	{
		final Object sync = new Object();
		IOperationListener operationListener = new IOperationListener()
		{

			private boolean isDone = false;

			public synchronized void done(OperationCompletionEvent operation)
			{
				isDone = true;
				synchronized (sync)
				{
					sync.notify();
				}
			}

			public synchronized boolean isDone()
			{
				return isDone;
			}

		};

		executeOperation(new ServerOperation(RECONFIGURE_ID, null)
		{

			protected IStatus internalExecute(IServer server, IProgressMonitor monitor)
			{
				installConfig(configuration);
				flushConfig();

				return Status.OK_STATUS;
			}

		}, operationListener);

		if (!operationListener.isDone())
		{
			synchronized (sync)
			{
				try
				{
					sync.wait();
				}
				catch (InterruptedException e)
				{
					throw new CoreException(new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, IStatus.ERROR, e
							.getMessage(), e));
				}
			}
		}
	}

	/**
	 * configures server from fiven configuration object
	 * 
	 * @param configuration
	 */
	protected void installConfig(IAbstractConfiguration configuration)
	{
		this.configuredModules.clear();
		String newId = configuration.getStringAttribute(IServer.KEY_ID);
		if (newId != null && newId.trim().length() > 0)
			this.id = newId;
		this.name = configuration.getStringAttribute(IServer.KEY_NAME);
		this.description = configuration.getStringAttribute(IServer.KEY_DESCRIPTION);
		this.assotiatedServers = configuration.getStringAttribute(IServer.KEY_ASSOCIATION_SERVER_ID);
		IAbstractConfiguration subConfiguration = configuration.createSubConfiguration("modules"); //$NON-NLS-1$
		int intAttribute = subConfiguration.getIntAttribute("count"); //$NON-NLS-1$
		String ps = configuration.getStringAttribute(IServer.KEY_LOG_PATH);
		if (ps != null && ps.length() > 0)
		{
			logPath = new Path(ps);
		}
		else
		{
			logPath = null;
		}
		for (int a = 0; a < intAttribute; a++)
		{
			IAbstractConfiguration moduleConfiguration = subConfiguration.createSubConfiguration(Integer.toString(a));
			String module = moduleConfiguration.getStringAttribute(IServer.KEY_TYPE);
			IModuleType moduleType = ModuleTypeRegistry.getInstance().getModuleType(module);
			IModule createModule = moduleType.createModule(moduleConfiguration);
			this.configuredModules.add(createModule);
		}
	}

	/**
	 * stores current server configuration to a given config
	 * 
	 * @param config
	 */
	public void storeConfiguration(IAbstractConfiguration config)
	{
		config.setStringAttribute(IServer.KEY_ID, id);
		config.setStringAttribute(IServer.KEY_NAME, name);
		config.setStringAttribute(IServer.KEY_LOG_PATH, logPath == null ? "" : logPath.toString()); //$NON-NLS-1$
		if (description != null && description.length() > 0)
		{
			config.setStringAttribute(IServer.KEY_DESCRIPTION, description);
		}
		else
		{
			config.setStringAttribute(IServer.KEY_DESCRIPTION, getConfigurationDescription());
		}
		IAbstractConfiguration conf = config.createSubConfiguration("modules"); //$NON-NLS-1$
		int a = 0;
		for (IModule module : configuredModules)
		{
			IAbstractConfiguration subConfiguration = conf.createSubConfiguration(Integer.toString(a));
			module.storeConfig(subConfiguration);
			subConfiguration.setStringAttribute(IServer.KEY_TYPE, module.getType().getId());
			a++;
		}
		conf.setIntAttribute("count", a); //$NON-NLS-1$
	}

	/**
	 * does nothing currently
	 */
	protected void flushConfig()
	{

	}

	/**
	 * @see com.aptana.ide.server.core.IServer#unconfigureModule(com.aptana.ide.server.core.IModule,
	 *      com.aptana.ide.server.core.IOperationListener, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public final void unconfigureModule(final IModule module, IOperationListener listener, IProgressMonitor monitor)
	{
		executeOperation(new ServerOperation("moduleUnconfigure", monitor) //$NON-NLS-1$
				{

					protected IStatus internalExecute(IServer server, IProgressMonitor monitor)
					{
						configuredModules.remove(module);
						flushConfig();
						return Status.OK_STATUS;
					}

				}, listener);
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#configureModule(com.aptana.ide.server.core.IModule,
	 *      com.aptana.ide.server.core.IOperationListener, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void configureModule(final IModule module, IOperationListener listener, IProgressMonitor monitor)
	{
		executeOperation(new ServerOperation("moduleConfigure", monitor) //$NON-NLS-1$
				{

					protected IStatus internalExecute(IServer server, IProgressMonitor monitor)
					{
						configuredModules.add(module);
						flushConfig();
						return Status.OK_STATUS;
					}

				}, listener);
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#isConfigured(com.aptana.ide.server.core.IModule)
	 */
	public boolean isConfigured(IModule module)
	{
		return configuredModules.contains(module);
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getId()
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter)
	{
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#configureModule(com.aptana.ide.server.core.IAbstractConfiguration,
	 *      com.aptana.ide.server.core.IOperationListener, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void configureModule(IAbstractConfiguration config, IOperationListener listener, IProgressMonitor monitor)
	{
		IModuleType moduleType = ModuleTypeRegistry.getInstance().getModuleType(
				config.getStringAttribute(IServer.KEY_TYPE));
		configureModule(moduleType.createModule(config), listener, monitor);
	}

	/**
	 * @return processes
	 * @see com.aptana.ide.server.core.impl.servers.AbstractServer#getProcesses()
	 */
	public IProcess[] getProcesses()
	{
		IProcess[] prs = new IProcess[processes.size()];
		return processes.keySet().toArray(prs);
	}

	/**
	 * registers process as one of processes that belongs to a given server
	 * 
	 * @param process
	 */
	public void registerProcess(IProcess process)
	{
		processes.put(process, ""); //$NON-NLS-1$
		serverChanged();
	}

	/**
	 * @return false by default
	 */
	public boolean isWebServer()
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#isTransient()
	 */
	public boolean isTransient()
	{
		return false;
	}

	/**
	 * @see IServer#getAssociatedServers()
	 */
	public IServer[] getAssociatedServers()
	{
		ArrayList<IServer> result = new ArrayList<IServer>();
		for (IServer s : ServerCore.getServerManager().getServers())
		{
			if (s == null || s.getId() == null || s.getId().trim().length() == 0)
				continue;
			if (assotiatedServers.contains(s.getId()))
			{
				result.add(s);
			}
		}
		IServer[] resultA = new IServer[result.size()];
		result.toArray(resultA);
		return resultA;
	}

	/**
	 * @return user editable description of the server
	 */
	public String getDescription()
	{
		// this is questionable
		// if (description==null||description.length()==0){
		// return getConfigurationDescription();
		// }
		return description;
	}

	/**
	 * @return description of the server configuration
	 */
	public abstract String getConfigurationDescription();

	/**
	 * @see com.aptana.ide.server.core.IServer#suppliesStatistics()
	 */
	public boolean suppliesStatistics()
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#fetchStatistics()
	 */
	public String fetchStatistics()
	{
		return StringUtils.EMPTY;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#showStatisticsInterface()
	 */
	public void showStatisticsInterface()
	{
		// Does nothing by default
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#suppliesStatisticsInterface()
	 */
	public boolean suppliesStatisticsInterface()
	{
		return false;
	}

	/**
	 * Sets the log file location for a server
	 * 
	 * @param logFileURI
	 */
	public void setLogFilePath(IPath logFileURI)
	{
		this.logPath = logFileURI;
	}

	/**
	 * Gets the log file path
	 * 
	 * @return - log file path
	 */
	public IPath getLogFilePath()
	{
		if (logPath == null || logPath.isEmpty())
		{
			this.logPath = getDefaultLogPath();

		}
		return this.logPath;
	}

	/**
	 * @return default log location for this server
	 */
	protected IPath getDefaultLogPath()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getAllLogs()
	 */
	public ILog[] getAllLogs()
	{
		return new ILog[] { getLog() };
	}

	/**
	 * @see com.aptana.ide.server.core.IServer#getLog()
	 */
	public ILog getLog()
	{

		return new ILog()
		{

			public URI getURI()
			{
				if (logPath == null)
				{
					logPath = getLogFilePath();
					if (logPath == null)
					{
						return null;
					}
				}
				return logPath.toFile().toURI();
			}

			public boolean exists()
			{
				if (logPath == null)
				{
					logPath = getLogFilePath();
				}
				if (logPath != null)
				{
					File file = logPath.toFile();
					return file.exists();
				}
				return false;
			}

		};
	}

}
