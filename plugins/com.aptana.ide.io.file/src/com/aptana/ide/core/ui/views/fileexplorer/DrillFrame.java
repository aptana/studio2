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
package com.aptana.ide.core.ui.views.fileexplorer;

import java.util.List;

/**
 * (non-Javadoc) A <code>DrillFrame</code> is used to record the input element and selection state
 * for one frame in a <code>DrillDownTreeViewer</code>. This class is not intended for use beyond
 * the package.
 */
/* package */class DrillFrame
{
	Object fElement;

	Object fPropertyName;

	List fExpansion = null;

	/**
	 * Allocates a new DrillFrame.
	 * 
	 * @param oElement
	 *            the tree input element
	 * @param strPropertyName
	 *            the visible tree property
	 * @param vExpansion
	 *            the current expansion state of the tree
	 */
	public DrillFrame(Object oElement, Object strPropertyName, List vExpansion)
	{
		fElement = oElement;
		fPropertyName = strPropertyName;
		fExpansion = vExpansion;
	}

	/**
	 * Compares two Objects for equality.
	 * <p>
	 * 
	 * @param obj
	 *            the reference object with which to compare.
	 * @return <code>true</code> if this object is the same as the obj argument;
	 *         <code>false</code> otherwise.
	 */
	public boolean equals(Object obj)
	{
		// Compare handles.
		if (this == obj)
		{
			return true;
		}

		// Compare class.
		if (!(obj instanceof DrillFrame))
		{
			return false;
		}

		// Compare contents.
		DrillFrame oOther = (DrillFrame) obj;
		return ((fElement == oOther.fElement) && (fPropertyName.equals(oOther.fPropertyName)));
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return super.hashCode();
	}

	/**
	 * Returns the input element.
	 * 
	 * @return the input element
	 */
	public Object getElement()
	{
		return fElement;
	}

	/**
	 * Returns the expansion state for a tree.
	 * 
	 * @return the expansion state for a tree
	 */
	public List getExpansion()
	{
		return fExpansion;
	}

	/**
	 * Returns the property name.
	 * 
	 * @return the property name
	 */
	public Object getPropertyName()
	{
		return fPropertyName;
	}
}
