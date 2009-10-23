/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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

package com.aptana.ide.ui.io.actions;

import java.text.MessageFormat;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.IOverwriteQuery;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ide.ui.io.internal.Utils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class CopyFilesOperation {

    /**
     * The parent shell used to show any dialogs
     */
    private Shell fShell;

    /**
     * Flag to indicate if the operation has been canceled by the user
     */
    private boolean fCancelled;

    /**
     * Overwrite-all flag
     */
    private boolean fAlwaysOverwrite;

    private IOverwriteQuery fOverwriteQuery = new IOverwriteQuery() {

        public String queryOverwrite(String pathString) {
            if (fAlwaysOverwrite) {
                return ALL;
            }

            final String returnCode[] = { CANCEL };
            final String msg = MessageFormat.format(Messages.CopyFilesOperation_OverwriteWarning,
                    pathString);
            final String[] options = { IDialogConstants.YES_LABEL,
                    IDialogConstants.YES_TO_ALL_LABEL, IDialogConstants.NO_LABEL,
                    IDialogConstants.CANCEL_LABEL };
            fShell.getDisplay().syncExec(new Runnable() {

                public void run() {
                    MessageDialog dialog = new MessageDialog(fShell,
                            Messages.CopyFilesOperation_QuestionTitle, null, msg,
                            MessageDialog.QUESTION, options, 0) {

                        protected int getShellStyle() {
                            return super.getShellStyle() | SWT.SHEET;
                        }
                    };
                    dialog.open();
                    int returnVal = dialog.getReturnCode();
                    String[] returnCodes = { YES, ALL, NO, CANCEL };
                    returnCode[0] = returnVal == -1 ? CANCEL : returnCodes[returnVal];
                }
            });
            if (returnCode[0] == ALL) {
                fAlwaysOverwrite = true;
            } else if (returnCode[0] == CANCEL) {
                fCancelled = true;
            }
            return returnCode[0];
        }
    };

    /**
     * Constructor.
     * 
     * @param shell
     *            the active shell
     */
    public CopyFilesOperation(Shell shell) {
        if (shell == null) {
            fShell = CoreUIUtils.getActiveShell();
        } else {
            fShell = shell;
        }
    }

    /**
     * Copies an array of sources to the destination location.
     * 
     * @param sources
     *            the array of IAdaptable objects
     * @param destination
     *            the destination file store
     * @param listener
     *            an optional job listener
     */
    public void copyFiles(IAdaptable[] sources, IFileStore destination, IJobChangeListener listener) {
        IFileStore[] fileStores = new IFileStore[sources.length];
        for (int i = 0; i < fileStores.length; ++i) {
            fileStores[i] = Utils.getFileStore(sources[i]);
        }
        copyFiles(fileStores, destination, listener);
    }

    /**
     * Copies an array of sources to the destination location.
     * 
     * @param sources
     *            the array of filenames
     * @param destination
     *            the destination file store
     * @param listener
     *            an optional job listener
     */
    public void copyFiles(String[] filenames, IFileStore destination, IJobChangeListener listener) {
        copyFiles(getFileStores(filenames), destination, listener);
    }

    /**
     * Copies an array of sources to the destination location.
     * 
     * @param sources
     *            the array of source file stores
     * @param destination
     *            the file store representing the destination folder
     * @param monitor
     *            an optional progress monitor
     */
    public IStatus copyFiles(IFileStore[] sources, IFileStore destination, IProgressMonitor monitor) {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        int successCount = 0;
        for (IFileStore source : sources) {
            if (copyFile(source, destination.getChild(source.getName()), monitor)) {
                successCount++;
            }
            if (fCancelled || monitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }
        }
        return new Status(IStatus.OK, IOUIPlugin.PLUGIN_ID, successCount,
                Messages.CopyFilesOperation_Status_OK, null);
    }

    /**
     * Copies an array of files from the source to the destination.
     * 
     * @param sources
     *            the array of source file stores
     * @param sourceRoot
     *            the file store representing the root of source connection
     * @param destinationRoot
     *            the file store representing the root of target connection
     * @param monitor
     *            an optional progress monitor
     */
    public IStatus copyFiles(IFileStore[] sources, IFileStore sourceRoot,
            IFileStore destinationRoot, IProgressMonitor monitor) {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        int successCount = 0;
        for (IFileStore source : sources) {
            if (copyFile(source, sourceRoot, destinationRoot, monitor)) {
                successCount++;
            }
            if (fCancelled || monitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }
        }
        return new Status(IStatus.OK, IOUIPlugin.PLUGIN_ID, successCount,
                Messages.CopyFilesOperation_Status_OK, null);
    }

    /**
     * Checks if there is structural conflict for transferring the sources to
     * the destination.
     * 
     * @param destination
     *            the destination adaptable
     * @param sources
     *            the array of source adaptables
     * @return a descriptive error message if the validation fails, and null
     *         otherwise
     */
    public static String validateDestination(IAdaptable destination, IAdaptable[] sources) {
        IFileStore[] sourceStores = new IFileStore[sources.length];
        for (int i = 0; i < sourceStores.length; ++i) {
            sourceStores[i] = Utils.getFileStore(sources[i]);
        }
        return validateDestination(destination, sourceStores);
    }

    /**
     * Checks if there is structural conflict for transferring the sources to
     * the destination.
     * 
     * @param destination
     *            the destination adaptable
     * @param sourceNames
     *            the array of source filenames
     * @return a descriptive error message if the validation fails, and null
     *         otherwise
     */
    public static String validateDestination(IAdaptable destination, String[] sourceNames) {
        return validateDestination(destination, getFileStores(sourceNames));
    }

    /**
     * @param sourceStore
     *            the file to be copied
     * @param destinationStore
     *            the destination location
     * @param monitor
     *            the progress monitor
     * @return true if the file is successfully copied, false if the operation
     *         did not go through for any reason
     */
    protected boolean copyFile(IFileStore sourceStore, IFileStore destinationStore,
            IProgressMonitor monitor) {
        if (sourceStore == null || CloakingUtils.isFileCloaked(sourceStore)) {
            return false;
        }

        boolean success = true;
        monitor.subTask(MessageFormat.format(Messages.CopyFilesOperation_Copy_Subtask, sourceStore
                .getName(), destinationStore.getName()));
        try {
            if (fAlwaysOverwrite) {
                sourceStore.copy(destinationStore, EFS.OVERWRITE, monitor);
            } else if (destinationStore.fetchInfo(0, monitor).exists()) {
                String overwrite = fOverwriteQuery.queryOverwrite(destinationStore.toString());
                if (overwrite.equals(IOverwriteQuery.ALL) || overwrite.equals(IOverwriteQuery.YES)) {
                    sourceStore.copy(destinationStore, EFS.OVERWRITE, monitor);
                } else {
                    success = false;
                }
            } else {
                sourceStore.copy(destinationStore, EFS.NONE, monitor);
            }
        } catch (CoreException e) {
            IdeLog
                    .logError(IOUIPlugin.getDefault(), MessageFormat.format(
                            Messages.CopyFilesOperation_ERR_FailedToCopy, sourceStore,
                            destinationStore), e);
            success = false;
        }
        return success;
    }

    /**
     * @param sourceStore
     *            the file to be copied
     * @param sourceRoot
     *            the source root
     * @param destinationRoot
     *            the destination root
     * @param monitor
     *            the progress monitor
     * @return true if the file is successfully copied, false if the operation
     *         did not go through for any reason
     */
    protected boolean copyFile(IFileStore sourceStore, IFileStore sourceRoot,
            IFileStore destinationRoot, IProgressMonitor monitor) {
        if (sourceStore == null || CloakingUtils.isFileCloaked(sourceStore)) {
            return false;
        }

        boolean success = true;
        IFileStore[] sourceStores = null, targetStores = null;
        try {
            if (sourceStore.equals(sourceRoot)) {
                // copying the whole source
                sourceStores = sourceRoot.childStores(EFS.NONE, monitor);
                targetStores = new IFileStore[sourceStores.length];
                for (int i = 0; i < targetStores.length; ++i) {
                    targetStores[i] = destinationRoot.getChild(sourceStores[i].getName());
                }
            } else if (sourceRoot.isParentOf(sourceStore)) {
                // finds the relative path of the file to be copied and maps to
                // the destination target
                sourceStores = new IFileStore[1];
                sourceStores[0] = sourceStore;

                targetStores = new IFileStore[1];
                String sourceRootPath = sourceRoot.toString();
                String sourcePath = sourceStore.toString();
                int index = sourcePath.indexOf(sourceRootPath);
                if (index > -1) {
                    String relativePath = sourcePath.substring(index + sourceRootPath.length());
                    targetStores[0] = destinationRoot.getFileStore(new Path(relativePath));
                    // makes sure the parent folder is created on the
                    // destination side
                    IFileStore parent = getFolderStore(targetStores[0]);
                    if (parent != targetStores[0]) {
                        parent.mkdir(EFS.NONE, monitor);
                    }
                }
            }
            if (sourceStores == null) {
                // the file to be copied is not a child of the source root;
                // cannot copy
                success = false;
                sourceStores = new IFileStore[0];
                targetStores = new IFileStore[0];
            }

            for (int i = 0; i < sourceStores.length; ++i) {
                success = copyFile(sourceStores[i], targetStores[i], monitor) && success;
            }
        } catch (CoreException e) {
            IdeLog.logError(IOUIPlugin.getDefault(), MessageFormat.format(
                    Messages.CopyFilesOperation_ERR_FailedToCopyToDest, sourceStore,
                    destinationRoot), e);
            success = false;
        }
        return success;
    }

    protected boolean getAlwaysOverwrite() {
        return fAlwaysOverwrite;
    }

    protected IOverwriteQuery getOverwriteQuery() {
        return fOverwriteQuery;
    }

    private void copyFiles(final IFileStore[] sources, final IFileStore destination,
            IJobChangeListener listener) {
        Job job = new Job(Messages.CopyFilesOperation_CopyJob_Title) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                return copyFiles(sources, destination, monitor);
            }

            public boolean belongsTo(Object family) {
                if (Messages.CopyFilesOperation_CopyJob_Title.equals(family)) {
                    return true;
                }
                return super.belongsTo(family);
            }
        };
        if (listener != null) {
            job.addJobChangeListener(listener);
        }
        job.setUser(true);
        job.schedule();
    }

    /**
     * Checks if there is structural conflict for transferring the sources to
     * the destination.
     * 
     * @param destination
     *            the destination adaptable
     * @param sourceStores
     *            the array of source stores
     * @return a descriptive error message if the validation fails, and null
     *         otherwise
     */
    private static String validateDestination(IAdaptable destination, IFileStore[] sourceStores) {
        IFileStore destinationStore = getFolderStore(destination);
        IFileStore sourceParentStore;
        for (IFileStore sourceStore : sourceStores) {
            sourceParentStore = sourceStore.getParent();
            if (destinationStore.equals(sourceStore)
                    || (sourceParentStore != null && destinationStore.equals(sourceParentStore))) {
                return Messages.CopyFilesOperation_ERR_SourceInDestination;
            }

            if (sourceStore.isParentOf(destinationStore)) {
                return Messages.CopyFilesOperation_ERR_DestinationInSource;
            }
        }
        return null;
    }

    /**
     * @param filename
     *            the filename
     * @return the corresponding file store, or null if it could not be found
     */
    private static IFileStore getFileStore(String filename) {
        try {
            return EFS.getStore((new Path(filename).toFile().toURI()));
        } catch (CoreException e) {
        }
        return null;
    }

    /**
     * @param filenames
     *            an array of filenames
     * @return the array of corresponding file stores
     */
    private static IFileStore[] getFileStores(String[] filenames) {
        IFileStore[] fileStores = new IFileStore[filenames.length];
        for (int i = 0; i < fileStores.length; ++i) {
            fileStores[i] = getFileStore(filenames[i]);
        }
        return fileStores;
    }

    /**
     * Gets the folder the file belongs in. If the file is a directory, returns
     * itself.
     * 
     * @param adaptable
     *            an IAdaptable that could adapt to an IFileStore
     * @return the folder file store
     */
    private static IFileStore getFolderStore(IAdaptable adaptable) {
        IFileStore store = Utils.getFileStore(adaptable);
        IFileInfo info = Utils.getFileInfo(adaptable);
        if (store != null && info != null && !info.isDirectory()) {
            store = store.getParent();
        }
        return store;
    }
}
