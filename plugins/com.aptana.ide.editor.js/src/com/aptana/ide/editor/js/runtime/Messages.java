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
package com.aptana.ide.editor.js.runtime;

import org.eclipse.osgi.util.NLS;

/**
 * @author Robin
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.editor.js.runtime.messages"; //$NON-NLS-1$

	private Messages()
	{
	}

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * ObjectBase_AttemptedToOverwritePropertyNameAtFileIndex
	 */
	public static String ObjectBase_AttemptedToOverwritePropertyNameAtFileIndex;

	/**
	 * ObjectBase_LocalPropertyNameDoesNotExist
	 */
	public static String ObjectBase_LocalPropertyNameDoesNotExist;

	/**
	 * ObjectBase_LocalPropertyNameDoesNotExist2
	 */
	public static String ObjectBase_LocalPropertyNameDoesNotExist2;

	/**
	 * ObjectBase_PropertyNameMustBeDefined
	 */
	public static String ObjectBase_PropertyNameMustBeDefined;

	/**
	 * ObjectBase_TryingToUnputAPropertyThatDoesNotExist
	 */
	public static String ObjectBase_TryingToUnputAPropertyThatDoesNotExist;

	/**
	 * JSFunction_TryingToConstructWithNonFunction
	 */
	public static String JSFunction_TryingToConstructWithNonFunction;

	/**
	 * Reference_ObjectBaseMustBeDefined
	 */
	public static String Reference_ObjectBaseMustBeDefined;

	/**
	 * Reference_PropertyNameMustBeDefined
	 */
	public static String Reference_PropertyNameMustBeDefined;

	/**
	 * Environment_CtorNameMustBeDefined
	 */
	public static String Environment_CtorNameMustBeDefined;

	/**
	 * Environment_RegionMustNotBeNull
	 */
	public static String Environment_RegionMustNotBeNull;

	/**
	 * OrderedObjectCollection_ObjectMustBeDefined
	 */
	public static String OrderedObjectCollection_ObjectMustBeDefined;

	/**
	 * OrderedObjectCollection_CanNotOverwriteExistingObject
	 */
	public static String OrderedObjectCollection_CanNotOverwriteExistingObject;

	/**
	 * OrderedObjectCollection_NoObjectExists
	 */
	public static String OrderedObjectCollection_NoObjectExists;

	/**
	 * OrderedObjectCollection_CanNotRemoveFromEmpty
	 */
	public static String OrderedObjectCollection_CanNotRemoveFromEmpty;

	/**
	 * Property_RefCountBelowZero
	 */
	public static String Property_RefCountBelowZero;
}
