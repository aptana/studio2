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
package com.aptana.ide.editor.js.formatting;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.ui.editors.preferences.formatter.DefaultCodeFormatterConstants;

/**
 * @author Pavel Petrochenko
 */
public class JSCodeFormatterOptions
{

	/**
	 * END_LINE
	 */
	public static final int END_LINE = 0;
	/**
	 * NEXT_LINE
	 */
	public static final int NEXT_LINE = 1;
	/**
	 * NEXT_LINE_SHIFTED
	 */
	public static final int NEXT_LINE_SHIFTED = 2;

	/**
	 * insertNewLineBeforeElse
	 */
	public boolean insertNewLineBeforeElse;
	/**
	 * insertNewLineBeforeCatch
	 */
	public boolean insertNewLineBeforeCatch;
	/**
	 * insertNewLineBeforeFinally
	 */
	public boolean insertNewLineBeforeFinally;
	/**
	 * insertNewLineBeforeWhile
	 */
	public boolean insertNewLineBeforeWhile;

	/**
	 * Insert a new line before return statements
	 */
	public boolean insertNewLineBeforeReturn;

	/**
	 * Insert a new line before if statements
	 */
	public boolean insertNewLineBeforeIf;

	/**
	 * keepThenSameLine
	 */
	public boolean keepThenSameLine;
	/**
	 * keepSimpleIfOnOneLine
	 */
	public boolean keepSimpleIfOnOneLine;
	/**
	 * keepElseStatementOnSameLine
	 */
	public boolean keepElseStatementOnSameLine;
	/**
	 * compactElseIf
	 */
	public boolean compactElseIf;
	/**
	 * keepGuardianClauseOnOneLine
	 */
	public boolean keepGuardianClauseOnOneLine;
	/**
	 * formatterTabChar
	 */
	public String formatterTabChar;
	/**
	 * tabSize
	 */
	public int tabSize;
	/**
	 * indentStatementsCompareToBody
	 */
	public boolean indentStatementsCompareToBody;
	/**
	 * indentStatementsCompareToBlock
	 */
	public boolean indentStatementsCompareToBlock;
	/**
	 * indentStatementsCompareToSwitch
	 */
	public boolean indentStatementsCompareToSwitch;
	/**
	 * indentStatementsCompareToCases
	 */
	public boolean indentStatementsCompareToCases;
	/**
	 * indentBreaksCompareToCases
	 */
	public boolean indentBreaksCompareToCases;
	/**
	 * indentEmptyLines
	 */
	public boolean indentEmptyLines;

	/**
	 * blankLinesBeforeMethod
	 */
	public int blankLinesBeforeMethod = 1;

	/**
	 * blankLinesInStartOfMethodBody
	 */
	public int blankLinesInStartOfMethodBody;

	/**
	 * keepEmptyArrayInitializerOnOneLine
	 */
	public boolean preserveLineBreaks;

	/**
	 * formatterBracePositionForMethodDecl
	 */
	public int formatterBracePositionForMethodDecl;
	/**
	 * formatterBracePositionForBlock
	 */
	public int formatterBracePositionForBlock;
	/**
	 * formatterBracePositionForBlockInCase
	 */
	public int formatterBracePositionForBlockInCase;
	/**
	 * formatterBracePositionForBlockInSwitch
	 */
	public int formatterBracePositionForBlockInSwitch;
	/**
	 * formatterBracePositionForArrayInitializer
	 */
	public int formatterBracePositionForArrayInitializer;
	/**
	 * keepEmptyArrayInitializerOnOneLine
	 */
	public boolean keepEmptyArrayInitializerOnOneLine;
	
	
	/**
	 * addSpaceAfterFunctionDeclaration
	 */
	public boolean addSpaceAfterFunctionDeclaration;

	/**
	 * do formatting
	 */
	public boolean doFormatting = true;

	/**
	 * 
	 */
	public JSCodeFormatterOptions()
	{
		initFromPreferences();
	}
	
	/**
	 * @return compact option map
	 */
	public static Map getCompactJSOptionsMap(){
		Map javaConventionsSettings = DefaultCodeFormatterConstants.getJavaConventionsSettings();
		javaConventionsSettings.put(DefaultCodeFormatterConstants.FORMATTER_COMPACT_ELSE_IF, Boolean.TRUE.toString());
		javaConventionsSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_RETURN_STATEMENT, Boolean.FALSE.toString());
		javaConventionsSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_IF_STATEMENT, Boolean.FALSE.toString());
		javaConventionsSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_ELSE_IN_IF_STATEMENT, Boolean.FALSE.toString());		
		javaConventionsSettings.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_THEN_STATEMENT_ON_SAME_LINE, Boolean.FALSE.toString());
		javaConventionsSettings.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_IF_ON_ONE_LINE, Boolean.FALSE.toString());
		javaConventionsSettings.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_ELSE_STATEMENT_ON_SAME_LINE, Boolean.TRUE.toString());
		return javaConventionsSettings;
	}
	
	/**
	 * @return compact option map
	 */
	public static Map getKeepThenMap(){
		Map javaConventionsSettings = DefaultCodeFormatterConstants.getJavaConventionsSettings();
		javaConventionsSettings.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_THEN_STATEMENT_ON_SAME_LINE, Boolean.TRUE.toString());
		return javaConventionsSettings;
	}
	
	/**
	 * @return compact option map
	 */
	public static Map getKeepSimpleIfMap(){
		Map javaConventionsSettings = DefaultCodeFormatterConstants.getJavaConventionsSettings();
		javaConventionsSettings.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_IF_ON_ONE_LINE, Boolean.TRUE.toString());
		return javaConventionsSettings;
	}
	
	/**
	 * @return compact option map
	 */
	public static Map getKeepSimpleIfMap1(){
		Map javaConventionsSettings = DefaultCodeFormatterConstants.getJavaConventionsSettings();
		javaConventionsSettings.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_RETURN_STATEMENT, Boolean.FALSE.toString());
		javaConventionsSettings.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_IF_ON_ONE_LINE, Boolean.TRUE.toString());
		return javaConventionsSettings;
	}
	
	/**
	 * @return compact option map
	 */
	public static Map getKeepElseIfMap(){
		Map javaConventionsSettings = DefaultCodeFormatterConstants.getJavaConventionsSettings();
		javaConventionsSettings.put(DefaultCodeFormatterConstants.FORMATTER_COMPACT_ELSE_IF, Boolean.TRUE.toString());
		return javaConventionsSettings;
	}
	
	/**
	 * @return compact option map
	 */
	public static Map getKeepGuardianMap(){
		Map javaConventionsSettings = DefaultCodeFormatterConstants.getJavaConventionsSettings();
		javaConventionsSettings.put(DefaultCodeFormatterConstants.FORMATTER_KEEP_GUARDIAN_CLAUSE_ON_ONE_LINE, Boolean.TRUE.toString());
		return javaConventionsSettings;
	}

	private void initFromPreferences()
	{
		IPreferenceStore preferenceStore = JSPlugin.getDefault().getPreferenceStore();
		insertNewLineBeforeReturn = parseInsert(preferenceStore
				.getString(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_RETURN_STATEMENT));
		insertNewLineBeforeIf = parseInsert(preferenceStore
				.getString(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_IF_STATEMENT));
		insertNewLineBeforeElse = parseInsert(preferenceStore
				.getString(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_ELSE_IN_IF_STATEMENT));
		insertNewLineBeforeCatch = parseInsert(preferenceStore
				.getString(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_CATCH_IN_TRY_STATEMENT));
		insertNewLineBeforeFinally = parseInsert(preferenceStore
				.getString(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_FINALLY_IN_TRY_STATEMENT));
		insertNewLineBeforeWhile = parseInsert(preferenceStore
				.getString(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_WHILE_IN_DO_STATEMENT));
		keepThenSameLine = preferenceStore
				.getBoolean(DefaultCodeFormatterConstants.FORMATTER_KEEP_THEN_STATEMENT_ON_SAME_LINE);
		keepSimpleIfOnOneLine = preferenceStore
				.getBoolean(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_IF_ON_ONE_LINE);
		keepElseStatementOnSameLine = preferenceStore
				.getBoolean(DefaultCodeFormatterConstants.FORMATTER_KEEP_ELSE_STATEMENT_ON_SAME_LINE);
		compactElseIf = preferenceStore.getBoolean(DefaultCodeFormatterConstants.FORMATTER_COMPACT_ELSE_IF);
		keepGuardianClauseOnOneLine = preferenceStore
				.getBoolean(DefaultCodeFormatterConstants.FORMATTER_KEEP_GUARDIAN_CLAUSE_ON_ONE_LINE);
		formatterTabChar = preferenceStore.getString(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR);
		tabSize = preferenceStore.getInt(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
		indentStatementsCompareToBody = preferenceStore
				.getBoolean(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_BODY);
		indentStatementsCompareToBlock = preferenceStore
				.getBoolean(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_BLOCK);
		indentStatementsCompareToSwitch = preferenceStore
				.getBoolean(DefaultCodeFormatterConstants.FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_SWITCH);
		indentStatementsCompareToCases = preferenceStore
				.getBoolean(DefaultCodeFormatterConstants.FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_CASES);
		indentBreaksCompareToCases = preferenceStore
				.getBoolean(DefaultCodeFormatterConstants.FORMATTER_INDENT_BREAKS_COMPARE_TO_CASES);
		indentEmptyLines = preferenceStore.getBoolean(DefaultCodeFormatterConstants.FORMATTER_INDENT_EMPTY_LINES);
		blankLinesBeforeMethod = preferenceStore
				.getInt(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_METHOD);
		blankLinesInStartOfMethodBody = preferenceStore
				.getInt(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AT_BEGINNING_OF_METHOD_BODY);

		preserveLineBreaks = preferenceStore
				.getBoolean(DefaultCodeFormatterConstants.FORMATTER_PRESERVE_EXTRA_CARRIAGE_RETURNS);

		formatterBracePositionForMethodDecl = parseOption(preferenceStore,
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_METHOD_DECLARATION);
		formatterBracePositionForBlock = parseOption(preferenceStore,
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK);
		formatterBracePositionForBlockInCase = parseOption(preferenceStore,
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK_IN_CASE);
		formatterBracePositionForBlockInSwitch = parseOption(preferenceStore,
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SWITCH);
		formatterBracePositionForArrayInitializer = parseOption(preferenceStore,
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ARRAY_INITIALIZER);
		keepEmptyArrayInitializerOnOneLine = preferenceStore
				.getBoolean(DefaultCodeFormatterConstants.FORMATTER_KEEP_EMPTY_ARRAY_INITIALIZER_ON_ONE_LINE);
		addSpaceAfterFunctionDeclaration = preferenceStore
		.getBoolean(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_CLOSING_BRACE_IN_BLOCK);

		String string = preferenceStore.getString(DefaultCodeFormatterConstants.NO_FORMATTING);
		if (string.length() > 0)
		{
			doFormatting = false;
		}
	}

	/**
	 * @param store
	 * @param id
	 * @return option
	 */
	public final int parseOption(IPreferenceStore store, String id)
	{
		String string = store.getString(id);
		return parseOption(string);
	}

	private int parseOption(String string)
	{
		if (string == null)
		{
			return NEXT_LINE_SHIFTED;
		}
		if (string.equals(DefaultCodeFormatterConstants.END_OF_LINE))
		{
			return END_LINE;
		}
		if (string.equals(DefaultCodeFormatterConstants.NEXT_LINE))
		{
			return NEXT_LINE;
		}
		return NEXT_LINE_SHIFTED;
	}

	/**
	 * @param store
	 * @param id
	 * @return option
	 */
	public final int parseOption(Map store, String id)
	{
		return parseOption((String) store.get(id));
	}

	/**
	 * @param store
	 * @param id
	 * @return option
	 */
	public final int parseOption(IEclipsePreferences store, String id)
	{
		return parseOption((String) store.get(id, null));
	}

	/**
	 * @param map
	 * @param project
	 */
	public JSCodeFormatterOptions(Map map, IProject project)
	{
		if (project != null)
		{
			IEclipsePreferences preferences = new ProjectScope(project).getNode(JSPlugin.ID);
			String string = preferences.get(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, null);
			if (string == null)
			{
				initFromPreferences();
				return;
			}
			initFromProject(preferences, string);
		}
		else if (map == null)
		{
			initFromPreferences();
		}
		else
		{
			Object object = map
					.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_ELSE_IN_IF_STATEMENT);
			if (object != null)
			{
				insertNewLineBeforeElse = parseInsert(object.toString());
			}
			Object object2 = map
					.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_CATCH_IN_TRY_STATEMENT);
			if (object2 != null)
			{
				insertNewLineBeforeCatch = parseInsert(object2.toString());
			}
			Object object3 = map
					.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_FINALLY_IN_TRY_STATEMENT);
			if (object3 != null)
			{
				insertNewLineBeforeFinally = parseInsert(object3.toString());
			}
			Object object4 = map
					.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_WHILE_IN_DO_STATEMENT);
			if (object4 != null)
			{
				insertNewLineBeforeWhile = parseInsert(object4.toString());
			}
			Object object5 = map.get(DefaultCodeFormatterConstants.FORMATTER_KEEP_THEN_STATEMENT_ON_SAME_LINE);
			if (object5 != null)
			{
				keepThenSameLine = Boolean.parseBoolean(object5.toString());
			}
			Object object6 = map.get(DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_IF_ON_ONE_LINE);
			if (object6 != null)
			{
				keepSimpleIfOnOneLine = Boolean.parseBoolean(object6.toString());
			}
			Object object7 = map.get(DefaultCodeFormatterConstants.FORMATTER_KEEP_ELSE_STATEMENT_ON_SAME_LINE);
			if (object7 != null)
			{
				keepElseStatementOnSameLine = Boolean.parseBoolean(object7.toString());
			}
			Object object8 = map.get(DefaultCodeFormatterConstants.FORMATTER_COMPACT_ELSE_IF);
			if (object8 != null)
			{
				compactElseIf = Boolean.parseBoolean(object8.toString());
			}
			Object object9 = map.get(DefaultCodeFormatterConstants.FORMATTER_KEEP_GUARDIAN_CLAUSE_ON_ONE_LINE);
			if (object9 != null)
			{
				keepGuardianClauseOnOneLine = Boolean.parseBoolean(object9.toString());
			}
			formatterTabChar = (String) map.get(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR);
			if (formatterTabChar == null)
			{
				formatterTabChar = " "; //$NON-NLS-1$
			}
			Object object10 = map.get(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
			if (object10 != null)
			{
				tabSize = Integer.parseInt(object10.toString());
			}
			else
			{
				tabSize = 4;
			}
			Object object11 = map.get(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_BODY);
			if (object11 != null)
			{
				indentStatementsCompareToBody = Boolean.parseBoolean(object11.toString());
			}
			Object object12 = map.get(DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_BLOCK);
			if (object12 != null)
			{
				indentStatementsCompareToBlock = Boolean.parseBoolean(object12.toString());
			}
			Object object13 = map
					.get(DefaultCodeFormatterConstants.FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_SWITCH);
			if (object13 != null)
			{
				indentStatementsCompareToSwitch = Boolean.parseBoolean(object13.toString());
			}
			Object object14 = map.get(DefaultCodeFormatterConstants.FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_CASES);
			if (object14 != null)
			{
				indentStatementsCompareToCases = Boolean.parseBoolean(object14.toString());
			}
			Object object15 = map.get(DefaultCodeFormatterConstants.FORMATTER_INDENT_BREAKS_COMPARE_TO_CASES);
			if (object15 != null)
			{
				indentBreaksCompareToCases = Boolean.parseBoolean(object15.toString());
			}
			Object object16 = map.get(DefaultCodeFormatterConstants.FORMATTER_INDENT_EMPTY_LINES);
			if (object16 != null)
			{
				indentEmptyLines = Boolean.parseBoolean(object16.toString());
			}
			Object object17 = map.get(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_METHOD);
			if (object17 != null)
			{
				blankLinesBeforeMethod = Integer.parseInt(object17.toString());
			}
			Object object18 = map.get(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AT_BEGINNING_OF_METHOD_BODY);
			if (object18 != null)
			{
				blankLinesInStartOfMethodBody = Integer.parseInt(object18.toString());
			}

			Object object19a = map.get(DefaultCodeFormatterConstants.FORMATTER_PRESERVE_EXTRA_CARRIAGE_RETURNS);
			if (object19a != null)
			{
				preserveLineBreaks = Boolean.parseBoolean(object19a.toString());
			}

			Object object20 = map.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_IF_STATEMENT);
			if (object20 != null)
			{
				insertNewLineBeforeIf = parseInsert(object20.toString());
			}

			Object object21 = map.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_RETURN_STATEMENT);
			if (object21 != null)
			{
				insertNewLineBeforeReturn = parseInsert(object21.toString());
			}

			formatterBracePositionForMethodDecl = parseOption(map,
					DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_METHOD_DECLARATION);
			formatterBracePositionForBlock = parseOption(map,
					DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK);
			formatterBracePositionForBlockInCase = parseOption(map,
					DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK_IN_CASE);
			formatterBracePositionForBlockInSwitch = parseOption(map,
					DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SWITCH);
			formatterBracePositionForArrayInitializer = parseOption(map,
					DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ARRAY_INITIALIZER);
			Object object19 = map.get(DefaultCodeFormatterConstants.FORMATTER_KEEP_EMPTY_ARRAY_INITIALIZER_ON_ONE_LINE);
			if (object19 != null)
			{
				keepEmptyArrayInitializerOnOneLine = Boolean.parseBoolean(object19.toString());
			}
			Object object24 = map.get(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_CLOSING_BRACE_IN_BLOCK);
			if (object24 != null)
			{
				addSpaceAfterFunctionDeclaration = Boolean.parseBoolean(object24.toString());
			}
			String sma = (String) map.get(DefaultCodeFormatterConstants.NO_FORMATTING);
			if (sma != null && sma.length() > 0)
			{
				doFormatting = false;
			}
		}

	}

	private void initFromProject(IEclipsePreferences preferences, String string)
	{
		insertNewLineBeforeIf = parseInsert(preferences.get(
				DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_IF_STATEMENT, "")); //$NON-NLS-1$
		insertNewLineBeforeReturn = parseInsert(preferences.get(
				DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_RETURN_STATEMENT, "")); //$NON-NLS-1$
		insertNewLineBeforeElse = parseInsert(preferences.get(
				DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_ELSE_IN_IF_STATEMENT, "")); //$NON-NLS-1$
		insertNewLineBeforeCatch = parseInsert(preferences.get(
				DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_CATCH_IN_TRY_STATEMENT, "")); //$NON-NLS-1$
		insertNewLineBeforeFinally = parseInsert(preferences.get(
				DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_FINALLY_IN_TRY_STATEMENT, "")); //$NON-NLS-1$
		insertNewLineBeforeWhile = parseInsert(preferences.get(
				DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_WHILE_IN_DO_STATEMENT, "")); //$NON-NLS-1$
		keepThenSameLine = preferences.getBoolean(
				DefaultCodeFormatterConstants.FORMATTER_KEEP_THEN_STATEMENT_ON_SAME_LINE, false);
		keepSimpleIfOnOneLine = preferences.getBoolean(
				DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_IF_ON_ONE_LINE, false);
		keepElseStatementOnSameLine = preferences.getBoolean(
				DefaultCodeFormatterConstants.FORMATTER_KEEP_ELSE_STATEMENT_ON_SAME_LINE, false);
		compactElseIf = preferences.getBoolean(DefaultCodeFormatterConstants.FORMATTER_COMPACT_ELSE_IF, false);
		keepGuardianClauseOnOneLine = preferences.getBoolean(
				DefaultCodeFormatterConstants.FORMATTER_KEEP_GUARDIAN_CLAUSE_ON_ONE_LINE, false);
		formatterTabChar = preferences.get(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, ""); //$NON-NLS-1$

		tabSize = Integer.parseInt(string);
		indentStatementsCompareToBody = preferences.getBoolean(
				DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_BODY, true);
		indentStatementsCompareToBlock = preferences.getBoolean(
				DefaultCodeFormatterConstants.FORMATTER_INDENT_STATEMENTS_COMPARE_TO_BLOCK, true);
		indentStatementsCompareToSwitch = preferences.getBoolean(
				DefaultCodeFormatterConstants.FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_SWITCH, true);
		indentStatementsCompareToCases = preferences.getBoolean(
				DefaultCodeFormatterConstants.FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_CASES, true);
		indentBreaksCompareToCases = preferences.getBoolean(
				DefaultCodeFormatterConstants.FORMATTER_INDENT_BREAKS_COMPARE_TO_CASES, true);
		indentEmptyLines = preferences.getBoolean(DefaultCodeFormatterConstants.FORMATTER_INDENT_EMPTY_LINES, true);
		blankLinesBeforeMethod = preferences.getInt(DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_METHOD,
				1);
		blankLinesInStartOfMethodBody = preferences.getInt(
				DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AT_BEGINNING_OF_METHOD_BODY, 0);

		preserveLineBreaks = preferences.getBoolean(
				DefaultCodeFormatterConstants.FORMATTER_PRESERVE_EXTRA_CARRIAGE_RETURNS, true);

		formatterBracePositionForMethodDecl = parseOption(preferences,
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_METHOD_DECLARATION);
		formatterBracePositionForBlock = parseOption(preferences,
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK);
		formatterBracePositionForBlockInCase = parseOption(preferences,
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK_IN_CASE);
		formatterBracePositionForBlockInSwitch = parseOption(preferences,
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SWITCH);
		formatterBracePositionForArrayInitializer = parseOption(preferences,
				DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_ARRAY_INITIALIZER);
		keepEmptyArrayInitializerOnOneLine = preferences.getBoolean(
				DefaultCodeFormatterConstants.FORMATTER_KEEP_EMPTY_ARRAY_INITIALIZER_ON_ONE_LINE, true);
		addSpaceAfterFunctionDeclaration = preferences
		.getBoolean(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_CLOSING_BRACE_IN_BLOCK,true);
		string = preferences.get(DefaultCodeFormatterConstants.NO_FORMATTING, ""); //$NON-NLS-1$
		if (string.length() > 0)
		{
			doFormatting = false;
		}
	}

	private boolean parseInsert(String str)
	{
		return str.equals("insert"); //$NON-NLS-1$
	}
}
