/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ide.search.epl.filesystem.ui.text;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.DocumentRewriteSessionType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ContentStamp;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextEditBasedChangeGroup;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.TextEditProcessor;
import org.eclipse.text.edits.UndoEdit;

public class FileSystemTextFileChange extends TextFileChange {

    // the file to change
    private File fFile;

    private int fAcquireCount;
    private ITextFileBuffer fBuffer;
    private ContentStamp fContentStamp;

    /**
     * Creates a new <code>TextFileChange</code> for the given file.
     * 
     * @param name
     *            the change's name mainly used to render the change in the UI
     * @param file
     *            the file this text change operates on
     */
    public FileSystemTextFileChange(String name, File file) {
        super(name, new FileSystemFile(file));
        Assert.isNotNull(file);
        fFile = file;
    }

    /**
     * Returns the <code>File</code> this change is working on.
     * 
     * @return the file this change is working on
     */
    public File getFileSystemFile() {
        return fFile;
    }

    /**
     * @see org.eclipse.ltk.core.refactoring.TextFileChange#createUndoChange(org.eclipse.text.edits.UndoEdit,
     *      org.eclipse.ltk.core.refactoring.ContentStamp)
     */
    protected Change createUndoChange(UndoEdit edit, ContentStamp stampToRestore) {
        return new UndoFileSystemTextFileChange(getName(), fFile, edit,
                fContentStamp, getSaveMode());
    }

    /**
     * {@inheritDoc}
     */
    public Object getModifiedElement() {
        return fFile;
    }

    /**
     * {@inheritDoc}
     */
    public void initializeValidationData(IProgressMonitor monitor) {
    }

    /**
     * {@inheritDoc}
     */
    public RefactoringStatus isValid(IProgressMonitor monitor)
            throws CoreException {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        try {
            monitor.beginTask("", 1); //$NON-NLS-1$

            RefactoringStatus result = new RefactoringStatus();
            return result;
        } finally {
            monitor.done();
        }
    }

    /**
     * {@inheritDoc}
     */
    protected IDocument acquireDocument(IProgressMonitor pm)
            throws CoreException {
        fAcquireCount++;
        if (fAcquireCount > 1) {
            return fBuffer.getDocument();
        }

        ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
        IPath path = new Path(fFile.getAbsolutePath());
        manager.connect(path, LocationKind.LOCATION, pm);
        fBuffer = manager.getTextFileBuffer(path, LocationKind.LOCATION);
        IDocument result = fBuffer.getDocument();
        fContentStamp = FileSystemContentStamps.get(fFile, result);
        return result;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The implementation of this method only commits the underlying buffer if
     * {@link #needsSaving()} and {@link #isDocumentModified()} returns
     * <code>true</code>.
     * </p>
     */
    protected void commit(IDocument document, IProgressMonitor pm)
            throws CoreException {
        if (needsSaving()) {
            fBuffer.commit(pm, false);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void releaseDocument(IDocument document, IProgressMonitor pm)
            throws CoreException {
        Assert.isTrue(fAcquireCount > 0);
        if (fAcquireCount == 1) {
            ITextFileBufferManager manager = FileBuffers
                    .getTextFileBufferManager();
            manager.disconnect(new Path(fFile.getAbsolutePath()),
                    LocationKind.LOCATION, pm);
        }
        fAcquireCount--;
    }

    /**
     * @see org.eclipse.ltk.core.refactoring.TextChange#performEdits(org.eclipse.jface.text.IDocument)
     */
    protected UndoEdit performEdits(final IDocument document)
            throws BadLocationException, MalformedTreeException {
        if (!fBuffer.isSynchronizationContextRequested()) {
            return performEdits2(document);
        }

        ITextFileBufferManager fileBufferManager = FileBuffers
                .getTextFileBufferManager();

        /** The lock for waiting for computation in the UI thread to complete. */
        final Lock completionLock = new Lock();
        final UndoEdit[] result = new UndoEdit[1];
        final BadLocationException[] exception = new BadLocationException[1];
        Runnable runnable = new Runnable() {
            public void run() {
                synchronized (completionLock) {
                    try {
                        result[0] = performEdits2(document);
                    } catch (BadLocationException e) {
                        exception[0] = e;
                    } finally {
                        completionLock.fDone = true;
                        completionLock.notifyAll();
                    }
                }
            }
        };

        synchronized (completionLock) {
            fileBufferManager.execute(runnable);
            while (!completionLock.fDone) {
                try {
                    completionLock.wait(500);
                } catch (InterruptedException x) {
                }
            }
        }

        if (exception[0] != null) {
            throw exception[0];
        }

        return result[0];
    }

    /**
     * @see org.eclipse.ltk.core.refactoring.TextFileChange#isDocumentAcquired()
     */
    protected boolean isDocumentAcquired() {
        return fAcquireCount > 0;
    }

    /**
     * @see org.eclipse.ltk.core.refactoring.TextFileChange#isDocumentModified()
     */
    protected boolean isDocumentModified() {
        if (fAcquireCount > 0) {
            ContentStamp currentStamp = FileSystemContentStamps.get(fFile,
                    fBuffer.getDocument());
            return !currentStamp.equals(fContentStamp);
        }
        return false;
    }

    private static class Lock {
        /**
         * <code>true</code> iff the operation is done.
         */
        public boolean fDone;
    }

    private UndoEdit performEdits2(final IDocument document)
            throws BadLocationException {
        DocumentRewriteSession session = null;
        try {
            if (document instanceof IDocumentExtension4) {
                session = ((IDocumentExtension4) document)
                        .startRewriteSession(DocumentRewriteSessionType.UNRESTRICTED);
            }

            LinkedModeModel.closeAllModels(document);
            TextEditProcessor processor = createTextEditProcessor(document,
                    TextEdit.CREATE_UNDO);
            return processor.performEdits();
        } finally {
            if (session != null) {
                ((IDocumentExtension4) document).stopRewriteSession(session);
            }
        }
    }

    private TextEditProcessor createTextEditProcessor(IDocument document,
            int flags) {
        if (getEdit() == null) {
            return new TextEditProcessor(document, new MultiTextEdit(0, 0),
                    flags);
        }
        List<TextEdit> excludes = new ArrayList<TextEdit>(0);
        TextEditBasedChangeGroup[] groups = getChangeGroups();
        for (int index = 0; index < groups.length; index++) {
            TextEditBasedChangeGroup edit = groups[index];
            if (!edit.isEnabled()) {
                excludes.addAll(Arrays.asList(edit.getTextEditGroup()
                        .getTextEdits()));
            }
        }

        LocalTextEditProcessor result = new LocalTextEditProcessor(document,
                getEdit(), flags | TextEdit.UPDATE_REGIONS);
        result.setExcludes((TextEdit[]) excludes.toArray(new TextEdit[excludes
                .size()]));
        return result;
    }

    /**
     * Text edit processor which has the ability to selectively include or
     * exclude single text edits.
     */
    private static final class LocalTextEditProcessor extends TextEditProcessor {
        private TextEdit[] fExcludes;
        private TextEdit[] fIncludes;

        protected LocalTextEditProcessor(IDocument document, TextEdit root,
                int flags) {
            super(document, root, flags);
        }

        public void setExcludes(TextEdit[] excludes) {
            Assert.isNotNull(excludes);
            Assert.isTrue(fIncludes == null);
            fExcludes = flatten(excludes);
        }

        protected boolean considerEdit(TextEdit edit) {
            if (fExcludes != null) {
                for (int i = 0; i < fExcludes.length; i++) {
                    if (edit.equals(fExcludes[i]))
                        return false;
                }
                return true;
            }
            if (fIncludes != null) {
                for (int i = 0; i < fIncludes.length; i++) {
                    if (edit.equals(fIncludes[i]))
                        return true;
                }
                return false;
            }
            return true;
        }

        private TextEdit[] flatten(TextEdit[] edits) {
            List<TextEdit> result = new ArrayList<TextEdit>(5);
            for (int i = 0; i < edits.length; i++) {
                flatten(result, edits[i]);
            }
            return result.toArray(new TextEdit[result.size()]);
        }

        private void flatten(List<TextEdit> result, TextEdit edit) {
            result.add(edit);
            TextEdit[] children = edit.getChildren();
            for (int i = 0; i < children.length; i++) {
                flatten(result, children[i]);
            }
        }
    }

}
