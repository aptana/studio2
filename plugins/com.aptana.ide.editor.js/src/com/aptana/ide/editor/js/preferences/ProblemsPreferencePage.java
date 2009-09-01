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

import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editor.js.parsing.JSMimeType;

/**
 * ProblemsPreferencePage
 * 
 * @author Ingo Muschenetz
 */
public class ProblemsPreferencePage extends com.aptana.ide.editors.preferences.ProblemsPreferencePage
{

	private Button useNoValidateComments;

	/**
	 * @see com.aptana.ide.editors.preferences.ProblemsPreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{
		Composite displayArea = (Composite) super.createContents(parent);
		useNoValidateComments = new Button(displayArea, SWT.CHECK);
		useNoValidateComments.setText(Messages.ProblemsPreferencePage_LBL_Enable_error_filtering_inside_novalidate);
		useNoValidateComments.setSelection(doGetPreferenceStore().getBoolean(
				IPreferenceConstants.ENABLE_NO_VALIDATE_COMMENT));
		return displayArea;
	}

	/**
	 * @see com.aptana.ide.editors.preferences.ProblemsPreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{
		super.performDefaults();
		useNoValidateComments.setSelection(doGetPreferenceStore().getDefaultBoolean(
				IPreferenceConstants.ENABLE_NO_VALIDATE_COMMENT));
	}

	/**
	 * @see com.aptana.ide.editors.preferences.ProblemsPreferencePage#performOk()
	 */
	public boolean performOk()
	{
		doGetPreferenceStore().setValue(IPreferenceConstants.ENABLE_NO_VALIDATE_COMMENT,
				useNoValidateComments.getSelection());
		return super.performOk();
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
	 */
	protected IPreferenceStore doGetPreferenceStore()
	{
		return JSPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * @see com.aptana.ide.editors.preferences.ProblemsPreferencePage#doGetPlugin()
	 */
	protected Plugin doGetPlugin()
	{
		return JSPlugin.getDefault();
	}

	/**
	 * @see com.aptana.ide.editors.preferences.ProblemsPreferencePage#getMimeType()
	 */
	protected String getMimeType()
	{
		return JSMimeType.MimeType;
	}
}
