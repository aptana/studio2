package com.aptana.ide.pathtools;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import com.aptana.ide.pathtools.handlers.Utilities;
import com.aptana.ide.pathtools.preferences.PathtoolsPreferences;
import com.aptana.ide.ui.io.FileSystemUtils;

/**
 * This copies the absolute paths of selected folders and files (one per line)
 * into the Clipboard.
 * 
 * @author Sandip V. Chitale
 * 
 */
public class CopyPathAction implements IViewActionDelegate, IObjectActionDelegate {
	private List<File> files = new LinkedList<File>();
	private List<IPath> resourcePaths = new LinkedList<IPath>();
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
		copyToClipboard(
				Activator.getDefault().getPreferenceStore().getString(PathtoolsPreferences.LAST_COPY_PATH_FORMAT),
				files);
	}

	@SuppressWarnings("unchecked")
	public void selectionChanged(IAction action, ISelection selection) {
		// Start with a clear list
		files.clear();
		resourcePaths.clear();
		if (selection instanceof IStructuredSelection) {
			// Get structured selection
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;

			// Iterate through selected items
			Iterator iterator = structuredSelection.iterator();
			while (iterator.hasNext()) {
				Object firstElement = iterator.next();
				IPath fullPath = null;
				IPath location = null;
				if (firstElement instanceof IResource) {
					// Is it a IResource ?
					IResource resource = (IResource) firstElement;
					// Get the location
					location = resource.getLocation();
					fullPath = resource.getFullPath();
                } else if (firstElement instanceof IAdaptable) {
                    IAdaptable adaptable = (IAdaptable) firstElement;
                    // Is it a IResource adaptable ?
                    IResource resource = (IResource) adaptable.getAdapter(IResource.class);
                    if (resource != null) {
                        // Get the location
                        location = resource.getLocation();
                        fullPath = resource.getFullPath();
                    } else {
                        IFileStore fileStore = FileSystemUtils.getFileStore(adaptable);
                        try {
                            File file = fileStore.toLocalFile(EFS.NONE, null);
                            if (file != null) {
                                files.add(file);
                            }
                        } catch (CoreException e) {
                        }
                    }
 				}
				if (location != null) {
					// Get the file for the location
					File file = location.toFile();
					if (file != null) {
						// Add the absolute path to the list
						files.add(file);
					}
				}
				if (fullPath != null) {
					resourcePaths.add(fullPath);
				}
			}
		}
		if (files.size() == 0) {
			if (window != null) {
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
								File file = iFile.getLocation().toFile();
								if (file != null) {
									files.add(file);
									resourcePaths.add(iFile.getFullPath());
								}
							}
						}
					}
				}
			}
		}
		action.setEnabled(files.size() > 0);
	}

	private static void copyToClipboard(String pathFormat, List<File> files) {
		// Are there any paths selected ?
		if (files.size() > 0) {
			// Build a string with each path on separate line
			StringBuilder stringBuilder = new StringBuilder();
			for (File file : files) {
				stringBuilder.append(Utilities.formatCommand(pathFormat, file)
						+ (files.size() > 1 ? System.getProperty("line.separator", "\n") : "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			copyToClipboard(stringBuilder.toString());
		}
	}
	
	private static void copyToClipboard(String string) {
		// Get Clipboard
		Clipboard clipboard = new Clipboard(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell().getDisplay());
		// Put the paths string into the Clipboard
		clipboard.setContents(new Object[] { string },
				new Transfer[] { TextTransfer.getInstance() });
	}
}
