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
import com.aptana.ide.editor.js.runtime.JSDateConstructor;
import com.aptana.ide.editor.js.runtime.JSFunction;
import com.aptana.ide.editor.js.runtime.JSNumber;

/**
 * @author Kevin Lindsey
 */
public class TestDateConstructor extends TestModelBase
{
	private static final String TARGET = "Date";
	
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
	 * testParse
	 */
	public void testParse()
	{
		this.testType(this.getGlobalProperty(TARGET), "parse", JSFunction.class);
	}
	
	/**
	 * testUTC
	 */
	public void testUTC()
	{
		this.testType(this.getGlobalProperty(TARGET), "UTC", JSFunction.class);
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
		this.testType(this.getPublicPrototype(TARGET), "constructor", JSDateConstructor.class);
	}
	
	/**
	 * testToString
	 */
	public void testToString()
	{
		this.testType(this.getPublicPrototype(TARGET), "toString", JSFunction.class);
	}
	
	/**
	 * testToDateString
	 */
	public void testToDateString()
	{
		this.testType(this.getPublicPrototype(TARGET), "toDateString", JSFunction.class);
	}
	
	/**
	 * testToTimeString
	 */
	public void testToTimeString()
	{
		this.testType(this.getPublicPrototype(TARGET), "toTimeString", JSFunction.class);
	}
	
	/**
	 * testToLocaleString
	 */
	public void testToLocaleString()
	{
		this.testType(this.getPublicPrototype(TARGET), "toLocaleString", JSFunction.class);
	}
	
	/**
	 * testToLocaleDateString
	 */
	public void testToLocaleDateString()
	{
		this.testType(this.getPublicPrototype(TARGET), "toLocaleDateString", JSFunction.class);
	}
	
	/**
	 * testToLocaleTimeString
	 */
	public void testToLocaleTimeString()
	{
		this.testType(this.getPublicPrototype(TARGET), "toLocaleTimeString", JSFunction.class);
	}
	
	/**
	 * testValueOf
	 */
	public void testValueOf()
	{
		this.testType(this.getPublicPrototype(TARGET), "valueOf", JSFunction.class);
	}
	
	/**
	 * testGetTime
	 */
	public void testGetTime()
	{
		this.testType(this.getPublicPrototype(TARGET), "getTime", JSFunction.class);
	}
	
	/**
	 * testGetFullYear
	 */
	public void testGetFullYear()
	{
		this.testType(this.getPublicPrototype(TARGET), "getFullYear", JSFunction.class);
	}
	
	/**
	 * testGetUTCFullYear
	 */
	public void testGetUTCFullYear()
	{
		this.testType(this.getPublicPrototype(TARGET), "getUTCFullYear", JSFunction.class);
	}
	
	/**
	 * testGetMonth
	 */
	public void testGetMonth()
	{
		this.testType(this.getPublicPrototype(TARGET), "getMonth", JSFunction.class);
	}
	
	/**
	 * testGetUTCMonth
	 */
	public void testGetUTCMonth()
	{
		this.testType(this.getPublicPrototype(TARGET), "getUTCMonth", JSFunction.class);
	}
	
	/**
	 * testGetDate
	 */
	public void testGetDate()
	{
		this.testType(this.getPublicPrototype(TARGET), "getDate", JSFunction.class);
	}
	
	/**
	 * testGetUTCDate
	 */
	public void testGetUTCDate()
	{
		this.testType(this.getPublicPrototype(TARGET), "getUTCDate", JSFunction.class);
	}
	
	/**
	 * testGetDay
	 */
	public void testGetDay()
	{
		this.testType(this.getPublicPrototype(TARGET), "getDay", JSFunction.class);
	}
	
	/**
	 * testGetUTCDay
	 */
	public void testGetUTCDay()
	{
		this.testType(this.getPublicPrototype(TARGET), "getUTCDay", JSFunction.class);
	}
	
	/**
	 * testGetHours
	 */
	public void testGetHours()
	{
		this.testType(this.getPublicPrototype(TARGET), "getHours", JSFunction.class);
	}
	
	/**
	 * testGetUTCHours
	 */
	public void testGetUTCHours()
	{
		this.testType(this.getPublicPrototype(TARGET), "getUTCHours", JSFunction.class);
	}
	
	/**
	 * testGetMinutes
	 */
	public void testVtestGetMinutesalueOf()
	{
		this.testType(this.getPublicPrototype(TARGET), "getMinutes", JSFunction.class);
	}
	
	/**
	 * testGetUTCMinutes
	 */
	public void testGetUTCMinutes()
	{
		this.testType(this.getPublicPrototype(TARGET), "getUTCMinutes", JSFunction.class);
	}
	
	/**
	 * testGetSeconds
	 */
	public void testGetSeconds()
	{
		this.testType(this.getPublicPrototype(TARGET), "getSeconds", JSFunction.class);
	}
	
	/**
	 * testGetUTCSeconds
	 */
	public void testGetUTCSeconds()
	{
		this.testType(this.getPublicPrototype(TARGET), "getUTCSeconds", JSFunction.class);
	}
	
	/**
	 * testGetMilliseconds
	 */
	public void testGetMilliseconds()
	{
		this.testType(this.getPublicPrototype(TARGET), "getMilliseconds", JSFunction.class);
	}
	
	/**
	 * testGetUTCMilliseconds
	 */
	public void testGetUTCMilliseconds()
	{
		this.testType(this.getPublicPrototype(TARGET), "getUTCMilliseconds", JSFunction.class);
	}
	
	/**
	 * testGetTimezoneOffset
	 */
	public void testGetTimezoneOffset()
	{
		this.testType(this.getPublicPrototype(TARGET), "getTimezoneOffset", JSFunction.class);
	}
	
	/**
	 * testSetTime
	 */
	public void testSetTime()
	{
		this.testType(this.getPublicPrototype(TARGET), "setTime", JSFunction.class);
	}
	
	/**
	 * testSetMilliseconds
	 */
	public void testSetMilliseconds()
	{
		this.testType(this.getPublicPrototype(TARGET), "setMilliseconds", JSFunction.class);
	}
	
	/**
	 * testSetUTCMilliseconds
	 */
	public void testSetUTCMilliseconds()
	{
		this.testType(this.getPublicPrototype(TARGET), "setUTCMilliseconds", JSFunction.class);
	}
	
	/**
	 * testSetSeconds
	 */
	public void testSetSeconds()
	{
		this.testType(this.getPublicPrototype(TARGET), "setSeconds", JSFunction.class);
	}
	
	/**
	 * testSetUTCSeconds
	 */
	public void testSetUTCSeconds()
	{
		this.testType(this.getPublicPrototype(TARGET), "setUTCSeconds", JSFunction.class);
	}
	
	/**
	 * testSetMinutes
	 */
	public void testSetMinutes()
	{
		this.testType(this.getPublicPrototype(TARGET), "setMinutes", JSFunction.class);
	}
	
	/**
	 * testSetUTCMinutes
	 */
	public void testSetUTCMinutes()
	{
		this.testType(this.getPublicPrototype(TARGET), "setUTCMinutes", JSFunction.class);
	}
	
	/**
	 * testSetHours
	 */
	public void testSetHours()
	{
		this.testType(this.getPublicPrototype(TARGET), "setHours", JSFunction.class);
	}
	
	/**
	 * testSetUTCHours
	 */
	public void testSetUTCHours()
	{
		this.testType(this.getPublicPrototype(TARGET), "setUTCHours", JSFunction.class);
	}
	
	/**
	 * testSetDate
	 */
	public void testSetDate()
	{
		this.testType(this.getPublicPrototype(TARGET), "setDate", JSFunction.class);
	}
	
	/**
	 * testSetUTCDate
	 */
	public void testSetUTCDate()
	{
		this.testType(this.getPublicPrototype(TARGET), "setUTCDate", JSFunction.class);
	}
	
	/**
	 * testSetMonth
	 */
	public void testSetMonth()
	{
		this.testType(this.getPublicPrototype(TARGET), "setMonth", JSFunction.class);
	}
	
	/**
	 * testSetUTCMonth
	 */
	public void testSetUTCMonth()
	{
		this.testType(this.getPublicPrototype(TARGET), "setUTCMonth", JSFunction.class);
	}
	
	/**
	 * testSetFullYear
	 */
	public void testSetFullYear()
	{
		this.testType(this.getPublicPrototype(TARGET), "setFullYear", JSFunction.class);
	}
	
	/**
	 * testSetUTCFullYear
	 */
	public void testSetUTCFullYear()
	{
		this.testType(this.getPublicPrototype(TARGET), "setUTCFullYear", JSFunction.class);
	}
	
	/**
	 * testToUTCString
	 */
	public void testToUTCString()
	{
		this.testType(this.getPublicPrototype(TARGET), "toUTCString", JSFunction.class);
	}
}
