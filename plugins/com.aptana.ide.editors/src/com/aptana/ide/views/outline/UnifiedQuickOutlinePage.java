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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.text.AbstractDocument.ElementEdit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.progress.WorkbenchJob;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.ContributedOutline;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.IFileService;
import com.aptana.ide.editors.unified.IFileServiceChangeListener;
import com.aptana.ide.editors.unified.UnifiedEditor;
import com.aptana.ide.lexer.IRange;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Paul Colton
 * @author Kevin Sawicki
 * @author Kevin Lindsey
 */
public class UnifiedQuickOutlinePage extends ContentOutlinePage 
    implements IUnifiedOutlinePage, IOpenListener
{
    /**
     * Close listener.
     * @author Denis Denisenko
     */
    public interface ICloseListener
    {
        /**
         * Does close.
         */
        void doClose();
    }
    
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

	private static final int FILTER_REFRESH_DELAY = 200;
	private static final int REFRESH_DELAY = 500;

	private Composite _composite;
	private SashForm _outlineSash;
	private SashForm _outlineTabsSash;
	private CTabFolder _outlineTabs;
	private UnifiedEditor _editor;
	private PatternFilter _filter;
	private String _pattern;
	private WorkbenchJob _filterRefreshJob;
	private WorkbenchJob _delayedRefreshJob;
	private Text _searchBox;
	private TreeViewer _treeViewer;
	private IDocumentListener _documentListener;
	private IFileServiceChangeListener _serviceListener;
	private IDocument _document;

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

	private ActionContributionItem openAction;
	
	/**
	 * Close listeners.
	 */
	private List<ICloseListener> closeListeners = new ArrayList<ICloseListener>();
	private ToolBarManager _toolbarManager;

	/**
	 * UnifiedOutlinePage
	 * 
	 * @param editor
	 */
	public UnifiedQuickOutlinePage(UnifiedEditor editor)
	{
		this._editor = editor;
		editor.getViewer().getTextWidget().addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent e)
			{
				if (outlineProvider != null)
				{
					// notify provider that active editor was changed
					outlineProvider.pageActivated(UnifiedQuickOutlinePage.this);
				}
			}
		});
		editor.getViewer().getTextWidget().addDisposeListener(new DisposeListener()
		{
			public void widgetDisposed(DisposeEvent e)
			{
				if (outlineProvider != null)
				{
					outlineProvider.pageClosed(UnifiedQuickOutlinePage.this);
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
     * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent)
    {
       createControl(parent, true); 
    }

	/**
	 * Creates page control.
	 * @param parent - page parent.
	 * @param createSearchArea - whether to create search area or it would be created separately 
	 * through calling {@link UnifiedQuickOutlinePage#createSearchArea(Composite)} by a third party.
	 */
	public void createControl(Composite parent, boolean createSearchArea)
	{
		// create main container
		this._composite = createComposite(parent);

		if (createSearchArea)
		{
    		// create top strip and search area
    		this.createSearchArea(this._composite, false);
		}

		// create tab sash
		this._outlineSash = this.createSash(this._composite);

		this.createSourceSash();

		// create tabs
		this._outlineTabsSash = this.createOutlineTabs(this._composite);

		// create tree view
		this._treeViewer = this.createTreeViewer(this._outlineTabsSash);
		this._treeViewer.addSelectionChangedListener(this);

		// apply tree filters
		this._treeViewer.addFilter(new UnifiedViewerFilter(this));
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
		this._treeViewer.addFilter(this._filter);

		// add filters
		for (BaseFilter filter : this._filters)
		{
			this._treeViewer.addFilter(filter);
		}

		// add listeners
		this._treeViewer.addOpenListener(this);

		// create filter refresh job
		this._filterRefreshJob = this.createRefreshJob();
		this._filterRefreshJob.setSystem(true);

		// create delayed update job
		this._delayedRefreshJob = this.createDelayedRefreshJob();
		this._delayedRefreshJob.setSystem(true);

		// create document change listener and add to editor
		this.createDocumentListener();
		this.createServiceListener();
		this._document = this._editor.getDocumentProvider().getDocument(this._editor.getEditorInput());
		this._document.addDocumentListener(this._documentListener);
		this._editor.addFileServiceChangeListener(new IFileServiceChangeListener()
		{
			public void fileServiceChanged(IFileService newService)
			{
				if (_document != null)
				{
					_document.removeDocumentListener(_documentListener);
				}

				_document = _editor.getDocumentProvider().getDocument(_editor.getEditorInput());

				if (_document != null)
				{
					_document.addDocumentListener(_documentListener);
				}
			}
		});

		// refresh tree
		this.updateProviders();
		this.refresh();
	}
	
	/**
	 * Reveals position.
	 * @param documentPos - document position.
	 */
	public void revealPosition(int documentPos)
	{
	    IStructuredContentProvider provider = createProvider();
	    final Object[] originalElements = provider.getElements(_treeViewer.getInput());
	    if (originalElements == null || originalElements.length == 0)
	    {
	        return;
	    }
	    
	    //list of outline elements
	    List<Object> elements = new ArrayList<Object>();
	    
	    //map from outline elements to the list of its parent elements
	    //we need such a construction due to the fact that OutlineItem has no information about its parent 
	    final Map<Object, List<Object>> parents = new IdentityHashMap<Object, List<Object>>();
	    
	    for (Object el : originalElements)
	    {
	        elements.add(el);
	    }
	    	
	    if (provider instanceof ITreeContentProvider)
	    {
	        ITreeContentProvider treeContentProvider = (ITreeContentProvider) provider; 
	        for (Object element : originalElements)
	        {
	            expandElement(element, treeContentProvider, elements, parents);
	        }
	    }
	    
//	    TreePath path = new TreePath(new Object[]{elements.get(0), elements.get(4)});
//	    _treeViewer.setSelection(new TreeSelection(path), true);
	    
	    Object bestElement = null;
	    int bestElementStartingOffset = -1;
	    int bestElementEndingOffset = -1;
	    
	    for (Object element : elements)
        {
	        int start = -1;
	        int end = -1; 
            if (element instanceof IParseNode)
            {
                IParseNode node = (IParseNode) element;
                start = node.getStartingOffset();
                end = node.getEndingOffset();
            }
            else if (element instanceof OutlineItem)
            {
            	int refNodeStart = ((OutlineItem) element).getReferenceNode().getStartingOffset();
            	int refNodeEnd = ((OutlineItem) element).getReferenceNode().getEndingOffset();
            	int elementStart = ((OutlineItem) element).getStartingOffset();
            	int elementEnd = ((OutlineItem) element).getEndingOffset();
            	start = refNodeStart < elementStart ? refNodeStart : elementStart;
            	end = refNodeEnd > elementEnd ? refNodeEnd : elementEnd;
//                start = ((OutlineItem) element).getStartingOffset();
//                end = ((OutlineItem) element).getEndingOffset();
            }
            
	        if (start != -1)
	        {
	            if (start <= documentPos 
	                    && end >= documentPos)
	            {
	                //choosing the node having the least length
	                if (bestElement == null || 
	                        bestElementEndingOffset - bestElementStartingOffset > end - start)
	                {
	                    bestElement = element;
	                    bestElementStartingOffset = start;
	                    bestElementEndingOffset  = end;
	                }
	            }
	        }
        }	    
	    final Object toReveal = bestElement; 
	    WorkbenchJob job =  new WorkbenchJob("Initial reveal") {//$NON-NLS-1$
            /**
             * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
             */
            public IStatus runInUIThread(IProgressMonitor monitor)
            {
                if (_treeViewer.getControl().isDisposed())
                {
                    return Status.CANCEL_STATUS;
                }

                try
                {
                    // don't want the user to see updates that will be made to the tree
                    _treeViewer.getControl().setRedraw(false);
                    _treeViewer.refresh(true);
                    
                    //_treeViewer.setSelection(new StructuredSelection(toReveal), true);
                    List<Object> path = new ArrayList<Object>();
                    List<Object> p = parents.get(toReveal); 
                    if (p != null)
                    {
                    	path.addAll(p);
                    }
                    path.add(toReveal);
                    TreePath treePath = new TreePath(path.toArray());
                    _treeViewer.setSelection(new TreeSelection(treePath), true);
                }
                finally
                {
                    // done updating the tree - set redraw back to true
                    _treeViewer.getControl().setRedraw(true);
                }

                return Status.OK_STATUS;
            }
	    };
            
	    if (bestElement != null)
	    {
	        job.schedule(FILTER_REFRESH_DELAY);
	    }
	}

	/**
	 * Creates new outline content provider.
	 * Does not cache provider instance due to the fact that some
	 * of the language providers are not reusable.
	 * @return outline content provider
	 */
	private UnifiedOutlineProvider createProvider() {
		UnifiedOutlineProvider provider = new UnifiedOutlineProvider();
		provider.loadExtensions();
		provider.setOutlinePage(this);
		return provider;
	}

	/**
	 * createDelayedRefreshJob
	 * 
	 * @return workbench job
	 */
	private WorkbenchJob createDelayedRefreshJob()
	{
		return new WorkbenchJob("Refresh Content") { //$NON-NLS-1$
			/**
			 * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
			 */
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				try
				{
					if (_treeViewer.getControl().isDisposed())
					{
						return Status.CANCEL_STATUS;
					}

					// refresh();
					updateProviders();

					_treeViewer.setSelection(null);
					_treeViewer.refresh();
				}
				// SWT errors may be thrown here and will show as an error box since this is done on the UI thread
				// Catch everything and log it so that the dialog doesn't annoy the user since they may be typing into
				// the editor when this code throws errors and will impact them severely
				catch (Exception e)
				{
					IdeLog.logError(UnifiedEditorsPlugin.getDefault(),
							Messages.UnifiedOutlinePage_ErrorRefreshingOutline, e);
				}
				catch (Error e)
				{
					IdeLog.logError(UnifiedEditorsPlugin.getDefault(),
							Messages.UnifiedOutlinePage_ErrorRefreshingOutline, e);
				}

				return Status.OK_STATUS;
			}
		};
	}

	/**
	 * createDocumentListener
	 */
	private void createDocumentListener()
	{
		this._documentListener = new IDocumentListener()
		{
			/**
			 * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
			 */
			public void documentAboutToBeChanged(DocumentEvent event)
			{
			}

			/**
			 * @see org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse.jface.text.DocumentEvent)
			 */
			public void documentChanged(DocumentEvent event)
			{
				// cancel currently running job first, to prevent unnecessary redraw
				if (_delayedRefreshJob != null)
				{
					_delayedRefreshJob.cancel();
					_delayedRefreshJob.schedule(REFRESH_DELAY);
				}
			}
		};
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
	 * createRefreshJob
	 * 
	 * @return Workbench job
	 */
	private WorkbenchJob createRefreshJob()
	{
		return new WorkbenchJob("Refresh Filter") {//$NON-NLS-1$
			/**
			 * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
			 */
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				if (_treeViewer.getControl().isDisposed())
				{
					return Status.CANCEL_STATUS;
				}

				if (_pattern == null)
				{
					return Status.OK_STATUS;
				}

				_filter.setPattern(_pattern);

				try
				{
					// don't want the user to see updates that will be made to the tree
					_treeViewer.getControl().setRedraw(false);
					_treeViewer.refresh(true);

					if (_pattern.length() > 0)
					{
						/*
						 * Expand elements one at a time. After each is expanded, check to see if the filter text has
						 * been modified. If it has, then cancel the refresh job so the user doesn't have to endure
						 * expansion of all the nodes.
						 */
						IStructuredContentProvider provider = (IStructuredContentProvider) _treeViewer
								.getContentProvider();
						Object[] elements = provider.getElements(_treeViewer.getInput());

						for (int i = 0; i < elements.length; i++)
						{
							if (monitor.isCanceled())
							{
								return Status.CANCEL_STATUS;
							}

							_treeViewer.expandToLevel(elements[i], AbstractTreeViewer.ALL_LEVELS);
						}

						TreeItem[] items = _treeViewer.getTree().getItems();

						if (items.length > 0)
						{
							// to prevent scrolling
							_treeViewer.getTree().showItem(items[0]);
						}
					}
				}
				finally
				{
					// done updating the tree - set redraw back to true
					_treeViewer.getControl().setRedraw(true);
				}

				return Status.OK_STATUS;
			}

		};
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
	 * Create search area
	 * 
	 * @param parent - parent
	 * @param embedded - whether to create embedded search area.
	 * @return Composite
	 */
	public Composite createSearchArea(Composite parent, boolean embedded)
	{
		GridLayout contentAreaLayout = new GridLayout();

		contentAreaLayout.numColumns = 1;
		contentAreaLayout.makeColumnsEqualWidth = false;
		contentAreaLayout.marginHeight = 3;
		contentAreaLayout.marginWidth = 0;
		contentAreaLayout.verticalSpacing = 0;
		contentAreaLayout.horizontalSpacing = 0;

		Composite top = new Composite(parent, SWT.NONE);

		top.setLayout(contentAreaLayout);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		// create layout
		contentAreaLayout = new GridLayout();

		contentAreaLayout.numColumns = 3;
		contentAreaLayout.makeColumnsEqualWidth = false;
		contentAreaLayout.marginHeight = 0;
		contentAreaLayout.marginWidth = 0;
		contentAreaLayout.verticalSpacing = 0;
		contentAreaLayout.horizontalSpacing = 0;

		// create layout data
		GridData data = new GridData();

		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 1;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;

		// create layout
		Composite result = new Composite(top, SWT.NONE);

		// assign layout and layout data
		result.setLayout(contentAreaLayout);
		result.setLayoutData(data);

		// create label
		if (!embedded)
		{
    		Label searchLabel = new Label(result, SWT.NONE);
    		searchLabel.setText(Messages.UnifiedOutlinePage_Filter);
		}

		// create text box
		int style = 0;
		if (embedded)
		{
		    style = SWT.SINGLE | SWT.FOCUSED;
		}
		else
		{
		    style = SWT.SINGLE | SWT.BORDER;
		}
		this._searchBox = new Text(result, style);
		this._searchBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		this._searchBox.setEditable(true);
		this._searchBox.addModifyListener(new ModifyListener()
		{
			/**
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			public void modifyText(ModifyEvent e)
			{
				textChanged();
			}
		});
		this._searchBox.addKeyListener(new KeyListener(){

            public void keyPressed(KeyEvent e)
            {
                if (e.keyCode == SWT.ARROW_UP || e.keyCode == SWT.ARROW_DOWN)
                {
                    _treeViewer.getControl().setFocus();
                }
            }

            public void keyReleased(KeyEvent e)
            {
            }
		});

		ToolBar filtersToolBar = new ToolBar(result, SWT.HORIZONTAL);
		filtersToolBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		_toolbarManager = new ToolBarManager(filtersToolBar);
		final List<BaseFilter> filters = new ArrayList<BaseFilter>();

		EditorFileContext fileContext = this._editor.getFileContext();
		String language = fileContext.getDefaultLanguage();

		for (FilterActionInfo filterInfo : UnifiedOutlineProvider.getInstance().getFilterActionInfos(language))
		{
			final FilterActionInfo info = filterInfo;
			_toolbarManager.add(new ContributionItemStub()
			{

				
				public String getId() {
					return info.getName();
				}
	
				@Override
				public void fill(ToolBar parent, int index) 
				{
					final BaseFilter filter = info.getFilter();
					ToolItem item = new ToolItem(parent, SWT.CHECK);
	
					filters.add(index, filter);
	
					item.setImage(info.getImageDescriptor().createImage());
					item.setToolTipText(info.getToolTip());
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
			});
		}
		
		_toolbarManager.update(false);
		_toolbarManager.getControl().update();

		this._filters = filters.toArray(new BaseFilter[filters.size()]);

		return result;
	}

	/**
	 * createServiceListener
	 */
	private void createServiceListener()
	{
		this._serviceListener = new IFileServiceChangeListener()
		{
			public void fileServiceChanged(IFileService newService)
			{
				if (_document != null)
				{
					_document.removeDocumentListener(_documentListener);
				}
				_document = _editor.getDocumentProvider().getDocument(_editor.getEditorInput());
				if (_document != null)
				{
					_document.addDocumentListener(_documentListener);
				}
			}
		};
	}

	/**
	 * createSourceSash
	 */
	private void createSourceSash()
	{
		Composite previewComp = new Composite(this._outlineSash, SWT.NONE);
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
	private TreeViewer createTreeViewer(Composite parent)
	{
		TreeViewer result = new TreeViewer(new Tree(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL));

		//outlineProvider = UnifiedOutlineProvider.getInstance();
		outlineProvider = createProvider();

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
//				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
//
//				if (selection.size() == 1)
//				{
//					Object item = selection.getFirstElement();
//
//					if (item != null && item instanceof OutlineItem)
//					{
//						OutlineItem outlineItem = (OutlineItem) selection.getFirstElement();
//
//						// Only select and reveal in editor if the tree viewer is focused meaning the selection
//						// originated from a user selection on the tree itself
//
//						// move cursor to start of this item's text
//						if (_treeViewer != null && _treeViewer.getTree() != null && !_treeViewer.getTree().isDisposed()
//								&& _treeViewer.getTree().isFocusControl())
//						{
//							_editor.selectAndReveal(outlineItem.getStartingOffset(), 0);
//						}
//					}
//				}
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

		if (this._delayedRefreshJob != null && this._editor != null)
		{
			this._editor.getDocumentProvider().getDocument(this._editor.getEditorInput()).removeDocumentListener(
					this._documentListener);
			this._editor.removeFileServiceChangeListener(_serviceListener);
		}
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
	 * hidePrivateMembers
	 * 
	 * @return boolean
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
			_toolbarManager.remove(openAction);
			openAction = null;
			_toolbarManager.update(false);
		}
	}

	/**
     * {@inheritDoc}
     */
	public void open(OpenEvent event)
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
					if (item instanceof OutlineItem)
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
					}
				}
			}
			if (element instanceof IRange)
			{
				int position = ((IRange) element).getStartingOffset();

				this._editor.selectAndReveal(position, 0);
			}

			notifyCloseListeners();
		}
		else
		{
			removeOpenActionIfNeeded();
			this._editor.getViewer().removeRangeIndication();
		}
	}

//	/**
//	 * @see org.eclipse.ui.part.Page#setActionBars(org.eclipse.ui.IActionBars)
//	 */
//	public void setActionBars(IActionBars actionBars)
//	{
//		// add split action
//		if (this._outlines.size() > 0)
//		{
//			SplitOutlinesAction splitAction = new SplitOutlinesAction(this);
//
//			splitAction.setEnabled(this._composite.isReparentable());
//			this._splitItem = new ActionContributionItem(splitAction);
//			actionBars.getToolBarManager().add(this._splitItem);
//		}
//		this._actionBars = actionBars;
//		// add sort action
//		SortAction sortAction = new SortAction(this);
//		IPreferenceStore store = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
//		boolean sort = store.getBoolean(IPreferenceConstants.SORT_OUTLINE_ALPHABETICALLY);
//		sortAction.setChecked(sort);
//		if (sort)
//		{
//			getTreeViewer().setSorter(SortAction.SORTER);
//		}
//		this._sortItem = new ActionContributionItem(sortAction);
//		actionBars.getToolBarManager().add(this._sortItem);
//
//		// add hide private members action
//		this._hidePrivateAction = new HidePrivateAction(this);
//		this._hidePrivateItem = new ActionContributionItem(this._hidePrivateAction);
//		actionBars.getToolBarManager().add(this._hidePrivateItem);
//
//		// add collapse all action
//		CollapseAction collapseAction = new CollapseAction(this);
//		this._collapseItem = new ActionContributionItem(collapseAction);
//		actionBars.getToolBarManager().add(this._collapseItem);
//
//		Action expandAction = new Action(Messages.UnifiedOutlinePage_ExpandAll)
//		{
//			public void run()
//			{
//				getTreeViewer().expandAll();
//			}
//		};
//		expandAction.setImageDescriptor(UnifiedEditorsPlugin.getImageDescriptor("icons/expandall.gif")); //$NON-NLS-1$
//		expandAction.setToolTipText(Messages.UnifiedOutlinePage_CollapseAll);
//		this._expandItem = new ActionContributionItem(expandAction);
//		actionBars.getToolBarManager().add(this._expandItem);
//
//		super.setActionBars(actionBars);
//	}
	
	/**
	 * Contibutes actions to quick outline menu. 
	 * @param manager - menu manager.
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
     * Gets searhbox.
     */
    public Control getSearchBox()
    {
        return _searchBox;
    }

	/**
	 * textChanged
	 */
	private void textChanged()
	{
		// cancel currently running job first, to prevent unnecessary redraw
		this._filterRefreshJob.cancel();
		this._filterRefreshJob.schedule(FILTER_REFRESH_DELAY);

		// set current filter pattern
		this._pattern = this._searchBox.getText();
		this._filter.setPattern(this._pattern);

		// update all contributed outline filter patterns
		Iterator<ContributedOutline> outlineIter = _outlines.values().iterator();

		while (outlineIter.hasNext())
		{
			outlineIter.next().setFilterText(this._pattern);
		}
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
	 * Adds close listener.
	 * @param listener - listener.
	 */
	public void addCloseListener(ICloseListener listener)
	{
	    closeListeners.add(listener);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void selectionChanged(SelectionChangedEvent event)
	{
		IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		
		if (openAction != null)
		{
			_toolbarManager.remove(openAction);
			_toolbarManager.update(true);
			_toolbarManager.getControl().getParent().layout(true, true);
		}

		// If a node in the outline view is selected
		if (selection.size() == 1)
		{
			Object element = selection.getFirstElement();

			if (element instanceof IResolvableItem)
			{
				IResolvableItem item = (IResolvableItem) element;
				// if item is resolvable and is targeting on external content
				// removing all item if needed
				
				if (item.isResolvable())
				{
					

					openAction = new ActionContributionItem(new OpenExternalAction(item));
					_toolbarManager.add(openAction);
					_toolbarManager.update(true);
					_toolbarManager.getControl().getParent().layout(true, true);
					
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
		}
		else
		{
			removeOpenActionIfNeeded();
			this._editor.getViewer().removeRangeIndication();
		}
	}
	
	/**
	 * Notifies close listeners.
	 */
	private void notifyCloseListeners()
	{
	    for (ICloseListener listener : closeListeners)
	    {
	        listener.doClose();
	    }
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
	
	/**
	 * Expands a tree element, also feels parents map.
     * @param element - element to expand.
     * @param treeContentProvider - tree content provider.
     * @param elements - elements.
     */
    private void expandElement(Object element, ITreeContentProvider treeContentProvider,
    		List<Object> elements, Map<Object, List<Object>> parents)
    {
    	//getting children
        Object[] children = treeContentProvider.getChildren(element);
        
        List<Object> elementParentsList = parents.get(element);
        
        for (Object child : children)
        {
        	//adding child to the elements list
            elements.add(child);
            
            //filling parents list for the child
            ArrayList<Object> parentsList = new ArrayList<Object>();
            
            //adding list of parent's parents, if exists
            if (elementParentsList != null)
            {
            	parentsList.addAll(elementParentsList);
            }
            
            //adding parent
            parentsList.add(element);
            parents.put(child, parentsList);
        }
        
        //expanding children
        for (Object child : children)
        {
            expandElement(child, treeContentProvider, elements, parents);
        }
    }
}
