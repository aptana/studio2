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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.IServerManager;
import com.aptana.ide.server.core.IServerManagerListener;
import com.aptana.ide.server.core.ServerManagerEvent;
import com.aptana.ide.server.core.impl.servers.GroupServer;

/**
 * @author Pavel Petrochenko
 */
public class ServerContentProvider implements ITreeContentProvider, IServerManagerListener
{
	private TreeViewer viewer;

	private IServerManager manager;

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		if (inputElement instanceof IServerManager)
		{
			IServerManager manager = (IServerManager) inputElement;
			return manager.getServers();
		}
		return new Object[0];
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 *      java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		if (manager != null)
		{
			dispose();
		}
		if (newInput instanceof IServerManager)
		{
			manager = (IServerManager) newInput;
			manager.addServerManagerListener(this);
		}
		this.viewer = (TreeViewer) viewer;
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose()
	{
		if (manager != null)
		{
			manager.removeServerManagerListener(this);
			manager = null;
		}
	}

	/**
	 * @param server
	 */
	public void serverAdded(IServer server)
	{
		safeRefresh();
	}

	private void safeRefresh()
	{
		Display.getDefault().syncExec(new Runnable()
		{

			public void run()
			{
				viewer.refresh();
			}

		});
	}

	/**
	 * @param server
	 */
	public void serverChanged(IServer server)
	{
		safeRefresh();
	}

	/**
	 * @param server
	 */
	public void serverRemoved(IServer server)
	{
		safeRefresh();
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof GroupServer)
		{
			return ((GroupServer) parentElement).getServers().toArray();
		}
		if (parentElement instanceof IServer)
		{
			IServer srv = (IServer) parentElement;
			return srv.getModules();
		}
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element)
	{
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element)
	{
		if (element instanceof GroupServer)
		{
			return !((GroupServer) element).isEmpty();
		}
		if (element instanceof IServer)
		{
			IServer srv = (IServer) element;
			return srv.getModules().length > 0;
		}
		return false;
	}

	/**
	 * @see com.aptana.ide.server.core.IServerManagerListener#serversChanged(com.aptana.ide.server.core.ServerManagerEvent)
	 */
	public void serversChanged(final ServerManagerEvent event)
	{
		// Control control = viewer.getControl();
		// if (!control.isDisposed())
		// {
		Display.getDefault().asyncExec(new Runnable()
		{

			public void run()
			{
				if (viewer.getControl().isDisposed())
				{
					return;
				}
				switch (event.getKind())
				{
					case ServerManagerEvent.KIND_ADDED:
						viewer.add(new TreePath(new Object[0]), event.getServer());
						break;
					case ServerManagerEvent.KIND_REMOVED:
						viewer.remove(event.getServer());
					case ServerManagerEvent.KIND_CHANGED:
						viewer.refresh(event.getServer(), true);
					default:
						break;
				}
			}

		});
		// }
	}
}
