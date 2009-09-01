/* ***** BEGIN LICENSE BLOCK *****
 * Version: GPL 3
 *
 * This program is Copyright (C) 2007-2008 Aptana, Inc. All Rights Reserved
 * This program is licensed under the GNU General Public license, version 3 (GPL).
 *
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by the GPL,
 * is prohibited.
 *
 * You can redistribute and/or modify this program under the terms of the GPL, 
 * as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * You may view the GPL, and Aptana's exception and additional terms in the file
 * titled license-jaxer.html in the main distribution folder of this program.
 * 
 * Any modifications to this file must keep this entire header intact.
 *
 * ***** END LICENSE BLOCK ***** */
package com.aptana.ide.editor.xml.parsing;

import java.text.ParseException;

import com.aptana.ide.editor.xml.lexing.XMLTokenTypes;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.parsing.ParserInitializationException;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * @author Kevin Lindsey
 */
public class XMLScanner extends XMLParserBase
{
	/**
	 * XMLScanner
	 * 
	 * @throws ParserInitializationException
	 */
	public XMLScanner() throws ParserInitializationException
	{
		this(XMLMimeType.MimeType);
	}

	/**
	 * XMLScanner
	 * 
	 * @param language
	 * @throws ParserInitializationException
	 */
	public XMLScanner(String language) throws ParserInitializationException
	{
		super(language);
	}
	
	/**
	 * @see com.aptana.ide.parsing.AbstractParser#parseAll(com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public void parseAll(IParseNode parentNode) throws ParseException, LexerException
	{
		ILexer lexer = this.getLexer();
		lexer.setLanguageAndGroup(this.getLanguage(), "default"); //$NON-NLS-1$

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
					case XMLTokenTypes.XML_DECL:
						this.getLexer().setGroup(XML_DECLARATION_GROUP);
						this.assertAndAdvance(XMLTokenTypes.XML_DECL, "error.xml.declaration"); //$NON-NLS-1$
						this.assertAndAdvance(XMLTokenTypes.VERSION, "error.xml.declaration.version"); //$NON-NLS-1$
						if (this.isType(XMLTokenTypes.ENCODING))
						{
							this.advance();
						}
						if (this.isType(XMLTokenTypes.STANDALONE))
						{
							this.advance();
						}
						this.parseText(false);
						break;
						
					case XMLTokenTypes.CDATA_START:
						this.getLexer().setGroup(CDATA_SECTION_GROUP);
						this.assertAndAdvance(XMLTokenTypes.CDATA_START, "error.cdata"); //$NON-NLS-1$
						this.assertType(XMLTokenTypes.CDATA_END, "error.cdata.close"); //$NON-NLS-1$
						this.parseText(false);
						break;
						
					case XMLTokenTypes.DOCTYPE_DECL:
						this.parseDocTypeDeclaration();
						this.parseText(false);
						break;
						
					case XMLTokenTypes.PI_OPEN:
						this.getLexer().setGroup(PROCESSING_INSTRUCTION_GROUP);
						this.assertAndAdvance(XMLTokenTypes.PI_OPEN, "error.pi"); //$NON-NLS-1$
						this.assertType(XMLTokenTypes.CDATA_END, "error.pi.close"); //$NON-NLS-1$
						this.parseText(false);
						break;
						
					case XMLTokenTypes.COMMENT:
						this.parseText(false);
						break;
						
					case XMLTokenTypes.GREATER_THAN:
					case XMLTokenTypes.SLASH_GREATER_THAN:
						this.parseText(false);
						break;
					
					default:
						this.advance();
				}
			}
			catch (ParseException e)
			{
				lexer.setGroup(DEFAULT_GROUP);
			}
		}
		
		this.getParseState().clearEditState();
	}
}
