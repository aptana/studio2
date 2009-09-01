/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.editors.unified.contentassist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.aptana.ide.core.StringUtils;

/**
 * UnifiedCompletionProposal
 * 
 * @author Ingo Muschenetz
 */
public abstract class UnifiedCompletionProposal implements ICompletionProposal, IUnifiedCompletionProposal// ,
																											// ICompletionProposalExtension,
																											// ICompletionProposalExtension2,
																											// ICompletionProposalExtension3,ICompletionProposalExtension4
{

	/** The string to be displayed in the completion proposal popup. */
	protected String displayString;
	/** The replacement string. */
	protected String replacementString;
	/** The replacement offset. */
	protected int replacementOffset;
	/** The replacement length. */
	protected int replacementLength;
	/** The cursor position after this proposal has been applied. */
	protected int cursorPosition;
	/** The image to be displayed in the completion proposal popup. */
	protected Image image;
	/** The context information of this proposal. */
	protected IContextInformation contextInformation;
	/** The additional info of this proposal. */
	protected String additionalProposalInfo;
	/** The type of the object. This is an 'enum' of JSCompletionProposalComparator sorting types. */
	protected int objectType;
	/** The local document viewer * */
	protected ITextViewer unifiedViewer;
	/** Do we pop code assist once the insertion has happened * */
	protected boolean popAssistOnInsert = false;
	/**
	 * The list of images to display for various user agents
	 */
	protected Image[] userAgentImages;

	/**
	 * Creates a new completion proposal. All fields are initialized based on the provided information.
	 * 
	 * @param replacementString
	 *            the actual string to be inserted into the document
	 * @param replacementOffset
	 *            the offset of the text to be replaced
	 * @param replacementLength
	 *            the length of the text to be replaced
	 * @param cursorPosition
	 *            the position of the cursor following the insert relative to replacementOffset
	 * @param image
	 *            the image to display for this proposal
	 * @param displayString
	 *            the string to be displayed for the proposal
	 * @param contextInformation
	 *            the context information associated with this proposal
	 * @param additionalProposalInfo
	 *            the additional information associated with this proposal
	 * @param objectType
	 *            The type of the object. This is an 'enum' of JSCompletionProposalComparator sorting types (used for
	 *            quicker sorting).
	 * @param unifiedViewer
	 * @param userAgentImages
	 */
	public UnifiedCompletionProposal(String replacementString, int replacementOffset, int replacementLength,
			int cursorPosition, Image image, String displayString, IContextInformation contextInformation,
			String additionalProposalInfo, int objectType, ITextViewer unifiedViewer, Image[] userAgentImages)
	{
		this.replacementString = replacementString;
		this.replacementOffset = replacementOffset;
		this.replacementLength = replacementLength;
		this.cursorPosition = cursorPosition;
		this.image = image;
		this.displayString = displayString;
		this.contextInformation = contextInformation;
		this.additionalProposalInfo = additionalProposalInfo;
		this.objectType = objectType;
		this.unifiedViewer = unifiedViewer;
		this.userAgentImages = userAgentImages;
	}

	/**
	 * Returns the type of object this proposal contains (class, method, etc).This is used for sorting.
	 * 
	 * @return Returns the type of object this proposal contains (class, method, etc).This is used for sorting.
	 */
	public int getObjectType()
	{
		return objectType;
	}

	/**
	 * @see ICompletionProposal#apply(IDocument)
	 */
	public void apply(IDocument document)
	{
		try
		{
			document.replace(replacementOffset, replacementLength, replacementString);

			if (unifiedViewer != null && popAssistOnInsert)
			{
				// Check if source viewer is able to perform operation
				if (((ITextOperationTarget) unifiedViewer).canDoOperation(SourceViewer.CONTENTASSIST_PROPOSALS))
				{
					// Perform operation
					((ITextOperationTarget) unifiedViewer).doOperation(SourceViewer.CONTENTASSIST_PROPOSALS);
				}
			}

		}
		catch (BadLocationException x)
		{
			// ignore
		}
	}

	/**
	 * @see ICompletionProposal#getSelection(IDocument)
	 */
	public Point getSelection(IDocument document)
	{
		return new Point(replacementOffset + cursorPosition, 0);
	}

	/**
	 * @see ICompletionProposal#getContextInformation()
	 */
	public IContextInformation getContextInformation()
	{
		return contextInformation;
	}

	/**
	 * @see ICompletionProposal#getImage()
	 */
	public Image getImage()
	{
		return image;
	}

	/**
	 * @see ICompletionProposal#getDisplayString()
	 */
	public String getDisplayString()
	{
		if (displayString != null)
		{
			return displayString;
		}
		return replacementString;
	}

	/**
	 * @see com.aptana.ide.editors.unified.contentassist.IUnifiedCompletionProposal#getReplaceString()
	 */
	public String getReplaceString()
	{
		return replacementString;
	}

	/**
	 * @see ICompletionProposal#getAdditionalProposalInfo()
	 */
	public String getAdditionalProposalInfo()
	{
		return additionalProposalInfo;
	}

	/**
	 * defaultSelection
	 */
	public boolean defaultSelection = false;

    /**
     * suggestedSelection
     */
    public boolean suggestedSelection = false;
    
	/**
	 * @return Returns the defaultSelection.
	 */
	public boolean isDefaultSelection()
	{
		return defaultSelection;
	}

	/**
	 * @param defaultSelection
	 *            The defaultSelection to set.
	 */
	public void setDefaultSelection(boolean defaultSelection)
	{
		this.defaultSelection = defaultSelection;
	}

    /**
     * @return Returns the defaultSelection.
     */
    public boolean isSuggestedSelection()
    {
        return suggestedSelection;
    }

    /**
     * @param suggestedSelection
     *            The suggestedSelection to set.
     */
    public void setSuggestedSelection(boolean suggestedSelection)
    {
        this.suggestedSelection = suggestedSelection;
    }
    
	/**
	 * activateContentAssistOnInsert
	 * 
	 * @return boolean
	 */
	public boolean activateContentAssistOnInsert()
	{
		return popAssistOnInsert;
	}

	/**
	 * scheduleContentAssistOnInsert
	 * 
	 * @param popAssistOnInsert
	 */
	public void scheduleContentAssistOnInsert(boolean popAssistOnInsert)
	{
		this.popAssistOnInsert = popAssistOnInsert;
	}

	/**
	 * getCursorPosition
	 * 
	 * @return int
	 */
	public int getCursorPosition()
	{
		return cursorPosition;
	}

	/**
	 * setCursorPosition
	 * 
	 * @param cursorPosition
	 */
	public void setCursorPosition(int cursorPosition)
	{
		this.cursorPosition = cursorPosition;
	}

	/**
	 * isPopAssistOnInsert
	 * 
	 * @return boolean
	 */
	public boolean isPopAssistOnInsert()
	{
		return popAssistOnInsert;
	}

	/**
	 * @return Returns the replacementLength.
	 */
	public int getReplacementLength()
	{
		return replacementLength;
	}

	/**
	 * @param replacementLength
	 *            The replacementLength to set.
	 */
	public void setReplacementLength(int replacementLength)
	{
		this.replacementLength = replacementLength;
	}

	/**
	 * setPopAssistOnInsert
	 * 
	 * @param popAssistOnInsert
	 */
	public void setPopAssistOnInsert(boolean popAssistOnInsert)
	{
		this.popAssistOnInsert = popAssistOnInsert;
	}

	/**
	 * getReplacementOffset
	 * 
	 * @return int
	 */
	public int getReplacementOffset()
	{
		return replacementOffset;
	}

	/**
	 * setReplacementOffset
	 * 
	 * @param replacementOffset
	 */
	public void setReplacementOffset(int replacementOffset)
	{
		this.replacementOffset = replacementOffset;
	}

	/**
	 * getReplacementString
	 * 
	 * @return String
	 */
	public String getReplacementString()
	{
		return replacementString;
	}

	/**
	 * setReplacementString
	 * 
	 * @param replacementString
	 */
	public void setReplacementString(String replacementString)
	{
		this.replacementString = replacementString;
	}

	/**
	 * @see com.aptana.ide.editors.unified.contentassist.IUnifiedCompletionProposal#getFileLocation()
	 */
	public String getFileLocation()
	{
		return StringUtils.EMPTY;
	}

	/**
	 * @see com.aptana.ide.editors.unified.contentassist.IUnifiedCompletionProposal#getUserAgentImages()
	 */
	public Image[] getUserAgentImages()
	{
		return userAgentImages;
	}
}
