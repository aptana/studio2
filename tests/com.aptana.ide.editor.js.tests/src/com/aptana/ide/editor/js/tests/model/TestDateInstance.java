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

/**
 * @author Kevin Lindsey
 */
public class TestDateInstance extends TestModelBase
{
	private static final String TARGET = "Date";
	
	/**
	 * testExists
	 */
	public void testExists()
	{
		IObject object = this.getInstance(TARGET);

		assertNotNull(object);
	}

	/**
	 * testClassName
	 */
	public void testClassName()
	{
		IObject object = this.getInstance(TARGET);
		String name = object.getClassName();

		assertEquals(TARGET, name);
	}

	/**
	 * testPrivatePrototype
	 */
	public void testPrivatePrototype()
	{
		IObject privatePrototype = this.getInstance(TARGET).getPrototype();
		IObject functionPrototype = this.getPublicPrototype(TARGET);

		assertSame(functionPrototype, privatePrototype);
	}
}
