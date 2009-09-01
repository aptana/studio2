/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.internal.ui.text.spelling;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;

/**
 * Spelling problem to be accepted by problem requesters.
 * 
 * @since 3.1
 */
public class CoreSpellingProblem extends SpellingProblem {

	// spelling 'marker type' name. Only virtual as spelling problems are never
	// persisted in markers.
	// marker type is used in the quickFixProcessor extension point
	public static final String MARKER_TYPE = "org.eclipse.jdt.ui.internal.spelling"; //$NON-NLS-1$

	/** The end offset of the problem */
	private int fSourceEnd = 0;

	/** The line number of the problem */
	private int fLineNumber = 1;

	/** The start offset of the problem */
	private int fSourceStart = 0;

	/** The description of the problem */
	private final String fMessage;

	/** The misspelled word */
	private final String fWord;

	/** Was the word found in the dictionary? */
	private final boolean fMatch;

	/** Does the word start a new sentence? */
	private final boolean fSentence;

	/** The associated document */
	private final IDocument fDocument;

	/** The originating file name */
	private final String fOrigin;

	/**
	 * Initialize with the given parameters.
	 * 
	 * @param start
	 *            the start offset
	 * @param end
	 *            the end offset
	 * @param line
	 *            the line
	 * @param message
	 *            the message
	 * @param word
	 *            the word
	 * @param match
	 *            <code>true</code> iff the word was found in the dictionary
	 * @param sentence
	 *            <code>true</code> iff the word starts a sentence
	 * @param document
	 *            the document
	 * @param origin
	 *            the originating file name
	 */
	public CoreSpellingProblem(int start, int end, int line, String message,
			String word, boolean match, boolean sentence, IDocument document,
			String origin) {
		super();
		this.fSourceStart = start;
		this.fSourceEnd = end;
		this.fLineNumber = line;
		this.fMessage = message;
		this.fWord = word;
		this.fMatch = match;
		this.fSentence = sentence;
		this.fDocument = document;
		this.fOrigin = origin;
	}

	/*
	 * @see org.eclipse.jdt.core.compiler.IProblem#getArguments()
	 */
	public String[] getArguments() {

		String prefix = ""; //$NON-NLS-1$
		String postfix = ""; //$NON-NLS-1$

		try {

			final IRegion line = this.fDocument
					.getLineInformationOfOffset(this.fSourceStart);
			prefix = this.fDocument.get(line.getOffset(), this.fSourceStart
					- line.getOffset());
			final int postfixStart = this.fSourceEnd + 1;
			postfix = this.fDocument.get(postfixStart, line.getOffset()
					+ line.getLength() - postfixStart);

		} catch (final BadLocationException exception) {
			// Do nothing
		}
		return new String[] {
				this.fWord,
				prefix,
				postfix,
				this.fSentence ? Boolean.toString(true) : Boolean
						.toString(false),
				this.fMatch ? Boolean.toString(true) : Boolean.toString(false) };
	}

	/** The id of the problem */
	public static final int SPELLING_PROBLEM_ID = 0x80000000;

	/*
	 * @see org.eclipse.jdt.core.compiler.IProblem#getID()
	 */
	public int getID() {
		return SPELLING_PROBLEM_ID;
	}

	/*
	 * @see org.eclipse.jdt.core.compiler.IProblem#getMessage()
	 */
	public String getMessage() {
		return this.fMessage;
	}

	/*
	 * @see org.eclipse.jdt.core.compiler.IProblem#getOriginatingFileName()
	 */
	public char[] getOriginatingFileName() {
		return this.fOrigin.toCharArray();
	}

	/*
	 * @see org.eclipse.jdt.core.compiler.IProblem#getSourceEnd()
	 */
	public int getSourceEnd() {
		return this.fSourceEnd;
	}

	/*
	 * @see org.eclipse.jdt.core.compiler.IProblem#getSourceLineNumber()
	 */
	public int getSourceLineNumber() {
		return this.fLineNumber;
	}

	/*
	 * @see org.eclipse.jdt.core.compiler.IProblem#getSourceStart()
	 */
	public int getSourceStart() {
		return this.fSourceStart;
	}

	/*
	 * @see org.eclipse.jdt.core.compiler.IProblem#isError()
	 */
	public boolean isError() {
		return false;
	}

	/*
	 * @see org.eclipse.jdt.core.compiler.IProblem#isWarning()
	 */
	public boolean isWarning() {
		return true;
	}

	/*
	 * @see org.eclipse.jdt.core.compiler.IProblem#setSourceStart(int)
	 */
	public void setSourceStart(int sourceStart) {
		this.fSourceStart = sourceStart;
	}

	/*
	 * @see org.eclipse.jdt.core.compiler.IProblem#setSourceEnd(int)
	 */
	public void setSourceEnd(int sourceEnd) {
		this.fSourceEnd = sourceEnd;
	}

	/*
	 * @see org.eclipse.jdt.core.compiler.IProblem#setSourceLineNumber(int)
	 */
	public void setSourceLineNumber(int lineNumber) {
		this.fLineNumber = lineNumber;
	}

	/*
	 * @see org.eclipse.jdt.core.compiler.CategorizedProblem#getCategoryID()
	 */
	public int getCategoryID() {
		return 0;
	}

	/*
	 * @see org.eclipse.jdt.core.compiler.CategorizedProblem#getMarkerType()
	 */
	public String getMarkerType() {
		return MARKER_TYPE;
	}

	
	public int getLength() {
		return this.fSourceEnd - this.fSourceStart;
	}

	
	public int getOffset() {
		return this.fSourceStart;
	}

	
	public ICompletionProposal[] getProposals() {
		return null;
	}
}
