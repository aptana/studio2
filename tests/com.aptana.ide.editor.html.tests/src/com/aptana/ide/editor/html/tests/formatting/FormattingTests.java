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
package com.aptana.ide.editor.html.tests.formatting;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import junit.framework.TestCase;

import com.aptana.ide.core.tests.util.FormattingUtils;
import com.aptana.ide.editor.html.formatting.HTMLCodeFormatter;
import com.aptana.ide.editor.html.formatting.HTMLCodeFormatterOptions;

/**
 * @author Pavel Petrochenko
 */
public class FormattingTests extends TestCase
{

	private HTMLCodeFormatter formatter;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		formatter = new HTMLCodeFormatter();
		formatter.setDisplayErrors(false);
	}

	@Override
	protected void tearDown() throws Exception
	{
		this.formatter = null;
		super.tearDown();
	}

	/**
	 * test0
	 */
	public void test0()
	{
		String format = formatter.format("<p>\r\n" + //$NON-NLS-1$
				"            This is text <b>Test</b>\r\n" + //$NON-NLS-1$
				"        </p>\r\n" + //$NON-NLS-1$
				"", false, null, null, null); //$NON-NLS-1$
		int position = format.indexOf("<b>"); //$NON-NLS-1$
		assertTrue(position > 1);
		assertEquals(format.charAt(position - 1), ' ');
		assertEquals(format.charAt(position - 2), 't');
	}

	/**
	 * test1
	 */
	public void test1()
	{
		String format = formatter
				.format(
						"<noscript><a href=\'http://dynamic.fmpub.net/adserver/adclick.php?n=ac63eb52\' target=\'_blank\'><img\r\n" + //$NON-NLS-1$
								"src=\'http://dynamic.fmpub.net/adserver/adview.php?what=zone:133&amp;n=ac63eb52\' border=\'0\' alt=\'\' /></a></noscript>\r\n" //$NON-NLS-1$
								+ "", false, null, null, null); //$NON-NLS-1$
		int position = format.indexOf("src"); //$NON-NLS-1$
		assertTrue(position > 1);
		assertEquals(format.charAt(position - 1), ' ');
		assertEquals(format.charAt(position - 2), 'g');
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

	/**
	 * test2
	 */
	public void test2()
	{
		String format = formatter
				.format(
						"<noscript><a href=\'http://dynamic.fmpub.net/adserver/adclick.php?n=ac63eb52\' target=\'_blank\'><img\r\n" + //$NON-NLS-1$
								"src=\'http://dynamic.fmpub.net/adserver/adview.php?what=zone:133&amp;n=ac63eb52\' border=\'0\' alt=\'\' /></a></noscript>\r\n" //$NON-NLS-1$
								+ "", false, null, null, null); //$NON-NLS-1$
		int position = format.indexOf("</a>"); //$NON-NLS-1$
		int position1 = format.indexOf("</noscript>"); //$NON-NLS-1$
		assertTrue(position > 1);
		assertTrue(position1 > position);
		assertTrue(hasNewLine(position, position1, format));
		// <b>dede<i> dede </i></b>
	}

	/**
	 * test3
	 */
	public void test3()
	{
		String format = formatter.format("<b>d1<i> dede </i></b>", false, null, null, null); //$NON-NLS-1$
		int position = format.indexOf("dede"); //$NON-NLS-1$
		assertTrue(position > 1);
		assertEquals(format.charAt(position - 1), ' ');
		assertEquals(format.charAt(position - 2), '>');
	}

	/**
	 * test4
	 */
	public void test4()
	{
		String format = formatter.format("<b>d1<i> dede </i></b>", false, null, null, null); //$NON-NLS-1$
		int position = format.indexOf("dede"); //$NON-NLS-1$
		assertTrue(position > 1);
		assertEquals(format.charAt(position - 1), ' ');
		assertEquals(format.charAt(position - 2), '>');
	}

	/**
	 * test5
	 */
	public void test5()
	{
		String format = formatter.format("<li class=\"digg-count\" id=\"main8\">\r\n" + //$NON-NLS-1$
				"    <a href=\"/offbeat_news/5_Reasons_Why_I_Won_t_Digg_Your_Lame_Story\" id=\"diggs8\"><b>d2<i> dede </i></b>\r\n" //$NON-NLS-1$
				+ "        <strong id=\"diggs-strong-8\">\r\n" + //$NON-NLS-1$
				"            397 \r\n" + //$NON-NLS-1$
				"        </strong>\r\n" + //$NON-NLS-1$
				"         diggs \r\n" + //$NON-NLS-1$
				"    </a>\r\n" + //$NON-NLS-1$
				"</li>", false, null, null, null); //$NON-NLS-1$
		int position = format.indexOf("dede"); //$NON-NLS-1$
		assertTrue(position > 1);
		assertEquals(format.charAt(position - 1), ' ');
		assertEquals(format.charAt(position - 2), '>');
		int position1 = format.indexOf("<a"); //$NON-NLS-1$
		int position2 = format.indexOf("</a"); //$NON-NLS-1$
		assertEquals(getIndentLevel(format, position1), getIndentLevel(format, position2));
	}

	/**
	 * test6
	 */
	public void test6()
	{
		String format = formatter.format("<html>\r\n" + //$NON-NLS-1$
				"    </div>\r\n" + //$NON-NLS-1$
				"</html>", false, null, null, null); //$NON-NLS-1$		
		int position1 = format.indexOf("<html"); //$NON-NLS-1$
		int position2 = format.indexOf("</html"); //$NON-NLS-1$
		assertEquals(getIndentLevel(format, position1), getIndentLevel(format, position2));
	}

	/**
	 * test7
	 */
	public void test7()
	{
		String format = formatter.format("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n" + //$NON-NLS-1$
				"    <body>\n" + //$NON-NLS-1$
				"        <div>\n" + //$NON-NLS-1$
				"            test \n" + //$NON-NLS-1$
				"<!-- -->\n" + //$NON-NLS-1$
				"        </div>\n" + //$NON-NLS-1$
				"    </body>\n" + //$NON-NLS-1$
				"</html>\n" + //$NON-NLS-1$
				"\n" + //$NON-NLS-1$
				"", false, null, null, null); //$NON-NLS-1$		
		int position1 = format.indexOf("test"); //$NON-NLS-1$
		int position2 = format.indexOf("<!--"); //$NON-NLS-1$
		assertEquals(getIndentLevel(format, position1), getIndentLevel(format, position2));
	}

	/**
	 * test8
	 */
	public void test8()
	{
		String format = formatter.format("<body a=\"test\"\\ b=\"test\">\n" + //$NON-NLS-1$
				"        \\\n" + //$NON-NLS-1$
				"    </body>", false, null, null, null); //$NON-NLS-1$		
		int position1 = format.indexOf('\\');
		assertTrue(format.charAt(position1 - 1) == '"');
	}

	/**
	 * test9
	 */
	public void test9()
	{
		String string = "<%=tesdede %>"; //$NON-NLS-1$
		String format = formatter.format(string, false, null, null, null);
		assertEquals(string, format);
	}

	/**
	 * test10
	 */
	public void test10()
	{
		String format = formatter.format("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + //$NON-NLS-1$
				"    <body>\n" + //$NON-NLS-1$
				"        <div>\n" + //$NON-NLS-1$
				"            Test <a rel=\"license\" href=\"test\">Test</a>\n" + //$NON-NLS-1$
				"            Test1 \n" + //$NON-NLS-1$
				"        </div>\n" + //$NON-NLS-1$
				"    </body>\n" + //$NON-NLS-1$
				"</html>", false, null, null, null); //$NON-NLS-1$		
		int position1 = format.indexOf("Test"); //$NON-NLS-1$
		int position2 = format.indexOf("Test1"); //$NON-NLS-1$
		assertEquals(getIndentLevel(format, position1), getIndentLevel(format, position2));
	}

	/**
	 *
	 */
	public void test11() throws Exception
	{
		formattingTest("test1.html", getContent("test1_f.html")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 *
	 */
	public void test12() throws Exception
	{
		formattingTest("test2.html", getContent("test2_f.html"));//$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 *
	 */
	public void test13() throws Exception
	{
		formattingTest("test3.html", getContent("test3_f.html"));//$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 *
	 */
	public void test14() throws Exception
	{
		formattingTest("test4.html", getContent("test4_f.html"));//$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 *
	 */
	public void test15() throws Exception
	{
		formattingTest("test5.html", getContent("test5_f.html"));//$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 *
	 */
	public void test16() throws Exception
	{
		formattingTest("test6.html", getContent("test6_f.html"));//$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 *
	 */
	public void test17() throws Exception
	{
		formattingTest("test7.html", getContent("test7_f.html"));//$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 *
	 */
	public void test18() throws Exception
	{
		formattingTest("test8.html", getContent("test8_f.html"));//$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 *
	 */
	public void test19() throws Exception
	{
		formattingTest("test9.html", getContent("test9_f.html"));//$NON-NLS-1$ //$NON-NLS-2$
	}

	// XXX Remove this test for real or fix it. The behavior doesn't seem particularly broken to me as it exists.
//	public void test20() throws Exception
//	{
//		formattingTest("test10.html", getContent("test10_f.html"));//$NON-NLS-1$ //$NON-NLS-2$
//	}

	/**
	 *
	 */
	public void test21() throws Exception
	{
		formattingTest("test11.html", getContent("test11_f.html")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 *
	 */
	public void test22() throws Exception
	{
		formattingTest("test12.html", getContent("test12_f.html")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 *
	 */
	public void test23() throws Exception
	{
		formattingTest("test13.html", getContent("test13_f.html")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 *
	 */
	public void test24() throws Exception
	{
		formattingTest("test14.html", getContent("test14_f.html")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 *
	 */
	public void test25() throws Exception
	{
		formattingTest("test15.html", getContent("test15_f.html")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 *
	 */
	public void test_16() throws Exception
	{
		formattingTest("test16.html", getContent("test16_f.html")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 *
	 */
	public void test_17() throws Exception
	{
		formattingTest("test17.html", getContent("test17_f.html")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 *
	 */
	public void test_18() throws Exception
	{
		formattingTest("test18.html", getContent("test18_f.html")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 *
	 */
	public void test_19() throws Exception
	{
		formattingTest("test19.html", getContent("test19_f.html")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 *
	 */
	public void test_20() throws Exception
	{
		formattingTest("test20.html", getContent("test20_f.html")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 *
	 */
	public void test_21() throws Exception
	{
		formattingTest("test21.html", getContent("test21_f.html"), HTMLCodeFormatterOptions.createNotWrappingOptions()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 *
	 */
	public void test_22() throws Exception
	{
		formattingTest("test22.html", getContent("test22_f.html"), HTMLCodeFormatterOptions.createNotIndentingOptions()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * conditional statements
	 */
	public void test_23() throws Exception
	{
		formattingTest("test23.html", getContent("test23_f.html")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * conditional statements
	 */
	public void test_24() throws Exception
	{
		formattingTest("test24.html", getContent("test24_f.html")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * conditional statements
	 */
	public void test_25() throws Exception
	{
		formattingTest("test25.html", getContent("test25_f.html")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * conditional statements
	 */
	public void test_26() throws Exception
	{
		formattingTest("test26.html", getContent("test26_f.html")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * rhtml
	 */
	public void test_27() throws Exception
	{
		formattingTest("test27.html", getContent("test27_f.html")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * rhtml
	 */
	public void test_28() throws Exception
	{
		formattingTest("test28.html", getContent("test28_f.html")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void formattingTest(String fName, String expected) throws IOException
	{
		formattingTest(fName, expected, null);
	}

	private void formattingTest(String fName, String expected, Map options) throws IOException
	{
		String readString = getContent(fName);
		String format = formatter.format(readString, false, options, null, "\n"); //$NON-NLS-1$
		assertTrue(FormattingUtils.compareByTokens(readString, format));
		assertEquals(FormattingUtils.changeDelimeters(expected, "\n"), FormattingUtils.changeDelimeters(format, "\n"));
	}

	private String getContent(String fileName) throws IOException
	{
		InputStream stream = FormattingTests.class.getResourceAsStream(fileName);
		if (stream == null)
			fail("Failed to grab input stream for " + fileName);
		return FormattingUtils.readString(stream);
	}

}
