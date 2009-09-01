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


///**
// * Edit Rule dialog.
// * @author Denis Denisenko
// */
//public class EditRuleDialog extends TitleAreaDialog
//{
//    /**
//     * Initial content.
//     */
//    private final String initialContent;
// 
//    /**
//     * Content edit.
//     */
//    private Text contentEdit;
//    
//    /**
//     * Content.
//     */
//    private String content;
//
//    /**
//     * Regexp content assistant. 
//     */
//    private ContentAssistHandler fReplaceContentAssistHandler;
//    
//    /**
//     * Whether rule is regexp-based.
//     */
//    private boolean isRegexp;
//
//    /**
//     * NewRuleDialog constructor.
//     * @param parentShell
//     * @param initialContent - initial content.
//     * @param isRegexp - whether rule is regexp-based.
//     */
//    public EditRuleDialog(Shell parentShell, String initialContent, boolean isRegexp)
//    {
//        super(parentShell);
//        this.initialContent = initialContent;
//        this.isRegexp = isRegexp;
//    }
//
//    /**
//      * {@inheritDoc}
//      */
//    @Override
//    protected Control createDialogArea(Composite parent)
//    {
//        Composite returned = (Composite) super.createDialogArea(parent);
//        
//        Composite par = new Composite(returned, SWT.NONE);
//        par.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//        
//        this.getShell().setText(Messages.EditRuleDialog_0);
//        this.setTitle(Messages.EditRuleDialog_0); 
//        this.setMessage(Messages.EditRuleDialog_1); 
//        par.setLayout(new GridLayout(2, false));
//        
//        Label contentLabel = new Label(par, SWT.NONE);
//        contentLabel.setText(Messages.EditRuleDialog_2 + ":");  
//        contentLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
//        contentEdit = new Text(par, SWT.BORDER);
//        contentEdit.setText(initialContent);
//        contentEdit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
//        contentEdit.addModifyListener(new ModifyListener()
//        {
//
//            public void modifyText(ModifyEvent e)
//            {
//                validate();
//            }
//        });
//        
//        validate();
//        
//        if (isRegexp)
//        {
//            createContentAssistant();
//        }
//        
//        return par;
//    }
//
//    /**
//     * Gets content.
//     * @return the content
//     */
//    public String getContent()
//    {
//        return content;
//    }
//
//    /**
//      * {@inheritDoc}
//      */
//    @Override
//    protected void okPressed()
//    {
//        content = contentEdit.getText();
//        super.okPressed();
//    }
//    
//    /**
//     * Validates values.
//     */
//    private void validate()
//    {
//        String contentText = contentEdit.getText();
//
//        if(contentText.length() == 0)
//        {
//            if (isRegexp)
//            {
//                setErrorMessage(Messages.EditRuleDialog_RuleError_RegexpRule_Content);
//            }
//            else
//            {
//                setErrorMessage(Messages.EditRuleDialog_RuleError_SimpleRule_Content);
//            }
//        }
//    }
//    
//    /**
//     * Creates regexp content assistant.
//     */
//    private void createContentAssistant() {
//        fReplaceContentAssistHandler = 
//            ContentAssistHandler.createHandlerForText(contentEdit, ReplaceDialog2.createContentAssistant(true));
//    }
//}
