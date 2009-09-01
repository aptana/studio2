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
package com.aptana.ide.server.jetty;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aptana.ide.core.FileUtils;
import com.aptana.ide.server.http.HttpContentTypes;
import com.aptana.ide.update.FeatureUtil;
import com.aptana.jaxer.connectors.servlet.interfaces.ICallbackResponse;
import com.aptana.jaxer.connectors.servlet.interfaces.IStatusLengthOnlyResponse;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class ResourceBaseServlet extends HttpServlet
{

	/**
	 * CACHE_CONTROL
	 */
	public static final String CACHE_CONTROL = "Cache-Control"; //$NON-NLS-1$

	/**
	 * PRAGMA
	 */
	public static final String PRAGMA = "Pragma"; //$NON-NLS-1$

	/**
	 * NO_CACHE
	 */
	public static final String NO_CACHE = "no-cache"; //$NON-NLS-1$

	/**
	 * NO_STORE
	 */
	public static final String NO_STORE = "no-store"; //$NON-NLS-1$

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private String resourceBase;
	private boolean noCache = false;

	/**
	 * Creates a new jaxer preview servlet
	 * 
	 * @param resourceBase
	 */
	public ResourceBaseServlet(String resourceBase)
	{
		this.resourceBase = resourceBase;
	}

	/**
	 * Creates a resource base servlet without no current path to serve
	 */
	public ResourceBaseServlet()
	{
		this(null);
	}

	/**
	 * Gets the file path to use for this request relative to the resource base.
	 * 
	 * @param request
	 * @return - relative file path rooted on the resource base path
	 */
	protected String getFilePath(HttpServletRequest request)
	{
		return request.getServletPath();
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
			String path = getFilePath(request);
			File file = new File(resourceBase, path);
			// HACK for STU-3219 - Remove it when we handle separately installed Jaxer in a better way in the My Aptana.
			if (file.getName().endsWith("jaxerservercontroller.js") && //$NON-NLS-1$
					(!file.exists()) &&
					(!FeatureUtil.isInstalled("com.aptana.ide.feature.framework.jaxer"))) { //$NON-NLS-1$
				response.setContentLength(0);
				response.getWriter().write(""); //$NON-NLS-1$
				return;
			}
			setContentLength(file, response);
			setContentType(path, response);
			setCacheHeaders(response);
			if (sendContent)
			{
				FileInputStream fis = new FileInputStream(file);
				FileUtils.pipe(fis, response.getOutputStream(), false);
				fis.close();
			}
		}
		catch (Exception e)
		{
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException
	{
		doGet(arg0, arg1);
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

	private void setCacheHeaders(HttpServletResponse response)
	{
		if (noCache)
		{
			response.addHeader(CACHE_CONTROL, NO_CACHE);
			response.addHeader(CACHE_CONTROL, NO_STORE);
			response.addHeader(PRAGMA, NO_CACHE);
		}
	}

	/**
	 * @return the resourceBase
	 */
	public String getResourceBase()
	{
		return resourceBase;
	}

	/**
	 * @param resourceBase
	 *            the resourceBase to set
	 */
	public void setResourceBase(String resourceBase)
	{
		this.resourceBase = resourceBase;
	}

	/**
	 * @return the setNocache
	 */
	public boolean isNocache()
	{
		return noCache;
	}

	/**
	 * @param noCache
	 *            the noCache to set
	 */
	public void setNoCache(boolean noCache)
	{
		this.noCache = noCache;
	}

}
