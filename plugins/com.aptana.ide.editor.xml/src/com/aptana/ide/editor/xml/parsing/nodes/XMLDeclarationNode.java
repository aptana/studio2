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
package com.aptana.ide.editor.xml.parsing.nodes;

import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.lexer.Lexeme;

/**
 * @author Robin Debreuil
 */
public class XMLDeclarationNode extends XMLParseNode
{
	private String _version;
	private String _encoding;
	private String _standalone;

	/**
	 * XMLDeclarationNode
	 * @param startLexeme
	 */
	public XMLDeclarationNode(Lexeme startLexeme)
	{
		super(XMLParseNodeTypes.DECLARATION, startLexeme);
	}

	/**
	 * @return Returns the encoding.
	 */
	public String getEncoding()
	{
		return _encoding;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.ParseNodeBase#getSource(com.aptana.ide.io.SourceWriter)
	 */
	public void getSource(SourceWriter writer)
	{
		writer.printWithIndent("<?xml"); //$NON-NLS-1$
		
		if (this._version != null && this._version.length() > 0)
		{
			writer.print(" ").print(this._version); //$NON-NLS-1$
		}
		
		if (this._encoding != null && this._encoding.length() > 0)
		{
			writer.print(" ").print(this._encoding); //$NON-NLS-1$
		}
		
		if (this._standalone != null && this._standalone.length() > 0)
		{
			writer.print(" ").print(this._standalone); //$NON-NLS-1$
		}
		
		writer.println("?>"); //$NON-NLS-1$
	}

	/**
	 * @return Returns the standalone.
	 */
	public String getStandalone()
	{
		return _standalone;
	}

	/**
	 * @see com.aptana.ide.parsing.nodes.ParseNodeBase#getText()
	 */
	public String getText()
	{
		return "xml-declaration"; //$NON-NLS-1$
	}

	/**
	 * @return Returns the version.
	 */
	public String getVersion()
	{
		return _version;
	}

	/*
	 * Constructors
	 */

	/**
	 * @param encoding
	 *            The encoding to set.
	 */
	public void setEncoding(String encoding)
	{
		this._encoding = encoding;
	}

	/**
	 * @param standalone
	 *            The standalone to set.
	 */
	public void setStandalone(String standalone)
	{
		this._standalone = standalone;
	}
	
	/**
	 * @param version
	 *            The version to set.
	 */
	public void setVersion(String version)
	{
		this._version = version;
	}
}
