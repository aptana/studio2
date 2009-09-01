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
package com.aptana.ide.core.db;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kevin Lindsey
 */
public class FeatureInfo implements Comparable<FeatureInfo>
{
	private static final Pattern TRAILING_DOTTED_NUMBERS = Pattern.compile("(?:\\.[0-9]+)+$"); //$NON-NLS-1$
	
	public final String name;
	public final String version;
	public final boolean enabled;
	
	/**
	 * FeatureInfo
	 * 
	 * @param name
	 * @param version
	 * @param enabled
	 */
	public FeatureInfo(String name, String version, boolean enabled)
	{
		if (name != null)
		{
			// adjust name if it's an AJAX library
			Matcher m = TRAILING_DOTTED_NUMBERS.matcher(name);
						
			// remove any trailing dotted numbers (for AJAX libs)
			if (m.find())
			{
				name = name.substring(0, m.start());
			}
		}
		
		this.name = (name != null) ? name : "";
		this.version = (version != null) ? version : "";
		this.enabled = enabled;
	}
	
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(FeatureInfo o)
	{
		int result = this.name.compareTo(o.name);
		
		if (result == 0)
		{
			result = this.version.compareTo(o.version);
			
			if (result == 0)
			{
				if (this.enabled != o.enabled)
				{
					if (this.enabled)
					{
						result = -1;
					}
					else
					{
						result = 1;
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		boolean result = false;
		
		if (obj instanceof FeatureInfo)
		{
			FeatureInfo info = (FeatureInfo) obj;
			
			result = this.name.equals(info.name) && this.version.equals(info.version) && this.enabled == info.enabled;
		}
		
		return result;
	}

	/**
	 * toString
	 */
	@Override
	public String toString()
	{
		return this.name + ":" + this.version + ":" + Boolean.toString(this.enabled);
	}
}
