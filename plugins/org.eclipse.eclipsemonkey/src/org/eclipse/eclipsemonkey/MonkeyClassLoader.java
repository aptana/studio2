/*******************************************************************************
 * Copyright (c) 2006 Eclipse Foundation
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bjorn Freeman-Benson - initial implementation
 *     Ward Cunningham - initial implementation
 *******************************************************************************/

package org.eclipse.eclipsemonkey;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * MonkeyClassLoader
 */
public class MonkeyClassLoader extends ClassLoader {
	List loaders = new ArrayList();

	/**
	 * @param loader
	 */
	public void add(ClassLoader loader) {
		loaders.add(loader);
	}

	/**
	 * @see java.lang.ClassLoader#loadClass(java.lang.String)
	 */
	public Class loadClass(String name) throws ClassNotFoundException {
		Iterator iter = loaders.iterator();
		while (iter.hasNext()) {
			ClassLoader loader = (ClassLoader) iter.next();
			try {
				// ((EclipseClassLoader)loader).getDelegate();
				Class clz = loader.loadClass(name);
				if (clz != null) {
					return clz;
				}
			} catch (ClassNotFoundException x) {
				// continue the loop
			}
		}
		throw new ClassNotFoundException(name);
	}
}
