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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.editor.json.wizards;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.jface.viewers.ISelection;

import com.aptana.ide.editors.wizards.SimpleNewFileWizard;
import com.aptana.ide.editors.wizards.SimpleNewWizardPage;

/**
 * @author Kevin Lindsey
 */
public class JsonNewFileWizard extends SimpleNewFileWizard
{
	/**
	 * JsonNewFileWizard
	 */
	public JsonNewFileWizard()
	{
		super();
		
		this.setWindowTitle(Messages.getString("JsonNewFileWizard.New_JSON_File")); //$NON-NLS-1$
	}
	
	/**
	 * @see com.aptana.ide.editors.wizards.SimpleNewFileWizard#createNewFilePage(org.eclipse.jface.viewers.ISelection)
	 */
	protected SimpleNewWizardPage createNewFilePage(ISelection selection)
	{
		SimpleNewWizardPage result = new SimpleNewWizardPage(selection);
		
		result.setRequiredFileExtensions(new String[] { "json" }); //$NON-NLS-1$
		result.setTitle(Messages.getString("JsonNewFileWizard.JSON_File")); //$NON-NLS-1$
		result.setDescription(Messages.getString("JsonNewFileWizard.Create_JSON_File")); //$NON-NLS-1$
		result.setDefaultFileName("new_file.json"); //$NON-NLS-1$
		
		return result;
	}

	/**
	 * @see com.aptana.ide.editors.wizards.SimpleNewFileWizard#getInitialFileContents()
	 */
	protected String getInitialFileContents()
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		
		pw.println("/*"); //$NON-NLS-1$
		pw.println(" * JSON File"); //$NON-NLS-1$
		pw.println(" * "); //$NON-NLS-1$
		pw.println(" * This comment may need to be removed for proper parsing"); //$NON-NLS-1$
		pw.println(" */"); //$NON-NLS-1$
		
		return sw.toString();
	}
}
