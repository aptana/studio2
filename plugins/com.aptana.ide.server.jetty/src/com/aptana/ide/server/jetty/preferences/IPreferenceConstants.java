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
package com.aptana.ide.server.jetty.preferences;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public interface IPreferenceConstants
{

	/**
	 * USE_FIREFOX
	 */
	public static final String USE_FIREFOX = "com.aptana.ide.server.jetty.preferences.USE_FIREFOX"; //$NON-NLS-1$

	/**
	 * ENABLE_BUILTIN_PREVIEW
	 */
	public static final String ENABLE_BUILTIN_PREVIEW = "com.aptana.ide.server.jetty.preferences.ENABLE_BUILTIN_PREVIEW"; //$NON-NLS-1$

	/**
	 * PORTAL_UPDATE_TYPE
	 */
	public static final String PORTAL_UPDATE_TYPE = "com.aptana.ide.server.jetty.preferences.PORTAL_UPDATE_TYPE"; //$NON-NLS-1$

	/**
	 * PORTAL_UPDATE_RELEASE
	 */
	public static final String PORTAL_UPDATE_RELEASE = "production"; //$NON-NLS-1$

	/**
	 * PORTAL_UPDATE_NEXT
	 */
	public static final String PORTAL_UPDATE_NEXT = "next"; //$NON-NLS-1$

	/**
	 * PORTAL_UPDATE_NIGHTLY
	 */
	public static final String PORTAL_UPDATE_NIGHTLY = "nightly"; //$NON-NLS-1$
	
	/**
	 * PORTAL_SHOWN_ALREADY
	 */
	public static final String PORTAL_SHOWN_ALREADY = "shownAlready"; //$NON-NLS-1$

	/**
	 * PORTAL_CHECK_FOR_UPDATE_INTERVAL
	 */
	public static final String PORTAL_CHECK_FOR_UPDATE_INTERVAL = "com.aptana.ide.server.jetty.preferences.portalCheckForUpdateInterval"; //$NON-NLS-1$

}
