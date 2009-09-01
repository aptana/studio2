/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.internal.ui.text.spelling;


public class UserWordRanker extends WordRanker
{
	WordRanker parentWordRanker;
	
	
	public UserWordRanker(WordRanker parentWordRanker)
	{
		super();
		this.parentWordRanker = parentWordRanker;		
	}
	
	@Override
	public int getRateForWord(String word)
	{	
		word = word.trim().toLowerCase();
		Integer i = wordRates.get(word);
		if (i == null && parentWordRanker != null) return parentWordRanker.getRateForWord(word);
		else if (parentWordRanker != null)
		{
			int k = parentWordRanker.getRateForWord(word);
			if (k > -1) return Math.min(i, k);
			return i;
		}
		return -1;
	}
	
	public void increaseRateForWord(String word)
	{
		word = word.trim().toLowerCase();
		Integer curRank = wordRates.get(word);
		if (curRank == null)
			wordRates.put(word, expectedWordCount * raringCoef);
		else
		{
			int increment = getIncrementValue(curRank);
			curRank = curRank - increment;
			if (curRank < 0) curRank = 5;
		 	wordRates.remove(word);
		 	wordRates.put(word,curRank);
		}
	}

	protected int getIncrementValue(Integer curRank)
	{
		if (curRank > 8000) return 1000;
		if (curRank > 6000) return 800;
		if (curRank > 4000) return 600;
		if (curRank > 2000) return 400;
		if (curRank > 1000) return 200;
		if (curRank > 500) return 100;
		if (curRank > 200) return 50;
		if (curRank > 100) return 40;
		if (curRank > 50) return 30;
		return 5;
	}
	
	

}
