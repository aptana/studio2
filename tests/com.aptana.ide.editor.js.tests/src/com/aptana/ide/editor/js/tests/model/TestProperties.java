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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.aptana.ide.editor.js.runtime.IObject;
import com.aptana.ide.editor.js.runtime.Property;
import com.aptana.ide.lexer.Range;


/**
 * @author Kevin Lindsey
 */
public class TestProperties extends TestModelBase
{
	private static final int FILE_INDEX = 1;
	
	/**
	 * testSetProperty - indirectly tests hasProperty too
	 */
	public void testSetProperty()
	{
		IObject object = this.environment.createObject(FILE_INDEX, new Range(0, 100));
		String name = "test";
		IObject value = this.environment.createString(FILE_INDEX, new Range(10, 20));
		
		assertFalse(object.hasProperty(name));
		
		object.putPropertyValue(name, value, FILE_INDEX);
		
		assertTrue(object.hasProperty(name));
	}

	/**
	 * testGetProperty
	 */
	public void testGetProperty()
	{
		IObject object = this.environment.createObject(FILE_INDEX, new Range(0, 100));
		String name = "test";
		IObject value = this.environment.createString(FILE_INDEX, new Range(10, 20));
		
		object.putPropertyValue(name, value, FILE_INDEX);
		
		assertSame(object.getPropertyValue(name, FILE_INDEX, LAST_OFFSET), value);
	}

	/**
	 * testPropertyInFileBeforeOffset
	 */
	public void testPropertyInFileBeforeOffset()
	{
		IObject object = this.environment.createObject(FILE_INDEX, new Range(0, 100));
		String name = "test";
		IObject value1 = this.environment.createString(FILE_INDEX, new Range(10, 20));
		IObject value2 = this.environment.createString(FILE_INDEX, new Range(0, 9));
		
		object.putPropertyValue(name, value1, FILE_INDEX);
		object.putPropertyValue(name, value2, FILE_INDEX);
		
		assertSame(object.getPropertyValue(name, FILE_INDEX, LAST_OFFSET), value1);
		assertSame(object.getPropertyValue(name, FILE_INDEX, value1.getStartingOffset() - 1), value2);
	}

	/**
	 * testPropertyInFileAtOffset
	 */
	public void testPropertyInFileAtOffset()
	{
		IObject object = this.environment.createObject(FILE_INDEX, new Range(0, 100));
		String name = "test";
		IObject value1 = this.environment.createString(FILE_INDEX, new Range(10, 20));
		IObject value2 = this.environment.createString(FILE_INDEX, new Range(10, 20));
		
		object.putPropertyValue(name, value1, FILE_INDEX);
		
		try
		{
			object.putPropertyValue(name, value2, FILE_INDEX);
			
			//fail("Setting two values to the same offset in the same file should throw IllegalStateException");
			
			assertSame(object.getPropertyValue(name, FILE_INDEX, LAST_OFFSET), value1);
		}
		catch (IllegalStateException e)
		{
			// expected exception
		}
	}

	/**
	 * testPropertyInFileAfterOffset
	 */
	public void testPropertyInFileAfterOffset()
	{
		IObject object = this.environment.createObject(FILE_INDEX, new Range(0, 100));
		String name = "test";
		IObject value1 = this.environment.createString(FILE_INDEX, new Range(0, 9));
		IObject value2 = this.environment.createString(FILE_INDEX, new Range(10, 20));
		
		object.putPropertyValue(name, value1, FILE_INDEX);
		object.putPropertyValue(name, value2, FILE_INDEX);
		
		assertSame(object.getPropertyValue(name, FILE_INDEX, LAST_OFFSET), value2);
		assertSame(object.getPropertyValue(name, FILE_INDEX, value2.getStartingOffset() - 1), value1);
	}

	/**
	 * testPropertyBeforeFile
	 */
	public void testPropertyBeforeFile()
	{
		IObject object = this.environment.createObject(FILE_INDEX - 1, new Range(0, 100));
		String name = "test";
		IObject value1 = this.environment.createString(FILE_INDEX, new Range(0, 9));
		IObject value2 = this.environment.createString(FILE_INDEX - 1, new Range(0, 9));
		
		object.putPropertyValue(name, value1, FILE_INDEX);
		object.putPropertyValue(name, value2, FILE_INDEX - 1);
		
		assertSame(object.getPropertyValue(name, FILE_INDEX, LAST_OFFSET), value1);
		assertSame(object.getPropertyValue(name, FILE_INDEX - 1, LAST_OFFSET), value2);
	}
	
	/**
	 * testPropertyAfterFile
	 */
	public void testPropertyAfterFile()
	{
		IObject object = this.environment.createObject(FILE_INDEX, new Range(0, 100));
		String name = "test";
		IObject value1 = this.environment.createString(FILE_INDEX, new Range(0, 9));
		IObject value2 = this.environment.createString(FILE_INDEX + 1, new Range(0, 9));
		
		object.putPropertyValue(name, value1, FILE_INDEX);
		object.putPropertyValue(name, value2, FILE_INDEX + 1);
		
		assertSame(object.getPropertyValue(name, FILE_INDEX, LAST_OFFSET), value1);
		assertSame(object.getPropertyValue(name, FILE_INDEX + 1, LAST_OFFSET), value2);
	}
	
	/**
	 * testPropertyOnPrivatePrototype
	 */
	public void testPropertyOnPrivatePrototype()
	{
		IObject object = this.environment.createObject(FILE_INDEX, new Range(0, 100));
		IObject proto = object.getPrototype();
		String name = "test";
		IObject value = this.environment.createString(FILE_INDEX, new Range(0, 9));
		
		proto.putPropertyValue(name, value, FILE_INDEX);
		
		assertFalse(object.hasLocalProperty(name));
		assertTrue(object.hasProperty(name));
		assertSame(object.getPropertyValue(name, FILE_INDEX, LAST_OFFSET), value);
	}
	
	/**
	 * testPropertyOnPrivatePrototype2
	 */
	public void testPropertyOnPrivatePrototype2()
	{
		IObject object = this.environment.createObject(FILE_INDEX, new Range(0, 100));
		IObject proto = object.getPrototype();
		IObject proto2 = this.environment.createObject(FILE_INDEX, new Range(0, 100));
		String name = "test";
		IObject value = this.environment.createString(FILE_INDEX, new Range(0, 9));
		
		proto2.putPropertyValue(name, value, FILE_INDEX);
		proto.setPrototype(proto2);
		
		assertFalse(object.hasLocalProperty(name));
		assertFalse(proto.hasLocalProperty(name));
		assertTrue(object.hasProperty(name));
		assertSame(object.getPropertyValue(name, FILE_INDEX, LAST_OFFSET), value);
	}
	
	/**
	 * testCanPut
	 */
	public void testCanPut()
	{
		IObject object = this.environment.createObject(FILE_INDEX, new Range(0, 100));
		String name = "test";
		IObject value = this.environment.createString(FILE_INDEX, new Range(0, 9));
		
		assertTrue(object.canPut(name));
		
		object.putPropertyValue(name, value, FILE_INDEX, Property.READ_ONLY);
		
		assertFalse(object.canPut(name));
	}

	/**
	 * testDelete
	 */
	public void testDelete()
	{
		IObject object = this.environment.createObject(FILE_INDEX, new Range(0, 100));
		String name = "test";
		IObject value = this.environment.createString(FILE_INDEX, new Range(0, 9));
		
		object.putPropertyValue(name, value, FILE_INDEX);
		
		assertTrue(object.hasProperty(name));
		
		object.unputPropertyName(name, FILE_INDEX, value.getStartingOffset());
		
		assertFalse(object.hasProperty(name));
	}
	
	/**
	 * testDontDelete
	 */
	public void testDontDelete()
	{
		IObject object = this.environment.createObject(FILE_INDEX, new Range(0, 100));
		String name = "test";
		IObject value = this.environment.createString(FILE_INDEX, new Range(0, 9));
		
		object.putPropertyValue(name, value, FILE_INDEX, Property.DONT_DELETE);
		
		assertTrue(object.hasProperty(name));
		
		object.unputPropertyName(name, FILE_INDEX, value.getStartingOffset());
		
		assertTrue(object.hasProperty(name));
	}
	
	/**
	 * testEnumerable
	 */
	public void testEnumerable()
	{
		IObject object = this.environment.createObject(FILE_INDEX, new Range(0, 100));
		String name = "test";
		IObject value = this.environment.createString(FILE_INDEX, new Range(0, 9));
		
		object.putPropertyValue(name, value, FILE_INDEX);
		
		String[] names = object.getPropertyNames();
		
		assertEquals(1, names.length);
		assertEquals(name, names[0]);
	}
	
	/**
	 * testNotEnumerable
	 */
	public void testNotEnumerable()
	{
		IObject object = this.environment.createObject(FILE_INDEX, new Range(0, 100));
		String name = "test";
		IObject value = this.environment.createString(FILE_INDEX, new Range(0, 9));
		
		object.putPropertyValue(name, value, FILE_INDEX, Property.DONT_ENUM);
		
		String[] names = object.getPropertyNames();
		
		assertEquals(0, names.length);
	}
	
	/**
	 * testNotEnumerable2
	 */
	public void testNotEnumerable2()
	{
		IObject object = this.environment.createObject(FILE_INDEX, new Range(0, 100));
		String name = "test";
		IObject value = this.environment.createString(FILE_INDEX, new Range(0, 9));
		
		object.putPropertyValue(name, value, FILE_INDEX, Property.DONT_ENUM);
		
		String[] names = object.getPropertyNames(true);
		
		Set<String> namesSet = new HashSet<String>(Arrays.asList(names));
		assertTrue(namesSet.contains(name));
	}
}
