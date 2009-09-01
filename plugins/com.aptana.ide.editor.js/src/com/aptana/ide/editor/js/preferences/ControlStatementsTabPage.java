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
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.aptana.ide.ui.editors.preferences.formatter.CommentsTabPage;
import com.aptana.ide.ui.editors.preferences.formatter.CompilationUnitPreview;
import com.aptana.ide.ui.editors.preferences.formatter.DefaultCodeFormatterConstants;
import com.aptana.ide.ui.editors.preferences.formatter.FormatterMessages;
import com.aptana.ide.ui.editors.preferences.formatter.FormatterTabPage;
import com.aptana.ide.ui.editors.preferences.formatter.Preview;
import com.aptana.ide.ui.editors.preferences.formatter.ModifyDialog;

/**
 * @author Pavel Petrochenko
 */
public class ControlStatementsTabPage extends FormatterTabPage
{

	String editor;

	/**
	 * Constant array for boolean selection
	 */
	private static String[] FALSE_TRUE = { DefaultCodeFormatterConstants.FALSE, DefaultCodeFormatterConstants.TRUE };

	/**
	 * Constant array for insert / not_insert.
	 */
	private static String[] DO_NOT_INSERT_INSERT = { CommentsTabPage.DO_NOT_INSERT, CommentsTabPage.INSERT };

	private final String PREVIEW = createPreviewHeader(FormatterMessages.ControlStatementsTabPage_preview_header) + "" + //$NON-NLS-1$	
			"  function bar() {" + //$NON-NLS-1$
			"    do {} while (true);" + //$NON-NLS-1$
			"    try {} catch ( e) { } finally { }" + //$NON-NLS-1$
			"  }" + //$NON-NLS-1$
			"  function foo2() {" + //$NON-NLS-1$
			"    if (true) { " + //$NON-NLS-1$
			"      return;" + //$NON-NLS-1$
			"    }" + //$NON-NLS-1$
			"    if (true) {" + //$NON-NLS-1$
			"      return;" + //$NON-NLS-1$
			"    } else if (false) {" + //$NON-NLS-1$
			"      return; " + //$NON-NLS-1$
			"    } else {" + //$NON-NLS-1$
			"      return;" + //$NON-NLS-1$
			"    }" + //$NON-NLS-1$
			"  }" + //$NON-NLS-1$
			"  function foo(state) {" + //$NON-NLS-1$
			"    if (true) return;" + //$NON-NLS-1$
			"if (state==5) state++; "+ //$NON-NLS-1$
			"    if (true) " + //$NON-NLS-1$
			"      return;" + //$NON-NLS-1$
			"    else if (false)" + //$NON-NLS-1$
			"      return;" + //$NON-NLS-1$
			"    else return;" + //$NON-NLS-1$
			"if (state==10) state++; "+ //$NON-NLS-1$
			"    else state--; " + //$NON-NLS-1$			
			"  }" + //$NON-NLS-1$
			""; //$NON-NLS-1$

	private CompilationUnitPreview fPreview;

	/**
	 * 
	 */

	protected CheckboxPreference fThenStatementPref;
	/**
	 * 
	 */
	protected CheckboxPreference fSimpleIfPref;

	/**
	 * @param modifyDialog
	 * @param workingValues
	 * @param editor
	 */
	public ControlStatementsTabPage(ModifyDialog modifyDialog, Map workingValues, String editor)
	{
		super(modifyDialog, workingValues);
		this.editor = editor;
	}

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.ModifyDialogTabPage#doCreatePreferences(org.eclipse.swt.widgets.Composite,
	 *      int)
	 */
	protected void doCreatePreferences(Composite composite, int numColumns)
	{

		final Group generalGroup = createGroup(numColumns, composite,
				FormatterMessages.ControlStatementsTabPage_general_group_title);
		createOption(generalGroup, numColumns, FormatterMessages.ControlStatementsTabPage_LBL_Insert_line_before_if,
				DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_IF_STATEMENT, DO_NOT_INSERT_INSERT);
		createOption(generalGroup, numColumns,
				FormatterMessages.ControlStatementsTabPage_general_group_insert_new_line_before_else_statements,
				DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_ELSE_IN_IF_STATEMENT,
				DO_NOT_INSERT_INSERT);
		createOption(generalGroup, numColumns, FormatterMessages.ControlStatementsTabPage_LBL_Insert_line_before_return,
				DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_RETURN_STATEMENT, DO_NOT_INSERT_INSERT);
		createOption(generalGroup, numColumns,
				FormatterMessages.ControlStatementsTabPage_general_group_insert_new_line_before_catch_statements,
				DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_CATCH_IN_TRY_STATEMENT,
				DO_NOT_INSERT_INSERT);
		createOption(generalGroup, numColumns,
				FormatterMessages.ControlStatementsTabPage_general_group_insert_new_line_before_finally_statements,
				DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_FINALLY_IN_TRY_STATEMENT,
				DO_NOT_INSERT_INSERT);
		createOption(generalGroup, numColumns,
				FormatterMessages.ControlStatementsTabPage_general_group_insert_new_line_before_while_in_do_statements,
				DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_BEFORE_WHILE_IN_DO_STATEMENT,
				DO_NOT_INSERT_INSERT);

		final Group ifElseGroup = createGroup(numColumns, composite,
				FormatterMessages.ControlStatementsTabPage_if_else_group_title);
		fThenStatementPref = createOption(ifElseGroup, numColumns,
				FormatterMessages.ControlStatementsTabPage_if_else_group_keep_then_on_same_line,
				DefaultCodeFormatterConstants.FORMATTER_KEEP_THEN_STATEMENT_ON_SAME_LINE, FALSE_TRUE);

		Label l = new Label(ifElseGroup, SWT.NONE);
		GridData gd = new GridData();
		gd.widthHint = fPixelConverter.convertWidthInCharsToPixels(4);
		l.setLayoutData(gd);

		fSimpleIfPref = createOption(ifElseGroup, numColumns - 1,
				FormatterMessages.ControlStatementsTabPage_if_else_group_keep_simple_if_on_one_line,
				DefaultCodeFormatterConstants.FORMATTER_KEEP_SIMPLE_IF_ON_ONE_LINE, FALSE_TRUE);

		fThenStatementPref.addObserver(new Observer()
		{
			public void update(Observable o, Object arg)
			{
				fSimpleIfPref.setEnabled(!fThenStatementPref.getChecked());
			}

		});

		fSimpleIfPref.setEnabled(!fThenStatementPref.getChecked());

		createOption(ifElseGroup, numColumns,
				FormatterMessages.ControlStatementsTabPage_if_else_group_keep_else_on_same_line,
				DefaultCodeFormatterConstants.FORMATTER_KEEP_ELSE_STATEMENT_ON_SAME_LINE, FALSE_TRUE);
		createCheckboxPref(ifElseGroup, numColumns,
				FormatterMessages.ControlStatementsTabPage_if_else_group_keep_else_if_on_one_line,
				DefaultCodeFormatterConstants.FORMATTER_COMPACT_ELSE_IF, FALSE_TRUE);
		createCheckboxPref(ifElseGroup, numColumns,
				FormatterMessages.ControlStatementsTabPage_if_else_group_keep_guardian_clause_on_one_line,
				DefaultCodeFormatterConstants.FORMATTER_KEEP_GUARDIAN_CLAUSE_ON_ONE_LINE, FALSE_TRUE);
	}

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.ModifyDialogTabPage#initializePage()
	 */
	protected void initializePage()
	{
		fPreview.setPreviewText(PREVIEW);
	}

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.ModifyDialogTabPage#doCreateJavaPreview(org.eclipse.swt.widgets.Composite)
	 */
	protected Preview doCreateJavaPreview(Composite parent)
	{
		fPreview = new CompilationUnitPreview(fWorkingValues, parent, editor, null);
		return fPreview;
	}

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.FormatterTabPage#doUpdatePreview()
	 */
	protected void doUpdatePreview()
	{
		super.doUpdatePreview();
		fPreview.update();
	}

	private CheckboxPreference createOption(Composite composite, int span, String name, String key, String[] values)
	{
		return createCheckboxPref(composite, span, name, key, values);
	}
}
