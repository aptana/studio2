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
package com.aptana.ide.update.preferences;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.osgi.service.prefs.BackingStoreException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.SWTUtils;
import com.aptana.ide.server.jetty.JettyPlugin;
import com.aptana.ide.update.Activator;
import com.aptana.ide.update.manager.IPluginManager;

/**
 * A preference page for automatic updates
 * 
 * @author Ingo Muschenetz
 */
public class AutomaticUpdatesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

	private static final String P_UPDATE_POLICY_URL = "updatePolicyURL"; //$NON-NLS-1$
	private static final String DELIMETER = ","; //$NON-NLS-1$
	private static final String ADDED_UPDATE_SITES = "ADDED_UPDATE_SITES"; //$NON-NLS-1$
	private static final String POLICY_XML_RC = "http://beta.aptana.com/beta/policy.xml"; //$NON-NLS-1$
	private static final String POLICY_XML_NIGHTLY = "http://nightly.aptana.com/nightly/policy.xml"; //$NON-NLS-1$
	private static final String POLICY_XML_DEV_START = "http://developer.aptana.com/"; //$NON-NLS-1$
	private static final String POLICY_XML_DEV_END = "/policy.xml"; //$NON-NLS-1$

	private Button currentRadio;
	private Button nextRadio;
	private Button nightlyRadio;
	private Button developerRadio;
	private Text branchText;

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		container.setLayout(layout);

		Link link = new Link(container, SWT.NULL);
		link.setText(Messages.AutomaticUpdatesPreferencePage_UseP2UpdatePreferences);
		link.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				PreferencesUtil.createPreferenceDialogOn(getShell(), getPluginManager().getUpdatePreferencePageId(),
						null, null);
			}
		});

		createSpacer(container, 1);

		Group updateTypeGroup = new Group(container, SWT.NONE);
		updateTypeGroup.setText(Messages.AutomaticUpdatesPreferencePage_WhatTypesOfUpdates);
		layout = new GridLayout();
		layout.numColumns = 3;
		updateTypeGroup.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		updateTypeGroup.setLayoutData(gd);

		Label basicLabel = new Label(updateTypeGroup, SWT.NONE);
		basicLabel.setText(Messages.AutomaticUpdatesPreferencePage_AptanaWillDownloadOnNextRestart);
		basicLabel.setFont(SWTUtils.getDefaultSmallFont());
		gd = new GridData();
		gd.horizontalSpan = 3;
		gd.verticalIndent = 10;
		basicLabel.setLayoutData(gd);
//		boolean isEnabled = CoreUIPlugin.isKeyValid();
		boolean isEnabled = true;
		basicLabel.setEnabled(isEnabled);

		SelectionAdapter adapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				pageChanged();
			}
		};

		currentRadio = new Button(updateTypeGroup, SWT.RADIO);
		currentRadio.setText(Messages.AutomaticUpdatesPreferencePage_StableReleasesOnly);
		gd = new GridData();
		gd.horizontalSpan = 3;
		currentRadio.setLayoutData(gd);
		currentRadio.addSelectionListener(adapter);
		currentRadio.setEnabled(isEnabled);

		nextRadio = new Button(updateTypeGroup, SWT.RADIO);
		nextRadio.setText(Messages.AutomaticUpdatesPreferencePage_NextCandidateBuilds);
		gd = new GridData();
		gd.horizontalSpan = 3;
		nextRadio.setLayoutData(gd);
		nextRadio.addSelectionListener(adapter);
		nextRadio.setEnabled(isEnabled);

		nightlyRadio = new Button(updateTypeGroup, SWT.RADIO);
		nightlyRadio.setText(Messages.AutomaticUpdatesPreferencePage_NightlyBuilds);
		gd = new GridData();
		gd.horizontalSpan = 3;
		nightlyRadio.setLayoutData(gd);
		nightlyRadio.addSelectionListener(adapter);
		nightlyRadio.setEnabled(isEnabled);

		Label developerLabel = new Label(updateTypeGroup, SWT.NONE);
		developerLabel.setText(Messages.AutomaticUpdatesPreferencePage_ForAdvancedUsers);
		developerLabel.setFont(SWTUtils.getDefaultSmallFont());
		gd = new GridData();
		gd.horizontalSpan = 3;
		gd.verticalIndent = 10;
		developerLabel.setLayoutData(gd);
		developerLabel.setEnabled(isEnabled);

		developerRadio = new Button(updateTypeGroup, SWT.RADIO);
		developerRadio.setText(Messages.AutomaticUpdatesPreferencePage_SpecifyBranchName);
		gd = new GridData();
		gd.horizontalSpan = 2;
		developerRadio.setLayoutData(gd);
		developerRadio.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				testBranchName();
			}
		});
		developerRadio.setEnabled(isEnabled);

		branchText = new Text(updateTypeGroup, SWT.BORDER);
		gd = new GridData();
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		branchText.setLayoutData(gd);
		branchText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				testBranchName();
			}
		});
		branchText.setEnabled(isEnabled);

		initialize();

		Dialog.applyDialogFont(container);
		return container;
	}

	/**
	 * 
	 */
	private void testBranchName()
	{
		if (developerRadio.getSelection() && branchText.getText().equals("")) //$NON-NLS-1$
		{
			// developer button is selected but branch field is empty
			setValid(false);
			setErrorMessage(Messages.AutomaticUpdatesPreferencePage_MustSpecifyBranchName);
		}
		else
		{
			setValid(true);
			setErrorMessage(null);
		}
	}

	/**
	 * createSpacer
	 * 
	 * @param composite
	 * @param columnSpan
	 */
	private static void createSpacer(Composite composite, int columnSpan)
	{
		Label label = new Label(composite, SWT.NONE);
		GridData gd = new GridData();
		gd.horizontalSpan = columnSpan;
		label.setLayoutData(gd);
	}

	private void initialize()
	{
		String updateUrl = Platform.getPreferencesService().getString(Activator.PLUGIN_ID, P_UPDATE_POLICY_URL,
				"", null); //$NON-NLS-1$
		if (updateUrl.equals("")) //$NON-NLS-1$
		{
			currentRadio.setSelection(true);
		}
		else if (updateUrl.equals(POLICY_XML_RC))
		{
			nextRadio.setSelection(true);
		}
		else if (updateUrl.equals(POLICY_XML_NIGHTLY))
		{
			nightlyRadio.setSelection(true);
		}
		else if (updateUrl.startsWith(POLICY_XML_DEV_START))
		{
			developerRadio.setSelection(true);
			// retrieves the branch name
			int length = POLICY_XML_DEV_START.length();
			int lastSlash = updateUrl.indexOf('/', length);
			String branchName = updateUrl.substring(length, lastSlash);
			branchText.setText(branchName);
		}
		pageChanged();
	}

	private void pageChanged()
	{
		branchText.setEnabled(developerRadio.getSelection());
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{
		super.performDefaults();
		pageChanged();
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk()
	{
		String updateUrl = Platform.getPreferencesService().getString(Activator.PLUGIN_ID, P_UPDATE_POLICY_URL,
				"", null); //$NON-NLS-1$
		final String oldUpdateUrl = updateUrl;
		if (currentRadio.getSelection())
		{
			updateUrl = ""; //$NON-NLS-1$
		}
		else if (nextRadio.getSelection())
		{
			updateUrl = POLICY_XML_RC;
		}
		else if (nightlyRadio.getSelection())
		{
			updateUrl = POLICY_XML_NIGHTLY;
		}
		else if (developerRadio.getSelection())
		{
			updateUrl = POLICY_XML_DEV_START + branchText.getText() + POLICY_XML_DEV_END;
		}
		IEclipsePreferences prefs = (new InstanceScope()).getNode(Activator.PLUGIN_ID);
		prefs.put(P_UPDATE_POLICY_URL, updateUrl);
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
		}
		savePortalUpdateType();

		doP2Scheduling(oldUpdateUrl, updateUrl);

		return true;
	}

	private void doP2Scheduling(final String oldUpdateURL, final String updateURL)
	{
		Job job = new Job(Messages.AutomaticUpdatesPreferencePage_Changing_update_stream_job_title)
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				// Parse the policy.xml file and add all the update sites it points to
				addPolicyFileUpdateSites();
				// Now tell P2 automatic update to check for updates (it will immediately if set to check on startup.
				// Otherwise it'll just check at next scheduled time)
				getPluginManager().checkForUpdates(false);
				return Status.OK_STATUS;
			}

			private void addPolicyFileUpdateSites()
			{
				if (oldUpdateURL != null && updateURL != null && (oldUpdateURL.equals(updateURL)))
					return; // value hasn't changed.

				// wipe the last list of sites we added from our known repositories!
				wipeOutLastAddedUpdateSites();

				if (updateURL == null || updateURL.trim().length() == 0)
					return;
				// Parse the policy.xml file and add all the update sites as repositories!
				Document doc = getPolicyXMLDocument(updateURL);
				List<String> addedSites = new ArrayList<String>();
				if (doc != null)
				{
					NodeList urlMaps = doc.getElementsByTagName("url-map"); //$NON-NLS-1$
					for (int i = 0; i < urlMaps.getLength(); i++)
					{
						Element urlMap = (Element) urlMaps.item(i);
						String updateSite = urlMap.getAttribute("url"); //$NON-NLS-1$
						try
						{
							URL updateSiteURL = new URL(updateSite);
							if (getPluginManager().addUpdateSite(updateSiteURL))
							{
								addedSites.add(updateSite);
							}
						}
						catch (Exception e)
						{
							IdeLog.logError(Activator.getDefault(), e.getMessage(), e);
						}
					}
				}
				// Store the added sites in a pref value so when user changes settings we can wipe the last group
				// of added ones...
				storeAddedUpdateSites(addedSites);
			}

			private void wipeOutLastAddedUpdateSites()
			{
				String addedSitesRaw = Platform.getPreferencesService().getString(Activator.PLUGIN_ID,
						ADDED_UPDATE_SITES, "", //$NON-NLS-1$
						null);
				String[] lastAdded = addedSitesRaw.split(DELIMETER);
				// TODO Load the list of URLs added from the prefs
				try
				{
					for (String oldAddedURL : lastAdded)
					{
						if (oldAddedURL == null || oldAddedURL.trim().length() == 0)
							continue;
						URL location = new URL(oldAddedURL);
						getPluginManager().removeUpdateSite(location);
					}
				}
				catch (Exception e)
				{
					IdeLog.logError(Activator.getDefault(), e.getMessage(), e);
				}
			}

			private void storeAddedUpdateSites(List<String> addedSites)
			{
				StringBuilder builder = new StringBuilder();
				for (String addedSite : addedSites)
				{
					builder.append(DELIMETER);
					builder.append(addedSite);
				}
				if (builder.length() > 0)
					builder.deleteCharAt(0);
				(new InstanceScope()).getNode(Activator.PLUGIN_ID).put(ADDED_UPDATE_SITES, builder.toString());
			}

			private Document getPolicyXMLDocument(final String updateURL)
			{
				InputStream stream = null;
				Document doc = null;
				try
				{
					stream = new URL(updateURL).openStream();
					doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
				}
				catch (Exception e)
				{
					IdeLog.logError(Activator.getDefault(), e.getMessage(), e);
				}
				finally
				{
					try
					{
						if (stream != null)
							stream.close();
					}
					catch (IOException e)
					{
						// ignore
					}
				}
				return doc;
			}

		};
		job.setSystem(true);
		job.setPriority(Job.LONG);
		job.schedule();
	}

	public static void savePortalUpdateType()
	{
		// updates the corresponding type for the portal update structure since
		// it knows the server.jetty plugin, not vise-versa
		// the default is production
		String portalUpdateType = com.aptana.ide.server.jetty.preferences.IPreferenceConstants.PORTAL_UPDATE_RELEASE;

		String updateUrl = Platform.getPreferencesService().getString(Activator.PLUGIN_ID, P_UPDATE_POLICY_URL,
				"", null); //$NON-NLS-1$
		if (updateUrl.equals(POLICY_XML_RC))
		{
			// next
			portalUpdateType = com.aptana.ide.server.jetty.preferences.IPreferenceConstants.PORTAL_UPDATE_NEXT;
		}
		else if (updateUrl.equals(POLICY_XML_NIGHTLY))
		{
			// nightly
			portalUpdateType = com.aptana.ide.server.jetty.preferences.IPreferenceConstants.PORTAL_UPDATE_NIGHTLY;
		}
		else if (updateUrl.startsWith(POLICY_XML_DEV_START))
		{
			// retrieves the branch name
			int length = POLICY_XML_DEV_START.length();
			int lastSlash = updateUrl.indexOf('/', length);
			portalUpdateType = updateUrl.substring(length, lastSlash);
		}

		JettyPlugin.getDefault().getPreferenceStore().setValue(
				com.aptana.ide.server.jetty.preferences.IPreferenceConstants.PORTAL_UPDATE_TYPE, portalUpdateType);
	}

	private static IPluginManager getPluginManager()
	{
		return Activator.getDefault().getPluginManager();
	}
}
