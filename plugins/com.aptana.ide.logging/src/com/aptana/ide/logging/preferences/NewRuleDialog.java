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
package com.aptana.ide.logging.preferences;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.aptana.ide.logging.LoggingPreferences;
import com.aptana.ide.core.ui.contentassist.ContentAssistHandler;
import com.aptana.ide.core.ui.contentassist.LoggingRegExContentAssistProcessor;

/**
 * New Rule dialog.
 * 
 * @author Denis Denisenko
 */
public class NewRuleDialog extends TitleAreaDialog
{
	/**
	 * Add Regexp Rule help context.
	 */
	private static final String ADD_REGEXP_RULE_CONTEXT = "com.aptana.ide.logging.loggingview_add_simple_rule_context";  //$NON-NLS-1$

	/**
	 * Simple rule item.
	 */
    private static final String SIMPLE_RULE_ITEM = Messages.NewRuleDialog_6; 

    /**
     * Regular expression item.
     */
    private static final String REGEXP_RULE_ITEM = Messages.NewRuleDialog_7;
    
    /**
     * True if "new rule" mode is on, false if "edit rule" mode is on.
     */
    private final boolean newRuleMode;

	/**
	 * Name edit.
	 */
	private Text nameEdit;

	/**
	 * Content edit.
	 */
	private Text contentEdit;

	/**
	 * Name.
	 */
	private String name;

	/**
	 * Content.
	 */
	private String content;
	
	/**
	 * Whether rule is regexp. 
	 */
	private boolean isRegexp = false;
	
	/**
	 * Whether dialog is case insensetive.
	 */
	private boolean isCaseInsensitive = false;

	/**
	 * Content assist handler.
	 */
	private ContentAssistHandler fContentAssistHandler;
	
	/**
	 * Rule type combo.
	 */
	private Combo ruleTypeCombo;

	/**
	 * Forbidden names.
	 */
    private List<String> forbiddenNames = null;

	/**
	 * NewRuleDialog constructor.
	 * 
	 * @param parentShell - parent shell.
	 * @param initialContent - setting non-null value means the dialog is opened for editing.
	 * Initial content of dialog elements is created using the rule provided, but the rule itself 
	 * is NOT being modified. Setting null value causes dialog to be opened in "new rule" mode.
	 */
	public NewRuleDialog(Shell parentShell, LoggingPreferences.Rule initialContent)
	{
		super(parentShell);
		if (initialContent != null)
		{
		    newRuleMode = false;
		    name = initialContent.getName();
		    isRegexp = initialContent.isRegexp();
		    isCaseInsensitive = initialContent.isCaseInsensitive();
		    content = initialContent.getRule();
		}
		else
		{
		    newRuleMode = true;
		}
	}

    /**
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		super.createButtonsForButtonBar(parent);
		//validating
        validate();
	}

	/**
	 * NewRuleDialog constructor.
	 * Opens dialog in "new rule" mode, not editing.
	 * 
	 * @param parentShell
	 * @param initialContent - initial content of a rule.
	 */
	public NewRuleDialog(String initialContent, Shell parentShell)
	{
		super(parentShell);
		if (!hasIgnorableCharacters(initialContent))
        {
            content = initialContent;
        }
        else
        {
            content = replaceIgnorableCharacters(initialContent);
            isRegexp = true;
        }
		newRuleMode = true;
	}

	/**
	 * Gets name.
	 * 
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets content.
	 * 
	 * @return the content
	 */
	public String getContent()
	{
		return content;
	}
	
	/**
	 * Whether rule is regexp.
	 * @return whether rule is regexp.
	 */
	public boolean isRegexp()
	{
	    return isRegexp;
	}
	
	/**
	 * Whether rule is case insensitive.
	 * @return whether rule is case insensitive.
	 */
	public boolean isCaseInsensitive()
	{
	    return isCaseInsensitive;
	}
	
	/**
	 * Sets forbidden names.
	 * @param names - forbidden names list.
	 */
	public void setForbiddenNames(List<String> names)
	{
	    this.forbiddenNames = names;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void okPressed()
	{
	    if (nameEdit != null)
	    {
	        name = nameEdit.getText();
	    }
		content = contentEdit.getText();
		super.okPressed();
	}
	
	/**
     * {@inheritDoc}
     */
    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite returned = (Composite) super.createDialogArea(parent);

        Composite par = new Composite(returned, SWT.NONE);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.widthHint = 200;
        par.setLayoutData(data);

        //setting titles
        if (newRuleMode)
        {
            this.getShell().setText(Messages.NewRuleDialog_0);
            this.setTitle(Messages.NewRuleDialog_0); 
            this.setMessage(Messages.NewRuleDialog_Rule_Message); 
        }
        else
        {
            this.getShell().setText(Messages.EditRuleDialog_0);
            this.setTitle(Messages.EditRuleDialog_0);     
            this.setMessage(Messages.EditRuleDialog_1);
        }

        par.setLayout(new GridLayout(4, false));

        if (newRuleMode)
        {
            createNameEdit(par);
        }

        createContentEdit(par);
        
        createCaseSensetiveEdit(par);
        
        createRuleTypeEdit(par);
        
        PlatformUI.getWorkbench().getHelpSystem().setHelp(par, ADD_REGEXP_RULE_CONTEXT);
        setTooltipText();
        
        return par;
    }

    /**
     * Creates rule type edit.
     * @param par - parent.
     */
    private void createRuleTypeEdit(Composite par)
    {
        Label ruleTypeLabel = new Label(par, SWT.NONE);
        ruleTypeLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        ruleTypeLabel.setText(Messages.NewRuleDialog_8); 
        
        ruleTypeCombo = new Combo(par, SWT.READ_ONLY);
        ruleTypeCombo.setItems(new String[]{SIMPLE_RULE_ITEM, REGEXP_RULE_ITEM});
        
        //selecting default
        if (isRegexp)
        {
            ruleTypeCombo.select(1);
        }
        else
        {
            ruleTypeCombo.select(0);
        }
        
        
        ruleTypeCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        createContentAssistant();
        
        
        fContentAssistHandler.setEnabled(isRegexp);
        
        
        ruleTypeCombo.addSelectionListener(new SelectionListener()
        {

            public void widgetDefaultSelected(SelectionEvent e)
            {
            }

            public void widgetSelected(SelectionEvent e)
            {
                if (ruleTypeCombo.getSelectionIndex() == 1)
                {
                    
                    fContentAssistHandler.setEnabled(true);
                    contentEdit.setToolTipText(Messages.NewRuleDialog_TTP_RegularExpression);
                    isRegexp = true;
                }
                else
                {
                    fContentAssistHandler.setEnabled(false);
                    contentEdit.setToolTipText(Messages.NewRuleDialog_TTP_Search);
                    isRegexp = false;
                }
                
                setTooltipText();
            }
            
        });
    }
    
    /**
     * Creates case sensitive edit.
     * @param par - parent.
     */
    private void createCaseSensetiveEdit(Composite par)
    {
        Label caseSensetiveLabel = new Label(par, SWT.NONE);
        caseSensetiveLabel.setText(Messages.CaseSensitiveLabel + ":"); //$NON-NLS-1$
        caseSensetiveLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        final Button caseSensitiveBox = new Button(par, SWT.CHECK);
        caseSensitiveBox.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        if (!newRuleMode)
        {
            caseSensitiveBox.setSelection(isCaseInsensitive);
        }
        
        caseSensitiveBox.addSelectionListener(new SelectionListener()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }

            public void widgetSelected(SelectionEvent e)
            {
                isCaseInsensitive = caseSensitiveBox.getSelection();
            }
        });
    }

    /**
     * Creates rule content edit.
     * @param par - parent.
     */
    private void createContentEdit(Composite par)
    {
        Label contentLabel = new Label(par, SWT.NONE);
        contentLabel.setText(Messages.NewRuleDialog_4 + ":"); //$NON-NLS-1$
        contentLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        contentEdit = new Text(par, SWT.BORDER);
        contentEdit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        if (content != null)
        {
            contentEdit.setText(content);
        }
        contentEdit.addModifyListener(new ModifyListener()
        {

            public void modifyText(ModifyEvent e)
            {
                validate();
            }
        });
    }

    /**
     * Create rule name edit.
     * @param par - parent.
     */
    private void createNameEdit(Composite par)
    {
        Label nameLabel = new Label(par, SWT.NONE);
        nameLabel.setText(Messages.NewRuleDialog_2 + ":");  //$NON-NLS-1$
        nameLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        nameEdit = new Text(par, SWT.BORDER);
        nameEdit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        nameEdit.addModifyListener(new ModifyListener()
        {

            public void modifyText(ModifyEvent e)
            {
                validate();
            }
        });
        if (name != null)
        {
            nameEdit.setText(name);
        }
    }

    /**
     * Sets tooltip text.
     */
    protected void setTooltipText()
    {
        if (ruleTypeCombo.getSelectionIndex() == 1)
        {            
            contentEdit.setToolTipText(Messages.NewRuleDialog_TTP_RegularExpression);
        }
        else
        {
            contentEdit.setToolTipText(Messages.NewRuleDialog_TTP_Search);
        }
    }

	/**
	 * Validates values.
	 */
	private void validate()
	{
	    String contentText = contentEdit.getText();
	    //replacing ignorable characters
        if (hasIgnorableCharacters(contentText))
        {
            contentEdit.setText(replaceIgnorableCharacters(contentText));
            setRegexpBased(true);
        }
	    
	    if (newRuleMode)
	    {
	        String nameText = nameEdit.getText();
    		if (nameText.length() == 0)
    		{
    			disableOKButton();
    			setErrorMessage(Messages.NewRuleDialog_RuleError_Name);
    			return;
    		}
    		String trimmedName = nameText.trim();
    		if (forbiddenNames != null && forbiddenNames.contains(trimmedName))
    		{
    		    disableOKButton();
    		    setErrorMessage(Messages.NewRuleDialog_RuleError_Name_AlreadyExists);
    		    return;
    		}
	    }
		
		if (contentText.length() == 0)
		{
		    setErrorMessage(Messages.NewRuleDialog_RuleError_RegexpRule_Content); 
		
			disableOKButton();
			return;
		}
		
        setErrorMessage(null);
        Button button = getButton(IDialogConstants.OK_ID);
        if (button != null){
            button.setEnabled(true);
        }
	}

    /**
     * Disables OK button.
     */
    private void disableOKButton()
    {
        Button button = getButton(IDialogConstants.OK_ID);
        if (button != null){
        	button.setEnabled(false);
        }
    }

	/**
	 * Creates regexp content assistant.
	 */
	private void createContentAssistant()
	{
		fContentAssistHandler = ContentAssistHandler.createHandlerForText(contentEdit, LoggingRegExContentAssistProcessor
				.createContentAssistant());
	}
	
	/**
	 * Replaces ignorable characters with a '.' sign.
     * @param str - input string.
     * @return string with ignorable characters replaced.
     */
    private String replaceIgnorableCharacters(String str)
    {
        StringBuilder builder = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++)
        {
            char ch = str.charAt(i); 
            if(Character.isIdentifierIgnorable(ch))
            {
                builder.append('.');
            }
            else
            {
                builder.append(ch);
            }
        }
        return builder.toString();
    }

    /**
     * Checks whether string contains ingnorable characters.
     * @param str - string to check.
     * @return true if contains, false otherwise.
     */
    private boolean hasIgnorableCharacters(String str)
    {
        for (int i = 0; i < str.length(); i++)
        {
            if(Character.isIdentifierIgnorable(str.charAt(i)))
            {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Sets whether rule is regexp-based.
     * @param isRegexp - whether rule is regexp or not.
     */
    private void setRegexpBased(boolean isRegexp)
    {
        this.isRegexp = isRegexp;
        fContentAssistHandler.setEnabled(isRegexp);
        if(isRegexp)
        {
            ruleTypeCombo.select(1);
        }
        else
        {
            ruleTypeCombo.select(0);
        }
    }
}
