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
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.IOverwriteQuery;

import com.aptana.ide.core.ui.CoreUIUtils;
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
            final String msg = MessageFormat.format("{0} exists. Do you wish to overwrite?",
                    pathString);
            final String[] options = { IDialogConstants.YES_LABEL,
                    IDialogConstants.YES_TO_ALL_LABEL, IDialogConstants.NO_LABEL,
                    IDialogConstants.CANCEL_LABEL };
            fShell.getDisplay().syncExec(new Runnable() {

                public void run() {
                    MessageDialog dialog = new MessageDialog(fShell, "Question", null, msg,
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

    public CopyFilesOperation(Shell shell) {
        if (shell == null) {
            fShell = CoreUIUtils.getActiveShell();
        } else {
            fShell = shell;
        }
    }

    public void copyFiles(IAdaptable[] sources, IFileStore destination) {
        IFileStore[] fileStores = new IFileStore[sources.length];
        for (int i = 0; i < fileStores.length; ++i) {
            fileStores[i] = Utils.getFileStore(sources[i]);
        }
        copyFiles(fileStores, destination);
    }

    public void copyFiles(String[] filenames, IFileStore destination) {
        copyFiles(getFileStores(filenames), destination);
    }

    public static String validateDestination(IAdaptable destination, IAdaptable[] sources) {
        IFileStore[] sourceStores = new IFileStore[sources.length];
        for (int i = 0; i < sourceStores.length; ++i) {
            sourceStores[i] = Utils.getFileStore(sources[i]);
        }
        return validateDestination(destination, sourceStores);
    }

    public static String validateDestination(IAdaptable destination, String[] sourceNames) {
        return validateDestination(destination, getFileStores(sourceNames));
    }

    public static String validateDestination(IAdaptable destination, IFileStore[] sourceStores) {
        IFileStore destinationStore = getFolderStore(destination);
        IFileStore sourceParentStore;
        for (IFileStore sourceStore : sourceStores) {
            sourceParentStore = sourceStore.getParent();
            if (destinationStore.equals(sourceStore)
                    || (sourceParentStore != null && destinationStore.equals(sourceParentStore))) {
                return "The source is already contained in the destination";
            }

            if (sourceStore.isParentOf(destinationStore)) {
                return "Destination cannot be a descendent of the source";
            }
        }
        return null;
    }

    protected void copyFile(IFileStore sourceStore, IFileStore destination, IProgressMonitor monitor) {
        if (sourceStore != null) {
            monitor.subTask(MessageFormat.format("Copying {0} to {1}", sourceStore.getName(),
                    destination.getName()));
            IFileStore targetStore = destination.getChild(sourceStore.getName());
            try {
                if (fAlwaysOverwrite) {
                    sourceStore.copy(targetStore, EFS.OVERWRITE, monitor);
                } else if (targetStore.fetchInfo(0, monitor).exists()) {
                    String overwrite = fOverwriteQuery.queryOverwrite(targetStore.toString());
                    if (overwrite.equals(IOverwriteQuery.ALL)
                            || overwrite.equals(IOverwriteQuery.YES)) {
                        sourceStore.copy(targetStore, EFS.OVERWRITE, monitor);
                    }
                } else {
                    sourceStore.copy(targetStore, 0, monitor);
                }
            } catch (CoreException e) {
                // TODO: report the error
            }
        }
    }

    protected boolean getAlwaysOverwrite() {
        return fAlwaysOverwrite;
    }

    protected IOverwriteQuery getOverwriteQuery() {
        return fOverwriteQuery;
    }

    private void copyFiles(final IFileStore[] sources, final IFileStore destination) {
        Job job = new Job("Copying files") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                monitor.beginTask("Copying files", sources.length);
                for (IFileStore source : sources) {
                    if (fCancelled || monitor.isCanceled()) {
                        return Status.CANCEL_STATUS;
                    }
                    copyFile(source, destination, monitor);
                    monitor.worked(1);
                }
                monitor.done();

                return Status.OK_STATUS;
            }

            public boolean belongsTo(Object family) {
                if ("Copying files".equals(family)) {
                    return true;
                }
                return super.belongsTo(family);
            }
        };
        job.setUser(true);
        job.schedule();
    }

    private static IFileStore getFileStore(String filename) {
        try {
            return EFS.getStore((new Path(filename).toFile().toURI()));
        } catch (CoreException e) {
        }
        return null;
    }

    private static IFileStore[] getFileStores(String[] filenames) {
        IFileStore[] fileStores = new IFileStore[filenames.length];
        for (int i = 0; i < fileStores.length; ++i) {
            fileStores[i] = getFileStore(filenames[i]);
        }
        return fileStores;
    }

    private static IFileStore getFolderStore(IAdaptable destination) {
        IFileStore store = Utils.getFileStore(destination);
        IFileInfo info = Utils.getFileInfo(destination);
        if (store != null && info != null && !info.isDirectory()) {
            store = store.getParent();
        }
        return store;
    }
}
