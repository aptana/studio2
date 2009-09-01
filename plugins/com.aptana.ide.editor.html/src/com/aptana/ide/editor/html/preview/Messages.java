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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.editor.html.preview;

import org.eclipse.osgi.util.NLS;

/**
 * @author Pavel Petrochenko
 *
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.editor.html.preview.messages"; //$NON-NLS-1$
	public static String DefaultPreviewConfigurationPage_ERR_Encode;
    public static String HTMLPreviewConfiguration_ERR_Exception;
    public static String PreviewConfigurationPage_LBL_BrowserGroup;
    public static String PreviewConfigurationPage_LBL_Cancel;
    public static String PreviewConfigurationPage_LBL_OptionsGroup;
    public static String PreviewConfigurationPage_LBL_PreviewName;
    public static String PreviewConfigurationPage_LBL_Save;
    public static String PreviewConfigurationPage_LBL_Status_Name;
    public static String PreviewConfigurationPage_LBL_Status_NameExists;
    public static String PreviewConfigurationPage_LBL_Status_URL;
    public static String PreviewConfigurationPage_TTP_Cancel;
    public static String PreviewConfigurationPage_TTP_Save;
    public static String PreviewConfigurations_ERR_CreateConfig;
    public static String PreviewConfigurations_ERR_WrongFormat;
    /**
	 * 
	 */
	public static String PreviewServer_CAN_NOT_HAVE_MODULES;
	/**
	 * 
	 */
	public static String PreviewServer_CAN_NOT_START;
	/**
	 * 
	 */
	public static String PreviewServer_CAN_NOT_STOP;
	/**
	 * 
	 */
	public static String PreviewServer_DESCRIPTION;
	/**
	 * 
	 */
	public static String PreviewServer_EXCEPTION_WHILE_RESTART;
	/**
	 * 
	 */
	public static String PreviewServerProvider_PreviewServerName;
    public static String PreviewTypeSelectionBlock_LBL_AddConfig;
    public static String PreviewTypeSelectionBlock_LBL_AddServer;
    public static String PreviewTypeSelectionBlock_LBL_Append;
    public static String PreviewTypeSelectionBlock_LBL_AppendPath;
    public static String PreviewTypeSelectionBlock_LBL_TypeGroup;
    public static String PreviewTypeSelectionBlock_LBL_URL;
    public static String PreviewTypeSelectionBlock_LBL_UseAbsURL;
    public static String PreviewTypeSelectionBlock_LBL_UseFileURL;
    public static String PreviewTypeSelectionBlock_LBL_UseRunConfig;
    public static String PreviewTypeSelectionBlock_LBL_UseServer;
    public static String PreviewWizardPage_LBL_DefineSettings;
    public static String PreviewWizardPage_LBL_PreferencesLink;
    public static String PreviewWizardPage_LBL_SampleURL;
	public static String PreviewWizardPageFactory_Description;
    public static String PreviewWizardPageFactory_TTL_HTMLPreview;
    static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
