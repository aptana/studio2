/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.eclipse.eclipsemonkey.views.scriptsView;

/**
 * @author Paul Colton
 */
public class ScriptActionsViewEventTypes
{
	/**
	 * EXECUTE
	 */
	public static final int EXECUTE = 0;
	
	/**
	 * DROP
	 */
	public static final int DROP = 1;
	
	/**
	 * ADD
	 */
	public static final int ADD = 2;
	
	/**
	 * ADD_CURRENT_FILE
	 */
	public static final int ADD_CURRENT_FILE = 3;
	
	/**
	 * CREATE_ACTION_SET
	 */
	public static final int CREATE_ACTION_SET = 4;
	
	/**
	 * DELETE
	 */
	public static final int DELETE = 5;
	
	/**
	 * DELETE_ACTION_SET
	 */
	public static final int DELETE_ACTION_SET = 6;
	
	/**
	 * FILE_SAVED
	 */
	public static final int RELOAD = 7;
}
