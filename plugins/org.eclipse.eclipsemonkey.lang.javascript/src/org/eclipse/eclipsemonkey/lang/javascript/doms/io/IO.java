/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.eclipse.eclipsemonkey.lang.javascript.doms.io;

/**
 * @author Paul Colton
 *
 */
public class IO {
	
	private static IO _io = new IO();

	/**
	 * 
	 */
	private IO() {
	}

	/**
	 * @return IO
	 */
	public static IO getIO()
	{
		return _io;
	}
}
