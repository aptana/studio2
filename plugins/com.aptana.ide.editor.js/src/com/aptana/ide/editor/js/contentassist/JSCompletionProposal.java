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
package com.aptana.ide.editor.js.contentassist; 

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.aptana.ide.editors.unified.contentassist.IUnifiedCompletionProposal;

/**
 * 
 */
public class JSCompletionProposal implements ICompletionProposal, IUnifiedCompletionProposal//extends UnifiedCompletionProposal
{

	/** The string to be displayed in the completion proposal popup. */
	private String displayString;
	/** The replacement string. */
	private String replacementString;
	/** The replacement offset. */
	private int fReplacementOffset;
	/** The replacement length. */
	private int replacementLength;
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
	 * The source file location where the CA proposal was found.
	 */
	private String fFileLocation;

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
	 * @param fileLocation The source file location where the CA proposal was found.
	 * @param userAgentImages 
	 */
	public JSCompletionProposal(
			String replacementString, 
			int replacementOffset, 
			int replacementLength, 
			int cursorPosition, 
			Image image, 
			String displayString, 
			IContextInformation contextInformation, 
			String additionalProposalInfo,
			int objectType,
			String fileLocation, 
			Image[] userAgentImages)
	{
//		Assert.isNotNull(replacementString);
//		Assert.isTrue(replacementOffset >= 0);
//		Assert.isTrue(replacementLength >= 0);
//		Assert.isTrue(cursorPosition >= 0);

		this.replacementString= replacementString;
		fReplacementOffset= replacementOffset;
		this.replacementLength= replacementLength;
		fCursorPosition= cursorPosition;
		fImage= image;
		this.displayString= displayString;
		fContextInformation= contextInformation;
		fAdditionalProposalInfo= additionalProposalInfo;
		fObjectType = objectType;
		fFileLocation = fileLocation;
		this.userAgentImages = userAgentImages;
		
		// temp adding in loc info
//		String padded = padToColumn(fDisplayString, 30);
//		fDisplayString = padded + " " + fileLocation + " ";
		
//		if(!fileLocation.equals(""))
//		{
//			String repl = "<i>(" + fileLocation + ")</i>";
//			int firstBr = fAdditionalProposalInfo.indexOf("<br>");
//			if(firstBr > -1)
//			{
//				fAdditionalProposalInfo = fAdditionalProposalInfo.replaceFirst("<br>", repl + "<br>");
//			}
//			else if(fAdditionalProposalInfo.indexOf("<p>") > -1)
//			{
//				fAdditionalProposalInfo = fAdditionalProposalInfo.replaceFirst("<p>", repl + "<p>");
//			}else
//			{
//				fAdditionalProposalInfo += repl;
//			}
//		}
	}

	/**
	 * padToColumn
	 *
	 * @param stringToPad
	 * @param columnWidth
	 * @return String
	 */
	public static String padToColumn(String stringToPad, int columnWidth)
	{
		String blanks = "                             "; //$NON-NLS-1$
		
		if(stringToPad.length() > columnWidth)
		{
			return stringToPad.substring(0, columnWidth);
		}
		else
		{
			int blankLength = columnWidth - stringToPad.length();
			return stringToPad + blanks.substring(0, blankLength);
		}
	}
	
	/**
	 * Returns the type of object this proposal contains (class, method, etc).This is used for sorting.
	 * @return Returns the type of object this proposal contains (class, method, etc).This is used for sorting.
	 */
	public int getObjectType() {
		return fObjectType;
	}
	/**
	 * @see ICompletionProposal#apply(IDocument)
	 */
	public void apply(IDocument document) {
		try {
			document.replace(fReplacementOffset, replacementLength, replacementString);
		} catch (BadLocationException x) {
			// ignore
		}
	}

	/**
	 * @see ICompletionProposal#getSelection(IDocument)
	 */
	public Point getSelection(IDocument document) {
		return new Point(fReplacementOffset + fCursorPosition, 0);
	}

	/**
	 * @see ICompletionProposal#getContextInformation()
	 */
	public IContextInformation getContextInformation() {
		return fContextInformation;
	}

	/**
	 * @see ICompletionProposal#getImage()
	 */
	public Image getImage() {
		return fImage;
	}

	/**
	 * @see ICompletionProposal#getDisplayString()
	 */
	public String getDisplayString() {
		if (displayString != null)
		{
			return displayString;
		}
		return replacementString;
	}

	/**
	 * @see com.aptana.ide.editors.unified.contentassist.IUnifiedCompletionProposal#getReplaceString()
	 */
	public String getReplaceString() {
		return replacementString;
	}
	
	/**
	 * @see ICompletionProposal#getAdditionalProposalInfo()
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
	 * @return Returns the file location where the given CA was found (or jsCore, Dom0, Dom1 etc).
	 */
	public String getFileLocation()
	{
		return fFileLocation;
	}
	
	/**
	 * @return Returns the replacementLength.
	 */
	public int getReplacementLength() {
		return replacementLength;
	}

	/**
	 * @param replacementLength The replacementLength to set.
	 */
	public void setReplacementLength(int replacementLength) {
		this.replacementLength = replacementLength;
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
    
//	/**
//	 * @see com.aptana.ide.editor.js.contentassist.UnifiedCompletionProposal#apply(org.eclipse.jface.text.IDocument, char, int)
//	 */
//	public void apply(IDocument document, char trigger, int offset)
//	{
//	}
//
//
//	/**
//	 * @see com.aptana.ide.editor.js.contentassist.UnifiedCompletionProposal#isValidFor(org.eclipse.jface.text.IDocument, int)
//	 */
//	public boolean isValidFor(IDocument document, int offset)
//	{
//		return false;
//	}
//
//
//	/**
//	 * @see com.aptana.ide.editor.js.contentassist.UnifiedCompletionProposal#getTriggerCharacters()
//	 */
//	public char[] getTriggerCharacters()
//	{
//		return null;
//	}
//
//
//	/**
//	 * @see com.aptana.ide.editor.js.contentassist.UnifiedCompletionProposal#getContextInformationPosition()
//	 */
//	public int getContextInformationPosition()
//	{
//		return 0;
//	}
//
//
//	/**
//	 * @see com.aptana.ide.editor.js.contentassist.UnifiedCompletionProposal#apply(org.eclipse.jface.text.ITextViewer, char, int, int)
//	 */
//	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset)
//	{
//	}
//
//
//	/**
//	 * @see com.aptana.ide.editor.js.contentassist.UnifiedCompletionProposal#selected(org.eclipse.jface.text.ITextViewer, boolean)
//	 */
//	public void selected(ITextViewer viewer, boolean smartToggle)
//	{
//	}
//
//
//	/**
//	 * @see com.aptana.ide.editor.js.contentassist.UnifiedCompletionProposal#unselected(org.eclipse.jface.text.ITextViewer)
//	 */
//	public void unselected(ITextViewer viewer)
//	{
//	}
//
//
//	/**
//	 * @see com.aptana.ide.editor.js.contentassist.UnifiedCompletionProposal#validate(org.eclipse.jface.text.IDocument, int, org.eclipse.jface.text.DocumentEvent)
//	 */
//	public boolean validate(IDocument document, int offset, DocumentEvent event)
//	{
//		return false;
//	}
//
//
//	/**
//	 * @see com.aptana.ide.editor.js.contentassist.UnifiedCompletionProposal#getInformationControlCreator()
//	 */
//	public IInformationControlCreator getInformationControlCreator()
//	{
//		return null;
//	}
//
//
//	/**
//	 * @see com.aptana.ide.editor.js.contentassist.UnifiedCompletionProposal#getPrefixCompletionText(org.eclipse.jface.text.IDocument, int)
//	 */
//	public CharSequence getPrefixCompletionText(IDocument document, int completionOffset)
//	{
//		return null;
//	}
//
//
//	/**
//	 * @see com.aptana.ide.editor.js.contentassist.UnifiedCompletionProposal#getPrefixCompletionStart(org.eclipse.jface.text.IDocument, int)
//	 */
//	public int getPrefixCompletionStart(IDocument document, int completionOffset)
//	{
//		return 0;
//	}
//
//
//	/**
//	 * @see com.aptana.ide.editor.js.contentassist.UnifiedCompletionProposal#isAutoInsertable()
//	 */
//	public boolean isAutoInsertable()
//	{
//		return false;
//	}

}
