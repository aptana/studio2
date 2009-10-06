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

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.js.JSFileServiceFactory;
import com.aptana.ide.editor.js.JSLanguageEnvironment;
import com.aptana.ide.editor.js.JSOffsetMapper;
import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.js.runtime.Environment;
import com.aptana.ide.editor.js.runtime.IObject;
import com.aptana.ide.editor.js.runtime.JSScope;
import com.aptana.ide.editor.js.runtime.JSUndefined;
import com.aptana.ide.editor.scriptdoc.parsing.FunctionDocumentation;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.junit.TestUtils;
import com.aptana.ide.editors.unified.FileService;
import com.aptana.ide.editors.unified.FileSourceProvider;
import com.aptana.ide.editors.unified.IFileLanguageService;
import com.aptana.ide.editors.unified.ILanguageEnvironment;
import com.aptana.ide.metadata.IDocumentation;

/**
 * 
 */
public class JSCoreTest extends TestCase
{
	private Environment _jsEnv;
	private JSScope _global;
	private JSUndefined _undef;
	private JSOffsetMapper _mapper;
	private int _max = Integer.MAX_VALUE;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception 
	{
		super.setUp();
		ILanguageEnvironment env = JSLanguageEnvironment.getInstance();
		env.loadEnvironment();
		
		try
		{
			Thread.sleep(2000);
		}
		catch (InterruptedException e)
		{
			IdeLog.logInfo(JSPlugin.getDefault(), "setUp failed", e); //$NON-NLS-1$
		}
		
		_jsEnv = (Environment)env.getRuntimeEnvironment();		
		_global = _jsEnv.getGlobal();
		_undef = JSUndefined.getSingletonInstance();		

		File temp = TestUtils.createFileFromString("test", ".js", StringUtils.EMPTY); //$NON-NLS-1$ //$NON-NLS-2$
		FileSourceProvider fsp = new FileSourceProvider(temp);
		new UnifiedEditorsPlugin();
		FileService fileService = JSFileServiceFactory.getInstance().createFileService(fsp);
		IFileLanguageService fls = fileService.getLanguageService(JSMimeType.MimeType);
		_mapper = (JSOffsetMapper)fls.getOffsetMapper();
		
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception 
	{
		super.tearDown();
	}

	/**
	 * testJSCore
	 */
	public void testJSCore()
	{
		IObject obj = _global.getLocalProperty("Object").getValue(_max, _max); //$NON-NLS-1$
		IObject objProt = obj.getLocalProperty("prototype").getValue(_max, _max); //$NON-NLS-1$
		IObject num = _global.getLocalProperty("Number").getValue(_max, _max); //$NON-NLS-1$
		IObject numProt = num.getLocalProperty("prototype").getValue(_max, _max); //$NON-NLS-1$
		
		// test Math
		IObject math = _global.getLocalProperty("Math").getValue(_max, _max); //$NON-NLS-1$
		assertNotSame(math, _undef);
		
		IObject sin = math.getLocalProperty("sin").getValue(_max, _max); //$NON-NLS-1$
		assertNotSame(sin, _undef);

		// check return type of Math.sin is Number.prototype
		IObject sinRet = _mapper.lookupReturnTypeFromNameHash("Math.sin()", _global); //$NON-NLS-1$
		assertEquals(sinRet.getDocumentation(), numProt.getDocumentation());
		assertNotSame(sinRet.getDocumentation(), objProt.getDocumentation());
		
		IDocumentation sinDoc = sin.getDocumentation();
		assertNotNull(sinDoc);		
		assertTrue(sinDoc.getDescription().length() > 0);
		
		// test Function
		IObject fn = _global.getLocalProperty("Function").getValue(_max, _max); //$NON-NLS-1$
		assertNotSame(fn, _undef);

		IObject fnProt = fn.getLocalProperty("prototype").getValue(_max, _max); //$NON-NLS-1$
		assertNotSame(fnProt, _undef);
		
		// Function.prototype.call
		IObject fnCall = fnProt.getLocalProperty("call").getValue(_max, _max); //$NON-NLS-1$
		assertNotSame(fnCall, _undef);
		FunctionDocumentation fnCallDoc = (FunctionDocumentation)fnCall.getDocumentation();
		assertNotNull(fnCallDoc);		
		assertTrue(fnCallDoc.getDescription().length() > 0);
		assertTrue(fnCallDoc.getParams().length == 2);
		
		// test inheritance
		IObject objWatch = fnProt.getProperty("watch").getValue(_max, _max);// prop on object //$NON-NLS-1$
		assertNotSame(objWatch, _undef);
	}
}















