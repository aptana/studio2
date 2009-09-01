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

import java.util.ArrayList;

import org.eclipse.core.runtime.Preferences;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.core.IAbstractConfiguration;

/**
 * implementation of IAbstractConfiguration wich stores data in {@link Preferences}
 * @author Pavel Petrochenko
 */
public class PreferencesConfiguration implements IAbstractConfiguration
{

	private static final String[] EMPTY_ARRAY = new String[0];
	Preferences prefs;
	String baseKey;

	/**
	 * @param prefs
	 * @param key
	 */
	public PreferencesConfiguration(Preferences prefs, String key)
	{
		this.prefs = prefs;
		this.baseKey = key;
	}

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#createSubConfiguration(java.lang.String)
	 */
	public IAbstractConfiguration createSubConfiguration(String name)
	{
		return new PreferencesConfiguration(prefs, getKey(name));
	}

	private String getKey(String name)
	{
		if (name == null)
		{
			throw new IllegalArgumentException();
		}
		return StringUtils.format("{0}.{1}", new Object[] { baseKey, name }); //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#getBooleanAttribute(java.lang.String)
	 */
	public boolean getBooleanAttribute(String name)
	{
		return prefs.getBoolean(getKey(name));
	}

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#getIntAttribute(java.lang.String)
	 */
	public int getIntAttribute(String name)
	{
		return prefs.getInt(getKey(name));
	}

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#getStringArrayAttribute(java.lang.String)
	 */
	public String[] getStringArrayAttribute(String name)
	{
		String string = prefs.getString(getKey(name));
		if (string.length() == 0)
		{
			return EMPTY_ARRAY;
		}
		return string.split(","); //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#setStringArrayAttribute(java.lang.String,
	 *      java.lang.String[])
	 */
	public void setStringArrayAttribute(String name, String[] value)
	{
		StringBuffer bf = new StringBuffer();
		for (int a = 0; a < value.length; a++)
		{
			bf.append(value[a]);
			if (a != value.length - 1)
			{
				bf.append(',');
			}
		}
		prefs.setValue(getKey(name), bf.toString());
	}

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#getStringAttribute(java.lang.String)
	 */
	public String getStringAttribute(String name)
	{
		return prefs.getString(getKey(name));
	}

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#getSubConfiguration(java.lang.String)
	 */
	public IAbstractConfiguration getSubConfiguration(String name)
	{
		return createSubConfiguration(name);
	}

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String name)
	{
		prefs.setValue(getKey(name), ""); //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#setBooleanAttribute(java.lang.String, boolean)
	 */
	public void setBooleanAttribute(String name, boolean value)
	{
		prefs.setValue(getKey(name), value);
	}

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#setIntAttribute(java.lang.String, int)
	 */
	public void setIntAttribute(String name, int value)
	{
		prefs.setValue(getKey(name), value);
	}

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#setStringAttribute(java.lang.String, java.lang.String)
	 */
	public void setStringAttribute(String name, String value)
	{
		prefs.setValue(getKey(name), value);
	}

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#setSubConfiguration(java.lang.String,
	 *      com.aptana.ide.server.core.IAbstractConfiguration)
	 */
	public void setSubConfiguration(String name, IAbstractConfiguration configuration)
	{
		String[] propertyNames = configuration.propertyNames();
		for (int a = 0; a < propertyNames.length; a++)
		{
			setStringAttribute(getKey(propertyNames[a]), configuration.getStringAttribute(propertyNames[a]));
		}
	}

	/**
	 * @see com.aptana.ide.server.core.IAbstractConfiguration#propertyNames()
	 */
	public String[] propertyNames()
	{
		String[] propertyNames = prefs.propertyNames();
		ArrayList<String> filtered = new ArrayList<String>();
		String start = getKey(""); //$NON-NLS-1$
		for (int a = 0; a < propertyNames.length; a++)
		{
			if (propertyNames[a].startsWith(start))
			{
				filtered.add(propertyNames[a].substring(start.length()));
			}
		}
		String[] result = new String[filtered.size()];
		filtered.toArray(result);
		return result;
	}

}
