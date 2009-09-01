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
package com.aptana.ide.views.outline;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.ContributedOutline;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.UnifiedEditor;
import com.aptana.ide.lexer.IRange;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Paul Colton
 * @author Kevin Sawicki
 * @author Kevin Lindsey
 */
public class UnifiedOutlinePage extends ContentOutlinePage implements ISelectionChangedListener, IUnifiedOutlinePage
{
	/**
	 * @author Pavel Petrochenko
	 */
	private static final class OpenExternalAction extends Action
	{
		IResolvableItem item;

		String editorId;

		/**
		 * @param item
		 */
		OpenExternalAction(IResolvableItem item)
		{

			IEditorDescriptor editorDescriptor;
			try
			{
				editorDescriptor = IDE.getEditorDescriptor(item.getEditorInput().getName());
				editorId = editorDescriptor.getId();
				this.setImageDescriptor(editorDescriptor.getImageDescriptor());
			}
			catch (PartInitException e)
			{
				IdeLog.logError(UnifiedEditorsPlugin.getDefault(), e.getMessage());
			}
			this.item = item;
			this.setToolTipText(Messages.UnifiedOutlinePage_Open);
		}

		/**
		 * opens editor on external item
		 * 
		 * @see org.eclipse.jface.action.Action#run()
		 */
		public void run()
		{
			IEditorInput input = item.getEditorInput();

			IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try
			{
				IEditorPart openEditor = IDE.openEditor(activePage, input, editorId);
				if (openEditor instanceof ITextEditor)
				{
					ITextEditor ed = (ITextEditor) openEditor;
					if (item instanceof IRange)
					{
						int position = ((IRange) item).getStartingOffset();
						ed.selectAndReveal(position, 0);
						return;
					}
					if (item instanceof OutlineItem)
					{
						int position = ((OutlineItem) item).getStartingOffset();
						ed.selectAndReveal(position, 0);
						return;
					}
				}
				return;
			}
			catch (PartInitException e)
			{
				IdeLog.logError(UnifiedEditorsPlugin.getDefault(), e.getMessage());
				return;
			}

		}
	}

	private Composite _composite;
	private SashForm _outlineSash;
	private SashForm _outlineTabsSash;
	private CTabFolder _outlineTabs;
	private UnifiedEditor _editor;
	private PatternFilter _filter;
	private TreeViewer _treeViewer;

	private boolean _hide;

	private ActionContributionItem _sortItem;
	private ActionContributionItem _collapseItem;
	private ActionContributionItem _expandItem;
	private ActionContributionItem _hidePrivateItem;
	private ActionContributionItem _splitItem;
	private HidePrivateAction _hidePrivateAction;

	private Map<String, ContributedOutline> _outlines;
	private UnifiedOutlineProvider outlineProvider;
	private BaseFilter[] _filters;

	private IActionBars _actionBars;

	private ActionContributionItem openAction;

	/**
	 * UnifiedOutlinePage
	 * 
	 * @param editor
	 */
	public UnifiedOutlinePage(UnifiedEditor editor)
	{
		this._editor = editor;
		editor.getViewer().getTextWidget().addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent e)
			{
				if (outlineProvider != null)
				{
					// notify provider that active editor was changed
					outlineProvider.pageActivated(UnifiedOutlinePage.this);
				}
			}
		});
		editor.getViewer().getTextWidget().addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e)
			{
				if (outlineProvider != null)
				{
					outlineProvider.pageClosed(UnifiedOutlinePage.this);
				}
			}
		});
		this._outlines = new HashMap<String, ContributedOutline>();
	}

	/**
	 * addOutline
	 * 
	 * @param outline
	 * @param name
	 */
	public void addOutline(ContributedOutline outline, String name)
	{
		this._outlines.put(name, outline);
	}

	/**
	 * createComposite
	 * 
	 * @param parent
	 * @return Composite
	 */
	private Composite createComposite(Composite parent)
	{
		GridLayout contentAreaLayout = new GridLayout();

		contentAreaLayout.numColumns = 1;
		contentAreaLayout.makeColumnsEqualWidth = false;
		contentAreaLayout.marginHeight = 0;
		contentAreaLayout.marginWidth = 0;
		contentAreaLayout.verticalSpacing = 0;
		contentAreaLayout.horizontalSpacing = 0;

		Composite result = new Composite(parent, SWT.NONE);

		result.setLayout(contentAreaLayout);
		result.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		return result;
	}

	/**
	 * createContributedOutlines
	 */
	private void createContributedOutlines(CTabFolder outlineTabs, SashForm outlineSash)
	{
		Iterator<String> iter = this._outlines.keySet().iterator();

		while (iter.hasNext())
		{
			String key = iter.next();
			ContributedOutline outline = this._outlines.get(key);
			CTabItem tab = new CTabItem(outlineTabs, SWT.NONE);

			Composite previewComp = new Composite(outlineSash, SWT.NONE);
			GridLayout contentAreaLayout = new GridLayout();

			contentAreaLayout.numColumns = 1;
			contentAreaLayout.makeColumnsEqualWidth = true;
			contentAreaLayout.marginHeight = 0;
			contentAreaLayout.marginWidth = 0;
			contentAreaLayout.verticalSpacing = 0;
			contentAreaLayout.horizontalSpacing = 0;
			previewComp.setLayout(contentAreaLayout);
			previewComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

			Label outlineLabel = new Label(previewComp, SWT.NONE);
			outlineLabel.setText(key);
			outlineLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

			tab.setText(key);
			SashForm preForm = new SashForm(outlineTabs, SWT.NONE);
			contentAreaLayout = new GridLayout();
			contentAreaLayout.numColumns = 1;
			contentAreaLayout.makeColumnsEqualWidth = false;
			contentAreaLayout.marginHeight = 0;
			contentAreaLayout.marginWidth = 0;
			contentAreaLayout.verticalSpacing = 0;
			contentAreaLayout.horizontalSpacing = 0;
			preForm.setLayout(contentAreaLayout);
			preForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			outline.createControl(preForm);
			tab.setControl(preForm);
		}
	}

	/**
	 * Creates page control.
	 * 
	 * @param parent
	 *            - page parent.
	 * @param createSearchArea
	 *            - whether to create search area or it would be created separately through calling
	 *            {@link UnifiedOutlinePage#createSearchArea(Composite)} by a third party.
	 */
	public void createControl(Composite parent)
	{
		// create main container
		this._composite = createComposite(parent);

		createFilters(this._composite);

		// create tab sash
		this._outlineSash = this.createSash(this._composite);

		this.createSourceSash(this._outlineSash);

		// create tabs
		this._outlineTabsSash = this.createOutlineTabs(this._composite);

		// create tree view
		this._filter = new PatternFilter()
		{
			/**
			 * @see org.eclipse.ui.dialogs.PatternFilter#isLeafMatch(org.eclipse.jface.viewers.Viewer, java.lang.Object)
			 */
			protected boolean isLeafMatch(Viewer viewer, Object element)
			{
				boolean result = true;
				String label = null;

				if (element instanceof OutlineItem)
				{
					label = ((OutlineItem) element).getLabel();
				}
				else if (element instanceof IParseNode)
				{
					label = UnifiedOutlineProvider.getInstance().getText(element);
				}

				if (label != null)
				{
					result = this.wordMatches(label);
				}

				return result;
			}
		};
		this._filter.setIncludeLeadingWildcard(true);
		this._treeViewer = this.createTreeViewer(this._outlineTabsSash, this._filter);

		// apply tree filters
		this._treeViewer.addFilter(new UnifiedViewerFilter(this));
		// add filters
		for (BaseFilter filter : this._filters)
		{
			this._treeViewer.addFilter(filter);
		}

		// add listeners
		this._treeViewer.addSelectionChangedListener(this);

		// refresh tree
		this.updateProviders();
		this.refresh();
	}

	private void createFilters(Composite result)
	{
		// create filter toolbar
		ToolBar toolBar = new ToolBar(result, SWT.HORIZONTAL);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		List<BaseFilter> filters = new ArrayList<BaseFilter>();

		EditorFileContext fileContext = this._editor.getFileContext();
		String language = fileContext.getDefaultLanguage();

		for (FilterActionInfo filterInfo : UnifiedOutlineProvider.getInstance().getFilterActionInfos(language))
		{
			final BaseFilter filter = filterInfo.getFilter();
			ToolItem item = new ToolItem(toolBar, SWT.CHECK);

			filters.add(filter);

			item.setImage(filterInfo.getImageDescriptor().createImage());
			item.setToolTipText(filterInfo.getToolTip());
			item.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					ToolItem item = (ToolItem) e.widget;

					filter.setActive(item.getSelection());
					_treeViewer.refresh(true);
				}
			});
		}

		this._filters = filters.toArray(new BaseFilter[filters.size()]);
	}

	/**
	 * createOutlineTabs
	 * 
	 * @param parent
	 * @return tab folder
	 */
	private SashForm createOutlineTabs(Composite parent)
	{
		this._outlineTabs = new CTabFolder(parent, SWT.BOTTOM);

		this._outlineTabs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// create source tab
		SashForm sourceForm = new SashForm(this._outlineTabs, SWT.NONE);

		sourceForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		CTabItem sourceTab = new CTabItem(this._outlineTabs, SWT.NONE);

		sourceTab.setText(Messages.UnifiedOutlinePage_Source);
		sourceTab.setControl(sourceForm);

		this._outlineTabs.setSelection(sourceTab);

		// create contributed outline tabs
		this.createContributedOutlines(this._outlineTabs, this._outlineSash);

		return sourceForm;
	}

	/**
	 * createSash
	 * 
	 * @param parent
	 * @return sash form
	 */
	private SashForm createSash(Composite parent)
	{
		// create layout
		GridLayout contentAreaLayout = new GridLayout();

		contentAreaLayout.numColumns = 1;
		contentAreaLayout.makeColumnsEqualWidth = false;
		contentAreaLayout.marginHeight = 0;
		contentAreaLayout.marginWidth = 0;
		contentAreaLayout.verticalSpacing = 0;
		contentAreaLayout.horizontalSpacing = 0;

		// create layout data
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);

		gridData.exclude = true;

		// create form
		SashForm result = new SashForm(parent, SWT.VERTICAL);

		// set layout and layout data
		result.setLayoutData(gridData);
		result.setLayout(contentAreaLayout);
		result.setVisible(false);

		return result;
	}

	/**
	 * createSourceSash
	 */
	private void createSourceSash(Composite parent)
	{
		Composite previewComp = new Composite(parent, SWT.NONE);
		GridLayout contentAreaLayout = new GridLayout();

		contentAreaLayout.numColumns = 1;
		contentAreaLayout.makeColumnsEqualWidth = false;
		contentAreaLayout.marginHeight = 0;
		contentAreaLayout.marginWidth = 0;
		contentAreaLayout.verticalSpacing = 0;
		contentAreaLayout.horizontalSpacing = 0;
		previewComp.setLayout(contentAreaLayout);
		previewComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label outlineLabel = new Label(previewComp, SWT.NONE);
		outlineLabel.setText(Messages.UnifiedOutlinePage_Source);
		outlineLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
	}

	/**
	 * createTreeViewer
	 * 
	 * @param parent
	 * @return TreeViewer
	 */
	private TreeViewer createTreeViewer(Composite parent, PatternFilter filter)
	{
		FilteredTree tree = null;
		try
		{
			// When available (3.5+) use new constructor
			Constructor<FilteredTree> cons = FilteredTree.class.getConstructor(Composite.class, int.class,
					PatternFilter.class, boolean.class);
			tree = cons.newInstance(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL, filter, true);
		}
		catch (Exception e)
		{
			// fallback to deprecated old constructor when new one not available
			tree = new FilteredTree(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL, filter);
		}

		TreeViewer result = tree.getViewer();

		outlineProvider = UnifiedOutlineProvider.getInstance();
		outlineProvider.setOutlinePage(this);

		result.setLabelProvider(outlineProvider);
		result.setContentProvider(outlineProvider);
		result.setInput(this._editor);

		// add selection changed listener
		result.addSelectionChangedListener(new ISelectionChangedListener()
		{
			/**
			 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
			 */
			public void selectionChanged(SelectionChangedEvent event)
			{
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();

				if (selection.size() == 1)
				{
					Object item = selection.getFirstElement();

					if (item != null && item instanceof OutlineItem)
					{
						OutlineItem outlineItem = (OutlineItem) selection.getFirstElement();

						// Only select and reveal in editor if the tree viewer is focused meaning the selection
						// originated from a user selection on the tree itself

						// move cursor to start of this item's text
						if (_treeViewer != null && _treeViewer.getTree() != null && !_treeViewer.getTree().isDisposed()
								&& _treeViewer.getTree().isFocusControl())
						{
							_editor.selectAndReveal(outlineItem.getStartingOffset(), 0);
						}

						// TODO: activate the editor window
						// Note this code works, but should only be called on mouse clicks so users can
						// navigate through outline items using the keyboard
						// IWorkbenchWindow window = JSPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
						//					
						// if (window != null)
						// {
						// IWorkbenchPage page = window.getActivePage();
						//						
						// page.activate(_editor);
						// }
					}
				}
			}
		});

		result.setComparer(new IElementComparer()
		{
			/**
			 * @see org.eclipse.jface.viewers.IElementComparer#equals(java.lang.Object, java.lang.Object)
			 */
			public boolean equals(Object a, Object b)
			{
				boolean result = false;

				if (a instanceof OutlineItem && b instanceof OutlineItem)
				{
					OutlineItem item1 = (OutlineItem) a;
					OutlineItem item2 = (OutlineItem) b;

					result = item1.equals(item2);
				}
				else if (a instanceof IParseNode && b instanceof IParseNode)
				{
					if (a == b)
					{
						result = true;
					}
					else
					{
						IParseNode node1 = (IParseNode) a;
						IParseNode node2 = (IParseNode) b;
						String path1 = node1.getUniquePath();
						String path2 = node2.getUniquePath();

						result = path1.equals(path2);
					}
				}
				else
				{
					result = (a == b);
				}

				return result;
			}

			/**
			 * @see org.eclipse.jface.viewers.IElementComparer#hashCode(java.lang.Object)
			 */
			public int hashCode(Object element)
			{
				return 0;
			}
		});

		return result;
	}

	/**
	 * @see org.eclipse.ui.part.Page#dispose()
	 */
	public void dispose()
	{
		super.dispose();

		// if (this._delayedRefreshJob != null && this._editor != null)
		// {
		// IEditorInput editorInput = this._editor.getEditorInput();
		// IDocument document = this._editor.getDocumentProvider().getDocument(editorInput);
		// // may be null in some cases
		// if (document != null)
		// {
		// document.removeDocumentListener(this._documentListener);
		// }
		// this._editor.removeFileServiceChangeListener(_serviceListener);
		// }
	}

	/**
	 * getContributedOutlines
	 * 
	 * @return HashMap
	 */
	public Map<String, ContributedOutline> getContributedOutlines()
	{
		return this._outlines;
	}

	/**
	 * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#getControl()
	 */
	public Control getControl()
	{
		return this._composite;
	}

	/**
	 * getOutlineSash
	 * 
	 * @return SashForm
	 */
	public SashForm getOutlineSash()
	{
		return this._outlineSash;
	}

	/**
	 * getOutlineTabs
	 * 
	 * @return CTabFolder
	 */
	public CTabFolder getOutlineTabs()
	{
		return this._outlineTabs;
	}

	/**
	 * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#getTreeViewer()
	 */
	public TreeViewer getTreeViewer()
	{
		return this._treeViewer;
	}

	/**
	 * @return editor
	 */
	public UnifiedEditor getUnifiedEditor()
	{
		return this._editor;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hidePrivateMembers()
	{
		return this._hide;
	}

	/**
	 * refresh
	 */
	public void refresh()
	{
		if (!_treeViewer.getControl().isDisposed())
		{
			this._treeViewer.refresh();
		}
	}

	/**
	 * removeOpenActionIfNeeded
	 */
	private void removeOpenActionIfNeeded()
	{
		if (this.openAction != null)
		{
			_actionBars.getToolBarManager().remove(openAction);
			openAction = null;
			_actionBars.getToolBarManager().update(false);
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event)
	{
		IStructuredSelection selection = (IStructuredSelection) event.getSelection();

		// If a node in the outline view is selected
		if (selection.size() == 1)
		{
			Object element = selection.getFirstElement();

			if (element instanceof IResolvableItem)
			{
				IResolvableItem item = (IResolvableItem) element;
				// if item is resolvable and is targeting on external content
				if (item.isResolvable())
				{
					// removing all item if needed
					if (openAction != null)
					{
						_actionBars.getToolBarManager().remove(openAction);
					}
					openAction = new ActionContributionItem(new OpenExternalAction(item));
					_actionBars.getToolBarManager().add(openAction);
					_actionBars.getToolBarManager().update(false);
					// while item is not able to be resolved in current file
					// getting parent item
					while (!item.stillHighlight())
					{
						item = item.getParentItem();
						if (item == null)
						{
							return;
						}
					}
					// selecting it in editor
					if (item instanceof IParseNode)
					{
						int position = ((IParseNode) item).getStartingOffset();
						this._editor.selectAndReveal(position, 0);
						return;
					}
				}
				else
				{
					// removing item from toolbar
					removeOpenActionIfNeeded();
				}
			}
			removeOpenActionIfNeeded();
			if (element instanceof IRange)
			{
				int position = ((IRange) element).getStartingOffset();

				this._editor.selectAndReveal(position, 0);
			}

		}
		else
		{
			removeOpenActionIfNeeded();
			this._editor.getViewer().removeRangeIndication();
		}
	}

	/**
	 * @see org.eclipse.ui.part.Page#setActionBars(org.eclipse.ui.IActionBars)
	 */
	public void setActionBars(IActionBars actionBars)
	{
		// add split action
		if (this._outlines.size() > 0)
		{
			SplitOutlinesAction splitAction = new SplitOutlinesAction(this);

			splitAction.setEnabled(this._composite.isReparentable());
			this._splitItem = new ActionContributionItem(splitAction);
			actionBars.getToolBarManager().add(this._splitItem);
		}
		this._actionBars = actionBars;
		// add sort action
		SortAction sortAction = new SortAction(this);
		IPreferenceStore store = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
		boolean sort = store.getBoolean(IPreferenceConstants.SORT_OUTLINE_ALPHABETICALLY);
		sortAction.setChecked(sort);
		if (sort)
		{
			getTreeViewer().setSorter(SortAction.SORTER);
		}
		this._sortItem = new ActionContributionItem(sortAction);
		actionBars.getToolBarManager().add(this._sortItem);

		// add hide private members action
		this._hidePrivateAction = new HidePrivateAction(this);
		this._hidePrivateItem = new ActionContributionItem(this._hidePrivateAction);
		actionBars.getToolBarManager().add(this._hidePrivateItem);

		// add collapse all action
		CollapseAction collapseAction = new CollapseAction(this);
		this._collapseItem = new ActionContributionItem(collapseAction);
		actionBars.getToolBarManager().add(this._collapseItem);

		Action expandAction = new Action(Messages.UnifiedOutlinePage_ExpandAll)
		{
			public void run()
			{
				getTreeViewer().expandAll();
			}
		};
		expandAction.setImageDescriptor(UnifiedEditorsPlugin.getImageDescriptor("icons/expandall.gif")); //$NON-NLS-1$
		expandAction.setToolTipText(Messages.UnifiedOutlinePage_CollapseAll);
		this._expandItem = new ActionContributionItem(expandAction);
		actionBars.getToolBarManager().add(this._expandItem);

		super.setActionBars(actionBars);
	}

	/**
	 * Contibutes actions to quick outline menu.
	 * 
	 * @param manager
	 *            - menu manager.
	 */
	public void contributeToQuickOutlineMenu(IMenuManager manager)
	{
		// add split action
		if (this._outlines.size() > 0)
		{
			SplitOutlinesAction splitAction = new SplitOutlinesAction(this);

			splitAction.setEnabled(this._composite.isReparentable());
			this._splitItem = new ActionContributionItem(splitAction);
			manager.add(this._splitItem);
		}
		// add sort action
		SortAction sortAction = new SortAction(this);
		IPreferenceStore store = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
		boolean sort = store.getBoolean(IPreferenceConstants.SORT_OUTLINE_ALPHABETICALLY);
		sortAction.setChecked(sort);
		if (sort)
		{
			getTreeViewer().setSorter(SortAction.SORTER);
		}
		this._sortItem = new ActionContributionItem(sortAction);
		manager.add(this._sortItem);

		// add hide private members action
		this._hidePrivateAction = new HidePrivateAction(this);
		this._hidePrivateItem = new ActionContributionItem(this._hidePrivateAction);
		manager.add(this._hidePrivateItem);

		// add collapse all action
		CollapseAction collapseAction = new CollapseAction(this);
		this._collapseItem = new ActionContributionItem(collapseAction);
		manager.add(this._collapseItem);

		Action expandAction = new Action(Messages.UnifiedOutlinePage_ExpandAll)
		{
			public void run()
			{
				getTreeViewer().expandAll();
			}
		};
		expandAction.setImageDescriptor(UnifiedEditorsPlugin.getImageDescriptor("icons/expandall.gif")); //$NON-NLS-1$
		expandAction.setToolTipText(Messages.UnifiedOutlinePage_CollapseAll);
		this._expandItem = new ActionContributionItem(expandAction);
		manager.add(this._expandItem);
	}

	/**
	 * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#setFocus()
	 */
	public void setFocus()
	{
		this._treeViewer.getControl().setFocus();
	}

	/**
	 * togglePrivateMemberVisibility
	 */
	public void togglePrivateMemberVisibility()
	{
		this._hide = (this._hide == false);

		this.refresh();
	}

	/**
	 * updateProviders
	 */
	private void updateProviders()
	{
		EditorFileContext fileContext = _editor.getFileContext();

		if (fileContext != null)
		{
			UnifiedOutlineProvider provider = UnifiedOutlineProvider.getInstance();

			provider.setCurrentLanguage(fileContext.getDefaultLanguage());
			provider.fireBeforeRefreshEvent(fileContext);
		}
	}
}
