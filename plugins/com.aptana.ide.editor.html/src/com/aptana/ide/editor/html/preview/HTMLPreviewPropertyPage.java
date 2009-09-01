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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.editor.html.preview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.dialogs.PropertyPage;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.EclipseUIUtils;
import com.aptana.ide.editor.html.BrowserExtensionLoader;
import com.aptana.ide.editor.html.HTMLPlugin;
import com.aptana.ide.editor.html.preferences.IPreferenceConstants;
import com.aptana.ide.editor.html.preferences.Messages;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.jetty.server.HTMLPreviewConstants;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class HTMLPreviewPropertyPage extends PropertyPage
{

	/**
	 * CONTEXT_ROOT
	 */
	public static final String CONTEXT_ROOT = HTMLPreviewConstants.CONTEXT_ROOT;

	/**
	 * HTML_PREVIEW_OVERRIDE
	 */
	public static final String HTML_PREVIEW_OVERRIDE = HTMLPreviewConstants.HTML_PREVIEW_OVERRIDE;

	/**
	 * HTML_PREVIEW_BROWSERS
	 */
	public static final String HTML_PREVIEW_BROWSERS = IPreferenceConstants.HTMLEDITOR_BROWSER_PREVIEW_PREFERENCE;

	/**
	 * HTML_PREVIEW_CONFIG
	 */
	public static final String HTML_PREVIEW_CONFIG = IPreferenceConstants.HTMLEDITOR_RUNCONFIG_PREVIEW_PREFERENCE;

	/**
	 * HTML_PREVIEW_ADDON_BROWSERS
	 */
	public static final String HTML_PREVIEW_ADDON_BROWSERS = HTMLPreviewConstants.HTML_PREVIEW_ADDON_BROWSERS;

	/**
	 * HTML_PREVIEW_ADDON_NAMES
	 */
	public static final String HTML_PREVIEW_ADDON_NAMES = HTMLPreviewConstants.HTML_PREVIEW_ADDON_NAMES;

	/**
	 * HTML_PREVIEW_ADDON_URLS
	 */
	public static final String HTML_PREVIEW_ADDON_URLS = HTMLPreviewConstants.HTML_PREVIEW_ADDON_URLS;

	/**
	 * HTML_PREVIEW_ADDON_TYPE
	 */
	public static final String HTML_PREVIEW_ADDON_TYPE = HTMLPreviewConstants.HTML_PREVIEW_ADDON_TYPE;

	/**
	 * HTML_PREVIEW_VALUE
	 */
	public static final String HTML_PREVIEW_VALUE = HTMLPreviewConstants.HTML_PREVIEW_VALUE;

	/**
	 * SERVER_BASED_TYPE
	 */
	public static final String SERVER_BASED_TYPE = HTMLPreviewConstants.SERVER_BASED_TYPE;

	/**
	 * APPENDED_SERVER_BASED_TYPE
	 */
	public static final String APPENDED_SERVER_BASED_TYPE = HTMLPreviewConstants.APPENDED_SERVER_BASED_TYPE;

	/**
	 * APPENDED_START_URL_BASED_TYPE
	 */
	public static final String APPENDED_ABSOLUTE_BASED_TYPE = HTMLPreviewConstants.APPENDED_ABSOLUTE_BASED_TYPE;

	/**
	 * CONFIG_BASED_TYPE
	 */
	public static final String CONFIG_BASED_TYPE = HTMLPreviewConstants.CONFIG_BASED_TYPE;

	/**
	 * ABSOLUTE_BASED_TYPE
	 */
	public static final String ABSOLUTE_BASED_TYPE = HTMLPreviewConstants.ABSOLUTE_BASED_TYPE;

	/**
	 * FILE_BASED_TYLE
	 */
	public static final String FILE_BASED_TYPE = HTMLPreviewConstants.FILE_BASED_TYPE;

	/**
	 * HTML_PREVIEW_ADDON_SERVER_ID
	 */
	public static final String HTML_PREVIEW_ADDON_SERVER_ID = HTMLPreviewConstants.HTML_PREVIEW_ADDON_SERVER_ID;

	/**
	 * HTML_PREVIEW_ADDON_CONFIG_ID
	 */
	public static final String HTML_PREVIEW_ADDON_CONFIG_ID = HTMLPreviewConstants.HTML_PREVIEW_ADDON_CONFIG_ID;

	/**
	 * INVALID
	 */
	public static final String INVALID = HTMLPreviewConstants.INVALID;

	/**
	 * HTML_PREVIEW_ADDON_USE_INTERNAL_SERVER
	 */
	public static final String HTML_PREVIEW_ADDON_USE_INTERNAL_SERVER = HTMLPreviewConstants.HTML_PREVIEW_ADDON_USE_INTERNAL_SERVER;

	/**
	 * HTML_PREVIEW_LAST_START_URLS
	 */
	public static final String HTML_PREVIEW_LAST_START_URLS = HTMLPreviewConstants.HTML_PREVIEW_LAST_START_URLS;

	/**
	 * PREFERENCE_DELIMITER
	 */
	public static final String PREFERENCE_DELIMITER = HTMLPreviewConstants.PREFERENCE_DELIMITER;

	/**
	 * TRUE
	 */
	public static final String TRUE = HTMLPreviewConstants.TRUE;

	/**
	 * FALSE
	 */
	public static final String FALSE = HTMLPreviewConstants.FALSE;

	private Button override;
	private CheckboxTableViewer previewViewer;
	private Label description;
	private Composite displayArea;
	private PreviewTypeSelectionBlock block;
	private Group browsers;
	private IResource resource;
	private Label contextRootLabel;
	private Text contextRootText;
	private Button browseContextRoot;

	/**
	 * Property page constructor
	 */
	public HTMLPreviewPropertyPage()
	{
		setDescription(Messages.HTMLPreviewPropertyPage_Description);
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{
		setPreferenceStore(HTMLPlugin.getDefault().getPreferenceStore());
		displayArea = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = false;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 0;
		displayArea.setLayout(layout);

		resource = (IResource) getElement();

		Composite top = new Composite(displayArea, SWT.NONE);
		GridLayout topLayout = new GridLayout(2, false);
		topLayout.marginHeight = 0;
		topLayout.marginWidth = 0;
		top.setLayout(topLayout);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		override = new Button(top, SWT.CHECK);
		override.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		override.setSelection(false);
		override.setText(Messages.HTMLPreviewPropertyPage_LBL_Override);
		override.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				boolean ov = override.getSelection();
				previewViewer.getTable().setEnabled(ov);
				previewViewer.refresh();
				contextRootText.setEnabled(ov);
				browseContextRoot.setEnabled(ov);
				block.setEnabled(ov);
				block.updateControls();
			}

		});
		String shouldOverride;
		boolean ov = false;
		try
		{
			shouldOverride = resource.getPersistentProperty(new QualifiedName("", HTML_PREVIEW_OVERRIDE)); //$NON-NLS-1$
		}
		catch (CoreException e2)
		{
			shouldOverride = FALSE;
		}
		if (TRUE.equals(shouldOverride))
		{
			override.setSelection(true);
			ov = true;
		}
		Link goToParent = new Link(top, SWT.NONE);
		goToParent.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				if (resource != null)
				{
					if (resource instanceof IProject)
					{
						PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(Display.getDefault()
								.getActiveShell(), "com.aptana.ide.editor.html.preferences.PreviewPreferencePage", //$NON-NLS-1$
								new String[] { "com.aptana.ide.editor.html.preferences.PreviewPreferencePage" }, null); //$NON-NLS-1$
						dialog.open();
					}
				}
			}

		});
		goToParent.setLayoutData(new GridData(SWT.END, SWT.FILL, true, false));
		goToParent.setText("<a>" + Messages.HTMLPreviewPropertyPage_LBL_EditLink + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$

		displayArea.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		Composite contextRootComp = new Composite(displayArea, SWT.NONE);
		GridLayout crcLayout = new GridLayout(3, false);
		crcLayout.marginHeight = 0;
		crcLayout.marginWidth = 0;
		contextRootComp.setLayout(crcLayout);
		contextRootComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		contextRootLabel = new Label(contextRootComp, SWT.NONE);
		contextRootLabel.setText(Messages.HTMLPreviewPropertyPage_LBL_DocRoot);
		contextRootText = new Text(contextRootComp, SWT.SINGLE | SWT.BORDER);
		contextRootText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		if (ov)
		{
			try
			{
				String root = resource.getPersistentProperty(new QualifiedName("", CONTEXT_ROOT)); //$NON-NLS-1$
				if (root != null)
				{
					contextRootText.setText(root);
				}
				else
				{
					contextRootText.setText("/"); //$NON-NLS-1$
				}
			}
			catch (CoreException e1)
			{
			}
		}
		else
		{
			contextRootText.setText("/"); //$NON-NLS-1$
		}
		contextRootText.setEditable(false);
		browseContextRoot = new Button(contextRootComp, SWT.PUSH);
		browseContextRoot.setText(Messages.HTMLPreviewPropertyPage_LBL_Browse);
		browseContextRoot.setEnabled(ov);
		browseContextRoot.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				final ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(browseContextRoot.getShell(),
						EclipseUIUtils.createWorkbenchLabelProvider(), createContentProvider());
				dialog.setInput(HTMLPreviewPropertyPage.this);
				dialog.setAllowMultiple(false);
				dialog.setMessage(Messages.HTMLPreviewPropertyPage_MSG_SelectRoot);
				int rc = dialog.open();
				if (rc == ElementTreeSelectionDialog.OK)
				{
					Object result = dialog.getFirstResult();
					if (result instanceof IContainer)
					{
						String root = "/"; //$NON-NLS-1$
						root += ((IContainer) result).getProjectRelativePath().toString();
						contextRootText.setText(root);
					}

				}
			}
		});
		String type = FILE_BASED_TYPE;
		String value = null;
		if (ov)
		{

			try
			{
				type = resource.getPersistentProperty(new QualifiedName("", HTML_PREVIEW_ADDON_TYPE)); //$NON-NLS-1$
				value = resource.getPersistentProperty(new QualifiedName("", HTML_PREVIEW_VALUE)); //$NON-NLS-1$
			}
			catch (CoreException e1)
			{
				type = FILE_BASED_TYPE;
				value = null;
			}
		}
		else
		{
			type = HTMLPlugin.getDefault().getPreferenceStore().getString(
					HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_TYPE);
			value = HTMLPlugin.getDefault().getPreferenceStore().getString(HTMLPreviewPropertyPage.HTML_PREVIEW_VALUE);
		}
		block = new PreviewTypeSelectionBlock();
		block.setSampleProjectName(resource.getName());
		block.useSampleURL();
		block.createStartActionSection(displayArea, type, value);
		block.setCurrentURLLabel(Messages.HTMLPreviewPropertyPage_LBL_SampleURL);
		block.setEnabled(ov);
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
		table.setEnabled(ov);
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

	private ITreeContentProvider createContentProvider()
	{
		ITreeContentProvider provider = new ITreeContentProvider()
		{

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
			{

			}

			public void dispose()
			{

			}

			public Object[] getElements(Object inputElement)
			{
				if (inputElement == HTMLPreviewPropertyPage.this)
				{
					return new Object[] { resource };
				}
				return getChildren(inputElement);
			}

			public boolean hasChildren(Object element)
			{
				if (element instanceof IContainer)
				{
					try
					{
						IResource[] resources = ((IContainer) element).members();
						for (int i = 0; i < resources.length; i++)
						{
							if (resources[i] instanceof IContainer)
							{
								return true;
							}
						}
					}
					catch (CoreException e)
					{
						return false;
					}
				}
				return false;
			}

			public Object getParent(Object element)
			{
				if (element instanceof IProject)
				{
					return null;
				}
				else if (element instanceof IContainer)
				{
					return ((IContainer) element).getParent();
				}
				return null;
			}

			public Object[] getChildren(Object parentElement)
			{
				if (parentElement instanceof IContainer)
				{
					try
					{
						IResource[] resources = ((IContainer) parentElement).members();
						List<IContainer> folders = new ArrayList<IContainer>();
						for (int i = 0; i < resources.length; i++)
						{
							if (resources[i] instanceof IContainer)
							{
								folders.add((IContainer) resources[i]);
							}
						}
						return folders.toArray();
					}
					catch (CoreException e)
					{
						return new Object[0];
					}

				}
				return new Object[0];
			}

		};
		return provider;
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
		String all_browsers;
		try
		{
			if (override.getSelection())
			{
				all_browsers = resource.getPersistentProperty(new QualifiedName("", HTML_PREVIEW_BROWSERS)); //$NON-NLS-1$
				if (all_browsers == null)
				{
					all_browsers = ""; //$NON-NLS-1$
				}
			}
			else
			{
				all_browsers = HTMLPlugin.getDefault().getPreferenceStore().getString(
						IPreferenceConstants.HTMLEDITOR_BROWSER_PREVIEW_PREFERENCE);
			}
			String[] browsers = all_browsers.split(","); //$NON-NLS-1$
			for (int i = 0; i < browsers.length; i++)
			{
				previewViewer.setChecked(browsers[i], true);
			}
		}
		catch (CoreException e)
		{
			IdeLog.logInfo(HTMLPlugin.getDefault(), Messages.HTMLPreviewPropertyPage_INF_ErrorLoading, e);
		}

	}

	/**
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk()
	{
		try
		{
			boolean ov = override.getSelection();
			if (ov)
			{
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
					block.saveURLs();
				}

				resource.setPersistentProperty(new QualifiedName("", HTML_PREVIEW_ADDON_TYPE), type); //$NON-NLS-1$
				resource.setPersistentProperty(new QualifiedName("", HTML_PREVIEW_VALUE), value); //$NON-NLS-1$
				resource.setPersistentProperty(new QualifiedName("", HTML_PREVIEW_OVERRIDE), TRUE); //$NON-NLS-1$
				resource.setPersistentProperty(new QualifiedName("", CONTEXT_ROOT), contextRootText.getText()); //$NON-NLS-1$
				ArrayList<String> al = new ArrayList<String>();
				Object[] elements = previewViewer.getCheckedElements();
				for (int i = 0; i < elements.length; i++)
				{
					al.add(elements[i].toString());
				}
				resource.setPersistentProperty(new QualifiedName("", //$NON-NLS-1$
						IPreferenceConstants.HTMLEDITOR_BROWSER_PREVIEW_PREFERENCE), StringUtils.join(
						",", (String[]) al.toArray(new String[0]))); //$NON-NLS-1$
			}
			else
			{
				resource.setPersistentProperty(new QualifiedName("", HTML_PREVIEW_OVERRIDE), FALSE); //$NON-NLS-1$
			}
		}
		catch (CoreException e)
		{
		}
		return true;
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{
		super.performDefaults();
		String prefs = getPreferenceStore().getString(IPreferenceConstants.HTMLEDITOR_BROWSER_PREVIEW_PREFERENCE);
		previewViewer.setCheckedElements(prefs.split(",")); //$NON-NLS-1$
		String type = HTMLPlugin.getDefault().getPreferenceStore().getString(
				HTMLPreviewPropertyPage.HTML_PREVIEW_ADDON_TYPE);
		String value = HTMLPlugin.getDefault().getPreferenceStore().getString(
				HTMLPreviewPropertyPage.HTML_PREVIEW_VALUE);
		block.setType(type);
		block.setValue(value);
		override.setSelection(false);
		previewViewer.getTable().setEnabled(false);
		contextRootText.setEnabled(false);
		browseContextRoot.setEnabled(false);
		block.updateControls();
	}
}
