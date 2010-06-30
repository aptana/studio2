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
package com.aptana.ide.server.core.impl;

import java.util.HashMap;
import java.util.Map;

import com.aptana.ide.server.core.IAbstractConfiguration;

/**
 * implementation of {@link IAbstractConfiguration} which stores data in {@link HashMap}
 * @author Pavel Petrochenko
 */
public class Configuration implements IAbstractConfiguration
{

	private static final String[] NO_VALUES = new String[0];
	private Map<String, Object> map = new HashMap<String, Object>();

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#createSubConfiguration(java.lang.String)
	 */
	public IAbstractConfiguration createSubConfiguration(String name)
	{
		checkName(name);
		Configuration cm = new Configuration();
		map.put(name, cm);
		return cm;
	}

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#getBooleanAttribute(java.lang.String)
	 */
	public boolean getBooleanAttribute(String name)
	{
		checkName(name);
		Object object = map.get(name);
		if (object == null)
		{
			return false;
		}
		else if (object instanceof Boolean)
		{
			Boolean bl = (Boolean) object;
			return bl.booleanValue();
		}
		else
		{
			return false;
		}
	}

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#getIntAttribute(java.lang.String)
	 */
	public int getIntAttribute(String name)
	{
		checkName(name);
		Object object = map.get(name);
		if (object == null)
		{
			return 0;
		}
		else if (object instanceof Integer)
		{
			Integer bl = (Integer) object;
			return bl.intValue();
		}		
		else
		{
			try{
			return Integer.parseInt(object.toString());
			}catch (NumberFormatException e) {
				return 0;
			}			
		}
	}

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#getStringArrayAttribute(java.lang.String)
	 */
	public String[] getStringArrayAttribute(String name)
	{
		checkName(name);
		Object object = map.get(name);
		if (object == null)
		{
			return NO_VALUES;
		}
		else if (object instanceof String[])
		{
			return (String[]) object;
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#getStringAttribute(java.lang.String)
	 */
	public String getStringAttribute(String name)
	{
		checkName(name);
		Object object = map.get(name);
		if (object == null)
		{
			return ""; //$NON-NLS-1$
		}
		else
		{
			return object.toString();
		}
	}

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#getSubConfiguration(java.lang.String)
	 */
	public IAbstractConfiguration getSubConfiguration(String name)
	{
		checkName(name);
		Object object = map.get(name);
		if (object == null)
		{
			return null;
		}
		else if (object instanceof IAbstractConfiguration)
		{
			return (IAbstractConfiguration) object;
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String name)
	{
		checkName(name);
		map.remove(name);
	}

	private void checkName(String name)
	{
		if (name == null)
		{
			throw new IllegalArgumentException();
		}
	}

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#setBooleanAttribute(java.lang.String, boolean)
	 */
	public void setBooleanAttribute(String name, boolean value)
	{
		checkName(name);
		map.put(name, value ? Boolean.TRUE : Boolean.FALSE);
	}

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#setIntAttribute(java.lang.String, int)
	 */
	public void setIntAttribute(String name, int value)
	{
		checkName(name);
		map.put(name, new Integer(value));
	}

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#setStringArrayAttribute(java.lang.String,
	 *      java.lang.String[])
	 */
	public void setStringArrayAttribute(String name, String[] value)
	{
		checkName(name);
		map.put(name, value);
	}

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#setStringAttribute(java.lang.String, java.lang.String)
	 */
	public void setStringAttribute(String name, String value)
	{
		checkName(name);
		map.put(name, value);
	}

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#setSubConfiguration(java.lang.String,
	 *      com.aptana.ide.server.core.IAbstractConfiguration)
	 */
	public void setSubConfiguration(String name, IAbstractConfiguration configuration)
	{
		checkName(name);
		map.put(name, configuration);
	}

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#propertyNames()
	 */
	public String[] propertyNames()
	{
		String[] set=new String[map.size()];
		map.keySet().toArray(set);
		return set;
	}

}
