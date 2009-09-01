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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.search.core.text.TextSearchScope;
import org.eclipse.search.internal.ui.SearchMessages;
import org.eclipse.search.internal.ui.SearchPlugin;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * The visitor that does the actual work.
 */
public class TextSearchVisitor extends
        org.eclipse.search.internal.core.text.TextSearchVisitor {

    private final MultiStatus fStatus;
    private final boolean openEditorsOnly;
    private final boolean refresh;

    /**
     * @param collector
     * @param searchPattern
     * @param openEditorsOnly
     * @param refresh
     */
    public TextSearchVisitor(TextSearchRequestor collector,
            Pattern searchPattern, boolean openEditorsOnly, boolean refresh) {
        super(collector, searchPattern);
        fStatus = new MultiStatus(NewSearchUI.PLUGIN_ID, IStatus.OK,
                SearchMessages.TextSearchEngine_statusMessage, null);
        this.openEditorsOnly = openEditorsOnly;
        this.refresh = refresh;
    }

    private class FilesOfScopeCalculator extends
            org.eclipse.search.internal.core.text.FilesOfScopeCalculator {

        private final TextSearchScope fScope;
        private final MultiStatus fStatus;

        public FilesOfScopeCalculator(TextSearchScope scope, MultiStatus status) {
            super(scope, status);
            fScope = scope;
            fStatus = status;
        }

        public IFile[] process() {
            IFile[] files = super.process();

            IResource[] roots = fScope.getRoots();
            for (IResource resource : roots) {
                try {
                    if (refresh) {
                        resource.refreshLocal(IResource.DEPTH_INFINITE, null);
                    }
                } catch (CoreException ex) {
                    // report and ignore
                    fStatus.add(ex.getStatus());
                }
            }
            return files;
        }
    }

    /**

     * @see org.eclipse.search.internal.core.text.TextSearchVisitor#search(org.eclipse.search.core.text.TextSearchScope, org.eclipse.core.runtime.IProgressMonitor)
     */
    public IStatus search(TextSearchScope scope, IProgressMonitor monitor) {
        IFile[] files = null;
        if (this.openEditorsOnly) {
            List<IFile> filesList = new ArrayList<IFile>();
            IWorkbench workbench = SearchPlugin.getDefault().getWorkbench();
            IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
            for (int i = 0; i < windows.length; i++) {
                IWorkbenchPage[] pages = windows[i].getPages();
                for (int x = 0; x < pages.length; x++) {
                    IEditorReference[] editorRefs = pages[x]
                            .getEditorReferences();
                    for (int z = 0; z < editorRefs.length; z++) {
                        IEditorPart ep = editorRefs[z].getEditor(false);
                        if ((ep instanceof ITextEditor)) {
                            IEditorInput editorInput = ep.getEditorInput();
                            if (editorInput instanceof IFileEditorInput) {
                                IFileEditorInput ed = (IFileEditorInput) editorInput;
                                filesList.add(ed.getFile());
                            }
                        }
                    }
                }
            }
            files = new IFile[filesList.size()];
            filesList.toArray(files);
        } else {
            files = new FilesOfScopeCalculator(scope, fStatus).process();
        }
        return search(files, monitor);
    }
}
