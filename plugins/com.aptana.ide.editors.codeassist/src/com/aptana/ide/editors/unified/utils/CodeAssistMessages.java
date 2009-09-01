/**
 * Copyright (c) 2005-2009 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.editors.unified.utils;

import org.eclipse.osgi.util.NLS;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public final class CodeAssistMessages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.editors.unified.utils.codeassistmessages"; //$NON-NLS-1$
	
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, CodeAssistMessages.class);
	}

	private CodeAssistMessages()
	{
	}

	/**
	 * IntHashMap_IllegalCapacity
	 */
	public static String IntHashMap_IllegalCapacity;

	/**
	 * IntHashMap_IllegalLoad
	 */
	public static String IntHashMap_IllegalLoad;

}
