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
package com.aptana.ide.editors.unified.utils;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.aptana.ide.core.ui.WorkbenchHelper;

/**
 * EditorHelper
 */
public class EditorHelper {
	
	/**
	 * Protected constructor for utility class
	 *
	 */
	protected EditorHelper()
	{
		
	}
	
	/**
	 * Opens the file in editor, and optionally activates it.
	 * @param file
	 * @param activate
	 * @return Returns the part editor used to open this file.
	 * @throws PartInitException
	 */
	public static IEditorPart openInEditor(IFile file, boolean activate) throws PartInitException {
		if (file != null) {
			IWorkbenchPage p= internalGetActivePage();
			if (p != null) {
				IEditorPart editorPart= IDE.openEditor(p, file, activate);
				return editorPart;
			}
		}
		return null;
	}
	
	/**
	 * Opens the file in editor.
	 * @param file
	 * @return Returns the part editor used to open this file.
	 */
	public static IEditorPart openInEditor(File file){
		if (file != null) {
			IWorkbenchPage p= internalGetActivePage();
			if (p != null) {
				IEditorPart editorPart = WorkbenchHelper.openFile(file, p.getWorkbenchWindow());
				return editorPart;
			}
		}
		return null;
	}
	
	/**
	 * Returns active page.
	 * @return Returns active page.
	 */
	private static IWorkbenchPage internalGetActivePage() {
		IWorkbenchWindow window= getWorkbench().getActiveWorkbenchWindow();
		if (window == null)
		{
			return null;
		}
		return getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}
	
	/**
	 * Gets the workbench.
	 * @return Gets the workbench.
	 */ 
	private static IWorkbench getWorkbench() {
        return PlatformUI.getWorkbench();
    }
}
