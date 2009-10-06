/**
 * Copyright (c) 2005-2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Aptana Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 *
 * Redistribution, except as permitted by the above license, is prohibited.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.editor.js.tests.model;

import com.aptana.ide.editor.js.runtime.IObject;
import com.aptana.ide.editor.js.runtime.JSFunction;
import com.aptana.ide.editor.js.runtime.JSNumber;
import com.aptana.ide.editor.js.runtime.JSStringConstructor;

/**
 * @author Kevin Lindsey
 */
public class TestStringConstructor extends TestModelBase
{
	private static final String TARGET = "String";
	
	/**
	 * testExists
	 */
	public void testExists()
	{
		IObject object = this.getGlobalProperty(TARGET);

		assertNotNull(object);
	}

	/**
	 * testClassName
	 */
	public void testClassName()
	{
		IObject object = this.getGlobalProperty(TARGET);
		String name = object.getClassName();

		assertEquals("Function", name);
	}

	/**
	 * testLength
	 */
	public void testLength()
	{
		this.testType(this.getGlobalProperty(TARGET), "length", JSNumber.class);
	}
	
	/**
	 * testFromCharCode
	 */
	public void testFromCharCode()
	{
		this.testType(this.getGlobalProperty(TARGET), "fromCharCode", JSFunction.class);
	}

	/**
	 * testPrivatePrototype
	 */
	public void testPrivatePrototype()
	{
		IObject privatePrototype = this.getPrivatePrototype(TARGET);
		IObject functionPrototype = this.getPublicPrototype("Function");

		assertSame(functionPrototype, privatePrototype);
	}

	/**
	 * testConstructor
	 */
	public void testConstructor()
	{
		this.testType(this.getPublicPrototype(TARGET), "constructor", JSStringConstructor.class);
	}
	
	/**
	 * testToString
	 */
	public void testToString()
	{
		this.testType(this.getPublicPrototype(TARGET), "toString", JSFunction.class);
	}
	
	/**
	 * testValueOf
	 */
	public void testValueOf()
	{
		this.testType(this.getPublicPrototype(TARGET), "valueOf", JSFunction.class);
	}
	
	/**
	 * testCharAt
	 */
	public void testCharAt()
	{
		this.testType(this.getPublicPrototype(TARGET), "charAt", JSFunction.class);
	}
	
	/**
	 * testCharCodeAt
	 */
	public void testCharCodeAt()
	{
		this.testType(this.getPublicPrototype(TARGET), "charCodeAt", JSFunction.class);
	}
	
	/**
	 * testConcat
	 */
	public void testConcat()
	{
		this.testType(this.getPublicPrototype(TARGET), "concat", JSFunction.class);
	}
	
	/**
	 * testIndexOf
	 */
	public void testIndexOf()
	{
		this.testType(this.getPublicPrototype(TARGET), "indexOf", JSFunction.class);
	}
	
	/**
	 * testLastIndexOf
	 */
	public void testLastIndexOf()
	{
		this.testType(this.getPublicPrototype(TARGET), "lastIndexOf", JSFunction.class);
	}
	
	/**
	 * testLocaleCompare
	 */
	public void testLocaleCompare()
	{
		this.testType(this.getPublicPrototype(TARGET), "localeCompare", JSFunction.class);
	}
	
	/**
	 * testMatch
	 */
	public void testMatch()
	{
		this.testType(this.getPublicPrototype(TARGET), "match", JSFunction.class);
	}
	
	/**
	 * testReplace
	 */
	public void testReplace()
	{
		this.testType(this.getPublicPrototype(TARGET), "replace", JSFunction.class);
	}
	
	/**
	 * testSearch
	 */
	public void testSearch()
	{
		this.testType(this.getPublicPrototype(TARGET), "search", JSFunction.class);
	}
	
	/**
	 * testSlice
	 */
	public void testSlice()
	{
		this.testType(this.getPublicPrototype(TARGET), "slice", JSFunction.class);
	}
	
	/**
	 * testSplit
	 */
	public void testSplit()
	{
		this.testType(this.getPublicPrototype(TARGET), "split", JSFunction.class);
	}
	
	/**
	 * testSubstring
	 */
	public void testSubstring()
	{
		this.testType(this.getPublicPrototype(TARGET), "substring", JSFunction.class);
	}
	
	/**
	 * testToLowerCase
	 */
	public void testToLowerCase()
	{
		this.testType(this.getPublicPrototype(TARGET), "toLowerCase", JSFunction.class);
	}
	
	/**
	 * testToLocaleLowerCase
	 */
	public void testToLocaleLowerCase()
	{
		this.testType(this.getPublicPrototype(TARGET), "toLocaleLowerCase", JSFunction.class);
	}
	
	/**
	 * testToUpperCase
	 */
	public void testToUpperCase()
	{
		this.testType(this.getPublicPrototype(TARGET), "toUpperCase", JSFunction.class);
	}
	
	/**
	 * testToLocaleUpperCase
	 */
	public void testToLocaleUpperCase()
	{
		this.testType(this.getPublicPrototype(TARGET), "toLocaleUpperCase", JSFunction.class);
	}
}
