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

import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;

import com.aptana.ide.editor.html.HTMLPlugin;

/**
 * The form for configuring the general top-level preferences for this plugin
 */

public class TypingPreferencePage extends com.aptana.ide.editors.preferences.TypingPreferencePage
{

	/**
	 * GeneralPreferencePage
	 */
	public TypingPreferencePage()
	{
		super(GRID);
		setPreferenceStore(HTMLPlugin.getDefault().getPreferenceStore());
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate various
	 * types of preferences. Each field editor knows how to save and restore itself.
	 */
	public void createFieldEditors()
	{
		super.createFieldEditors();

		addField(new RadioGroupFieldEditor(IPreferenceConstants.AUTO_INDENT_ON_CARRIAGE_RETURN,
				Messages.GeneralPreferencePage_AutoIndentCarriageReturn, 1, new String[][] {
						{ Messages.GeneralPreferencePage_Indent, "true" }, //$NON-NLS-1$
						{ Messages.GeneralPreferencePage_DontIndent, "false" } }, //$NON-NLS-1$
				getFieldEditorParent(), true));
		
		addField(new RadioGroupFieldEditor(
				IPreferenceConstants.ATTRIBUTE_QUOTE_CHARACTER,
				Messages.GeneralPreferencePage_CharacterToUseWhenQuotingAttributes,
				1,
				new String[][] {
						{ Messages.GeneralPreferencePage_None, "" }, //$NON-NLS-1$
						{ Messages.GeneralPreferencePage_DoubleQuote, "\"" }, { Messages.GeneralPreferencePage_SingleQuote, "'" }, }, getFieldEditorParent(), true)); //$NON-NLS-1$//$NON-NLS-2$

		addField(new RadioGroupFieldEditor(
				IPreferenceConstants.AUTO_COMPLETE_CLOSE_TAGS,
				Messages.TypingPreferencePage_AutoCloseTags,
				1,
				new String[][] {
						{ Messages.TypingPreferencePage_WhenTypingOpeningTag, "OPEN" }, //$NON-NLS-1$
						{ Messages.TypingPreferencePage_WhyenTypingClosingTag, "CLOSE" }, //$NON-NLS-1$
						/*{ Messages.TypingPreferencePage_BothClosingTag, "BOTH" },*/
						{ Messages.TypingPreferencePage_Never, "NONE" }}, getFieldEditorParent(), true)); //$NON-NLS-1$

		addField(new RadioGroupFieldEditor(IPreferenceConstants.AUTO_MODIFY_PAIR_TAG,
                Messages.TypingPreferencePage_AutoModifyPairTag, 1, new String[][] {
                        { Messages.TypingPreferencePage_ModifyPairTag, "true" }, //$NON-NLS-1$
                        { Messages.TypingPreferencePage_NOModifyPairTag, "false" } }, //$NON-NLS-1$
                getFieldEditorParent(), true));
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}

	/**
	 * @return string
	 * @see com.aptana.ide.editors.preferences.CoreEditorPreferencePage#getEditorDescription()
	 */
	protected String getEditorDescription()
	{
		return Messages.GeneralPreferencePage_AptanaHTMLEditorEditsHTMLFiles;
	}

	/**
	 * @return string
	 * @see com.aptana.ide.editors.preferences.CoreEditorPreferencePage#getEditorId()
	 */
	protected String getEditorId()
	{
		return "com.aptana.ide.editors.HTMLEditor"; //$NON-NLS-1$
	}

	/**
	 * @return descriptor
	 * @see com.aptana.ide.editors.preferences.CoreEditorPreferencePage#getEditorImage()
	 */
	protected ImageDescriptor getEditorImage()
	{
		return HTMLPlugin.getImageDescriptor("images/html_file.png"); //$NON-NLS-1$
	}

}
