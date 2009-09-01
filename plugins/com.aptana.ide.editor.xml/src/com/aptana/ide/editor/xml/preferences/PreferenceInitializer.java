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
package com.aptana.ide.editor.xml.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.xml.IXMLColorConstants;
import com.aptana.ide.editor.xml.XMLPlugin;
import com.aptana.ide.editor.xml.parsing.XMLMimeType;
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

		IPreferenceStore store = XMLPlugin.getDefault().getPreferenceStore();

		store.setDefault(IPreferenceConstants.ATTRIBUTE_QUOTE_CHARACTER, StringUtils.EMPTY);
		store.setDefault(com.aptana.ide.editors.preferences.IPreferenceConstants.AUTO_BRACKET_INSERTION, "INSERT"); //$NON-NLS-1$
		store.setDefault(com.aptana.ide.editors.preferences.IPreferenceConstants.CODE_ASSIST_AUTO_ACTIVATION, true);
		store.setDefault(IPreferenceConstants.AUTO_INSERT_CLOSE_TAGS, true);
		store.setDefault(IPreferenceConstants.XMLEDITOR_OUTLINER_ATTRIBUTE_LIST, "id"); //$NON-NLS-1$
		store.setDefault(IPreferenceConstants.XMLEDITOR_INITIAL_FILE_NAME, "new_file.xml"); //$NON-NLS-1$
		store.setDefault(IPreferenceConstants.XMLEDITOR_INITIAL_CONTENTS, "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n "); //$NON-NLS-1$
		store.setDefault(IPreferenceConstants.XMLEDITOR_HIGHLIGHT_START_END_TAGS, false);
		store.setDefault(IPreferenceConstants.SHOW_XML_TOOLBAR, false);
		store.setDefault(IPreferenceConstants.AUTO_MODIFY_PAIR_TAG, true);
		store.setDefault(IPreferenceConstants.AUTO_INDENT_ON_CARRIAGE_RETURN, true);

		IPreferenceStore unifiedStore = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
		unifiedStore.setDefault(FoldingExtensionPointLoader.createEnablePreferenceId(XMLMimeType.MimeType), true);

		PreferenceConverter.setDefault(store, IPreferenceConstants.XMLEDITOR_ERROR_COLOR, IXMLColorConstants.COMMENT);
		PreferenceConverter.setDefault(store, IPreferenceConstants.XMLEDITOR_LITERAL_COLOR, IXMLColorConstants.LITERAL);
		PreferenceConverter.setDefault(store, IPreferenceConstants.XMLEDITOR_KEYWORD_COLOR, IXMLColorConstants.KEYWORD);
		PreferenceConverter.setDefault(store, IPreferenceConstants.XMLEDITOR_PUNCTUATOR_COLOR,
				IXMLColorConstants.PUNCTUATOR);
		PreferenceConverter.setDefault(store, IPreferenceConstants.XMLEDITOR_PI_OPEN_CLOSE_COLOR,
				IXMLColorConstants.PI_OPEN_CLOSE);
		PreferenceConverter.setDefault(store, IPreferenceConstants.XMLEDITOR_PI_TEXT_COLOR, IXMLColorConstants.PI_TEXT);
		PreferenceConverter.setDefault(store, IPreferenceConstants.XMLEDITOR_TAG_OPEN_CLOSE_COLOR,
				IXMLColorConstants.TAG_OPEN_CLOSE);
		PreferenceConverter.setDefault(store, IPreferenceConstants.XMLEDITOR_NAME_COLOR, IXMLColorConstants.NAME);
		PreferenceConverter.setDefault(store, IPreferenceConstants.XMLEDITOR_ATTRIBUTE_COLOR,
				IXMLColorConstants.ATTRIBUTE);
		PreferenceConverter.setDefault(store, IPreferenceConstants.XMLEDITOR_ATTRIBUTE_VALUE_COLOR,
				IXMLColorConstants.ATTRIBUTE_VALUE);
		PreferenceConverter.setDefault(store, IPreferenceConstants.XMLEDITOR_EQUAL_COLOR, IXMLColorConstants.EQUAL);
		PreferenceConverter.setDefault(store, IPreferenceConstants.XMLEDITOR_TEXT_COLOR, IXMLColorConstants.TEXT);
		PreferenceConverter.setDefault(store, IPreferenceConstants.XMLEDITOR_ENTITY_REF_COLOR,
				IXMLColorConstants.ENTITY_REF);
		PreferenceConverter.setDefault(store, IPreferenceConstants.XMLEDITOR_CHAR_REF_COLOR,
				IXMLColorConstants.CHAR_REF);
		PreferenceConverter.setDefault(store, IPreferenceConstants.XMLEDITOR_PE_REF_COLOR, IXMLColorConstants.PE_REF);
		PreferenceConverter.setDefault(store, IPreferenceConstants.XMLEDITOR_CDATA_START_END_COLOR,
				IXMLColorConstants.CDATA_START_END);
		PreferenceConverter.setDefault(store, IPreferenceConstants.XMLEDITOR_CDATA_TEXT_COLOR,
				IXMLColorConstants.CDATA_TEXT);
		PreferenceConverter.setDefault(store, IPreferenceConstants.XMLEDITOR_DECL_START_END_COLOR,
				IXMLColorConstants.DECL_START_END);
		PreferenceConverter.setDefault(store, IPreferenceConstants.XMLEDITOR_COMMENT_COLOR, IXMLColorConstants.COMMENT);
		PreferenceConverter.setDefault(store,
				com.aptana.ide.editors.preferences.IPreferenceConstants.PAIR_MATCHING_COLOR, new RGB(192, 192, 192));
		store.setDefault(com.aptana.ide.editors.preferences.IPreferenceConstants.SHOW_PAIR_MATCHES,
				com.aptana.ide.editors.preferences.IPreferenceConstants.BOTH);

		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, " "); //$NON-NLS-1$
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "4"); //$NON-NLS-1$
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_DO_NOT_WRAP_TAGS2, ""); //$NON-NLS-1$
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_WRAP_TAGS2, ""); //$NON-NLS-1$
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK,
				DefaultCodeFormatterConstants.END_OF_LINE);
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_PRESERVE_EXTRA_CARRIAGE_RETURNS, Boolean.TRUE
				.toString());
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_PRESERVE_WHITESPACE_IN_CDATA, Boolean.TRUE.toString());
		store.setDefault(DefaultCodeFormatterConstants.DO_NOT_WRAP_SIMPLE_TAGS, Boolean.FALSE.toString());
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_SPACES_BEFORE_ATTRS_ON_MULTILINE, "1"); //$NON-NLS-1$

	}
}
