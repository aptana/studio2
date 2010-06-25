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
package com.aptana.ide.snippets;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Kevin Lindsey
 */
public class ResourceDeltaVisitor implements IResourceDeltaVisitor
{
	private static final Pattern snippet = Pattern.compile("^/.+/snippets/.+$"); //$NON-NLS-1$

	/**
	 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
	 */
	public boolean visit(IResourceDelta delta) throws CoreException
	{
		String path = delta.getFullPath().toString();
		Matcher m = snippet.matcher(path);
		boolean result = true;

		if (m.matches())
		{
			SnippetsManager snippets = SnippetsManager.getInstance();
			File file = delta.getResource().getLocation().toFile();
			Snippet snippet;

			switch (delta.getKind())
			{
				case IResourceDelta.ADDED:
					snippet = Snippet.fromFile(file);

					if (snippet != null)
					{
						snippets.addSnippet(snippet);
					}
					break;

				case IResourceDelta.REMOVED:
					snippet = snippets.getSnippetByFile(file);

					if (snippet != null)
					{
						snippets.removeSnippet(snippet);
					}
					break;

				case IResourceDelta.CHANGED:
					int flags = delta.getFlags();

					// CHECKSTYLE:OFF
					if ((flags & IResourceDelta.MOVED_FROM) == IResourceDelta.MOVED_FROM)
					{
						// TODO: [KEL] unable to create this event
					}
					if ((flags & IResourceDelta.MOVED_TO) == IResourceDelta.MOVED_TO)
					{
						// TODO: [KEL] unable to create this event
					}
					// CHECKSTYLE:ON
					
					if ((flags & IResourceDelta.REPLACED) == IResourceDelta.REPLACED)
					{
						snippet = snippets.getSnippetByFile(file);

						if (snippet != null)
						{
							snippets.removeSnippet(snippet);
						}

						snippets.addSnippet(Snippet.fromFile(file));
					}
					if ((flags & IResourceDelta.CONTENT) == IResourceDelta.CONTENT)
					{
						snippet = snippets.getSnippetByFile(file);

						if (snippet != null)
						{
							snippets.removeSnippet(snippet);
						}

						snippets.addSnippet(Snippet.fromFile(file));
					}
					break;

				default:
					break;
			}
		}

		return result;
	}
}
