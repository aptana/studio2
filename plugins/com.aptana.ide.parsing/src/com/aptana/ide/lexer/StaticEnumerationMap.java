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

import java.lang.reflect.Array;
import java.lang.reflect.Method;

/**
 * @author Kevin Lindsey
 */
public class StaticEnumerationMap implements IEnumerationMap
{
	private static final String[] NO_STRINGS = new String[0];
	
	private Method _getIntValueMethod;
	private Method _getNameMethod;
	private Method _getNamesMethod;

	/**
	 * StaticMap
	 * 
	 * @param enumeration
	 */
	public StaticEnumerationMap(Class<?> enumeration)
	{
		if (enumeration == null)
		{
			throw new IllegalArgumentException(Messages.StaticEnumerationMap_Enumeration_Class_Not_Defined);
		}
		
		// find getIntValue and getName methods
		Method[] methods = enumeration.getMethods();
		
		for (int i = 0; i < methods.length; i++)
		{
			Method method = methods[i];
			Class<?> returnType = method.getReturnType();
			String name = method.getName();
			
			if (name.equals("getIntValue")) //$NON-NLS-1$
			{
				Class<?>[] paramTypes = method.getParameterTypes();
				
				if (paramTypes.length == 1 && paramTypes[0] == String.class && returnType == int.class)
				{
					this._getIntValueMethod = method;
				}
			}
			else if (name.equals("getName")) //$NON-NLS-1$
			{
				Class<?>[] paramTypes = method.getParameterTypes();
				
				if (paramTypes.length == 1 && paramTypes[0] == int.class && returnType == String.class)
				{
					this._getNameMethod = method;
				}
			}
			else if (name.equals("getNames")) //$NON-NLS-1$
			{
				Class<?>[] paramTypes = method.getParameterTypes();
				
				if (paramTypes.length == 0 && returnType == Array.class)
				{
					this._getNamesMethod = method;
				}
			}
		}
	}

	/**
	 * @see com.aptana.ide.lexer.IEnumerationMap#getIntValue(java.lang.String)
	 */
	public int getIntValue(String name)
	{
		int result = -1;
		
		if (this._getIntValueMethod != null)
		{
			try
			{
				Integer value = (Integer) this._getIntValueMethod.invoke(null, new Object[] { name });
				
				result = value.intValue();
			}
			catch (Exception e)
			{
				//e.printStackTrace();
			}
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.lexer.IEnumerationMap#getName(int)
	 */
	public String getName(int index)
	{
		String result = null;
		
		if (this._getNameMethod != null)
		{
			try
			{
				// TODO: replace new Integer() with Integer.valueOf()
				result = (String) this._getNameMethod.invoke(null, new Object[] { new Integer(index) });
			}
			catch (Exception e)
			{
				//e.printStackTrace();
			}
		}
		
		return result;
	}
	
	/**
	 * @see com.aptana.ide.lexer.IEnumerationMap#getNames()
	 */
	public String[] getNames()
	{
		String[] result = NO_STRINGS;
		
		if (this._getNamesMethod != null)
		{
			try
			{
				result = (String[]) this._getNamesMethod.invoke(null, new Object[0]);
			}
			catch (Exception e)
			{
				//e.printStackTrace();
			}
		}
		
		return result;
	}
}
