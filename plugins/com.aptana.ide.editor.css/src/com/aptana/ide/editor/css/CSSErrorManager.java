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
package com.aptana.ide.editor.css;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITypedRegion;

import com.aptana.ide.core.PluginUtils;
import com.aptana.ide.editor.css.parsing.CSSMimeType;
import com.aptana.ide.editors.unified.FileService;
import com.aptana.ide.editors.unified.errors.UnifiedErrorManager;

/**
 * @author Robin Debreuil
 * @author Kevin Lindsey
 */
public class CSSErrorManager extends UnifiedErrorManager
{

	/**
	 * CSSErrorManager
	 * 
	 * @param fileService
	 */
	public CSSErrorManager(FileService fileService)
	{
		super(fileService, CSSMimeType.MimeType);
	}

	/**
	 * Returns the preference store
	 * 
	 * @return IPreferenceStore
	 */
	protected IPreferenceStore getPreferenceStore()
	{
		if (PluginUtils.isPluginLoaded(CSSPlugin.getDefault()))
		{
			return CSSPlugin.getDefault().getPreferenceStore();
		}
		else
		{
			return null;
		}
	}

	/**
	 * processLanguagePartition
	 * 
	 * @param partition
	 * @param source
	 * @return String
	 */
	public String processLanguagePartition(ITypedRegion partition, String source)
	{
		int start = partition.getOffset();
		int length = partition.getLength();
		if (start > 0)
		{
			String priorChar = source.substring(start - 1, start - 0);
			if (priorChar.equals("'") || priorChar.equals("\"")) //$NON-NLS-1$ //$NON-NLS-2$
			{
				source = stripChars(source, start, length);
			}
			else
			{
				return source;
			}
		}
		return source;
	}
}
