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

import org.eclipse.eclipsemonkey.lang.javascript.events.EventTarget;
import org.eclipse.ui.IWorkbenchPart;
import org.mozilla.javascript.Scriptable;

/**
 * @author Paul Colton (Aptana, Inc.)
 *
 */
public class View extends EventTarget {
	/*
	 * Fields
	 */
	private static final long serialVersionUID = 4997730027595762224L;

	private IWorkbenchPart _view;

	/*
	 * Properties
	 */

	/**
	 * getClassName
	 * 
	 * @return String
	 */
	public String getClassName()
	{
		return "View"; //$NON-NLS-1$
	}

	/**
	 * Get the underlying view
	 * 
	 * @return IWorkbenchPart
	 */
	public IWorkbenchPart getView()
	{
		return this._view;
	}

	/**
	 * setView
	 * 
	 * @param view
	 */
	public void setView(IWorkbenchPart view)
	{
		this._view = view;
	}

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of View
	 * 
	 * @param scope
	 * @param view
	 */
	public View(Scriptable scope, IWorkbenchPart view)
	{
		if (scope == null)
		{
			throw new IllegalArgumentException(Messages.View_Scope_Not_Defined);
		}

		this.setParentScope(scope);
		this.setView(view);
	}
}