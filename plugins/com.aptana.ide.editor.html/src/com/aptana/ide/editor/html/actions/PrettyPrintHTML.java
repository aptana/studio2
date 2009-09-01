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
package com.aptana.ide.editor.html.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.w3c.tidy.Tidy;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.html.HTMLPlugin;
import com.aptana.ide.editor.html.preferences.IPreferenceConstants;

/**
 * @author Ingo Muschenetz
 */
public class PrettyPrintHTML extends Action implements IEditorActionDelegate, IWorkbenchWindowActionDelegate
{
	IEditorPart part = null;
	ISelection selection = null;

	/**
	 * @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction,
	 *      org.eclipse.ui.IEditorPart)
	 */
	public void setActiveEditor(IAction action, IEditorPart targetEditor)
	{
		part = targetEditor;
	}

	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run()
	{
		if (!(part instanceof ITextEditor))
		{
			return;
		}

		ITextEditor editor = (ITextEditor) part;
		if (editor == null)
		{
			return;
		}

		IDocument document = getDocument(editor);
		if (document == null)
		{
			return;
		}

		IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
		String doc = null;
		Tidy tidy = new Tidy();
		tidy.setAltText(store.getString(IPreferenceConstants.FORMATTING_ALT_TEXT));
		tidy.setBreakBeforeBR(store.getBoolean(IPreferenceConstants.FORMATTING_BREAK_BEFORE_BR));
		tidy.setDropEmptyParas(store.getBoolean(IPreferenceConstants.FORMATTING_DROP_EMPTY_PARAS));
		tidy.setDropFontTags(store.getBoolean(IPreferenceConstants.FORMATTING_DROP_FONT_TAGS));
		tidy.setEncloseBlockText(store.getBoolean(IPreferenceConstants.FORMATTING_ENCLOSE_BLOCK_TEXT));
		tidy.setEncloseText(store.getBoolean(IPreferenceConstants.FORMATTING_ENCLOSE_TEXT));
		tidy.setFixBackslash(store.getBoolean(IPreferenceConstants.FORMATTING_FIX_BACKSLASH));
		tidy.setFixComments(store.getBoolean(IPreferenceConstants.FORMATTING_FIX_COMMENTS));
		tidy.setHideEndTags(store.getBoolean(IPreferenceConstants.FORMATTING_HIDE_END_TAGS));
		tidy.setIndentAttributes(store.getBoolean(IPreferenceConstants.FORMATTING_INDENT_ATTRIBUTES));
		tidy.setLiteralAttribs(store.getBoolean(IPreferenceConstants.FORMATTING_LITERAL_ATTRIBS));
		tidy.setLogicalEmphasis(store.getBoolean(IPreferenceConstants.FORMATTING_LOGICAL_EMPHASIS));
		tidy.setMakeClean(store.getBoolean(IPreferenceConstants.FORMATTING_MAKE_CLEAN));
		tidy.setNumEntities(store.getBoolean(IPreferenceConstants.FORMATTING_NUM_ENTITIES));
		tidy.setQuoteAmpersand(store.getBoolean(IPreferenceConstants.FORMATTING_QUOTE_AMPERSAND));
		tidy.setQuoteMarks(store.getBoolean(IPreferenceConstants.FORMATTING_QUOTE_MARKS));
		tidy.setQuoteNbsp(store.getBoolean(IPreferenceConstants.FORMATTING_QUOTE_NBSP));
		tidy.setSmartIndent(store.getBoolean(IPreferenceConstants.FORMATTING_SMART_INDENT));

		boolean indentContent = store.getBoolean(IPreferenceConstants.FORMATTING_INDENT_CONTENT);
		tidy.setIndentContent(indentContent);
		if (indentContent)
		{
			tidy.setSpaces(store.getInt(IPreferenceConstants.FORMATTING_SPACES_SIZE));
		}

		int tabSize = store.getInt(IPreferenceConstants.FORMATTING_TAB_SIZE);
		if(tabSize > 0)
		{
			tidy.setTabsize(store.getInt(IPreferenceConstants.FORMATTING_TAB_SIZE));
		}
		tidy.setUpperCaseAttrs(store.getBoolean(IPreferenceConstants.FORMATTING_UPPER_CASE_ATTRS));
		tidy.setUpperCaseTags(store.getBoolean(IPreferenceConstants.FORMATTING_UPPER_CASE_TAGS));
		tidy.setWord2000(store.getBoolean(IPreferenceConstants.FORMATTING_WORD_2000));
		tidy.setWrapAsp(store.getBoolean(IPreferenceConstants.FORMATTING_WRAP_ASP));
		tidy.setWrapAttVals(store.getBoolean(IPreferenceConstants.FORMATTING_WRAP_ATTR_VALUES));
		tidy.setWrapJste(store.getBoolean(IPreferenceConstants.FORMATTING_WRAP_JSTE));

		int wrapMargin = store.getInt(IPreferenceConstants.FORMATTING_WRAP_MARGIN);
		if(wrapMargin > 0)
		{
			tidy.setWraplen(store.getInt(IPreferenceConstants.FORMATTING_WRAP_MARGIN));
		}
		
		tidy.setWrapPhp(store.getBoolean(IPreferenceConstants.FORMATTING_WRAP_PHP));
		tidy.setWrapScriptlets(store.getBoolean(IPreferenceConstants.FORMATTING_WRAP_SCRIPTLETS));
		tidy.setWrapSection(store.getBoolean(IPreferenceConstants.FORMATTING_WRAP_SECTION));

		String output = store.getString(IPreferenceConstants.FORMATTING_SET_OUTPUT);
		if ("XHTML".equals(output)) //$NON-NLS-1$
		{
			tidy.setXHTML(true);
		}
		else if ("XML".equals(output)) //$NON-NLS-1$
		{
			tidy.setXmlOut(true);
		}

		TextSelection ts = (TextSelection) selection;
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		tidy.setErrout(pw);

		if (selection instanceof TextSelection && ((TextSelection) selection).getLength() > 0)
		{
			try
			{
				
				doc = document.get(ts.getOffset(), ts.getLength());
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				tidy.parse(new ByteArrayInputStream(doc.getBytes()), baos);
				document.replace(ts.getOffset(), ts.getLength(), baos.toString());
			}
			catch (BadLocationException e)
			{
				IdeLog.logError(HTMLPlugin.getDefault(), Messages.PrettyPrintHTML_Selection_Error);
			}
		}
		else
		{
			doc = document.get();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			tidy.parse(new ByteArrayInputStream(doc.getBytes()), baos);
			if(!baos.toString().equals("") && tidy.getParseErrors() == 0) //$NON-NLS-1$
			{
				document.set(baos.toString());
			}
			else
			{
				try
				{
					pw.flush();
					pw.close();
					sw.flush();
					String errors = sw.toString();
					
					ArrayList statii = new ArrayList();
					String[] lines = errors.split("\r|\n|\r\n"); //$NON-NLS-1$
					for (int i = 0; i < lines.length; i++) {
						String string = lines[i];
						Status s = new Status(Status.ERROR, HTMLPlugin.ID, 0, string, null);	
						statii.add(s);
					}
					MultiStatus ms = new MultiStatus(HTMLPlugin.ID, 0, (IStatus[])statii.toArray(new IStatus[0]), Messages.PrettyPrintHTML_ErrorStatus, null);
					ErrorDialog.openError(Display.getCurrent().getActiveShell(), Messages.PrettyPrintHTML_ErrorTidyTitle, Messages.PrettyPrintHTML_ErrorTidyMessage, ms);
				}
				catch(Exception ex)
				{
					IdeLog.logError(HTMLPlugin.getDefault(), Messages.PrettyPrintHTML_ERR_Tidy, ex);
				}
				
			}
		}		
	}
	
	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action)
	{
		run();
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection)
	{
		this.selection = selection;
	}

	/**
	 * Returns the editor's document.
	 * 
	 * @param editor
	 *            the editor
	 * @return the editor's document
	 */
	private static IDocument getDocument(ITextEditor editor)
	{

		IDocumentProvider documentProvider = editor.getDocumentProvider();
		if (documentProvider == null)
		{
			return null;
		}

		IDocument document = documentProvider.getDocument(editor.getEditorInput());
		if (document == null)
		{
			return null;
		}

		return document;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose()
	{
		// TODO Auto-generated method stub
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window)
	{
		// TODO Auto-generated method stub
	}
}