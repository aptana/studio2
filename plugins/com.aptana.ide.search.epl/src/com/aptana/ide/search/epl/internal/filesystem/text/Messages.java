/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ide.search.epl.internal.filesystem.text;

import org.eclipse.osgi.util.NLS;

/**
 * @author Pavel Petrochenko
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.search.epl.internal.filesystem.text.messages"; //$NON-NLS-1$
	/**
	 * START_SEARCH
	 */
	public static String START_SEARCH ;
	/**
	 * PERFORM_SEARCH
	 */
	public static String PERFORM_SEARCH ;
	
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(Messages.BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
