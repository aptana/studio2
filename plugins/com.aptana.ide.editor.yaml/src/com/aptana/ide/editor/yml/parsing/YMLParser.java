/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL youelect, is prohibited.
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
package com.aptana.ide.editor.yml.parsing;

import java.text.ParseException;

import com.aptana.ide.editor.yml.YMLMimeType;
import com.aptana.ide.editor.yml.lexing.YMLTokenList;
import com.aptana.ide.editor.yml.lexing.YMLTokenTypes;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.editors.unified.parsing.UnifiedParser;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.ITokenList;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.ParserInitializationException;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * YMLParser
 * 
 * @author Kevin Sawicki
 */
public class YMLParser extends UnifiedParser
{

	/**
	 * YMLParser constructor
	 * 
	 * @throws ParserInitializationException
	 */
	public YMLParser() throws ParserInitializationException
	{
		super(YMLMimeType.MimeType);
	}

	/**
	 * @see com.aptana.ide.parsing.AbstractParser#createLanguageTokenList()
	 */
	protected ITokenList createLanguageTokenList()
	{
		return LanguageRegistry.getTokenList(this.getLanguage());
	}

	/**
	 * @see com.aptana.ide.parsing.AbstractParser#createParseState(com.aptana.ide.parsing.IParseState)
	 */
	public IParseState createParseState(IParseState parent)
	{
		IParseState result;

		if (parent == null)
		{
			result = new YMLParseState();
		}
		else
		{
			result = new YMLParseState(parent);
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.AbstractParser#initializeLexer()
	 */
	public void initializeLexer() throws LexerException
	{
		ILexer lexer = this.getLexer();
		String language = this.getLanguage();

		// ignore whitespace
		lexer.setIgnoreSet(language, new int[] { YMLTokenTypes.WHITESPACE });
		lexer.setLanguageAndGroup(language, YMLTokenList.DEFAULT_GROUP);
	}

	/**
	 * @see com.aptana.ide.parsing.AbstractParser#parseAll(com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public void parseAll(IParseNode parentNode) throws ParseException, LexerException
	{
		ILexer lexer = this.getLexer();
		lexer.setLanguageAndGroup(this.getLanguage(), "default"); //$NON-NLS-1$

		this.advance();

		while (this.isEOS() == false)
		{
			this.advance();
		}
	}

}
