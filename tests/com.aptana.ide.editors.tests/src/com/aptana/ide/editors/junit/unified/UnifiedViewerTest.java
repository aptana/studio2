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
package com.aptana.ide.editors.junit.unified;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jface.text.Document;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editors.unified.UnifiedViewer;

import junit.framework.TestCase;

/**
 * @author Ingo Muschenetz
 */
public class UnifiedViewerTest extends TestCase
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
	 * Test method for 'com.aptana.ide.editors.unified.UnifiedViewer.shift(boolean, boolean)'
	 */
	public void testShiftBooleanBoolean()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.editors.unified.UnifiedViewer.shift(boolean, boolean, boolean)'
	 */
	public void testShiftBooleanBooleanBoolean()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.editors.unified.UnifiedViewer.UnifiedViewer(Composite, IVerticalRuler,
	 * IOverviewRuler, boolean, int)'
	 */
	public void testUnifiedViewer()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.editors.unified.UnifiedViewer.shiftRight(int, int, String)'
	 */
	public void testShiftRightLeft()
	{
		String origDocument = "  \t\ttext\n   \t\ttext2"; //$NON-NLS-1$
		String shiftRightDocument = "\t  \t\ttext\n   \t\ttext2"; //$NON-NLS-1$
		String shiftLeftDocument = "\t\ttext\n   \t\ttext2"; //$NON-NLS-1$
		String shiftRightSpaces = "    \t\ttext\n   \t\ttext2"; //$NON-NLS-1$
		Document d = new Document(origDocument);

		UnifiedViewer.shiftRight(0, 0, StringUtils.TAB, d);
		assertEquals(shiftRightDocument, d.get());
		UnifiedViewer.shiftLeft(0, 0, getTabsIndexPrefix(), false, d);
		assertEquals(origDocument, d.get());
		UnifiedViewer.shiftLeft(0, 0, getTabsIndexPrefix(), false, d);
		assertEquals(shiftLeftDocument, d.get());
		UnifiedViewer.shiftRight(0, 0, "    ", d); //$NON-NLS-1$
		assertEquals(shiftRightSpaces, d.get());
		UnifiedViewer.shiftLeft(0, 0, getSpacesIndexPrefix(), false, d);
		assertEquals(shiftLeftDocument, d.get());
	}

	/**
	 * Test method for 'com.aptana.ide.editors.unified.UnifiedViewer.isHotkeyActivated()'
	 */
	public void testIsHotkeyActivated()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.editors.unified.UnifiedViewer.setHotkeyActivated(boolean)'
	 */
	public void testSetHotkeyActivated()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.editors.unified.UnifiedViewer.setNextIdleActivated(boolean)'
	 */
	public void testSetNextIdleActivated()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.editors.unified.UnifiedViewer.isNextIdleActivated()'
	 */
	public void testIsNextIdleActivated()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.editors.unified.UnifiedViewer.closeContentAssist()'
	 */
	public void testCloseContentAssist()
	{

	}

	/**
	 * Returns the array of prefixes for use in indenting (where it is set to indent with spaces)
	 * 
	 * @return String[]
	 */
	protected String[] getSpacesIndexPrefix()
	{
		String spaces = getTabAsSpaces();
		ArrayList prefixes = new ArrayList();

		// Spaces before tabs. In UnifiedViewer, it will use the [0] item for the indent
		// but the rest of the array for removing prefixes
		prefixes.addAll(Arrays.asList(StringUtils.getArrayOfSpaces(spaces.length())));
		prefixes.add(StringUtils.TAB);

		return (String[]) prefixes.toArray(new String[0]);

	}

	/**
	 * Returns the array of prefixes for use in indenting (where it is set to indent with tabs)
	 * 
	 * @return String[]
	 */
	protected String[] getTabsIndexPrefix()
	{
		String spaces = getTabAsSpaces();
		ArrayList prefixes = new ArrayList();

		// tab before spaces. In UnifiedViewer, it will use the [0] item for the indent
		// but the rest of the array for removing prefixes
		prefixes.add(StringUtils.TAB);
		prefixes.addAll(Arrays.asList(StringUtils.getArrayOfSpaces(spaces.length())));

		return (String[]) prefixes.toArray(new String[0]);
	}

	private String getTabAsSpaces()
	{
		return "    "; //$NON-NLS-1$
	}
}
