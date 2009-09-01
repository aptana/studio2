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
package com.aptana.ide.editors.unified;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editors.managers.FileContextManager;
import com.aptana.ide.editors.unified.help.HelpResource;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.IOffsetMapper;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.IParser;

/**
 * @author Robin Debreuil
 */
public class BaseFileLanguageService implements IFileLanguageService
{
	/**
	 * fileService
	 */
	protected FileService fileService;
	
	private IParseState parseState;
	private IParser parser;
	private IOffsetMapper offsetMapper;

	/**
	 * BaseFileLanguageService
	 * 
	 * @param fileService
	 * @param parseState
	 * @param parser
	 * @param mapper
	 */
	public BaseFileLanguageService(FileService fileService, IParseState parseState, IParser parser,
			IParentOffsetMapper mapper)
	{
		this.fileService = fileService;
		this.parseState = parseState;
		this.parser = parser;
	}

	/*
	 * Methods
	 */

	/**
	 * Return the parse state for this language.
	 * 
	 * @return ParseState
	 */
	public IParseState getParseState()
	{
		return parseState;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileLanguageService#getParseState(java.lang.String)
	 */
	public IParseState getParseState(String language)
	{
		return parseState.getParseState(language);
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileLanguageService#getParser()
	 */
	public IParser getParser()
	{
		return parser;
	}

	/**
	 * Gets the language service provider
	 * 
	 * @return Returns the language service provider
	 */
	public IFileService getFileContext()
	{
		return fileService;
	}

	/**
	 * This will return a default offset mapper if not overridden. overrides shouldn't call the base class.
	 * 
	 * @return IOffsetMapper
	 * @see com.aptana.ide.editors.unified.IFileLanguageService#getOffsetMapper()
	 */
	public IOffsetMapper getOffsetMapper()
	{
		return offsetMapper;
	}

	/**
	 * This will return a default offset mapper if not overridden. overrides shouldn't call the base class.
	 * 
	 * @param parent
	 */
	public void createOffsetMapper(IParentOffsetMapper parent)
	{
		offsetMapper = new ChildOffsetMapper(parent);
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileLanguageService#getDocumentationFromLexeme(com.aptana.ide.lexer.Lexeme)
	 */
	public String getDocumentationFromLexeme(Lexeme lexeme)
	{
		return StringUtils.EMPTY;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileLanguageService#getDocumentationTitleFromLexeme(com.aptana.ide.lexer.Lexeme)
	 */
	public String getDocumentationTitleFromLexeme(Lexeme lexeme)
	{
		return lexeme.getText();
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileLanguageService#getDocumentationResourcesFromLexeme(com.aptana.ide.lexer.Lexeme)
	 */
	public HelpResource[] getDocumentationResourcesFromLexeme(Lexeme lexeme)
	{
		return new HelpResource[0];
	}
	
	/**
	 * @see com.aptana.ide.editors.unified.IFileLanguageService#getValidDocumentationLexeme(com.aptana.ide.lexer.Lexeme)
	 */
	public Lexeme getValidDocumentationLexeme(Lexeme lexeme)
	{
		return lexeme;
	}

	/**
	 * @see IFileLanguageService#activateForEditing()
	 */
	public void activateForEditing()
	{
	}

	/**
	 * @see IFileLanguageService#deactivateForEditing()
	 */
	public void deactivateForEditing()
	{
	}

	/**
	 * Perhaps rename to onFileOpen?
	 * 
	 * @param sourceProvider
	 */
	public void connectSourceProvider(IFileSourceProvider sourceProvider)
	{
	}

	/**
	 * Perhaps rename to onFileClose. Languages should clean out their environement, delete the given file, and rebuild
	 * here
	 * 
	 * @param sourceProvider
	 */
	public void disconnectSourceProvider(IFileSourceProvider sourceProvider)
	{
		// Languages should clean out their environement, delete the given file, and rebuild here
		if (this.offsetMapper != null)
		{
			this.offsetMapper.dispose();
		}
		if (this.parseState != null)
		{
			this.parseState.reset();
		}

		this.offsetMapper = null;
		this.parseState = null;
		this.parser = null;
		this.fileService.setPartitions(null);
		this.fileService = null;
		this.reset(false);
	}

	/**
	 * Reconnecting to the environment is occasionally necessary when the environment gets reset and file indexes get
	 * reset this can happen when the profile manager is updated...in that case, all fileIndexes are invalidated, so we
	 * need to reset ours.
	 */
	public void reconnectToEnvironment()
	{
		IParseState ps = getParseState();
		
		if (ps.getFileIndex() == -1)
		{
			// nextFileIndex += 1;
			// ps.setFileIndex(nextFileIndex);
			ps.setFileIndex(FileContextManager.CURRENT_FILE_INDEX);
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileLanguageService#reset(boolean)
	 */
	public void reset(boolean resetFileIndex)
	{
	}

}
