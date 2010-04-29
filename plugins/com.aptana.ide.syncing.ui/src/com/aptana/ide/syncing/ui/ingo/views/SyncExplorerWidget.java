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
package com.aptana.ide.syncing.ui.ingo.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.progress.UIJob;

import com.aptana.ide.core.LoggerAdapater;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.efs.EFSUtils;
import com.aptana.ide.core.io.ingo.ISyncManagerChangeListener;
import com.aptana.ide.core.io.ingo.IVirtualFile;
import com.aptana.ide.core.io.ingo.IVirtualFileManager;
import com.aptana.ide.core.io.ingo.LocalFileManager;
import com.aptana.ide.core.io.ingo.ProjectFileManager;
import com.aptana.ide.core.io.ingo.ProjectProtocolManager;
import com.aptana.ide.core.io.ingo.ProtocolManager;
import com.aptana.ide.core.io.ingo.SyncManager;
import com.aptana.ide.core.io.ingo.VirtualFileManagerGroup;
import com.aptana.ide.core.io.ingo.VirtualFileSyncPair;
import com.aptana.ide.core.model.IModelListener;
import com.aptana.ide.core.model.IModifiableObject;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.SWTUtils;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;
import com.aptana.ide.syncing.ui.ingo.FileExplorerView;
import com.aptana.ide.syncing.ui.ingo.SyncEventHandlerAdapter;
import com.aptana.ide.syncing.ui.ingo.SyncModel;
import com.aptana.ide.syncing.ui.ingo.SyncModel.SyncPair;
import com.aptana.ide.ui.io.navigator.FileTreeContentProvider;

/**
 * The widget for Sync Explorer.
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class SyncExplorerWidget
{

	private static final String[] COLUMNS = { Messages.SyncExplorerWidget_ColumnFilename, 
		Messages.SyncExplorerWidget_ColumnSize, Messages.SyncExplorerWidget_ColumnLastModified };

	/**
	 * ID
	 */
	public static final String ID = "com.aptana.ide.syncing.views.SyncManagerView"; //$NON-NLS-1$

	/**
	 * REMOTE
	 */
	private static final String REMOTE = " (remote)"; //$NON-NLS-1$

	/**
	 * LOCAL
	 */
	private static final String LOCAL = " (local)"; //$NON-NLS-1$

	/**
	 * PATH
	 */
	public static final String PATH = Messages.SyncExplorerWidget_Path;

	private class NameSorter extends ViewerSorter
	{
		/**
		 * @see org.eclipse.jface.viewers.ViewerSorter#category(java.lang.Object)
		 */
		public int category(Object element)
		{
			if (element instanceof ProjectProtocolManager)
			{
				return 2;
			}
			if (element instanceof ProtocolManager)
			{
				return 3;
			}
			if (element instanceof IVirtualFileManager)
			{
				return 1;
			}
			if (element instanceof IVirtualFile)
			{
				IVirtualFile f = (IVirtualFile) element;
				return f.fetchInfo().isDirectory() ? 0 : 1;
			}
			if (element instanceof IContainer)
			{
				return 0;
			}
			if (element instanceof IResource)
			{
				return 1;
			}

			return super.category(element);
		}

		/**
		 * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object,
		 *      java.lang.Object)
		 */
		public int compare(Viewer viewer, Object e1, Object e2)
		{
			int cat1 = category(e1);
			int cat2 = category(e2);

			if (cat1 != cat2)
			{
				return cat1 - cat2;
			}

			if (e1 instanceof ProtocolManager && e2 instanceof ProtocolManager)
			{
				return ((ProtocolManager) e1).compareTo(e2);
			}
			if (e1 instanceof IVirtualFileManager && e2 instanceof IVirtualFileManager)
			{
				return ((IVirtualFileManager) e1).compareTo(e2);
			}
			if (e1 instanceof IVirtualFile && e2 instanceof IVirtualFile)
			{
				return ((IVirtualFile) e1).compareTo(e2);
			}

			return super.compare(viewer, e1, e2);
		}

	}

	private class SyncDropAdapter extends DropTargetAdapter
	{

		/**
		 * @see org.eclipse.swt.dnd.DropTargetAdapter#dragEnter(org.eclipse.swt.dnd.DropTargetEvent)
		 */
		public void dragEnter(DropTargetEvent event)
		{
			if (event.detail == DND.DROP_DEFAULT)
			{
				if ((event.operations & DND.DROP_COPY) != 0)
				{
					event.detail = DND.DROP_COPY;
				}
				else
				{
					event.detail = DND.DROP_NONE;
				}
			}
		}
	}

	private ISyncManagerChangeListener syncListener = new ISyncManagerChangeListener()
	{

		public void syncManagerEvent(final Object obj, final int actionId)
		{
			CoreUIUtils.getDisplay().asyncExec(new Runnable()
			{
				public void run()
				{
					if (actionId == ISyncManagerChangeListener.EDIT)
					{
						if (obj instanceof IVirtualFileManager)
						{
							IVirtualFileManager vfm = (IVirtualFileManager) obj;
							if (end1Id == vfm.getId())
							{
								end1 = vfm.getNickName();
							}
							if (end2Id == vfm.getId())
							{
								end2 = vfm.getNickName();
							}
						}
					}
					else if (actionId == ISyncManagerChangeListener.DELETE)
					{
						if (obj instanceof IVirtualFileManager)
						{
							IVirtualFileManager vfm = (IVirtualFileManager) obj;
							if (end2Id == vfm.getId())
							{
								end2Id = "-1";
								endpoint2TreeViewer.setInput(null);
							}
							if (end1Id == vfm.getId())
							{
								end1Id = "-1";
								endpoint1TreeViewer.setInput(null);
							}
						}
					}
					String previous = endpoint1Combo.getText();
					fillCombo(endpoint1Combo);
					if (end1Id != "-1")
					{
						select(end1Id, endpoint1Combo);
					}
					else
					{
						endpoint1Combo.setText(previous);
					}
					previous = endpoint2Combo.getText();
					fillCombo(endpoint2Combo);
					if (end2Id != "-1")
					{
						select(end2Id, endpoint2Combo);
					}
					else
					{
						endpoint2Combo.setText(previous);
					}
				}
			});
		}

	};

	private SashForm displayArea;
	private Composite top;
	private Font emptyFont;
	private Label empty1Label;
	private Label empty2Label;
	private Composite endpoint1Comp;
	private Composite endpoint2Comp;
	private Label endpoint1Label;
	private Label endpoint2Label;
	private Label endpoint1Path;
	private Label endpoint2Path;
	private Composite endpoint1Main;
	private Composite endpoint2Main;
	private TreeViewer endpoint1TreeViewer;
	private FileTreeContentProvider e1FileProvider;
	private SyncManagerFileLabelProvider e1FileLabeler;
	//private WorkbenchContentProvider e1ProjectProvider;
	private SyncWorkbenchLabelProvider e1ProjectLabeler;
	private TreeViewer endpoint2TreeViewer;
	private FileTreeContentProvider e2FileProvider;
	private SyncManagerFileLabelProvider e2FileLabeler;
	//private WorkbenchContentProvider e2ProjectProvider;
	private SyncWorkbenchLabelProvider e2ProjectLabeler;
	private Combo endpoint1Combo;
	private Combo endpoint2Combo;
	private ToolBar endpoint1Bar;
	private ToolItem endpoint1Up;
	private ToolItem endpoint1Refresh;
	private ToolItem endpoint1Home;
	private ToolBar endpoint2Bar;
	private ToolItem endpoint2Up;
	private ToolItem endpoint2Refresh;
	private ToolItem endpoint2Home;
	private CTabFolder statusTabs;
	private Table statusTable;
	private StyledText outputText;
	private Composite smartSyncComp;
	private Label smartSyncLabel1;
	private Button smartSyncButton;
	private Font pathFont;
	private ToolBar bottomBar;
	private ToolItem configure;
	private ToolItem clear;
	private ToolItem showHide;

	private SyncModel model;

	private List<Object> comboEntries;
	private String end1;
	private String end2;
	private String end1Id = "-1";
	private String end2Id = "-1";

	/**
	 * Creates a new widget
	 */
	public SyncExplorerWidget()
	{
		this.model = new SyncModel();
		this.comboEntries = new ArrayList<Object>();
	}

	/**
	 * Create the widget with the specified parent
	 * 
	 * @param parent
	 */
	public void createPartControl(Composite parent)
	{
		displayArea = new SashForm(parent, SWT.VERTICAL);
		displayArea.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW));
		pathFont = new Font(displayArea.getDisplay(), SWTUtils.boldFont(displayArea.getFont()));
		GridLayout daLayout = new GridLayout(1, true);
		daLayout.marginHeight = 0;
		daLayout.marginWidth = 0;
		displayArea.setLayout(daLayout);
		displayArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		top = new Composite(displayArea, SWT.NONE);
		GridLayout tLayout = new GridLayout(2, false);
		tLayout.marginHeight = 0;
		tLayout.marginWidth = 0;
		top.setLayout(tLayout);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createInsructions(top);

		emptyFont = new Font(parent.getDisplay(), "Arial", 12, SWT.NONE); //$NON-NLS-1$

		endpoint1Comp = new Composite(top, SWT.BORDER);
		endpoint1Label = new Label(endpoint1Comp, SWT.LEFT);
		endpoint1Label.setText(Messages.SyncExplorerWidget_Endpoint1);
		endpoint1Combo = new Combo(endpoint1Comp, SWT.DROP_DOWN | SWT.READ_ONLY);

		SelectionAdapter endpoint1Adapter = new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				String selection = endpoint1Combo.getText();
				Object input = comboEntries.get(endpoint1Combo.getSelectionIndex());
				updateFileProvider(endpoint1TreeViewer);
				if (selection.endsWith(REMOTE))
				{
					end1 = selection.substring(0, selection.length() - REMOTE.length());
				}
				else if (selection.endsWith(LOCAL))
				{
					end1 = selection.substring(0, selection.length() - LOCAL.length());
				}
				else
				{
					end1 = selection;
				}
				if (input instanceof IProject)
				{
					end1Id = "-1";
					setPath(endpoint1Path, input);
					//endpoint1TreeViewer.setContentProvider(e1ProjectProvider);
					endpoint1TreeViewer.setLabelProvider(e1ProjectLabeler);
				}
				else
				{
					if (input instanceof IVirtualFileManager)
					{
						end1Id = ((IVirtualFileManager) input).getId();
					}
					else
					{
						end1Id = "-1";
					}
					setPath(endpoint1Path, input);
					endpoint1TreeViewer.setContentProvider(e1FileProvider);
					endpoint1TreeViewer.setLabelProvider(e1FileLabeler);
				}
				endpoint1TreeViewer.setInput(input);
			}

		};

		endpoint1Combo.addSelectionListener(endpoint1Adapter);
		endpoint1Bar = new ToolBar(endpoint1Comp, SWT.FLAT);
		endpoint1Up = new ToolItem(endpoint1Bar, SWT.PUSH);
		endpoint1Refresh = new ToolItem(endpoint1Bar, SWT.PUSH);
		endpoint1Home = new ToolItem(endpoint1Bar, SWT.PUSH);
		endpoint1Home.addSelectionListener(endpoint1Adapter);
		endpoint1Path = new Label(endpoint1Comp, SWT.LEFT);
		endpoint1Main = new Composite(endpoint1Comp, SWT.NONE);
		empty1Label = new Label(endpoint1Main, SWT.CENTER | SWT.WRAP);
		endpoint1TreeViewer = new TreeViewer(endpoint1Main, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);

		e1FileLabeler = new SyncManagerFileLabelProvider();
		e1FileProvider = new FileTreeContentProvider();
		e1ProjectLabeler = new SyncWorkbenchLabelProvider();
		//e1ProjectProvider = new WorkbenchContentProvider();

		endpoint2Comp = new Composite(top, SWT.BORDER);
		endpoint2Label = new Label(endpoint2Comp, SWT.LEFT);
		endpoint2Label.setText(Messages.SyncExplorerWidget_Endpoint2);
		endpoint2Combo = new Combo(endpoint2Comp, SWT.DROP_DOWN | SWT.READ_ONLY);

		SelectionAdapter endpoint2Adapter = new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				String selection = endpoint2Combo.getText();
				Object input = comboEntries.get(endpoint2Combo.getSelectionIndex());
				updateFileProvider(endpoint2TreeViewer);
				if (selection.endsWith(REMOTE))
				{
					end2 = selection.substring(0, selection.length() - REMOTE.length());
				}
				else if (selection.endsWith(LOCAL))
				{
					end2 = selection.substring(0, selection.length() - LOCAL.length());
				}
				else
				{
					end2 = selection;
				}
				if (input instanceof IProject)
				{
					end2Id = "-1";
					setPath(endpoint2Path, input);
					//endpoint2TreeViewer.setContentProvider(e2ProjectProvider);
					endpoint2TreeViewer.setLabelProvider(e2ProjectLabeler);
				}
				else
				{
					if (input instanceof IVirtualFileManager)
					{
						end2Id = ((IVirtualFileManager) input).getId();
					}
					else
					{
						end2Id = "-1";
					}
					setPath(endpoint2Path, input);
					endpoint2TreeViewer.setContentProvider(e2FileProvider);
					endpoint2TreeViewer.setLabelProvider(e2FileLabeler);
				}
				endpoint2TreeViewer.setInput(input);
			}

		};

		endpoint2Combo.addSelectionListener(endpoint2Adapter);
		endpoint2Bar = new ToolBar(endpoint2Comp, SWT.FLAT);
		endpoint2Up = new ToolItem(endpoint2Bar, SWT.PUSH);
		endpoint2Refresh = new ToolItem(endpoint2Bar, SWT.PUSH);
		endpoint2Home = new ToolItem(endpoint2Bar, SWT.PUSH);
		endpoint2Home.addSelectionListener(endpoint2Adapter);
		endpoint2Path = new Label(endpoint2Comp, SWT.LEFT);
		endpoint2Main = new Composite(endpoint2Comp, SWT.NONE);
		empty2Label = new Label(endpoint2Main, SWT.CENTER | SWT.WRAP);
		endpoint2TreeViewer = new TreeViewer(endpoint2Main, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);

		e2FileLabeler = new SyncManagerFileLabelProvider();
		e2FileProvider = new FileTreeContentProvider();
		e2ProjectLabeler = new SyncWorkbenchLabelProvider();
		//e2ProjectProvider = new WorkbenchContentProvider();

		fillMappings();

		createEndpoint(endpoint1Comp, endpoint1Combo, endpoint2Combo, endpoint1Bar, endpoint1Up, endpoint1Refresh,
				endpoint1Home, endpoint1Path, endpoint1Main, empty1Label, endpoint1TreeViewer, endpoint2TreeViewer);

		createEndpoint(endpoint2Comp, endpoint2Combo, endpoint1Combo, endpoint2Bar, endpoint2Up, endpoint2Refresh,
				endpoint2Home, endpoint2Path, endpoint2Main, empty2Label, endpoint2TreeViewer, endpoint1TreeViewer);

		createBottom(displayArea);

		SyncManager.getSyncManager().addSyncManagerChangeEvent(syncListener);

		model.setLogger(new LoggerAdapater()
		{

			public void logWarning(String message)
			{
				logInfo(message);
			}

			public void logInfo(final String message)
			{
				CoreUIUtils.getDisplay().asyncExec(new Runnable()
				{

					public void run()
					{
						if (outputText != null && !outputText.isDisposed())
						{
							StringBuffer buffer = new StringBuffer(outputText.getText());
							buffer.append(message);
							outputText.setText(buffer.toString());
							outputText.setSelection(buffer.length());
						}
					}

				});
			}

			public void logError(String message)
			{
				logInfo(message);
			}

		});
		model.addListener(new IModelListener()
		{

			public void modelChanged(IModifiableObject object)
			{
				final SyncPair lastSync = model.getLastSync();
				final SyncPair lastAdded = model.getLastAdded();
				final SyncPair current = model.getCurrentSync();
				CoreUIUtils.getDisplay().asyncExec(new Runnable()
				{

					public void run()
					{
						addTransfer(lastAdded);
						updateTransfer(current);
						updateTransfer(lastSync);
					}

				});
			}

		});
	}

	private void createInsructions(Composite parent)
	{
		smartSyncComp = new Composite(parent, SWT.NONE);
		GridLayout sscLayout = new GridLayout(2, false);
		sscLayout.marginTop = 3;
		sscLayout.marginHeight = 0;
		sscLayout.marginWidth = 0;
		smartSyncComp.setLayout(sscLayout);
		GridData sscData = new GridData(SWT.CENTER, SWT.FILL, true, false);
		sscData.horizontalSpan = 2;
		smartSyncComp.setLayoutData(sscData);
		smartSyncLabel1 = new Label(smartSyncComp, SWT.LEFT | SWT.WRAP);
		smartSyncLabel1.setText(Messages.SyncExplorerWidget_SmartSyncLabel);
		smartSyncLabel1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		smartSyncButton = new Button(smartSyncComp, SWT.PUSH);
		smartSyncButton.setEnabled(false);
		smartSyncButton.setText(Messages.SyncExplorerWidget_SmartSyncButton);
		smartSyncButton.setImage(SyncingUIPlugin.getImage("icons/direction_both.gif")); //$NON-NLS-1$
		smartSyncButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		smartSyncButton.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				Object input1 = endpoint1TreeViewer.getInput();
				Object input2 = endpoint2TreeViewer.getInput();
				IFileStore vf1 = null;
				IFileStore vf2 = null;
				IConnectionPoint cp1 = null;
				IConnectionPoint cp2 = null;
				if (input1 instanceof IConnectionPoint)
				{
					IConnectionPoint vfm1 = (IConnectionPoint) input1;
					try {
						vf1 = vfm1.getRoot();
					} catch (CoreException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else if (input1 instanceof IContainer)
				{
					vf1 = ProjectFileManager.convertResourceToFile(input1);
				}
				else if (input1 instanceof IVirtualFile)
				{
					vf1 = (IFileStore) input1;
				}

				if (input2 instanceof IConnectionPoint)
				{
					IConnectionPoint vfm2 = (IConnectionPoint) input2;
					try {
						vf2 = vfm2.getRoot();
					} catch (CoreException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else if (input2 instanceof IContainer)
				{
					vf2 = ProjectFileManager.convertResourceToFile(input2);
				}
				else if (input2 instanceof IFileStore)
				{
					vf2 = (IFileStore) input2;
				}

				if (vf1 != null && vf2 != null)
				{
					final IVirtualFileManager fromManager = (IVirtualFileManager)cp1; //vf1.getFileManager().cloneManager();
					final IVirtualFileManager toManager = (IVirtualFileManager)cp2; //vf2.getFileManager().cloneManager();
					if (fromManager != null && toManager != null)
					{
						fromManager.setBasePath(EFSUtils.getAbsolutePath(vf1));
						vf1 = fromManager.createVirtualDirectory(EFSUtils.getAbsolutePath(vf1));
						toManager.setBasePath(EFSUtils.getAbsolutePath(vf2));
						vf2 = toManager.createVirtualDirectory(EFSUtils.getAbsolutePath(vf2));
						final IFileStore virtual1 = vf1;
						final IFileStore virtual2 = vf2;
						SmartSyncDialog dialog = new SmartSyncDialog(smartSyncButton.getShell(), fromManager, toManager, vf1, vf2,
								endpoint1Combo.getText(), endpoint2Combo.getText());
						dialog.setHandler(new SyncEventHandlerAdapter()
						{

							public void syncDone(VirtualFileSyncPair item)
							{
								UIJob job = new UIJob("Refreshing sync manager view") //$NON-NLS-1$
								{

									public IStatus runInUIThread(IProgressMonitor monitor)
									{
										if (endpoint1TreeViewer != null && !endpoint1TreeViewer.getTree().isDisposed())
										{
											if (end1Id == "-1")
											{
												refresh(endpoint1TreeViewer, null);
											}
											else if (end1Id == toManager.getId())
											{
												updateFileProvider(endpoint1TreeViewer);
												refresh(endpoint1TreeViewer, virtual1);
											}
											if (end2Id == "-1")
											{
												refresh(endpoint2TreeViewer, null);
											}
											else if (end2Id == fromManager.getId())
											{
												updateFileProvider(endpoint1TreeViewer);
												refresh(endpoint2TreeViewer, virtual2);
											}
										}
										return Status.OK_STATUS;
									}
								};
								job.schedule();
							}
						});
						dialog.open();
					}
				}
			}
		});
	}

	private void createBottom(Composite parent)
	{
		Composite bottom = new Composite(parent, SWT.NONE);
		GridLayout bLayout = new GridLayout(1, true);
		bLayout.marginHeight = 0;
		bLayout.marginWidth = 0;
		bottom.setLayout(bLayout);
		bottom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		GridData bbData = new GridData(SWT.END, SWT.FILL, true, false);
		bbData.horizontalSpan = 2;
		Composite bars = new Composite(top, SWT.NONE);
		GridLayout barsLayout = new GridLayout(2, false);
		barsLayout.marginWidth = 0;
		barsLayout.marginHeight = 0;
		barsLayout.horizontalSpacing = 0;
		barsLayout.verticalSpacing = 0;
		bars.setLayout(barsLayout);
		bars.setLayoutData(bbData);

		final ToolBar clearBar = new ToolBar(bars, SWT.FLAT);
		clear = new ToolItem(clearBar, SWT.PUSH);
		clear.setToolTipText(Messages.SyncExplorerWidget_Clear);
		clear.setImage(SyncingUIPlugin.getImage("icons/clear.png")); //$NON-NLS-1$
		clear.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				if (statusTabs.getSelectionIndex() == 0)
				{
					removeOldTransfers();
				}
				else if (statusTabs.getSelectionIndex() == 1)
				{
					outputText.setText(""); //$NON-NLS-1$
				}
			}

		});
		clearBar.setVisible(SyncingUIPlugin.getDefault().getPreferenceStore().getBoolean(
				com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.SHOW_SYNC_EXPLORER_TABLE));

		bottomBar = new ToolBar(bars, SWT.FLAT);
		bottomBar.setLayout(new GridLayout(1, true));
		showHide = new ToolItem(bottomBar, SWT.PUSH);
		showHide.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				if (statusTabs.isVisible())
				{
					SyncingUIPlugin.getDefault().getPreferenceStore().setValue(
							com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.SHOW_SYNC_EXPLORER_TABLE, false);
					statusTabs.setVisible(false);
					showHide.setImage(SyncingUIPlugin.getImage("icons/up_arrow.png")); //$NON-NLS-1$
					showHide.setToolTipText(Messages.SyncExplorerWidget_ShowTabs);
					clearBar.setVisible(false);
					displayArea.setMaximizedControl(top);
				}
				else
				{
					SyncingUIPlugin.getDefault().getPreferenceStore().setValue(
							com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.SHOW_SYNC_EXPLORER_TABLE, true);
					statusTabs.setVisible(true);
					showHide.setImage(SyncingUIPlugin.getImage("icons/down_arrow.png")); //$NON-NLS-1$
					clearBar.setVisible(true);
					showHide.setToolTipText(Messages.SyncExplorerWidget_HideTabs);
					displayArea.setMaximizedControl(null);
				}
			}

		});

		final Menu menu = new Menu(bars);
		final MenuItem columns = new MenuItem(menu, SWT.CASCADE);
		columns.setText(Messages.SyncExplorerWidget_MenuColumns);
		final Menu columnsMenu = new Menu(menu);
		columns.setMenu(columnsMenu);

		final MenuItem displayFileSize = new MenuItem(columnsMenu, SWT.CHECK);
		displayFileSize.setText(Messages.SyncExplorerWidget_DisplayFileSize);
		displayFileSize.setSelection(SyncingUIPlugin.getDefault().getPreferenceStore().getBoolean(
				com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.SHOW_SIZE));
		displayFileSize.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				if (displayFileSize.getSelection())
				{
					SyncingUIPlugin.getDefault().getPreferenceStore().setValue(com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.SHOW_SIZE, true);
					setSizeIndex(1);
					setModificationIndex(2);
					getFileSizeColumn(endpoint1TreeViewer);
					endpoint1TreeViewer.refresh();
					getFileSizeColumn(endpoint2TreeViewer);
					endpoint2TreeViewer.refresh();
				}
				else
				{
					SyncingUIPlugin.getDefault().getPreferenceStore().setValue(com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.SHOW_SIZE, false);
					setSizeIndex(-1);
					setModificationIndex(1);
					endpoint1TreeViewer.getTree().getColumn(1).dispose();
					endpoint2TreeViewer.getTree().getColumn(1).dispose();
				}
			}

		});

		final MenuItem displayDate = new MenuItem(columnsMenu, SWT.CHECK);
		displayDate.setText(Messages.SyncExplorerWidget_DisplayLastModified);
		displayDate.setSelection(SyncingUIPlugin.getDefault().getPreferenceStore().getBoolean(
				com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.SHOW_DATE));
		displayDate.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				if (displayDate.getSelection())
				{
					SyncingUIPlugin.getDefault().getPreferenceStore().setValue(com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.SHOW_DATE, true);
					if (endpoint1TreeViewer.getTree().getColumnCount() == 2)
					{
						setModificationIndex(2);
					}
					else
					{
						setModificationIndex(1);
					}
					getModificationColumn(endpoint1TreeViewer);
					endpoint1TreeViewer.refresh();
					getModificationColumn(endpoint2TreeViewer);
					endpoint2TreeViewer.refresh();
				}
				else
				{
					SyncingUIPlugin.getDefault().getPreferenceStore().setValue(com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.SHOW_DATE, false);
					setModificationIndex(-1);
					if (endpoint1TreeViewer.getTree().getColumnCount() == 3)
					{
						endpoint1TreeViewer.getTree().getColumn(2).dispose();
					}
					else
					{
						endpoint1TreeViewer.getTree().getColumn(1).dispose();
					}
					if (endpoint2TreeViewer.getTree().getColumnCount() == 3)
					{
						endpoint2TreeViewer.getTree().getColumn(2).dispose();
					}
					else
					{
						endpoint2TreeViewer.getTree().getColumn(1).dispose();
					}
				}
			}

		});

		configure = new ToolItem(bottomBar, SWT.DROP_DOWN);
		configure.setImage(SyncingUIPlugin.getImage("icons/configure.gif")); //$NON-NLS-1$
		configure.setToolTipText(Messages.SyncExplorerWidget_Options);
		configure.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				Rectangle rect = configure.getBounds();
				Point pt = new Point(rect.x, rect.y + rect.height);
				pt = bottomBar.toDisplay(pt);
				menu.setLocation(pt.x, pt.y);
				menu.setVisible(true);
			}

		});

		statusTabs = new CTabFolder(bottom, SWT.BOTTOM | SWT.BORDER | SWT.FLAT);
		GridLayout stLayout = new GridLayout(1, true);
		stLayout.marginHeight = 0;
		stLayout.marginWidth = 0;
		statusTabs.setLayout(stLayout);
		statusTabs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		CTabItem statusTab = new CTabItem(statusTabs, SWT.NONE);
		statusTab.setText(Messages.SyncExplorerWidget_StatusTransfers);
		statusTable = new Table(statusTabs, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
		statusTable.setHeaderVisible(true);
		statusTable.setLinesVisible(true);

		TableColumn column = new TableColumn(statusTable, SWT.LEFT);
		column.setText(Messages.SyncExplorerWidget_ColumnFile);
		column.setWidth(100);
		column = new TableColumn(statusTable, SWT.LEFT);
		column.setText(Messages.SyncExplorerWidget_ColumnFromEndpoint);
		column.setWidth(100);
		column = new TableColumn(statusTable, SWT.LEFT);
		column.setText(Messages.SyncExplorerWidget_ColumnFromFolder);
		column.setWidth(100);
		column.setWidth(100);
		column = new TableColumn(statusTable, SWT.LEFT);
		column.setText(Messages.SyncExplorerWidget_ColumnToEndpoint);
		column.setWidth(100);
		column = new TableColumn(statusTable, SWT.LEFT);
		column.setText(Messages.SyncExplorerWidget_ColumnToFolder);
		column.setWidth(100);
		column = new TableColumn(statusTable, SWT.LEFT);
		column.setText(Messages.SyncExplorerWidget_ColumnStatus);
		column.setWidth(100);
		column = new TableColumn(statusTable, SWT.LEFT);
		column.setWidth(100);
		column.setText(Messages.SyncExplorerWidget_ColumnBytes);

		statusTab.setControl(statusTable);
		CTabItem logTab = new CTabItem(statusTabs, SWT.NONE);
		logTab.setText(Messages.SyncExplorerWidget_Log);
		outputText = new StyledText(statusTabs, SWT.MULTI | SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
		outputText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		logTab.setControl(outputText);
		statusTabs.setSelection(0);

		boolean start = SyncingUIPlugin.getDefault().getPreferenceStore().getBoolean(
				com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.SHOW_SYNC_EXPLORER_TABLE);
		displayArea.setWeights(new int[] { 70, 30 });
		if (start)
		{
			showHide.setImage(SyncingUIPlugin.getImage("icons/down_arrow.png")); //$NON-NLS-1$
			showHide.setToolTipText(Messages.SyncExplorerWidget_HideTabs);
		}
		else
		{
			displayArea.setMaximizedControl(top);
			showHide.setImage(SyncingUIPlugin.getImage("icons/up_arrow.png")); //$NON-NLS-1$
			showHide.setToolTipText(Messages.SyncExplorerWidget_ShowTabs);
			statusTabs.setVisible(false);
		}
	}

	private TreeColumn getFileSizeColumn(TreeViewer viewer)
	{
		if (SyncingUIPlugin.getDefault().getPreferenceStore().getBoolean(com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.SHOW_SIZE))
		{
			TreeColumn filesize = new TreeColumn(viewer.getTree(), SWT.LEFT, 1);
			filesize.setWidth(50);
			filesize.setText(COLUMNS[1]);
			return filesize;
		}
		setSizeIndex(-1);
		setModificationIndex(1);
		return null;
	}

	private void setSizeIndex(int index)
	{
		if (e1FileLabeler != null)
		{
			e1FileLabeler.setSizeIndex(index);
		}
		if (e2FileLabeler != null)
		{
			e2FileLabeler.setSizeIndex(index);
		}
		if (e1ProjectLabeler != null)
		{
			e1ProjectLabeler.setSizeIndex(index);
		}
		if (e2ProjectLabeler != null)
		{
			e2ProjectLabeler.setSizeIndex(index);
		}
	}

	private void setModificationIndex(int index)
	{
		if (e1FileLabeler != null)
		{
			e1FileLabeler.setModificationIndex(index);
		}
		if (e2FileLabeler != null)
		{
			e2FileLabeler.setModificationIndex(index);
		}
		if (e1ProjectLabeler != null)
		{
			e1ProjectLabeler.setModificationIndex(index);
		}
		if (e2ProjectLabeler != null)
		{
			e2ProjectLabeler.setModificationIndex(index);
		}
	}

	private TreeColumn getModificationColumn(TreeViewer viewer)
	{
		if (SyncingUIPlugin.getDefault().getPreferenceStore().getBoolean(com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.SHOW_DATE))
		{
			TreeColumn lastModified = new TreeColumn(viewer.getTree(), SWT.LEFT);
			lastModified.setWidth(125);
			lastModified.setText(COLUMNS[2]);
			return lastModified;
		}
		setModificationIndex(-1);
		return null;
	}

	/**
	 * Sets the end points to display.
	 * 
	 * @param end1
	 * @param end2
	 */
	public void setEndpoint(IProject end1, IVirtualFileManager end2)
	{
		enable();
		if (end1 != null)
		{
			this.end1 = end1.getName();
			end1Id = "-1";
			Object input = findProject(this.end1);
			endpoint1Combo.setText(this.end1 + LOCAL);
			setPath(endpoint1Path, input);
			updateFileProvider(endpoint1TreeViewer);
			//endpoint1TreeViewer.setContentProvider(e1ProjectProvider);
			endpoint1TreeViewer.setLabelProvider(e1ProjectLabeler);
			endpoint1TreeViewer.setInput(input);
		}
		if (end2 != null)
		{
			if (e2FileProvider != null)
			{
				e2FileProvider.dispose();
			}
			this.end2 = end2.getNickName();
			Object input = findVFM(this.end2);
			if (input != null)
			{
				end2Id = ((IVirtualFileManager) input).getId();
			}
			else
			{
				end2Id = "-1";
			}
			endpoint2Combo.setText(this.end2 + REMOTE);
			setPath(endpoint2Path, input);
			updateFileProvider(endpoint2TreeViewer);
			endpoint2TreeViewer.setContentProvider(e2FileProvider);
			endpoint2TreeViewer.setLabelProvider(e2FileLabeler);
			endpoint2TreeViewer.setInput(input);
		}
	}

	private void enable()
	{
		smartSyncButton.setEnabled(true);
		endpoint1Home.setEnabled(true);
		endpoint1Up.setEnabled(true);
		endpoint1Refresh.setEnabled(true);
		endpoint2Home.setEnabled(true);
		endpoint2Up.setEnabled(true);
		endpoint2Refresh.setEnabled(true);
		if (empty1Label.isVisible())
		{
			GridData data = (GridData) empty1Label.getLayoutData();
			data.exclude = true;
			empty1Label.setVisible(false);
			data = (GridData) endpoint1TreeViewer.getTree().getLayoutData();
			data.exclude = false;
			endpoint1TreeViewer.getTree().setVisible(true);
			endpoint1Main.layout(true, true);
		}
		if (empty2Label.isVisible())
		{
			GridData data = (GridData) empty2Label.getLayoutData();
			data.exclude = true;
			empty2Label.setVisible(false);
			data = (GridData) endpoint2TreeViewer.getTree().getLayoutData();
			data.exclude = false;
			endpoint2TreeViewer.getTree().setVisible(true);
			endpoint2Main.layout(true, true);
		}
	}

	/**
	 * Disposes the widget
	 */
	public void dispose()
	{
		if (pathFont != null && !pathFont.isDisposed())
		{
			pathFont.dispose();
		}
		if (emptyFont != null && !emptyFont.isDisposed())
		{
			emptyFont.dispose();
		}
		model.dispose();
		model.setLogger(null);
		SyncManager.getSyncManager().removeSyncManagerChangeEvent(syncListener);
	}

	private void createEndpoint(Composite endpointComp, final Combo endpointCombo, final Combo otherCombo,
			ToolBar endpointToolbar, final ToolItem up, final ToolItem refresh, final ToolItem home, final Label path,
			final Composite endpointMain, final Label emptyLabel, final TreeViewer treeViewer,
			final TreeViewer otherViewer)
	{
		endpointMain.setBackgroundMode(SWT.INHERIT_DEFAULT);
		endpointMain.setBackground(endpointComp.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		emptyLabel.setText(Messages.SyncExplorerWidget_SelectWarningLabel);
		emptyLabel.setFont(emptyFont);
		emptyLabel.setForeground(endpointComp.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		emptyLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		GridLayout e1Comp = new GridLayout(3, false);
		e1Comp.marginHeight = 3;
		e1Comp.marginWidth = 3;
		endpointComp.setLayout(e1Comp);
		endpointComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		treeViewer.setSorter(new NameSorter());

		final TreeColumn filename = new TreeColumn(treeViewer.getTree(), SWT.LEFT);
		filename.setWidth(300);
		filename.setText(COLUMNS[0]);
		getFileSizeColumn(treeViewer);
		getModificationColumn(treeViewer);
		treeViewer.getTree().setHeaderVisible(true);

		endpointCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fillCombo(endpointCombo);

		endpointCombo.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				smartSyncButton.setEnabled(endpoint1Combo.getSelectionIndex() > -1 
						&& endpoint2Combo.getSelectionIndex() > -1);
				home.setEnabled(true);
				up.setEnabled(true);
				refresh.setEnabled(true);
				if (emptyLabel.isVisible())
				{
					GridData data = (GridData) emptyLabel.getLayoutData();
					data.exclude = true;
					emptyLabel.setVisible(false);
					data = (GridData) treeViewer.getTree().getLayoutData();
					data.exclude = false;
					treeViewer.getTree().setVisible(true);
					endpointMain.layout(true, true);
				}
			}

		});

		up.setImage(SyncingUIPlugin.getImage("icons/up.png")); //$NON-NLS-1$
		//up.setToolTipText(com.aptana.ide.syncing.Messages.SyncManager_Up);
		up.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				Object e1Input = treeViewer.getInput();
				if (e1Input != null)
				{
					if (e1Input instanceof IContainer)
					{
						IContainer e1Parent = ((IContainer) e1Input).getParent();
						if (e1Parent != null && !CoreUIUtils.getWorkspaceRoot().equals(e1Parent))
						{
							treeViewer.setInput(e1Parent);
							setPath(path, e1Parent);
						}
					}
					else if (e1Input instanceof IVirtualFile)
					{
						IVirtualFile e1Parent = EFSUtils.getParentFile(((IVirtualFile) e1Input));
						if (e1Parent != null && !e1Parent.equals(e1Input))
						{
							treeViewer.setInput(e1Parent);
							setPath(path, e1Parent);
						}
					}
				}
			}

		});
		up.setEnabled(false);

		refresh.setImage(SyncingUIPlugin.getImage("icons/refresh.gif")); //$NON-NLS-1$
		//refresh.setToolTipText(com.aptana.ide.syncing.Messages.SyncManager_Refresh);
		refresh.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				refresh(treeViewer, null);
			}

		});
		refresh.setEnabled(false);

		home.setImage(SyncingUIPlugin.getImage("icons/home.png")); //$NON-NLS-1$
		//home.setToolTipText(com.aptana.ide.syncing.Messages.SyncManager_Home);
		home.setEnabled(false);

		GridData ep1Data = new GridData(SWT.FILL, SWT.FILL, true, false);
		ep1Data.horizontalSpan = 3;
		path.setText(PATH);
		path.setLayoutData(ep1Data);
		path.setFont(pathFont);

		treeViewer.addDragSupport(DND.DROP_COPY | DND.DROP_DEFAULT, new Transfer[] { LocalSelectionTransfer
				.getTransfer() }, new TransferDragSourceListener()
		{

			public void dragSetData(DragSourceEvent event)
			{
				event.data = treeViewer.getSelection();
			}

			public Transfer getTransfer()
			{
				return LocalSelectionTransfer.getTransfer();
			}

			public void dragFinished(DragSourceEvent event)
			{
				LocalSelectionTransfer.getTransfer().setSelection(null);
				LocalSelectionTransfer.getTransfer().setSelectionSetTime(0);
			}

			public void dragStart(DragSourceEvent event)
			{
				LocalSelectionTransfer.getTransfer().setSelection(treeViewer.getSelection());
				LocalSelectionTransfer.getTransfer().setSelectionSetTime(event.time & 0xFFFFFFFFL);
				event.doit = true;
			}

		});
		treeViewer.addDoubleClickListener(new IDoubleClickListener()
		{

			public void doubleClick(DoubleClickEvent event)
			{
				Object selection = getSelection(treeViewer);
				if (selection != null
						&& (selection instanceof IContainer || (selection instanceof IVirtualFile && ((IVirtualFile) selection)
								.fetchInfo().isDirectory())))
				{
					setPath(path, selection);
					updateFileProvider(treeViewer);
					treeViewer.setInput(selection);
				}
			}

		});
		treeViewer.addDropSupport(DND.DROP_COPY | DND.DROP_DEFAULT, new Transfer[] { LocalSelectionTransfer
				.getTransfer() }, new SyncDropAdapter()
		{

			public void drop(DropTargetEvent event)
			{
				Object element1 = null;
				if (event.item != null)
				{
					TreeItem target = (TreeItem) event.item;
					element1 = target.getData();
					if (element1 instanceof IFile)
					{
						element1 = ((IFile) element1).getParent();
					}
					else if (element1 instanceof IVirtualFile && !((IVirtualFile) element1).fetchInfo().isDirectory())
					{
						element1 = EFSUtils.getParentFile(((IVirtualFile) element1));
					}
				}
				else
				{
					element1 = treeViewer.getInput();
				}
				if (event.data instanceof ITreeSelection)
				{
					ITreeSelection selection = (ITreeSelection) event.data;
					TreePath[] paths = selection.getPaths();
					if (paths.length > 0)
					{
						List<Object> elements = new ArrayList<Object>();
						for (TreePath path : paths)
						{
							boolean alreadyIn = false;
							for (TreePath path2 : paths)
							{
								if (!path.equals(path2) && path.startsWith(path2, null))
								{
									alreadyIn = true;
									break;
								}
							}
							if (!alreadyIn)
							{
								elements.add(path.getLastSegment());
							}
						}
						for (Object element : elements)
						{
							copy(element, element1);
						}
					}
				}
			}

		});
		GridLayout mLayout = new GridLayout(1, true);
		mLayout.marginWidth = 0;
		mLayout.marginHeight = 0;
		endpointMain.setLayout(mLayout);
		GridData et1Data = new GridData(SWT.FILL, SWT.FILL, true, true);
		et1Data.horizontalSpan = 3;
		endpointMain.setLayoutData(et1Data);
		GridData treeData = new GridData(SWT.FILL, SWT.FILL, true, true);
		treeData.exclude = true;
		treeViewer.getTree().setLayoutData(treeData);
	}

	private void refresh(TreeViewer viewer, Object element)
	{
		if (viewer.getInput() instanceof IResource)
		{
			try
			{
				((IResource) viewer.getInput()).refreshLocal(IResource.DEPTH_INFINITE, null);
			}
			catch (CoreException e1)
			{
			}
		}
		updateFileProvider(viewer);
		if (element == null)
		{
			viewer.refresh();
		}
		else
		{
			if (element instanceof IVirtualFile)
			{
				"/".equals(EFSUtils.getAbsolutePath(((IVirtualFile) element))); //$NON-NLS-1$
				viewer.refresh();
			}
			else
			{
				viewer.refresh(element);
			}
		}
	}

	private void fillMappings()
	{
		comboEntries.clear();

		for (IProject project : CoreUIUtils.getWorkspaceRoot().getProjects())
		{
			if (project.isOpen())
			{
				comboEntries.add(project);
			}
		}
		ProtocolManager[] managers = ProtocolManager.getPrototcolManagers();
		for (ProtocolManager manager : managers)
		{
			if (manager.hasCustomContent())
			{
				Object[] contents = manager.getContent();
				if (contents != null)
				{
					for (Object content : contents)
					{
						if (content instanceof VirtualFileManagerGroup)
						{
							IVirtualFileManager[] vfms = ((VirtualFileManagerGroup) content)
									.getFileManagers();
							for (IVirtualFileManager vfm : vfms)
							{
								if (!(vfm instanceof LocalFileManager)
										&& !(vfm instanceof ProjectFileManager))
								{
									comboEntries.add(vfm);
								}
							}
						}
						else
						{
							comboEntries.add(content);
						}
					}
				}
			}
			else
			{
				IVirtualFileManager[] vfms = manager.getFileManagers();
				for (IVirtualFileManager vfm : vfms)
				{
					if (!(vfm instanceof LocalFileManager) && !(vfm instanceof ProjectFileManager))
					{
						comboEntries.add(vfm);
					}
				}
			}
		}
		Object[] others = FileExplorerView.getVirtualRoots(true);
		for (Object other : others)
		{
			if (!(other instanceof ProtocolManager))
			{
				comboEntries.add(other);
			}
		}
	}

	private void fillCombo(Combo combo)
	{
		combo.removeAll();
		
		// sorts the content by local, remote, and the rest
		List<String> locals = new ArrayList<String>();
		List<String> remotes = new ArrayList<String>();
		List<String> rest = new ArrayList<String>();
		String label;
		Map<Object, String> entryMappings = new HashMap<Object, String>();
		for (Object entry : comboEntries)
		{
			if (entry instanceof IProject)
			{
				label = ((IProject) entry).getName() + LOCAL;
				locals.add(label);
			}
			else if (entry instanceof LocalFileManager)
			{
				label = ((LocalFileManager) entry).getNickName() + LOCAL;
				locals.add(label);
			}
			else if (entry instanceof IVirtualFileManager)
			{
				label = ((IVirtualFileManager) entry).getNickName() + REMOTE;
				remotes.add(label);
			}
			else if (entry instanceof IVirtualFile)
			{
				label = ((IVirtualFile) entry).getName() + LOCAL;
				locals.add(label);
			}
			else
			{
				label = entry.toString();
				rest.add(label);
			}
			entryMappings.put(entry, label);
		}
		Collections.sort(locals);
		Collections.sort(remotes);
		Collections.sort(rest);

		for (String text : locals)
		{
			combo.add(text);
		}
		for (String text : remotes)
		{
			combo.add(text);
		}
		for (String text : rest)
		{
			combo.add(text);
		}

		List<Object> sortedEntries = new ArrayList<Object>();
		sortedEntries.addAll(comboEntries);
		for (Object entry : comboEntries)
		{
			sortedEntries.set(combo.indexOf(entryMappings.get(entry)), entry);
		}
		comboEntries = sortedEntries;
	}

	private void select(String id, Combo combo)
	{
		if (id != "-1")
		{
			ProtocolManager[] managers = ProtocolManager.getPrototcolManagers();
			for (ProtocolManager manager : managers)
			{
				IVirtualFileManager[] vfms = manager.getFileManagers();
				for (IVirtualFileManager vfm : vfms)
				{
					if (id == vfm.getId())
					{
						combo.setText(vfm.getNickName() + REMOTE);
					}
				}
			}
		}
	}

	private void setPath(Label label, Object selection)
	{
		if (selection instanceof IContainer)
		{
			String path = ((IContainer) selection).getProjectRelativePath().toString();
			if (!path.startsWith("/")) //$NON-NLS-1$
			{
				path = "/" + path; //$NON-NLS-1$
			}
			label.setText(PATH + path);
		}
		else if (selection instanceof IVirtualFile)
		{
			String path;
			path = EFSUtils.getRelativePath((IVirtualFile)selection);
			path = StringUtils.replace(path, "\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
			if (!path.startsWith("/")) //$NON-NLS-1$
			{
				path = "/" + path; //$NON-NLS-1$
			}
			label.setText(PATH + path);
		}
		else
		{
			label.setText(PATH + "/"); //$NON-NLS-1$
		}
	}

	private void removeOldTransfers()
	{
		TableItem[] items = statusTable.getItems();
		for (int i = 0; i < items.length; i++)
		{
			if (items[i].getData() instanceof SyncPair)
			{
				SyncPair pair = (SyncPair) items[i].getData();
				if (pair.getStatus() == SyncModel.SUCCESS || pair.getStatus() == SyncModel.FAILURE)
				{
					items[i].dispose();
				}
			}
		}
	}

	private void updateTransfer(SyncPair pair)
	{
		if (pair == null || statusTable == null || statusTable.isDisposed())
		{
			return;
		}
		TableItem[] items = statusTable.getItems();
		for (int i = 0; i < items.length; i++)
		{
			if (pair.equals(items[i].getData()))
			{
				if (pair.getStatus() == SyncModel.RUNNING)
				{
					items[i].setText(5, Messages.SyncExplorerWidget_StatusRunning);
					statusTable.showItem(items[i]);
				}
				else if (pair.getStatus() == SyncModel.SUCCESS)
				{
					if (!items[i].getText(5).equals(Messages.SyncExplorerWidget_StatusSuccess))
					{
						if (end1Id == "-1")
						{
							refresh(endpoint1TreeViewer, null);
						}
						else if (end1Id == pair.getDestManager().getId())
						{
							refresh(endpoint1TreeViewer, pair.getDestinationFile());
						}
						if (end2Id == "-1")
						{
							refresh(endpoint2TreeViewer, null);
						}
						else if (end2Id == pair.getDestManager().getId())
						{
							refresh(endpoint2TreeViewer, EFSUtils.getParentFile(pair.getDestinationFile()));
						}
						items[i].setText(5, Messages.SyncExplorerWidget_StatusSuccess);
						statusTable.showItem(items[i]);
					}
				}
				else if (pair.getStatus() == SyncModel.FAILURE)
				{
					items[i].setText(5, Messages.SyncExplorerWidget_StatusFailed);
					statusTable.showItem(items[i]);
				}
				return;
			}
		}
	}

	private void updateFileProvider(TreeViewer viewer)
	{
		if (viewer.getContentProvider() instanceof SyncManagerFileLabelProvider)
		{
			viewer.getContentProvider().dispose();
		}
	}

	private void addTransfer(SyncPair pair)
	{
		if (pair == null || statusTable == null || statusTable.isDisposed())
		{
			return;
		}
		TableItem[] items = statusTable.getItems();
		for (int i = 0; i < items.length; i++)
		{
			if (pair.equals(items[i].getData()))
			{
				return;
			}
		}
		TableItem item = new TableItem(statusTable, SWT.LEFT);
		item.setData(pair);
		item.setText(new String[] { pair.getSourceFile().getName(), pair.getFromEndpoint(), pair.getFromFolder(),
				pair.getToEndpoint(), pair.getToFolder(), Messages.SyncExplorerWidget_StatusWaiting, Long.toString(pair.getSourceFile().fetchInfo().getLength()) });
		statusTable.showItem(item);
	}

	private IProject findProject(String name)
	{
		if (name == null)
			return null;

		for (IProject p : CoreUIUtils.getWorkspaceRoot().getProjects())
		{
			if (name.equals(p.getName()))
			{
				return p;
			}
		}
		return null;
	}

	private IVirtualFileManager findVFM(String nickName)
	{
		if (nickName == null)
			return null;

		ProtocolManager[] managers = ProtocolManager.getPrototcolManagers();
		IVirtualFileManager[] vfms;
		for (ProtocolManager manager : managers)
		{
			vfms = manager.getFileManagers();
			for (IVirtualFileManager vfm : vfms)
			{
				if (nickName.equals(vfm.getNickName()))
				{
					return vfm;
				}
			}
		}
		return null;
	}

	private void copy(Object from, Object to)
	{
		if (from != null && to != null)
		{
			model.addSyncing(from, to);
		}
	}

	private Object getSelection(TreeViewer viewer)
	{
		if (viewer.getSelection().isEmpty())
			return null;

			return ((ITreeSelection) viewer.getSelection()).getFirstElement();
	}

	/**
	 * Sets the focus to this widget.
	 */
	public void setFocus()
	{
		displayArea.setFocus();
	}

}
