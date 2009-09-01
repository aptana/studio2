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

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.internal.provisional.action.IToolBarContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;

import com.aptana.ide.core.ui.CoreUIUtils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public abstract class AbstractWorkbenchWindowPulldownDelegate implements
		IWorkbenchWindowPulldownDelegate2 {

	/**
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
	    String id = action.getId();
	    ToolItem widget = null;

	    WorkbenchWindow window = (WorkbenchWindow) PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow();
	    CoolBarManager manager = window.getCoolBarManager();
	    CoolBar parent = manager.getControl();
	    // this returns the list of actionSets groups
	    IContributionItem[] items = manager.getItems();
	    for (IContributionItem item : items)
	    {
	        if (item instanceof IToolBarContributionItem)
	        {
	            IToolBarContributionItem toolbarItem = (IToolBarContributionItem) item;
	            // this returns the list of actual items for the actions
	            IContributionItem[] children = toolbarItem.getToolBarManager().getItems();
	            for (IContributionItem child : children)
	            {
	                if (child.getId().equals(id))
	                {
	                    // found the toolbar item that corresponds to the action
	                    ActionContributionItem actionItem = (ActionContributionItem) child;
	                    if (CoreUIUtils.inEclipse34orHigher)
	                    {
	                    	// uses the 3.4 API
	                        widget = (ToolItem) actionItem.getWidget();
	                    }
	                    break;
	                }
	            }
	        }
	    }
	    Menu menu = getMenu(parent);
	    if (widget != null)
	    {
	        // sets the location of where the menu is displayed to be the same
            // as when the dropdown arrow is clicked
	        Rectangle bounds = widget.getBounds();
	        Point point = widget.getParent().toDisplay(bounds.x,
                    bounds.y + bounds.height);
	        menu.setLocation(point);
	    }
	    menu.setVisible(true);
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
