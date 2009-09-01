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
 */
public class MySqlServerTypeDelegate implements IServerTypeDelegate
{

	/**
	 * ID
	 */
	public static final String ID = "com.aptana.ide.server.mysql"; //$NON-NLS-1$

	/**
	 * LAUNCHARRGS
	 */
	public static final String LAUNCHARRGS = "launchargs"; //$NON-NLS-1$

	/**
	 * @see com.aptana.ide.server.core.model.IServerTypeDelegate#createServer(com.aptana.ide.server.core.IAbstractConfiguration,
	 *      com.aptana.ide.server.core.IServerType)
	 */
	public IServer createServer(IAbstractConfiguration configuration, IServerType type) throws CoreException
	{
		return new MySqlServer(type, configuration);
	}

}
