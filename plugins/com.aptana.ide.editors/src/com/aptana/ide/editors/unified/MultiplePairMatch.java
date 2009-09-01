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
package com.aptana.ide.editors.unified;

import java.util.Collections;
import java.util.List;

/**
 * MultiplePairMatch.
 * @author Denis Denisenko
 */
public class MultiplePairMatch extends PairMatch implements IPairMatchExt
{
	/**
	 * List of subsequent matches.
	 */
	private List<PairMatch> subsequentMatches;
	
	/**
	 * Creates Multiple pair match from the list of matches, whether the first
	 * match would be the main, and other matches would be sunsequent. 
	 * @param matches - matches list.
	 * @return multiple pair match.
	 */
	public static MultiplePairMatch createFromList(List<PairMatch> matches)
	{
		if (matches == null || matches.size() == 0)
		{
			throw new IllegalArgumentException("Matches argument should not be null or empty"); //$NON-NLS-1$
		}
		
		MultiplePairMatch match;
		List<PairMatch> subsequentMatches = null;
		if (matches.size() == 1)
		{
			subsequentMatches = Collections.emptyList();
		}
		else
		{
			subsequentMatches = matches.subList(1, matches.size());
			
		}
		
		match = new MultiplePairMatch(subsequentMatches);
		
		PairMatch mainMatch = matches.get(0);
		match.beginEnd = mainMatch.beginEnd;
		match.beginStart = mainMatch.beginStart;
		match.endEnd = mainMatch.endEnd;
		match.endStart = mainMatch.endStart;
		match.offset = mainMatch.offset;
		match.setDisplayOnlyMatch(mainMatch.displayOnlyMatch());
		match.setColor(mainMatch.getColor());
		
		return match;
	}
	
	/**
	 * MultiplePairMatch constructor.
	 * @param subsequentMatches - subsequent matches.
	 */
	public MultiplePairMatch(List<PairMatch> subsequentMatches)
	{
		this.subsequentMatches = subsequentMatches;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<PairMatch> getSubsequentMatches()
	{
		return subsequentMatches;
	}
}
