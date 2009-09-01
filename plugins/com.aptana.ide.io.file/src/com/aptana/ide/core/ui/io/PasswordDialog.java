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
package com.aptana.ide.core.ui.io;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.aptana.ide.core.CoreStrings;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.IPasswordListener;
import com.aptana.ide.core.io.PasswordEvent;
import com.aptana.ide.core.ui.SWTUtils;

/**
 * @author Kevin Lindsey
 */
public class PasswordDialog implements IPasswordListener
{
	/**
	 * @author Kevin Lindsey
	 */
	public class OKSelection extends SelectionAdapter
	{
		/**
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e)
		{
			super.widgetSelected(e);

			_event.password = _password.getText();
			_event.remember = _remember.getSelection();

			_shell.dispose();
		}
	}

	/**
	 * @author Kevin Lindsey
	 */
	public class CancelSelection extends SelectionAdapter
	{
		/**
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e)
		{
			super.widgetSelected(e);
			handleCancel();	
		}
	}
	
	private Shell _shell;
	private Text _password;
	private Button _remember;
	private PasswordEvent _event;

	/**
	 * PasswordDialog
	 */
	public PasswordDialog()
	{
	}

	/**
	 * getPassword
	 */
	private void getPassword()
	{
		Display display = PlatformUI.getWorkbench().getDisplay();

		// Create the dialog window
		createContents();

		this._shell.layout();

		// centers the item on screen and resizes it
		SWTUtils.centerAndPack(this._shell, display.getActiveShell());

		this._shell.open();

		while (this._shell.isDisposed() == false)
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
	}

	/**
	 * createContents
	 */
	private void createContents()
	{
		this._shell = new Shell(PlatformUI.getWorkbench().getDisplay().getActiveShell(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		addCloseBoxListener(this._shell);

		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginRight = 10;
		gridLayout.marginLeft = 10;
		gridLayout.marginTop = 10;
		gridLayout.marginBottom = 10;
		gridLayout.numColumns = 2;
		gridLayout.verticalSpacing = 10;
		gridLayout.horizontalSpacing = 10;
		this._shell.setLayout(gridLayout);
		this._shell.setSize(407, 520);
		this._shell.setText(this._event.title);

		Label passwordLabel = new Label(this._shell, SWT.NONE);
		passwordLabel.setText(StringUtils.makeFormLabel(Messages.PasswordDialog_Password));

		this._password = new Text(this._shell, SWT.BORDER);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.widthHint = 136;
		this._password.setLayoutData(gridData);
		SWTUtils.setTextAsPassword(this._password);

		this._remember = new Button(this._shell, SWT.CHECK);
		this._remember.setText(Messages.PasswordDialog_RememberOnlyThisSession);
		GridData gridData2 = new GridData(SWT.FILL, SWT.CENTER, true, true);
		gridData.horizontalSpan = 2;
		gridData.widthHint = 136;
		this._remember.setLayoutData(gridData2);

		Composite composite = new Composite(this._shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		GridLayout gridLayout_4 = new GridLayout();
		gridLayout_4.marginHeight = 0;
		gridLayout_4.numColumns = 2;
		composite.setLayout(gridLayout_4);

		Button okButton = new Button(composite, SWT.NONE);
		okButton.addSelectionListener(new OKSelection());
		this._shell.setDefaultButton(okButton);
		okButton.setText(CoreStrings.OK);
		
		Button cancelButton = new Button(composite, SWT.NONE);
		cancelButton.addSelectionListener(new CancelSelection());
		this._shell.setDefaultButton(okButton);
		cancelButton.setText(CoreStrings.CANCEL);

	}

	/**
	 * Adds a listener to clear out items when hitting the close box
	 * @param parent
	 */
	private void addCloseBoxListener(Shell parent)
	{
		parent.addListener(SWT.Close, new Listener()
        {
			public void handleEvent(Event event)
			{
				handleCancel();	
			}
		});
	}
	
	/**
	 * @see com.aptana.ide.core.ui.dialogs.GenericDialog#handleCancel()
	 */
	private void handleCancel()
	{
		_event.password = null;
		_event.remember = false;
		_shell.dispose();
	}

	/**
	 * @see com.aptana.ide.core.io.IPasswordListener#getPassword(com.aptana.ide.core.io.PasswordEvent)
	 */
	public void getPassword(PasswordEvent event)
	{
		this._event = event;

		Display display = PlatformUI.getWorkbench().getDisplay();

		display.syncExec(new Runnable()
		{
			public void run()
			{
				getPassword();
			}
		});
	}
}
