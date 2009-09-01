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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;

import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;

import com.aptana.internal.ui.text.spelling.engine.ISpellCheckEngine;
import com.aptana.internal.ui.text.spelling.engine.ISpellChecker;

/**
 * Proposal to add the unknown word to the dictionaries.
 * 
 * @since 3.0
 */
public class AddWordProposal implements ICompletionProposal {

	private static final String PREF_KEY_DO_NOT_ASK = "do_not_ask_to_install_user_dictionary"; //$NON-NLS-1$

	/** The word to add */
	private final String fWord;

	private final IQuickAssistInvocationContext context;

	/**
	 * Creates a new add word proposal
	 * 
	 * @param word
	 *            The word to add
	 * @param context
	 *            The invocation context
	 */
	public AddWordProposal(final String word, final IInvocationContext context) {
		this.fWord = word;
		this.context = (IQuickAssistInvocationContext) context;
	}

	/*
	 * @see
	 * org.eclipse.jface.text.contentassist.ICompletionProposal#apply(org.eclipse
	 * .jface.text.IDocument)
	 */
	public final void apply(final IDocument document) {

		final ISpellCheckEngine engine = SpellCheckEngine.getInstance();
		final ISpellChecker checker = engine.getSpellChecker();

		if (checker == null) {
			return;
		}

		IQuickAssistInvocationContext quickAssistContext = null;
		if (this.context instanceof IQuickAssistInvocationContext) {
			quickAssistContext = this.context;
		}

		if (!checker.acceptsWords()) {
			final Shell shell;
			if ((quickAssistContext != null)
					&& (quickAssistContext.getSourceViewer() != null)) {
				shell = quickAssistContext.getSourceViewer().getTextWidget()
						.getShell();
			} else {
				shell = Display.getCurrent().getActiveShell();
			}

			if (!canAskToConfigure()
					|| !this.askUserToConfigureUserDictionary(shell)) {
				return;
			}

			final String[] preferencePageIds = new String[] { "org.eclipse.ui.editors.preferencePages.Spelling" }; //$NON-NLS-1$
			PreferencesUtil.createPreferenceDialogOn(shell,
					preferencePageIds[0], preferencePageIds, null).open();
		}

		if (checker.acceptsWords()) {
			checker.addWord(this.fWord);
			if ((quickAssistContext != null)
					&& (quickAssistContext.getSourceViewer() != null)) {
				SpellingProblemUtils.removeAll(quickAssistContext.getSourceViewer(),
						this.fWord);
			}
		}
	}

	/**
	 * Asks the user whether he wants to configure a user dictionary.
	 * 
	 * @param shell
	 * @return <code>true</code> if the user wants to configure the user
	 *         dictionary
	 * @since 3.3
	 */
	private boolean askUserToConfigureUserDictionary(Shell shell) {
		final MessageDialogWithToggle toggleDialog = MessageDialogWithToggle
				.openYesNoQuestion(
						shell,
						JavaUIMessages.Spelling_add_askToConfigure_title,
						JavaUIMessages.Spelling_add_askToConfigure_question,
						JavaUIMessages.Spelling_add_askToConfigure_ignoreMessage,
						false, null, null);

		PreferenceConstants.getPreferenceStore().setValue(PREF_KEY_DO_NOT_ASK,
				toggleDialog.getToggleState());
		return toggleDialog.getReturnCode() == IDialogConstants.YES_ID;
	}

	/**
	 * Tells whether this proposal can ask to configure a user dictionary.
	 * 
	 * @return <code>true</code> if it can ask the user
	 */
	static boolean canAskToConfigure() {
		return !PreferenceConstants.getPreferenceStore().getBoolean(
				PREF_KEY_DO_NOT_ASK);
	}

	/*
	 * @seeorg.eclipse.jface.text.contentassist.ICompletionProposal#
	 * getAdditionalProposalInfo()
	 */
	public String getAdditionalProposalInfo() {
		return MessageFormat.format(JavaUIMessages.Spelling_add_info,
				this.fWord);
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
		return MessageFormat.format(JavaUIMessages.Spelling_add_label,
				this.fWord);
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getImage()
	 */
	public Image getImage() {
		return JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_ADD);
	}

	/*
	 * @see org.eclipse.jdt.ui.text.java.IJavaCompletionProposal#getRelevance()
	 */
	public int getRelevance() {
		return Integer.MIN_VALUE;
	}

	/*
	 * @see
	 * org.eclipse.jface.text.contentassist.ICompletionProposal#getSelection
	 * (org.eclipse.jface.text.IDocument)
	 */
	public final Point getSelection(final IDocument document) {
		return new Point(this.context.getOffset(), this.context.getLength());
	}
}
