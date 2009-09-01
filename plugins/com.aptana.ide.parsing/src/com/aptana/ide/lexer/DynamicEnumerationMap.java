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
package com.aptana.ide.lexer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Kevin Lindsey
 */
public class DynamicEnumerationMap implements IEnumerationMap
{
	private Map<String,Integer> _nameToInt;
	private Map<Integer,String> _intToName;

	/**
	 * DynamicEnumerationMap
	 */
	public DynamicEnumerationMap()
	{
		this._nameToInt = new HashMap<String,Integer>();
		this._intToName = new HashMap<Integer,String>();
	}

	/**
	 * @see com.aptana.ide.lexer.IEnumerationMap#getIntValue(java.lang.String)
	 */
	public int getIntValue(String name)
	{
		int result = -1;
		
		if (this._nameToInt.containsKey(name))
		{
			result = this._nameToInt.get(name).intValue();
		}
		else
		{
			result = this._nameToInt.size();
			
			// TODO: Replace new Integer() with Integer.valueOf()
			this._nameToInt.put(name, new Integer(result));
			this._intToName.put(new Integer(result), name);
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.lexer.IEnumerationMap#getName(int)
	 */
	public String getName(int index)
	{
		String result = null;
		Integer key = new Integer(index);
		
		if (this._intToName.containsKey(key))
		{
			result = this._intToName.get(key);
		}
		
		return result;
	}
	
	/**
	 * @see com.aptana.ide.lexer.IEnumerationMap#getNames()
	 */
	public String[] getNames()
	{
		Set<String> keys = this._nameToInt.keySet();

		return keys.toArray(new String[keys.size()]);
	}
}
