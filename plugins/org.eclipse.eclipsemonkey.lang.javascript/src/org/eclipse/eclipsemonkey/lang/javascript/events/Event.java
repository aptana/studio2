/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.eclipse.eclipsemonkey.lang.javascript.events;

import org.mozilla.javascript.ScriptableObject;

/**
 * @author Kevin Lindsey
 */
public class Event extends ScriptableObject
{
	/*
	 * Fields
	 */
	private static final long serialVersionUID = 1744174189434610182L;
	private String _type;
	private Object _target;

	/**
	 * @see org.mozilla.javascript.ScriptableObject#getClassName()
	 */
	public String getClassName()
	{
		return "Event"; //$NON-NLS-1$
	}

	/**
	 * Get the target where this event was fired
	 * 
	 * @return Returns the source of this event
	 */
	public Object getTarget()
	{
		return this._target;
	}

	/**
	 * Get the type of this event
	 * 
	 * @return Returns the string name of the type of this event
	 */
	public String getType()
	{
		return this._type;
	}

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of Event
	 * 
	 * @param type
	 *            The event type
	 * @param target
	 *            The object that threw this event
	 */
	public Event(String type, Object target)
	{
		this._type = type;
		this._target = target;

		this.defineProperty("target", Event.class, READONLY); //$NON-NLS-1$
		this.defineProperty("type", Event.class, READONLY); //$NON-NLS-1$
	}
}

