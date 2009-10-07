package com.aptana.ide.pathtools.handlers;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.ide.pathtools.Activator;
import com.aptana.ide.pathtools.CommandLauncher;
import com.aptana.ide.pathtools.preferences.PathtoolsPreferences;

public class OpenShellHandler extends AbstractHandler {

    private File fileObject;
    private static String shellOnFileEditComand = null;
    private static String shellOnFolderComand = null;

    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        updateSelection(event, selection);

        // Get the configured explorer commands for folder and file
        shellOnFolderComand = Activator.getDefault().getPreferenceStore()
                .getString(PathtoolsPreferences.SHELL_ON_FOLDER_COMMAND_KEY);
        shellOnFileEditComand = Activator.getDefault().getPreferenceStore()
                .getString(PathtoolsPreferences.SHELL_ON_FILE_COMMAND_KEY);
        if (shellOnFileEditComand == null || shellOnFolderComand == null) {
            return null;
        }
        // Is this a physical file on the disk ?
        if (fileObject != null) {
            String commandFormat = fileObject.isDirectory() ? shellOnFolderComand
                    : shellOnFileEditComand;

            // Substitute parameter values and format the edit command
            String command = Utilities.formatCommand(commandFormat, fileObject);

            // Launch the edit command
            CommandLauncher.launch(command);
        }
        return null;
    }

    private void updateSelection(ExecutionEvent event, ISelection selection) {
        if (selection == null) {
            return;
        }
        fileObject = null;
        IPath location = null;
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;
            // Is only one item selected?
            if (structuredSelection.size() == 1) {
                Object firstElement = structuredSelection.getFirstElement();
                if (firstElement instanceof IResource) {
                    // Is this an IResource
                    IResource resource = (IResource) firstElement;
                    location = resource.getLocation();
                } else if (firstElement instanceof IAdaptable) {
                    IAdaptable adaptable = (IAdaptable) firstElement;
                    // Is this a File adaptable
                    fileObject = (File) adaptable.getAdapter(File.class);
                    if (fileObject == null) {
	                    // Is this an IResource adaptable
	                    IResource resource = (IResource) adaptable
	                            .getAdapter(IResource.class);
	                    if (resource != null) {
	                        location = resource.getLocation();
	                    }
                    }
                }
            }
        }
        if (fileObject == null) {
            if (location != null) {
                fileObject = location.toFile();
            } else {
                IWorkbenchWindow window = HandlerUtil
                        .getActiveWorkbenchWindow(event);
                if (window != null) {
                    IWorkbenchPage activePage = window.getActivePage();
                    if (activePage != null) {
                        IWorkbenchPart activeEditor = activePage
                                .getActivePart();
                        if (activeEditor instanceof ITextEditor) {
                            ITextEditor abstractTextEditor = (ITextEditor) activeEditor;
                            IEditorInput editorInput = abstractTextEditor
                                    .getEditorInput();
                            if (editorInput instanceof IFileEditorInput) {
                                IFileEditorInput fileEditorInput = (IFileEditorInput) editorInput;
                                IFile iFile = fileEditorInput.getFile();
                                if (iFile != null) {
                                    location = iFile.getLocation();
                                    fileObject = location.toFile();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
