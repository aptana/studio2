package com.aptana.ide.pathtools;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.ui.io.file.LocalFile;
import com.aptana.ide.pathtools.handlers.Utilities;
import com.aptana.ide.pathtools.preferences.PathtoolsPreferences;

/**
 * This launches the OS file explorer showing the selected folder or the folder
 * containing the selected file.
 * 
 * @author Sandip V. Chitale
 * 
 */
public class ExploreAction implements IViewActionDelegate, IObjectActionDelegate {
	private File fileObject;

	private static String fileExploreComand = null;
	private static String folderExploreComand = null;

	private IWorkbenchWindow window;

	public void dispose() {
	}

	public void init(IViewPart view) {
		this.window = view.getViewSite().getWorkbenchWindow();
	}
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.window = targetPart.getSite().getWorkbenchWindow();
	}

	public void run(IAction action) {
		// Get the configured explorer commands for folder and file
		folderExploreComand = Activator.getDefault().getPreferenceStore()
				.getString(PathtoolsPreferences.FOLDER_EXPLORE_COMMAND_KEY);
		fileExploreComand = Activator.getDefault().getPreferenceStore()
				.getString(PathtoolsPreferences.FILE_EXPLORE_COMMAND_KEY);
		if (fileExploreComand == null || folderExploreComand == null) {
			return;
		}
		// Is this a physical file on the disk ?
		if (fileObject != null) {
			String commandFormat = fileObject.isDirectory() ? folderExploreComand
					: fileExploreComand;

			Utilities.launch(commandFormat, fileObject);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		fileObject = null;
		action.setEnabled(false);
		try {
			IPath location = null;
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				// Is only one item selected?
				if (structuredSelection.size() == 1) {
					Object firstElement = structuredSelection.getFirstElement();
					if (firstElement instanceof IResource) {
						// Is this an IResource ?
						IResource resource = (IResource) firstElement;
						location = resource.getLocation();
                    } else if (firstElement instanceof IAdaptable) {
                    	if (firstElement instanceof IConnectionPoint) {
                    		try {
    							firstElement = ((IConnectionPoint) firstElement).getRoot();
    						} catch (CoreException ignore) {
    						}
                    	}
						IAdaptable adaptable = (IAdaptable) firstElement;
						// Is this a File adaptable ?
						fileObject = (File) adaptable.getAdapter(File.class);
						if (fileObject == null) {
							// Is this an IResource adaptable ?
							IResource resource = (IResource) adaptable
									.getAdapter(IResource.class);
							if (resource != null) {
								location = resource.getLocation();
							}
						}
					} else if (firstElement instanceof LocalFile) {
					    LocalFile file = (LocalFile) firstElement;
					    fileObject = file.getFile();
					}
				}
			}
			if (fileObject == null) {
			    if (location != null) {
                    fileObject = location.toFile();
                } else if (window != null) {
					IWorkbenchPage activePage = window.getActivePage();
					if (activePage != null) {
						IWorkbenchPart activeEditor = activePage.getActivePart();
						if (activeEditor instanceof ITextEditor) {
							ITextEditor abstractTextEditor = (ITextEditor) activeEditor;
							IEditorInput editorInput = abstractTextEditor.getEditorInput();
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
		} finally {
			action.setEnabled(fileObject != null);
		}
	}

}
