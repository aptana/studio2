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
package com.aptana.ide.scripting.menus;

import java.util.Hashtable;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.aptana.ide.scripting.events.MenuSelectedEvent;

/**
 * @author Kevin Lindsey
 */
public class Menus extends ScriptableObject
{
	/*
	 * Fields
	 */
	private static final long serialVersionUID = 9204365910016489311L;
	private static Hashtable _menus;

	/*
	 * Properties
	 */

	/**
	 * @see org.mozilla.javascript.ScriptableObject#getClassName()
	 */
	public String getClassName()
	{
		return "Menus"; //$NON-NLS-1$
	}

	/*
	 * Constructors
	 */

	/**
	 * static constructor
	 */
	static
	{
		_menus = new Hashtable();
	}

	/**
	 * Create a new instance Menus
	 * 
	 * @param scope
	 */
	public Menus(Scriptable scope)
	{
		this.setParentScope(scope);

		String[] names = new String[] { "getMenu" }; //$NON-NLS-1$
		this.defineFunctionProperties(names, Menus.class, READONLY);
	}

	/*
	 * Methods
	 */

	/**
	 * addMenu
	 * 
	 * @param name
	 */
	public static void addMenu(String name)
	{
		if (_menus.containsKey(name) == false)
		{
			MenuItem item = new MenuItem(name);

			_menus.put(name, item);
		}

	}

	/**
	 * removeMenu
	 * 
	 * @param name
	 */
	public static void removeMenu(String name)
	{
		_menus.remove(name);
	}

	/**
	 * getMenu
	 * 
	 * @param cx
	 * @param thisObj
	 * @param args
	 * @param funObj
	 * @return Object
	 */
	public static Object getMenu(Context cx, Scriptable thisObj, Object[] args, Function funObj)
	{
		String name = Context.toString(args[0]);
		Object result;

		if (_menus.containsKey(name))
		{
			result = _menus.get(name);
		}
		else
		{
			result = new MenuItem(name);

			_menus.put(name, result);
		}

		return result;
	}

	/**
	 * fireEventListeners
	 * 
	 * @param name
	 */
	public static void fireEventListeners(String name)
	{
		if (_menus.containsKey(name))
		{
			MenuItem item = (MenuItem) _menus.get(name);
			MenuSelectedEvent event = new MenuSelectedEvent(item);

			item.fireEventListeners(event);
		}
	}
}
