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

/**
 * @author Kevin Lindsey
 */
public class PartClosedEvent extends Event
{
	/*
	 * Fields
	 */
	private static final long serialVersionUID = -5066742898559352788L;
	private String _id;
	private String _secondaryId;
	private String _title;
	private String _path;

	/**
	 * This event's type name
	 */
	public static final String eventType = "PartClosed"; //$NON-NLS-1$

	/*
	 * Properties
	 */

	/**
	 * get the title of the part
	 * 
	 * @return String
	 */
	public String getTitle()
	{
		return this._title;
	}

	/**
	 * get the part's id
	 * 
	 * @return String
	 */
	public String getId()
	{
		return this._id;
	}

	/**
	 * get the part's secondary id
	 * 
	 * @return String
	 */
	public String getSecondaryId()
	{
		return this._secondaryId;
	}

	/**
	 * @return Returns the editor.
	 */
	public String getPath()
	{
		return _path;
	}

	/*
	 * Constructors
	 */

	/**
	 * PartOpenedEvent
	 * 
	 * @param target
	 * @param title
	 * @param id
	 * @param secondaryId
	 * @param path
	 */
	public PartClosedEvent(Object target, String title, String id, String secondaryId, String path)
	{
		super(eventType, target);

		this._title = title;
		this._id = id;
		this._secondaryId = secondaryId;
		this._path = path;

		this.defineProperty("title", PartClosedEvent.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("id", PartClosedEvent.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("secondaryId", PartClosedEvent.class, READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("path", PartClosedEvent.class, READONLY | PERMANENT); //$NON-NLS-1$
	}
}
