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
package com.aptana.ide.syncing.ui.ingo.views;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.aptana.ide.core.io.ingo.IVirtualFileManager;
import com.aptana.ide.core.ui.EmptyEditorInput;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class SyncExplorerEditor extends EditorPart
{

	/**
	 * ID
	 */
	public static final String ID = "com.aptana.ide.syncing.views.SyncExplorerEditor"; //$NON-NLS-1$

	/**
	 * INPUT
	 */
	public static final IEditorInput INPUT = new EmptyEditorInput();

	private SyncExplorerWidget widget;

	/**
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor)
	{
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	public void doSaveAs()
	{
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	public void dispose()
	{
		super.dispose();
		if (widget != null)
		{
			widget.dispose();
		}
	}

	/**
	 * Sets the end points to display.
	 * 
	 * @param end1
	 * @param end2
	 */
	public void setEndpoint(IProject end1, IVirtualFileManager end2)
	{
		widget.setEndpoint(end1, end2);
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		setSite(site);
		if (input == INPUT)
		{
			setInput(input);
		}
		else
		{
			throw new PartInitException("Editor created with incorrect input"); //$NON-NLS-1$
		}
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	public boolean isDirty()
	{
		return false;
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
		widget = new SyncExplorerWidget();
		widget.createPartControl(parent);
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus()
	{
		widget.setFocus();
	}
}
