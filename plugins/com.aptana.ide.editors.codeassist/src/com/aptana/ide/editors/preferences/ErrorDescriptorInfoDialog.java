/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ide.editors.preferences;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.IWorkbenchHelpContextIds;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editors.unified.errors.ErrorDescriptor;

/**
 * This class is used to prompt the user for a file name & extension.
 */
public class ErrorDescriptorInfoDialog extends TitleAreaDialog {

	private Text message;
	private String messageText;

    private Button okButton;
	private ErrorDescriptor error;

    /**
     * Constructs a new file extension dialog.
     * @param parentShell the parent shell
     */
    public ErrorDescriptorInfoDialog(Shell parentShell) {
        super(parentShell);
    }

    /**
     * Method declared in Window.
     * @param shell 
     */
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(CodeAssistMessages.ErrorDescriptorInfoDialog_IgnoreWarningError);
        //$NON-NLS-1$
        PlatformUI.getWorkbench().getHelpSystem().setHelp(shell,
				IWorkbenchHelpContextIds.FILE_EXTENSION_DIALOG);
    }

    /**
     * Creates and returns the contents of the upper part 
     * of the dialog (above the button bar).
     *
     * Subclasses should overide.
     *
     * @param parent the parent composite to contain the dialog area
     * @return the dialog area control
     */
    protected Control createDialogArea(Composite parent) {
        // top level composite
        Composite parentComposite = (Composite) super.createDialogArea(parent);

        // create a composite with standard margins and spacing
        Composite contents = new Composite(parentComposite, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        layout.numColumns = 2;
        contents.setLayout(layout);
        contents.setLayoutData(new GridData(GridData.FILL_BOTH));
        contents.setFont(parentComposite.getFont());

        setTitle(CodeAssistMessages.ErrorDescriptorInfoDialog_IgnoreWarningError); 
        setMessage(CodeAssistMessages.ErrorDescriptorInfoDialog_AddRegEx);

        // begin the layout

        Label label = new Label(contents, SWT.LEFT);
        label.setText(StringUtils.makeFormLabel(CodeAssistMessages.ErrorDescriptorInfoDialog_Message));

        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        label.setLayoutData(data);
        label.setFont(parent.getFont());

        message = new Text(contents, SWT.SINGLE | SWT.BORDER);
        message.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent event) {
                if (event.widget == message) {
                    if(okButton != null)
                    {
                    	okButton.setEnabled(validateErrorDescriptor());
                    }
                }
            }
        });
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        message.setLayoutData(data);
        message.setFocus();

        if(error != null)
        {
        	message.setText(error.getMessage());
        }
        
        Dialog.applyDialogFont(parentComposite);
        
        return contents;
    }
    
    /**
     * Validate the user input for a file type
     */
    private boolean validateErrorDescriptor() {
         // check for empty message
        if (StringUtils.EMPTY.equals(message.getText())) {
            return false;
        }

        messageText = message.getText();
        return true;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent) {
        okButton = createButton(parent, IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
        okButton.setEnabled(validateErrorDescriptor());
    }

    /**
     * Get the message.
     * 
     * @return the extension
     */
    public String getMessage() {
        return messageText;
    }

    /**
     * Sets the item
     * @param ed
     */
	public void setItem(ErrorDescriptor ed) {
		this.error = ed;
	}
}
