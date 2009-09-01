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
package com.aptana.ide.editor.json.parsing;

import java.text.ParseException;

import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.ide.editor.json.lexing.JSONTokenTypes;
import com.aptana.ide.editor.json.parsing.nodes.JSONParseNode;
import com.aptana.ide.editor.json.parsing.nodes.JSONParseNodeTypes;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.folding.FoldingExtensionPointLoader;
import com.aptana.ide.editors.unified.parsing.UnifiedParser;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.TokenCategories;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.ParserInitializationException;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.parsing.nodes.IParseNodeFactory;

/**
 * @author Kevin Lindsey
 */
public class JSONParser extends UnifiedParser
{
	/**
	 * JSONParser
	 * 
	 * @throws ParserInitializationException
	 */
	public JSONParser() throws ParserInitializationException
	{
		this(JSONMimeType.MimeType);
	}
	
	/**
	 * JSONParser
	 * 
	 * @param mimeType
	 * @throws ParserInitializationException
	 */
	public JSONParser(String mimeType) throws ParserInitializationException
	{
		super(mimeType);
		
		IPreferenceStore unifiedStore = UnifiedEditorsPlugin.getDefault().getPreferenceStore();
		unifiedStore.setDefault(FoldingExtensionPointLoader.createEnablePreferenceId(JSONMimeType.MimeType), true);
	}

	/**
	 * @see com.aptana.ide.parsing.AbstractParser#advance()
	 */
	protected void advance() throws LexerException
	{
		super.advance();

		while (this.isEOS() == false && this.currentLexeme.getCategoryIndex() == TokenCategories.WHITESPACE)
		{
			super.advance();
		}
	}

	/**
	 * createNode
	 * 
	 * @param type
	 * @param startingLexeme
	 * @return JSParseNode
	 */
	private JSONParseNode createNode(int type, Lexeme startingLexeme)
	{
		IParseNodeFactory factory = this.getParseNodeFactory();
		JSONParseNode result = null;

		if (factory != null)
		{
			result = (JSONParseNode) factory.createParseNode(type, startingLexeme);
		}
		else
		{
			// we need to return something to prevent NPE's
			result = new JSONParseNode(type, startingLexeme);
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.AbstractParser#createParseState(com.aptana.ide.parsing.IParseState)
	 */
	public IParseState createParseState(IParseState parent)
	{
		IParseState result;

		if (parent == null)
		{
			result = new JSONParseState();
		}
		else
		{
			result = new JSONParseState(parent);
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

		lexer.setIgnoreSet(language, new int[] { JSONTokenTypes.WHITESPACE });
		lexer.setLanguageAndGroup(language, "default"); //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.parsing.AbstractParser#parseAll(com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public void parseAll(IParseNode parentNode) throws ParseException, LexerException
	{
		// set node to be used as the root node for the results of this parse
		IParseNode rootNode;

		if (parentNode == null)
		{
			IParseNodeFactory nodeFactory = this.getParseNodeFactory();

			if (nodeFactory != null)
			{
				rootNode = nodeFactory.createRootNode();

				if (parentNode != null)
				{
					parentNode.appendChild(rootNode);
				}
			}
			else
			{
				rootNode = null;
			}
		}
		else
		{
			rootNode = parentNode;
		}

		ILexer lexer = this.getLexer();
		lexer.setLanguageAndGroup(this.getLanguage(), "default"); //$NON-NLS-1$

		this.advance();

		while (this.isEOS() == false)
		{
			IParseNode result = this.parseStatement();

			if (rootNode != null && result != null)
			{
				rootNode.appendChild(result);
			}
		}
	}

	/**
	 * parseStatement
	 * 
	 * @return IParseNode
	 * @throws LexerException
	 */
	private IParseNode parseStatement() throws LexerException
	{
		IParseNode result = null;

		switch (this.currentLexeme.typeIndex)
		{
			case JSONTokenTypes.LBRACKET:
				result = this.parseArray();
				break;

			case JSONTokenTypes.LCURLY:
				result = this.parseObject();
				break;

			case JSONTokenTypes.FALSE:
			case JSONTokenTypes.TRUE:
			case JSONTokenTypes.NUMBER:
			case JSONTokenTypes.STRING:
			case JSONTokenTypes.NULL:
			case JSONTokenTypes.REFERENCE:
				result = this.createNode(JSONParseNodeTypes.SCALAR, this.currentLexeme);
				this.advance();
				break;
				
			default:
				advance();
		}

		return result;
	}

	/**
	 * parseArray
	 * 
	 * @return parse node
	 * @throws LexerException
	 */
	private IParseNode parseArray() throws LexerException
	{
		IParseNode result = this.createNode(JSONParseNodeTypes.ARRAY, this.currentLexeme);

		// assuming '[' and advancing over (not asserting to make things a bit easier)
		this.advance();

		while (this.isEOS() == false && this.currentLexeme.typeIndex != JSONTokenTypes.RBRACKET)
		{
			IParseNode child = this.parseStatement();

			if (child != null)
			{
				result.appendChild(child);
			}
			else
			{
				this.recover(JSONTokenTypes.RBRACKET);
			}

			if (this.currentLexeme.typeIndex == JSONTokenTypes.COMMA)
			{
				this.advance();
			}
		}

		if (this.currentLexeme.typeIndex == JSONTokenTypes.RBRACKET)
		{
			result.setEndingLexeme(this.currentLexeme);
			this.advance();
		}

		return result;
	}

	/**
	 * parseObject
	 * 
	 * @return parse node
	 * @throws LexerException
	 */
	private IParseNode parseObject() throws LexerException
	{
		IParseNode result = this.createNode(JSONParseNodeTypes.OBJECT, this.currentLexeme);

		// assuming '{' and advancing over (not asserting to make things a bit easier)
		this.advance();

		while (this.isEOS() == false && this.currentLexeme.typeIndex != JSONTokenTypes.RCURLY)
		{
			IParseNode child = null;
			
			try
			{
				child = this.parseNameValuePair();
			}
			catch (ParseException pe)
			{
			}

			if (child != null)
			{
				result.appendChild(child);
			}
			else
			{
				this.recover(JSONTokenTypes.RCURLY);
			}

			if (this.currentLexeme.typeIndex == JSONTokenTypes.COMMA)
			{
				this.advance();
			}
		}

		if (this.currentLexeme.typeIndex == JSONTokenTypes.RCURLY)
		{
			result.setEndingLexeme(this.currentLexeme);
			this.advance();
		}

		return result;
	}

	/**
	 * parseNameValuePair
	 * 
	 * @return parse node
	 * @throws LexerException
	 * @throws ParseException
	 */
	private IParseNode parseNameValuePair() throws LexerException, ParseException
	{
		IParseNode result = this.createNode(JSONParseNodeTypes.NAME_VALUE_PAIR, this.currentLexeme);

		switch (this.currentLexeme.typeIndex)
		{
			case JSONTokenTypes.STRING:
			case JSONTokenTypes.PROPERTY:
				IParseNode name = this.createNode(JSONParseNodeTypes.SCALAR, this.currentLexeme);

				result.appendChild(name);

				this.advance();

				this.assertAndAdvance(JSONTokenTypes.COLON, Messages.getString("JSONParser.Missing_Colon")); //$NON-NLS-1$

				IParseNode value = this.parseStatement();

				if (value != null)
				{
					result.appendChild(value);
				}
				else
				{
					// report failure
					result = null;
				}
				break;

			default:
				// report failure
				result = null;
		}

		return result;
	}

	/**
	 * recover
	 * 
	 * @param closeType
	 */
	private void recover(int closeType) throws LexerException
	{
		Lexeme startLexeme = this.currentLexeme;
		
		while (this.isEOS() == false)
		{
			int typeIndex = this.currentLexeme.typeIndex;

			if (typeIndex == closeType) 
			{
				break;
			}
			else if (typeIndex == JSONTokenTypes.LBRACKET || typeIndex == JSONTokenTypes.LCURLY || typeIndex == JSONTokenTypes.COMMA)
			{
				if (this.currentLexeme != startLexeme)
				{
					break;
				}
				else
				{
					this.advance();
				}
			}
			else
			{
				this.advance();
			}
		}
	}
}
