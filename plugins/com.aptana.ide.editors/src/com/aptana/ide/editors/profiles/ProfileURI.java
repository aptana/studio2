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
package com.aptana.ide.editors.profiles;

import java.util.ArrayList;

/**
 * @author Paul Colton
 *
 */
public class ProfileURI
{
	String _uri;
	Profile _parent;
	ArrayList _children = new ArrayList();
	
	/**
	 * ProfilePath
	 * @param uri
	 * @param parent
	 */
	public ProfileURI(String uri, Profile parent)
	{
		this._uri = uri;
		this._parent = parent;
	}

	/**
	 * ProfilePath
	 * @param uri
	 */
	public ProfileURI(String uri)
	{
		this(uri, null);
	}

	/**
	 * setParent
	 * @param parent The parent to set.
	 */
	public void setParent(Profile parent)
	{
		_parent = parent;
	}

	/**
	 * getParent
	 * @return Returns the parent.
	 */
	public Profile getParent()
	{
		return _parent;
	}

	/**
	 * getURI
	 * @return String
	 */
	public String getURI()
	{
		return _uri;
	}
	
	/**
	 * getChildren
	 * @return ProfileURI[]
	 */
	public ProfileURI[] getChildren()
	{
		return (ProfileURI[]) _children.toArray(new ProfileURI[0]);
	}

	/**
	 * addChild
	 * @param path
	 */
	public void addChild(ProfileURI path)
	{
		this._children.add(path);
	}

	/** 
	 * Check for equality against another ProfilePath by comparing the URI
	 * @param o 
	 * @return  boolean
	 */
	public boolean equals(Object o)
	{
		if(o instanceof ProfileURI)
		{
			ProfileURI path = (ProfileURI) o;
			if(path._uri == null || this._uri == null)
			{
				return false;
			}
			else
			{
				return path._uri.equals(this._uri);
			}
		}
		else
		{
			return super.equals(o);
		}
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return super.hashCode();
	}
}
