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
package com.aptana.ide.core.ui.io.file;

import java.text.DateFormat;
import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.ide.core.io.IVirtualFile;
import com.aptana.ide.core.ui.SWTUtils;

/**
 * @author Ingo Muschenetz
 * @author Michael Xia (modified to use JFace Dialog)
 */
public class InfoDialog extends Dialog {

    private IVirtualFile _item;

    private Text _where;
    private Text _kind;
    private Text _size;
    private Text _modified;

    /**
     * Create the dialog.
     * 
     * @param parent
     *            the parent shell
     */
    public InfoDialog(Shell parent) {
        super(parent);
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    /**
     * Sets the item to show the information on.
     * 
     * @param item
     *            the file item
     */
    public void setItem(IVirtualFile item) {
        _item = item;
    }

    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.InfoDialog_Info);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets.Composite)
     */
    protected Control createContents(Composite parent) {
        Control control = super.createContents(parent);

        Shell shell = getShell();
        Shell parentShell = getShell().getParent().getShell();
        SWTUtils.centerAndPack(shell, parentShell);

        return control;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea(Composite parent) {
        Composite main = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        main.setLayout(layout);
        main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Group group = new Group(main, SWT.NONE);
        group.setText(Messages.InfoDialog_General);
        layout = new GridLayout();
        layout.numColumns = 2;
        group.setLayout(layout);
        group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2,
                1));

        Label label = new Label(group, SWT.NONE);
        label.setText(Messages.InfoDialog_Kind);
        _kind = new Text(group, SWT.READ_ONLY);

        label = new Label(group, SWT.NONE);
        label.setText(Messages.InfoDialog_Size);
        _size = new Text(group, SWT.READ_ONLY);

        label = new Label(group, SWT.NONE);
        label.setText(Messages.InfoDialog_Where);
        _where = new Text(group, SWT.READ_ONLY);

        label = new Label(group, SWT.NONE);
        label.setText(Messages.InfoDialog_Modified);
        _modified = new Text(group, SWT.READ_ONLY);

        setInitialFieldValues();

        return main;
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent) {
        // creates only the OK button
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
                true);
    }

    /**
     * Sets the initial field values
     */
    private void setInitialFieldValues() {
        _kind.setText(_item.isDirectory() ? Messages.InfoDialog_Folder
                : Messages.InfoDialog_File);
        _size.setText(_item.getSize() + Messages.InfoDialog_Bytes);
        _where.setText(_item.getAbsolutePath());

        if (_item.getModificationMillis() != 0l) {
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG,
                    DateFormat.LONG);
            _modified.setText(df
                    .format(new Date(_item.getModificationMillis())));
        } else if (_item.getTimeStamp() != null) {
            _modified.setText(_item.getTimeStamp());
        }

    }

}
