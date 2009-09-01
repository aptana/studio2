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
package com.aptana.ide.scripting.events;

import java.util.ArrayList;
import java.util.Hashtable;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.aptana.ide.scripting.Global;
import com.aptana.ide.scripting.ScriptingEngine;

/**
 * @author Kevin Lindsey
 */
public abstract class EventTarget extends ScriptableObject implements IEventTarget
{
	/*
	 * Fields
	 */
	private Hashtable _events;

	/*
	 * Constructors
	 */

	/**
	 * Create a new instance of EventTarget
	 */
	public EventTarget()
	{
		String[] names = new String[] { "addEventListener", "removeEventListener" }; //$NON-NLS-1$ //$NON-NLS-2$

		this.defineFunctionProperties(names, EventTarget.class, READONLY | PERMANENT);
	}

	/*
	 * Methods
	 */

	/**
	 * @see com.aptana.ide.scripting.events.IEventTarget#fireEventListeners(com.aptana.ide.scripting.events.Event)
	 */
	public void fireEventListeners(Event event)
	{
		this.fireEventListeners(event.getType(), new Object[] { event });
	}

	/**
	 * @see com.aptana.ide.scripting.events.IEventTarget#fireEventListeners(java.lang.String, java.lang.Object[])
	 */
	public void fireEventListeners(String eventType, Object[] args)
	{
		if (this._events != null && this._events.containsKey(eventType))
		{
			ArrayList handlers = (ArrayList) _events.get(eventType);

			for (int i = 0; i < handlers.size(); i++)
			{
				Object eventHandler = handlers.get(i);

				if (eventHandler instanceof Function)
				{
					Function f = (Function) eventHandler;
					Scriptable scope = f.getParentScope();
					String ID = Context.toString(scope.get(Global.idPropertyName, scope));
					ScriptingEngine.getInstance().fireCallback(ID, eventHandler, args);
				}
			}
		}

	}

	/**
	 * @see com.aptana.ide.scripting.events.IEventTarget#addEventListener(java.lang.String, java.lang.Object)
	 */
	public void addEventListener(String eventType, Object eventHandler)
	{
		if (this._events == null)
		{
			this._events = new Hashtable();
		}

		if (this._events.containsKey(eventType) == false)
		{
			this._events.put(eventType, new ArrayList());
		}

		ArrayList handlers = (ArrayList) this._events.get(eventType);

		handlers.add(eventHandler);
	}

	/**
	 * @see com.aptana.ide.scripting.events.IEventTarget#removeEventListener(java.lang.String, java.lang.Object)
	 */
	public void removeEventListener(String eventType, Object eventHandler)
	{
		if (this._events != null)
		{
			if (this._events.containsKey(eventType))
			{
				ArrayList handlers = (ArrayList) this._events.get(eventType);

				handlers.remove(eventHandler);
			}
		}
	}
}
