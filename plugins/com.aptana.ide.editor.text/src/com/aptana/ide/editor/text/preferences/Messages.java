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
package com.aptana.ide.editor.text.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 *
 */
public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.editor.text.preferences.messages"; //$NON-NLS-1$
	
	/**
	 * TextEditorPreferencePage_Apply_Changes
	 */
	public static String TextEditorPreferencePage_Apply_Changes;

	/**
	 * TextEditorPreferencePage_Error_Adding_Language
	 */
	public static String TextEditorPreferencePage_Error_Adding_Language;

	/**
	 * TextEditorPreferencePage_Error_Loading_Language
	 */
	public static String TextEditorPreferencePage_Error_Loading_Language;

	/**
	 * TextEditorPreferencePage_Error_Loading_Lexer_File
	 */
	public static String TextEditorPreferencePage_Error_Loading_Lexer_File;

	/**
	 * TextEditorPreferencePage_FILE_TYPE_EXISTS
	 */
	public static String TextEditorPreferencePage_FILE_TYPE_EXISTS;
	
	/**
	 * TextEditorPreferencePage_FILE_TYPE_EXISTS_TITLE
	 */
	public static String TextEditorPreferencePage_FILE_TYPE_EXISTS_TITLE;
	
	/**
	 * TextEditorPreferencePage_ADD
	 */
	public static String TextEditorPreferencePage_ADD;

	public static String TextEditorPreferencePage_Allows_Users_To_Create_Custom_Editors;
	
	/**
	 * TextEditorPreferencePage_ASSOCIATED_FILE_EXTENSIONS
	 */
	public static String TextEditorPreferencePage_ASSOCIATED_FILE_EXTENSIONS;
	
	/**
	 * TextEditorPreferencePage_BROWSE
	 */
	public static String TextEditorPreferencePage_BROWSE;
	
	/**
	 * TextEditorPreferencePage_COLORIATION_FILE
	 */
	public static String TextEditorPreferencePage_COLORIATION_FILE;
	
	/**
	 * TextEditorPreferencePage_GRAMMAR_FILE
	 */
	public static String TextEditorPreferencePage_GRAMMAR_FILE;

	/**
	 * TextEditorPreferencePage_Language_Already_Supported
	 */
	public static String TextEditorPreferencePage_Language_Already_Supported;
	
	/**
	 * TextEditorPreferencePage_REMOVE_ASSOCIATION
	 */
	public static String TextEditorPreferencePage_REMOVE_ASSOCIATION;
	
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
