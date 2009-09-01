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
package com.aptana.ide.search.ui.filesystem;

import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.SubContributionItem;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.search.internal.ui.Messages;
import org.eclipse.search.internal.ui.SearchMessages;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.search.ui.SearchResultEvent;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.Match;
import org.eclipse.search.ui.text.MatchEvent;
import org.eclipse.search.ui.text.RemoveAllEvent;
import org.eclipse.search2.internal.ui.basic.views.SetLayoutAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.IPageSite;

import com.aptana.ide.core.ui.PixelConverter;
import com.aptana.ide.core.ui.PreferenceUtils;
import com.aptana.ide.search.epl.Activator;
import com.aptana.ide.search.epl.AptanaFileSystemMatch;
import com.aptana.ide.search.epl.FileSystemSearchResult;
import com.aptana.ide.search.epl.filesystem.ui.text.FileSystemReplaceAction;
import com.aptana.ide.search.epl.filesystem.ui.text.FileSystemSearchPage;

/**
 * custom search page with extra layout for showing matches in list
 * 
 * @author Pavel Petrochenko
 */
public class AptanaFileSystemSearchPage extends FileSystemSearchPage {

    /**
     * SHOW_LABEL
     */
    public static final int SHOW_LABEL = 1;
    /**
     * SHOW_LABEL_PATH
     */
    public static final int SHOW_LABEL_PATH = 2;
    /**
     * SHOW_PATH_LABEL
     */
    public static final int SHOW_PATH_LABEL = 3;

    /**
     * @author Pavel Petrochenko
     */
    private final class MatchesTableLabelProvider implements
            ITableLabelProvider, ILabelProvider {
        private WorkbenchLabelProvider fLabelProvider = new WorkbenchLabelProvider();
        private String[] fArgs = new String[2];
        private static final String fgSeparatorFormat = "{0} - {1}"; //$NON-NLS-1$

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object,
         *      int)
         */
        public Image getColumnImage(Object element, int columnIndex) {
            if (columnIndex == 0) {
                return AptanaFileSystemSearchPage.this.baseLabelProvider
                        .getImage(((Match) element).getElement());
            }
            return null;
        }

        /**
         * @param resource
         * @return
         */
        private String getText(File resource) {
            String text;

            if (resource.exists()) {
                IPath path = new Path(resource.getAbsolutePath())
                        .removeLastSegments(1);
                if (path.getDevice() == null) {
                    path = path.makeRelative();
                }
                if ((fOrder == AptanaFileSystemSearchPage.SHOW_LABEL)
                        || (AptanaFileSystemSearchPage.this.fOrder == AptanaFileSystemSearchPage.SHOW_LABEL_PATH)) {
                    text = fLabelProvider.getText(resource);
                    if ((path != null)
                            && (fOrder == AptanaFileSystemSearchPage.SHOW_LABEL_PATH)) {
                        fArgs[0] = resource.getName();
                        fArgs[1] = path.toString();
                        text = MessageFormat.format(
                                MatchesTableLabelProvider.fgSeparatorFormat,
                                (Object[]) fArgs);
                    }
                } else {
                    if (path != null) {
                        text = path.toString();
                    } else {
                        text = ""; //$NON-NLS-1$
                    }
                    if (fOrder == AptanaFileSystemSearchPage.SHOW_PATH_LABEL) {
                        fArgs[0] = text;
                        fArgs[1] = resource.getName();
                        text = MessageFormat.format(
                                MatchesTableLabelProvider.fgSeparatorFormat,
                                (Object[]) fArgs);
                    }
                }
            } else {
                text = SearchMessages.FileLabelProvider_removed_resource_label;
            }
            return text;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
         *      int)
         */
        public String getColumnText(Object element, int columnIndex) {
            // workaround
            if (element instanceof File) {
                return ""; //$NON-NLS-1$
            }
            AptanaFileSystemMatch match = (AptanaFileSystemMatch) (element);
            if (columnIndex == 0) {
                return getText(match.getFile());
            }
            if (columnIndex == 1) {
                return Integer.toString(match.getLineNumber());
            }
            if (columnIndex == 2) {
                return ""; //$NON-NLS-1$
            }
            return null;
        }

        /**
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
         */
        public void addListener(ILabelProviderListener listener) {
            baseLabelProvider.addListener(listener);
        }

        /**
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
         */
        public void dispose() {
        }

        /**
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
         *      java.lang.String)
         */
        public boolean isLabelProperty(Object element, String property) {
            return baseLabelProvider.isLabelProperty(element, property);
        }

        /**
         * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
         */
        public void removeListener(ILabelProviderListener listener) {
            baseLabelProvider.removeListener(listener);
        }

        /**
         * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
         */
        public Image getImage(Object element) {
            return null;
        }

        /**
         * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
         */
        public String getText(Object element) {
            StringBuilder bld = new StringBuilder();
            bld.append(this.getColumnText(element, 0));
            bld.append('\t');
            bld.append(this.getColumnText(element, 1));
            bld.append('\t');
            bld.append(((AptanaFileSystemMatch) element).getLineContent());
            return bld.toString();
        }
    }

    static final Object[] NO_ELEMENTS = new Object[0];
    static final Match[] NO_MATCH = new Match[0];

    private static final int LAYOUT_MATCHES = 4;

    ITreeContentProvider provider;

    boolean turnOff;

    private int layout;
    private static final String KEY_LAYOUT = "org.eclipse.search.resultpage.layout"; //$NON-NLS-1$
    private static final String KEY_SORTING = "org.eclipse.search.resultpage.sorting"; //$NON-NLS-1$

    private MatchesContentProvider structuredContentProvider;
    private Listener listener;
    private Listener listener1;

    private int fOrder = AptanaFileSystemSearchPage.SHOW_LABEL_PATH;

    private SetLayoutAction fFlatAction = new SetLayoutAction(
            this,
            com.aptana.ide.search.Messages.AptanaFileSearchPage_ShowMatches,
            com.aptana.ide.search.Messages.AptanaFileSearchPage_ShowMatchesTooltip,
            AptanaFileSystemSearchPage.LAYOUT_MATCHES);

    private PixelConverter metrics;
    private LabelProvider baseLabelProvider;

    /**
     * 
     */
    public AptanaFileSystemSearchPage() {
        fFlatAction.setImageDescriptor(Activator.imageDescriptorFromPlugin(
                Activator.PLUGIN_ID, "/icons/verticalOrientation.gif")); //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.search.internal.ui.text.FileSearchPage#fillContextMenu(org.eclipse.jface.action.IMenuManager)
     */
    protected void fillContextMenu(IMenuManager mgr) {
        super.fillContextMenu(mgr);

        // should be here if our layout is active
        IStructuredSelection selection = (IStructuredSelection) getViewer()
                .getSelection();
        if (!selection.isEmpty()) {
            FileSystemReplaceAction replaceSelection = new FileSystemReplaceAction(
                    getSite().getShell(), (FileSystemSearchResult) getInput(),
                    selection.toArray(), true);
            replaceSelection
                    .setText(SearchMessages.ReplaceAction_label_selected);
            mgr.appendToGroup(IContextMenuConstants.GROUP_REORGANIZE,
                    replaceSelection);
        }
        FileSystemReplaceAction replaceAll = new FileSystemReplaceAction(
                getSite().getShell(), (FileSystemSearchResult) getInput(),
                null, true);
        replaceAll.setText(SearchMessages.ReplaceAction_label_all);
        mgr.appendToGroup(IContextMenuConstants.GROUP_REORGANIZE, replaceAll);

        if (this.layout == AptanaFileSystemSearchPage.LAYOUT_MATCHES) {
            Separator find = (Separator) mgr
                    .find(IContextMenuConstants.GROUP_VIEWER_SETUP);
            IContributionItem[] items = find.getParent().getItems();
            int indexOf = Arrays.asList(items).indexOf(find);
            MenuManager contributionItem = (MenuManager) items[indexOf + 1];
            IContributionItem[] items2 = contributionItem.getItems();
            ActionContributionItem it0 = (ActionContributionItem) items2[0];
            ActionContributionItem it1 = (ActionContributionItem) items2[1];
            it0.getAction().setChecked(
                    fOrder == AptanaFileSystemSearchPage.SHOW_LABEL_PATH);
            it1.getAction().setChecked(
                    fOrder == AptanaFileSystemSearchPage.SHOW_PATH_LABEL);
        }
    }

    /**
     * @see org.eclipse.search.internal.ui.text.FileSearchPage#getLabel()
     */
    public String getLabel() {
        if (this.layout == LAYOUT_MATCHES) {
            AbstractTextSearchResult input = getInput();
            if (input != null) {
                String label = input.getLabel();
                TableViewer viewer = (TableViewer) getViewer();
                int itemCount = viewer.getTable().getItemCount();
                int matchCount = input.getMatchCount();
                if (itemCount < matchCount) {
                    return Messages
                            .format(
                                    com.aptana.ide.search.Messages.AptanaFileSearchPage_NOT_ALL_MATCHES,
                                    new Object[] { label,
                                            new Integer(itemCount),
                                            new Integer(matchCount) });
                }
            } else {
                return ""; //$NON-NLS-1$
            }
        }
        return super.getLabel();
    }

    /**
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void init(IPageSite site) {
        super.init(site);
        site.getActionBars().getMenuManager().appendToGroup(
                IContextMenuConstants.GROUP_VIEWER_SETUP, fFlatAction);
    }

    /**
     * @see org.eclipse.search.internal.ui.text.FileSearchPage#setSortOrder(int)
     */
    public void setSortOrder(int sortOrder) {
        if (this.layout == LAYOUT_MATCHES) {
            fOrder = sortOrder;
            getSettings().put(KEY_SORTING, sortOrder);
            getViewer().refresh();
        } else {
            super.setSortOrder(sortOrder);
        }
    }

    /**
     * @see org.eclipse.search.internal.ui.text.FileSearchPage#restoreState(org.eclipse.ui.IMemento)
     */
    public void restoreState(IMemento memento) {
        try {
            this.layout = getSettings().getInt(KEY_LAYOUT);
        } catch (NumberFormatException e) {
            this.layout = FLAG_LAYOUT_TREE;
        }
        if (this.layout == LAYOUT_MATCHES) {
            getSettings().put(KEY_LAYOUT, FLAG_LAYOUT_FLAT);
        }
        if (memento != null) {
            Integer l = memento.getInteger(KEY_LAYOUT);
            if (l != null) {
                this.layout = l.intValue();
                if (this.layout == LAYOUT_MATCHES) {
                    memento.putInteger(KEY_LAYOUT, FLAG_LAYOUT_FLAT);
                }
            }
        }
        super.restoreState(memento);
        try {
            fOrder = getSettings().getInt(KEY_SORTING);
        } catch (NumberFormatException e) {
            fOrder = SHOW_LABEL_PATH;
        }
        if (memento != null) {
            Integer value = memento.getInteger(KEY_SORTING);
            if (value != null) {
                fOrder = value.intValue();
            }
        }
    }

    /**
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        super.createControl(parent);
        if (this.layout == LAYOUT_MATCHES) {
            this.layout = 1;
            this.setLayout(LAYOUT_MATCHES);
        }
    }

    /**
     * @see org.eclipse.search.internal.ui.text.FileSearchPage#saveState(org.eclipse.ui.IMemento)
     */
    public void saveState(IMemento memento) {
        super.saveState(memento);
        memento.putInteger(KEY_LAYOUT, this.layout);
    }

    /**
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#setLayout(int)
     */
    public void setLayout(int layout) {
        if (this.layout == layout) {
            return;
        }
        if (layout == LAYOUT_MATCHES) {
            getControl().setRedraw(false);

            if (getLayout() != FLAG_LAYOUT_FLAT) {
                super.setLayout(FLAG_LAYOUT_FLAT);
            }
            this.layout = layout;
            reconfigureTableViewer();
            getControl().setRedraw(true);
            ((Composite) getControl()).layout(true, true);
            getControl().redraw();
            Separator find = (Separator) getSite().getActionBars()
                    .getMenuManager().find(
                            IContextMenuConstants.GROUP_VIEWER_SETUP);
            IContributionItem[] items = find.getParent().getItems();
            int indexOf = Arrays.asList(items).indexOf(find);
            SubContributionItem contributionItem = (SubContributionItem) items[indexOf + 1];
            ActionContributionItem ac = (ActionContributionItem) contributionItem
                    .getInnerItem();
            ac.getAction().setChecked(false);
            contributionItem = (SubContributionItem) items[indexOf + 3];
            ac = (ActionContributionItem) contributionItem.getInnerItem();
            ac.getAction().setChecked(true);
            getSettings().put(KEY_LAYOUT, layout);
        } else {

            if (this.layout == LAYOUT_MATCHES) {
                this.layout = layout;
                if (layout == FLAG_LAYOUT_FLAT) {
                    getControl().setRedraw(false);
                    super.setLayout(FLAG_LAYOUT_TREE);
                    super.setLayout(FLAG_LAYOUT_FLAT);
                    ((Composite) this.getControl()).layout(true, true);
                    getControl().setRedraw(true);
                    return;
                }
            }
            this.layout = layout;
            super.setLayout(layout);
        }
    }

    /**
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#getCurrentMatch()
     */
    public Match getCurrentMatch() {
        if (this.layout == LAYOUT_MATCHES) {
            // hack for returning correct results for next and previous matches
            StackTraceElement[] stackTrace = new Exception().getStackTrace();
            String methodName = stackTrace[1].getMethodName();
            if (methodName.equals("gotoNextMatch")) //$NON-NLS-1$
            {
                return null;
            }
            if (methodName.equals("gotoPreviousMatch")) //$NON-NLS-1$
            {
                return null;
            }

            IStructuredSelection selection = (IStructuredSelection) this
                    .getViewer().getSelection();
            Object element = selection.getFirstElement();
            return (Match) element;
        }
        return super.getCurrentMatch();
    }

    /**
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#internalRemoveSelected()
     */
    public void internalRemoveSelected() {
        if (this.layout == LAYOUT_MATCHES) {
            TableViewer cc = (TableViewer) this.getViewer();
            StructuredSelection ss = (StructuredSelection) cc.getSelection();
            List list = ss.toList();
            Match[] ms = new Match[list.size()];
            list.toArray(ms);
            this.getInput().removeMatches(ms);
            getViewPart().updateLabel();
        } else {
            super.internalRemoveSelected();
        }
    }

    /**
     * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#handleSearchResultChanged(org.eclipse.search.ui.SearchResultEvent)
     */
    protected void handleSearchResultChanged(final SearchResultEvent e) {
        if (this.layout != LAYOUT_MATCHES) {
            super.handleSearchResultChanged(e);
            return;
        }
        if (e instanceof MatchEvent) {
            MatchEvent me = (MatchEvent) e;
            int kind = me.getKind();
            if ((Display.getCurrent() != null) && (kind == MatchEvent.REMOVED)) {
                TableViewer cc = (TableViewer) getViewer();
                Table table = cc.getTable();
                int selectionIndex = table.getSelectionIndex();
                cc.refresh();
                if (selectionIndex != -1) {
                    if (selectionIndex < table.getItemCount()) {
                        TableItem item = cc.getTable().getItem(selectionIndex);
                        cc
                                .setSelection(new StructuredSelection(item
                                        .getData()));
                    }
                }
            } else {
                super.handleSearchResultChanged(e);
            }
        } else if (e instanceof RemoveAllEvent) {
            super.handleSearchResultChanged(e);
        }
    }

    private void reconfigureTableViewer() {
        TableViewer tViewer = (TableViewer) getViewer();
        TableColumn[] columns = tViewer.getTable().getColumns();
        for (int a = 0; a < columns.length; a++) {
            columns[a].dispose();
        }
        metrics = new PixelConverter(tViewer.getControl());
        tViewer.getTable().setHeaderVisible(true);
        TableColumn clmn = new TableColumn(tViewer.getTable(), SWT.LEFT);
        clmn.setText(com.aptana.ide.search.Messages.AptanaFileSearchPage_File);
        TableColumn clmn1 = new TableColumn(tViewer.getTable(), SWT.LEFT);
        clmn1.setText(com.aptana.ide.search.Messages.AptanaFileSearchPage_Line);
        TableColumn clmn2 = new TableColumn(tViewer.getTable(), SWT.LEFT);
        clmn2
                .setText(com.aptana.ide.search.Messages.AptanaFileSearchPage_Source);

        TableLayout tableLayout = new TableLayout();
        tableLayout.addColumnData(new ColumnWeightData(1));
        tableLayout.addColumnData(new ColumnPixelData(50));
        tableLayout.addColumnData(new ColumnWeightData(1));
        PreferenceUtils.persist(Activator.getDefault().getPreferenceStore(),
                tViewer.getTable(), "tviewer"); //$NON-NLS-1$
        this.listener = new Listener() {

            public void handleEvent(Event event) {
                if (event.index == 2) {
                    AptanaFileSystemMatch aptanaFileMatch = ((AptanaFileSystemMatch) event.item
                            .getData());
                    if (aptanaFileMatch != null) {
                        event.gc.drawText(aptanaFileMatch.getLineContent(),
                                event.x, event.y);
                    }
                }
            }

        };
        this.listener1 = new Listener() {

            public void handleEvent(Event event) {

                if (event.index == 2) {
                    AptanaFileSystemMatch aptanaFileMatch = ((AptanaFileSystemMatch) event.item
                            .getData());
                    if (aptanaFileMatch != null) {
                        event.width = AptanaFileSystemSearchPage.this.metrics
                                .convertWidthInCharsToPixels(aptanaFileMatch
                                        .getLineContent().length());
                    }
                }
            }

        };
        tViewer.getTable().addListener(SWT.PaintItem, this.listener);
        tViewer.getTable().setLayout(tableLayout);
        this.baseLabelProvider = (LabelProvider) tViewer.getLabelProvider();
        tViewer.setInput(null);
        tViewer.setLabelProvider(new MatchesTableLabelProvider());
        final IContentProvider contentProvider = tViewer.getContentProvider();
        this.structuredContentProvider = new MatchesContentProvider(this,
                contentProvider);
        tViewer.setContentProvider(this.structuredContentProvider);
        tViewer.setInput(this.getInput());
        tViewer.getTable().getParent().layout(true);
    }

    /**
     * @see org.eclipse.search.internal.ui.text.FileSearchPage#clear()
     */
    protected void clear() {
        StructuredViewer viewer = getViewer();
        if (this.layout == LAYOUT_MATCHES) {
            viewer.getControl().setRedraw(false);
            viewer.getControl().removeListener(SWT.PaintItem, this.listener);
            viewer.getControl().removeListener(SWT.MeasureItem, this.listener1);
            super.clear();
            viewer.getControl().setRedraw(true);
            viewer.getControl().addListener(SWT.PaintItem, this.listener);
            viewer.getControl().addListener(SWT.MeasureItem, this.listener1);
        } else {
            super.clear();
        }
    }

    /**
     * @see org.eclipse.search.internal.ui.text.FileSearchPage#elementsChanged(java.lang.Object[])
     */
    protected void elementsChanged(Object[] objects) {
        if (this.layout == LAYOUT_MATCHES) {
            this.structuredContentProvider.elementsChanged(objects);
        } else {
            super.elementsChanged(objects);
        }
    }

    /**
     * @see org.eclipse.search.internal.ui.text.FileSearchPage#configureTreeViewer(org.eclipse.jface.viewers.TreeViewer)
     */
    protected void configureTreeViewer(TreeViewer viewer) {
        super.configureTreeViewer(viewer);

        final DecoratingLabelProvider labelProvider = (DecoratingLabelProvider) viewer
                .getLabelProvider();
        DecoratingLabelProvider decoratingLabelProvider = new DecoratingLabelProvider(
                labelProvider, new AptanaExtraDecorator(this)) {

            public void setLabelDecorator(ILabelDecorator decorator) {
                if (decorator != null) {
                    AptanaFileSystemSearchPage.this.turnOff = true;
                }
                labelProvider.setLabelDecorator(decorator);
                AptanaFileSystemSearchPage.this.turnOff = false;
            }

        };
        this.provider = (ITreeContentProvider) viewer.getContentProvider();
        viewer.setLabelProvider(decoratingLabelProvider);
    }
}
