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
package com.aptana.ide.views.outline;

import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.aptana.ide.editors.unified.ContributedOutline;

/**
 * @author Kevin Sawicki
 */
public class SplitOutlinesAction extends BaseAction
{
	private static ImageDescriptor SPLIT_OUTLINES_ICON = getImageDescriptor("icons/split_outlines.gif"); //$NON-NLS-1$
	
	/**
	 * SplitOutlinesAction
	 * 
	 * @param page
	 */
	public SplitOutlinesAction(IUnifiedOutlinePage page)
	{
		super(page, SPLIT_OUTLINES_ICON, Messages.SplitOutlinesAction_LBL_SplitView, Action.AS_CHECK_BOX);
	}
	
	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run()
	{
		super.run();
		
		Composite composite = (Composite) page.getControl();
		SashForm outlineSash = page.getOutlineSash();
		CTabFolder outlineTabs = page.getOutlineTabs();
		
		if (this.isChecked())
		{
			Control[] tabs = outlineTabs.getTabList();
			Control[] sections = outlineSash.getTabList();
			for (int i = 0; i < tabs.length; i++)
			{
				tabs[i].setParent((Composite) sections[i]);
				tabs[i].setVisible(true);
			}
			outlineSash.setMaximizedControl(null);
			((GridData) outlineTabs.getLayoutData()).exclude = true;
			((GridData) outlineSash.getLayoutData()).exclude = false;
			outlineTabs.setVisible(false);
			outlineSash.setVisible(true);
			composite.layout(true, true);
			sections = outlineSash.getTabList();
			for (int i = 0; i < sections.length; i++)
			{
				sections[i].getParent().layout(true, true);
			}
		}
		else
		{
			Map<String,ContributedOutline> outlines = page.getContributedOutlines();
			
			outlineTabs.setVisible(true);
			outlineSash.setVisible(false);
			CTabItem[] ctabs = outlineTabs.getItems();
			treeViewer.getControl().getParent().setParent(outlineTabs);
			ctabs[0].setControl(treeViewer.getControl().getParent());
			for (int i = 1; i < ctabs.length; i++)
			{
				ContributedOutline outline = outlines.get(ctabs[i].getText());
				outline.getParent().setParent(outlineTabs);
				ctabs[i].setControl(outline.getParent());
			}
			outlineTabs.getItem(0).getControl().setVisible(true);
			outlineTabs.setSelection(0);
			((GridData) outlineSash.getLayoutData()).exclude = true;
			((GridData) outlineTabs.getLayoutData()).exclude = false;
			
			composite.layout(true, true);
		}
	}
}
