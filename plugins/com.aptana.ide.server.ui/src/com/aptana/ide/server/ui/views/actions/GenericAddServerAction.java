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
package com.aptana.ide.server.ui.views.actions;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;

import com.aptana.ide.server.ServerCore;
import com.aptana.ide.server.core.IServerType;
import com.aptana.ide.server.ui.ServerDialogPageRegistry;
import com.aptana.ide.server.ui.ServerUIPlugin;
import com.aptana.ide.server.ui.generic.dialogs.ServerTypeSelectionDialog;

/**
 * @author Pavel Petrochenko
 */
public class GenericAddServerAction extends Action
{
	private static final boolean USE_DROP_DOWN = false;

	/**
	 * 
	 */
	public GenericAddServerAction()
	{
		super(Messages.GenericAddServerActionTITLE, Action.AS_DROP_DOWN_MENU);
		this.setImageDescriptor(ServerUIPlugin.getImageDescriptor("/icons/server/add_server.gif")); //$NON-NLS-1$
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

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run()
	{
		if (USE_DROP_DOWN)
		{
			Display current = Display.getCurrent();
			// final Menu mn=new Menu(current.getActiveShell(),SWT.POP_UP);

			MenuManager mnu = new MenuManager();
			fillManager(mnu);
			final Menu mn = mnu.createContextMenu(current.getActiveShell());
			mn.setLocation(current.getCursorLocation());

			mn.addListener(SWT.Hide, new Listener()
			{

				public void handleEvent(Event event)
				{
					mn.dispose();
				}

			});
			mn.setVisible(true);
		}
		else
		{
			ServerTypeSelectionDialog s = new ServerTypeSelectionDialog(Display.getCurrent().getActiveShell());
			s.open();
			IServerType result = s.getResult();
			if (result != null)
			{
				new NewServerAction(result).run();
			}
		}
	}

	private void fillManager(MenuManager mn)
	{
		IServerType[] knownServerTypes = ServerCore.getServerManager().getServerTypes();
		Arrays.sort(knownServerTypes,new Comparator<IServerType>(){

			public int compare(IServerType arg0, IServerType arg1)
			{
				return arg0.getName().compareTo(arg1.getName());
			}
			
		});
		for (int a = 0; a < knownServerTypes.length; a++)
		{
			final IServerType type = knownServerTypes[a];
			boolean hasDialog = ServerDialogPageRegistry.getInstance().hasDialog(type.getId());
			boolean b = type.getAdapter(ICanAdd.class) != null;

			if (hasDialog || b)
			{
				Action action = new NewServerAction(type);
				mn.add(action);
			}
		}
	}
}
