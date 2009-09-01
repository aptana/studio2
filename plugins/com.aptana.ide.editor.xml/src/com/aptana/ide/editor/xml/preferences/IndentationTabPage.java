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
public class IndentationTabPage extends FormatterTabPage
{

	private static final String PREVIEW = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" +  //$NON-NLS-1$
	"\r\n" +  //$NON-NLS-1$
	"<rdf:RDF\r\n" +  //$NON-NLS-1$
	"xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\r\n" +  //$NON-NLS-1$
	"xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\r\n" +  //$NON-NLS-1$
	"xmlns:sy=\"http://purl.org/rss/1.0/modules/syndication/\"\r\n" +  //$NON-NLS-1$
	"xmlns:admin=\"http://webns.net/mvcb/\"\r\n" +  //$NON-NLS-1$
	"xmlns:cc=\"http://web.resource.org/cc/\"\r\n" +  //$NON-NLS-1$
	"xmlns=\"http://purl.org/rss/1.0/\">\r\n" +  //$NON-NLS-1$
	"\r\n" +  //$NON-NLS-1$
	"<channel rdf:about=\"http://weblogs.java.net/blog/editors/\">\r\n" +  //$NON-NLS-1$
	"<title>Editor&apos;s Daily Blog</title>\r\n" +  //$NON-NLS-1$
	"<link>http://weblogs.java.net/blog/editors/</link>\r\n" +  //$NON-NLS-1$
	"<description>A daily update from our java.net editor, Chris Adamson, and other items from the java.net front page.</description>\r\n" +  //$NON-NLS-1$
	"<dc:language>en-us</dc:language>\r\n" +  //$NON-NLS-1$
	"<dc:creator></dc:creator>\r\n" +  //$NON-NLS-1$
	"<dc:date>2007-10-12T10:48:50+00:00</dc:date>\r\n" +  //$NON-NLS-1$
	"<admin:generatorAgent rdf:resource=\"http://www.movabletype.org/?v=3.01D\" />\r\n" +  //$NON-NLS-1$
	"\r\n" +  //$NON-NLS-1$
	"\r\n" +  //$NON-NLS-1$
	"<items>\r\n" +  //$NON-NLS-1$
	"<rdf:Seq><rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/hammer_and_a_na.html\" />\r\n" +  //$NON-NLS-1$
	"<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/lay_my_head_dow.html\" />\r\n" +  //$NON-NLS-1$
	"<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/power_of_two.html\" />\r\n" +  //$NON-NLS-1$
	"<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/run_1.html\" />\r\n" +  //$NON-NLS-1$
	"<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/closer_to_fine.html\" />\r\n" +  //$NON-NLS-1$
	"<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/more_adventurou.html\" />\r\n" +  //$NON-NLS-1$
	"<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/does_he_love_yo.html\" />\r\n" +  //$NON-NLS-1$
	"<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/its_a_hit.html\" />\r\n" +  //$NON-NLS-1$
	"<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/close_call.html\" />\r\n" +  //$NON-NLS-1$
	"<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/10/go_ahead.html\" />\r\n" +  //$NON-NLS-1$
	"<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/09/everyday.html\" />\r\n" +  //$NON-NLS-1$
	"<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/09/the_space_betwe.html\" />\r\n" +  //$NON-NLS-1$
	"<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/09/the_best_of_wha.html\" />\r\n" +  //$NON-NLS-1$
	"<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/09/what_would_you.html\" />\r\n" +  //$NON-NLS-1$
	"<rdf:li rdf:resource=\"http://weblogs.java.net/blog/editors/archives/2007/09/out_of_my_hands.html\" />\r\n" +  //$NON-NLS-1$
	"</rdf:Seq>\r\n" +  //$NON-NLS-1$
	"</items>\r\n" +  //$NON-NLS-1$
	"\r\n" +  //$NON-NLS-1$
	"</channel>\r\n" +  //$NON-NLS-1$
	"\r\n" +  //$NON-NLS-1$
	"</rdf:RDF>"; //$NON-NLS-1$
	
	
	private CompilationUnitPreview fPreview;
	private String editor;

	/**
	 * @param modifyDialog
	 * @param workingValues
	 * @param editor
	 */
	public IndentationTabPage(ModifyDialog modifyDialog, Map workingValues, String editor)
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
				FormatterMessages.IndentationTabPage_general_group_title);

		final String[] tabPolicyValues = new String[] { CommentsTabPage.SPACE, CommentsTabPage.TAB };
		final String[] tabPolicyLabels = new String[] {
				FormatterMessages.IndentationTabPage_general_group_option_tab_policy_SPACE,
				FormatterMessages.IndentationTabPage_general_group_option_tab_policy_TAB,
		// FormatterMessages.IndentationTabPage_general_group_option_tab_policy_MIXED
		};
		createComboPref(generalGroup, numColumns, FormatterMessages.IndentationTabPage_general_group_option_tab_policy,
				DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, tabPolicyValues, tabPolicyLabels);
		// final CheckboxPreference onlyForLeading= createCheckboxPref(generalGroup, numColumns,
		// FormatterMessages.IndentationTabPage_use_tabs_only_for_leading_indentations,
		// DefaultCodeFormatterConstants.FORMATTER_USE_TABS_ONLY_FOR_LEADING_INDENTATIONS, FALSE_TRUE);
		final NumberPreference indentSize = createNumberPref(generalGroup, numColumns,
				FormatterMessages.IndentationTabPage_general_group_option_indent_size,
				DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, 0, 32);
		final NumberPreference tabSize = createNumberPref(generalGroup, numColumns,
				FormatterMessages.IndentationTabPage_general_group_option_tab_size,
				DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, 0, 32);

		String tabchar = (String) fWorkingValues.get(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR);
		if (tabchar == null)
		{
			tabchar = " "; //$NON-NLS-1$
		}
		final Group attr = createGroup(numColumns, composite, FormatterMessages.IndentationTabPage_MultilineTitle);
		createNumberPref(attr, numColumns, FormatterMessages.IndentationTabPage_MultilineAttrsTitle,
				DefaultCodeFormatterConstants.FORMATTER_SPACES_BEFORE_ATTRS_ON_MULTILINE, 0, 32);
		// updateTabPreferences(tabchar, tabSize, indentSize, onlyForLeading);
		// tabPolicy.addObserver(new Observer() {
		// public void update(Observable o, Object arg) {
		// updateTabPreferences((String) arg, tabSize, indentSize, onlyForLeading);
		// }
		// });
		tabSize.addObserver(new Observer()
		{
			public void update(Observable o, Object arg)
			{
				indentSize.updateWidget();
			}
		});
	}

	/**
	 * @see com.aptana.ide.ui.editors.preferences.formatter.ModifyDialogTabPage#initializePage()
	 */
	public void initializePage()
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
}
