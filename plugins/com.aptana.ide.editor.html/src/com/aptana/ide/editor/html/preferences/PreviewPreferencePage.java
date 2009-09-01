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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.html.BrowserExtensionLoader;
import com.aptana.ide.editor.html.HTMLPlugin;
import com.aptana.ide.editor.html.preview.HTMLPreviewPropertyPage;
import com.aptana.ide.editor.html.preview.PreviewTypeSelectionBlock;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.server.core.IServer;

/**
 * Preference page for enabling browser preview tabs
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class PreviewPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

	private CheckboxTableViewer previewViewer;
	private Label description;
	private Composite displayArea;
	private Group browsers;
	private PreviewTypeSelectionBlock block;
	private Button autoSave;
	private Button cacheBust;
	private Button useTempFiles;

	/**
	 * Creates a new preview preference page
	 */
	public PreviewPreferencePage()
	{
		setDescription(Messages.PreviewPreferencePage_LBL_DefaultSettings);
	}

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
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 0;
		displayArea.setLayout(layout);
		displayArea.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		autoSave = new Button(displayArea, SWT.CHECK);
		autoSave.setText(Messages.PreviewPreferencePage_LBL_AutoSave);
		autoSave.setSelection(getPreferenceStore().getBoolean(IPreferenceConstants.AUTO_SAVE_BEFORE_PREVIEWING));

		cacheBust = new Button(displayArea, SWT.CHECK);
		cacheBust.setText(Messages.PreviewPreferencePage_LBL_PreventCache);
		cacheBust.setSelection(UnifiedEditorsPlugin.getDefault().getPreferenceStore().getBoolean(
				com.aptana.ide.editors.preferences.IPreferenceConstants.CACHE_BUST_BROWSERS));

		useTempFiles = new Button(displayArea, SWT.CHECK);
		useTempFiles.setText(Messages.PreviewPreferencePage_LBL_GenerateTemp);
		useTempFiles.setSelection(getPreferenceStore().getBoolean(IPreferenceConstants.USE_TEMP_FILES_FOR_PREVIEW));

		String type = getPreferenceStore().getString(HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_TYPE);
		String value = getPreferenceStore().getString(HTMLPreviewPropertyPage.HTML_PREVIEW_VALUE);
		block = new PreviewTypeSelectionBlock();
		block.useSampleURL();
		block.createStartActionSection(displayArea, type, value);
		block.setCurrentURLLabel(Messages.PreviewPreferencePage_LBL_SampleURL);
		block.setEnabled(true);
		block.updateControls();
		block.updateCurrentURL();

		description = new Label(displayArea, SWT.WRAP);
		description.setText(Messages.PreviewPreferencePage_PreviewBrowserDescription);
		browsers = new Group(displayArea, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		browsers.setLayout(gridLayout);
		browsers.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		browsers.setText(Messages.PreviewPreferencePage_PreviewBrowsers);
		Table table = new Table(browsers, SWT.CHECK | SWT.BORDER | SWT.SINGLE);
		table.setFont(parent.getFont());
		previewViewer = new CheckboxTableViewer(table);
		previewViewer.setContentProvider(new ArrayContentProvider());
		previewViewer.setLabelProvider(new ILabelProvider()
		{

			public void removeListener(ILabelProviderListener listener)
			{
			}

			public boolean isLabelProperty(Object element, String property)
			{
				return false;
			}

			public void dispose()
			{
			}

			public void addListener(ILabelProviderListener listener)
			{
			}

			public Image getImage(Object element)
			{
				return BrowserExtensionLoader.getBrowserImage(element.toString());
			}

			public String getText(Object element)
			{
				return element.toString();
			}

		});
		previewViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				if (Platform.getOS().equals(Platform.OS_MACOSX))
				{
					if (previewViewer.getChecked("Firefox") && !previewViewer.getChecked("Safari")) //$NON-NLS-1$ //$NON-NLS-2$
					{
						MessageDialog.openInformation(getShell(),
								Messages.PreviewPreferencePage_FirefoxPreviewIssueTitle,
								Messages.PreviewPreferencePage_FirefoxPreviewIssueMessage);
						previewViewer.setChecked("Firefox", false); //$NON-NLS-1$
					}
				}
			}

		});
		table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		addPreviewBrowsers();
		addCheckedBrowsers();

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
		for (int i = 0; i < extensions.length; i++)
		{
			IConfigurationElement[] ce = extensions[i].getConfigurationElements();
			for (int j = 0; j < ce.length; j++)
			{
				String browserClass = ce[j].getAttribute(UnifiedEditorsPlugin.CLASS_ATTR);
				String name = BrowserExtensionLoader.getBrowserLabel(ce[j]);
				if (browserClass != null && name != null)
				{
					browserPreviews.add(name);
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
				IPreferenceConstants.HTMLEDITOR_BROWSER_PREVIEW_PREFERENCE);
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
		ArrayList<String> al = new ArrayList<String>();
		Object[] elements = previewViewer.getCheckedElements();
		for (int i = 0; i < elements.length; i++)
		{
			al.add(elements[i].toString());
		}
		getPreferenceStore().setValue(IPreferenceConstants.HTMLEDITOR_BROWSER_PREVIEW_PREFERENCE,
				StringUtils.join(",", (String[]) al.toArray(new String[0]))); //$NON-NLS-1$
		String type = ""; //$NON-NLS-1$
		if (block.getServerButton().getSelection())
		{
			if (block.getServerAppendButton().getSelection())
			{
				type = HTMLPreviewPropertyPage.APPENDED_SERVER_BASED_TYPE;
			}
			else
			{
				type = HTMLPreviewPropertyPage.SERVER_BASED_TYPE;
			}
		}
		else if (block.getConfigurationButton().getSelection())
		{
			type = HTMLPreviewPropertyPage.CONFIG_BASED_TYPE;
		}
		else if (block.getStartURLButton().getSelection())
		{
			if (block.getStartURLAppendButton().getSelection())
			{
				type = HTMLPreviewPropertyPage.APPENDED_ABSOLUTE_BASED_TYPE;
			}
			else
			{
				type = HTMLPreviewPropertyPage.ABSOLUTE_BASED_TYPE;
			}
		}
		else
		{
			type = HTMLPreviewPropertyPage.FILE_BASED_TYPE;
		}
		String value = ""; //$NON-NLS-1$
		if (block.getServerButton().getSelection())
		{
			Object obj = block.getServerText().getData();
			if (obj != null && obj instanceof IServer)
			{
				value = ((IServer) obj).getId();
			}
			else
			{
				value = block.getServerText().getText();
			}
		}
		else if (block.getConfigurationButton().getSelection())
		{
			value = block.getConfigurationText().getText();
		}
		else if (block.getStartURLButton().getSelection())
		{
			value = block.getStartURLText().getText().trim();
		}
		getPreferenceStore().setValue(HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_TYPE, type);
		getPreferenceStore().setValue(HTMLPreviewPropertyPage.HTML_PREVIEW_VALUE, value);
		getPreferenceStore().setValue(IPreferenceConstants.AUTO_SAVE_BEFORE_PREVIEWING, autoSave.getSelection());
		getPreferenceStore().setValue(IPreferenceConstants.USE_TEMP_FILES_FOR_PREVIEW, useTempFiles.getSelection());
		UnifiedEditorsPlugin.getDefault().getPreferenceStore().setValue(
				com.aptana.ide.editors.preferences.IPreferenceConstants.CACHE_BUST_BROWSERS, cacheBust.getSelection());
		return true;
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{
		super.performDefaults();
		String prefs = getPreferenceStore()
				.getDefaultString(IPreferenceConstants.HTMLEDITOR_BROWSER_PREVIEW_PREFERENCE);
		previewViewer.setCheckedElements(prefs.split(",")); //$NON-NLS-1$
		String type = HTMLPlugin.getDefault().getPreferenceStore().getDefaultString(
				HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_TYPE);
		String value = HTMLPlugin.getDefault().getPreferenceStore().getDefaultString(
				HTMLPreviewPropertyPage.HTML_PREVIEW_VALUE);
		block.setType(type);
		block.setValue(value);
		block.updateControls();
		autoSave.setSelection(getPreferenceStore().getDefaultBoolean(IPreferenceConstants.AUTO_SAVE_BEFORE_PREVIEWING));
		cacheBust.setSelection(UnifiedEditorsPlugin.getDefault().getPreferenceStore().getDefaultBoolean(
				com.aptana.ide.editors.preferences.IPreferenceConstants.CACHE_BUST_BROWSERS));
		useTempFiles.setSelection(getPreferenceStore().getDefaultBoolean(
				IPreferenceConstants.USE_TEMP_FILES_FOR_PREVIEW));
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
		setPreferenceStore(HTMLPlugin.getDefault().getPreferenceStore());
	}

}
