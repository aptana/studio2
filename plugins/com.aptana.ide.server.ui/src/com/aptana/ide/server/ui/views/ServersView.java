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

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.PreferenceUtils;
import com.aptana.ide.server.IServerListener;
import com.aptana.ide.server.IServerRunnable;
import com.aptana.ide.server.core.ServerManager;
import com.aptana.ide.server.ui.ServerUIPlugin;

/**
 * ServersView
 */
public class ServersView extends ViewPart implements IServerListener
{

	private TableViewer serverTableViewer;

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
		parent.setLayout(new GridLayout());
		serverTableViewer = createServerTable(parent);
	}

	private TableViewer createServerTable(Composite parent)
	{
		TableViewer view = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION);
		Table serverTable = view.getTable();
		serverTable.setHeaderVisible(true);
		serverTable.setLinesVisible(true);
		serverTable.setLayoutData(new GridData(GridData.FILL_BOTH));

		TableColumn imageCol = new TableColumn(serverTable, SWT.LEFT);
		imageCol.setWidth(20);

		TableColumn nameCol = new TableColumn(serverTable, SWT.LEFT);
		nameCol.setText(Messages.ServersView_NAME);
		nameCol.setWidth(100);

		TableColumn statusCol = new TableColumn(serverTable, SWT.LEFT);
		statusCol.setText(Messages.ServersView_STATUS);
		statusCol.setWidth(125);

		ServerManager.getInstance().addServerListener(this);
		view.setLabelProvider(new ServerLabelProvider());
		view.setContentProvider(new ServerContentProvider());
		PreferenceUtils.registerBackgroundColorPreference(view.getControl(), "com.aptana.ide.core.ui.background.color.serversView"); //$NON-NLS-1$
		PreferenceUtils.registerForegroundColorPreference(view.getControl(), "com.aptana.ide.core.ui.foreground.color.serversView"); //$NON-NLS-1$
		return view;
	}

	/**
	 * getSelection
	 *
	 * @return ISelection
	 */
	public ISelection getSelection()
	{
		return serverTableViewer.getSelection();
	}

	/**
	 * @see com.aptana.ide.server.IServerListener#serverStarted(com.aptana.ide.server.IServerRunnable)
	 */
	public void serverStarted(final IServerRunnable server)
	{
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
		{
			public void run()
			{
				serverTableViewer.add(server);
			}
		});
	}

	/**
	 * @see com.aptana.ide.server.IServerListener#serverStopped(com.aptana.ide.server.IServerRunnable)
	 */
	public void serverStopped(final IServerRunnable server)
	{
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
		{
			public void run()
			{
				serverTableViewer.remove(server);
			}
		});
	}

	/**
	 * @see com.aptana.ide.server.IServerListener#serverChanged(com.aptana.ide.server.IServerRunnable)
	 */
	public void serverChanged(final IServerRunnable server)
	{
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable()
		{
			public void run()
			{
				serverTableViewer.update(server, null);
			}
		});
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus()
	{
		serverTableViewer.getTable().setFocus();
	}

	/**
	 * ServerLabelProvider
	 * @author Ingo Muschenetz
	 *
	 */
	class ServerLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex)
		{
			if (columnIndex == 0)
			{
				return ServerUIPlugin.getImageDescriptor("icons/server.gif").createImage(); //$NON-NLS-1$
			}
			return null;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex)
		{
			IServerRunnable server = (IServerRunnable) element;
			ILaunchConfiguration launchConfig = server.getLaunchConfiguration();
			switch (columnIndex)
			{
				case 1:
					return launchConfig.getName();
				case 2:
					return Messages.ServersView_STATUS_STARTED;
				default:
					return StringUtils.EMPTY;
			}
		}
	}

	/**
	 * ServerContentProvider
	 * @author Ingo Muschenetz
	 *
	 */
	class ServerContentProvider implements IStructuredContentProvider
	{
		/**
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			return ServerManager.getInstance().getRunningServers();
		}
		
		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose()
		{
		}

		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
	}

	
}
