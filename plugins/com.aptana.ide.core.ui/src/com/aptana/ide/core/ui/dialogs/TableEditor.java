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
package com.aptana.ide.core.ui.dialogs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.aptana.ide.core.CoreStrings;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.SWTUtils;

/**
 * TableEditor
 * 
 * @author Ingo Muschenetz
 */
public class TableEditor extends Composite implements Listener
{
	private Button addResourceTypeButton;
	private Button editResourceTypeButton;
	private Button removeResourceTypeButton;
	private Label label;
	List<Object> _items = new ArrayList<Object>();
	private TableViewer _viewer;
	private List<IAddItemListener> _listeners = new ArrayList<IAddItemListener>();
	private boolean editEnabled = false;

	/**
	 * The description of the table editor
	 * 
	 * @param description
	 */
	public void setDescription(String description)
	{
		label.setText(description);
	}

	/**
	 * TableEditor
	 * 
	 * @param parent
	 * @param style
	 */
	public TableEditor(Composite parent, int style)
	{
		super(parent, style);
		createComposite(this);
	}

	/**
	 * TableEditor
	 * 
	 * @param parent
	 * @param style
	 * @param editEnabled
	 *            Is the "edit" button enabled?
	 */
	public TableEditor(Composite parent, int style, boolean editEnabled)
	{
		super(parent, style);
		this.editEnabled = editEnabled;
		createComposite(this);
	}

	/**
	 * setItems
	 * 
	 * @param items
	 */
	public void setItems(List<Object> items)
	{
		_items = items;
		_viewer.setInput(_items);
		refreshTable();
	}

	/**
	 * getItems
	 * 
	 * @return ArrayList
	 */
	public List<Object> getItems()
	{
		return _items;
	}

	/**
	 * getTable
	 * 
	 * @return Table
	 */
	public Table getTable()
	{
		return _viewer.getTable();
	}

	/**
	 * getAddButton
	 * 
	 * @return Button
	 */
	public Button getAddButton()
	{
		return addResourceTypeButton;
	}

	/**
	 * getEditButton
	 * 
	 * @return Button
	 */
	public Button getEditButton()
	{
		return editResourceTypeButton;
	}

	/**
	 * getRemoveButton
	 * 
	 * @return Button
	 */
	public Button getRemoveButton()
	{
		return removeResourceTypeButton;
	}

	/**
	 * createTable
	 * 
	 * @param parent
	 * @param items
	 * @return TableViewer
	 */
	private TableViewer createTable(Composite parent)
	{
		_viewer = new TableViewer(parent, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		Table serverTable = _viewer.getTable();
		serverTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		_viewer.setContentProvider(new TableContentProvider());
		_viewer.setLabelProvider(new TableLabelProvider());
		_viewer.setSorter(new TableSorter());
		_viewer.setInput(_items);
		_viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				updateEnabledState();
			}

		});
		updateEnabledState();
		return _viewer;
	}

	/**
	 * createComposite
	 * 
	 * @param pageComponent
	 */
	private void createComposite(Composite pageComponent)
	{
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		pageComponent.setLayout(layout);
		pageComponent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// layout the top table & its buttons
		label = new Label(pageComponent, SWT.LEFT | SWT.WRAP);
		label.setText(StringUtils.EMPTY);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.horizontalSpan = 2;
		label.setLayoutData(data);

		Composite groupComponent = new Composite(pageComponent, SWT.NULL);
		GridLayout groupLayout = new GridLayout();
		groupLayout.marginWidth = 0;
		groupLayout.marginHeight = 0;
		groupLayout.numColumns = 3;
		groupComponent.setLayout(groupLayout);
		data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		groupComponent.setLayoutData(data);

		addResourceTypeButton = new Button(groupComponent, SWT.PUSH);
		addResourceTypeButton.setToolTipText(StringUtils.ellipsify(CoreStrings.ADD));
		addResourceTypeButton.addListener(SWT.Selection, this);
		addResourceTypeButton.setImage(SWTUtils.getImage(CoreUIPlugin.getDefault(), "/icons/add.gif")); //$NON-NLS-1$

		if (editEnabled)
		{
			editResourceTypeButton = new Button(groupComponent, SWT.PUSH);
			editResourceTypeButton.setToolTipText(StringUtils.ellipsify(CoreStrings.EDIT));
			editResourceTypeButton.addListener(SWT.Selection, this);
			editResourceTypeButton.setImage(SWTUtils.getImage(CoreUIPlugin.getDefault(), "/icons/edit.png")); //$NON-NLS-1$
		}

		removeResourceTypeButton = new Button(groupComponent, SWT.PUSH);
		removeResourceTypeButton.setToolTipText(CoreStrings.REMOVE);
		removeResourceTypeButton.addListener(SWT.Selection, this);
		removeResourceTypeButton.setImage(SWTUtils.getImage(CoreUIPlugin.getDefault(), "/icons/delete.gif")); //$NON-NLS-1$

		createTable(pageComponent);

	}

	/**
	 * setSorter
	 * 
	 * @param sorter
	 */
	public void setSorter(ViewerSorter sorter)
	{
		_viewer.setSorter(sorter);
	}

	/**
	 * setLabelProvider
	 * 
	 * @param provider
	 */
	public void setLabelProvider(ITableLabelProvider provider)
	{
		_viewer.setLabelProvider(provider);
	}

	/**
	 * setContentProvider
	 * 
	 * @param provider
	 */
	public void setContentProvider(IStructuredContentProvider provider)
	{
		_viewer.setContentProvider(provider);
	}

	/**
	 * Remove the type from the table
	 */
	public void removeSelectedResourceType()
	{
		Table foldingTable = getTable();
		TableItem[] items = foldingTable.getSelection();
		int[] indices = foldingTable.getSelectionIndices();
		if (items != null)
		{
			for (int i = 0; i < items.length; i++)
			{
				_items.remove(items[i].getData());
				items[i].dispose();
			}
			if (indices.length > 1)
			{
				int last = indices[indices.length - 1];
				if (foldingTable.getItemCount() - 1 >= last)
				{
					foldingTable.setSelection(last);
				}
				else if (foldingTable.getItemCount() > 0)
				{
					foldingTable.setSelection(foldingTable.getItemCount() - 1);
				}
			}
			else if (indices.length == 1)
			{
				if (foldingTable.getItemCount() - 1 >= indices[0])
				{
					foldingTable.setSelection(indices[0]);
				}
				else if (foldingTable.getItemCount() > 0)
				{
					foldingTable.setSelection(foldingTable.getItemCount() - 1);
				}
			}
		}
		removeResourceTypeButton.setEnabled(foldingTable.getSelectionCount() > 0);
	}

	/**
	 * Remove the type from the table
	 */
	public void editSelectedResourceType()
	{
		ISelection selection = _viewer.getSelection();
		if (selection instanceof IStructuredSelection)
		{
			IStructuredSelection select = (IStructuredSelection) selection;
			Object o = select.getFirstElement();
			for (int i = 0; i < _listeners.size(); i++)
			{
				IAddItemListener l = _listeners.get(i);
				o = l.editItem(o);
				refreshTable();
			}
		}
	}

	/**
	 * addAddItemListener
	 * 
	 * @param listener
	 */
	public void addAddItemListener(IAddItemListener listener)
	{
		_listeners.add(listener);
	}

	/**
	 * removeAddItemListener
	 * 
	 * @param listener
	 */
	public void removeAddItemListener(IAddItemListener listener)
	{
		_listeners.add(listener);
	}

	/**
	 * @param imageCol
	 * @param fileNameCol
	 * @param folderPathCol
	 * @param messageCol
	 */
	private void refreshTable()
	{
		if (_viewer != null)
		{
			_viewer.refresh();
			Table t = _viewer.getTable();
			TableColumn[] columns = t.getColumns();
			for (int i = 0; i < columns.length; i++)
			{
				TableColumn column = columns[i];
				column.pack();
			}
		}
	}

	/**
	 * handleEvent
	 * 
	 * @param event
	 */
	public void handleEvent(Event event)
	{
		if (event.widget == addResourceTypeButton)
		{
			for (int i = 0; i < _listeners.size(); i++)
			{
				IAddItemListener l = _listeners.get(i);
				Object o = l.addItem();
				if (o != null)
				{
					_items.add(o);
					refreshTable();
				}
			}
		}
		else if (event.widget == editResourceTypeButton)
		{
			editSelectedResourceType();
		}
		else if (event.widget == removeResourceTypeButton)
		{
			removeSelectedResourceType();
		}

		updateEnabledState();
	}

	/**
	 * Update the enabled state.
	 */
	public void updateEnabledState()
	{
		// Update enabled state
		if(removeResourceTypeButton != null)
		{
			removeResourceTypeButton.setEnabled(_viewer.getSelection() != null && !_viewer.getSelection().isEmpty());
		}

		if(editResourceTypeButton != null)
		{
			editResourceTypeButton.setEnabled(_viewer.getSelection() != null && !_viewer.getSelection().isEmpty());
		}
	}

	/**
	 * TableContentProvider
	 * 
	 * @author Ingo Muschenetz
	 */
	public class TableContentProvider implements IStructuredContentProvider
	{
		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose()
		{
		}

		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}

		/**
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			ArrayList elements = (ArrayList) inputElement;
			ArrayList newElements = new ArrayList();
			for (Iterator iter = elements.iterator(); iter.hasNext();)
			{
				Object element = iter.next();
				newElements.add(element);
			}
			return newElements.toArray();
		}
	}

	/**
	 * TableLabelProvider
	 * 
	 * @author Ingo Muschenetz
	 */
	public class TableLabelProvider implements ITableLabelProvider
	{
		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex)
		{
			Image image = null;
			switch (columnIndex)
			{
				default:
					break;
			}
			return image;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex)
		{
			String name = StringUtils.EMPTY;
			switch (columnIndex)
			{
				default:
					name = element.toString();
					break;
			}
			return name;

		}

		/**
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void addListener(ILabelProviderListener listener)
		{
		}

		/**
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		public void dispose()
		{
		}

		/**
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
		 */
		public boolean isLabelProperty(Object element, String property)
		{
			return false;
		}

		/**
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void removeListener(ILabelProviderListener listener)
		{
		}
	}

	/**
	 * @author Ingo Muschenetz
	 */
	class TableSorter extends ViewerSorter
	{
		/**
		 * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object,
		 *      java.lang.Object)
		 */
		public int compare(Viewer viewer, Object e1, Object e2)
		{
			return super.compare(viewer, e1, e2);
		}
	}
}
