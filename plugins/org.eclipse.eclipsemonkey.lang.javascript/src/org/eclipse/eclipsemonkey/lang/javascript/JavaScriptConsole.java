/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.eclipse.eclipsemonkey.lang.javascript;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.MessageConsole;

/**
 * JavaScriptConsole
 */
public class JavaScriptConsole extends MessageConsole
{
	/*
	 * Constructors
	 */

	/**
	 * JavaScriptConsole
	 * 
	 * @param name
	 * @param imageDescriptor
	 */
	public JavaScriptConsole(String name, ImageDescriptor imageDescriptor)
	{
		super(name, imageDescriptor);
	}
}
