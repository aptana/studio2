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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.internal.IWorkbenchHelpContextIds;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.dialogs.IAddItemListener;
import com.aptana.ide.core.ui.dialogs.TableEditor;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.errors.ErrorDescriptor;
import com.aptana.ide.editors.validator.ValidatorManager;
import com.aptana.ide.editors.validator.ValidatorRef;

/**
 * The file editors page presents the collection of file names and extensions for which the user has registered editors.
 * It also lets the user add new internal or external (program) editors for a given file name and extension. The user
 * can add an editor for either a specific file name and extension (e.g. report.doc), or for all file names of a given
 * extension (e.g. *.doc) The set of registered editors is tracked by the EditorRegistery available from the workbench
 * plugin.
 */
public abstract class ProblemsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage,
		IAddItemListener
{
	private static Image fWarningImage = UnifiedEditorsPlugin.getImageDescriptor("icons/warning.png").createImage(); //$NON-NLS-1$

	/**
	 * workbench
	 */
	protected IWorkbench workbench;

	private TableEditor _tableEditor;
	private CheckboxTableViewer _validatorViewer;

	/**
	 * Creates the page's UI content.
	 * 
	 * @param parent
	 * @return Control
	 */
	protected Control createContents(Composite parent)
	{
		// define common container to Problems Filter and list of available validators
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, true);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		createValidatorPreferenceControls(composite);

		// create controls for filtering errors
		// define container & its gridding

		Group errorFilter = new Group(composite, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		errorFilter.setLayout(gridLayout);
		errorFilter.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		errorFilter.setText(CodeAssistMessages.ProblemsPreferencePage_ProblemViewFilters);

		_tableEditor = new TableEditor(errorFilter, SWT.NULL, true);
		_tableEditor.setDescription(CodeAssistMessages.ProblemsPreferencePage_ProblemsDescription);

		GridData data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 150;
		_tableEditor.setLayoutData(data);

		IPreferenceStore store = doGetPreferenceStore();
		String editors = store.getString(doGetPreferenceString());
		ErrorDescriptor[] descriptors = ErrorDescriptor.deserializeErrorDescriptors(editors);
		List<Object> items = new ArrayList<Object>(Arrays.asList(descriptors));

		_tableEditor.setLabelProvider(new TableLabelProvider());
		_tableEditor.addAddItemListener(this);

		new TableColumn(_tableEditor.getTable(), SWT.LEFT);
		_tableEditor.setItems(items);

		workbench.getHelpSystem().setHelp(parent, IWorkbenchHelpContextIds.FILE_EDITORS_PREFERENCE_PAGE);
		applyDialogFont(_tableEditor);

		return composite;
	}

	private void createValidatorPreferenceControls(Composite parent)
	{
		// create controls for validators

		Composite displayArea = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = false;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 0;
		displayArea.setLayout(layout);
		GridData data = new GridData(GridData.FILL, GridData.FILL, true, true);
		displayArea.setLayoutData(data);

		Group validators = new Group(displayArea, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		validators.setLayout(gridLayout);
		validators.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		validators.setText(CodeAssistMessages.ProblemsPreferencePage_Validators);
		Table table = new Table(validators, SWT.CHECK | SWT.BORDER);
		table.setFont(parent.getFont());
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		_validatorViewer = new CheckboxTableViewer(table);
		_validatorViewer.setContentProvider(new ArrayContentProvider());
		_validatorViewer.setLabelProvider(new LabelProvider());
		data = new GridData(GridData.FILL, GridData.FILL, true, true);
		data.heightHint = 100;
		data.widthHint = 140;
		table.setLayoutData(data);

		addvalidators();

		String stored_validators = this.getPreferenceStore().getString(IPreferenceConstants.VALIDATORS_LIST);
		restoreCheckedValidators(stored_validators);

	}

	private void restoreCheckedValidators(String stored_validators)
	{
		// CHECKSTYLE:OFF
		if (stored_validators.equals(IPreferenceConstants.VALIDATORS_NONE))
		{
			// Do nothing : user de-selected all the validators
		}
		// CHECKSTYLE:ON
		else if (stored_validators.length() == 0)
		{
			// Default-default value : select everything
			List<String> validatorNames = (List<String>) _validatorViewer.getInput();
			_validatorViewer.setCheckedElements(validatorNames.toArray(new String[validatorNames.size()]));
		}
		else
		{
			String[] validators = stored_validators.split(","); //$NON-NLS-1$
			List<String> validatorNames = (List<String>) _validatorViewer.getInput();
			String name;
			int size = validatorNames.size();
			int j;
			for (int i = 0; i < size; ++i)
			{
				name = validatorNames.get(i);

				for (j = 0; j < validators.length; ++j)
				{
					if (name.equals(validators[j]))
					{
						// the name is in the list of checked validators
						_validatorViewer.setChecked(name, true);
						break;
					}
				}
				if (j == validators.length)
				{
					// the name is not in the list of checked validators
					_validatorViewer.setChecked(name, false);
				}
			}
		}
	}

	private void addvalidators()
	{
		String mimeType = getMimeType();
		ValidatorManager validatiorManager = ValidatorManager.getInstance();
		ValidatorRef[] validators = validatiorManager.getValidators(mimeType);
		if (validators != null && validators.length > 0)
		{
			List<String> validatorNames = new ArrayList<String>();
			for (int i = 0; i < validators.length; i++)
			{
				validatorNames.add(validators[i].getName());
			}
			_validatorViewer.setInput(validatorNames);
		}
	}

	/**
	 * getMimeType
	 * 
	 * @return mime type
	 */
	protected abstract String getMimeType();

	/**
	 * The preference page is going to be disposed. So deallocate all allocated SWT resources that aren't disposed
	 * automatically by disposing the page (i.e fonts, cursors, etc). Subclasses should re-implement this method to
	 * release their own allocated SWT resources.
	 */
	public void dispose()
	{
		_tableEditor.removeAddItemListener(this);
		super.dispose();
	}

	/**
	 * Hook method to get a page specific preference store. Reimplement this method if a page don't want to use its
	 * parent's preference store.
	 * 
	 * @return IPreferenceStore
	 */
	protected abstract IPreferenceStore doGetPreferenceStore();

	/**
	 * Hook method to get a page specific preference store. Reimplement this method if a page don't want to use its
	 * parent's preference store.
	 * 
	 * @return Plugin
	 */
	protected abstract Plugin doGetPlugin();

	/**
	 * Hook method to get a page specific preference store. Reimplement this method if a page don't want to use its
	 * parent's preference store.
	 * 
	 * @return String
	 */
	protected String doGetPreferenceString()
	{
		return IPreferenceConstants.IGNORE_PROBLEMS;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench aWorkbench)
	{
		this.workbench = aWorkbench;
		// noDefaultAndApplyButton();
	}

	/**
	 * Performs special processing when this page's Defaults button has been pressed.
	 * <p>
	 * This is a framework hook method for subclasses to do special things when the Defaults button has been pressed.
	 * Subclasses may override, but should call <code>super.performDefaults</code>.
	 * </p>
	 */
	protected void performDefaults()
	{
		super.performDefaults();

		IPreferenceStore store = doGetPreferenceStore();
		String editors = store.getDefaultString(doGetPreferenceString());
		ErrorDescriptor[] descriptors = ErrorDescriptor.deserializeErrorDescriptors(editors);
		List<Object> items = new ArrayList<Object>(Arrays.asList(descriptors));
		_tableEditor.setItems(items);

		String stored_validators = this.getPreferenceStore().getDefaultString(IPreferenceConstants.VALIDATORS_LIST);
		restoreCheckedValidators(stored_validators);
	}

	/**
	 * Prompt for resource type.
	 * 
	 * @return Object
	 */
	public Object addItem()
	{
		ErrorDescriptorInfoDialog dialog = new ErrorDescriptorInfoDialog(getControl().getShell());
		if (dialog.open() == Window.OK)
		{
			String message = dialog.getMessage();
			ErrorDescriptor ed = new ErrorDescriptor();
			ed.setMessage(message);
			return ed;
		}

		return null;
	}

	/**
	 * This is a hook for subclasses to do special things when the ok button is pressed. For example, re-implement this
	 * method if you want to save the page's data into the preference bundle.
	 * 
	 * @return boolean
	 */
	public boolean performOk()
	{
		IPreferenceStore store = doGetPreferenceStore();

		List<Object> items = _tableEditor.getItems();
		store.setValue(doGetPreferenceString(), ErrorDescriptor.serializeErrorDescriptors((ErrorDescriptor[]) items
				.toArray(new ErrorDescriptor[0])));

		List<String> validatorItems = new ArrayList<String>();
		Object[] checkedItems = _validatorViewer.getCheckedElements();

		if (checkedItems.length > 0)
		{
			for (int i = 0; i < checkedItems.length; i++)
			{
				validatorItems.add(checkedItems[i].toString());
			}
			store.setValue(IPreferenceConstants.VALIDATORS_LIST, StringUtils.join(
					",", validatorItems.toArray(new String[0]))); //$NON-NLS-1$
		}
		else
		{
			// Store special value to indicate the fact that user unselected all the validators.
			// This help us distinguish no-selection state from initial state wherein nothing is stored as preference
			// initially.
			store.setValue(IPreferenceConstants.VALIDATORS_LIST, IPreferenceConstants.VALIDATORS_NONE);
		}
		doGetPlugin().savePluginPreferences();
		return true;
	}

	/**
	 * editItem
	 * 
	 * @param item
	 * @return Object
	 */
	public Object editItem(Object item)
	{
		if (item instanceof ErrorDescriptor)
		{
			ErrorDescriptorInfoDialog dialog = new ErrorDescriptorInfoDialog(getControl().getShell());
			ErrorDescriptor ed = (ErrorDescriptor) item;
			dialog.setItem(ed);
			if (dialog.open() == Window.OK)
			{
				ed.setMessage(dialog.getMessage());
				return ed;
			}
			return null;
		}
		else
		{
			return null;
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
				case 0:
					image = fWarningImage;
					break;
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
			ErrorDescriptor ed = (ErrorDescriptor) element;
			switch (columnIndex)
			{
				case 0:
					name = ed.getMessage();
					break;
				case 1:
					name = ed.getFolderPath();
					break;
				case 2:
					name = ed.getFileName();
					break;
				default:
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
}
