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
package com.aptana.ide.editors.actions;

import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * 
 * @author Ingo Muschenetz
 *
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.editors.actions.messages"; //$NON-NLS-1$
	private static ResourceBundle resourceBundle= ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages()
	{
	}
	
	/**
	 * Gets the resourse bundle
	 * @return - resource bundle
	 */
	public static ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	/**
	 * GotoNextMemberAction_TITLE
	 */
	public static String GotoNextMemberAction_TITLE;
	/**
	 * GotoPreviousMemberAction_TITLE
	 */
	public static String GotoPreviousMemberAction_TITLE;
	/**
	 * OpenDeclarationAction_OpenDeclaration
	 */
	public static String OpenDeclarationAction_OpenDeclaration;
    public static String QuickOutlineAction_0;
	
	/**
	 * ShowErrors_TEXT
	 */
	public static String ShowErrors_TEXT;
	
	/**
	 * ShowInfos_TEXT
	 */
	public static String ShowInfos_TEXT;
	public static String ShowPianoKeys_ERR_ErrorSettingPianoKeysPreferences;
	
	/**
	 * ShowWarnings_TEXT
	 */
	public static String ShowWarnings_TEXT;
	public static String ShowWhitespace_ERR_ErrorSettingWhitespacePreferences;
}
