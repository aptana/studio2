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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.ide.core.ui.preferences.TabbedFieldEditorPreferencePage;
import com.aptana.ide.editor.html.HTMLPlugin;

/**
 * 
 * @author Ingo Muschenetz
 *
 */
public class TidyPreferencePage extends TabbedFieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	/**
	 * GeneralPreferencePage
	 */
	public TidyPreferencePage()
	{
		super(GRID);
		setPreferenceStore(HTMLPlugin.getDefault().getPreferenceStore());
		setDescription("Formatting preferences for HTML"); //$NON-NLS-1$
	}
	
	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	protected void createFieldEditors()
	{
        addTab("Indentation"); //$NON-NLS-1$
        
		Composite appearanceComposite = getFieldEditorParent();
		Composite group = com.aptana.ide.core.ui.preferences.GeneralPreferencePage.createGroup(appearanceComposite,
				Messages.FormattingPreferencePage_GeneralSettings);
		
		addField(new IntegerFieldEditor(IPreferenceConstants.FORMATTING_TAB_SIZE,
 				Messages.FormattingPreferencePage_TabSize, group));

		appearanceComposite = getFieldEditorParent();
		group = com.aptana.ide.core.ui.preferences.GeneralPreferencePage.createGroup(appearanceComposite,
				Messages.FormattingPreferencePage_Indent);

		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_SMART_INDENT,
 				Messages.FormattingPreferencePage_IndentTags, group));

		FieldEditor content = new BooleanFieldEditor(IPreferenceConstants.FORMATTING_INDENT_CONTENT,
 				Messages.FormattingPreferencePage_IndentTagContent, group);
		addField(content);

		FieldEditor spaces = new IntegerFieldEditor(IPreferenceConstants.FORMATTING_SPACES_SIZE,
 				Messages.FormattingPreferencePage_NumberOfSpacesToIndentContent, group);
		addField(spaces);
		
		appearanceComposite = getFieldEditorParent();
		group = com.aptana.ide.core.ui.preferences.GeneralPreferencePage.createGroup(appearanceComposite,
				Messages.FormattingPreferencePage_LineWrapping);

		addField(new IntegerFieldEditor(IPreferenceConstants.FORMATTING_WRAP_MARGIN,
 				Messages.FormattingPreferencePage_DefaultWrapMargin, group));
		
 		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_BREAK_BEFORE_BR,
 				Messages.FormattingPreferencePage_InsertNewlineBeforeBR, group));

		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_ENCLOSE_BLOCK_TEXT,
 				Messages.FormattingPreferencePage_WrapTextInBlocks, group));

		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_ENCLOSE_TEXT,
 				Messages.FormattingPreferencePage_WrapTextInBody, group));

		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_INDENT_ATTRIBUTES,
 				Messages.FormattingPreferencePage_InsertNewlineBeforeAttribute, group));

		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_LITERAL_ATTRIBS,
 				Messages.FormattingPreferencePage_AllowAttributesOnNewlines, group));

        addTab("Cleanup"); //$NON-NLS-1$

		appearanceComposite = getFieldEditorParent();
		group = com.aptana.ide.core.ui.preferences.GeneralPreferencePage.createGroup(appearanceComposite,
				Messages.FormattingPreferencePage_Cleanup);

		addField(new RadioGroupFieldEditor(IPreferenceConstants.FORMATTING_SET_OUTPUT, 
 				Messages.FormattingPreferencePage_OutputFormat, 1, new String[][] {
						 {Messages.FormattingPreferencePage_Original, Messages.FormattingPreferencePage_Original},
 					     {Messages.FormattingPreferencePage_XHTML, Messages.FormattingPreferencePage_XHTML},
 					     {Messages.FormattingPreferencePage_XML, Messages.FormattingPreferencePage_XML}
 				},  group));

		addField(new StringFieldEditor(IPreferenceConstants.FORMATTING_ALT_TEXT,
 				Messages.FormattingPreferencePage_DefaultTextForAlt, group));

 		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_DROP_EMPTY_PARAS,
 				Messages.FormattingPreferencePage_DiscardEmptyPElements, group));

 		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_DROP_FONT_TAGS,
 				Messages.FormattingPreferencePage_DiscardPresentationTags, group));

		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_FIX_BACKSLASH,
 				Messages.FormattingPreferencePage_FirURLs, group));

		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_FIX_COMMENTS,
 				Messages.FormattingPreferencePage_FixAdjacentComments, group));

		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_HIDE_END_TAGS,
 				Messages.FormattingPreferencePage_SuppressOptionalEndTags, group));

		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_LOGICAL_EMPHASIS,
 				Messages.FormattingPreferencePage_ReplaceIAndBTags, group));

		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_MAKE_CLEAN,
 				Messages.FormattingPreferencePage_RemovePresentationalClutter, group));

		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_NUM_ENTITIES,
 				Messages.FormattingPreferencePage_UseNumericEntities, group));

		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_QUOTE_AMPERSAND,
 				Messages.FormattingPreferencePage_OutputNakedAmperand, group));

		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_QUOTE_MARKS,
 				Messages.FormattingPreferencePage_OutputQuoteMarks, group));

		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_QUOTE_NBSP,
 				Messages.FormattingPreferencePage_OutputNonBreakingSpace, group));

		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_RAW_OUT,
 				Messages.FormattingPreferencePage_AvoidMappingValues, group));

		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_WORD_2000,
 				Messages.FormattingPreferencePage_CleanWordTags, group));

		/*
		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_UPPER_CASE_ATTRS,
 				"Output attributes in upper case", group));

		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_UPPER_CASE_TAGS,
 				"Output tags in upper case", group));

		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_WRAP_ASP,
 				"Wrap ASP in pseudo elements", group));

		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_WRAP_ATTR_VALUES,
 				"Wrap within attribute values", group));

		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_WRAP_JSTE,
 				"Wrap within JSTE pseudo elements", group));

		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_WRAP_PHP,
 				"Wrap within PHP pseudo elements", group));

		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_WRAP_SCRIPTLETS,
 				"Wrap within JavaScript string literals", group));

		addField(new BooleanFieldEditor(IPreferenceConstants.FORMATTING_WRAP_SECTION,
 				"wrap within <![", group));

		*/
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}
}
