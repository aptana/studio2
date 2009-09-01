/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.eclipse.eclipsemonkey.lang.javascript.doms.views;

import org.eclipse.osgi.util.NLS;

/**
 * @author Kevin Lindsey
 *
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "org.eclipse.dash.doms.views.messages"; //$NON-NLS-1$

	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	/**
	 * ActionsView_Error
	 */
	public static String ActionsView_Error;
	
	/**
	 * ActionsView_Unrecognized_Event_Type
	 */
	public static String ActionsView_Unrecognized_Event_Type;
	
	/**
	 * OutlineView_Error
	 */
	public static String OutlineView_Error;
	
	/**
	 * ProblemsView_Error
	 */
	public static String ProblemsView_Error;
	
	/**
	 * ScriptableView_Error
	 */
	public static String ScriptableView_Error;
	
	/**
	 * View_Scope_Not_Defined
	 */
	public static String View_Scope_Not_Defined;
}
