/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
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
import java.util.Locale;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

/**
 * Iterator to spell check Java properties files where '&' is ignored.
 * 
 * @since 3.3
 */
public class PropertiesFileSpellCheckIterator extends SpellCheckIterator {

	public PropertiesFileSpellCheckIterator(IDocument document, IRegion region,
			Locale locale) {
		super(document, region, locale);
	}

	/*
	 * @see com.onpositive.internal.ui.text.spelling.SpellCheckIterator#next()
	 */
	public final Object next() {
		int previous = -1;
		String token = this.nextToken();
		while ((this.fSuccessor != BreakIterator.DONE)
				&& ((token == null) || (this.fContent.charAt(this.fNext) == '&'))) {
			if (token != null) {
				if (previous == -1) {
					previous = this.fPrevious;
				}
				final String nextToken = this.nextToken();
				if (nextToken != null) {
					token = token + nextToken.substring(1);
				} else {
					token = token + '&';
				}
			} else {
				token = this.nextToken();
			}

		}

		if (previous != -1) {
			this.fPrevious = previous;
		}

		if ((token != null) && (token.length() > 1) && token.startsWith("&")) { //$NON-NLS-1$
			token = token.substring(1);

			// Add characters in front of '&'
			while ((this.fPrevious > 0)
					&& !Character.isWhitespace(this.fContent
							.charAt(this.fPrevious - 1))
					&& (this.fContent.charAt(this.fPrevious - 1) != '=')) {
				token = this.fContent.charAt(this.fPrevious - 1) + token;
				this.fPrevious--;
			}

		}

		this.fLastToken = token;

		return token;
	}

}
