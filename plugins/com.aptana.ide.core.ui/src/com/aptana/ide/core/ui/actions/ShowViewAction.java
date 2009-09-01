/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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
package com.aptana.ide.core.ui.actions;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class ShowViewAction extends AbstractWorkbenchWindowPulldownDelegate
{

	private static final String[] VIEW_IDS =
		{ "com.aptana.ide", "zigen.plugin.db", "org.eclipse.eclipsemonkey", "org.tigris.subversion.subclipse", "org.eclipse.ui.views.ContentOutline" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

	private Menu toolbarMenu = null;

	/**
	 * org.eclipse.ui.IWorkbenchWindowPulldownDelegate#getMenu(org.eclipse.swt.
	 * widgets.Control)
	 */
	public Menu getMenu(Control parent)
	{
		if (toolbarMenu != null)
		{
			toolbarMenu.dispose();
		}
		toolbarMenu = new Menu(parent);
		buildMenu(toolbarMenu);

		return toolbarMenu;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWindowPulldownDelegate2#getMenu(org.eclipse.swt.widgets.Menu)
	 */
	public Menu getMenu(Menu parent)
	{
		Menu menu = new Menu(parent);
		buildMenu(menu);

		return menu;
	}

	private void buildMenu(Menu menu)
	{
		final IViewRegistry viewsRegistry = PlatformUI.getWorkbench().getViewRegistry();
		// gets all views
		IViewDescriptor[] viewDescriptors = viewsRegistry.getViews();

		// sorts alphabetically by label
		Arrays.sort(viewDescriptors, new Comparator<IViewDescriptor>()
		{
			public int compare(IViewDescriptor vd1, IViewDescriptor vd2)
			{
				return vd1.getLabel().compareTo(vd2.getLabel());
			}
		});

		// configures the menu items for the Aptana views
		SelectionAdapter selAdapter = new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				try
				{
					IViewDescriptor viewId = viewsRegistry.find((String) e.widget.getData());
					if (viewId != null)
					{
						IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
								.getActivePage();
						IViewPart view = activePage.showView(viewId.getId(), null, IWorkbenchPage.VIEW_CREATE);
						activePage.activate(view);
					}
				}
				catch (PartInitException pie)
				{
				}
			}

		};
		String viewId;
		MenuItem menuItem;
		for (IViewDescriptor viewDescriptor : viewDescriptors)
		{
			viewId = viewDescriptor.getId();
			if (isAptanaView(viewId))
			{
				menuItem = new MenuItem(menu, SWT.PUSH);
				menuItem.setText(viewDescriptor.getLabel());
				menuItem.setImage(viewDescriptor.getImageDescriptor().createImage());
				menuItem.setData(viewId);
				// handles selection
				menuItem.addSelectionListener(selAdapter);
			}
		}
	}
	
	private static boolean isAptanaView(String viewId)
	{
		if (viewId == null)
		{
			return false;
		}
		for (String id : VIEW_IDS)
		{
			if (viewId.startsWith(id))
			{
				return true;
			}
		}
		return false;
	}

}
