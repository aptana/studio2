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
package com.aptana.ide.editors.ui.views.problems;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;

import com.aptana.ide.core.ui.PreferenceUtils;
import com.aptana.ide.editors.actions.ShowErrors;
import com.aptana.ide.editors.actions.ShowInfos;
import com.aptana.ide.editors.actions.ShowWarnings;
import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.editors.unified.UnifiedMessagePage;

/**
 * This class represents the problems view and maintains a page book of all the problems for all the open Aptana
 * editors. It will display a default message if opened for an editor that is not an Aptana editor. This view listens to
 * all IEditorPart selection changes to switch between pages.
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class ProblemsView extends PageBookView
{

	/**
	 * Problems view ID
	 */
	public static final String ID = "com.aptana.ide.js.ui.views.problemsView"; //$NON-NLS-1$

	/**
	 * @see org.eclipse.ui.part.PageBookView#createDefaultPage(org.eclipse.ui.part.PageBook)
	 */
	protected IPage createDefaultPage(PageBook book)
	{
		UnifiedMessagePage page = new UnifiedMessagePage();
		initPage(page);
		page.createControl(book);
		page.setMessage(Messages.getString("ProblemsView.NO_ACTIVE_EDITOR")); //$NON-NLS-1$
		PreferenceUtils.registerBackgroundColorPreference(page.getControl(),
		"com.aptana.ide.core.ui.background.color.validationView"); //$NON-NLS-1$
		PreferenceUtils.registerForegroundColorPreference(page.getControl(),
		"com.aptana.ide.core.ui.foreground.color.validationView"); //$NON-NLS-1$
		return page;
	}

	/**
	 * @see org.eclipse.ui.part.PageBookView#doCreatePage(org.eclipse.ui.IWorkbenchPart)
	 */
	protected PageRec doCreatePage(IWorkbenchPart part)
	{
		if (part instanceof IUnifiedEditor)
		{
			IUnifiedEditor editor = (IUnifiedEditor) part;
			ProblemsPage page = new ProblemsPage(editor);
			initPage(page);
			page.createControl(getPageBook());
			return new PageRec(part, page);
		}
		return null;
	}

	/**
	 * @see org.eclipse.ui.part.PageBookView#doDestroyPage(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.ui.part.PageBookView.PageRec)
	 */
	protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord)
	{
		pageRecord.page.dispose();
		pageRecord.dispose();
	}

	/**
	 * @see org.eclipse.ui.part.PageBookView#getBootstrapPart()
	 */
	protected IWorkbenchPart getBootstrapPart()
	{
		IWorkbenchPage page = getSite().getPage();
		if (page != null)
		{
			return page.getActiveEditor();
		}
		return null;
	}

	/**
	 * @see org.eclipse.ui.part.PageBookView#isImportant(org.eclipse.ui.IWorkbenchPart)
	 */
	protected boolean isImportant(IWorkbenchPart part)
	{
		return part instanceof IEditorPart;
	}

	/**
	 * @see org.eclipse.ui.part.PageBookView#init(org.eclipse.ui.IViewSite)
	 */
	public void init(IViewSite site) throws PartInitException {
		site.getActionBars().getToolBarManager().add(new ShowInfos());
		site.getActionBars().getToolBarManager().add(new ShowWarnings());
		site.getActionBars().getToolBarManager().add(new ShowErrors());
		super.init(site);
	}
}
