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
package com.aptana.ide.editor.css.preview;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "com.aptana.ide.editor.css.preview.messages"; //$NON-NLS-1$

	public static String CSSPreviewPropertyPage_BrowseText;
	public static String CSSPreviewPropertyPage_OverrideProjectLinkText;

	public static String CSSPreviewPropertyPage_OverrideProjectText;

	public static String CSSPreviewPropertyPage_OverrideWorkspaceLinkText;

	public static String CSSPreviewPropertyPage_OverrideWorkspaceText;

	public static String CSSPreviewPropertyPage_PreviewText;
	public static String CSSPreviewPropertyPage_ProjectFilesText;
	public static String CSSPreviewPropertyPage_URLText;
	public static String CSSPreviewPropertyPage_UseProjectFileText;
	public static String CSSPreviewPropertyPage_UseURLText;

	public static String PreviewConfigurationPage_AddProjectText;

	public static String PreviewConfigurationPage_BaseUrlLabel;

	public static String PreviewConfigurationPage_BrowserGroupText;

	public static String PreviewConfigurationPage_CancelText;

	public static String PreviewConfigurationPage_CurrentPageText;

	public static String PreviewConfigurationPage_ExternalServerText;

	public static String PreviewConfigurationPage_GroupTitle;

	public static String PreviewConfigurationPage_InternalServerText;

	public static String PreviewConfigurationPage_NameLabel;

	public static String PreviewConfigurationPage_SaveText;

	public static String PreviewConfigurationPage_ServerGroupTitle;

	public static String PreviewConfigurationPage_StartActionGroupText;

	public static String PreviewConfigurationPage_StartUrlText;

	public static String PreviewConfigurationPage_Title;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
