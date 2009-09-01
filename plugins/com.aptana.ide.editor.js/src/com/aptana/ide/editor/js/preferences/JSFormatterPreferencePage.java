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
package com.aptana.ide.editor.js.preferences;

import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;

import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.jscomment.JSCommentContributor;
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
 * @author Pavel Petrochenko
 *
 */
public class JSFormatterPreferencePage extends CodeFormatterPreferencePage{

	private static final String PREVIEW="// Dojo configuration and Variable Initialization\r\n" +//$NON-NLS-1$ 
			"// Put this code in index.php before the line where you include the javascript for dojo\r\n" + //$NON-NLS-1$
			"// djConfig = { isDebug: true };\r\n" + //$NON-NLS-1$
			"dojo.require(\"dojo.io.*\");\r\n" + //$NON-NLS-1$
			"dojo.require(\"dojo.io.IframeIO\");\r\n" +//$NON-NLS-1$ 
			"ctr = 0;\r\n" + //$NON-NLS-1$
			"function upload_file_submit()\r\n" +//$NON-NLS-1$ 
			"{\r\n" + //$NON-NLS-1$
			"var bindArgs = {\r\n" + //$NON-NLS-1$
			"formNode: document.getElementById(\"upload_file\"), //form\'s id\r\n" + //$NON-NLS-1$
			"mimetype: \"text/plain\", //Enter file type info here\r\n" +//$NON-NLS-1$ 
			"content:\r\n" + //$NON-NLS-1$
			"{\r\n" + //$NON-NLS-1$
			"increment: ctr++,\r\n" + //$NON-NLS-1$
			"name: \"select_file\", //file name in the form\r\n" + //$NON-NLS-1$
			"post_field: \"\" // add more fields here .. field will be accessible by $_POST[\"post_field\"]\r\n" +//$NON-NLS-1$ 
			"},\r\n" + //$NON-NLS-1$
			"handler: function(type, data, evt)\r\n" +//$NON-NLS-1$ 
			"{\r\n" + //$NON-NLS-1$
			"//handle successful response here\r\n" + //$NON-NLS-1$
			"if(type == \"error\") alert(\"Error occurred.\");\r\n" +//$NON-NLS-1$ 
			"else\r\n" + //$NON-NLS-1$
			"{\r\n" + //$NON-NLS-1$
			"//getting error message from PHP\'s file upload script\r\n" + //$NON-NLS-1$
			"res = dojo.byId(\"dojoIoIframe\").contentWindow.document.getElementById(\"output\").innerHTML;\r\n" +//$NON-NLS-1$ 
			"//Incase of an error, display the error message\r\n" +//$NON-NLS-1$ 
			"if(res != \"true\") alert(res);\r\n" + //$NON-NLS-1$
			"else alert(\"File uploaded successfully.\");\r\n" +//$NON-NLS-1$ 
			"}\r\n" + //$NON-NLS-1$
			"}\r\n" + //$NON-NLS-1$
			"};\r\n" + //$NON-NLS-1$
			"var request = dojo.io.bind(bindArgs);\r\n" +  //$NON-NLS-1$
			"}\r\n" + //$NON-NLS-1$
			"";//$NON-NLS-1$

	static{
		new JSCommentContributor();
	}
	
	/**
	 * 
	 */
	public JSFormatterPreferencePage() {
		super(JSMimeType.MimeType,JSPlugin.getDefault().getPreferenceStore());
		
	}
	
	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.CodeFormatterPreferencePage#createConfigurationBlock(com.aptana.ide.internal.ui.dialogs.PreferencesAccess)
	 */
	protected ProfileConfigurationBlock createConfigurationBlock(
			PreferencesAccess access) {
		return new CodeFormatterConfigurationBlock(getProject(), access,
				JSMimeType.MimeType, JSPlugin.getDefault()
						.getPreferenceStore(), PREVIEW, JSPlugin.ID) {

			protected ModifyDialog createModifyDialog(Shell shell,
					Profile profile, ProfileManager profileManager,
					ProfileStore profileStore, boolean newProfile) {

				return new FormatterModifyDialog(shell, profile,
						profileManager, profileStore, newProfile,
						FORMATTER_DIALOG_PREFERENCE_KEY,
						DIALOGSTORE_LASTSAVELOADPATH, JSMimeType.MimeType) {

					@SuppressWarnings("unchecked")
					protected void addPages(Map values) {
						addTabPage(
								FormatterMessages.ModifyDialog_tabpage_control_statements_title,
								new ControlStatementsTabPage(this, values,
										JSMimeType.MimeType));
						addTabPage(
								FormatterMessages.ModifyDialog_tabpage_indentation_title,
								new IndentationTabPage(this, values,
										JSMimeType.MimeType));
						addTabPage(
								FormatterMessages.ModifyDialog_tabpage_blank_lines_title,
								new BlankLinesTabPage(this, values,
										JSMimeType.MimeType));
						addTabPage(
								FormatterMessages.ModifyDialog_tabpage_braces_title,
								new BracesTabPage(this, values,
										JSMimeType.MimeType));
					}

				};
			};

		};
	}

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.PropertyAndPreferencePage#getPreferencePageID()
	 */
	protected String getPreferencePageID() {
		return "com.aptana.ide.editor.js.preferences.FormatterPreferencePage"; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.PropertyAndPreferencePage#getPropertyPageID()
	 */
	protected String getPropertyPageID() {
		return "com.aptana.js.ui.propertyPages.CodeFormatterPreferencePage"; //$NON-NLS-1$
	}

	/**
	 * @see PreferencePage#doGetPreferenceStore()
	 */
    protected IPreferenceStore doGetPreferenceStore() {
        return JSPlugin.getDefault().getPreferenceStore();
    }


	

}
