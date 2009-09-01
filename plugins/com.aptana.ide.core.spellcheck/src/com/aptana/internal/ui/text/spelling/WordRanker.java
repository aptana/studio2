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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Scanner;


public class WordRanker
{
	protected HashMap<String,Integer> wordRates;
	protected int minProposalLength = 2; //No need to put into completition words shorter then this 
	protected int raringCoef = 10; //All rates will be multiplied by this to have an ability to shift rates without
	//getting several words with same rank
	protected int expectedWordCount = 1000; //Total word count expected to be this
	
	public WordRanker()
	{
		wordRates = new HashMap<String, Integer>(expectedWordCount);		
	}
	
	public void loadFromStream(InputStream stream)
	{
		Scanner scanner;
		scanner = new Scanner(stream);
		
		while (scanner.hasNext())
		{
			boolean intScanned = true;
			int rate = -1;
			
			do
			{
				try
				{
					rate = scanner.nextInt();
				}
				catch(InputMismatchException e)
				{
					intScanned = false;
					scanner.next();
				}
			}
			while (!intScanned && scanner.hasNext());
			
			if (rate > -1 && scanner.hasNext())
			{
				String word = scanner.next();
				if (word.length() >= minProposalLength) wordRates.put(word,rate);
				else wordRates.put(word, rate + expectedWordCount);
			}
		}
		//makeRare();
	}
	
	protected void makeRare()
	{
		for (Iterator<String> iterator = wordRates.keySet().iterator(); iterator.hasNext();)
		{
			String s = (String) iterator.next();
			wordRates.put(s, wordRates.get(s) * raringCoef);			
		}
	}
	
	public HashMap<String, Integer> getWordRateMap()
	{
		return wordRates;
	}

	public int getRateForWord(String word)
	{
		Integer i = wordRates.get(word.trim().toLowerCase());
		if (i != null) return i;
		return -1;
		
	}
	
	public int putWithDefaultRank(String word)
	{
		wordRates.put(word, expectedWordCount * raringCoef);
		return expectedWordCount * raringCoef;
	}
	
	public void saveToStream(OutputStream stream)
	{
		PrintWriter pw = new PrintWriter(stream);
		for (Iterator<String> iterator = wordRates.keySet().iterator(); iterator.hasNext();)
		{
			String key = (String) iterator.next();
			pw.println((Integer)(wordRates.get(key)) + " " + key.toLowerCase());			 //$NON-NLS-1$
		}
		pw.close();
	}
	
}
