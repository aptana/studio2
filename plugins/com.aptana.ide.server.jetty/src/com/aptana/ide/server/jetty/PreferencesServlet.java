/**
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * This servlet is used to set preferences sent via a POST request in newline seperated name value pairs. This servlet
 * uses the JettyPlugin preference store by default but can be configured to use other stores
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class PreferencesServlet extends HttpServlet
{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private IPreferenceStore prefs;

	/**
	 * Creates a new preferences servlet around the jetty plugin pref store
	 */
	public PreferencesServlet()
	{
		this(JettyPlugin.getDefault().getPreferenceStore());
	}

	/**
	 * Creates a new preferences servlet with a custom preference store
	 * 
	 * @param store
	 */
	public PreferencesServlet(IPreferenceStore store)
	{
		this.prefs = store;
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException
	{
		if (prefs == null)
		{
			return;
		}
		String prefLine = request.getReader().readLine();
		while (prefLine != null)
		{
			int equal = prefLine.indexOf("="); //$NON-NLS-1$
			if (equal != -1 && equal + 1 < prefLine.length())
			{
				String name = prefLine.substring(0, equal);
				String value = prefLine.substring(equal + 1);
				prefs.setValue(name, value);
			}
		}
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if (prefs == null)
		{
			return;
		}
		String pref = request.getParameter("p"); //$NON-NLS-1$
		if (pref != null)
		{
			String value = prefs.getString(pref);
			response.setContentLength(value.length());
			response.getWriter().print(value);
		}
	}

	/**
	 * @return the plugin
	 */
	public IPreferenceStore getPreferences()
	{
		return prefs;
	}

	/**
	 * @param prefs -
	 *            the prefs to use
	 */
	public void setPreferences(IPreferenceStore prefs)
	{
		this.prefs = prefs;
	}

}
