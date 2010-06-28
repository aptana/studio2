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
package com.aptana.ide.snippets;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.ide.core.ui.PreferenceUtils;
import com.aptana.ide.core.ui.WorkbenchHelper;

/**
 * @author Kevin Lindsey
 */
public class SnippetsView extends ViewPart implements SnippetListChangeListener {
	private StackLayout _layout;
	private TreeViewer _viewer;
	private String _textPattern = ""; //$NON-NLS-1$
	private Action _applyAction;
	private Action _doubleClickAction;
	private Action _editAction;
	private Action _toggleAction;
	private Action collapseAllAction;
	private DrillDownAdapter drillDown;
	private SnippetsViewLabelProvider snippetsViewLabelProvider;
	private SnippetsViewContentProvider snippetsViewContentProvider;

	/**
	 * SnippetsView
	 */
	public SnippetsView() {
	}

	/**
	 * applySnippet
	 * 
	 * @param snippet
	 */
	private void applySnippet(Snippet snippet) {
		IEditorPart activeEditor = getActiveEditor();

		if (activeEditor != null && activeEditor instanceof ITextEditor) {
			final ITextEditor editor = (ITextEditor) activeEditor;
			snippet.apply(editor);
		}
	}

	

	/**
	 * createActions
	 */
	private void createActions() {
		createApplyAction();
		createDoubleClickAction();
		createEditAction();
		createToggleAction();
		createCollapseAllAction();
		drillDown = new DrillDownAdapter(this._viewer);
		// attach double-click action to tree
		this._viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				_doubleClickAction.run();
			}
		});
	}

	private void createCollapseAllAction() {
		this.collapseAllAction = new Action(Messages.SnippetsView_CollapseAll) {

			public void run() {
				if (_viewer != null) {
					_viewer.collapseAll();
				}
			}

		};
		this.collapseAllAction
				.setToolTipText(Messages.SnippetsView_CollapseAll);
		this.collapseAllAction.setImageDescriptor(SnippetsPlugin
				.getImageDescriptor("icons/collapseall.gif")); //$NON-NLS-1$
	}

	/**
	 * createApplyAction
	 */
	private void createApplyAction() {
		this._applyAction = new Action() {
			public void run() {
				ISelection selection = _viewer.getSelection();
				Object firstElement = ((IStructuredSelection) selection)
						.getFirstElement();

				if (firstElement instanceof Snippet) {
					applySnippet((Snippet) firstElement);
				}
			}
		};

		this._applyAction.setText(Messages.SnippetsView_Apply_Snippet);
	}

	/**
	 * createDoubleClickAction
	 */
	private void createDoubleClickAction() {
		this._doubleClickAction = new Action() {
			public void run() {
				ISelection selection = _viewer.getSelection();
				Object firstElement = ((IStructuredSelection) selection)
						.getFirstElement();

				if (firstElement instanceof String) {
					toggleElementState(firstElement);
				} else if (firstElement instanceof Snippet) {
					applySnippet((Snippet) firstElement);
				}
			}
		};
	}

	/**
	 * createEditAction
	 */
	private void createEditAction() {
		this._editAction = new Action() {
			public void run() {
				ISelection selection = _viewer.getSelection();
				Object firstElement = ((IStructuredSelection) selection)
						.getFirstElement();

				if (firstElement instanceof Snippet) {
					editSnippet((Snippet) firstElement);
				}
			}
		};

		this._editAction.setText(Messages.SnippetsView_Edit_Snippet);
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		this._layout = new StackLayout();
		parent.setLayout(this._layout);
		Composite body = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		body.setLayout(gridLayout);
		Composite head = new Composite(body, SWT.NONE);
		this._viewer = this.createTreeViewer(body);
		GridLayout gridLayout2 = new GridLayout(2, false);
		gridLayout2.marginHeight = 5;
		gridLayout2.verticalSpacing = 0;
		head.setLayout(gridLayout2);
		Label l = new Label(head, SWT.NONE);
		l.setText(Messages.SnippetsView_Filter);
		final Text txt = new Text(head, SWT.BORDER);
		txt.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				_textPattern = txt.getText();
				_viewer.getControl().setRedraw(false);
				_viewer.refresh();
				_viewer.getControl().setRedraw(true);
			}

		});
		_viewer.addFilter(new ViewerFilter() {

			private int k = 0;

			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				if (snippetsViewLabelProvider.getText(element).contains(
						_textPattern))
					return true;
				Object[] children = snippetsViewContentProvider
						.getChildren(element);
				for (int a = 0; a < children.length; a++) {
					if (select(viewer, element, children[a])) {
						if (k <= 4) {
							k++;
							_viewer.expandToLevel(element, 2);
							k--;
						}
						return true;
					}
				}
				return false;
			}

		});
		txt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		head.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this._layout.topControl = body;
		this._viewer.getControl().setLayoutData(
				new GridData(GridData.FILL_BOTH));
		this.createActions();
		this.hookContextMenu();
		this.hookToolbarActions();
		SnippetsStartup.init();
		PreferenceUtils.registerBackgroundColorPreference(_viewer.getControl(),
		"com.aptana.ide.core.ui.background.color.snippetsView"); //$NON-NLS-1$
		PreferenceUtils.registerForegroundColorPreference(_viewer.getControl(),
		"com.aptana.ide.core.ui.foreground.color.snippetsView"); //$NON-NLS-1$
		SnippetsManager snippets = SnippetsManager.getInstance();
		this._viewer.setInput(snippets);
		snippets.addChangeListener(this);
	}

	private void hookToolbarActions() {
		IActionBars bars = getViewSite().getActionBars();
		IToolBarManager manager = bars.getToolBarManager();
		drillDown.addNavigationActions(manager);
		manager.add(new Separator());
		manager.add(collapseAllAction);
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	public void dispose() {
		super.dispose();
		SnippetsManager snippets = SnippetsManager.getInstance();
		snippets.removeChangeListener(this);
	}

	/**
	 * createToggleAction
	 */
	private void createToggleAction() {
		this._toggleAction = new Action() {
			public void run() {
				ISelection selection = _viewer.getSelection();
				Object firstElement = ((IStructuredSelection) selection)
						.getFirstElement();

				if (firstElement instanceof String) {
					toggleElementState(firstElement);
				}
			}
		};

		this._toggleAction
				.setText(Messages.SnippetsView_Expand_Collapse_Category);
	}

	/**
	 * createTreeViewer
	 * 
	 * @param parent
	 * @return TreeViewer
	 */
	private TreeViewer createTreeViewer(Composite parent) {
		Tree tree = new Tree(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		TreeViewer viewer = new TreeViewer(tree);

		snippetsViewContentProvider = new SnippetsViewContentProvider();
		viewer.setContentProvider(snippetsViewContentProvider);
		snippetsViewLabelProvider = new SnippetsViewLabelProvider();
		viewer.setLabelProvider(snippetsViewLabelProvider);

		return viewer;
	}

	/**
	 * editSnippet
	 * 
	 * @param snippet
	 */
	private void editSnippet(Snippet snippet) {
		File file = snippet.getFile();

		if (file != null) {
			WorkbenchHelper.openFile(file, PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow());
		}
	}

	/**
	 * fillContextMenu
	 * 
	 * @param manager
	 * @param element
	 */
	private void fillContextMenu(IMenuManager manager, Object element) {
		if (element instanceof SnippetsManager.SnippetNode) {
			manager.add(this._toggleAction);

		} else if (element instanceof Snippet) {
			manager.add(this._applyAction);
			manager.add(this._editAction);
		}
		drillDown.addNavigationActions(manager);

		// manager.add(new Separator());
		// manager.add(actionReload);

		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/**
	 * getActiveEditor
	 * 
	 * @return IEditorPart
	 */
	private IEditorPart getActiveEditor() {
		/**
		 * ActiveEditorRef
		 */
		class ActiveEditorRef {
			public IEditorPart activeEditor;
		}

		final IWorkbench workbench = PlatformUI.getWorkbench();
		final ActiveEditorRef activeEditor = new ActiveEditorRef();
		Display display = workbench.getDisplay();
		IEditorPart result;

		display.syncExec(new Runnable() {
			public void run() {
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();

				// this can be null if you close all perspectives
				if (window != null && window.getActivePage() != null) {
					activeEditor.activeEditor = window.getActivePage()
							.getActiveEditor();
				}
			}
		});

		result = activeEditor.activeEditor;

		return result;
	}

	/**
	 * hookContextMenu
	 */
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$

		menuMgr.setRemoveAllWhenShown(true);

		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ISelection selection = _viewer.getSelection();
				Object firstElement = ((IStructuredSelection) selection)
						.getFirstElement();

				fillContextMenu(manager, firstElement);
			}
		});

		Menu menu = menuMgr.createContextMenu(this._viewer.getControl());

		this._viewer.getControl().setMenu(menu);

		this.getSite().registerContextMenu(menuMgr, this._viewer);
	}

	/**
	 * listChanged
	 * 
	 * @param list
	 */
	public void listChanged(final SnippetsManager list) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		Display display = workbench.getDisplay();

		display.syncExec(new Runnable() {
			public void run() {
				if (!_viewer.getTree().isDisposed()
						&& _viewer.getContentProvider() != null) {
					_viewer.setInput(list);
				}
			}
		});
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
	}

	/**
	 * toggleElementState
	 * 
	 * @param element
	 */
	private void toggleElementState(Object element) {
		boolean state = this._viewer.getExpandedState(element);

		if (state) {
			this._viewer.setExpandedState(element, false);
		} else {
			this._viewer.setExpandedState(element, true);
		}
	}
}
