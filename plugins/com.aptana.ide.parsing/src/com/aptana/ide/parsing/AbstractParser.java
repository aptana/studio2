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
package com.aptana.ide.parsing;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.ILexerBuilder;
import com.aptana.ide.lexer.IToken;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.LexerInitializationException;
import com.aptana.ide.lexer.Range;
import com.aptana.ide.lexer.Token;
import com.aptana.ide.lexer.TokenList;
import com.aptana.ide.lexer.matcher.MatcherLexerBuilder;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.parsing.nodes.IParseNodeFactory;

/**
 * @author Kevin Lindsey
 */
public abstract class AbstractParser implements IParser
{
	/**
	 * End of stream lexeme. Used internally
	 */
	protected static final Lexeme EOS;

	/**
	 * The current lexeme being considered in the parse
	 */
	protected Lexeme currentLexeme;
	
	private ILexer _lexer;
	private TokenList _tokenList;
	private IParser _parent;
	private List<IParser> _children;
	private Map<String,IParser> _registeredParsers;
	private IParseState _parseState;
	private String _language;
	private ILanguageChangeListener _languageChangeListener;

	/**
	 * static constructor
	 */
	static
	{
		IToken t = new Token(null);
		t.setCategory("Metadata"); //$NON-NLS-1$
		t.setType("$"); //$NON-NLS-1$

		EOS = new Lexeme(t, "$", -1); //$NON-NLS-1$
	}
	
	/**
	 * AbstractParser
	 * 
	 * @param tokenList
	 * @throws ParserInitializationException
	 */
	protected AbstractParser(TokenList tokenList) throws ParserInitializationException
	{
		this._tokenList = tokenList;
		this._language = tokenList.getLanguage();
		
		this.initialize();
	}
	
	/**
	 * Creates a parser for the language specified and from the resource specified by the stream.
	 * 
	 * @param language -
	 *            language mime type
	 * @throws ParserInitializationException
	 */
	protected AbstractParser(String language) throws ParserInitializationException
	{
		this._language = language;
		
		this.initialize();
	}

	/**
	 * initialize
	 * 
	 * @throws ParserInitializationException
	 */
	protected void initialize() throws ParserInitializationException
	{
		this._registeredParsers = new HashMap<String,IParser>();
		
		this.registerParser(this);

		this.addChildParsers();

		try
		{
			this.buildLexer();
		}
		catch (LexerException e)
		{
			String message = Messages.ParserBase_UnableToCreateParserLexerException;

			throw new ParserInitializationException(message, e);
		}
		catch (LexerInitializationException e)
		{
			String message = Messages.ParserBase_UnableToCreateParserLexerInitializationException;

			throw new ParserInitializationException(message, e);
		}
	}
	
	/**
	 * addChildParser
	 * 
	 * @param child
	 */
	protected void addChildParser(IParser child)
	{
		// check for valid input
		if (child == null)
		{
			throw new IllegalArgumentException(Messages.ParserBase_ChildMustNotBeNull);
		}

		// make sure we have a list to which we can add
		if (this._children == null)
		{
			this._children = new ArrayList<IParser>();
		}

		// add the child
		this._children.add(child);

		// register its language
		this.registerParser(child);

		// set this as the parent
		if (child instanceof AbstractParser)
		{
			((AbstractParser) child)._parent = this;
		}
	}

	/**
	 * addChildParsers
	 * 
	 * @throws ParserInitializationException
	 */
	protected void addChildParsers() throws ParserInitializationException
	{
		// default implementation is empty
	}

	/**
	 * addGrammars
	 * 
	 * @param lexerBuilder
	 * @throws LexerException
	 */
	protected void addGrammars(ILexerBuilder lexerBuilder) throws LexerException
	{
		this.addLexerGrammar(lexerBuilder);

		if (this._children != null)
		{
			// add each parser's lexer grammar
			for (int i = 0; i < this._children.size(); i++)
			{
				IParser parser = this._children.get(i);

				if (parser instanceof AbstractParser)
				{
					((AbstractParser) parser).addGrammars(lexerBuilder);
				}
			}
		}
	}

	/**
	 * addLexeme
	 * 
	 * @param lexeme
	 */
	protected void addLexeme(Lexeme lexeme)
	{
		if (lexeme == null)
		{
			throw new IllegalArgumentException(Messages.ParserBase_LexemeMustNotBeNull);
		}

		this.getLexemeList().add(lexeme);
	}

	/**
	 * This method loads the parser's lexer grammar using the specified builder. This allows multiple languages to share
	 * a single lexer.
	 * 
	 * @param builder
	 *            The lexer builder being used to generate this language's lexer
	 * @throws LexerException
	 */
	public void addLexerGrammar(ILexerBuilder builder) throws LexerException
	{
		if (this._tokenList != null)
		{
			builder.addTokenList(this._tokenList);
		}
	}

	/**
	 * Advance to the next lexeme in the lexeme stream
	 * 
	 * @throws LexerException
	 */
	protected void advance() throws LexerException
	{
		ILexer lexer = this.getLexer();
		Lexeme currentLexeme = EOS;

		if (lexer.isEOS() == false)
		{
			boolean inWhitespace = true;

			while (inWhitespace)
			{
				if (lexer.isEOS() == false)
				{
					currentLexeme = this.getNextLexemeInLanguage();

					if (currentLexeme == null && lexer.isEOS() == false)
					{
						// Switch to error group.
						// NOTE: We want setGroup's exception to propagate since
						// that indicates an internal inconsistency when it
						// fails
						lexer.setGroup("error"); //$NON-NLS-1$

						currentLexeme = lexer.getNextLexeme();
					}

					if (currentLexeme == null)
					{
						// couldn't recover from error, so mark as end of stream
						// NOTE: We may want to throw an exception here since we
						// should be able to return at least an ERROR token
						currentLexeme = EOS;
						inWhitespace = false;
					}
					else
					{
						this.addLexeme(currentLexeme);
						inWhitespace = false;
					}
				}
			}
		}

		this.currentLexeme = currentLexeme;
	}

	/**
	 * Advance to the next lexeme if the current one is of the specified name
	 * 
	 * @param type
	 *            The name index to test against the current lexeme
	 * @param errorKey
	 *            A key used to look up an error message from our error property list
	 * @throws LexerException
	 * @throws ParseException
	 */
	protected void assertAndAdvance(int type, String errorKey) throws LexerException, ParseException
	{
		this.assertType(type, errorKey);
		this.advance();
	}

	/**
	 * Make sure the current token is in the specified set
	 * 
	 * @param set
	 *            The target set of lexeme type indexes to test against
	 * @param errorKey
	 *            A key used to look up an error message from our error property list
	 * @throws ParseException
	 */
	protected void assertInSet(int[] set, String errorKey) throws ParseException
	{
		if (this.inSet(set) == false)
		{
			this.throwParseError(errorKey);
		}
	}

	/**
	 * Make sure the current token is of the specified type
	 * 
	 * @param type
	 *            The type index to compare against the current token
	 * @param errorKey
	 *            A key used to look up an error message from our error property list
	 * @throws ParseException
	 */
	protected void assertType(int type, String errorKey) throws ParseException
	{
		if (this.currentLexeme.typeIndex != type)
		{
			this.throwParseError(errorKey);
		}
	}

	/**
	 * buildLexer
	 * 
	 * @throws LexerException
	 * @throws LexerInitializationException
	 */
	protected void buildLexer() throws LexerException, LexerInitializationException
	{
		// create lexer builder
		ILexerBuilder lexerBuilder = new MatcherLexerBuilder();

		// add this parser's lexer grammar and all descendent lexer grammars
		this.addGrammars(lexerBuilder);

		// create lexer
		this._lexer = lexerBuilder.buildLexer();

		// make sure all lexers have been initialized and in a good starting state
		this.initializeLexers();
	}

	/**
	 * @see com.aptana.ide.parsing.IParser#changeLanguage(java.lang.String, int, IParseNode)
	 */
	public void changeLanguage(String mimeType, int offset, IParseNode parentNode) throws LexerException,
			ParseException
	{
		IParser targetParser = this.getParserForMimeType(mimeType);
		ILexer lexer = this.getLexer();

		if (targetParser != null)
		{
			// set EOF for the new language
			int parentOffset = lexer.getEOFOffset();
			int newOffset = (parentOffset > offset) ? offset : parentOffset;

			lexer.setEOFOffset(newOffset);

			// remove any stray lexemes that may have come from the end
			LexemeList lexemes = this.getLexemeList();
			int eofLexemeIndex = lexemes.getLexemeIndex(newOffset);

			if (eofLexemeIndex > -1)
			{
				Lexeme eofLexeme = lexemes.get(eofLexemeIndex);

				if (eofLexeme != null)
				{
//					lexemes.getAffectedRegion().includeInRange(newOffset);
					lexemes.getAffectedRegion().includeInRange(eofLexeme);
					lexemes.remove(eofLexeme);
				}
			}

			// cache values we need to reset after parsing
			String language = lexer.getLanguage();
			String group = lexer.getGroup();

			// fire language change event into new language
			fireLanguageChangeEvent(mimeType, lexer.getCurrentOffset());

			// parse
			targetParser.parseAll(parentNode);

			// fire language change event back into parent language
			fireLanguageChangeEvent(language, lexer.getCurrentOffset());

			// restore values
			lexer.setLanguageAndGroup(language, group);
			lexer.setEOFOffset(parentOffset);
		}
		else
		{
			lexer.setCurrentOffset(offset);
		}
	}

	/**
	 * @see com.aptana.ide.parsing.IParser#createParseState(com.aptana.ide.parsing.IParseState)
	 */
	public IParseState createParseState(IParseState parent)
	{
		IParseState result;

		if (parent == null)
		{
			result = new ParseStateChild(getLanguage());
		}
		else
		{
			result = new ParseStateChild(getLanguage(), parent);
		}

		return result;
	}

	/**
	 * fireLanguageChangeEvent
	 * 
	 * @param mimeType
	 * @param currentOffset
	 */
	private void fireLanguageChangeEvent(String mimeType, int currentOffset)
	{
		ILanguageChangeListener handler = this.getLanguageChangeListener();

		if (handler != null)
		{
			handler.startNewLanguage(mimeType, currentOffset);
		}
	}

	/**
	 * Clear the lexeme cache up to and including the specified delimiter. This method is called when it has been
	 * determined that the lexer's cache (the lexeme list) has returned a faulty lexeme. A faulty lexeme is generally
	 * considered to be a lexeme whose language is of an unexpected type.
	 * 
	 * @param delimiterGroupName
	 * @throws LexerException
	 */
	protected void flushCache(String delimiterGroupName) throws LexerException
	{
		ILexer lexer = this.getLexer();

		// find offset
		LexemeList lexemes = this.getLexemeList();
		Range range = lexer.find(delimiterGroupName);
		int startingOffset = this.currentLexeme.offset;
		int endingOffset = range.isEmpty() ? lexer.getEOFOffset() : range.getEndingOffset();
		int startingIndex = lexemes.getLexemeCeilingIndex(startingOffset);
		int endingIndex = lexemes.getLexemeFloorIndex(endingOffset);

		if (startingIndex == -1)
		{
			startingIndex = endingIndex;
		}

		if (endingIndex == -1)
		{
			endingIndex = startingIndex;
		}

		if (startingIndex != -1 && endingIndex != -1)
		{
			lexemes.getAffectedRegion().includeInRange(endingOffset);
			lexemes.remove(startingIndex, endingIndex);

			lexer.setCurrentOffset(this.currentLexeme.offset);

			this.advance();
		}
		else
		{
			// throw new IllegalStateException("ParserBase.flushCache: Internal inconsistency");
		}
	}

	/**
	 * getEndingOffset
	 * 
	 * @return int
	 */
	public int getEndingOffset()
	{
		int result;

		if (this._parent != null)
		{
			result = this._lexer.getSourceLength();
		}
		else
		{
			result = this.getLexemeList().getAffectedRegion().getEndingOffset();
		}

		return result;
	}

	/**
	 * Get this parser's language type
	 * 
	 * @return Returns the name of the language this parser targets
	 */
	public String getLanguage()
	{
		return _language;
	}

	/**
	 * @see com.aptana.ide.parsing.IParser#getLanguageChangeListener()
	 */
	public ILanguageChangeListener getLanguageChangeListener()
	{
		ILanguageChangeListener result = null;

		if (this._parent != null)
		{
			result = this._parent.getLanguageChangeListener();
		}
		else
		{
			result = this._languageChangeListener;
		}

		return result;
	}

	/**
	 * getLexemList
	 * 
	 * @return _lexemeList
	 */
	protected LexemeList getLexemeList()
	{
		LexemeList result = null;

		if (this._parent != null)
		{
			if (this._parent instanceof AbstractParser)
			{
				result = ((AbstractParser) this._parent).getLexemeList();
			}
		}
		else
		{
			result = this._parseState.getLexemeList();
		}

		return result;
	}

	/**
	 * Get the lexer associated with this parser
	 * 
	 * @return Returns the lexer used by this parser
	 */
	public ILexer getLexer()
	{
		ILexer result;

		if (this._parent != null)
		{
			// we're part of a composite, so use the composite lexer
			result = this._parent.getLexer();
		}
		else
		{
			// we're a stand-alone parser, so use our local lexer
			result = this._lexer;
		}

		return result;
	}

	/**
	 * getNextLexemeInLanguage
	 * 
	 * @return Lexeme
	 * @throws LexerException
	 */
	protected Lexeme getNextLexemeInLanguage() throws LexerException
	{
		ILexer lexer = this.getLexer();
		Lexeme result = null;

		while (result == null && lexer.isEOS() == false)
		{
			result = lexer.getNextLexeme();

			// if this is a stale lexeme (from a different language) back up, damage the whole partition, and re-parse.
			if (result != null && result != EOS && !result.getLanguage().equals(this.getLanguage()))
			{
				LexemeList lexemes = this.getLexemeList();

				lexemes.getAffectedRegion().includeInRange(result);
				this.removeLexeme(result);
				lexer.setCurrentOffset(result.offset);
				result = lexer.getNextLexeme();
			}

			if (result == null && lexer.isEOS() == false)
			{
				// if we're already in the error group, then abort
				if ("error".equals(lexer.getGroup())) //$NON-NLS-1$
				{
					break;
				}

				// Switch to error group.
				lexer.setGroup("error"); //$NON-NLS-1$

				// get error lexeme
				result = lexer.getNextLexeme();

				// if we failed to get a new lexeme and we're still in the error state,
				// then we need to abort to prevent an infinite loop
				if (result == null && "error".equals(lexer.getGroup())) //$NON-NLS-1$
				{
					break;
				}
			}
		}

		return result;
	}

	/**
	 * getParseNodeFactory
	 * 
	 * @return IParseNodeFactory
	 */
	protected IParseNodeFactory getParseNodeFactory()
	{
		IParseState parseState = this.getParseState();
		IParseNodeFactory result = null;
		
		if (parseState != null)
		{
			result = parseState.getParseNodeFactory();
		}
		
		return result;
	}

	/**
	 * getParserForLanguage
	 * 
	 * @param language
	 * @return IParser
	 */
	public IParser getParserForMimeType(String language)
	{
		return this._registeredParsers.get(language);
	}

	/**
	 * getParseRootNode
	 * 
	 * @param parentNode
	 * @param baseNodeClass
	 * @return
	 */
	protected IParseNode getParseRootNode(IParseNode parentNode, Class<?> baseNodeClass)
	{
		// set node to be used as the root node for the results of this parse
		IParseNode rootNode;

		if (parentNode == null || baseNodeClass.isAssignableFrom(parentNode.getClass()) == false)
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
		
		return rootNode;
	}
	
	/**
	 * getParseState
	 * 
	 * @return IParseState
	 */
	protected IParseState getParseState()
	{
		IParseState result = null;

		if (this._parent != null)
		{
			if (this._parent instanceof AbstractParser)
			{
				result = ((AbstractParser) this._parent).getParseState();
				result = result.getParseState(this.getLanguage());
			}
		}
		else
		{
			result = this._parseState;
		}

		return result;
	}

	/**
	 * hasParent
	 * 
	 * @return boolean
	 */
	protected boolean hasParent()
	{
		return this._parent != null;
	}

	/**
	 * Perform any initializations on the lexer now that it has been created
	 * 
	 * @throws LexerException
	 */
	public void initializeLexer() throws LexerException
	{
		ILexer lexer = this.getLexer();
		String language = this.getLanguage();

		lexer.setLanguageAndGroup(language, "default"); //$NON-NLS-1$
	}

	/**
	 * initializeLexers
	 * 
	 * @throws LexerException
	 */
	protected void initializeLexers() throws LexerException
	{
		this.initializeLexer();

		if (this._children != null)
		{
			// allow each parser to initialize its lexer settings
			for (int i = 0; i < this._children.size(); i++)
			{
				IParser parser = this._children.get(i);

				if (parser instanceof AbstractParser)
				{
					((AbstractParser) parser).initializeLexer();
				}
			}
		}
	}

	/**
	 * Determine if the current lexeme is in the specified set
	 * 
	 * @param set
	 *            The target set of lexeme name indexes to test against
	 * @return Returns true if the current lexeme exists in the specified set
	 */
	protected boolean inSet(int[] set)
	{
		boolean result = false;

		if (this.currentLexeme != null)
		{
			int typeIndex = this.currentLexeme.typeIndex;
		
			// NOTE: These sets should be small (<= 10) and linear search wins over
			// binary search with those sizes. Plus our lists are sorted, so we can
			// stop as soon as we reach values greater than what we're looking for
			for (int i = 0; i < set.length; i++)
			{
				int current = set[i];
				
				if (current >= typeIndex)
				{
					result = (current == typeIndex);
					break;
				}
			}
		}

		return result;
	}

	/**
	 * Determine if we are at the end of the source we are currently parsing
	 * 
	 * @return Returns true if we are past the end of the source
	 */
	protected boolean isEOS()
	{
		return this.currentLexeme == EOS;
	}

	/**
	 * Determine if the current lexeme is of the given type
	 * 
	 * @param type
	 *            The type to test against the current lexeme
	 * @return Returns true if the current lexeme is of the specified type
	 */
	protected boolean isType(int type)
	{
		return this.currentLexeme.typeIndex == type;
	}

	/**
	 * @see com.aptana.ide.parsing.IParser#parse(com.aptana.ide.parsing.IParseState)
	 */
	public synchronized IParseNode parse(IParseState parseState) throws LexerException
	{
		IParseNode result = null;

		synchronized (parseState.getLexemeList())
		{
			// cache parse state so other methods can use it
			this._parseState = parseState;

			// create root node
			IParseNodeFactory nodeFactory = this.getParseNodeFactory();

			if (nodeFactory != null)
			{
				result = nodeFactory.createRootNode();
			}

			// move the source over to the lexer and set the lexing starting position
			ILexer lexer = this.getLexer();
			lexer.setLexemeCache(this.getLexemeList());
			lexer.setSource(parseState.getSource());
			lexer.setCurrentOffset(0);
			// lexer.setCurrentOffset(parseState.getLexemeList().getAffectedRegion().getStartingOffset());

			// pre-parse call
			parseState.onBeforeParse();

			// perform parse
			try
			{
				this.parseAll(result);
			}
			catch (ParseException e)
			{
			}
			catch (Exception e)
			{
				// something really unexpected happened, so report it
				ParsingPlugin plugin = ParsingPlugin.getDefault();
				String message = Messages.ParserBase_UnexpectedErrorDuringParse;

				if (plugin != null)
				{
					plugin.getLog().log(
							new Status(IStatus.INFO, plugin.getBundle().getSymbolicName(), IStatus.OK, message, e));
				}

				// CHECKSTYLE:OFF
				System.err.println(message);
				e.printStackTrace();
				// CHECKSTYLE:ON
			}

			// store results
			parseState.setParseResults(result);
			
			// post-parse call
			parseState.onAfterParse();
		}

		return result;
	}

	/**
	 * parseOneStatement
	 * 
	 * @param parentNode
	 * @throws ParseException
	 * @throws LexerException
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

	/**
	 * registerParser
	 * 
	 * @param parser
	 */
	private void registerParser(IParser parser)
	{
		this._registeredParsers.put(parser.getLanguage(), parser);
	}

	/**
	 * removeLexeme
	 * 
	 * @param lexeme
	 */
	protected void removeLexeme(Lexeme lexeme)
	{
		this.getLexemeList().remove(lexeme);
	}

	/**
	 * @see com.aptana.ide.parsing.IParser#setLanguageChangeListener(com.aptana.ide.parsing.ILanguageChangeListener)
	 */
	public void setLanguageChangeListener(ILanguageChangeListener eventHandler)
	{
		if (this._parent != null)
		{
			this._parent.setLanguageChangeListener(eventHandler);
		}
		else
		{
			this._languageChangeListener = eventHandler;
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
		throw new ParseException(message, -1);
	}
}
