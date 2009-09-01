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
package com.aptana.ide.editor.html;

import java.util.Hashtable;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.html.parsing.HTMLMimeType;
import com.aptana.ide.editor.html.parsing.HTMLParseState;
import com.aptana.ide.editor.html.parsing.nodes.HTMLElementNode;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.IRuntimeEnvironment;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Robin Debreuil
 */
public class HTMLEnvironmentLoader {
	IRuntimeEnvironment env;
	int workingFileIndex;
	
	/**
	 * HTMLEnvironmentLoader
	 * 
	 * @param env
	 */
	public HTMLEnvironmentLoader(IRuntimeEnvironment env)
	{
		this.env = env;
	}
	
	/**
	 * reloadEnvironment
	 * 
	 * @param parseState
	 * @param lexemeList
	 * @param fileIndex
	 */
	public void reloadEnvironment(IParseState parseState, LexemeList lexemeList, int fileIndex) {
		synchronized(env)
		{
			try{
				unloadEnvironment(fileIndex);
				loadEnvironment(parseState, lexemeList, fileIndex);
			}
			catch(Exception e)
			{
				IdeLog.logError(UnifiedEditorsPlugin.getDefault(), StringUtils.format(Messages.HTMLEnvironmentLoader_ErrorReloadingFile, fileIndex), e);
			}
		}
	}
	
	/**
	 * Clear the current environment
	 * @param fileIndex 
	 */ 
	public void unloadEnvironment(int fileIndex)
	{
		synchronized(env)
		{
			env.removeFileIds(fileIndex);			
		}
	}
	
	private void loadEnvironment(IParseState parseState, LexemeList lexemeList, int fileIndex)
	{
		try
		{
			HTMLParseState ps = (HTMLParseState)parseState.getParseState(HTMLMimeType.MimeType);
			IParseNode parseNode = ps.getParseResults();
			if (parseNode != null)
				walkTree(parseNode.getChildren(), fileIndex, new Hashtable());
		}
		catch(Exception e)
		{
			IdeLog.logError(HTMLPlugin.getDefault(), StringUtils.format(Messages.HTMLEnvironmentLoader_ErrorLoadingFile, fileIndex), e);
		}
	}

	/**
	 * Walks the parse tree, trying to find any id attributes in the document
	 * @param parseNodes
	 * @param fileIndex
	 * @param items
	 */
	private void walkTree(IParseNode[] parseNodes, int fileIndex, Hashtable items) {
		for(int i = 0; i < parseNodes.length; i++)
		{
			IParseNode pn = parseNodes[i];
			
			if(pn instanceof HTMLElementNode)
			{
				HTMLElementNode hn = (HTMLElementNode)pn;
				String id = hn.getID();
				if(id != null)
				{
					env.addId(id, fileIndex, hn);
				}
				String cssClass = hn.getCSSClass();
				if(cssClass != null)
				{
					env.addClass(cssClass, fileIndex, hn);
				}
			}
			
			if(pn.getChildCount() > 0)
			{
				walkTree(pn.getChildren(), fileIndex, items);
			}
		}
	}			
}
