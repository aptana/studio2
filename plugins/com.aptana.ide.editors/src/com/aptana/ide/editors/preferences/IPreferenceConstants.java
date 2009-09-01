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

import com.aptana.ide.editors.UnifiedEditorsPlugin;

/**
 * Contains all preferences for the com.aptana.ide.editors plugin To add a preference, create a static string with an
 * all-uppercase preference key. Then assign a identically-named string to it, prefixing it with the plugin name" i.e.
 * SHOW_WHITESPACE = "com.aptana.ide.editors.SHOW_WHITESPACE"
 * 
 * @author Ingo Muschenetz
 */
public interface IPreferenceConstants
{

	/**
	 * SHOW_WHITESPACE
	 */
	String SHOW_WHITESPACE = "com.aptana.ide.editors.SHOW_WHITESPACE"; //$NON-NLS-1$

	/**
	 * SHOW_PIANO_KEYS
	 */
	String SHOW_PIANO_KEYS = "com.aptana.ide.editors.SHOW_PIANO_KEYS"; //$NON-NLS-1$

	/**
	 * PIANO_KEY_DIFFERENCE
	 */
	String PIANO_KEY_DIFFERENCE = "com.aptana.ide.editors.PIANO_KEY_DIFFERENCE"; //$NON-NLS-1$

	/**
	 * CACHE_BUST_BROWSERS
	 */
	String CACHE_BUST_BROWSERS = "com.aptana.ide.editors.CACHE_BUST_BROWSERS"; //$NON-NLS-1$;

	/**
	 * Do we insert spaces instead of tabs when pressing the tab key?
	 */
	String INSERT_SPACES_FOR_TABS = "com.aptana.ide.editor.INSERT_SPACES_FOR_TABS"; //$NON-NLS-1$

	/**
	 * If pausing during typing, do we show code assist?
	 */
	String SHOW_CONTENT_ASSIST_ON_PAUSE = "com.aptana.ide.editor.SHOW_CONTENT_ASSIST_ON_PAUSE"; //$NON-NLS-1$

	/**
	 * Do we show code assist while typing backspace?
	 */
	String SHOW_CONTENT_ASSIST_ON_BACKSPACE = "com.aptana.ide.editor.SHOW_CONTENT_ASSIST_ON_BACKSPACE"; //$NON-NLS-1$

	/**
	 * Is word wrap enabled?
	 */
	String ENABLE_WORD_WRAP = "com.aptana.ide.editor.ENABLE_WORD_WRAP"; //$NON-NLS-1$

	/**
	 * To we auto-insert the matching character for items like (, ", etc.
	 */
	String ENABLE_CHARACTER_BALANCING = "com.aptana.ide.editor.ENABLE_CHARACTER_BALANCING"; //$NON-NLS-1$

	/**
	 * The delay before which we show code assist
	 */
	String CONTENT_ASSIST_DELAY = "com.aptana.ide.editor.CONTENT_ASSIST_DELAY"; //$NON-NLS-1$

	/**
	 * AUTO_BRACKET_INSERTION
	 */
	String AUTO_BRACKET_INSERTION = "com.aptana.ide.editor.AUTO_BRACKET_INSERTION"; //$NON-NLS-1$

	/**
	 * The types of user agents to display in code assist
	 */
	String USER_AGENT_PREFERENCE = "com.aptana.ide.editor.USER_AGENT_PREFERENCE"; //$NON-NLS-1$

	/**
	 * The max number of columns to color
	 */
	String COLORIZER_MAXCOLUMNS = "com.aptana.ide.editor.COLORIZER_MAXCOLUMNS"; //$NON-NLS-1$

	/**
	 * Enable highlighting of occurrences of selected text
	 */
	String COLORIZER_TEXT_HIGHLIGHT_ENABLED = "com.aptana.ide.editors.COLORIZER_TEXT_HIGHLIGHT_ENABLED"; //$NON-NLS-1$

	/**
	 * COLORIZER_TEXT_HIGHLIGHT_BACKGROUND_COLOR
	 */
	String COLORIZER_TEXT_HIGHLIGHT_BACKGROUND_COLOR = "com.aptana.ide.editors.COLORIZER_TEXT_HIGHLIGHT_BACKGROUND_COLOR"; //$NON-NLS-1$

	/**
	 * MATCH_BRACKETS
	 */
	String MATCH_BRACKETS = "com.aptana.ide.editors.MATCH_BRACKETS"; //$NON-NLS-1$

	/**
	 * MATCH_BRACKETS_COLOR
	 */
	String MATCH_BRACKETS_COLOR = "com.aptana.ide.editors.MATCH_BRACKETS_COLOR"; //$NON-NLS-1$

	/**
	 * IGNORE_PROBLEMS
	 */
	String IGNORE_PROBLEMS = "com.aptana.ide.editors.HTMLEDITOR_IGNORE_PROBLEMS"; //$NON-NLS-1$

	/**
	 * INSERT_ON_TAB
	 */
	String INSERT_ON_TAB = "com.aptana.ide.editors.INSERT_ON_TAB"; //$NON-NLS-1$

	/**
	 * 
	 */
	String CODE_ASSIST_EXPRESSIONS = "com.aptana.ide.editors.CODE_ASSIST_EXPRESSIONS"; //$NON-NLS-1$

	/**
	 * VALIDATORS_LIST
	 */
	String VALIDATORS_LIST = "com.aptana.ide.editors.VALIDATORS_LIST"; // $NIN-NLS-1$ //$NON-NLS-1$

	/**
	 * Value to be stored when user de-selects all the validators from list
	 */
	String VALIDATORS_NONE = "com.aptana.ide.editors.VALIDATORS_NONE"; //$NON-NLS-1$

	/**
	 * Expand editor options section in colorization preference page
	 */
	String EXPAND_EDITOR_OPTIONS = "com.aptana.ide.editors.EXPAND_EDITOR_OPTIONS"; //$NON-NLS-1$

	/**
	 * Expand tokens section in colorization preference page
	 */
	String EXPAND_TOKENS = "com.aptana.ide.editors.EXPAND_TOKENS"; //$NON-NLS-1$

	/**
	 * Expand regions section in colorization preference page
	 */
	String EXPAND_REGIONS = "com.aptana.ide.editors.EXPAND_REGIONS"; //$NON-NLS-1$

	/**
	 * Value that indicates if we should show lexeme debug information
	 */
	String SHOW_DEBUG_HOVER = "com.aptana.ide.editors.SHOW_DEBUG_HOVER"; //$NON-NLS-1$

	/**
	 * Do we auto-pop content assist?
	 */
	String CODE_ASSIST_AUTO_ACTIVATION = "com.aptana.ide.editors.CODE_ASSIST_AUTO_ACTIVATION"; //$NON-NLS-1$

	/**
	 * The characters to use when popping content assist
	 */
	String CODE_ASSIST_ACTIVATION_CHARACTERS = "com.aptana.ide.editors.CODE_ASSIST_ACTIVATION_CHARACTERS"; //$NON-NLS-1$

	/**
	 * Status of whether folding is enabled in the editors
	 */
	String EDITOR_FOLDING_ENABLED = "com.aptana.ide.editors.EDITOR_FOLDING_ENABLED"; //$NON-NLS-1$

	/**
	 * Status of initial folding for language and node
	 */
	String INITIAL_FOLDING_ENABLED = "com.aptana.ide.editor.INITIAL_FOLDING_ENABLED"; //$NON-NLS-1$

	/**
	 * Drag and drop enabled
	 */
	String DRAG_AND_DROP_ENABLED = "com.aptana.ide.editors.DRAG_AND_DROP_ENABLED"; //$NON-NLS-1$

	/**
	 * Pair matching color
	 */
	String PAIR_MATCHING_COLOR = "com.aptana.ide.editors.PAIR_MATCHING_COLOR"; //$NON-NLS-1$

	/**
	 * Pair matching preference
	 */
	String SHOW_PAIR_MATCHES = "com.aptana.ide.editors.SHOW_PAIR_MATCHES"; //$NON-NLS-1$

	/**
	 * Sort outline alphabetically
	 */
	String SORT_OUTLINE_ALPHABETICALLY = "com.aptana.ide.editors.SORT_OUTLINE_ALPHABETICALLY"; //$NON-NLS-1$

	/**
	 * NONE
	 */
	String NONE = "NONE"; //$NON-NLS-1$

	/**
	 * MATCHING
	 */
	String MATCHING = "MATCHING"; //$NON-NLS-1$

	/**
	 * BOTH
	 */
	String BOTH = "BOTH"; //$NON-NLS-1$

	/**
	 * SHOW_ERRORS
	 */
	String SHOW_ERRORS = "SHOW_ERRORS"; //$NON-NLS-1$

	/**
	 * SHOW_WARNINGS
	 */
	String SHOW_WARNINGS = "SHOW_WARNINGS"; //$NON-NLS-1$

	/**
	 * SHOW_INFOS
	 */
	String SHOW_INFOS = "SHOW_INFOS"; //$NON-NLS-1$

	/**
	 * A flag indicating, when true, that we should perform a scanning pass on the UI thread
	 * and defer full parsing on the delay thread
	 */
	String PARSER_OFF_UI = "com.aptana.ide.editors.PARSER_OFF_UI"; //$NON-NLS-1$

	String COMPILER_TASK_CASE_SENSITIVE = UnifiedEditorsPlugin.ID + ".tasks.caseSensitive"; //$NON-NLS-1$

	String COMPILER_TASK_TAGS = UnifiedEditorsPlugin.ID + ".tasks.tags"; //$NON-NLS-1$

	String COMPILER_TASK_PRIORITIES = UnifiedEditorsPlugin.ID + ".tasks.priorities"; //$NON-NLS-1$

	String COMPILER_TASK_PRIORITY_LOW = "LOW"; //$NON-NLS-1$

	String COMPILER_TASK_PRIORITY_HIGH = "HIGH"; //$NON-NLS-1$
}
