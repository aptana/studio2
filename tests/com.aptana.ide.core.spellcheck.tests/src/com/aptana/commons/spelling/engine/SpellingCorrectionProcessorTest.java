package com.aptana.commons.spelling.engine;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;

import com.aptana.ide.editors.junit.TestTextViewer;
import com.aptana.internal.ui.text.spelling.AddWordProposal;
import com.aptana.internal.ui.text.spelling.DisableSpellCheckingProposal;
import com.aptana.internal.ui.text.spelling.JavaSpellingProblem;
import com.aptana.internal.ui.text.spelling.SpellCheckEngine;
import com.aptana.internal.ui.text.spelling.WordCorrectionProposal;
import com.aptana.internal.ui.text.spelling.WordIgnoreProposal;
import com.aptana.internal.ui.text.spelling.engine.ISpellChecker;
import com.aptana.internal.ui.text.spelling.engine.SpellEvent;

public class SpellingCorrectionProcessorTest extends TestCase
{

	/**
	 * Tests when we have no spelling errors.
	 */
	public void testNoCompletions()
	{
		final String source = "spelling";
		final int offset = 0;
		final int length = source.length();

		SpellingCorrectionProcessor processor = new SpellingCorrectionProcessor();
		ISourceViewer sourceViewer = new TestTextViewer(source)
		{
			@Override
			public Point getSelectedRange()
			{
				return new Point(offset, length);
			}
		};
		IQuickAssistInvocationContext quickAssistContext = new TextInvocationContext(sourceViewer, offset, length);
		ICompletionProposal[] proposals = processor.computeQuickAssistProposals(quickAssistContext);
		assertNotNull(proposals);
		assertEquals(1, proposals.length);
		assertTrue(proposals[0] instanceof NoCompletionsProposal);
	}

	/**
	 * Tests that we have a number of completions available: add the word to dictionary, ignore word, fix the word, or
	 * disable spellchecking.
	 */
	public void testCompletionsAvailable()
	{
		final String source = "speling";
		final int offset = 0;
		final int length = source.length();

		SpellingCorrectionProcessor processor = new SpellingCorrectionProcessor();
		ISourceViewer sourceViewer = new TestTextViewer(source)
		{
			@Override
			public Point getSelectedRange()
			{
				return new Point(offset, length);
			}

			@Override
			public IAnnotationModel getAnnotationModel()
			{
				AnnotationModel model = new AnnotationModel();
				ISpellChecker checker = SpellCheckEngine.getInstance().getSpellChecker();
				SpellingProblem problem = new JavaSpellingProblem(new SpellEvent(checker, source, offset, length - 1,
						false, false)
				{
				}, new Document(source));
				SpellingAnnotation annotation = new SpellingAnnotation(problem);
				Position position = new Position(offset, length);
				model.addAnnotation(annotation, position);
				return model;
			}
		};
		IQuickAssistInvocationContext quickAssistContext = new TextInvocationContext(sourceViewer, offset, length);
		ICompletionProposal[] proposals = processor.computeQuickAssistProposals(quickAssistContext);
		assertNotNull(proposals);
		assertEquals(4, proposals.length);
		assertTrue(contains(proposals, WordIgnoreProposal.class));
		assertTrue(contains(proposals, AddWordProposal.class));
		assertTrue(contains(proposals, DisableSpellCheckingProposal.class));
		assertTrue(contains(proposals, WordCorrectionProposal.class));
	}

	private boolean contains(ICompletionProposal[] proposals, Class klazz)
	{
		for (ICompletionProposal completionProposal : proposals)
		{
			if (completionProposal.getClass().equals(klazz))
				return true;
		}
		return false;
	}

}
