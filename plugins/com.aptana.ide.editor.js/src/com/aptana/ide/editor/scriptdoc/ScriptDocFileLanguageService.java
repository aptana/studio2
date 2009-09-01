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

import com.aptana.ide.editor.scriptdoc.lexing.ScriptDocTokenTypes;
import com.aptana.ide.editor.scriptdoc.parsing.ScriptDocMimeType;
import com.aptana.ide.editors.unified.BaseFileLanguageService;
import com.aptana.ide.editors.unified.FileService;
import com.aptana.ide.editors.unified.IFileService;
import com.aptana.ide.editors.unified.IParentOffsetMapper;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.IOffsetMapper;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.IParser;

/**
 * @author Robin Debreuil
 */
public class ScriptDocFileLanguageService extends BaseFileLanguageService
{	
	private ScriptDocLanguageEnvironment languageEnvironment;
	private boolean isProfileMember;
	private ScriptDocOffsetMapper offsetMapper;

	//private ScriptDocOffsetMapper offsetMapper;
	
	/**
	 * ScriptDocFileLanguageService
	 * 
	 * @param fileService
	 * @param parseState
	 * @param parser
	 * @param mapper
	 */
	public ScriptDocFileLanguageService(
			FileService fileService, IParseState parseState, IParser parser, IParentOffsetMapper mapper)
	{
		super(fileService, parseState, parser, mapper);
		this.languageEnvironment = ScriptDocLanguageEnvironment.getInstance();
		
		createOffsetMapper(mapper);
	}
	
	/**
	 * Given a lexeme, returns the documentation for that lexeme based upon the current 
	 * "environment"
	 * @param lexeme The lexeme to search documentation for
	 * @return A string representing the code assist documentation
	 */
	public String getDocumentationFromLexeme(Lexeme lexeme) 
	{
		String result = null;
		switch(lexeme.typeIndex)
		{
			case ScriptDocTokenTypes.AUTHOR:
				result = Messages.ScriptDocFileLanguageService_Author;
				break;
			case ScriptDocTokenTypes.CLASS_DESCRIPTION:
				result = Messages.ScriptDocFileLanguageService_ClassDescription;
				break;
			case ScriptDocTokenTypes.CONSTRUCTOR:
				result = Messages.ScriptDocFileLanguageService_Constructor;
				break;
			case ScriptDocTokenTypes.DEPRECATED:
				result = Messages.ScriptDocFileLanguageService_Deprecated;
				break;
				
			// @example
				
			case ScriptDocTokenTypes.EXCEPTION:
				result = Messages.ScriptDocFileLanguageService_Exception;
				break;
			case ScriptDocTokenTypes.INTERNAL:
				result = Messages.ScriptDocFileLanguageService_Internal;
				break;
			case ScriptDocTokenTypes.MEMBER_OF:
				result = Messages.ScriptDocFileLanguageService_MemberOf;
				break;
			case ScriptDocTokenTypes.METHOD:
				result = Messages.ScriptDocFileLanguageService_Method;
				break;
			case ScriptDocTokenTypes.NATIVE:
				result = Messages.ScriptDocFileLanguageService_Native;
				break;
			case ScriptDocTokenTypes.PARAM:
				result = Messages.ScriptDocFileLanguageService_Param;
				break;
			case ScriptDocTokenTypes.PRIVATE:
				result = Messages.ScriptDocFileLanguageService_Private;
				break;
			case ScriptDocTokenTypes.PROJECT_DESCRIPTION:
				result = Messages.ScriptDocFileLanguageService_ProjectDescription;
				break;
			case ScriptDocTokenTypes.PROPERTY:
				result = Messages.ScriptDocFileLanguageService_Property;
				break;
			case ScriptDocTokenTypes.RETURN:
				result = Messages.ScriptDocFileLanguageService_Return;
				break;
			case ScriptDocTokenTypes.SEE:
				result = Messages.ScriptDocFileLanguageService_See;
				break;
			case ScriptDocTokenTypes.SINCE:
				result = Messages.ScriptDocFileLanguageService_Since;
				break;
			case ScriptDocTokenTypes.TYPE:
				result = Messages.ScriptDocFileLanguageService_Type;
				break;
			case ScriptDocTokenTypes.VERSION:
				result = Messages.ScriptDocFileLanguageService_Version;
				break;
			default:
				break;
		}
		return result;
	}
	
	/**
	 * Returns the current language environment
	 * @return The langauge environment 
	 */
	public ScriptDocLanguageEnvironment getLanguageEnvironment()
	{
		return languageEnvironment;
	}
	
	/**
	 * 
	 * @return DefaultLanguage
	 */
	public String getDefaultLanguage()
	{
		return ScriptDocMimeType.MimeType;
	}
	
	/**
	 * @see com.aptana.ide.editors.unified.IFileLanguageService#getParseState()
	 */
	public IParseState getParseState()
	{
		return getParseState(ScriptDocMimeType.MimeType);
	}
	
	/**
	 * getScriptDocFileLanguageService
	 * 
	 * @param fileContext
	 * @return ScriptDocFileLanguageService
	 */
	public static ScriptDocFileLanguageService getScriptDocFileLanguageService(IFileService fileContext){
		ScriptDocFileLanguageService languageService = (ScriptDocFileLanguageService)fileContext.getLanguageService(ScriptDocMimeType.MimeType);
		if(languageService == null)
		{
			throw new IllegalStateException(Messages.ScriptDocFileLanguageService_NoScriptDocLanguageService);
		}
		return languageService;
	}

	/**
	 * isProfileMember
	 *
	 * @return boolean
	 */
	public boolean isProfileMember() 
	{
		return this.isProfileMember;
	}
	
	/**
	 * setProfileMember
	 *
	 * @param isProfileMember
	 */
	public void setProfileMember(boolean isProfileMember) 
	{
		this.isProfileMember = isProfileMember;
	}
	/**
	 * @see com.aptana.ide.editors.unified.IFileLanguageService#reset(boolean)
	 */
	public void reset(boolean resetFileIndex)
	{
	}
	
	/**
	 * @see com.aptana.ide.editors.unified.IFileLanguageService#getOffsetMapper()
	 */
	public IOffsetMapper getOffsetMapper()
	{
		return offsetMapper;
	}
	/**
	 * This will return a default offset mapper if not overridden. overrides shouldn't call the base class.
	 * @param parent 
	 */
	public void createOffsetMapper(IParentOffsetMapper parent) 
	{
		offsetMapper = new ScriptDocOffsetMapper(parent);
	}
}
