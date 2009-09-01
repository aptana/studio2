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
public class BracesTabPage extends FormatterTabPage {
	
	/**
	 * Constant array for boolean selection 
	 */
	private static String[] FALSE_TRUE = {
		DefaultCodeFormatterConstants.FALSE,
		DefaultCodeFormatterConstants.TRUE
	};	
	
	private static final String PREVIEW = "H1 {\r\n" +  //$NON-NLS-1$
	"color: white; background: teal; FONT-FAMILY: arial, helvetica, lucida-sans, sans-serif; FONT-SIZE: 18pt; FONT-STYLE: normal; FONT-VARIANT: normal\r\n" +  //$NON-NLS-1$
	"}\r\n" +  //$NON-NLS-1$
	"H2 {\r\n" +  //$NON-NLS-1$
	"COLOR: #000000; FONT-FAMILY: verdana, helvetica, lucida-sans, sans-serif; FONT-SIZE: 14pt; FONT-STYLE: normal; FONT-VARIANT: normal\r\n" +  //$NON-NLS-1$
	"}\r\n" +  //$NON-NLS-1$
	"H3 {\r\n" +  //$NON-NLS-1$
	"COLOR: #000000; FONT-FAMILY: lucida-sans, sans-serif; FONT-SIZE: 14pt; FONT-STYLE: normal; FONT-VARIANT: normal\r\n" +  //$NON-NLS-1$
	"}\r\n" +  //$NON-NLS-1$
	"H4 {\r\n" +  //$NON-NLS-1$
	"COLOR: #000000; FONT-FAMILY: lucida-sans, sans-serif; FONT-SIZE: 12pt; FONT-STYLE: normal; FONT-VARIANT: normal\r\n" +  //$NON-NLS-1$
	"}\r\n" +  //$NON-NLS-1$
	"H5 {\r\n" +  //$NON-NLS-1$
	"color: white; background: darkblue; FONT-FAMILY: lucida-sans, sans-serif; FONT-SIZE: 12pt; FONT-STYLE: normal; FONT-VARIANT: normal\r\n" +  //$NON-NLS-1$
	"}\r\n" +  //$NON-NLS-1$
	"H6 {\r\n" +  //$NON-NLS-1$
	"color: yellow; background: green; FONT-FAMILY: lucida-sans, sans-serif; FONT-SIZE: 10pt; FONT-STYLE: normal; FONT-VARIANT: normal\r\n" +  //$NON-NLS-1$
	"} \r\n" +  //$NON-NLS-1$
	"\r\n" +  //$NON-NLS-1$
	"body {\r\n" +  //$NON-NLS-1$
	"COLOR: #000000; FONT-FAMILY: lucida-sans, sans-serif; FONT-SIZE: 10pt; FONT-STYLE: normal; FONT-VARIANT: normal; background-image: url(\'bkgnd.gif\') \r\n" +  //$NON-NLS-1$
	"}\r\n" +  //$NON-NLS-1$
	""; //$NON-NLS-1$
	

	
	
	private CompilationUnitPreview fPreview;
	
	
	private final String [] fBracePositions= {
	    DefaultCodeFormatterConstants.END_OF_LINE,
	    DefaultCodeFormatterConstants.NEXT_LINE,
	    DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED
	};
	
	private final String [] fExtendedBracePositions= {
		DefaultCodeFormatterConstants.END_OF_LINE,
	    DefaultCodeFormatterConstants.NEXT_LINE,
	    DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED, 
		DefaultCodeFormatterConstants.NEXT_LINE_ON_WRAP
	};
	
	private final String [] fBracePositionNames= {
	    FormatterMessages.BracesTabPage_position_same_line, 
	    FormatterMessages.BracesTabPage_position_next_line, 
	    FormatterMessages.BracesTabPage_position_next_line_indented
	};
	
	private final String [] fExtendedBracePositionNames= {
	    FormatterMessages.BracesTabPage_position_same_line, 
	    FormatterMessages.BracesTabPage_position_next_line, 
	    FormatterMessages.BracesTabPage_position_next_line_indented, 
		FormatterMessages.BracesTabPage_position_next_line_on_wrap
	};

	private String editor;

	
	/**
	 * Create a new BracesTabPage.
	 * 
	 * @param modifyDialog
	 * @param workingValues
	 * @param editor 
	 */
	public BracesTabPage(ModifyDialog modifyDialog, Map<String, String> workingValues, String editor) {
		super(modifyDialog, workingValues);
		this.editor=editor;
	}
	
	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.ModifyDialogTabPage#doCreatePreferences(org.eclipse.swt.widgets.Composite, int)
	 */
	protected void doCreatePreferences(Composite composite, int numColumns) {
		final Group group = createGroup(numColumns, composite, FormatterMessages.BracesTabPage_group_brace_positions_title); 		 
		//createBracesCombo(group, numColumns, FormatterMessages.BracesTabPage_option_method_declaration, DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_METHOD_DECLARATION); 
		createBracesCombo(group, numColumns, FormatterMessages.BracesTabPage_option_blocks, DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK); 
		//createBracesCombo(group, numColumns, FormatterMessages.BracesTabPage_option_blocks_in_case, DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_BLOCK_IN_CASE); 
		//createBracesCombo(group, numColumns, FormatterMessages.BracesTabPage_option_switch_case, DefaultCodeFormatterConstants.FORMATTER_BRACE_POSITION_FOR_SWITCH); 			
	}
	
	/**
	 * @param arrayInitOption
	 * @param arrayInitCheckBox
	 */
	protected final void updateOptionEnablement(ComboPreference arrayInitOption, CheckboxPreference arrayInitCheckBox) {
		arrayInitCheckBox.setEnabled(!arrayInitOption.hasValue(DefaultCodeFormatterConstants.END_OF_LINE));
	}

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.ModifyDialogTabPage#initializePage()
	 */
	protected void initializePage() {
	    fPreview.setPreviewText(PREVIEW);
	}
	
	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.ModifyDialogTabPage#doCreateJavaPreview(org.eclipse.swt.widgets.Composite)
	 */
	protected Preview doCreateJavaPreview(Composite parent) {
	    fPreview = new CompilationUnitPreview(fWorkingValues, parent,editor, null);
	    return fPreview;
	}
	
	private ComboPreference createBracesCombo(Composite composite, int numColumns, String message, String key) {
		return createComboPref(composite, numColumns, message, key, fBracePositions, fBracePositionNames);
	}
	
    /**
     * @see com.aptana.ide.ui.editors.preferences.formatter.FormatterTabPage#doUpdatePreview()
     */
    protected void doUpdatePreview() {
    	super.doUpdatePreview();
        fPreview.update();
    }
}
