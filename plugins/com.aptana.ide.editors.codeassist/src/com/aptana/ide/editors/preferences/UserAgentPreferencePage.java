/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.editors.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.contentassist.UnifiedContentAssistProcessor;

/**
 * Allows the user to edit the set of user agents
 * 
 * @since 3.1
 */
public final class UserAgentPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
	/**
	 * CategoryContentProvider
	 * 
	 * @author Ingo Muschenetz
	 */
	private class CategoryContentProvider implements IStructuredContentProvider
	{
		/**
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			return (Object[]) inputElement;
		}

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
	}

	/**
	 * CategoryLabelProvider
	 * 
	 * @author Ingo Muschenetz
	 */
	private class CategoryLabelProvider extends LabelProvider implements ITableLabelProvider
	{

		private LocalResourceManager manager = new LocalResourceManager(JFaceResources.getResources());

		/**
		 * @param decorate
		 */
		public CategoryLabelProvider(boolean decorate)
		{
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex)
		{

			HashMap images = UnifiedContentAssistProcessor.getUserAgentImages();
			if (images.containsKey(element))
			{
				return (Image) images.get(element);
			}
			else
			{
				return null;
			}
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex)
		{
			return (String) element;
		}

		/**
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		public void dispose()
		{
			super.dispose();
			manager.dispose();
		}
	}

	/**
	 * workbench
	 */
	protected IWorkbench workbench;

	private CheckboxTableViewer categoryViewer;

	private TableViewer dependantViewer;

	private Text descriptionText;

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		Label label = new Label(composite, SWT.WRAP);
		label.setText(CodeAssistMessages.UserAgentPreferencePage_SelectBrowsers);
		label.setFont(parent.getFont());
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 400;
		data.horizontalSpan = 2;
		label.setLayoutData(data);
		label = new Label(composite, SWT.NONE); // spacer
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = 2;
		label.setLayoutData(data);
		createCategoryArea(composite);
		createButtons(composite);

		return composite;
	}

	private void createButtons(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(4, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		composite.setLayoutData(data);

		Button enableAll = new Button(composite, SWT.PUSH);
		enableAll.setFont(parent.getFont());
		enableAll.addSelectionListener(new SelectionAdapter()
		{

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e)
			{
				categoryViewer.setCheckedElements(UnifiedContentAssistProcessor.getDefaultUserAgents());
			}
		});
		enableAll.setText(CodeAssistMessages.UserAgentPreferencePage_SelectAll);
		setButtonLayoutData(enableAll);

		Button disableAll = new Button(composite, SWT.PUSH);
		disableAll.setFont(parent.getFont());
		disableAll.addSelectionListener(new SelectionAdapter()
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e)
			{
				categoryViewer.setCheckedElements(new String[0]);
			}
		});
		disableAll.setText(CodeAssistMessages.UserAgentPreferencePage_SelectNone);
		setButtonLayoutData(disableAll);
	}

	/**
	 * @param parent
	 */
	private void createCategoryArea(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 200;
		composite.setLayoutData(data);
		Label label = new Label(composite, SWT.NONE);
		label.setFont(parent.getFont());
		label.setText(StringUtils.makeFormLabel("User Agents")); //$NON-NLS-1$
		Table table = new Table(composite, SWT.CHECK | SWT.BORDER | SWT.SINGLE);
		table.setFont(parent.getFont());
		table.addSelectionListener(new SelectionAdapter()
		{

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e)
			{
			}
		});
		categoryViewer = new CheckboxTableViewer(table);
		categoryViewer.getControl().setFont(parent.getFont());
		categoryViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		categoryViewer.setContentProvider(new CategoryContentProvider());
		CategoryLabelProvider categoryLabelProvider = new CategoryLabelProvider(true);
		categoryViewer.setLabelProvider(categoryLabelProvider);
		categoryViewer.setSorter(new ViewerSorter());

		categoryViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
			 */
			public void selectionChanged(SelectionChangedEvent event)
			{
			}
		});
		categoryViewer.setInput(UnifiedContentAssistProcessor.getDefaultUserAgents());
		categoryViewer.setCheckedElements(getEnabledCategories());
	}

	private String[] getEnabledCategories()
	{
		return UnifiedContentAssistProcessor.getUserAgents();
	}

	/**
	 * Clear the details area.
	 */
	protected void clearDetails()
	{
		dependantViewer.setInput(Collections.EMPTY_SET);
		descriptionText.setText(StringUtils.EMPTY); 
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
		this.workbench = workbench;
		setPreferenceStore(UnifiedEditorsPlugin.getDefault().getPreferenceStore());
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk()
	{

		ArrayList al = new ArrayList();
		Object[] elements = categoryViewer.getCheckedElements();
		for (int i = 0; i < elements.length; i++)
		{
			al.add(elements[i].toString());
		}
		getPreferenceStore().setValue(com.aptana.ide.editors.preferences.IPreferenceConstants.USER_AGENT_PREFERENCE,
				StringUtils.join(",", (String[]) al.toArray(new String[0]))); //$NON-NLS-1$
		return true;
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{
		super.performDefaults();

		String prefs = getPreferenceStore().getDefaultString(
				com.aptana.ide.editors.preferences.IPreferenceConstants.USER_AGENT_PREFERENCE);
		categoryViewer.setCheckedElements(prefs.split(",")); //$NON-NLS-1$
	}
}
