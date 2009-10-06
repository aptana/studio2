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
package com.aptana.ide.intro.tests.preferences;

import com.aptana.ide.intro.preferences.ProFeatureDescriptor;

import junit.framework.TestCase;

/**
 * ProFeatureDescriptorTest
 * @author ingo
 *
 */
public class ProFeatureDescriptorTest extends TestCase
{

	/**
	 * testProFeatureDescriptor
	 */
	public void testProFeatureDescriptor()
	{
		String[] conflicts = new String[] {"d"};
		ProFeatureDescriptor pro = new ProFeatureDescriptor("a", "b", "c", conflicts);
		assertEquals("a", pro.getId());
		assertEquals("b", pro.getName());
		assertEquals("c", pro.getURL());
		assertEquals(conflicts, pro.getConflicts());
	}

}
