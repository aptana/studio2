/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * 
 *******************************************************************************/
package com.aptana.ide.search.epl;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.search.internal.ui.SearchPlugin;

/**
 * @author Pavel Petrochenko
 */
public final class Compatibility
{
	
	private Compatibility(){
		
	}

	/**
	 * 
	 */
	public static final String ORG_ECLIPSE_SEARCH_LIMIT_TABLE_TO = "org.eclipse.search.limitTableTo"; //$NON-NLS-1$

	/**
	 * 
	 */
	public static final String ORG_ECLIPSE_SEARCH_LIMIT_TABLE = "org.eclipse.search.limitTable"; //$NON-NLS-1$

	/**
	 * @return
	 */
	public static int getTableLimit()
	{
		IPreferenceStore store = SearchPlugin.getDefault().getPreferenceStore();
		return store.getInt(Compatibility.ORG_ECLIPSE_SEARCH_LIMIT_TABLE_TO);
	}

	/**
	 * @return
	 */
	public static boolean isTableLimited()
	{
		IPreferenceStore store = SearchPlugin.getDefault().getPreferenceStore();
		return store.getBoolean(Compatibility.ORG_ECLIPSE_SEARCH_LIMIT_TABLE);
	}
}
