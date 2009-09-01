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
package com.aptana.ide.core.ui.views.fileexplorer;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.preferences.FileExtensionPreferencePage;
import com.aptana.ide.core.ui.preferences.IPreferenceConstants;

/**
 * The file editors page presents the collection of file names and extensions for which the user has registered editors.
 * It also lets the user add new internal or external (program) editors for a given file name and extension. The user
 * can add an editor for either a specific file name and extension (e.g. report.doc), or for all file names of a given
 * extension (e.g. *.doc) The set of registered editors is tracked by the EditorRegistery available from the workbench
 * plugin.
 */
public class FileExplorerPreferencePage extends FileExtensionPreferencePage
{

	private Button expandCompressedFiles;

	/**
	 * Creates the page's UI content.
	 * 
	 * @param parent
	 * @return Control
	 */
	protected Control createContents(Composite parent)
	{
		Control c = super.createContents(parent);
		expandCompressedFiles = new Button(parent, SWT.CHECK);
		expandCompressedFiles.setSelection(doGetPreferenceStore().getBoolean(
				IPreferenceConstants.PREF_FILE_EXPLORER_SHOW_COMPRESSED));
		expandCompressedFiles.setText(Messages.FileExplorerPreferencePage_Display_compressed_files_as_expandable);
		return c;
	}

	/**
	 * @see com.aptana.ide.core.ui.preferences.FileExtensionPreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{
		expandCompressedFiles.setSelection(doGetPreferenceStore().getDefaultBoolean(
				IPreferenceConstants.PREF_FILE_EXPLORER_SHOW_COMPRESSED));
		super.performDefaults();
	}

	/**
	 * @see com.aptana.ide.core.ui.preferences.FileExtensionPreferencePage#performOk()
	 */
	public boolean performOk()
	{
		doGetPreferenceStore().setValue(IPreferenceConstants.PREF_FILE_EXPLORER_SHOW_COMPRESSED,
				expandCompressedFiles.getSelection());
		return super.performOk();
	}

	/**
	 * @see com.aptana.ide.core.ui.preferences.FileExtensionPreferencePage#getTableDescription()
	 */
	protected String getTableDescription()
	{
		return Messages.FileExplorerPreferencePage_AddExtensions;
	}

	/**
	 * @see com.aptana.ide.core.ui.preferences.FileExtensionPreferencePage#doGetPreferenceID()
	 */
	protected String doGetPreferenceID()
	{
		return IPreferenceConstants.PREF_FILE_EXPLORER_WEB_FILES;
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
	 */
	protected IPreferenceStore doGetPreferenceStore()
	{
		return CoreUIPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * @see com.aptana.ide.core.ui.preferences.FileExtensionPreferencePage#doGetPlugin()
	 */
	protected Plugin doGetPlugin()
	{
		return CoreUIPlugin.getDefault();
	}
}
