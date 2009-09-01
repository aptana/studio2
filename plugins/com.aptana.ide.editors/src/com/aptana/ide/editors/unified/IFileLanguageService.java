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

import com.aptana.ide.editors.unified.help.HelpResource;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.IOffsetMapper;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.IParser;

/**
 * Holds language-specific file items. "IFile" generally means it is a per-file class.
 * 
 * @author Ingo Muschenetz
 */
public interface IFileLanguageService
{
	/**
	 * Called when the associated editor is activated.
	 */
	void activateForEditing();

	/**
	 * connectSourceProvider
	 * 
	 * @param sourceProvider
	 */
	void connectSourceProvider(IFileSourceProvider sourceProvider);

	/**
	 * Called when the associated editor is deactivated.
	 */
	void deactivateForEditing();

	/**
	 * disconnectSourceProvider
	 * 
	 * @param sourceProvider
	 */
	void disconnectSourceProvider(IFileSourceProvider sourceProvider);

	/**
	 * Given a lexeme, returns the documentation for that lexeme based upon the current "environment"
	 * 
	 * @param lexeme
	 *            The lexeme to search documentation for
	 * @return A string representing the code assist documentation
	 */
	String getDocumentationFromLexeme(Lexeme lexeme);

	/**
	 * Reurns a list of documentation links pertinent to the particular lexeme
	 * @param lexeme The lexeme to search documentation for
	 * @return A list of help resources
	 */
	HelpResource[] getDocumentationResourcesFromLexeme(Lexeme lexeme);

	/**
	 * Given a lexeme, returns the documentation title for that lexeme based upon the current "environment"
	 * 
	 * @param lexeme
	 *            The lexeme to search documentation for
	 * @return A string representing the code assist documentation
	 */
	String getDocumentationTitleFromLexeme(Lexeme lexeme);

	/**
	 * getFileContext
	 * 
	 * @return IFileService
	 */
	IFileService getFileContext();

	/**
	 * A language service has an associated "offset mapper" that maps document offsets to lexemes.
	 * 
	 * @return The current associated IOffsetMapper
	 */
	IOffsetMapper getOffsetMapper();

	/**
	 * getParser
	 * 
	 * @return IParser
	 */
	IParser getParser();

	/**
	 * getParseState
	 * 
	 * @return IParseState
	 */
	IParseState getParseState();

	/**
	 * getParseState
	 * 
	 * @param language
	 * @return IParseState
	 */
	IParseState getParseState(String language);

	/**
	 * Given a lexeme, returns the "valid" neighboring lexeme that is useful for documentation. As an example, if doing
	 * document.getElementById(, it will return "getElementById", not "("
	 * 
	 * @param lexeme
	 *            The lexeme to search documentation for
	 * @return A string representing the valid lexeme
	 */
	Lexeme getValidDocumentationLexeme(Lexeme lexeme);

	/**
	 * reconnectToEnvironment
	 */
	void reconnectToEnvironment();

	/**
	 * reset
	 * 
	 * @param resetFileIndex
	 */
	void reset(boolean resetFileIndex);
}
