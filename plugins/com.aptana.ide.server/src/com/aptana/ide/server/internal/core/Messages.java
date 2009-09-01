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
package com.aptana.ide.server.internal.core;

import org.eclipse.osgi.util.NLS;

/**
 * @author Pavel Petrochenko
 *
 */
public final class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.ide.server.internal.core.messages"; //$NON-NLS-1$
	

	/**
	 * AbstractServer_DOES_NOT_SUPPORTS_RESTART
	 */
	public static String AbstractServer_DOES_NOT_SUPPORTS_RESTART;

	/**
	 * AbstractServer_DOES_NOT_SUPPORTS_PUBLISH
	 */
	public static String AbstractServer_DOES_NOT_SUPPORTS_PUBLISH;

	/**
	 * AbstractServer_IS_NOT_RUNNING
	 */
	public static String AbstractServer_IS_NOT_RUNNING;

	/**
	 * AbstractServer_IS_RUNNG
	 */
	public static String AbstractServer_IS_RUNNG;

	/**
	 * AbstractServer_DOES_NOT_SUPPORTS_START
	 */
	public static String AbstractServer_DOES_NOT_SUPPORTS_START;

	/**
	 * AbstractServer_LBL_Unknown
	 */
    public static String AbstractServer_LBL_Unknown;

	/**
	 * AbstractServer_PUBLISH_ON_OP_LABEL
	 */
	public static String AbstractServer_PUBLISH_ON_OP_LABEL;


	/**
	 * AbstractServer_RECONFIGURE_OP_LABEL
	 */
	public static String AbstractServer_RECONFIGURE_OP_LABEL;


	/**
	 * AbstractServer_RESTART_OP_LABEL
	 */
	public static String AbstractServer_RESTART_OP_LABEL;


	/**
	 * AbstractServer_START_OP_LABEL
	 */
	public static String AbstractServer_START_OP_LABEL;


	/**
	 * AbstractServer_STOP_OP_LABEL
	 */
	public static String AbstractServer_STOP_OP_LABEL;


    public static String GroupServer_Status_CannotModify;


    public static String GroupServer_Status_NoServerRestart;


    public static String GroupServer_Status_NoServerStart;


    public static String GroupServer_Status_NoServerStop;

	private Messages()
	{
	}

	static{
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
