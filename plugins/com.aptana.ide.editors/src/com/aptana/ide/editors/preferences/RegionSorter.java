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
package com.aptana.ide.editors.preferences;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.aptana.ide.editors.unified.colorizer.ColorizationConstants;
import com.aptana.ide.editors.unified.colorizer.Region;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class RegionSorter extends ViewerSorter
{

	/**
	 * NAME
	 */
	public static final int NAME = 1;
	
	/**
	 * OFFSET
	 */
	public static final int OFFSET = 2;
	
	/**
	 * LENGTH
	 */
	public static final int LENGTH = 3;

	private int criteria;

	/**
	 * Creates a resource sorter that will use the given sort criteria.
	 * 
	 * @param criteria
	 */
	public RegionSorter(int criteria)
	{
		super();
		this.criteria = criteria;
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 *      java.lang.Object)
	 */
	public int compare(Viewer viewer, Object o1, Object o2)
	{

		Region r1 = (Region) o1;
		Region r2 = (Region) o2;

		switch (criteria)
		{
			case NAME:
				return collator.compare(r1.getName(), r2.getName());
			case OFFSET:
				return compareComplexStrings(r1.getOffsetString(), r2.getOffsetString());
			case LENGTH:
				return compareComplexStrings(r1.getLengthString(), r2.getLengthString());
			default:
				return 0;
		}
	}

	private int compareComplexStrings(String s1, String s2)
	{
		int diff = 0;
		int i1 = 0;
		int i2 = 0;
		if (s1.startsWith(ColorizationConstants.LENGTH_KEYWORD) && s2.startsWith(ColorizationConstants.LENGTH_KEYWORD))
		{
			i1 = Integer.parseInt(s1.substring(ColorizationConstants.LENGTH_KEYWORD.length(), s1.length()));
			i2 = Integer.parseInt(s2.substring(ColorizationConstants.LENGTH_KEYWORD.length(), s2.length()));
			diff = i1 - i2;
			return diff < 0 ? -1 : diff > 0 ? 1 : 0;
		}
		else if (s1.startsWith(ColorizationConstants.LENGTH_KEYWORD))
		{
			return 1;
		}
		else if (s2.startsWith(ColorizationConstants.LENGTH_KEYWORD))
		{
			return -1;
		}
		else
		{
			i1 = Integer.parseInt(s1);
			i2 = Integer.parseInt(s2);
			diff = i1 - i2;
			return diff < 0 ? -1 : diff > 0 ? 1 : 0;
		}
	}
}
