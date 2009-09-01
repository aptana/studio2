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
package com.aptana.ide.editors.views.outline.propertyManager;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;

import com.aptana.ide.core.StringUtils;

/**
 * Oct 22, 2005
 * 
 * @author Dan Phifer
 * @version 1.0 This class allows the user to choose a directory by clicking a button in a table cell
 */
public class DirectoryCellEditor extends DialogCellEditor
{
	DirectoryDialog directoryDialog;

	/**
	 * Creates a new color cell editor parented under the given control. The cell editor value is black (<code>RGB(0,0,0)</code>)
	 * initially, and has no validator.
	 * 
	 * @param parent
	 *            the parent control
	 */
	public DirectoryCellEditor(Composite parent)
	{
		this(parent, SWT.NONE);
	}

	/**
	 * Creates a new color cell editor parented under the given control. The cell editor value is black (<code>RGB(0,0,0)</code>)
	 * initially, and has no validator.
	 * 
	 * @param parent
	 *            the parent control
	 * @param style
	 *            the style bits
	 * @since 2.1
	 */
	public DirectoryCellEditor(Composite parent, int style)
	{
		super(parent, style);
		doSetValue(StringUtils.EMPTY);
	}

	/**
	 * @see org.eclipse.jface.viewers.DialogCellEditor#openDialogBox(org.eclipse.swt.widgets.Control)
	 */
	protected Object openDialogBox(Control cellEditorWindow)
	{
		DirectoryDialog dialog = getDirectoryDialog(cellEditorWindow);
		return dialog.open();
	}

	private DirectoryDialog getDirectoryDialog(Control cellEditorWindow)
	{
		if (directoryDialog == null)
		{
			directoryDialog = new DirectoryDialog(cellEditorWindow.getShell());
		}
		return directoryDialog;
	}

}
