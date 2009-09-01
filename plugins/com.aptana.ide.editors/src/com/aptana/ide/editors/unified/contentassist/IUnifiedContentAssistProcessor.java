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

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.aptana.ide.parsing.IOffsetMapper;

/**
 * IUnifiedContentAssistProcessor
 * @author Ingo Muschenetz
 *
 */
public interface IUnifiedContentAssistProcessor
{
	/**
	 * Indicates this code assist processor has been invoked via a hotkey
	 * @param value
	 */
	//void setHotkeyActivated(boolean value);
	//void setNextIdleActivated(boolean value);
	
	/**
	 * Indicates this code assist processor has been invoked via an idle activation
	 * @return True or false
	 */
	//boolean isNextIdleActivated();
	
	/**
	 * Is this a valid location for auto-assisting (based on lexeme, usually)
	 * @param viewer 
	 * @param offset 
	 * @return Returns a valid location for auto-assisting (based on lexeme, usually)
	 */
	boolean isValidIdleActivationLocation(ITextViewer viewer, int offset); 
	
	/**
	 * The token types that triggers completion proposals on idle
	 * 
	 * @return Returns the trigger tokens for code completion.
	 */
	int[] getCompletionProposalIdleActivationTokens();
	
	/**
	 * @param viewer 
	 * @param offset 
	 * @param activationChar 
	 * @return ICompletionProposal[]
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer,
	 *      int)
	 */
	ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset, char activationChar);
	
	/**
	 * @param viewer
	 * @param offset
	 * @param activationChar
	 * @param autoActivated
	 * @return ICompletionProposal[]
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer,
	 *      int)
	 */
	ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset, char activationChar, boolean autoActivated);
	
	/**
	 * Returns the local offset mapper
	 * @return The current offset mapper
	 * @deprecated This function will be removed in the future.
	 */
	IOffsetMapper getOffsetMapper();
	
}
