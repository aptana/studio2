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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.ide.server.ServerCore;
import com.aptana.ide.server.core.IModule;
import com.aptana.ide.server.core.IModuleFile;
import com.aptana.ide.server.core.IModuleFolder;
import com.aptana.ide.server.core.IModuleResource;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.IServerWithWritablePathes;
import com.aptana.ide.server.core.model.IPublishOperationDelegate;

/**
 * this is delegate for operation wich performs simple copying of module resources to server
 * @author Pavel Petrochenko
 */
public class CopyOperationDelegate implements IPublishOperationDelegate
{

	/**
	 * @see com.aptana.ide.server.core.model.IPublishOperationDelegate#canPublish(com.aptana.ide.server.core.IServer,
	 *      int, com.aptana.ide.server.core.IModule[])
	 */
	public IStatus canPublish(IServer server, int kind, IModule[] module)
	{
		return Status.OK_STATUS;
	}

	/**
	 * @see com.aptana.ide.server.core.model.IPublishOperationDelegate#performPublish(com.aptana.ide.server.core.IServer,
	 *      int, com.aptana.ide.server.core.IModule[], org.eclipse.core.runtime.IProgressMonitor,
	 *      com.aptana.ide.server.core.IOperationListener)
	 */
	public IStatus performPublish(IServer server, int kind, IModule[] modules, IProgressMonitor monitor)
	{
		try
		{
			IServerWithWritablePathes srv = (IServerWithWritablePathes) server;
			switch (kind)
			{
				case IServer.PUBLISH_CLEAN:
					for (int a = 0; a < modules.length; a++)
					{
						IPath rootPath = srv.getRootPath(modules[a]);
						IModuleResource[] rootResources = modules[a].getRootResources();
						for (int b = 0; b < rootResources.length; b++)
						{
							IModuleResource r = rootResources[b];
							IPath append = rootPath.append(r.getPath());
							IStatus clean = srv.clean(append);
							if (monitor.isCanceled())
							{						
								return Status.OK_STATUS;
							}
							if (clean.getSeverity() == IStatus.ERROR)
							{								
								return clean;
							}
						}
					}
					break;
				case IServer.PUBLISH_FULL:
					return doPublish(srv, modules, monitor, true);
				case IServer.PUBLISH_INCREMENTAL:
					return doPublish(srv, modules, monitor, false);

				default:
					break;
			}
			return Status.OK_STATUS;
		}
		catch (CoreException e)
		{
			Status sts = new Status(IStatus.ERROR, ServerCore.PLUGIN_ID, IStatus.ERROR, "core exeption", e); //$NON-NLS-1$			
			return sts;
		}
	}

	private IStatus doPublish(IServerWithWritablePathes server, IModule[] modules, IProgressMonitor monitor,
			 boolean c) throws CoreException
	{
		for (int a = 0; a < modules.length; a++)
		{
			IPath rootPath = server.getRootPath(modules[a]);
			IModuleResource[] rootResources = modules[a].getRootResources();
			for (int b = 0; b < rootResources.length; b++)
			{
				IModuleResource r = rootResources[b];
				IStatus clean = publish(server, rootPath, r, c);
				if (monitor.isCanceled())
				{
					return Status.OK_STATUS;
				}
				if (clean.getSeverity() == IStatus.ERROR)
				{
					return clean;
				}
			}
		}
		return Status.OK_STATUS;
	}

	private IStatus publish(IServerWithWritablePathes server, IPath rootPath, IModuleResource r, boolean c)
			throws CoreException
	{
		if (r.isFolder())
		{
			IModuleFolder fld = (IModuleFolder) r;
			return publishFolder(server, rootPath, c, fld);
		}
		IPath append = rootPath.append(r.getPath());
		boolean shouldPublish = true;
		IModuleFile file = (IModuleFile) r;
		if (c)
		{
			long timeStamp = server.getTimeStamp(append);

			shouldPublish = file.getModificationStamp() > timeStamp;
		}
		if (shouldPublish)
		{
			IStatus writeResource = server.writeResource(append, file.getContents());
			return writeResource;
		}
		return Status.OK_STATUS;
	}

	/**
	 * @param server
	 * @param rootPath
	 * @param c
	 * @param fld
	 * @return
	 * @throws CoreException
	 */
	protected IStatus publishFolder(IServerWithWritablePathes server, IPath rootPath, boolean c, IModuleFolder fld)
			throws CoreException
	{
		IModuleResource[] members = fld.getMembers();
		for (int a = 0; a < members.length; a++)
		{
			IStatus ok = publish(server, rootPath, members[a], c);
			if (ok.getSeverity() == IStatus.ERROR)
			{
				return ok;
			}
		}
		return Status.OK_STATUS;
	}

}
