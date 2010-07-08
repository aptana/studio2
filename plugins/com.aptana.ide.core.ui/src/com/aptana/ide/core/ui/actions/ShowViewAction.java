/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.intro.IIntroConstants;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class ShowViewAction extends AbstractWorkbenchWindowPulldownDelegate
{

	private Menu toolbarMenu = null;
	private IWorkbenchWindow window;

	@Override
	public void init(IWorkbenchWindow window)
	{
		this.window = window;
	}

	/**
	 * org.eclipse.ui.IWorkbenchWindowPulldownDelegate#getMenu(org.eclipse.swt. widgets.Control)
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
		// If no page disable all.
		IWorkbenchPage page = window.getActivePage();
		if (page == null)
		{
			return;
		}

		// If no active perspective disable all
		if (page.getPerspective() == null)
		{
			return;
		}

		// Get visible actions.
		List<String> viewIds = Arrays.asList(page.getShowViewShortcuts());

		// add all open views
		viewIds = addOpenedViews(page, viewIds);

		// Remove INTRO_VIEW_ID
		viewIds.remove(IIntroConstants.INTRO_VIEW_ID);

		final IViewRegistry viewsRegistry = PlatformUI.getWorkbench().getViewRegistry();
		List<IViewDescriptor> viewDescriptorsList = new ArrayList<IViewDescriptor>();

		// gets all views
		IViewDescriptor[] viewDescriptors = viewsRegistry.getViews();

		// Filter for views of interest
		for (int i = 0; i < viewDescriptors.length; i++)
		{
			if (viewIds.contains(viewDescriptors[i].getId()))
			{
				viewDescriptorsList.add(viewDescriptors[i]);
			}
		}

		viewDescriptors = viewDescriptorsList.toArray(new IViewDescriptor[viewDescriptorsList.size()]);

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
			menuItem = new MenuItem(menu, SWT.PUSH);
			menuItem.setText(viewDescriptor.getLabel());
			menuItem.setImage(viewDescriptor.getImageDescriptor().createImage());
			menuItem.setData(viewId);
			// handles selection
			menuItem.addSelectionListener(selAdapter);
		}
	}

	// Maps pages to a list of opened views
	private Map<IWorkbenchPage, List<String>> openedViews = new HashMap<IWorkbenchPage, List<String>>();

	private List<String> addOpenedViews(IWorkbenchPage page, List<String> actions)
	{
		List<String> views = getParts(page);
		List<String> result = new ArrayList<String>(views.size() + actions.size());

		for (int i = 0; i < actions.size(); i++)
		{
			String element = actions.get(i);
			if (result.indexOf(element) < 0)
			{
				result.add(element);
			}
		}
		for (int i = 0; i < views.size(); i++)
		{
			String element = views.get(i);
			if (result.indexOf(element) < 0)
			{
				result.add(element);
			}
		}
		return result;
	}

	private List<String> getParts(IWorkbenchPage page)
	{
		List<String> parts = openedViews.get(page);
		if (parts == null)
		{
			parts = new ArrayList<String>();
			openedViews.put(page, parts);
		}
		return parts;
	}
}