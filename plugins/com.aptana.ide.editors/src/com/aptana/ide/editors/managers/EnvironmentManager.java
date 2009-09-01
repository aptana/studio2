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
package com.aptana.ide.editors.managers;

import java.util.HashMap;
import java.util.Map;

import com.aptana.ide.metadata.IDocumentation;
import com.aptana.ide.parsing.IRuntimeEnvironment;

/**
 * @author Paul Colton
 */
public abstract class EnvironmentManager
{
	private static Map<String, IRuntimeEnvironment> mapper = new HashMap<String, IRuntimeEnvironment>();
	private static Map<String, IDocumentation> documentationIdMap = new HashMap<String, IDocumentation>(); // used to store and match the @id external docs
	private static Map documentationIdRefMap = new HashMap(); // and refs to these to enable removing

	/**
	 * Protected constructor for utility class
	 */
	protected EnvironmentManager()
	{

	}

	/**
	 * getEnvironment
	 * 
	 * @param mimeType
	 * @return IRuntimeEnvironment
	 */
	public static IRuntimeEnvironment getEnvironment(String mimeType)
	{
		if (mapper.containsKey(mimeType))
		{
			return mapper.get(mimeType);
		}
		else
		{
			return null;
		}
	}

	/**
	 * addEnvironmentMapping
	 * 
	 * @param mimeType
	 * @param env
	 */
	public static void addEnvironmentMapping(String mimeType, IRuntimeEnvironment env)
	{
		mapper.put(mimeType, env);
	}

	/**
	 * @return Returns the documentationIdMap, this is used to store and match the
	 * @id external docs.
	 */
	public static Map<String, IDocumentation> getDocumentationIdMap()
	{
		return documentationIdMap;
	}

	/**
	 * @return Returns the documentationIdMap, this is used to store and match the
	 * @id external docs.
	 */
	public static Map getDocumentationRefMap()
	{
		return documentationIdRefMap;
	}
}
