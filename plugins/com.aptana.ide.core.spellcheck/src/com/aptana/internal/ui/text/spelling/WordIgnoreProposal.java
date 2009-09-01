/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package com.aptana.internal.ui.text.spelling;

import java.text.MessageFormat;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.source.ISourceViewer;

import org.eclipse.ui.texteditor.spelling.SpellingProblem;

import com.aptana.internal.ui.text.spelling.engine.ISpellCheckEngine;
import com.aptana.internal.ui.text.spelling.engine.ISpellChecker;

/**
 * Proposal to ignore the word during the current editing session.
 * 
 * @since 3.0
 */
public class WordIgnoreProposal implements ICompletionProposal {

	/** The word to ignore */
	private final String fWord;
	private final IInvocationContext fContext;

	/**
	 * Creates a new spell ignore proposal.
	 * 
	 * @param word
	 *            The word to ignore
	 * @param context
	 *            The invocation context
	 */
	public WordIgnoreProposal(final String word,
			final IInvocationContext context) {
		this.fWord = word;
		this.fContext = context;
	}

	/*
	 * @see
	 * org.eclipse.jface.text.contentassist.ICompletionProposal#apply(org.eclipse
	 * .jface.text.IDocument)
	 */
	public final void apply(final IDocument document) {

		final ISpellCheckEngine engine = SpellCheckEngine.getInstance();
		final ISpellChecker checker = engine.getSpellChecker();

		if (checker != null) {
			checker.ignoreWord(this.fWord);
			if (this.fContext instanceof IQuickAssistInvocationContext) {
				final ISourceViewer sourceViewer = ((IQuickAssistInvocationContext) this.fContext)
						.getSourceViewer();
				if (sourceViewer != null) {
					SpellingProblemUtils.removeAll(sourceViewer, this.fWord);
				}
			}
		}
	}

	/*
	 * @seeorg.eclipse.jface.text.contentassist.ICompletionProposal#
	 * getAdditionalProposalInfo()
	 */
	public String getAdditionalProposalInfo() {
		return MessageFormat.format(JavaUIMessages.Spelling_ignore_info,
				WordCorrectionProposal.getHtmlRepresentation(this.fWord));
	}

	/*
	 * @seeorg.eclipse.jface.text.contentassist.ICompletionProposal#
	 * getContextInformation()
	 */
	public final IContextInformation getContextInformation() {
		return null;
	}

	/*
	 * @see
	 * org.eclipse.jface.text.contentassist.ICompletionProposal#getDisplayString
	 * ()
	 */
	public String getDisplayString() {
		return MessageFormat.format(JavaUIMessages.Spelling_ignore_label,
				this.fWord);
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getImage()
	 */
	public Image getImage() {
		return JavaPluginImages
				.get(JavaPluginImages.IMG_OBJS_NLS_NEVER_TRANSLATE);
	}

	/*
	 * @see org.eclipse.jdt.ui.text.java.IJavaCompletionProposal#getRelevance()
	 */
	public final int getRelevance() {
		return Integer.MIN_VALUE + 1;
	}

	/*
	 * @see
	 * org.eclipse.jface.text.contentassist.ICompletionProposal#getSelection
	 * (org.eclipse.jface.text.IDocument)
	 */
	public final Point getSelection(final IDocument document) {
		return new Point(this.fContext.getSelectionOffset(), this.fContext
				.getSelectionLength());
	}
}
