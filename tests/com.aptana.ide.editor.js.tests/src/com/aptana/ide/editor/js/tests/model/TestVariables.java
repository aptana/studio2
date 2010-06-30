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
import com.aptana.ide.editor.js.runtime.IScope;
import com.aptana.ide.editor.js.runtime.JSScope;
import com.aptana.ide.lexer.Range;


/**
 * @author Kevin Lindsey
 */
public class TestVariables extends TestModelBase
{
	private static final int FILE_INDEX = 1;
	
	/**
	 * testSetVariable - indirectly tests hasVariable too
	 */
	public void testSetVariable()
	{
		IScope scope = new JSScope();
		String name = "test";
		IObject value = this.environment.createString(FILE_INDEX, new Range(10, 20));
		
		assertFalse(scope.hasVariable(name));
		
		scope.putVariableValue(name, value, FILE_INDEX);
		
		assertTrue(scope.hasVariable(name));
	}

	/**
	 * testGetVariable
	 */
	public void testGetVariable()
	{
		IScope scope = new JSScope();
		String name = "test";
		IObject value = this.environment.createString(FILE_INDEX, new Range(10, 20));
		
		scope.putVariableValue(name, value, FILE_INDEX);
		
		assertSame(scope.getVariableValue(name, FILE_INDEX, LAST_OFFSET), value);
	}

	/**
	 * testPropertyOnParentScope
	 */
	public void testPropertyOnParentScope()
	{
		IScope scope = new JSScope();
		IScope parent = new JSScope();
		String name = "test";
		IObject value = this.environment.createString(FILE_INDEX, new Range(0, 9));
		
		scope.setParentScope(parent);
		
		parent.putVariableValue(name, value, FILE_INDEX);
		
		assertFalse(scope.hasLocalProperty(name));
		assertTrue(scope.hasVariable(name));
		assertSame(scope.getVariableValue(name, FILE_INDEX, LAST_OFFSET), value);
	}
	
	/**
	 * testPropertyOnPrivatePrototype2
	 */
	public void testPropertyOnPrivatePrototype2()
	{
		IScope scope = new JSScope();
		IScope parent = new JSScope();
		IScope grandparent = new JSScope();
		String name = "test";
		IObject value = this.environment.createString(FILE_INDEX, new Range(0, 9));
		
		scope.setParentScope(parent);
		parent.setParentScope(grandparent);
		
		grandparent.putVariableValue(name, value, FILE_INDEX);
		
		assertFalse(scope.hasLocalProperty(name));
		assertFalse(parent.hasLocalProperty(name));
		assertTrue(scope.hasVariable(name));
		assertSame(scope.getVariableValue(name, FILE_INDEX, LAST_OFFSET), value);
	}

	/**
	 * testDelete
	 */
	public void testDelete()
	{
		IScope scope = new JSScope();
		String name = "test";
		IObject value = this.environment.createString(FILE_INDEX, new Range(10, 20));
		
		scope.putVariableValue(name, value, FILE_INDEX);
		
		assertTrue(scope.hasVariable(name));
		
		scope.unputVariableName(name);
		
		assertFalse(scope.hasVariable(name));
	}
	
	/**
	 * testEnumerable
	 */
	public void testEnumerable()
	{
		IScope scope = new JSScope();
		String name = "test";
		IObject value = this.environment.createString(FILE_INDEX, new Range(10, 20));
		
		scope.putVariableValue(name, value, FILE_INDEX);
		
		String[] names = scope.getVariableNames();
		
		assertEquals(1, names.length);
		assertEquals(name, names[0]);
	}
}
