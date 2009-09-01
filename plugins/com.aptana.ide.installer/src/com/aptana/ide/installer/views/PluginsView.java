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
package com.aptana.ide.installer.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.PreferenceUtils;
import com.aptana.ide.core.ui.widgets.TableViewerSorter;
import com.aptana.ide.installer.Activator;
import com.aptana.ide.update.manager.IPlugin;
import com.aptana.ide.update.manager.IPluginManager;
import com.aptana.ide.update.manager.Plugin;
import com.aptana.ide.update.manager.PluginListener;

/**
 * @author cwilliams
 * @author Ingo Muschenetz
 */
public class PluginsView extends ViewPart implements ISelectionProvider, ISelectionChangedListener
{

	private static final String[] INSTALLED_ACTION_IDS = new String[] {
			"com.aptana.ide.installer.plugins_view_context", //$NON-NLS-1$
			"com.aptana.ide.installer.actions.RemoveFeatureAction", //$NON-NLS-1$
			"com.aptana.ide.installer.actions.InstallFeatureAction", //$NON-NLS-1$
			"com.aptana.ide.installer.views.ToggleLatestFeatureAction" }; //$NON-NLS-1$
	private static final String NAME = PluginMessages.PluginsView_Name;
	private static final String VERSION = PluginMessages.PluginsView_Version;
	private static final String RELEASE_DATE = PluginMessages.PluginsView_ReleaseDate;
	private static final String DESCRIPTION = PluginMessages.PluginsView_Description;

	private TableViewer installedPluginsTableViewer;
	private Set<ISelectionChangedListener> listeners;
	private ISelection selection;
	private LatestFeatureFilter latestFilter;
	private boolean latestFilterOn;
	private PluginListener pluginsListener;

	/**
	 * 
	 */
	public PluginsView()
	{
		listeners = new HashSet<ISelectionChangedListener>();
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
		// create main container
		Composite composite = createComposite(parent);
		createInstalledPluginsTable(composite);

		getSite().setSelectionProvider(this);
		createPopupMenu(installedPluginsTableViewer, INSTALLED_ACTION_IDS);
		pluginsListener = new PluginListener()
		{

			public void pluginUninstalled()
			{
				refreshInstalledPlugins();
			}

			public void pluginInstalled()
			{
				refreshInstalledPlugins();
			}

			public void pluginDisabled()
			{
				refreshInstalledPlugins();
			}

			public void remotePluginsRefreshed()
			{
			}

			public void pluginEnabled()
			{
				refreshInstalledPlugins();
			}

		};
		getPluginManager().addListener(pluginsListener);

		PreferenceUtils.registerBackgroundColorPreference(installedPluginsTableViewer.getControl(),
				"com.aptana.ide.core.ui.background.color.pluginsView"); //$NON-NLS-1$
		PreferenceUtils.registerForegroundColorPreference(installedPluginsTableViewer.getControl(),
				"com.aptana.ide.core.ui.foreground.color.pluginsView"); //$NON-NLS-1$
	}

	private IPluginManager getPluginManager()
	{
		return Activator.getDefault().getPluginManager();
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	public void dispose()
	{
		try
		{
			getPluginManager().removeListener(pluginsListener);
		}
		finally
		{
			super.dispose();
		}
	}

	/**
	 * Creates and registers the context menu
	 */
	private void createPopupMenu(TableViewer table, String[] ids)
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);

		menuMgr.addMenuListener(new MyMenuListener(getViewSite(), ids));

		Menu menu = menuMgr.createContextMenu(table.getControl());
		table.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, table);
	}

	private static final class MyMenuListener implements IMenuListener
	{

		private String[] ids;
		private IViewSite fViewSite;

		/**
		 * @param viewSite
		 * @param strings
		 */
		public MyMenuListener(IViewSite viewSite, String[] strings)
		{
			this.fViewSite = viewSite;
			this.ids = strings;
		}

		/**
		 * @see org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
		 */
		public void menuAboutToShow(IMenuManager manager)
		{
			IContributionItem[] items = fViewSite.getActionBars().getToolBarManager().getItems();
			for (int i = 0; i < items.length; i++)
			{
				if (items[i] instanceof ActionContributionItem)
				{
					ActionContributionItem aci = (ActionContributionItem) items[i];
					if (filtered(aci.getId()))
						continue;
					manager.add(aci.getAction());
				}
			}
			manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		}

		private boolean filtered(String id)
		{
			for (int i = 0; i < ids.length; i++)
			{
				if (id.equals(ids[i]))
					return false;
			}
			return true;
		}

	}

	/**
	 * createComposite
	 * 
	 * @param parent
	 * @return Composite
	 */
	private Composite createComposite(Composite parent)
	{
		Composite result = new Composite(parent, SWT.NONE);
		GridLayout contentAreaLayout = new GridLayout();
		contentAreaLayout.marginHeight = 0;
		contentAreaLayout.marginWidth = 0;
		contentAreaLayout.verticalSpacing = 0;
		contentAreaLayout.horizontalSpacing = 0;
		result.setLayout(contentAreaLayout);
		result.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		return result;
	}

	private void createInstalledPluginsTable(Composite parent)
	{
		installedPluginsTableViewer = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION);
		Table pluginsTable = installedPluginsTableViewer.getTable();
		pluginsTable.setHeaderVisible(true);
		pluginsTable.setLinesVisible(false);
		pluginsTable.setLayoutData(new GridData(GridData.FILL_BOTH));

		createColumn(pluginsTable, "", 32); // the image column //$NON-NLS-1$
		createColumn(pluginsTable, NAME, 200);
		createColumn(pluginsTable, VERSION, 75);
		createColumn(pluginsTable, RELEASE_DATE, 100);
		createColumn(pluginsTable, DESCRIPTION, 275);

		installedPluginsTableViewer.setContentProvider(new PluginContentProvider());
		installedPluginsTableViewer.setLabelProvider(new InstalledPluginsLabelProvider(pluginsTable.getDisplay()));
		TableViewerSorter.bind(installedPluginsTableViewer);
		installedPluginsTableViewer.addSelectionChangedListener(this);
		installedPluginsTableViewer.addFilter(new InstalledPluginsViewerFilter());

		refreshInstalledPlugins();

		PreferenceUtils.persist(Activator.getDefault().getPreferenceStore(), pluginsTable, "installedPlugins"); //$NON-NLS-1$
	}

	private void refreshInstalledPlugins()
	{
		Job fetchJob = new Job("Fetching installed plugins") //$NON-NLS-1$
		{

			protected IStatus run(IProgressMonitor monitor)
			{
				List<IPlugin> plugins = getPluginManager().getInstalledPlugins();
				// make a copy of unmodifiable list so we can sort it
				final List<IPlugin> sorted = new ArrayList<IPlugin>(plugins);
				// adds the information from the plugins.xml whenever possible
				List<Plugin> remotePlugins = getPluginManager().getRemotePlugins();
				for (Plugin remote : remotePlugins)
				{
					List<IPlugin> matchingInstalled = findMatch(remote.getId(), plugins);
					if (matchingInstalled == null || matchingInstalled.isEmpty())
						continue;
					for (IPlugin match : matchingInstalled)
					{
						Plugin newPlugin = new Plugin(remote.getId(), match.getName(), match.getVersion(), remote
								.getReleaseDate(), remote.getDescription(), remote.getURL(), remote.getMore(), remote
								.getCategory(), remote.getSortweight(), remote.getImagePath(), remote
								.getRequiredPlugins(), remote.getInstallerCategory());
						sorted.remove(match);
						sorted.add(newPlugin);
					}

				}
				Collections.sort(sorted, new Comparator<IPlugin>()
				{
					public int compare(IPlugin o1, IPlugin o2)
					{
						return o1.getName().compareTo(o2.getName());
					}
				});

				CoreUIUtils.getDisplay().asyncExec(new Runnable()
				{
					public void run()
					{
						installedPluginsTableViewer.setInput(sorted);
					}

				});
				return Status.OK_STATUS;
			}

			private List<IPlugin> findMatch(String id, List<IPlugin> plugins)
			{
				// TODO Search in a more efficient way than iteration!
				List<IPlugin> matches = new ArrayList<IPlugin>();
				for (IPlugin plugin : plugins)
				{
					if (plugin.getId().equals(id))
						matches.add(plugin);
				}
				return matches;
			}

		};
		fetchJob.setPriority(Job.BUILD);
		fetchJob.setSystem(true);
		fetchJob.schedule();
	}

	private static void createColumn(Table pluginsTable, String text, int width)
	{
		TableColumn nameColumn = new TableColumn(pluginsTable, SWT.LEFT);
		nameColumn.setText(text);
		nameColumn.setWidth(width);
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus()
	{
		// do nothing
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		listeners.add(listener);
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection()
	{
		return selection;
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		listeners.remove(listener);
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection)
	{
		this.selection = selection;
		for (ISelectionChangedListener listener : listeners)
		{
			listener.selectionChanged(new SelectionChangedEvent(this, selection));
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event)
	{
		setSelection(event.getSelection());
	}

	/**
	 * 
	 */
	public void toggleLatestFeatureFilter()
	{
		if (latestFilter == null)
			latestFilter = new LatestFeatureFilter();
		if (latestFilterOn)
		{
			installedPluginsTableViewer.removeFilter(latestFilter);
		}
		else
		{
			installedPluginsTableViewer.addFilter(latestFilter);
		}
		latestFilterOn = !latestFilterOn;
	}

}
