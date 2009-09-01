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
package com.aptana.ide.editors.unified.errors;

import java.util.Map;

import org.eclipse.core.internal.resources.MarkerInfo;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.PlatformObject;

/**
 * ErrorMarker
 * 
 * @author Ingo Muschenetz
 */
public class ErrorMarker extends PlatformObject implements IMarker
{
	MarkerInfo info;
	int offset;
	int length;

	/**
	 * ErrorMarker
	 * 
	 * @param markerInfo
	 * @param offset
	 * @param length
	 */
	public ErrorMarker(MarkerInfo markerInfo, int offset, int length)
	{
		this.info = markerInfo;
		this.offset = offset;
		this.length = length;
	}

	/**
	 * getOffset
	 * 
	 * @return int
	 */
	public int getOffset()
	{
		return offset;
	}

	/**
	 * getLength
	 * 
	 * @return int
	 */
	public int getLength()
	{
		return length;
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#delete()
	 */
	public void delete()
	{
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#exists()
	 */
	public boolean exists()
	{
		return true;
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String attributeName)
	{
		return info.getAttribute(attributeName);
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#getAttribute(java.lang.String, int)
	 */
	public int getAttribute(String attributeName, int defaultValue)
	{
		Object object = info.getAttribute(attributeName);
		if (object == null || !(object instanceof Integer))
		{
			return defaultValue;
		}
		else
		{
			return ((Integer) object).intValue();
		}
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#getAttribute(java.lang.String, java.lang.String)
	 */
	public String getAttribute(String attributeName, String defaultValue)
	{
		Object object = info.getAttribute(attributeName);
		if (object == null || !(object instanceof String))
		{
			return defaultValue;
		}
		else
		{
			return (String) object;
		}
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#getAttribute(java.lang.String, boolean)
	 */
	public boolean getAttribute(String attributeName, boolean defaultValue)
	{
		Object object = info.getAttribute(attributeName);
		if (object == null || !(object instanceof Boolean))
		{
			return defaultValue;
		}
		else
		{
			return ((Boolean) object).booleanValue();
		}
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#getAttributes()
	 */
	public Map getAttributes()
	{
		return info.getAttributes();
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#getAttributes(java.lang.String[])
	 */
	public Object[] getAttributes(String[] attributeNames)
	{
		return info.getAttributes(attributeNames);
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#getCreationTime()
	 */
	public long getCreationTime()
	{
		return info.getCreationTime();
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#getId()
	 */
	public long getId()
	{
		return info.getId();
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#getResource()
	 */
	public IResource getResource()
	{
		return null;
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#getType()
	 */
	public String getType()
	{
		return info.getType();
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#isSubtypeOf(java.lang.String)
	 */
	public boolean isSubtypeOf(String superType)
	{
		return ((Workspace) ResourcesPlugin.getWorkspace()).getMarkerManager().isSubtype(getType(), superType);
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#setAttribute(java.lang.String, int)
	 */
	public void setAttribute(String attributeName, int value)
	{
		info.setAttribute(attributeName, new Integer(value));
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String attributeName, Object value)
	{
		info.setAttribute(attributeName, value);
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#setAttribute(java.lang.String, boolean)
	 */
	public void setAttribute(String attributeName, boolean value)
	{
		info.setAttribute(attributeName, Boolean.valueOf(value));
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#setAttributes(java.lang.String[], java.lang.Object[])
	 */
	public void setAttributes(String[] attributeNames, Object[] values)
	{
		info.setAttributes(attributeNames, values);
	}

	/**
	 * @see org.eclipse.core.resources.IMarker#setAttributes(java.util.Map)
	 */
	public void setAttributes(Map attributes)
	{
		info.setAttributes(attributes);
	}
}
