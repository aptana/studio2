/**
 * Copyright (c) 2005-2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Aptana Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 *
 * Redistribution, except as permitted by the above license, is prohibited.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.editor.json.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.ide.editor.json.Activator;
import com.aptana.ide.editor.json.parsing.JSONMimeType;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class FoldingPreferencePage extends com.aptana.ide.editors.preferences.FoldingPreferencePage
{

	/**
	 * @see com.aptana.ide.editors.preferences.FoldingPreferencePage#addInitialFoldingFields()
	 */
	public void addInitialFoldingFields()
	{
		this.addInitialFoldingField(getLanguage(), "ARRAY", Messages.getString("FoldingPreferencePage.Fold_Arrays")); //$NON-NLS-1$ //$NON-NLS-2$
		this.addInitialFoldingField(getLanguage(), "OBJECT", Messages.getString("FoldingPreferencePage.Fold_Objects")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * @see com.aptana.ide.editors.preferences.FoldingPreferencePage#getLanguage()
	 */
	public String getLanguage()
	{
		return JSONMimeType.MimeType;
	}

	/**
	 * @see com.aptana.ide.editors.preferences.FoldingPreferencePage#storeToInitialize()
	 */
	public IPreferenceStore storeToInitialize()
	{
		return Activator.getDefault().getPreferenceStore();
	}

}
