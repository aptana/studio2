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
package com.aptana.ide.editor.html.contentassist;

import java.util.Comparator;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

/**
 * Sorts
 */
public class HTMLCompletionProposalComparator implements Comparator
{
	// note the order of these determines sorting, for speed reasons
	/** Parameter object */
	public static final int OBJECT_TYPE_PARAMETER = 0;
	/** Parameter object */
	public static final int OBJECT_TYPE_PROPERTY = 1;
	/** Property object */
	public static final int OBJECT_TYPE_GLOBAL_OBJECT = 2;
	/** Global object */
	public static final int OBJECT_TYPE_METHOD = 3;
	/** Method object */
	public static final int OBJECT_TYPE_CLASS = 4;
	/** Other object */
	public static final int OBJECT_TYPE_OTHER = 5;

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2)
	{
		if (o1 instanceof HTMLCompletionProposal && o2 instanceof HTMLCompletionProposal)
		{
			HTMLCompletionProposal cp1 = (HTMLCompletionProposal) o1;
			HTMLCompletionProposal cp2 = (HTMLCompletionProposal) o2;
			int type1 = cp1.getObjectType();
			int type2 = cp2.getObjectType();
			if (type1 == type2)
			{
				return cp1.getDisplayString().compareToIgnoreCase(cp2.getDisplayString());
			}
			else if (type1 > type2)
			{
				return 1;
			}
			else if (type1 < type2)
			{
				return -1;
			}
		}
		else if (o1 instanceof ICompletionProposal && o2 instanceof ICompletionProposal)
		{
			ICompletionProposal cp1 = (ICompletionProposal) o1;
			ICompletionProposal cp2 = (ICompletionProposal) o2;
			if (cp1.getDisplayString() != null && cp2.getDisplayString() != null)
			{
				return cp1.getDisplayString().compareToIgnoreCase(cp2.getDisplayString());
			}
		}
		return 0;
	}

}
