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

import org.eclipse.eclipsemonkey.lang.javascript.events.EventTarget;
import org.mozilla.javascript.ScriptableObject;

/**
 * @author Kevin Lindsey
 */
public class EditorType extends EventTarget
{
	/*
	 * fields
	 */
	private static final long serialVersionUID = -959471594220447372L;

	private String _type;

	/*
	 * Properties
	 */

	/**
	 * getType
	 * 
	 * @return String
	 */
	public String getType()
	{
		return this._type;
	}

	/*
	 * Constructors
	 */

	/**
	 * Create a new instane of EditorType
	 * 
	 * @param type
	 */
	public EditorType(String type)
	{
		this._type = type;

		// define properties
		this.defineProperty("type", EditorType.class, ScriptableObject.READONLY); //$NON-NLS-1$
	}

	/*
	 * Methods
	 */

	/**
	 * @see org.mozilla.javascript.ScriptableObject#getClassName()
	 */
	public String getClassName()
	{
		return "EditorType"; //$NON-NLS-1$
	}
}
