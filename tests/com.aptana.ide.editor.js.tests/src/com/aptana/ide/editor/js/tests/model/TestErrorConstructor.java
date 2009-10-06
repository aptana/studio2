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
import com.aptana.ide.editor.js.runtime.JSErrorConstructor;
import com.aptana.ide.editor.js.runtime.JSFunction;
import com.aptana.ide.editor.js.runtime.JSNumber;
import com.aptana.ide.editor.js.runtime.JSString;

/**
 * @author Kevin Lindsey
 */
public class TestErrorConstructor extends TestModelBase
{
	private static final String TARGET = "Error";
	
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
		this.testType(this.getPublicPrototype(TARGET), "constructor", JSErrorConstructor.class);
	}
	
	/**
	 * testName
	 */
	public void testName()
	{
		this.testType(this.getPublicPrototype(TARGET), "name", JSString.class);
	}
	
	/**
	 * testMessage
	 */
	public void testMessage()
	{
		this.testType(this.getPublicPrototype(TARGET), "message", JSString.class);
	}
	
	/**
	 * testToString
	 */
	public void testToString()
	{
		this.testType(this.getPublicPrototype(TARGET), "toString", JSFunction.class);
	}
}
