/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.eclipse.eclipsemonkey.lang.javascript.doms.editors;

import java.util.ArrayList;
import java.util.Hashtable;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * @author Kevin Lindsey, Paul Colton (Aptana, Inc.)
 */
public class Editors extends ScriptableObject
{
	/*
	 * Fields
	 */
	private static final long serialVersionUID = -1034231442157154583L;
	
	private Hashtable _editorsByType;
	
	/*
	 * Properties
	 */

	/**
	 * @see org.mozilla.javascript.ScriptableObject#getClassName()
	 */
	public String getClassName()
	{
		return Messages.getString("Editors.0"); //$NON-NLS-1$
	}

	/*
	 * Constructors
	 */

	/**
	 * Editors (default contructor-should not be used directly)
	 */
	public Editors()
	{
		System.err.println(Messages.getString("Editors.1")); //$NON-NLS-1$
	}

	/**
	 * Create a new instance of Views
	 * 
	 * @param scope
	 */
	public Editors(Scriptable scope)
	{
		this.setParentScope(scope);

		String[] names = new String[] { 
				//"open", 
				Messages.getString("Editors.2") //$NON-NLS-1$
				};

		this.defineFunctionProperties(names, Editors.class, PERMANENT | READONLY);

		this.defineProperty("all", Editors.class, PERMANENT | READONLY); //$NON-NLS-1$
		this.defineProperty(Messages.getString("Editors.4"), Editors.class, PERMANENT | READONLY); //$NON-NLS-1$
		
		return;
	}

	/*
	 * Methods
	 */

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
	 * getActiveEditor
	 * 
	 * @param thisObj
	 * @return Scriptable
	 */
	public static Object getActiveEditor(ScriptableObject thisObj)
	{
		IEditorPart editor = getActiveEditor();
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
	 * getActiveEditor
	 * 
	 * @return IEditorPart
	 */
	private static IEditorPart getActiveEditor()
	{
		/**
		 * ActiveEditorRef
		 */
		class ActiveEditorRef
		{
			public IEditorPart activeEditor;
		}

		final IWorkbench workbench = PlatformUI.getWorkbench();
		final ActiveEditorRef activeEditor = new ActiveEditorRef();
		Display display = workbench.getDisplay();
		IEditorPart result;

		display.syncExec(new Runnable()
		{
			public void run()
			{
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();

				// this can be null if you close all perspectives
				if (window != null && window.getActivePage() != null)
				{
					activeEditor.activeEditor = window.getActivePage().getActiveEditor();
				}
			}
		});

		result = activeEditor.activeEditor;

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

//	/**
//	 * open the specified file in a new editor
//	 * 
//	 * @param cx
//	 * @param thisObj
//	 * @param args
//	 * @param funObj
//	 * @return Scriptable
//	 */
//	public static Scriptable open(Context cx, Scriptable thisObj, Object[] args, Function funObj)
//	{
//		String filename = Context.toString(args[0]);
//		File file = new File(filename);
//		IEditorPart editor = WorkbenchHelper.openFile(file, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
//
//		if(editor == null)
//		{
//			return null;
//		}
//		
//		return new Editor(thisObj.getParentScope(), editor);
//	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return super.toString();
	}
}
