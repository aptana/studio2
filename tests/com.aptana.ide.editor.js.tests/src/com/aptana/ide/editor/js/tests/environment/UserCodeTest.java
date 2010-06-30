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
package com.aptana.ide.editor.js.tests.environment;

import java.io.File;

import junit.framework.TestCase;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.js.JSFileServiceFactory;
import com.aptana.ide.editor.js.JSLanguageEnvironment;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.js.runtime.Environment;
import com.aptana.ide.editor.js.runtime.IObject;
import com.aptana.ide.editor.js.runtime.JSFunction;
import com.aptana.ide.editor.js.runtime.JSNumber;
import com.aptana.ide.editor.js.runtime.JSObject;
import com.aptana.ide.editor.js.runtime.JSScope;
import com.aptana.ide.editor.js.runtime.JSUndefined;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.junit.TestUtils;
import com.aptana.ide.editors.profiles.Profile;
import com.aptana.ide.editors.profiles.ProfileManager;
import com.aptana.ide.editors.unified.FileService;
import com.aptana.ide.editors.unified.FileSourceProvider;
import com.aptana.ide.editors.unified.IFileLanguageService;
import com.aptana.ide.editors.unified.ILanguageEnvironment;

/**
 * 
 */
public class UserCodeTest extends TestCase
{
	private Environment _jsEnv;
	private JSScope _global;
	private int _max = Integer.MAX_VALUE;
	
	private String _userCode = StringUtils.EMPTY; 
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception 
	{
		super.setUp();
		createUserCode();
		ILanguageEnvironment env = JSLanguageEnvironment.getInstance();
		env.loadEnvironment();
		
		TestUtils.waitForParse();
		
		_jsEnv = (Environment)env.getRuntimeEnvironment();		
		_global = _jsEnv.getGlobal();
		JSUndefined.getSingletonInstance();

		File temp = TestUtils.createFileFromString("test", ".js", _userCode); //$NON-NLS-1$ //$NON-NLS-2$
		FileSourceProvider fsp = new FileSourceProvider(temp);
		new UnifiedEditorsPlugin();
		FileService fileService = JSFileServiceFactory.getInstance().createFileService(fsp);
		IFileLanguageService fls = fileService.getLanguageService(JSMimeType.MimeType);
		fls.getOffsetMapper(); 
		
		TestUtils.waitForParse();
	}

	/**
	 * testUserCode
	 *
	 */
	public void testUserCode()
	{
		ILanguageEnvironment env = JSLanguageEnvironment.getInstance();
		env.loadEnvironment();
		
		env.getRuntimeEnvironment();	
		
		ProfileManager pm = TestUtils.createProfileManager();
		
		// set up two profiles
		File userCodeFile = TestUtils.createFileFromString("userCode", ".js", _userCode); //$NON-NLS-1$ //$NON-NLS-2$
		Profile profile0 = TestUtils.createProfile("name", "paths", new File[] {userCodeFile});		 //$NON-NLS-1$ //$NON-NLS-2$
		pm.addProfile(profile0);
				
		// set the first to be currentProfile
		pm.setCurrentProfile(profile0.getURI()); // this calls apply profiles
		TestUtils.waitForParse();
		
		// test fn0
		IObject fn0 = _global.getLocalProperty("fn0").getValue(_max, _max); //$NON-NLS-1$
		assertTrue(fn0 instanceof JSFunction);
		IObject fn0StaticProp = fn0.getLocalProperty("staticProp").getValue(_max, _max); //$NON-NLS-1$
		assertTrue(fn0StaticProp instanceof JSNumber);
		
		// test fn1
		IObject fn1 = _global.getLocalProperty("fn1").getValue(_max, _max); //$NON-NLS-1$
		assertTrue(fn1 instanceof JSFunction);

		// test fn0Inst
		IObject fn0Inst = _global.getLocalProperty("fn0Inst").getValue(_max, _max); //$NON-NLS-1$
		assertTrue(fn0Inst instanceof JSObject);
		IObject fn0InstBaseProp = fn0Inst.getProperty("baseProp").getValue(_max, _max); //$NON-NLS-1$
		assertTrue(fn0InstBaseProp instanceof JSNumber);
		
		// test fn1Inst
		IObject fn1Inst = _global.getLocalProperty("fn1Inst").getValue(_max, _max); //$NON-NLS-1$
		assertTrue(fn1Inst instanceof JSObject);

		// test fn1Inst properties
		// this props
//		IObject fn1Inst_prop0 = fn1Inst.getLocalProperty("prop0").getValue(max, max);
//		assertTrue(fn1Inst_prop0 instanceof JSNumber);
//		IObject fn1Inst_nestedFn = fn1Inst.getLocalProperty("nestedFn").getValue(max, max);
//		assertTrue(fn1Inst_nestedFn instanceof JSFunction);
//		// prototype assignments
//		IObject fn1Inst_meth0 = fn1Inst.getLocalProperty("meth0").getValue(max, max);
//		assertTrue(fn1Inst_meth0 instanceof JSFunction);
//		// indirect prototype assignment
////		IObject fn1Inst_prop1 = fn1Inst.getLocalProperty("prop1").getValue(max, max);
////		assertTrue(fn1Inst_prop1 instanceof JSString);
//		// base properties
//		IObject fn1Inst_baseProp = fn1Inst.getProperty("baseProp").getValue(max, max);
//		assertEquals(fn1Inst_baseProp, fn0Inst_baseProp);
//		assertTrue(fn1Inst_baseProp instanceof JSNumber);
//		
//		// test a,b,c,d
//		IObject a = global.getLocalProperty("a").getValue(max, max);
//		assertTrue(a instanceof JSNumber);
//		IObject b = global.getLocalProperty("b").getValue(max, max);
//		assertEquals(a, b);
//		IObject c = global.getLocalProperty("c").getValue(max, max);
//		assertEquals(a, c);
//		IObject d = global.getLocalProperty("d").getValue(max, max);
//		assertEquals(a, d);
	}
	
	
	
	
	
	
	
	private void createUserCode()
	{
		_userCode += "function fn0(){};                                      \n"; //$NON-NLS-1$
		_userCode += "fn0.prototype.baseProp = 10;                           \n"; //$NON-NLS-1$
		_userCode += "fn0.staticProp = 77;                                   \n"; //$NON-NLS-1$
		_userCode += "                                                       \n"; //$NON-NLS-1$
		_userCode += "var fn1 = function(a,b,c)                              \n"; //$NON-NLS-1$
		_userCode += "{                                                      \n"; //$NON-NLS-1$
		_userCode += "   this.prop0 = 10;                                    \n"; //$NON-NLS-1$
		_userCode += "   this.nestedFn = function(){};                       \n"; //$NON-NLS-1$
		_userCode += "}                                                      \n"; //$NON-NLS-1$
		_userCode += "fn1.prototype = new fn0();                             \n"; //$NON-NLS-1$
		_userCode += "                                                       \n"; //$NON-NLS-1$
		_userCode += "fn1.prototype.meth0 = function()                       \n"; //$NON-NLS-1$
		_userCode += "{                                                      \n"; //$NON-NLS-1$
		_userCode += "    this.methProp = 5;                                 \n"; //$NON-NLS-1$
		_userCode += "}                                                      \n"; //$NON-NLS-1$
		_userCode += "var prot = fn1.prototype;                              \n"; //$NON-NLS-1$
		_userCode += "prot.prop1 = \"test\";                                 \n"; //$NON-NLS-1$
		_userCode += "                                                       \n"; //$NON-NLS-1$
		_userCode += "                                                       \n"; //$NON-NLS-1$
		_userCode += "                                                       \n"; //$NON-NLS-1$
		_userCode += "var fn0Inst = new fn0();                               \n"; //$NON-NLS-1$
		_userCode += "var fn1Inst = new fn1();                               \n"; //$NON-NLS-1$
		_userCode += "                                                       \n"; //$NON-NLS-1$
		_userCode += "                                                       \n"; //$NON-NLS-1$
		_userCode += "                                                       \n"; //$NON-NLS-1$
		_userCode += "                                                       \n"; //$NON-NLS-1$
		_userCode += "                                                       \n"; //$NON-NLS-1$
		_userCode += "                                                       \n"; //$NON-NLS-1$
		_userCode += "                                                       \n"; //$NON-NLS-1$
		_userCode += "var a = 5;                                             \n"; //$NON-NLS-1$
		_userCode += "var b = a;                                             \n"; //$NON-NLS-1$
		_userCode += "var c = b;                                             \n"; //$NON-NLS-1$
		_userCode += "var d = 5 + c;                                         \n"; //$NON-NLS-1$
		_userCode += "                                                       \n"; //$NON-NLS-1$
		_userCode += "                                                       \n"; //$NON-NLS-1$
		_userCode += "                                                       \n"; //$NON-NLS-1$
		_userCode += "                                                       \n"; //$NON-NLS-1$
		_userCode += "                                                       \n"; //$NON-NLS-1$
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception 
	{
		super.tearDown();
	}
	
	/**
	 * testNestedFunctions
	 *
	 */
	public void testNestedFunctions()
	{
		
	}
}
