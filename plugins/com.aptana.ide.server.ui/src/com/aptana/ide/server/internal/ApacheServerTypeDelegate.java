/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.server.internal;

import org.eclipse.core.runtime.CoreException;
import com.aptana.ide.server.core.IAbstractConfiguration;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.IServerType;
import com.aptana.ide.server.core.model.IServerTypeDelegate;

/**
 * @author Pavel Petrochenko
 *
 */
public class ApacheServerTypeDelegate implements IServerTypeDelegate {

	
	/**
	 * ID
	 */
	public static final String ID = "com.aptana.ide.server.apache"; //$NON-NLS-1$

	/**
	 * STARTAPACHE
	 */
	public static final String STARTAPACHE="start"; //$NON-NLS-1$
	/**
	 * STOPAPACHE
	 */
	public static final String STOPAPACHE="shutdown"; //$NON-NLS-1$
	/**
	 * RESTARTAPACHE
	 */
	public static final String RESTARTAPACHE="restart";	 //$NON-NLS-1$
	
	/**
	 * ETCHOSTS
	 */
	public static final String ETCHOSTS="ETCHOSTS"; //$NON-NLS-1$

	/**
	 * HOSTNAME
	 */
	public static final String HOSTNAME = "HOSTNAME"; //$NON-NLS-1$
	
	/**
	 * DOCUMENT_ROOT
	 */
	public static final String DOCUMENT_ROOT="DOCUMENT_ROOT"; //$NON-NLS-1$
	
	/**
	 * @see com.aptana.ide.server.core.model.IServerTypeDelegate#createServer(com.aptana.ide.server.core.IAbstractConfiguration, com.aptana.ide.server.core.IServerType)
	 */
	public IServer createServer(IAbstractConfiguration configuration, IServerType type) throws CoreException
	{
		return new ApacheServer(type,configuration);
	}

}
