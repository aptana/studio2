/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
package com.aptana.ide.editors.junit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.framework.internal.core.BundleURLConnection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editors.unified.utils.EditorHelper;

/**
 * The main plugin class to be used in the desktop.
 */
public class EditorsJunitPlugin extends AbstractUIPlugin {

	//The shared instance.
	private static EditorsJunitPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public EditorsJunitPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 * @param context 
	 * @throws Exception 
	 */ 
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 * @param context 
	 * @throws Exception 
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 * @return  Returns the shared instance.
	 */
	public static EditorsJunitPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("com.aptana.ide.editors.junit", path); //$NON-NLS-1$
	}
	
	/**
	 * Open a project file in an editor.  
	 * If the project has not yet been imported into the workspace, it will automatically be imported.
	 * @param projectName the name of the project to open (must match a plugin root folder name)
	 * @param filePath
	 * @return Returns a project file in an editor.  
	 * @throws PartInitException
	 */
	public IEditorPart openProjectFile(String projectName, IPath filePath) throws PartInitException
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProject project = workspace.getRoot().getProject(projectName);
		if(!project.exists())
		{
			File pluginDir = getPluginDir();
			File projectDir = new File(pluginDir, projectName);
			if(!projectDir.exists())
			{
				//the is no folder in the plugin root directory that matches the project name
				//if the folder exists in subversion, but is still not found, it probably wasn't marked
				//to be included in the binary build (on the plugin editor's build tab)
				throw new IllegalArgumentException("Project not found [" + projectName + "]: was it marked for inclusion in the binary build???"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			

			//update the project's path to point to the projectDir on disk. 
	        final IProjectDescription description = workspace
	                .newProjectDescription(project.getName());
	        description.setLocation(new Path(getFullPath(projectDir)));
	        
	        //define the operation to create a new project
	        WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
	            protected void execute(IProgressMonitor monitor)
	                    throws CoreException {
	                createProject(description, project, monitor);
	            }
	        };
	        try {
				PlatformUI.getWorkbench().getProgressService().run(false, false, op);
			}
			catch (InvocationTargetException e) {
				IdeLog.logError(this, "Error", e); //$NON-NLS-1$
			}
			catch (InterruptedException e) {
				IdeLog.logError(this, "Error", e); //$NON-NLS-1$
			}
		}
		
		//open the file
		IFile file = (IFile)project.findMember(filePath);
		if(file == null || !file.exists())
		{
			throw new IllegalStateException("Unable to open the specified file: " + projectName + ": " + filePath.toPortableString()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		IEditorPart editorPart = EditorHelper.openInEditor(file, true);
		return editorPart;
	}
	
	/**
	 * getFullPath
	 * 
	 * @param f
	 * @return String
	 */
	public static String getFullPath(File f)
	{
		try
		{
			return f.getCanonicalPath();
		}
		catch (IOException e)
		{
			return f.getAbsolutePath();
		}
	}
	
	/**
     * Creates a project resource given the project handle and description.
     *
     * @param description the project description to create a project resource for
     * @param projectHandle the project handle to create a project resource for
     * @param monitor the progress monitor to show visual progress with
     *
     * @exception CoreException if the operation fails
     * @exception OperationCanceledException if the operation is canceled
     */
    private void createProject(IProjectDescription description,
            IProject projectHandle, IProgressMonitor monitor)
            throws CoreException, OperationCanceledException {
        try {
            monitor.beginTask(StringUtils.EMPTY, 2000);

            projectHandle.create(description, new SubProgressMonitor(monitor,
                    1000));

            if (monitor.isCanceled())
            {
            	throw new OperationCanceledException();
            }

            projectHandle.open(new SubProgressMonitor(monitor, 1000));

        } finally {
            monitor.done();
        }
    }
	
	private File pluginDir;
	private File getPluginDir(){
		if(pluginDir == null)
		{
			URL pluginUrl = EditorsJunitPlugin.getDefault().getBundle().getEntry("/"); //$NON-NLS-1$
			try {
				BundleURLConnection conn = (BundleURLConnection)pluginUrl.openConnection();
				URL fileUrl = conn.getFileURL();
				String file = fileUrl.getFile();
				pluginDir = new File(file);
			}
			catch (IOException e) {
				throw new IllegalStateException("Could not locate plugin directory"); //$NON-NLS-1$
			}
		}
		return pluginDir;
	}
}
