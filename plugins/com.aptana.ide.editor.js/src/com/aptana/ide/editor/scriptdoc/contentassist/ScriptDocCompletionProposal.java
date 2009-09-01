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
package com.aptana.ide.editor.scriptdoc.contentassist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.aptana.ide.editors.unified.contentassist.IUnifiedCompletionProposal;

/**
 * @author Robin Debreuil
 */
public class ScriptDocCompletionProposal implements ICompletionProposal, IUnifiedCompletionProposal
{

	/** The string to be displayed in the completion proposal popup. */
	private String fDisplayString;
	/** The replacement string. */
	private String fReplacementString;
	/** The replacement offset. */
	private int fReplacementOffset;
	/** The replacement length. */
	private int fReplacementLength;
	/** The cursor position after this proposal has been applied. */
	private int fCursorPosition;
	/** The image to be displayed in the completion proposal popup. */
	private Image fImage;
	/** The context information of this proposal. */
	private IContextInformation fContextInformation;
	/** The additional info of this proposal. */
	private String fAdditionalProposalInfo;
	/** The type of the object. This is an 'enum' of JSCompletionProposalComparator sorting types. */
	private int fObjectType;

	/**
	 * Creates a new completion proposal. All fields are initialized based on the provided information.
	 *
	 * @param replacementString the actual string to be inserted into the document
	 * @param replacementOffset the offset of the text to be replaced
	 * @param replacementLength the length of the text to be replaced
	 * @param cursorPosition the position of the cursor following the insert relative to replacementOffset
	 * @param image the image to display for this proposal
	 * @param displayString the string to be displayed for the proposal
	 * @param contextInformation the context information associated with this proposal
	 * @param additionalProposalInfo the additional information associated with this proposal
	 * @param objectType The type of the object. This is an 'enum' of JSCompletionProposalComparator sorting types (used for quicker sorting).
	 */
	public ScriptDocCompletionProposal(
			String replacementString, 
			int replacementOffset, 
			int replacementLength, 
			int cursorPosition, 
			Image image, 
			String displayString, 
			IContextInformation contextInformation, 
			String additionalProposalInfo,
			int objectType)
	{
//		Assert.isNotNull(replacementString);
//		Assert.isTrue(replacementOffset >= 0);
//		Assert.isTrue(replacementLength >= 0);
//		Assert.isTrue(cursorPosition >= 0);

		fReplacementString= replacementString;
		fReplacementOffset= replacementOffset;
		fReplacementLength= replacementLength;
		fCursorPosition= cursorPosition;
		fImage= image;
		fDisplayString= displayString;
		fContextInformation= contextInformation;
		fAdditionalProposalInfo= additionalProposalInfo;
		fObjectType = objectType;
	}


	/**
	 * Returns the type of object this proposal contains (class, method, etc).This is used for sorting.
	 * @return Returns the type of object this proposal contains (class, method, etc).This is used for sorting.
	 */
	public int getObjectType() {
		return fObjectType;
	}
	
	/**
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#apply(org.eclipse.jface.text.IDocument)
	 */
	public void apply(IDocument document) {
		try {
			document.replace(fReplacementOffset, fReplacementLength, fReplacementString);
		} catch (BadLocationException x) {
			// ignore
		}
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getSelection(org.eclipse.jface.text.IDocument)
	 */
	public Point getSelection(IDocument document) {
		return new Point(fReplacementOffset + fCursorPosition, 0);
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getContextInformation()
	 */
	public IContextInformation getContextInformation() {
		return fContextInformation;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getImage()
	 */
	public Image getImage() {
		return fImage;
	}

	/**
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getDisplayString()
	 */
	public String getDisplayString() {
		if (fDisplayString != null)
		{
			return fDisplayString;
		}
		return fReplacementString;
	}

	/**
	 * @see com.aptana.ide.editors.unified.contentassist.IUnifiedCompletionProposal#getReplaceString()
	 */
	public String getReplaceString() {
		return fReplacementString;
	}
	
	/**
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getAdditionalProposalInfo()
	 */
	public String getAdditionalProposalInfo() {
		return fAdditionalProposalInfo;
	}

	/**
	 * defaultSelection
	 */
	public boolean defaultSelection = false;

    /**
     * suggestedSelection
     */
    public boolean suggestedSelection = false;
	private Image[] userAgentImages;
	
	/**
	 * @return Returns the defaultSelection.
	 */
	public boolean isDefaultSelection()
	{
		return defaultSelection;
	}

	/**
	 * @param defaultSelection The defaultSelection to set.
	 */
	public void setDefaultSelection(boolean defaultSelection)
	{
		this.defaultSelection = defaultSelection;
	}

	/**
	 * @see com.aptana.ide.editors.unified.contentassist.IUnifiedCompletionProposal#getFileLocation()
	 */
	public String getFileLocation()
	{
		return ""; //$NON-NLS-1$
	}
	
	/**
	 * @return Returns the replacementLength.
	 */
	public int getReplacementLength() {
		return fReplacementLength;
	}

	/**
	 * @param replacementLength The replacementLength to set.
	 */
	public void setReplacementLength(int replacementLength) {
		this.fReplacementLength = replacementLength;
	}
	
	/**
	 * @see com.aptana.ide.editors.unified.contentassist.IUnifiedCompletionProposal#getUserAgentImages()
	 */
	public Image[] getUserAgentImages() {
		return userAgentImages;
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
}
