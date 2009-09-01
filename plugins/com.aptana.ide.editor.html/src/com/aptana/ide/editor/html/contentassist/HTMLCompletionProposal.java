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
package com.aptana.ide.editor.html.contentassist; 

import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

import com.aptana.ide.editors.unified.IUnifiedViewer;
import com.aptana.ide.editors.unified.contentassist.UnifiedCompletionProposal;

/**
 * 
 */
public class HTMLCompletionProposal extends UnifiedCompletionProposal//, ICompletionProposalExtension2
{
	/**
	 * HTMLCompletionProposal
	 *
	 * @param replacementString
	 * @param replacementOffset
	 * @param replacementLength
	 * @param cursorPosition
	 * @param image
	 * @param displayString
	 * @param contextInformation
	 * @param additionalProposalInfo
	 * @param objectType
	 * @param unifiedViewer
	 * @param userAgentImages
	 */
	public HTMLCompletionProposal(String replacementString, int replacementOffset, int replacementLength,
			int cursorPosition, Image image, String displayString, IContextInformation contextInformation,
			String additionalProposalInfo, int objectType, IUnifiedViewer unifiedViewer, Image[] userAgentImages) {
		super(replacementString, replacementOffset, replacementLength, cursorPosition,
				image, displayString, contextInformation, additionalProposalInfo,
				objectType, unifiedViewer, userAgentImages);
	}
	
//	/**
//	 * Returns the type of object this proposal contains (class, method, etc).This is used for sorting.
//	 * @return Returns the type of object this proposal contains (class, method, etc).This is used for sorting.
//	 */
//	public int getObjectType() {
//		return fObjectType;
//	}
//	/**
//	 * @see ICompletionProposal#apply(IDocument)
//	 */
//	public void apply(IDocument document) {
//		try {
//			document.replace(fReplacementOffset, fReplacementLength, fReplacementString);
//		} catch (BadLocationException x) {
//			// ignore
//		}
//	}
//
//	/**
//	 * apply
//	 * 
//	 * @param viewer
//	 * @param trigger
//	 * @param stateMask
//	 * @param offset
//	 */
//	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset)
//	{
//		apply(viewer.getDocument());
//		
//		try {
//			setUpLinkedMode(viewer, offset);
//		} catch(BadLocationException x) {
//			// ignore
//		}
//	}
//	
//	private void setUpLinkedMode(ITextViewer viewer, int offset) throws BadLocationException
//	{
//        LinkedPositionGroup group = new LinkedPositionGroup();
//        group.addPosition(new LinkedPosition(viewer.getDocument(), offset + 1, 0, LinkedPositionGroup.NO_STOP));
//
//        LinkedModeModel model = new LinkedModeModel();
//        model.addGroup(group);
//        model.forceInstall();
//
//        LinkedModeUI ui = new LinkedModeUI(model, viewer);
//        ui.setSimpleMode(true);
//		ui.setExitPolicy(new ExitPolicy('>'));
//        ui.setExitPosition(viewer, offset + 1, 0, Integer.MAX_VALUE);
//        ui.setCyclingMode(LinkedModeUI.CYCLE_NEVER);
//        ui.enter();
//	}
//	/**
//	 * @see ICompletionProposal#getSelection(IDocument)
//	 */
//	public Point getSelection(IDocument document) {
//		return new Point(fReplacementOffset + fCursorPosition, 0);
//	}
//
//	/**
//	 * @see ICompletionProposal#getContextInformation()
//	 */
//	public IContextInformation getContextInformation() {
//		return fContextInformation;
//	}
//
//	/**
//	 * @see ICompletionProposal#getImage()
//	 */
//	public Image getImage() {
//		return fImage;
//	}
//
//	/**
//	 * @see ICompletionProposal#getDisplayString()
//	 */
//	public String getDisplayString() {
//		if (fDisplayString != null)
//			return fDisplayString;
//		return fReplacementString;
//	}
//
//	/**
//	 * @see ICompletionProposal#getAdditionalProposalInfo()
//	 */
//	public String getAdditionalProposalInfo() {
//		return fAdditionalProposalInfo;
//	}
//
//	/**
//	 * @author Robin Debreuil
//	 */
//	protected static class ExitPolicy implements IExitPolicy {
//
//		final char fExitCharacter;
//
//		/**
//		 * ExitPolicy
//		 * 
//		 * @param exitCharacter
//		 */
//		public ExitPolicy(char exitCharacter) {
//			fExitCharacter= exitCharacter;
//		}
//
//		/**
//		 * @see org.eclipse.jface.text.link.LinkedModeUI.IExitPolicy#doExit(org.eclipse.jface.text.link.LinkedModeModel, org.eclipse.swt.events.VerifyEvent, int, int)
//		 */
//		public ExitFlags doExit(LinkedModeModel environment, VerifyEvent event, int offset, int length) {
//
//			if (event.character == fExitCharacter) {
//				if (environment.anyPositionContains(offset))
//					return new ExitFlags(ILinkedModeListener.UPDATE_CARET, false);
//				else
//					return new ExitFlags(ILinkedModeListener.UPDATE_CARET, true);
//			}
//
//			switch (event.character) {
//			case ';':
//				return new ExitFlags(ILinkedModeListener.NONE, true);
//			
//			case '\r':
//				return new ExitFlags(ILinkedModeListener.NONE, true);
//
//			default:
//				return null;
//			}
//		}
//
//	}
//
//
//	/**
//	 * selected
//	 * 
//	 * @param viewer
//	 * @param smartToggle
//	 */
//	public void selected(ITextViewer viewer, boolean smartToggle)
//	{
//		// TODO Auto-generated method stub
//		
//	}
//
//	/**
//	 * unselected
//	 * 
//	 * @param viewer
//	 */
//	public void unselected(ITextViewer viewer)
//	{
//		// TODO Auto-generated method stub
//		
//	}
//
//	/**
//	 * validate
//	 * 
//	 * @param document
//	 * @param offset
//	 * @param event
//	 * @return boolean
//	 */
//	public boolean validate(IDocument document, int offset, DocumentEvent event)
//	{
//		if (offset < this.fReplacementOffset)
//			return false;
//
//		/*
//		 * See http://dev.eclipse.org/bugs/show_bug.cgi?id=17667
//		String word= fReplacementString;
//		 */
//		boolean validated= startsWith(document, offset, getDisplayString()); // TODO remove early display string reference
//
//		if (validated && event != null) {
//			// adapt replacement range to document change
//			int delta= (event.fText == null ? 0 : event.fText.length()) - event.fLength;
//			final int newLength= Math.max(this.fReplacementLength + delta, 0);
//			fReplacementLength = newLength;
//		}
//
//		return validated;
//	}
//	
//	/**
//	 * Returns <code>true</code> if a words starts with the code completion prefix in the document,
//	 * <code>false</code> otherwise. 
//	 * @param document 
//	 * @param offset 
//	 * @param word 
//	 * @return boolean
//	 */
//	protected final boolean startsWith(IDocument document, int offset, String word) {
//		int wordLength= word == null ? 0 : word.length();
//		if (offset >  fReplacementOffset + wordLength)
//			return false;
//
//		try {
//			int length= offset - fReplacementOffset;
//			String start= document.get(fReplacementOffset, length);
//			return word.substring(0, length).equalsIgnoreCase(start);
//		} catch (BadLocationException x) {
//		}
//
//		return false;
//	}
//	
//	/**
//	 * defaultSelection
//	 */
//	public boolean defaultSelection = false;
//	
//	/**
//	 * @return Returns the defaultSelection.
//	 */
//	public boolean isDefaultSelection()
//	{
//		return defaultSelection;
//	}
//
//	/**
//	 * @param defaultSelection The defaultSelection to set.
//	 */
//	public void setDefaultSelection(boolean defaultSelection)
//	{
//		this.defaultSelection = defaultSelection;
//	}	
}
