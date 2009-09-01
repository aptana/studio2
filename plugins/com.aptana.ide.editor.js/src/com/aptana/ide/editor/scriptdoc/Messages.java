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
package com.aptana.ide.editor.scriptdoc;

import org.eclipse.osgi.util.NLS;

/**
 * @author Robin
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.editor.scriptdoc.messages"; //$NON-NLS-1$

	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * DocumentationManager_ErrorGettingDocParser
	 */
	public static String DocumentationManager_ErrorGettingDocParser;

	/**
	 * ScriptDocHelper_Supported1
	 */
	public static String ScriptDocHelper_Supported1;

	/**
	 * ScriptDocHelper_None1
	 */
	public static String ScriptDocHelper_None1;

	/**
	 * ScriptDocHelper_JSKeyword
	 */
	public static String ScriptDocHelper_JSKeyword;

	/**
	 * ScriptDocHelper_JSLiteral
	 */
	public static String ScriptDocHelper_JSLiteral;

	/**
	 * ScriptDocHelper_None2
	 */
	public static String ScriptDocHelper_None2;

	/**
	 * ScriptDocHelper_Supported2
	 */
	public static String ScriptDocHelper_Supported2;

	/**
	 * ScriptDocHelper_Supported3
	 */
	public static String ScriptDocHelper_Supported3;


	/**
	 * ScriptDocFileLanguageService_Author
	 */
	public static String ScriptDocFileLanguageService_Author;

	/**
	 * ScriptDocFileLanguageService_ClassDescription
	 */
	public static String ScriptDocFileLanguageService_ClassDescription;

	/**
	 * ScriptDocFileLanguageService_Constructor
	 */
	public static String ScriptDocFileLanguageService_Constructor;

	/**
	 * ScriptDocFileLanguageService_Deprecated
	 */
	public static String ScriptDocFileLanguageService_Deprecated;

	/**
	 * ScriptDocFileLanguageService_Exception
	 */
	public static String ScriptDocFileLanguageService_Exception;

	/**
	 * ScriptDocFileLanguageService_Internal
	 */
	public static String ScriptDocFileLanguageService_Internal;

	/**
	 * ScriptDocFileLanguageService_MemberOf
	 */
	public static String ScriptDocFileLanguageService_MemberOf;

	/**
	 * ScriptDocFileLanguageService_Method
	 */
	public static String ScriptDocFileLanguageService_Method;

	/**
	 * ScriptDocFileLanguageService_Native
	 */
	public static String ScriptDocFileLanguageService_Native;

	/**
	 * ScriptDocFileLanguageService_Param
	 */
	public static String ScriptDocFileLanguageService_Param;

	/**
	 * ScriptDocFileLanguageService_Private
	 */
	public static String ScriptDocFileLanguageService_Private;

	/**
	 * ScriptDocFileLanguageService_ProjectDescription
	 */
	public static String ScriptDocFileLanguageService_ProjectDescription;

	/**
	 * ScriptDocFileLanguageService_Property
	 */
	public static String ScriptDocFileLanguageService_Property;

	/**
	 * ScriptDocFileLanguageService_Return
	 */
	public static String ScriptDocFileLanguageService_Return;

	/**
	 * ScriptDocFileLanguageService_See
	 */
	public static String ScriptDocFileLanguageService_See;

	/**
	 * ScriptDocFileLanguageService_Since
	 */
	public static String ScriptDocFileLanguageService_Since;

	/**
	 * ScriptDocFileLanguageService_Type
	 */
	public static String ScriptDocFileLanguageService_Type;

	/**
	 * ScriptDocFileLanguageService_Version
	 */
	public static String ScriptDocFileLanguageService_Version;

	/**
	 * ScriptDocFileLanguageService_NoScriptDocLanguageService
	 */
	public static String ScriptDocFileLanguageService_NoScriptDocLanguageService;

	/**
	 * DocumentationManager_UnableToCreateDocsParser
	 */
	public static String DocumentationManager_UnableToCreateDocsParser;

	/**
	 * ScriptDocFileServiceFactory_ScriptDocFileServiceFactoryInstallationFailed
	 */
	public static String ScriptDocFileServiceFactory_ScriptDocFileServiceFactoryInstallationFailed;
}
