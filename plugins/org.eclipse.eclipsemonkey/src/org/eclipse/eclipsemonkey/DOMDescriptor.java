/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.eclipse.eclipsemonkey;

/**
 * @author Paul Colton
 *
 */
public class DOMDescriptor {
	
	/**
	 * url
	 */
	public String url;

	/**
	 * plugin_name
	 */
	public String plugin_name;

	/**
	 * @param u
	 * @param n
	 */
	public DOMDescriptor(String u, String n) {
		url = u;
		plugin_name = n;
	}
}