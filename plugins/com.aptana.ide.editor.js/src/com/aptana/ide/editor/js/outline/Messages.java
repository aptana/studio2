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
package com.aptana.ide.editor.js.outline;

import org.eclipse.osgi.util.NLS;

/**
 * @author Kevin Lindsey
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.editor.js.outline.messages"; //$NON-NLS-1$

	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * JSCollapseAction_Collapse_All
	 */
	public static String JSCollapseAction_Collapse_All;
	
	/**
	 * JSContentProvider_Error_Processing_Parse_Node
	 */
	public static String JSContentProvider_Error_Processing_Parse_Node;
	
	/**
	 * JSHidePrivateAction_Hide_Private_Members
	 */
	public static String JSHidePrivateAction_Hide_Private_Members;
	
	/**
	 * JSOutlineItem_Label_Not_Defined
	 */
	public static String JSOutlineItem_Label_Not_Defined;
	
	/**
	 * JSOutlineItem_Source_Range_Not_Defined
	 */
	public static String JSOutlineItem_Source_Range_Not_Defined;
	
	/**
	 * JSOutlineItem_Target_Not_Defined
	 */
	public static String JSOutlineItem_Target_Not_Defined;
	
	/**
	 * JSSortAction_Sort
	 */
	public static String JSSortAction_Sort;
	
	/**
	 * Reference_Node_Not_Defined
	 */
	public static String Reference_Node_Not_Defined;
	
	/**
	 * Reference_Name_Not_Defined
	 */
	public static String Reference_Name_Not_Defined;
	
	/**
	 * Reference_Type_Not_Defined
	 */
	public static String Reference_Type_Not_Defined;
	
	/**
	 * Reference_Scope_Not_Defined
	 */
	public static String Reference_Scope_Not_Defined;
}
