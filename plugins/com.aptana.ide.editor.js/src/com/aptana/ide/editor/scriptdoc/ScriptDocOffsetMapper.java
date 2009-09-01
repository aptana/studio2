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
package com.aptana.ide.editor.scriptdoc;

import java.util.Map;

import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.editor.js.JSLanguageEnvironment;
import com.aptana.ide.editor.js.JSOffsetMapper;
import com.aptana.ide.editor.js.runtime.Environment;
import com.aptana.ide.editor.js.runtime.IObject;
import com.aptana.ide.editor.js.runtime.JSUndefined;
import com.aptana.ide.editor.js.runtime.Property;
import com.aptana.ide.editor.scriptdoc.lexing.ScriptDocTokenTypes;
import com.aptana.ide.editor.scriptdoc.parsing.ScriptDocMimeType;
import com.aptana.ide.editor.scriptdoc.parsing.ScriptDocParseState;
import com.aptana.ide.editors.managers.EnvironmentManager;
import com.aptana.ide.editors.managers.FileContextManager;
import com.aptana.ide.editors.unified.ChildOffsetMapper;
import com.aptana.ide.editors.unified.IChildOffsetMapper;
import com.aptana.ide.editors.unified.IParentOffsetMapper;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.metadata.IDocumentation;
import com.aptana.ide.metadata.IDocumentationStore;
import com.aptana.ide.parsing.CodeLocation;
import com.aptana.ide.parsing.ICodeLocation;

/**
 * 
 */
public class ScriptDocOffsetMapper extends ChildOffsetMapper implements IChildOffsetMapper
{

	/**
	 * @param parent
	 */
	public ScriptDocOffsetMapper(IParentOffsetMapper parent)
	{
		super(parent);
	}

	/**
	 * @see com.aptana.ide.editors.unified.ChildOffsetMapper#findTarget(com.aptana.ide.lexer.Lexeme)
	 */
	public ICodeLocation findTarget(Lexeme lexeme)
	{
		ICodeLocation codeLoc = getIDTargetFromOffset(lexeme);
		// need source to find the @id location
		//this.getFileService().getParseState().getSource()
		return codeLoc;
	}
	/**
	 * This only works for @id name.name tags
	 * @return Returns the @id identifier
	 */
	private ICodeLocation getIDTargetFromOffset(Lexeme lexeme)
	{
		ICodeLocation result = null;
		ScriptDocParseState ps = (ScriptDocParseState)this.getFileService().getParseState().getParseState(ScriptDocMimeType.MimeType);
		Lexeme lastLexeme = getLastDocLexeme(ps, lexeme);
		int offset = lastLexeme.getEndingOffset(); 
		if(offset == -1)
		{
			return null;
		}
		
		String type = getTypeName(ps, lexeme);
		if(!type.equals("")) //$NON-NLS-1$
		{
			result = getType(type, lexeme);
		}
		else
		{
			result = getID(ps, lexeme, offset);
		}		
		return result;
	}
	
	private ICodeLocation getType(String fullName, Lexeme lexeme)
	{
		ICodeLocation result = null;
		Environment jsEnv = (Environment)JSLanguageEnvironment.getInstance().getRuntimeEnvironment();
		IObject global = jsEnv.getGlobal();
		
		Property prop = global.getProperty(fullName);
		if(prop == null)
		{
			return null;
		}

		IObject obj = prop.getValue(FileContextManager.CURRENT_FILE_INDEX, Integer.MAX_VALUE);
		if(obj == JSUndefined.getSingletonInstance())
		{
			return null;
		}
		
		result = JSOffsetMapper.findTargetFromName(obj, prop);
		
		return result;
	}
	
	private ICodeLocation getID(ScriptDocParseState ps, Lexeme lexeme, int offset)
	{		
		ICodeLocation result = null; 
		IDocumentationStore store = ps.getDocumentationStore();
		//int offset = lexeme.getStartingOffset();
		IDocumentation doc = store.getDocumentationFromOffset(offset);
		String[] ids = doc.getIDs();
		if(doc != null && ids.length > 0)
		{
			String id = ids[0]; // todo: find codeLoc closest to the passed lexeme 
			
			Map<String, IDocumentation> idMap = EnvironmentManager.getDocumentationIdMap();
			if(idMap.containsKey(id))
			{
				IDocumentation mapDoc = idMap.get(id);
				CodeLocation[] locs = mapDoc.getID(id);
				for (int i = 0; i < locs.length; i++)
				{
					CodeLocation location = locs[i];
					
					// TODO: the fullPath of CodeLocation should use URIs
					String locationURI = CoreUIUtils.getURI(location.getFullPath());
					String psURI = FileContextManager.getURIFromFileIndex(ps.getFileIndex());
					
					// filter out the current script doc comment
					if((location.getStartLexeme().getEndingOffset() != lexeme.getEndingOffset()) ||
						!locationURI.equals(psURI))
					{
						result = location;
						break;
					}
				}
			}
		}
		return result;
	}
	
	
	private String getTypeName(ScriptDocParseState ps, Lexeme lexeme)
	{
		String result = ""; //$NON-NLS-1$

		Lexeme orgLexeme = lexeme;
		LexemeList ll = ps.getLexemeList();
		int index = ll.getLexemeIndex(lexeme);

		while(lexeme != null && lexeme.getLanguage().equals(ScriptDocMimeType.MimeType))
		{
			if(	lexeme.typeIndex == ScriptDocTokenTypes.COMMA || 
				lexeme.typeIndex == ScriptDocTokenTypes.IDENTIFIER || 
				lexeme.typeIndex == ScriptDocTokenTypes.ELLIPSIS
				)
			{
				// keep looking for brace
				index = (index == 0) ? 0 : index;
			}
			else if(lexeme.typeIndex == ScriptDocTokenTypes.LCURLY)
			{
				result = orgLexeme.getText();
				break;
			}
			else
			{
				break;
			}
			
			if(index > 0)
			{
				index--;
				lexeme = ll.get(index);
			}
			else
			{
				break;
			}
		}
		return result;
	}

	private Lexeme getLastDocLexeme(ScriptDocParseState ps, Lexeme lexeme)
	{
		Lexeme result = null;
		
		LexemeList ll = ps.getLexemeList();
		int index = ll.getLexemeIndex(lexeme);
		while(lexeme != null && lexeme.getLanguage().equals(ScriptDocMimeType.MimeType))
		{
			if(lexeme.typeIndex == ScriptDocTokenTypes.END_DOCUMENTATION)
			{
				result = lexeme;
				break;
			}
			if(index < ll.size())
			{
				index++;
				lexeme = ll.get(index);
			}
			else
			{
				break;
			}
		}
		return result;
	}

}






