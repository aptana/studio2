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
package com.aptana.ide.editors.css.tests.formatting;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.aptana.ide.core.tests.util.FormattingUtils;
import com.aptana.ide.editor.css.formatting.CSSCodeFormatter;
import com.aptana.ide.editor.css.formatting.CSSCodeFormatterOptions;
import com.aptana.ide.ui.editors.preferences.formatter.DefaultCodeFormatterConstants;

/**
 * @author Pavel Petrochenko
 */
public class FormattingTests extends TestCase
{

	private void formattingTest(String fName, String expected) throws IOException
	{
		formattingTest(fName, expected, null);
	}
	
	private void formattingTest(String fName, String expected, Map options) throws IOException
	{
		String readString = getContent(fName);
		CSSCodeFormatter formatter = new CSSCodeFormatter();
		formatter.setDisplayErrors(false);
		String format = formatter.format(readString, false, options, null, "\n"); //$NON-NLS-1$
		assertTrue(FormattingUtils.compareByTokens(readString, format));
		assertEquals(FormattingUtils.changeDelimeters(expected, "\n"), FormattingUtils.changeDelimeters(format, "\n"));
	}

	private String getContent(String fileName) throws IOException
	{
		InputStream stream = FormattingTests.class.getResourceAsStream(fileName);
		return FormattingUtils.readString(stream);
	}

	public void test1() throws Exception
	{
		formattingTest("test1.css", getContent("test1_f.css")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test2() throws Exception
	{
		formattingTest("test2.css", getContent("test2_f.css")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test3() throws Exception
	{
		formattingTest("test3.css", getContent("test3_f.css")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test4() throws Exception
	{
		formattingTest("test4.css", getContent("test4_f.css")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test5() throws Exception
	{
		formattingTest("test5.css", getContent("test5_f.css")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test6() throws Exception
	{
		formattingTest("test6.css", getContent("test6_f.css")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testRetainsExistingFormatOfSourceAfterEncounteringErrorLexeme() throws Exception
	{
		formattingTest("test7.css", getContent("test7_f.css")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Seems that we have different behavior depending on where the CSS property is in the document
	 */
	public void test9() throws Exception
	{
		formattingTest("test9.css", getContent("test9_f.css")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testNakedCSSClassRuleAfterComment() throws Exception
	{
		formattingTest("test10.css", getContent("test10_f.css")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void testRawNumberLiteralAsAttributeValue() throws Exception
	{
		formattingTest("test11.css", getContent("test11_f.css")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void testNewlinesSeparatingSelectors() throws Exception
	{
		Map options = new HashMap();
		options.put(CSSCodeFormatterOptions.NEWLINES_BETWEEN_SELECTORS, true);
		options.put(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK, DefaultCodeFormatterConstants.END_OF_LINE);
		formattingTest("test12.css", getContent("test12_f.css"), options); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void testRawSelectorWithoutIdentifierPreceding() throws Exception
	{
		formattingTest("test13.css", getContent("test13_f.css")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	// STU-4824
	public void testFormattingWithIEHacks() throws Exception
	{
		formattingTest("test14.css", getContent("test14_f.css")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * http://aptanastudio.tenderapp.com/discussions/problems/65-incorrect-formatting-of-css-media?mail_type=queue
	 * @throws Exception
	 */
	public void testFormattingOfMediaDirectiveContents() throws Exception
	{
		formattingTest("test15.css", getContent("test15_f.css")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testFormattingFontFace() throws Exception
	{
		formattingTest("test16.css", getContent("test16_f.css")); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
