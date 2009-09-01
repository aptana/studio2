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
package com.aptana.ide.editor.html.preferences;

import java.util.Map;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.ide.editor.html.HTMLPlugin;
import com.aptana.ide.editor.html.parsing.HTMLMimeType;
import com.aptana.ide.internal.ui.dialogs.PreferencesAccess;
import com.aptana.ide.ui.editors.preferences.formatter.CodeFormatterConfigurationBlock;
import com.aptana.ide.ui.editors.preferences.formatter.CodeFormatterPreferencePage;
import com.aptana.ide.ui.editors.preferences.formatter.FormatterMessages;
import com.aptana.ide.ui.editors.preferences.formatter.FormatterModifyDialog;
import com.aptana.ide.ui.editors.preferences.formatter.ModifyDialog;
import com.aptana.ide.ui.editors.preferences.formatter.ProfileConfigurationBlock;
import com.aptana.ide.ui.editors.preferences.formatter.ProfileManager;
import com.aptana.ide.ui.editors.preferences.formatter.ProfileStore;
import com.aptana.ide.ui.editors.preferences.formatter.ProfileManager.Profile;

/**
 * 
 * @author Ingo Muschenetz
 * 
 */
public class FormattingPreferencePage extends CodeFormatterPreferencePage
		implements IWorkbenchPreferencePage {
	
	private static final String PREVIEW = "<!DOCTYPE html\r\n" +  //$NON-NLS-1$
			"    PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\r\n" +  //$NON-NLS-1$
			"    \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\r\n" +  //$NON-NLS-1$
			"<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">\r\n" +  //$NON-NLS-1$
			"<head>\r\n" +  //$NON-NLS-1$
			" <title>HTML Formatting Sample</title><style type=\"text/css\">\r\n" +  //$NON-NLS-1$
			"\r\n" +  //$NON-NLS-1$
			"@import url(\"/shared.css\");\r\n" +  //$NON-NLS-1$
			"\r\n" +  //$NON-NLS-1$
			"#footer {\r\n" +  //$NON-NLS-1$
			"	border:1px solid white;\r\n" +  //$NON-NLS-1$
			"}\r\n" +  //$NON-NLS-1$
			"\r\n" +  //$NON-NLS-1$
			"#banner {\r\n" +  //$NON-NLS-1$
			"  background-color: #636D84;\r\n" +  //$NON-NLS-1$
			"  padding-right:40px;\r\n" +  //$NON-NLS-1$
			"  padding-top:10px;\r\n" +  //$NON-NLS-1$
			"  height:40px;\r\n" +  //$NON-NLS-1$
			"}\r\n" +  //$NON-NLS-1$
			"</style>\r\n" +  //$NON-NLS-1$
			" <script type=\"text/javascript\" src=\"/trac/chrome/common/js/trac.js\"></script>\r\n" +  //$NON-NLS-1$
			"</head>\r\n" +  //$NON-NLS-1$
			"<body>\r\n" +  //$NON-NLS-1$
			"\r\n" +  //$NON-NLS-1$
			"<div id=\"navigation\">\r\n" +  //$NON-NLS-1$
			"   		<div id=\"header\">\r\n" +  //$NON-NLS-1$
			"		<h1><a title=\"Return to home page\" accesskey=\"1\" href=\"/\">Aptana</a></h1>\r\n" +  //$NON-NLS-1$
			"	</div>\r\n" +  //$NON-NLS-1$
			"	<div>\r\n" +  //$NON-NLS-1$
			"		<ul>\r\n" +  //$NON-NLS-1$
			"			<li><a href=\"/dev\">contribute</a></li>\r\n" +  //$NON-NLS-1$
			"			<li><a href=\"/forums\">forums</a></li>\r\n" +  //$NON-NLS-1$
			"			<li><a href=\"/download_all.php\">products</a></li>\r\n" +  //$NON-NLS-1$
			"			<li><a href=\"/support.php\">support</a></li>\r\n" +  //$NON-NLS-1$
			"			<li><a href=\"/about.php\">about</a></li>\r\n" +  //$NON-NLS-1$
			"		</ul>\r\n" +  //$NON-NLS-1$
			"	</div>\r\n" +  //$NON-NLS-1$
			"</div>\r\n" +  //$NON-NLS-1$
			" </body>\r\n" +  //$NON-NLS-1$
			"</html>"; //$NON-NLS-1$


	/**
	 * 
	 */
	public FormattingPreferencePage() {
		super(HTMLMimeType.MimeType, HTMLPlugin.getDefault().getPreferenceStore());
	}


	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.CodeFormatterPreferencePage#createConfigurationBlock(com.aptana.ide.internal.ui.dialogs.PreferencesAccess)
	 */
	protected ProfileConfigurationBlock createConfigurationBlock(
			PreferencesAccess access) {
		return new CodeFormatterConfigurationBlock(getProject(), access,
				HTMLMimeType.MimeType, HTMLPlugin.getDefault()
						.getPreferenceStore(), PREVIEW, HTMLPlugin.ID) {

			protected ModifyDialog createModifyDialog(Shell shell,
					Profile profile, ProfileManager profileManager,
					ProfileStore profileStore, boolean newProfile) {

				return new FormatterModifyDialog(shell, profile,
						profileManager, profileStore, newProfile,
						FORMATTER_DIALOG_PREFERENCE_KEY,
						DIALOGSTORE_LASTSAVELOADPATH, HTMLMimeType.MimeType) {

					protected void addPages(Map values) {
						addTabPage(
								FormatterMessages.ModifyDialog_tabpage_control_statements_title,
								new GeneralTabPage(this, values,
										HTMLMimeType.MimeType));
						
				     
//					addTabPage(FormatterMessages.ModifyDialog_tabpage_braces_title, new BracesTabPage(this, values,PHPMimeType.MimeType)); 
//					addTabPage(FormatterMessages.ModifyDialog_tabpage_whitespace_title, new WhiteSpaceTabPage(this, values,PHPMimeType.MimeType)); 
//					addTabPage(FormatterMessages.ModifyDialog_tabpage_blank_lines_title, new BlankLinesTabPage(this, values,PHPMimeType.MimeType)); 
					addTabPage(FormatterMessages.ModifyDialog_tabpage_new_lines_title, new NewLinesTabPage(this, values,HTMLMimeType.MimeType));
					addTabPage(FormatterMessages.ModifyDialog_tabpage_indentation_title, new IndentationTabPage(this, values,HTMLMimeType.MimeType));
					//addTabPage(FormatterMessages.ModifyDialog_tabpage_control_statements_title, new ControlStatementsTabPage(this, values,PHPMimeType.MimeType)); 					
					//addTabPage(FormatterMessages.ModifyDialog_tabpage_comments_title, new CommentsTabPage(this, values,PHPMimeType.MimeType));
					}

				};
			};

		};		
	}

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.PropertyAndPreferencePage#getPreferencePageID()
	 */
	protected String getPreferencePageID() {
		return "com.aptana.ide.editor.html.preferences.FormattingPreferencePage"; //$NON-NLS-1$
	}

	
	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.PropertyAndPreferencePage#getPropertyPageID()
	 */
	protected String getPropertyPageID() {
		return "com.aptana.html.ui.propertyPages.CodeFormatterPreferencePage"; //$NON-NLS-1$
	}
}
