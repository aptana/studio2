/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.editor.js.tests;

import java.io.File;

import junit.framework.TestCase;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.js.JSFileServiceFactory;
import com.aptana.ide.editor.js.JSOffsetMapper;
import com.aptana.ide.editor.js.contentassist.JSContentAssistProcessor;
import com.aptana.ide.editor.js.runtime.IObject;
import com.aptana.ide.editor.js.runtime.IScope;
import com.aptana.ide.editors.junit.TestUtils;
import com.aptana.ide.editors.unified.FileService;
import com.aptana.ide.editors.unified.FileSourceProvider;
import com.aptana.ide.editors.unified.ParentOffsetMapper;

/**
 * @author Ingo Muschenetz
 */
public class JSLanguageEnvironmentTest extends TestCase
{

	/**
	 * JSLanguageEnvironmentTest
	 */
	public JSLanguageEnvironmentTest()
	{
		TestUtils.loadEnvironment();
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	/**
	 * Test method for 'com.aptana.ide.editor.js.JSLanguageEnvironment.JSLanguageEnvironment()'
	 */
	public void testEnvironment()
	{

		JSOffsetMapper mapper = getOffsetMapper();

		IScope scope = mapper.getGlobal();

		IObject obj = mapper.lookupReturnTypeFromNameHash("document.", scope); //$NON-NLS-1$
		String[] props = JSContentAssistProcessor.getAllPropertyNamesInScope(obj, true);
		assertTrue("getElementById", TestUtils.find(props, "getElementById")); //$NON-NLS-1$ //$NON-NLS-2$

		obj = mapper.lookupReturnTypeFromNameHash("window.", scope); //$NON-NLS-1$
		props = JSContentAssistProcessor.getAllPropertyNamesInScope(obj, true);
		assertTrue("alert", TestUtils.find(props, "alert")); //$NON-NLS-1$ //$NON-NLS-2$

		obj = mapper.lookupReturnTypeFromNameHash("document.body.", scope); //$NON-NLS-1$
		props = JSContentAssistProcessor.getAllPropertyNamesInScope(obj, true);

		// Test document. Document is of type element, which derives from Node, and then Object
		// From Element
		assertTrue("getElementsByTagNameNS", TestUtils.find(props, "getElementsByTagNameNS")); //$NON-NLS-1$ //$NON-NLS-2$

		// From Node
		assertTrue("firstChild", TestUtils.find(props, "firstChild")); //$NON-NLS-1$ //$NON-NLS-2$

		// From Object
		assertTrue("isPrototypeOf", TestUtils.find(props, "isPrototypeOf")); //$NON-NLS-1$ //$NON-NLS-2$

		// Test document.createElement(). This returns an object of type HTMLElement, which is a
		// subclass of Element
		obj = mapper.lookupReturnTypeFromNameHash("document.createElement().", scope); //$NON-NLS-1$
		props = JSContentAssistProcessor.getAllPropertyNamesInScope(obj, true);

		// From HTMLElement (Dom Level 2)
		assertTrue("applyElement", TestUtils.find(props, "applyElement")); //$NON-NLS-1$ //$NON-NLS-2$

		// From HTMLElement (Dom Level 0). This should work.
		// assertTrue("onkeyup", find(props, "onkeyup"));

		// From Element
		assertTrue("getElementsByTagNameNS", TestUtils.find(props, "getElementsByTagNameNS")); //$NON-NLS-1$ //$NON-NLS-2$

		// From Node
		assertTrue("firstChild", TestUtils.find(props, "firstChild")); //$NON-NLS-1$ //$NON-NLS-2$

		// From Object
		assertTrue("isPrototypeOf", TestUtils.find(props, "isPrototypeOf")); //$NON-NLS-1$ //$NON-NLS-2$

		// Test Event as well for dom 0 & dom 2
	}

	/**
	 * Test method for 'com.aptana.ide.editor.js.JSLanguageEnvironment.JSLanguageEnvironment()'
	 */
	public void testJSCoreEnvironment()
	{

		JSOffsetMapper mapper = getOffsetMapper();
		assertPropertyOnObject(mapper, "Date", "parse"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Date", "UTC"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "E"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "LN10"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "LN2"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "LOG10E"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "LOG2E"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "PI"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "SQRT1_2"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "SQRT2"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "abs"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "acos"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "asin"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "atan"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "atan2"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "ceil"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "cos"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "exp"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "floor"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "log"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "max"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "min"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "pow"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "random"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "round"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "sin"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "sqrt"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Math", "tan"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Number", "MAX_VALUE"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Number", "MIN_VALUE"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Number", "NaN"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Number", "NEGATIVE_INFINITY"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "Number", "POSITIVE_INFINITY"); //$NON-NLS-1$ //$NON-NLS-2$
		assertPropertyOnObject(mapper, "String", "fromCharCode"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * testAlias
	 */
	public void testAlias()
	{

	}

	/**
	 * Tests loading dojo
	 */
	public void testDojo()
	{
		/*
		 * Load hyperscope JS packages Here's the feedback: hs.model - src/client/lib/hs/model.js
		 * hs.address - src/client/lib/hs/address.js hs.util- src/client/lib/hs/util.js etc. These
		 * files are defined in the Dojo __package__.js file to be loadable by just doing a
		 * dojo.require("hs.*"), in: src/client/lib/hs/__package__.js
		 */

		// Load dojo.js.uncompressed.js
		// Test the following:
		/*
		 * var a = new hs.address.Address(window.location.href); a. <-- after you type this period,
		 * the Aptana JavaScript interpreter should prompt and show all of the methods and
		 * properties of this object, such as resolve(), but it does not
		 */

		// Name hash is hs.address.Address(window.location.href);
		// Test inside address.js and model.js
		/*
		 * These are all loaded dynamically through Dojo notation, which might cause issues. For
		 * example, in the demo (which isn't finished yet), inside src/demos/address/address.js and
		 * src/demos/address/address.html, at the top of address.html we have the following: <script
		 * type="text/javascript"> var djConfig = { isDebug: true, disableFlashStorage: true };
		 * </script> <script type="text/javascript"
		 * src="../../client/lib/dojo/release/dojo/dojo.js"></script> <script
		 * type="text/javascript" src="address.js"></script> And in demo's address.js file, we have
		 * the following at the top: define our custom packages path is relative to dojo root
		 * dojo.hostenv.setModulePrefix("hs", "../../../hs"); bring in required packages
		 * dojo.require("dojo.event.*"); dojo.require("hs.*");
		 */

		/*
		 * One interesting thing is that static, not instantiated code complete works. So if I type:
		 * hs.address. at the last period I am generally prompted with the available properties
		 * here. It looks like things break that are attached to the object's prototype (i.e. things
		 * that are only copied to instantiated objects with the new operator). If you look at my
		 * unit tests, created with JSUnit such as in src/tests/client/TestHyperScope.html, you will
		 * see I directly load my hs JavaScript files in, instead of using dojo.require, so they are
		 * staticly loaded in: <head> <title>Test Suite for HyperScope</title> <script
		 * language="javascript" src="lib/jsunit/app/jsUnitCore.js"></script> <script
		 * type="text/javascript"> var djConfig = { isDebug: true, disableFlashStorage: true };
		 * </script> <script type="text/javascript"
		 * src="../../client/lib/dojo/release/dojo/dojo.js"></script> <script
		 * type="text/javascript" src="../../client/lib/hs/util.js"></script> <script
		 * type="text/javascript" src="../../client/lib/hs/exception.js"></script> <script
		 * type="text/javascript" src="../../client/lib/hs/model.js"></script> <script
		 * type="text/javascript" src="../../client/lib/hs/filter.js"></script> <script
		 * type="text/javascript" src="../../client/lib/hs/address.js"></script> <script
		 * type="text/javascript" src="../../client/lib/hs/commands.js"></script> </head> Inside of
		 * here, you will see that autosuggest is also broken. Also, if you load up any of the
		 * complex hs package files, such as src/client/lib/hs/address.js, you will see that the
		 * outline view gives incorrect information. For example, it incorrectly will nest a top
		 * level object at the top that doesn't make any sense; it shows my 'classes' correctly
		 * (really just objects), but opening them up doesn't show the methods and properties right.
		 * The code above, since it is uses complicated features of JavaScript and invokes them in
		 * several different ways, should be useful for you to track these bugs down.
		 */
	}

	/**
	 * @param mapper
	 * @param objectName
	 */
	public static void assertObject(JSOffsetMapper mapper, String objectName)
	{
		IScope scope = mapper.getGlobal();
		IObject obj = mapper.lookupReturnTypeFromNameHash(objectName, scope);
		assertNotNull(objectName, obj);
	}

	/**
	 * @param mapper
	 * @param objectName
	 * @param propertyName
	 */
	public static void assertPropertyOnObject(JSOffsetMapper mapper, String objectName, String propertyName)
	{
		IScope scope = mapper.getGlobal();
		IObject obj = mapper.lookupReturnTypeFromNameHash(objectName + ".", scope); //$NON-NLS-1$
		String[] props = JSContentAssistProcessor.getAllPropertyNamesInScope(obj, true);
		assertTrue(propertyName, TestUtils.find(props, propertyName));
	}

	/**
	 * Creates a new offset mapper from an empty, temporary file
	 * 
	 * @return Returns a new offset mapper from an empty, temporary file.
	 */
	private JSOffsetMapper getOffsetMapper()
	{

		File temp = TestUtils.createFileFromString("test", ".js", StringUtils.EMPTY); //$NON-NLS-1$ //$NON-NLS-2$ 
		FileSourceProvider fsp = new FileSourceProvider(temp);
		FileService fileService = JSFileServiceFactory.getInstance().createFileService(fsp);
		ParentOffsetMapper parentMapper = new ParentOffsetMapper(fileService);
		JSOffsetMapper mapper = new JSOffsetMapper(parentMapper);

		return mapper;
	}

	/**
	 * Test method for 'com.aptana.ide.editor.js.JSLanguageEnvironment.JSLanguageEnvironment()'
	 */
	public void testJSLanguageEnvironment()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.editor.js.JSLanguageEnvironment.getRuntimeEnvironment()'
	 */
	public void testGetRuntimeEnvironment()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.editor.js.JSLanguageEnvironment.getEnvironment()'
	 */
	public void testGetEnvironment()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.editor.js.JSLanguageEnvironment.getJSEnvironment()'
	 */
	public void testGetJSEnvironment()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.editor.js.JSLanguageEnvironment.getInstance()'
	 */
	public void testGetInstance()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.editor.js.JSLanguageEnvironment.getDocumentationManager()'
	 */
	public void testGetDocumentationManager()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.editor.js.JSLanguageEnvironment.cleanEnvironment()'
	 */
	public void testCleanEnvironment()
	{

	}

	/**
	 * Test method for 'com.aptana.ide.editor.js.JSLanguageEnvironment.attachFile(FileService)'
	 */
	public void testAttachFile()
	{

	}

}
