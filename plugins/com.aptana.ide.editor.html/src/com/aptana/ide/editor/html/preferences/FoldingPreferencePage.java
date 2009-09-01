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
package com.aptana.ide.editor.html.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.aptana.ide.editor.html.HTMLPlugin;
import com.aptana.ide.editor.html.parsing.HTMLMimeType;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class FoldingPreferencePage extends
		com.aptana.ide.editors.preferences.FoldingPreferencePage {

	private Table foldingTable;
	private Group nodeGroup;
	private Composite buttons;
	private Button add;
	private Button remove;

	/**
	 * @see com.aptana.ide.editors.preferences.FoldingPreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		final Composite displayArea = (Composite) super.createContents(parent);
		nodeGroup = new Group(displayArea, SWT.NONE);
		GridLayout groupLayout = new GridLayout(1, true);
		nodeGroup.setLayout(groupLayout);
		nodeGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		nodeGroup.setText(Messages.FoldingPreferencePage_FoldableHTMLNodes);
		buttons = new Composite(nodeGroup, SWT.NONE);
		GridLayout buttonsLayout = new GridLayout(2, true);
		buttonsLayout.marginHeight = 0;
		buttonsLayout.marginWidth = 0;
		buttons.setLayout(buttonsLayout);
		add = new Button(buttons, SWT.PUSH);
		add.setImage(HTMLPlugin.getImage("icons/add_obj.gif")); //$NON-NLS-1$
		add.setToolTipText(Messages.FoldingPreferencePage_Add_One_Or_Mode_Tooltip);
		add.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				InputDialog dialog = new InputDialog(
						getShell(),
						Messages.FoldingPreferencePage_AddFoldableNodes,
						Messages.FoldingPreferencePage_AddFoldableNodesDesc,
						null, null);
				int rc = dialog.open();
				if (rc == InputDialog.OK) {
					String newNodes = dialog.getValue();
					String[] nodes = newNodes.split(","); //$NON-NLS-1$
					List nodeList = new ArrayList();
					for (int i = 0; i < nodes.length; i++) {
						String trimmed = nodes[i].trim();
						if (trimmed.length() > 0) {
							nodeList.add(trimmed);
						}
					}
					TableItem[] items = foldingTable.getItems();
					for (int i = 0; items != null && i < items.length; i++) {
						if (!nodeList.contains(items[i].getText())) {
							nodeList.add(items[i].getText());
						}
					}
					Collections.sort(nodeList);
					foldingTable.removeAll();
					for (int i = 0; i < nodeList.size(); i++) {
						TableItem item = new TableItem(foldingTable, SWT.LEFT);
						item.setText((String) nodeList.get(i));
						item.setImage(HTMLPlugin
								.getImage("icons/element_icon.gif")); //$NON-NLS-1$
					}
				}
			}

		});
		remove = new Button(buttons, SWT.PUSH);
		remove.setImage(HTMLPlugin.getImage("icons/delete_obj.gif")); //$NON-NLS-1$
		remove.setToolTipText(Messages.FoldingPreferencePage_RemoveSelectedNode);
		remove.setEnabled(false);
		remove.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = foldingTable.getSelection();
				int[] indices = foldingTable.getSelectionIndices();
				if (items != null) {
					for (int i = 0; i < items.length; i++) {
						items[i].dispose();
					}
					if (indices.length > 1) {
						int last = indices[indices.length - 1];
						if (foldingTable.getItemCount() - 1 >= last) {
							foldingTable.setSelection(last);
						} else if (foldingTable.getItemCount() > 0) {
							foldingTable.setSelection(foldingTable
									.getItemCount() - 1);
						}
					} else if (indices.length == 1) {
						if (foldingTable.getItemCount() - 1 >= indices[0]) {
							foldingTable.setSelection(indices[0]);
						} else if (foldingTable.getItemCount() > 0) {
							foldingTable.setSelection(foldingTable
									.getItemCount() - 1);
						}
					}
				}
				remove.setEnabled(foldingTable.getSelectionCount() > 0);
			}

		});
		buttons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		foldingTable = new Table(nodeGroup, SWT.MULTI | SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.BORDER);
		foldingTable
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		foldingTable.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				remove.setEnabled(true);
			}

		});
		String foldedNodeList = HTMLPlugin.getDefault().getPreferenceStore()
				.getString(IPreferenceConstants.FOLDING_HTML_NODE_LIST);
		String[] nodes = foldedNodeList.split(","); //$NON-NLS-1$
		for (int i = 0; i < nodes.length; i++) {
			TableItem item = new TableItem(foldingTable, SWT.LEFT);
			item.setText(nodes[i].trim());
			item.setImage(HTMLPlugin.getImage("icons/element_icon.gif")); //$NON-NLS-1$
		}
		return displayArea;
	}

	/**
	 * @see com.aptana.ide.editors.preferences.FoldingPreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		foldingTable.removeAll();
		String foldedNodeList = HTMLPlugin.getDefault().getPreferenceStore()
				.getDefaultString(IPreferenceConstants.FOLDING_HTML_NODE_LIST);
		String[] nodes = foldedNodeList.split(","); //$NON-NLS-1$
		for (int i = 0; i < nodes.length; i++) {
			TableItem item = new TableItem(foldingTable, SWT.LEFT);
			item.setText(nodes[i].trim());
			item.setImage(HTMLPlugin.getImage("icons/element_icon.gif")); //$NON-NLS-1$
		}
		super.performDefaults();
	}

	/**
	 * @see com.aptana.ide.editors.preferences.FoldingPreferencePage#performOk()
	 */
	public boolean performOk() {
		String nodeListPref = ""; //$NON-NLS-1$
		for (int i = 0; i < foldingTable.getItemCount(); i++) {
			TableItem item = foldingTable.getItem(i);
			nodeListPref += item.getText() + ","; //$NON-NLS-1$
		}
		HTMLPlugin.getDefault().getPreferenceStore().setValue(
				IPreferenceConstants.FOLDING_HTML_NODE_LIST, nodeListPref);
		return super.performOk();
	}

	/**
	 * @see com.aptana.ide.editors.preferences.FoldingPreferencePage#addInitialFoldingFields()
	 */
	public void addInitialFoldingFields() {
		// Does nothing
	}

	/**
	 * @see com.aptana.ide.editors.preferences.FoldingPreferencePage#getLanguage()
	 */
	public String getLanguage() {
		return HTMLMimeType.MimeType;
	}

	/**
	 * @see com.aptana.ide.editors.preferences.FoldingPreferencePage#storeToInitialize()
	 */
	public IPreferenceStore storeToInitialize() {
		return HTMLPlugin.getDefault().getPreferenceStore();
	}

}
