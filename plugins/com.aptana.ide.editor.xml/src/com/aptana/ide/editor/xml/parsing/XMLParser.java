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
package com.aptana.ide.editor.xml.parsing;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Stack;

import com.aptana.ide.editor.xml.lexing.XMLTokenTypes;
import com.aptana.ide.editor.xml.parsing.nodes.XMLDeclarationNode;
import com.aptana.ide.editor.xml.parsing.nodes.XMLElementNode;
import com.aptana.ide.editor.xml.parsing.nodes.XMLParseNode;
import com.aptana.ide.editor.xml.parsing.nodes.XMLParseNodeTypes;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.TokenCategories;
import com.aptana.ide.parsing.ParserInitializationException;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.parsing.nodes.IParseNodeAttribute;
import com.aptana.ide.parsing.nodes.ParseRootNode;
import com.aptana.ide.parsing.nodes.QuoteType;

/**
 * @author Kevin Lindsey
 */
public class XMLParser extends XMLParserBase
{
	private static final int[] elementEndSet = new int[] { XMLTokenTypes.GREATER_THAN, XMLTokenTypes.SLASH_GREATER_THAN };

	private Stack<IParseNode> _elementStack;
	
	static
	{
		// make sure all of our sets are sorted so that inSet will work properly
		// (that method uses a binary search to test existence of members in the
		// set)
		Arrays.sort(elementEndSet);
	}
	
	/**
	 * Create a new instance of XMLParser
	 * 
	 * @throws ParserInitializationException
	 */
	public XMLParser() throws ParserInitializationException
	{
		this(XMLMimeType.MimeType);
	}
	
	/**
	 * Create a new instance of XMLParser
	 * @param mimeType 
	 * 
	 * @throws ParserInitializationException
	 */
	public XMLParser(String mimeType) throws ParserInitializationException
	{
		super(mimeType);
		
		this._elementStack = new Stack<IParseNode>();
	}
	
	/**
	 * Close the element that is on the top of the stack
	 */
	private void closeElement()
	{
		if (this._currentElement != null)
		{
			this._currentElement.includeLexemeInRange(this.currentLexeme);
		}

		if (this._elementStack.size() > 0)
		{
			this._currentElement = this._elementStack.pop();
		}
		else
		{
			this._currentElement = null;
		}
	}

	/**
	 * createNode
	 * 
	 * @param type
	 * @param startingLexeme
	 * @return HTMLParseNode
	 */
	private XMLParseNode createNode(int type, Lexeme startingLexeme)
	{
		return (XMLParseNode) this.getParseNodeFactory().createParseNode(type, startingLexeme);
	}
	
	/**
	 * Push the currently active element onto the stack and set the specified element as the new active element
	 * 
	 * @param element
	 */
	private void openElement(XMLElementNode element)
	{
		// add the new parent as a child of the current parent
		if (this._currentElement != null)
		{
			this._currentElement.appendChild(element);
		}

		this._elementStack.push(this._currentElement);
		this._currentElement = element;
	}

	/**
	 * @see com.aptana.ide.parsing.AbstractParser#parseAll(com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public synchronized void parseAll(IParseNode parentNode) throws LexerException
	{
		this._elementStack.clear();
		this._currentElement = parentNode;

		ILexer lexer = this.getLexer();
		lexer.setLanguageAndGroup(this.getLanguage(), DEFAULT_GROUP);

		//this.advance();
		try
		{
			this.parseText(false);
		}
		catch (ParseException e)
		{
			// reset group
			lexer.setGroup(DEFAULT_GROUP);
		}

		while (this.isEOS() == false)
		{
			try
			{
				switch (this.currentLexeme.typeIndex)
				{
					case XMLTokenTypes.CDATA_START:
						this.parseCDATASection();
						break;

					case XMLTokenTypes.COMMENT:
						this.parseText(false);
						break;
						
					case XMLTokenTypes.DOCTYPE_DECL:
						this.parseDocTypeDeclaration();
						break;
						
					case XMLTokenTypes.END_TAG:
						this.parseEndTag();
						break;

					case XMLTokenTypes.PI_OPEN:
						this.parsePI();
						this.parseText(false);
						break;

					case XMLTokenTypes.START_TAG:
						this.parseStartTag();
						break;

					case XMLTokenTypes.XML_DECL:
						this.parseXMLDeclaration();
						break;

					default:
						this.advance();
				}
			}
			catch (ParseException e)
			{
				// reset group
				lexer.setGroup(DEFAULT_GROUP);
			}
		}
	}

	/**
	 * parseException
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private void parseAttribute() throws ParseException, LexerException
	{
		// assume we have a valid attribute
		String name = this.currentLexeme.getText();

		// advance over attribute name
		this.assertAndAdvance(XMLTokenTypes.NAME, "error.attribute"); //$NON-NLS-1$

		// advance over '='
		this.assertAndAdvance(XMLTokenTypes.EQUAL, "error.attribute.equal"); //$NON-NLS-1$

		// advance over value
		this.assertType(XMLTokenTypes.STRING, "error.attribute.value"); //$NON-NLS-1$

		if (this.currentLexeme.getCategoryIndex() != TokenCategories.ERROR)
		{
			// grab attribute value
			String value = this.currentLexeme.getText();

			// remove quotes, if needed
			char firstChar = value.charAt(0);
			int quoteType = QuoteType.NONE;

			if (firstChar == '"')
			{
				value = value.substring(1, value.length() - 1);
				quoteType = QuoteType.DOUBLE_QUOTE;
			}
			else if (firstChar == '\'')
			{
				value = value.substring(1, value.length() - 1);
				quoteType = QuoteType.SINGLE_QUOTE;
			}

			// add attribute to element node
			this._currentElement.setAttribute(name, value);

			// set quote type
			IParseNodeAttribute attr = this._currentElement.getAttributeNode(name);
			attr.setQuoteType(quoteType);
		}

		this.advance();
	}

	/**
	 * parseCDATASection
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private void parseCDATASection() throws LexerException, ParseException
	{
		// get lexer
		ILexer lexer = this.getLexer();

		// switch to cdata-section group
		lexer.setGroup(CDATA_SECTION_GROUP);

		this.assertAndAdvance(XMLTokenTypes.CDATA_START, "error.cdata"); //$NON-NLS-1$

		// grab text
		this.assertAndAdvance(XMLTokenTypes.CDATA_END, "error.cdata.close"); //$NON-NLS-1$
	}

	/**
	 * parseEndTag
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private void parseEndTag() throws LexerException, ParseException
	{
		this.assertAndAdvance(XMLTokenTypes.END_TAG, "error.tag.end"); //$NON-NLS-1$

		this.closeElement();

		this.parseText(true);
	}

	/**
	 * parsePI
	 * 
	 * @throws LexerException
	 * @throws ParseException
	 */
	private void parsePI() throws LexerException, ParseException
	{
		// get lexer
		ILexer lexer = this.getLexer();

		// switch to cdata-section group
		lexer.setGroup(PROCESSING_INSTRUCTION_GROUP);

		this.assertAndAdvance(XMLTokenTypes.PI_OPEN, "error.pi"); //$NON-NLS-1$

		// grab text
		this.assertAndAdvance(XMLTokenTypes.CDATA_END, "error.pi.close"); //$NON-NLS-1$
	}

	/**
	 * parseStartTag
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private void parseStartTag() throws ParseException, LexerException
	{
		// make sure we're currently on a start tag
		this.assertType(XMLTokenTypes.START_TAG, "error.tag.start"); //$NON-NLS-1$

		// create the new element
		XMLElementNode element = (XMLElementNode) this.createNode(XMLParseNodeTypes.ELEMENT, this.currentLexeme);

		// push the element onto our stack
		this.openElement(element);

		// advance over beginning of element
		this.advance();

		// process any attributes
		while (this.isEOS() == false && this.inSet(elementEndSet) == false)
		{
			this.parseAttribute();
		}

		switch (this.currentLexeme.typeIndex)
		{
			case XMLTokenTypes.GREATER_THAN:
				break;

			case XMLTokenTypes.SLASH_GREATER_THAN:
				this.closeElement();
				break;

			default:
				throwParseError("error.tag.start.unclosed"); //$NON-NLS-1$
		}

		// handle possible inner or trailing text

		parseText(false);
	}

	/**
	 * Parse XML declaration
	 * 
	 * @throws LexerException
	 * @throws ParseException
	 */
	private XMLDeclarationNode parseXMLDeclaration() throws LexerException, ParseException
	{
		// switch to XML declaration
		this.getLexer().setGroup(XML_DECLARATION_GROUP);

		XMLDeclarationNode decl = (XMLDeclarationNode) this.createNode(XMLParseNodeTypes.DECLARATION, this.currentLexeme);

		// advance over '<?xml'
		this.assertAndAdvance(XMLTokenTypes.XML_DECL, "error.xml.declaration"); //$NON-NLS-1$

		// set version
		decl.setVersion(this.currentLexeme.getText());

		// always root
		if (this._currentElement instanceof ParseRootNode)
		{
			this._currentElement.appendChild(decl);
		}

		// parse declaration
		this.assertAndAdvance(XMLTokenTypes.VERSION, "error.xml.declaration.version"); //$NON-NLS-1$

		if (this.isType(XMLTokenTypes.ENCODING))
		{
			decl.setEncoding(this.currentLexeme.getText());
			this.advance();
		}

		if (this.isType(XMLTokenTypes.STANDALONE))
		{
			decl.setStandalone(this.currentLexeme.getText());
			this.advance();
		}

		this.getLexer().setGroup(DEFAULT_GROUP);

		this.assertAndAdvance(XMLTokenTypes.QUESTION_GREATER_THAN, "error.xml.declaration.close"); //$NON-NLS-1$

		return decl;
	}
}
