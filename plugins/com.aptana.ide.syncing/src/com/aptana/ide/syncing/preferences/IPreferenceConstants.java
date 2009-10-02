/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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
package com.aptana.ide.syncing.preferences;

/**
 * @author Ingo Muschenetz
 */
public interface IPreferenceConstants
{

	/**
	 * IGNORE_DIALOG_FILE_UPLOAD
	 */
	String IGNORE_DIALOG_FILE_UPLOAD = "com.aptana.ide.syncing.IGNORE_DIALOG_FILE_UPLOAD"; //$NON-NLS-1$

	/**
	 * IGNORE_DIALOG_FILE_DOWNLOAD
	 */
	String IGNORE_DIALOG_FILE_DOWNLOAD = "com.aptana.ide.syncing.IGNORE_DIALOG_FILE_DOWNLOAD"; //$NON-NLS-1$

	/**
	 * IGNORE_DIALOG_FILE_UPLOAD_PROMPT
	 */
	String IGNORE_DIALOG_FILE_SYNC_PROMPT = "com.aptana.ide.syncing.IGNORE_DIALOG_FILE_SYNC_PROMPT"; //$NON-NLS-1$

	/**
	 * SHOW_SYNC_EXPLORER_TABLE
	 */
	String SHOW_SYNC_EXPLORER_TABLE = "com.aptana.ide.syncing.SHOW_SYNC_EXPLORER_TABLE"; //$NON-NLS-1$

	/**
	 * SHOW_DATE
	 */
	String SHOW_DATE = "com.aptana.ide.syncing.SHOW_DATE"; //$NON-NLS-1$

	/**
	 * SHOW_SIZE
	 */
	String SHOW_SIZE = "com.aptana.ide.syncing.SHOW_SIZE"; //$NON-NLS-1$

    /**
     * VIEW_MODE
     */
    static final String VIEW_MODE = "com.aptana.ide.syncing.views.VIEW_MODE"; //$NON-NLS-1$

    /**
     * FLAT
     */
    static final String VIEW_FLAT = "FLAT"; //$NON-NLS-1$

    /**
     * TREE
     */
    static final String VIEW_TREE = "TREE"; //$NON-NLS-1$

    /**
     * DIRECTION_MODE
     */
    static final String DIRECTION_MODE = "com.aptana.ide.syncing.views.DIRECTION_MODE"; //$NON-NLS-1$

    /**
     * DIRECTION_BOTH
     */
    static final String DIRECTION_BOTH = "BOTH"; //$NON-NLS-1$

    /**
     * DIRECTION_UPLOAD
     */
    static final String DIRECTION_UPLOAD = "UPLOAD"; //$NON-NLS-1$

    /**
     * DIRECTION_DOWNLOAD
     */
    static final String DIRECTION_DOWNLOAD = "DOWNLOAD"; //$NON-NLS-1$

    /**
     * DIRECTION_FORCE_UPLOAD
     */
    static final String DIRECTION_FORCE_UPLOAD = "FORCE_UPLOAD"; //$NON-NLS-1$

    /**
     * DIRECTION_FORCE_DOWNLOAD
     */
    static final String DIRECTION_FORCE_DOWNLOAD = "FORCE_DOWNLOAD"; //$NON-NLS-1$

    /**
     * DELETE_REMOTE_FILES
     */
    static final String DELETE_REMOTE_FILES = "com.aptana.ide.syncing.views.DELETE_REMOTE_FILES"; //$NON-NLS-1$

    /**
     * DELETE_LOCAL_FILES
     */
    static final String DELETE_LOCAL_FILES = "com.aptana.ide.syncing.views.DELETE_LOCAL_FILES"; //$NON-NLS-1$

    /**
     * SHOW_MODIFICATION_TIME
     */
    static final String SHOW_MODIFICATION_TIME = "com.aptana.ide.syncing.views.SHOW_MODIFICATION_TIME"; //$NON-NLS-1$

}
