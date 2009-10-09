package com.aptana.ide.editor.xml.tests;

import java.io.IOException;
import java.io.InputStream;

import com.aptana.ide.core.StreamUtils;
import com.aptana.ide.editor.xml.lexing.XMLTokenTypes;
import com.aptana.ide.editor.xml.parsing.XMLMimeType;
import com.aptana.ide.editor.xml.parsing.XMLParseState;
import com.aptana.ide.editor.xml.parsing.XMLParser;
import com.aptana.ide.editor.xml.parsing.XMLParserBase;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.TokenCategories;
import com.aptana.ide.lexer.tests.TestTokenBase;

public class TestDTDTokens extends TestTokenBase
{
	private XMLParser _parser;

	/**
	 * createLexer
	 */
	@Override
	protected ILexer createLexer() throws Exception
	{
		XMLParser parser = new XMLParser();
		ILexer lexer = parser.getLexer();

		return lexer;
	}

	/**
	 * getContent
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	private String getContent(String fileName) throws IOException
	{
		InputStream stream = getClass().getResourceAsStream(fileName);
		return StreamUtils.readContent(stream, null);
	}

	/**
	 * getLanguage
	 */
	@Override
	protected String getLanguage()
	{
		return XMLMimeType.MimeType;
	}

	/**
	 * setUp
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		this._parser = new XMLParser();
	}

	/**
	 * tearDown
	 */
	@Override
	protected void tearDown() throws Exception
	{
		this._parser = null;

		super.tearDown();
	}

	/**
	 * testAdvanceElementDeclContentDTD
	 * 
	 * @throws Exception
	 */
	public void testAdvancedElementDeclContentDTD() throws Exception
	{
		String xml = getContent("DTDwithAdvancedElementContent.xml");
		XMLParseState parseState = (XMLParseState) _parser.createParseState(null);
		parseState.setEditState(xml, xml, 0, 0);
		_parser.parse(parseState);
		LexemeList ll = parseState.getLexemeList();
		int index = 0;

		// assertEquals(83, ll.size()); FIXME I don't really know how many lexemes this should break up into. Definitely
		// over 59 or whatever it currently gives

		assertEquals(XMLTokenTypes.XML_DECL, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.VERSION, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.QUESTION_GREATER_THAN, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.DOCTYPE_DECL, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.NAME, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.LBRACKET, ll.get(index++).typeIndex);

		// note element declaration
		assertEquals(XMLTokenTypes.ELEMENT_DECL, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.NAME, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.LPAREN, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.NAME, ll.get(index++).typeIndex); // to+
		assertEquals(XMLTokenTypes.PLUS, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.COMMA, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.NAME, ll.get(index++).typeIndex); // from?
		assertEquals(XMLTokenTypes.QUESTION, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.COMMA, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.NAME, ll.get(index++).typeIndex); // heading
		assertEquals(XMLTokenTypes.COMMA, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.NAME, ll.get(index++).typeIndex); // img
		assertEquals(XMLTokenTypes.COMMA, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.NAME, ll.get(index++).typeIndex); // body*
		assertEquals(XMLTokenTypes.STAR, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.RPAREN, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.GREATER_THAN, ll.get(index++).typeIndex);

		// to element declaration
		assertEquals(XMLTokenTypes.ELEMENT_DECL, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.NAME, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.LPAREN, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.PCDATA, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.RPAREN, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.GREATER_THAN, ll.get(index++).typeIndex);

		// from element declaration
		assertEquals(XMLTokenTypes.ELEMENT_DECL, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.NAME, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.LPAREN, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.PCDATA, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.RPAREN, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.GREATER_THAN, ll.get(index++).typeIndex);

		// img element declaration
		assertEquals(XMLTokenTypes.ELEMENT_DECL, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.NAME, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.LPAREN, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.EMPTY, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.RPAREN, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.GREATER_THAN, ll.get(index++).typeIndex);

		// heading element declaration
		assertEquals(XMLTokenTypes.ELEMENT_DECL, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.NAME, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.LPAREN, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.PCDATA, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.RPAREN, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.GREATER_THAN, ll.get(index++).typeIndex);

		// body element declaration
		assertEquals(XMLTokenTypes.ELEMENT_DECL, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.NAME, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.LPAREN, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.ANY, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.RPAREN, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.GREATER_THAN, ll.get(index++).typeIndex);

		// finish embedded DTD
		assertEquals(XMLTokenTypes.RBRACKET, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.GREATER_THAN, ll.get(index++).typeIndex);

		// Now on to normal XML...
	}

	/**
	 * testBasicDTD
	 * 
	 * @throws Exception
	 */
	public void testBasicDTD() throws Exception
	{
		String xml = getContent("basicDTD.xml");
		XMLParseState parseState = (XMLParseState) _parser.createParseState(null);
		parseState.setEditState(xml, xml, 0, 0);
		_parser.parse(parseState);
		LexemeList ll = parseState.getLexemeList();
		int index = 0;

		// assertEquals(83, ll.size()); FIXME I don't really know how many lexemes this should break up into. Definitely
		// over 59 or whatever it currently gives

		assertEquals(XMLTokenTypes.XML_DECL, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.VERSION, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.QUESTION_GREATER_THAN, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.DOCTYPE_DECL, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.NAME, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.LBRACKET, ll.get(index++).typeIndex);

		// note element declaration
		assertEquals(XMLTokenTypes.ELEMENT_DECL, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.NAME, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.LPAREN, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.NAME, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.COMMA, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.NAME, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.COMMA, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.NAME, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.COMMA, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.NAME, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.RPAREN, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.GREATER_THAN, ll.get(index++).typeIndex);

		// to element declaration
		assertEquals(XMLTokenTypes.ELEMENT_DECL, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.NAME, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.LPAREN, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.PCDATA, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.RPAREN, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.GREATER_THAN, ll.get(index++).typeIndex);

		// from element declaration
		assertEquals(XMLTokenTypes.ELEMENT_DECL, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.NAME, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.LPAREN, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.PCDATA, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.RPAREN, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.GREATER_THAN, ll.get(index++).typeIndex);

		// heading element declaration
		assertEquals(XMLTokenTypes.ELEMENT_DECL, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.NAME, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.LPAREN, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.PCDATA, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.RPAREN, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.GREATER_THAN, ll.get(index++).typeIndex);

		// body element declaration
		assertEquals(XMLTokenTypes.ELEMENT_DECL, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.NAME, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.LPAREN, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.PCDATA, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.RPAREN, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.GREATER_THAN, ll.get(index++).typeIndex);

		// finish up embedded DTD
		assertEquals(XMLTokenTypes.RBRACKET, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.GREATER_THAN, ll.get(index++).typeIndex);

		// Now on to normal XML...
		assertEquals(XMLTokenTypes.START_TAG, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.GREATER_THAN, ll.get(index++).typeIndex);
	}

	/**
	 * testXMLWithDOCTYPE
	 * 
	 * @throws Exception
	 */
	public void testXMLWithDOCTYPE() throws Exception
	{
		String xml = getContent("xmlWithDOCTYPE.xml");
		XMLParseState parseState = (XMLParseState) _parser.createParseState(null);
		parseState.setEditState(xml, xml, 0, 0);
		_parser.parse(parseState);
		LexemeList ll = parseState.getLexemeList();
		int index = 0;
		assertEquals(XMLTokenTypes.XML_DECL, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.VERSION, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.ENCODING, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.QUESTION_GREATER_THAN, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.DOCTYPE_DECL, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.NAME, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.SYSTEM, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.STRING, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.GREATER_THAN, ll.get(index++).typeIndex);

		// Now on to normal XML...
		assertEquals(XMLTokenTypes.START_TAG, ll.get(index++).typeIndex);
		assertEquals(XMLTokenTypes.GREATER_THAN, ll.get(index++).typeIndex);
	}

	/*
	 * Begin keyword tests
	 */

	/**
	 * testANYKeyword
	 * 
	 * @throws LexerException
	 */
	public void testANYKeyword() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("ANY", TokenCategories.KEYWORD, XMLTokenTypes.ANY); //$NON-NLS-1$
		this.lexemeTest("ANYS", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testCDATAKeyword
	 * 
	 * @throws LexerException
	 */
	public void testCDATAKeyword() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("CDATA", TokenCategories.KEYWORD, XMLTokenTypes.CDATA); //$NON-NLS-1$
		this.lexemeTest("CDATAS", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testEMPTYKeyword
	 * 
	 * @throws LexerException
	 */
	public void testEMPTYKeyword() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("EMPTY", TokenCategories.KEYWORD, XMLTokenTypes.EMPTY); //$NON-NLS-1$
		this.lexemeTest("EMPTYS", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testENTITYKeyword
	 * 
	 * @throws LexerException
	 */
	public void testENTITYKeyword() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("ENTITY", TokenCategories.KEYWORD, XMLTokenTypes.ENTITY); //$NON-NLS-1$
		this.lexemeTest("ENTITYS", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testENTITIESKeyword
	 * 
	 * @throws LexerException
	 */
	public void testENTITIESKeyword() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("ENTITIES", TokenCategories.KEYWORD, XMLTokenTypes.ENTITIES); //$NON-NLS-1$
		this.lexemeTest("ENTITIESS", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testFIXEDKeyword
	 * 
	 * @throws LexerException
	 */
	public void testFIXEDKeyword() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("#FIXED", TokenCategories.KEYWORD, XMLTokenTypes.FIXED); //$NON-NLS-1$
		this.noLexemeTest("#FIXEDS"); //$NON-NLS-1$
	}

	/**
	 * testIDKeyword
	 * 
	 * @throws LexerException
	 */
	public void testIDKeyword() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("ID", TokenCategories.KEYWORD, XMLTokenTypes.ID); //$NON-NLS-1$
		this.lexemeTest("IDS", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testIDREFKeyword
	 * 
	 * @throws LexerException
	 */
	public void testIDREFKeyword() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("IDREF", TokenCategories.KEYWORD, XMLTokenTypes.IDREF); //$NON-NLS-1$
		this.lexemeTest("IDREFX", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testIDREFSKeyword
	 * 
	 * @throws LexerException
	 */
	public void testIDREFSKeyword() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("IDREFS", TokenCategories.KEYWORD, XMLTokenTypes.IDREFS); //$NON-NLS-1$
		this.lexemeTest("IDREFSS", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testIMPLIEDKeyword
	 * 
	 * @throws LexerException
	 */
	public void testIMPLIEDKeyword() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("#IMPLIED", TokenCategories.KEYWORD, XMLTokenTypes.IMPLIED); //$NON-NLS-1$
		this.noLexemeTest("#IMPLIEDS"); //$NON-NLS-1$
	}

	/**
	 * testNDATAKeyword
	 * 
	 * @throws LexerException
	 */
	public void testNDATAKeyword() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("NDATA", TokenCategories.KEYWORD, XMLTokenTypes.NDATA); //$NON-NLS-1$
		this.lexemeTest("NDATAS", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testNMTOKENKeyword
	 * 
	 * @throws LexerException
	 */
	public void testNMTOKENKeyword() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("NMTOKEN", TokenCategories.KEYWORD, XMLTokenTypes.NMTOKEN); //$NON-NLS-1$
		this.lexemeTest("NMTOKENX", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testNMTOKENSKeyword
	 * 
	 * @throws LexerException
	 */
	public void testNMTOKENSKeyword() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("NMTOKENS", TokenCategories.KEYWORD, XMLTokenTypes.NMTOKENS); //$NON-NLS-1$
		this.lexemeTest("NMTOKENSS", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testNOTATIONKeyword
	 * 
	 * @throws LexerException
	 */
	public void testNOTATIONKeyword() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("NOTATION", TokenCategories.KEYWORD, XMLTokenTypes.NOTATION); //$NON-NLS-1$
		this.lexemeTest("NOTATIONS", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testPCDATAKeyword
	 * 
	 * @throws LexerException
	 */
	public void testPCDATAKeyword() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("#PCDATA", TokenCategories.KEYWORD, XMLTokenTypes.PCDATA); //$NON-NLS-1$
		this.noLexemeTest("#PCDATAS"); //$NON-NLS-1$
	}

	/**
	 * testPUBLICKeyword
	 * 
	 * @throws LexerException
	 */
	public void testPUBLICKeyword() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("PUBLIC", TokenCategories.KEYWORD, XMLTokenTypes.PUBLIC); //$NON-NLS-1$
		this.lexemeTest("PUBLICS", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/**
	 * testREQUIREDKeyword
	 * 
	 * @throws LexerException
	 */
	public void testREQUIREDKeyword() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("#REQUIRED", TokenCategories.KEYWORD, XMLTokenTypes.REQUIRED); //$NON-NLS-1$
		this.noLexemeTest("#REQUIREDS"); //$NON-NLS-1$
	}

	/**
	 * testSYSTEMKeyword
	 * 
	 * @throws LexerException
	 */
	public void testSYSTEMKeyword() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("SYSTEM", TokenCategories.KEYWORD, XMLTokenTypes.SYSTEM); //$NON-NLS-1$
		this.lexemeTest("SYSTEMS", TokenCategories.LITERAL, XMLTokenTypes.NAME); //$NON-NLS-1$
	}

	/*
	 * Begin name tests
	 */
	public void testName() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("img", TokenCategories.LITERAL, XMLTokenTypes.NAME);
	}

	/*
	 * Begin punctuator tests
	 */

	/**
	 * testATTLISTPunctuator
	 * 
	 * @throws LexerException
	 */
	public void testATTLISTPunctuator() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("<!ATTLIST", TokenCategories.PUNCTUATOR, XMLTokenTypes.ATTLIST_DECL); //$NON-NLS-1$
		this.noLexemeTest("<!ATTLISTS"); //$NON-NLS-1$
	}

	/**
	 * testELEMENTPunctuator
	 * 
	 * @throws LexerException
	 */
	public void testELEMENTPunctuator() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("<!ELEMENT", TokenCategories.PUNCTUATOR, XMLTokenTypes.ELEMENT_DECL); //$NON-NLS-1$
		this.noLexemeTest("<!ELEMENTS"); //$NON-NLS-1$
	}

	/**
	 * testENTITYPunctuator
	 * 
	 * @throws LexerException
	 */
	public void testENTITYPunctuator() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("<!ENTITY", TokenCategories.PUNCTUATOR, XMLTokenTypes.ENTITY_DECL); //$NON-NLS-1$
		this.noLexemeTest("<!ENTITYS"); //$NON-NLS-1$
	}

	/**
	 * testNOTATIONPunctuator
	 * 
	 * @throws LexerException
	 */
	public void testNOTATIONPunctuator() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("<!NOTATION", TokenCategories.PUNCTUATOR, XMLTokenTypes.NOTATION_DECL); //$NON-NLS-1$
		this.noLexemeTest("<!NOTATIONS"); //$NON-NLS-1$
	}

	/**
	 * testLParen
	 * 
	 * @throws LexerException
	 */
	public void testLParen() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("(", TokenCategories.PUNCTUATOR, XMLTokenTypes.LPAREN); //$NON-NLS-1$
	}

	/**
	 * testRParen
	 * 
	 * @throws LexerException
	 */
	public void testRParen() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest(")", TokenCategories.PUNCTUATOR, XMLTokenTypes.RPAREN); //$NON-NLS-1$
	}

	/**
	 * testLBracketPunctuator
	 * 
	 * @throws LexerException
	 */
	public void testLBracketPunctuator() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("[", TokenCategories.PUNCTUATOR, XMLTokenTypes.LBRACKET); //$NON-NLS-1$
	}

	/**
	 * testRBracketPunctuator
	 * 
	 * @throws LexerException
	 */
	public void testRBracketPunctuator() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("]", TokenCategories.PUNCTUATOR, XMLTokenTypes.RBRACKET); //$NON-NLS-1$
	}

	/**
	 * testGreaterPunctuator
	 * 
	 * @throws LexerException
	 */
	public void testGreaterPunctuator() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest(">", TokenCategories.PUNCTUATOR, XMLTokenTypes.GREATER_THAN); //$NON-NLS-1$
	}

	/**
	 * testComma
	 * 
	 * @throws LexerException
	 */
	public void testComma() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest(",", TokenCategories.PUNCTUATOR, XMLTokenTypes.COMMA); //$NON-NLS-1$
	}

	/**
	 * testPipe
	 * 
	 * @throws LexerException
	 */
	public void testPipe() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("|", TokenCategories.PUNCTUATOR, XMLTokenTypes.PIPE); //$NON-NLS-1$
	}

	/**
	 * testPlusPunctuator
	 * 
	 * @throws LexerException
	 */
	public void testPlusPunctuator() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("+", TokenCategories.PUNCTUATOR, XMLTokenTypes.PLUS); //$NON-NLS-1$
	}

	/**
	 * testQuestionPunctuator
	 * 
	 * @throws LexerException
	 */
	public void testQuestionPunctuator() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("?", TokenCategories.PUNCTUATOR, XMLTokenTypes.QUESTION); //$NON-NLS-1$
	}

	/**
	 * testStarPunctuator
	 * 
	 * @throws LexerException
	 */
	public void testStarPunctuator() throws LexerException
	{
		lexer.setGroup(XMLParserBase.DOCTYPE_DECLARATION_GROUP);
		this.lexemeTest("*", TokenCategories.PUNCTUATOR, XMLTokenTypes.STAR); //$NON-NLS-1$
	}
}
