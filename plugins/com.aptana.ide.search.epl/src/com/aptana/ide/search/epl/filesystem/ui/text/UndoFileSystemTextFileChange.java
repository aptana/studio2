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

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ContentStamp;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.UndoTextFileChange;
import org.eclipse.ltk.internal.core.refactoring.Changes;
import org.eclipse.ltk.internal.core.refactoring.ContentStamps;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.UndoEdit;

public class UndoFileSystemTextFileChange extends UndoTextFileChange {

    private File fFile;
    private UndoEdit fUndo;
    private ContentStamp fContentStamp;

    public UndoFileSystemTextFileChange(String name, File file, UndoEdit undo,
            ContentStamp stamp, int saveMode) {
        super(name, new FileSystemFile(file), undo, stamp, saveMode);
        fFile = file;
        fUndo = undo;
        fContentStamp = stamp;
    }

    protected Change createUndoChange(UndoEdit edit, ContentStamp stampToRestore)
            throws CoreException {
        return new UndoFileSystemTextFileChange(getName(), fFile, edit,
                stampToRestore, getSaveMode());
    }

    /**
     * {@inheritDoc}
     */
    public void initializeValidationData(IProgressMonitor pm) {
    }

    /**
     * {@inheritDoc}
     */
    public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException {
        if (pm == null) {
            pm = new NullProgressMonitor();
        }
        pm.beginTask("", 1); //$NON-NLS-1$
        try {
            return new RefactoringStatus();
        } finally {
            pm.done();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Change perform(IProgressMonitor pm) throws CoreException {
        if (pm == null) {
            pm = new NullProgressMonitor();
        }
        ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
        pm.beginTask("", 2); //$NON-NLS-1$

        ITextFileBuffer buffer = null;
        IPath path = new Path(fFile.getAbsolutePath());
        try {
            manager.connect(path, LocationKind.LOCATION,
                    new SubProgressMonitor(pm, 1));
            buffer = manager.getTextFileBuffer(path, LocationKind.LOCATION);
            IDocument document = buffer.getDocument();

            LinkedModeModel.closeAllModels(document);

            ContentStamp currentStamp = FileSystemContentStamps.get(fFile,
                    document);
            // perform the changes
            UndoEdit redo = fUndo.apply(document, TextEdit.CREATE_UNDO);
            // try to restore the document content stamp
            boolean success = ContentStamps.set(document, fContentStamp);
            if (needsSaving()) {
                buffer.commit(pm, false);
                if (!success) {
                    // We weren't able to restore document stamp.
                    // Since we save restore the file stamp instead
                    FileSystemContentStamps.set(fFile, fContentStamp);
                }
            }
            return createUndoChange(redo, currentStamp);
        } catch (BadLocationException e) {
            throw Changes.asCoreException(e);
        } catch (MalformedTreeException e) {
            throw Changes.asCoreException(e);
        } catch (CoreException e) {
            throw e;
        } finally {
            if (buffer != null)
                manager.disconnect(path, LocationKind.LOCATION,
                        new SubProgressMonitor(pm, 1));
        }
    }

    private boolean needsSaving() {
        return (getSaveMode() & TextFileChange.FORCE_SAVE) != 0;
    }
}
