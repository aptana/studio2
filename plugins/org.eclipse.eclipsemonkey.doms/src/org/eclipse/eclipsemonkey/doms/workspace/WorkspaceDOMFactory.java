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

package org.eclipse.eclipsemonkey.doms.workspace;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.eclipsemonkey.dom.IMonkeyDOMFactory;

/**
 * WorkspaceDOMFactory
 */
public class WorkspaceDOMFactory implements IMonkeyDOMFactory {

	/**
	 * @see org.eclipse.eclipsemonkey.dom.IMonkeyDOMFactory#getDOMroot()
	 */
	public Object getDOMroot() {
		return ResourcesPlugin.getWorkspace();
	}
}
