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
package com.aptana.ide.server.ui.launchConfigurations;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceSorter;

import com.aptana.ide.core.CoreStrings;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.server.core.HttpServerLaunchConfiguration;

/**
 * LaunchBrowserSettingsTab
 */
public class LaunchBrowserSettingsTab extends AbstractLaunchConfigurationTab
{
	private HttpServerLaunchConfiguration launchConfiguration;

	private Text fBrowserExeText;
	private Button bBrowserExeBrowse;

	private Button rbCurrentPage;
	private Button rbSpecificPage;
	private Button bSpecificPageBrowse;
	private Text fSpecificPageText;
	private Button rbStartUrl;
	private Text fStartUrlText;

	private Text fbaseUrlText;
	private Button rbInternalServer;
	private Button rbCustomServer;
	private Label baseUrlLabel;

	/**
	 * LaunchBrowserSettingsTab
	 */
	public LaunchBrowserSettingsTab()
	{
		launchConfiguration = new HttpServerLaunchConfiguration();
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent)
	{
		Font font = parent.getFont();

		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		GridLayout topLayout = new GridLayout();
		topLayout.verticalSpacing = 5;
		comp.setLayout(topLayout);
		comp.setFont(font);

		createBrowserExeControl(comp);
		createStartActionControl(comp);
		createServerControl(comp);
	}

	/**
	 * createBrowserExeControl
	 *
	 * @param parent
	 */
	public void createBrowserExeControl(Composite parent)
	{
		Font font = parent.getFont();
		Group group = new Group(parent, SWT.FLAT | SWT.SHADOW_IN);
		group.setText(Messages.LaunchBrowserSettingsTab_LBL_BrowserGroup);
		group.setFont(font);

		GridData gd = new GridData(SWT.FILL, 20, true, false);
		group.setLayoutData(gd);

		FormLayout form = new FormLayout();
		group.setLayout(form);
		FormData data;
		form.marginTop = 10;
		form.marginBottom = 10;
		form.marginLeft = 10;
		form.marginRight = 10;

		int column1Offset = 0; // the left offset of the items placed in the first column
		int column2Offset = 120; // the left offset of the items placed in the second column

		Label browserExeLabel = new Label(group, SWT.NONE);
		browserExeLabel.setText(StringUtils.makeFormLabel(Messages.LaunchBrowserSettingsTab_LBL_BrowserExe));
		data = new FormData();
		data.left = new FormAttachment(0, column1Offset);
		browserExeLabel.setLayoutData(data);

		bBrowserExeBrowse = new Button(group, SWT.PUSH);
		bBrowserExeBrowse.setText(StringUtils.ellipsify(CoreStrings.BROWSE));
		data = new FormData();
		data.top = new FormAttachment(browserExeLabel, -1, SWT.TOP);
		data.right = new FormAttachment(100, 0);
		bBrowserExeBrowse.setLayoutData(data);

		fBrowserExeText = new Text(group, SWT.SINGLE | SWT.BORDER);
		data = new FormData();
		data.top = new FormAttachment(browserExeLabel, 0, SWT.TOP);
		data.left = new FormAttachment(0, column2Offset);
		data.right = new FormAttachment(bBrowserExeBrowse, -5, SWT.LEFT);
		fBrowserExeText.setLayoutData(data);

		bBrowserExeBrowse.addSelectionListener(new SelectionAdapter()
		{
			/**
			 * Prompts the user to choose a location from the filesystem and sets the location as
			 * the full path of the selected file.
			 */
			public void widgetSelected(SelectionEvent e)
			{
				FileDialog fileDialog = new FileDialog(getShell(), SWT.NONE);
				fileDialog.setFileName(fBrowserExeText.getText());
				String text = fileDialog.open();
				if (text != null)
				{
					fBrowserExeText.setText(text);
				}
			}
		});

		// hook up event handlers to update the configuration dialog when settings change
		Listener listener = new Listener()
		{
			public void handleEvent(Event event)
			{
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		};
		fBrowserExeText.addListener(SWT.Modify, listener);
	}	

	/**
	 * createStartActionControl
	 *
	 * @param parent
	 */
	public void createStartActionControl(Composite parent)
	{
		Font font = parent.getFont();
		Group group = new Group(parent, SWT.FLAT | SWT.SHADOW_IN);
		group.setText(Messages.LaunchBrowserSettingsTab_LBL_Start);
		group.setFont(font);

		GridData gd = new GridData(SWT.FILL, 20, true, false);
		group.setLayoutData(gd);

		FormLayout form = new FormLayout();
		group.setLayout(form);
		FormData data;
		form.marginTop = 10;
		form.marginBottom = 10;
		form.marginLeft = 10;
		form.marginRight = 10;

		int column1Offset = 0; // the left offset of the items placed in the first column
		int column2Offset = 120; // the left offset of the items placed in the second column

		rbCurrentPage = new Button(group, SWT.RADIO);
		rbCurrentPage.setText(Messages.LaunchBrowserSettingsTab_LBL_UseCurrent);
		data = new FormData();
		data.left = new FormAttachment(0, column1Offset);
		rbCurrentPage.setLayoutData(data);

		rbSpecificPage = new Button(group, SWT.RADIO);
		rbSpecificPage.setText(StringUtils.makeFormLabel(Messages.LaunchBrowserSettingsTab_LBL_SpecificPage));
		data = new FormData();
		data.top = new FormAttachment(rbCurrentPage, 10, SWT.BOTTOM);
		data.left = new FormAttachment(0, column1Offset);
		rbSpecificPage.setLayoutData(data);

		bSpecificPageBrowse = new Button(group, SWT.PUSH);
		bSpecificPageBrowse.setText(StringUtils.ellipsify(CoreStrings.BROWSE));
		data = new FormData();
		data.top = new FormAttachment(rbSpecificPage, -1, SWT.TOP);
		data.right = new FormAttachment(100, 0);
		bSpecificPageBrowse.setLayoutData(data);

		fSpecificPageText = new Text(group, SWT.SINGLE | SWT.BORDER);
		data = new FormData();
		data.top = new FormAttachment(rbSpecificPage, 0, SWT.TOP);
		data.left = new FormAttachment(0, column2Offset);
		data.right = new FormAttachment(bSpecificPageBrowse, -5, SWT.LEFT);
		fSpecificPageText.setLayoutData(data);

		rbStartUrl = new Button(group, SWT.RADIO);
		rbStartUrl.setText(StringUtils.makeFormLabel(Messages.LaunchBrowserSettingsTab_LBL_StartURL));
		data = new FormData();
		data.top = new FormAttachment(rbSpecificPage, 10, SWT.BOTTOM);
		data.left = new FormAttachment(0, column1Offset);
		rbStartUrl.setLayoutData(data);

		fStartUrlText = new Text(group, SWT.SINGLE | SWT.BORDER);
		data = new FormData();
		data.top = new FormAttachment(rbStartUrl, 0, SWT.TOP);
		data.left = new FormAttachment(0, column2Offset);
		data.right = new FormAttachment(100, 0);
		fStartUrlText.setLayoutData(data);

		// hook up event handlers to update the configuration dialog when settings change
		Listener listener = new Listener()
		{
			public void handleEvent(Event event)
			{
				setDirty(true);
				updateLaunchConfigurationDialog();

				fStartUrlText.setEnabled(rbStartUrl.getSelection());
				fSpecificPageText.setEnabled(rbSpecificPage.getSelection());
				bSpecificPageBrowse.setEnabled(rbSpecificPage.getSelection());
			}
		};
		rbCurrentPage.addListener(SWT.Selection, listener);
		rbSpecificPage.addListener(SWT.Selection, listener);
		fSpecificPageText.addListener(SWT.Modify, listener);
		rbStartUrl.addListener(SWT.Selection, listener);
		fStartUrlText.addListener(SWT.Modify, listener);

		bSpecificPageBrowse.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				IResource resource = chooseWorkspaceLocation();
				if (resource != null)
				{
					fSpecificPageText.setText(resource.getFullPath().toPortableString());
				}
			}
		});
	}

	/**
	 * createServerControl
	 *
	 * @param parent
	 */
	public void createServerControl(Composite parent)
	{
		Font font = parent.getFont();
		Group group = new Group(parent, SWT.FLAT);
		group.setText(Messages.LaunchBrowserSettingsTab_LBL_Server);
		group.setFont(font);

		GridData gd = new GridData(SWT.FILL, 20, true, false);
		group.setLayoutData(gd);

		FormLayout form = new FormLayout();
		group.setLayout(form);
		FormData data;
		form.marginTop = 10;
		form.marginBottom = 10;
		form.marginLeft = 10;
		form.marginRight = 10;

		int column1Offset = 0; // the left offset of the items placed in the first column
		int column3Offset = 120; // the left offset of the items placed in the second column

		rbInternalServer = new Button(group, SWT.RADIO);
		rbInternalServer.setText(Messages.LaunchBrowserSettingsTab_LBL_InternalServer);
		data = new FormData();
		data.left = new FormAttachment(0, column1Offset);
		rbInternalServer.setLayoutData(data);

		rbCustomServer = new Button(group, SWT.RADIO);
		rbCustomServer.setText(Messages.LaunchBrowserSettingsTab_LBL_ExternalServer);
		data = new FormData();
		data.top = new FormAttachment(rbInternalServer, 10, SWT.BOTTOM);
		data.left = new FormAttachment(0, column1Offset);
		rbCustomServer.setLayoutData(data);

		fbaseUrlText = new Text(group, SWT.SINGLE | SWT.BORDER);
		data = new FormData();
		data.top = new FormAttachment(rbCustomServer, 10, SWT.BOTTOM);
		data.left = new FormAttachment(0, column3Offset);
		data.right = new FormAttachment(100, 0);
		fbaseUrlText.setLayoutData(data);

		baseUrlLabel = new Label(group, SWT.NONE);
		baseUrlLabel.setText(StringUtils.makeFormLabel(Messages.LaunchBrowserSettingsTab_LBL_BaseURL));
		baseUrlLabel.setAlignment(SWT.RIGHT);
		data = new FormData();
		data.right = new FormAttachment(fbaseUrlText, -8, SWT.LEFT);
		data.top = new FormAttachment(fbaseUrlText, 0, SWT.TOP);
		baseUrlLabel.setLayoutData(data);

		// hook up event handlers to update the configuration dialog when settings change
		Listener listener = new Listener()
		{
			public void handleEvent(Event event)
			{
				setDirty(true);
				updateLaunchConfigurationDialog();

				// update the UI based on the current selected server type
				boolean customServerEnabled = rbCustomServer.getSelection();
				baseUrlLabel.setEnabled(customServerEnabled);
				fbaseUrlText.setEnabled(customServerEnabled);
			}
		};
		rbCustomServer.addListener(SWT.Selection, listener);
		rbInternalServer.addListener(SWT.Selection, listener);
		fbaseUrlText.addListener(SWT.Modify, listener);
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		HttpServerLaunchConfiguration httpConfig = new HttpServerLaunchConfiguration();
		httpConfig.setDefaults(configuration);
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration configuration)
	{
		launchConfiguration.load(configuration);

		fBrowserExeText.setText(launchConfiguration.getBrowserExe());
		rbCurrentPage
				.setSelection(launchConfiguration.getStartActionType() == HttpServerLaunchConfiguration.START_ACTION_CURRENT_PAGE);
		rbSpecificPage
				.setSelection(launchConfiguration.getStartActionType() == HttpServerLaunchConfiguration.START_ACTION_SPECIFIC_PAGE);
		rbStartUrl
				.setSelection(launchConfiguration.getStartActionType() == HttpServerLaunchConfiguration.START_ACTION_START_URL);

		fSpecificPageText.setText(launchConfiguration.getStartPagePath());
		fStartUrlText.setText(launchConfiguration.getStartPageUrl());

		rbInternalServer
				.setSelection(launchConfiguration.getServerType() == HttpServerLaunchConfiguration.SERVER_INTERNAL);
		rbCustomServer
				.setSelection(launchConfiguration.getServerType() == HttpServerLaunchConfiguration.SERVER_EXTERNAL);
		fbaseUrlText.setText(launchConfiguration.getExternalBaseUrl());
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		launchConfiguration.setBrowserExe(fBrowserExeText.getText());

		if (rbCurrentPage.getSelection())
		{
			launchConfiguration.setStartActionType(HttpServerLaunchConfiguration.START_ACTION_CURRENT_PAGE);
		}
		else if (rbSpecificPage.getSelection())
		{
			launchConfiguration.setStartActionType(HttpServerLaunchConfiguration.START_ACTION_SPECIFIC_PAGE);
		}
		else if (rbStartUrl.getSelection())
		{
			launchConfiguration.setStartActionType(HttpServerLaunchConfiguration.START_ACTION_START_URL);
		}
		launchConfiguration.setStartPagePath(fSpecificPageText.getText());
		launchConfiguration.setStartPageUrl(fStartUrlText.getText());

		if (rbInternalServer.getSelection())
		{
			launchConfiguration.setServerType(HttpServerLaunchConfiguration.SERVER_INTERNAL);
		}
		else if (rbCustomServer.getSelection())
		{
			launchConfiguration.setServerType(HttpServerLaunchConfiguration.SERVER_EXTERNAL);
		}

		launchConfiguration.setExternalBaseUrl(fbaseUrlText.getText());

		launchConfiguration.save(configuration);
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	public String getName()
	{
		return Messages.LaunchBrowserSettingsTab_Name;
	}

	/**
	 * chooseWorkspaceLocation
	 *
	 * @return IResource
	 */
	private IResource chooseWorkspaceLocation()
	{
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(),
				new WorkbenchContentProvider());
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setSorter(new ResourceSorter(ResourceSorter.NAME));
		dialog.setMessage(Messages.LaunchBrowserSettingsTab_File_Title);
		dialog.open();
		Object result = dialog.getFirstResult();

		return (IResource) result;
	}
}
