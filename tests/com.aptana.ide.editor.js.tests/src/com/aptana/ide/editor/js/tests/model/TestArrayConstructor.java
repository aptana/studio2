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
import com.aptana.ide.editor.js.runtime.JSArrayConstructor;
import com.aptana.ide.editor.js.runtime.JSFunction;
import com.aptana.ide.editor.js.runtime.JSNumber;

/**
 * @author Kevin Lindsey
 */
public class TestArrayConstructor extends TestModelBase
{
	private static final String TARGET = "Array";
	
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
		this.testType(this.getPublicPrototype(TARGET), "constructor", JSArrayConstructor.class);
	}
	
	/**
	 * testToString
	 */
	public void testToString()
	{
		this.testType(this.getPublicPrototype(TARGET), "toString", JSFunction.class);
	}
	
	/**
	 * testToLocaleString
	 */
	public void testToLocaleString()
	{
		this.testType(this.getPublicPrototype(TARGET), "toLocaleString", JSFunction.class);
	}
	
	/**
	 * testConcat
	 */
	public void testConcat()
	{
		this.testType(this.getPublicPrototype(TARGET), "concat", JSFunction.class);
	}
	
	/**
	 * testJoin
	 */
	public void testJoin()
	{
		this.testType(this.getPublicPrototype(TARGET), "join", JSFunction.class);
	}
	
	/**
	 * testPop
	 */
	public void testPop()
	{
		this.testType(this.getPublicPrototype(TARGET), "pop", JSFunction.class);
	}
	
	/**
	 * testPush
	 */
	public void testPush()
	{
		this.testType(this.getPublicPrototype(TARGET), "push", JSFunction.class);
	}
	
	/**
	 * testReverse
	 */
	public void testReverse()
	{
		this.testType(this.getPublicPrototype(TARGET), "reverse", JSFunction.class);
	}
	
	/**
	 * testShift
	 */
	public void testShift()
	{
		this.testType(this.getPublicPrototype(TARGET), "shift", JSFunction.class);
	}
	
	/**
	 * testSlice
	 */
	public void testSlice()
	{
		this.testType(this.getPublicPrototype(TARGET), "slice", JSFunction.class);
	}
	
	/**
	 * testSort
	 */
	public void testSort()
	{
		this.testType(this.getPublicPrototype(TARGET), "sort", JSFunction.class);
	}
	
	/**
	 * testSplice
	 */
	public void testSplice()
	{
		this.testType(this.getPublicPrototype(TARGET), "splice", JSFunction.class);
	}
	
	/**
	 * testUnshift
	 */
	public void testUnshift()
	{
		this.testType(this.getPublicPrototype(TARGET), "unshift", JSFunction.class);
	}
}
