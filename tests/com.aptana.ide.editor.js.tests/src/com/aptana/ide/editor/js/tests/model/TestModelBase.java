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

import junit.framework.TestCase;

import com.aptana.ide.editor.js.runtime.Environment;
import com.aptana.ide.editor.js.runtime.IFunction;
import com.aptana.ide.editor.js.runtime.IObject;
import com.aptana.ide.lexer.Range;

/**
 * @author Kevin Lindsey
 *
 */
public abstract class TestModelBase extends TestCase
{
	protected static final int LAST_FILE = Integer.MAX_VALUE;
	protected static final int LAST_OFFSET = Integer.MAX_VALUE;
	protected static final IObject[] NO_ARGS = new IObject[0];
	
	protected Environment environment;

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		this.environment = new Environment();
		this.environment.initBuiltInObjects();
	}

	/**
	 * getGlobal
	 *
	 * @param name
	 * @return
	 */
	protected IObject getGlobalProperty(String name)
	{
		return this.getProperty(this.environment.getGlobal(), name);
	}
	
	/**
	 * getInstance
	 *
	 * @param name
	 * @return
	 */
	protected IObject getInstance(String name)
	{
		IObject ctor = this.getGlobalProperty(name);
		
		assertTrue(ctor instanceof IFunction);
		
		return ((IFunction) ctor).construct(this.environment, NO_ARGS, LAST_FILE, Range.Empty);
	}
	
	/**
	 * getProperty
	 *
	 * @param object
	 * @param name
	 * @return
	 */
	protected IObject getProperty(IObject object, String name) {
		assertTrue(object.hasLocalProperty(name));
		
		return object.getPropertyValue(name, LAST_FILE, LAST_OFFSET);
	}
	
	/**
	 * getPrivatePrototype
	 *
	 * @param name
	 * @return
	 */
	protected IObject getPrivatePrototype(String name)
	{
		IObject object = this.getGlobalProperty(name);
		
		return object.getPrototype();
	}
	
	/**
	 * getPublicPrototype
	 *
	 * @param name
	 * @return
	 */
	protected IObject getPublicPrototype(String name)
	{
		IObject object = this.getGlobalProperty(name);
		
		return this.getProperty(object, "prototype");
	}
		
	/**
	 * invoke
	 *
	 * @param name
	 * @return
	 */
	protected IObject invoke(String name)
	{
		IObject ctor = this.getGlobalProperty(name);
		
		assertTrue(ctor instanceof IFunction);
		
		return ((IFunction) ctor).invoke(this.environment, NO_ARGS, LAST_FILE, Range.Empty);
	}
	
	/**
	 * testType
	 *
	 * @param object
	 * @param name
	 * @param type
	 */
	protected void testType(IObject object, String name, Class type) {
		IObject property = this.getProperty(object, name);
		
		// make sure it exists
		assertNotNull(property);
		
		// make sure it's a number
		assertSame(type, property.getClass());
	}
}
