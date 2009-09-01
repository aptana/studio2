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

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

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
 *
 */
public class GeneralTabPage extends FormatterTabPage {
	
	private static final String PREVIEW = "<!DOCTYPE html\r\n" +  //$NON-NLS-1$
	"    PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\r\n" +  //$NON-NLS-1$
	"    \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\r\n" +  //$NON-NLS-1$
	"<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">\r\n" +  //$NON-NLS-1$
	"<head>\r\n" +  //$NON-NLS-1$
	" <title>HTML Formatting Sample</title><style type=\"text/css\">\r\n" +  //$NON-NLS-1$
	"\r\n" +  //$NON-NLS-1$
	"@import url(\"/shared.css\");\r\n" +  //$NON-NLS-1$
	"\r\n" +  //$NON-NLS-1$
	"#footer {\r\n" +  //$NON-NLS-1$
	"	border:1px solid white;\r\n" +  //$NON-NLS-1$
	"}\r\n" +  //$NON-NLS-1$
	"\r\n" +  //$NON-NLS-1$
	"#banner {\r\n" +  //$NON-NLS-1$
	"  background-color: #636D84;\r\n" +  //$NON-NLS-1$
	"  padding-right:40px;\r\n" +  //$NON-NLS-1$
	"  padding-top:10px;\r\n" +  //$NON-NLS-1$
	"  height:40px;\r\n" +  //$NON-NLS-1$
	"}\r\n" +  //$NON-NLS-1$
	"</style>\r\n" +  //$NON-NLS-1$
	" <script type=\"text/javascript\" src=\"/trac/chrome/common/js/trac.js\"></script>\r\n" +  //$NON-NLS-1$
	"</head>\r\n" +  //$NON-NLS-1$
	"<body>\r\n" +  //$NON-NLS-1$
	"\r\n" +  //$NON-NLS-1$
	"<div id=\"navigation\">\r\n" +  //$NON-NLS-1$
	"   		<div id=\"header\">\r\n" +  //$NON-NLS-1$
	"		<h1><a title=\"Return to home page\" accesskey=\"1\" href=\"/\">Aptana</a></h1>\r\n" +  //$NON-NLS-1$
	"	</div>\r\n" +  //$NON-NLS-1$
	"	<div>\r\n" +  //$NON-NLS-1$
	"		<ul>\r\n" +  //$NON-NLS-1$
	"			<li><a href=\"/dev\">contribute</a></li>\r\n" +  //$NON-NLS-1$
	"			<li><a href=\"/forums\">forums</a></li>\r\n" +  //$NON-NLS-1$
	"			<li><a href=\"/download_all.php\">products</a></li>\r\n" +  //$NON-NLS-1$
	"			<li><a href=\"/support.php\">support</a></li>\r\n" +  //$NON-NLS-1$
	"			<li><a href=\"/about.php\">about</a></li>\r\n" +  //$NON-NLS-1$
	"		</ul>\r\n" +  //$NON-NLS-1$
	"	</div>\r\n" +  //$NON-NLS-1$
	"</div>\r\n" +  //$NON-NLS-1$
	" </body>\r\n" +  //$NON-NLS-1$
	"</html>"; //$NON-NLS-1$
	
	
	private CompilationUnitPreview fPreview;
	private String editor;
	
	/**
	 * @param modifyDialog
	 * @param workingValues
	 * @param editor
	 */
	public GeneralTabPage(ModifyDialog modifyDialog, Map workingValues,String editor) {
		super(modifyDialog, workingValues);
		this.editor=editor;
	}

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.ModifyDialogTabPage#doCreatePreferences(org.eclipse.swt.widgets.Composite, int)
	 */
	protected void doCreatePreferences(Composite composite, int numColumns) {

		final Group generalGroup= createGroup(numColumns, composite, FormatterMessages.IndentationTabPage_general_group_title); 
		
		final String[] tabPolicyValues= new String[] {CommentsTabPage.SPACE, CommentsTabPage.TAB};
		final String[] tabPolicyLabels= new String[] {
				FormatterMessages.IndentationTabPage_general_group_option_tab_policy_SPACE, 
				FormatterMessages.IndentationTabPage_general_group_option_tab_policy_TAB, 
				//FormatterMessages.IndentationTabPage_general_group_option_tab_policy_MIXED
		};
		createComboPref(generalGroup, numColumns, FormatterMessages.IndentationTabPage_general_group_option_tab_policy, DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, tabPolicyValues, tabPolicyLabels);
		//final CheckboxPreference onlyForLeading= createCheckboxPref(generalGroup, numColumns, FormatterMessages.IndentationTabPage_use_tabs_only_for_leading_indentations, DefaultCodeFormatterConstants.FORMATTER_USE_TABS_ONLY_FOR_LEADING_INDENTATIONS, FALSE_TRUE);
		final NumberPreference indentSize= createNumberPref(generalGroup, numColumns, FormatterMessages.IndentationTabPage_general_group_option_indent_size, DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, 0, 32); 
		final NumberPreference tabSize= createNumberPref(generalGroup, numColumns, FormatterMessages.IndentationTabPage_general_group_option_tab_size, DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, 0, 32);
		
		String tabchar= (String) fWorkingValues.get(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR);
		if (tabchar==null){
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
        fPreview= new CompilationUnitPreview(fWorkingValues, parent,editor, null);
        return fPreview;
    }

    
    /**
     * @see com.aptana.ide.ui.editors.preferences.formatter.FormatterTabPage#doUpdatePreview()
     */
    protected void doUpdatePreview() {
    	super.doUpdatePreview();
        fPreview.update();
    }
}
