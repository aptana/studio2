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
package com.aptana.ide.editor.js.tests;

import junit.framework.TestCase;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.scriptdoc.parsing.ScriptDocParseState;
import com.aptana.ide.editor.scriptdoc.parsing.ScriptDocParser;
import com.aptana.ide.lexer.LexerException;

/**
 * @author Ingo Muschenetz
 */
public class Documentation2Tests extends TestCase
{
	ScriptDocParser parser;

	/**
	 * Documentation2Tests
	 * 
	 * @param name
	 */
	public Documentation2Tests(String name)
	{
		super(name);
		try
		{
			parser = new ScriptDocParser();
		}
		catch (Exception e)
		{
		}
	}

	// /**
	// * Test doc
	// * @param {String} a First Param
	// * @param {Number} b Second Param
	// * @param {Object} c Third Param
	// * @return {RegExp} Return value
	// */
	String simpleTestSource = "/**\n * Test doc\n * @param {String} a First Param\n" + //$NON-NLS-1$
		"* @param {Number} b Second Param\n * @param {Object} c Third Param\n *" +  //$NON-NLS-1$
		"@return {RegExp} Return value\n */"; //$NON-NLS-1$

	/**
	 * testSimpleParsing
	 */
	public void testSimpleParsing()
	{
		// TODO: Needs fixing
		/*
		 * try { ScriptDocParseState ps = new ScriptDocParseState();
		 * ps.setEditState(simpleTestSource, simpleTestSource, 0, 0); parser.parse(ps);
		 * FunctionDocumentation fd = parser.getParsedObject(); assertEquals(fd.getDescription(),
		 * "Test doc"); TypedDescription[] pd = fd.getParams(); assertEquals(pd.length, 3);
		 * TypedDescription p0 = pd[0]; assertEquals(p0.getDescription(), "First Param");
		 * assertEquals(p0.getName(), "a"); assertEquals(p0.getTypes()[0], "String");
		 * TypedDescription p1 = pd[1]; assertEquals(p1.getDescription(), "Second Param");
		 * assertEquals(p1.getName(), "b"); assertEquals(p1.getTypes()[0], "Number");
		 * TypedDescription p2 = pd[2]; assertEquals(p2.getDescription(), "Third Param");
		 * assertEquals(p2.getName(), "c"); assertEquals(p2.getTypes()[0], "Object");
		 * assertEquals(fd.getReturn().getDescription(), "Return value");
		 * assertEquals(fd.getReturn().getTypes()[0], "RegExp"); } catch (LexerException e) {
		 * e.printStackTrace(); }
		 */
	}

	String projectTestSource = "/**\r\n" + " * @projectDescription\r\n" + " *  	Description of project.\r\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			+ " * @author Joe Blow\r\n" + " * @version 6.0\r\n" + " */"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	/**
	 * testProjectParsing
	 */
	public void testProjectParsing()
	{
		/*
		 * try { ScriptDocParseState ps = new ScriptDocParseState();
		 * ps.setEditState(projectTestSource, projectTestSource, 0, 0); parser.parse(ps);
		 * FunctionDocumentation pd = parser.getParsedObject(); assertEquals(pd.getDocumentType(),
		 * IDocumentation.TYPE_PROJECT); assertEquals(pd.getDescription(), "Description of
		 * project."); assertEquals(pd.getAuthor(), "Joe Blow"); assertEquals(pd.getVersion(),
		 * "6.0"); } catch (LexerException e) { e.printStackTrace(); }
		 */
	}

	String functionTestSource = "/**\r\n" + " * Test function doc\r\n" + " * @param a\r\n" + " * @param {String} b\r\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			+ " * @param c Third Param\r\n" + " * @param {Object} d Fourth Param\r\n" //$NON-NLS-1$ //$NON-NLS-2$
			+ " * @param {Object, String, ...} e Fifth Param\r\n" //$NON-NLS-1$
			+ " * @param { Object, [String],  UserType.Name.Name2 } f \r\n" + " * 		Sixth Param with\r\n" //$NON-NLS-1$ //$NON-NLS-2$
			+ " * 		description running over multiple lines.\r\n" + " *\r\n" + " * @ignore \r\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			+ " * @author Joe Blow\r\n" + " * @version 6.0\r\n" + " * @since 1.7.0\r\n" + " * @deprecated\r\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			+ " * @private\r\n" + " * @internal @native\r\n" + " *\r\n" + " * @constructor @method\r\n" + " *\r\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			+ " * @exception {SringException} Description of the string exception \r\n" //$NON-NLS-1$
			+ " * @exception {NumberException} Description of the number exception \r\n" //$NON-NLS-1$
			+ " * @member_of Base.Class\r\n" + " * @class_description 	\r\n" + " *  	Description of class\r\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			+ " * @example\r\n" + " *	var x = 5;\r\n" + " *	var y = x * y + \"@param\";\r\n" + " * @remarks\r\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			+ " *	Remarks <b>go</b> here.\r\n" + " *\r\n" + " * @return {RegExp, String} Return value description\r\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			+ " */"; //$NON-NLS-1$

	/**
	 * testFunctionParsing
	 */
	public void testFunctionParsing()
	{
		/*
		 * try { ScriptDocParseState ps = new ScriptDocParseState();
		 * ps.setEditState(functionTestSource, functionTestSource, 0, 0); parser.parse(ps);
		 * FunctionDocumentation fd = parser.getParsedObject(); assertEquals(fd.getDescription(),
		 * "Test function doc"); TypedDescription[] pd = fd.getParams(); assertEquals(pd.length, 6); //
		 * @param a TypedDescription p = pd[0]; assertEquals(p.getDescription(), StringUtils.EMPTY);
		 * assertEquals(p.getName(), "a"); assertEquals(p.getTypes().length, 0); // @param {String}
		 * b\r\n" p = pd[1]; assertEquals(p.getDescription(), StringUtils.EMPTY); assertEquals(p.getName(), "b");
		 * assertEquals(p.getTypes()[0], "String"); // @param c Third Param p = pd[2];
		 * assertEquals(p.getDescription(), "Third Param"); assertEquals(p.getName(), "c");
		 * assertEquals(p.getTypes().length, 0); // @param {Object} d Fourth Param p = pd[3];
		 * assertEquals(p.getDescription(), "Fourth Param"); assertEquals(p.getName(), "d");
		 * assertEquals(p.getTypes()[0], "Object"); // @param {Object, String, ...} e Fifth Param p =
		 * pd[4]; assertEquals(p.getDescription(), "Fifth Param"); assertEquals(p.getName(), "e");
		 * assertEquals(p.getTypes()[0], "Object"); assertEquals(p.getTypes()[1], "String");
		 * assertEquals(p.getTypes()[2], "..."); // @param { Object, [String], UserType.Name.Name2 }
		 * f // Fifth Param with // description running over multiple lines. p = pd[5];
		 * assertEquals(p.getDescription(), "Sixth Param with description running over multiple
		 * lines."); assertEquals(p.getName(), "f"); assertEquals(p.getTypes()[0], "Object");
		 * assertEquals(p.getTypes()[1], "[String]"); assertEquals(p.getTypes()[2],
		 * "UserType.Name.Name2"); } catch (LexerException e) { e.printStackTrace(); }
		 */
	}

	String propertyTestSource = "/**\r\n" + " * Test property doc\r\n" + " *\r\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			+ " * @type {string} propName Property Description\r\n" + " * @memberOf Base.Class\r\n" + " */"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	/**
	 * testPropertyParsing
	 */
	public void testPropertyParsing()
	{
		try
		{
			ScriptDocParseState ps = new ScriptDocParseState();
			ps.setEditState(propertyTestSource, propertyTestSource, 0, 0);
			parser.parse(ps);
			// FunctionDocumentation fd =
			parser.getParsedObject();
		}
		catch (LexerException e)
		{
			IdeLog.logError(TestingPlugin.getDefault(), "Error", e); //$NON-NLS-1$
		}
	}

}
