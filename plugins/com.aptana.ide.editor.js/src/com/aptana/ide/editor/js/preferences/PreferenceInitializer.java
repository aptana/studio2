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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

import com.aptana.ide.editor.js.IJSColorConstants;
import com.aptana.ide.editor.js.JSLanguageEnvironment;
import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.jscomment.IJSCommentColorConstants;
import com.aptana.ide.editor.scriptdoc.IScriptDocColorConstants;
import com.aptana.ide.editor.scriptdoc.parsing.ScriptDocMimeType;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.folding.FoldingExtensionPointLoader;
import com.aptana.ide.ui.editors.preferences.formatter.CommentsTabPage;
import com.aptana.ide.ui.editors.preferences.formatter.DefaultCodeFormatterConstants;

/**
 * Sets the default values for preferences in this plug-in
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

		IPreferenceStore store = JSPlugin.getDefault().getPreferenceStore();
		store.setDefault(IPreferenceConstants.ENABLE_NO_VALIDATE_COMMENT, false);
		store.setDefault(IPreferenceConstants.SHOW_JS_TOOLBAR, true);
		store.setDefault(IPreferenceConstants.PREFERENCE_COMMENT_INDENT_USE_STAR, true);
		store.setDefault(com.aptana.ide.editors.preferences.IPreferenceConstants.AUTO_BRACKET_INSERTION, "INSERT"); //$NON-NLS-1$
		store.setDefault(com.aptana.ide.editors.preferences.IPreferenceConstants.CODE_ASSIST_AUTO_ACTIVATION, true);
		store.setDefault(IPreferenceConstants.AUTO_FORMAT_ON_CLOSE_CURLY, true);
		store.setDefault(IPreferenceConstants.AUTO_INDENT_ON_CARRIAGE_RETURN, true);
		store.setDefault(IPreferenceConstants.PREFERENCE_PRIVATE_FIELD_INDICATOR, "_"); //$NON-NLS-1$
		store.setDefault(com.aptana.ide.editors.preferences.IPreferenceConstants.VALIDATORS_LIST,
				Messages.PreferenceInitializer_Mozilla_javascript_validator);

		store.setDefault(IPreferenceConstants.LOADED_ENVIRONMENTS, JSLanguageEnvironment.DOM_5 + "," + JSLanguageEnvironment.DOM_3 + "," + JSLanguageEnvironment.DOM_1_2 + "," //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ JSLanguageEnvironment.DOM_0);

		PreferenceConverter.setDefault(store, IPreferenceConstants.JSEDITOR_PROPOSALS_FOREGROUND_COLOR,
				IJSColorConstants.PROPOSALS_FOREGROUND);
		PreferenceConverter.setDefault(store, IPreferenceConstants.JSEDITOR_PROPOSALS_BACKGROUND_COLOR,
				IJSColorConstants.PROPOSALS_BACKGROUND);
		PreferenceConverter.setDefault(store, IPreferenceConstants.JSEDITOR_DEFAULT_COLOR, IJSColorConstants.DEFAULT);
		PreferenceConverter.setDefault(store, IPreferenceConstants.JSEDITOR_KEYWORD_COLOR, IJSColorConstants.KEYWORD);
		PreferenceConverter.setDefault(store, IPreferenceConstants.JSEDITOR_NATIVETYPE_COLOR,
				IJSColorConstants.NATIVETYPE);
		PreferenceConverter.setDefault(store, IPreferenceConstants.JSEDITOR_PUNCTUATOR_COLOR,
				IJSColorConstants.PUNCTUATOR);
		PreferenceConverter.setDefault(store, IPreferenceConstants.JSEDITOR_RESERVED_COLOR, IJSColorConstants.RESERVED);
		PreferenceConverter.setDefault(store, IPreferenceConstants.JSEDITOR_FUTURE_COLOR, IJSColorConstants.FUTURE);
		PreferenceConverter.setDefault(store, IPreferenceConstants.JSEDITOR_STRING_COLOR, IJSColorConstants.STRING);
		PreferenceConverter.setDefault(store, IPreferenceConstants.JSEDITOR_NUMBER_COLOR, IJSColorConstants.NUMBER);
		PreferenceConverter.setDefault(store, IPreferenceConstants.JSEDITOR_LITERAL_COLOR, IJSColorConstants.LITERAL);
		PreferenceConverter.setDefault(store, IPreferenceConstants.JSEDITOR_COMMENT_COLOR, IJSColorConstants.COMMENT);
		PreferenceConverter.setDefault(store, IPreferenceConstants.JSEDITOR_DOCUMENTATION_COLOR,
				IJSColorConstants.DOCUMENTATION);
		PreferenceConverter.setDefault(store, IPreferenceConstants.JSEDITOR_ERROR_COLOR, IJSColorConstants.ERROR);
		PreferenceConverter.setDefault(store, IPreferenceConstants.JSEDITOR_HTMLDOM_COLOR, IJSColorConstants.HTMLDOM);
		PreferenceConverter.setDefault(store, IPreferenceConstants.JSEDITOR_JSCORE_COLOR, IJSColorConstants.JSCORE);

		PreferenceConverter.setDefault(store, IPreferenceConstants.JSCOMMENTEDITOR_TEXT_COLOR,
				IJSCommentColorConstants.TEXT);
		PreferenceConverter.setDefault(store, IPreferenceConstants.JSCOMMENTEDITOR_DELIMITER_COLOR,
				IJSCommentColorConstants.DELIMITER);

		PreferenceConverter.setDefault(store, IPreferenceConstants.SCRIPTDOCEDITOR_TEXT_COLOR,
				IScriptDocColorConstants.TEXT);
		PreferenceConverter.setDefault(store, IPreferenceConstants.SCRIPTDOCEDITOR_KEYWORDCOLOR,
				IScriptDocColorConstants.KEYWORD);
		PreferenceConverter.setDefault(store, IPreferenceConstants.SCRIPTDOCEDITOR_USER_KEYWORD_COLOR,
				IScriptDocColorConstants.KEYWORD);
		PreferenceConverter.setDefault(store, IPreferenceConstants.SCRIPTDOCEDITOR_IDENTIFIER_COLOR,
				IScriptDocColorConstants.IDENTIFIER);
		PreferenceConverter.setDefault(store, IPreferenceConstants.SCRIPTDOCEDITOR_PUNCTUATOR_COLOR,
				IScriptDocColorConstants.PUNCTUATOR);
		PreferenceConverter.setDefault(store, IPreferenceConstants.SCRIPTDOCEDITOR_DELIMITER_COLOR,
				IScriptDocColorConstants.DELIMITER);

		store
				.setDefault(
						com.aptana.ide.editors.preferences.IPreferenceConstants.IGNORE_PROBLEMS,
						"~~~~~~~~-1~~~~Undefined variable.*####~~~~~~~~-1~~~~Expected an identifier and instead saw 'const'.####~~~~~~~~-1~~~~.*identifier is a reserved word.*####~~~~~~~~-1~~~~All debugger statements should be removed."); //$NON-NLS-1$
		store.setDefault(IPreferenceConstants.JSEDITOR_INITIAL_CONTENTS,
				Messages.PreferenceInitializer_InitialFileContents);
		store.setDefault(IPreferenceConstants.JSEDITOR_INITIAL_FILE_NAME, Messages.PreferenceInitializer_NewFileName);
		store.setDefault(IPreferenceConstants.SCRIPTDOCEDITOR_INITIAL_FILE_NAME, Messages.PreferenceInitializer_Default_sdoc_filename);
		store.setDefault(com.aptana.ide.editors.preferences.IPreferenceConstants.SHOW_PAIR_MATCHES,
				com.aptana.ide.editors.preferences.IPreferenceConstants.BOTH);
		PreferenceConverter.setDefault(store,
				com.aptana.ide.editors.preferences.IPreferenceConstants.PAIR_MATCHING_COLOR, new RGB(192, 192, 192));

		IPreferenceStore unifiedStore = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
		unifiedStore.setDefault(FoldingExtensionPointLoader.createEnablePreferenceId(JSMimeType.MimeType), true);
		unifiedStore.setDefault(FoldingExtensionPointLoader.createEnablePreferenceId(ScriptDocMimeType.MimeType), true);

		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_IF_STATEMENT,
				CommentsTabPage.INSERT);
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_RETURN_STATEMENT,
				CommentsTabPage.INSERT);
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_ELSE_IN_IF_STATEMENT,
				CommentsTabPage.INSERT);
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_CATCH_IN_TRY_STATEMENT,
				CommentsTabPage.INSERT);
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_FINALLY_IN_TRY_STATEMENT,
				CommentsTabPage.INSERT);
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_WHILE_IN_DO_STATEMENT,
				CommentsTabPage.INSERT);
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_KEEP_THEN_STATEMENT_ON_SAME_LINE, Boolean.FALSE
				.toString());
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_IF_ON_ONE_LINE, Boolean.FALSE.toString());
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_KEEP_ELSE_STATEMENT_ON_SAME_LINE, Boolean.FALSE
				.toString());
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_COMPACT_ELSE_IF, Boolean.FALSE.toString());
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_KEEP_GUARDIAN_CLAUSE_ON_ONE_LINE, Boolean.FALSE
				.toString());
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, " "); //$NON-NLS-1$
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "4"); //$NON-NLS-1$
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_BODY, Boolean.TRUE
				.toString());
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_BLOCK, Boolean.TRUE
				.toString());
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_SWITCH,
				Boolean.TRUE.toString());
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_CASES, Boolean.TRUE
				.toString());
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_INDENT_BREAKS_COMPARE_TO_CASES, Boolean.TRUE
				.toString());
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_METHOD, "1"); //$NON-NLS-1$
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AT_BEGINNING_OF_METHOD_BODY, "0"); //$NON-NLS-1$
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_PRESERVE_EXTRA_CARRIAGE_RETURNS, Boolean.TRUE
				.toString()); //$NON-NLS-1$

		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_METHOD_DECLARATION,
				DefaultCodeFormatterConstants.END_OF_LINE);
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK,
				DefaultCodeFormatterConstants.END_OF_LINE);
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK_IN_CASE,
				DefaultCodeFormatterConstants.END_OF_LINE);
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SWITCH,
				DefaultCodeFormatterConstants.END_OF_LINE);
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ARRAY_INITIALIZER,
				DefaultCodeFormatterConstants.END_OF_LINE);
		store.setDefault(DefaultCodeFormatterConstants.FORMATTER_KEEP_EMPTY_ARRAY_INITIALIZER_ON_ONE_LINE, Boolean.TRUE
				.toString());
	}
}