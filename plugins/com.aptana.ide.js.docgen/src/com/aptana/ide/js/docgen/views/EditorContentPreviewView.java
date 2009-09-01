/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.js.docgen.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IContributedContentsView;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;

import com.aptana.ide.core.ui.PreferenceUtils;
import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.editors.unified.UnifiedMessagePage;

/**
 * View for editor content previews
 * @author Ingo Muschenetz
 *
 */
public class EditorContentPreviewView extends PageBookView {

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
     * Method declared on PageBookView.
     * @param book 
     * @return 
     */
    protected IPage createDefaultPage(PageBook book) {
		UnifiedMessagePage page = new UnifiedMessagePage();
		initPage(page);
		page.createControl(book);
		page.setMessage(Messages.EditorContentPreviewView_NoPreview);
		PreferenceUtils.registerBackgroundColorPreference(page.getControl(),
		"com.aptana.ide.core.ui.background.color.visualscriptdocView"); //$NON-NLS-1$
		PreferenceUtils.registerForegroundColorPreference(page.getControl(),
		"com.aptana.ide.core.ui.foreground.color.visualscriptdocView"); //$NON-NLS-1$
		return page;
    }

    /**
     * The <code>PageBookView</code> implementation of this <code>IWorkbenchPart</code>
     * method creates a <code>PageBook</code> control with its default page showing.
     * @param parent 
     */
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(getPageBook(),
                "context id"); //$NON-NLS-1$
    }

	/**
	 * @see org.eclipse.ui.part.PageBookView#doCreatePage(org.eclipse.ui.IWorkbenchPart)
	 */
	protected PageRec doCreatePage(IWorkbenchPart part)
	{
		if (part instanceof IUnifiedEditor)
		{
			IUnifiedEditor editor = (IUnifiedEditor) part;
			ScriptdocPreviewPage page = new ScriptdocPreviewPage(editor);
			initPage(page);
			page.createControl(getPageBook());
			return new PageRec(part, page);
		}
		return null;
	}

    /**
     * Method declared on PageBookView.
     * @param part 
     * @param rec 
     */
    protected void doDestroyPage(IWorkbenchPart part, PageRec rec) {
        rec.dispose();
    }

    /**
     * Method declared on IAdaptable.
     * @param key 
     * @return object 
     */
    public Object getAdapter(Class key) {
        if (key == IContributedContentsView.class) {
			return new IContributedContentsView() {
                public IWorkbenchPart getContributingPart() {
                    return getContributingEditor();
                }
            };
		}
        return super.getAdapter(key);
    }
    
    /**
     * Returns the editor which contributed the current 
     * page to this view.
     *
     * @return the editor which contributed the current page
     * or <code>null</code> if no editor contributed the current page
     */
    private IWorkbenchPart getContributingEditor() {
        return getCurrentContributingPart();
    }

}
