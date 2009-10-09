package com.aptana.commons.spelling.engine;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class NoCompletionsProposalTest extends TestCase
{

	public void testNoCompletionsProposal()
	{
		ICompletionProposal proposal = new NoCompletionsProposal();
		assertNull(proposal.getAdditionalProposalInfo());
		assertEquals(Messages.NoCompletionsProposal_DisplayText, proposal.getDisplayString());
		assertNull(proposal.getContextInformation());
		assertNull(proposal.getImage());
		assertNull(proposal.getSelection(null));
	}

	public void testApplyDoesNothing()
	{
		final String initialContent = "initial coontent";
		ICompletionProposal proposal = new NoCompletionsProposal();
		IDocument doc = new Document(initialContent);
		proposal.apply(doc);
		assertEquals(initialContent, doc.get());
	}

}
