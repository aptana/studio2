/*******************************************************************************
 * Copyright (c) 2005, 2006 Eclipse Foundation
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bjorn Freeman-Benson - initial implementation
 *     Ward Cunningham - initial implementation
 *******************************************************************************/

package org.eclipse.eclipsemonkey;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.eclipsemonkey.actions.RecreateMonkeyCoolbarAction;
import org.eclipse.eclipsemonkey.actions.RecreateMonkeyMenuAction;
import org.eclipse.eclipsemonkey.dom.Utilities;
import org.eclipse.eclipsemonkey.language.IMonkeyLanguageFactory;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * UpdateMonkeyActionsResourceChangeListener
 */
public class UpdateMonkeyActionsResourceChangeListener implements
		IResourceChangeListener
{
	/**
	 * Valid default monkey exceptions
	 */
	public static String extensions = "js|em"; //$NON-NLS-1$
	
	/**
	 * @param exts
	 */
	public static void setExtensions(String[] exts)
	{
		if(exts == null)
			return;
		
		String extPattern = ""; //$NON-NLS-1$
		
		for (int i = 0; i < exts.length; i++) {
			extPattern += exts[i];
			if(i < exts.length - 1) extPattern += "|"; //$NON-NLS-1$
		}
		
		extensions = extPattern;
	}
	
	/**
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		final Boolean changes[] = new Boolean[1];
		changes[0] = new Boolean(false);
		
		IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {
			private void found_a_change() {
				changes[0] = new Boolean(true);
			}
			
			private Pattern monkey_file_pattern = Pattern.compile("/.+/(monkey|scripts)/(.+\\.(" + //$NON-NLS-1$
					extensions
					+ "))"); //$NON-NLS-1$

			public boolean visit(IResourceDelta delta) {
				String fullPath = delta.getFullPath().toString();
				Matcher matcher = monkey_file_pattern.matcher(fullPath.toLowerCase());
				if( matcher.matches() ) {
					IFile file = (IFile) delta.getResource();
					if (file == null || file.getLocation() == null) {
						return true;
					}
					fullPath = file.getLocation().toPortableString();
					
					switch (delta.getKind()) {
					case IResourceDelta.ADDED:
						processNewOrChangedScript(fullPath, file.getLocation());
						found_a_change();
						break;
					case IResourceDelta.REMOVED:
						processRemovedScript(fullPath, file.getLocation());
						found_a_change();
						break;
					case IResourceDelta.CHANGED:
						if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0) {
							processRemovedScript(delta.getMovedFromPath()
									.toString(), file.getLocation());
							processNewOrChangedScript(fullPath, file.getLocation());
							found_a_change();
						}
						if ((delta.getFlags() & IResourceDelta.MOVED_TO) != 0) {
							processRemovedScript(fullPath, file.getLocation());
							processNewOrChangedScript(delta.getMovedToPath()
									.toString(), file.getLocation());
							found_a_change();
						}
						if ((delta.getFlags() & IResourceDelta.REPLACED) != 0) {
							processNewOrChangedScript(fullPath, file.getLocation());
							found_a_change();
						}
						if ((delta.getFlags() & IResourceDelta.CONTENT) != 0) {
							processNewOrChangedScript(fullPath, file.getLocation());
							found_a_change();
						}
						break;
					}
				}
				return true;
			}
		};
		try {
			event.getDelta().accept(visitor);
		} catch (CoreException x) {
			// log an error in the error log
		}
		boolean anyMatches = ((Boolean) (changes[0])).booleanValue();
		if (anyMatches) {
			createTheMonkeyMenu();
		}
	}

	private void processNewOrChangedScript(String name, IPath path) {
		StoredScript store = new StoredScript();
		store.scriptPath = path;
		try {
			IMonkeyLanguageFactory langFactory = (IMonkeyLanguageFactory) EclipseMonkeyPlugin.getDefault().getLanguageStore().get(path.getFileExtension());
			store.metadata = getMetadataFrom(langFactory, path);
		} catch (CoreException x) {
			store.metadata = new ScriptMetadata();
			// log an error in the error log
		} catch (IOException x) {
			store.metadata = new ScriptMetadata();
			// log an error in the error log
		}
		EclipseMonkeyPlugin.getDefault().addScript(name, store);
	}

	private void processRemovedScript(String name, IPath path) {
		EclipseMonkeyPlugin.getDefault().removeScript(name);
	}

	/**
	 * @param extensions
	 * @param alternatePaths
	 */
	public void rescanAllFiles(String[] extensions, String[] alternatePaths) 
	{
		EclipseMonkeyPlugin.getDefault().clearScripts();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		findScriptsInProjects("scripts", extensions, workspace); //$NON-NLS-1$
		findScriptsInProjects("monkey", extensions, workspace); //$NON-NLS-1$
		findScriptsInFolder(extensions, alternatePaths);
	}

	private void findScriptsInFolder(String[] extensions, String[] alternatePaths) {
		for (int i = 0; i < alternatePaths.length; i++) {
			String path = alternatePaths[i];
			
			File folder = new File(path);
			String[] files = folder.list();
			
			for (int j = 0; j < files.length; j++) {
				
				String fullPath = folder.getAbsolutePath() + File.separator + files[j];
				File f = new File(fullPath);
				
				if(f.isFile())
				{
					for (int k = 0; k < extensions.length; k++) 
					{
						String ext = extensions[k].toLowerCase();
						if (f.getName().toLowerCase().endsWith("." + ext)) //$NON-NLS-1$
						{
							Path p = new Path(f.getAbsolutePath());
							processNewOrChangedScript(p.toPortableString(), p);
						}
					}
				}
			}
		}
	}

	private void findScriptsInProjects(String folderName,
			String[] extensions, IWorkspace workspace) {
		for (int i = 0; i < workspace.getRoot().getProjects().length; i++)
		{
			IProject project = workspace.getRoot().getProjects()[i];
			IFolder folder = project.getFolder(folderName);
			if (folder == null)
				continue;
			try {
				for (int j = 0; j < folder.members().length; j++) {
					IResource resource = folder.members()[j];
					if (resource instanceof IFile) {
						IFile file = (IFile) resource;
						
						for (int k = 0; k < extensions.length; k++) 
						{
							String ext = extensions[k].toLowerCase();

							if (file.getName().toLowerCase().endsWith("." + ext)) //$NON-NLS-1$
							{
								String fullPath = file.getLocation().toPortableString();
								processNewOrChangedScript(fullPath, new Path(fullPath));
							}
						}
					}
				}
			} catch (CoreException x) {
				// ignore folders we cannot access
			}
		}
	}

	private ScriptMetadata getMetadataFrom(IMonkeyLanguageFactory langFactory, IPath path) throws CoreException,
			IOException {
		String contents = Utilities.getFileContents(path);
		ScriptMetadata metadata = langFactory.getScriptMetadata(contents);
		metadata.setPath(path);
		return metadata;
	}

	/**
	 * 
	 */
	public static void createTheMonkeyMenu() {
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench()
				.getWorkbenchWindows();
		for (int i = 0; i < windows.length; i++) {
			final IWorkbenchWindow window = windows[i];
			window.getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
					RecreateMonkeyMenuAction action = new RecreateMonkeyMenuAction();
					action.init(window);
					action.run(null);
					RecreateMonkeyCoolbarAction cAction = new RecreateMonkeyCoolbarAction();
					cAction.init(window);
					cAction.run(null);
				}
			});
		}
	}

}
