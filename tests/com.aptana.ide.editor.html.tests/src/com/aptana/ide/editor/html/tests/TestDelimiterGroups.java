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
package com.aptana.ide.editor.html.tests;

import com.aptana.ide.editor.html.parsing.HTMLMimeType;
import com.aptana.ide.editor.html.parsing.HTMLParser;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.tests.TestTokenBase;

/**
 * @author Kevin Lindsey
 */
public class TestDelimiterGroups extends TestTokenBase
{
	/**
	 * @see com.aptana.ide.lexer.tests.TestTokenBase#createLexer()
	 */
	protected ILexer createLexer() throws Exception
	{
		HTMLParser parser = new HTMLParser();

		return parser.getLexer();
	}
	
	/**
	 * @see com.aptana.ide.lexer.tests.TestTokenBase#getLanguage()
	 */
	protected String getLanguage()
	{
		return HTMLMimeType.MimeType;
	}

	/**
	 * testScriptDelimiter
	 * 
	 * @throws LexerException
	 */
	public void testScriptDelimiter() throws LexerException
	{
		String source = "this is some </script> text"; //$NON-NLS-1$
		String delimiterText = "</script>"; //$NON-NLS-1$
		
		this.delimiterTest(HTMLParser.SCRIPT_DELIMITER_GROUP, source, delimiterText);
	}
	
	/**
	 * testStyleDelimiter
	 * 
	 * @throws LexerException
	 */
	public void testStyleDelimiter() throws LexerException
	{
		String source = "this is some </style> text"; //$NON-NLS-1$
		String delimiterText = "</style>"; //$NON-NLS-1$
		
		this.delimiterTest(HTMLParser.STYLE_DELIMITER_GROUP, source, delimiterText);
	}
	
	/**
	 * testPercentInstructionDelimiter
	 * 
	 * @throws LexerException
	 */
	public void testPercentInstructionDelimiter() throws LexerException
	{
		String source = "this is some %> text"; //$NON-NLS-1$
		String delimiterText = "%>"; //$NON-NLS-1$
		
		this.delimiterTest(HTMLParser.PERCENT_INSTRUCTION_DELIMITER_GROUP, source, delimiterText);
	}
	
	/**
	 * testProcessingInstructionDelimiter
	 * 
	 * @throws LexerException
	 */
	public void testProcessingInstructionDelimiter() throws LexerException
	{
		String source = "this is some ?> text"; //$NON-NLS-1$
		String delimiterText = "?>"; //$NON-NLS-1$
		
		this.delimiterTest(HTMLParser.PROCESSING_INSTRUCTION_DELIMITER_GROUP, source, delimiterText);
	}
	
	/**
	 * testGreaterTagDelimiter
	 * 
	 * @throws LexerException
	 */
	public void testGreaterTagDelimiter() throws LexerException
	{
		String source = "='this is some'> text"; //$NON-NLS-1$
		String delimiterText = ">"; //$NON-NLS-1$
		
		this.delimiterTest(HTMLParser.TAG_DELIMITER_GROUP, source, delimiterText);
	}
	
	/**
	 * testForwardSlashGreaterTagDelimiter
	 * 
	 * @throws LexerException
	 */
	public void testForwardSlashGreaterTagDelimiter() throws LexerException
	{
		String source = "='this is some'/> text"; //$NON-NLS-1$
		String delimiterText = "/>"; //$NON-NLS-1$
		
		this.delimiterTest(HTMLParser.TAG_DELIMITER_GROUP, source, delimiterText);
	}
}
