/**
 * Copyright (c) 2005-2009 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.search.epl;

import java.lang.reflect.Method;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.search.internal.ui.SearchMessages;
import org.eclipse.search.internal.ui.SearchPlugin;
import org.eclipse.search.internal.ui.text.FileSearchPage;
import org.eclipse.search.internal.ui.text.FileSearchResult;
import org.eclipse.search.internal.ui.text.ReplaceAction;
import org.eclipse.search.internal.ui.text.TextSearchPage;
import org.eclipse.search.internal.ui.util.SWTUtil;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.search.ui.text.TextSearchQueryProvider.TextSearchInput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.search.epl.filesystem.ui.text.FileSystemReplaceAction;
import com.aptana.ide.search.epl.filesystem.ui.text.FileSystemSearchPage;

/**
 * @author Pavel Petrochenko
 */
public class AptanaTextSearchPage extends TextSearchPage {

    /**
     * EXTENSION_POINT_ID
     */
    public static final String EXTENSION_POINT_ID = "com.aptana.ide.search.epl.internal.ui.text.TextSearchPage"; //$NON-NLS-1$

    /**
     * Selection listener for scope controls.
     * 
     * @author Denis Denisenko
     * 
     */
    private class ScopeSelectionListener implements SelectionListener {
        /**
         * {@inheritDoc}
         */
        public void widgetDefaultSelected(SelectionEvent e) {
        }

        /**
         * {@inheritDoc}
         */
        public void widgetSelected(SelectionEvent e) {
            if (!AptanaTextSearchPage.this.firstOpen) {
                return;
            }

            Widget widget = e.widget;
            if (widget != null) {
                Object data = widget.getData();
                if (data != null && data instanceof Integer) {
                    scope = ((Integer) data).intValue();
                }
            }
        }
    }

    /**
     * Directory scope.
     */
    private static final int DIRECTORY_SCOPE = 12;

    /**
     * Open files scope.
     */
    private static final int OPEN_FILES_SCOPE = 13;

    /**
     * Scope selection listener.
     */
    private ScopeSelectionListener scopeSelectionListener = new ScopeSelectionListener();

    // Dialog store id constants
    private static final String PAGE_NAME = "TextSearchPage"; //$NON-NLS-1$
    private static final String STORE_SEARCH_DIRECTORY = "SEARCH_DIRECTORY"; //$NON-NLS-1$

    /**
     * Search scope configuration constant.
     */
    private static final String SEARCH_SCOPE = "SEARCH_SCOPE"; //$NON-NLS-1$

    private ISearchPageContainer fContainer;
    private Text fDirectory;
    private Button fSearchInDirectory;
    private Button fSearchInOpenFiles;

    private String fSearchDirectory;

    /**
     * Search scope.
     */
    private int scope;

    private boolean firstOpen;

    public static class TextSearchPageInput extends TextSearchInput {

        private final String fSearchText;
        private final boolean fIsCaseSensitive;
        private final boolean fIsRegEx;
        private final FileTextSearchScope fScope;
        private final boolean isDirectory;
        private final String directory;
        private final boolean isOpenFiles;
        private boolean fIsIgnoreLineEndings;
        public boolean doRefresh;

        /**
         * @return
         */
        public boolean isDirectory() {
            return this.isDirectory;
        }

        /**
         * @return
         */
        public String getDirectory() {
            return this.directory;
        }

        /**
         * @return should search ignore line endings
         */
        public boolean isIgnoreLineEndings() {
            return fIsIgnoreLineEndings;
        }

        /**
         * @return
         */
        public boolean isOpenFiles() {
            return this.isOpenFiles;
        }

        /**
         * @param searchText
         * @param isCaseSensitive
         * @param isRegEx
         * @param scope
         * @param isDir
         * @param directory
         * @param openFiles
         * @param isIgnoreLineEndings
         */
        public TextSearchPageInput(String searchText, boolean isCaseSensitive,
                boolean isRegEx, FileTextSearchScope scope, boolean isDir,
                String directory, boolean openFiles, boolean isIgnoreLineEndings) {
            this.fSearchText = searchText;
            this.isDirectory = isDir;
            this.directory = directory;
            this.isOpenFiles = openFiles;
            this.fIsCaseSensitive = isCaseSensitive;
            this.fIsRegEx = isRegEx;
            this.fScope = scope;
            this.fIsIgnoreLineEndings = isIgnoreLineEndings;
        }

        /**
         * @see org.eclipse.search.ui.text.TextSearchQueryProvider.TextSearchInput#getSearchText()
         */
        public String getSearchText() {
            return this.fSearchText;
        }

        /**
         * @see org.eclipse.search.ui.text.TextSearchQueryProvider.TextSearchInput#isCaseSensitiveSearch()
         */
        public boolean isCaseSensitiveSearch() {
            return this.fIsCaseSensitive;
        }

        /**
         * @see org.eclipse.search.ui.text.TextSearchQueryProvider.TextSearchInput#isRegExSearch()
         */
        public boolean isRegExSearch() {
            return this.fIsRegEx;
        }

        /**
         * @see org.eclipse.search.ui.text.TextSearchQueryProvider.TextSearchInput#getScope()
         */
        public FileTextSearchScope getScope() {
            return this.fScope;
        }

        public boolean isRefresh() {
            return doRefresh;
        }
    }

    private ISearchQuery newTextQuery() throws CoreException {
        Method method;
        try {
            method = TextSearchPage.class.getDeclaredMethod(
                    "newQuery", (Class[]) null); //$NON-NLS-1$
            method.setAccessible(true);
            org.eclipse.search.internal.ui.text.FileSearchQuery query = (org.eclipse.search.internal.ui.text.FileSearchQuery) method
                    .invoke(this, (Object[]) null);

            TextSearchPageInput input = new TextSearchPageInput(query
                    .getSearchString(), query.isCaseSensitive(), query
                    .isRegexSearch(), query.getSearchScope(),
                    isSearchingInDirectory(), getDirectoryText(),
                    isSearchingInOpenFiles(), false);
            return (new AptanaTextSearchQueryProvider()).createQuery(input);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * @see org.eclipse.search.internal.ui.text.TextSearchPage#performAction()
     */
    public boolean performAction() {
        try {
            NewSearchUI.runQueryInBackground(newTextQuery());
        } catch (CoreException e) {
            ErrorDialog
                    .openError(
                            this.getShell(),
                            SearchMessages.TextSearchPage_replace_searchproblems_title,
                            SearchMessages.TextSearchPage_replace_searchproblems_message,
                            e.getStatus());
            return false;
        }
        return true;
    }

    /**
     * @see org.eclipse.search.internal.ui.text.TextSearchPage#performReplace()
     */
    public boolean performReplace() {
        try {
            IStatus status = NewSearchUI.runQueryInForeground(getContainer()
                    .getRunnableContext(), newTextQuery());
            if (status.matches(IStatus.CANCEL)) {
                return false;
            }
            if (!status.isOK()) {
                ErrorDialog
                        .openError(
                                getShell(),
                                SearchMessages.TextSearchPage_replace_searchproblems_title,
                                SearchMessages.TextSearchPage_replace_runproblem_message,
                                status);
            }

            Display.getCurrent().asyncExec(new Runnable() {
                public void run() {
                    ISearchResultViewPart view = NewSearchUI
                            .activateSearchResultView();
                    if (view != null) {
                        ISearchResultPage page = view.getActivePage();
                        if (page instanceof FileSearchPage) {
                            FileSearchPage filePage = (FileSearchPage) page;
                            new ReplaceAction(filePage.getSite().getShell(),
                                    (FileSearchResult) filePage.getInput(),
                                    null, true).run();
                        } else if (page instanceof FileSystemSearchPage) {
                            FileSystemSearchPage filePage = (FileSystemSearchPage) page;
                            new FileSystemReplaceAction(filePage.getSite()
                                    .getShell(),
                                    (FileSystemSearchResult) filePage
                                            .getInput(), null, true).run();
                        }
                    }
                }
            });
            return true;
        } catch (CoreException e) {
            ErrorDialog
                    .openError(
                            getShell(),
                            SearchMessages.TextSearchPage_replace_searchproblems_title,
                            SearchMessages.TextSearchPage_replace_querycreationproblem_message,
                            e.getStatus());
            return false;
        }
    }

    private boolean isSearchingInOpenFiles() {
        if (fSearchInOpenFiles == null) {
            IdeLog.logError(Activator.getDefault(),
                    "Search in open files is null"); //$NON-NLS-1$
            return false;
        }
        return fSearchInOpenFiles.getSelection();
    }

    private String getDirectoryText() {
        return fDirectory.getText();
    }

    private boolean isSearchingInDirectory() {
        if (fSearchInDirectory == null) {
            IdeLog.logError(Activator.getDefault(),
                    "Search in directory is null"); //$NON-NLS-1$
            return false;
        }
        return this.fSearchInDirectory.getSelection();
    }

    /**
     * @see org.eclipse.search.internal.ui.text.TextSearchPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(final Composite parent) {
        super.createControl(parent);

        readConfiguration();

        final Composite result = (Composite) parent.getChildren()[0];
        result.setLayoutData(new GridData(GridData.FILL_BOTH));

        parent.addControlListener(new ControlListener() {

            public void controlMoved(ControlEvent e) {

            }

            public void controlResized(ControlEvent e) {
                modifyScope(parent, result);
            }

        });

        Point computeSize = parent.getShell().computeSize(-1, -1);
        Point size = parent.getShell().getSize();
        parent.getShell().setMinimumSize(computeSize);
        if (size.x < computeSize.x || size.y < computeSize.y) {
            parent.getShell().setSize(computeSize.x, computeSize.y);
        }
        parent.getDisplay().asyncExec(new Runnable() {

            public void run() {
                modifyScope(parent, result);
                parent.getShell().layout(true, true);
            }

        });
    }

    /**
     * @see org.eclipse.search.internal.ui.text.TextSearchPage#setContainer(org.eclipse.search.ui.ISearchPageContainer)
     */
    public void setContainer(ISearchPageContainer container) {
        fContainer = container;
        super.setContainer(container);
    }

    private ISearchPageContainer getContainer() {
        return fContainer;
    }

    /**
     * @see org.eclipse.jface.dialogs.DialogPage#dispose()
     */
    public void dispose() {
        writeConfiguration();
        super.dispose();
    }

    /**
     * Returns the page settings for this Text search page.
     * 
     * @return the page settings to be used
     */
    private IDialogSettings getDialogSettings() {
        return SearchPlugin.getDefault().getDialogSettingsSection(
                AptanaTextSearchPage.PAGE_NAME);
    }

    /**
     * Initializes itself from the stored page settings.
     */
    private void readConfiguration() {
        IDialogSettings s = getDialogSettings();
        try {
            this.scope = s.getInt(SEARCH_SCOPE);
        } catch (Exception ex) {
            // setting default scope.
            this.scope = ISearchPageContainer.WORKSPACE_SCOPE;
        }

        this.fSearchDirectory = s
                .get(AptanaTextSearchPage.STORE_SEARCH_DIRECTORY);
        if (this.fSearchDirectory == null) {
            this.fSearchDirectory = ""; //$NON-NLS-1$
        }
    }

    /**
     * Stores it current configuration in the dialog store.
     */
    private void writeConfiguration() {
        IDialogSettings s = getDialogSettings();
        s.put(AptanaTextSearchPage.STORE_SEARCH_DIRECTORY,
                this.fSearchDirectory);
        s.put(SEARCH_SCOPE, scope);
    }

    private void modifyScope(final Composite parent, final Composite result) {
        try {
            IdeLog.logInfo(Activator.getDefault(), StringUtils.format(
                    "Accessing children of ", //$NON-NLS-1$
                    new Object[] { parent.toString() }));
            Control[] children = parent.getChildren();
            IdeLog.logInfo(Activator.getDefault(), StringUtils.format(
                    "children taken ", new Object[] { parent.toString() })); //$NON-NLS-1$
            try {
                for (int a = 0; a < children.length; a++) {
                    if (children[a] != result && !firstOpen) {
                        IdeLog.logInfo(Activator.getDefault(),
                                "Search in directory UI is creating"); //$NON-NLS-1$
                        firstOpen = true;

                        children = ((Composite) children[a]).getChildren();
                        final Group scopeGroup = (Group) children[0];
                        Control[] children2 = scopeGroup.getChildren();
                        for (Control control2 : children2) {
                            if (control2 instanceof Button) {
                                Button m = (Button) control2;
                                if ((m.getStyle() & SWT.RADIO) != 0) {
                                    m
                                            .addSelectionListener(scopeSelectionListener);
                                }
                                if ((m.getStyle() & SWT.PUSH) != 0) {
                                    GridData gridData = new GridData();
                                    m.setLayoutData(gridData);
                                    gridData.widthHint = SWTUtil
                                            .getButtonWidthHint(m);
                                    m.setLayoutData(gridData);
                                }
                            }
                        }

                        fSearchInDirectory = new Button(scopeGroup, SWT.RADIO);
                        fSearchInDirectory.setText(Messages.DIRECTORY);
                        fSearchInDirectory
                                .addSelectionListener(scopeSelectionListener);
                        fSearchInDirectory
                                .setData(new Integer(DIRECTORY_SCOPE));
                        IdeLog.logInfo(Activator.getDefault(),
                                "Search in directory UI is nearly created"); //$NON-NLS-1$

                        fDirectory = new Text(scopeGroup, SWT.READ_ONLY
                                | SWT.BORDER);
                        GridData gridData = new GridData(
                                GridData.FILL_HORIZONTAL);
                        gridData.horizontalIndent = 8;
                        gridData.horizontalSpan = 2;
                        fDirectory.setLayoutData(gridData);
                        fDirectory.setText(fSearchDirectory);
                        IdeLog.logInfo(Activator.getDefault(),
                                "Search in directory was created"); //$NON-NLS-1$

                        Button choose = new Button(scopeGroup, SWT.PUSH);
                        choose.setText(Messages.CHOOSE);

                        choose.addSelectionListener(new SelectionListener() {

                            public void widgetDefaultSelected(SelectionEvent e) {
                            }

                            public void widgetSelected(SelectionEvent e) {
                                DirectoryDialog ddialog = new DirectoryDialog(
                                        fSearchInDirectory.getShell(), SWT.NONE);

                                String open = ddialog.open();
                                if (open != null) {
                                    fSearchDirectory = open;
                                    writeConfiguration();
                                    fDirectory.setText(open);
                                    if (fSearchInDirectory.getSelection()) {
                                        getContainer()
                                                .setPerformActionEnabled(
                                                        fDirectory.getText()
                                                                .length() > 0);
                                    }
                                }
                            }

                        });

                        fSearchInOpenFiles = new Button(scopeGroup, SWT.RADIO);
                        fSearchInOpenFiles
                                .addSelectionListener(scopeSelectionListener);
                        fSearchInOpenFiles.setText(Messages.OPEN_EDITORS);
                        fSearchInOpenFiles
                                .setData(new Integer(OPEN_FILES_SCOPE));
                        fSearchInOpenFiles.setLayoutData(new GridData(SWT.LEFT,
                                SWT.CENTER, false, false));

                        GridData gridData2 = new GridData();
                        gridData2.widthHint = SWTUtil
                                .getButtonWidthHint(choose);
                        choose.setLayoutData(gridData2);

                        fSearchInDirectory
                                .addSelectionListener(new SelectionListener() {

                                    public void widgetDefaultSelected(
                                            SelectionEvent e) {
                                    }

                                    public void widgetSelected(SelectionEvent e) {
                                        boolean selection = fSearchInDirectory
                                                .getSelection();
                                        if (!selection) {
                                            getContainer()
                                                    .setPerformActionEnabled(
                                                            true);
                                            return;
                                        }
                                        getContainer().setSelectedScope(1);
                                        Control[] c = scopeGroup.getChildren();
                                        for (Control cm : c) {
                                            if (cm instanceof Button) {
                                                Button b = (Button) cm;
                                                if (b != fSearchInDirectory) {
                                                    if (b.getSelection()) {
                                                        b.setSelection(false);
                                                    }
                                                }
                                            }
                                        }
                                        getContainer()
                                                .setPerformActionEnabled(
                                                        fDirectory.getText()
                                                                .length() > 0);
                                    }

                                });
                        scopeGroup.layout(true, true);
                        selectCurrentScope();

                        break;
                    }
                }
                if (fSearchInDirectory == null) {
                    IdeLog.logError(Activator.getDefault(),
                            "Search in directory UI was not created"); //$NON-NLS-1$
                }
            } catch (Throwable e) {
                IdeLog.logError(Activator.getDefault(),
                        "Exception while modifing scope", e); //$NON-NLS-1$
            }
        } catch (Throwable e) {
            IdeLog.logError(Activator.getDefault(),
                    "Exception on top of  modifing scope", e); //$NON-NLS-1$
        }
    }

    /**
     * Selects current scope.
     */
    private void selectCurrentScope() {
        if (fSearchInDirectory != null) {
            Composite parent = fSearchInDirectory.getParent();
            Control[] children = parent.getChildren();
            for (Control child : children) {
                if (child instanceof Button) {
                    final Button btn = (Button) child;
                    if ((btn.getStyle() & SWT.RADIO) != 0) {
                        Object data = btn.getData();
                        if (data != null && data instanceof Integer) {
                            if (((Integer) data).intValue() == scope) {
                                Display.getCurrent().asyncExec(new Runnable() {
                                    public void run() {
                                        btn.setSelection(true);
                                    }
                                });
                                break;
                            } else {
                                Display.getCurrent().asyncExec(new Runnable() {
                                    public void run() {
                                        btn.setSelection(false);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }
    }
}
