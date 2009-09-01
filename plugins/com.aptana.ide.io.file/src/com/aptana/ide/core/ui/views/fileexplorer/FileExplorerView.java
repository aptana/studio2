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
package com.aptana.ide.core.ui.views.fileexplorer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.BaseNewWizardMenu;
import org.eclipse.ui.activities.ActivityManagerEvent;
import org.eclipse.ui.activities.IActivityManager;
import org.eclipse.ui.activities.IActivityManagerListener;
import org.eclipse.ui.activities.IIdentifier;
import org.eclipse.ui.activities.IIdentifierListener;
import org.eclipse.ui.activities.IdentifierEvent;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.editors.text.UntitledTextFileWizard;
import org.eclipse.ui.internal.views.navigator.ResourceNavigatorMessages;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.wizards.IWizardDescriptor;

import com.aptana.ide.core.CoreStrings;
import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.PlatformUtils;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.io.ConnectionException;
import com.aptana.ide.core.io.IFileProgressMonitor;
import com.aptana.ide.core.io.IVirtualFile;
import com.aptana.ide.core.io.IVirtualFileManager;
import com.aptana.ide.core.io.IVirtualFileManagerDialog;
import com.aptana.ide.core.io.ProtocolManager;
import com.aptana.ide.core.io.VirtualFile;
import com.aptana.ide.core.io.VirtualFileManagerException;
import com.aptana.ide.core.io.VirtualFileManagerGroup;
import com.aptana.ide.core.io.sync.ISyncManagerChangeListener;
import com.aptana.ide.core.io.sync.SyncManager;
import com.aptana.ide.core.io.sync.VirtualFileManagerSyncPair;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.EclipseUIUtils;
import com.aptana.ide.core.ui.ImageUtils;
import com.aptana.ide.core.ui.PreferenceUtils;
import com.aptana.ide.core.ui.actions.CheckBoxAction;
import com.aptana.ide.core.ui.actions.PushButtonAction;
import com.aptana.ide.core.ui.io.file.ILocalFile;
import com.aptana.ide.core.ui.io.file.LocalFile;
import com.aptana.ide.core.ui.io.file.LocalFileManager;
import com.aptana.ide.core.ui.io.file.LocalProtocolManager;
import com.aptana.ide.core.ui.io.file.ProjectProtocolManager;
import com.aptana.ide.core.ui.preferences.IPreferenceConstants;
import com.aptana.ide.core.ui.views.IRefreshableView;
import com.aptana.ide.io.file.FilePlugin;
import com.aptana.ide.io.file.epl.OpenWithMenuExternal;

/**
 * The view to navigator file system.
 */
public class FileExplorerView extends ViewPart implements ISyncManagerChangeListener, IRefreshableView, NewVirtualFileDialog.Client
{

	/**
	 * ID
	 */
	public static final String ID = "com.aptana.ide.js.ui.views.FileExplorerView"; //$NON-NLS-1$

	public static final String GROUP_PROJECT = "group.project"; //$NON-NLS-1$

	/*
	 * Static fields
	 */
	private static final int MAX_EXPAND_TIME = 30000;

	private static final int EXPAND_INTERVAL = 250;

	private static final String STATE_START_TOKEN = "#APT#"; //$NON-NLS-1$

	private static String DESKTOP = Messages.FileExplorerView_Desktop;

	private static String MY_COMPUTER_GUID = "::{20D04FE0-3AEA-1069-A2D8-08002B30309D}"; //$NON-NLS-1$

	private static String MY_NETWORK_PLACES_GUID = "::{208D2C60-3AEA-1069-A2D7-08002B30309D}"; //$NON-NLS-1$

	/**
	 * lastSelected
	 */
	// TODO: change how this works, shouldn't be static with
	public static String lastSelected = null; 

	/**
	 * secondaryIdCounter
	 */
	private static int secondaryIdCounter = 1;

	private static ImageDescriptor fSortIconDescriptor = ImageUtils.getImageDescriptor("icons/sort.gif"); //$NON-NLS-1$

	private static ImageDescriptor fCollapseIconDescriptor = ImageUtils.getImageDescriptor("icons/collapse.gif"); //$NON-NLS-1$

	private static ImageDescriptor fWebFilesIconDescriptor = ImageUtils.getImageDescriptor("icons/method_public.gif"); //$NON-NLS-1$

	private static ImageDescriptor fRefreshIconDescriptor = ImageUtils.getImageDescriptor("icons/refresh.gif"); //$NON-NLS-1$

	private static ImageDescriptor fNewIconDescriptor = ImageUtils.getImageDescriptor("icons/file_new.png"); //$NON-NLS-1$

	private static ImageDescriptor fNewFolderIconDescriptor = ImageUtils.getImageDescriptor("icons/folder.gif"); //$NON-NLS-1$

	private static ImageDescriptor fnewViewActionIconDescriptor = FilePlugin
			.getImageDescriptor("icons/file_explorer.png"); //$NON-NLS-1$

	private static ImageDescriptor fDeleteIcon = ImageUtils.getImageDescriptor("icons/delete_edit.gif"); //$NON-NLS-1$

	private Action sortAction;
	private Action collapseAction;
	private Action webFilterAction;
	private Action refreshAction;
	private Action refreshAllAction;
	private Action openAction;
	private Action doubleClickAction;
	private Action newAction;
	private Action renameAction;
	private Action deleteAction;
	private Action newFolderAction;
	private Action newViewAction;
	private Action editVirtualFileManager;
	private Action addVirtualFileManager;
	private Action editVirtualFileProperties;
	private Action actionShowHideLocal;
	private Action actionNewVirtualFile;
	private Action actionNewVirtualFolder;
	private Action actionNewVirtualProject;

	private TreeViewer viewer;

	private DrillDownAdapter drillDownAdapter;

	private boolean webFiltering;

	private static List<Object> transientShortcuts = new ArrayList<Object>();

	private boolean alwaysOverwrite;

	private Object[] dragSelection = null;

	private int dragDetail = 0;

	/**
	 * showLocalEndpoints
	 */
	protected boolean showLocalEndpoints = false;

	private Map<IEditorDescriptor, Image> images = new HashMap<IEditorDescriptor, Image>();
	private IEditorRegistry registry = EclipseUIUtils.getWorkbenchEditorRegistry();

	private FileSelectionListener fSelectionListener = new FileSelectionListener();

	private ITreeViewerListener treeListener;
	private Object[] expandedElements;

	/*
	 * Private classes
	 */

	/**
	 * Provides labels for File View tree items
	 */
	private class ViewLabelProvider extends LabelProvider
	{

		private WorkbenchLabelProvider backupProvider = new WorkbenchLabelProvider();

		/**
		 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object element)
		{
			String retVal = null;
			if (element instanceof ProtocolManager)
			{
				ProtocolManager pm = (ProtocolManager) element;
				retVal = pm.getDisplayName();
			}
			else if (element instanceof VirtualFileManagerGroup)
			{
				VirtualFileManagerGroup group = (VirtualFileManagerGroup) element;
				retVal = group.getName();
			}
			else if (element instanceof IVirtualFileManager)
			{
				retVal = ((IVirtualFileManager) element).getDescriptiveLabel();
			}
			else if (element instanceof IVirtualFile)
			{
				IVirtualFile f = (IVirtualFile) element;
				retVal = f.getName();
			}
			else if (element instanceof IAdaptable)
			{
				Object adapter = ((IAdaptable) element).getAdapter(FileTreeContentProvider.class);
				retVal = backupProvider.getText(adapter);
			}
			else
			{
				retVal = backupProvider.getText(element);
			}
			if (element == FileTreeContentProvider.LOADING)
			{
				return Messages.FileExplorerView_Loading;
			}
			if (retVal == null)
			{
				retVal = Messages.FileExplorerView_UnknownElement;
			}
			return retVal;
		}

		/**
		 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
		 */
		public Image getImage(Object element)
		{
			Image image = null;

			if (element instanceof ProtocolManager)
			{
				ProtocolManager pm = (ProtocolManager) element;
				image = pm.getImage();
			}
			else if (element instanceof VirtualFileManagerGroup)
			{
				VirtualFileManagerGroup group = (VirtualFileManagerGroup) element;
				return group.getImage();
			}
			else if (element instanceof IVirtualFileManager)
			{
				IVirtualFileManager fm = (IVirtualFileManager) element;
				image = fm.getImage();
			}
			else if (element instanceof IVirtualFile)
			{
				IVirtualFile f = (IVirtualFile) element;
				image = f.getImage();
				if (image == null)
				{
					IEditorDescriptor desc = registry.getDefaultEditor(f.getName());
					if (desc == null || desc.getImageDescriptor() == null)
					{
						IWorkbench workbench = PlatformUI.getWorkbench();
						if (workbench != null)
						{
							ISharedImages sharedImages = workbench.getSharedImages();
							if (f.isDirectory())
							{
								image = sharedImages.getImage(ISharedImages.IMG_OBJ_FOLDER);
							}
							else
							{
								image = ImageUtils.getIcon(f.getExtension());
								if (image == null)
								{
									image = sharedImages.getImage(ISharedImages.IMG_OBJ_FILE);
								}
							}
						}
					}
					else
					{
						if (images.containsKey(desc))
						{
							image = (Image) images.get(desc);
						}
						else
						{
							image = desc.getImageDescriptor().createImage();
							images.put(desc, image);
						}
					}
				}
			}
			else if (element == FileTreeContentProvider.LOADING)
			{
				return FilePlugin.getImage("icons/hourglass.png"); //$NON-NLS-1$
			}
			else if (element instanceof IAdaptable)
			{
				Object adapter = ((IAdaptable) element).getAdapter(FileTreeContentProvider.class);
				image = backupProvider.getImage(adapter);
			}
			else
			{
				image = backupProvider.getImage(element);
			}

			return image;
		}
	}

	/**
	 * Allows sorting of tree items
	 * 
	 * @author Ingo Muschenetz
	 */
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
			else if (element instanceof ProtocolManager)
			{
				return 3;
			}
			else if (element instanceof IVirtualFileManager)
			{
				return 1;
			}
			else if (element instanceof IVirtualFile)
			{
				IVirtualFile f = (IVirtualFile) element;
				return f.isDirectory() ? 0 : 1;
			}

			return 0;
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
			else if (e1 instanceof IVirtualFileManager && e2 instanceof IVirtualFileManager)
			{
				return ((IVirtualFileManager) e1).compareTo(e2);
			}
			else if (e1 instanceof IVirtualFile && e2 instanceof IVirtualFile)
			{
				return ((IVirtualFile) e1).compareTo(e2);
			}

			return 0;
		}

	}

	/**
	 * A filter to only show "web" files. Pulls from a preference page
	 * 
	 * @author Ingo Muschenetz
	 */
	private class AllowOnlyWebFilesFilter extends ViewerFilter
	{
		List<String> list = null;

		/**
		 * AllowOnlyWebFilesFilter
		 * 
		 * @param extensions
		 */
		public AllowOnlyWebFilesFilter(String[] extensions)
		{
			list = new ArrayList<String>(Arrays.asList(extensions));
		}

		/**
		 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object,
		 *      java.lang.Object)
		 */
		public boolean select(Viewer viewer, Object parent, Object element)
		{
			if (webFiltering && element instanceof IVirtualFile)
			{
				IVirtualFile f = (IVirtualFile) element;

				if (f.isDirectory())
				{
					return true;
				}

				String extension = "*" + f.getExtension(); //$NON-NLS-1$

				return list.contains(extension);
			}
			else
			{
				return true;
			}
		}
	}

	private class FileSelectionListener implements SelectionListener, MouseListener {

		private static final int INTERVAL = 2000;

		private boolean shouldRename;
		private TreeItem selection;
		private long firstSelectionTime;

		public void widgetDefaultSelected(SelectionEvent e) {	
		}

		public void widgetSelected(SelectionEvent e) {
			TreeItem[] selections = viewer.getTree().getSelection();
			if (selections.length == 0) {
				return;
			}
			Object obj = getFirstSelectedItem();
			if (!(obj instanceof IVirtualFile)) {
				return;
			}

			if (selection == null || selections.length > 1 || selection != selections[0]) {
				selection = selections[0];
				firstSelectionTime = Calendar.getInstance().getTimeInMillis();
			} else {
				long currentTime = Calendar.getInstance().getTimeInMillis();
				if (currentTime - firstSelectionTime < INTERVAL) {
					shouldRename = true;
					selection = null;
				} else {
					firstSelectionTime = currentTime;
				}
			}
		}

		public void mouseDoubleClick(MouseEvent e) {
		}

		public void mouseDown(MouseEvent e) {			
		}

		public void mouseUp(MouseEvent e) {
			if (shouldRename) {
				rename();
				shouldRename = false;
			}
		}
	}

	/**
	 * The constructor.
	 */
	public FileExplorerView()
	{
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	public void dispose()
	{
		try
		{
			if (activityManager != null && activityManagerListener != null)
			{
				activityManager.removeActivityManagerListener(activityManagerListener);
			}
			SyncManager.getSyncManager().removeSyncManagerChangeEvent(this);
			if (viewer != null && treeListener != null)
				viewer.removeTreeListener(treeListener);
			// disconnects all the open virtual file managers
			// runs in a job to not block the UI if disconnect() takes long
			Job job = new Job(Messages.FileExplorerView_Disconnecting_file_managers_job_title)
			{

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					if (expandedElements != null)
					{
			            for (Object element : expandedElements)
			            {
			                if (element instanceof IVirtualFileManager)
			                {
			                    ((IVirtualFileManager) element).disconnect();
			                }
			            }
					}
					return Status.OK_STATUS;
				}
				
			};
			job.setPriority(Job.BUILD);
			job.setSystem(true);
			job.schedule();

			// disposes all the images
			Iterator<Image> iter = images.values().iterator();
			while (iter.hasNext())
			{
				((Image) iter.next()).dispose();
			}
			
			identifierListener = null;
		}
		finally {
			super.dispose();
		}		
	}

	/**
	 * syncManagerEvent
	 * 
	 * @param obj
	 * @param actionId
	 */
	public void syncManagerEvent(final Object obj, final int actionId)
	{
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				switch (actionId)
				{
					case ISyncManagerChangeListener.EDIT:
						// NOTE: may want to consider "update" here instead
						refresh(obj);
						if (viewer != null && !viewer.getTree().isDisposed())
						{
							viewer.update(obj, null);
						}
						break;

					case ISyncManagerChangeListener.ADD:
						if (obj instanceof IVirtualFileManager && viewerIsAvailable())
						{
							IVirtualFileManager fileManager = (IVirtualFileManager) obj;
							ProtocolManager owningManager = fileManager.getProtocolManager();

							if (viewer != null && viewer.getTree() != null && !viewer.getTree().isDisposed()
									&& fileManager != null && owningManager != null)
							{
								viewer.add(owningManager, fileManager);
								refresh(owningManager);
								viewer.setExpandedState(owningManager, true);
								viewer.setSelection(new StructuredSelection(fileManager), true);
							}
						}
						break;

					case ISyncManagerChangeListener.DELETE:
						if (obj instanceof IVirtualFileManager && viewerIsAvailable())
						{
							IVirtualFileManager fileManager = (IVirtualFileManager) obj;
							ProtocolManager owningManager = fileManager.getProtocolManager();
							viewer.remove(owningManager, new Object[] { fileManager });
							refresh(owningManager);
						}
						break;

					default:
						refresh(null);
						break;
				}
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
		try
		{
			ImageUtils.loadImageCache(parent.getDisplay());
		}
		catch (Exception ex)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.FileExplorerView_UnableToLoadIconCache, ex);
		}

		viewer = createViewer(parent);
		treeListener = new ITreeViewerListener()
		{
		
			public void treeExpanded(TreeExpansionEvent e)
			{
			    // TreeViewer.getExpandedElements() doesn't include the item
                // that was just expanded, so needs to add it manually
				Object[] elements = viewer.getExpandedElements();
				expandedElements = new Object[elements.length + 1];
				int i;
				for (i = 0; i < expandedElements.length - 1; ++i)
				{
				    expandedElements[i] = elements[i];
				}
				expandedElements[i] = e.getElement();
			}
		
			public void treeCollapsed(TreeExpansionEvent e)
			{
			    expandedElements = viewer.getExpandedElements();
			}
		};
		viewer.addTreeListener(treeListener);
		drillDownAdapter = new DrillDownAdapter(viewer);

		initFilter();

		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, "com.aptana.ide.js.ui.FileExplorerView"); //$NON-NLS-1$

		createDragSource();
		createDropTarget();
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		// turn web file filtering off by default
		webFiltering = false;
		webFilterAction.setChecked(false);

		sortAction.setChecked(true);

		setInitialState();

		SyncManager.getSyncManager().addSyncManagerChangeEvent(this);

		// Not adding this means that other actions added to the toolbar
		// will not be updated properly on selection change.
		getSite().setSelectionProvider(viewer);

		/*
		 * From ResourceNavigator. These all may be useful at some point
		 * getSite().getPage().addPartListener(partListener); IWorkingSetManager workingSetManager =
		 * getPlugin().getWorkbench() .getWorkingSetManager();
		 * workingSetManager.addPropertyChangeListener(propertyChangeListener); if (memento != null)
		 * restoreState(memento); memento = null; // Set help for the view
		 * getSite().getWorkbenchWindow().getWorkbench().getHelpSystem().setHelp( viewer.getControl(),
		 * getHelpContextId());
		 */
		PreferenceUtils.registerBackgroundColorPreference(viewer.getControl(),
				"com.aptana.ide.core.ui.background.color.filesView"); //$NON-NLS-1$
		PreferenceUtils.registerForegroundColorPreference(viewer.getControl(),
				"com.aptana.ide.core.ui.foreground.color.filesView"); //$NON-NLS-1$
	}

	/**
	 * Callback from the new file/folder creation dialog.
	 */
    public void fileCreated(IVirtualFile targetFile)
    {
        getViewer().refresh(targetFile.getParentFile());
        if (targetFile.isFile())
        {
            openFileInEditor(targetFile);
        }
    }

	/**
	 * Creates the viewer.
	 * 
	 * @param parent
	 *            the parent composite
	 * @return TreeViewer
	 * @since 2.0
	 */
	protected TreeViewer createViewer(Composite parent)
	{
		final TreeViewer viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		// viewer.setUseHashlookup(true);
		initLabelProvider(viewer);
		initListeners(viewer);

		viewer.setContentProvider(new FileTreeContentProvider(viewer));
		viewer.setSorter(new NameSorter());
		viewer.setAutoExpandLevel(1);
		
		activityManager = PlatformUI
		.getWorkbench().getActivitySupport()
		.getActivityManager();
		
		identifierListener = new IIdentifierListener () {

			public void identifierChanged(IdentifierEvent identifierEvent) {
				if ( identifierEvent.hasEnabledChanged() ){
					Job job = new UIJob (Messages.FileExplorerView_Updating_view_job_title) {
						public IStatus runInUIThread(IProgressMonitor monitor) {
							if (FileExplorerView.this.viewer == null || FileExplorerView.this.viewer.getTree().isDisposed())
							{
								return Status.CANCEL_STATUS;
							}
							ViewerFilter[] filters = viewer.getFilters();
							viewer.resetFilters();
							for (int i = 0; i < filters.length; i++)
							{
								viewer.addFilter(filters[i]);
							}
							viewer.refresh();
							return Status.OK_STATUS;
						};
					};
					job.setPriority(UIJob.INTERACTIVE);
					job.schedule();
				}
				return;
			}
		};
	
		ViewerFilter filter = new ViewerFilter() {

			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				
				if (element instanceof ProtocolManager) {
					ProtocolManager manager = (ProtocolManager) element;
					String extensionId = manager.getExtensionId();
					String extensionPluginId = manager.getExtensionPluginId();

					if (extensionPluginId != null && extensionId != null
							&& extensionPluginId.length() > 0
							&& extensionId.length() > 0) {
						final IIdentifier id = activityManager
								.getIdentifier(extensionPluginId + "/" //$NON-NLS-1$
										+ extensionId);
						if ( id != null ) {
							id.addIdentifierListener(identifierListener);
							// Make sure we dispose the listener when needed
							viewer.getControl().addDisposeListener(new DisposeListener() {
								public void widgetDisposed(DisposeEvent e)
								{
									id.removeIdentifierListener(identifierListener);
								}
							});
							return id.isEnabled();
						}
					}
				}
				return true;
			}
			
		};

		
//		viewer.setFilters(new ViewerFilter[] {filter});
		viewer.addFilter(filter);
		viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				Object obj = getFirstSelectedItem();

				if (obj instanceof IVirtualFileManager)
				{
					try
					{
						obj = ((IVirtualFileManager) obj).getBaseFile();
					}
					catch (Exception e)
					{
						// Do nothing since virtual file manager does not have a base file
					}
				}

				if (obj instanceof IVirtualFile == false)
				{
					return;
				}

				IVirtualFile f = (IVirtualFile) obj;

				if (f.isDirectory())
				{
					setLastSelected(f.getPath());
				}
				else if (f.getParentFile() != null)
				{
					setLastSelected(f.getParentFile().getPath());
				}
				else
				{
					setLastSelected(null);
				}
			}

			private void setLastSelected(String path)
			{
				lastSelected = path;
				CoreUIPlugin.getDefault().getPreferenceStore().setValue(
						com.aptana.ide.core.ui.preferences.IPreferenceConstants.PREF_CURRENT_DIRECTORY,
						lastSelected != null ? lastSelected : ""); //$NON-NLS-1$
			}
		});

		try
		{
			viewer.setInput(getVirtualRoots(showLocalEndpoints));
		}
		catch (IOException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.FileExplorerView_ErrorCreatingFileView, e);
		}

		activityManagerListener = new IActivityManagerListener()
		{
			public void activityManagerChanged(ActivityManagerEvent activityManagerEvent)
			{
				try
				{
					viewer.setInput(getVirtualRoots(showLocalEndpoints));
				}
				catch (IOException e)
				{
					IdeLog.logError(CoreUIPlugin.getDefault(), Messages.FileExplorerView_ErrorCreatingFileView, e);
				}
			}
		};
		activityManager.addActivityManagerListener(activityManagerListener);
		return viewer;
	}

	/**
	 * initLabelProvider
	 * 
	 * @param viewer
	 */
	protected void initLabelProvider(TreeViewer viewer)
	{
		viewer.setLabelProvider(new DecoratingLabelProvider(new ViewLabelProvider(), CoreUIPlugin.getDefault()
				.getWorkbench().getDecoratorManager().getLabelDecorator()));
	}

	/**
	 * Adds the listeners to the viewer.
	 * 
	 * @param viewer
	 *            the viewer
	 * @since 2.0
	 */
	protected void initListeners(TreeViewer viewer)
	{
		viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				handleSelectionChanged(event);
			}
		});
		viewer.getControl().addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent event)
			{
				handleKeyPressed(event);
			}

			public void keyReleased(KeyEvent event)
			{
				handleKeyReleased(event);
			}
		});
		viewer.getTree().addMouseListener(fSelectionListener);
		viewer.getTree().addSelectionListener(fSelectionListener);
	}

	/**
	 * Handles a key press event from the viewer. Delegates to the action group.
	 * 
	 * @param event
	 *            the key event
	 * @since 2.0
	 */
	protected void handleKeyPressed(KeyEvent event)
	{
		if (event.character == SWT.DEL && event.stateMask == 0)
		{
			if (deleteAction.isEnabled())
			{
				deleteAction.run();
			}

			// Swallow the event.
			event.doit = false;
		}
	}

	/**
	 * Handles a selection changed event from the viewer. Updates the status line and the action bars, and links to
	 * editor (if option enabled).
	 * 
	 * @param event
	 *            the selection event
	 * @since 2.0
	 */
	protected void handleSelectionChanged(SelectionChangedEvent event)
	{
		final IStructuredSelection sel = (IStructuredSelection) event.getSelection();
		updateStatusLine(sel);
		updateActionBars(sel);
	}

	/**
	 * Updates the action bar actions.
	 * 
	 * @param selection
	 *            the current selection
	 * @since 2.0
	 */
	protected void updateActionBars(IStructuredSelection selection)
	{
		getViewSite().getActionBars().updateActionBars();
	}

	/**
	 * Updates the message shown in the status line.
	 * 
	 * @param selection
	 *            the current selection
	 */
	protected void updateStatusLine(IStructuredSelection selection)
	{
		String msg = getStatusLineMessage(selection);
		getViewSite().getActionBars().getStatusLineManager().setMessage(msg);
	}

	/**
	 * Returns the message to show in the status line.
	 * 
	 * @param selection
	 *            the current selection
	 * @return the status line message
	 * @since 2.0
	 */
	protected String getStatusLineMessage(IStructuredSelection selection)
	{
		if (selection.size() == 1)
		{
			Object o = selection.getFirstElement();
			if (o instanceof IVirtualFile)
			{
				return ((IVirtualFile) o).getAbsolutePath();
			}
			else
			{
				return ResourceNavigatorMessages.ResourceNavigator_oneItemSelected;
			}
		}
		if (selection.size() > 1)
		{
			return NLS.bind(ResourceNavigatorMessages.ResourceNavigator_statusLine, String.valueOf(selection.size()));
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * Handles a key release in the viewer. Does nothing by default.
	 * 
	 * @param event
	 *            the key event
	 * @since 2.0
	 */
	protected void handleKeyReleased(KeyEvent event)
	{
	}

	/**
	 * 
	 */
	private void createDragSource()
	{
		// Create the drag source on the tree
		DragSource ds = new DragSource(viewer.getControl(), DND.DROP_COPY | DND.DROP_MOVE);
		ds.setTransfer(new Transfer[] { LocalSelectionTransfer.getTransfer(), FileTransfer.getInstance() });
		ds.addDragListener(new DragSourceAdapter()
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.dnd.DragSourceAdapter#dragStart(org.eclipse.swt.dnd.DragSourceEvent)
			 */
			public void dragStart(DragSourceEvent event)
			{
				IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
				dragSelection = ((IStructuredSelection) selection).toArray();

				for (int i = 0; i < dragSelection.length; i++)
				{
					Object element = dragSelection[i];

					if (element instanceof ProtocolManager)
					{
						event.doit = false;
						dragSelection = null;
						break;
					}
					else if (element instanceof IVirtualFile)
					{
						IVirtualFile f = (IVirtualFile) element;
						if (f.getFileManager() != null && f.getFileManager().getBasePath() != null)
						{
							IVirtualFile base = f.getFileManager().getBaseFile();
							if (base == f)
							{
								event.doit = false;
								dragSelection = null;
								break;
							}
						}
					}
				}
			}

			public void dragFinished(DragSourceEvent event)
			{
				super.dragFinished(event);
				dragSelection = null;
			}

			public void dragSetData(DragSourceEvent event)
			{
				IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

				if (FileTransfer.getInstance().isSupportedType(event.dataType))
				{
					Object[] items = selection.toArray();
					String[] data = new String[items.length];
					for (int i = 0; i < items.length; i++)
					{
						Object element = items[i];

						if (element instanceof IVirtualFile)
						{
							data[i] = ((IVirtualFile) element).getAbsolutePath();
						}
					}
					event.data = data;
				}
			}
		});
	}

	/**
	 * 
	 */
	private void createDropTarget()
	{
		DropTarget dt = new DropTarget(viewer.getControl(), DND.DROP_DEFAULT | DND.DROP_COPY);
		dt.setTransfer(new Transfer[] { LocalSelectionTransfer.getTransfer(), FileTransfer.getInstance() });
		dt.addDropListener(new DropTargetAdapter()
		{

			public void dropAccept(DropTargetEvent event)
			{
				super.dropAccept(event);
			}

			public void dragEnter(DropTargetEvent event)
			{
				// copy is the default operation
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

				dragDetail = event.detail;
			}

			public void drop(DropTargetEvent event)
			{
				handleDrop(event);
			}

			public void dragOver(DropTargetEvent event)
			{
				if (CoreUIUtils.onMacOSX == false)
				{
					event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL | DND.FEEDBACK_EXPAND;
				}

				dragDetail = event.detail;
			}
		});
	}

	/**
	 * handleDrop
	 * 
	 * @param event
	 */
	protected void handleDrop(DropTargetEvent event)
	{
		this.alwaysOverwrite = false;
		
		if (event.data == null)
		{
			Object treeData = ((TreeItem) event.item).getData();
			IVirtualFile dest = null;

			if (treeData instanceof IVirtualFileManager)
			{
				IVirtualFileManager vfm = (IVirtualFileManager) treeData;
				
				if (vfm.getBasePath() == null)
				{
					try
					{
						if (vfm.getBaseFile().exists() == false)
						{
							CoreUIUtils.showMessage(Messages.FileExplorerView_DestinationDoesNotExist);
							this.refresh(null);
							return;
						}
					}
					catch (ConnectionException e)
					{
					}
				}

				dest = vfm.getBaseFile();
			}
			else if (treeData instanceof IVirtualFile)
			{
				IVirtualFile vf = (IVirtualFile) treeData;
				
				try
				{
					if (vf.exists() == false)
					{
						CoreUIUtils.showMessage(Messages.FileExplorerView_DestinationDoesNotExist);
						this.refresh(null);
						return;
					}
				}
				catch (ConnectionException e)
				{
				}
				
				dest = (IVirtualFile) treeData;
			}

			Object[] sources = dragSelection;

			if (dest == null)
			{
				CoreUIUtils.showMessage(Messages.FileExplorerView_UnableToDrop);
				return;
			}

			if (dest.isDirectory() == false)
			{
				dest = dest.getParentFile();
			}

			if (sources == null)
			{
				CoreUIUtils.showMessage(Messages.FileExplorerView_CopyingBetweenFileViewsIsNotCurrentlySupported);
				return;
			}

			// Get a list of only IVirtualFiles
			List<Object> filteredSourcesTemp = new ArrayList<Object>();
			
			for (int i = 0; i < sources.length; i++)
			{
				Object object = sources[i];
				
				if (object instanceof IVirtualFile)
				{
					filteredSourcesTemp.add(object);
				}
				else if (object instanceof IVirtualFileManager)
				{
					filteredSourcesTemp.add(((IVirtualFileManager) object).getBaseFile());
				}
				else
				{
					MessageDialog.openError(
						viewer.getControl().getShell(),
						Messages.FileExplorerView_CopyFailed,
						Messages.FileExplorerView_UnableToDropSourceOntoDestination
					);
					
					return;
				}
			}

			// Remove any files from the copy where the new parent is the same
			// as the current parent
			IVirtualFile[] filteredSources = (IVirtualFile[]) filteredSourcesTemp.toArray(new IVirtualFile[0]);
			
			if (filteredSources != null)
			{
				filteredSources = VirtualFile.removeDuplicateFile(filteredSources, dest);
			}

			// If the list is empty, then nothing to copy
			if (filteredSources == null || filteredSources.length == 0)
			{
				MessageDialog.openInformation(viewer.getControl().getShell(), Messages.FileExplorerView_CopyStopped,
						Messages.FileExplorerView_NothingToCopy);
				return;
			}

			if (dragDetail == DND.DROP_COPY)
			{
				boolean res = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
						Messages.FileExplorerView_AptanaIDE, StringUtils.format(
								Messages.FileExplorerView_AreYouSureYouWishToCopyToDirectory, new String[] {
										String.valueOf(sources.length), dest.getName() }));

				if (res == true)
				{
					ProgressMonitorDialog pmd = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
					try
					{
						final IVirtualFile[] sourceList = filteredSources;
						final IVirtualFile destination = dest;
						pmd.run(true, true, new IRunnableWithProgress()
						{
							public void run(final IProgressMonitor monitor) throws InvocationTargetException,
									InterruptedException
							{
								monitor.beginTask(Messages.FileExplorerView_CopyingFiles, sourceList.length);
								for (int i = 0; i < sourceList.length; i++)
								{
									IVirtualFile sfile = sourceList[i];

									
									try
									{
										copyVirtualFile(monitor, sfile, destination);
									}
									catch (Exception e)
									{
										throw new InvocationTargetException(e, StringUtils.format(Messages.FileExplorerView_UnableToDropOnto,
												new String[] { sfile.getAbsolutePath(), destination.getAbsolutePath() }));
									} finally {
										monitor.worked(1);
									}

								}
								monitor.done();
							}
						});
					}
					catch (InvocationTargetException e)
					{
						if (e.getCause() instanceof ConnectionException)
						{
							CoreUIUtils.fixConnection(dest.getFileManager());
						}
						else
						{
							CoreUIUtils.showError(e.getMessage(), e, true);
						}
					}
					catch (InterruptedException e)
					{
						// canceled
					}

					viewer.refresh(treeData);

				}
			}
			else if (dragDetail == DND.DROP_MOVE)
			{
				MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), Messages.FileExplorerView_AptanaIDE,
						StringUtils.format(Messages.FileExplorerView_OkToMoveFilesToDirectory, new String[] {
								String.valueOf(sources.length), dest.getName() }));
			}
		}
		else
		{
			// TODO: support String[] drops (i.e. from file system)
		}
	}

	/**
	 * @param source
	 * @param dest
	 * @throws IOException
	 */
	private void copyVirtualFile(IProgressMonitor monitor, IVirtualFile source, IVirtualFile dest) throws IOException,
			ConnectionException, VirtualFileManagerException
	{
		IVirtualFileManager destFileManager = dest.getFileManager();
		String path = dest.getAbsolutePath() + destFileManager.getFileSeparator() + source.getName();

		if (source.isFile())
		{
			IVirtualFile targetFile = destFileManager.createVirtualFile(path);
			boolean copyFile = false;
			if (!alwaysOverwrite && targetFile.exists())
			{
				copyFile = overwriteConfirmation(path);
			}
			if (alwaysOverwrite || copyFile || !targetFile.exists())
			{
				monitor.subTask(StringUtils.ellipsify(Messages.FileExplorerView_Copying + StringUtils.SPACE
						+ source.getAbsolutePath()));
				InputStream in = source.getStream();
				final SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, 1);
				final long[] lastBytes = new long[] { 0 };
				subMonitor.beginTask(source.getAbsolutePath(), (int) (source.getSize()/1024));
				try {
					targetFile.putStream(in, new IFileProgressMonitor() {
						public void bytesTransferred(long bytes) {
							long delta = bytes - lastBytes[0];
							lastBytes[0] = bytes;
							subMonitor.worked((int) (delta/1024));
						}
	
						public void done() {
							subMonitor.done();
						}
					});
				} finally {
					subMonitor.done();
				}
			}
		}
		else
		// is directory
		{
			monitor.subTask(StringUtils.ellipsify(Messages.FileExplorerView_Copying + StringUtils.SPACE
					+ source.getAbsolutePath()));
			IVirtualFile newDestDir = dest.getFileManager().createVirtualDirectory(path);

			SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, 1);
			try
			{
				if (!newDestDir.exists())
				{
					dest.getFileManager().createLocalDirectory(newDestDir);
				}

				IVirtualFile[] files = getVirtualFilesProtected(source);
				
				subMonitor.beginTask(source.getAbsolutePath(), files.length);

				for (int i = 0; i < files.length; i++)
				{
					IVirtualFile file = files[i];
					copyVirtualFile(subMonitor, file, newDestDir);
				}
			}
			catch (ConnectionException e)
			{
				String errorMsg = StringUtils.format(Messages.FileExplorerView_UnableToConnectTo, dest.getFileManager()
						.getNickName());
				CoreUIUtils.showError(errorMsg, e);
			}
			catch (VirtualFileManagerException e)
			{
				String errorMsg = StringUtils.format(Messages.FileExplorerView_ErrorCopyingFileToDestination,
						new String[] { source.getAbsolutePath(), dest.getAbsolutePath() });
				CoreUIUtils.showError(errorMsg, e);
			}
			finally {
				subMonitor.done();
			}
		}
	}

	/**
	 * @param source
	 * @param dest
	 * @throws IOException
	 */
	private void deleteVirtualFile(IProgressMonitor monitor, IVirtualFile source) throws IOException,
			ConnectionException, VirtualFileManagerException
	{
		monitor.subTask(StringUtils.ellipsify(Messages.FileExplorerView_Deleting + source.getAbsolutePath()));

		if (source.isFile())
		{
			source.delete();
		}
		else
		// is directory
		{
			IVirtualFile[] files = getVirtualFilesProtected(source);

			for (int i = 0; i < files.length; i++)
			{
				if (monitor.isCanceled()) {
					return;
				}
				IVirtualFile file = files[i];
				deleteVirtualFile(monitor, file);
			}

			if (!source.delete())
			{
				CoreUIUtils.showMessage(StringUtils.format(Messages.FileExplorerView_UnableToDeleteFile, source
						.getAbsolutePath()));
			}
		}
	}

	/**
	 * getVirtualFilesProtected
	 * 
	 * @param source
	 * @return IVirtualFile[]
	 * @throws IOException
	 */
	public static IVirtualFile[] getVirtualFilesProtected(IVirtualFile source) throws IOException
	{
		if (source == null)
		{
			throw new IllegalArgumentException(Messages.FileExplorerView_SourceCannotBeNull);
		}

		IVirtualFile[] files = new IVirtualFile[0];
		try
		{
			files = source.getFiles(false, true);
		}
		catch (ConnectionException ex)
		{
			if (source.getFileManager() != null)
			{
				CoreUIUtils.fixConnection(source.getFileManager());
				if (source.getFileManager().isConnected())
				{
					files = getVirtualFilesProtected(source);
				}
			}
			else
			{
				throw new IllegalArgumentException(Messages.FileExplorerView_SourceNoFileManagerAttached);
			}
		}
		catch (NullPointerException ex)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), StringUtils.format(
					Messages.FileExplorerView_EncounteredNullWhenRetrievingFiles, source.getAbsolutePath()));
		}

		List<IVirtualFile> newFiles = new ArrayList<IVirtualFile>();
		if (files.length > 0)
		{
			// Here we filter out all files named '.' or '..'
			for (int i = 0; i < files.length; i++)
			{
				IVirtualFile file = files[i];
				if (!file.getName().equals(".") && !file.getName().equals("..")) //$NON-NLS-1$ //$NON-NLS-2$
				{
					newFiles.add(file);
				}
			}
			files = (IVirtualFile[]) newFiles.toArray(new IVirtualFile[0]);
		}

		return files;
	}

	/**
	 * 
	 */
	private void initFilter()
	{
		IPreferenceStore store = CoreUIPlugin.getDefault().getPreferenceStore();
		String editors = store.getString(IPreferenceConstants.PREF_FILE_EXPLORER_WEB_FILES);
		String[] webFiles = new String[0];
		if (editors != null && !"".equals(editors)) //$NON-NLS-1$
		{
			webFiles = editors.split(";"); //$NON-NLS-1$
		}
		viewer.addFilter(new AllowOnlyWebFilesFilter(webFiles));
	}

	/**
	 * getVirtualRoots
	 * 
	 * @return Object[]
	 * @throws IOException
	 */
	public static Object[] getVirtualRoots(boolean showLocalEndpoints) throws IOException
	{
		Image desktopIcon = null;
		List<Object> list = new ArrayList<Object>();

		if (transientShortcuts.size() == 0)
		{
			LocalProtocolManager lpm = LocalProtocolManager.getInstance();
			IVirtualFileManager ivfm = lpm.createFileManager();
			ivfm.setBasePath(LocalProtocolManager.FileSystemRoots);
			ivfm.setNickName("Computer"); //$NON-NLS-1$
			ivfm.setHidden(true);
			ivfm.setTransient(true);

			String localizedDesktop = DESKTOP;

			// Show Desktop and My Computer and My Network Places on Windows
			// Only
			if (CoreUIUtils.runningOnWindows)
			{

				try
				{
					String tempLocalizedDesktop = PlatformUtils
							.expandEnvironmentStrings(PlatformUtils.DESKTOP_DIRECTORY);
					if (tempLocalizedDesktop != null)
					{
						IPath path = new Path(tempLocalizedDesktop);
						localizedDesktop = path.lastSegment();
					}
				}
				catch (Exception ex)
				{
					IdeLog.logError(FilePlugin.getDefault(), Messages.FileExplorerView_LocalizedError, ex);
				}

				IVirtualFile[] files = getVirtualFilesProtected(ivfm.getBaseFile());

				for (int i = 0; i < files.length; i++)
				{
					LocalFile file = (LocalFile) files[i];

					if (file.getName().equals(localizedDesktop) || file.getFile().getName().equals(localizedDesktop))
					{
						if (desktopIcon == null)
						{
							desktopIcon = file.getImage();
						}

						IVirtualFile[] desktopFiles = getVirtualFilesProtected(file);

						for (int j = 0; j < desktopFiles.length; j++)
						{
							ILocalFile desktopFile = (ILocalFile) desktopFiles[j];
							String path = desktopFile.getFile().getName();
							if (path.equals(MY_COMPUTER_GUID) || path.equals(MY_NETWORK_PLACES_GUID)
									|| path.startsWith("::")) //$NON-NLS-1$
							{
								transientShortcuts.add(desktopFile);
							}
						}
					}
				}
			}
			// Show root volumes on Mac or Linux
			else
			{
				if (CoreUIUtils.onMacOSX)
				{
					ivfm.setBasePath("/Volumes"); //$NON-NLS-1$
				}

				IVirtualFile[] files = getVirtualFilesProtected(ivfm.getBaseFile());

				for (IVirtualFile file : files)
				{
					file.setImage(ImageUtils.getDriveIcon());
					transientShortcuts.add(file);
				}
			}

			String basePath = null;
			if (CoreUIUtils.runningOnWindows)
			{
				basePath = PlatformUtils.expandEnvironmentStrings(PlatformUtils.DESKTOP_DIRECTORY);
			}
			else
			{
				basePath = System.getProperty("user.home") + java.io.File.separator + "Desktop"; //$NON-NLS-1$ //$NON-NLS-2$
			}

			File f = new File(basePath);
			if (f.exists())
			{
				ivfm = lpm.createFileManager();
				ivfm.setNickName(localizedDesktop);
				ivfm.setImage(desktopIcon);
				ivfm.setBasePath(basePath);
				ivfm.setTransient(true);
				ivfm.setHidden(true);
				transientShortcuts.add(ivfm);
			}
		}

		for (Iterator<Object> iter = transientShortcuts.iterator(); iter.hasNext();)
		{
			list.add(iter.next());
		}

		ProtocolManager[] pm = ProtocolManager.getPrototcolManagers();
		if (pm != null)
		{
			for (ProtocolManager manager : pm)
			{
				if (manager.isHidden() == false || showLocalEndpoints)
				{
					list.add(manager);
				}
			}
		}

		return list.toArray();
	}

	private void setInitialState()
	{
		if (savedElements != null)
		{
			viewer.setExpandedElements(savedElements);
		}
	}

	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				FileExplorerView.this.fillContextMenu(manager);
			}
		});

		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
		bars.updateActionBars();
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(newAction);
		manager.add(new Separator());
		manager.add(refreshAllAction);
		manager.add(webFilterAction);
		manager.add(sortAction);
		manager.add(collapseAction);
		manager.add(newViewAction);
		manager.add(new Separator());
		manager.add(actionShowHideLocal);
	}

	private void fillContextMenu(IMenuManager manager)
	{
		Object obj = getFirstSelectedItem();

		if (obj instanceof LocalFile || obj instanceof LocalFileManager)
		{
			boolean addEntry = true;

			if (obj instanceof LocalFile)
			{
				addEntry = ((LocalFile) obj).isDirectory();
			}

			if (addEntry)
			{
				manager.add(newAction);
			}
		}

		if (obj instanceof ProtocolManager)
		{
			if (((ProtocolManager) obj).canAddFileManagers())
			{
				addVirtualFileManager.setText(StringUtils.format(Messages.FileExplorerView_AddNew,
						((ProtocolManager) obj).getFileManagerName()));
				manager.add(addVirtualFileManager);
//				if (CoreUIPlugin.isKeyValid())
//				{
//					manager.add(actionNewVirtualProject);
//				}
			}
		}

		if (obj instanceof IVirtualFile)
		{
			MenuManager submenu = new MenuManager(ResourceNavigatorMessages.ResourceNavigator_openWith, CoreUIPlugin
					.getPluginId()
					+ ".OpenWithSubMenu"); //$NON-NLS-1$
			IVirtualFile vFile = (IVirtualFile) obj;

			if (vFile.isFile())
			{
				manager.add(openAction);
				submenu.add(new OpenWithMenuExternal(this, (IVirtualFile) obj));
				manager.add(submenu);
			}

			if (vFile.isDirectory() && !(vFile instanceof LocalFile))
			{
				manager.add(actionNewVirtualFile);
				manager.add(actionNewVirtualFolder);

//				if (CoreUIPlugin.isKeyValid())
//				{
//					manager.add(actionNewVirtualProject);
//				}
			}
		}

		if (obj instanceof IVirtualFileManager && !(obj instanceof LocalFileManager))
		{
			manager.add(actionNewVirtualFile);
			manager.add(actionNewVirtualFolder);
			manager.add(actionNewVirtualProject);
		}
		
		if (obj instanceof ProtocolManager && !((ProtocolManager) obj).allowNavigation()) {
			// No navigation actions
		} else {
			manager.add(new Separator());
			drillDownAdapter.addNavigationActions(manager);
		}
		manager.add(new Separator());

		if (obj instanceof IVirtualFile || (obj instanceof IVirtualFileManager && ((IVirtualFileManager) obj).isEditable()))
		{
			manager.add(deleteAction);
		}
		if (obj instanceof IVirtualFile)
		{
			manager.add(renameAction);
		}

		manager.add(new Separator());
		manager.add(refreshAction);

		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        manager.add(new Separator(GROUP_PROJECT));

		if (obj instanceof IVirtualFileManager)
		{
			if (((IVirtualFileManager) obj).isEditable())
			{
				manager.add(new Separator());
				manager.add(editVirtualFileManager);
			}
		}

		if (obj instanceof IVirtualFile)
		{
			manager.add(new Separator());
			manager.add(editVirtualFileProperties);
		}
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(newAction);
		manager.add(new Separator());
		manager.add(collapseAction);
		manager.add(refreshAllAction);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);

		// Other plug-ins can contribute there actions here
		Separator sep = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
		manager.add(sep);

		// Other plug-ins can contribute there actions here
		sep = new Separator(IWorkbenchActionConstants.HELP_END);
		manager.add(sep);
	}

	private void makeActions()
	{
		createRenameAction();
		createDeleteAction();
		createNewFolderAction();
		createNewAction();
		createOpenAction();
		createRefreshAction();
		createRefreshAllAction();
		createSortAction();
		createCollapseAction();
		createWebFilterAction();
		createDoubleClickAction();
		createNewViewAction();
		createEditVirtualFileManagerAction();
		createAddVirtualFileManagerAction();
		createEditVirtualFilePropertiesAction();
		createShowHideLocalAction();
		createNewVirtualFileAction();
		createNewVirtualFolderAction();
		createNewVirtualProjectAction();
	}

	private void createNewViewAction()
	{
		newViewAction = new Action()
		{
			public void run()
			{
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

				try
				{
					page.showView("com.aptana.ide.js.ui.views.FileExplorerView", new Integer( //$NON-NLS-1$
							FileExplorerView.secondaryIdCounter++).toString(), IWorkbenchPage.VIEW_CREATE);
				}
				catch (PartInitException e)
				{
					CoreUIUtils.showError(Messages.FileExplorerView_ErrorCreatingDuplicateFileView, e);
				}
			}
		};
		newViewAction.setText(Messages.FileExplorerView_CreateANewFileExplorer);
		newViewAction.setImageDescriptor(fnewViewActionIconDescriptor);
	}

	/**
	 * 
	 */
	private void createDoubleClickAction()
	{
		doubleClickAction = new Action()
		{
			public void run()
			{
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();

				if (obj == null)
				{
					return;
				}

				if (obj instanceof IVirtualFile && ((IVirtualFile) obj).isFile())
				{
					IVirtualFile f = (IVirtualFile) obj;
					openFileInEditor(f);
				}
				else
				{
					if (viewer.getExpandedState(obj))
					{
						viewer.collapseToLevel(obj, 1);
					}
					else
					{
						viewer.expandToLevel(obj, 1);
					}
				}
			}
		};
	}

	private void createShowHideLocalAction()
	{
		actionShowHideLocal = new CheckBoxAction(Messages.FileExplorerView_ShowHideLocalConnections)
		{
			public void run()
			{
				showLocalEndpoints = !showLocalEndpoints;
				try
				{
					viewer.setInput(getVirtualRoots(showLocalEndpoints));
				}
				catch (IOException e)
				{
					IdeLog.logError(CoreUIPlugin.getDefault(), Messages.FileExplorerView_ErrorCreatingFileView, e);
				}
			}
		};
		actionShowHideLocal.setToolTipText(Messages.FileExplorerView_ShowHideLocalConnections);
		actionShowHideLocal.setImageDescriptor(fNewFolderIconDescriptor);
		actionShowHideLocal.setChecked(showLocalEndpoints);
	}

	/**
	 * 
	 */
	private void createWebFilterAction()
	{
		webFilterAction = new CheckBoxAction(Messages.FileExplorerView_ShowWebFilesOnly)
		{

			public void run()
			{
				if (isChecked())
				{
					webFiltering = true;
				}
				else
				{
					webFiltering = false;
				}

				refresh(null);
			}
		};
		webFilterAction.setToolTipText(Messages.FileExplorerView_ShowWebFilesOnlyTT);
		webFilterAction.setImageDescriptor(fWebFilesIconDescriptor);
	}

	/**
	 * 
	 */
	private void createCollapseAction()
	{
		collapseAction = new Action()
		{
			public void run()
			{
				viewer.collapseAll();
			}
		};
		collapseAction.setText(Messages.FileExplorerView_CollapseAll);
		collapseAction.setToolTipText(Messages.FileExplorerView_CollapseAllTT);
		collapseAction.setImageDescriptor(fCollapseIconDescriptor);
	}

	/**
	 * 
	 */
	private void createSortAction()
	{
		sortAction = new CheckBoxAction(Messages.FileExplorerView_SortFiles)
		{

			public void run()
			{
				if (isChecked())
				{
					viewer.setSorter(new NameSorter());
				}
				else
				{
					viewer.setSorter(null);
				}
			}
		};
		sortAction.setToolTipText(Messages.FileExplorerView_SortFilesTT);
		sortAction.setImageDescriptor(fSortIconDescriptor);
	}

	/**
	 * 
	 */
	private void createRefreshAction()
	{
		refreshAction = new PushButtonAction(Messages.FileExplorerView_Refresh)
		{
			public void run()
			{
				ISelection selection = viewer.getSelection();
				if (selection.isEmpty()) {
					viewer.refresh();
					return;
				}
				IStructuredSelection items = (IStructuredSelection) selection;
				for (Iterator<Object> iter = items.iterator(); iter.hasNext();)
				{
					Object element = iter.next();
					if (element instanceof IVirtualFileManager)
					{
						IVirtualFileManager vfm = (IVirtualFileManager) element;
						vfm.refresh();
					}
					viewer.refresh(element);
				}
			}
		};
		refreshAction.setToolTipText(Messages.FileExplorerView_RefreshTT);
		refreshAction.setImageDescriptor(fRefreshIconDescriptor);
		getViewSite().getActionBars().setGlobalActionHandler(
				ActionFactory.REFRESH.getId(), refreshAction);
	}

	/**
	 * 
	 */
	private void createRefreshAllAction()
	{
		refreshAllAction = new PushButtonAction(Messages.FileExplorerView_Refresh)
		{
			public void run()
			{
				viewer.refresh(null);
			}
		};
		refreshAllAction.setToolTipText(Messages.FileExplorerView_RefreshTT);
		refreshAllAction.setImageDescriptor(fRefreshIconDescriptor);
	}

	/**
	 * 
	 */
	private void createOpenAction()
	{
		openAction = new PushButtonAction(Messages.FileExplorerView_Open)
		{
			public void run()
			{
				Object o = getFirstSelectedItem();
				if (o instanceof IVirtualFile)
				{
					openFileInEditor((IVirtualFile) o);
				}
			}
		};
	}

	private void createNewVirtualFileAction()
	{
		actionNewVirtualFile = new PushButtonAction(Messages.FileExplorerView_NewFile)
		{
			public void run()
			{
				Object o = getFirstSelectedItem();
				IVirtualFileManager manager = null;
				if (o instanceof IVirtualFileManager)
				{
					manager = (IVirtualFileManager) o;
					o = manager.getBaseFile();
				}
				if (o instanceof IVirtualFile)
				{
					IVirtualFile folder = (IVirtualFile) o;
					if (!folder.canWrite())
					{
						MessageBox box = new MessageBox(getSite().getShell(), SWT.OK | SWT.ICON_ERROR);
						box.setText(Messages.FileExplorerView_ReadOnlyText);
						box.setMessage(Messages.FileExplorerView_ReadOnlyMessage);
						box.open();
						return;
					}
					NewVirtualFileDialog dialog = new NewVirtualFileDialog(folder, getSite().getShell(),
							true, FileExplorerView.this);
					int rc = dialog.open();
					if (rc == NewVirtualFileDialog.OK && manager != null)
					{
						viewer.refresh(manager);
					}
				}
			}
		};
	}

	private void createNewVirtualFolderAction()
	{
		actionNewVirtualFolder = new PushButtonAction(Messages.FileExplorerView_NewFolder)
		{
			public void run()
			{
				Object o = getFirstSelectedItem();
				IVirtualFileManager manager = null;
				if (o instanceof IVirtualFileManager)
				{
					manager = (IVirtualFileManager) o;
					o = manager.getBaseFile();
				}
				if (o instanceof IVirtualFile)
				{
					IVirtualFile folder = (IVirtualFile) o;
					if (!folder.canWrite())
					{
						MessageBox box = new MessageBox(getSite().getShell(), SWT.OK | SWT.ICON_ERROR);
						box.setText(Messages.FileExplorerView_ReadOnlyText);
						box.setMessage(Messages.FileExplorerView_ReadOnlyMessage);
						box.open();
						return;
					}
					NewVirtualFileDialog dialog = new NewVirtualFileDialog(folder, getSite().getShell(),
							false, FileExplorerView.this);
					int rc = dialog.open();
					if (rc == NewVirtualFileDialog.OK && manager != null)
					{
						viewer.refresh(manager);
					}
				}
			}
		};
	}

	private void createNewVirtualProjectAction()
	{
		actionNewVirtualProject = new PushButtonAction(Messages.FileExplorerView_CreateProject)
		{
			public void run()
			{
				IWizardDescriptor descriptor = PlatformUI.getWorkbench().getNewWizardRegistry().findWizard(
						"com.aptana.ide.wizards.RemoteProjectWizard"); //$NON-NLS-1$
				if (descriptor != null)
				{
					IWorkbenchWizard wizard;
					try
					{
						wizard = descriptor.createWizard();
						IStructuredSelection selectionToPass = (IStructuredSelection) viewer.getSelection();
						wizard.init(FilePlugin.getDefault().getWorkbench(), selectionToPass);
						if (wizard instanceof IWizard)
						{
							WizardDialog dialog = new WizardDialog(Display.getDefault().getActiveShell(),
									(IWizard) wizard);
							dialog.create();
							if (wizard.getPageCount() > 0)
							{
								dialog.open();
							}
						}
					}
					catch (Exception e)
					{
						IdeLog.logError(FilePlugin.getDefault(), Messages.FileExplorerView_OpenRemoteError, e);
					}

				}
			}
		};
	}

	/**
	 * 
	 */
	private void createNewAction()
	{
		newAction = new NewButtonAction(CoreStrings.NEW)
		{
			public void run()
			{
				UntitledTextFileWizard w = new UntitledTextFileWizard();
				w.init(getViewSite().getWorkbenchWindow().getWorkbench(), (IStructuredSelection) viewer.getSelection());
				w.performFinish();
				w.dispose();
			}
		};

		newAction.setToolTipText(Messages.FileExplorerView_NewTT);
		newAction.setImageDescriptor(fNewIconDescriptor);
	}

	/**
	 * 
	 */
	private void createEditVirtualFileManagerAction()
	{
		editVirtualFileManager = new Action(Messages.FileExplorerView_Properties)
		{
			public void run()
			{
				Object obj = getFirstSelectedItem();
				if (obj instanceof IVirtualFileManager)
				{
					CoreUIUtils.editVirtualFileManagerProperties((IVirtualFileManager) obj);
				}
			}
		};

		editVirtualFileManager.setToolTipText(Messages.FileExplorerView_PropertiesForConnection);
	}

	/**
	 * 
	 */
	private void createAddVirtualFileManagerAction()
	{
		addVirtualFileManager = new Action(Messages.FileExplorerView_PropertiesTT2)
		{
			public void run()
			{
				Object obj = getFirstSelectedItem();
				if (obj instanceof ProtocolManager)
				{
					ProtocolManager pm = (ProtocolManager) obj;
					IVirtualFileManagerDialog nld = pm.createPropertyDialog(viewer.getTree().getShell(),
							SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
					if (nld != null)
					{
						// create virtual file manager
						IVirtualFileManager vfm = pm.createFileManager(false);

						// build a list of names already in use
						IVirtualFileManager[] managers = (IVirtualFileManager[]) SyncManager.getSyncManager().getItems(
								vfm.getClass());
						Set<String> names = new HashSet<String>();

						for (int i = 0; i < managers.length; i++)
						{
							names.add(managers[i].getNickName());
						}

						// build default name
						String nickName = StringUtils.format(Messages.FileExplorerView_AddNewFileManager, pm
								.getFileManagerName());

						// make sure it is unique
						if (names.contains(nickName))
						{
							String base = nickName;
							int index = 1;

							nickName = base + "-" + Integer.toString(index); //$NON-NLS-1$

							while (names.contains(nickName))
							{
								index++;
								nickName = base + "-" + Integer.toString(index); //$NON-NLS-1$
							}
						}

						// set nickname
						vfm.setNickName(nickName);
						nld.setItem(vfm, true);
						IVirtualFileManager item = nld.open();

						// Item will be null if we cancelled out of previous dialog
						if (item != null)
						{
							// There is an option that the protocol manager was replaced when the user set up
							// the FTP. So we make sure that the right pm is called.
							ProtocolManager pmInItem = item.getProtocolManager();
							pmInItem.addFileManager(item);

							// NOTE: addFileManager calls refresh as a side-effect
							// refresh(obj);
						}
					}
				}
			}
		};

		addVirtualFileManager.setToolTipText(Messages.FileExplorerView_AddANewItem2);
	}

	/**
	 * 
	 */
	private void createEditVirtualFilePropertiesAction()
	{
		editVirtualFileProperties = new Action(Messages.FileExplorerView_Properties)
		{
			public void run()
			{
				Object obj = getFirstSelectedItem();
				if (obj instanceof IVirtualFile)
				{
					IVirtualFile item = (IVirtualFile) obj;

					item.editProperties(viewer.getTree().getShell());
				}
			}
		};

		addVirtualFileManager.setToolTipText(Messages.FileExplorerView_PropertiesTT3);
	}

	/**
	 * 
	 */
	private void createNewFolderAction()
	{
		newFolderAction = new PushButtonAction(Messages.FileExplorerView_NewFolder)
		{
			public void run()
			{
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();

				IVirtualFile file = getFile(obj);

				if (file != null)
				{
					String path = file.getAbsolutePath();

					if (path.startsWith("::")) //$NON-NLS-1$
					{
						CoreUIUtils.showMessage(Messages.FileExplorerView_CannotCreateFolderInThisSystemDirectory);
						return;
					}

					String baseDir;

					if (file.isDirectory())
					{
						baseDir = file.getAbsolutePath();
					}
					else
					{
						baseDir = file.getParentFile().getAbsolutePath();
					}

					InputDialog input = new InputDialog(getSite().getShell(), Messages.FileExplorerView_NewFolderName,
							Messages.FileExplorerView_EnterFolderName, "", null); //$NON-NLS-1$

					if (input.open() == Window.OK)
					{
						path = baseDir + file.getFileManager().getFileSeparator() + input.getValue();

						IVirtualFile newDir = file.getFileManager().createVirtualDirectory(path);

						try
						{
							if (!newDir.exists())
							{
								file.getFileManager().createLocalDirectory(newDir);
							}

							viewer.refresh(obj);
						}
						catch (ConnectionException e)
						{
							CoreUIUtils.showMessage(StringUtils.format(Messages.FileExplorerView_CouldNotCreateFolder,
									path));
						}
						catch (VirtualFileManagerException e)
						{
							CoreUIUtils.showMessage(StringUtils.format(Messages.FileExplorerView_CouldNotCreateFolder,
									path));
						}
					}
				}
				else
				{
					CoreUIUtils.showMessage(Messages.FileExplorerView_CannotCreateAFolderInThisTypeOfItem);
				}

			}
		};
		newFolderAction.setToolTipText(Messages.FileExplorerView_NewFolderTT);
		newFolderAction.setImageDescriptor(fNewFolderIconDescriptor);
	}

	/**
	 * 
	 */
	private void createDeleteAction()
	{
		deleteAction = new PushButtonAction(Messages.FileExplorerView_Delete)
		{
			public void run()
			{
				ISelection selection = viewer.getSelection();
				Object[] objs = ((IStructuredSelection) selection).toArray();

				if (!MessageDialog.openQuestion(viewer.getControl().getShell(),
						Messages.FileExplorerView_ConfirmDelete, StringUtils.format(
								Messages.FileExplorerView_AreYouSureYouWantToDeleteThis, objs.length)))
				{
					return;
				}

                Set<Object> refreshList = new HashSet<Object>();
                List<Object> deleteList = new ArrayList<Object>();
				for (Object object : objs)
				{
					if (object instanceof IVirtualFile)
					{
						IVirtualFile sfile = (IVirtualFile) object;
						TreeItem ti = findTreeItem(viewer.getTree().getItems(), sfile);
						if (ti != null && !refreshList.contains(ti.getParentItem().getData()))
						{
							refreshList.add(ti.getParentItem().getData());
						}
						deleteList.add(object);
					}
					else if (object instanceof IVirtualFileManager)
					{
						IVirtualFileManager manager = (IVirtualFileManager) object;
						VirtualFileManagerSyncPair[] pairs = SyncManager.getSyncPairs(manager);
						if (pairs.length > 0)
						{
							List<String> pairNames = new ArrayList<String>();
							for (VirtualFileManagerSyncPair object2 : pairs)
							{
								pairNames.add(object2.getNickName());
							}

							if (MessageDialog.openQuestion(viewer.getControl().getShell(),
									Messages.FileExplorerView_ConfirmDelete, StringUtils.format(
											Messages.FileExplorerView_AreYouSureYouWantToDeleteThisFileManager,
											new String[] { manager.getNickName(), StringUtils.join(", ", //$NON-NLS-1$
													(String[]) pairNames.toArray(new String[0])) })))
							{
								deleteList.add(object);
							}
						}
						else
						{
							deleteList.add(object);
						}
					}
				}

				if (deleteList.size() == 0)
				{
					return;
				}

				for (Object obj : deleteList)
				{
					if (obj instanceof IVirtualFile)
					{
						final IVirtualFile sfile = (IVirtualFile) obj;

						ProgressMonitorDialog pmd = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
						try
						{
							pmd.run(true, true, new IRunnableWithProgress()
							{
								public void run(final IProgressMonitor monitor) throws InvocationTargetException,
										InterruptedException
								{
									monitor
											.beginTask(Messages.FileExplorerView_DeletingFiles,
													IProgressMonitor.UNKNOWN);

									try
									{
										deleteVirtualFile(monitor, sfile);
									}
									catch (Exception e)
									{
										throw new InvocationTargetException(e);
									}

									monitor.done();
								}
							});
						}
						catch (InvocationTargetException e)
						{
							if (e.getCause() instanceof ConnectionException)
							{
								CoreUIUtils.fixConnection(sfile.getFileManager());
							}
							else
							{
								CoreUIUtils.showError(StringUtils.format(Messages.FileExplorerView_UnableToDelete,
										sfile.getAbsolutePath()), e, true);
							}
						}
						catch (InterruptedException e)
						{
							// canceled
						}
					}
					else if (obj instanceof IVirtualFileManager)
					{
						IVirtualFileManager f = (IVirtualFileManager) obj;
						f.getProtocolManager().removeFileManager(f);
					}
					else
					{
						MessageDialog.openError(viewer.getControl().getShell(), Messages.FileExplorerView_DeleteFailed,
								Messages.FileExplorerView_UnableToDeleteItemsOfThisType);
					}

				}

				for (Object element : refreshList)
				{
					refresh(element);
				}
			}
		};
		deleteAction.setToolTipText(Messages.FileExplorerView_DeleteTT);
		deleteAction.setImageDescriptor(fDeleteIcon);
	}

	/**
	 * 
	 */
	private void createRenameAction()
	{
		renameAction = new PushButtonAction(Messages.FileExplorerView_Rename)
		{
			public void run()
			{
				rename();
			}
		};

		renameAction.setToolTipText(Messages.FileExplorerView_RenameTT);
	}

	/**
	 * refresh
	 * 
	 * @deprecated (use refresh with passing in an object
	 */
	public void refresh()
	{
		refresh(null);
	}

	/**
	 * refresh
	 * 
	 * @param toRefresh
	 */
	public void refresh(Object toRefresh)
	{
		if (this.viewerIsAvailable())
		{
			if (toRefresh == null)
			{
				viewer.refresh();
			}
			else
			{
				viewer.refresh(toRefresh);
			}
		}
	}

	/**
	 * Determine if the viewer is available and usable
	 * 
	 * @return
	 */
	private boolean viewerIsAvailable()
	{
		return (viewer != null && viewer.getControl() != null && viewer.getControl().isDisposed() == false);
	}

	/**
	 * renameFile
	 * 
	 * @param item
	 */
	private void renameFile(final TreeItem item)
	{
		if (item.getData() instanceof IVirtualFile == false)
		{
			return;
		}

		if (Platform.OS_MACOSX.equals(Platform.getOS()))
		{
			InputDialog dialog = new InputDialog(getSite().getShell(), Messages.FileExplorerView_RenameTitle, Messages.FileExplorerView_NewName, item.getText(),
					null);
			int rc = dialog.open();
			if (rc == InputDialog.OK)
			{
				boolean renamed = renameItem(item, dialog.getValue());
				if (renamed)
				{
					item.setText(dialog.getValue());
				}
				else
				{
					CoreUIUtils.showError(StringUtils.format(Messages.FileExplorerView_UnableToRenameFile,
							((IVirtualFile) item.getData()).getName()), null);
				}
				refresh(item);
			}
		}
		else
		{
			final Tree tree = viewer.getTree();
			final TreeEditor editor = new TreeEditor(viewer.getTree());

			final Text text = new Text(tree, SWT.BORDER);
			text.setText(item.getText());
			text.selectAll();
			editor.horizontalAlignment = SWT.LEFT;
			editor.verticalAlignment = SWT.TOP;
			editor.grabHorizontal = true;

			editor.setEditor(text, item);

			Listener textListener = new Listener()
			{
				public void handleEvent(final Event e)
				{
					switch (e.detail)
					{
						case SWT.TRAVERSE_RETURN:
							boolean renamed = renameItem(item, text.getText());
							if (renamed)
							{
								item.setText(text.getText());
							}
							else
							{
								CoreUIUtils.showError(StringUtils.format(Messages.FileExplorerView_UnableToRenameFile,
										((IVirtualFile) item.getData()).getName()), null);
							}
							refresh(item);
							// FALL THROUGH
						case SWT.TRAVERSE_ESCAPE:
							text.dispose();
							e.doit = true;
							e.detail = SWT.TRAVERSE_NONE;
						default:
							break;
					}
				}
			};
			tree.addSelectionListener(new SelectionAdapter()
			{

				public void widgetSelected(SelectionEvent e)
				{
					tree.removeSelectionListener(this);
					text.dispose();
				}

			});
			FocusAdapter listener = new FocusAdapter()
			{
				public void focusLost(FocusEvent fe)
				{
					boolean renamed = renameItem(item, text.getText());
					if (renamed)
					{
						item.setText(text.getText());
					}
					else
					{
						CoreUIUtils.showError(StringUtils.format(Messages.FileExplorerView_UnableToRenameFile,
								((IVirtualFile) item.getData()).getName()), null);
					}
					text.dispose();
				}
			};

			text.addFocusListener(listener);
			text.addListener(SWT.Traverse, textListener);
			text.setFocus();
		}
	}

	/**
	 * openFileInEditor
	 * 
	 * @param file
	 */
	public void openFileInEditor(final IVirtualFile file)
	{
		IEditorDescriptor editorDesc = null;
		try
		{
			if (file.exists())
			{
				editorDesc = IDE.getEditorDescriptor(file.getName());
				CoreUIUtils.openFileInEditor(file, editorDesc);
			}
			else if (file.isLink())
			{
				CoreUIUtils.showError(
						Messages.FileExplorerView_LinkNotExistError, null,
						false);
			}
			else
			{
				CoreUIUtils.showError(Messages.FileExplorerView_FileNotExistError, null);
			}
		}
		catch (PartInitException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.FileExplorerView_ErrorInGetEditorDescriptor, e);
		}
		catch (ConnectionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @author Paul Colton
	 */
	protected class NewButtonAction extends Action
	{
		/**
		 * Indicates whether the launch history has changed and the sub menu needs to be recreated.
		 */
		protected boolean fRecreateMenu = false;

		/**
		 * NewButtonAction
		 * 
		 * @param text
		 */
		public NewButtonAction(String text)
		{
			super(text, Action.AS_DROP_DOWN_MENU);
			this.setMenuCreator(menuCreator);
		}

		private IMenuCreator menuCreator = new IMenuCreator()
		{

			private MenuManager dropDownMenuMgr;

			/**
			 * Creates the menu manager for the drop-down.
			 */
			private void createDropDownMenuMgr()
			{
				if (dropDownMenuMgr == null)
				{
					dropDownMenuMgr = new MenuManager();
					dropDownMenuMgr.add(new NewWizardMenu(getViewSite().getWorkbenchWindow()));
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Control)
			 */
			public Menu getMenu(Control parent)
			{
				createDropDownMenuMgr();
				return dropDownMenuMgr.createContextMenu(parent);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Menu)
			 */
			public Menu getMenu(Menu parent)
			{
				createDropDownMenuMgr();
				Menu menu = new Menu(parent);

				IContributionItem newItem = new ActionContributionItem(newFolderAction);
				newItem.fill(menu, 0);

				IContributionItem[] items = dropDownMenuMgr.getItems();

				for (int i = 0; i < items.length; i++)
				{
					IContributionItem item = items[i];
					newItem = item;
					if (item instanceof ActionContributionItem)
					{
						newItem = new ActionContributionItem(((ActionContributionItem) item).getAction());
					}
					newItem.fill(menu, -1);
				}
				return menu;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.action.IMenuCreator#dispose()
			 */
			public void dispose()
			{
				if (dropDownMenuMgr != null)
				{
					dropDownMenuMgr.dispose();
					dropDownMenuMgr = null;
				}
			}
		};
	}

	/**
	 * 
	 * 
	 */
	private void hookDoubleClickAction()
	{
		viewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				doubleClickAction.run();
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}

	/**
	 * A <code>NewWizardMenu</code> augments <code>BaseNewWizardMenu</code> with IDE-specific actions: New
	 * Project... (always shown) and New Example... (shown only if there are example wizards installed).
	 */
	public class NewWizardMenu extends BaseNewWizardMenu
	{
		private boolean enabled = true;

		/**
		 * Creates a new wizard shortcut menu for the IDE.
		 * 
		 * @param window
		 *            the window containing the menu
		 */
		public NewWizardMenu(IWorkbenchWindow window)
		{
			this(window, null);
		}

		/**
		 * Creates a new wizard shortcut menu for the IDE.
		 * 
		 * @param window
		 *            the window containing the menu
		 * @param id
		 *            the identifier for this contribution item
		 */
		public NewWizardMenu(IWorkbenchWindow window, String id)
		{
			super(window, id);
		}

		/**
		 * Create a new wizard shortcut menu.
		 * <p>
		 * If the menu will appear on a semi-permanent basis, for instance within a toolbar or menubar, the value passed
		 * for <code>register</code> should be true. If set, the menu will listen to perspective activation and update
		 * itself to suit. In this case clients are expected to call <code>deregister</code> when the menu is no
		 * longer needed. This will unhook any perspective listeners.
		 * </p>
		 * 
		 * @param innerMgr
		 *            the location for the shortcut menu contents
		 * @param window
		 *            the window containing the menu
		 * @param register
		 *            if <code>true</code> the menu listens to perspective changes in the window
		 */
		public NewWizardMenu(IMenuManager innerMgr, IWorkbenchWindow window, boolean register)
		{
			this(window, null);
			fillMenu(innerMgr); // NOPMD
			// Must be done after constructor to ensure field initialization.
		}

		/*
		 * (non-Javadoc) Fills the menu with New Wizards.
		 */
		private void fillMenu(IContributionManager innerMgr)
		{
			// Remove all.
			innerMgr.removeAll();

			IContributionItem[] items = getContributionItems();
			for (int i = 0; i < items.length; i++)
			{
				innerMgr.add(items[i]);
			}
		}

		/**
		 * Removes all listeners from the containing workbench window.
		 * <p>
		 * This method should only be called if the shortcut menu is created with <code>register = true</code>.
		 * </p>
		 * 
		 * @deprecated has no effect
		 */
		public void deregisterListeners()
		{
			// do nothing
		}

		/**
		 * @see org.eclipse.ui.actions.BaseNewWizardMenu#addItems(java.util.List)
		 */
		protected void addItems(List list)
		{
			// list.add(new ActionContributionItem(newProjectAction));
			list.add(new Separator());
			if (addShortcuts(list))
			{
				list.add(new Separator());
			}
			list.add(new ActionContributionItem(getShowDialogAction()));
		}

		/**
		 * @see org.eclipse.jface.action.IContributionItem#isEnabled()
		 */
		public boolean isEnabled()
		{
			return enabled;
		}

		/**
		 * Sets the enabled state of the receiver.
		 * 
		 * @param enabledValue
		 *            if <code>true</code> the menu is enabled; else it is disabled
		 */
		public void setEnabled(boolean enabledValue)
		{
			this.enabled = enabledValue;
		}

		/**
		 * @see org.eclipse.ui.actions.CompoundContributionItem#getContributionItems()
		 */
		protected IContributionItem[] getContributionItems()
		{
			if (isEnabled())
			{
				return super.getContributionItems();
			}
			return new IContributionItem[0];
		}
	}

	Object[] savedElements = null;

	private IActivityManager activityManager;
	private IActivityManagerListener activityManagerListener;
	private IIdentifierListener identifierListener;


	/**
	 * @see org.eclipse.ui.IViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
	 */
	public void init(IViewSite site, IMemento memento) throws PartInitException
	{
		super.init(site, memento);

		if (memento != null)
		{
			String state = memento.getString("tree"); //$NON-NLS-1$

			if (state != null && state.trim().length() > 0 && state.startsWith(STATE_START_TOKEN))
			{
				state = state.trim();

				try
				{
					state = state.substring(STATE_START_TOKEN.length());

					String[] parts = state.split("\\|"); //$NON-NLS-1$

					if (parts == null || parts.length == 0)
					{
						return;
					}

					// Load tree state
					int count = Integer.parseInt(parts[0]);

					if (count > 0)
					{
						List<LocalFile> elements = new ArrayList<LocalFile>();
						for (int i = 0; i < count; i++)
						{
							if (i + 1 < parts.length)
							{
								String src = parts[i + 1];
								elements.add(new LocalFile(null, new File(src)));
							}
						}

						savedElements = elements.toArray(new Object[0]);
					}

				}
				catch (Exception e)
				{
					IdeLog.logError(CoreUIPlugin.getDefault(), Messages.FileExplorerView_ErrorInitializingFileView, e);
				}
			}
		}
	}

	/**
	 * Select and reveal a virtual file manager
	 * 
	 * @param manager
	 */
	public void selectAndReveal(IVirtualFileManager manager)
	{
		if (manager != null && manager.getProtocolManager() != null)
		{
			ProtocolManager protocolManager = manager.getProtocolManager();
	        viewer.expandToLevel(protocolManager, 1);
            if (protocolManager.hasCustomContent())
            {
                Object[] content = protocolManager.getContent();
                for (Object element : content)
                {
                    if (element instanceof VirtualFileManagerGroup)
                    {
                        // expands the group as well
                        VirtualFileManagerGroup group = (VirtualFileManagerGroup) element;
                        if (group.contains(manager))
                        {
                            viewer.expandToLevel(group, 1);
                            break;
                        }
                    }
                }
            }
			viewer.expandToLevel(manager, 1);
			viewer.setSelection(new StructuredSelection(manager), true);
		}
	}

	/**
	 * Adds ability to select and reveal a remote file by expanding folders until the file is found
	 * 
	 * @param file
	 */
	public void selectAndReveal(final IVirtualFile file)
	{
		if (file instanceof LocalFile)
		{
			selectAndReveal(file);
		}
		else
		{
			final IVirtualFileManager vfm = file.getFileManager();
			if (vfm != null && vfm.getProtocolManager() != null && vfm.getBasePath() != null && file != null
					&& file.getAbsolutePath() != null)
			{
			    ProtocolManager protocolManager = vfm.getProtocolManager();
				viewer.expandToLevel(protocolManager, 1);
				if (protocolManager.hasCustomContent())
				{
				    Object[] content = protocolManager.getContent();
				    for (Object element : content)
				    {
				        if (element instanceof VirtualFileManagerGroup)
				        {
				            // expands the group as well
				            VirtualFileManagerGroup group = (VirtualFileManagerGroup) element;
				            if (group.contains(vfm))
				            {
				                viewer.expandToLevel(group, 1);
				                break;
				            }
				        }
				    }
				}
				viewer.expandToLevel(vfm, 1);
				final Path path = new Path(file.getAbsolutePath().replace(vfm.getBasePath(), "")); //$NON-NLS-1$
				final ISelection selection = new StructuredSelection(file);
				viewer.setSelection(new StructuredSelection(file), true);
				final ISelection current = viewer.getSelection();
				if (!selection.equals(viewer.getSelection()))
				{
					UIJob job = new UIJob("Refreshing File view") //$NON-NLS-1$
					{

						int totalTime;
						int segment = 0;
						String folderPath = vfm.getBasePath();

						public IStatus runInUIThread(IProgressMonitor monitor)
						{
							if (viewer != null && !viewer.getTree().isDisposed() && totalTime < MAX_EXPAND_TIME)
							{
								if (segment < path.segmentCount() - 1)
								{
									String folder = path.segment(segment);
									String newFolder = folderPath + "/" + folder; //$NON-NLS-1$
									IVirtualFile f = vfm.createVirtualDirectory(newFolder);
									Widget widget = viewer.testFindItem(f);
									if (widget != null)
									{
										viewer.expandToLevel(f, 1);
										if (widget instanceof TreeItem)
										{
											if (((TreeItem) widget).getItemCount() == 0)
											{
												return Status.CANCEL_STATUS;
											}
										}
										segment++;
										folderPath = newFolder;
									}
									totalTime += EXPAND_INTERVAL;
									schedule(EXPAND_INTERVAL);
								}
								else
								{
									if (current.equals(viewer.getSelection()))
									{
										viewer.setSelection(new StructuredSelection(file), true);
										if (file.isDirectory())
										{
											viewer.expandToLevel(file, 1);
										}
									}
									if (!selection.equals(viewer.getSelection()))
									{
										totalTime += EXPAND_INTERVAL;
										schedule(EXPAND_INTERVAL);
									}
								}
							}
							return Status.OK_STATUS;
						}
					};
					job.schedule(100);
				}
				else if (file.isDirectory())
				{
					viewer.expandToLevel(file, 1);
				}
			}
		}
	}

	/**
	 * Selects and reveals a file in the tree
	 * 
	 * @param file
	 */
	public void selectAndReveal(LocalFile file)
	{
		TreeItem[] items = viewer.getTree().getItems();
		File target = new File(file.getAbsolutePath());
		List<String> segments = new ArrayList<String>();
		segments.add(target.getAbsolutePath());
		File parent = target.getParentFile();
		while (parent != null)
		{
			segments.add(parent.getAbsolutePath());
			parent = parent.getParentFile();
		}
		TreeItem startItem = null;
		if (CoreUIUtils.runningOnWindows)
		{
			for (int i = 0; i < items.length; i++)
			{
				if (items[i].getData() instanceof LocalFile)
				{
					LocalFile lf = (LocalFile) items[i].getData();
					if (lf.getPath().equals(MY_COMPUTER_GUID))
					{
						viewer.expandToLevel(lf, 1);
						startItem = items[i];
						break;
					}
				}
			}
		}
		else
		{
			for (int i = 0; i < items.length; i++)
			{
				if (items[i].getData() instanceof LocalFile)
				{
					LocalFile lf = (LocalFile) items[i].getData();
					if (lf.getAbsolutePath().equals("/")) //$NON-NLS-1$
					{
						viewer.expandToLevel(lf, 1);
						startItem = items[i];
					}
				}
			}
		}
		if (startItem != null)
		{
			for (int s = segments.size() - 1; s >= 0; s--)
			{
				items = startItem.getItems();
				for (int t = 0; t < items.length; t++)
				{
					if (items[t].getData() instanceof LocalFile)
					{
						LocalFile currFile = (LocalFile) items[t].getData();
						if (currFile.getAbsolutePath().equals(segments.get(s)))
						{
							viewer.expandToLevel(currFile, 1);
							if (s == 0)
							{
								viewer.setSelection(new StructuredSelection(currFile), true);
							}
							startItem = items[t];
						}
					}
				}
			}
		}
	}

	/**
	 * @see org.eclipse.ui.IPersistable#saveState(org.eclipse.ui.IMemento)
	 */
	public void saveState(IMemento memento)
	{
		Object[] elements = viewer.getExpandedElements();

		String state = ""; //$NON-NLS-1$
		int count = 0;

		try
		{
			for (int i = 0; i < elements.length; i++)
			{
				// We only serialize open/close state of IVirtualFile objects
				// ATM
				if (elements[i] instanceof IVirtualFile)
				{
					String path = ((IVirtualFile) elements[i]).getPath();
					state += path + "|"; //$NON-NLS-1$
					count++;
				}
			}

			if (count > 0)
			{
				state = STATE_START_TOKEN + count + "|" + state; //$NON-NLS-1$
				memento.putString("tree", state); //$NON-NLS-1$
			}

		}
		catch (Exception e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.FileExplorerView_UnableToSaveFileViewState, e);
		}
	}

	/**
	 * getFirstSelectedItem
	 * 
	 * @return Object
	 */
	private Object getFirstSelectedItem()
	{
		ISelection selection = viewer.getSelection();
		return ((IStructuredSelection) selection).getFirstElement();
	}

	/**
	 * getFile
	 * 
	 * @param obj
	 * @return IVirtualFile
	 */
	private IVirtualFile getFile(Object obj)
	{
		IVirtualFile file = null;

		if (obj instanceof IVirtualFile)
		{
			file = (IVirtualFile) obj;
		}
		else if (obj instanceof IVirtualFileManager)
		{
			file = ((IVirtualFileManager) obj).getBaseFile();
		}
		return file;
	}

	/**
	 * @return Returns the viewer.
	 */
	public TreeViewer getViewer()
	{
		return viewer;
	}

	/**
	 * @param viewer
	 *            The viewer to set.
	 */
	public void setViewer(TreeViewer viewer)
	{
		this.viewer = viewer;
	}

	/**
	 * Find the action set for the given name
	 * 
	 * @param treeItems
	 * @param dataToFind
	 * @return Returns the matching action set or null;
	 */
	public TreeItem findTreeItem(TreeItem[] treeItems, Object dataToFind)
	{
		TreeItem result = null;

		for (int i = 0; i < treeItems.length; i++)
		{
			Object itemData = treeItems[i].getData();

			if (itemData == dataToFind)
			{
				result = treeItems[i];
			}
			else
			{
				result = findTreeItem(treeItems[i].getItems(), dataToFind);
			}

			if (result != null)
			{
				break;
			}
		}

		return result;
	}

	/**
	 * Confirms user wants to overwrite file(s)
	 * 
	 * @return boolean
	 */
	private boolean overwriteConfirmation(final String filename)
	{
		final boolean[] result = new boolean[] { false };
		if (!alwaysOverwrite)
		{
		    viewer.getControl().getDisplay().syncExec(new Runnable()
			{
				public void run()
				{
					MessageDialog dlg = new MessageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							Messages.FileReplaceDialog_ReplaceItem,
							null,
							MessageFormat.format(Messages.FileReplaceDialog_OverwriteExistingItem, filename),
							MessageDialog.QUESTION,
							new String[] { IDialogConstants.NO_LABEL, IDialogConstants.YES_LABEL, IDialogConstants.YES_TO_ALL_LABEL },
							0);
					switch (dlg.open()) {
					case 2:
						alwaysOverwrite = true;
					case 1:
						result[0] = true;
						break;
					}
				}
			});
		}
		else
        {
            result[0] = true;
        }
		return result[0];
	}

	private void rename() {
		ISelection selection = viewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();

		if (obj instanceof IVirtualFile == false)
		{
			CoreUIUtils.showMessage(Messages.FileExplorerView_CannotRenameThisItem);
			return;
		}
		TreeItem[] items = viewer.getTree().getSelection();
		if (items.length > 0)
		{
			renameFile(items[0]);
		}
		
		viewer.refresh(obj);
	}

	/**
	 * Renames the item
	 * 
	 * @param item
	 * @param text
	 * @return
	 */
	private boolean renameItem(TreeItem item, String text)
	{
		boolean renamed = true;

		try
		{
			IVirtualFile file = (IVirtualFile) item.getData();

			if (file == null)
			{
				renamed = false;
			}
			else
			{
				if (file.getName().equals(text) == false)
				{
					renamed = file.rename(text);
				}
			}
		}
		catch (ConnectionException ex)
		{
			CoreUIUtils.fixConnection(((IVirtualFile) item.getData()).getFileManager());
		}
		catch (VirtualFileManagerException ex)
		{
			CoreUIUtils.showError(StringUtils.format(Messages.FileExplorerView_UnableToRenameFile, ((IVirtualFile) item
					.getData()).getName()), ex);
		}

		return renamed;
	}
}
