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
package com.aptana.ide.editor.css.parsing;

import java.text.ParseException;
import java.util.Arrays;

import com.aptana.ide.editor.css.lexing.CSSTokenTypes;
import com.aptana.ide.editor.css.parsing.nodes.CSSCharSetNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSDeclarationNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSExprNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSImportNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSListNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSMediaNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSMediumNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSPageNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSParseNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSParseNodeTypes;
import com.aptana.ide.editor.css.parsing.nodes.CSSRuleSetNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSSelectorNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSSimpleSelectorNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSTermNode;
import com.aptana.ide.editor.css.parsing.nodes.CSSTextNode;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.IToken;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.ParserInitializationException;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.parsing.nodes.ParseNodeBase;
import com.aptana.ide.parsing.nodes.ParseRootNode;

/**
 * @author Kevin Lindsey
 */
public class CSSParser extends CSSParserBase
{
	private static final String HTML_QUOTE_TYPE = "QUOTE";

	private static final String HTML_MIME_TYPE = "text/html";

	private static final int[] atKeywordSet = new int[] {
		CSSTokenTypes.AT_KEYWORD,
		CSSTokenTypes.IMPORT,
		CSSTokenTypes.PAGE,
		CSSTokenTypes.MEDIA,
		CSSTokenTypes.CHARSET
	};

	private static final int[] attributeValueOperator = new int[] {
		CSSTokenTypes.EQUAL,
		CSSTokenTypes.INCLUDES,
		CSSTokenTypes.DASHMATCH
	};

	private static final int[] attributeSet2 = new int[] {
		CSSTokenTypes.IDENTIFIER,
		CSSTokenTypes.STRING
	};

	private static final int[] combinatorSet = new int[] {
		CSSTokenTypes.PLUS,
		CSSTokenTypes.GREATER
	};

	private static final int[] dimensionsSet = new int[] {
		CSSTokenTypes.DIMENSION,
		CSSTokenTypes.EMS,
		CSSTokenTypes.EXS,
		CSSTokenTypes.LENGTH,
		CSSTokenTypes.ANGLE,
		CSSTokenTypes.TIME,
		CSSTokenTypes.FREQUENCY };

	private static final int[] typeOrUniversalSelector = new int[] {
		CSSTokenTypes.IDENTIFIER,
		CSSTokenTypes.STAR,
		CSSTokenTypes.SELECTOR
	};

	private static final int[] operatorSet = new int[] {
		CSSTokenTypes.FORWARD_SLASH,
		CSSTokenTypes.COMMA,
		CSSTokenTypes.EQUAL // added to support 'filter: alpha(opacity = 70);'
	};

	private static final int[] simpleSelectorSet1 = new int[] {
		CSSTokenTypes.IDENTIFIER,
		CSSTokenTypes.STAR,
		CSSTokenTypes.HASH,
		CSSTokenTypes.CLASS,
		CSSTokenTypes.LBRACKET,
		CSSTokenTypes.COLON
	};

	private static final int[] attributeSelector = new int[] {
		CSSTokenTypes.HASH,
		CSSTokenTypes.CLASS,
		CSSTokenTypes.LBRACKET,
		CSSTokenTypes.COLON,
		CSSTokenTypes.COLOR
	};
	// CSSTokenTypes.COLOR is temporary until we know when we're inside a ruleset or not

	private static final int[] primitivesAndOperators = new int[] {
		CSSTokenTypes.FORWARD_SLASH,
		CSSTokenTypes.COMMA,
		CSSTokenTypes.PLUS,
		CSSTokenTypes.MINUS,
		CSSTokenTypes.NUMBER,
		CSSTokenTypes.PERCENTAGE,
		CSSTokenTypes.LENGTH,
		CSSTokenTypes.EMS,
		CSSTokenTypes.EXS,
		CSSTokenTypes.ANGLE,
		CSSTokenTypes.TIME,
		CSSTokenTypes.FREQUENCY,
		CSSTokenTypes.FUNCTION,
		CSSTokenTypes.STRING,
		CSSTokenTypes.IDENTIFIER,
		CSSTokenTypes.URL,
		CSSTokenTypes.COLOR,
		CSSTokenTypes.EQUAL // added to support 'filter: alpha(opacity = 70);'
	};

	private static final int[] primitives = new int[] {
		CSSTokenTypes.PLUS,
		CSSTokenTypes.MINUS,
		CSSTokenTypes.NUMBER,
		CSSTokenTypes.PERCENTAGE,
		CSSTokenTypes.LENGTH,
		CSSTokenTypes.EMS,
		CSSTokenTypes.EXS,
		CSSTokenTypes.ANGLE,
		CSSTokenTypes.TIME,
		CSSTokenTypes.FREQUENCY,
		CSSTokenTypes.FUNCTION,
		CSSTokenTypes.STRING,
		CSSTokenTypes.IDENTIFIER,
		CSSTokenTypes.URL, 
		CSSTokenTypes.COLOR,
		CSSTokenTypes.FORWARD_SLASH,
		CSSTokenTypes.COMMA
	};

	private static final int[] termSet3 = new int[] {
		CSSTokenTypes.NUMBER,
		CSSTokenTypes.PERCENTAGE,
		CSSTokenTypes.LENGTH,
		CSSTokenTypes.EMS,
		CSSTokenTypes.EXS,
		CSSTokenTypes.ANGLE,
		CSSTokenTypes.TIME,
		CSSTokenTypes.FREQUENCY,
		CSSTokenTypes.FUNCTION,
		CSSTokenTypes.STRING,
		CSSTokenTypes.IDENTIFIER,
		CSSTokenTypes.URL,
		CSSTokenTypes.COLOR
	};

	private static final int[] unaryOperatorSet = new int[] {
		CSSTokenTypes.PLUS,
		CSSTokenTypes.MINUS
	};
	
	private static final String DEFAULT_GROUP = "default"; //$NON-NLS-1$

	/**
	 * static constructor
	 */
	static
	{
		Arrays.sort(atKeywordSet);
		Arrays.sort(attributeValueOperator);
		Arrays.sort(attributeSet2);
		Arrays.sort(combinatorSet);
		Arrays.sort(dimensionsSet);
		Arrays.sort(typeOrUniversalSelector);
		Arrays.sort(operatorSet);
		Arrays.sort(simpleSelectorSet1);
		Arrays.sort(attributeSelector);
		Arrays.sort(primitivesAndOperators);
		Arrays.sort(primitives);
		Arrays.sort(termSet3);
		Arrays.sort(unaryOperatorSet);
	}

	/**
	 * Create a new instance of CSSParser
	 * 
	 * @throws ParserInitializationException
	 */
	public CSSParser() throws ParserInitializationException
	{
		this(CSSMimeType.MimeType);
	}

	/**
	 * Create a new instance of CSSParser
	 * 
	 * @param mimeType
	 * @throws ParserInitializationException
	 */
	public CSSParser(String mimeType) throws ParserInitializationException
	{
		super(mimeType);
	}

	/**
	 * createNode
	 * 
	 * @param type
	 * @param startingLexeme
	 * @return CSSParseNode
	 */
	private CSSParseNode createNode(int type, Lexeme startingLexeme)
	{
		return (CSSParseNode) this.getParseNodeFactory().createParseNode(type, startingLexeme);
	}

	/**
	 * <pre>
	 *  CSS
	 *      	:   Import
	 *      	|   Page
	 *  		|	Media
	 *  		|	CharSet
	 *  		|	RuleSet
	 *      	;
	 * </pre>
	 * 
	 * @see com.aptana.ide.parsing.AbstractParser#parseAll(com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public synchronized void parseAll(IParseNode parentNode) throws LexerException
	{
		IParseNode rootNode = this.getParseRootNode(parentNode, ParseRootNode.class);

		ILexer lexer = this.getLexer();
		lexer.setLanguageAndGroup(this.getLanguage(), DEFAULT_GROUP);

		// HACK [KEL]: check if the lexeme before our current offset is an HTML quote.
		// If it is, then parse as a style: no rule sets, only property definitions
		IParseState parseState = this.getParseState();
		boolean inStyleElement = false;

		if (parseState != null)
		{
			LexemeList lexemes = parseState.getLexemeList();
			Lexeme lastLexeme = lexemes.getFloorLexeme(lexer.getCurrentOffset());
			inStyleElement = (lastLexeme != null) ? (lastLexeme.getLanguage().equals(HTML_MIME_TYPE) && lastLexeme
					.getType().equals(HTML_QUOTE_TYPE)) : false;
		}

		this.advance();

		while (this.isEOS() == false)
		{
			IParseNode result = null;

			try
			{
				if (inStyleElement)
				{
					Lexeme currentLexeme = this.currentLexeme;

					result = this.parseRuleSetBody();

					if (currentLexeme == this.currentLexeme)
					{
						this.advance();
					}
				}
				else
				{
					switch (this.currentLexeme.typeIndex)
					{
						case CSSTokenTypes.AT_KEYWORD:
							result = this.parseAtRule();
							break;

						case CSSTokenTypes.CHARSET:
							result = this.parseCharSet();
							break;

						case CSSTokenTypes.IMPORT:
							result = this.parseImport();
							break;

						case CSSTokenTypes.PAGE:
							result = this.parsePage();
							break;

						case CSSTokenTypes.MEDIA:
							result = this.parseMedia();
							break;

						case CSSTokenTypes.COLOR: // temporary until we know when we're inside a ruleset or not
						case CSSTokenTypes.IDENTIFIER:
						case CSSTokenTypes.SELECTOR:
						case CSSTokenTypes.PROPERTY:
						case CSSTokenTypes.STAR:
						case CSSTokenTypes.HASH:
						case CSSTokenTypes.CLASS:
						case CSSTokenTypes.LBRACKET:
						case CSSTokenTypes.COLON:
							result = this.parseRuleSet();
							break;

						default:
							this.advance();
							break;
					}
				}
			}
			catch (ParseException e)
			{
				// reset group
				lexer.setGroup(DEFAULT_GROUP);

				this.advance();
			}
			finally
			{
				if (rootNode != null && result != null)
				{
					rootNode.appendChild(result);
				}
			}
		}
	}

	/**
	 * parseCharSet
	 * <p>
	 * <code>
	 * 		CharSet
	 * 			:	CHARSET STRING SEMICOLON
	 * 			;
	 * </code>
	 * 
	 * @return CSSCharSetNode
	 * @throws ParseException
	 * @throws LexerException
	 */
	private CSSCharSetNode parseCharSet() throws LexerException, ParseException
	{
		CSSCharSetNode result = (CSSCharSetNode) this.createNode(CSSParseNodeTypes.CHAR_SET, this.currentLexeme);

		// advance over '@charset'
		this.assertAndAdvance(CSSTokenTypes.CHARSET, "error.charset"); //$NON-NLS-1$

		// advance over name
		this.assertType(CSSTokenTypes.STRING, "error.charset.name"); //$NON-NLS-1$
		CSSTextNode name = (CSSTextNode) this.createNode(CSSParseNodeTypes.TEXT, this.currentLexeme);
		result.appendChild(name);
		this.advance();

		// advance over ';'
		this.assertType(CSSTokenTypes.SEMICOLON, "error.charset.semicolon"); //$NON-NLS-1$
		result.includeLexemeInRange(this.currentLexeme);
		this.advance();

		return result;
	}

	/**
	 * parseImport
	 * <p>
	 * Import : IMPORT (STRING | URL) (IDENTIFIER (COMMA IDENTIFIER)*)? SEMICOLON ; <code>
	 * </code>
	 * 
	 * @return ParseNode
	 * @throws ParseException
	 * @throws LexerException
	 */
	private ParseNodeBase parseImport() throws LexerException, ParseException
	{
		CSSImportNode result = (CSSImportNode) this.createNode(CSSParseNodeTypes.IMPORT, this.currentLexeme);

		// advance over '@import'
		this.assertAndAdvance(CSSTokenTypes.IMPORT, "error.import"); //$NON-NLS-1$

		switch (this.currentLexeme.typeIndex)
		{
			case CSSTokenTypes.STRING:
			case CSSTokenTypes.URL: // lexer no longer creates this token type
				String name = this.currentLexeme.getText();
				result.setAttribute("name", name); //$NON-NLS-1$
				this.advance();
				break;

			case CSSTokenTypes.FUNCTION:
				StringBuilder buffer = new StringBuilder();

				buffer.append(this.currentLexeme.getText());
				this.advance();

				while (this.isEOS() == false && this.isType(CSSTokenTypes.RPAREN) == false)
				{
					buffer.append(this.currentLexeme.getText());
					this.advance();
				}

				if (this.isType(CSSTokenTypes.RPAREN))
				{
					buffer.append(this.currentLexeme.getText());
					result.setAttribute("name", buffer.toString()); //$NON-NLS-1$
					this.advance();
				}
				else
				{
					this.throwParseError("error.import.name"); //$NON-NLS-1$
				}
				break;

			default:
				this.throwParseError("error.import.name"); //$NON-NLS-1$
		}

		if (this.isType(CSSTokenTypes.IDENTIFIER))
		{
			// create list
			CSSListNode list = (CSSListNode) this.createNode(CSSParseNodeTypes.LIST, this.currentLexeme);

			// set list item delimiter
			list.setDelimiter(", "); //$NON-NLS-1$

			// get first medium name
			CSSTextNode medium = (CSSTextNode) this.createNode(CSSParseNodeTypes.TEXT, this.currentLexeme);

			// and add it to the list
			list.appendChild(medium);

			// advance over the medium name
			this.advance();

			// process all remaining medium names
			while (this.isEOS() == false && this.isType(CSSTokenTypes.COMMA))
			{
				// advance over ','
				this.advance();

				this.assertType(CSSTokenTypes.IDENTIFIER, "error.import.medium"); //$NON-NLS-1$

				medium = (CSSTextNode) this.createNode(CSSParseNodeTypes.TEXT, this.currentLexeme);
				list.appendChild(medium);

				this.advance();
			}

			// add list to import node
			result.appendChild(list);
		}

		// advance over ';'
		this.assertType(CSSTokenTypes.SEMICOLON, "error.import.semicolon"); //$NON-NLS-1$
		result.includeLexemeInRange(this.currentLexeme);
		this.advance();

		return result;
	}

	/**
	 * parseMedia
	 * <p>
	 * <code>
	 * 		Media
	 * 			:	MEDIA IDENTIFIER (COMMA IDENTIFIER)* LBRACE RuleSet* RBRACE
	 * 			;
	 * </code>
	 * 
	 * @return ParseNode
	 * @throws ParseException
	 * @throws LexerException
	 */
	private CSSMediaNode parseMedia() throws LexerException, ParseException
	{
		CSSMediaNode result = (CSSMediaNode) this.createNode(CSSParseNodeTypes.MEDIA, this.currentLexeme);

		// advance over '@media"
		this.assertAndAdvance(CSSTokenTypes.MEDIA, "error.media"); //$NON-NLS-1$

		// create media list
		CSSListNode media = (CSSListNode) this.createNode(CSSParseNodeTypes.LIST, null);

		// set media list delimiter
		media.setDelimiter(", "); //$NON-NLS-1$

		// grab first medium
		this.assertType(CSSTokenTypes.IDENTIFIER, "error.media.identifier"); //$NON-NLS-1$
		CSSMediumNode medium = (CSSMediumNode) this.createNode(CSSParseNodeTypes.MEDIUM, this.currentLexeme);

		// add it to our list
		media.appendChild(medium);

		// advance over medium name
		this.advance();

		// process any remaining medium names
		while (this.isEOS() == false && this.isType(CSSTokenTypes.COMMA))
		{
			// advance over ','
			this.advance();

			this.assertType(CSSTokenTypes.IDENTIFIER, "error.import.medium"); //$NON-NLS-1$
			medium = (CSSMediumNode) this.createNode(CSSParseNodeTypes.MEDIUM, this.currentLexeme);
			media.appendChild(medium);
			this.advance();
		}

		// add list to result
		result.appendChild(media);

		// advance over '{'
		this.assertAndAdvance(CSSTokenTypes.LCURLY, "error.import.open"); //$NON-NLS-1$

		if (this.isType(CSSTokenTypes.IDENTIFIER))
		{
			CSSListNode ruleSets = (CSSListNode) this.createNode(CSSParseNodeTypes.LIST, this.currentLexeme);

			// advance over rulesets
			while (this.isEOS() == false && this.isType(CSSTokenTypes.IDENTIFIER))
			{
				ruleSets.appendChild(this.parseRuleSet());
			}

			result.appendChild(ruleSets);
		}
		else
		{
			result.appendChild(CSSParseNode.Empty);
		}

		// advance over '}'
		this.assertType(CSSTokenTypes.RCURLY, "error.import.close"); //$NON-NLS-1$
		result.includeLexemeInRange(this.currentLexeme);
		this.advance();

		return result;
	}

	/**
	 * parsePage
	 * <p>
	 * <code>
	 * 		Page
	 * 			:	PAGE (COLON IDENTIFIER)? LCURLY (Declaration SEMICOLON)* RCURLY
	 * 			;
	 * </code>
	 * 
	 * @return ParseNode
	 * @throws ParseException
	 * @throws LexerException
	 */
	private CSSPageNode parsePage() throws LexerException, ParseException
	{
		CSSPageNode result = (CSSPageNode) this.createNode(CSSParseNodeTypes.PAGE, this.currentLexeme);

		// advance over '@page'
		this.assertAndAdvance(CSSTokenTypes.PAGE, "error.page"); //$NON-NLS-1$

		// check for pseudo page
		if (this.isType(CSSTokenTypes.COLON))
		{
			// advance over ':'
			this.advance();

			this.assertType(CSSTokenTypes.IDENTIFIER, "error.page.name"); //$NON-NLS-1$
			String pseudoPage = this.currentLexeme.getText();
			result.setAttribute("name", pseudoPage); //$NON-NLS-1$
			this.advance();
		}

		// advance over '{'
		this.assertAndAdvance(CSSTokenTypes.LCURLY, "error.page.open"); //$NON-NLS-1$

		CSSListNode declarations = (CSSListNode) this.createNode(CSSParseNodeTypes.LIST, null);
		declarations.setDelimiter("\n"); //$NON-NLS-1$

		// parse declaration
		if (this.isType(CSSTokenTypes.IDENTIFIER) || this.isType(CSSTokenTypes.PROPERTY))
		{
			declarations.appendChild(this.parseDeclaration());
		}

		while (this.isType(CSSTokenTypes.SEMICOLON))
		{
			// advance over ';'
			this.advance();

			// parse declaration
			if (this.isType(CSSTokenTypes.IDENTIFIER) || this.isType(CSSTokenTypes.PROPERTY))
			{
				declarations.appendChild(this.parseDeclaration());
			}
			else
			{
				declarations.appendChild(CSSParseNode.Empty);
			}
		}

		result.appendChild(declarations);

		// advance over '}'
		this.assertType(CSSTokenTypes.RCURLY, "error.page.close"); //$NON-NLS-1$
		result.includeLexemeInRange(this.currentLexeme);
		this.advance();

		return result;
	}

	/**
	 * parseAtRule
	 * <p>
	 * <code>
	 * 		AtRule
	 * 			:	ATKEYWORD STRING (Block | SEMICOLON)
	 * 			;
	 * </code>
	 * 
	 * @throws LexerException
	 * @throws ParseException
	 * @return CSSAtRuleNode
	 */
	private CSSParseNode parseAtRule() throws LexerException, ParseException
	{
		CSSParseNode result = this.createNode(CSSParseNodeTypes.AT_RULE, this.currentLexeme);

		// consume @ keyword
		this.inSet(atKeywordSet);
		this.advance();

		// zero or more "any" tokens
		if (this.isType(CSSTokenTypes.STRING))
		{
			this.advance();
		}

		// block or semicolon
		switch (this.currentLexeme.typeIndex)
		{
			case CSSTokenTypes.LCURLY:
				this.advance();

				CSSListNode declarations = parseRuleSetBody();
				result.appendChild(declarations);
				result.includeLexemeInRange(this.currentLexeme);

				this.assertAndAdvance(CSSTokenTypes.RCURLY, "error.rule-set.close"); //$NON-NLS-1$
				break;

			case CSSTokenTypes.SEMICOLON:
				result.includeLexemeInRange(this.currentLexeme);
				this.advance();
				break;

			default:
				this.throwParseError("error-at-rule.semicolon"); //$NON-NLS-1$
		}

		return result;
	}

	/**
	 * parseBlock
	 * <p>
	 * <code>
	 * 		Block
	 * 			:	LCURLY ^RCURLY* RCURLY
	 * 			;
	 * </code>
	 * 
	 * @return CSSBlockNode
	 * @throws ParseException
	 * @throws LexerException
	 */
	private CSSParseNode parseBlock() throws LexerException, ParseException
	{
		CSSParseNode result = this.createNode(CSSParseNodeTypes.BLOCK, this.currentLexeme);

		this.assertAndAdvance(CSSTokenTypes.LCURLY, "error.block.open"); //$NON-NLS-1$

		while (this.isEOS() == false && this.isType(CSSTokenTypes.RCURLY) == false)
		{
			this.advance();
		}

		if (this.isType(CSSTokenTypes.RCURLY))
		{
			this.advance();
		}

		return result;
	}

	/**
	 * parseDeclaration
	 * <p>
	 * <code>
	 * 		Declaration
	 * 			:	IDENTIFIER COLON Expression IMPORTANT?
	 * 			;
	 * </code>
	 * 
	 * @throws LexerException
	 * @throws ParseException
	 * @return CSSDeclarationNode
	 */
	private CSSDeclarationNode parseDeclaration() throws LexerException, ParseException
	{
		CSSDeclarationNode result;

		// grab property name
		try
		{
			this.assertType(CSSTokenTypes.IDENTIFIER, "error.property"); //$NON-NLS-1$
		}
		catch (ParseException e)
		{
			this.assertType(CSSTokenTypes.PROPERTY, "error.property"); //$NON-NLS-1$
		}

		// change identifier to PROPERTY token type for proper semantics and colorization
		this.changeTokenType(PROPERTY_TOKEN);

		String name = this.currentLexeme.getText();
		result = (CSSDeclarationNode) this.createNode(CSSParseNodeTypes.DECLARATION, this.currentLexeme);
		result.setAttribute("name", name); //$NON-NLS-1$
		this.advance();

		// advance over ':'
		this.assertAndAdvance(CSSTokenTypes.COLON, "error.declaration.colon"); //$NON-NLS-1$
		result.includeLexemeInRange(currentLexeme); // ensure there is an end lexeme

		// handle expr
		result.appendChild(this.parseExpression());

		// check for "!important"
		if (this.isType(CSSTokenTypes.IMPORTANT))
		{
			String important = this.currentLexeme.getText();
			result.setAttribute("status", important); //$NON-NLS-1$
			result.includeLexemeInRange(this.currentLexeme);

			this.advance();
		}

		return result;
	}

	/**
	 * changeTokenType
	 */
	private void changeTokenType(IToken token)
	{
		if (this.currentLexeme.getToken() != token)
		{
			// change token type
			this.currentLexeme.setToken(token);

			// force refresh of this token
			this.getParseState().addUpdateRegion(this.currentLexeme);
		}
	}

	/**
	 * parseExpression
	 * <p>
	 * <code>
	 * 		Expression
	 * 			:	Term ((SLASH | COMMA)? (PLUS | MINUS | NUMBER | PERCENTAGE | LENGTH |
	 * 										EMS | EXS | ANGLE | TIME | FREQUENCY | FUNCTION |
	 * 										STRING | IDENTIFIER | URL | COLOR | SLASH | COMMA) )*
	 * 			;
	 * </code>
	 * 
	 * @return CSSExprNode
	 * @throws LexerException
	 * @throws ParseException
	 */
	private CSSExprNode parseExpression() throws LexerException, ParseException
	{
		CSSExprNode result = (CSSExprNode) this.createNode(CSSParseNodeTypes.EXPR, this.currentLexeme);

		// get at least one term
		result.appendChild(this.parseTerm());

		// keep collecting terms until there are no more primitives nor separators
		while (this.inSet(primitivesAndOperators))
		{
			String operator = " "; //$NON-NLS-1$
			CSSTermNode term = null;

			if (this.inSet(operatorSet))
			{
				operator = this.currentLexeme.getText();
				this.advance();
			}

			if (this.inSet(primitives))
			{
				term = this.parseTerm();
				term.setAttribute("joining-operator", operator); //$NON-NLS-1$

				result.appendChild(term);
			}
			else
			{
				throwParseError("error.expression.term"); //$NON-NLS-1$
			}
		}

		return result;
	}

	/**
	 * parseTerm
	 * <p>
	 * <code>
	 * 		Term
	 * 			:	(PLUS | MINUS)? (NUMBER | PERCENTAGE | LENGTH | EMS | EXS | ANGLE | TIME |
	 FREQUENCY  | STRING | IDENTIFIER | URL | COLOR | FUNCTION Expression)
	 ;
	 * </code>
	 * 
	 * @return CSSTermNode
	 * @throws LexerException
	 * @throws ParseException
	 */
	private CSSTermNode parseTerm() throws LexerException, ParseException
	{
		CSSTermNode result = (CSSTermNode) this.createNode(CSSParseNodeTypes.TERM, this.currentLexeme);

		if (this.inSet(unaryOperatorSet))
		{
			// grab '-' or '+' and advance
			String operator = this.currentLexeme.getText();
			result.setAttribute("operator", operator); //$NON-NLS-1$
			this.advance();
		}

		// make sure this is a primitive
		this.assertInSet(termSet3, "error.term"); //$NON-NLS-1$
		String value = this.currentLexeme.getText();
		result.setAttribute("value", value); //$NON-NLS-1$
		result.includeLexemeInRange(this.currentLexeme);

		if (this.isType(CSSTokenTypes.FUNCTION))
		{
			// advance over function
			this.advance();
			
			CSSExprNode expr = this.parseExpression();
			
			result.appendChild(expr);

			if (this.isType(CSSTokenTypes.RPAREN))
			{
				result.includeLexemeInRange(this.currentLexeme);
			}

			this.assertAndAdvance(CSSTokenTypes.RPAREN, "error.function.close"); //$NON-NLS-1$
		}
		else
		{
			this.advance();
		}

		return result;
	}

	/**
	 * parseRuleSet
	 * <p>
	 * <code>
	 * 		RuleSet
	 * 			:	Selector (COMMA Selector)* LCURLY (Declaration | SEMICOLON)* RCURLY
	 * 			;
	 * </code>
	 * 
	 * @return CSSRuleSetNode
	 * @throws ParseException
	 * @throws LexerException
	 */
	private CSSRuleSetNode parseRuleSet() throws ParseException, LexerException
	{
		CSSRuleSetNode result = (CSSRuleSetNode) this.createNode(CSSParseNodeTypes.RULE_SET, this.currentLexeme);
		CSSListNode selectors = (CSSListNode) this.createNode(CSSParseNodeTypes.LIST, this.currentLexeme);

		selectors.setListName("selectors"); //$NON-NLS-1$

		// add first selector
		selectors.appendChild(this.parseSelector());

		// add any following selectors
		while (this.isType(CSSTokenTypes.COMMA))
		{
			// advance over ','
			this.advance();

			// add selector
			selectors.appendChild(this.parseSelector());
		}

		result.appendChild(selectors);

		this.assertAndAdvance(CSSTokenTypes.LCURLY, "error.rule-set.open"); //$NON-NLS-1$

		CSSListNode declarations = parseRuleSetBody();
		result.appendChild(declarations);
		result.includeLexemeInRange(this.currentLexeme);

		this.assertAndAdvance(CSSTokenTypes.RCURLY, "error.rule-set.close"); //$NON-NLS-1$

		return result;
	}

	/**
	 * parseRuleSetBody
	 * 
	 * @return CSSListNode
	 * @throws LexerException
	 * @throws ParseException
	 */
	private CSSListNode parseRuleSetBody() throws LexerException, ParseException
	{
		CSSListNode declarations = (CSSListNode) this.createNode(CSSParseNodeTypes.LIST, null);
		declarations.setDelimiter("\n"); //$NON-NLS-1$
		declarations.setListName("properties"); //$NON-NLS-1$

		// add declaration
		if (this.isType(CSSTokenTypes.IDENTIFIER) || this.isType(CSSTokenTypes.PROPERTY))
		{
			declarations.appendChild(this.parseDeclaration());
		}
		else
		{
			if (this.isType(CSSTokenTypes.SEMICOLON))
			{
				declarations.appendChild(CSSParseNode.Empty);
			}
		}

		// add any following declarations
		while (this.isType(CSSTokenTypes.SEMICOLON))
		{
			// advance over ';'
			this.advance();

			// add declaration
			if (this.isType(CSSTokenTypes.IDENTIFIER) || this.isType(CSSTokenTypes.PROPERTY))
			{
				declarations.appendChild(this.parseDeclaration());
			}
			else
			{
				if (this.isType(CSSTokenTypes.SEMICOLON))
				{
					declarations.appendChild(CSSParseNode.Empty);
				}
			}
		}

		return declarations;
	}

	/**
	 * parseSelector
	 * <p>
	 * <code>
	 * 		Selector
	 * 			:	SimpleSelector ((PLUS | GREATER_THAN)? SimpleSelector?)*
	 * 			;
	 * </code>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private CSSSelectorNode parseSelector() throws ParseException, LexerException
	{
		boolean process = true;
		CSSSelectorNode result = (CSSSelectorNode) this.createNode(CSSParseNodeTypes.SELECTOR, this.currentLexeme);

		// change identifier to SELECTOR token type for proper semantics and colorization
		if (this.isType(CSSTokenTypes.IDENTIFIER))
		{
			this.changeTokenType(SELECTOR_TOKEN);
		}

		CSSSimpleSelectorNode simpleSelector = this.parseSimpleSelector();

		simpleSelector.appendChild(CSSParseNode.Empty);
		result.appendChild(simpleSelector);

		while (process)
		{
			CSSTextNode combinator = null;
			simpleSelector = null;

			// assume we're done
			process = false;

			// see if we have a combinator
			if (this.inSet(combinatorSet))
			{
				// grab combinator
				combinator = (CSSTextNode) this.createNode(CSSParseNodeTypes.TEXT, this.currentLexeme);

				// advance
				this.advance();

				// continue
				process = true;
			}

			// see if we have a selector
			if (this.inSet(simpleSelectorSet1))
			{
				simpleSelector = parseSimpleSelector();

				if (combinator != null)
				{
					simpleSelector.appendChild(combinator);
				}
				else
				{
					simpleSelector.appendChild(CSSParseNode.Empty);
				}

				// continue
				process = true;
			}

			if (simpleSelector != null)
			{
				result.appendChild(simpleSelector);
			}
		}

		return result;
	}

	/**
	 * parseSimpleSelector
	 * <p>
	 * <code>
	 * 		SimpleSelector
	 * 			:	(PLUS | GREATER_THAN)
	 * 			|	(PLUS | GREATER_THAN)? (HASH | CLASS | LBRACKET IDENTIFIER (EQUALS INCLUDES DASHMATCH)? RBRACKET
	 * 				 | COLON (IDENTIFIER | FUNCTION)
	 * 			;
	 * </code>
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private CSSSimpleSelectorNode parseSimpleSelector() throws ParseException, LexerException
	{
		CSSSimpleSelectorNode result = (CSSSimpleSelectorNode) this.createNode(CSSParseNodeTypes.SIMPLE_SELECTOR,
				this.currentLexeme);

		CSSListNode components = (CSSListNode) this.createNode(CSSParseNodeTypes.LIST, null);
		components.setListName("components"); //$NON-NLS-1$

		CSSTextNode component;
		boolean needsOne = true;

		if (this.inSet(typeOrUniversalSelector))
		{
			component = (CSSTextNode) this.createNode(CSSParseNodeTypes.TEXT, this.currentLexeme);
			components.appendChild(component);

			// set possible end
			result.includeLexemeInRange(this.currentLexeme);

			// advance over element name
			this.advance();

			// clear flag indicating that we need to consume more
			needsOne = false;
		}

		while (this.inSet(attributeSelector) || needsOne)
		{
			Lexeme startingLexeme = this.currentLexeme;
			Lexeme endingLexeme;

			switch (this.currentLexeme.typeIndex)
			{
				case CSSTokenTypes.COLOR:
				case CSSTokenTypes.HASH:
				case CSSTokenTypes.CLASS:
					component = (CSSTextNode) this.createNode(CSSParseNodeTypes.TEXT, this.currentLexeme);
					components.appendChild(component);

					// set possible end
					result.includeLexemeInRange(this.currentLexeme);

					// advance over class
					this.advance();
					break;

				case CSSTokenTypes.LBRACKET:
					String name;
					String assertion = ""; //$NON-NLS-1$

					startingLexeme = this.currentLexeme;

					// advance over '['
					this.advance();

					// advance over attribute name
					this.assertType(CSSTokenTypes.IDENTIFIER, "error.attrib.name"); //$NON-NLS-1$
					name = this.currentLexeme.getText();
					this.advance();

					if (this.inSet(attributeValueOperator))
					{
						// advance over '=', '~=', or '|='
						assertion = this.currentLexeme.getText();
						this.advance();

						this.assertInSet(attributeSet2, "error.attribute.assignment"); //$NON-NLS-1$
						assertion += this.currentLexeme.getText();
						this.advance();
					}

					// set possible end
					result.includeLexemeInRange(this.currentLexeme);

					// advance over ']'
					endingLexeme = this.currentLexeme;
					this.assertAndAdvance(CSSTokenTypes.RBRACKET, "error.attrib.close"); //$NON-NLS-1$

					component = (CSSTextNode) this.createNode(CSSParseNodeTypes.TEXT, startingLexeme);
					component.setText("[" + name + assertion + "]"); //$NON-NLS-1$//$NON-NLS-2$
					component.includeLexemeInRange(endingLexeme);
					components.appendChild(component);
					break;

				case CSSTokenTypes.COLON:
					String text = ":"; //$NON-NLS-1$

					startingLexeme = this.currentLexeme;
					endingLexeme = this.currentLexeme; // make compiler happy

					// advance over ':'
					this.advance();

					switch (this.currentLexeme.typeIndex)
					{
						case CSSTokenTypes.IDENTIFIER:
							// grab identifier and advance
							text += this.currentLexeme.getText();

							// set possible end
							endingLexeme = this.currentLexeme;
							result.includeLexemeInRange(this.currentLexeme);

							// advance over name
							this.advance();
							break;

						case CSSTokenTypes.FUNCTION:
							text += this.currentLexeme.getText();
							this.advance();

							if (this.isType(CSSTokenTypes.IDENTIFIER))
							{
								text += this.currentLexeme.getText();
								this.advance();
							}

							// set possible end
							endingLexeme = this.currentLexeme;
							result.includeLexemeInRange(this.currentLexeme);

							// make sure we have ')'
							this.assertAndAdvance(CSSTokenTypes.RPAREN, "error.pseudo.function.close"); //$NON-NLS-1$

							text += ")"; //$NON-NLS-1$
							break;

						default:
							throwParseError("error.pseudo"); //$NON-NLS-1$
					}

					component = (CSSTextNode) this.createNode(CSSParseNodeTypes.TEXT, startingLexeme);
					component.setText(text);
					component.includeLexemeInRange(endingLexeme);
					components.appendChild(component);
					break;

				default:
					throwParseError("error.simple-selector"); //$NON-NLS-1$
			}

			// clear flag indicating that we need to consume more
			needsOne = false;
		}

		result.appendChild(components);

		return result;
	}
}
