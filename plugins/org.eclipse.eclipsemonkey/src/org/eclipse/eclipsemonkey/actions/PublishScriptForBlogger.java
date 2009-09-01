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

package org.eclipse.eclipsemonkey.actions;


/**
 * PublishScriptForBlogger
 */
public class PublishScriptForBlogger extends PublishScript {
	/**
	 * @see org.eclipse.eclipsemonkey.actions.PublishScript#decorateText(java.lang.String)
	 */
	protected String decorateText(String contents) {
		return "<pre>\n" + super.decorateText(contents) //$NON-NLS-1$
				+ "\n</pre>\n"; //$NON-NLS-1$
	}
}
