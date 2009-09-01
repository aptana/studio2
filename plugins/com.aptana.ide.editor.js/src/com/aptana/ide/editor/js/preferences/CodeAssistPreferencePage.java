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
package com.aptana.ide.editor.js.preferences;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.osgi.framework.Bundle;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.js.JSLanguageEnvironment;
import com.aptana.ide.editor.js.JSPlugin;

/**
 * CodeAssistPreferencePage
 * 
 * @author Ingo Muschenetz
 * @author Kevin Sawicki (ksawicki@aptana.com) Added environment table
 */
public class CodeAssistPreferencePage extends com.aptana.ide.editors.preferences.CodeAssistPreferencePage
{

	private Map<File,Image> images = new HashMap<File,Image>();
	private CheckboxTableViewer envTable;

	/**
	 * @see com.aptana.ide.editors.preferences.CodeAssistPreferencePage#createFieldEditors()
	 */
	public void createFieldEditors()
	{
		super.createFieldEditors();
		Composite appearanceComposite = getFieldEditorParent();
		Composite table = com.aptana.ide.core.ui.preferences.GeneralPreferencePage.createGroup(appearanceComposite,
				Messages.CodeAssistPreferencePage_Javascript_environments);
		table.getParent().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(1, true);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		table.setLayout(layout);
		Label envLabel = new Label(table, SWT.LEFT | SWT.WRAP);
		envLabel.setText(Messages.CodeAssistPreferencePage_LBL_Environments_present_in_code_assist);
		envLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		envTable = CheckboxTableViewer.newCheckList(table, SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE);
		envTable.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		String[] environments = JSLanguageEnvironment.getEnabledEnvironments();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		if (registry != null)
		{
			IExtensionPoint extensionPoint = registry
					.getExtensionPoint(JSPlugin.ID, JSLanguageEnvironment.SCRIPTDOC_ID);

			if (extensionPoint != null)
			{
				IExtension[] extensions = extensionPoint.getExtensions();

				for (int i = 0; i < extensions.length; i++)
				{
					IExtension extension = extensions[i];
					IConfigurationElement[] elements = extension.getConfigurationElements();

					for (int j = 0; j < elements.length; j++)
					{
						IConfigurationElement element = elements[j];
						IExtension declaring = element.getDeclaringExtension();
						String declaringPluginID = declaring.getNamespaceIdentifier();
						Bundle bundle = Platform.getBundle(declaringPluginID);
						String agent = element.getAttribute(JSLanguageEnvironment.ATTR_USER_AGENT);
						String icon = element.getAttribute(JSLanguageEnvironment.ATTR_ICON);
						if (agent != null)
						{
							TableItem item = new TableItem(envTable.getTable(), SWT.NONE);
							item.setText(agent);
							if (icon != null)
							{
								String iconFile = getResolvedFilename(bundle, icon);
								if (iconFile != null)
								{
									File file = new File(iconFile);
									if (file.exists())
									{
										Image result = null;
										if (images.containsKey(file.getAbsolutePath()))
										{
											result = images.get(file.getAbsolutePath());
										}
										else
										{
											result = new Image(Display.getDefault(), file.getAbsolutePath());
										}
										images.put(file.getAbsoluteFile(), result);
										item.setImage(result);
									}
								}
							}
						}
					}
				}
			}
		}
		TableItem[] items = envTable.getTable().getItems();
		for (int i = 0; i < items.length; i++)
		{
			String label = items[i].getText();
			for (int j = 0; j < environments.length; j++)
			{
				if (label.equals(environments[j]))
				{
					items[i].setChecked(true);
					break;
				}
			}
		}
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#dispose()
	 */
	public void dispose()
	{
		Iterator<Image> iter = images.values().iterator();
		while (iter.hasNext())
		{
			iter.next().dispose();
		}
		super.dispose();
	}

	private static URL getResolvedURL(Bundle b, String fullPath)
	{
		URL url = FileLocator.find(b, new Path(fullPath), null);

		if (url != null)
		{
			try
			{

				URL localUrl = FileLocator.toFileURL(url);
				if (localUrl != null)
				{
					return localUrl;
				}
			}
			catch (IOException e)
			{
				IdeLog.logError(JSPlugin.getDefault(), e.getMessage());
			}
		}
		return null;
	}

	private static String getResolvedFilename(Bundle b, String fullPath)
	{
		URL url = getResolvedURL(b, fullPath);
		if (url != null)
		{
			return url.getFile();
		}

		return null;
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
	 */
	public boolean performOk()
	{
		TableItem[] items = envTable.getTable().getItems();
		List<String> environments = new ArrayList<String>();
		List<String> disabledEnvironments = new ArrayList<String>();
		for (int i = 0; i < items.length; i++)
		{
			if (items[i].getChecked())
			{
				environments.add(items[i].getText().trim());
			}
			else
			{
				disabledEnvironments.add(items[i].getText().trim());				
			}
		}
		JSLanguageEnvironment.setEnabledEnvironments(environments.toArray(new String[0]));
		JSLanguageEnvironment.setDisabledEnvironments(disabledEnvironments.toArray(new String[0]));
		JSLanguageEnvironment.resetEnvironment();
		return super.performOk();
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{
		String[] envs = JSLanguageEnvironment.getDefaultLoadedEnvironments();
		TableItem[] items = envTable.getTable().getItems();
		for (int k = 0; k < items.length; k++)
		{
			items[k].setChecked(false);
			for (int j = 0; j < envs.length; j++)
			{
				String agent = envs[j];
				if (agent.equals(items[k].getText()))
				{
					items[k].setChecked(true);
					break;
				}				
			}
		}
		super.performDefaults();
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
	 */
	protected IPreferenceStore doGetPreferenceStore()
	{
		return JSPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * doGetPlugin
	 * 
	 * @return Plugin
	 */
	protected Plugin doGetPlugin()
	{
		return JSPlugin.getDefault();
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}
}
