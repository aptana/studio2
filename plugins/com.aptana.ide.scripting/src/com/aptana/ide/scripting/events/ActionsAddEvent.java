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

import org.eclipse.core.runtime.IPath;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

import com.aptana.ide.scripting.ScriptingEngine;
import com.aptana.ide.scripting.io.File;

/**
 * @author Kevin Lindsey
 */
public class ActionsAddEvent extends Event
{
	/*
	 * Fields
	 */
	private static final long serialVersionUID = 7849332809139641052L;

	/**
	 * This event's type name
	 */
	public static final String eventType = "ActionsAddEvent"; //$NON-NLS-1$

	/*
	 * Constructors
	 */

	/**
	 * ActionsAddEvent
	 * 
	 * @param target
	 * @param id
	 * @param paths
	 */
	public ActionsAddEvent(Object target, int id, IPath[] paths)
	{
		super(eventType, target);

		ScriptableObject global = ScriptingEngine.getInstance().getGlobal();
		Object[] files = new Object[paths.length];

		// populate elements use to initialize JS array
		Context cx = Context.enter();

		for (int i = 0; i < paths.length; i++)
		{
			files[i] = cx.newObject(global, "File", new Object[] { paths[i].toFile() }); //$NON-NLS-1$
		}

		// define properties
		this.defineProperty("id", new Integer(id), READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("files", cx.newArray(global, files), READONLY | PERMANENT); //$NON-NLS-1$

		Context.exit();
	}

	/**
	 * ActionsAddEvent
	 * 
	 * @param target
	 * @param id
	 * @param file
	 */
	public ActionsAddEvent(Object target, int id, File file)
	{
		super(eventType, target);

		// populate elements use to intiialize JS array
		ScriptableObject global = ScriptingEngine.getInstance().getGlobal();
		Context cx = Context.enter();

		// define properties
		this.defineProperty("id", new Integer(id), READONLY | PERMANENT); //$NON-NLS-1$
		this.defineProperty("files", cx.newArray(global, new Object[] { file }), READONLY | PERMANENT); //$NON-NLS-1$

		Context.exit();
	}
}
