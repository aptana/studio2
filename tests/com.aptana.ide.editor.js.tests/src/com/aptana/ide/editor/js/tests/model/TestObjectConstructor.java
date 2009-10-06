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
import com.aptana.ide.editor.js.runtime.JSObjectConstructor;

/**
 * @author Kevin Lindsey
 */
public class TestObjectConstructor extends TestModelBase
{
	private static final String TARGET = "Object";
	
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
		this.testType(this.getPublicPrototype(TARGET), "constructor", JSObjectConstructor.class);
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
	 * testHasOwnProperty
	 */
	public void testHasOwnProperty()
	{
		this.testType(this.getPublicPrototype(TARGET), "hasOwnProperty", JSFunction.class);
	}
	
	/**
	 * testIsPrototypeOf
	 */
	public void testIsPrototypeOf()
	{
		this.testType(this.getPublicPrototype(TARGET), "isPrototypeOf", JSFunction.class);
	}
	
	/**
	 * testPropertyIsEnumerable
	 */
	public void testPropertyIsEnumerable()
	{
		this.testType(this.getPublicPrototype(TARGET), "propertyIsEnumerable", JSFunction.class);
	}
}
