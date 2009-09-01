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
package com.aptana.ide.scripting.editors;

import java.io.IOException;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.DocumentRewriteSessionType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.FileService;
import com.aptana.ide.editors.unified.IFileService;
import com.aptana.ide.editors.unified.IFileSourceProvider;
import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.scripting.ScriptingPlugin;
import com.aptana.ide.scripting.events.EventTarget;
import com.aptana.ide.scripting.parsing.LexemeList;
import com.aptana.ide.scripting.parsing.ParseResults;

/**
 * @author Kevin Lindsey
 */
public class Editor extends EventTarget
{
	/*
	 * Fields
	 */
	private static final long serialVersionUID = 2489151579665730844L;
	private IEditorPart _editor;
	private DocumentRewriteSession _key;

	/*
	 * Properties
	 */

	/**
	 * @see org.mozilla.javascript.ScriptableObject#getClassName()
	 */
	public String getClassName()
	{
		return "Editor"; //$NON-NLS-1$
	}

	/**
	 * getDocument
	 * 
	 * @return IDocument
	 */
	private IDocument getDocument()
	{
		IUnifiedEditor uniEditor = this.getUnifiedEditor();
		IDocument result = null;

		if (uniEditor != null)
		{
			ISourceViewer viewer = uniEditor.getViewer();

			if (viewer != null)
			{
				result = viewer.getDocument();
			}
		}

		return result;
	}

	/**
	 * Get this editor's file service
	 * 
	 * @return FileService
	 */
	private FileService getFileService()
	{
		IUnifiedEditor uniEditor = this.getUnifiedEditor();
		FileService result = null;

		if (uniEditor != null)
		{
			IFileService service = uniEditor.getFileContext();

			if (service instanceof EditorFileContext)
			{
				service = ((EditorFileContext) service).getFileContext();
			}

			if (service instanceof FileService)
			{
				result = (FileService) service;
			}
		}

		return result;
	}

	/**
	 * Get this editor's source provider
	 * 
	 * @return IFileSourceProvider
	 */
	private IFileSourceProvider getSourceProvider()
	{
		FileService fileService = this.getFileService();
		IFileSourceProvider result = null;

		if (fileService != null)
		{
			result = fileService.getSourceProvider();
		}

		return result;
	}

	/**
	 * getStyledText
	 * 
	 * @return StyledText
	 */
	private StyledText getStyledText()
	{
		IUnifiedEditor uniEditor = this.getUnifiedEditor();
		StyledText result = null;

		if (uniEditor != null)
		{
			ISourceViewer viewer = uniEditor.getViewer();

			if (viewer != null)
			{
				result = viewer.getTextWidget();
			}
		}

		return result;
	}

	/**
	 * Get the unified editor
	 * 
	 * @return Unified editor
	 */
	private IUnifiedEditor getUnifiedEditor()
	{
		IEditorPart part = this._editor;
		IUnifiedEditor result = null;

		if (part instanceof ITextEditor)
		{
			ITextEditor editor = (ITextEditor) part;

			if (editor instanceof IUnifiedEditor)
			{
				result = (IUnifiedEditor) editor;
			}
		}

		return result;
	}

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of Editor
	 * 
	 * @param scope
	 * @param editor
	 */
	public Editor(Scriptable scope, IEditorPart editor)
	{
		this.setParentScope(scope);
		this._editor = editor;

		String[] functions = new String[] {
			"applyEdit", //$NON-NLS-1$
			"beginCompoundChange", //$NON-NLS-1$
			"close", //$NON-NLS-1$
			"endCompoundChange", //$NON-NLS-1$
			"getLineAtOffset", //$NON-NLS-1$
			"getOffsetAtLine", //$NON-NLS-1$
			"save", //$NON-NLS-1$
			"selectAndReveal", //$NON-NLS-1$
			"showSelection", //$NON-NLS-1$
			"toString" //$NON-NLS-1$
		};

		this.defineFunctionProperties(functions, Editor.class, READONLY | PERMANENT);

		// read-only properties
		this.defineProperty("file", Editor.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("id", Editor.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("language", Editor.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("lexemes", Editor.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("lineDelimiter", Editor.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("parseResults", Editor.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("source", Editor.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("sourceLength", Editor.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("selectionRange", Editor.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("tabWidth", Editor.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("title", Editor.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("topIndex", Editor.class, READONLY | PERMANENT); //$NON-NLS-1$
		//this.defineProperty("outline", Editor.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("uri", Editor.class, READONLY | PERMANENT); //$NON-NLS-1$

		// read/write properties
		this.defineProperty("currentOffset", Editor.class, PERMANENT); //$NON-NLS-1$
		this.defineProperty("wordWrap", Editor.class, PERMANENT); //$NON-NLS-1$
	}

	/*
	 * Methods
	 */

	/**
	 * save
	 */
	public void save()
	{
		if (this._editor != null)
		{
			this._editor.doSave(null);
		}
	}

	/**
	 * @param offset
	 * @param length
	 */
	public void selectAndReveal(final int offset, final int length)
	{
		final IWorkbench workbench = PlatformUI.getWorkbench();
		Display display = workbench.getDisplay();

		display.asyncExec(new Runnable()
		{
			public void run()
			{
				IUnifiedEditor uniEditor = getUnifiedEditor();
				uniEditor.selectAndReveal(offset, length);
			}
		});
	}

	/**
	 * getCurrentOffset
	 * 
	 * @return int
	 */
	public int getCurrentOffset()
	{
		/**
		 * ResultRef
		 */
		class ResultRef
		{
			public int result = -1;
		}
		
		final IWorkbench workbench = PlatformUI.getWorkbench();
		Display display = workbench.getDisplay();
		final ResultRef result = new ResultRef();
		
		display.syncExec(new Runnable()
		{
			public void run()
			{
		
				StyledText textWidget = getStyledText();
		
				if (textWidget != null)
				{
					result.result = textWidget.getCaretOffset();
				}
			}
		});
		
		return result.result;
	}

//	/**
//	 * Return the outline for this editor
//	 * 
//	 * @return IUnifiedContentOutlinePage
//	 */
//	public UnifiedOutlinePage getOutline()
//	{
//		IUnifiedEditor uniEditor = this.getUnifiedEditor();
//		UnifiedOutlinePage result = null;
//
//		if (uniEditor != null)
//		{
//			result = uniEditor.getOutlinePage();
//		}
//
//		return result;
//	}

	/**
	 * setCurrentOffset
	 * 
	 * @param offset
	 */
	public void setCurrentOffset(int offset)
	{
		StyledText textWidget = this.getStyledText();

		if (textWidget != null)
		{
			textWidget.setCaretOffset(offset);
		}
	}

	/**
	 * Get the File object associated with this editor
	 * 
	 * @return Returns a File object for this editor's underlying file or undefined
	 */
	public Object getFile()
	{
		IFileSourceProvider sourceProvider = this.getSourceProvider();
		Object result;

		if (sourceProvider != null)
		{
			String sourcePath = sourceProvider.getSourceURI();
			Context cx = Context.getCurrentContext();

			result = cx.newObject(this.getParentScope(), "File", //$NON-NLS-1$
					new Object[] { CoreUIUtils.getPathFromURI(sourcePath) });
		}
		else
		{
			result = Context.getUndefinedValue();
		}

		return result;
	}

	/**
	 * getUri
	 * 
	 * @return String
	 */
	public String getUri()
	{
		IFileSourceProvider sourceProvider = this.getSourceProvider();

		if (sourceProvider != null)
		{
			String uri = sourceProvider.getSourceURI();
			
			return uri;
		}

		return null;
	}

	/**
	 * getLanguage
	 * 
	 * @return Object
	 */
	public Object getLanguage()
	{
		FileService fileService = this.getFileService();
		Object result;

		if (fileService != null)
		{
			result = fileService.getDefaultLanguage();
		}
		else
		{
			result = Context.getUndefinedValue();
		}

		return result;
	}

	/**
	 * getLexemes
	 * 
	 * @return Scriptable
	 */
	public Scriptable getLexemes()
	{
		IFileService service = this.getFileService();
		Context cx = Context.getCurrentContext();
		Scriptable result;

		if (service != null)
		{
			result = new LexemeList(this.getParentScope(), service.getLexemeList());
		}
		else
		{
			result = cx.newArray(this.getParentScope(), 0);
		}

		return result;
	}

	/**
	 * getParseResults
	 * 
	 * @return Scriptable
	 */
	public Scriptable getParseResults()
	{
		IFileService service = this.getFileService();
		Context cx = Context.getCurrentContext();
		Scriptable result;

		if (service != null)
		{
			result = new ParseResults(this.getParentScope(), service.getParseState());
		}
		else
		{
			result = cx.newArray(this.getParentScope(), 0);
		}

		return result;
	}

	/**
	 * getLineDelimiter
	 * 
	 * @return String
	 */
	public String getLineDelimiter()
	{
		IDocument document = this.getDocument();
		String result = "\n"; //$NON-NLS-1$

		if (document != null)
		{
			String[] delims = document.getLegalLineDelimiters();

			if (delims.length > 0)
			{
				result = delims[0];
			}
		}

		return result;
	}

	/**
	 * getId
	 * 
	 * @return String
	 */
	public String getId()
	{
		String result = StringUtils.EMPTY;

		if (this._editor != null)
		{
			result = this._editor.getSite().getId();
		}

		return result;
	}

	/**
	 * getSelectionRange
	 * 
	 * @return Object
	 */
	public Scriptable getSelectionRange()
	{
		StyledText textWidget = this.getStyledText();
		Context cx = Context.getCurrentContext();
		Scriptable result = null;

		if (textWidget != null)
		{
			Point p = textWidget.getSelection();

			result = cx.newObject(this.getParentScope(), "Object", new Object[0]); //$NON-NLS-1$
			result.put("startingOffset", result, new Integer(p.x)); //$NON-NLS-1$
			result.put("endingOffset", result, new Integer(p.y)); //$NON-NLS-1$
		}

		return result;
	}

	/**
	 * getSource
	 * 
	 * @return Scriptable
	 */
	public String getSource()
	{
		IFileSourceProvider sourceProvider = this.getSourceProvider();
		String result = StringUtils.EMPTY;

		if (sourceProvider != null)
		{
			try
			{
				result = sourceProvider.getSource();
			}
			catch (IOException e)
			{
				IdeLog.logError(ScriptingPlugin.getDefault(), Messages.Editor_Error, e);
			}
		}

		return result;
	}

	/**
	 * getSourceLength
	 * 
	 * @return Scriptable
	 */
	public int getSourceLength()
	{
		IFileSourceProvider sourceProvider = this.getSourceProvider();
		int result = 0;

		if (sourceProvider != null)
		{
			result = sourceProvider.getSourceLength();
		}

		return result;
	}

	/**
	 * getTabWidth
	 * 
	 * @return int
	 */
	public int getTabWidth()
	{
		IUnifiedEditor uniEditor = this.getUnifiedEditor();
		int result = 4;

		if (uniEditor != null)
		{
			ISourceViewer viewer = uniEditor.getViewer();

			result = uniEditor.getConfiguration().getTabWidth(viewer);
		}

		return result;
	}

	/**
	 * getTitle
	 * 
	 * @return String
	 */
	public String getTitle()
	{
		IUnifiedEditor uniEditor = this.getUnifiedEditor();

		if (uniEditor != null)
		{
			return ((IEditorPart) uniEditor).getTitle();
		}

		return null;
	}

	/**
	 * getTopIndex
	 * 
	 * @return int
	 */
	public int getTopIndex()
	{
		StyledText textWidget = this.getStyledText();
		int result = -1;

		if (textWidget != null)
		{
			result = textWidget.getTopIndex();
		}

		return result;
	}

	/**
	 * getWordWrap
	 * 
	 * @return boolean
	 */
	public boolean getWordWrap()
	{
		StyledText textWidget = this.getStyledText();
		boolean result = false;

		if (textWidget != null)
		{
			result = textWidget.getWordWrap();
		}

		return result;
	}

	/**
	 * setWordWrap
	 * 
	 * @param wrap
	 */
	public void setWordWrap(boolean wrap)
	{
		StyledText textWidget = this.getStyledText();

		if (textWidget != null)
		{
			textWidget.setWordWrap(wrap);
		}
	}

	/**
	 * applyEdit
	 * 
	 * @param offset
	 * @param deleteLength
	 * @param insertText
	 */
	public void applyEdit(int offset, int deleteLength, String insertText)
	{
		IEditorPart part = this._editor;

		if (part instanceof AbstractTextEditor)
		{
			ITextEditor editor = (ITextEditor) part;
			IDocumentProvider dp = editor.getDocumentProvider();
			IDocument doc = dp.getDocument(editor.getEditorInput());

			try
			{
				doc.replace(offset, deleteLength, insertText);
			}
			catch (BadLocationException e)
			{
				IdeLog.logError(ScriptingPlugin.getDefault(), Messages.Editor_Error, e);
			}
		}
	}

	/**
	 * beginCompoundChange
	 */
	public void beginCompoundChange()
	{
		if (this._key == null)
		{
			IDocument document = this.getDocument();

			if (document != null)
			{
				IDocumentExtension4 docExt = (IDocumentExtension4) document;

				this._key = docExt.startRewriteSession(DocumentRewriteSessionType.SEQUENTIAL);
			}
		}
		else
		{
			throw new IllegalStateException(Messages.Editor_Unclosed_Compound_Change);
		}
	}

	/**
	 * close
	 * 
	 * @param save
	 */
	public void close(boolean save)
	{
		IUnifiedEditor uniEditor = this.getUnifiedEditor();

		if (uniEditor != null)
		{
			uniEditor.close(save);
		}
	}

	/**
	 * endCompoundChange
	 */
	public void endCompoundChange()
	{
		if (this._key != null)
		{
			IDocument document = this.getDocument();

			if (document != null)
			{
				IDocumentExtension4 docExt = (IDocumentExtension4) document;

				docExt.stopRewriteSession(this._key);
			}

			this._key = null;
		}
	}

	/**
	 * getLineAtOffset
	 * 
	 * @param offset
	 * @return int
	 */
	public int getLineAtOffset(int offset)
	{
		StyledText textWidget = this.getStyledText();
		int result = -1;

		if (textWidget != null)
		{
			result = textWidget.getLineAtOffset(offset);
		}

		return result;
	}

	/**
	 * getOffsetAtLine
	 * 
	 * @param line
	 * @return int
	 */
	public int getOffsetAtLine(final int line)
	{
		/**
		 * Result
		 */
		class Result
		{
			public int result;
		}
		
		final Result r = new Result();
		r.result = -1;

		final IWorkbench workbench = PlatformUI.getWorkbench();
		Display display = workbench.getDisplay();

		display.syncExec(new Runnable()
		{
			public void run()
			{
				StyledText textWidget = getStyledText();
				
				if (textWidget != null)
				{
					r.result = textWidget.getOffsetAtLine(line);
				}
			}
		});

		return r.result;
	}

	/**
	 * showSelection
	 */
	public void showSelection()
	{
		StyledText textWidget = this.getStyledText();

		if (textWidget != null)
		{
			textWidget.showSelection();
		}
	}

	/**
	 * toString
	 * 
	 * @return String
	 */
	public String toString()
	{
		return "[object Editor]"; //$NON-NLS-1$
	}
}
