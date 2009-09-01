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
package com.aptana.ide.editor.scriptdoc.parsing;

import java.text.ParseException;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.scriptdoc.lexing.ScriptDocTokenTypes;
import com.aptana.ide.editors.managers.FileContextManager;
import com.aptana.ide.editors.unified.folding.GenericCommentNode;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.TokenCategories;
import com.aptana.ide.metadata.IDocumentation;
import com.aptana.ide.metadata.IDocumentationStore;
import com.aptana.ide.parsing.CodeLocation;
import com.aptana.ide.parsing.ErrorMessage;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.ParserInitializationException;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Robin Debreuil
 */
public class ScriptDocParser extends ScriptDocParserBase
{
	/**
	 * INDENT_GROUP
	 */
	public static final String INDENT_GROUP = "indent"; //$NON-NLS-1$
	
	private IDocumentation _rootNode;
	private FunctionDocumentation _parsedObject;
	private IParseNode _curParent;
	private String _curScriptNamespace;

	/**
	 * Parses ScriptDoc comments, converting them to IDocumentation objects.
	 * 
	 * @throws ParserInitializationException
	 */
	public ScriptDocParser() throws ParserInitializationException
	{
		this(ScriptDocMimeType.MimeType);
	}
	
	/**
	 * Parses ScriptDoc comments, converting them to IDocumentation objects.
	 * 
	 * @throws ParserInitializationException
	 */
	public ScriptDocParser(String mimeType) throws ParserInitializationException
	{
		super(mimeType);

		this._curScriptNamespace = ""; //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.parsing.AbstractParser#parseAll(com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public synchronized void parseAll(IParseNode parentNode) throws LexerException
	{
		this._curParent = parentNode;
		this.startingIndex = -1;
		this.endingIndex = -1;

		// make sure our lexer is using our lexeme cache and switch over to our language and default group
		ILexer lexer = this.getLexer();
		lexer.setLanguageAndGroup(this.getLanguage(), "default"); //$NON-NLS-1$

		try
		{
			this._parsedObject = this.parseDocumentation();
		}
		catch (ParseException e)
		{
			lexer.setCurrentOffset(lexer.getEOFOffset());
		}

		IParseState parseState = this.getParseState();

		if (parseState instanceof ScriptDocParseState)
		{
			ScriptDocParseState scriptDocParseState = (ScriptDocParseState) parseState;
			LexemeList lexemes = this.getLexemeList();
			Lexeme lastLexeme = lexemes.get(lexemes.size() - 1); // will always have at least one
			IDocumentationStore documentationStore = scriptDocParseState.getDocumentationStore();

			documentationStore.addScriptDocObject(lexer.getCurrentOffset(), lastLexeme, this._parsedObject);
		}
		else
		{
			throw new IllegalStateException(Messages.ScriptDocParser_MustHaveScriptDocParseState);
		}

		if (endingIndex != -1 && startingIndex != -1)
		{
			GenericCommentNode node = new GenericCommentNode(startingIndex, endingIndex, "SDCOMMENT", //$NON-NLS-1$
					JSMimeType.MimeType);
			this.getParseState().addCommentRegion(node);
		}
	}

	/**
	 * Returns the last parsed Object
	 * 
	 * @return Returns the last parsed Object
	 */
	public FunctionDocumentation getParsedObject()
	{
		return this._parsedObject;
	}

	/**
	 * Parse the associated source input text ('source' must be set first).
	 * 
	 * @return A FunctionDocumentation object from the parsed source.
	 * @throws ParseException
	 * @throws LexerException
	 * @throws LexerException
	 */
	private FunctionDocumentation parseDocumentation() throws ParseException, LexerException
	{
		FunctionDocumentation fd = new FunctionDocumentation();
		this._rootNode = fd;

		boolean wasNull = false;

		if (this.currentLexeme == null)
		{
			advance(); // first parse, first time, with only script doc, can be null

			wasNull = true;
		}

		// case where there were no previous lexemes (first lex in doc is /**).
		if (this.currentLexeme == EOS)
		{
			advance();

			if (this.currentLexeme != null && this.currentLexeme != EOS)
			{
				this._curNode = new ScriptDocParseNode(this.currentLexeme);
				this._curNode.includeLexemeInRange(this.currentLexeme);
			}
			else
			{
				return fd;
			}
		}
		else
		{
			this._curNode = new ScriptDocParseNode(this.currentLexeme);

			if (!wasNull)
			{
				advance();
			}
		}

		if (this.currentLexeme != EOS)
		{
			if (this._curParent != null)
			{
				this._curParent.appendChild(this._curNode);
			}

			this._curNode.setDocument(this._parsedObject);

			// make sure first token is a start doc
			assertAndAdvance(ScriptDocTokenTypes.START_DOCUMENTATION);

			// get description if any
			fd.setDescription(parseText());

			// now parse all the parameters
			while (this._holderLexeme != EOS)
			{
				this.parseFunctionDocumentationSection(fd);
			}
		}

		return fd;
	}

	/**
	 * Set the source code to parse
	 * 
	 * @param source
	 *            The source of this Documentation.
	 */
	public void setSource(String source)
	{
		this.getLexer().setSource(source);
	}

	/**
	 * parseBaseTags
	 * 
	 * @param bd
	 * @return boolean
	 * @throws LexerException
	 */
	private boolean parseBaseTags(DocumentationBase bd) throws LexerException
	{
		boolean result = false;

		if (this._holderLexeme.getCategoryIndex() == TokenCategories.KEYWORD)
		{
			try
			{
				switch (this._holderLexeme.typeIndex)
				{
					case ScriptDocTokenTypes.ADVANCED:
						advance();
						result = true;
						break;
						
					case ScriptDocTokenTypes.AUTHOR:
						advance();
						bd.setAuthor(parseText());
						result = true;
						break;

					case ScriptDocTokenTypes.VERSION:
						advance();
						bd.setVersion(parseText());
						result = true;
						break;

					case ScriptDocTokenTypes.SEE:
						advance();
						bd.addSee(parseText());
						result = true;
						break;

					case ScriptDocTokenTypes.SDOC:
						advance();
						bd.addSDocLocation(parseText());
						result = true;
						break;

					case ScriptDocTokenTypes.NAMESPACE:
						advance();
						parseNamespace();
						result = true;
						break;

					case ScriptDocTokenTypes.COPYRIGHT:
						advance();
						parseText(); // ignore for now
						result = true;
						break;

					case ScriptDocTokenTypes.LICENSE:
						advance();
						parseText(); // ignore for now
						result = true;
						break;

					case ScriptDocTokenTypes.EXAMPLE:
						advance();
						bd.addExample(parseText());
						result = true;
						break;

					case ScriptDocTokenTypes.OVERVIEW:
						advance();
						this.parseText();
						result = true;
						break;
						
					// adding project description here to make the parse generic
					case ScriptDocTokenTypes.PROJECT_DESCRIPTION:
						advance(); // there is actually a tag for this description (@projectDescription).
						bd.setDescription(parseText());
						bd.setDocumentType(IDocumentation.TYPE_PROJECT);
						result = true;

					default:
						break;
				}
			}
			catch (ParseException e)
			{
				skipTag(e);
			}
		}
		else
		{
			try
			{
				String desc = parseText();
				// We may already have a good description--don't mess it up
				if (!StringUtils.EMPTY.equals(desc))
				{
					bd.setDescription(desc);
				}
			}
			catch (ParseException e)
			{
				skipTag(e);
			}
		}

		return result;
	}

	/**
	 * parsePropertyTags
	 * 
	 * @param pd
	 * @return boolean
	 * @throws LexerException
	 */
	private boolean parsePropertyTags(PropertyDocumentation pd) throws LexerException
	{
		boolean found = parseBaseTags(pd);

		if (found)
		{
			return true;
		}

		boolean result = false;

		if (found == false && this._holderLexeme.getCategoryIndex() == TokenCategories.KEYWORD)
		{
			try
			{
				switch (this._holderLexeme.typeIndex)
				{
					case ScriptDocTokenTypes.MEMBER_OF:
						advance();
						parseMemberOf(pd);
						result = true;
						break;

					case ScriptDocTokenTypes.IGNORE:
						advance();
						pd.setIsIgnored(true);
						result = true;
						break;

					case ScriptDocTokenTypes.TYPE:
						advance();
						parseTypeValue(pd);
						pd.setDocumentType(IDocumentation.TYPE_PROPERTY);
						result = true;
						break;

					case ScriptDocTokenTypes.SINCE:
						advance();
						pd.setSince(parseText());
						result = true;
						break;

					case ScriptDocTokenTypes.DEPRECATED:
						advance();
						pd.setIsDeprecated(true);
						pd.setDeprecatedDescription(parseText());
						result = true;
						break;

					case ScriptDocTokenTypes.PRIVATE:
						advance();
						pd.setIsPrivate(true);
						result = true;
						break;

					case ScriptDocTokenTypes.PROPERTY:
						advance();
						pd.setDocumentType(IDocumentation.TYPE_PROPERTY);
						break;

					case ScriptDocTokenTypes.INTERNAL:
						advance();
						pd.setIsInternal(true);
						result = true;
						break;

					case ScriptDocTokenTypes.NATIVE:
						advance();
						pd.setIsNative(true);
						result = true;
						break;

					case ScriptDocTokenTypes.ALIAS:
						advance();
						parseAlias(pd);
						result = true;
						break;

					case ScriptDocTokenTypes.ID:
						advance();
						parseID(pd);
						result = true;
						break;

					default:
						break;
				}
			}
			catch (ParseException e)
			{
				skipTag(e);
			}
		}

		return result;
	}

	/**
	 * parseFunctionDocumentationSection
	 * 
	 * @param fd
	 * @throws LexerException
	 */
	private void parseFunctionDocumentationSection(FunctionDocumentation fd) throws LexerException
	{
		boolean found = parsePropertyTags(fd);

		if (found)
		{
			return;
		}

		if (this._holderLexeme.getCategoryIndex() == TokenCategories.KEYWORD)
		{
			try
			{
				switch (this._holderLexeme.typeIndex)
				{
					case ScriptDocTokenTypes.PARAM:
						advance();
						parseParams(fd);
						fd.setDocumentType(IDocumentation.TYPE_FUNCTION);
						break;

					case ScriptDocTokenTypes.RETURN:
						advance();
						parseReturnValue(fd);
						fd.setDocumentType(IDocumentation.TYPE_FUNCTION);
						break;

					case ScriptDocTokenTypes.EXCEPTION:
						advance();
						parseException(fd);
						break;

					case ScriptDocTokenTypes.CLASS_DESCRIPTION:
						advance();
						fd.setClassDescription(parseText());
						break;

					case ScriptDocTokenTypes.CONSTRUCTOR:
						advance();
						fd.setIsConstructor(true);
						fd.setDocumentType(IDocumentation.TYPE_FUNCTION);
						break;

					case ScriptDocTokenTypes.METHOD:
						advance();
						fd.setIsMethod(true);
						fd.setDocumentType(IDocumentation.TYPE_FUNCTION);
						fd.setMethodName(parseText());
						break;

					case ScriptDocTokenTypes.EXTENDS:
						advance();
						parseExtends(fd);
						fd.setDocumentType(IDocumentation.TYPE_FUNCTION);
						break;

					case ScriptDocTokenTypes.IGNORE:
						advance();
						fd.setIsIgnored(true);
						break;

					default:
						skipTag(new ParseException(Messages.ScriptDocParser_InvalidSyntax
								+ this._holderLexeme.getType(), 0));
						break;
				}
			}
			catch (ParseException e)
			{
				skipTag(e);
			}
		}
		else if (this._holderLexeme.typeIndex == ScriptDocTokenTypes.END_DOCUMENTATION)
		{
			this._curNode.includeLexemeInRange(this.currentLexeme);
			advance();
		}
		else
		{
			skipTag(new ParseException(Messages.ScriptDocParser_InvalidSyntaxForStatement
					+ this._holderLexeme.getType(), 0));
		}
	}

	/**
	 * parseAlias
	 * 
	 * @param pd
	 * @throws ParseException
	 * @throws LexerException
	 */
	private void parseAlias(PropertyDocumentation pd) throws ParseException, LexerException
	{
		pd.getAliases().addType(getIdentifier());
	}

	/**
	 * parseID
	 * 
	 * @param pd
	 * @throws ParseException
	 * @throws LexerException
	 */
	private void parseID(PropertyDocumentation pd) throws ParseException, LexerException
	{
		LexemeList lexemes = this.getLexemeList();
		Lexeme start = this.currentLexeme;
		String id = getIdentifier();
		Lexeme end = lexemes.get(lexemes.size() - 1);

		String dot = this._curScriptNamespace.equals("") ? "" : "."; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		String fullname = this._curScriptNamespace + dot + id;
		String uri = FileContextManager.getURIFromFileIndex(this.getParseState().getFileIndex());

		pd.setID(fullname, new CodeLocation(uri, start, end));
	}

	/**
	 * parseNamespace
	 * 
	 * @throws ParseException
	 * @throws LexerException
	 */
	private void parseNamespace() throws ParseException, LexerException
	{
		this._curScriptNamespace = getIdentifier();

		// TODO: need to allow namespace definitions to describe themselves (so save this text).
		this.parseText();
	}

	/**
	 * parseReturnValue
	 * 
	 * @param pd
	 * @throws ParseException
	 * @throws LexerException
	 */
	private void parseReturnValue(PropertyDocumentation pd) throws ParseException, LexerException
	{
		parseIntoTypedDescription(pd.getReturn(), false);
	}

	/**
	 * parseTypeValue
	 * 
	 * @param pd
	 * @throws ParseException
	 * @throws LexerException
	 */
	private void parseTypeValue(PropertyDocumentation pd) throws ParseException, LexerException
	{
		boolean hasType = this._holderLexeme.typeIndex == ScriptDocTokenTypes.LCURLY;
		boolean isIdent = (this._holderLexeme.typeIndex == ScriptDocTokenTypes.IDENTIFIER)
				|| (this._holderLexeme.typeIndex == ScriptDocTokenTypes.TEXT)
				|| (this._holderLexeme.typeIndex == ScriptDocTokenTypes.ELLIPSIS);

		if (hasType || !isIdent)
		{
			// this is the normal sdoc case
			// @type {type} description or
			// @type description
			parseIntoTypedDescription(pd.getReturn(), false);
		}
		else
		{
			// this is the jsdoc case
			// @type type
			// but still might be the sdoc
			// @type description
			// we will choose the jsdoc way if there is only a single identifier compatible word (ex. YAHOO.xxx)
			String type = getIdentifier();
			boolean hasFollowText = (this._holderLexeme.typeIndex == ScriptDocTokenTypes.IDENTIFIER)
					|| (this._holderLexeme.typeIndex == ScriptDocTokenTypes.TEXT);

			if (hasFollowText)
			{
				String text = type + parseText();

				pd.getReturn().setDescription(text);
			}
			else
			{
				pd.getReturn().addType(type);
			}
		}
	}

	/**
	 * parseParams
	 * 
	 * @param fd
	 * @throws ParseException
	 * @throws LexerException
	 */
	private void parseParams(FunctionDocumentation fd) throws ParseException, LexerException
	{
		TypedDescription td = new TypedDescription();

		parseIntoTypedDescription(td, true);
		fd.addParam(td);
	}

	/**
	 * parseExtends
	 * 
	 * @param fd
	 * @throws ParseException
	 * @throws LexerException
	 */
	private void parseExtends(FunctionDocumentation fd) throws ParseException, LexerException
	{
		parseIntoTypedDescription(fd.getExtends(), false);
	}

	/**
	 * parseException
	 * 
	 * @param fd
	 * @throws ParseException
	 * @throws LexerException
	 */
	private void parseException(FunctionDocumentation fd) throws ParseException, LexerException
	{
		TypedDescription td = new TypedDescription();

		parseIntoTypedDescription(td, false);
		fd.addException(td);
	}

	/**
	 * parseMemberOf
	 * 
	 * @param pd
	 * @throws ParseException
	 * @throws LexerException
	 */
	private void parseMemberOf(PropertyDocumentation pd) throws ParseException, LexerException
	{
		parseIntoTypedDescription(pd.getMemberOf(), false);
	}

	/**
	 * parseIntoTypedDescription
	 * 
	 * @param td
	 * @param includeName
	 * @throws ParseException
	 * @throws LexerException
	 */
	private void parseIntoTypedDescription(TypedDescription td, boolean includeName) throws ParseException,
			LexerException
	{
		// curlies are always optional
		boolean hasType = this._holderLexeme.typeIndex == ScriptDocTokenTypes.LCURLY;

		if (hasType)
		{
			assertAndAdvance(ScriptDocTokenTypes.LCURLY);

			if (this._holderLexeme.typeIndex == ScriptDocTokenTypes.IDENTIFIER
					|| this._holderLexeme.typeIndex == ScriptDocTokenTypes.ELLIPSIS)
			{
				// identifier includes dots
				td.addType(getIdentifier());
				// advance();
			}
			else
			{
				throwParseError(Messages.ScriptDocParser_InvalidID);
			}
			while (this._holderLexeme != EOS
					&& (this._holderLexeme.typeIndex == ScriptDocTokenTypes.COMMA
							|| this._holderLexeme.typeIndex == ScriptDocTokenTypes.PIPE || this._holderLexeme.typeIndex == ScriptDocTokenTypes.FORWARD_SLASH))
			{
				advance();
				td.addType(getIdentifier());
			}
			assertAndAdvance(ScriptDocTokenTypes.RCURLY);
		}
		// add name, if any
		if (includeName)
		{
			td.setName(getIdentifier());
		}
		// add text
		td.setDescription(parseText());

	}

	/**
	 * getIdentifier
	 * 
	 * @return String
	 * @throws ParseException
	 * @throws LexerException
	 */
	private String getIdentifier() throws ParseException, LexerException
	{
		String result = ""; //$NON-NLS-1$
		boolean isOptional = false;

		if (this._holderLexeme.typeIndex == ScriptDocTokenTypes.LBRACKET)
		{
			isOptional = true;
			result += "["; //$NON-NLS-1$
			advance();
		}
		if (this._holderLexeme.typeIndex == ScriptDocTokenTypes.IDENTIFIER
				|| this._holderLexeme.typeIndex == ScriptDocTokenTypes.TEXT)
		{
			result += this._holderLexeme.getText();
			advance();
		}
		else if (this._holderLexeme.typeIndex == ScriptDocTokenTypes.ELLIPSIS)
		{
			result = this._holderLexeme.getText();
			advance();
		}
		else
		{
			throwParseError(Messages.ScriptDocParser_InvalidIdInComment);
		}
		if (isOptional)
		{
			result += "]"; //$NON-NLS-1$
			assertAndAdvance(ScriptDocTokenTypes.RBRACKET);
		}

		return result;
	}

	/**
	 * parseText
	 * 
	 * @return String
	 * @throws ParseException
	 * @throws LexerException
	 */
	private String parseText() throws ParseException, LexerException
	{
		StringBuilder text = new StringBuilder();

		loop: while (this._holderLexeme != EOS)
		{
			switch (this._holderLexeme.getCategoryIndex())
			{
				case TokenCategories.WHITESPACE:
					break;

				case TokenCategories.LITERAL:
					String hText = this._holderLexeme.getText();
					if (hText.startsWith("@")) //$NON-NLS-1$
					{
						break loop;
					}
					text.append(hText);
					text.append(" "); //$NON-NLS-1$
					break;

				case TokenCategories.KEYWORD:
					// some keywords will be allowed (ex. @link)
					if (this._holderLexeme.typeIndex == ScriptDocTokenTypes.LINK
							|| this._holderLexeme.typeIndex == ScriptDocTokenTypes.SINCE)
					{
						text.append(this._holderLexeme.getText());
					}
					else
					{
						break loop;
					}
					break;

				case TokenCategories.PUNCTUATOR:
					if (this._holderLexeme.typeIndex == ScriptDocTokenTypes.END_DOCUMENTATION)
					{
						this._curNode.includeLexemeInRange(this.currentLexeme);
						break loop;
					}
					else
					{
						text.append(this._holderLexeme.getText());
						text.append(" "); //$NON-NLS-1$
					}
					break;

				default:
					text.append(this._holderLexeme.getText());
				 	text.append(" "); //$NON-NLS-1$
					break;

			}

			advance();
		}

		return text.toString();
	}

	/**
	 * getFollowText
	 * 
	 * @return String
	 */
	private String getFollowText()
	{
		ILexer lexer = this.getLexer();

		if (lexer.getSource().length() > lexer.getCurrentOffset())
		{
			int len = Math.min(6, lexer.getSource().length() - lexer.getCurrentOffset());

			return "\"" + lexer.getSource().substring(lexer.getCurrentOffset(), lexer.getCurrentOffset() + len) + "\""; //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			return "end of document"; //$NON-NLS-1$
		}
	}

	/**
	 * assertAndAdvance
	 * 
	 * @param type
	 * @throws ParseException
	 * @throws LexerException
	 */
	private void assertAndAdvance(int type) throws ParseException, LexerException
	{
		this.assertType(type);
		this.advance();
	}

	/**
	 * assertType
	 * 
	 * @param type
	 * @throws ParseException
	 */
	private void assertType(int type) throws ParseException
	{
		if (this._holderLexeme.typeIndex != type)
		{
			String targetType = ScriptDocTokenTypes.getName(type);
			String actualType = ScriptDocTokenTypes.getName(this._holderLexeme.typeIndex);

			if (this._holderLexeme == EOS)
			{
				actualType = getFollowText();
			}

			this.throwParseError(Messages.ScriptDocParser_Expected + targetType + Messages.ScriptDocParser_Found
					+ actualType);
		}
	}

	/**
	 * Advance until next valid section
	 * 
	 * @throws LexerException
	 * @throws LexerException
	 */
	private void skipTag(ParseException e) throws LexerException
	{
		LexemeList lexemes = this.getLexemeList();
		Lexeme targetLexeme = null;

		// skip until next valid keyword or eos
		if (this._holderLexeme == EOS && lexemes.size() > 1)
		{
			targetLexeme = lexemes.get(lexemes.size() - 2);
			// en.add();
		}
		else
		{
			// en.add(curLexeme);
			advance();

			while (this._holderLexeme != EOS)
			{
				targetLexeme = this._holderLexeme;

				if (this._holderLexeme.getCategoryIndex() != TokenCategories.KEYWORD)
				{
					// curLexeme.setCommandNode(en);
					// en.add(curLexeme);
					advance();
				}
				else
				{
					if (this._holderLexeme.typeIndex == ScriptDocTokenTypes.LINK)
					{
						// curLexeme.setCommandNode(en);
						// en.add(curLexeme);
						advance();
					}
					else
					{
						break;
					}
				}
			}
		}

		ErrorMessage en = new ErrorMessage(e.getMessage(), targetLexeme);

		if (e != null)
		{
			this._rootNode.addError(en);
		}
	}

	/**
	 * Throw a parse exception
	 * 
	 * @param message
	 *            The exception message
	 * @throws ParseException
	 */
	protected void throwParseError(String message) throws ParseException
	{
		// determine line number
		LexemeList lexemes = this.getLexemeList();
		int lastValid = (this._holderLexeme == EOS) ? lexemes.size() - 2 : lexemes.size() - 1;

		if (lastValid < 0)
		{
			message = Messages.ScriptDocParser_PrematureEndOfDoc;
		}
		else
		{
			String position;

			if (this._holderLexeme != EOS)
			{
				position = " [" + this._holderLexeme.getText() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
			{
				position = " [" + getFollowText() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
			}

			message = Messages.ScriptDocParser_ParseError + position + ": " + message; //$NON-NLS-1$
		}

		throw new ParseException(message, -1);
	}
}
