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
package com.aptana.ide.editors.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.aptana.ide.editors.UnifiedEditorsPlugin;

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
		IPreferenceStore store = UnifiedEditorsPlugin.getDefault().getPreferenceStore();

		store.setDefault(com.aptana.ide.core.preferences.IPreferenceConstants.SHOW_LIVE_HELP, true);

		store.setDefault(IPreferenceConstants.CACHE_BUST_BROWSERS, true);
		store.setDefault(IPreferenceConstants.PIANO_KEY_DIFFERENCE, 7);
		store.setDefault(IPreferenceConstants.MATCH_BRACKETS, "true"); //$NON-NLS-1$
		store.setDefault(IPreferenceConstants.MATCH_BRACKETS_COLOR, StringConverter.asString(new RGB(150, 150, 150)));
		store.setDefault(IPreferenceConstants.SHOW_WHITESPACE, false);
		store.setDefault(IPreferenceConstants.ENABLE_WORD_WRAP, false);
		store.setDefault(IPreferenceConstants.USER_AGENT_PREFERENCE, "IE,Mozilla"); //$NON-NLS-1$
		store.setDefault(IPreferenceConstants.INSERT_ON_TAB, false);

		store.setDefault(AbstractTextEditor.PREFERENCE_NAVIGATION_SMART_HOME_END, false);
		store.setDefault(IPreferenceConstants.CONTENT_ASSIST_DELAY, 200);
		store.setDefault(IPreferenceConstants.COLORIZER_TEXT_HIGHLIGHT_ENABLED, false);
		store.setDefault(IPreferenceConstants.COLORIZER_TEXT_HIGHLIGHT_BACKGROUND_COLOR, StringConverter
				.asString(new RGB(212, 212, 212)));
		store.setDefault(IPreferenceConstants.COLORIZER_MAXCOLUMNS, 500);
		store.setDefault(IPreferenceConstants.EXPAND_EDITOR_OPTIONS, true);
		store.setDefault(IPreferenceConstants.EXPAND_TOKENS, true);
		store.setDefault(IPreferenceConstants.EXPAND_REGIONS, false);
		store.setDefault(IPreferenceConstants.SHOW_ERRORS, true);
		store.setDefault(IPreferenceConstants.SHOW_WARNINGS, false);
		store.setDefault(IPreferenceConstants.SHOW_INFOS, false);
		store.setDefault(IPreferenceConstants.DRAG_AND_DROP_ENABLED, true);
		store.setDefault(IPreferenceConstants.SORT_OUTLINE_ALPHABETICALLY, false);
		store.setDefault(IPreferenceConstants.PARSER_OFF_UI, true);
		
		// Tasks
		store.setDefault(IPreferenceConstants.COMPILER_TASK_CASE_SENSITIVE, true);
		store.setDefault(IPreferenceConstants.COMPILER_TASK_TAGS, "TODO,FIXME,XXX,OPTIMIZE");
		store.setDefault(IPreferenceConstants.COMPILER_TASK_PRIORITIES, "NORMAL,HIGH,NORMAL,LOW");		
		
		// These two _must_ be set as a preference, or UnifiedEditor will crash
		// hard without
		// warning on initialization.
		// store.setDefault(ColorizerPreferencePage.HRD_SET, "default");
		// store.setDefault(ColorizerPreferencePage.USE_BACK, true);
		//		
		// store.setDefault(ColorizerPreferencePage.FULL_BACK, true);
		// store.setDefault(ColorizerPreferencePage.PAIRS_MATCH,
		// "PAIRS_OUTLINE");
		//
		// store.setDefault(ColorizerPreferencePage.HORZ_CROSS, true);
		// store.setDefault(ColorizerPreferencePage.VERT_CROSS, true);

		// To set preferences for AbstractTextEditor
		store = org.eclipse.ui.internal.editors.text.EditorsPlugin.getDefault().getPreferenceStore();
		store.setToDefault(AbstractTextEditor.PREFERENCE_NAVIGATION_SMART_HOME_END);
		store.setDefault(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_UNDO_HISTORY_SIZE, 500);

	}

}
