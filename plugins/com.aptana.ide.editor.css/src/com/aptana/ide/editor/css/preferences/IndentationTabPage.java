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
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.aptana.ide.ui.editors.preferences.formatter.CommentsTabPage;
import com.aptana.ide.ui.editors.preferences.formatter.CompilationUnitPreview;
import com.aptana.ide.ui.editors.preferences.formatter.DefaultCodeFormatterConstants;
import com.aptana.ide.ui.editors.preferences.formatter.FormatterMessages;
import com.aptana.ide.ui.editors.preferences.formatter.FormatterTabPage;
import com.aptana.ide.ui.editors.preferences.formatter.ModifyDialog;
import com.aptana.ide.ui.editors.preferences.formatter.Preview;


/**
 * 
 */
public class IndentationTabPage extends FormatterTabPage {

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
	private String fOldTabChar= null;
	private String editor;
	
	/**
	 * @param modifyDialog
	 * @param workingValues
	 * @param editor
	 */
	public IndentationTabPage(ModifyDialog modifyDialog, Map<String, String> workingValues, String editor) {
		super(modifyDialog, workingValues);
		this.editor = editor;
	}

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.ModifyDialogTabPage#doCreatePreferences(org.eclipse.swt.widgets.Composite, int)
	 */
	protected void doCreatePreferences(Composite composite, int numColumns) {
		final Group generalGroup = createGroup(numColumns, composite, FormatterMessages.IndentationTabPage_general_group_title); 
		
		final String[] tabPolicyValues = new String[] {CommentsTabPage.SPACE, CommentsTabPage.TAB};
		final String[] tabPolicyLabels = new String[] {
				FormatterMessages.IndentationTabPage_general_group_option_tab_policy_SPACE, 
				FormatterMessages.IndentationTabPage_general_group_option_tab_policy_TAB, 
				//FormatterMessages.IndentationTabPage_general_group_option_tab_policy_MIXED
		};
		final ComboPreference tabPolicy = createComboPref(generalGroup, numColumns, FormatterMessages.IndentationTabPage_general_group_option_tab_policy, DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, tabPolicyValues, tabPolicyLabels);
		//final CheckboxPreference onlyForLeading= createCheckboxPref(generalGroup, numColumns, FormatterMessages.IndentationTabPage_use_tabs_only_for_leading_indentations, DefaultCodeFormatterConstants.FORMATTER_USE_TABS_ONLY_FOR_LEADING_INDENTATIONS, FALSE_TRUE);
		final NumberPreference indentSize = createNumberPref(generalGroup, numColumns, FormatterMessages.IndentationTabPage_general_group_option_indent_size, DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, 0, 32); 
		final NumberPreference tabSize = createNumberPref(generalGroup, numColumns, FormatterMessages.IndentationTabPage_general_group_option_tab_size, DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, 0, 32);
		
		String tabchar = fWorkingValues.get(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR);
		if (tabchar==null) {
			tabchar=" "; //$NON-NLS-1$
		}
//		updateTabPreferences(tabchar, tabSize, indentSize, onlyForLeading);
//		tabPolicy.addObserver(new Observer() {
//			public void update(Observable o, Object arg) {
//				updateTabPreferences((String) arg, tabSize, indentSize, onlyForLeading);
//			}
//		});
		tabSize.addObserver(new Observer() {
			public void update(Observable o, Object arg) {
				indentSize.updateWidget();
			}
		});				 	
	}
	
	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.ModifyDialogTabPage#initializePage()
	 */
	public void initializePage() {
	    fPreview.setPreviewText(PREVIEW);
	}

    /**
     * @see com.aptana.ide.ui.editors.preferences.formatter.ModifyDialogTabPage#doCreateJavaPreview(org.eclipse.swt.widgets.Composite)
     */
    protected Preview doCreateJavaPreview(Composite parent) {
        fPreview = new CompilationUnitPreview(fWorkingValues, parent,editor, null);
        return fPreview;
    }

    /**
     * @see com.aptana.ide.ui.editors.preferences.formatter.FormatterTabPage#doUpdatePreview()
     */
    protected void doUpdatePreview() {
    	super.doUpdatePreview();
        fPreview.update();
    }

	private void updateTabPreferences(String tabPolicy, NumberPreference tabPreference, NumberPreference indentPreference, CheckboxPreference onlyForLeading) {
		/*
		 * If the tab-char is SPACE (or TAB), INDENTATION_SIZE
		 * preference is not used by the core formatter. We piggy back the
		 * visual tab length setting in that preference in that case. If the
		 * user selects MIXED, we use the previous TAB_SIZE preference as the
		 * new INDENTATION_SIZE (as this is what it really is) and set the 
		 * visual tab size to the value piggy backed in the INDENTATION_SIZE
		 * preference. See also CodeFormatterUtil. 
		 */
		if (DefaultCodeFormatterConstants.MIXED.equals(tabPolicy)) {
			if (CommentsTabPage.SPACE.equals(fOldTabChar) || CommentsTabPage.TAB.equals(fOldTabChar)){
				swapTabValues();
			}
			tabPreference.setEnabled(true);
			tabPreference.setKey(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
			indentPreference.setEnabled(true);
			indentPreference.setKey(DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE);
			onlyForLeading.setEnabled(true);
		} else if (CommentsTabPage.SPACE.equals(tabPolicy)) {
			if (DefaultCodeFormatterConstants.MIXED.equals(fOldTabChar)){
				swapTabValues();
			}
			tabPreference.setEnabled(true);
			tabPreference.setKey(DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE);
			indentPreference.setEnabled(true);
			indentPreference.setKey(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
			onlyForLeading.setEnabled(false);
		} else if (CommentsTabPage.TAB.equals(tabPolicy)) {
			if (DefaultCodeFormatterConstants.MIXED.equals(fOldTabChar)){
				swapTabValues();
			}
			tabPreference.setEnabled(true);
			tabPreference.setKey(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
			indentPreference.setEnabled(false);
			indentPreference.setKey(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
			onlyForLeading.setEnabled(true);
		} else {
			Assert.isTrue(false);
		}
		fOldTabChar = tabPolicy;
	}

	private void swapTabValues() {
		String tabSize = fWorkingValues.get(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE);
		String indentSize = fWorkingValues.get(DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE);
		fWorkingValues.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, indentSize);
		fWorkingValues.put(DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE, tabSize);
	}
}
