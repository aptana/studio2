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
package com.aptana.ide.scripting;

import org.eclipse.osgi.util.NLS;

/**
 * @author Kevin Lindsey
 *
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.scripting.messages"; //$NON-NLS-1$

	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	/**
	 * FileUtilities_Zip_File_Path_Undefined
	 */
	public static String FileUtilities_Zip_File_Path_Undefined;
	
	/**
	 * FileUtilities_Destination_Directory_Undefined
	 */
	public static String FileUtilities_Destination_Directory_Undefined;
	
	/**
	 * FileUtilities_Prefix_Undefined
	 */
	public static String FileUtilities_Prefix_Undefined;
	
	/**
	 * FileUtilities_Extracted_File
	 */
	public static String FileUtilities_Extracted_File;
	
	/**
	 * FileUtilities_Error
	 */
	public static String FileUtilities_Error;

	/**
	 * Global_Error
	 */
	public static String Global_Error;

	/**
	 * Global_Prompt
	 */
	public static String Global_Prompt;

	/**
	 * Global_Cannot_Locate_File
	 */
	public static String Global_Cannot_Locate_File;

	/**
	 * Global_Filename_Not_Defined
	 */
	public static String Global_Filename_Not_Defined;

	/**
	 * Global_Bundle_Not_Found
	 */
	public static String Global_Bundle_Not_Found;

	/**
	 * Global_Function_Does_Not_Exist
	 */
	public static String Global_Function_Does_Not_Exist;

	/**
	 * ScriptClassLoader_Bundle_Not_Defined
	 */
	public static String ScriptClassLoader_Bundle_Not_Defined;

	/**
	 * ScriptClassLoader_Unable_To_Find_Class
	 */
	public static String ScriptClassLoader_Unable_To_Find_Class;

	/**
	 * ScriptClassLoader_Unable_To_Find_Resource
	 */
	public static String ScriptClassLoader_Unable_To_Find_Resource;

	/**
	 * ScriptClassLoader_Unable_To_Load_Class
	 */
	public static String ScriptClassLoader_Unable_To_Load_Class;

	/**
	 * ScriptingEngine_ScriptingEngine_Is_Singleton
	 */
	public static String ScriptingEngine_ScriptingEngine_Is_Singleton;

	/**
	 * ScriptingEngine_Script_Id_Not_Found
	 */
	public static String ScriptingEngine_Script_Id_Not_Found;

	/**
	 * ScriptingEngine_Error
	 */
	public static String ScriptingEngine_Error;

	/**
	 * ScriptingEngine_Activated
	 */
	public static String ScriptingEngine_Activated;

	/**
	 * ScriptingEngine_Changed
	 */
	public static String ScriptingEngine_Changed;

	/**
	 * ScriptingEngine_Reset
	 */
	public static String ScriptingEngine_Reset;

	/**
	 * ScriptingEngine_Registering_Listener
	 */
	public static String ScriptingEngine_Registering_Listener;

	/**
	 * ScriptingEngine_Started
	 */
	public static String ScriptingEngine_Started;

	/**
	 * ScriptingEngine_Error_On_Start
	 */
	public static String ScriptingEngine_Error_On_Start;

	/**
	 * ScriptingEngine_Base_Directory_Does_Not_Exist
	 */
	public static String ScriptingEngine_Base_Directory_Does_Not_Exist;

	/**
	 * ScriptingHttpResource_Error
	 */
	public static String ScriptingHttpResource_Error;
	
	/**
	 * ScriptingHttpResource_Processing_Error
	 */
	public static String ScriptingHttpResource_Processing_Error;

	/**
	 * ScriptingHttpResource_File_Does_Not_Exist
	 */
	public static String ScriptingHttpResource_File_Does_Not_Exist;

	/**
	 * ScriptingResourceResolver_Error
	 */
	public static String ScriptingResourceResolver_Error;

	/**
	 * ScriptThread_Callback_Does_Not_Exist
	 */
	public static String ScriptThread_Callback_Does_Not_Exist;
}
