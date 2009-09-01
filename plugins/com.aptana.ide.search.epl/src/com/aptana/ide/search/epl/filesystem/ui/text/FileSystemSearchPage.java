/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Juerg Billeter, juergbi@ethz.ch - 47136 Search view should show match objects
 *     Ulrich Etter, etteru@ethz.ch - 47136 Search view should show match objects
 *     Roman Fuchs, fuchsro@ethz.ch - 47136 Search view should show match objects
 *******************************************************************************/
package com.aptana.ide.search.epl.filesystem.ui.text;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.DelegatingDragAdapter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.search.internal.ui.Messages;
import org.eclipse.search.internal.ui.SearchPlugin;
import org.eclipse.search.internal.ui.text.IFileSearchContentProvider;
import org.eclipse.search.internal.ui.text.NewTextSearchActionGroup;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.search.ui.text.Match;
import org.eclipse.search2.internal.ui.OpenSearchPreferencesAction;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.ide.search.epl.AptanaFileSystemMatch;
import com.aptana.ide.search.epl.Compatibility;

public class FileSystemSearchPage extends AbstractTextSearchViewPage implements
        IAdaptable {

    public Integer getElementLimit1() {
        if (Compatibility.isTableLimited()) {
            return new Integer(Compatibility.getTableLimit());
        }
        return new Integer(-1);
    }

    /**
     * @author Pavel Petrochenko
     */
    public static class DecoratorIgnoringViewerSorter extends ViewerComparator {

        private final ILabelProvider fLabelProvider;

        /**
         * @param labelProvider
         */
        public DecoratorIgnoringViewerSorter(ILabelProvider labelProvider) {
            fLabelProvider = labelProvider;
        }

        /**
         * @see org.eclipse.jface.viewers.ViewerComparator#category(java.lang.Object)
         */
        public int category(Object element) {
            File fl = null;
            if (element instanceof File) {
                fl = (File) element;
            } else {
                fl = ((AptanaFileSystemMatch) element).getFile();
            }

            if (fl.isDirectory()) {
                return 1;
            }
            return 2;
        }

        /**
         * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer,
         *      java.lang.Object, java.lang.Object)
         */
        public int compare(Viewer viewer, Object e1, Object e2) {
            int cat1 = category(e1);
            int cat2 = category(e2);

            if (cat1 != cat2) {
                return cat1 - cat2;
            }

            String name1 = fLabelProvider.getText(e1);
            String name2 = fLabelProvider.getText(e2);
            if (name1 == null) {
                name1 = ""; //$NON-NLS-1$
            }
            if (name2 == null) {
                name2 = ""; //$NON-NLS-1$
            }
            return getComparator().compare(name1, name2);
        }

    }

    private static final String KEY_SORTING = "org.eclipse.search.resultpage.sorting"; //$NON-NLS-1$

    private ActionGroup fActionGroup;
    private IFileSearchContentProvider fContentProvider;
    private int fCurrentSortOrder;
    private SortAction fSortByNameAction;
    private SortAction fSortByPathAction;

    private EditorOpener fEditorOpener = new EditorOpener();

    private static final String[] SHOW_IN_TARGETS = new String[] { IPageLayout.ID_RES_NAV };
    private static final IShowInTargetList SHOW_IN_TARGET_LIST = new IShowInTargetList() {
        public String[] getShowInTargetIds() {
            return FileSystemSearchPage.SHOW_IN_TARGETS;
        }
    };

    private IPropertyChangeListener fPropertyChangeListener;

    /**
	 * 
	 */
    public FileSystemSearchPage() {
        fSortByNameAction = new SortAction(
                SearchMessages.FileSearchPage_sort_name_label, this,
                FileLabelProvider.SHOW_LABEL_PATH);
        fSortByPathAction = new SortAction(
                SearchMessages.FileSearchPage_sort_path_label, this,
                FileLabelProvider.SHOW_PATH_LABEL);

        fPropertyChangeListener = new IPropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (Compatibility.ORG_ECLIPSE_SEARCH_LIMIT_TABLE.equals(event
                        .getProperty())
                        || Compatibility.ORG_ECLIPSE_SEARCH_LIMIT_TABLE_TO
                                .equals(event.getProperty())) {
                    if (getViewer() instanceof TableViewer) {
                        getViewPart().updateLabel();
                        getViewer().refresh();
                    }
                }
            }
        };
        SearchPlugin.getDefault().getPreferenceStore()
                .addPropertyChangeListener(fPropertyChangeListener);
    }

    private void addDragAdapters(StructuredViewer viewer) {
        Transfer[] transfers = new Transfer[] { ResourceTransfer.getInstance() };
        int ops = DND.DROP_COPY | DND.DROP_LINK;

        DelegatingDragAdapter adapter = new DelegatingDragAdapter();
        adapter
                .addDragSourceListener(new com.aptana.ide.search.epl.ResourceTransferDragAdapter(
                        viewer));

        viewer.addDragSupport(ops, transfers, adapter);
    }

    /**
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#getViewer()
     */
    public StructuredViewer getViewer() {
        return super.getViewer();
    }

    /**
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#configureTableViewer(org.eclipse.jface.viewers.TableViewer)
     */
    protected void configureTableViewer(TableViewer viewer) {
        viewer.setUseHashlookup(true);
        FileLabelProvider innerLabelProvider = new FileLabelProvider(this,
                fCurrentSortOrder);
        viewer.setLabelProvider(new DecoratingLabelProvider(innerLabelProvider,
                PlatformUI.getWorkbench().getDecoratorManager()
                        .getLabelDecorator()));
        viewer.setContentProvider(new FileTableContentProvider(this));
        viewer.setComparator(new DecoratorIgnoringViewerSorter(
                innerLabelProvider));
        fContentProvider = (IFileSearchContentProvider) viewer
                .getContentProvider();
        addDragAdapters(viewer);
    }

    /**
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#configureTreeViewer(org.eclipse.jface.viewers.TreeViewer)
     */
    protected void configureTreeViewer(TreeViewer viewer) {
        viewer.setUseHashlookup(true);
        FileLabelProvider innerLabelProvider = new FileLabelProvider(this,
                FileLabelProvider.SHOW_LABEL);
        viewer.setLabelProvider(new DecoratingLabelProvider(innerLabelProvider,
                PlatformUI.getWorkbench().getDecoratorManager()
                        .getLabelDecorator()));
        viewer.setContentProvider(new FileTreeContentProvider(this, viewer));
        viewer.setComparator(new DecoratorIgnoringViewerSorter(
                innerLabelProvider));
        fContentProvider = (IFileSearchContentProvider) viewer
                .getContentProvider();
        addDragAdapters(viewer);
    }

    /**
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#showMatch(org.eclipse.search.ui.text.Match,
     *      int, int, boolean)
     */
    protected void showMatch(Match match, int offset, int length,
            boolean activate) throws PartInitException {
        File file = (File) match.getElement();
        IEditorPart editor = fEditorOpener.open(file, activate);
        if ((offset != 0) || (length != 0)) {
            if (editor instanceof ITextEditor) {
                ITextEditor textEditor = (ITextEditor) editor;
                textEditor.selectAndReveal(offset, length);
            } else if (editor != null) {
                showWithMarker(editor, file, offset, length);
            }
        }
    }

    /**
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#canRemoveMatchesWith(org.eclipse.jface.viewers.ISelection)
     */
    protected boolean canRemoveMatchesWith(ISelection selection) {
        if (selection.isEmpty()) {
            StructuredViewer viewer = getViewer();
            if ((viewer != null) && (viewer instanceof TableViewer)) {
                TableViewer tv = (TableViewer) viewer;
                tv.getTable().getParent().setFocus();
                tv.getTable().deselectAll();
            }
            if ((viewer != null) && (viewer instanceof TreeViewer)) {
                TreeViewer tv = (TreeViewer) viewer;
                tv.getTree().deselectAll();
            }
        }
        return !selection.isEmpty();
    }

    private void showWithMarker(IEditorPart editor, File file, int offset,
            int length) throws PartInitException {
        IMarker marker = null;
        try {
            // marker = file.createMarker(NewSearchUI.SEARCH_MARKER);
            Map<String, Integer> attributes = new HashMap<String, Integer>(4);
            attributes.put(IMarker.CHAR_START, new Integer(offset));
            attributes.put(IMarker.CHAR_END, new Integer(offset + length));
            // marker.setAttributes(attributes);
            // IDE.gotoMarker(editor, marker);
        } finally {
            if (marker != null) {
                try {
                    marker.delete();
                } catch (CoreException e) {
                    // ignore
                }
            }
        }
    }

    /**
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#fillContextMenu(org.eclipse.jface.action.IMenuManager)
     */
    protected void fillContextMenu(IMenuManager mgr) {
        super.fillContextMenu(mgr);
        addSortActions(mgr);
        fActionGroup.setContext(new ActionContext(getSite()
                .getSelectionProvider().getSelection()));
        fActionGroup.fillContextMenu(mgr);
    }

    private void addSortActions(IMenuManager mgr) {
        if (getLayout() != AbstractTextSearchViewPage.FLAG_LAYOUT_FLAT) {
            return;
        }
        MenuManager sortMenu = new MenuManager(
                SearchMessages.FileSearchPage_sort_by_label);
        sortMenu.add(fSortByNameAction);
        sortMenu.add(fSortByPathAction);

        fSortByNameAction.setChecked(fCurrentSortOrder == fSortByNameAction
                .getSortOrder());
        fSortByPathAction.setChecked(fCurrentSortOrder == fSortByPathAction
                .getSortOrder());

        mgr.appendToGroup(IContextMenuConstants.GROUP_VIEWER_SETUP, sortMenu);
    }

    /**
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#setInput(org.eclipse.search.ui.ISearchResult,
     *      java.lang.Object)
     */
    public void setInput(ISearchResult newSearch, Object viewState) {
        super.setInput(newSearch, viewState);
        getViewer().setSelection(new StructuredSelection());
    }

    /**
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#setViewPart(org.eclipse.search.ui.ISearchResultViewPart)
     */
    public void setViewPart(ISearchResultViewPart part) {
        super.setViewPart(part);
        fActionGroup = new NewTextSearchActionGroup(part);
    }

    /**
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#init(org.eclipse.ui.part.IPageSite)
     */
    public void init(IPageSite site) {
        super.init(site);
        IMenuManager menuManager = site.getActionBars().getMenuManager();
        menuManager.appendToGroup(IContextMenuConstants.GROUP_PROPERTIES,
                new OpenSearchPreferencesAction());
    }

    /**
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#dispose()
     */
    public void dispose() {
        fActionGroup.dispose();
        SearchPlugin.getDefault().getPreferenceStore()
                .removePropertyChangeListener(fPropertyChangeListener);
        super.dispose();
    }

    /**
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#elementsChanged(java.lang.Object[])
     */
    protected void elementsChanged(Object[] objects) {
        if (fContentProvider != null) {
            fContentProvider.elementsChanged(objects);
        }
    }

    /**
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#clear()
     */
    protected void clear() {
        if (fContentProvider != null) {
            fContentProvider.clear();
        }
    }

    /**
     * @param sortOrder
     */
    public void setSortOrder(int sortOrder) {
        fCurrentSortOrder = sortOrder;
        DecoratingLabelProvider lpWrapper = (DecoratingLabelProvider) getViewer()
                .getLabelProvider();
        ((FileLabelProvider) lpWrapper.getLabelProvider()).setOrder(sortOrder);
        getViewer().refresh();
        getSettings().put(FileSystemSearchPage.KEY_SORTING, fCurrentSortOrder);
    }

    /**
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#restoreState(org.eclipse.ui.IMemento)
     */
    public void restoreState(IMemento memento) {
        super.restoreState(memento);
        try {
            fCurrentSortOrder = getSettings().getInt(
                    FileSystemSearchPage.KEY_SORTING);
        } catch (NumberFormatException e) {
            fCurrentSortOrder = fSortByNameAction.getSortOrder();
        }
        if (memento != null) {
            Integer value = memento
                    .getInteger(FileSystemSearchPage.KEY_SORTING);
            if (value != null) {
                fCurrentSortOrder = value.intValue();
            }
        }
    }

    /**
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#saveState(org.eclipse.ui.IMemento)
     */
    public void saveState(IMemento memento) {
        super.saveState(memento);
        memento.putInteger(FileSystemSearchPage.KEY_SORTING, fCurrentSortOrder);
    }

    /**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter(Class adapter) {
        if (IShowInTargetList.class.equals(adapter)) {
            return FileSystemSearchPage.SHOW_IN_TARGET_LIST;
        }
        return null;
    }

    /**
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#getLabel()
     */
    public String getLabel() {
        String label = super.getLabel();
        StructuredViewer viewer = getViewer();
        if (viewer instanceof TableViewer) {
            TableViewer tv = (TableViewer) viewer;

            AbstractTextSearchResult result = getInput();
            if (result != null) {
                int itemCount = ((IStructuredContentProvider) tv
                        .getContentProvider()).getElements(getInput()).length;
                int fileCount = getInput().getElements().length;
                if (itemCount < fileCount) {
                    return Messages
                            .format(
                                    SearchMessages.AptanaFileSearchPage_NOT_ALL_MATCHES,
                                    new Object[] { label,
                                            new Integer(itemCount),
                                            new Integer(fileCount) });
                }
            }
        }
        return label;
    }

}
