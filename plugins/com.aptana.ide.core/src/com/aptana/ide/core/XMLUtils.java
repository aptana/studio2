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
package com.aptana.ide.core;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kevin Lindsey
 */
public class XMLUtils
{
	private static final Pattern SPECIAL_CHARS = Pattern.compile("[<>&'\"]"); //$NON-NLS-1$
	private static final Map<String,String> ENTITIES = new HashMap<String,String>();
	
	/**
	 * static constructor
	 */
	static
	{
		ENTITIES.put("<", "&lt;"); //$NON-NLS-1$ //$NON-NLS-2$
		ENTITIES.put(">", "&gt;"); //$NON-NLS-1$ //$NON-NLS-2$
		ENTITIES.put("&", "&amp;"); //$NON-NLS-1$ //$NON-NLS-2$
		ENTITIES.put("'", "&apos;"); //$NON-NLS-1$ //$NON-NLS-2$
		ENTITIES.put("\"", "&quot;"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * XMLUtils
	 */
	protected XMLUtils()
	{
	}

	/**
	 * entitize
	 * 
	 * @param source
	 * @return
	 */
	public static String entitize(String source)
	{
		String result = null;
		
		if (source != null)
		{
			Matcher m = SPECIAL_CHARS.matcher(source);
			StringBuffer sb = new StringBuffer();
			
			while (m.find())
			{
				m.appendReplacement(sb, ENTITIES.get(m.group()));
			}
			
			m.appendTail(sb);
			
			result = sb.toString();
		}
		
		return result;
	}
}
