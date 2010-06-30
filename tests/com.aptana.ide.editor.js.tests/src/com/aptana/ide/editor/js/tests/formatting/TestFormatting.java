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
package com.aptana.ide.editor.js.tests.formatting;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.aptana.ide.core.tests.util.FormattingUtils;
import com.aptana.ide.editor.js.formatting.JSCodeFormatter;
import com.aptana.ide.editor.js.formatting.JSCodeFormatterOptions;

/**
 * @author Pavel Petrochenko
 */
public class TestFormatting extends TestCase
{

	private String separator = "\r\n"; //$NON-NLS-1$

	/**
	 * Creates the formatter
	 * 
	 * @return - js code formatter
	 */
	public static final JSCodeFormatter createFormatter()
	{
		JSCodeFormatter formatter = new JSCodeFormatter();
		formatter.setDisplayErrors(false);
		return formatter;
	}

	public void testCommentA() throws Exception
	{
		JSCodeFormatter formatter = createFormatter();
		String format = formatter.format("{ }//comment2", new HashMap(), null, separator); //$NON-NLS-1$
		assertTrue(format.indexOf("//comment2") != -1); //$NON-NLS-1$

		// assertTrue(format.indexOf("erer")!=-1); //$NON-NLS-1$
	}

	public void testCommentB() throws Exception
	{
		JSCodeFormatter formatter = createFormatter();
		String format = formatter.format("{ if (x){alert(/*erer*/10);}}//comment2", new HashMap(), null, separator); //$NON-NLS-1$
		assertTrue(format.indexOf("//comment2") != -1); //$NON-NLS-1$
		assertTrue(format.indexOf("erer") != -1); //$NON-NLS-1$
	}

	public void testCommentC() throws Exception
	{
		JSCodeFormatter formatter = createFormatter();
		String format = formatter.format("x=/*erer\n\ner*/10;//comment2",//$NON-NLS-1$
				new HashMap(), null, separator);
		assertTrue(format.indexOf("//comment2") != -1);//$NON-NLS-1$
		assertTrue(format.indexOf("erer") != -1);//$NON-NLS-1$
	}

	public void testCommentD() throws Exception
	{
		JSCodeFormatter formatter = createFormatter();
		String format = formatter.format("var j=1;/** */ /*dede*/ var h;",//$NON-NLS-1$
				new HashMap(), null, separator);
		int indexOf = format.indexOf('\n');
		char c = format.charAt(indexOf + 1);
		assertEquals(c, 'v');
	}

	public void testCommentE() throws Exception
	{
		JSCodeFormatter formatter = createFormatter();
		String format = formatter.format("var j=1;/** */ var h;",//$NON-NLS-1$
				new HashMap(), null, separator);
		int indexOf = format.indexOf('\n');
		char c = format.charAt(indexOf + 1);
		assertEquals(c, 'v');
	}

	public void testAptanaTest() throws Exception
	{
		formattingTest("aptanaTest.js", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testAptanaTest1() throws Exception
	{
		formattingTestIdentity("aptanaTest1.js"); //$NON-NLS-1$
	}

	public void testDebug() throws Exception
	{
		formattingTest("testDebug.js", getContent("testDebug_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testDebug2() throws Exception
	{
		formattingTest("testDebug2.js", getContent("testDebug2_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testDebug3() throws Exception
	{
		formattingTest("testDebug3.js", getContent("testDebug3_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testDebug4() throws Exception
	{
		formattingTest("testDebug4.js", getContent("testDebug4_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void formattingTest(String fName, String expected) throws IOException
	{
		formattingTest(fName, expected, null);
	}

	private void formattingTest(String fName, String expected, Map options) throws IOException
	{
		String readString = getContent(fName);
		JSCodeFormatter formatter = createFormatter();
		String format = formatter.format(readString, options, null, separator);
		assertTrue(FormattingUtils.compareByTokens(readString, format));
		assertEquals(FormattingUtils.changeDelimeters(expected, "\n"), FormattingUtils.changeDelimeters(format, "\n"));
	}

	private void formattingTestIdentity(String fName) throws IOException
	{
		String readString = getContent(fName);
		JSCodeFormatter formatter = createFormatter();
		String format = formatter.format(readString, null, null, separator);
		assertEquals(readString.trim(), format.trim());
	}

	private String getContent(String fileName) throws IOException
	{
		InputStream stream = TestFormatting.class.getResourceAsStream(fileName);
		if (stream == null)
			fail("Failed to grab input stream for " + fileName);
		// TODO What if the contents are empty!? We should probably fail the test!
		return FormattingUtils.readString(stream);
	}

	public void testComment0() throws Exception
	{
		formattingTest("test0.js", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment1() throws Exception
	{
		formattingTest("test1.js", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment2() throws Exception
	{
		formattingTest("test2.js", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment3() throws Exception
	{
		formattingTest("test3.js", getContent("test3_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment4() throws Exception
	{
		formattingTest("test4.js", getContent("test4_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment5() throws Exception
	{
		formattingTest("test5.js", getContent("test5_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment6() throws Exception
	{
		formattingTest("test6.js", getContent("test6_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment7() throws Exception
	{
		formattingTest("test7.js", getContent("test7_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment8() throws Exception
	{
		formattingTest("test8.js", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment9() throws Exception
	{
		formattingTest("test9.js", getContent("test9_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment10() throws Exception
	{
		formattingTest("test10.js", getContent("test10_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment11() throws Exception
	{
		formattingTest("test11.js", getContent("test11_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment12() throws Exception
	{
		formattingTest("test12.js", getContent("test12_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment13() throws Exception
	{
		formattingTest("test13.js", getContent("test13_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment14() throws Exception
	{
		formattingTest("test14.js", getContent("test14_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment15() throws Exception
	{
		formattingTest("test15.js", getContent("test15_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment16() throws Exception
	{
		formattingTest("test16.js", getContent("test16_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment17() throws Exception
	{
		formattingTest("test17.js", getContent("test17_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment18() throws Exception
	{
		formattingTest("test18.js", getContent("test18_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment19() throws Exception
	{
		formattingTest("test19.js", getContent("test19_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment20() throws Exception
	{
		formattingTest("test20.js", getContent("test20_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment21() throws Exception
	{
		formattingTest("test21.js", getContent("test21_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment22() throws Exception
	{
		formattingTest("test22.js", getContent("test22_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment23() throws Exception
	{
		formattingTest("test23.js", getContent("test23_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment24() throws Exception
	{
		formattingTest("test24.js", getContent("test24_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment25() throws Exception
	{
		formattingTest("test25.js", getContent("test25_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment26() throws Exception
	{
		formattingTest("test26.js", getContent("test26_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment27() throws Exception
	{
		formattingTest("test27.js", getContent("test27_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment28() throws Exception
	{
		formattingTest("test28.js", getContent("test28_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment29() throws Exception
	{
		formattingTest("test29.js", getContent("test29_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment30() throws Exception
	{
		formattingTest("test30.js", getContent("test30_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment31() throws Exception
	{
		formattingTest("test31.js", getContent("test31_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment32() throws Exception
	{
		formattingTest("test32.js", getContent("test32_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment33() throws Exception
	{
		formattingTest("test33.js", getContent("test33_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment34() throws Exception
	{
		formattingTest("test34.js", getContent("test34_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment35() throws Exception
	{
		formattingTest("test35.js", getContent("test35_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment36() throws Exception
	{
		formattingTest("test36.js", getContent("test36_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment37() throws Exception
	{
		formattingTest("test37.js", getContent("test37_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment38() throws Exception
	{
		formattingTest("test38.js", getContent("test38_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment39() throws Exception
	{
		formattingTest("test39.js", getContent("test39_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment40() throws Exception
	{
		formattingTest("test40.js", getContent("test40_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment41() throws Exception
	{
		formattingTest("test41.js", getContent("test41_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment42() throws Exception
	{
		formattingTest("test42.js", getContent("test42_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment43() throws Exception
	{
		formattingTest("test43.js", getContent("test43_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment44() throws Exception
	{
		formattingTest("test44.js", getContent("test44_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment45() throws Exception
	{
		formattingTest("test45.js", getContent("test45_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment46() throws Exception
	{
		formattingTest("test46.js", getContent("test46_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment47() throws Exception
	{
		formattingTest("test47.js", getContent("test47_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment48() throws Exception
	{
		formattingTest("test48.js", getContent("test48_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment49() throws Exception
	{
		formattingTest("test49.js", getContent("test49_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment50() throws Exception
	{
		formattingTest("test50.js", getContent("test50_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment51() throws Exception
	{
		formattingTest("test51.js", getContent("test51_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment52() throws Exception
	{
		formattingTest("test52.js", getContent("test52_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment53() throws Exception
	{
		formattingTest("test53.js", getContent("test53_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment54() throws Exception
	{
		formattingTest("test54.js", getContent("test54_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment55() throws Exception
	{
		formattingTest("test55.js", getContent("test55_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment56() throws Exception
	{
		formattingTest("test56.js", getContent("test56_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment57() throws Exception
	{
		formattingTest("test57.js", getContent("test57_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment58() throws Exception
	{
		formattingTest("test58.js", getContent("test58_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment59() throws Exception
	{
		formattingTest("test59.js", getContent("test59_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment60() throws Exception
	{
		formattingTest("test60.js", getContent("test60_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment61() throws Exception
	{
		formattingTest("test61.js", getContent("test61_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment62() throws Exception
	{
		formattingTest("test62.js", getContent("test62_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment63() throws Exception
	{
		formattingTest("test63.js", getContent("test63_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment64() throws Exception
	{
		formattingTest("test64.js", getContent("test64_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment65() throws Exception
	{
		formattingTest("test65.js", getContent("test65_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment66() throws Exception
	{
		formattingTest("test66.js", getContent("test66_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment67() throws Exception
	{
		formattingTest("test67.js", getContent("test67_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment68() throws Exception
	{
		formattingTest("test68.js", getContent("test68_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment69() throws Exception
	{
		formattingTest("test69.js", getContent("test69_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment70() throws Exception
	{
		formattingTest("test70.js", getContent("test70_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment71() throws Exception
	{
		formattingTest("test71.js", getContent("test71_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment72() throws Exception
	{
		formattingTest("test72.js", getContent("test72_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment73() throws Exception
	{
		formattingTest("test73.js", getContent("test73_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment74() throws Exception
	{
		formattingTest("test74.js", getContent("test74_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment75() throws Exception
	{
		formattingTest("test75.js", getContent("test75_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment76() throws Exception
	{
		formattingTest("test76.js", getContent("test76_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment77() throws Exception
	{
		formattingTest("test77.js", getContent("test77_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment78() throws Exception
	{
		formattingTest("test78.js", getContent("test78_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment79() throws Exception
	{
		formattingTest("test79.js", getContent("test79_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment80() throws Exception
	{
		formattingTest("test80.js", getContent("test80_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment81() throws Exception
	{
		formattingTest("test81.js", getContent("test81_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment82() throws Exception
	{
		formattingTest("test82.js", getContent("test82_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment83() throws Exception
	{
		formattingTest("test83.js", getContent("test83_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Regression to #6355
	 */
	public void testOption1() throws Exception
	{
		formattingTest("test84.js", getContent("test84_f.js"), JSCodeFormatterOptions.getCompactJSOptionsMap()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Regression to STU-16
	 */
	public void testOption2() throws Exception
	{
		formattingTest("test85.js", getContent("test85_f.js"), JSCodeFormatterOptions.getKeepThenMap()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Regression to STU-16
	 */
	public void testOption3() throws Exception
	{
		formattingTest("test86.js", getContent("test86_f.js"), JSCodeFormatterOptions.getKeepSimpleIfMap()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Regression to STU-16
	 */
	public void testOption4() throws Exception
	{
		formattingTest("test87.js", getContent("test87_f.js"), JSCodeFormatterOptions.getKeepSimpleIfMap1()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Regression to STU-16
	 */
	public void testOption5() throws Exception
	{
		formattingTest("test88.js", getContent("test88_f.js"), JSCodeFormatterOptions.getKeepElseIfMap()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Regression to STU-16
	 */
	public void testOption6() throws Exception
	{
		formattingTest("test89.js", getContent("test89_f.js"), JSCodeFormatterOptions.getKeepElseIfMap()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Regression to STU-16
	 */
	public void testOption7() throws Exception
	{
		formattingTest("test90.js", getContent("test90_f.js"), JSCodeFormatterOptions.getKeepGuardianMap()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Regression to STU-16
	 */
	public void testOption8() throws Exception
	{
		formattingTest("test91.js", getContent("test91_f.js"), JSCodeFormatterOptions.getKeepGuardianMap()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment92() throws Exception
	{
		formattingTest("test92.js", getContent("test92_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment93() throws Exception
	{
		formattingTest("test93.js", getContent("test93_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testComment94() throws Exception
	{
		formattingTest("test94.js", getContent("test94_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testNew95() throws Exception
	{
		formattingTest("test95.js", getContent("test95_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void test96() throws Exception
	{
		formattingTest("test96.js", getContent("test96_f.js")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void testEmptyBracesAfterOr() throws Exception
	{
		String readString = "var x = y || {};";
		String expected = "var x = y || {};\n";
		JSCodeFormatter formatter = createFormatter();
		String format = formatter.format(readString, null, null, separator);
		assertTrue(FormattingUtils.compareByTokens(readString, format));
		assertEquals(FormattingUtils.changeDelimeters(expected, "\n"), FormattingUtils.changeDelimeters(format, "\n"));
	}
}
