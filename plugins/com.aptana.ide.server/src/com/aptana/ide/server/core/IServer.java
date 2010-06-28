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
package com.aptana.ide.server.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;

/**
 * Represents a server instance. Every server is an instance of particular fixed server type. <code>IServer</code>
 * object is a proxy for the real web server. Through this proxy, a client can configure the server, and start, stop,
 * and restart it. Server has a state. Server can be started, stopped, and restarted. To modify server attributes, get a
 * working copy, modify it, and then save it to commit the changes. Server has a set of root modules.
 * 
 * @author Pavel Petrochenko
 */
public interface IServer extends IAdaptable
{
	/**
	 * KEY_PATH
	 */
	String KEY_PATH = "path"; //$NON-NLS-1$

	/**
	 * KEY_HOST
	 */
	String KEY_HOST = "host"; //$NON-NLS-1$

	/**
	 * KEY_PORT
	 */
	String KEY_PORT = "port"; //$NON-NLS-1$

	/**
	 * KEY_ID
	 */
	String KEY_ID = "id"; //$NON-NLS-1$
	/**
	 * KEY_NAME
	 */
	String KEY_NAME = "name"; //$NON-NLS-1$

	/**
	 * KEY_ASSOCIATION_SERVER_ID
	 */
	String KEY_ASSOCIATION_SERVER_ID = "association_id"; //$NON-NLS-1$

	/**
	 * KEY_SERVER_DESCRIPTION
	 */
	String KEY_DESCRIPTION = "description"; //$NON-NLS-1$

	/**
	 * KEY_TYPE
	 */
	String KEY_TYPE = "type"; //$NON-NLS-1$

	/**
	 * KEY_LOG_PATH
	 */
	String KEY_LOG_PATH = "logpath"; //$NON-NLS-1$

	/**
	 * KEY_LOG_PATH
	 */
	String KEY_DOCUMENT_ROOT = "document_root"; //$NON-NLS-1$

	/**
	 * Publish kind constant (value 1) indicating an incremental publish request.
	 * 
	 * @see #publish(int, IModule[], IOperationListener, IProgressMonitor)
	 */
	int PUBLISH_INCREMENTAL = 1;

	/**
	 * Publish kind constant (value 2) indicating a full publish request.
	 * 
	 * @see #publish(int, IModule[], IOperationListener, IProgressMonitor)
	 */
	int PUBLISH_FULL = 2;

	/**
	 * Publish kind constant (value 3) indicating an automatic publish request.
	 * 
	 * @see #publish(int, IModule[], IOperationListener, IProgressMonitor)
	 */
	int PUBLISH_AUTO = 3;

	/**
	 * Publish kind constant (value 4) indicating a publish clean request
	 * 
	 * @see #publish(int, IModule[], IOperationListener, IProgressMonitor)
	 */
	int PUBLISH_CLEAN = 4;

	/**
	 * Server state constant (value 0) indicating that the server is in an unknown state.
	 * 
	 * @see #getServerState()
	 */
	int STATE_UNKNOWN = 0;

	/**
	 * Server state constant (value 1) indicating that the server is starting, but not yet ready to serve content.
	 * 
	 * @see #getServerState()
	 */
	int STATE_STARTING = 1;

	/**
	 * Server state constant (value 2) indicating that the server is ready to serve content.
	 * 
	 * @see #getServerState()
	 */
	int STATE_STARTED = 2;

	/**
	 * Server state constant (value 3) indicating that the server is shutting down.
	 * 
	 * @see #getServerState()
	 */
	int STATE_STOPPING = 3;

	/**
	 * Server state constant (value 4) indicating that the server is stopped.
	 * 
	 * @see #getServerState()
	 */
	int STATE_STOPPED = 4;

	/**
	 * Server state constant (value 6) indicating that no server state applies to the type or configuration of server
	 * 
	 * @see #getServerState()
	 */
	int STATE_NOT_APPLICABLE = 6;

	/**
	 * Adds the given server state listener to this server. Once registered, a listener starts receiving notification of
	 * state changes to this server. The listener continues to receive notifications until it is removed. Has no effect
	 * if an identical listener is already registered.
	 * 
	 * @param listener
	 */
	void addServerListener(IServerListener listener);

	/**
	 * Adds the given operation listener to this server. Once registered, a listener starts receiving notification of
	 * state changes to this server. The listener continues to receive notifications until it is removed. Has no effect
	 * if an identical listener is already registered.
	 * 
	 * @param listener
	 */
	void addOperationListener(IOperationListener listener);

	/**
	 * Removes the given operation listener from this server. Has no effect if the listener is not registered.
	 * 
	 * @param listener
	 */
	void removeOperationListener(IServerListener listener);

	/**
	 * Removes the given server listener from this server. Has no effect if the listener is not registered.
	 * 
	 * @param listener
	 */
	void removeServerListener(IServerListener listener);

	/**
	 * Returns whether this server can be started in the given mode.
	 * 
	 * @param launchMode
	 *            a mode in which a server can be launched, one of the mode constants defined by
	 *            {@link org.eclipse.debug.core.ILaunchManager}
	 * @return a status object with code <code>IStatus.OK</code> if the server can be started, otherwise a status
	 *         object indicating why it can't
	 */
	IStatus canStart(String launchMode);

	/**
	 * Returns whether this server is in a state that it can be restarted in the given mode. Note that only servers that
	 * are currently running can be restarted.
	 * 
	 * @param mode
	 *            a mode in which a server can be launched, one of the mode constants defined by
	 *            {@link org.eclipse.debug.core.ILaunchManager}
	 * @return a status object with code <code>IStatus.OK</code> if the server can be restarted, otherwise a status
	 *         object indicating why it can't
	 */
	IStatus canRestart(String mode);

	/**
	 * This method will be used to determine if this server can be modified. Some servers may want to contribute
	 * themselves with specific configurations that should not be exposed to modify.
	 * 
	 * @return - a status object, IStatus.OK for modifiable servers
	 */
	IStatus canModify();

	/**
	 * This method will be used to determine if this server can be modified in any state. Some servers may want to contribute
	 * themselves with specific configurations that should not be exposed to modified when.
	 * 
	 * @return - a status object, IStatus.OK for modifiable servers that can be modified in stopped state only
	 */
	IStatus canModifyInStoppedStateOnly();

	/**
	 * This method will be used to determine if this server can be deleted. Some servers may want to contribute
	 * themselves with specific configurations that should not be exposed to be deleted.
	 * 
	 * @return - a status object, IStatus.OK for deletable servers
	 */
	IStatus canDelete();
	
	/**
	 * This method will be used to determine if the user should be asked to stop the server before it is deleted.
	 * Some servers may want to contribute themselves with specific configurations for which it does not make
	 * sense to ask the user to stop the server before deletion.
	 * 
	 * @return - a status object, IStatus.OK for servers for which the user should be asked about stopping the
	 * server before deletion.
	 */
	IStatus askStopBeforeDelete();

	/**
	 * Returns whether this server is in a state that it can be stopped. Servers can be stopped if they are not already
	 * stoppe
	 * 
	 * @return a status object with code <code>IStatus.OK</code> if the server can be stopped, otherwise a status
	 *         object indicating why it can't
	 */
	IStatus canStop();

	/**
	 * Returns whether this server is in a state that it can be published to.
	 * 
	 * @return a status object with code <code>IStatus.OK</code> if the server can be published to, otherwise a status
	 *         object indicating what is wrong
	 */
	IStatus canPublish();

	/**
	 * Returns whether this server can support a given module .
	 * 
	 * @param module
	 * @return a status object with code <code>IStatus.OK</code> if it can, otherwise a status object indicating what
	 *         is wrong
	 */
	IStatus canHaveModule(IModule module);

	/**
	 * @param force
	 * @param listener
	 * @param monitor
	 *            optional progress monitor
	 */
	void stop(boolean force, IOperationListener listener, IProgressMonitor monitor);

	/**
	 * @param mode
	 * @param listener
	 * @param monitor
	 *            optional progress monitor
	 */
	void restart(String mode, IOperationListener listener, IProgressMonitor monitor);

	/**
	 * @param mode
	 * @param listener
	 * @param monitor
	 *            optional progress monitor
	 */
	void start(String mode, IOperationListener listener, IProgressMonitor monitor);

	/**
	 * Returns the ILaunchManager mode that the server is in. This method will return null if the server is not running.
	 * 
	 * @return the mode in which a server is running, one of the mode constants defined by
	 *         {@link org.eclipse.debug.core.ILaunchManager}, or <code>null</code> if the server is stopped.
	 */
	String getMode();

	/**
	 * Returns the current state of this server.
	 * 
	 * @return one of the server state (<code>STATE_XXX</code>) constants declared on IServer
	 */
	int getServerState();

	/**
	 * Returns the server type of this server.
	 * 
	 * @return server
	 */
	IServerType getServerType();

	/**
	 * @return set of modules currently configured to be deployed on this server
	 */
	IModule[] getModules();

	/**
	 * Gets a list of associated servers
	 * 
	 * @return the list of associated servers
	 */
	IServer[] getAssociatedServers();

	/**
	 * Publish one or more modules to the server.
	 * <p>
	 * The operation listener can be used to add a listener for notification of the publish result. The listener will be
	 * called with a single successful status (severity OK) when the server has finished publishing, or a single failure
	 * (severity ERROR) if there was an error publishing to the server.
	 * </p>
	 * 
	 * @param kind
	 *            the kind of publish being requested. Valid values are:
	 *            <ul>
	 *            <li><code>PUBLISH_FULL</code>- indicates a full publish.</li>
	 *            <li><code>PUBLISH_INCREMENTAL</code>- indicates a incremental publish.
	 *            <li><code>PUBLISH_CLEAN</code>- indicates a clean request. Clean throws out all state and cleans
	 *            up the module on the server before doing a full publish.
	 *            </ul>
	 * @param modules
	 *            an array of modules, or <code>null</code> to publish all modules
	 * @param listener
	 *            an operation listener to receive notification when this operation is done, or <code>null</code> if
	 *            notification is not required
	 * @param monitor
	 *            optional progress monitor
	 */
	void publish(int kind, IModule[] modules, IOperationListener listener, IProgressMonitor monitor);

	/**
	 * Returns the launch that was used to start the server, if available. If the server is not running, or does not
	 * uses launches will return <code>null</code>.
	 * 
	 * @return the launch used to start the currently running server, or <code>null</code> if the launch is
	 *         unavailable or could not be found
	 */
	ILaunch getLaunch();

	/**
	 * @return name of this server
	 */
	String getName();

	/**
	 * @return unique id of this server
	 */
	String getId();

	/**
	 * Returns the log that is used by this server, or null if no log is available.
	 * 
	 * @return log
	 */
	ILog getLog();

	/**
	 * Returns all logs that this server currently logs to.
	 * 
	 * @return - array of logs
	 */
	ILog[] getAllLogs();

	/**
	 * True if the server supplies statistics
	 * 
	 * @return - true if providing statistics
	 */
	boolean suppliesStatistics();

	/**
	 * Gets statistics for this server
	 * 
	 * @return - string of stats or null for no stats provided
	 */
	String fetchStatistics();

	/**
	 * True if this server supplies a custom statistics interface instead of just return a string that can be displayed
	 * in an arbitrary dialog
	 * 
	 * @return - true if providing a custom ui for statisics
	 */
	boolean suppliesStatisticsInterface();

	/**
	 * Shows a custom statistics interface for this server
	 */
	void showStatisticsInterface();

	/**
	 * @return a streams proxy for a running server instance
	 */
	IStreamsProxy getStreamsProxy();

	/**
	 * this operation is synchronous
	 * 
	 * @param configuration
	 * @throws CoreException
	 */
	void reconfigure(IAbstractConfiguration configuration) throws CoreException;

	/**
	 * @param module
	 * @param listener
	 * @param monitor
	 */
	void configureModule(IModule module, IOperationListener listener, IProgressMonitor monitor);

	/**
	 * @param config
	 * @param listener
	 * @param monitor
	 */
	void configureModule(IAbstractConfiguration config, IOperationListener listener, IProgressMonitor monitor);

	/**
	 * @param module
	 * @param listener
	 * @param monitor
	 */
	void unconfigureModule(IModule module, IOperationListener listener, IProgressMonitor monitor);

	/**
	 * @param module
	 * @return true if a given module is one of modules configured for this server
	 */
	boolean isConfigured(IModule module);

	/**
	 * @param configuration
	 */
	void storeConfiguration(IAbstractConfiguration configuration);

	/**
	 * @return short description of current server configuration
	 */
	String getDescription();

	/**
	 * @return - array of processes
	 */
	IProcess[] getProcesses();

	/**
	 * @return - true if external
	 */
	boolean isExternal();

	/**
	 * @return - true if a web server
	 */
	boolean isWebServer();

	/**
	 * True if the server should not be persisted
	 * 
	 * @return - true if it should not be persisted, false if it should
	 */
	boolean isTransient();

	/**
	 * Gets the hostname + port of this server. This will be used for the initial part of a url http://host:port/ where
	 * this method should return the host:port portion
	 * 
	 * @return - host:port for this server or null if it does not apply
	 */
	String getHost();

	/**
	 * Gets the hostname of this server;
	 * 
	 * @return - just the hostname
	 */
	String getHostname();

	/**
	 * Gets the port of this server
	 * 
	 * @return - just the port
	 */
	int getPort();

	/**
	 * Gets the document root for this server
	 * 
	 * @return - path of document root
	 */
	IPath getDocumentRoot();

	/**
	 * Gets server root location for local servers.
	 * 
	 * @return server root location.
	 */
	IPath getServerRoot();

}