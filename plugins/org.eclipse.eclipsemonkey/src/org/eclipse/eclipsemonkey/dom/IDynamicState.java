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

package org.eclipse.eclipsemonkey.dom;

/**
 * IDynamicState
 */
public interface IDynamicState {

	/**
	 * @param id
	 */
	public void begin(Object id);
	
	/**
	 * @param id
	 */
	public void end(Object id);
	
	/**
	 * @param name
	 * @param value
	 */
	public void set(String name, Object value);

	/**
	 * @param name
	 * @return Object
	 */
	public Object get(String name);
}
