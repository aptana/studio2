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
package com.aptana.ide.views.outline;

import java.text.Collator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 *
 */
public class CategorySorter extends ViewerSorter
{
	/**
	 * CategorySorter
	 */
	public CategorySorter()
	{
		super();
	}

	/**
	 * CategorySorter
	 * 
	 * @param collator
	 */
	public CategorySorter(Collator collator)
	{
		super(collator);
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 *      java.lang.Object)
	 */
	public int compare(Viewer view, Object e1, Object e2)
	{
		int result = 0;

		if (e1 instanceof OutlineItem && e2 instanceof OutlineItem)
		{
			OutlineItem item1 = (OutlineItem) e1;
			OutlineItem item2 = (OutlineItem) e2;
	
			result = item1.getLabel().compareToIgnoreCase(item2.getLabel());
		}
		else if (e1 instanceof IParseNode && e2 instanceof IParseNode)
		{
			IParseNode node1 = (IParseNode) e1;
			IParseNode node2 = (IParseNode) e2;
			UnifiedOutlineProvider provider = UnifiedOutlineProvider.getInstance();
			
			if (provider.isSortable(node1.getLanguage()) && provider.isSortable(node2.getLanguage()))
			{
				String label1 = provider.getText(e1);
				String label2 = provider.getText(e2);
				
				result = label1.compareTo(label2);
			}
			else
			{
				result = node1.getStartingOffset() - node2.getStartingOffset();
			}
		}
		else
		{
			result = e1.toString().compareToIgnoreCase(e2.toString());
		}

		return result;
	}
}
