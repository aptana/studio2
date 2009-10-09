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

import junit.framework.TestCase;

import com.aptana.ide.intro.preferences.FeatureDescriptor;

/**
 * FeatureDescriptorTest
 * @author ingo
 *
 */
public class FeatureDescriptorTest extends TestCase
{

	public void testHashCode()
	{
		FeatureDescriptor pro = new FeatureDescriptor("a", "b", "c", null);
		assertEquals(pro.getId().hashCode(), pro.hashCode());
	}

	public void testFeatureDescriptor()
	{
		String[] conflicts = new String[] {"d"};
		FeatureDescriptor pro = new FeatureDescriptor("a", "b", "c", conflicts);
		assertEquals("a", pro.getId());
		assertEquals("b", pro.getName());
		assertEquals("c", pro.getURL());
		assertEquals(conflicts, pro.getConflicts());
	}

	public void testEqualsObject()
	{
		// equality is based on ID
		FeatureDescriptor proa = new FeatureDescriptor("a", "b", "c", new String[] {"d"});
		FeatureDescriptor prob = new FeatureDescriptor("a", "bc", "cd", new String[] {"e"});
		FeatureDescriptor proc = new FeatureDescriptor("ab", "bc", "cd", new String[] {"f"});
		assertEquals(proa, prob);
		assertNotSame(proa, proc);
	}

	public void testToString()
	{
		FeatureDescriptor proa = new FeatureDescriptor("a", "b", "c", new String[] {"d"});
		assertEquals("a, b(c)", proa.toString());
	}

}
