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

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.ui.WorkbenchHelper;
import com.aptana.ide.editor.html.HTMLEditor;
import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.editors.unified.errors.IFileError;
import com.aptana.ide.scripting.ScriptingEngine;
import com.aptana.ide.scripting.ScriptingPlugin;

/**
 * @author Kevin Lindsey
 */
public class Editors extends ScriptableObject
{
	/*
	 * Fields
	 */
	private static final long serialVersionUID = -1034231442157154583L;
	
	private Hashtable _editorsByType;
	private Callable _bracketMatcher;
	
	/**
	 * UNIFIED_EDITOR
	 */
	public static final int UNIFIED_EDITOR = 0;
	
	/**
	 * CSS_EDITOR
	 */
	public static final int CSS_EDITOR = 1;
	
	/**
	 * HTML_EDITOR
	 */
	public static final int HTML_EDITOR = 2;
	
	/**
	 * HTML_EDITOR
	 */
	public static final int JAVASCRIPT_EDITOR = 3;
	
	/**
	 * XML_EDITOR
	 */
	public static final int XML_EDITOR = 4;
	

	/*
	 * Properties
	 */

	/**
	 * @see org.mozilla.javascript.ScriptableObject#getClassName()
	 */
	public String getClassName()
	{
		return "Editors"; //$NON-NLS-1$
	}

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of Views
	 * 
	 * @param scope
	 */
	public Editors(Scriptable scope)
	{
		this.setParentScope(scope);

		String[] names = new String[] {
			"getEditorType", //$NON-NLS-1$
			"open", //$NON-NLS-1$
			"applyEditToActiveEditor" //$NON-NLS-1$
		};

		this.defineFunctionProperties(names, Editors.class, PERMANENT | READONLY);

		this.defineProperty("all", Editors.class, PERMANENT | READONLY); //$NON-NLS-1$
		this.defineProperty("activeEditor", Editors.class, PERMANENT | READONLY); //$NON-NLS-1$
		this.defineProperty("activeErrors", Editors.class, PERMANENT | READONLY); //$NON-NLS-1$
		this.defineProperty("bracketMatcher", Editors.class, PERMANENT); //$NON-NLS-1$
		
		this.defineProperty("UNIFIED_EDITOR", new Integer(UNIFIED_EDITOR), PERMANENT | READONLY); //$NON-NLS-1$
		this.defineProperty("CSS_EDITOR", new Integer(CSS_EDITOR), PERMANENT | READONLY); //$NON-NLS-1$
		this.defineProperty("HTML_EDITOR", new Integer(HTML_EDITOR), PERMANENT | READONLY); //$NON-NLS-1$
		this.defineProperty("JAVASCRIPT_EDITOR", new Integer(JAVASCRIPT_EDITOR), PERMANENT | READONLY); //$NON-NLS-1$
		this.defineProperty("XML_EDITOR", new Integer(XML_EDITOR), PERMANENT | READONLY); //$NON-NLS-1$
	}

	/*
	 * Methods
	 */

	/**
	 * setBracketMatcher
	 * 
	 * @param callback
	 */
	public void setBracketMatcher(Object callback)
	{
		if (callback instanceof Callable)
		{
			_bracketMatcher = (Callable) callback;
		}
		else
		{
			_bracketMatcher = null;
		}
	}

	/**
	 * getBracketMatcher
	 * 
	 * @return Callable
	 */
	public Callable getBracketMatcher()
	{
		return _bracketMatcher;
	}

	/**
	 * getAll
	 * 
	 * @param thisObj
	 * @return Scriptable
	 */
	public static Scriptable getAll(ScriptableObject thisObj)
	{
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final ArrayList editors = new ArrayList();
		Display display = workbench.getDisplay();
		final Scriptable scope = thisObj.getParentScope();

		display.syncExec(new Runnable()
		{
			public void run()
			{
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
				IWorkbenchPage[] pages = window.getPages();

				for (int i = 0; i < pages.length; i++)
				{
					IWorkbenchPage page = pages[i];
					IEditorReference[] editorRefs = page.getEditorReferences();

					for (int j = 0; j < editorRefs.length; j++)
					{
						IEditorPart editor = editorRefs[j].getEditor(false);

						editors.add(new Editor(scope, editor));
					}
				}
			}
		});

		Context cx = Context.getCurrentContext();
		Object[] args = editors.toArray();

		return cx.newArray(scope, args);
	}

	/**
	 * applyEditToActiveEditor
	 * 
	 * @param cx
	 * @param thisObj
	 * @param args
	 * @param funObj
	 * @return Object
	 */
	public static Object applyEditToActiveEditor(Context cx, Scriptable thisObj, Object[] args, Function funObj)
	{
		final String insertText = Context.toString(args[0]);

		final IWorkbench workbench = PlatformUI.getWorkbench();
		Display display = workbench.getDisplay();

		display.asyncExec(new Runnable()
		{
			public void run()
			{
				int offset = -1;
				IEditorPart activeEditor = null;
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();

				// this can be null if you close all perspectives
				if (window != null && window.getActivePage() != null)
				{
					activeEditor = window.getActivePage().getActiveEditor();

					if (activeEditor instanceof IUnifiedEditor)
					{
						ITextEditor editor = (ITextEditor) activeEditor;
						IDocumentProvider dp = editor.getDocumentProvider();
						IDocument doc = dp.getDocument(editor.getEditorInput());

						StyledText textWidget = ((IUnifiedEditor) editor).getViewer().getTextWidget();

						if (textWidget != null)
						{
							offset = textWidget.getCaretOffset();

							try
							{
								doc.replace(offset, 0, insertText);
							}
							catch (BadLocationException e)
							{
								IdeLog.logError(ScriptingPlugin.getDefault(), Messages.Editors_Error, e);
							}
						}
					}
				}
			}
		});

		return null;
	}

	/**
	 * getActiveEditor
	 * 
	 * @param thisObj
	 * @return Scriptable
	 */
	public static Object getActiveEditor(ScriptableObject thisObj)
	{
		IEditorPart editor = ScriptingEngine.getActiveEditor();
		Object result;

		if (editor != null)
		{
			result = new Editor(thisObj.getParentScope(), editor);
		}
		else
		{
			result = Context.getUndefinedValue();
		}

		return result;
	}

	/**
	 * getActiveErrors
	 * 
	 * @param thisObj
	 * @return IFileError[]
	 */
	public static IFileError[] getActiveErrors(ScriptableObject thisObj)
	{
		IEditorPart part = ScriptingEngine.getActiveEditor();
		IFileError[] result;

		if (part != null && part instanceof IUnifiedEditor)
		{
			IUnifiedEditor editor = (IUnifiedEditor) part;
			result = editor.getFileContext().getFileErrors();
		}
		else
		{
			result = new IFileError[0]; // Context.getUndefinedValue();
		}

		return result;
	}

	/**
	 * getAllEventTargets
	 * 
	 * @return EditorType[]
	 */
	public EditorType[] getAllEventTargets()
	{
		if (this._editorsByType != null)
		{
			return (EditorType[]) this._editorsByType.values().toArray(new EditorType[0]);
		}
		else
		{
			return new EditorType[0];
		}
	}

	/**
	 * getEventTarget
	 * 
	 * @param type
	 * @return EditorType
	 */
	public EditorType getEventTarget(String type)
	{
		if (this._editorsByType != null && this._editorsByType.containsKey(type))
		{
			return (EditorType) this._editorsByType.get(type);
		}
		else
		{
			return null;
		}
	}

	/**
	 * get editor type for the
	 * 
	 * @param cx
	 * @param thisObj
	 * @param args
	 * @param funObj
	 * @return Scriptable
	 */
	public static Scriptable getEditorType(Context cx, Scriptable thisObj, Object[] args, Function funObj)
	{
		Editors instance = (Editors) thisObj;
		String type = Context.toString(args[0]);

		if (instance._editorsByType == null)
		{
			instance._editorsByType = new Hashtable();
		}

		if (instance._editorsByType.containsKey(type) == false)
		{
			EditorType editorType = new EditorType(type);

			editorType.setParentScope(instance.getParentScope());
			instance._editorsByType.put(type, editorType);
		}

		return (EditorType) instance._editorsByType.get(type);
	}

	/**
	 * open the specified file in a new editor
	 * 
	 * @param cx
	 * @param thisObj
	 * @param args
	 * @param funObj
	 * @return Scriptable
	 */
	public static Scriptable open(Context cx, Scriptable thisObj, Object[] args, Function funObj)
	{
		String filename = Context.toString(args[0]);
		File file = new File(filename);
		IEditorPart editor = WorkbenchHelper.openFile(file, PlatformUI.getWorkbench().getActiveWorkbenchWindow());

		if(editor == null)
		{
			return null;
		}
		
		if (editor instanceof HTMLEditor)
		{
			HTMLEditor html = (HTMLEditor) editor;
			editor = html.getSourceEditor();
		}

		return new Editor(thisObj.getParentScope(), editor);
	}
}
