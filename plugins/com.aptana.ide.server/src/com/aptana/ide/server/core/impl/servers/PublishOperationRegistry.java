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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.ServerCore;
import com.aptana.ide.server.core.IPublishOperation;
import com.aptana.ide.server.core.impl.RegistryLazyObject;
import com.aptana.ide.server.core.impl.RegistryObjectCollection;

/**
 * @author Pavel Petrochenko
 */
public final class PublishOperationRegistry extends RegistryObjectCollection
{

	/**
	 * 
	 */
	private PublishOperationRegistry()
	{
		super("com.aptana.ide.server.publishOperation"); //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.server.core.impl.RegistryObjectCollection#createObject(org.eclipse.core.runtime.IConfigurationElement)
	 */
	protected RegistryLazyObject createObject(IConfigurationElement configurationElement)
	{
		return new PublishOperation(configurationElement);
	}

	private static PublishOperationRegistry INSTANCE = null;

	/**
	 * @return instance
	 */
	public static PublishOperationRegistry getOperationRegistry()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new PublishOperationRegistry();
		}
		return INSTANCE;
	}

	/**
	 * @param publishOperationIds
	 * @return operations for a given operation id's
	 * @throws CoreException if something goes wrong
	 */
	public IPublishOperation[] getOperations(String[] publishOperationIds) throws CoreException
	{
		IPublishOperation[] result = new IPublishOperation[publishOperationIds.length];
		for (int a = 0; a < publishOperationIds.length; a++)
		{
			result[a] = (IPublishOperation) getObject(publishOperationIds[a]);
			if (result[a]==null){
				throw new CoreException(new Status(IStatus.ERROR,ServerCore.PLUGIN_ID,0,StringUtils.format(Messages.PublishOperationRegistry_0,publishOperationIds[a]),null));
			}
		}
		return result;
	}
}
