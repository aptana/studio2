/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.aptana.internal.ui.text.spelling;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.aptana.internal.ui.text.spelling.engine.DefaultSpellChecker;
import com.aptana.internal.ui.text.spelling.engine.ISpellCheckEngine;
import com.aptana.internal.ui.text.spelling.engine.ISpellChecker;
import com.aptana.internal.ui.text.spelling.engine.RankedWordProposal;
import com.aptana.semantic.ui.text.spelling.Activator;

/**
 * Content assist processor to complete words. <strong>Note:</strong> This is
 * currently not supported because the spelling engine cannot return word
 * proposals but only correction proposals.
 * <p>
 * If we enable this again we must register the computer in
 * <code>plugin.xml</code>:
 * 
 * <pre>
 * </pre>
 * </p>
 * 
 * @since 3.0
 */
public final class WordCompletionProposalComputer implements
		ICompletionProposalComputer, ICompletionListener {
	
	static WordRanker basicWordRanker = new WordRanker();
	static UserWordRanker userWordRanker = new UserWordRanker(basicWordRanker);
	static String stateLocation;
	
	static{
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run()
			{
				dispose();
			}
		});
	}
	
	/** The prefix rank shift */
	private static final int PREFIX_RANK_SHIFT = 500;

	
	
	public WordCompletionProposalComputer()
	{
		stateLocation = com.aptana.semantic.ui.text.spelling.Activator.getDefault().getStateLocation().toString();
		try
		{
			basicWordRanker.loadFromStream(new FileInputStream(stateLocation + "\\rates.txt")); //$NON-NLS-1$
			userWordRanker.loadFromStream(new FileInputStream(stateLocation + "\\userRates.txt")); //$NON-NLS-1$
		} catch (FileNotFoundException e)
		{
		}
	}
	
	/**
	 * @seeorg.eclipse.jface.text.contentassist.ICompletionProposalComputer#
	 * computeCompletionProposals
	 * (org.eclipse.jface.text.contentassist.TextContentAssistInvocationContext,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	public List computeCompletionProposals(
			ContentAssistInvocationContext context, IProgressMonitor monitor) {
		if (this.contributes()) {
			try {
				final IDocument document = context.getDocument();
				final int offset = context.getInvocationOffset();
				
				final IRegion region = document
						.getLineInformationOfOffset(offset);
				final String content = document.get(region.getOffset(), region
						.getLength());

				int index = offset - region.getOffset() - 1;
				while ((index >= 0) && Character.isLetter(content.charAt(index))) {
					index--;
				}

				final int start = region.getOffset() + index + 1;
				boolean isSentenceBeginning = checkSentenceBeginning(start, document);
				final String candidate = content.substring(index + 1, offset
						- region.getOffset());

				if (candidate.length() > 0) {

					final ISpellCheckEngine engine = SpellCheckEngine
							.getInstance();
					final ISpellChecker checker = engine.getSpellChecker();

					if (checker != null) {

						Set proposals2 = null;
						if (checker instanceof DefaultSpellChecker) {
							final DefaultSpellChecker sp = (DefaultSpellChecker) checker;
							proposals2 = sp.getCompletionProposals(candidate,
									Character.isUpperCase(candidate.charAt(0)));
						} else {
							proposals2 = checker.getProposals(candidate,
									Character.isUpperCase(candidate.charAt(0)));
						}
						final List proposals = new ArrayList(proposals2);
						final List result = new ArrayList(proposals.size());
						for (final Iterator it = proposals.iterator(); it.hasNext();) {
							final RankedWordProposal word = (RankedWordProposal) it
									.next();
							String text = word.getText();
							if (text.startsWith(candidate)) {
								word.setRank(word.getRank()	+ PREFIX_RANK_SHIFT);
							}
							
							if (isSentenceBeginning && text.length() > 0) text = Character.toUpperCase(text.charAt(0)) +
																		  text.substring(1);
							
							result.add(new SpellingCompletionProposal(
											text,
											start,
											candidate.length(),
											text.length(),
											JavaPluginImages
													.get(JavaPluginImages.IMG_CORRECTION_RENAME),
											text, null, null, userWordRanker));
						}
						;
						Collections.sort(result,new Comparator<SpellingCompletionProposal>()
						{

							public int compare(SpellingCompletionProposal o1,
									SpellingCompletionProposal o2)
							{
								String displayString = o1.getDisplayString();
								int a1 = userWordRanker.getRateForWord(displayString);
								
								if (a1 < 0) a1 = userWordRanker.putWithDefaultRank(displayString);
								if (displayString.startsWith(candidate)){
									a1=a1/2-1;
								}
								String displayString2 = o2.getDisplayString();
								int a2 = userWordRanker.getRateForWord(displayString2);
								if (a2 < 0) a2 = userWordRanker.putWithDefaultRank(displayString2);
								if (displayString2.startsWith(candidate)){
									a2=a2/2-1;
								}
								if (a1 < a2) return -1;
								if (a1 > a2) return 1;
								//Shorter words has larger priority
								if (displayString.length() < displayString2.length())
									return -1;
								if (displayString.length() > displayString2.length())
									return 1;
								return 0;
							}
						}
						);
						return result;
					}
				}
			} catch (final BadLocationException exception) {

				Activator.log(exception);
			}
		}
		return Collections.EMPTY_LIST;
	}

	public static boolean checkSentenceBeginning(int offset, final IDocument document) throws BadLocationException
	{
		if (offset > 0) offset--;
		while (offset > 0 && (document.get(offset,1).equals(" ") || document.get(offset,1).equals("\t"))) offset--; //$NON-NLS-1$ //$NON-NLS-2$
		if (offset == 0 || document.get(offset,1).equals(".") //$NON-NLS-1$
			|| document.get(offset,1).equals("\r") || document.get(offset,1).equals("\n")) //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		return false;
	}
	
	private boolean contributes() {
		return true || PreferenceConstants.getPreferenceStore().getBoolean(
				PreferenceConstants.SPELLING_ENABLE_CONTENTASSIST);
	}

	/**
	 * @seeorg.eclipse.jface.text.contentassist.ICompletionProposalComputer#
	 * computeContextInformation
	 * (org.eclipse.jface.text.contentassist.TextContentAssistInvocationContext,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	public List computeContextInformation(
			ContentAssistInvocationContext context, IProgressMonitor monitor) {
		return Collections.EMPTY_LIST;
	}

	/**
	 * @seeorg.eclipse.jface.text.contentassist.ICompletionProposalComputer#
	 * getErrorMessage()
	 */
	public String getErrorMessage() {
		return null; // no error message available
	}

	/**
	 * @see
	 * org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer#sessionStarted
	 * ()
	 */
	public void sessionStarted() {
	}

	/**
	 * @see
	 * org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer#sessionEnded
	 * ()
	 */
	public void sessionEnded() {
	}
	
	public static void dispose()
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(stateLocation + "\\rates.txt"); //$NON-NLS-1$
			basicWordRanker.saveToStream(fos);
			fos.close();
			fos = new FileOutputStream(stateLocation + "\\userRates.txt"); //$NON-NLS-1$
			userWordRanker.saveToStream(fos);
			fos.close();
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void assistSessionEnded(ContentAssistEvent event)
	{
				
	}

	public void assistSessionStarted(ContentAssistEvent event)
	{
		// TODO Auto-generated method stub
		
	}

	public void selectionChanged(ICompletionProposal proposal,
			boolean smartToggle)
	{
		// TODO Auto-generated method stub
		
	}
}
