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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PublishScriptForWiki
 */
public class PublishScriptForWiki extends PublishScript {

	/**
	 * @see org.eclipse.eclipsemonkey.actions.PublishScript#decorateText(java.lang.String)
	 */
	protected String decorateText(String contents) {
		String result = "\n" + super.decorateText(contents); //$NON-NLS-1$
		Pattern pattern = Pattern.compile("\\n"); //$NON-NLS-1$
		Matcher matcher = pattern.matcher(result);
		result = matcher.replaceAll("\n  "); //$NON-NLS-1$
		result = result + "\n"; //$NON-NLS-1$
		return result;
	}
}
