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
package com.aptana.ide.server.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.core.IPausableServer;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.impl.servers.AbstractServer;
import com.aptana.ide.server.ui.ServerImagesRegistry;

/**
 * Label provider for the server view.
 * 
 * @author Pavel Petrochenko
 */
public class ServerLabelProvider extends LabelProvider implements ITableLabelProvider
{

	/**
	 * SERVER_IS_RUNNING_NO_EDIT
	 */
	public static final String SERVER_IS_RUNNING_NO_EDIT = Messages.GenericServersView_ServerReadOnlyDescription;

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex)
	{
		if (columnIndex == 0)
		{
			Image image = ServerImagesRegistry.getInstance().getImage(element);
			return PlatformUI.getWorkbench().getDecoratorManager().decorateImage(image, element);
		}
		else
		{
			return null;
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex)
	{
		IServer server = (IServer) element;
		int serverState = server.getServerState();

		String description = server.getDescription();
		switch (columnIndex)
		{
			case 0:
				String name = server.getName();
				IServer[] assoc = server.getAssociatedServers();
				if (assoc.length > 0)
				{
					List<String> names = new ArrayList<String>();
					for (int i = 0; i < assoc.length; i++)
					{
						IServer server2 = assoc[i];
						names.add(server2.getName());
					}

					String nameList = StringUtils.join(", ", names.toArray(new String[0])); //$NON-NLS-1$
					name = StringUtils.format(Messages.ServerLabelProvider_BOUND_TO, new String[] { name, nameList });
				}

				return name;
			case 1:
				switch (serverState)
				{
					case IServer.STATE_STARTED:
						return Messages.ServerLabelProvider_Running;
					case IServer.STATE_STARTING:
						return Messages.ServerLabelProvider_Starting;
					case IServer.STATE_STOPPING:
						return Messages.ServerLabelProvider_Stopping;
					case IServer.STATE_STOPPED:
						return Messages.ServerLabelProvider_Stopped;
					case IServer.STATE_NOT_APPLICABLE:
						return Messages.ServerLabelProvider_NotApplicable;
					case IPausableServer.STATE_PAUSED:
						return Messages.ServerLabelProvider_PAUSED;

					default:
						break;
				}
				return Messages.ServerLabelProvider_Unknown;
			case 2:
				if (description == null || description.length() == 0)
				{
					if (server instanceof AbstractServer)
					{
						AbstractServer as = (AbstractServer) server;
						return as.getConfigurationDescription();
					}
				}
				return description;
			case 3:
				return server.getServerType().getCategory();
			case 4:
				String host = server.getHost();
				if (host != null)
				{
					int indexOf = host.indexOf(':');
					if (indexOf != -1)
					{
						return host.substring(0, indexOf);
					}
					return host;
				}
				return null;
			case 5:
				String hostp = server.getHost();
				if (server.getPort() > 0)
				{
					return Integer.toString(server.getPort());
				}
				else if (hostp != null)
				{
					int indexOf = hostp.indexOf(':');
					if (indexOf != -1)
					{
						return hostp.substring(indexOf + 1);
					}
					return ""; //$NON-NLS-1$
				}
				return null;
			default:
				break;
		}
		return null;
	}
}
