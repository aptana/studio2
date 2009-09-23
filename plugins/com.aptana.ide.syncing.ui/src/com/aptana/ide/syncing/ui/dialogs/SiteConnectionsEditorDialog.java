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

package com.aptana.ide.syncing.ui.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.aptana.ide.core.CoreStrings;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.SWTUtils;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.SiteConnection;
import com.aptana.ide.syncing.core.SyncingPlugin;
import com.aptana.ide.syncing.ui.internal.SiteConnectionPropertiesWidget;

/**
 * @author Max Stepanov
 *
 */
public class SiteConnectionsEditorDialog extends TitleAreaDialog {

	private ISiteConnection selection;
	
	private ListViewer sitesViewer;
	private Button addButton;
	private Button removeButton;
	private SiteConnectionPropertiesWidget sitePropertiesWidget;
	
	private List<ISiteConnection> sites = new ArrayList<ISiteConnection>();
	
	/**
	 * @param parentShell
	 */
	public SiteConnectionsEditorDialog(Shell parentShell) {
		super(parentShell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        setHelpAvailable(false);
        
		sites.addAll(Arrays.asList(SyncingPlugin.getSiteConnectionManager().getSiteConnections()));
	}

	public void setCreateNew(String name, IAdaptable source, IAdaptable destination) {
		IConnectionPoint sourceConnection = null;
		IConnectionPoint destinationConnection = null;

		SiteConnection siteConnection = new SiteConnection();
		siteConnection.setName(name);
		siteConnection.setSource(sourceConnection);
		siteConnection.setDestination(destinationConnection);
		sites.add(siteConnection);
		setSelection(siteConnection);
	}
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Connection Manager");
	}

	protected Control createDialogArea(Composite parent) {
		Composite dialogArea = (Composite) super.createDialogArea(parent);

        setTitle("Connection Manager");
        setMessage("Configures connections between a local container and a remote site or another local container.");

		Composite container = new Composite(dialogArea, SWT.NONE);
		container.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		container.setLayout(GridLayoutFactory.swtDefaults()
				.margins(convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN), convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN))
				.spacing(convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING), convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING))
				.create());

		SashForm sashForm = new SashForm(container, SWT.HORIZONTAL);
		sashForm.setLayoutData(GridDataFactory.fillDefaults().hint(SWT.DEFAULT, 400).grab(true, true).create());

		/* column 1 */
		Group group = new Group(sashForm, SWT.NONE);
		group.setText("Connections");
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		group.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());

		sitesViewer = new ListViewer(group, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
		sitesViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(2, 1).create());
		sitesViewer.setContentProvider(new ArrayContentProvider());
		sitesViewer.setLabelProvider(new SitesLabelProvider());
		
		addButton = new Button(group, SWT.PUSH);
		addButton.setLayoutData(GridDataFactory.swtDefaults().create());
		addButton.setImage(SWTUtils.getImage(CoreUIPlugin.getDefault(), "/icons/add.gif")); //$NON-NLS-1$
		addButton.setToolTipText(StringUtils.ellipsify(CoreStrings.ADD));
		
		removeButton = new Button(group, SWT.PUSH);
		removeButton.setLayoutData(GridDataFactory.swtDefaults().create());
		removeButton.setImage(SWTUtils.getImage(CoreUIPlugin.getDefault(), "/icons/delete.gif")); //$NON-NLS-1$
		removeButton.setToolTipText(CoreStrings.REMOVE);
		
		/* column 2 */
		sitePropertiesWidget = new SiteConnectionPropertiesWidget(sashForm, SWT.NONE);
		sitePropertiesWidget.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		sashForm.setWeights(new int[] { 30, 70 });
				
		/* -- */
		sitesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				sitePropertiesWidget.setSource((ISiteConnection) ((IStructuredSelection) event.getSelection()).getFirstElement());
			}
		});
		sitesViewer.setInput(sites);
		
		if (selection != null) {
			sitesViewer.setSelection(new StructuredSelection(selection), true);
		}

		return dialogArea;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.APPLY_ID, IDialogConstants.APPLY_LABEL, false);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.APPLY_ID) {
			applyPressed();
		}
		super.buttonPressed(buttonId);
	}

	protected void okPressed() {
		if (applyPressed()) {
			super.okPressed();
		}
	}

    protected boolean applyPressed() {
    	return true;
    }
    
    public void setSelection(ISiteConnection selection) {
    	this.selection = selection;
    	if (sitesViewer != null) {
    		sitesViewer.setSelection(new StructuredSelection(selection), true);
    	}
    }
    
    private class SitesLabelProvider extends LabelProvider {

    	/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if (element instanceof ISiteConnection) {
				return ((ISiteConnection) element).getName();
			}
			return super.getText(element);
		}
    }
}
