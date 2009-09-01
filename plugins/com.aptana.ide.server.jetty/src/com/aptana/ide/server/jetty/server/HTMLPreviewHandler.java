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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.server.jetty.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.server.http.HttpContentTypes;
import com.aptana.jaxer.connectors.servlet.interfaces.ICallbackResponse;
import com.aptana.jaxer.connectors.servlet.interfaces.IStatisticsHandler;
import com.aptana.jaxer.connectors.servlet.interfaces.IStatisticsProvider;
import com.aptana.jaxer.connectors.servlet.interfaces.IStatusLengthOnlyResponse;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class HTMLPreviewHandler extends HttpServlet implements IStatisticsProvider
{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

	private IStatisticsHandler handler;

	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException
	{
		doGet(request, response);
	}

	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		doGet(req, resp);	
	}

	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		doGet(req, resp);	
	}

	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		doGet(req, resp);	
	}

	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		doGet(req, resp);	
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			if(response instanceof ICallbackResponse)
			{
				response.setContentLength(0);
				response.getWriter().write(""); //$NON-NLS-1$
				return;
			}
			
			boolean sendContent = !(response instanceof IStatusLengthOnlyResponse);
			String path = request.getServletPath();
			String ref = request.getHeader("Referer"); //$NON-NLS-1$
			if (ref != null)
			{
				try
				{
					URL url = new URL(ref);
					String refPath = url.getPath();
					IResource resource = root.findMember(new Path(refPath));
					if (resource != null)
					{
						IProject project = resource.getProject();
						path = HTMLContextRootUtils.resolveURL(project, path);
						IResource candidate = root.findMember(new Path(path));
						if (candidate != null && candidate.getProject().equals(project) && candidate instanceof IFile)
						{
							FileInputStream stream = new FileInputStream(candidate.getLocation().toFile());
							setContentLength(candidate.getLocation().toFile(), response);
							setContentType(path, response);
							if (sendContent)
							{
								FileUtils.pipe(stream, response.getOutputStream(), false);
							}
							if (this.handler != null && !(response instanceof IStatusLengthOnlyResponse))
							{
								this.handler.parseStatistics(request, response);
							}
							stream.close();
						}
						else
						{
							candidate = project.findMember(new Path(path));
							if (candidate != null && candidate.getProject().equals(project)
									&& candidate instanceof IFile)
							{
								FileInputStream stream = new FileInputStream(candidate.getLocation().toFile());
								setContentLength(candidate.getLocation().toFile(), response);
								setContentType(path, response);
								if (sendContent)
								{
									FileUtils.pipe(stream, response.getOutputStream(), false);
								}
								if (this.handler != null && !(response instanceof IStatusLengthOnlyResponse))
								{
									this.handler.parseStatistics(request, response);
								}
								stream.close();
							}
							else
							{
								response.setStatus(HttpServletResponse.SC_NOT_FOUND);
								if (sendContent)
								{
									streamErrorPage(response, path);
								}
							}
						}
						return;
					}
				}
				catch (MalformedURLException e)
				{
				}
			}
			IResource resource = root.findMember(new Path(path));
			if (resource != null && resource instanceof IFile)
			{
				FileInputStream stream = new FileInputStream(resource.getLocation().toFile());
				setContentLength(resource.getLocation().toFile(), response);
				setContentType(path, response);
				if (sendContent)
				{
					FileUtils.pipe(stream, response.getOutputStream(), false);
				}
				stream.close();
				if (this.handler != null && !(response instanceof IStatusLengthOnlyResponse))
				{
					this.handler.parseStatistics(request, response);
				}
			}
			else
			{
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				if (sendContent)
				{
					streamErrorPage(response, path);
				}
			}
		}
		catch (Exception e)
		{
			throw new ServletException(e.getMessage());
		}
	}

	private void streamErrorPage(HttpServletResponse response, String path) throws IOException
	{
		response.getWriter().println("<h1>Page not found: " + path + "</h1>"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Sets the content length on a respone
	 * 
	 * @param file
	 * @param response
	 */
	public static void setContentLength(File file, HttpServletResponse response)
	{
		if (file != null)
		{
			response.setContentLength((int) file.length());
		}
	}

	/**
	 * Sets the content type on a response
	 * 
	 * @param path
	 * @param response
	 */
	public static void setContentType(String path, HttpServletResponse response)
	{
		String ext = FileUtils.getExtension(path);
		if (ext != null)
		{
			String type = HttpContentTypes.getContentType("." + ext); //$NON-NLS-1$
			if (type != null)
			{
				response.setContentType(type);
			}
		}
	}

	/**
	 * @see com.aptana.jaxer.connectors.servlet.interfaces.IStatisticsProvider#getStatisticsHandler()
	 */
	public IStatisticsHandler getStatisticsHandler()
	{
		return this.handler;
	}

	/**
	 * @see com.aptana.jaxer.connectors.servlet.interfaces.IStatisticsProvider#setStatisticsHandler(com.aptana.jaxer.connectors.servlet.interfaces.IStatisticsHandler)
	 */
	public void setStatisticsHandler(IStatisticsHandler handler)
	{
		this.handler = handler;
	}
}
