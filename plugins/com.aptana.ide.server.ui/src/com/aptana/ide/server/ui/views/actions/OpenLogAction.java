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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.server.ui.views.actions;

import java.io.File;
import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.logging.view.LogView;
import com.aptana.ide.server.core.ILog;
import com.aptana.ide.server.core.ILogOpener;
import com.aptana.ide.server.core.INamedLog;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.impl.Configuration;
import com.aptana.ide.server.ui.ServerUIPlugin;

/**
 * @author Pavel Petrochenko
 */
public class OpenLogAction extends Action
{

	private ISelectionProvider provider;

	public OpenLogAction(ISelectionProvider provider)
	{
		this(provider, Action.AS_DROP_DOWN_MENU);
	}

	/**
	 * @param provider
	 * @param style
	 */
	public OpenLogAction(ISelectionProvider provider, int style)
	{
		super("", style); //$NON-NLS-1$
		this.setImageDescriptor(ServerUIPlugin.getImageDescriptor("/icons/windowlist.png")); //$NON-NLS-1$
		this.setToolTipText(Messages.OpenLogAction_Title0);
		this.provider = provider;
		provider.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				OpenLogAction.this.selectionChanged(event);
			}

		});
		this.setMenuCreator(new IMenuCreator()
		{

			public void dispose()
			{

			}

			public Menu getMenu(Control parent)
			{
				MenuManager mn = new MenuManager();
				fillManager(mn);
				return mn.createContextMenu(parent);
			}

			public Menu getMenu(Menu parent)
			{
				MenuManager mn = new MenuManager();
				fillManager(mn);
				Menu menu = new Menu(parent);
				mn.fill(menu, 0);
				return menu;
			}

		});
	}

	private void fillManager(MenuManager mn)
	{
		ISelection selection = provider.getSelection();
		if (selection instanceof StructuredSelection && !selection.isEmpty())
		{
			StructuredSelection ss = (StructuredSelection) selection;
			final IServer server = (IServer) ss.getFirstElement();
			ILog[] logs = server.getAllLogs();
			final Object logOpener = server.getAdapter(ILogOpener.class);
			if (logs != null)
			{
				for (final ILog log : logs)
				{
					Action action = new Action()
					{

						public void run()
						{
							if (logOpener instanceof ILogOpener)
							{
								((ILogOpener) logOpener).openLog(log);
							}
							else
							{
								openLogView(log.getURI(), server.getName());
							}
						}

					};
					if (log instanceof INamedLog)
					{
						action.setText(((INamedLog) log).getName());
					}
					else
					{
						action.setText(getLogName(log.getURI()));
					}
					mn.add(action);
				}
			}
		}
	}

	private void openLogView(URI uri, String tabName)
	{
		try
		{
			LogView showView = (LogView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
					LogView.ID);
			showView.addTab(uri, StringUtils.format(Messages.OpenLogAction_LOG_NAME, tabName), true);
		}
		catch (PartInitException e)
		{
			IdeLog.logError(ServerUIPlugin.getDefault(), "Part init exception while opening log view", e); //$NON-NLS-1$
		}
	}

	private String getLogName(URI logURI)
	{
		String name = ""; //$NON-NLS-1$
		if (logURI != null)
		{
			name = logURI.getPath();
			int lastSlash = name.lastIndexOf('/');
			if (lastSlash + 1 < name.length() - 1)
			{
				name = name.substring(lastSlash + 1);
			}
		}
		return name;
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run()
	{
		ISelection selection = provider.getSelection();
		if (selection instanceof StructuredSelection && !selection.isEmpty())
		{
			StructuredSelection ss = (StructuredSelection) selection;
			IServer oo = (IServer) ss.getFirstElement();
			Object logOpener = oo.getAdapter(ILogOpener.class);

			ILog log = oo.getLog();
			if (log == null)
			{
				FileDialog dlg = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
				dlg.setText(Messages.OpenLogAction_CHOOSE_LOG_TITLE);
				dlg.setFilterNames(new String[] { "" }); //$NON-NLS-1$
				dlg.setFilterExtensions(new String[] { "*.log" }); //$NON-NLS-1$
				String sm = dlg.open();
				if (sm == null)
				{
					return;
				}
				File fl = new File(sm);
				String string = fl.getAbsolutePath();
				Configuration config = new Configuration();
				oo.storeConfiguration(config);
				config.setStringAttribute(IServer.KEY_LOG_PATH, string);
				try
				{
					oo.reconfigure(config);
				}
				catch (CoreException e)
				{
					IdeLog.logError(ServerUIPlugin.getDefault(),
							"Core exception while setting log location for server", e); //$NON-NLS-1$
				}
				log = oo.getLog();
			}
			if (logOpener instanceof ILogOpener)
			{
				((ILogOpener) logOpener).openLog(log);
			}
			else
			{
				openLogView(log.getURI(), oo.getName());
			}
		}
	}

	/**
	 * @param event
	 */
	public void selectionChanged(SelectionChangedEvent event)
	{
		ISelection selection2 = event.getSelection();
		if (selection2 instanceof StructuredSelection && !selection2.isEmpty())
		{
			StructuredSelection ss = (StructuredSelection) selection2;
			IServer ss1 = (IServer) ss.getFirstElement();
			setEnabled(ss1.getLog() != null);
			if (ss1.getLog() != null)
			{
				setToolTipText(Messages.OpenLogAction_Title0);
			}
			else
			{
				setToolTipText(Messages.OpenLogAction_NO_LOG);
			}
		}
		else
		{
			setToolTipText(Messages.OpenLogAction_NO_LOG);
			setEnabled(false);
		}
	}

}
