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
package com.aptana.ide.core.preferences;

/**
 * Contains all preferences for the com.aptana.ide.core.ui plugin To add a preference, create a static string with an
 * all-uppercase preference key. Then assign a identically-named string to it, prefixing it with the plugin name" i.e.
 * SHOW_WHITESPACE = "com.aptana.ide.core.ui.SHOW_WHITESPACE"
 * 
 * @author Ingo Muschenetz
 */
public interface IPreferenceConstants
{
	/**
	 * Preference for which files we wish to cloak by default
	 */
	String PREF_GLOBAL_SYNC_CLOAKING_EXTENSIONS = "com.aptana.ide.core.PREF_GLOBAL_SYNC_CLOAKING_EXTENSIONS"; //$NON-NLS-1$

	/**
	 * Do we cloak against the full path or just the name
	 */
	String PREF_GLOBAL_CLOAK_FULL_PATH = "com.aptana.ide.core.PREF_GLOBAL_CLOAK_FULL_PATH"; //$NON-NLS-1$

	/**
	 * Do we clean the configuration on restart?
	 */
	String PREF_CLEAN_RESTART = "com.aptana.ide.core.PREF_CLEAN_RESTART"; //$NON-NLS-1$

	/**
	 * Print to log
	 */
	String PREF_ENABLE_DEBUGGING = "com.aptana.ide.core.PREF_ENABLE_DEBUGGING"; //$NON-NLS-1$

	/**
	 * Pref debug level
	 */
	String PREF_DEBUG_LEVEL = "com.aptana.ide.core.PREF_DEBUG_LEVEL"; //$NON-NLS-1$

	/**
	 * SHOW_LIVE_HELP
	 */
	String SHOW_LIVE_HELP = "com.aptana.ide.core.SHOW_LIVE_HELP"; //$NON-NLS-1$

	/**
	 * Preference for what are the default set of web files
	 */
	String PREF_ENABLE_PASSWORD_CACHING = "com.aptana.ide.core.PREF_ENABLE_PASSWORD_CACHING"; //$NON-NLS-1$

	/**
	 * Comma delimeted list of hosts whose username/password are cached
	 */
	String SAVED_PASSWORD_HOSTS = "com.aptana.ide.core.SAVED_PASSWORD_HOSTS"; //$NON-NLS-1$

	/**
	 * Cached username
	 */
	String USERNAME = "com.aptana.ide.core.cached.username"; //$NON-NLS-1$

	/**
	 * Cached username
	 */
	String PASSWORD = "com.aptana.ide.core.cached.password"; //$NON-NLS-1$	

	/**
	 * Cached key
	 */
	String CACHED_KEY = "com.aptana.ide.core.CACHED_KEY"; //$NON-NLS-1$

}
