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
package com.aptana.ide.editor.css.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.css.BrowserExtensionLoader;
import com.aptana.ide.editor.css.CSSPlugin;
import com.aptana.ide.editors.UnifiedEditorsPlugin;

/**
 * Preference page for enabling browser preview tabs.
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class PreviewPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

	private CheckboxTableViewer previewViewer;
	private Label description;
	private Composite displayArea;
	private Group browsers;
	private Button useCustomTemplate;
	private Text defaultTemplate;
	private Button useUrl;
	private Label globalUrlLabel;
	private Text globalUrlText;
	private Button browseButton;
	private Button useTempFiles;

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{
		displayArea = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = false;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 0;
		displayArea.setLayout(layout);
		displayArea.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		useTempFiles = new Button(displayArea, SWT.CHECK);
		useTempFiles.setText(Messages.PreviewPreferencePage_UseTempFilesText);
		useTempFiles.setSelection(getPreferenceStore().getBoolean(IPreferenceConstants.USE_TEMP_FILES_FOR_PREVIEW));

		description = new Label(displayArea, SWT.WRAP);
		description.setText(Messages.PreviewPreferencePage_DescriptionText);
		browsers = new Group(displayArea, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		browsers.setLayout(gridLayout);
		browsers.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		browsers.setText(Messages.PreviewPreferencePage_BrowsersText);
		Table table = new Table(browsers, SWT.CHECK | SWT.BORDER | SWT.SINGLE);
		table.setFont(parent.getFont());
		previewViewer = new CheckboxTableViewer(table);
		previewViewer.setContentProvider(new ArrayContentProvider());
		previewViewer.setLabelProvider(new LabelProvider());
		previewViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				if (Platform.getOS().equals(Platform.OS_MACOSX))
				{
					if (previewViewer.getChecked("Firefox") && !previewViewer.getChecked("Safari")) //$NON-NLS-1$ //$NON-NLS-2$
					{
						MessageDialog
								.openInformation(
										getShell(),
										Messages.PreviewPreferencePage_FirefoxIssueTitle,
										Messages.PreviewPreferencePage_FireFoxIssueMessage);
						previewViewer.setChecked("Firefox", false); //$NON-NLS-1$
					}
				}
			}

		});
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		addPreviewBrowsers();
		addCheckedBrowsers();

		Label templateDescription = new Label(displayArea, SWT.LEFT | SWT.WRAP);
		templateDescription.setText(Messages.PreviewPreferencePage_TemplateDescText);
		GridData tdData = new GridData(SWT.FILL, SWT.FILL, true, false);
		tdData.verticalIndent = 5;
		templateDescription.setLayoutData(tdData);

		boolean useTemplate = getPreferenceStore().getBoolean(
				IPreferenceConstants.CSSEDITOR_BROWSER_USE_TEMPLATE_PREFERENCE);
		useCustomTemplate = new Button(displayArea, SWT.RADIO);
		useCustomTemplate.setText(Messages.PreviewPreferencePage_UseCustomTemplateText);
		useCustomTemplate.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				boolean template = useCustomTemplate.getSelection();
				defaultTemplate.setEnabled(template);
				globalUrlText.setEnabled(!template);
				browseButton.setEnabled(!template);
			}

		});
		useCustomTemplate.setSelection(useTemplate);
		Group template = new Group(displayArea, SWT.NONE);
		template.setText(Messages.PreviewPreferencePage_TemplateGroupTitle);
		GridLayout tLayout = new GridLayout(1, true);
		tLayout.marginHeight = 0;
		tLayout.marginWidth = 0;
		template.setLayout(tLayout);
		GridData tData = new GridData(GridData.FILL, GridData.FILL, true, false);
		tData.horizontalIndent = 13;
		template.setLayoutData(tData);
		defaultTemplate = new Text(template, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData dtData = new GridData(SWT.FILL, SWT.FILL, true, true);
		dtData.heightHint = 225;
		dtData.widthHint = 500;
		defaultTemplate.setText(getPreferenceStore().getString(
				IPreferenceConstants.CSSEDITOR_BROWSER_TEMPLATE_PREFERENCE));
		defaultTemplate.setLayoutData(dtData);
		defaultTemplate.setEnabled(useTemplate);

		useUrl = new Button(displayArea, SWT.RADIO);
		useUrl.setText(Messages.PreviewPreferencePage_UseUrlText);
		useUrl.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				boolean url = useUrl.getSelection();
				defaultTemplate.setEnabled(!url);
				globalUrlText.setEnabled(url);
				browseButton.setEnabled(url);
			}

		});
		useUrl.setSelection(!useTemplate);
		Composite globalUrl = new Composite(displayArea, SWT.NONE);
		GridLayout guLayout = new GridLayout(3, false);
		guLayout.marginHeight = 0;
		guLayout.marginWidth = 0;
		guLayout.marginBottom = 10;
		globalUrl.setLayout(guLayout);
		GridData gData = new GridData(GridData.FILL, GridData.FILL, true, false);
		gData.horizontalIndent = 15;
		globalUrl.setLayoutData(gData);
		globalUrlLabel = new Label(globalUrl, SWT.LEFT);
		globalUrlLabel.setText(Messages.PreviewPreferencePage_UrlLabel);
		globalUrlText = new Text(globalUrl, SWT.SINGLE | SWT.BORDER);
		globalUrlText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		globalUrlText.setText(getPreferenceStore().getString(IPreferenceConstants.CSSEDITOR_BROWSER_URL_PREFERENCE));
		globalUrlText.setEnabled(!useTemplate);
		browseButton = new Button(globalUrl, SWT.PUSH);
		browseButton.setText(Messages.PreviewPreferencePage_BrowseText);
		browseButton.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				FileDialog dialog = new FileDialog(browseButton.getShell(), SWT.OPEN);
				String file = dialog.open();
				if (file != null)
				{
					globalUrlText.setText(file);
				}
			}

		});
		browseButton.setEnabled(!useTemplate);
		return displayArea;
	}

	/**
	 * Adds all the preview browser options to the preference page.
	 */
	private void addPreviewBrowsers()
	{
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg.getExtensionPoint(UnifiedEditorsPlugin.BROWSER_EXTENSION_POINT);
		IExtension[] extensions = ep.getExtensions();
		List<String> browserPreviews = new ArrayList<String>();
		IConfigurationElement[] ce;
		String browserClass;
		String browserName;
		for (int i = 0; i < extensions.length; i++)
		{
			ce = extensions[i].getConfigurationElements();
			for (int j = 0; j < ce.length; j++)
			{
				browserClass = ce[j].getAttribute(UnifiedEditorsPlugin.CLASS_ATTR);
				browserName = BrowserExtensionLoader.getBrowserLabel(ce[j]);
				if (browserClass != null && browserName != null)
				{
					browserPreviews.add(browserName);
				}
			}
		}
		Collections.sort(browserPreviews);
		previewViewer.setInput(browserPreviews.toArray());
	}

	/**
	 * Checks the browser entries that exist in the preferences.
	 */
	private void addCheckedBrowsers()
	{
		String all_browsers = this.getPreferenceStore().getString(
				IPreferenceConstants.CSSEDITOR_BROWSER_PREVIEW_PREFERENCE);
		String[] browsers = all_browsers.split(","); //$NON-NLS-1$
		for (int i = 0; i < browsers.length; i++)
		{
			previewViewer.setChecked(browsers[i], true);
		}
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk()
	{
		getPreferenceStore().setValue(IPreferenceConstants.CSSEDITOR_BROWSER_URL_PREFERENCE, globalUrlText.getText());
		getPreferenceStore().setValue(IPreferenceConstants.CSSEDITOR_BROWSER_TEMPLATE_PREFERENCE,
				defaultTemplate.getText());
		getPreferenceStore().setValue(IPreferenceConstants.CSSEDITOR_BROWSER_USE_TEMPLATE_PREFERENCE,
				useCustomTemplate.getSelection());

		ArrayList<String> al = new ArrayList<String>();
		Object[] elements = previewViewer.getCheckedElements();
		for (int i = 0; i < elements.length; i++)
		{
			al.add(elements[i].toString());
		}
		getPreferenceStore().setValue(IPreferenceConstants.CSSEDITOR_BROWSER_PREVIEW_PREFERENCE,
				StringUtils.join(",", (String[]) al.toArray(new String[0]))); //$NON-NLS-1$

		getPreferenceStore().setValue(IPreferenceConstants.USE_TEMP_FILES_FOR_PREVIEW, useTempFiles.getSelection());
		return true;
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{
		super.performDefaults();
		String prefs = getPreferenceStore().getDefaultString(IPreferenceConstants.CSSEDITOR_BROWSER_PREVIEW_PREFERENCE);
		previewViewer.setCheckedElements(prefs.split(",")); //$NON-NLS-1$
		String url = getPreferenceStore().getDefaultString(IPreferenceConstants.CSSEDITOR_BROWSER_URL_PREFERENCE);
		globalUrlText.setText(url);
		String template = getPreferenceStore().getDefaultString(
				IPreferenceConstants.CSSEDITOR_BROWSER_TEMPLATE_PREFERENCE);
		defaultTemplate.setText(template);
		boolean useTemplate = getPreferenceStore().getDefaultBoolean(
				IPreferenceConstants.CSSEDITOR_BROWSER_USE_TEMPLATE_PREFERENCE);
		useCustomTemplate.setSelection(useTemplate);
		useUrl.setSelection(!useTemplate);
		defaultTemplate.setEnabled(useTemplate);
		globalUrlText.setEnabled(!useTemplate);
		browseButton.setEnabled(!useTemplate);
		useTempFiles.setSelection(getPreferenceStore().getDefaultBoolean(
				IPreferenceConstants.USE_TEMP_FILES_FOR_PREVIEW));
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
		setPreferenceStore(CSSPlugin.getDefault().getPreferenceStore());
	}
}
