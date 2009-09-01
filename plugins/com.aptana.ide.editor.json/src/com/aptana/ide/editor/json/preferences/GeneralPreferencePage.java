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
package com.aptana.ide.editor.json.preferences;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;

import com.aptana.ide.editor.json.Activator;
import com.aptana.ide.editors.preferences.CoreEditorPreferencePage;

/**
 * The form for configuring the general top-level preferences for this plug-in
 */
public class GeneralPreferencePage extends CoreEditorPreferencePage
{
	/**
	 * GeneralPreferencePage
	 */
	public GeneralPreferencePage()
	{
		super(GRID);
		
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}

	/**
	 * @see com.aptana.ide.editors.preferences.CoreEditorPreferencePage#getEditorDescription()
	 */
	protected String getEditorDescription()
	{
		return Messages.getString("GeneralPreferencePage.Edits_JSON_Files"); //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.editors.preferences.CoreEditorPreferencePage#getFileAssociations(java.lang.String)
	 */
	protected String[] getFileAssociations(String id)
	{
		return new String[] { "*.json" }; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.editors.preferences.CoreEditorPreferencePage#getEditorId()
	 */
	protected String getEditorId()
	{
		return "com.aptana.ide.editors.JSONEditor"; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.editors.preferences.CoreEditorPreferencePage#getEditorImage()
	 */
	protected ImageDescriptor getEditorImage()
	{
		return Activator.getImageDescriptor("/json/json_file.png"); //$NON-NLS-1$
	}
}
