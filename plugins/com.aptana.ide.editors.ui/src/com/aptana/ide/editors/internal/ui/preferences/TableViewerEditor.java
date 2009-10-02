/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ide.editors.internal.ui.preferences;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Widget;

/**
 * An abstract field editor that manages a list of input values. The editor displays a list containing the values,
 * buttons for adding and removing values, and Up and Down buttons to adjust the order of elements in the list.
 * <p>
 * Subclasses must implement the <code>parseString</code>, <code>createList</code>, and <code>getNewInputObject</code>
 * framework methods.
 * </p>
 */
public abstract class TableViewerEditor extends FieldEditor
{

	public static class ColumnsDescription
	{
		private ColumnLayoutData[] columns;
		private String[] headers;
		private boolean drawLines;

		public ColumnsDescription(ColumnLayoutData[] columns, String[] headers, boolean drawLines)
		{
			this.columns = columns;
			this.headers = headers;
			this.drawLines = drawLines;
		}

		public ColumnsDescription(String[] headers, boolean drawLines)
		{
			this(createColumnWeightData(headers.length), headers, drawLines);
		}

		public ColumnsDescription(int nColumns, boolean drawLines)
		{
			this(createColumnWeightData(nColumns), null, drawLines);
		}

		private static ColumnLayoutData[] createColumnWeightData(int nColumns)
		{
			ColumnLayoutData[] data = new ColumnLayoutData[nColumns];
			for (int i = 0; i < nColumns; i++)
			{
				data[i] = new ColumnWeightData(1);
			}
			return data;
		}
	}

	/**
	 * The table viewer widget; <code>null</code> if none (before creation or after disposal).
	 */
	private TableViewer fTable;

	protected Control fTableControl;
	private ColumnsDescription fTableColumns;

	private ListViewerAdapter fListViewerAdapter;

	/**
	 * The button box containing the Add, Remove, Up, and Down buttons; <code>null</code> if none (before creation or
	 * after disposal).
	 */
	private Composite buttonBox;

	protected List<Object> fElements;

	/**
	 * The Add button.
	 */
	private Button addButton;
	
	/**
	 * The Edit button.
	 */
	private Button editButton;

	/**
	 * The Remove button.
	 */
	private Button removeButton;

	/**
	 * The selection listener.
	 */
	private SelectionListener selectionListener;

	/**
	 * Creates a table viewer editor.
	 * 
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 */
	protected TableViewerEditor(String labelText, Composite parent)
	{
		init("", labelText);
		fListViewerAdapter = new ListViewerAdapter();
		fElements = new ArrayList(10);
		fTableColumns = createTableColumns();
		createControl(parent);
	}

	protected ColumnsDescription createTableColumns()
	{
		return null;
	}

	protected abstract ITableLabelProvider createLabelProvider();

	/**
	 * Notifies that the Add button has been pressed.
	 */
	private void addPressed()
	{
		setPresentsDefaultValue(false);
		Object input = createObject();
		if (input != null)
		{
			addElement(input);
		}
	}
	
	private void editPressed()
	{
		setPresentsDefaultValue(false);
		Object toEdit = getSelectedElements().get(0);
		Object input = editObject(toEdit);
		if (input != null)
		{
			replaceElement(toEdit, input);
		}
	}
	
	/**
	 * Edits an existing in the table. Called when the user selects the edit button.
	 * <p>
	 * Subclasses must implement this method.
	 * </p>
	 * 
	 * @return the item to replace the existing one 9may be the original item if no changes were made)
	 */
	protected abstract Object editObject(Object toEdit);

	/**
	 * Replace an element.
	 */		
	private void replaceElement(Object oldElement, Object newElement) throws IllegalArgumentException { 
		int idx= fElements.indexOf(oldElement);
		if (idx != -1) {
			fElements.set(idx, newElement);
			if (isOkToUse(fTableControl)) {
				List selected= getSelectedElements();
				if (selected.remove(oldElement)) {
					selected.add(newElement);
				}
				fTable.refresh();
				selectElements(new StructuredSelection(selected));
			}
			selectionChanged();
		} else {
			throw new IllegalArgumentException();
		}
	}	

	private void selectElements(ISelection selection) {
		if (isOkToUse(fTableControl)) {
			fTable.setSelection(selection, true);
		}
	}
	
	/**
	 * Adds an element at the end of the list.
	 */
	public boolean addElement(Object element)
	{
		return addElement(element, fElements.size());
	}

	/**
	 * Adds an element at a position.
	 */
	public boolean addElement(Object element, int index)
	{
		if (fElements.contains(element))
		{
			return false;
		}
		fElements.add(index, element);
		if (isOkToUse(fTableControl))
		{
			fTable.refresh();
			fTable.setSelection(new StructuredSelection(element));
		}

		selectionChanged();
		return true;
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void adjustForNumColumns(int numColumns)
	{
		Control control = getLabelControl();
		((GridData) control.getLayoutData()).horizontalSpan = numColumns;
		((GridData) fTableControl.getLayoutData()).horizontalSpan = numColumns - 1;
	}

	/**
	 * Creates the Add, Edit, Remove, Up, and Down button in the given button box.
	 * 
	 * @param box
	 *            the box for the buttons
	 */
	private void createButtons(Composite box)
	{
		addButton = createPushButton(box, "ListEditor.add");//$NON-NLS-1$
		editButton = createPushButton(box, "TableViewerEditor.edit");//$NON-NLS-1$
		editButton.setText("&Edit...");
		removeButton = createPushButton(box, "ListEditor.remove");//$NON-NLS-1$
		//        upButton = createPushButton(box, "ListEditor.up");//$NON-NLS-1$
		//        downButton = createPushButton(box, "ListEditor.down");//$NON-NLS-1$
	}

	/**
	 * Helper method to create a push button.
	 * 
	 * @param parent
	 *            the parent control
	 * @param key
	 *            the resource name used to supply the button's label text
	 * @return Button
	 */
	private Button createPushButton(Composite parent, String key)
	{
		Button button = new Button(parent, SWT.PUSH);
		button.setText(JFaceResources.getString(key));
		button.setFont(parent.getFont());
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		int widthHint = convertHorizontalDLUsToPixels(button, IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		button.setLayoutData(data);
		button.addSelectionListener(getSelectionListener());
		return button;
	}

	/**
	 * Creates a selection listener.
	 */
	public void createSelectionListener()
	{
		selectionListener = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent event)
			{
				Widget widget = event.widget;
				if (widget == addButton)
				{
					addPressed();
				}
				else if (widget == editButton)
				{
					editPressed();
				}
				else if (widget == removeButton)
				{
					removePressed();
					// } else if (widget == upButton) {
					// upPressed();
					// } else if (widget == downButton) {
					// downPressed();
				}
				else if (widget == fTable.getTable())
				{
					selectionChanged();
				}
			}
		};
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doFillIntoGrid(Composite parent, int numColumns)
	{
		Control control = getLabelControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns;
		control.setLayoutData(gd);

		Control list = getListControl(parent);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalSpan = numColumns - 1;
		gd.grabExcessHorizontalSpace = true;
		list.setLayoutData(gd);

		buttonBox = getButtonBoxControl(parent);
		gd = new GridData();
		gd.verticalAlignment = GridData.BEGINNING;
		buttonBox.setLayoutData(gd);
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected abstract void doLoad();

	public void removeAllElements()
	{
		if (fElements.size() > 0)
		{
			fElements.clear();
			if (isOkToUse(fTableControl))
			{
				fTable.refresh();
			}
			selectionChanged();
		}
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected abstract void doLoadDefault();

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doStore()
	{
		Map<String, String> s = createPrefMap(fElements);
		if (s != null)
		{
			for (Map.Entry<String, String> entry : s.entrySet())
				getPreferenceStore().setValue(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Notifies that the Down button has been pressed.
	 */
	// private void downPressed() {
	// swap(false);
	// }

	/**
	 * Convert the custom objects into a map of preference keys to values to store the objects in preferences.
	 */
	protected abstract Map<String, String> createPrefMap(List<Object> elements);

	/**
	 * Returns this field editor's button box containing the Add, Remove, Up, and Down button.
	 * 
	 * @param parent
	 *            the parent control
	 * @return the button box
	 */
	public Composite getButtonBoxControl(Composite parent)
	{
		if (buttonBox == null)
		{
			buttonBox = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			layout.marginWidth = 0;
			buttonBox.setLayout(layout);
			createButtons(buttonBox);
			buttonBox.addDisposeListener(new DisposeListener()
			{
				public void widgetDisposed(DisposeEvent event)
				{
					addButton = null;
					editButton = null;
					removeButton = null;
					// upButton = null;
					// downButton = null;
					buttonBox = null;
				}
			});

		}
		else
		{
			checkParent(buttonBox, parent);
		}

		selectionChanged();
		return buttonBox;
	}

	/**
	 * Returns this field editor's list control.
	 * 
	 * @param parent
	 *            the parent control
	 * @return the list control
	 */
	public Control getListControl(Composite parent)
	{
		if (fTableControl == null)
		{
			// assertCompositeNotNull(parent);

			if (fTableColumns == null)
			{
				fTable = createTableViewer(parent);
				Table tableControl = fTable.getTable();

				fTableControl = tableControl;
				tableControl.setLayout(new TableLayout());
			}
			else
			{
				TableLayoutComposite composite = new TableLayoutComposite(parent, SWT.NONE);
				fTableControl = composite;

				fTable = createTableViewer(composite);
				Table tableControl = fTable.getTable();

				tableControl.setHeaderVisible(fTableColumns.headers != null);
				tableControl.setLinesVisible(fTableColumns.drawLines);
				ColumnLayoutData[] columns = fTableColumns.columns;
				for (int i = 0; i < columns.length; i++)
				{
					composite.addColumnData(columns[i]);
					TableColumn column = new TableColumn(tableControl, SWT.NONE);
					// tableLayout.addColumnData(columns[i]);
					if (fTableColumns.headers != null)
					{
						column.setText(fTableColumns.headers[i]);
					}
				}
			}

			fTable.getTable().addKeyListener(new KeyAdapter()
			{
				public void keyPressed(KeyEvent e)
				{
					if (e.character == SWT.DEL && e.stateMask == 0)
					{
						removePressed();
					}
				}
			});

			// fTableControl.setLayout(tableLayout);

			fTable.setContentProvider(fListViewerAdapter);
			fTable.setLabelProvider(createLabelProvider());
			fTable.addSelectionChangedListener(fListViewerAdapter);
			fTable.addDoubleClickListener(fListViewerAdapter);

			fTable.setInput(this);

			// if (fViewerSorter != null) {
			// fTable.setSorter(fViewerSorter);
			// }

			// fTableControl.setEnabled(isEnabled());
			// if (fSelectionWhenEnabled != null) {
			// postSetSelection(fSelectionWhenEnabled);
			// }
		}
		return fTableControl;
	}

	protected TableViewer createTableViewer(Composite parent)
	{
		Table table = new Table(parent, getTableStyle());
		return new TableViewer(table);
	}

	/*
	 * Subclasses may override to specify a different style.
	 */
	protected int getTableStyle()
	{
		int style = SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL;
		if (fTableColumns != null)
		{
			style |= SWT.FULL_SELECTION;
		}
		return style;
	}

	/**
	 * Creates a new item to be added to the table. Called when the user selects the add button.
	 * <p>
	 * Subclasses must implement this method.
	 * </p>
	 * 
	 * @return a new item
	 */
	protected abstract Object createObject();

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public int getNumberOfControls()
	{
		return 3;
	}

	/**
	 * Returns this field editor's selection listener. The listener is created if nessessary.
	 * 
	 * @return the selection listener
	 */
	private SelectionListener getSelectionListener()
	{
		if (selectionListener == null)
		{
			createSelectionListener();
		}
		return selectionListener;
	}

	/**
	 * Returns this field editor's shell.
	 * <p>
	 * This method is internal to the framework; subclassers should not call this method.
	 * </p>
	 * 
	 * @return the shell
	 */
	protected Shell getShell()
	{
		if (addButton == null)
		{
			return null;
		}
		return addButton.getShell();
	}

	/**
	 * Notifies that the Remove button has been pressed.
	 */
	private void removePressed()
	{
		setPresentsDefaultValue(false);
		removeElements(getSelectedElements());
	}

	/**
	 * Returns the selected elements.
	 */
	public List getSelectedElements()
	{
		List result = new ArrayList();
		if (isOkToUse(fTableControl))
		{
			ISelection selection = fTable.getSelection();
			if (selection instanceof IStructuredSelection)
			{
				Iterator iter = ((IStructuredSelection) selection).iterator();
				while (iter.hasNext())
				{
					result.add(iter.next());
				}
			}
		}
		return result;
	}

	/**
	 * Tests is the control is not <code>null</code> and not disposed.
	 */
	protected final boolean isOkToUse(Control control)
	{
		return (control != null) && (Display.getCurrent() != null) && !control.isDisposed();
	}

	/**
	 * Removes an element from the list.
	 */
	public void removeElement(Object element) throws IllegalArgumentException
	{
		if (fElements.remove(element))
		{
			if (isOkToUse(fTableControl))
			{
				fTable.remove(element);
			}
			selectionChanged();
		}
		else
		{
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Removes elements from the list.
	 */
	public void removeElements(List elements)
	{
		if (elements.size() > 0)
		{
			fElements.removeAll(elements);
			if (isOkToUse(fTableControl))
			{
				fTable.remove(elements.toArray());
			}
			selectionChanged();
		}
	}

	/**
	 * Invoked when the selection in the list has changed.
	 * <p>
	 * The default implementation of this method utilizes the selection index and the size of the list to toggle the
	 * enablement of the up, down and remove buttons.
	 * </p>
	 * <p>
	 * Sublcasses may override.
	 * </p>
	 * 
	 * @since 3.5
	 */
	protected void selectionChanged()
	{
		removeButton.setEnabled(!fTable.getSelection().isEmpty());
		// upButton.setEnabled(size > 1 && index > 0);
		// downButton.setEnabled(size > 1 && index >= 0 && index < size - 1);
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public void setFocus()
	{
		if (fTable != null)
		{
			fTableControl.setFocus();
		}
	}

	// /**
	// * Moves the currently selected item up or down.
	// *
	// * @param up <code>true</code> if the item should move up,
	// * and <code>false</code> if it should move down
	// */
	// private void swap(boolean up) {
	// setPresentsDefaultValue(false);
	// int index = list.getSelectionIndex();
	// int target = up ? index - 1 : index + 1;
	//
	// if (index >= 0) {
	// String[] selection = list.getSelection();
	// Assert.isTrue(selection.length == 1);
	// list.remove(index);
	// list.add(selection[0], target);
	// list.setSelection(target);
	// }
	// selectionChanged();
	// }

	// /**
	// * Notifies that the Up button has been pressed.
	// */
	// private void upPressed() {
	// swap(true);
	// }

	/*
	 * @see FieldEditor.setEnabled(boolean,Composite).
	 */
	public void setEnabled(boolean enabled, Composite parent)
	{
		super.setEnabled(enabled, parent);
		getListControl(parent).setEnabled(enabled);
		addButton.setEnabled(enabled);
		editButton.setEnabled(enabled);
		removeButton.setEnabled(enabled);
		// upButton.setEnabled(enabled);
		// downButton.setEnabled(enabled);
	}

	/**
	 * Return the Add button.
	 * 
	 * @return the button
	 * @since 3.5
	 */
	protected Button getAddButton()
	{
		return addButton;
	}
	
	/**
	 * Return the Edit button.
	 * 
	 * @return the button
	 * @since 3.5
	 */
	protected Button getEditButton()
	{
		return editButton;
	}

	/**
	 * Return the Remove button.
	 * 
	 * @return the button
	 * @since 3.5
	 */
	protected Button getRemoveButton()
	{
		return removeButton;
	}

	//    
	// /**
	// * Return the Up button.
	// *
	// * @return the button
	// * @since 3.5
	// */
	// protected Button getUpButton() {
	// return upButton;
	// }
	//    
	// /**
	// * Return the Down button.
	// *
	// * @return the button
	// * @since 3.5
	// */
	// protected Button getDownButton() {
	// return downButton;
	// }

	/**
	 * Return the List.
	 * 
	 * @return the list
	 * @since 3.5
	 */
	protected TableViewer getTableViewer()
	{
		return fTable;
	}

	private class ListViewerAdapter implements IStructuredContentProvider, ISelectionChangedListener,
			IDoubleClickListener
	{

		// ------- ITableContentProvider Interface ------------

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
			// will never happen
		}

		public boolean isDeleted(Object element)
		{
			return false;
		}

		public void dispose()
		{
		}

		public Object[] getElements(Object obj)
		{
			return fElements.toArray();
		}

		// ------- ISelectionChangedListener Interface ------------

		public void selectionChanged(SelectionChangedEvent event)
		{
			doListSelected(event);
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
		 */
		public void doubleClick(DoubleClickEvent event)
		{
			doDoubleClick(event);
		}

	}

	public void doDoubleClick(DoubleClickEvent event)
	{
		// TODO Auto-generated method stub

	}

	public void doListSelected(SelectionChangedEvent event)
	{
		// TODO Auto-generated method stub

	}
}
