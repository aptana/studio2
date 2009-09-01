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

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

import com.aptana.ide.editor.html.HTMLEditor;
import com.aptana.ide.editor.html.HTMLPlugin;
import com.aptana.ide.editor.html.IHTMLColorConstants;
import com.aptana.ide.editor.html.parsing.HTMLMimeType;
import com.aptana.ide.editor.html.preview.HTMLPreviewPropertyPage;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.folding.FoldingExtensionPointLoader;
import com.aptana.ide.server.jetty.server.PreviewServerProvider;
import com.aptana.ide.ui.editors.preferences.formatter.DefaultCodeFormatterConstants;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{
	/**
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences()
	{
		IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();

		store.setDefault(IPreferenceConstants.AUTO_SAVE_PROMPTED, false);
		store.setDefault(IPreferenceConstants.HTML_EDITOR_VIEW_CHOICE, HTMLEditor.TAB_MODE);
		store.setDefault(IPreferenceConstants.AUTO_INSERT_CLOSE_TAGS, true);
		store.setDefault(IPreferenceConstants.AUTO_COMPLETE_CLOSE_TAGS, "OPEN"); //$NON-NLS-1$
		store.setDefault(IPreferenceConstants.AUTO_INDENT_ON_CARRIAGE_RETURN, true);
		store.setDefault(IPreferenceConstants.ATTRIBUTE_QUOTE_CHARACTER, ""); //$NON-NLS-1$
		store.setDefault(IPreferenceConstants.HTMLEDITOR_OUTLINER_ATTRIBUTE_LIST, "id src runat"); //$NON-NLS-1$
		store.setDefault(IPreferenceConstants.HTMLEDITOR_INSERT_EQUALS, true);
		store.setDefault(IPreferenceConstants.AUTO_MODIFY_PAIR_TAG, false);

		store.setDefault(IPreferenceConstants.HTMLEDITOR_DEFAULT_EXTENSION, ".html"); //$NON-NLS-1$
		if (Platform.getOS().equals(Platform.OS_WIN32))
		{
			store.setDefault(IPreferenceConstants.HTMLEDITOR_BROWSER_PREVIEW_PREFERENCE, "IE,Firefox"); //$NON-NLS-1$
		}
		else if (Platform.getOS().equals(Platform.OS_LINUX))
		{
			store.setDefault(IPreferenceConstants.HTMLEDITOR_BROWSER_PREVIEW_PREFERENCE, "Default"); //$NON-NLS-1$
		}
		else if (Platform.getOS().equals(Platform.OS_MACOSX))
		{
			store.setDefault(IPreferenceConstants.HTMLEDITOR_BROWSER_PREVIEW_PREFERENCE, "Safari,Firefox"); //$NON-NLS-1$
		}
		store.setDefault(IPreferenceConstants.HTMLEDITOR_TWO_BROWSER_WEIGHT_HORIZONTAL, "50,50"); //$NON-NLS-1$
		store.setDefault(IPreferenceConstants.HTMLEDITOR_TWO_BROWSER_WEIGHT_VERTICAL, "50,50"); //$NON-NLS-1$
		store.setDefault(IPreferenceConstants.HTMLEDITOR_EDITOR_BROWSER_WEIGHT_HORIZONTAL, "50,50"); //$NON-NLS-1$
		store.setDefault(IPreferenceConstants.HTMLEDITOR_EDITOR_BROWSER_WEIGHT_VERTICAL, "50,50"); //$NON-NLS-1$
		store.setDefault(IPreferenceConstants.HTMLEDITOR_HIGHLIGHT_START_END_TAGS, false);
		store.setDefault(IPreferenceConstants.LINK_CURSOR_WITH_HTML_TOOLBAR_TAB, true);
		store.setDefault(IPreferenceConstants.SHOW_HTML_TOOLBAR, true);
		store.setDefault(IPreferenceConstants.USE_TEMP_FILES_FOR_PREVIEW, true);

		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, " "); //$NON-NLS-1$
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "4"); //$NON-NLS-1$
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_DO_NOT_WRAP_TAGS,
				DefaultCodeFormatterConstants.INITIAL_TAGS_DO_NOT_WRAP);
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_DO_NOT_INDENT_TAGS,
				DefaultCodeFormatterConstants.INITIAL_TAGS_TO_NOT_INDENT);
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_WRAP_TAGS,
				DefaultCodeFormatterConstants.INITIAL_TAGS_TO_WRAP);
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK,
				DefaultCodeFormatterConstants.END_OF_LINE);
		store.setDefault(IPreferenceConstants.FOLDING_HTML_NODE_LIST, "body,div,head,html,script,style"); //$NON-NLS-1$

		store.setDefault(HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_TYPE,
				HTMLPreviewPropertyPage.APPENDED_SERVER_BASED_TYPE);
		store.setDefault(HTMLPreviewPropertyPage.HTML_PREVIEW_VALUE, PreviewServerProvider.INTERNAL_PREVIEW_SERVER_ID);

		store.setDefault(com.aptana.ide.editors.preferences.IPreferenceConstants.AUTO_BRACKET_INSERTION, "INSERT"); //$NON-NLS-1$
		// store.setDefault(com.aptana.ide.editors.preferences.IPreferenceConstants.CODE_ASSIST_EXPRESSIONS,
		// "~~~~class~~~~//CSSTextNode/@value[starts-with(., '.')]####~~~~on.*~~~~//function/@name"); //$NON-NLS-1$
		store.setDefault(com.aptana.ide.editors.preferences.IPreferenceConstants.CODE_ASSIST_AUTO_ACTIVATION, true);

		store.setDefault(IPreferenceConstants.HTMLEDITOR_INITIAL_CONTENTS,
				Messages.PreferenceInitializer_IntitialFileContents);
		store.setDefault(IPreferenceConstants.HTMLEDITOR_INITIAL_FILE_NAME, Messages.PreferenceInitializer_NewFileName);

		store.setDefault(IPreferenceConstants.FORMATTING_SMART_INDENT, true);
		store.setDefault(IPreferenceConstants.FORMATTING_INDENT_CONTENT, true);
		store.setDefault(IPreferenceConstants.FORMATTING_SPACES_SIZE, 2);
		store.setDefault(IPreferenceConstants.FORMATTING_TAB_SIZE, 4);
		store.setDefault(IPreferenceConstants.FORMATTING_SPACES_SIZE, 4);
		store.setDefault(DefaultCodeFormatterConstants.DO_NOT_WRAP_SIMPLE_TAGS, Boolean.FALSE.toString());
		store.setDefault(com.aptana.ide.editors.preferences.IPreferenceConstants.SHOW_PAIR_MATCHES,
				com.aptana.ide.editors.preferences.IPreferenceConstants.BOTH);

		IPreferenceStore unifiedStore = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
		unifiedStore.setDefault(FoldingExtensionPointLoader.createEnablePreferenceId(HTMLMimeType.MimeType), true);

		store.setDefault(com.aptana.ide.editors.preferences.IPreferenceConstants.IGNORE_PROBLEMS,
				"~~~~~~~~-1~~~~.*jaxer:include.*" //$NON-NLS-1$
		);

		PreferenceConverter.setDefault(store, IPreferenceConstants.HTMLEDITOR_ERROR_COLOR, IHTMLColorConstants.COMMENT);
		PreferenceConverter.setDefault(store, IPreferenceConstants.HTMLEDITOR_LITERAL_COLOR,
				IHTMLColorConstants.LITERAL);
		PreferenceConverter.setDefault(store, IPreferenceConstants.HTMLEDITOR_KEYWORD_COLOR,
				IHTMLColorConstants.KEYWORD);
		PreferenceConverter.setDefault(store, IPreferenceConstants.HTMLEDITOR_PUNCTUATOR_COLOR,
				IHTMLColorConstants.PUNCTUATOR);
		PreferenceConverter.setDefault(store, IPreferenceConstants.HTMLEDITOR_PI_OPEN_CLOSE_COLOR,
				IHTMLColorConstants.PI_OPEN_CLOSE);
		PreferenceConverter.setDefault(store, IPreferenceConstants.HTMLEDITOR_PI_TEXT_COLOR,
				IHTMLColorConstants.PI_TEXT);
		PreferenceConverter.setDefault(store, IPreferenceConstants.HTMLEDITOR_TAG_OPEN_CLOSE_COLOR,
				IHTMLColorConstants.TAG_OPEN_CLOSE);
		PreferenceConverter.setDefault(store, IPreferenceConstants.HTMLEDITOR_COMMENT_OPEN_CLOSE_COLOR,
				IHTMLColorConstants.COMMENT_OPEN_CLOSE);
		PreferenceConverter.setDefault(store, IPreferenceConstants.HTMLEDITOR_NAME_COLOR, IHTMLColorConstants.NAME);
		PreferenceConverter.setDefault(store, IPreferenceConstants.HTMLEDITOR_ATTRIBUTE_COLOR,
				IHTMLColorConstants.ATTRIBUTE);
		PreferenceConverter.setDefault(store, IPreferenceConstants.HTMLEDITOR_ATTRIBUTE_VALUE_COLOR,
				IHTMLColorConstants.ATTRIBUTE_VALUE);
		PreferenceConverter.setDefault(store, IPreferenceConstants.HTMLEDITOR_EQUAL_COLOR, IHTMLColorConstants.EQUAL);
		PreferenceConverter.setDefault(store, IPreferenceConstants.HTMLEDITOR_TEXT_COLOR, IHTMLColorConstants.TEXT);
		PreferenceConverter.setDefault(store, IPreferenceConstants.HTMLEDITOR_ENTITY_REF_COLOR,
				IHTMLColorConstants.ENTITY_REF);
		PreferenceConverter.setDefault(store, IPreferenceConstants.HTMLEDITOR_CHAR_REF_COLOR,
				IHTMLColorConstants.CHAR_REF);
		PreferenceConverter.setDefault(store, IPreferenceConstants.HTMLEDITOR_PE_REF_COLOR, IHTMLColorConstants.PE_REF);
		PreferenceConverter.setDefault(store, IPreferenceConstants.HTMLEDITOR_CDATA_START_END_COLOR,
				IHTMLColorConstants.CDATA_START_END);
		PreferenceConverter.setDefault(store, IPreferenceConstants.HTMLEDITOR_CDATA_TEXT_COLOR,
				IHTMLColorConstants.CDATA_TEXT);
		PreferenceConverter.setDefault(store, IPreferenceConstants.HTMLEDITOR_DECL_START_END_COLOR,
				IHTMLColorConstants.DECL_START_END);
		PreferenceConverter.setDefault(store, IPreferenceConstants.HTMLEDITOR_COMMENT_COLOR,
				IHTMLColorConstants.COMMENT);

		PreferenceConverter.setDefault(store,
				com.aptana.ide.editors.preferences.IPreferenceConstants.PAIR_MATCHING_COLOR, new RGB(192, 192, 192));
	}

}
