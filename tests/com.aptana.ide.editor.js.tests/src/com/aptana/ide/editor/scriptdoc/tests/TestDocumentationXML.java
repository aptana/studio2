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
package com.aptana.ide.editor.scriptdoc.tests;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import com.aptana.ide.editor.js.runtime.Environment;
import com.aptana.ide.editor.js.runtime.IFunction;
import com.aptana.ide.editor.js.runtime.IObject;
import com.aptana.ide.editor.js.runtime.JSFunction;
import com.aptana.ide.editor.js.runtime.JSScope;
import com.aptana.ide.editor.js.runtime.ObjectBase;
import com.aptana.ide.editor.scriptdoc.parsing.reader.NativeObjectsReader2;
import com.aptana.ide.editor.scriptdoc.parsing.reader.ScriptDocException;
import com.aptana.ide.editor.scriptdoc.parsing.reader.ScriptDocInitializationException;
import com.aptana.ide.lexer.Range;

/**
 * @author Kevin Lindsey
 */
public class TestDocumentationXML extends TestCase
{
	private Environment _environment;
	private JSScope _global;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		// create javascript environment
		this._environment = new Environment();
		this._global = this._environment.initBuiltInObjects();
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		// free memory
		this._environment = null;
		this._global = null;
	}

	/**
	 * getClass
	 * 
	 * @param name
	 * @return
	 */
	private JSFunction getClass(String name)
	{
		assertTrue(this._global.hasLocalProperty(name));
		
		// grab function
		IObject myClass = this._global.getPropertyValue(name, 0, 0);
		
		// make sure it is a function
		assertTrue(myClass instanceof IFunction);
		
		return (JSFunction) myClass;
	}
	
	/**
	 * check for instance property
	 * 
	 * @param classFunction
	 * @param propertyName
	 */
	private void assertInstanceProperty(JSFunction classFunction, String propertyName)
	{
		IObject instance = classFunction.construct(this._environment, null, 0, new Range(0,0));
		
		// make sure we got something
		assertNotNull(instance);
		
		// and that it is defined
		assertNotSame(ObjectBase.UNDEFINED, instance);
		
		// make sure property is not directly on the instance
		assertFalse(instance.hasLocalProperty(propertyName));
		
		// but it should exist somewhere in the [[proto]] chain
		assertTrue(instance.hasProperty(propertyName));
	}
	
	/**
	 * loadResource
	 * 
	 * @param resourceName
	 * @throws IOException
	 */
	private void loadResource(String resourceName)
	{
		InputStream input = TestDocumentationXML.class.getResourceAsStream(resourceName);
		NativeObjectsReader2 reader = new NativeObjectsReader2(this._environment);
		
		try
		{
			reader.loadXML(input, true);
		}
		catch (ScriptDocInitializationException e)
		{
			e.printStackTrace();
		}
		catch (ScriptDocException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * test class
	 */
	public void testClass()
	{
		this.loadResource("/Documentation Tests/class.xml");
		
		// NOTE: getting class does all the basic checks we need here
		this.getClass("myClass");
	}
	
	/**
	 * test instance property
	 */
	public void testInstanceProperty()
	{
		this.loadResource("/Documentation Tests/instanceProperty.xml");
		
		// get class
		JSFunction myClass = this.getClass("myClass");
		
		// make sure instance property exists
		this.assertInstanceProperty(myClass, "myInstanceProperty");
	}
	
	/**
	 * test static property
	 */
	public void testStaticProperty()
	{
		this.loadResource("/Documentation Tests/staticProperty.xml");
		
		// get class
		JSFunction myClass = this.getClass("myClass");
		
		// make sure property is in on function
		assertTrue(myClass.hasProperty("myStaticProperty"));
	}
	
	/**
	 * test a single level of single-inheritance
	 */
	public void testSingleInheritance()
	{
		this.loadResource("/Documentation Tests/singleInheritance.xml");
		
		// get class
		JSFunction superClass = this.getClass("superClass");
		
		// check for static property
		assertTrue(superClass.hasProperty("superClassStaticProperty"));
		
		// check for instance property
		this.assertInstanceProperty(superClass, "superClassInstanceProperty");

		// get sub-class
		JSFunction subClass = this.getClass("subClass");
		
		// check for static property and make sure we don't have the superclass property
		assertTrue(subClass.hasProperty("subClassStaticProperty"));
		assertFalse(subClass.hasProperty("superClassStaticProperty"));
		
		// check for all instance properties, including inherited ones
		this.assertInstanceProperty(subClass, "subClassInstanceProperty");
		this.assertInstanceProperty(subClass, "superClassInstanceProperty");
	}
	
	/**
	 * test a single level of single-inheritance with classes defined out of order
	 */
	public void testSingleInheritance2()
	{
		this.loadResource("/Documentation Tests/singleInheritanceOutOfOrder.xml");
		
		// get class
		JSFunction superClass = this.getClass("superClass");
		
		// check for static property
		assertTrue(superClass.hasProperty("superClassStaticProperty"));
		
		// check for instance property
		this.assertInstanceProperty(superClass, "superClassInstanceProperty");
		
		// get sub-class
		JSFunction subClass = this.getClass("subClass");
		
		// check for static property and make sure we don't have the superclass property
		assertTrue(subClass.hasProperty("subClassStaticProperty"));
		assertFalse(subClass.hasProperty("superClassStaticProperty"));
		
		// check for all instance properties, including inherited ones
		this.assertInstanceProperty(subClass, "subClassInstanceProperty");
		this.assertInstanceProperty(subClass, "superClassInstanceProperty");
	}
	
	/**
	 * test a single level of multiple-inheritance
	 */
	public void testMultipleInheritance()
	{
		this.loadResource("/Documentation Tests/multipleInheritance.xml");
		
		// get class
		JSFunction superClass = this.getClass("superClass");
		
		// check for static property
		assertTrue(superClass.hasProperty("superClassStaticProperty"));
		
		// check for instance property
		this.assertInstanceProperty(superClass, "superClassInstanceProperty");
		
		// get class
		JSFunction superClass2 = this.getClass("superClass2");
		
		// check for static property
		assertTrue(superClass2.hasProperty("superClass2StaticProperty"));
		
		// check for instance property
		this.assertInstanceProperty(superClass2, "superClass2InstanceProperty");
		
		// get sub-class
		JSFunction subClass = this.getClass("subClass");
		
		// check for static property and make sure we don't have the superclass property
		assertTrue(subClass.hasProperty("subClassStaticProperty"));
		assertFalse(subClass.hasProperty("superClassStaticProperty"));
		assertFalse(subClass.hasProperty("superClass2StaticProperty"));
		
		// check for all instance properties, including inherited ones
		this.assertInstanceProperty(subClass, "subClassInstanceProperty");
		this.assertInstanceProperty(subClass, "superClassInstanceProperty");
		this.assertInstanceProperty(subClass, "superClass2InstanceProperty");
	}
	
	/**
	 * test a single level of multiple-inheritance with classes defined out of order
	 */
	public void testMultipleInheritance2()
	{
		this.loadResource("/Documentation Tests/multipleInheritanceOutOfOrder.xml");
		
		// get class
		JSFunction superClass = this.getClass("superClass");
		
		// check for static property
		assertTrue(superClass.hasProperty("superClassStaticProperty"));
		
		// check for instance property
		this.assertInstanceProperty(superClass, "superClassInstanceProperty");
		
		// get class
		JSFunction superClass2 = this.getClass("superClass2");
		
		// check for static property
		assertTrue(superClass2.hasProperty("superClass2StaticProperty"));
		
		// check for instance property
		this.assertInstanceProperty(superClass2, "superClass2InstanceProperty");
		
		// get sub-class
		JSFunction subClass = this.getClass("subClass");
		
		// check for static property and make sure we don't have the superclass property
		assertTrue(subClass.hasProperty("subClassStaticProperty"));
		assertFalse(subClass.hasProperty("superClassStaticProperty"));
		assertFalse(subClass.hasProperty("superClass2StaticProperty"));
		
		// check for all instance properties, including inherited ones
		this.assertInstanceProperty(subClass, "subClassInstanceProperty");
		this.assertInstanceProperty(subClass, "superClassInstanceProperty");
		this.assertInstanceProperty(subClass, "superClass2InstanceProperty");
	}
}
