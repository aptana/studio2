/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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
package com.aptana.ide.installer.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.aptana.ide.update.manager.IPlugin;

/**
 * Filters out the non-installed plug-ins from the installed viewer.
 * 
 * @author Ingo Muschenetz
 */
public class InstalledPluginsViewerFilter extends ViewerFilter
{

	private static final String[] CORE_PLUGINS = { "org.eclipse.rcp", //$NON-NLS-1$
			"org.eclipse.help", //$NON-NLS-1$
			"org.eclipse.platform", //$NON-NLS-1$
			"org.eclipse.eclipsemonkey", //$NON-NLS-1$
			"org.eclipse.equinox.p2.user.ui", //$NON-NLS-1$
			"org.eclipse.sdk", //$NON-NLS-1$
			"com.aptana.ide.feature", //$NON-NLS-1$
			"com.aptana.ide.feature.desktop.integration", //$NON-NLS-1$
			"com.aptana.ide.feature.doc", //$NON-NLS-1$
			"com.aptana.ide.feature.editors", //$NON-NLS-1$
			"com.aptana.ide.feature.syncing.ftp" }; //$NON-NLS-1$

	private static final String[] CORE_PLUGIN_PREFIXES = { "org.eclipse.rcp", //$NON-NLS-1$
			"org.eclipse.platform", "org.eclipse.pde", //$NON-NLS-1$ //$NON-NLS-2$
			"com.aptana.ide.feature.rcp", "org.radrails.rails" }; //$NON-NLS-1$ //$NON-NLS-2$

	private static final String[] INCLUDED_PLUGINS = { "org.radrails.rails_feature" }; //$NON-NLS-1$

	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		if (element instanceof IPlugin)
		{
			IPlugin ref = (IPlugin) element;
			String name = ref.getId();
			if (isIn(name, CORE_PLUGINS)
					|| (matchesPrefix(name, CORE_PLUGIN_PREFIXES) && !isIn(name, INCLUDED_PLUGINS)))
			{
				return false;
			}

		}
		return true;
	}

	private boolean matchesPrefix(String name, String[] prefixes)
	{
		if (prefixes == null)
			return false;
		for (int i = 0; i < prefixes.length; i++)
		{
			if (name.startsWith(prefixes[i]))
				return true;
		}
		return false;
	}

	private boolean isIn(String name, String[] plugins)
	{
		if (plugins == null)
			return false;
		for (int i = 0; i < plugins.length; i++)
		{
			if (name.equals(plugins[i]))
				return true;
		}
		return false;
	}

}
