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

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

import com.aptana.ide.editor.css.CSSPlugin;
import com.aptana.ide.editor.css.ICSSColorConstants;
import com.aptana.ide.editor.css.parsing.CSSMimeType;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.folding.FoldingExtensionPointLoader;
import com.aptana.ide.ui.editors.preferences.formatter.DefaultCodeFormatterConstants;

/**
 * Sets the default values for preferences in this plugin
 * 
 * @author Ingo Muschenetz
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{

	/**
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences()
	{

		IPreferenceStore store = CSSPlugin.getDefault().getPreferenceStore();

		if (Platform.getOS().equals(Platform.OS_WIN32))
		{
			store.setDefault(IPreferenceConstants.CSSEDITOR_BROWSER_PREVIEW_PREFERENCE, "IE,Firefox"); //$NON-NLS-1$
		}
		else if (Platform.getOS().equals(Platform.OS_LINUX))
		{
			store.setDefault(IPreferenceConstants.CSSEDITOR_BROWSER_PREVIEW_PREFERENCE, "Default"); //$NON-NLS-1$
		}
		else if (Platform.getOS().equals(Platform.OS_MACOSX))
		{
			store.setDefault(IPreferenceConstants.CSSEDITOR_BROWSER_PREVIEW_PREFERENCE, "Safari,Firefox"); //$NON-NLS-1$
		}
		store
				.setDefault(
						IPreferenceConstants.CSSEDITOR_BROWSER_TEMPLATE_PREFERENCE,
						"<h1>The H1 tag looks like this</h1>\n" //$NON-NLS-1$
								+ "<p>The paragraph tag after an H1 tag looks like this</p>\n" //$NON-NLS-1$
								+ "<h2>The H2 tag looks like this</h2>\n" //$NON-NLS-1$
								+ "<p>The paragraph tag after an H2 tag looks like this</p>\n" //$NON-NLS-1$
								+ "<h3>The H1 tag looks like this</h3>\n" //$NON-NLS-1$
								+ "<p>The paragraph tag after an H3 tag looks like this</p>\n" //$NON-NLS-1$
								+ "<h4>The H4 tag looks like this</h4>\n" //$NON-NLS-1$
								+ "<p>The paragraph tag after an H4 tag looks like this</p>\n" //$NON-NLS-1$
								+ "<h5>The H5 tag looks like this</h5>\n" //$NON-NLS-1$
								+ "<p>The paragraph tag after an H5 tag looks like this</p>\n" //$NON-NLS-1$
								+ "<ol>\n" //$NON-NLS-1$
								+ "<li><strong>This is a strong element in an ordered list</strong></li>\n" //$NON-NLS-1$
								+ "<li><i>This is an italic element in an ordered list</i></li>\n" //$NON-NLS-1$
								+ "<li><b>This is a bold element in an ordered list</b></li>\n" //$NON-NLS-1$
								+ "<li><em>This is an emphasized element in an ordered list</em></li>\n" //$NON-NLS-1$
								+ "<li>This is a regular element in an ordered list</li>\n" //$NON-NLS-1$
								+ "</ol>\n" //$NON-NLS-1$
								+ "<ul>\n" //$NON-NLS-1$
								+ "<li><strong>This is a strong element in an unordered list</strong></li>\n" //$NON-NLS-1$
								+ "<li><i>This is an italic element in an unordered list</i></li>\n" //$NON-NLS-1$
								+ "<li><b>This is a bold element in an unordered list</b></li>\n" //$NON-NLS-1$
								+ "<li><em>This is an emphasized element in an unordered list</em></li>\n" //$NON-NLS-1$
								+ "<li>This is a regular element in an unordered list</li>\n" //$NON-NLS-1$
								+ "</ul>\n" //$NON-NLS-1$
								+ "<blockquote>The block quote element looks like this</blockquote>\n" //$NON-NLS-1$
								+ "<pre>The pre element looks like this</pre>\n" //$NON-NLS-1$
								+ "<code>The code element looks like this</code>\n" //$NON-NLS-1$
								+ "<br/>\n" //$NON-NLS-1$
								+ "<span>The span element looks like this</span>\n" //$NON-NLS-1$
								+ "<br/>\n" //$NON-NLS-1$
								+ "<a href=\"#\">The anchor element looks like this</a>\n" //$NON-NLS-1$
								+ "<br/>\n" //$NON-NLS-1$
								+ "<br/>\n" //$NON-NLS-1$
								+ "<div style=\"float:left;margin:20px;width:200px;\">Text inside a div that is 200 pixels wide looks like this.  Text inside a div that is 200 pixels wide looks like this.  Text inside a div that is 200 pixels wide looks like this.  Text inside a div that is 200 pixels wide looks like this.  Text inside a div that is 200 pixels wide looks like this.  Text inside a div that is 200 pixels wide looks like this.  Text inside a div that is 200 pixels wide looks like this.  Text inside a div that is 200 pixels wide looks like this.  Text inside a div that is 200 pixels wide looks like this.  Text inside a div that is 200 pixels wide looks like this.</div>\n" //$NON-NLS-1$
								+ "<div style=\"float:left;margin:20px;width:400px;\">Text inside a div that is 400 pixels wide looks like this.  Text inside a div that is 400 pixels wide looks like this.  Text inside a div that is 400 pixels wide looks like this.  Text inside a div that is 400 pixels wide looks like this.  Text inside a div that is 400 pixels wide looks like this.  Text inside a div that is 400 pixels wide looks like this.  Text inside a div that is 400 pixels wide looks like this.  Text inside a div that is 400 pixels wide looks like this.  Text inside a div that is 400 pixels wide looks like this.  Text inside a div that is 400 pixels wide looks like this.  Text inside a div that is 400 pixels wide looks like this.  Text inside a div that is 400 pixels wide looks like this.  Text inside a div that is 400 pixels wide looks like this.  Text inside a div that is 400 pixels wide looks like this.  Text inside a div that is 400 pixels wide looks like this.  Text inside a div that is 400 pixels wide looks like this.  Text inside a div that is 400 pixels wide looks like this.  Text inside a div that is 400 pixels wide looks like this.  Text inside a div that is 400 pixels wide looks like this.  Text inside a div that is 400 pixels wide looks like this.  Text inside a div that is 400 pixels wide looks like this.  Text inside a div that is 400 pixels wide looks like this.</div>\n" //$NON-NLS-1$
								+ "<div style=\"float:left;margin:20px;width:600px;\">Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.  Text inside a div that is 600 pixels wide looks like this.</div>"); //$NON-NLS-1$

		store.setDefault(IPreferenceConstants.CSSEDITOR_BROWSER_USE_TEMPLATE_PREFERENCE, true);

		store.setDefault(com.aptana.ide.editors.preferences.IPreferenceConstants.AUTO_BRACKET_INSERTION, "INSERT"); //$NON-NLS-1$
		store.setDefault(com.aptana.ide.editors.preferences.IPreferenceConstants.CODE_ASSIST_AUTO_ACTIVATION, true);

		store.setDefault(IPreferenceConstants.CSSEDITOR_INSERT_SEMICOLON, true);

		store.setDefault(IPreferenceConstants.SHOW_CSS_TOOLBAR, true);
		store.setDefault(IPreferenceConstants.USE_TEMP_FILES_FOR_PREVIEW, true);

		PreferenceConverter.setDefault(store, IPreferenceConstants.CSSEDITOR_ERROR_COLOR, ICSSColorConstants.ERROR);
		PreferenceConverter.setDefault(store, IPreferenceConstants.CSSEDITOR_IDENTIFIER_COLOR,
				ICSSColorConstants.IDENTIFIER);
		PreferenceConverter.setDefault(store, IPreferenceConstants.CSSEDITOR_LITERAL_COLOR, ICSSColorConstants.LITERAL);
		PreferenceConverter.setDefault(store, IPreferenceConstants.CSSEDITOR_KEYWORD_COLOR, ICSSColorConstants.KEYWORD);
		PreferenceConverter.setDefault(store, IPreferenceConstants.CSSEDITOR_PUNCTUATOR_COLOR,
				ICSSColorConstants.PUNCTUATOR);
		PreferenceConverter.setDefault(store, IPreferenceConstants.CSSEDITOR_COMMENT_COLOR, ICSSColorConstants.COMMENT);
		PreferenceConverter.setDefault(store, IPreferenceConstants.CSSEDITOR_HASH_COLOR, ICSSColorConstants.HASH);
		PreferenceConverter.setDefault(store, IPreferenceConstants.CSSEDITOR_STRING_COLOR, ICSSColorConstants.STRING);

		store.setDefault(com.aptana.ide.editors.preferences.IPreferenceConstants.IGNORE_PROBLEMS,
				"~~~~~~~~-1~~~~.*You have no background-color with your color.*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*You have no color with your background-color.*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~C\\s*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*-moz-.*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*-o-*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*accelerator.*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*background-position-.*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*filter.*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*ime-mode.*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*layout-.*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*line-break.*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*overflow-.*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*page.*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*ruby-.*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*scrollbar-.*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*text-align-.*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*text-justify.*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*text-overflow.*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*text-shadow.*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*text-underline-position.*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*word-spacing.*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*word-wrap.*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*writing-mode.*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*zoom.*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*opacity.*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*hand is not a cursor value.*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*inline-block is not a display value.*" + //$NON-NLS-1$
						"####~~~~~~~~-1~~~~.*Property word-break doesn't exist.*" //$NON-NLS-1$
		);
		store.setDefault(IPreferenceConstants.CSSEDITOR_INITIAL_CONTENTS, "body {\n\t\n}\n"); //$NON-NLS-1$
		store.setDefault(IPreferenceConstants.CSSEDITOR_INITIAL_FILE_NAME, "new_file.css"); //$NON-NLS-1$
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, " "); //$NON-NLS-1$
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "4"); //$NON-NLS-1$
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK,
				DefaultCodeFormatterConstants.END_OF_LINE);
		store.setDefault(com.aptana.ide.editors.preferences.IPreferenceConstants.SHOW_PAIR_MATCHES,
				com.aptana.ide.editors.preferences.IPreferenceConstants.BOTH);
		PreferenceConverter.setDefault(store,
				com.aptana.ide.editors.preferences.IPreferenceConstants.PAIR_MATCHING_COLOR, new RGB(192, 192, 192));

		IPreferenceStore unifiedStore = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
		unifiedStore.setDefault(FoldingExtensionPointLoader.createEnablePreferenceId(CSSMimeType.MimeType), true);
	}
}
