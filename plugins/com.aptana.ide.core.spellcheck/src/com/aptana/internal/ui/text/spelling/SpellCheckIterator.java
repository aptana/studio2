/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.internal.ui.text.spelling;

import java.text.BreakIterator;
import java.util.LinkedList;
import java.util.Locale;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;

import com.aptana.internal.ui.text.spelling.engine.DefaultSpellChecker;
import com.aptana.internal.ui.text.spelling.engine.ISpellCheckIterator;

/**
 * Iterator to spell check javadoc comment regions.
 * 
 * @since 3.0
 */
public class SpellCheckIterator implements ISpellCheckIterator {

	/** The content of the region */
	protected final String fContent;

	/** The line delimiter */
	private final String fDelimiter;

	/** The last token */
	protected String fLastToken = null;

	/** The next break */
	protected int fNext = 1;

	/** The offset of the region */
	protected final int fOffset;

	/** The predecessor break */
	private int fPredecessor;

	/** The previous break */
	protected int fPrevious = 0;

	/** The sentence breaks */
	private final LinkedList fSentenceBreaks = new LinkedList();

	/** Does the current word start a sentence? */
	private boolean fStartsSentence = false;

	/** The successor break */
	protected int fSuccessor;

	/** The word iterator */
	private final BreakIterator fWordIterator;

	private boolean fIsIgnoringSingleLetters;

	/**
	 * Creates a new spell check iterator.
	 * 
	 * @param document
	 *            the document containing the specified partition
	 * @param region
	 *            the region to spell check
	 * @param locale
	 *            the locale to use for spell checking
	 */
	public SpellCheckIterator(IDocument document, IRegion region, Locale locale) {
		this(document, region, locale, BreakIterator.getWordInstance(locale));
	}

	/**
	 * Creates a new spell check iterator.
	 * 
	 * @param document
	 *            the document containing the specified partition
	 * @param region
	 *            the region to spell check
	 * @param locale
	 *            the locale to use for spell checking
	 * @param breakIterator
	 *            the break-iterator
	 */
	public SpellCheckIterator(IDocument document, IRegion region,
			Locale locale, BreakIterator breakIterator) {
		this.fOffset = region.getOffset();
		this.fWordIterator = breakIterator;
		this.fDelimiter = TextUtilities.getDefaultLineDelimiter(document);

		String content;
		try {

			content = document.get(region.getOffset(), region.getLength());
			// if (content.startsWith(NLSElement.TAG_PREFIX))
			//content= ""; //$NON-NLS-1$

		} catch (final Exception exception) {
			content = ""; //$NON-NLS-1$
		}
		this.fContent = content;

		this.fWordIterator.setText(content);
		this.fPredecessor = this.fWordIterator.first();
		this.fSuccessor = this.fWordIterator.next();

		final BreakIterator iterator = BreakIterator
				.getSentenceInstance(locale);
		iterator.setText(content);

		int offset = iterator.current();
		while (offset != BreakIterator.DONE) {

			this.fSentenceBreaks.add(new Integer(offset));
			offset = iterator.next();
		}
	}

	/*
	 * @seecom.onpositive.internal.ui.text.spelling.engine.ISpellCheckIterator#
	 * setIgnoreSingleLetters(boolean)
	 * 
	 * @since 3.3
	 */
	public void setIgnoreSingleLetters(boolean state) {
		this.fIsIgnoringSingleLetters = state;
	}

	/*
	 * @see org.eclipse.spelling.done.ISpellCheckIterator#getBegin()
	 */
	public final int getBegin() {
		return this.fPrevious + this.fOffset;
	}

	/*
	 * @see org.eclipse.spelling.done.ISpellCheckIterator#getEnd()
	 */
	public final int getEnd() {
		return this.fNext + this.fOffset - 1;
	}

	/*
	 * @see java.util.Iterator#hasNext()
	 */
	public final boolean hasNext() {
		return this.fSuccessor != BreakIterator.DONE;
	}

	/**
	 * Does the specified token consist of at least one letter and digits only?
	 * 
	 * @param begin
	 *            the begin index
	 * @param end
	 *            the end index
	 * @return <code>true</code> iff the token consists of digits and at least
	 *         one letter only, <code>false</code> otherwise
	 */
	protected final boolean isAlphaNumeric(final int begin, final int end) {

		char character = 0;

		boolean letter = false;
		for (int index = begin; index < end; index++) {

			character = this.fContent.charAt(index);
			if (Character.isLetter(character)) {
				letter = true;
			}

			if (!Character.isLetterOrDigit(character)) {
				return false;
			}
		}
		return letter;
	}

	/**
	 * Checks the last token against the given tags?
	 * 
	 * @param tags
	 *            the tags to check
	 * @return <code>true</code> iff the last token is in the given array
	 */
	protected final boolean isToken(final String[] tags) {
		return this.isToken(this.fLastToken, tags);
	}

	/**
	 * Checks the given token against the given tags?
	 * 
	 * @param token
	 *            the token to check
	 * @param tags
	 *            the tags to check
	 * @return <code>true</code> iff the last token is in the given array
	 * @since 3.3
	 */
	protected final boolean isToken(final String token, final String[] tags) {

		if (token != null) {

			for (int index = 0; index < tags.length; index++) {

				if (token.equals(tags[index])) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Is the current token a single letter token surrounded by non-whitespace
	 * characters?
	 * 
	 * @param begin
	 *            the begin index
	 * @return <code>true</code> iff the token is a single letter token,
	 *         <code>false</code> otherwise
	 */
	protected final boolean isSingleLetter(final int begin) {
		if (!Character.isLetter(this.fContent.charAt(begin))) {
			return false;
		}

		if ((begin > 0)
				&& !Character.isWhitespace(this.fContent.charAt(begin - 1))) {
			return false;
		}

		if ((begin < this.fContent.length() - 1)
				&& !Character.isWhitespace(this.fContent.charAt(begin + 1))) {
			return false;
		}

		return true;
	}

	/**
	 * Does the specified token look like an URL?
	 * 
	 * @param begin
	 *            the begin index
	 * @return <code>true</code> iff this token look like an URL,
	 *         <code>false</code> otherwise
	 */
	protected final boolean isUrlToken(final int begin) {

		for (int index = 0; index < DefaultSpellChecker.URL_PREFIXES.length; index++) {

			if (this.fContent.startsWith(
					DefaultSpellChecker.URL_PREFIXES[index], begin)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Does the specified token consist of whitespace only?
	 * 
	 * @param begin
	 *            the begin index
	 * @param end
	 *            the end index
	 * @return <code>true</code> iff the token consists of whitespace only,
	 *         <code>false</code> otherwise
	 */
	protected final boolean isWhitespace(final int begin, final int end) {

		for (int index = begin; index < end; index++) {

			if (!Character.isWhitespace(this.fContent.charAt(index))) {
				return false;
			}
		}
		return true;
	}

	/*
	 * @see java.util.Iterator#next()
	 */
	public Object next() {

		String token = this.nextToken();
		while ((token == null) && (this.fSuccessor != BreakIterator.DONE)) {
			token = this.nextToken();
		}

		this.fLastToken = token;

		return token;
	}

	/**
	 * Advances the end index to the next word break.
	 */
	protected final void nextBreak() {

		this.fNext = this.fSuccessor;
		this.fPredecessor = this.fSuccessor;

		this.fSuccessor = this.fWordIterator.next();
	}

	/**
	 * Returns the next sentence break.
	 * 
	 * @return the next sentence break
	 */
	protected final int nextSentence() {
		return ((Integer) this.fSentenceBreaks.getFirst()).intValue();
	}

	/**
	 * Determines the next token to be spell checked.
	 * 
	 * @return the next token to be spell checked, or <code>null</code> iff the
	 *         next token is not a candidate for spell checking.
	 */
	protected String nextToken() {

		String token = null;

		this.fPrevious = this.fPredecessor;
		this.fStartsSentence = false;

		this.nextBreak();

		boolean update = false;
		if (this.fNext - this.fPrevious > 0) {

			if ((this.fSuccessor != BreakIterator.DONE)
					&& (this.fContent.charAt(this.fPrevious) == IJavaDocTagConstants.JAVADOC_TAG_PREFIX)) {

				this.nextBreak();
				if (Character
						.isLetter(this.fContent.charAt(this.fPrevious + 1))) {
					update = true;
					token = this.fContent.substring(this.fPrevious, this.fNext);
				} else {
					this.fPredecessor = this.fNext;
				}

			} else if ((this.fSuccessor != BreakIterator.DONE)
					&& (this.fContent.charAt(this.fPrevious) == IHtmlTagConstants.HTML_TAG_PREFIX)
					&& (Character.isLetter(this.fContent.charAt(this.fNext)) || (this.fContent
							.charAt(this.fNext) == '/'))) {

				if (this.fContent.startsWith(
						IHtmlTagConstants.HTML_CLOSE_PREFIX, this.fPrevious)) {
					this.nextBreak();
				}

				this.nextBreak();

				if ((this.fSuccessor != BreakIterator.DONE)
						&& (this.fContent.charAt(this.fNext) == IHtmlTagConstants.HTML_TAG_POSTFIX)) {

					this.nextBreak();
					if (this.fSuccessor != BreakIterator.DONE) {
						update = true;
						token = this.fContent.substring(this.fPrevious,
								this.fNext);
					}
				}
			} else if ((this.fSuccessor != BreakIterator.DONE)
					&& (this.fContent.charAt(this.fPrevious) == IHtmlTagConstants.HTML_ENTITY_START)
					&& (Character.isLetter(this.fContent.charAt(this.fNext)))) {
				this.nextBreak();
				if ((this.fSuccessor != BreakIterator.DONE)
						&& (this.fContent.charAt(this.fNext) == IHtmlTagConstants.HTML_ENTITY_END)) {
					this.nextBreak();
					if (this.isToken(this.fContent.substring(this.fPrevious,
							this.fNext), IHtmlTagConstants.HTML_ENTITY_CODES)) {
						this.skipTokens(this.fPrevious,
								IHtmlTagConstants.HTML_ENTITY_END);
						update = true;
					} else {
						token = this.fContent.substring(this.fPrevious,
								this.fNext);
					}
				} else {
					token = this.fContent.substring(this.fPrevious, this.fNext);
				}

				update = true;
			} else if (!this.isWhitespace(this.fPrevious, this.fNext)
					&& this.isAlphaNumeric(this.fPrevious, this.fNext)) {

				if (this.isUrlToken(this.fPrevious)) {
					this.skipTokens(this.fPrevious, ' ');
				} else if (this
						.isToken(IJavaDocTagConstants.JAVADOC_PARAM_TAGS)) {
					this.fLastToken = null;
				} else if (this
						.isToken(IJavaDocTagConstants.JAVADOC_REFERENCE_TAGS)) {
					this.fLastToken = null;
					this.skipTokens(this.fPrevious, this.fDelimiter.charAt(0));
				} else if ((this.fNext - this.fPrevious > 1)
						|| (this.isSingleLetter(this.fPrevious) && !this.fIsIgnoringSingleLetters)) {
					token = this.fContent.substring(this.fPrevious, this.fNext);
				}

				update = true;
			}
		}

		if (update && (this.fSentenceBreaks.size() > 0)) {

			if (this.fPrevious >= this.nextSentence()) {

				while ((this.fSentenceBreaks.size() > 0)
						&& (this.fPrevious >= this.nextSentence())) {
					this.fSentenceBreaks.removeFirst();
				}

				this.fStartsSentence = (this.fLastToken == null)
						|| (token != null);
			}
		}
		return token;
	}

	/*
	 * @see java.util.Iterator#remove()
	 */
	public final void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Skip the tokens until the stop character is reached.
	 * 
	 * @param begin
	 *            the begin index
	 * @param stop
	 *            the stop character
	 */
	protected final void skipTokens(final int begin, final char stop) {

		int end = begin;

		while ((end < this.fContent.length())
				&& (this.fContent.charAt(end) != stop)) {
			end++;
		}

		if (end < this.fContent.length()) {

			this.fNext = end;
			this.fPredecessor = this.fNext;

			this.fSuccessor = this.fWordIterator.following(this.fNext);
		} else {
			this.fSuccessor = BreakIterator.DONE;
		}
	}

	/*
	 * @see org.eclipse.spelling.done.ISpellCheckIterator#startsSentence()
	 */
	public final boolean startsSentence() {
		return this.fStartsSentence;
	}
}
