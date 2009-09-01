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
package com.aptana.ide.server.ui.views;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.PreferenceUtils;
import com.aptana.ide.core.ui.widgets.ToolbarTooltip;
import com.aptana.ide.core.ui.widgets.TreeViewerSorter;
import com.aptana.ide.server.ServerCore;
import com.aptana.ide.server.core.IAbstractConfiguration;
import com.aptana.ide.server.core.IServer;
import com.aptana.ide.server.core.IServerManagerListener;
import com.aptana.ide.server.core.ServerManagerEvent;
import com.aptana.ide.server.core.impl.Configuration;
import com.aptana.ide.server.core.impl.servers.ServerManager;
import com.aptana.ide.server.ui.IConfigurationDialog;
import com.aptana.ide.server.ui.ServerDialogPageRegistry;
import com.aptana.ide.server.ui.ServerUIPlugin;
import com.aptana.ide.server.ui.preferences.IPreferenceConstants;
import com.aptana.ide.server.ui.views.actions.GenericAddServerAction;
import com.aptana.ide.server.ui.views.actions.ICanEdit;
import com.aptana.ide.server.ui.views.actions.OpenConsole;
import com.aptana.ide.server.ui.views.actions.OpenLogAction;
import com.aptana.ide.server.ui.views.actions.OpenStatisticsAction;
import com.aptana.ide.server.ui.views.actions.RestartServerAction;
import com.aptana.ide.server.ui.views.actions.StartServerAction;
import com.aptana.ide.server.ui.views.actions.StopServerAction;
import com.aptana.ide.server.ui.views.actions.SuspendServerAction;

/**
 * This view displays the existing web servers in the workspace. Users can start, stop and edit the servers from this
 * view.
 * 
 * @author Pavel Petrochenko
 */
public class GenericServersView extends ViewPart
{

	/**
	 * @author Pavel Petrochenko
	 */
	private final class DeleteServerConfirmDialog extends MessageDialog
	{
		private Button stopServer;

		private boolean shouldStop;

		private boolean mayStop;

		private boolean askStopBeforeDelete;

		private DeleteServerConfirmDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage,
				String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex,
				boolean mayStop, boolean askStopBeforeDelete)
		{
			super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
					defaultIndex);
			this.mayStop = mayStop;
			this.askStopBeforeDelete = askStopBeforeDelete;
		}

		/**
		 * @see org.eclipse.jface.dialogs.MessageDialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
		 */
		@Override
		protected void createButtonsForButtonBar(Composite parent)
		{
			if (askStopBeforeDelete) {
				GridLayout ll = (GridLayout) parent.getLayout();

				ll.numColumns++;
				ll.makeColumnsEqualWidth = false;

				GridData layoutData = (GridData) parent.getLayoutData();
				layoutData.grabExcessHorizontalSpace = true;
				layoutData.horizontalAlignment = SWT.FILL;
				stopServer = new Button(parent, SWT.CHECK);
				stopServer.setText(Messages.GenericServersView_STOP_SERVER_BEFORE_DELETING);
				stopServer.setEnabled(mayStop);
				stopServer.setSelection(mayStop);
				stopServer.addSelectionListener(new SelectionAdapter()
				{

					public void widgetSelected(SelectionEvent e)
					{
						shouldStop = stopServer.getSelection();
					}

				});
				GridData gridData = new GridData();
				gridData.horizontalAlignment = SWT.LEFT;
				gridData.grabExcessHorizontalSpace = true;
				gridData.horizontalIndent = 5;
				stopServer.setLayoutData(gridData);
			}
			super.createButtonsForButtonBar(parent);
		}
	}

	private static final int MAX_SHOWN_SERVER_NAME = 15;

	/**
	 * ID
	 */
	public static final String ID = "com.aptana.ide.server.ui.serversView"; //$NON-NLS-1$

	/**
	 * 
	 */
	private static final String ELLIPSIS = "..."; //$NON-NLS-1$

	private TreeViewer serverViewer;

	private Action startAction;
	private IContributionItem debugActionItem;
	private IContributionItem profileActionItem;
	private Action debugAction;
	private Action profileAction;
	private Action stopAction;
	private Action restartAction;
	private Action collapseAction;
	private Action expandAction;

	private IServerManagerListener externalServerRegistryListener;

	private SuspendServerAction pauseAction;

	private OpenLogAction openLog;

	private OpenStatisticsAction openStats;

	/**
	 * Constructor.
	 */
	public GenericServersView()
	{
		super();
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose()
	{
		ServerCore.getServerManager().removeServerManagerListener(externalServerRegistryListener);
		super.dispose();
	}

	/**
	 * @return selection
	 */
	public ISelection getSelection()
	{
		return serverViewer.getSelection();
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		parent.setLayout(gridLayout);
		createTableViewer(parent);
		// PreferenceUtils.registerBackgroundColorPreference(serverViewer.getControl(),
		// "com.aptana.ide.core.ui.background.color.serversView"); //$NON-NLS-1$
		createPopupMenu();
		createToolbar();

		ToolBarManager toolBarManager = (ToolBarManager) getViewSite().getActionBars().getToolBarManager();
		final Image smallGlobe = ServerUIPlugin.getImageDescriptor("icons/server/small_globe.png").createImage(); //$NON-NLS-1$
		final Image jaxerImage = ServerUIPlugin.getImageDescriptor("icons/server/jaxer_decorator.gif").createImage(); //$NON-NLS-1$
		final ToolBar control = toolBarManager.getControl();

		final ToolbarTooltip toolbarTooltip = new ToolbarTooltip(control,
				"com.aptana.ide.server.ui.servers_context_view") { //$NON-NLS-1$

			@Override
			protected Composite createToolTipContentArea(Event event, Composite parent)
			{
				Composite sm = new Composite(parent, SWT.NONE);
				sm.setLayout(GridLayoutFactory.fillDefaults().margins(10, 10).create());
				FormText ts = new FormText(sm, SWT.NONE);
				ts.setImage("jaxer", jaxerImage); //$NON-NLS-1$
				ts.setImage("web", smallGlobe); //$NON-NLS-1$
				ts.setText(Messages.GenericServersView_LEGEND, true, false);
				ts.setLayoutData(new GridData(320, 75));
				return sm;
			}

		};
		this.serverViewer.getControl().addDisposeListener(new DisposeListener()
		{

			public void widgetDisposed(DisposeEvent e)
			{
				smallGlobe.dispose();
				jaxerImage.dispose();
				toolbarTooltip.deactivate();
			}

		});

		TreeViewerSorter.bind(serverViewer);

	}

	/**
	 * Helper method to create the table viewer.
	 * 
	 * @param parent
	 */
	private void createTableViewer(Composite parent)
	{
		serverViewer = new TreeViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION);
		serverViewer.getTree().setLinesVisible(true);
		serverViewer.addTreeListener(new ITreeViewerListener()
		{

			public void treeExpanded(TreeExpansionEvent event)
			{
				Object server = event.getElement();
				if (server instanceof IServer)
				{
					ServerUIPlugin.getDefault().getPreferenceStore().setValue(
							IPreferenceConstants.COLLAPSE_SERVER + "." + ((IServer) server).getId(), false); //$NON-NLS-1$
				}
				updateCollapseAllAction();
			}

			public void treeCollapsed(TreeExpansionEvent event)
			{
				Object server = event.getElement();
				if (server instanceof IServer)
				{
					ServerUIPlugin.getDefault().getPreferenceStore().setValue(
							IPreferenceConstants.COLLAPSE_SERVER + "." + ((IServer) server).getId(), true); //$NON-NLS-1$
				}
				updateCollapseAllAction();
			}

		});
		PreferenceUtils.registerBackgroundColorPreference(serverViewer.getControl(),
				"com.aptana.ide.core.ui.background.color.serversView"); //$NON-NLS-1$
		PreferenceUtils.registerForegroundColorPreference(serverViewer.getControl(),
				"com.aptana.ide.core.ui.foreground.color.serversView"); //$NON-NLS-1$
		createTree();
	}

	private void updateStartActions()
	{
		boolean canDebug = false;
		boolean canProfile = false;
		IServer[] servers = ServerManager.getInstance().getServers();
		if (debugActionItem != null)
		{
			for (int i = 0; i < servers.length; i++)
			{
				IStatus status = servers[i].canStart("debug"); //$NON-NLS-1$
				if (status != null && status.isOK())
				{
					canDebug = true;
					break;
				}
			}
			debugActionItem.setVisible(canDebug);
		}
		if (profileActionItem != null)
		{
			for (int i = 0; i < servers.length; i++)
			{
				IStatus status = servers[i].canStart("profile"); //$NON-NLS-1$
				if (status != null && status.isOK())
				{
					canProfile = true;
					break;
				}
			}
			profileActionItem.setVisible(canProfile);
		}
	}
	
	private void updateCollapseAllAction()
	{
		UIJob updateActionState = new UIJob("") {
			public IStatus runInUIThread(IProgressMonitor monitor) {
				if (collapseAction != null) {
					collapseAction.setEnabled(serverViewer.getExpandedElements().length > 0);
				}
				return Status.OK_STATUS;
			}
		};
		updateActionState.setPriority(UIJob.INTERACTIVE);
		updateActionState.setSystem(true);
		updateActionState.schedule(10);
	}
	
	private void updateExpandAllAction()
	{
		UIJob updateActionState = new UIJob("") {
			public IStatus runInUIThread(IProgressMonitor monitor) {
				if (expandAction != null) {
					IStructuredContentProvider contentProvider = (IStructuredContentProvider) serverViewer.getContentProvider();
					Object[] elements = contentProvider.getElements(serverViewer.getInput());
					boolean atleastOneExpandable = false;
					for (Object element : elements) {
						if (serverViewer.isExpandable(element)) {
							atleastOneExpandable = true;
							break;
						}
					}
					expandAction.setEnabled(atleastOneExpandable);
				}
				return Status.OK_STATUS;
			}
		};
		updateActionState.setPriority(UIJob.INTERACTIVE);
		updateActionState.setSystem(true);
		updateActionState.schedule(10);
	}

	private void createToolbar()
	{

		final IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();

		toolBarManager.add(new Separator());
		this.startAction = new StartServerAction(serverViewer, "run"); //$NON-NLS-1$
		this.debugAction = new StartServerAction(serverViewer, "debug"); //$NON-NLS-1$
		this.profileAction = new StartServerAction(serverViewer, "profile"); //$NON-NLS-1$
		debugActionItem = new ActionContributionItem(this.debugAction);
		profileActionItem = new ActionContributionItem(this.profileAction);

		this.restartAction = new RestartServerAction(serverViewer);
		this.pauseAction = new SuspendServerAction(serverViewer);
		this.stopAction = new StopServerAction(serverViewer);
		this.openLog = new OpenLogAction(serverViewer);
		this.openStats = new OpenStatisticsAction(serverViewer);
		this.collapseAction = new Action()
		{

			public void run()
			{
				serverViewer.collapseAll();
				updateCollapseAllAction();
			}

		};
		this.collapseAction.setImageDescriptor(CoreUIPlugin.getImageDescriptor("icons/collapse.gif")); //$NON-NLS-1$
		this.collapseAction.setToolTipText(Messages.GenericServersView_TTP_Collapse);
		this.expandAction = new Action()
		{

			public void run()
			{
				serverViewer.expandAll();
				updateCollapseAllAction();
			}

		};
		this.expandAction.setImageDescriptor(CoreUIPlugin.getImageDescriptor("icons/expand.gif")); //$NON-NLS-1$
		this.expandAction.setToolTipText(Messages.GenericServersView_TTP_Expand);
		toolBarManager.add(new GenericAddServerAction());
		toolBarManager.add(this.startAction);
		toolBarManager.add(debugActionItem);
		toolBarManager.add(profileActionItem);
		toolBarManager.add(this.restartAction);
		toolBarManager.add(this.pauseAction);
		toolBarManager.add(this.stopAction);
		serverViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				updateToolbar(toolBarManager);
			}

		});
		externalServerRegistryListener = new IServerManagerListener()
		{

			public void serversChanged(final ServerManagerEvent event)
			{
				Display.getDefault().asyncExec(new Runnable()
				{

					public void run()
					{
						if (serverViewer.getControl().isDisposed())
						{
							return;
						}
						if (event.getKind() == ServerManagerEvent.KIND_CHANGED)
						{
							serverViewer.refresh(event.getServer(), true);
						}
						else
						{
							serverViewer.refresh(true);
							if (event.getKind() == ServerManagerEvent.KIND_ADDED)
							{
								if (!ServerUIPlugin.getDefault().getPreferenceStore().getBoolean(
										IPreferenceConstants.COLLAPSE_SERVER + "." + event.getServer().getId())) //$NON-NLS-1$
								{
									serverViewer.expandToLevel(event.getServer(), 2);
								}
							}
						}
						updateToolbar(toolBarManager);
					}

				});
			}

		};
		ServerCore.getServerManager().addServerManagerListener(externalServerRegistryListener);
		startAction.setEnabled(false);
		restartAction.setEnabled(false);
		stopAction.setEnabled(false);
		toolBarManager.add(new Separator());
		openConsole = new OpenConsole(serverViewer);
		toolBarManager.add(new Separator("helpEnd")); //$NON-NLS-1$
		toolBarManager.add(openConsole);
		toolBarManager.add(openLog);
		toolBarManager.add(openStats);
		toolBarManager.add(new Separator());
		toolBarManager.add(collapseAction);
		toolBarManager.add(expandAction);
		updateStartActions();
		updateToolbar(toolBarManager);
	}

	private void updateToolbar(final IToolBarManager toolBarManager)
	{
		IStructuredSelection selection = (IStructuredSelection) serverViewer.getSelection();
		this.pauseAction.selectionChanged(new SelectionChangedEvent(this.serverViewer, selection));
		this.openLog.selectionChanged(new SelectionChangedEvent(this.serverViewer, selection));
		if (selection.isEmpty())
		{
			startAction.setEnabled(false);
			debugAction.setEnabled(false);
			profileAction.setEnabled(false);
			restartAction.setEnabled(false);
			stopAction.setEnabled(false);
		}
		else
		{
			IServer server = (IServer) selection.getFirstElement();
			// boolean isStopped = server.getServerState() == IServer.STATE_STOPPED;
			IStatus canStart0 = server.canStart("run");//$NON-NLS-1$
			startAction.setEnabled(canStart0.isOK());
			if (!canStart0.isOK())
			{
				startAction.setToolTipText(canStart0.getMessage());
			}
			else
			{
				startAction.setToolTipText(Messages.ServersView_START);
			}
			IStatus canStart = server.canStart("debug");//$NON-NLS-1$
			debugAction.setEnabled(canStart.isOK());
			if (!canStart.isOK())
			{
				debugAction.setToolTipText(canStart.getMessage());
			}
			else
			{
				debugAction.setToolTipText(Messages.ServersView_DEBUG);
			}
			IStatus canStart2 = server.canStart("profile"); //$NON-NLS-1$
			profileAction.setEnabled(canStart2.isOK());
			if (!canStart2.isOK())
			{
				profileAction.setToolTipText(canStart2.getMessage());
			}
			else
			{
				profileAction.setToolTipText(Messages.ServersView_PROFILE);
			}
			restartAction.setEnabled(server.canRestart(server.getMode()).getSeverity() == IStatus.OK);
			stopAction.setEnabled(server.canStop().getSeverity() == IStatus.OK);
			openConsole.selectionChanged(new SelectionChangedEvent(serverViewer, serverViewer.getSelection()));
		}
		updateStartActions();
		updateCollapseAllAction();
		updateExpandAllAction();
		toolBarManager.update(true);
	}

	private void createTree()
	{
		final Tree serverTable = serverViewer.getTree();
		serverTable.setHeaderVisible(true);
		serverTable.setLayoutData(new GridData(GridData.FILL_BOTH));

		TreeColumn nameColumn = new TreeColumn(serverTable, SWT.LEFT);
		nameColumn.setText(Messages.ServersView_NAME);
		nameColumn.setWidth(200);

		TreeColumn statusColumn = new TreeColumn(serverTable, SWT.LEFT);
		statusColumn.setText(Messages.ServersView_STATUS);
		statusColumn.setWidth(100);

		TreeColumn projectColumn = new TreeColumn(serverTable, SWT.LEFT);
		projectColumn.setText(Messages.ServersView_DESCRIPTION);
		projectColumn.setWidth(400);

		TreeColumn typeColumn = new TreeColumn(serverTable, SWT.LEFT);
		typeColumn.setText(Messages.ServersView_TYPE);
		typeColumn.setWidth(100);

		TreeColumn hostColumn = new TreeColumn(serverTable, SWT.LEFT);
		hostColumn.setText(Messages.GenericServersView_HOST);
		hostColumn.setWidth(100);

		TreeColumn portColumn = new TreeColumn(serverTable, SWT.LEFT);
		portColumn.setText(Messages.GenericServersView_PORT);
		portColumn.setWidth(50);
		serverViewer.setLabelProvider(new ServerLabelProvider());
		serverViewer.setContentProvider(new ServerContentProvider());
		serverViewer.setInput(ServerCore.getServerManager());
		serverViewer.addDoubleClickListener(new IDoubleClickListener()
		{

			public void doubleClick(DoubleClickEvent event)
			{
				IStructuredSelection selection = (IStructuredSelection) serverViewer.getSelection();
				final IServer server = (IServer) selection.getFirstElement();
				if (server != null)
				{
					doEdit(server);
				}
			}

		});
		getSite().setSelectionProvider(serverViewer);
		ServerUIPlugin default1 = ServerUIPlugin.getDefault();
		final IPreferenceStore preferenceStore = default1.getPreferenceStore();
		PreferenceUtils.persist(preferenceStore, serverTable, "serversView"); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus()
	{
		serverViewer.getTree().setFocus();
	}

	private static IWorkbenchAction deleteAction;

	private OpenConsole openConsole;

	/**
	 * Creates and registers the context menu
	 */
	private void createPopupMenu()
	{
		deleteAction = ActionFactory.DELETE.create(getViewSite().getWorkbenchWindow());
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.DELETE.getId(), new Action()
		{

			public void run()
			{
				doDelete();
			}
		});
		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				IContributionItem[] items = getViewSite().getActionBars().getToolBarManager().getItems();
				for (int i = 0; i < items.length; i++)
				{

					if (items[i] instanceof ActionContributionItem)
					{
						ActionContributionItem aci = (ActionContributionItem) items[i];
						IAction action = aci.getAction();
						if (action == openLog)
						{
							// adds the Open Log action to the context menu as a push button instead of 
							// drop-down
							boolean enabled = action.isEnabled();
							action = new OpenLogAction(serverViewer, Action.AS_PUSH_BUTTON);
							action.setEnabled(enabled);
						}
						if (action.isEnabled() && action.getStyle() != Action.AS_DROP_DOWN_MENU)
						{
							if (action.getText() == null || action.getText().length() == 0)
							{
								action.setText(action.getToolTipText());
							}
							manager.add(action);
						}
					}
					else
					{
						if (items[i] instanceof Separator)
						{
							manager.add(new Separator());
						}
					}
				}
				manager.add(new Separator());
				IStructuredSelection selection = (IStructuredSelection) serverViewer.getSelection();
				final IServer server = (IServer) selection.getFirstElement();
				if (server != null)
				{
					deleteAction.setText(StringUtils.format(Messages.ServersView_DELETE, getShortenName(server)));
					// deleteAction.setEnabled(server.getServerState() == IServer.STATE_STOPPED);
					deleteAction.setEnabled(server.canDelete().isOK());
					manager.add(deleteAction);
					
					Action action = new Action()
					{
						public void run()
						{
							doEdit(server);
						}
					};
					action.setText(StringUtils.format(Messages.ServersView_EDIT, getShortenName(server)));
					IStatus canModify = server.canModify();
					IStatus canModifyInStoppedStateOnly = server.canModifyInStoppedStateOnly();
					action.setEnabled(
							((canModifyInStoppedStateOnly == null || canModifyInStoppedStateOnly.getCode() == IStatus.OK) ? server.getServerState() == IServer.STATE_STOPPED : true)
							&& (canModify == null || canModify.getCode() == IStatus.OK));
					manager.add(action);
				}
				// deleteAction.setEnabled(!selection.isEmpty());
				manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS)); // Allow
			}

			private String getShortenName(final IServer server)
			{
				String name = server.getName();
				int length = name.length();
				if (length > MAX_SHOWN_SERVER_NAME)
				{
					int delta = (length - 15) / 2;
					int pivot = length / 2;
					int start = pivot - delta;
					int end = pivot + delta;
					String s1 = name.substring(0, start);
					String s2 = name.substring(end, length);
					String s = s1 + ELLIPSIS + s2;
					return s;
				}
				return name;
			}
		});
		Menu menu = menuMgr.createContextMenu(serverViewer.getControl());
		serverViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, serverViewer);
	}

	private void doDelete()
	{

		IStructuredSelection selection = (IStructuredSelection) serverViewer.getSelection();
		if (selection.isEmpty())
		{
			return;
		}
		IServer server = (IServer) selection.getFirstElement();
		if (!server.canDelete().isOK())
		{
			return;
		}
		boolean mayStop = (server.getServerState() != IServer.STATE_STOPPED && server.getServerState() != IServer.STATE_UNKNOWN);
		boolean askStopBeforeDelete = (server.askStopBeforeDelete().getCode() == IStatus.OK);
		DeleteServerConfirmDialog dlg = new DeleteServerConfirmDialog(getViewSite().getShell(),
				Messages.ServersView_CONFIRM_DELETE, null, StringUtils.format(Messages.ServersView_CONFIRM_DELETE_TEXT,
						server.getName()), MessageDialog.QUESTION, new String[] { Messages.GenericServersView_YES,
						Messages.GenericServersView_NO }, 0, mayStop, askStopBeforeDelete);

		int openConfirm = dlg.open();
		if (openConfirm != 0)
		{
			return;
		}
		boolean doStop = dlg.shouldStop;
		if (doStop)
		{
			server.stop(true, null, null);
		}

		try
		{
			ServerCore.getServerManager().removeServer(server);
		}
		catch (CoreException e)
		{
			MessageDialog.openError(getViewSite().getShell(), Messages.ServersView_CONFIRM_DELETE, StringUtils.format(
					Messages.ServersView_CONFIRM_DELETE_TEXT, server.getName()));
		}
	}

	private void doEdit(final IServer server)
	{
		// if (server.getServerState() != IServer.STATE_STOPPED && server.getServerState() != IServer.STATE_UNKNOWN)
		// {
		// MessageDialog.openInformation(serverViewer.getControl().getShell(),
		// Messages.GenericServersView_ServerReadOnly_TITLE,
		// Messages.GenericServersView_ServerReadOnlyDescription);
		// return;
		// }

		IStatus canModify = server.canModify();
		if (canModify != null && canModify.getCode() != IStatus.OK)
		{
			MessageDialog.openInformation(serverViewer.getControl().getShell(),
					Messages.GenericServersView_READONLY_TITLE, Messages.GenericServersView_READONLY_DESCRIPTION);
			return;
		}

		ICanEdit adapter = (ICanEdit) server.getAdapter(ICanEdit.class);
		if (adapter != null)
		{
			adapter.doEdit();
			return;
		}
		IConfigurationDialog dialog = ServerDialogPageRegistry.getInstance().getDialog(server.getServerType().getId());
		Configuration cf = new Configuration();
		server.storeConfiguration(cf);
		dialog.setConfiguration(cf);
		dialog.setServer(server);
		dialog.setEdit(true);

		int open = dialog.getDialog().open();
		if (open == Dialog.OK)
		{
			IAbstractConfiguration configuration = dialog.getConfiguration();
			// configuration.setStringAttribute(IServer.KEY_ID,ServerManager.getFreeId());
			configuration.setStringAttribute(IServer.KEY_TYPE, server.getServerType().getId());
			try
			{
				server.reconfigure(configuration);
			}
			catch (CoreException e)
			{
				MessageDialog.openError(Display.getCurrent().getActiveShell(),
						Messages.GenericServersView_ERROR_WHILE_EDITING_DESCRIPTION, e.getMessage());
				IdeLog.log(ServerUIPlugin.getDefault(), IStatus.ERROR,
						Messages.GenericServersView_ERROR_WHILE_EDITING_TITLE, e);
			}
		}
		IStructuredSelection selection = (IStructuredSelection) serverViewer.getSelection();
		this.pauseAction.selectionChanged(new SelectionChangedEvent(this.serverViewer, selection));
	}

	/**
	 * @param srv
	 */
	public void select(IServer srv)
	{
		serverViewer.setSelection(new StructuredSelection(srv), true);
	}

}
