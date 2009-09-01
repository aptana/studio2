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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * The file editors page presents the collection of file names and extensions for which the user has registered editors.
 * It also lets the user add new internal or external (program) editors for a given file name and extension. The user
 * can add an editor for either a specific file name and extension (e.g. report.doc), or for all file names of a given
 * extension (e.g. *.doc) The set of registered editors is tracked by the EditorRegistery available from the workbench
 * plugin.
 */
public abstract class CodeAssistPreferencePage extends EditorPreferencePage // implements IWorkbenchPreferencePage, IAddItemListener
{
	/**
	 * Auto activation group.
	 */
	private Composite autoactivationGroup;
	
	/**
	 * GeneralPreferencePage
	 */
	public CodeAssistPreferencePage()
	{
		super(GRID);
		setDescription(CodeAssistMessages.CodeAssistPreferencePage_PreferencesForCodeAssist);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate various
	 * types of preferences. Each field editor knows how to save and restore itself.
	 */
	public void createFieldEditors()
	{
		Composite appearanceComposite = getFieldEditorParent();
		autoactivationGroup = com.aptana.ide.core.ui.preferences.GeneralPreferencePage
				.createGroup(appearanceComposite, CodeAssistMessages.CodeAssistPreferencePage_AutoActivation);

		addField(new BooleanFieldEditor(IPreferenceConstants.CODE_ASSIST_AUTO_ACTIVATION,
				CodeAssistMessages.CodeAssistPreferencePage_EnableAutoActivation, autoactivationGroup));

//		addField(new StringFieldEditor(IPreferenceConstants.CODE_ASSIST_ACTIVATION_CHARACTERS,
//				"Auto-activation triggers", group));
		
	}	
	
	/**
	 * Gets auto activation group.
	 * @return auto activation group.
	 */
	protected Composite getAutoactivationGroup()
	{
		return autoactivationGroup;
	}
	
//	protected CodeAssistPreferencePage() {
//		super(GRID);
//	}
//
//	/**
//	 * addResourceTypeButton
//	 */
//	protected Button addResourceTypeButton;
//
//	/**
//	 * removeResourceTypeButton
//	 */
//	protected Button removeResourceTypeButton;
//
//	/**
//	 * workbench
//	 */
//	protected IWorkbench workbench;
//
//	private TableEditor _tableEditor;
//
//	/**
//	 * Creates the page's UI content.
//	 * 
//	 * @param parent
//	 * @return Control
//	 */
//	protected Control createContents(Composite parent)
//	{
//		Composite appearanceComposite = getFieldEditorParent();
//
//		// define container & its gridding
//		_tableEditor = new TableEditor(appearanceComposite, SWT.NULL, true);
//		_tableEditor
//				.setDescription(Messages.CodeAssistPreferencePage_XPathExpressionsForCodeAssist);
//
//		IPreferenceStore store = doGetPreferenceStore();
//		String editors = store.getString(doGetPreferenceString());
//		CodeAssistExpression[] descriptors = CodeAssistExpression.deserializeErrorDescriptors(editors);
//		ArrayList _items = new ArrayList(Arrays.asList(descriptors));
//
//		_tableEditor.setLabelProvider(new TableLabelProvider());
//		_tableEditor.addAddItemListener(this);
//
//		setButtonLayoutData(_tableEditor.getAddButton());
//		setButtonLayoutData(_tableEditor.getEditButton());
//		setButtonLayoutData(_tableEditor.getRemoveButton());
//
//		TableColumn t1 = new TableColumn(_tableEditor.getTable(), SWT.LEFT);
//		t1.setText(Messages.CodeAssistPreferencePage_Expression);
//		
//		TableColumn t2 = new TableColumn(_tableEditor.getTable(), SWT.LEFT);
//		t2.setText(Messages.CodeAssistPreferencePage_XPath);
//
//		_tableEditor.setItems(_items);
//		_tableEditor.getTable().setHeaderVisible(true);
//
//		workbench.getHelpSystem().setHelp(appearanceComposite, IWorkbenchHelpContextIds.FILE_EDITORS_PREFERENCE_PAGE);
//		applyDialogFont(_tableEditor);
//
//		return _tableEditor;
//	}
//
//	/**
//	 * The preference page is going to be disposed. So deallocate all allocated SWT resources that aren't disposed
//	 * automatically by disposing the page (i.e fonts, cursors, etc). Subclasses should re-implement this method to
//	 * release their own allocated SWT resources.
//	 */
//	public void dispose()
//	{
//		_tableEditor.removeAddItemListener(this);
//		super.dispose();
//	}
//
//	/**
//	 * Hook method to get a page specific preference store. Reimplement this method if a page don't want to use its
//	 * parent's preference store.
//	 * 
//	 * @return IPreferenceStore
//	 */
//	protected abstract IPreferenceStore doGetPreferenceStore();
//
//	/**
//	 * Hook method to get a page specific preference store. Reimplement this method if a page don't want to use its
//	 * parent's preference store.
//	 * 
//	 * @return Plugin
//	 */
//	protected abstract Plugin doGetPlugin();
//
//	/**
//	 * Hook method to get a page specific preference store. Reimplement this method if a page don't want to use its
//	 * parent's preference store.
//	 * 
//	 * @return String
//	 */
//	protected String doGetPreferenceString()
//	{
//		return IPreferenceConstants.CODE_ASSIST_EXPRESSIONS;
//	}
//
//	/**
//	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
//	 */
//	public void init(IWorkbench aWorkbench)
//	{
//		this.workbench = aWorkbench;
//	}
//
//	/**
//	 * Prompt for resource type.
//	 * 
//	 * @return Object
//	 */
//	public Object addItem()
//	{
//		CodeAssistExpressionInfoDialog dialog = new CodeAssistExpressionInfoDialog(getControl().getShell());
//		if (dialog.open() == Window.OK)
//		{
//			CodeAssistExpression ed = new CodeAssistExpression();
//			ed.setExpression(dialog.getExpression());
//			ed.setXPath(dialog.getXPath());
//			return ed;
//		}
//
//		return null;
//	}
//
//	/**
//	 * This is a hook for subclasses to do special things when the ok button is pressed. For example, re-implement this
//	 * method if you want to save the page's data into the preference bundle.
//	 * 
//	 * @return boolean
//	 */
//	public boolean performOk()
//	{
//		IPreferenceStore store = doGetPreferenceStore();
//		ArrayList items = _tableEditor.getItems();
//		String serializedString = CodeAssistExpression.serializeErrorDescriptors((CodeAssistExpression[]) items
//				.toArray(new CodeAssistExpression[0]));
//		store.setValue(doGetPreferenceString(), serializedString); //$NON-NLS-1$
//		doGetPlugin().savePluginPreferences();
//		return true;
//	}
//
//    /**
//     * editItem
//     * @param item 
//     * @return Object
//     */
//	public Object editItem(Object item) {
//		if(item instanceof CodeAssistExpression)
//		{
//			CodeAssistExpressionInfoDialog dialog = new CodeAssistExpressionInfoDialog(getControl().getShell());
//			CodeAssistExpression ed = (CodeAssistExpression)item;
//			dialog.setItem(ed);
//			if (dialog.open() == Window.OK)
//			{
//				ed.setExpression(dialog.getExpression());
//				ed.setXPath(dialog.getXPath());
//				return ed;
//			}
//			return null;
//		}
//		else
//		{
//			return null;
//		}
//	}
//	
//	/**
//	 * TableLabelProvider
//	 * 
//	 * @author Ingo Muschenetz
//	 */
//	public class TableLabelProvider implements ITableLabelProvider
//	{
//		/**
//		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
//		 */
//		public Image getColumnImage(Object element, int columnIndex)
//		{
//			Image image = null;
//			switch (columnIndex)
//			{
//				default:
//					break;
//			}
//			return image;
//		}
//
//		/**
//		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
//		 */
//		public String getColumnText(Object element, int columnIndex)
//		{
//			String name = StringUtils.EMPTY;
//			CodeAssistExpression ed = (CodeAssistExpression) element;
//			switch (columnIndex)
//			{
//				case 0:
//					name = ed.getExpression();
//					break;
//				case 1:
//					name = ed.getXPath();
//					break;
//				default:
//					break;
//			}
//			return name;
//
//		}
//
//		/**
//		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
//		 */
//		public void addListener(ILabelProviderListener listener)
//		{
//		}
//
//		/**
//		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
//		 */
//		public void dispose()
//		{
//		}
//
//		/**
//		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
//		 */
//		public boolean isLabelProperty(Object element, String property)
//		{
//			return false;
//		}
//
//		/**
//		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
//		 */
//		public void removeListener(ILabelProviderListener listener)
//		{
//		}
//	}
//
//	/**
//	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
//	 */
//	protected void performDefaults() {
//		
//		super.performDefaults();
//		
//		IPreferenceStore store = doGetPreferenceStore();
//		String editors = store.getDefaultString(doGetPreferenceString());
//		CodeAssistExpression[] descriptors = CodeAssistExpression.deserializeErrorDescriptors(editors);
//		ArrayList _items = new ArrayList(Arrays.asList(descriptors));
//		_tableEditor.setItems(_items);
//	}
}
