/**
 * Copyright (c) 2005-2006 Aptana, Inc. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code, this entire header must remain intact.
 */
package org.eclipse.eclipsemonkey.views.scriptsView;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.eclipsemonkey.EclipseMonkeyPlugin;
import org.eclipse.eclipsemonkey.IScriptStoreListener;
import org.eclipse.eclipsemonkey.MenuRunMonkeyScript;
import org.eclipse.eclipsemonkey.RunMonkeyException;
import org.eclipse.eclipsemonkey.StoredScript;
import org.eclipse.eclipsemonkey.utils.UIUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.aptana.ide.core.ui.PreferenceUtils;

/**
 * ScriptsView
 * 
 * @author Paul Colton (Aptana, Inc.)
 */
public class ScriptsView extends ViewPart implements IScriptStoreListener
{

	/*
	 * Fields
	 */
	static final String INFO_MESSAGE = Messages.ScriptsView_INF_MSG_Aptana_scripts_view;

	static final String[] FILTER_EXTENSIONS = new String[] { "*.js", "*.*" }; //$NON-NLS-1$ //$NON-NLS-2$

	static final String[] FILTER_NAMES = new String[] { MessageFormat.format(Messages.ScriptsView_Javascript_files_0, new Object[] { FILTER_EXTENSIONS[0] }),
			MessageFormat.format(Messages.ScriptsView_All_files_0, new Object[] { FILTER_EXTENSIONS[1] } )};

	private static Image fProfileFileIcon = getImageDescriptor("icons/js_file.gif").createImage(); //$NON-NLS-1$

	private static ImageDescriptor fAddFileIconDescriptor = getImageDescriptor("icons/js_file_new.gif"); //$NON-NLS-1$

	private static ImageDescriptor fRefreshIconDescriptor = getImageDescriptor("icons/refresh.gif"); //$NON-NLS-1$
	
	private static ImageDescriptor fEditIconDescriptor = getImageDescriptor("icons/pencil.png"); //$NON-NLS-1$

	private static Image fErrIcon = getImageDescriptor("icons/error.gif").createImage(); //$NON-NLS-1$

	private static Image fProfileIcon = getImageDescriptor("icons/profile.gif").createImage(); //$NON-NLS-1$

	private static Image fProfileDynamicIcon = getImageDescriptor("icons/profile-dynamic.gif").createImage(); //$NON-NLS-1$

	private TreeViewer viewer;

	private StackLayout layout;

	private Composite viewParent;

	private Label infoLabel;

	private Font infoLabelFont;

	private static ImageRegistry imageRegistry = new ImageRegistry();

	private org.eclipse.jface.action.Action actionNewActionSet;

	private org.eclipse.jface.action.Action actionAdd;

	private org.eclipse.jface.action.Action actionEdit;

	private org.eclipse.jface.action.Action actionReload;

	private Action actionCollapseAll;

	private Action actionRefresh;

	private org.eclipse.jface.action.Action actionDelete;

	private org.eclipse.jface.action.Action actionDoubleClick;

	private org.eclipse.jface.action.Action actionAddCurrentFile;

	private org.eclipse.jface.action.Action actionExecute;

	private org.eclipse.jface.action.Action actionMakeExecutable;

	private ArrayList listeners = new ArrayList();

	private Pattern submenu_pattern = Pattern.compile("^(.+?)>(.*)$"); //$NON-NLS-1$

	private ScriptActionsManager _scriptActionsManager;

	/*
	 * Constructor.
	 */

	/**
	 * ScriptsView
	 */
	public ScriptsView()
	{
		_scriptActionsManager = ScriptActionsManager.getInstance();
	}

	/*
	 * Methods
	 */

	/**
	 * fireActionsViewEvent
	 * 
	 * @param e
	 */
	public void fireActionsViewEvent(ScriptActionsViewEvent e)
	{
		for (int i = 0; i < listeners.size(); i++)
		{
			IScriptActionsViewEventListener listener = (IScriptActionsViewEventListener) listeners.get(i);
			listener.onScriptActionsViewEvent(e);
		}
	}

	/**
	 * addActionsViewEventListener
	 * 
	 * @param l
	 */
	public void addScriptsViewEventListener(IScriptActionsViewEventListener l)
	{
		listeners.add(l);
	}

	/**
	 * removeActionsViewEventListener
	 * 
	 * @param l
	 */
	public void removeScriptsViewEventListener(IScriptActionsViewEventListener l)
	{
		listeners.remove(l);
	}

	/**
	 * The content provider class is responsible for providing objects to the view. It can wrap existing objects in
	 * adapters or simply return objects as-is. These objects may be sensitive to the current input of the view, or
	 * ignore it and always show the same content (like Task List, for example).
	 */
	class ScriptsViewContentProvider implements ITreeContentProvider
	{
		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer v, Object oldInput, Object newInput)
		{
		}

		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose()
		{
		}

		/**
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object parent)
		{

			updateActionSets();

			// Get actions and action sets
			IScriptAction[] actions = _scriptActionsManager.getAll();

			if (actions != null && actions.length > 0)
			{
				if (layout != null && infoLabel != null && viewParent != null && layout.topControl == infoLabel)
				{
					layout.topControl = viewer.getControl();
					viewParent.layout();
				}

				return actions;
			}

			if (layout != null && infoLabel != null && viewParent != null)
			{
				layout.topControl = infoLabel;
				viewParent.layout();
			}

			return new Object[0];
		}

		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object parentElement)
		{
			if (parentElement instanceof ScriptActionSet)
			{
				ScriptActionSet actionSet = (ScriptActionSet) parentElement;
				ScriptAction[] actions = actionSet.getScriptActions();

				return actions;
			}
			else
			{
				return new Object[0];
			}
		}

		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		public Object getParent(Object element)
		{
			if (element instanceof ScriptAction)
			{
				return ((ScriptAction) element).getParent();
			}
			else
			{
				// ActionSets have no parents
				return null;
			}
		}

		/**
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object element)
		{
			if (element instanceof ScriptActionSet)
			{
				ScriptActionSet actionSet = (ScriptActionSet) element;

				return actionSet.getScriptActions().length > 0;
			}
			else
			{
				return false;
			}
		}
	}

	/**
	 * ViewerSorterProvider
	 * 
	 * @author Ingo Muschenetz
	 */
	class ScriptsViewSorterProvider extends ViewerSorter
	{

		/**
		 * @see org.eclipse.jface.viewers.ViewerSorter#category(java.lang.Object)
		 */
		public int category(Object element)
		{
			if (element instanceof ScriptAction)
			{
				return 0;
			}
			else
			{
				return 1;
			}
		}

		/**
		 * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object,
		 *      java.lang.Object)
		 */
		public int compare(Viewer viewer, Object e1, Object e2)
		{
			int cat1 = category(e1);
			int cat2 = category(e2);

			if (cat1 == cat2)
			{
				IScriptAction action1 = (IScriptAction) e1;
				IScriptAction action2 = (IScriptAction) e2;

				return action1.getName().compareTo(action2.getName());
			}
			else
			{
				return cat1 - cat2;
			}
		}
	}

	/**
	 * ViewLabelProvider
	 * 
	 * @author Ingo Muschenetz
	 */
	class ScriptsViewLabelProvider extends LabelProvider
	{
		/**
		 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object element)
		{
			if (element instanceof IScriptAction)
			{
				IScriptAction profile = (IScriptAction) element;
				return profile.getName();
			}
			else
			{
				return null;
			}
		}

		// public Image getColumnImage(Object obj, int index)
		// {
		// return getImage(obj);
		// }

		/**
		 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
		 */
		public Image getImage(Object obj)
		{
			if (obj instanceof ScriptActionSet)
			{
				ScriptActionSet set = (ScriptActionSet) obj;

				if (set.isExecutable())
				{
					return fProfileDynamicIcon;
				}
				else
				{
					return fProfileIcon;
				}
			}
			else if (obj instanceof ScriptAction)
			{
				ScriptAction action = (ScriptAction) obj;
				IPath scriptPath = action.getStoredScript().metadata.getPath();
				String imagePath = action.getStoredScript().metadata.getImage();
				if (imagePath != null)
				{
					return getImageFromMetadata(scriptPath, imagePath);
				}
				else
				{
					return fProfileFileIcon;
				}
			}
			else
			{
				return null;
			}
		}
	}

	/**
	 * @param scriptPath
	 * @param imagePath
	 * @return
	 */
	private Image getImageFromMetadata(IPath scriptPath, String imagePath)
	{
		IPath newPath = scriptPath.removeLastSegments(1).append(imagePath);
		String absolutePath = newPath.toFile().getAbsolutePath();

		Image i = imageRegistry.get(absolutePath);
		if (i == null)
		{
			ImageDescriptor id = ImageDescriptor.createFromFile(null, absolutePath);
			if (id != null)
			{
				imageRegistry.put(absolutePath, id);
				i = imageRegistry.get(absolutePath);
			}
		}

		return i;
	}

	/**
	 * @see org.eclipse.eclipsemonkey.IScriptStoreListener#storeChanged()
	 */
	public void storeChanged()
	{

		Display display = viewer.getControl().getDisplay();
		if (!display.isDisposed())
		{
			display.asyncExec(new Runnable()
			{
				public void run()
				{
					if (viewer.getControl().isDisposed())
						return;
					viewer.refresh();
				}
			});
		}
	}

	private void updateActionSets()
	{
		ArrayList foundItems = new ArrayList();

		Map scriptStore = EclipseMonkeyPlugin.getDefault().getScriptStore();
		Object[] scripts = scriptStore.values().toArray();

		for (int i = 0; i < scripts.length; i++)
		{
			if (scripts[i] instanceof StoredScript)
			{
				StoredScript s = (StoredScript) scripts[i];

				if (s.metadata == null || s.metadata.getMenuName() == null)
					continue;

				String menuName = s.metadata.getMenuName().trim();

				foundItems.add(menuName);

				Matcher match = submenu_pattern.matcher(menuName);

				if (match.find())
				{
					String primary_key = match.group(1).trim();
					String secondary_key = match.group(2).trim();

					ScriptActionSet as = _scriptActionsManager.createScriptActionSet(primary_key);
					as.addScriptAction(secondary_key, s);
				}
				else
				{
					_scriptActionsManager.addScriptAction(menuName, s);
				}
			}
		}

		pruneUnusedActions(foundItems);
	}

	private void pruneUnusedActions(ArrayList foundItems)
	{

		ScriptAction[] actions = _scriptActionsManager.getScriptActions();
		ScriptActionSet[] sets = _scriptActionsManager.getScriptActionSets();

		for (int i = 0; i < actions.length; i++)
		{
			String name = actions[i].getStoredScript().metadata.getMenuName();
			if (foundItems.contains(name) == false)
				_scriptActionsManager.removeScriptAction(actions[i]);
		}

		for (int i = 0; i < sets.length; i++)
		{
			actions = sets[i].getScriptActions();
			for (int j = 0; j < actions.length; j++)
			{
				String name = actions[j].getStoredScript().metadata.getMenuName();
				if (foundItems.contains(name) == false)
					_scriptActionsManager.removeScriptActionSet(name);
			}
		}
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose()
	{
		if (infoLabelFont != null)
		{
			infoLabelFont.dispose();
		}

		if (fErrIcon != null)
		{
			fErrIcon.dispose();
		}

		if (imageRegistry != null)
		{
			imageRegistry.dispose();
		}

		EclipseMonkeyPlugin.getDefault().removeScriptStoreListener(this);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 * 
	 * @param parent
	 */
	public void createPartControl(Composite parent)
	{

		layout = new StackLayout();
		parent.setLayout(layout);

		viewer = new TreeViewer(new Tree(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL));
		viewer.setContentProvider(new ScriptsViewContentProvider());
		viewer.setLabelProvider(new ScriptsViewLabelProvider());
		viewer.setSorter(new ScriptsViewSorterProvider());
		viewer.setInput(getViewSite());
		viewer.expandAll();

		infoLabel = new Label(parent, SWT.CENTER);
		infoLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		infoLabelFont = new Font(parent.getDisplay(), Messages.ScriptsView_FNT_Aptana_scripts_view, 14, SWT.NONE);
		infoLabel.setFont(infoLabelFont);
		infoLabel.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		infoLabel.setText(INFO_MESSAGE);
		layout.topControl = infoLabel;
		layout.topControl = viewer.getControl();
		viewParent = parent;
		viewParent.layout();

		final DropTarget labeldt = new DropTarget(infoLabel, DND.DROP_MOVE);

		labeldt.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		labeldt.addDropListener(new DropTargetAdapter()
		{
			public void drop(DropTargetEvent event)
			{
				handleDrop(event);
			}
		});

		/*
		 * DragSource ds = new DragSource(viewer.getControl(), DND.DROP_COPY | DND.DROP_MOVE); ds.setTransfer(new
		 * Transfer[] { FileTransfer.getInstance() }); ds.addDragListener(new DragSourceAdapter() { public void
		 * dragSetData(DragSourceEvent event) { IStructuredSelection selection = (IStructuredSelection)
		 * viewer.getSelection(); if (FileTransfer.getInstance().isSupportedType(event.dataType)) { Object[] items =
		 * selection.toArray(); ArrayList data = new ArrayList(); for (int j = 0; j < items.length; j++) { Object item =
		 * items[j]; if (item instanceof ActionSet) { // no op } else if (item instanceof Action) { String path =
		 * ((Action)item).getFilePath(); data.add(path); } } if (data.size() > 0) { event.data = data.toArray(new
		 * String[0]); } } } });
		 */

		DropTarget dt = new DropTarget(viewer.getControl(), DND.DROP_MOVE);
		dt.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		dt.addDropListener(new DropTargetAdapter()
		{
			public void drop(DropTargetEvent event)
			{
				handleDrop(event);
			}
		});

		makeActions();
		hookKeyActions(viewer.getControl());
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		EclipseMonkeyPlugin.getDefault().addScriptStoreListener(this);
		
		PreferenceUtils.registerBackgroundColorPreference(viewer.getControl(), "com.aptana.ide.core.ui.background.color.scriptsView"); //$NON-NLS-1$
		PreferenceUtils.registerForegroundColorPreference(viewer.getControl(), "com.aptana.ide.core.ui.foreground.color.scriptsView"); //$NON-NLS-1$
	}

	/**
	 * handleDrop
	 * 
	 * @param event
	 */
	protected void handleDrop(DropTargetEvent event)
	{
		String[] files = (String[]) event.data;
		ArrayList paths = new ArrayList();

		for (int i = 0; i < files.length; i++)
		{
			paths.add(new Path(files[i]));
		}

		if (paths.size() > 0)
		{
			IPath[] ipaths = (IPath[]) paths.toArray(new IPath[0]);
			ScriptActionsViewEvent e = new ScriptActionsViewEvent(ScriptActionsViewEventTypes.DROP);

			e.setPaths(ipaths);

			Widget w = event.item;

			if (w != null)
			{
				TreeItem item = (TreeItem) w;
				Object element = item.getData();
				IScriptAction action = null;

				if (element instanceof ScriptActionSet)
				{
					action = (ScriptActionSet) element;
				}
				else if (element instanceof ScriptAction)
				{
					action = ((ScriptAction) element).getParent();
				}

				if (action != null)
				{
					e.setName(action.getName());
				}
			}

			fireActionsViewEvent(e);
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
				ISelection selection = viewer.getSelection();
				Object firstElement = ((IStructuredSelection) selection).getFirstElement();
				ScriptsView.this.fillContextMenu(manager, firstElement);
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
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
		// No toolbar items for the moment
		// manager.add(new Separator());
	}

	private void fillContextMenu(IMenuManager manager, Object element)
	{

		if (element instanceof ScriptActionSet)
		{
			ScriptActionSet set = (ScriptActionSet) element;

			if (set.isExecutable())
			{
				manager.add(actionExecute);
			}

			manager.add(actionMakeExecutable);
		}
		else
		{
			manager.add(actionExecute);
		}

		manager.add(new Separator());
		// manager.add(actionAddCurrentFile);

		if (element instanceof ScriptActionSet == false)
		{
			manager.add(actionEdit);
		}

		// manager.add(actionReload);
		// manager.add(actionDelete);

		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(actionEdit);
		manager.add(actionCollapseAll);
		manager.add(actionRefresh);
	}

	class PushButtonAction extends Action
	{
		/**
		 * PushButtonAction
		 * 
		 * @param text
		 */
		public PushButtonAction(String text)
		{
			super(text, Action.AS_PUSH_BUTTON);
		}
	}

	private void makeActions()
	{

		actionRefresh = new PushButtonAction(Messages.ScriptsView_LBL_Refresh)
		{
			public void run()
			{
				_scriptActionsManager.clearAll();
				viewer.refresh();
			}
		};
		actionRefresh.setToolTipText(Messages.ScriptsView_TTP_Refresh);
		actionRefresh.setImageDescriptor(fRefreshIconDescriptor);

		actionMakeExecutable = new org.eclipse.jface.action.Action()
		{
			public void run()
			{
				ISelection selection = viewer.getSelection();

				if (selection != null && selection instanceof IStructuredSelection)
				{
					IStructuredSelection structuredSelection = (IStructuredSelection) selection;
					Object element = structuredSelection.getFirstElement();
					if (element instanceof ScriptActionSet)
					{
						if (((ScriptActionSet) element).isExecutable())
						{
							((ScriptActionSet) element).setExecutable(false);
						}
						else
						{
							((ScriptActionSet) element).setExecutable(true);
						}

						viewer.refresh();
					}
				}

			}
		};
		actionMakeExecutable.setText(Messages.ScriptsView_LBL_Toggle_executable);
		actionMakeExecutable.setToolTipText(Messages.ScriptsView_TTP_Toggle_executable);

		actionExecute = new org.eclipse.jface.action.Action()
		{
			public void run()
			{
				ScriptActionsViewEvent e = new ScriptActionsViewEvent(ScriptActionsViewEventTypes.EXECUTE);
				ISelection selection = viewer.getSelection();

				if (selection != null && selection instanceof IStructuredSelection)
				{
					IStructuredSelection structuredSelection = (IStructuredSelection) selection;
					Object element = structuredSelection.getFirstElement();
					IScriptAction[] actions;

					if (element instanceof ScriptAction)
					{
						actions = new IScriptAction[] { (IScriptAction) element };
						executeScript((ScriptAction) element);
					}
					else if (element instanceof ScriptActionSet)
					{
						actions = ((ScriptActionSet) element).getScriptActions();
						for (int i = 0; i < actions.length; i++)
						{
							executeScript(actions[i]);
						}
					}
					else
					{
						actions = new IScriptAction[0];
					}

					e.setActions(actions);
				}

				fireActionsViewEvent(e);
			}
		};
		actionExecute.setText(Messages.ScriptsView_LBL_Execute);
		actionExecute.setToolTipText(Messages.ScriptsView_TTP_Execute);

		actionAddCurrentFile = new org.eclipse.jface.action.Action()
		{
			public void run()
			{
				ScriptActionsViewEvent e = new ScriptActionsViewEvent(ScriptActionsViewEventTypes.ADD_CURRENT_FILE);
				ISelection selection = viewer.getSelection();
				if (selection != null)
				{
					Object firstElement = ((IStructuredSelection) selection).getFirstElement();
					if (firstElement instanceof ScriptActionSet)
					{
						e.setName(((ScriptActionSet) firstElement).getName());
					}
				}

				fireActionsViewEvent(e);
			}
		};
		actionAddCurrentFile.setText(Messages.ScriptsView_LBL_Add_current_file);
		actionAddCurrentFile.setToolTipText(Messages.ScriptsView_TTP_Add_current_file);

		actionNewActionSet = new org.eclipse.jface.action.Action()
		{
			public void run()
			{
				InputDialog input = new InputDialog(getSite().getShell(), Messages.ScriptsView_TTL_New_action_set_name,
						Messages.ScriptsView_MSG_New_action_set_name, org.eclipse.eclipsemonkey.utils.StringUtils.EMPTY, null);

				if (input.open() == Window.OK && input.getValue().length() > 0)
				{
					ScriptActionsViewEvent e = new ScriptActionsViewEvent(ScriptActionsViewEventTypes.CREATE_ACTION_SET);
					e.setName(input.getValue());
					fireActionsViewEvent(e);
				}
			}
		};
		actionNewActionSet.setText(Messages.ScriptsView_LBL_New_script_set);
		actionNewActionSet.setToolTipText(Messages.ScriptsView_TTP_New_script_set);
		actionNewActionSet.setImageDescriptor(fAddFileIconDescriptor);

		actionEdit = new org.eclipse.jface.action.Action()
		{
			public void run()
			{
				ISelection selection = viewer.getSelection();

				if (selection != null)
				{
					Object firstElement = ((IStructuredSelection) selection).getFirstElement();
					if (firstElement instanceof ScriptAction)
					{
						ScriptAction a = (ScriptAction) firstElement;
						editAction(a);
					}
				}
			}
		};
		actionEdit.setText(Messages.ScriptsView_LBL_Edit_script);
		actionEdit.setToolTipText(Messages.ScriptsView_TTP_Edit_script);
		actionEdit.setImageDescriptor(fEditIconDescriptor);

		actionReload = new org.eclipse.jface.action.Action()
		{
			public void run()
			{
				ISelection selection = viewer.getSelection();

				if (selection != null)
				{
					Object firstElement = ((IStructuredSelection) selection).getFirstElement();
					if (firstElement instanceof ScriptAction)
					{
						ScriptAction a = (ScriptAction) firstElement;
						reloadAction(a);
					}
				}
			}
		};
		actionReload.setText(Messages.ScriptsView_LBL_Reload_script);
		actionReload.setToolTipText(Messages.ScriptsView_TTP_Reload_script);

		actionAdd = new org.eclipse.jface.action.Action()
		{
			public void run()
			{
				FileDialog fileDialog = new FileDialog(viewer.getControl().getShell(), SWT.MULTI);
				fileDialog.setFilterExtensions(FILTER_EXTENSIONS);
				fileDialog.setFilterNames(FILTER_NAMES);
				String text = fileDialog.open();
				if (text != null)
				{
					IPath basePath = new Path(fileDialog.getFilterPath());
					String[] fileNames = fileDialog.getFileNames();
					IPath[] paths = new IPath[fileNames.length];

					for (int i = 0; i < paths.length; i++)
					{
						paths[i] = basePath.append(fileNames[i]);
					}

					ScriptActionsViewEvent e = new ScriptActionsViewEvent(ScriptActionsViewEventTypes.ADD);
					e.setPaths(paths);
					fireActionsViewEvent(e);
				}
			}
		};

		actionAdd.setText(Messages.ScriptsView_LBL_Add_file);
		actionAdd.setToolTipText(Messages.ScriptsView_TTP_Add_file);
		actionAdd.setImageDescriptor(fAddFileIconDescriptor);

		actionDelete = new org.eclipse.jface.action.Action()
		{
			public void run()
			{
				removeFiles(viewer.getSelection());
			}
		};
		actionDelete.setText(Messages.ScriptsView_LBL_Remove_file);
		actionDelete.setToolTipText(Messages.ScriptsView_TTP_Remove_file);
		actionDelete.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
				ISharedImages.IMG_TOOL_DELETE));

		actionDoubleClick = new org.eclipse.jface.action.Action()
		{
			public void run()
			{
				ISelection selection = viewer.getSelection();
				Object firstElement = ((IStructuredSelection) selection).getFirstElement();

				if (firstElement instanceof ScriptActionSet)
				{
					toggleElementState(firstElement);
				}
				else if (firstElement instanceof IScriptAction)
				{
					ScriptActionsViewEvent e = new ScriptActionsViewEvent(ScriptActionsViewEventTypes.EXECUTE);
					IScriptAction[] actions = new IScriptAction[] { (IScriptAction) firstElement };

					e.setActions(actions);

					fireActionsViewEvent(e);

					executeScript((ScriptAction) firstElement);
				}
			}
		};

		this.actionCollapseAll = new Action(Messages.ScriptsView_LBL_Collapse_all)
		{

			public void run()
			{
				if (viewer != null)
				{
					viewer.collapseAll();
				}
			}

		};
		this.actionCollapseAll.setToolTipText(Messages.ScriptsView_TTP_Collapse_all);
		this.actionCollapseAll.setImageDescriptor(getImageDescriptor("icons/collapseall.gif")); //$NON-NLS-1$
	}

	/**
	 * executeScript
	 * 
	 * @param script
	 */
	private void executeScript(IScriptAction script)
	{
		MenuRunMonkeyScript run = new MenuRunMonkeyScript(script.getStoredScript().scriptPath);
		try
		{
			run.run("main", new Object[] {}); //$NON-NLS-1$
		}
		catch (RunMonkeyException e1)
		{
			e1.printStackTrace();
		}
	}

	/**
	 * toggleElementState
	 * 
	 * @param element
	 */
	private void toggleElementState(Object element)
	{
		boolean state = viewer.getExpandedState(element);

		if (state)
		{
			viewer.setExpandedState(element, false);
		}
		else
		{
			viewer.setExpandedState(element, true);
		}
	}

	/**
	 * Removes one or more files
	 * 
	 * @param selection
	 *            The currently selected files
	 */
	private void removeFiles(ISelection selection)
	{
		if (!(selection instanceof StructuredSelection))
		{
			return;
		}

		Object o = ((StructuredSelection) selection).getFirstElement();

		if (o == null)
		{
			return;
		}

		if (o instanceof ScriptActionSet)
		{
			ScriptActionsViewEvent e = new ScriptActionsViewEvent(ScriptActionsViewEventTypes.DELETE_ACTION_SET);
			e.setName(((ScriptActionSet) o).getName());
			fireActionsViewEvent(e);
		}
		else
		{

			ArrayList actionsList = new ArrayList();

			for (Iterator iter = ((StructuredSelection) selection).iterator(); iter.hasNext();)
			{
				actionsList.add(iter.next());
			}

			ScriptAction[] actions = (ScriptAction[]) actionsList.toArray(new ScriptAction[0]);

			if (actions.length > 0)
			{
				ScriptActionsViewEvent e = new ScriptActionsViewEvent(ScriptActionsViewEventTypes.DELETE);
				e.setActions(actions);
				fireActionsViewEvent(e);
			}
		}
	}

	// private void showMessage(String message) {
	// MessageDialog.openInformation(
	// viewer.getControl().getShell(),
	// "File Explorer",
	// message);
	// }

	/**
	 * hookDoubleClickAction
	 */
	private void hookDoubleClickAction()
	{
		viewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				actionDoubleClick.run();
			}
		});
	}

	/**
	 * hookKeyActions
	 * 
	 * @param control
	 */
	private void hookKeyActions(Control control)
	{
		control.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e)
			{
				if (e.character == SWT.DEL)
				{
					removeFiles(viewer.getSelection());
				}
			}

			public void keyReleased(KeyEvent e)
			{
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
	 * Retrieves the image descriptor associated with resource from the image descriptor registry. If the image
	 * descriptor cannot be retrieved, attempt to find and load the image descriptor at the location specified in
	 * resource.
	 * 
	 * @param imageFilePath
	 *            the image descriptor to retrieve
	 * @return The image descriptor assocated with resource or the default "missing" image descriptor if one could not
	 *         be found
	 */
	private static ImageDescriptor getImageDescriptor(String imageFilePath)
	{

		ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin("org.eclipse.eclipsemonkey.ui", //$NON-NLS-1$
				imageFilePath);

		if (imageDescriptor == null)
		{
			imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
		}

		return imageDescriptor;
	}

	/**
	 * expandAll
	 */
	public void expandAll()
	{
		this.viewer.expandAll();
	}

	/**
	 * expandActionSet
	 * 
	 * @param setName
	 */
	public void expandScriptActionSet(String setName)
	{
		TreeItem[] treeItems = viewer.getTree().getItems();

		for (int i = 0; i < treeItems.length; i++)
		{
			Object o = treeItems[i].getData();

			if (o instanceof ScriptActionSet)
			{
				ScriptActionSet p = (ScriptActionSet) o;
				String name = p.getName();
				if (name.equals(setName))
				{
					viewer.setExpandedState(o, true);
					viewer.getTree().showItem(treeItems[i]);
					return;
				}
			}
		}

	}

	/**
	 * selectAndReveal
	 * 
	 * @param actionPath
	 */
	public void selectAndReveal(String actionPath)
	{
		IScriptAction a = findAction(actionPath);

		if (a != null && a instanceof ScriptAction)
		{
			selectAndReveal((ScriptAction) a);
		}
	}

	/**
	 * selectAndReveal
	 * 
	 * @param action
	 */
	public void selectAndReveal(ScriptAction action)
	{
		TreeItem[] treeItems = viewer.getTree().getItems();

		forcePopulateTree();

		for (int i = 0; i < treeItems.length; i++)
		{
			Object o = treeItems[i].getData();

			if (o instanceof ScriptActionSet)
			{
				TreeItem[] children = treeItems[i].getItems();

				for (int j = 0; j < children.length; j++)
				{
					IScriptAction a = (IScriptAction) children[j].getData();

					if (a == action)
					{
						viewer.getTree().showItem(children[j]);
						viewer.getTree().setSelection(new TreeItem[] { children[j] });
						return;
					}

				}
			}
			else if (o instanceof ScriptAction)
			{
				ScriptAction a = (ScriptAction) o;
				if (a == action)
				{
					viewer.getTree().showItem(treeItems[i]);
					viewer.getTree().setSelection(new TreeItem[] { treeItems[i] });
					return;
				}
			}
		}
	}

	/**
	 * forcePopulateTree
	 */
	public void forcePopulateTree()
	{
		viewer.getTree().setVisible(false);
		Object[] expandedElement = viewer.getExpandedElements();
		viewer.expandAll();
		viewer.setExpandedElements(expandedElement);
		viewer.getTree().setVisible(true);
	}

	/**
	 * Find the action set for the given name
	 * 
	 * @param name
	 *            The action set name to find
	 * @return Returns the matching action set or null;
	 */
	public ScriptActionSet findActionSet(String name)
	{
		ScriptActionSet result = null;
		TreeItem[] treeItems = viewer.getTree().getItems();

		for (int i = 0; i < treeItems.length; i++)
		{
			Object itemData = treeItems[i].getData();

			if (itemData instanceof ScriptActionSet)
			{
				ScriptActionSet actionSet = (ScriptActionSet) itemData;

				if (actionSet.getName().equals(name))
				{
					result = actionSet;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * findAction
	 * 
	 * @param path
	 * @return IAction
	 */
	public ScriptAction findAction(String path)
	{
		ScriptAction result = null;

		if (path != null && path.length() > 0 && path.charAt(0) == '/')
		{
			int slashIndex = path.indexOf('/', 1);

			if (slashIndex != -1)
			{
				String actionSetName = path.substring(1, slashIndex);
				String actionName = path.substring(slashIndex + 1);

				ScriptActionSet actionSet = findActionSet(actionSetName);

				if (actionSet != null)
				{
					ScriptAction[] actions = actionSet.getScriptActions();

					for (int i = 0; i < actions.length; i++)
					{
						ScriptAction action = actions[i];

						if (action.getName().equals(actionName))
						{
							result = action;
							break;
						}
					}
				}
			}
		}

		return result;
	}

	/**
	 * fireAction
	 * 
	 * @param actionName
	 */
	public void fireAction(String actionName)
	{
		IScriptAction action = findAction(actionName);

		if (action != null)
		{
			ScriptActionsViewEvent actionEvent = new ScriptActionsViewEvent(ScriptActionsViewEventTypes.EXECUTE);
			IScriptAction[] actions = new IScriptAction[] { action };
			actionEvent.setActions(actions);
			fireActionsViewEvent(actionEvent);
		}
	}

	/**
	 * refresh
	 */
	public void refresh()
	{
		this.viewer.refresh();
	}

	/**
	 * editAction
	 * 
	 * @param path
	 */
	public void editAction(String path)
	{
		editAction(findAction(path));
	}

	/**
	 * editAction
	 * 
	 * @param a
	 */
	private void editAction(final IScriptAction a)
	{
		if (a == null)
		{
			return;
		}

		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorDescriptor editorDesc = null;

		File f = a.getStoredScript().metadata.getPath().toFile();

		try
		{
			editorDesc = IDE.getEditorDescriptor(f.getName());

			if (editorDesc.isOpenExternal() == true)
			{
				editorDesc = IDE.getEditorDescriptor("foo.txt"); //$NON-NLS-1$
			}

		}
		catch (PartInitException e)
		{
			System.err.println("Error opening file in editor: " + e); //$NON-NLS-1$
		}

		try
		{
			// IEditorPart editorPart =
			IDE.openEditor(page, UIUtils.createJavaFileEditorInput(f), editorDesc.getId());

			// if (editorPart instanceof IUnifiedEditor)
			// {
			// IUnifiedEditor editor = (IUnifiedEditor) editorPart;
			//
			// editor.addSaveListener(new ISaveEvent()
			// {
			// public void onSave(IEditorPart part)
			// {
			// reloadAction(a);
			// }
			// });
			// }
		}
		catch (PartInitException e)
		{
			System.err.println("Error opening editor: " + e); //$NON-NLS-1$
		}
	}

	/**
	 * reloadAction
	 * 
	 * @param a
	 */
	private void reloadAction(final IScriptAction a)
	{
		ScriptActionsViewEvent e = new ScriptActionsViewEvent(ScriptActionsViewEventTypes.RELOAD);
		e.setActions(new IScriptAction[] { a });
		fireActionsViewEvent(e);
	}
}
