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

/**
 * @author Kevin Lindsey
 */
public interface IEventTarget
{

	/**
	 * fireEventListeners
	 * 
	 * @param event
	 */
	void fireEventListeners(Event event);

	/**
	 * fireEventListeners
	 * 
	 * @param eventType
	 * @param args
	 */
	void fireEventListeners(String eventType, Object[] args);

	/**
	 * Add an event handler to this event target
	 * 
	 * @param eventType
	 * @param eventHandler
	 */
	void addEventListener(String eventType, Object eventHandler);

	/**
	 * Remove an event handler from this event target
	 * 
	 * @param eventType
	 * @param eventHandler
	 */
	void removeEventListener(String eventType, Object eventHandler);

}
