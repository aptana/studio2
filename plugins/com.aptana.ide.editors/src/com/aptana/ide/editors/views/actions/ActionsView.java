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
package com.aptana.ide.editors.views.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
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
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.CoreUIPlugin;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.core.ui.editors.ISaveEvent;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.editors.unified.UnifiedInformationControl;
import com.aptana.ide.editors.unified.utils.HTMLTextPresenter;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view shows data obtained from the model. The
 * sample creates a dummy model on the fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be presented in the view. Each view can present the
 * same model objects using different labels and icons, if needed. Alternatively, a single label provider can be shared
 * between views in order to ensure that objects of the same type are presented in the same way everywhere.
 * <p>
 */

public class ActionsView extends ViewPart
{
	/*
	 * Fields
	 */
	// static final String INFO_MESSAGE = "\nDrop JavaScript action files here.";
	static final String INFO_MESSAGE = Messages.ActionsView_ActionsView;

	static final String[] FILTER_EXTENSIONS = new String[] { "*.js", "*.*" }; //$NON-NLS-1$ //$NON-NLS-2$

	static final String[] FILTER_NAMES = new String[] { StringUtils.format(Messages.ActionsView_JavaScriptFiles, FILTER_EXTENSIONS[0]),
			StringUtils.format(Messages.ActionsView_AllFiles, FILTER_EXTENSIONS[1])};

	private static ImageDescriptor fAddFileIconDescriptor = getImageDescriptor("icons/js_file_new.gif"); //$NON-NLS-1$

	private static Image fErrIcon = getImageDescriptor("icons/error.gif").createImage(); //$NON-NLS-1$

	private static Image fProfileIcon = getImageDescriptor("icons/profile.gif").createImage(); //$NON-NLS-1$
	private static Image fProfileDynamicIcon = getImageDescriptor("icons/profile-dynamic.gif").createImage(); //$NON-NLS-1$

	private static Image fProfileFileIcon = getImageDescriptor("icons/js_file.gif").createImage(); //$NON-NLS-1$

	private TreeViewer viewer;
	private StackLayout layout;
	private Composite viewParent;
	private Label infoLabel;
	private Font infoLabelFont;

	private org.eclipse.jface.action.Action actionNewActionSet;
	private org.eclipse.jface.action.Action actionAdd;
	private org.eclipse.jface.action.Action actionEdit;
	private org.eclipse.jface.action.Action actionReload;
	private org.eclipse.jface.action.Action actionDelete;
	private org.eclipse.jface.action.Action actionDoubleClick;
	private org.eclipse.jface.action.Action actionAddCurrentFile;
	private org.eclipse.jface.action.Action actionExecute;
	private org.eclipse.jface.action.Action actionMakeExecutable;

	private ArrayList listeners = new ArrayList();

	/**
	 * actionsManager
	 */
	public ActionsManager actionsManager;

	/*
	 * Methods
	 */

	/**
	 * fireActionsViewEvent
	 * 
	 * @param e
	 */
	public void fireActionsViewEvent(ActionsViewEvent e)
	{
		for (int i = 0; i < listeners.size(); i++)
		{
			IActionsViewEventListener listener = (IActionsViewEventListener) listeners.get(i);
			listener.onActionsViewEvent(e);
		}
	}

	/**
	 * addActionsViewEventListener
	 * 
	 * @param l
	 */
	public void addActionsViewEventListener(IActionsViewEventListener l)
	{
		listeners.add(l);
	}

	/**
	 * removeActionsViewEventListener
	 * 
	 * @param l
	 */
	public void removeActionsViewEventListener(IActionsViewEventListener l)
	{
		listeners.remove(l);
	}

	/**
	 * The content provider class is responsible for providing objects to the view. It can wrap existing objects in
	 * adapters or simply return objects as-is. These objects may be sensitive to the current input of the view, or
	 * ignore it and always show the same content (like Task List, for example).
	 */
	class ViewContentProvider implements ITreeContentProvider
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
			// Get actions and action sets
			IAction[] actions = actionsManager.getAll();

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
			if (parentElement instanceof ActionSet)
			{
				ActionSet actionSet = (ActionSet) parentElement;
				Action[] actions = actionSet.getActions();

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
			if (element instanceof Action)
			{
				return ((Action) element).getParent();
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
			if (element instanceof ActionSet)
			{
				ActionSet actionSet = (ActionSet) element;
				
				return actionSet.getActions().length > 0;
			}
			else
			{
				return false;
			}
		}
	}

	/**
	 * ViewerSorterProvider
	 * @author Ingo Muschenetz
	 *
	 */
	class ViewerSorterProvider extends ViewerSorter
	{

		/**
		 * @see org.eclipse.jface.viewers.ViewerSorter#category(java.lang.Object)
		 */
		public int category(Object element)
		{
			if (element instanceof Action)
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
				IAction action1 = (IAction) e1;
				IAction action2 = (IAction) e2;

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
	 * @author Ingo Muschenetz
	 *
	 */
	class ViewLabelProvider extends LabelProvider
	{
		/**
		 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
		public String getText(Object element)
		{
			if (element instanceof IAction)
			{
				IAction profile = (IAction) element;
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
			if (obj instanceof ActionSet)
			{
				ActionSet set = (ActionSet) obj;

				if (set.isExecutable())
				{
					return fProfileDynamicIcon;
				}
				else
				{
					return fProfileIcon;
				}
			}
			else if (obj instanceof Action)
			{
				return fProfileFileIcon;
			}
			else
			{
				return null;
			}
		}
	}

	/**
	 * The constructor.
	 */
	public ActionsView()
	{
		actionsManager = UnifiedEditorsPlugin.getDefault().getActionsManager();
		actionsManager.addActionsChangeListener(new IActionChangeListener()
		{
			public void onActionChanged(IAction a)
			{
				if (viewer.getControl().isDisposed() == false)
				{
					Object[] expanded = viewer.getExpandedElements();
					viewer.refresh();
					viewer.setExpandedElements(expanded);
				}
			}
		});
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
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 * 
	 * @param parent
	 */
	public void createPartControl(Composite parent)
	{
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, "com.aptana.ide.js.ui.ProfilesView"); //$NON-NLS-1$

		layout = new StackLayout();
		parent.setLayout(layout);

		viewer = createTreeViewer(parent);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new ViewerSorterProvider());
		viewer.setInput(getViewSite());
		viewer.expandAll();

		infoLabel = new Label(parent, SWT.CENTER);
		infoLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		infoLabelFont = new Font(parent.getDisplay(), Messages.ActionsView_Arial, 14, SWT.NONE);
		infoLabel.setFont(infoLabelFont);
		infoLabel.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		infoLabel.setText(INFO_MESSAGE);

		layout.topControl = infoLabel;
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

		viewer.refresh();
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
			ActionsViewEvent e = new ActionsViewEvent(ActionsViewEventTypes.DROP);
			
			e.setPaths(ipaths);

			Widget w = event.item;

			if (w != null)
			{
				TreeItem item = (TreeItem) w;
				Object element = item.getData();
				IAction action = null;

				if (element instanceof ActionSet)
				{
					action = (ActionSet) element;
				}
				else if (element instanceof Action)
				{
					action = ((Action) element).getParent();
				}

				if (action != null)
				{
					e.setName(action.getName());
				}
			}

			fireActionsViewEvent(e);
		}
	}

	/**
	 * createTreeViewer
	 * 
	 * @param parent
	 * @return TreeViewer
	 */
	protected TreeViewer createTreeViewer(Composite parent)
	{
		final TreeViewer treeViewer = new TreeViewer(new Tree(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL));

		// Implement a "fake" tooltip
		final Listener labelListener = new Listener()
		{
			public void handleEvent(Event event)
			{
				StyledText label = (StyledText) event.widget;
				Shell shell = (Shell) label.getData("_SHELL"); // label.getShell(); //$NON-NLS-1$

				switch (event.type)
				{
					// case SWT.MouseDown:
					case SWT.MouseDoubleClick:
						Event e = new Event();
						e.item = (TreeItem) label.getData("_TREEITEM"); //$NON-NLS-1$
						// Assuming table is single select, set the selection as if
						// the mouse down event went through to the table
						treeViewer.getTree().setSelection(new TreeItem[] { (TreeItem) e.item });
						actionDoubleClick.run();
						// treeViewer.getTree().notifyListeners(SWT.Selection, e);
						shell.dispose();
					// fallthrough

					case SWT.MouseExit:
						shell.dispose();
						break;
						
					default:
						break;
				}
			}
		};

		final Shell shell = getSite().getShell();

		Listener tableListener = new Listener()
		{
			UnifiedInformationControl info = null;

			public void handleEvent(Event event)
			{
				switch (event.type)
				{
					case SWT.Dispose:
					case SWT.KeyDown:
					case SWT.MouseMove:
					{
						if (info == null || info.getShell() == null)
						{
							break;
						}
						info.getShell().dispose();
						info = null;
						break;
					}
					case SWT.MouseHover:
					{
						TreeItem item = treeViewer.getTree().getItem(new Point(event.x, event.y));
						if (item != null)
						{
							if (info != null && info.getShell() != null && !info.getShell().isDisposed())
							{
								info.getShell().dispose();
							}

							info = new UnifiedInformationControl(shell, SWT.NONE, new HTMLTextPresenter(false));

							info.getStyledTextWidget().setData("_TREEITEM", item); //$NON-NLS-1$
							info.getStyledTextWidget().setData("_SHELL", info.getShell()); //$NON-NLS-1$
							info.getStyledTextWidget().addListener(SWT.MouseExit, labelListener);
							// info.getStyledTextWidget().addListener(SWT.MouseDown, labelListener);
							info.getStyledTextWidget().addListener(SWT.MouseDoubleClick, labelListener);

							Object data = item.getData();
							String txt = null;

							if (data instanceof IAction)
							{
								IAction action = (IAction) data;

								txt = action.getToolTipText();
							}
							else if (data instanceof IPath)
							{
								IPath path = (IPath) data;

								txt = path.toOSString();
							}

							if (txt != null)
							{
								if (txt.indexOf("<") != -1) //$NON-NLS-1$
								{
									txt = txt.replaceAll("<", "&lt;"); //$NON-NLS-1$ //$NON-NLS-2$
								}

								info.setSizeConstraints(300, 500);
								info.setInformation(txt);

								StyledText styledText = info.getStyledTextWidget();
								GC gc = new GC(styledText);
								int width = gc.getFontMetrics().getAverageCharWidth();

								width = ((txt.length() + 2) * width);

								Rectangle rect = item.getBounds(0);
								Point pt = treeViewer.getTree().toDisplay(20 + rect.x, rect.y);

								info.setSize(width, 0);
								info.setLocation(pt);
								info.setVisible(true);
							}
						}
					}
					
					default:
						break;					
				}
			}
		};

		treeViewer.getTree().addListener(SWT.Dispose, tableListener);
		treeViewer.getTree().addListener(SWT.KeyDown, tableListener);
		treeViewer.getTree().addListener(SWT.MouseMove, tableListener);
		treeViewer.getTree().addListener(SWT.MouseHover, tableListener);

		return treeViewer;
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
				ActionsView.this.fillContextMenu(manager, firstElement);
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
		manager.add(new Separator());
		// manager.add(actionAdd);
		// manager.add(actionNewActionSet);
		manager.add(actionEdit);
		manager.add(actionReload);
		// manager.add(actionDelete);
	}

	private void fillContextMenu(IMenuManager manager, Object element)
	{
		// if(element instanceof Profile)
		// {
		// Profile p = ((Profile) element);
		//			
		//
		// if(p.isDynamic() == false)
		// {
		// manager.add(actionDelete);
		// }
		// else
		// {
		// manager.add(actionMakeStatic);
		// }
		// }
		// else if(element instanceof ProfilePath)
		// {
		// ProfilePath path = ((ProfilePath) element);
		//			
		// if(path.getParent().isDynamic() == false)
		//			
		// manager.add(actionMoveUp);
		// manager.add(actionMoveDown);
		// }

		if (element instanceof ActionSet)
		{
			ActionSet set = (ActionSet) element;

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
		manager.add(actionEdit);
		manager.add(actionReload);
		// manager.add(actionDelete);

		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		// manager.add(actionNewActionSet);
		// manager.add(actionDelete);
	}

	private void makeActions()
	{

		actionMakeExecutable = new org.eclipse.jface.action.Action()
		{
			public void run()
			{
				ISelection selection = viewer.getSelection();

				if (selection != null && selection instanceof IStructuredSelection)
				{
					IStructuredSelection structuredSelection = (IStructuredSelection) selection;
					Object element = structuredSelection.getFirstElement();
					if (element instanceof ActionSet)
					{
						if (((ActionSet) element).isExecutable())
						{
							((ActionSet) element).setExecutable(false);
						}
						else
						{
							((ActionSet) element).setExecutable(true);
						}

						viewer.refresh();
					}
				}

			}
		};
		actionMakeExecutable.setText(Messages.ActionsView_ToggleExecutable);
		actionMakeExecutable.setToolTipText(Messages.ActionsView_ToggleToolTip);

		actionExecute = new org.eclipse.jface.action.Action()
		{
			public void run()
			{
				ActionsViewEvent e = new ActionsViewEvent(ActionsViewEventTypes.EXECUTE);
				ISelection selection = viewer.getSelection();

				if (selection != null && selection instanceof IStructuredSelection)
				{
					IStructuredSelection structuredSelection = (IStructuredSelection) selection;
					Object element = structuredSelection.getFirstElement();
					IAction[] actions;

					if (element instanceof IAction)
					{
						actions = new IAction[] { (IAction) element };
					}
					else
					{
						actions = new IAction[0];
					}

					e.setActions(actions);
				}

				fireActionsViewEvent(e);
			}
		};
		actionExecute.setText(Messages.ActionsView_Execute);
		actionExecute.setToolTipText(Messages.ActionsView_ExecuteToolTip);

		actionAddCurrentFile = new org.eclipse.jface.action.Action()
		{
			public void run()
			{
				ActionsViewEvent e = new ActionsViewEvent(ActionsViewEventTypes.ADD_CURRENT_FILE);
				ISelection selection = viewer.getSelection();
				if (selection != null)
				{
					Object firstElement = ((IStructuredSelection) selection).getFirstElement();
					if (firstElement instanceof ActionSet)
					{
						e.setName(((ActionSet) firstElement).getName());
					}
				}

				fireActionsViewEvent(e);
			}
		};
		actionAddCurrentFile.setText(Messages.ActionsView_AddCurrentFile);
		actionAddCurrentFile.setToolTipText(Messages.ActionsView_AddCurrentFileToolTip);

		actionNewActionSet = new org.eclipse.jface.action.Action()
		{
			public void run()
			{
				InputDialog input = new InputDialog(getSite().getShell(), Messages.ActionsView_NewActionSetName,
						Messages.ActionsView_PleaseEnterActionSetName, StringUtils.EMPTY, null);

				if (input.open() == Window.OK && input.getValue().length() > 0)
				{
					ActionsViewEvent e = new ActionsViewEvent(ActionsViewEventTypes.CREATE_ACTION_SET);
					e.setName(input.getValue());
					fireActionsViewEvent(e);
				}
			}
		};
		actionNewActionSet.setText(Messages.ActionsView_NewActionSet);
		actionNewActionSet.setToolTipText(Messages.ActionsView_NewActionSet);
		actionNewActionSet.setImageDescriptor(fAddFileIconDescriptor);

		actionEdit = new org.eclipse.jface.action.Action()
		{
			public void run()
			{
				ISelection selection = viewer.getSelection();

				if (selection != null)
				{
					Object firstElement = ((IStructuredSelection) selection).getFirstElement();
					if (firstElement instanceof Action)
					{
						Action a = (Action) firstElement;
						editAction(a);
					}
				}
			}
		};
		actionEdit.setText(Messages.ActionsView_EditAction);
		actionEdit.setToolTipText(Messages.ActionsView_EditAction);

		actionReload = new org.eclipse.jface.action.Action()
		{
			public void run()
			{
				ISelection selection = viewer.getSelection();

				if (selection != null)
				{
					Object firstElement = ((IStructuredSelection) selection).getFirstElement();
					if (firstElement instanceof Action)
					{
						Action a = (Action) firstElement;
						reloadAction(a);
					}
				}
			}
		};
		actionReload.setText(Messages.ActionsView_ReloadAction);
		actionReload.setToolTipText(Messages.ActionsView_ReloadAction);

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

					ActionsViewEvent e = new ActionsViewEvent(ActionsViewEventTypes.ADD);
					e.setPaths(paths);
					fireActionsViewEvent(e);
				}
			}
		};

		actionAdd.setText(Messages.ActionsView_AddFile);
		actionAdd.setToolTipText(Messages.ActionsView_AddFile);
		actionAdd.setImageDescriptor(fAddFileIconDescriptor);

		actionDelete = new org.eclipse.jface.action.Action()
		{
			public void run()
			{
				removeFiles(viewer.getSelection());
			}
		};
		actionDelete.setText(Messages.ActionsView_RemoveFile);
		actionDelete.setToolTipText(Messages.ActionsView_RemoveFile);
		actionDelete.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
				ISharedImages.IMG_TOOL_DELETE));

		actionDoubleClick = new org.eclipse.jface.action.Action()
		{
			public void run()
			{
				ISelection selection = viewer.getSelection();
				Object firstElement = ((IStructuredSelection) selection).getFirstElement();

				if (firstElement instanceof ActionSet)
				{
					toggleElementState(firstElement);
				}
				else if (firstElement instanceof IAction)
				{
					ActionsViewEvent e = new ActionsViewEvent(ActionsViewEventTypes.EXECUTE);
					IAction[] actions = new IAction[] { (IAction) firstElement };

					e.setActions(actions);

					fireActionsViewEvent(e);
				}
			}
		};
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

		if (o instanceof ActionSet)
		{
			ActionsViewEvent e = new ActionsViewEvent(ActionsViewEventTypes.DELETE_ACTION_SET);
			e.setName(((ActionSet) o).getName());
			fireActionsViewEvent(e);
		}
		else
		{

			ArrayList actionsList = new ArrayList();

			for (Iterator iter = ((StructuredSelection) selection).iterator(); iter.hasNext();)
			{
				actionsList.add(iter.next());
			}

			Action[] actions = (Action[]) actionsList.toArray(new Action[0]);

			if (actions.length > 0)
			{
				ActionsViewEvent e = new ActionsViewEvent(ActionsViewEventTypes.DELETE);
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
		ImageDescriptor imageDescriptor = UnifiedEditorsPlugin.getImageDescriptor(imageFilePath);

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
	public void expandActionSet(String setName)
	{
		TreeItem[] treeItems = viewer.getTree().getItems();

		for (int i = 0; i < treeItems.length; i++)
		{
			Object o = treeItems[i].getData();

			if (o instanceof ActionSet)
			{
				ActionSet p = (ActionSet) o;
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
		IAction a = findAction(actionPath);
		
		if (a != null && a instanceof Action)
		{
			selectAndReveal((Action) a);
		}
	}

	/**
	 * selectAndReveal
	 * 
	 * @param action
	 */
	public void selectAndReveal(Action action)
	{
		TreeItem[] treeItems = viewer.getTree().getItems();

		forcePopulateTree();

		for (int i = 0; i < treeItems.length; i++)
		{
			Object o = treeItems[i].getData();

			if (o instanceof ActionSet)
			{
				TreeItem[] children = treeItems[i].getItems();

				for (int j = 0; j < children.length; j++)
				{
					IAction a = (IAction) children[j].getData();

					if (a == action)
					{
						viewer.getTree().showItem(children[j]);
						viewer.getTree().setSelection(new TreeItem[] { children[j] });
						return;
					}

				}
			}
			else if (o instanceof Action)
			{
				Action a = (Action) o;
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
	 * 		The action set name to find
	 * @return
	 * 		Returns the matching action set or null;
	 */
	public ActionSet findActionSet(String name)
	{
		ActionSet result = null;
		TreeItem[] treeItems = viewer.getTree().getItems();

		for (int i = 0; i < treeItems.length; i++)
		{
			Object itemData = treeItems[i].getData();

			if (itemData instanceof ActionSet)
			{
				ActionSet actionSet = (ActionSet) itemData;
				
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
	public Action findAction(String path)
	{
		Action result = null;
		
		if (path != null && path.length() > 0 && path.charAt(0) == '/')
		{
			int slashIndex = path.indexOf('/', 1);
			
			if (slashIndex != -1)
			{
				String actionSetName = path.substring(1, slashIndex);
				String actionName = path.substring(slashIndex + 1);
				
				ActionSet actionSet = findActionSet(actionSetName);
				
				if (actionSet != null)
				{
					Action[] actions = actionSet.getActions();
					
					for (int i = 0; i < actions.length; i++)
					{
						Action action = actions[i];
						
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
		IAction action = findAction(actionName);
		
		if (action != null)
		{
			ActionsViewEvent actionEvent = new ActionsViewEvent(ActionsViewEventTypes.EXECUTE);
			IAction[] actions = new IAction[] { action };
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
	private void editAction(final IAction a)
	{
		if (a == null)
		{
			return;
		}

		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorDescriptor editorDesc = null;

		File f = new File(a.getFilePath());

		try
		{
			editorDesc = IDE.getEditorDescriptor(f.getName());
		}
		catch (PartInitException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.ActionsView_ErrorOpeningFile, e);
		}

		try
		{
			IEditorPart editorPart = IDE.openEditor(page, CoreUIUtils.createJavaFileEditorInput(f), editorDesc.getId());

			if (editorPart instanceof IUnifiedEditor)
			{
				IUnifiedEditor editor = (IUnifiedEditor) editorPart;

				editor.addSaveListener(new ISaveEvent()
				{
					public void onSave(IEditorPart part)
					{
						reloadAction(a);
					}
				});
			}
		}
		catch (PartInitException e)
		{
			IdeLog.logError(CoreUIPlugin.getDefault(), Messages.ActionsView_ErrorOpeningEditor, e);
		}
	}

	/**
	 * reloadAction
	 * 
	 * @param a
	 */
	private void reloadAction(final IAction a)
	{
		ActionsViewEvent e = new ActionsViewEvent(ActionsViewEventTypes.RELOAD);
		e.setActions(new IAction[] { a });
		fireActionsViewEvent(e);
	}
}
