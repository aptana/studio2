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

/**
 * @author Kevin Lindsey
 */
public class TestMathInstance extends TestModelBase
{
	private static final String TARGET = "Math";
	
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

		assertEquals(TARGET, name);
	}

	/**
	 * testE
	 */
	public void testE()
	{
		this.testType(this.getGlobalProperty(TARGET), "E", JSNumber.class);
	}
	
	/**
	 * testLN10
	 */
	public void testLN10()
	{
		this.testType(this.getGlobalProperty(TARGET), "LN10", JSNumber.class);
	}
	
	/**
	 * testLN2
	 */
	public void testLN2()
	{
		this.testType(this.getGlobalProperty(TARGET), "LN2", JSNumber.class);
	}
	
	/**
	 * testLOG2EE
	 */
	public void testLOG2E()
	{
		this.testType(this.getGlobalProperty(TARGET), "LOG2E", JSNumber.class);
	}
	
	/**
	 * testLOG10E
	 */
	public void testLOG10E()
	{
		this.testType(this.getGlobalProperty(TARGET), "LOG10E", JSNumber.class);
	}
	
	/**
	 * testPI
	 */
	public void testPI()
	{
		this.testType(this.getGlobalProperty(TARGET), "PI", JSNumber.class);
	}
	
	/**
	 * testSQRT1_2
	 */
	public void testSQRT1_2()
	{
		this.testType(this.getGlobalProperty(TARGET), "SQRT1_2", JSNumber.class);
	}
	
	/**
	 * testSQRT2
	 */
	public void testSQRT2()
	{
		this.testType(this.getGlobalProperty(TARGET), "SQRT2", JSNumber.class);
	}
	
	/**
	 * testAbs
	 */
	public void testAbs()
	{
		this.testType(this.getGlobalProperty(TARGET), "abs", JSFunction.class);
	}
	
	/**
	 * testAcos
	 */
	public void testAcos()
	{
		this.testType(this.getGlobalProperty(TARGET), "acos", JSFunction.class);
	}
	
	/**
	 * testAsin
	 */
	public void testAsin()
	{
		this.testType(this.getGlobalProperty(TARGET), "asin", JSFunction.class);
	}
	
	/**
	 * testAtan
	 */
	public void testAtan()
	{
		this.testType(this.getGlobalProperty(TARGET), "atan", JSFunction.class);
	}
	
	/**
	 * testAtan2
	 */
	public void testAtan2()
	{
		this.testType(this.getGlobalProperty(TARGET), "atan2", JSFunction.class);
	}
	
	/**
	 * testCeil
	 */
	public void testCeil()
	{
		this.testType(this.getGlobalProperty(TARGET), "ceil", JSFunction.class);
	}
	
	/**
	 * testCos
	 */
	public void testCos()
	{
		this.testType(this.getGlobalProperty(TARGET), "cos", JSFunction.class);
	}
	
	/**
	 * testExp
	 */
	public void testExp()
	{
		this.testType(this.getGlobalProperty(TARGET), "exp", JSFunction.class);
	}
	
	/**
	 * testFloor
	 */
	public void testFloor()
	{
		this.testType(this.getGlobalProperty(TARGET), "floor", JSFunction.class);
	}
	
	/**
	 * testLog
	 */
	public void testLog()
	{
		this.testType(this.getGlobalProperty(TARGET), "log", JSFunction.class);
	}
	
	/**
	 * testMax
	 */
	public void testMax()
	{
		this.testType(this.getGlobalProperty(TARGET), "max", JSFunction.class);
	}
	
	/**
	 * testMin
	 */
	public void testMin()
	{
		this.testType(this.getGlobalProperty(TARGET), "min", JSFunction.class);
	}
	
	/**
	 * testPow
	 */
	public void testPow()
	{
		this.testType(this.getGlobalProperty(TARGET), "pow", JSFunction.class);
	}
	
	/**
	 * testRandom
	 */
	public void testRandom()
	{
		this.testType(this.getGlobalProperty(TARGET), "random", JSFunction.class);
	}
	
	/**
	 * testRound
	 */
	public void testRound()
	{
		this.testType(this.getGlobalProperty(TARGET), "round", JSFunction.class);
	}
	
	/**
	 * testSin
	 */
	public void testSin()
	{
		this.testType(this.getGlobalProperty(TARGET), "sin", JSFunction.class);
	}
	
	/**
	 * testSqrt
	 */
	public void testSqrt()
	{
		this.testType(this.getGlobalProperty(TARGET), "sqrt", JSFunction.class);
	}
	
	/**
	 * testTan
	 */
	public void testTan()
	{
		this.testType(this.getGlobalProperty(TARGET), "tan", JSFunction.class);
	}

	/**
	 * testPrivatePrototype
	 */
	public void testPrivatePrototype()
	{
		IObject privatePrototype = this.getPrivatePrototype(TARGET);
		IObject functionPrototype = this.getPublicPrototype("Object");

		assertSame(functionPrototype, privatePrototype);
	}
}
