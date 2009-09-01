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

import org.eclipse.swt.browser.LocationEvent;

/**
 * @author Kevin Lindsey
 */
public class LocationChangingEvent extends Event
{
	/*
	 * Fields
	 */
	private static final long serialVersionUID = -5136024629622265505L;
	private LocationEvent _innerEvent;

	/**
	 * This event's type name
	 */
	public static final String eventType = "LocationChanging"; //$NON-NLS-1$

	/*
	 * Properties
	 */

	/**
	 * getInnerEvent
	 * 
	 * @return LocationEvent
	 */
	public LocationEvent getInnerEvent()
	{
		return this._innerEvent;
	}

	/*
	 * Constructors
	 */

	/**
	 * @param target
	 *            The object that threw this event
	 * @param innerEvent
	 */
	public LocationChangingEvent(Object target, LocationEvent innerEvent)
	{
		super(eventType, target);

		this._innerEvent = innerEvent;

		this.defineProperty("innerEvent", LocationChangingEvent.class, READONLY | PERMANENT); //$NON-NLS-1$
	}
}
