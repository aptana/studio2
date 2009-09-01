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
 *     Michael Forster - bug 132810
 *******************************************************************************/

package org.eclipse.eclipsemonkey.doms.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

/**
 * Resources
 */
public class Resources {

	/**
	 * standardMarkerName
	 */
	public static final String standardMarkerName = Messages.Resources_Standard_marker_name;
	
	/**
	 * Resources
	 */
	public Resources() {
	}
	
	/**
	 * 
	 * @param patternString
	 * @return Matching files
	 */
	public Object[] filesMatching(String patternString)
	{
		return filesMatching(null, patternString, true);
	}

	public Object[] filesMatchingIgnoreCase(String patternString)
	{
		return filesMatching(null, patternString, false);
	}

	public Object[] filesMatchingForProject(String project, String patternString)
	{
		return filesMatching(project, patternString, true);
	}
	
	public Object[] filesMatchingForProjectIgnoreCase(String project, String patternString)
	{
		return filesMatching(project, patternString, false);
	}
	
	private Object[] filesMatching(String projectName, String patternString, boolean isCaseSensitive) 
	{
		Pattern pattern = null;
		
		if(isCaseSensitive)
		{
			pattern = Pattern.compile(patternString);
		}
		else
		{
			pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
		}
		
		Collection result = new ArrayList();
		
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			
			if(projectName == null)
			{
				IProject[] projects = workspace.getRoot().getProjects();
				for (int i = 0; i < projects.length; i++) {
					IProject project = projects[i];
					walk(project, pattern, result);
				}
			}
			else
			{
				IProject project = workspace.getRoot().getProject(projectName);
				walk(project, pattern, result);
			}
		} catch (CoreException x) {
			// ignore Eclipse internal errors
		}
		Object[] array = new Object[result.size()];
		int i = 0;
		for (Iterator iter = result.iterator(); iter.hasNext();) {
			Object element = iter.next();
			array[i++] = element;
		}
		return array;
	}

	private void walk(IResource resource, Pattern pattern, Collection result)
			throws CoreException {
		if (resource instanceof IFolder) {
			IResource[] children = ((IFolder) resource).members();
			for (int i = 0; i < children.length; i++) {
				IResource resource2 = children[i];
				walk(resource2, pattern, result);
			}
		} else if (resource instanceof IProject) {
			IProject project = (IProject) resource;
			if(!project.isOpen())
				return;
			IResource[] children = project.members();
			for (int i = 0; i < children.length; i++) {
				IResource resource2 = children[i];
				walk(resource2, pattern, result);
			}

		} else if (resource instanceof IFile) {
			String path = resource.getFullPath().toString();
			Matcher match = pattern.matcher(path);
			if (match.matches()) {
				result.add(new File(resource));
			}
		}
	}
}
