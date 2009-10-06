/**
 * Copyright (c) 2005-2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */package com.aptana.ide.editor.js.tests.outline;

/**
 * @author Kevin Lindsey
 */
public class TestInheritanceItems extends TestOutlineItemBase
{
	/**
	 * testInheritance
	 * 
	 * @param source
	 */
	private void testInheritance(String source)
	{
		this.testItem(
			source,
			"/outline/object-literal",
			"Subclass",
			1
		);
		this.testItem(
			source,
			"/outline/object-literal/boolean",
			"a"
		);
	}
	
	/**
	 * testDojoLangExtend
	 */
	public void testDojoLangExtend()
	{
		this.testInheritance("dojo.lang.extend(Subclass, { a: true });");
	}
	
	/**
	 * testMochiKitBaseUpdate
	 */
	public void testMochiKitBaseUpdate()
	{
		this.testInheritance("MochiKit.Base.update(Subclass, { a: true });");
	}
	
	/**
	 * testObjectExtend
	 */
	public void testObjectExtend()
	{
		this.testInheritance("Object.extend(Subclass, { a: true });");
	}
	
	/**
	 * testExtExtend
	 */
	public void testExtExtend()
	{
		this.testInheritance("Ext.extend(Subclass, Superclass, { a: true });");
	}
	
	/**
	 * testQxClassDefine
	 */
	public void testQxClassDefine()
	{
		this.testInheritance("qx.Class.define(Subclass, { a: true });");
	}
	
	/**
	 * testQxInterfaceDefine
	 */
	public void testQxInterfaceDefine()
	{
		this.testInheritance("qx.Interface.define(Subclass, { a: true });");
	}
	
	/**
	 * testQxThemeDefine
	 */
	public void testQxThemeDefine()
	{
		this.testInheritance("qx.Theme.define(Subclass, { a: true });");
	}
	
	/**
	 * testQxMixinDefine
	 */
	public void testQxMixinDefine()
	{
		this.testInheritance("qx.Mixin.define(Subclass, { a: true });");
	}
}
