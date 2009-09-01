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
package com.aptana.ide.editor.html.toolbar;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.aptana.ide.editors.toolbar.IToolBarMember;
import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.editors.unified.UnifiedEditor;

/**
 * @author Pavel Petrochenko
 */
public class InsertTableItem implements IToolBarMember
{

	/**
	 * 
	 */
	public InsertTableItem()
	{
	}

	/**
	 * @see com.aptana.ide.editors.toolbar.IToolBarMember#execute(com.aptana.ide.editors.unified.IUnifiedEditor,
	 *      java.lang.String)
	 */
	public void execute(final IUnifiedEditor editor, String string)
	{
		IDocument doc = (IDocument) editor.getViewer().getDocument();
		String lineSeparator=System.getProperty("line.separator"); //$NON-NLS-1$
		if (doc instanceof IDocumentExtension4) {
			IDocumentExtension4 ext = (IDocumentExtension4) doc;
			lineSeparator = ext.getDefaultLineDelimiter();
		}		
		IEditorInput editorInput = editor.getEditorInput();
		IProject prj=null;
		if (editorInput instanceof FileEditorInput){
			prj=((FileEditorInput)editorInput).getFile().getProject();
		}
		InsertTableDialog dlg = new InsertTableDialog(editor.getViewer().getTextWidget().getShell(),lineSeparator,prj);
		int open = dlg.open();
		if (open == Dialog.OK)
		{
			String content = dlg.getHTMLText();
			TextSelection selection = (TextSelection) editor.getViewer().getSelectionProvider().getSelection();
			final int selectionOffset = selection.getOffset();
			int selectionLength = selection.getLength();
			
			if (content != null)
			{
				final int contentLength = content.length();

				try
				{
					// replace the current selection
					doc.replace(selectionOffset, selectionLength, content);

					final IWorkbench workbench = PlatformUI.getWorkbench();
					Display display = workbench.getDisplay();

					display.asyncExec(new Runnable()
					{
						public void run()
						{
							((UnifiedEditor) editor).selectAndReveal(selectionOffset, contentLength);
						}
					});
				}
				catch (BadLocationException e)
				{
					// e.printStackTrace();
				}
			}
		}
	}
}
