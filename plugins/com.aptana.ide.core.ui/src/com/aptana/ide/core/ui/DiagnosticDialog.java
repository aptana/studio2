/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.ide.core.ui;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class DiagnosticDialog extends TrayDialog
{

	private static final int COPY_ID = 32;

	private Text fText;

	/**
	 * Constructor.
	 * 
	 * @param parentShell
	 *            the parent shell
	 */
	public DiagnosticDialog(Shell parentShell)
	{
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		setHelpAvailable(true);
		setBlockOnOpen(false);
	}

	/**
	 * Appends the specified text to the text area.
	 * 
	 * @param text
	 *            the text to append
	 */
	public void append(String text)
	{
		if (fText != null && !fText.isDisposed() && text != null)
		{
			fText.append(text);
		}
	}

	/**
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText(Messages.DiagnosticDialog_Title);
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent)
	{
		// creates the Copy to Clipboard and Close buttons
		createButton(parent, COPY_ID, Messages.DiagnosticDialog_CopyToClipboard, false);
		createButton(parent, IDialogConstants.OK_ID, Messages.DiagnosticDialog_Close, true);
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent)
	{
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		fText = new Text(main, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		fText.setBackground(fText.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.widthHint = 500;
		gridData.heightHint = 500;
		fText.setLayoutData(gridData);

		return main;
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	protected void buttonPressed(int buttonId)
	{
		if (buttonId == COPY_ID)
		{
			fText.selectAll();
			fText.copy();
			fText.clearSelection();
		}
		else
		{
			super.buttonPressed(buttonId);
		}
	}

}
