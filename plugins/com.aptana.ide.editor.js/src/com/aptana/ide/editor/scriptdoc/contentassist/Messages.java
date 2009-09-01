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
package com.aptana.ide.editor.scriptdoc.contentassist;

import org.eclipse.osgi.util.NLS;

/**
 * @author Robin
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.editor.scriptdoc.contentassist.messages"; //$NON-NLS-1$

	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * ScriptDocContentAssistProcessor_Alias
	 */
	public static String ScriptDocContentAssistProcessor_Alias;

	/**
	 * ScriptDocContentAssistProcessor_Author
	 */
	public static String ScriptDocContentAssistProcessor_Author;

	/**
	 * ScriptDocContentAssistProcessor_Description
	 */
	public static String ScriptDocContentAssistProcessor_Description;

	/**
	 * ScriptDocContentAssistProcessor_Ctor
	 */
	public static String ScriptDocContentAssistProcessor_Ctor;

	/**
	 * ScriptDocContentAssistProcessor_Copyright
	 */
	public static String ScriptDocContentAssistProcessor_Copyright;

	/**
	 * ScriptDocContentAssistProcessor_Deprecated
	 */
	public static String ScriptDocContentAssistProcessor_Deprecated;

	/**
	 * ScriptDocContentAssistProcessor_Exception
	 */
	public static String ScriptDocContentAssistProcessor_Exception;

	/**
	 * ScriptDocContentAssistProcessor_Extends
	 */
	public static String ScriptDocContentAssistProcessor_Extends;

	/**
	 * ScriptDocContentAssistProcessor_Id
	 */
	public static String ScriptDocContentAssistProcessor_Id;

	/**
	 * ScriptDocContentAssistProcessor_License
	 */
	public static String ScriptDocContentAssistProcessor_License;

	/**
	 * ScriptDocContentAssistProcessor_MemberOf
	 */
	public static String ScriptDocContentAssistProcessor_MemberOf;

	/**
	 * ScriptDocContentAssistProcessor_Method
	 */
	public static String ScriptDocContentAssistProcessor_Method;

	/**
	 * ScriptDocContentAssistProcessor_Namespace
	 */
	public static String ScriptDocContentAssistProcessor_Namespace;

	/**
	 * ScriptDocContentAssistProcessor_Param
	 */
	public static String ScriptDocContentAssistProcessor_Param;

	public static String ScriptDocContentAssistProcessor_Private_meaning_msg;

	/**
	 * ScriptDocContentAssistProcessor_ProjectDescription
	 */
	public static String ScriptDocContentAssistProcessor_ProjectDescription;

	/**
	 * ScriptDocContentAssistProcessor_Return
	 */
	public static String ScriptDocContentAssistProcessor_Return;

	/**
	 * ScriptDocContentAssistProcessor_Sdoc
	 */
	public static String ScriptDocContentAssistProcessor_Sdoc;

	/**
	 * ScriptDocContentAssistProcessor_See
	 */
	public static String ScriptDocContentAssistProcessor_See;

	/**
	 * ScriptDocContentAssistProcessor_Since
	 */
	public static String ScriptDocContentAssistProcessor_Since;

	/**
	 * ScriptDocContentAssistProcessor_Type
	 */
	public static String ScriptDocContentAssistProcessor_Type;

	/**
	 * ScriptDocContentAssistProcessor_Version
	 */
	public static String ScriptDocContentAssistProcessor_Version;

	/**
	 * ScriptDocContentAssistProcessor_ErrorComputingCompletionProposals
	 */
	public static String ScriptDocContentAssistProcessor_ErrorComputingCompletionProposals;

	/**
	 * ScriptDocContentAssistProcessor_Property
	 */
	public static String ScriptDocContentAssistProcessor_Property;
}
