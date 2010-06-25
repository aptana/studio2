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
package com.aptana.ide.editors.junit.unified.contentassist;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.html.contentassist.HTMLCompletionProposal;
import com.aptana.ide.editors.unified.contentassist.UnifiedCompletionProposal;
import com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor;

/**
 * UnifiedContentAssistProcessorTest
 * 
 * @author Ingo Muschenetz
 */
public class UnifiedContentAssistProcessorTest extends TestCase
{
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	/**
	 * Test method for
	 * 'com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor.getCompletionProposalIdleActivationTokens()'
	 */
	public void testGetCompletionProposalIdleActivationTokens()
	{

	}

	/**
	 * Test method for
	 * 'com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor.computeCompletionProposals(ITextViewer,
	 * int)'
	 */
	public void testComputeCompletionProposals()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor.getOffsetMapper()'
	 */
	public void testGetOffsetMapper()
	{

	}

	/**
	 * Test method for
	 * 'com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor.computeInnerCompletionProposals(String,
	 * int, int, Lexeme, LexemeList)'
	 */
	public void testComputeInnerCompletionProposals()
	{

	}

	/**
	 * Test method for
	 * 'com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor.isValidIdleActivationToken(Lexeme)'
	 */
	public void testIsValidIdleActivationToken()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor.setSelection(String,
	 * ICompletionProposal[])'
	 */
	public void testSetSelection()
	{
		ArrayList proposals = new ArrayList();
		UnifiedCompletionProposal b = createProposal("b"); //$NON-NLS-1$
		UnifiedCompletionProposal c = createProposal("c"); //$NON-NLS-1$
		UnifiedCompletionProposal e = createProposal("e"); //$NON-NLS-1$
		UnifiedCompletionProposal f = createProposal("Function"); //$NON-NLS-1$
        UnifiedCompletionProposal f2 = createProposal("function"); //$NON-NLS-1$

		proposals.add(b);
		proposals.add(c);
		proposals.add(e);
		proposals.add(f);
        proposals.add(f2);

		int selectedIndex = UnifiedContentAssistProcessor.setSelection(
				StringUtils.EMPTY, (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[0])); 
		assertEquals(-1, selectedIndex);

        clearDefaults(proposals);
        selectedIndex = UnifiedContentAssistProcessor.setSelection(
                "c", (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[0])); //$NON-NLS-1$
        assertTrue(c.isDefaultSelection());
        assertFalse(e.isDefaultSelection());
        assertFalse(e.isSuggestedSelection());

        clearDefaults(proposals);
        selectedIndex = UnifiedContentAssistProcessor.setSelection(
                "cc", (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[0])); //$NON-NLS-1$
        assertFalse(c.isDefaultSelection());
        assertFalse(e.isDefaultSelection());
        assertTrue(e.isSuggestedSelection());

        // should select f, not b
        clearDefaults(proposals);
        selectedIndex = UnifiedContentAssistProcessor.setSelection(
                "g", (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[0])); //$NON-NLS-1$
        assertFalse(b.isSuggestedSelection());
        assertTrue(f2.isSuggestedSelection());

        // should select upper-case function, not lower
        clearDefaults(proposals);
        selectedIndex = UnifiedContentAssistProcessor.setSelection(
                "f", (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[0])); //$NON-NLS-1$
        assertTrue(f2.isDefaultSelection());
        assertFalse(f.isDefaultSelection());

        // should select upper-case function, not lower
        clearDefaults(proposals);
        selectedIndex = UnifiedContentAssistProcessor.setSelection(
                "Fu", (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[0])); //$NON-NLS-1$
        assertTrue(f.isDefaultSelection());
        assertFalse(f2.isDefaultSelection());

        // should select lower-case function, not upper
        clearDefaults(proposals);
        selectedIndex = UnifiedContentAssistProcessor.setSelection(
                "fu", (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[0])); //$NON-NLS-1$
        assertFalse(f.isDefaultSelection());
        assertTrue(f2.isDefaultSelection());
       
        // Note: we are now no longer auto-selecting an item if nothing matches at least the prefix.
	}

    private void clearDefaults(ArrayList proposals) {
        for (int i = 0; i < proposals.size(); i++) {
            UnifiedCompletionProposal array_element = (UnifiedCompletionProposal)proposals.get(i);
            array_element.setDefaultSelection(false);
            array_element.setSuggestedSelection(false);
        }
    }

	/**
	 * createProposal
	 * 
	 * @param displayString
	 * @return HTMLCompletionProposal
	 */
	public static HTMLCompletionProposal createProposal(String displayString)
	{
		return new HTMLCompletionProposal(displayString, 0, 0, 0, null, displayString, null, null, 0, null, null);
	}

	/**
	 * Test method for
	 * 'com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor.setSelectionUnsorted(String,
	 * ICompletionProposal[])'
	 */
	public void testSetSelectionUnsorted()
	{

		ArrayList proposals = new ArrayList();
		UnifiedCompletionProposal b = createProposal("b"); //$NON-NLS-1$
		UnifiedCompletionProposal c = createProposal("c"); //$NON-NLS-1$
		UnifiedCompletionProposal e = createProposal("e"); //$NON-NLS-1$
		UnifiedCompletionProposal f = createProposal("f"); //$NON-NLS-1$

		proposals.add(b);
		proposals.add(c);
		proposals.add(e);
		proposals.add(f);

		int selectedIndex = UnifiedContentAssistProcessor.setSelectionUnsorted(
				StringUtils.EMPTY, (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[0])); 
		assertEquals(-1, selectedIndex);

		selectedIndex = UnifiedContentAssistProcessor.setSelectionUnsorted(
				"e", (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[0])); //$NON-NLS-1$
		assertTrue(e.isDefaultSelection());

		selectedIndex = UnifiedContentAssistProcessor.setSelectionUnsorted(
				"eee", (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[0])); //$NON-NLS-1$
		assertTrue(b.isDefaultSelection());

		selectedIndex = UnifiedContentAssistProcessor.setSelectionUnsorted(
				"a", (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[0])); //$NON-NLS-1$
		assertTrue(b.isDefaultSelection());

		// Should select first item, as we assume list is unsorted
		selectedIndex = UnifiedContentAssistProcessor.setSelectionUnsorted(
				"g", (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[0])); //$NON-NLS-1$
		assertTrue(b.isDefaultSelection());

	}

	/**
	 * Test method for
	 * 'com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor.getPreviousLexemeOfType(int, int[],
	 * LexemeList, boolean)'
	 */
	public void testGetPreviousLexemeOfTypeIntIntArrayLexemeListBoolean()
	{

	}

	/**
	 * Test method for
	 * 'com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor.getPreviousLexemeOfType(int, int[],
	 * int[], LexemeList, boolean)'
	 */
	public void testGetPreviousLexemeOfTypeIntIntArrayIntArrayLexemeListBoolean()
	{

	}

	/**
	 * Test method for
	 * 'com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor.getPreviousLexeme(int, LexemeList)'
	 */
	public void testGetPreviousLexeme()
	{

	}

	/**
	 * Test method for
	 * 'com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor.getActivationChar(String, int,
	 * char[], char[])'
	 */
	public void testGetActivationCharStringIntCharArrayCharArray()
	{

	}

	/**
	 * Test method for
	 * 'com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor.getActivationChar(String, int,
	 * char[])'
	 */
	public void testGetActivationCharStringIntCharArray()
	{

	}

	/**
	 * Test method for
	 * 'com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor.getPreviousChar(String, int)'
	 */
	public void testGetPreviousChar()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor.getPreferenceStore()'
	 */
	public void testGetPreferenceStore()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor.getPreferenceStore()'
	 */
	public void testContainsExactProposalMatch()
	{

		ArrayList proposals = new ArrayList();
		UnifiedCompletionProposal b = createProposal("b"); //$NON-NLS-1$
		UnifiedCompletionProposal c = createProposal("c"); //$NON-NLS-1$
		UnifiedCompletionProposal e = createProposal("e"); //$NON-NLS-1$
		UnifiedCompletionProposal f = createProposal("f"); //$NON-NLS-1$

		proposals.add(b);
		proposals.add(c);
		proposals.add(e);
		proposals.add(f);

		assertFalse(UnifiedContentAssistProcessor.containsExactProposalMatch("e", null)); //$NON-NLS-1$
		assertTrue(UnifiedContentAssistProcessor.containsExactProposalMatch(
				"e", (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[0]))); //$NON-NLS-1$
		assertFalse(UnifiedContentAssistProcessor.containsExactProposalMatch(
				"g", (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[0]))); //$NON-NLS-1$
		assertTrue(UnifiedContentAssistProcessor.containsExactProposalMatch(
				"\"e\"", (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[0]))); //$NON-NLS-1$

	}
}
