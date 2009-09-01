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
package com.aptana.ide.editor.js;

import org.eclipse.osgi.util.NLS;

/**
 * @author Robin
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.editor.js.messages"; //$NON-NLS-1$

	/**
	 * Messages
	 */
	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String JSEditor_CollapseComments;

	public static String JSEditor_ExpandComments;

	public static String JSFileLanguageService_0_identifier;

	public static String JSFileLanguageService_0_keyword;

	public static String JSFileLanguageService_11;

	public static String JSFileLanguageService_HTML_dom_0_reference;

	public static String JSFileLanguageService_HTML_dom_1_2_reference;

	public static String JSFileLanguageService_Javascript_core_reference;

	public static String JSFileLanguageService_Javascript_editor;

	/**
	 * JSFileLanguageService_NoInfoAvailable
	 */
	public static String JSFileLanguageService_NoInfoAvailable;

	/**
	 * JSErrorManager_JSLintUndefined
	 */
	public static String JSErrorManager_JSLintUndefined;

	/**
	 * JSErrorManager_ParseForErrorsFailed
	 */
	public static String JSErrorManager_ParseForErrorsFailed;

	/**
	 * JSErrorManager_JSLintFailed
	 */
	public static String JSErrorManager_JSLintFailed;

	/**
	 * JSErrorManager_GetResourceTextFailed
	 */
	public static String JSErrorManager_GetResourceTextFailed;

	public static String JSFileServiceFactory_DBG_Parser_for_mime_type_not_registered;

	/**
	 * JSFileServiceFactory_JSFileServiceFactoryInitializationFailed
	 */
	public static String JSFileServiceFactory_JSFileServiceFactoryInitializationFailed;

	/**
	 * JSLanguageEnvironment_FileServiceNull
	 */
	public static String JSLanguageEnvironment_FileServiceNull;

	public static String JSLanguageEnvironment_ERR_Loading_contributed_javascript_file;

	public static String JSLanguageEnvironment_ERR_Unable_load_javascript_env_resource;

	/**
	 * JSLanguageEnvironment_ErrorLoadingEnvironment
	 */
	public static String JSLanguageEnvironment_ErrorLoadingEnvironment;
	
	public static String JSLanguageEnvironment_INF_Default_loaded_environments;

	public static String JSLanguageEnvironment_INF_Disabled_environments;

	public static String JSLanguageEnvironment_INF_Enabled_environments;

	public static String JSLanguageEnvironment_INF_Loaded_environment;

	public static String JSLanguageEnvironment_INF_Resulting_set;

	/**
	 * JSDocumentProvider_CantCreateFileInfo
	 */
	public static String JSDocumentProvider_CantCreateFileInfo;
	
	/**
	 * JSDocumentProvider_ErrorDisconnectingDocumentProvider
	 */
	public static String JSDocumentProvider_ErrorDisconnectingDocumentProvider;

	/**
	 * JSEditor_JSFileInfo_Not_Defined
	 */
	public static String JSEditor_JSFileInfo_Not_Defined;

	/**
	 * JSEditor_Provider_Not_Defined
	 */
	public static String JSEditor_Provider_Not_Defined;

	/**
	 * JSEditor_Document_Provider_Not_Defined
	 */
	public static String JSEditor_Document_Provider_Not_Defined;
}
