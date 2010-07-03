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
public class TestRegExpFromInvocation extends TestRegExpInstance
{
	/**
	 * @see com.aptana.ide.editor.js.tests.model.TestModelBase#getInstance(java.lang.String)
	 */
	protected IObject getInstance(String name)
	{
		return this.invoke(name);
	}
}
