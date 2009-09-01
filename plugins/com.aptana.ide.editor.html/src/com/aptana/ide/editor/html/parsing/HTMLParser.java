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
package com.aptana.ide.editor.html.parsing;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Stack;

import com.aptana.ide.editor.html.lexing.HTMLTokenTypes;
import com.aptana.ide.editor.html.parsing.nodes.HTMLDeclarationNode;
import com.aptana.ide.editor.html.parsing.nodes.HTMLDocumentNode;
import com.aptana.ide.editor.html.parsing.nodes.HTMLElementNode;
import com.aptana.ide.editor.html.parsing.nodes.HTMLParseNode;
import com.aptana.ide.editor.html.parsing.nodes.HTMLParseNodeTypes;
import com.aptana.ide.editor.html.parsing.nodes.HTMLSpecialNode;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.Range;
import com.aptana.ide.lexer.TokenCategories;
import com.aptana.ide.lexer.matcher.AndMatcher;
import com.aptana.ide.lexer.matcher.CharacterMatcher;
import com.aptana.ide.lexer.matcher.StringMatcher;
import com.aptana.ide.lexer.matcher.WhitespaceMatcher;
import com.aptana.ide.lexer.matcher.ZeroOrMoreMatcher;
import com.aptana.ide.parsing.IParser;
import com.aptana.ide.parsing.ParserInitializationException;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.parsing.nodes.IParseNodeAttribute;
import com.aptana.ide.parsing.nodes.QuoteType;

/**
 * @author Kevin Lindsey
 */
public class HTMLParser extends HTMLParserBase
{
	private static String ATTRIBUTE_LANGUAGE_GROUP = "attribute-language"; //$NON-NLS-1$
	private static String DOUBLE_QUOTED_ATTRIBUTE_DELIMITER_GROUP = "double-quoted-attribute-delimiter"; //$NON-NLS-1$
	private static String SINGLE_QUOTED_ATTRIBUTE_DELIMITER_GROUP = "single-quoted-attribute-delimiter"; //$NON-NLS-1$
	
	private static final String CDATA_SECTION_GROUP = "cdata-section"; //$NON-NLS-1$
	private static final String DEFAULT_GROUP = "default"; //$NON-NLS-1$
	private static final String PERCENT_INSTRUCTION_GROUP = "percent-instruction"; //$NON-NLS-1$
	private static final String PROCESSING_INSTRUCTION_GROUP = "processing-instruction"; //$NON-NLS-1$
//	private static final String TEXT_GROUP = "text"; //$NON-NLS-1$
//	private static final String XML_DECLARATION_GROUP = "xml-declaration"; //$NON-NLS-1$
	
	public static final String ATTRIBUTE_GROUP = "attribute"; //$NON-NLS-1$
	public static final String PERCENT_INSTRUCTION_DELIMITER_GROUP = "percent-instruction-delimiter"; //$NON-NLS-1$
	public static final String PROCESSING_INSTRUCTION_DELIMITER_GROUP = "processing-instruction-delimiter"; //$NON-NLS-1$
	public static final String SCRIPT_DELIMITER_GROUP = "script-delimiter"; //$NON-NLS-1$
	public static final String STYLE_DELIMITER_GROUP = "style-delimiter"; //$NON-NLS-1$
	public static final String TAG_DELIMITER_GROUP = "tag-delimiter"; //$NON-NLS-1$

	private Stack<IParseNode> _elementStack = new Stack<IParseNode>();

	private static final int[] elementEndSet = new int[] { HTMLTokenTypes.GREATER_THAN,
			HTMLTokenTypes.SLASH_GREATER_THAN };
	
	private AndMatcher _closeTagMatcher;
	private StringMatcher _closeTagNameMatcher;

	/**
	 * static constructor
	 */
	static
	{
		// make sure all of our sets are sorted so that inSet will work properly
		// (that method uses a binary search to test existence of members in the
		// set)
		Arrays.sort(elementEndSet);
	}

	/**
	 * Create a new instance of CSSParser
	 * 
	 * @throws ParserInitializationException
	 */
	public HTMLParser() throws ParserInitializationException
	{
		this(HTMLMimeType.MimeType);
	}

	/**
	 * Create a new instance of CSSParser
	 * 
	 * @param mimeType
	 * @throws ParserInitializationException
	 */
	public HTMLParser(String mimeType) throws ParserInitializationException
	{
		super(mimeType);

		this._elementStack = new Stack<IParseNode>();
		
		// match tag name. Note this will change each time this is needed
		this._closeTagNameMatcher = new StringMatcher();
		this._closeTagNameMatcher.setCaseInsensitive(true);
		
		// match whitespace
		ZeroOrMoreMatcher whitespaces = new ZeroOrMoreMatcher();
		whitespaces.appendChild(new WhitespaceMatcher());
		
		// match </tagname\s*>
		this._closeTagMatcher = new AndMatcher();
		this._closeTagMatcher.appendChild(new StringMatcher("</")); //$NON-NLS-1$
		this._closeTagMatcher.appendChild(this._closeTagNameMatcher);
		this._closeTagMatcher.appendChild(whitespaces);
		this._closeTagMatcher.appendChild(new CharacterMatcher('>'));
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
	 * Push the currently active element onto the stack and set the specified element as the new active element
	 * 
	 * @param element
	 */
	private void openElement(HTMLParseNode element)
	{
		// add the new parent as a child of the current parent
		if (this._currentElement != null)
		{
			this._currentElement.appendChild(element);
		}

		HTMLParseState parseState = (HTMLParseState) this.getParseState();

		if (parseState.getCloseTagType(element.getName()) != HTMLTagInfo.END_FORBIDDEN)
		{
			this._elementStack.push(this._currentElement);
			this._currentElement = element;
		}
	}

	/**
	 * createNode
	 * 
	 * @param type
	 * @param startingLexeme
	 * @return HTMLParseNode
	 */
	private HTMLParseNode createNode(int type, Lexeme startingLexeme)
	{
		return (HTMLParseNode) this.getParseNodeFactory().createParseNode(type, startingLexeme);
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

		// this.advance();
		this.parseText();

		while (this.currentLexeme != EOS)
		{
			try
			{
				switch (this.currentLexeme.typeIndex)
				{
					case HTMLTokenTypes.CDATA_START:
						this.parseCDATASection();
						break;

					case HTMLTokenTypes.COMMENT:
						this.parseText();
						break;

					case HTMLTokenTypes.END_TAG:
						this.parseEndTag();
						break;

					case HTMLTokenTypes.PERCENT_OPEN:
						this.parsePercentInstruction();
						this.parseText();
						break;

					case HTMLTokenTypes.PI_OPEN:
						this.parseProcessingInstruction();
						this.parseText();
						break;

					case HTMLTokenTypes.START_TAG:
						this.parseStartTag();
						break;

					case HTMLTokenTypes.XML_DECL:
						this.parseXMLDeclaration();
						break;

					// attempt error recovery for language change on malformed script and style tags
					case HTMLTokenTypes.GREATER_THAN:
						if (this._currentElement != null)
						{
							String currentElementName = ((HTMLParseNode) this._currentElement).getName();

							if (currentElementName.equals("script")) //$NON-NLS-1$
							{
								switchToScriptLanguage();
							}
							else if (currentElementName.equals("style")) //$NON-NLS-1$
							{
								switchToStyleLanguage();
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
						break;

					default:
						if (this.currentLexeme.getLanguage().equals(HTMLMimeType.MimeType) == false)
						{
							LexemeList lexemes = this.getLexemeList();

							lexemes.getAffectedRegion().includeInRange(this.currentLexeme.offset);
							lexemes.remove(this.currentLexeme);
							lexer.setCurrentOffset(this.currentLexeme.offset);
						}

						this.advance();

						break;
				}
			}
			catch (ParseException e)
			{
				// reset group
				lexer.setGroup(DEFAULT_GROUP);
			}
		}

		if (parentNode instanceof HTMLDocumentNode)
		{
			correctDocumentNodeEndingLexeme((HTMLDocumentNode) parentNode);
		}
	}

	/**
	 * This function corrects an error with the ending lexeme of the html document not being set correctly. We want it
	 * to be set to the ending lexeme of the last child.
	 * 
	 * @param parentNode
	 */
	private void correctDocumentNodeEndingLexeme(HTMLDocumentNode parentNode)
	{
		int childCount = parentNode.getChildCount();
		if (childCount > 0)
		{
			IParseNode lastChild = parentNode.getChild(childCount - 1);
			parentNode.includeLexemeInRange(lastChild.getEndingLexeme());
		}
	}

	/**
	 * parseException
	 * 
	 * @param element
	 * @throws ParseException
	 * @throws LexerException
	 */
	private void parseAttribute(HTMLElementNode element) throws ParseException, LexerException
	{
		// assume we have a valid attribute
		String name = this.currentLexeme.getText();

		// advance over attribute name
		this.assertAndAdvance(HTMLTokenTypes.NAME, "error.attribute"); //$NON-NLS-1$

		// get lexer
		ILexer lexer = this.getLexer();

		// switch to attribute lexer group
		lexer.setGroup(ATTRIBUTE_GROUP);

		// advance over '='
		this.assertAndAdvance(HTMLTokenTypes.EQUAL, "error.attribute.equal"); //$NON-NLS-1$

		// update attribute value and quote flag
		if (this.currentLexeme.getCategoryIndex() != TokenCategories.ERROR)
		{
			// get value
			String value = this.currentLexeme.getText();

			// see if we're starting with a quote
			char firstChar = value.charAt(0);

			// only check for attribute language if the value is in quotes
			IParser parser = null;

			if (firstChar == '"' || firstChar == '\'')
			{
				parser = this.languageRegistry.getProcessingInstructionLanguage(name);
			}

			if (this.isType(HTMLTokenTypes.STRING))
			{
				// remove quotes, if needed
				int quoteType = QuoteType.NONE;

				if (value.length() > 1)
				{
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
				}

				// add attribute to element node
				element.setAttribute(name, value);

				// set quote type
				IParseNodeAttribute attr = element.getAttributeNode(name);
				attr.setQuoteType(quoteType);

				if (parser != null)
				{
					int offset = this.currentLexeme.offset;

					// remove lexeme from list
					LexemeList lexemes = this.getLexemeList();
					lexemes.remove(this.currentLexeme);
					lexemes.getAffectedRegion().includeInRange(offset);

					// reposition lexer
					lexer.setCurrentOffset(offset);
					lexer.setGroup(HTMLParser.ATTRIBUTE_LANGUAGE_GROUP);

					// capture quote
					this.advance();
				}
			}

			if (parser != null)
			{
				Range range;

				if (firstChar == '"')
				{
					range = lexer.find(HTMLParser.DOUBLE_QUOTED_ATTRIBUTE_DELIMITER_GROUP);
				}
				else
				{
					range = lexer.find(HTMLParser.SINGLE_QUOTED_ATTRIBUTE_DELIMITER_GROUP);
				}

				// process nested language
				this.processNestedLanguage(parser, range.getStartingOffset(), false);

				// capture closing quote
				lexer.setGroup(HTMLParser.ATTRIBUTE_LANGUAGE_GROUP);
				this.advance();
			}
		}

		lexer.setGroup(DEFAULT_GROUP);
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

		this.assertAndAdvance(HTMLTokenTypes.CDATA_START, "error.cdata"); //$NON-NLS-1$

		// grab text
		this.assertAndAdvance(HTMLTokenTypes.CDATA_END, "error.cdata.close"); //$NON-NLS-1$
	}

	/**
	 * parseEndTag
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private void parseEndTag() throws LexerException, ParseException
	{
		Lexeme currentLexeme2 = this.currentLexeme;

		// skip over close tag
		this.assertAndAdvance(HTMLTokenTypes.END_TAG, "error.tag.end"); //$NON-NLS-1$
		
		// only close current element if current lexeme and element have the same tag name
		if (this._currentElement != null)
		{
			String tagName = HTMLUtils.stripTagEndings(currentLexeme2.getText());
			
			if (this._currentElement.getName().equalsIgnoreCase(tagName))
			{
				this.closeElement();
			}
		}
		
		// handle possible inner text
		ILexer lexer = this.getLexer();

		// switch to text group
		lexer.setGroup("text"); //$NON-NLS-1$

		// advance over tag close
		this.assertAndAdvance(HTMLTokenTypes.GREATER_THAN, "error.tag.end.close"); //$NON-NLS-1$

		// switch back to default group
		lexer.setGroup(DEFAULT_GROUP);

		if (this.currentLexeme == EOS || this.isType(HTMLTokenTypes.ERROR))
		{
			if (this.currentLexeme != EOS)
			{
				lexer.setCurrentOffset(this.currentLexeme.offset);
				this.removeLexeme(this.currentLexeme);
			}

			// rescan in case we have a false EOS
			this.advance();
		}
	}

	/**
	 * parsePercentInstruction
	 * 
	 * @throws LexerException
	 * @throws ParseException
	 */
	private void parsePercentInstruction() throws LexerException, ParseException
	{
		ILexer lexer = this.getLexer();

		// find offset
		Range range = lexer.find(PERCENT_INSTRUCTION_DELIMITER_GROUP);

		int offset = range.getStartingOffset();

		if (range.isEmpty())
		{
			offset = lexer.getSourceLength();
		}

		String elementName = this.currentLexeme.getText();
		IParser parser = this.languageRegistry.getPercentInstructionLanguage(elementName);

		if (parser != null)
		{
			this.processNestedLanguage(parser, offset);
		}
		else
		{
			// switch to percent-instruction group
			lexer.setGroup(PERCENT_INSTRUCTION_GROUP);

			this.assertAndAdvance(HTMLTokenTypes.PERCENT_OPEN, "error.percent.instruction"); //$NON-NLS-1$
		}

		// switch back to default
		lexer.setGroup(DEFAULT_GROUP);

		// advance over '%>'
		this.advance();
	}

	/**
	 * parseProcessingInstruction
	 * 
	 * @throws LexerException
	 * @throws ParseException
	 */
	private void parseProcessingInstruction() throws LexerException, ParseException
	{
		ILexer lexer = this.getLexer();
		String elementName = this.currentLexeme.getText();
		int offset = lexer.getSourceLength();

		if (this.languageRegistry.getHandlesEOF(elementName) == false)
		{
			// find offset
			Range range = lexer.find(PROCESSING_INSTRUCTION_DELIMITER_GROUP);

			if (range.isEmpty() == false)
			{
				offset = range.getStartingOffset();
			}
		}

		IParser parser = this.languageRegistry.getProcessingInstructionLanguage(elementName);

		if (parser != null)
		{
			this.processNestedLanguage(parser, offset);
		}
		else
		{
			// switch to cdata-section group
			lexer.setGroup(PROCESSING_INSTRUCTION_GROUP);

			this.assertAndAdvance(HTMLTokenTypes.PI_OPEN, "error.pi"); //$NON-NLS-1$
		}

		// switch back to default
		lexer.setGroup(DEFAULT_GROUP);

		// advance over '?>'
		this.advance();
	}

	/**
	 * parseStartTag
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private void parseStartTag() throws ParseException, LexerException
	{
		Lexeme startTag = this.currentLexeme;
		
		// make sure we're currently on a start tag
		this.assertType(HTMLTokenTypes.START_TAG, "error.tag.start"); //$NON-NLS-1$

		// create the new element
		HTMLElementNode element = (HTMLElementNode) this.createNode(HTMLParseNodeTypes.ELEMENT, this.currentLexeme);

		// push the element onto our stack
		this.openElement(element);

		// grab element name
		String elementName = this.currentLexeme.getText().substring(1).toLowerCase();

		// advance over beginning of element
		this.advance();

		// check for possible cache fault
		if (this.isEOS() == false && this.currentLexeme.getLanguage().equals(this.getLanguage()) == false)
		{
			this.flushCache(TAG_DELIMITER_GROUP);
		}

		// process until we close this tag
		while (this.isEOS() == false && this.inSet(elementEndSet) == false)
		{
			switch (this.currentLexeme.typeIndex)
			{
				case HTMLTokenTypes.PI_OPEN:
					this.parseProcessingInstruction();
					break;
					
				case HTMLTokenTypes.PERCENT_OPEN:
					this.parsePercentInstruction();
					break;
					
				default:
					this.parseAttribute(element);
			}

		}

		switch (this.currentLexeme.typeIndex)
		{
			case HTMLTokenTypes.GREATER_THAN:
				// check for extension points
				if (elementName.equals("script")) //$NON-NLS-1$
				{
					this.switchToScriptLanguage();
				}
				else if (elementName.equals("style")) //$NON-NLS-1$
				{
					this.switchToStyleLanguage();
				}
				else
				{
					IParser parser = this.languageRegistry.getElementLanguage(elementName, "", ""); //$NON-NLS-1$ //$NON-NLS-2$
					
					if (parser != null)
					{
						ILexer lexer = this.getLexer();
						int offset = -1;
						
						if (this.languageRegistry.getLanguageOwnsElement(elementName))
						{
							LexemeList lexemes = this.getLexemeList();

							int startingOffset = startTag.offset;

							// get the current lexeme's index in the list
							int index = lexemes.getLexemeIndex(startTag);

							// remove token since it is (potentially) invalid
							lexemes.remove(index);

							// update the affected region
							lexemes.getAffectedRegion().includeInRange(startingOffset);
							offset = lexer.getSourceLength();
							
							lexer.setCurrentOffset(startingOffset);
						}
						else
						{
							this._closeTagNameMatcher.removeText();
							this._closeTagNameMatcher.appendText(elementName);
							
							char[] source = lexer.getSourceUnsafe();
							
							for (int i = 0; i < lexer.getSourceLength(); i++)
							{
								int candidate = this._closeTagMatcher.match(source, i, source.length);
								
								if (candidate != -1)
								{
									offset = i;
									break;
								}
							}
							
							if (offset == -1)
							{
								offset = lexer.getSourceLength();
							}
						}
						
						this.processNestedLanguage(parser, offset);
					}
					else
					{
						this.parseText();
					}
				}
				break;

			case HTMLTokenTypes.SLASH_GREATER_THAN:
				// NOTE: the current element will not equal this element if this element forbids close tags
				if (this._currentElement == element)
				{
					this.closeElement();
				}
				this.parseText();
				break;

			default:
				throwParseError("error.tag.start.unclosed"); //$NON-NLS-1$
		}
	}

	private void parseText() throws LexerException
	{
		// get reference to lexer
		ILexer lexer = this.getLexer();

		// switch to text group
		lexer.setGroup("text"); //$NON-NLS-1$

		// advance over '>' or '/>'
		this.advance();

		// switch back to default group
		lexer.setGroup(DEFAULT_GROUP);

		if (this.currentLexeme == EOS || this.isType(HTMLTokenTypes.ERROR))
		{
			if (this.currentLexeme != EOS)
			{
				lexer.setCurrentOffset(this.currentLexeme.offset);
				this.removeLexeme(this.currentLexeme);
			}

			// rescan in case we have a false EOS
			this.advance();
		}
	}

	/**
	 * processNestedLanguage
	 * 
	 * @param parser
	 * @throws ParseException
	 * @throws LexerException
	 */
	private void processNestedLanguage(IParser parser, int offset) throws LexerException, ParseException
	{
		this.processNestedLanguage(parser, offset, false);
	}

	/**
	 * processNestedLanguage
	 * 
	 * @param parser
	 * @throws ParseException
	 * @throws LexerException
	 */
	private void processNestedLanguage(IParser parser, int offset, boolean createNode) throws LexerException, ParseException
	{
		// save current lexeme for later
		Lexeme startingLexeme = this.currentLexeme;

		// parse nested language
		this.changeLanguage(parser.getLanguage(), offset, this._currentElement);

		if (createNode)
		{
			// create placeholder node for outline
			HTMLSpecialNode node = (HTMLSpecialNode) this.createNode(HTMLParseNodeTypes.SPECIAL, startingLexeme);

			// this node needs access to the lexeme list to build its label in the outline
			node.setLexemeList(this.getParseState().getLexemeList());

			// set language
			node.setNestedLanguage(parser.getLanguage());

			// open and close element
			this.openElement(node);
			this.closeElement();
		}
	}

	/**
	 * switchToScriptLanguage
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private void switchToScriptLanguage() throws ParseException, LexerException
	{
		this.switchLanguage(SCRIPT_DELIMITER_GROUP, "type"); //$NON-NLS-1$
	}

	/**
	 * switchToStyleLanguage
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private void switchToStyleLanguage() throws ParseException, LexerException
	{
		this.switchLanguage(STYLE_DELIMITER_GROUP, "type"); //$NON-NLS-1$
	}

	/**
	 * switchLanguage
	 * 
	 * @param endingDelimiterGroup
	 * @throws LexerException
	 * @throws ParseException
	 */
	private void switchLanguage(String endingDelimiterGroup, String attributeName) throws LexerException,
			ParseException
	{
		ILexer lexer = this.getLexer();

		// find offset
		Range range = lexer.find(endingDelimiterGroup);

		int offset = range.getStartingOffset();

		if (range.isEmpty())
		{
			offset = lexer.getSourceLength();
		}

		String elementName = this._currentElement.getName();
		String attributeValue = this._currentElement.getAttribute(attributeName);

		if (attributeValue == null)
		{
			attributeValue = ""; //$NON-NLS-1$
		}

		IParser parser = this.languageRegistry.getElementLanguage(elementName, attributeName, attributeValue);

		if (parser != null)
		{
			this.changeLanguage(parser.getLanguage(), offset, this._currentElement);
			
			// advance over tag close
			this.advance();
		}
		else
		{
			LexemeList lexemes = this.getLexemeList();

			lexemes.getAffectedRegion().includeInRange(offset);

			this.parseText();
		}
	}

	/**
	 * Parse XML declaration
	 * 
	 * @throws LexerException
	 * @throws ParseException
	 */
	private void parseXMLDeclaration() throws LexerException, ParseException
	{
		// switch to XML declaration
		this.getLexer().setGroup("xml-declaration"); //$NON-NLS-1$

		HTMLDeclarationNode decl = (HTMLDeclarationNode) this.createNode(HTMLParseNodeTypes.DECLARATION,
				this.currentLexeme);

		// advance over '<?xml'
		this.assertAndAdvance(HTMLTokenTypes.XML_DECL, "error.xml.declaration"); //$NON-NLS-1$

		// this._parseResults.add(decl); // always root
		decl.setVersion(this.currentLexeme.getText());

		// parse declaration
		this.assertAndAdvance(HTMLTokenTypes.VERSION, "error.xml.declaration.version"); //$NON-NLS-1$

		if (this.isType(HTMLTokenTypes.ENCODING))
		{
			decl.setEncoding(this.currentLexeme.getText());
			this.advance();
		}

		if (this.isType(HTMLTokenTypes.STANDALONE))
		{
			decl.setStandalone(this.currentLexeme.getText());
			this.advance();
		}
		decl.includeLexemeInRange(this.currentLexeme);

		this.assertAndAdvance(HTMLTokenTypes.GREATER_THAN, "error.xml.declaration.close"); //$NON-NLS-1$
	}
}
