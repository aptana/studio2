/**
 * Copyright (c) 2005-2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Aptana Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 *
 * Redistribution, except as permitted by the above license, is prohibited.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.editor.xml;

import com.aptana.ide.editor.html.lexing.HTMLTokenTypes;
import com.aptana.ide.editors.unified.ChildOffsetMapper;
import com.aptana.ide.editors.unified.IParentOffsetMapper;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.Token;
import com.aptana.ide.parsing.CodeLocation;
import com.aptana.ide.parsing.ICodeLocation;

/**
 * The HTMLOffsetMapper understands how to map between lexemes, offsets
 * and the environment
 * @author Ingo Muschenetz
 *
 */
public class XMLOffsetMapper  extends ChildOffsetMapper
{
	//private IRuntimeEnvironment environment;
	
	/**
	 * @param parent 
	 */
	public XMLOffsetMapper(IParentOffsetMapper parent)
	{
		super(parent);
		//environment = HTMLLanguageEnvironment.getInstance().getRuntimeEnvironment();
	}


	/**
	 * Returns a "hash name" allowing us to properly query code assist for
	 * appropriate completions.
	 * @return NameHash
	 */
	public String getNameHash() { 
		
		String name = ""; //$NON-NLS-1$
		int position = getCurrentLexemeIndex();

		// backtrack over lexemes to find name - we are really just
		// searching for the last OPEN_ELEMENT
		while (position >= 0)
		{
			Lexeme curLexeme = getLexemeList().get(position);

			//If we've jsut typed a ">", we jsut closed a tag.
			if(curLexeme.getText().equals(">")) //$NON-NLS-1$
			{
				return ""; //$NON-NLS-1$
			}
			
			//If we've just typed a "<", we will be in an error state.
			if(curLexeme.typeIndex == HTMLTokenTypes.ERROR && curLexeme.getText().equals("<")) //$NON-NLS-1$
			{
				return ""; //$NON-NLS-1$
			}
			
			if(curLexeme.typeIndex == HTMLTokenTypes.START_TAG)
			{
				return curLexeme.getText().replaceAll("<",""); //$NON-NLS-1$ //$NON-NLS-2$
			}

			position--;
		}
		
		return name;

	}
	
	/**
	 * @param lexeme 
	 * @return target 
	 * 
	 */
	public ICodeLocation findTarget(Lexeme lexeme) {
		// check for src attributes
		if (lexeme.getToken().getLexerGroup().equals("attribute"))  //$NON-NLS-1$
		{				
			LexemeList lexemeList = getFileService().getLexemeList();
			int index = getLexemeIndexFromDocumentOffset(lexeme.getStartingOffset() + 1);
			if (index < 2) 
			{
				return null;
			}
			Lexeme srcLexeme = lexemeList.get(index - 2); // get lexeme two tokens ago (skip the '=')
			if (srcLexeme.getText().equals("src"))  //$NON-NLS-1$
			{
				String name = lexeme.getText();
				// strip off quotes from name, if there
				if (name.startsWith("\"") || name.startsWith("'")) //$NON-NLS-1$ //$NON-NLS-2$
				{
					name = name.substring(1);
				}
				if (name.endsWith("\"") || name.endsWith("'")) //$NON-NLS-1$ //$NON-NLS-2$
				{
					name = name.substring(0, name.length() - 1);
				}
				Lexeme destLexeme = new Lexeme(new Token(null), "", 0); //$NON-NLS-1$
				String pathToCurrent = getFileService().getSourceProvider().getSourceURI();
				String parentPath = pathToCurrent.substring(0, pathToCurrent.lastIndexOf('/'));
				// TODO name may not be relative!
				return new CodeLocation(parentPath + '/' + name, destLexeme);
			}			
		}
		return null;
	}


}
