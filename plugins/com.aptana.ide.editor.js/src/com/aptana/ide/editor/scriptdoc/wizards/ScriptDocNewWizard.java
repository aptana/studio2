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
package com.aptana.ide.editor.scriptdoc.wizards;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;

import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editor.js.preferences.IPreferenceConstants;
import com.aptana.ide.editors.wizards.SimpleNewFileWizard;
import com.aptana.ide.editors.wizards.SimpleNewWizardPage;

/**
 * Wizard creates a new empty JavaScript file.
 */
public class ScriptDocNewWizard extends SimpleNewFileWizard
{
	/**
	 * DESCRIPTION
	 */
	public static final String DESCRIPTION = Messages.ScriptDocNewWizard_Desc;
	
	/**
	 * Constructor for ScriptDocNewWizard.
	 */
	public ScriptDocNewWizard()
	{
		super();
		setWindowTitle(Messages.ScriptDocNewWizard_Title);
	}

	/**
	 * @see com.aptana.ide.editors.wizards.SimpleNewFileWizard#createNewFilePage(org.eclipse.jface.viewers.ISelection)
	 */
	protected SimpleNewWizardPage createNewFilePage(ISelection selection)
	{
		SimpleNewWizardPage page = new SimpleNewWizardPage(selection);
		page.setRequiredFileExtensions(new String[] { "sdoc" }); //$NON-NLS-1$
		page.setTitle(Messages.ScriptDocNewWizard_Title2);
		page.setDescription(Messages.ScriptDocNewWizard_Desc2);
		IPreferenceStore store = JSPlugin.getDefault().getPreferenceStore();
		String fileName = store.getString(IPreferenceConstants.SCRIPTDOCEDITOR_INITIAL_FILE_NAME);
		page.setDefaultFileName(fileName); 
		return page;
	}

	/**
	 * @see com.aptana.ide.editors.wizards.SimpleNewFileWizard#getInitialFileContents()
	 */
	protected String getInitialFileContents()
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		String userName = CoreUIUtils.getUserName();
		
		pw.println("/**"); //$NON-NLS-1$
		
		pw.println(" * " + ScriptDocNewWizard.DESCRIPTION); //$NON-NLS-1$
		
		pw.println(" *"); //$NON-NLS-1$
		
		if(userName != null)
		{
			pw.println(" * @author " + userName); //$NON-NLS-1$
		}

		pw.println(" */"); //$NON-NLS-1$
		pw.close();
		
		return sw.toString();
	}
}