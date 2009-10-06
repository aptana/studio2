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
package com.aptana.ide.editor.css.tests;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.css.parsing.CSSParser;
import com.aptana.ide.editor.css.parsing.CSSParser2;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.IParser;
import com.aptana.ide.parsing.ParserInitializationException;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 */
public class TestStatements extends TestCase
{
	private static String EOL = System.getProperty("line.separator"); //$NON-NLS-1$

	/**
	 * typingTests
	 * 
	 * @param source
	 * @throws ParserInitializationException 
	 */
	protected void parseTest(String source) throws ParserInitializationException
	{
		source += EOL;
		
		this.parseTest(new CSSParser(), source);
		this.parseTest(new CSSParser2(), source);
	}
	
	/**
	 * parseTest
	 * 
	 * @param parser
	 * @param source
	 */
	protected void parseTest(IParser parser, String source)
	{
		IParseState parseState = parser.createParseState(null);

		parseState.setEditState(source, source, 0, 0);

		try
		{
			parser.parse(parseState);
			
			IParseNode parseResults = parseState.getParseResults();
			
			if (parseResults != null && parseResults.hasChildren())
			{
				String result = parseResults.getSource();
	
				assertEquals(source, result);
			}
			else
			{
				Assert.fail("no parse results"); //$NON-NLS-1$
			}
		}
		catch (Exception e)
		{
			IdeLog.logInfo(TestsPlugin.getDefault(), "parseTest failed", e); //$NON-NLS-1$
		}
	}

	/**
	 * Test empty import with a string
	 *
	 * @throws Exception
	 */
	public void testImportStringNoIdentifier() throws Exception
	{
		this.parseTest("@import 'test';"); //$NON-NLS-1$
	}
	
	/**
	 * Test empty import with a url
	 *
	 * @throws Exception
	 */
	public void testImportUrlNoIdentifier() throws Exception
	{
		this.parseTest("@import url('test');"); //$NON-NLS-1$
	}
	
	/**
	 * Test import with a string and a single identifier
	 *
	 * @throws Exception
	 */
	public void testImportStringSingleIdentifier() throws Exception
	{
		this.parseTest("@import 'test' abc123;"); //$NON-NLS-1$
	}
	
	/**
	 * Test import with a url and a single identifier
	 *
	 * @throws Exception
	 */
	public void testImportUrlSingleIdentifier() throws Exception
	{
		this.parseTest("@import url('test') abc123;"); //$NON-NLS-1$
	}
	
	/**
	 * Test import with a string and multiple identifiers
	 *
	 * @throws Exception
	 */
	public void testImportStringMultipleIdentifiers() throws Exception
	{
		this.parseTest("@import 'test' abc123, def456;"); //$NON-NLS-1$
	}
	
	/**
	 * Test import with a url and multiple identifiers
	 *
	 * @throws Exception
	 */
	public void testImportUrlMultipleIdentifiers() throws Exception
	{
		this.parseTest("@import url('test') abc123, def456;"); //$NON-NLS-1$
	}
	
	/**
	 * Test empty media
	 *
	 * @throws Exception
	 */
	public void testMediaEmpty() throws Exception
	{
		this.parseTest("@media test {}"); //$NON-NLS-1$
	}
	
	/**
	 * Test empty page
	 *
	 * @throws Exception
	 */
	public void testPageEmpty() throws Exception
	{
		this.parseTest("@page {}"); //$NON-NLS-1$
	}
	
	/**
	 * Test page with an identifier
	 *
	 * @throws Exception
	 */
	public void testPagePseudoIdentifier() throws Exception
	{
		this.parseTest("@page:abc123 {}"); //$NON-NLS-1$
	}
	
	/**
	 * Test page with a number declaration
	 *
	 * @throws Exception
	 */
	public void testPageNumberDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: 10;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with a percent declaration
	 *
	 * @throws Exception
	 */
	public void testPagePercentDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: 10%;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with a pixel declaration
	 *
	 * @throws Exception
	 */
	public void testPagePixelDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: 10px;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with a centimeter declaration
	 *
	 * @throws Exception
	 */
	public void testPageCentimeterDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: 10cm;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with a millimeter declaration
	 *
	 * @throws Exception
	 */
	public void testPageMillimeterDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: 10mm;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with a inch declaration
	 *
	 * @throws Exception
	 */
	public void testPageInchDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: 10in;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with a point declaration
	 *
	 * @throws Exception
	 */
	public void testPagePointDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: 10pt;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with a pica declaration
	 *
	 * @throws Exception
	 */
	public void testPagePicaDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: 10pc;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with an em declaration
	 *
	 * @throws Exception
	 */
	public void testPageEmDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: 10em;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with an ex declaration
	 *
	 * @throws Exception
	 */
	public void testPageExDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: 10ex;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with a degree declaration
	 *
	 * @throws Exception
	 */
	public void testPageDegreeDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: 10deg;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with a radian declaration
	 *
	 * @throws Exception
	 */
	public void testPageRadianDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: 10rad;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with a gradian declaration
	 *
	 * @throws Exception
	 */
	public void testPageGradianDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: 10grad;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with a millisecond declaration
	 *
	 * @throws Exception
	 */
	public void testPageMillisecondDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: 10ms;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with a second declaration
	 *
	 * @throws Exception
	 */
	public void testPageSecondDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: 10s;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with a hertz declaration
	 *
	 * @throws Exception
	 */
	public void testPageHertzDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: 10Hz;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with a kilohertz declaration
	 *
	 * @throws Exception
	 */
	public void testPageKilohertzDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: 10kHz;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with a single-quoted string declaration
	 *
	 * @throws Exception
	 */
	public void testPageSingleQuotedStringDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: '10';" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with a single-quoted string declaration
	 *
	 * @throws Exception
	 */
	public void testPageDoubleQuotedStringDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: \"10\";" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with an identifier declaration
	 *
	 * @throws Exception
	 */
	public void testPageIdentifierDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: abc123;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with a url declaration
	 *
	 * @throws Exception
	 */
	public void testPageUrlDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: url(abc123);" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with a 3-digit color declaration
	 *
	 * @throws Exception
	 */
	public void testPageThreeDigitColorDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: #eee;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with a 6-digit color declaration
	 *
	 * @throws Exception
	 */
	public void testPageSixDigitColorDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: #80A0FF;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with a plus declaration
	 *
	 * @throws Exception
	 */
	public void testPagePlusDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: +10;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with a minus declaration
	 *
	 * @throws Exception
	 */
	public void testPageMinusDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: -10;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with a function declaration
	 *
	 * @throws Exception
	 */
	public void testPageFunctionDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: rgb(10,20,30);" + EOL + "}");
	}
	
	/**
	 * Test page with an important declaration
	 *
	 * @throws Exception
	 */
	public void testPageImportantDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: aptana !important;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with a slash declaration
	 *
	 * @throws Exception
	 */
	public void testPageSlashDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: abc/123;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with multiple slashes declaration
	 *
	 * @throws Exception
	 */
	public void testPageMultiSlashDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: abc/123/rgb(1,2,3);" + EOL + "}");
	}
	
	/**
	 * Test page with a comma declaration
	 *
	 * @throws Exception
	 */
	public void testPageCommaDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: abc,123;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with multiple commas declaration
	 *
	 * @throws Exception
	 */
	public void testPageMultiCommaDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: abc,123,rgb(1,2,3);" + EOL + "}");
	}
	
	/**
	 * Test page with a space-delimited declaration
	 *
	 * @throws Exception
	 */
	public void testPageSpaceDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: abc 123;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * Test page with multiple space-delimiters declaration
	 *
	 * @throws Exception
	 */
	public void testPageMultiSpaceDeclaration() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: abc 123 rgb(1,2,3);" + EOL + "}");
	}
	
	/**
	 * Test page with multiple declaration
	 *
	 * @throws Exception
	 */
	public void testPageMultipleDeclarations() throws Exception
	{
		this.parseTest("@page {" + EOL + "    testing: abc123;" + EOL + "    forward: +10pt;" + EOL + "}");
	}
	
	/**
	 * Test single-quoted charset
	 *
	 * @throws Exception
	 */
	public void testCharsetSingleQuotedCharSet() throws Exception
	{
		this.parseTest("@charset 'test';");
	}
	
	/**
	 * Test double-quoted charset
	 *
	 * @throws Exception
	 */
	public void testCharsetDoubleQuotedCharSet() throws Exception
	{
		this.parseTest("@charset \"test\";");
	}
	
	/**
	 * Test the star, '*', selector
	 * 
	 * @throws Exception
	 */
	public void testAnyElementSelector() throws Exception
	{
		this.parseTest("* {}");
	}
	
	/**
	 * Test a simple element selector 
	 * 
	 * @throws Exception
	 */
	public void testElementSelector() throws Exception
	{
		this.parseTest("a {}");
	}
	
	/**
	 * Test descendant selector
	 * 
	 * @throws Exception
	 */
	public void testDescendantSelector() throws Exception
	{
		this.parseTest("table td {}");
	}
	
	/**
	 * Test child selector
	 * 
	 * @throws Exception
	 */
	public void testChildSelector() throws Exception
	{
		this.parseTest("table > tr {}");
	}
	
	/**
	 * Test element pseudo-class selector
	 * 
	 * @throws Exception
	 */
	public void testElementPseudoclassSelector() throws Exception
	{
		this.parseTest("td:first-child {}");
	}
	
	/**
	 * Test element pseudo-class function selector
	 * 
	 * @throws Exception
	 */
	public void testElementPseudoclassFunctionSelector() throws Exception
	{
		this.parseTest("p:lang(en) {}");
	}
	
	/**
	 * Test adjacent element selector
	 * 
	 * @throws Exception
	 */
	public void testAdjacentSelector() throws Exception
	{
		this.parseTest("p + p {}");
	}
	
	/**
	 * Test attribute-exists element selector
	 * 
	 * @throws Exception
	 */
	public void testAttributeSetSelector() throws Exception
	{
		this.parseTest("a[href] {}");
	}
	
	/**
	 * Test attribute-value element selector
	 * 
	 * @throws Exception
	 */
	public void testAttributeValueSelector() throws Exception
	{
		this.parseTest("p[lang=\"en\"] {}");
	}
	
	/**
	 * Test attribute-value-in-list element selector
	 * 
	 * @throws Exception
	 */
	public void testAttributeValueInListSelector() throws Exception
	{
		this.parseTest("p[lang~=\"en\"] {}");
	}
	
	/**
	 * Test attribute-hyphenated-value-in-list-starts-with-value selector
	 * 
	 * @throws Exception
	 */
	public void testAttributeHyphenateInListSelector() throws Exception
	{
		this.parseTest("p[lang|=\"en\"] {}");
	}
	
	/**
	 * Test element class-value-in-list selector
	 * 
	 * @throws Exception
	 */
	public void testClassSelector() throws Exception
	{
		this.parseTest("div.warning {}");
	}
	
	/**
	 * test element id selector
	 * 
	 * @throws Exception
	 */
	public void testIdSelector() throws Exception
	{
		this.parseTest("div#menu {}");
	}
	
	/**
	 * test multiple element selector
	 * 
	 * @throws Exception
	 */
	public void testMultipleElementSelector() throws Exception
	{
		this.parseTest("h1, h2, h3 {}");
	}
	
	/**
	 * Test the universal selector 
	 * 
	 * @throws Exception
	 */
	public void testUniversalSelector() throws Exception
	{
		this.parseTest("* {}");
	}
	
	/**
	 * Test universal with descendant selector
	 * 
	 * @throws Exception
	 */
	public void testUniversalDescendantSelector() throws Exception
	{
		this.parseTest("* td {}");
	}
	
	/**
	 * Test universal with child selector
	 * 
	 * @throws Exception
	 */
	public void testUniversalChildSelector() throws Exception
	{
		this.parseTest("* > tr {}");
	}
	
	/**
	 * Test universal element pseudo-class selector
	 * 
	 * @throws Exception
	 */
	public void testUniversalPseudoclassSelector() throws Exception
	{
		this.parseTest("*:first-child {}");
		this.parseTest(":first-child {}");
	}
	
	/**
	 * Test universal element pseudo-class function selector
	 * 
	 * @throws Exception
	 */
	public void testUniversalPseudoclassFunctionSelector() throws Exception
	{
		this.parseTest("*:lang(en) {}");
		this.parseTest(":lang(en) {}");
	}
	
	/**
	 * Test universal element adjacent element selector
	 * 
	 * @throws Exception
	 */
	public void testUniversalAdjacentSelector() throws Exception
	{
		this.parseTest("* + p {}");
	}
	
	/**
	 * Test universal element attribute-exists element selector
	 * 
	 * @throws Exception
	 */
	public void testUniversalAttributeSetSelector() throws Exception
	{
		this.parseTest("*[href] {}");
		this.parseTest("[href] {}");
	}
	
	/**
	 * Test universal element attribute-value element selector
	 * 
	 * @throws Exception
	 */
	public void testUniveralAttributeValueSelector() throws Exception
	{
		this.parseTest("*[lang=\"en\"] {}");
		this.parseTest("[lang=\"en\"] {}");
	}
	
	/**
	 * Test universal element attribute-value-in-list element selector
	 * 
	 * @throws Exception
	 */
	public void testUniversalAttributeValueInListSelector() throws Exception
	{
		this.parseTest("*[lang~=\"en\"] {}");
		this.parseTest("[lang~=\"en\"] {}");
	}
	
	/**
	 * Test universal element attribute-hyphenated-value-in-list-starts-with-value selector
	 * 
	 * @throws Exception
	 */
	public void testUniversalAttributeHyphenateInListSelector() throws Exception
	{
		this.parseTest("*[lang|=\"en\"] {}");
		this.parseTest("[lang|=\"en\"] {}");
	}
	
	/**
	 * Test universal element class-value-in-list selector
	 * 
	 * @throws Exception
	 */
	public void testUniversalClassSelector() throws Exception
	{
		this.parseTest("*.warning {}");
		this.parseTest(".warning {}");
	}
	
	/**
	 * test universal element id selector
	 * 
	 * @throws Exception
	 */
	public void testUniversalIdSelector() throws Exception
	{
		this.parseTest("*#menu {}");
		this.parseTest("#menu {}");
	}
	
	/**
	 * test single type selector with one property
	 * 
	 * @throws Exception
	 */
	public void testSimpleSelectorOneProperty() throws Exception
	{
		this.parseTest("a {" + EOL + "    testing: 10;" + EOL + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	/**
	 * test single type selector with multiple properties
	 * 
	 * @throws Exception
	 */
	public void testSimpleSelectorMultipleProperties() throws Exception
	{
		this.parseTest("a {" + EOL + "    testing: abc123;" + EOL + "    forward: +10pt;" + EOL + "}");
	}
	
	/**
	 * test multiple rule set definitions
	 * 
	 * @throws Exception
	 */
	public void testMultipleRuleSets() throws Exception
	{
		this.parseTest("a {}" + EOL + "b {}");
	}
}
