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
package com.aptana.ide.server.generic;

import org.eclipse.core.runtime.CoreException;

import com.aptana.ide.server.core.IAbstractConfiguration;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.IServerType;
import com.aptana.ide.server.core.model.IServerTypeDelegate;

/**
 * @author Pavel Petrochenko
 */
public class GenericServerTypeDelegate implements IServerTypeDelegate
{

	/**
	 * ID
	 */
	public static final String ID = "com.aptana.ide.server.generic.genericHttpServer"; //$NON-NLS-1$

	/**
	 * START_SERVER_COMMAND
	 */
	public static final String START_SERVER_COMMAND = "start"; //$NON-NLS-1$

	/**
	 * STOP_SERVER_COMMAND
	 */
	public static final String STOP_SERVER_COMMAND = "stop"; //$NON-NLS-1$

	/**
	 * PAUSE_SERVER_COMMAND
	 */
	public static final String PAUSE_SERVER_COMMAND = "pause"; //$NON-NLS-1$

	/**
	 * RESUME_SERVER_COMMAND
	 */
	public static final String RESUME_SERVER_COMMAND = "resume"; //$NON-NLS-1$

	/**
	 * HEALTH_URL
	 */
	public static final String HEALTH_URL = "health_url"; //$NON-NLS-1$

	/**
	 * POLLING_INTERVAL
	 */
	public static final String POLLING_INTERVAL = "polling_interval"; //$NON-NLS-1$

	/**
	 * RESUME_SERVER_COMMAND
	 */
	public static final String IS_LOCAL = "isLocal"; //$NON-NLS-1$

	/**
	 * 
	 */
	public GenericServerTypeDelegate()
	{
	}

	/**
	 * @see com.aptana.ide.server.core.model.IServerTypeDelegate#createServer(com.aptana.ide.server.core.IAbstractConfiguration,
	 *      com.aptana.ide.server.core.IServerType)
	 */
	public IServer createServer(IAbstractConfiguration configuration, IServerType type) throws CoreException
	{
		return new GenericServer(type, configuration);
	}

}
