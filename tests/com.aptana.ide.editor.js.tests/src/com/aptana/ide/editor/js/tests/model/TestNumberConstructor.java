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
import com.aptana.ide.editor.js.runtime.JSNumberConstructor;

/**
 * @author Kevin Lindsey
 */
public class TestNumberConstructor extends TestModelBase
{
	private static final String TARGET = "Number";
	
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
	 * testMaxValue
	 */
	public void testMaxValue()
	{
		this.testType(this.getGlobalProperty(TARGET), "MAX_VALUE", JSNumber.class);
	}
	
	/**
	 * testMinValue
	 */
	public void testMinValue()
	{
		this.testType(this.getGlobalProperty(TARGET), "MIN_VALUE", JSNumber.class);
	}
	
	/**
	 * testNaN
	 */
	public void testNaN()
	{
		this.testType(this.getGlobalProperty(TARGET), "NaN", JSNumber.class);
	}
	
	/**
	 * testNegativeInfinity
	 */
	public void testNegativeInfinity()
	{
		this.testType(this.getGlobalProperty(TARGET), "NEGATIVE_INFINITY", JSNumber.class);
	}
	
	/**
	 * testPositiveInfinity
	 */
	public void testPositiveInfinity()
	{
		this.testType(this.getGlobalProperty(TARGET), "POSITIVE_INFINITY", JSNumber.class);
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
		this.testType(this.getPublicPrototype(TARGET), "constructor", JSNumberConstructor.class);
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
	 * testValueOf
	 */
	public void testValueOf()
	{
		this.testType(this.getPublicPrototype(TARGET), "valueOf", JSFunction.class);
	}
	
	/**
	 * testToFixed
	 */
	public void testToFixed()
	{
		this.testType(this.getPublicPrototype(TARGET), "toFixed", JSFunction.class);
	}
	
	/**
	 * testToExponential
	 */
	public void testToExponential()
	{
		this.testType(this.getPublicPrototype(TARGET), "toExponential", JSFunction.class);
	}
	
	/**
	 * testToPrecision
	 */
	public void testToPrecision()
	{
		this.testType(this.getPublicPrototype(TARGET), "toPrecision", JSFunction.class);
	}
}
