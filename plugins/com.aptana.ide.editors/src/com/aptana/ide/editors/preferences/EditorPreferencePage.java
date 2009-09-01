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

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * EditorPreferencePage
 * 
 * @author Ingo Muschenetz
 * @author Kevin Sawicki
 */
public abstract class EditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

	/**
	 * Pair matching group
	 */
	protected Group pairMatchingGroup;

	/**
	 * EditorPreferencePage
	 * 
	 * @param style
	 */
	protected EditorPreferencePage(int style)
	{
		super(style);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate various
	 * types of preferences. Each field editor knows how to save and restore itself.
	 */
	public void createFieldEditors()
	{
		pairMatchingGroup = new Group(getFieldEditorParent(), SWT.NONE);
		GridData gdLabel = new GridData(SWT.FILL, SWT.FILL, true, false);
		gdLabel.horizontalSpan = 2;
		pairMatchingGroup.setLayoutData(gdLabel);
		pairMatchingGroup.setText(Messages.EditorPreferencePage_PairMatching);
		Composite colorComp = new Composite(pairMatchingGroup, SWT.NONE);
		addField(new ColorFieldEditor(IPreferenceConstants.PAIR_MATCHING_COLOR,
				Messages.EditorPreferencePage_HighlightColor, colorComp));
		addField(new RadioGroupFieldEditor(IPreferenceConstants.SHOW_PAIR_MATCHES,
				Messages.EditorPreferencePage_DisplayOptions, 1, new String[][] {
						{ Messages.EditorPreferencePage_DoNotDisplay, IPreferenceConstants.NONE },
						{ Messages.EditorPreferencePage_HiglightMatch, IPreferenceConstants.MATCHING },
						{ Messages.EditorPreferencePage_HighlightBoth, IPreferenceConstants.BOTH } },
				pairMatchingGroup, false));
		GridLayout layout = new GridLayout(1, true);
		pairMatchingGroup.setLayout(layout);

	}
}
