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

import junit.framework.TestCase;

import com.aptana.ide.editor.js.runtime.Environment;
import com.aptana.ide.editor.js.runtime.IFunction;
import com.aptana.ide.editor.js.runtime.IObject;
import com.aptana.ide.editor.js.runtime.JSScope;
import com.aptana.ide.editor.js.runtime.JSString;
import com.aptana.ide.editor.scriptdoc.runtime.ScriptDocVM;
import com.aptana.ide.lexer.Range;

/**
 * @author Kevin Lindsey
 */
public class TestOpcodes extends TestCase
{
	private Environment _environment;
	private ScriptDocVM _vm;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		this._environment = new Environment();
		this._environment.initBuiltInObjects();
		this._vm = new ScriptDocVM();
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		this._environment = null;
		this._vm = null;
	}

	/**
	 * testGetGlobal
	 */
	public void testGetGlobal()
	{
		// create program
		this._vm.addGetGlobal();
		
		// execute it
		this._vm.execute(this._environment);
		
		// make sure we got something
		Object[] results = this._vm.getStackValues();
		assertEquals(1, results.length);
		
		// and make sure it is global
		assertSame(results[0], this._environment.getGlobal());
	}
	
	/**
	 * testPush
	 */
	public void testPush()
	{
		String value = "string";
		
		// create program
		this._vm.addPush(value);
		
		// execute it
		this._vm.execute(this._environment);
		
		// make sure we got something
		Object[] results = this._vm.getStackValues();
		assertEquals(1, results.length);
		
		// and make sure it is the original string
		assertSame(results[0], value);
	}
	
	/**
	 * testDuplicate
	 */
	public void testDuplicate()
	{
		String value = "string";
		
		// create program
		this._vm.addPush(value);
		this._vm.addDuplicate();
		
		// execute it
		this._vm.execute(this._environment);
		
		// make sure we got something
		Object[] results = this._vm.getStackValues();
		assertEquals(2, results.length);
		
		// and make sure it is the original string
		assertSame(results[0], value);
		assertSame(results[1], value);
	}
	
	/**
	 * testSwap
	 */
	public void testSwap()
	{
		String value1 = "value1";
		String value2 = "value2";
		
		// create program
		this._vm.addPush(value1);
		this._vm.addPush(value2);
		this._vm.addSwap();
		
		// execute it
		this._vm.execute(this._environment);
		
		// make sure we got something
		Object[] results = this._vm.getStackValues();
		assertEquals(2, results.length);
		
		// and make sure it is the original string
		assertSame(results[0], value2);
		assertSame(results[1], value1);
	}
	
	/**
	 * testPop
	 */
	public void testPop()
	{
		// create program
		this._vm.addPush("string");
		this._vm.addPop();
		
		// execute it
		this._vm.execute(this._environment);
		
		// make sure we have nothing
		Object[] results = this._vm.getStackValues();
		assertEquals(0, results.length);
	}
	
	/**
	 * testGet
	 */
	public void testGet()
	{
		// create program
		this._vm.addGetGlobal();
		this._vm.addPush("String");
		this._vm.addGet(0, 0);
		
		// execute it
		this._vm.execute(this._environment);
		
		// make sure we got something
		Object[] results = this._vm.getStackValues();
		assertEquals(1, results.length);
		
		// and make sure it is the string constructor function
		JSScope global = this._environment.getGlobal();
		IFunction stringConstructor = (IFunction) global.getPropertyValue("String", 0, 0);
		
		assertSame(results[0], stringConstructor);
	}
	
	/**
	 * testInstantiation
	 */
	public void testInstantiation()
	{
		// create program
		this._vm.addGetGlobal();
		this._vm.addPush("String");
		this._vm.addGet(0, 0);
		this._vm.addInstantiate(0, new Range(0,0));
		
		// execute it
		this._vm.execute(this._environment);
		
		// make sure we got something
		Object[] results = this._vm.getStackValues();
		assertEquals(1, results.length);
		
		// and make sure it is a string object
		assertTrue(results[0] instanceof JSString);
	}
	
	/**
	 * testInvoke
	 */
	public void testInvoke()
	{
		// create program
		this._vm.addGetGlobal();
		this._vm.addPush("String");
		this._vm.addGet(0, 0);
		this._vm.addInvoke(0, new Range(0,0));
		
		// execute it
		this._vm.execute(this._environment);
		
		// make sure we got something
		Object[] results = this._vm.getStackValues();
		assertEquals(1, results.length);
		
		// and make sure it is a string object
		assertTrue(results[0] instanceof JSString);
	}
	
	/**
	 * testPut
	 */
	public void testPut()
	{
		String propertyName = "myString";
		
		// create program
		this._vm.addGetGlobal();
		this._vm.addDuplicate();
		this._vm.addPush("String");
		this._vm.addGet(0, 0);
		this._vm.addInstantiate(0, new Range(0,0));
		this._vm.addPush(propertyName);
		this._vm.addSwap();
		this._vm.addPut(0);
		
		// execute it
		this._vm.execute(this._environment);
		
		// make sure we have nothing
		Object[] results = this._vm.getStackValues();
		assertEquals(0, results.length);
		
		// make sure "myString" exists
		JSScope global = this._environment.getGlobal();
		assertTrue(global.hasLocalProperty(propertyName));
		
		// and make sure it is a string object
		IObject value = global.getPropertyValue(propertyName, 0, 0);
		assertTrue(value instanceof JSString);
	}
}
