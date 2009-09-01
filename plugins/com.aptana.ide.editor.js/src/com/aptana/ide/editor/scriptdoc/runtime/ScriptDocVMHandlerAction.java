/* ***** BEGIN LICENSE BLOCK *****
 * Version: GPL 3
 *
 * This program is Copyright (C) 2007-2008 Aptana, Inc. All Rights Reserved
 * This program is licensed under the GNU General Public license, version 3 (GPL).
 *
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by the GPL,
 * is prohibited.
 *
 * You can redistribute and/or modify this program under the terms of the GPL, 
 * as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * You may view the GPL, and Aptana's exception and additional terms in the file
 * titled license-jaxer.html in the main distribution folder of this program.
 * 
 * Any modifications to this file must keep this entire header intact.
 *
 * ***** END LICENSE BLOCK ***** */
package com.aptana.ide.editor.scriptdoc.runtime;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kevin Lindsey
 */
public enum ScriptDocVMHandlerAction
{
	ADD_STATEMENT("AddStatement"), //$NON-NLS-1$
	DUPLICATE("Duplicate"), //$NON-NLS-1$
	FIRST_STATEMENT("FirstStatement"), //$NON-NLS-1$
	GET("Get"), //$NON-NLS-1$
	GET_GLOBAL("GetGlobal"), //$NON-NLS-1$
	INSTANTIATE("Instantiate"), //$NON-NLS-1$
	INVOKE("Invoke"), //$NON-NLS-1$
	NO_OPERATION("NoOperation"), //$NON-NLS-1$
	POP("Pop"), //$NON-NLS-1$
	PUSH("Push"), //$NON-NLS-1$
	PUT("Put"), //$NON-NLS-1$
	SWAP("Swap"); //$NON-NLS-1$
	
	private static final Map<String,ScriptDocVMHandlerAction> NAME_MAP;
	private String _name;
	
	/**
	 * static constructor
	 */
	static
	{
		NAME_MAP = new HashMap<String,ScriptDocVMHandlerAction>();
		
		for (ScriptDocVMHandlerAction action : EnumSet.allOf(ScriptDocVMHandlerAction.class))
		{
			NAME_MAP.put(action.getName(), action);
		}
	}
	
	/**
	 * JSHandleActions
	 * 
	 * @param name
	 */
	private ScriptDocVMHandlerAction(String name)
	{
		this._name = name;
	}
	
	/**
	 * get
	 * 
	 * @param name
	 * @return
	 */
	public static final ScriptDocVMHandlerAction get(String name)
	{
		return NAME_MAP.get(name);
	}
	
	/**
	 * getName
	 * 
	 * @return
	 */
	public String getName()
	{
		return this._name;
	}
}
