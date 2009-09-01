/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.editors.views.actions;

import org.eclipse.osgi.util.NLS;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.editors.views.actions.messages"; //$NON-NLS-1$

	/**
	 * ActionSet_ActionSetActions
	 */
	public static String ActionSet_ActionSetActions;

	/**
	 * ActionsView_ActionsView
	 */
	public static String ActionsView_ActionsView;

	/**
	 * ActionsView_AddCurrentFile
	 */
	public static String ActionsView_AddCurrentFile;

	/**
	 * ActionsView_AddCurrentFileToolTip
	 */
	public static String ActionsView_AddCurrentFileToolTip;

	/**
	 * ActionsView_AddFile
	 */
	public static String ActionsView_AddFile;

	/**
	 * ActionsView_AllFiles
	 */
	public static String ActionsView_AllFiles;

	/**
	 * ActionsView_Arial
	 */
	public static String ActionsView_Arial;

	/**
	 * ActionsView_EditAction
	 */
	public static String ActionsView_EditAction;

	/**
	 * ActionsView_ErrorOpeningEditor
	 */
	public static String ActionsView_ErrorOpeningEditor;

	/**
	 * ActionsView_ErrorOpeningFile
	 */
	public static String ActionsView_ErrorOpeningFile;

	/**
	 * ActionsView_Execute
	 */
	public static String ActionsView_Execute;

	/**
	 * ActionsView_ExecuteToolTip
	 */
	public static String ActionsView_ExecuteToolTip;

	/**
	 * ActionsView_JavaScriptFiles
	 */
	public static String ActionsView_JavaScriptFiles;

	/**
	 * ActionsView_NewActionSet
	 */
	public static String ActionsView_NewActionSet;

	/**
	 * ActionsView_NewActionSetName
	 */
	public static String ActionsView_NewActionSetName;

	/**
	 * ActionsView_PleaseEnterActionSetName
	 */
	public static String ActionsView_PleaseEnterActionSetName;

	/**
	 * ActionsView_ReloadAction
	 */
	public static String ActionsView_ReloadAction;

	/**
	 * ActionsView_RemoveFile
	 */
	public static String ActionsView_RemoveFile;

	/**
	 * ActionsView_ToggleExecutable
	 */
	public static String ActionsView_ToggleExecutable;

	/**
	 * ActionsView_ToggleToolTip
	 */
	public static String ActionsView_ToggleToolTip;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
