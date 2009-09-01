/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL youelect, is prohibited.
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
package com.aptana.ide.editor.yml.preferences;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;

import com.aptana.ide.core.ui.widgets.TextFieldEditor;
import com.aptana.ide.editor.yml.YMLPlugin;
import com.aptana.ide.editors.preferences.CoreEditorPreferencePage;

/**
 * @author Chris Williams
 * 
 */
public class GeneralPreferencePage extends CoreEditorPreferencePage {
	/**
	 * GeneralPreferencePage
	 */
	public GeneralPreferencePage() {
		super(GRID);
		setPreferenceStore(YMLPlugin.getDefault().getPreferenceStore());
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		super.createFieldEditors();
		Group group = new Group(getFieldEditorParent(), SWT.NONE);
		group.setLayout(new GridLayout(1, true));
		group.setText(Messages.GeneralPreferencePage_DefaultContent);
		GridData gdLabel = new GridData(SWT.FILL, SWT.FILL, true, false);
		gdLabel.horizontalSpan = 2;
		group.setLayoutData(gdLabel);
		Composite comp = new Composite(group, SWT.NONE);
		comp.setLayoutData(gdLabel);
		addField(new StringFieldEditor(
				IPreferenceConstants.YMLEDITOR_INITIAL_FILE_NAME,
				Messages.GeneralPreferencePage_DefaultFileName, comp));

		TextFieldEditor contents = new TextFieldEditor(
				IPreferenceConstants.YMLEDITOR_INITIAL_CONTENTS,
				Messages.GeneralPreferencePage_InitialFileContents,
				TextFieldEditor.UNLIMITED,
				TextFieldEditor.VALIDATE_ON_KEY_STROKE, comp, SWT.MULTI
						| SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		addField(contents);
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	/**
	 * @see com.aptana.ide.editors.preferences.CoreEditorPreferencePage#getEditorDescription()
	 */
	protected String getEditorDescription() {
		return "The Aptana YML Editor edits YAML Ain't a Markup Language files.";
	}

	/**
	 * @see com.aptana.ide.editors.preferences.CoreEditorPreferencePage#getEditorId()
	 */
	protected String getEditorId() {
		return "com.aptana.ide.editors.YMLEditor";
	}
}
