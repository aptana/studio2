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
package com.aptana.ide.editor.xml.contentassist;

import java.util.ArrayList;

import com.aptana.ide.core.KeyValuePair;

/**
 * XMLContextLocation
 */
public class XMLContextLocation
{

	private String location = null;
	private String tagName;
	private ArrayList attributes = new ArrayList();

	/**
	 * @return Returns the attributes.
	 */
	public ArrayList getAttributes()
	{
		return attributes;
	}

	/**
	 * @return Returns the location.
	 */
	public String getLocation()
	{
		return location;
	}

	/**
	 * @param location
	 *            The location to set.
	 */
	public void setLocation(String location)
	{
		this.location = location;
	}

	/**
	 * @return Returns the tagName.
	 */
	public String getTagName()
	{
		return tagName;
	}

	/**
	 * @param tagName
	 *            The tagName to set.
	 */
	public void setTagName(String tagName)
	{
		this.tagName = tagName;
	}

	/**
	 * Finds the attribute with the named key
	 * 
	 * @param key
	 * @return KeyValuePair
	 */
	public KeyValuePair find(Object key)
	{
		for (int i = 0; i < attributes.size(); i++)
		{
			KeyValuePair kvp = (KeyValuePair) attributes.get(i);
			if (kvp.getKey().equals(key))
			{
				return kvp;
			}
		}

		return null;
	}
}
