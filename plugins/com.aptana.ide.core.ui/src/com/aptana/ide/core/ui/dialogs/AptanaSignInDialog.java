/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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
package com.aptana.ide.core.ui.dialogs;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.ide.core.model.RESTServiceProvider;
import com.aptana.ide.core.model.user.AptanaUser;
import com.aptana.ide.core.model.user.User;
import com.aptana.ide.core.model.user.UserRequestBuilder;
import com.aptana.ide.core.online.OnlineDetectionService;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.CoreUIUtils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class AptanaSignInDialog extends TitleAreaDialog implements ModifyListener, SelectionListener
{

    private static final String IMAGE = "icons/aptana_dialog_tag.png"; //$NON-NLS-1$
	private static final String FORGOT_PASSWORD = "http://id.aptana.com/reset_password"; //$NON-NLS-1$
	private static final String WHAT_IS_APTANA_ID = "http://www.aptana.com/aptana_id"; //$NON-NLS-1$
	private static final String CREATE_ID = "https://id.aptana.com/register"; //$NON-NLS-1$

	private Text username;
	private Text password;
	private Link forgotPassword;
	private Link whatIsIt;
	private Link createId;
	
	private final boolean allowAnonymous;

	private KeyAdapter signInKeyAdapter = new KeyAdapter()
	{

		public void keyPressed(KeyEvent e)
		{
			if ((e.character == '\r' || e.character == '\n')
                    && getButton(IDialogConstants.OK_ID).getEnabled())
			{
                signIn();
            }
		}

	};
	/**
	 * @param parentShell
	 */
	public AptanaSignInDialog(Shell parentShell)
	{
		this(parentShell, false);
	}

	/**
	 * @param parentShell
	 * @param allowAnonymous - allow anonymous access to submit a bug
	 */
	public AptanaSignInDialog(Shell parentShell, boolean allowAnonymous)
	{
		super(parentShell);
		setShellStyle(getDefaultOrientation() | SWT.RESIZE | SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		setHelpAvailable(false);
		this.allowAnonymous = allowAnonymous;
	}

	/**
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	public void modifyText(ModifyEvent e)
    {
		setErrorMessage(null);
	    getButton(IDialogConstants.OK_ID).setEnabled(true);
    }

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e)
	{
	    Object source = e.getSource();
	    if (source == forgotPassword)
	    {
	        CoreUIUtils.openBrowserURL(FORGOT_PASSWORD);
	    }
	    else if (source == whatIsIt)
	    {
	        CoreUIUtils.openBrowserURL(WHAT_IS_APTANA_ID);
	    }
	    else if (source == createId)
	    {
	        CoreUIUtils.openBrowserURL(CREATE_ID);
	    }
	}

	/**
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent e)
	{
	}

	/**
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText(Messages.AptanaSignInWidget_Title);
	}

	/**
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
    protected Control createDialogArea(Composite parent)
    {
        setTitle(Messages.AptanaSignInWidget_LBL_MainTitle);
        setTitleImage(CoreUIPlugin.getImage(IMAGE));
        setMessage(Messages.AptanaSignInWidget_LBL_Subtitle);

        Composite main = new Composite(parent, SWT.NONE);
        main.setLayout(new GridLayout(2, false));
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        main.setLayoutData(gridData);
        createMiddleColumn(main);
        createRightColumn(main);

        return main;
    }

    /**
     * @see org.eclipse.jface.dialogs.TrayDialog#createButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent)
    {
    	if (allowAnonymous) {
			createButton(parent, IDialogConstants.IGNORE_ID,
					Messages.AptanaSignInDialog_LBL_Anonymous, false);
    	}
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setText(Messages.AptanaSignInWidget_LBL_SignIn);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

    /**
     * @see org.eclipse.jface.dialogs.TitleAreaDialog#getInitialSize()
     */
    protected Point getInitialSize()
    {
    	return getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     */
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.IGNORE_ID == buttonId) {
			setReturnCode(IDialogConstants.IGNORE_ID);
			close();
		} else {
			super.buttonPressed(buttonId);
		}
	}
	
    /**
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed()
    {
        signIn();
    }

	private void signIn()
	{
		if (!validate()) {
			return;
		}

	    // fetches and verifies the user
        final User user = new User(username.getText().toLowerCase(), password.getText(), null, null, null, null, null);
        try
        {
            user.setDefaultLocation(new URL(AptanaUser.LOGINS));
            user.setServiceProvider(new RESTServiceProvider());
            user.setRequestBuilder(new UserRequestBuilder());
            AptanaUser.signOut();
            // gets the user location
            user.update();
            if (user.hasLocation())
            {
                // gets the user model
                user.update();
                AptanaUser.signIn(username.getText().toLowerCase(), password.getText(), user.getLocation(), user
                                      .getId());
                super.okPressed();
                return;
            }
            if (user.getLastServiceErrors() != null && user.getLastServiceErrors().getItems().length > 0)
            {
                String message = user.getLastServiceErrors().getItems()[0].getMessage();
                if (message.length() > 1)
                {
                    message = message.substring(0, 1).toUpperCase() + message.substring(1, message.length());
                }
                setErrorMessage(message);
            }
            else if (!OnlineDetectionService.isAvailable(new URL(WHAT_IS_APTANA_ID)))
            {
            	setErrorMessage(Messages.AptanaSignInDialog_Label_ErrorOffline);
            }
            else
            {
                setErrorMessage(Messages.AptanaSignInWidget_Label_ErrorVerified);
            }
        }
        catch (MalformedURLException e)
        {
            setErrorMessage(Messages.AptanaSignInWidget_Label_ErrorVerified);
        }
        getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

	private boolean validate()
	{
	    if (username.getText().trim().length() == 0)
	    {
	        setErrorMessage(Messages.AptanaSignInWidget_LBL_ErrorUsername);
			getButton(IDialogConstants.OK_ID).setEnabled(false);
			return false;
	    }
	    if (password.getText().trim().length() == 0)
	    {
	        setErrorMessage(Messages.AptanaSignInWidget_LBL_ErrorPassword);
	        getButton(IDialogConstants.OK_ID).setEnabled(false);
	        return false;
	    }
	    setErrorMessage(null);
	    getButton(IDialogConstants.OK_ID).setEnabled(true);
	    return true;
	}

	private Composite createMiddleColumn(Composite parent)
	{
		Composite middle = new Composite(parent, SWT.NONE);
		middle.setLayout(new GridLayout(2, false));
		middle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label label = new Label(middle, SWT.LEFT);
		label.setText(Messages.AptanaSignInWidget_LBL_Username);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		username = new Text(middle, SWT.BORDER | SWT.SINGLE);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.widthHint = 125;
		username.setLayoutData(gridData);
		username.addModifyListener(this);
		username.addKeyListener(signInKeyAdapter);
		username.forceFocus();

		label = new Label(middle, SWT.LEFT);
		label.setText(Messages.AptanaSignInWidget_LBL_Password);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		password = new Text(middle, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData.widthHint = 125;
		password.setLayoutData(gridData);
		password.addModifyListener(this);
		password.addKeyListener(signInKeyAdapter);

		return middle;
	}

	private Composite createRightColumn(Composite parent)
	{
		Composite right = new Composite(parent, SWT.NONE);
		right.setLayout(new GridLayout());
		right.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

        whatIsIt = new Link(right, SWT.NONE);
        whatIsIt.setText("<a>" + Messages.AptanaSignInWidget_IDLink + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
        whatIsIt.addSelectionListener(this);

        forgotPassword = new Link(right, SWT.NONE);
        forgotPassword.setText("<a>" + Messages.AptanaSignInWidget_PasswordLink + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
        forgotPassword.addSelectionListener(this);

        createId = new Link(right, SWT.NONE);
        createId.setText("<a>" + Messages.AptanaSignInWidget_LBL_CreateID + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData.verticalIndent = 5;
        createId.setLayoutData(gridData);
        createId.addSelectionListener(this);

        return right;
	}

}
