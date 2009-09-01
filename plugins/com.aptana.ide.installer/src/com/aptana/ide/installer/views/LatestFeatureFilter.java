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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.osgi.framework.Version;

import com.aptana.ide.update.manager.IPlugin;

public class LatestFeatureFilter extends ViewerFilter
{

	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		IPlugin ref = (IPlugin) element;
		Collection<IPlugin> matches = findMatching(viewer, ref);
		if (isOnlyMatch(matches))
			return true;
		if (thereIsANewerMatch(ref, matches))
			return false;
		return true;
	}

	private boolean isOnlyMatch(Collection<IPlugin> matches)
	{
		return matches.size() <= 1;
	}

	private boolean thereIsANewerMatch(IPlugin ref, Collection<IPlugin> matches)
	{
		for (IPlugin ref2 : matches)
		{
			if (ref2.equals(ref))
				continue;
			if (Version.parseVersion(ref2.getVersion()).compareTo(Version.parseVersion(ref.getVersion())) > 0)
				return true;

		}
		return false;
	}

	private Collection<IPlugin> findMatching(Viewer viewer, IPlugin ref)
	{
		Collection<IPlugin> matches = new ArrayList<IPlugin>();
		Object input = viewer.getInput();
		if (input instanceof Collection)
		{
			Collection<IPlugin> coll = (Collection<IPlugin>) input;
			for (IPlugin ref2 : coll)
			{
				if (ref.getId().equals(ref2.getId()))
				{
					matches.add(ref2);
				}
			}
		}
		return matches;
	}
}
