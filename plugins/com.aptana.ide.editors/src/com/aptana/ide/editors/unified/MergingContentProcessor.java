package com.aptana.ide.editors.unified;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import com.aptana.ide.editors.unified.contentassist.IUnifiedContentAssistProcessor;
import com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor;
import com.aptana.ide.editors.unified.contentassist.UnifiedTemplateCompletionProcessor;
import com.aptana.ide.parsing.IOffsetMapper;

public class MergingContentProcessor implements IContentAssistProcessor, IUnifiedContentAssistProcessor
{
	private IContentAssistProcessor processor;
	private UnifiedTemplateCompletionProcessor templateProcessor;

	/**
	 * MergingContentProcessor
	 * 
	 * @param processor
	 * @param unifiedTemplateCompletionProcessor
	 */
	public MergingContentProcessor(IContentAssistProcessor processor, UnifiedTemplateCompletionProcessor unifiedTemplateCompletionProcessor)
	{
		this.processor = processor;
		this.templateProcessor = unifiedTemplateCompletionProcessor;
	}

	/**
	 * computeCompletionProposals
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset)
	{
		return computeCompletionProposals(viewer, offset, UnifiedContentAssistProcessor.DEFAULT_CHARACTER);
	}

	/**
	 * computeCompletionProposals
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset, char activationChar)
	{
		return computeCompletionProposals(viewer, offset, activationChar, false);
	}

	/**
	 * computeCompletionProposals
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset, char activationChar, boolean autoActivated)
	{
		ICompletionProposal[] proposals;
		if (processor instanceof IUnifiedContentAssistProcessor)
		{
			proposals = ((IUnifiedContentAssistProcessor) processor).computeCompletionProposals(viewer, offset, activationChar, autoActivated);
		}
		else
		{
			proposals = processor.computeCompletionProposals(viewer, offset);
		}
		ICompletionProposal[] templates = templateProcessor.computeCompletionProposals(viewer, offset);
		if (proposals == null || proposals.length == 0)
			return templates;
		if (templates == null || templates.length == 0)
			return proposals;
		ICompletionProposal[] combined = new ICompletionProposal[proposals.length + templates.length];
		System.arraycopy(proposals, 0, combined, 0, proposals.length);
		System.arraycopy(templates, 0, combined, proposals.length, templates.length);

		Arrays.sort(combined, new Comparator<ICompletionProposal>()
		{
			public int compare(ICompletionProposal o1, ICompletionProposal o2)
			{
				if (o1 == null)
					return -1;
				if (o2 == null)
					return 1;
				return o1.getDisplayString().compareTo(o2.getDisplayString());
			}
		});
		return combined;
	}

	/**
	 * computeContextInformation
	 */
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset)
	{
		return processor.computeContextInformation(viewer, offset);
	}

	/**
	 * getCompletionProposalAutoActivationCharacters
	 */
	public char[] getCompletionProposalAutoActivationCharacters()
	{
		return processor.getCompletionProposalAutoActivationCharacters();
	}

	/**
	 * getCompletionProposalIdleActivationTokens
	 */
	public int[] getCompletionProposalIdleActivationTokens()
	{
		if (processor instanceof IUnifiedContentAssistProcessor)
		{
			return ((IUnifiedContentAssistProcessor) processor).getCompletionProposalIdleActivationTokens();
		}
		return null;
	}

	/**
	 * getContentAssistProcessor
	 * 
	 * @return
	 */
	public IContentAssistProcessor getContentAssistProcessor()
	{
		return processor;
	}
	
	/**
	 * getContextInformationAutoActivationCharacters
	 */
	public char[] getContextInformationAutoActivationCharacters()
	{
		return processor.getContextInformationAutoActivationCharacters();
	}

	/**
	 * getContextInformationValidator
	 */
	public IContextInformationValidator getContextInformationValidator()
	{
		return processor.getContextInformationValidator();
	}

	/**
	 * getErrorMessage
	 */
	public String getErrorMessage()
	{
		return processor.getErrorMessage();
	}

	/**
	 * getOffsetMapper
	 */
	public IOffsetMapper getOffsetMapper()
	{
		if (processor instanceof IUnifiedContentAssistProcessor)
		{
			return ((IUnifiedContentAssistProcessor) processor).getOffsetMapper();
		}
		return null;
	}

	/**
	 * isValidIdleActivationLocation
	 */
	public boolean isValidIdleActivationLocation(ITextViewer viewer, int offset)
	{
		if (processor instanceof IUnifiedContentAssistProcessor)
		{
			return ((IUnifiedContentAssistProcessor) processor).isValidIdleActivationLocation(viewer, offset);
		}
		return false;
	}
}
