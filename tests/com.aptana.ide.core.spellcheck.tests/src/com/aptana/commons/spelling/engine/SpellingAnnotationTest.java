package com.aptana.commons.spelling.engine;

import junit.framework.TestCase;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;

public class SpellingAnnotationTest extends TestCase
{

	private SpellingProblem problem;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		problem = new SpellingProblem()
		{

			@Override
			public ICompletionProposal[] getProposals()
			{
				return null;
			}

			@Override
			public int getOffset()
			{
				return 0;
			}

			@Override
			public String getMessage()
			{
				return null;
			}

			@Override
			public int getLength()
			{
				return 0;
			}
		};
	}

	@Override
	protected void tearDown() throws Exception
	{
		problem = null;
		super.tearDown();
	}

	public void testAnnotationIsAlwaysQuickFixable()
	{
		SpellingAnnotation annotation = new SpellingAnnotation(problem);
		assertTrue(annotation.isQuickFixableStateSet());
		annotation.setQuickFixable(false);
		assertTrue(annotation.isQuickFixable());
		assertTrue(annotation.isQuickFixableStateSet());
	}

	public void testGetSpellingProblem()
	{
		SpellingAnnotation annotation = new SpellingAnnotation(problem);
		assertEquals(problem, annotation.getSpellingProblem());
	}
}
