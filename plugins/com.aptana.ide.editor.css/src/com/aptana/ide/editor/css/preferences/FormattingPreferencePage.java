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
package com.aptana.ide.editor.css.preferences;

import java.util.Map;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.ide.editor.css.CSSPlugin;
import com.aptana.ide.editor.css.parsing.CSSMimeType;
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
	
	private static final String PREVIEW = "H1 {\r\n" +  //$NON-NLS-1$
			"color: white; background: teal; FONT-FAMILY: arial, helvetica, lucida-sans, sans-serif; FONT-SIZE: 18pt; FONT-STYLE: normal; FONT-VARIANT: normal\r\n" +  //$NON-NLS-1$
			"}\r\n" +  //$NON-NLS-1$
			"H2 {\r\n" +  //$NON-NLS-1$
			"COLOR: #000000; FONT-FAMILY: verdana, helvetica, lucida-sans, sans-serif; FONT-SIZE: 14pt; FONT-STYLE: normal; FONT-VARIANT: normal\r\n" +  //$NON-NLS-1$
			"}\r\n" +  //$NON-NLS-1$
			"H3 {\r\n" +  //$NON-NLS-1$
			"COLOR: #000000; FONT-FAMILY: lucida-sans, sans-serif; FONT-SIZE: 14pt; FONT-STYLE: normal; FONT-VARIANT: normal\r\n" +  //$NON-NLS-1$
			"}\r\n" +  //$NON-NLS-1$
			"H4 {\r\n" +  //$NON-NLS-1$
			"COLOR: #000000; FONT-FAMILY: lucida-sans, sans-serif; FONT-SIZE: 12pt; FONT-STYLE: normal; FONT-VARIANT: normal\r\n" +  //$NON-NLS-1$
			"}\r\n" +  //$NON-NLS-1$
			"H5 {\r\n" +  //$NON-NLS-1$
			"color: white; background: darkblue; FONT-FAMILY: lucida-sans, sans-serif; FONT-SIZE: 12pt; FONT-STYLE: normal; FONT-VARIANT: normal\r\n" +  //$NON-NLS-1$
			"}\r\n" +  //$NON-NLS-1$
			"H6 {\r\n" +  //$NON-NLS-1$
			"color: yellow; background: green; FONT-FAMILY: lucida-sans, sans-serif; FONT-SIZE: 10pt; FONT-STYLE: normal; FONT-VARIANT: normal\r\n" +  //$NON-NLS-1$
			"} \r\n" +  //$NON-NLS-1$
			"\r\n" +  //$NON-NLS-1$
			"body {\r\n" +  //$NON-NLS-1$
			"COLOR: #000000; FONT-FAMILY: lucida-sans, sans-serif; FONT-SIZE: 10pt; FONT-STYLE: normal; FONT-VARIANT: normal; background-image: url(\'bkgnd.gif\') \r\n" +  //$NON-NLS-1$
			"}\r\n" +  //$NON-NLS-1$
			""; //$NON-NLS-1$


	/**
	 * 
	 */
	public FormattingPreferencePage() {
		super(CSSMimeType.MimeType, CSSPlugin.getDefault().getPreferenceStore());
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
				CSSMimeType.MimeType, CSSPlugin.getDefault()
						.getPreferenceStore(), PREVIEW, CSSPlugin.ID) {

			protected ModifyDialog createModifyDialog(Shell shell,
					Profile profile, ProfileManager profileManager,
					ProfileStore profileStore, boolean newProfile) {

				return new FormatterModifyDialog(shell, profile,
						profileManager, profileStore, newProfile,
						FORMATTER_DIALOG_PREFERENCE_KEY,
						DIALOGSTORE_LASTSAVELOADPATH, CSSMimeType.MimeType) {

					protected void addPages(Map values) {
						addTabPage(
								FormatterMessages.ModifyDialog_tabpage_control_statements_title,
								new IndentationTabPage(this, values,
										CSSMimeType.MimeType));
						addTabPage(
								FormatterMessages.ModifyDialog_tabpage_braces_title,
								new BracesTabPage(this, values,
										CSSMimeType.MimeType));
						
				    //addTabPage(FormatterMessages.ModifyDialog_tabpage_indentation_title, new IndentationTabPage(this, values,PHPMimeType.MimeType)); 
//					addTabPage(FormatterMessages.ModifyDialog_tabpage_braces_title, new BracesTabPage(this, values,PHPMimeType.MimeType)); 
//					addTabPage(FormatterMessages.ModifyDialog_tabpage_whitespace_title, new WhiteSpaceTabPage(this, values,PHPMimeType.MimeType)); 
//					addTabPage(FormatterMessages.ModifyDialog_tabpage_blank_lines_title, new BlankLinesTabPage(this, values,PHPMimeType.MimeType)); 
//					addTabPage(FormatterMessages.ModifyDialog_tabpage_new_lines_title, new NewLinesTabPage(this, values,PHPMimeType.MimeType)); 
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
		return "com.aptana.ide.editor.css.preferences.FormattingPreferencePage"; //$NON-NLS-1$
	}

	
	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.PropertyAndPreferencePage#getPropertyPageID()
	 */
	protected String getPropertyPageID() {
		return "com.aptana.css.ui.propertyPages.CodeFormatterPreferencePage"; //$NON-NLS-1$
	}
}
