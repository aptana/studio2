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
package com.aptana.ide.editor.xml.tests.formatting;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.aptana.ide.core.StreamUtils;
import com.aptana.ide.core.tests.util.FormattingUtils;
import com.aptana.ide.editor.xml.formatting.XMLCodeFormatter;
import com.aptana.ide.editor.xml.formatting.XMLCodeFormatterOptions;
import com.aptana.ide.ui.editors.preferences.formatter.DefaultCodeFormatterConstants;

/**
 * @author Pavel Petrochenko
 */
public class FormattingTests extends TestCase
{

	public void testNoScript()
	{
		XMLCodeFormatter formatter = createFormatter();
		String format = formatter
				.format(
						"<noscript><a href=\'http://dynamic.fmpub.net/adserver/adclick.php?n=ac63eb52\' target=\'_blank\'><img\r\n" + //$NON-NLS-1$
								"src=\'http://dynamic.fmpub.net/adserver/adview.php?what=zone:133&amp;n=ac63eb52\' border=\'0\' alt=\'\' /></a></noscript>\r\n" //$NON-NLS-1$
								+ "", false, defaultOptions(), null, null); //$NON-NLS-1$
		int position = format.indexOf("</a>"); //$NON-NLS-1$
		int position1 = format.indexOf("</noscript>"); //$NON-NLS-1$
		assertTrue(position > 1);
		assertTrue(position1 > position);
		assertTrue(hasNewLine(position, position1, format));
		// <b>dede<i> dede </i></b>
	}

	public void testXMLDiv()
	{
		XMLCodeFormatter formatter = createFormatter();
		String format = formatter.format("<XML>\r\n" + //$NON-NLS-1$
				"    </div>\r\n" + //$NON-NLS-1$
				"</XML>", false, null, null, null); //$NON-NLS-1$		
		int position1 = format.indexOf("<XML"); //$NON-NLS-1$
		int position2 = format.indexOf("</XML"); //$NON-NLS-1$
		assertEquals(getIndentLevel(format, position1), getIndentLevel(format, position2));
	}

	public void testDivContent()
	{
		XMLCodeFormatter formatter = createFormatter();
		String format = formatter.format("<XML xmlns=\"http://www.w3.org/1999/xXML\" xml:lang=\"en\" lang=\"en\">\n" + //$NON-NLS-1$
				"    <body>\n" + //$NON-NLS-1$
				"        <div>\n" + //$NON-NLS-1$
				"            test \n" + //$NON-NLS-1$
				"<!-- -->\n" + //$NON-NLS-1$
				"        </div>\n" + //$NON-NLS-1$
				"    </body>\n" + //$NON-NLS-1$
				"</XML>\n" + //$NON-NLS-1$
				"\n" + //$NON-NLS-1$
				"", false, null, null, null); //$NON-NLS-1$		
		int position1 = format.indexOf("test"); //$NON-NLS-1$
		int position2 = format.indexOf("<!--"); //$NON-NLS-1$
		assertEquals(getIndentLevel(format, position1), getIndentLevel(format, position2));
	}

	public void testSlashInTag()
	{
		XMLCodeFormatter formatter = createFormatter();
		String format = formatter.format("<body a=\"test\"\\ b=\"test\">\n" + //$NON-NLS-1$
				"        \\\n" + //$NON-NLS-1$
				"    </body>", false, null, null, null); //$NON-NLS-1$		
		int position1 = format.indexOf('\\');
		assertTrue(format.charAt(position1 - 1) == '"');
	}

	public void testTextContentSeparatedByTag()
	{
		XMLCodeFormatter formatter = createFormatter();
		String format = formatter.format("<XML xmlns=\"http://www.w3.org/1999/xXML\">\n" + //$NON-NLS-1$
				"    <body>\n" + //$NON-NLS-1$
				"        <div>\n" + //$NON-NLS-1$
				"            Test <a rel=\"license\" href=\"test\">Test</a>\n" + //$NON-NLS-1$
				"            Test1 \n" + //$NON-NLS-1$
				"        </div>\n" + //$NON-NLS-1$
				"    </body>\n" + //$NON-NLS-1$
				"</XML>", false, null, null, null); //$NON-NLS-1$		
		int position1 = format.indexOf("Test"); //$NON-NLS-1$
		int position2 = format.indexOf("Test1"); //$NON-NLS-1$
		assertEquals(getIndentLevel(format, position1), getIndentLevel(format, position2));
	}

	public void test1() throws Exception
	{
		formattingTest("test1.xml", getContent("test1_f.xml")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test2() throws Exception
	{
		formattingTest("test2.xml", getContent("test2_f.xml")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 *commented because of STU-2059
	 */
	// public void test8() throws Exception
	// {
	//		formattingTest("test3.xml", getContent("test3_f.xml")); //$NON-NLS-1$ //$NON-NLS-2$
	// }
	public void test4() throws Exception
	{
		formattingTest("test4.xml", getContent("test4_f.xml")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test5() throws Exception
	{
		formattingTest("test5.xml", getContent("test5_f.xml")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test6() throws Exception
	{
		formattingTest("test6.xml", getContent("test6_f.xml")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test7() throws Exception
	{
		formattingTest("test7.xml", getContent("test7_f.xml")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test8() throws Exception
	{
		formattingTest("test8.xml", getContent("test8_f.xml")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test9() throws Exception
	{
		formattingTest("test9.xml", getContent("test9_f.xml")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test10() throws Exception
	{
		formattingTest("test10.xml", getContent("test10_f.xml")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test11() throws Exception
	{
		formattingTest("test11.xml", getContent("test11_f.xml"), XMLCodeFormatterOptions.createNotWrappingOptions()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test12() throws Exception
	{
		formattingTest("test12.xml", getContent("test12_f.xml")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test13() throws Exception
	{
		formattingTest("test13.xml", getContent("test13_f.xml")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test14() throws Exception
	{
		formattingTest(
				"test14.xml", getContent("test14_f.xml"), XMLCodeFormatterOptions.createNotPreservingReturnsOptions()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test15() throws Exception
	{
		formattingTest("test15.xml", getContent("test15_f.xml")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test16() throws Exception
	{
		formattingTest(
				"test16.xml", getContent("test16_f.xml"), XMLCodeFormatterOptions.createNotPreservingReturnsOptions()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test17() throws Exception
	{
		formattingTest("test17.xml", getContent("test17_f.xml")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test17NotPreservingReturns() throws Exception
	{
		formattingTest(
				"test17.xml", getContent("test17_f.xml"), XMLCodeFormatterOptions.createNotPreservingReturnsOptions()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test18() throws Exception
	{
		formattingTest("test18.xml", getContent("test18_f.xml")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test18NotPreservingReturns() throws Exception
	{
		formattingTest(
				"test18.xml", getContent("test18_f.xml"), XMLCodeFormatterOptions.createNotPreservingReturnsOptions()); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void testAdvancedInlineDTD() throws Exception
	{
		formattingTest("test19.xml", getContent("test19_f.xml")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void formattingTest(String fName, String expected) throws IOException
	{
		formattingTest(fName, expected, defaultOptions());
	}

	private void formattingTest(String fName, String expected, Map options) throws IOException
	{
		InputStream stream = FormattingTests.class.getResourceAsStream(fName);
		String readString = FormattingUtils.readString(stream);
		XMLCodeFormatter formatter = createFormatter();
		String format = formatter.format(readString, false, options, null, "\n"); //$NON-NLS-1$
		assertTrue(FormattingUtils.compareByTokens(readString, format));
		assertEquals(FormattingUtils.changeDelimeters(expected, "\n"), FormattingUtils.changeDelimeters(format, "\n"));
	}

	private Map<String, String> defaultOptions()
	{
		Map<String, String> options = new HashMap<String, String>();
		options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, " ");
		options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "4");
		options.put(DefaultCodeFormatterConstants.FORMATTER_SPACES_BEFORE_ATTRS_ON_MULTILINE, "1");
		options.put(DefaultCodeFormatterConstants.FORMATTER_PRESERVE_EXTRA_CARRIAGE_RETURNS, "true");
		options.put(DefaultCodeFormatterConstants.FORMATTER_PRESERVE_WHITESPACE_IN_CDATA, "true");
		options.put(DefaultCodeFormatterConstants.DO_NOT_WRAP_SIMPLE_TAGS, "false");
		return options;
	}

	private String getContent(String fileName) throws IOException
	{
		InputStream stream = FormattingTests.class.getResourceAsStream(fileName);
		return StreamUtils.readContent(stream, null);
	}

	private static final XMLCodeFormatter createFormatter()
	{
		XMLCodeFormatter formatter = new XMLCodeFormatter();
		formatter.setDisplayErrors(false);
		return formatter;
	}

	private boolean hasNewLine(int p1, int p2, String string)
	{
		for (int a = p1; a < p2; a++)
		{
			char c = string.charAt(a);
			if (c == '\r' || c == '\n')
			{
				return true;
			}
		}
		return false;
	}

	private int getIndentLevel(String string, int position)
	{
		int iL = 0;
		for (int a = position; a >= 0; a--)
		{
			char c = string.charAt(a);
			if (c == '\r' || c == '\n')
			{
				return iL;
			}
			if (!Character.isWhitespace(c))
			{
				return -1;
			}
			iL++;
		}
		return iL;
	}

}
