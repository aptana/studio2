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
package com.aptana.ide.editor.scriptdoc.tests;

import com.aptana.ide.editor.scriptdoc.lexing.ScriptDocTokenTypes;
import com.aptana.ide.editor.scriptdoc.parsing.ScriptDocMimeType;
import com.aptana.ide.editor.scriptdoc.parsing.ScriptDocParser;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.TokenCategories;
import com.aptana.ide.lexer.tests.TestTokenBase;

/**
 * @author Kevin Lindsey
 */
public class TestKeywordTokens extends TestTokenBase
{
	/**
	 * @see com.aptana.ide.lexer.tests.TestTokenBase#createLexer()
	 */
	protected ILexer createLexer() throws Exception
	{
		ScriptDocParser parser = new ScriptDocParser();

		return parser.getLexer();
	}
	
	/**
	 * @see com.aptana.ide.lexer.tests.TestTokenBase#getLanguage()
	 */
	protected String getLanguage()
	{
		return ScriptDocMimeType.MimeType;
	}
	
	/**
	 * testLinkKeyword
	 * 
	 * @throws LexerException
	 */
	public void testLinkKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@link", TokenCategories.KEYWORD, ScriptDocTokenTypes.LINK); //$NON-NLS-1$
		this.lexemeTest("@links", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testIdentifierLinkKeyword
	 * 
	 * @throws LexerException
	 */
	public void testIdentifierLinkKeyword() throws LexerException
	{
		this.lexer.setGroup("identifier"); //$NON-NLS-1$
		this.lexemeTest("@link", TokenCategories.KEYWORD, ScriptDocTokenTypes.LINK); //$NON-NLS-1$
		this.noLexemeTest("@links"); //$NON-NLS-1$
	}
	
	/**
	 * testAliasKeyword
	 * 
	 * @throws LexerException
	 */
	public void testAliasKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@alias", TokenCategories.KEYWORD, ScriptDocTokenTypes.ALIAS); //$NON-NLS-1$
		this.lexemeTest("@aliass", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testAuthorKeyword
	 * 
	 * @throws LexerException
	 */
	public void testAuthorKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@author", TokenCategories.KEYWORD, ScriptDocTokenTypes.AUTHOR); //$NON-NLS-1$
		this.lexemeTest("@authors", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testVersionKeyword
	 * 
	 * @throws LexerException
	 */
	public void testVersionKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@version", TokenCategories.KEYWORD, ScriptDocTokenTypes.VERSION); //$NON-NLS-1$
		this.lexemeTest("@versions", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testCopyrightKeyword
	 * 
	 * @throws LexerException
	 */
	public void testCopyrightKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@copyright", TokenCategories.KEYWORD, ScriptDocTokenTypes.COPYRIGHT); //$NON-NLS-1$
		this.lexemeTest("@copyrights", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testLicenseKeyword
	 * 
	 * @throws LexerException
	 */
	public void testLicenseKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@license", TokenCategories.KEYWORD, ScriptDocTokenTypes.LICENSE); //$NON-NLS-1$
		this.lexemeTest("@licenses", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testSinceKeyword
	 * 
	 * @throws LexerException
	 */
	public void testSinceKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@since", TokenCategories.KEYWORD, ScriptDocTokenTypes.SINCE); //$NON-NLS-1$
		this.lexemeTest("@sinces", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testSeeKeyword
	 * 
	 * @throws LexerException
	 */
	public void testSeeKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@see", TokenCategories.KEYWORD, ScriptDocTokenTypes.SEE); //$NON-NLS-1$
		this.lexemeTest("@sees", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testSDocKeyword
	 * 
	 * @throws LexerException
	 */
	public void testSDocKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@sdoc", TokenCategories.KEYWORD, ScriptDocTokenTypes.SDOC); //$NON-NLS-1$
		this.lexemeTest("@sdocs", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testProjectDescriptionKeyword
	 * 
	 * @throws LexerException
	 */
	public void testProjectDescriptionKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@projectDescription", TokenCategories.KEYWORD, ScriptDocTokenTypes.PROJECT_DESCRIPTION); //$NON-NLS-1$
		this.lexemeTest("@projectDescriptions", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testProjectDescriptionKeyword
	 * 
	 * @throws LexerException
	 */
	public void testExampleKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@example", TokenCategories.KEYWORD, ScriptDocTokenTypes.EXAMPLE); //$NON-NLS-1$
		this.lexemeTest("@examples", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testIdKeyword
	 * 
	 * @throws LexerException
	 */
	public void testIdKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@id", TokenCategories.KEYWORD, ScriptDocTokenTypes.ID); //$NON-NLS-1$
		this.lexemeTest("@ids", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testIgnoreKeyword
	 * 
	 * @throws LexerException
	 */
	public void testIgnoreKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@ignore", TokenCategories.KEYWORD, ScriptDocTokenTypes.IGNORE); //$NON-NLS-1$
		this.lexemeTest("@ignores", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testDeprecatedKeyword
	 * 
	 * @throws LexerException
	 */
	public void testDeprecatedKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@deprecated", TokenCategories.KEYWORD, ScriptDocTokenTypes.DEPRECATED); //$NON-NLS-1$
		this.lexemeTest("@deprecateds", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testPrivateKeyword
	 * 
	 * @throws LexerException
	 */
	public void testPrivateKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@private", TokenCategories.KEYWORD, ScriptDocTokenTypes.PRIVATE); //$NON-NLS-1$
		this.lexemeTest("@privates", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testProtectedKeyword
	 * 
	 * @throws LexerException
	 */
	public void testProtectedKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@protected", TokenCategories.KEYWORD, ScriptDocTokenTypes.PROTECTED); //$NON-NLS-1$
		this.lexemeTest("@protecteds", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testInternalKeyword
	 * 
	 * @throws LexerException
	 */
	public void testInternalKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@internal", TokenCategories.KEYWORD, ScriptDocTokenTypes.INTERNAL); //$NON-NLS-1$
		this.lexemeTest("@internals", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testNativeKeyword
	 * 
	 * @throws LexerException
	 */
	public void testNativeKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@native", TokenCategories.KEYWORD, ScriptDocTokenTypes.NATIVE); //$NON-NLS-1$
		this.lexemeTest("@natives", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testNamespaceKeyword
	 * 
	 * @throws LexerException
	 */
	public void testNamespaceKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@namespace", TokenCategories.KEYWORD, ScriptDocTokenTypes.NAMESPACE); //$NON-NLS-1$
		this.lexemeTest("@namespaces", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testTypeKeyword
	 * 
	 * @throws LexerException
	 */
	public void testTypeKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@type", TokenCategories.KEYWORD, ScriptDocTokenTypes.TYPE); //$NON-NLS-1$
		this.lexemeTest("@types", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testConstructorKeyword
	 * 
	 * @throws LexerException
	 */
	public void testConstructorKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@constructor", TokenCategories.KEYWORD, ScriptDocTokenTypes.CONSTRUCTOR); //$NON-NLS-1$
		this.lexemeTest("@constructors", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testMethodKeyword
	 * 
	 * @throws LexerException
	 */
	public void testMethodKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@method", TokenCategories.KEYWORD, ScriptDocTokenTypes.METHOD); //$NON-NLS-1$
		this.lexemeTest("@methods", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}

	/**
	 * testPropertyKeyword
	 * 
	 * @throws LexerException
	 */
	public void testPropertyKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@property", TokenCategories.KEYWORD, ScriptDocTokenTypes.PROPERTY); //$NON-NLS-1$
		this.lexemeTest("@properties", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}

	/**
	 * testClassDescriptionKeyword
	 * 
	 * @throws LexerException
	 */
	public void testClassDescriptionKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@classDescription", TokenCategories.KEYWORD, ScriptDocTokenTypes.CLASS_DESCRIPTION); //$NON-NLS-1$
		this.lexemeTest("@classDescriptions", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testMemberOfKeyword
	 * 
	 * @throws LexerException
	 */
	public void testMemberOfKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@memberOf", TokenCategories.KEYWORD, ScriptDocTokenTypes.MEMBER_OF); //$NON-NLS-1$
		this.lexemeTest("@memberOfs", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testParamKeyword
	 * 
	 * @throws LexerException
	 */
	public void testParamKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@param", TokenCategories.KEYWORD, ScriptDocTokenTypes.PARAM); //$NON-NLS-1$
		this.lexemeTest("@params", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testExceptionKeyword
	 * 
	 * @throws LexerException
	 */
	public void testExceptionKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@exception", TokenCategories.KEYWORD, ScriptDocTokenTypes.EXCEPTION); //$NON-NLS-1$
		this.lexemeTest("@exceptions", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testReturnKeyword
	 * 
	 * @throws LexerException
	 */
	public void testReturnKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@return", TokenCategories.KEYWORD, ScriptDocTokenTypes.RETURN); //$NON-NLS-1$
		this.lexemeTest("@returns", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
	
	/**
	 * testExtendsKeyword
	 * 
	 * @throws LexerException
	 */
	public void testExtendsKeyword() throws LexerException
	{
		this.lexer.setGroup("documentation"); //$NON-NLS-1$
		this.lexemeTest("@extends", TokenCategories.KEYWORD, ScriptDocTokenTypes.EXTENDS); //$NON-NLS-1$
		this.lexemeTest("@extendss", TokenCategories.LITERAL, ScriptDocTokenTypes.TEXT); //$NON-NLS-1$
	}
}
