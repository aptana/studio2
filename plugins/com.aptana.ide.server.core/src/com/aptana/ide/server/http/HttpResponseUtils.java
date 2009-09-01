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
package com.aptana.ide.server.http;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

/**
 * @author Kevin Lindsey
 */
public final class HttpResponseUtils
{
	/**
	 * HttpResponseUtils
	 */
	private HttpResponseUtils()
	{
	}
	
	/**
	 * Generates a message telling the user that they request a file that is not accessible based on the current project
	 * reference configuration.
	 * 
	 * @param filePath
	 * @param unreferencedProjects
	 *            the unreferenced projects
	 * @param currentProject
	 * @return String
	 */
	static String createFileNotAccessibleMessage(IPath filePath, Set unreferencedProjects, IProject currentProject)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		
		pw.println("<H2>File is not accessible from the current project</h2>"); //$NON-NLS-1$
		pw.println("<p>The requested file was found in another workspace project that is not currently referenced this project."); //$NON-NLS-1$
		pw.println("You need to update the project's reference settings if you would like to access this file</p>"); //$NON-NLS-1$
		pw.println("<br><b>Requested file: </b>" + filePath.toOSString()); //$NON-NLS-1$
		pw.println("<br><b>Current startup project: </b>" + currentProject.getName()); //$NON-NLS-1$
		pw.println("<p><b>Unreferenced projects containing file: </b></p>"); //$NON-NLS-1$
		pw.println("<ul>"); //$NON-NLS-1$

		// Arrays.s
		String[] projectNames = (String[]) unreferencedProjects.toArray(new String[0]);
		Arrays.sort(projectNames);
		for (int i = 0; i < projectNames.length; i++)
		{
			pw.println("<li>" + projectNames[i] + "</li>"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		pw.println("</ul>"); //$NON-NLS-1$
		pw.close();
		
		return sw.toString();
	}

	/**
	 * createBrowseFolderHTML
	 * 
	 * @param parentFolder
	 * @param fileNames
	 * @param folderNames
	 * @return String
	 */
	static String createBrowseFolderHTML(IPath parentFolder, String[] fileNames, String[] folderNames)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		
		pw.println("<h2>" + parentFolder.toPortableString() + "</h2>"); //$NON-NLS-1$ //$NON-NLS-2$
		pw.println("<h5>folders</h5>"); //$NON-NLS-1$
		pw.println("<ul>"); //$NON-NLS-1$
		for (int i = 0; i < folderNames.length; i++)
		{
			pw.println("<li><a href='" + parentFolder.toPortableString() + folderNames[i] + "'>" + folderNames[i] //$NON-NLS-1$ //$NON-NLS-2$
					+ "</a></li>"); //$NON-NLS-1$
		}
		pw.println("</ul>"); //$NON-NLS-1$

		pw.println("<h5>files</h5>"); //$NON-NLS-1$
		pw.println("<ul>"); //$NON-NLS-1$
		for (int i = 0; i < fileNames.length; i++)
		{
			pw.println("<li><a href='" + parentFolder.toPortableString() + fileNames[i] + "'>" + fileNames[i] //$NON-NLS-1$ //$NON-NLS-2$
					+ "</a></li>"); //$NON-NLS-1$
		}
		pw.println("</ul>"); //$NON-NLS-1$
		pw.close();
		
		return sw.toString();
	}
}
