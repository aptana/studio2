/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.eclipse.eclipsemonkey.lang.javascript.doms.views;

import java.util.Hashtable;

import org.eclipse.eclipsemonkey.lang.javascript.events.EventTarget;
import org.eclipse.eclipsemonkey.utils.UIUtils;
import org.eclipse.ui.IWorkbenchPart;
import org.mozilla.javascript.Scriptable;

/**
 * @author Paul Colton (Aptana, Inc.)
 *
 */
public class Views extends EventTarget {
	/*
	 * Fields
	 */
	private static final long serialVersionUID = -1034231442157154583L;
	
	private Hashtable _scriptableViews;
	
	/*
	 * Properties
	 */

	/**
	 * @see org.mozilla.javascript.ScriptableObject#getClassName()
	 */
	public String getClassName()
	{
		return "Views"; //$NON-NLS-1$
	}
	
	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of Views
	 * 
	 * @param scope
	 */
	public Views(Scriptable scope)
	{
		this.setParentScope(scope);

		this._scriptableViews = new Hashtable();

		String[] names = new String[] { "getView" }; //$NON-NLS-1$
		this.defineFunctionProperties(names, Views.class, READONLY | PERMANENT);

//		this.defineProperty("all", Views.class, READONLY | PERMANENT); //$NON-NLS-1$
//		this.defineProperty("actionsView", Views.class, READONLY | PERMANENT); //$NON-NLS-1$
//		this.defineProperty("navigatorView", Views.class, READONLY | PERMANENT); //$NON-NLS-1$
//		this.defineProperty("profilesView", Views.class, READONLY | PERMANENT); //$NON-NLS-1$
//		this.defineProperty("outlineView", Views.class, READONLY | PERMANENT); //$NON-NLS-1$
//		this.defineProperty("problemsView", Views.class, READONLY | PERMANENT); //$NON-NLS-1$
	}
	
	/**
	 * getView Get a generic scritable view
	 * 
	 * @param id
	 * @return ProblemsView
	 */
	public ScriptableView getView(String id)
	{
		ScriptableView _scriptableView = null;

		if (id == null)
		{
			return null;
		}

		if (_scriptableViews.containsKey(id))
		{
			_scriptableView = (ScriptableView) _scriptableViews.get(id);
		}
		else
		{
			IWorkbenchPart part = getViewInternal("org.eclipse.eclipsemonkey.ui.scriptableView.GenericScriptableView", id); //$NON-NLS-1$

			_scriptableView = new ScriptableView(this.getParentScope(), part, id);
			_scriptableViews.put(id, _scriptableView);
		}

		return _scriptableView;
	}
	
	/**
	 * getViewInternal
	 * 
	 * @param id
	 * @return Scriptable
	 */
	public static IWorkbenchPart getViewInternal(final String id)
	{
		return getViewInternal(id, null);
	}

	/**
	 * getViewInternal
	 * 
	 * @param id
	 * @param secondaryId
	 * @return IWorkbenchPart
	 */
	public static IWorkbenchPart getViewInternal(String id, String secondaryId)
	{
		return UIUtils.getViewInternal(id, secondaryId);
	}
}
