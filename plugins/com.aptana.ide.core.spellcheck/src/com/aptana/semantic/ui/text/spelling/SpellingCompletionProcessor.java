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
package com.aptana.semantic.ui.text.spelling;

import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import com.aptana.internal.ui.text.spelling.ContentAssistInvocationContext;
import com.aptana.internal.ui.text.spelling.WordCompletionProposalComputer;

public class SpellingCompletionProcessor implements IContentAssistProcessor {

	WordCompletionProposalComputer computer = new WordCompletionProposalComputer();

	
	
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		final ContentAssistInvocationContext context = new ContentAssistInvocationContext(
				viewer.getDocument(), offset);
		final List computeCompletionProposals = this.computer
				.computeCompletionProposals(context, null);
		final ICompletionProposal[] prs = new ICompletionProposal[computeCompletionProposals
				.size()];
		computeCompletionProposals.toArray(prs);
		return prs;
	}

	
	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		return null;
	}

	
	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	
	public String getErrorMessage() {
		return null;
	}	
	
	/**
	 * @return the computer
	 */
	public WordCompletionProposalComputer getComputer()
	{
		return computer;
	}

}
