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
package com.aptana.ide.editors.unified;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.lexer.IToken;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;

/**
 * A class to help with generating documentation for lexemes
 * @author Ingo Muschenetz
 *
 */
public class DocumentationHelper {

	/**
	 * Protected constructor for utility class
	 *
	 */
	protected DocumentationHelper()
	{
		
	}
	
	/**
	 * @param fileContext 
	 * @param lexeme
	 * @return returns debug info
	 */
	public static String generateLexemeDebugInfo(IFileService fileContext, Lexeme lexeme) {
		StringBuffer sb = new StringBuffer();
		
		IToken t = lexeme.getToken();
		
		sb.append(Messages.DocumentationHelper_DebugInformation);		
		sb.append(StringUtils.format(Messages.DocumentationHelper_Language, t.getLanguage()));
		sb.append(StringUtils.format(Messages.DocumentationHelper_Text, lexeme.getText()));
		sb.append(StringUtils.format(Messages.DocumentationHelper_Category, new Object[]{ lexeme.getType(), lexeme.getCategory()}));
		//sb.append("<b>Category Index:</b> " + lexeme.getCategoryIndex() + "<br><br>");
		sb.append(StringUtils.format(Messages.DocumentationHelper_Offset, new Object[]{ Integer.toString(lexeme.getStartingOffset()), 
																						Integer.toString(lexeme.getEndingOffset()), 
																						Integer.toString(lexeme.getLength())} ));
		//sb.append("<b>Index:</b> " + t.getIndex() + "<br>");
		//sb.append("<b>LexerGroup:</b> " + t.getLexerGroup() + "<br>");
		//sb.append("<b>LexerGroupIndex:</b> " + t.getLexerGroupIndex() + "<br>");
		//sb.append("<b>NewLexerGroup:</b> " + t.getNewLexerGroup() + "<br>");
		//sb.append("<b>NewLexerGroupIndex:</b> " + t.getNewLexerGroupIndex() + "<br>");
		//sb.append("<b>SourceRegex:</b> " + t.getSourceRegex() + "<br><br>");
		
		if(fileContext instanceof FileService)
		{
			IFileSourceProvider provider = ((FileService)fileContext).getSourceProvider();
			sb.append(StringUtils.format(Messages.DocumentationHelper_SourceLength, provider.getSourceLength()));
			sb.append(StringUtils.format(Messages.DocumentationHelper_SourcePath, provider.getSourceURI()));
		}
		
		LexemeList ll = fileContext.getLexemeList();
		if(ll != null)
		{
			sb.append(StringUtils.format(Messages.DocumentationHelper_Total, ll.size()));
		}
		
		return sb.toString();
	}
}
