/* ***** BEGIN LICENSE BLOCK *****
 * Version: GPL 3
 *
 * This program is Copyright (C) 2007-2008 Aptana, Inc. All Rights Reserved
 * This program is licensed under the GNU General Public license, version 3 (GPL).
 *
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by the GPL,
 * is prohibited.
 *
 * You can redistribute and/or modify this program under the terms of the GPL, 
 * as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * You may view the GPL, and Aptana's exception and additional terms in the file
 * titled license-jaxer.html in the main distribution folder of this program.
 * 
 * Any modifications to this file must keep this entire header intact.
 *
 * ***** END LICENSE BLOCK ***** */
package com.aptana.ide.editor.scriptdoc.tests.runtime;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import com.aptana.ide.editor.js.parsing.JSEnvironmentHandler;
import com.aptana.ide.editor.js.parsing.JSParser2;
import com.aptana.ide.editor.js.runtime.Environment;
import com.aptana.ide.editor.js.runtime.IObject;
import com.aptana.ide.editor.js.runtime.JSArray;
import com.aptana.ide.editor.js.runtime.JSBoolean;
import com.aptana.ide.editor.js.runtime.JSNull;
import com.aptana.ide.editor.js.runtime.JSNumber;
import com.aptana.ide.editor.js.runtime.JSObject;
import com.aptana.ide.editor.js.runtime.JSScope;
import com.aptana.ide.editor.js.runtime.JSString;
import com.aptana.ide.io.StreamUtils;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.parsing.IParseState;

/**
 * @author Kevin Lindsey
 */
public class TestCodeGeneration extends TestCase
{
	private JSParser2 _parser;
	private IParseState _parseState;
	private JSEnvironmentHandler _handler;
	private Environment _environment;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		this._parser = new JSParser2();
		this._parseState = this._parser.createParseState(null);
		this._handler = new JSEnvironmentHandler();
		this._environment = new Environment();
		this._environment.initBuiltInObjects();
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		this._parser = null;
		this._parseState = null;
		this._handler = null;
		this._environment = null;
		super.tearDown();
	}

	/**
	 * getFileContents
	 * 
	 * @return
	 * @throws IOException
	 */
	private String getFileContents(String filename) throws IOException
	{
		String fullFilename = "/Code Generation Tests/" + filename;
		InputStream input = this.getClass().getResourceAsStream(fullFilename);
		if (input == null)
			fail("Failed to get input stream for " + fullFilename);
		return StreamUtils.getText(input);
	}

	/**
	 * parse
	 * 
	 * @param source
	 * @throws LexerException
	 * @throws IOException
	 */
	private void parseAndExecute(String filename) throws LexerException, IOException
	{
		String source = getFileContents(filename);
		this._parseState.setEditState(source, source, 0, 0);
		this._parser.addHandler(this._handler);
		this._parser.parse(this._parseState);
		this._handler.getVM().execute(this._environment);
	}

	private IObject grabObject(String jsFile, String varName) throws LexerException, IOException
	{
		parseAndExecute(jsFile);
		JSScope global = this._environment.getGlobal();
		return global.getPropertyValue(varName, 0, Integer.MAX_VALUE);
	}

	/**
	 * testSimpleBooleanAssignment
	 */
	public void testSimpleBooleanAssignment() throws Exception
	{
		IObject x = grabObject("simpleBooleanAssignment.js", "x");
		assertTrue(x instanceof JSBoolean);
	}

	/**
	 * testEmptyArrayAssignment
	 */
	public void testEmptyArrayAssignment() throws Exception
	{
		IObject x = grabObject("emptyArrayAssignment.js", "x");
		assertTrue(x instanceof JSArray);
	}

	/**
	 * testElidedArrayAssignment
	 */
	public void testElidedArrayAssignment() throws Exception
	{
		IObject x = grabObject("elidedArrayAssignment.js", "x");
		assertTrue(x instanceof JSArray);
	}

	/**
	 * testFirstElementElidedArrayAssignment
	 */
	public void testFirstElementElidedArrayAssignment() throws Exception
	{
		IObject x = grabObject("firstElementElidedArrayAssignment.js", "x");
		assertTrue(x instanceof JSArray);
	}

	/**
	 * testLastElementElidedArrayAssignment
	 */
	public void testLastElementElidedArrayAssignment() throws Exception
	{
		IObject x = grabObject("lastElementElidedArrayAssignment.js", "x");
		assertTrue(x instanceof JSArray);
	}

	/**
	 * testTrailingElementElidedArrayAssignment
	 */
	public void testTrailingElementElidedArrayAssignment() throws Exception
	{
		IObject x = grabObject("trailingElementElidedArrayAssignment.js", "x");
		assertTrue(x instanceof JSArray);
	}

	/**
	 * testSingleElementArrayAssignment
	 */
	public void testSingleElementArrayAssignment() throws Exception
	{
		IObject x = grabObject("singleElementArrayAssignment.js", "x");
		assertTrue(x instanceof JSArray);
	}

	/**
	 * testMultipleElementArrayAssignment
	 */
	public void testMultipleElementArrayAssignment() throws Exception
	{
		IObject x = grabObject("multipleElementArrayAssignment.js", "x");
		assertTrue(x instanceof JSArray);
	}

	/**
	 * testSimpleNullAssignment
	 */
	public void testSimpleNullAssignment() throws Exception
	{
		IObject x = grabObject("simpleNullAssignment.js", "x");
		assertTrue(x instanceof JSNull);
	}

	/**
	 * testSimpleNumberAssignment
	 */
	public void testSimpleNumberAssignment() throws Exception
	{
		IObject x = grabObject("simpleNumberAssignment.js", "x");
		assertTrue(x instanceof JSNumber);
	}

	/**
	 * testEmptyObjectAssignment
	 */
	public void testEmptyObjectAssignment() throws Exception
	{
		IObject x = grabObject("emptyObjectAssignment.js", "x");
		assertTrue(x instanceof JSObject);
	}

	// /**
	// * testSimpleRegExpAssignment
	// */
	// public void testSimpleRegExpAssignment()
	// {
	// IObject x = grabObject("simpleRegExpAssignment.js", "x");
	// assertTrue(x instanceof JSRegExp);
	// }

	/**
	 * testSimpleStringAssignment
	 */
	public void testSimpleStringAssignment() throws Exception
	{
		IObject x = grabObject("simpleStringAssignment.js", "x");
		assertTrue(x instanceof JSString);
	}
}
