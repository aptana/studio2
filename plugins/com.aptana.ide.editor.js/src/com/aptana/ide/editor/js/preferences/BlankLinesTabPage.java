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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.aptana.ide.ui.editors.preferences.formatter.CompilationUnitPreview;
import com.aptana.ide.ui.editors.preferences.formatter.DefaultCodeFormatterConstants;
import com.aptana.ide.ui.editors.preferences.formatter.FormatterMessages;
import com.aptana.ide.ui.editors.preferences.formatter.FormatterTabPage;
import com.aptana.ide.ui.editors.preferences.formatter.ModifyDialog;
import com.aptana.ide.ui.editors.preferences.formatter.Preview;


/**
 * 
 *
 */
public class BlankLinesTabPage extends FormatterTabPage {

	private final String PREVIEW=
	createPreviewHeader(FormatterMessages.BlankLinesTabPage_preview_header) +"function testFunction(){a();\r\n" +  //$NON-NLS-1$
			"	//this is first function		\r\n" +  //$NON-NLS-1$
			"}\r\n" +  //$NON-NLS-1$
			"\r\n" +  //$NON-NLS-1$
			"function main(){b();\r\n" +  //$NON-NLS-1$
			"	//this is second function\r\n" +  //$NON-NLS-1$
			"}"; 	 //$NON-NLS-1$
	
	
	private static final int MIN_NUMBER_LINES= 0;
	private static final int MAX_NUMBER_LINES= 99;
	
	/**
	 * Constant array for boolean selection 
	 */
	private static String[] FALSE_TRUE = {
		DefaultCodeFormatterConstants.FALSE,
		DefaultCodeFormatterConstants.TRUE
	};	

	private CompilationUnitPreview fPreview;

	private String editor;
	
	/**
	 * Create a new BlankLinesTabPage.
	 * @param modifyDialog The main configuration dialog
	 * 
	 * @param workingValues The values wherein the options are stored. 
	 * @param editor 
	 */
	public BlankLinesTabPage(ModifyDialog modifyDialog, Map workingValues,String editor) {
		super(modifyDialog, workingValues);
		this.editor=editor;
	}

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.ModifyDialogTabPage#doCreatePreferences(org.eclipse.swt.widgets.Composite, int)
	 */
	protected void doCreatePreferences(Composite composite, int numColumns) {
				
		
	    Group group = createGroup(numColumns, composite, FormatterMessages.BlankLinesTabPage_LBL_Preserve_lines); 
		createOption(group, numColumns, FormatterMessages.BlankLinesTabPage_LBL_Preserve_extra_carriage_returns, DefaultCodeFormatterConstants.FORMATTER_PRESERVE_EXTRA_CARRIAGE_RETURNS, FALSE_TRUE); 

		group = createGroup(numColumns, composite, FormatterMessages.BlankLinesTabPage_compilation_unit_group_title); 
		createBlankLineTextField(group, numColumns, FormatterMessages.BlankLinesTabPage_class_option_before_method_decls, DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_BEFORE_METHOD); 
		createBlankLineTextField(group, numColumns, FormatterMessages.BlankLinesTabPage_class_option_at_beginning_of_method_body, DefaultCodeFormatterConstants.FORMATTER_BLANK_LINES_AT_BEGINNING_OF_METHOD_BODY); 
	}
	
	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.ModifyDialogTabPage#initializePage()
	 */
	protected void initializePage() {
	    fPreview.setPreviewText(PREVIEW);
	}
	
	/*
	 * A helper method to create a number preference for blank lines.
	 */
	private void createBlankLineTextField(Composite composite, int numColumns, String message, String key) {
		createNumberPref(composite, numColumns, message, key, MIN_NUMBER_LINES, MAX_NUMBER_LINES);
	}

    /**
     * Helper method to create checkboxes
     * @param composite
     * @param span
     * @param name
     * @param key
     * @param values
     * @return
     */
    private CheckboxPreference createOption(Composite composite, int span, String name, String key, String [] values) {
		return createCheckboxPref(composite, span, name, key, values);
	}

    /**
     * @see com.aptana.ide.ui.editors.preferences.formatter.ModifyDialogTabPage#doCreateJavaPreview(org.eclipse.swt.widgets.Composite)
     */
    protected Preview doCreateJavaPreview(Composite parent) {
        fPreview= new CompilationUnitPreview(fWorkingValues, parent,editor, null);
        return fPreview;
    }

    /* (non-Javadoc)
     * @see com.aptana.ide.ui.editors.preferences.formatter.ModifyDialogTabPage#doUpdatePreview()
     */
    /**
     * @see com.aptana.ide.ui.editors.preferences.formatter.FormatterTabPage#doUpdatePreview()
     */
    protected void doUpdatePreview() {
    	super.doUpdatePreview();
        fPreview.update();
    }
}