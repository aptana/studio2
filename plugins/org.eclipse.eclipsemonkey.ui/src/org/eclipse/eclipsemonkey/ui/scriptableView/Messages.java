/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.eclipse.eclipsemonkey.ui.scriptableView;

import org.eclipse.osgi.util.NLS;

/**
 * NLS
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "org.eclipse.eclipsemonkey.ui.scriptableView.messages";//$NON-NLS-1$

	private Messages()
	{
		// Do not instantiate
	}

	/**
	 * BrowserView_Home
	 */
	public static String BrowserView_Home;

	/**
	 * BrowserView_NavigateToNextTopic
	 */
	public static String BrowserView_NavigateToNextTopic;

	/**
	 * BrowserView_NavigateToPreviousTopic
	 */
	public static String BrowserView_NavigateToPreviousTopic;

	/**
	 * BrowserView_InvalidConfiguration
	 */
	public static String BrowserView_InvalidConfiguration;
	
	/**
	 * GenericScriptableView_UnableToSetUrlForViewTo
	 */
	public static String GenericScriptableView_UnableToSetUrlForViewTo;
	
	/**
	 * GenericScriptableView_SetUrlFailed
	 */
	public static String GenericScriptableView_SetUrlFailed;
	
	/**
	 * GenericScriptableView_PoweredByAptanaScriptingEngine
	 */
	public static String GenericScriptableView_PoweredByEclipseMonkey;

	static
	{
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}