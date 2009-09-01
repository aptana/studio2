/** 
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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
package com.aptana.ide.editors.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.UnifiedEditor;
import com.aptana.ide.views.outline.UnifiedQuickOutlinePage;

/**
 * Quick outline action
 * @author Denis Denisenko
 */
public class QuickOutlineAction extends UnifiedEditorAction
{
    
    /**
     * ACTION_ID
     */
    public static final String ACTION_ID = UnifiedEditorsPlugin.getDefault().getBundle().getSymbolicName()
            + ".quickOutlineAction"; //$NON-NLS-1$
    
    /**
     * QuickOutlineAction constructor.
     */
    public QuickOutlineAction()
    {
        super(ACTION_ID, com.aptana.ide.editors.actions.Messages.QuickOutlineAction_0);
    }

    private class QuickOutlinePopupDialog extends PopupDialog 
        implements UnifiedQuickOutlinePage.ICloseListener
    {
        /**
         * Content outline page.
         */
        private ContentOutlinePage page;

        /**
         * QuickOutlinePopupDialog constructor.
         * @param parent - parent.
         * @param editor - editor.
         * @param infoText - info text.
         */
        public QuickOutlinePopupDialog(Shell parent, ContentOutlinePage page,
                String infoText)
        {
            super(parent, SWT.BORDER | SWT.RESIZE, true, false, true,
                    true, null, infoText);
            this.page = page;
        }

        /**
          * {@inheritDoc}
          */
        @Override
        protected Control createDialogArea(Composite parent)
        {
            Composite composite = new Composite(parent, SWT.NONE);
            composite.setLayout(new GridLayout());
            if (page instanceof UnifiedQuickOutlinePage)
            {
                ((UnifiedQuickOutlinePage) page).createControl(composite, false);
                ((UnifiedQuickOutlinePage) page).addCloseListener(this);
                
                ISourceViewer viewer = getActiveEditor().getViewer();
                int offset = viewer.getTextWidget().getCaretOffset();
                if (viewer instanceof ITextViewerExtension5)
                {
                    ITextViewerExtension5 v5 = (ITextViewerExtension5) viewer;
                    offset = v5.widgetOffset2ModelOffset(offset);
                }
                if (offset != -1)
                {
                    ((UnifiedQuickOutlinePage) page).revealPosition(offset);
                }
            }            
            else
            {
                page.createControl(composite);
            }
            
            GridData gd = new GridData(GridData.FILL_BOTH);
            gd.widthHint = 320;
            gd.heightHint = 240;
            composite.setLayoutData(gd);
            
            return composite;
        }

        /**
          * {@inheritDoc}
          */
        @Override
        protected Control createTitleControl(Composite parent)
        {
            if (page instanceof UnifiedQuickOutlinePage)
            {
                return ((UnifiedQuickOutlinePage) page).createSearchArea(parent, true);
            }
            else
            {
                return super.createTitleControl(parent);
            }
        }

        /**
          * {@inheritDoc}
          */
        @Override
        protected void fillDialogMenu(IMenuManager dialogMenu)
        {
            super.fillDialogMenu(dialogMenu);
            if (page instanceof UnifiedQuickOutlinePage)
            {
                ((UnifiedQuickOutlinePage) page).contributeToQuickOutlineMenu(dialogMenu);
            }
        }

        /**
          * {@inheritDoc}
          */
        @Override
        protected Control getFocusControl()
        {
            if (page instanceof UnifiedQuickOutlinePage)
            {
                return ((UnifiedQuickOutlinePage) page).getSearchBox();
            }
            else
            {
                return super.getFocusControl();
            }
        }

        /**
          * {@inheritDoc}
          */
        public void doClose()
        {
            close();
        }
    }
    

    /**
      * {@inheritDoc}
      */
    @Override
    public void run()
    {
        if (getActiveEditor() != null && getActiveEditor() instanceof UnifiedEditor)
        {
            ContentOutlinePage page = ((UnifiedEditor) getActiveEditor()).createQuickOutlinePage();
            if (page != null)
            {
                QuickOutlinePopupDialog dialog = new QuickOutlinePopupDialog(
                        Display.getCurrent().getShells()[0],
                        page, null);
                dialog.open();
            }
        }
    }
}
