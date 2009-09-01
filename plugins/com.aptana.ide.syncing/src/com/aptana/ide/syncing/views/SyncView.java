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
package com.aptana.ide.syncing.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

import com.aptana.ide.core.CoreStrings;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.IVirtualFileManager;
import com.aptana.ide.core.io.IVirtualFileManagerDialog;
import com.aptana.ide.core.io.ProtocolManager;
import com.aptana.ide.core.io.sync.ISyncManagerChangeListener;
import com.aptana.ide.core.io.sync.SyncManager;
import com.aptana.ide.core.io.sync.VirtualFileManagerSyncPair;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.ImageUtils;
import com.aptana.ide.core.ui.PreferenceUtils;
import com.aptana.ide.core.ui.actions.CheckBoxAction;
import com.aptana.ide.syncing.SyncInfoDialog;
import com.aptana.ide.syncing.SyncingPlugin;

/**
 * @author Paul Colton
 */
public class SyncView extends ViewPart implements ISyncManagerChangeListener
{
	static final String INFO_MESSAGE = Messages.SyncView_SyncViewInfo;

	/**
	 * View context id.
	 */
	private static final String VIEW_CONTEXT_ID = "com.aptana.core.ui.sync.context"; //$NON-NLS-1$

	private static ImageDescriptor fNewFolderIconDescriptor = SyncingPlugin
			.getImageDescriptor("icons/sync_connection_new.gif"); //$NON-NLS-1$
	private static Image fSiteImage = SyncingPlugin.getImageDescriptor("icons/sync_connection.gif").createImage(); //$NON-NLS-1$
	private static Image fSiteErrorImage = SyncingPlugin.getImageDescriptor("icons/error.png").createImage(); //$NON-NLS-1$
	private static ImageDescriptor fRefreshIconDescriptor = ImageUtils.getImageDescriptor("icons/refresh.gif"); //$NON-NLS-1$
	private static ImageDescriptor fShowLocalIconDescriptor = SyncingPlugin
			.getImageDescriptor("icons/show_local_connections.gif"); //$NON-NLS-1$
	private static ImageDescriptor fShowRemoteIconDescriptor = SyncingPlugin
			.getImageDescriptor("icons/show_remote_connections.gif"); //$NON-NLS-1$

	private TableViewer _connectionTableViewer;
	private ArrayList<Object> _items = new ArrayList<Object>();

	private Action actionAdd;
	private Action actionEdit;
	private Action actionDelete;
	private Action actionDoubleClick;
	private Action actionSync;

	/**
	 * shell
	 */
	protected Shell shell;

	private IAction actionRefresh;

	private TableColumn locationCol;

	private TableColumn imageCol;

	private ConnectionContentProvider connectionContentProvider;

	private Action actionShowHideLocal;

	private Action actionShowHideRemote;

	/**
	 * The constructor.
	 */
	public SyncView()
	{
		_items.addAll(Arrays.asList(SyncManager.getSyncManager().getItems()));
		SyncManager.getSyncManager().addSyncManagerChangeEvent(this);
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose()
	{
		SyncManager.getSyncManager().removeSyncManagerChangeEvent(this);
		super.dispose();
	}

	/**
	 * Returns a reference to the static connection table
	 * 
	 * @return TableViewer
	 */
	private TableViewer getConnectionTable()
	{
		return _connectionTableViewer;
	}

	/**
	 * @param obj
	 * @param actionId
	 */
	public void syncManagerEvent(final Object obj, final int actionId)
	{
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				if (actionId == ISyncManagerChangeListener.ADD)
				{
					_items.add(obj);
				}
				else if (actionId == ISyncManagerChangeListener.DELETE)
				{
					_items.remove(obj);
				}

				try
				{
					TableViewer tv = getConnectionTable();
					if (!tv.getControl().isDisposed())
					{
						tv.refresh();
					}
				}
				catch (Exception ex)
				{
					IdeLog.logError(SyncingPlugin.getDefault(), Messages.SyncView_ErrorRefreshingSyncView, ex);
				}

				imageCol.pack();
				locationCol.pack();
			}
		});
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 * 
	 * @param parent
	 */
	public void createPartControl(Composite parent)
	{
		shell = parent.getShell();
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		parent.setLayout(layout);
		if (_connectionTableViewer == null)
		{
			_connectionTableViewer = createConnectionTable(parent);
			PreferenceUtils.registerBackgroundColorPreference(_connectionTableViewer.getControl(),
					"com.aptana.ide.core.ui.background.color.syncingView"); //$NON-NLS-1$
			PreferenceUtils.registerForegroundColorPreference(_connectionTableViewer.getControl(),
					"com.aptana.ide.core.ui.foreground.color.syncingView"); //$NON-NLS-1$
		}

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		activateContext();
	}

	private TableViewer createConnectionTable(Composite parent)
	{
		TableViewer view = new TableViewer(parent, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
		Table serverTable = view.getTable();
		serverTable.setLayoutData(new GridData(GridData.FILL_BOTH));

		imageCol = new TableColumn(serverTable, SWT.LEFT);
		imageCol.setWidth(50);

		locationCol = new TableColumn(serverTable, SWT.LEFT);
		locationCol.setWidth(100);

		connectionContentProvider = new ConnectionContentProvider();

		view.setContentProvider(connectionContentProvider);
		view.setLabelProvider(new ConnectionLabelProvider());

		view.setInput(_items);
		view.setSorter(new SyncViewSorter());
		imageCol.pack();
		locationCol.pack();
		PreferenceUtils.persist(SyncingPlugin.getDefault().getPreferenceStore(), serverTable, "syncView"); //$NON-NLS-1$
		return view;
	}

	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				ISelection selection = getConnectionTable().getSelection();
				Object firstElement = ((IStructuredSelection) selection).getFirstElement();
				fillContextMenu(manager, firstElement);
			}
		});
		Menu menu = menuMgr.createContextMenu(getConnectionTable().getControl());
		getConnectionTable().getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, getConnectionTable());
	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(actionAdd);
		manager.add(actionDelete);
		manager.add(new Separator());
		manager.add(actionShowHideLocal);
		manager.add(actionShowHideRemote);
		manager.add(new Separator());
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillContextMenu(IMenuManager manager, Object element)
	{
		if (element instanceof VirtualFileManagerSyncPair)
		{
			manager.add(actionSync);
			manager.add(new Separator());
		}

		manager.add(actionAdd);
		manager.add(actionRefresh);

		manager.add(new Separator());
		manager.add(actionDelete);
		manager.add(new Separator());

		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		manager.add(new Separator());
		if (element instanceof IVirtualFileManager)
		{
			if (((IVirtualFileManager) element).isEditable())
			{
				manager.add(actionEdit);
			}
		}
		else
		{
			manager.add(actionEdit);
		}

	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(actionAdd);
		manager.add(actionDelete);
	}

	private void makeActions()
	{
		createAddAction();
		createEditAction();
		createDeleteAction();
		createDoubleClickAction();
		createSyncAction();
		createRefreshAction();
		createShowHideLocalAction();
		createShowHideRemoteAction();
	}

	private void createSyncAction()
	{
		actionSync = new Action(StringUtils.ellipsify(Messages.SyncView_Synchronize))
		{
			public void run()
			{
				startSynchronize();
			}
		};
		actionSync.setText(StringUtils.ellipsify(Messages.SyncView_Synchronize));
		actionSync.setToolTipText(Messages.SyncView_SynchronizeFiles);
	}

	private void createDoubleClickAction()
	{
		actionDoubleClick = new Action()
		{
			public void run()
			{
				startSynchronize();
			}
		};
	}

	/**
	 * Starts the synchronization dialog.
	 */
	private void startSynchronize()
	{
		ISelection selection = getConnectionTable().getSelection();
		Object element = ((IStructuredSelection) selection).getFirstElement();

		if (element instanceof VirtualFileManagerSyncPair)
		{
			VirtualFileManagerSyncPair firstElement = (VirtualFileManagerSyncPair) element;

			if (!firstElement.isValid())
			{
				actionEdit.run();
			}
			else
			{
				SmartSyncDialog dialog = new SmartSyncDialog(CoreUIUtils.getActiveShell(), firstElement
						.getSourceFileManager().getBaseFile(), firstElement.getDestinationFileManager().getBaseFile(),
						firstElement.getSourceFileManager().getDescriptiveLabel(), firstElement
								.getDestinationFileManager().getDescriptiveLabel());
				dialog.open();
			}
		}
		else if (element instanceof IVirtualFileManager)
		{
			IVirtualFileManager conf = (IVirtualFileManager) element;
			IVirtualFileManagerDialog nld = conf.getProtocolManager().createPropertyDialog(shell,
					SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);

			if (nld != null)
			{
			    nld.setItem(conf, false);
			    nld.open();
			    SyncManager.getSyncManager().fireSyncManagerChangeEvent(conf, ISyncManagerChangeListener.EDIT);
			}
		}
	}

	private void createDeleteAction()
	{
		actionDelete = new Action()
		{
			public void run()
			{
				removeFiles(getConnectionTable().getSelection());
			}
		};
		actionDelete.setText(CoreStrings.DELETE);
		actionDelete.setToolTipText(Messages.SyncView_DeleteSiteConnection);
		actionDelete.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
				ISharedImages.IMG_TOOL_DELETE));
		actionDelete.setAccelerator(SWT.DEL);
		actionDelete.setId(ActionFactory.DELETE.getId());
		getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.DELETE.getId(), actionDelete);
	}

	private void createAddAction()
	{
		actionAdd = new Action()
		{
			public void run()
			{
				// profileManager.refreshEnvironment();
				SyncInfoDialog nld = new SyncInfoDialog(shell);
				VirtualFileManagerSyncPair item = new VirtualFileManagerSyncPair();
				nld.setItem(item, true);
				nld.open();
				item = nld.getItem();
				if (item != null)
				{
					SyncManager.getSyncManager().addItem(item);
					SyncUIUtils.updateSyncLabels();
				}
			}
		};
		actionAdd.setText(Messages.SyncView_AddSiteConnection);
		actionAdd.setToolTipText(Messages.SyncView_AddSiteConnection);
		actionAdd.setImageDescriptor(fNewFolderIconDescriptor);
	}

	private void createShowHideLocalAction()
	{
		actionShowHideLocal = new CheckBoxAction(Messages.SyncView_ShowHideLocalConnections)
		{
			public void run()
			{
				connectionContentProvider.showLocalEndpoints = !connectionContentProvider.showLocalEndpoints;
				_connectionTableViewer.refresh();
			}
		};
		actionShowHideLocal.setToolTipText(Messages.SyncView_ShowHideLocalConnections);
		actionShowHideLocal.setImageDescriptor(fShowLocalIconDescriptor);
		actionShowHideLocal.setChecked(connectionContentProvider.showLocalEndpoints);
	}

	private void createShowHideRemoteAction()
	{
		actionShowHideRemote = new CheckBoxAction(Messages.SyncView_ShowHideRemoteConnections)
		{
			public void run()
			{
				connectionContentProvider.showRemoteEndpoints = !connectionContentProvider.showRemoteEndpoints;
				_connectionTableViewer.refresh();
			}
		};
		actionShowHideRemote.setToolTipText(Messages.SyncView_ShowHideRemoteConnections);
		actionShowHideRemote.setImageDescriptor(fShowRemoteIconDescriptor);
		actionShowHideRemote.setChecked(connectionContentProvider.showRemoteEndpoints);
	}

	private void createEditAction()
	{
		actionEdit = new Action()
		{
			public void run()
			{
				Object obj = getSelection(getConnectionTable().getSelection());

				if (obj != null)
				{
					if (obj instanceof VirtualFileManagerSyncPair)
					{
						VirtualFileManagerSyncPair conf = (VirtualFileManagerSyncPair) obj;
						SyncInfoDialog nld = new SyncInfoDialog(shell);
						nld.setTitle(Messages.SyncView_EditSiteConnection);
						nld.setItem(conf, false);
						nld.open();
					}
					else if (obj instanceof IVirtualFileManager)
					{
						IVirtualFileManager conf = (IVirtualFileManager) obj;
						IVirtualFileManagerDialog nld = conf.getProtocolManager().createPropertyDialog(shell,
								SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);

						nld.setItem(conf, false);
						nld.open();
						SyncManager.getSyncManager().fireSyncManagerChangeEvent(conf, ISyncManagerChangeListener.EDIT);
					}
					SyncUIUtils.updateSyncLabels();
				}
			}
		};
		actionEdit.setText(CoreStrings.PROPERTIES);
		actionEdit.setToolTipText(Messages.SyncView_EditSiteConnection);
	}

	private void createRefreshAction()
	{
		actionRefresh = new Action(CoreStrings.REFRESH)
		{
			public void run()
			{
				_connectionTableViewer.refresh();
			}
		};
		actionRefresh.setToolTipText(CoreStrings.REFRESH);
		actionRefresh.setImageDescriptor(fRefreshIconDescriptor);
	}

	/**
	 * Removes one or more files
	 * 
	 * @param selection
	 *            The currently selected files
	 */
	private void removeFiles(ISelection selection)
	{
		Object obj = getSelection(selection);
		if (obj != null && obj instanceof VirtualFileManagerSyncPair)
		{
			VirtualFileManagerSyncPair conf = (VirtualFileManagerSyncPair) obj;
			if (MessageDialog.openConfirm(getSite().getShell(), Messages.SyncView_ConfirmDelete, StringUtils.format(
					Messages.SyncView_AreYouSureYouWishToDelete, conf.getNickName())))
			{
				IVirtualFileManager fm = conf.getSourceFileManager();
				if (fm != null)
				{
					ProtocolManager pm = fm.getProtocolManager();
					pm.removeFileManager(fm);
				}

				SyncManager.getSyncManager().removeItem(conf);
				getConnectionTable().getTable().select(0);
				SyncUIUtils.updateSyncLabels();
			}
		}
		else if (obj != null && obj instanceof IVirtualFileManager)
		{
			IVirtualFileManager conf = (IVirtualFileManager) obj;

			if (MessageDialog.openConfirm(getSite().getShell(), Messages.SyncView_ConfirmDelete, StringUtils.format(
					Messages.SyncView_AreYouSureYouWishToDelete, conf.getNickName())))
			{
				conf.getProtocolManager().removeFileManager(conf);
				getConnectionTable().getTable().select(0);
			}

		}
	}

	private void hookDoubleClickAction()
	{
		getConnectionTable().addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				actionDoubleClick.run();
			}
		});
	}

	/**
	 * @see org.eclipse.ui.IViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
	 */
	public void init(IViewSite site, IMemento memento) throws PartInitException
	{
		super.init(site, memento);
	}

	/**
	 * @see org.eclipse.ui.IPersistable#saveState(org.eclipse.ui.IMemento)
	 */
	public void saveState(IMemento memento)
	{
	}

	/**
	 * @author Ingo Muschenetz
	 */
	class ConnectionContentProvider implements IStructuredContentProvider
	{
		/**
		 * showRemoteEndpoints
		 */
		protected boolean showRemoteEndpoints = true;

		/**
		 * showLocalEndpoints
		 */
		protected boolean showLocalEndpoints = false;

		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose()
		{
			// TODO Auto-generated method stub
		}

		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
			// TODO Auto-generated method stub
		}

		/**
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		@SuppressWarnings("unchecked")
		public Object[] getElements(Object inputElement)
		{
			ArrayList<Object> elements = (ArrayList<Object>) inputElement;
			ArrayList<Object> newElements = new ArrayList<Object>();
			for (Iterator<Object> iter = elements.iterator(); iter.hasNext();)
			{
				Object element = iter.next();
				if (element instanceof IVirtualFileManager)
				{
					IVirtualFileManager manager = ((IVirtualFileManager) element);
					if (manager.isTransient())
					{
						continue;
					}

					if (manager.isHidden() && showLocalEndpoints)
					{
						newElements.add(element);
					}
					else if (manager.isHidden() == false && showRemoteEndpoints)
					{
						newElements.add(element);
					}
				}
				else
				{
					newElements.add(element);
				}
			}
			return newElements.toArray();
		}
	}

	/**
	 * ConnectionLabelProvider
	 */
	class ConnectionLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex)
		{
			if (element instanceof VirtualFileManagerSyncPair)
			{
				VirtualFileManagerSyncPair sync = (VirtualFileManagerSyncPair) element;
				if (columnIndex == 0)
				{
					if (sync.isValid())
					{
						return fSiteImage;
					}
					else
					{
						return fSiteErrorImage;
					}
				}
			}
			else if (element instanceof IVirtualFileManager)
			{
				IVirtualFileManager sync = (IVirtualFileManager) element;
				if (columnIndex == 0)
				{
					return sync.getImage();
				}
			}

			return null;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex)
		{
			String name = StringUtils.EMPTY;

			if (element instanceof VirtualFileManagerSyncPair)
			{
				VirtualFileManagerSyncPair conf = ((VirtualFileManagerSyncPair) element);

				switch (columnIndex)
				{
					case 0:
						name = conf.getNickName();
						break;
					case 1:
						if (conf.isValid())
						{
							name = StringUtils.format(Messages.SyncView_SyncConnectionLocationLabel, new String[] {
									conf.getSourceFileManager().getBaseFile().getName(),
									conf.getDestinationFileManager().getNickName() });
						}
						else
						{
							name = Messages.SyncView_EndpointMissing;
						}
						break;
					default:
						break;
				}
			}
			else if (element instanceof IVirtualFileManager)
			{
				switch (columnIndex)
				{
					case 0:
						name = ((IVirtualFileManager) element).getNickName();
						break;
					case 1:
						name = ((IVirtualFileManager) element).getBasePath();
						break;
					default:
						break;
				}
			}

			return name;
		}
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus()
	{
		// does nothing
	}

	/**
	 * getSelection
	 * 
	 * @param selection
	 * @return SyncConfiguration
	 */
	public Object getSelection(ISelection selection)
	{
		if (!(selection instanceof StructuredSelection))
		{
			return null;
		}

		return ((StructuredSelection) selection).getFirstElement();
	}

	/**
	 * @author Ingo Muschenetz
	 */
	class SyncViewSorter extends ViewerSorter
	{
		/**
		 * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object,
		 *      java.lang.Object)
		 */
		public int compare(Viewer viewer, Object e1, Object e2)
		{
			return super.compare(viewer, e1, e2);
		}
	}

	/**
	 * Activate a context that this view uses. It will be tied to this view activation events and will be removed when
	 * the view is disposed.
	 */
	private void activateContext()
	{
		IContextService contextService = (IContextService) getSite().getService(IContextService.class);
		contextService.activateContext(VIEW_CONTEXT_ID);
	}
}
