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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.framework.Bundle;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.core.ui.WebPerspectiveFactory;

/**
 * @author Robin Debreuil
 */
public final class ProjectTestUtils
{
	public static String HTML_EDITOR_ID = "com.aptana.ide.editors.HTMLEditor"; //$NON-NLS-1$
	public static String CSS_EDITOR_ID = "com.aptana.ide.editors.CSSEditor"; //$NON-NLS-1$
	public static String JS_EDITOR_ID = "com.aptana.ide.editors.JSEditor"; //$NON-NLS-1$
	public static String XML_EDITOR_ID = "com.aptana.ide.editors.XMLEditor"; //$NON-NLS-1$
	public static String PHP_EDITOR_ID = "com.aptana.ide.editors.PHPEditor"; //$NON-NLS-1$

	private ProjectTestUtils()
	{
	}

	/**
	 * Opens a file in the editor
	 * 
	 * @param file
	 * @return IEditorPart
	 */
	public static IEditorPart openInEditor(IFile file, String editorId)
	{
		IEditorPart result = null;
		try
		{
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().findEditor(editorId);
			FileEditorInput input = new FileEditorInput(file);
			IWorkbenchPage page = window.getActivePage();

			if(desc == null)
			{
				throw new IllegalArgumentException(StringUtils.format("Unable to find an editor type for file {0}", file.getName())); //$NON-NLS-1$
			}
			result = page.openEditor(input, desc.getId());
		}
		catch (WorkbenchException e)
		{
		}
		return result;
	}

	/**
	 * Closes the current file
	 * 
	 * @param part
	 */
	public static void closeEditor(IEditorPart part)
	{
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		page.closeEditor(part, false);
	}

	/**
	 * Sets the aptana perspective
	 */
	public static void setAptanaPerspective()
	{
		try
		{
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			PlatformUI.getWorkbench().showPerspective(WebPerspectiveFactory.PERSPECTIVE_ID, window);
		}
		catch (WorkbenchException e)
		{
		}
	}

	/**
	 * Creates a project of the given name.
	 * 
	 * @param name
	 * @return IProject
	 */
	public static IProject createProject(String name)
	{
		IProject result = null;

		IWorkspace wkspc = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = wkspc.getRoot();
		result = root.getProject(name);
		try
		{
			result.create(null);
			result.open(null);
		}
		catch (CoreException e)
		{
		}

		return result;
	}

	/**
	 * Creates a folder of the given name.
	 * 
	 * @param folderName
	 * @param project
	 * @return IFolder
	 */
	public static IFolder createFolder(String folderName, IProject project)
	{
		IFolder jsFolder = project.getFolder(folderName);
		try
		{
			jsFolder.create(false, true, null);
		}
		catch (CoreException e)
		{
		}
		return jsFolder;
	}

	/**
	 * addFileToProject
	 * 
	 * @param path
	 * @param project
	 * @return IFile
	 */
	public static IFile addFileToProject(Path path, IProject project)
	{
		return addFileToProject(path, StringUtils.EMPTY, project);
	}

	/**
	 * addFileToProject
	 * 
	 * @param path
	 * @param folder
	 * @param project
	 * @return IFile
	 */
	public static IFile addFileToProject(Path path, String folder, IProject project)
	{
		String filePath = path.toOSString();
		String fileName = path.segment(path.segmentCount() - 1);
		FileInputStream stream;
		try
		{
			stream = new FileInputStream(filePath);
		}
		catch (FileNotFoundException e)
		{
			IdeLog.logError(EditorsJunitPlugin.getDefault(), "File not found: " + filePath); //$NON-NLS-1$
			return null;
		}

		folder = (folder == null) ? StringUtils.EMPTY : folder;
		if (folder != StringUtils.EMPTY)
		{
			if (!folder.endsWith("/") && !folder.endsWith("\\")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				folder += "/"; //$NON-NLS-1$
			}
		}
		IFile jsFile = project.getFile(folder + fileName);

		try
		{
			jsFile.create(stream, false, null);
			// jsFile.createLink(path, IResource.ALLOW_MISSING_LOCAL, null); // links other project files
			stream.close();
		}
		catch (CoreException e)
		{
		}
		catch (IOException e)
		{
		}

		return jsFile;
	}

	/**
	 * findFileInPlugin
	 * 
	 * @param plugin
	 * @param file
	 * @return Path
	 */
	public static Path findFileInPlugin(String plugin, String file)
	{
		Bundle bundle = Platform.getBundle(plugin);
		URL url = bundle.getEntry(file);
		Path result = null;
		try
		{
			URL localURL = Platform.asLocalURL(url);
			result = new Path(localURL.getPath());
		}
		catch (IOException e)
		{
		}

		return result;
	}
}
