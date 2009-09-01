/*******************************************************************************
 * Copyright (c) 2005, 2006 Eclipse Foundation
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bjorn Freeman-Benson - initial implementation
 *     Ward Cunningham - initial implementation
 *******************************************************************************/

package org.eclipse.eclipsemonkey.internal;

import junit.framework.TestCase;

/**
 * DynamicStateTests
 */
public class DynamicStateTests extends TestCase
{

	/**
	 * 
	 */
	public void test1()
	{
		DynamicState ds = new DynamicState();
		assertNull(ds.get("foo")); //$NON-NLS-1$
	}

	/**
	 * 
	 */
	public void test2()
	{
		DynamicState ds = new DynamicState();
		ds.set("foo", "three"); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("three", ds.get("foo")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * 
	 */
	public void test3()
	{
		DynamicState ds = new DynamicState();
		ds.set("foo", "three"); //$NON-NLS-1$ //$NON-NLS-2$
		ds.begin("bar"); //$NON-NLS-1$
		assertEquals("three", ds.get("foo")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * 
	 */
	public void test4()
	{
		DynamicState ds = new DynamicState();
		ds.begin("bar"); //$NON-NLS-1$
		ds.set("foo", "three"); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("three", ds.get("foo")); //$NON-NLS-1$ //$NON-NLS-2$
		ds.end("bar"); //$NON-NLS-1$
		assertNull(ds.get("foo")); //$NON-NLS-1$
	}

	/**
	 * 
	 */
	public void test5()
	{
		DynamicState ds = new DynamicState();
		ds.begin("bar"); //$NON-NLS-1$
		ds.set("foo", "three"); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("three", ds.get("foo")); //$NON-NLS-1$ //$NON-NLS-2$
		ds.end("noname"); //$NON-NLS-1$
		assertEquals("three", ds.get("foo")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * 
	 */
	public void test6()
	{
		DynamicState ds = new DynamicState();
		ds.begin("bar"); //$NON-NLS-1$
		ds.set("foo", "three"); //$NON-NLS-1$ //$NON-NLS-2$
		ds.begin("whiz"); //$NON-NLS-1$
		ds.set("foo", "four"); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("four", ds.get("foo")); //$NON-NLS-1$ //$NON-NLS-2$
		ds.end("bar"); //$NON-NLS-1$
		assertNull(ds.get("foo")); //$NON-NLS-1$
	}
}
