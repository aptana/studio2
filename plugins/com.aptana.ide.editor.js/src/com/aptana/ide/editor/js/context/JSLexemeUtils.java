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
package com.aptana.ide.editor.js.context;

import com.aptana.ide.editor.js.lexing.JSTokenTypes;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.TokenCategories;

/**
 * @author Robin Debreuil
 */
public class JSLexemeUtils
{
	private LexemeList lexemeList;
	private Lexeme currentLexeme;
	private int currentLexemeIndex;

	/**
	 * Constructs a new File environment. There is one such object per file, and
	 * this takes care of parsing it and keeping its environment up to date.
	 * @param lexemeList 
	 */
	public JSLexemeUtils(LexemeList lexemeList) 
	{
		this.lexemeList = lexemeList;
	}

	/**
	 * Get the current list of lexemes and synchronize the command tree.
	 * Performance warning: this method will trigger a full parse if necessary,
	 * if you do not need access to the parser's command nodes, then use
	 * getLexemeListFast().
	 * 
	 * @return The lexeme list
	 */
	public LexemeList getLexemeList()
	{
		return lexemeList;
	}

	/**
	 * Gets the cached current Lexeme based on the current offset of the
	 * document.
	 * 
	 * @return Returns the current lexeme.
	 */
	public Lexeme getCurrentLexeme()
	{
		return currentLexeme;
	}

	/**
	 * Gets the cached current Lexeme index based on the offset in the current
	 * document.
	 * 
	 * @return Returns the current lexeme index.
	 */
	public int getCurrentLexemeIndex()
	{
		return currentLexemeIndex;
	}



	/**
	 * Calculates and returns the Lexeme index at the current document offset. 
	 * Note that document offsets are one greater that lexeme offsets. 
	 * Use getCurrentLexemeInde if querying for the current caret offset.
	 * 
	 * @param offset
	 *            The offset in the document to check at.
	 * @return Returns the index of the Lexeme at the current offset.
	 */
	public int getLexemeIndexFromDocumentOffset(int offset)
	{
		if (offset < 0 || lexemeList.size() == 0)
		{
			return -1;
		}
		int index = lexemeList.getLexemeIndex(offset - 1);
		if (index < 1) // zero is a special case
		{
			if (index < -1)
			{
				index = -index - 2;
			}
			else
			{
				index = 0;
			}
		}
		return index;
	}
	/**
	 * Gets the lexeme floor index from a given offset (lexeme offset, not document offset).
	 * If this hits a whitespace character (which has no lexeme), it will return the previous (lower) lexeme. 
	 * This is a convenience method that directly calls getLexemeFloorIndex on lexemelist.
	 * @param offset
	 * @return Returns the lexeme floor index from a given lexeme offset.
	 */
	public int getLexemeFloorIndex(int offset)
	{
		return lexemeList.getLexemeFloorIndex(offset);
	}
	/**
	 * Gets the lexeme ceiling index from a given offset (lexeme offset, not document offset). 
	 * If this hits a whitespace character (which has no lexeme), it will return the next (higher) lexeme. 
	 * This is a convenience method that directly calls getLexemeCeilingIndex on lexemelist.
	 * @param offset
	 * @return Returns the lexeme floor index from a given lexeme offset.
	 */
	public int getLexemeCeilingIndex(int offset)
	{
		return lexemeList.getLexemeCeilingIndex(offset);
	}

	/**
	 * Calculates and returns the Lexeme at a document based offset. Use
	 * getCurrentLexeme if querying for the current caret offset.
	 * 
	 * @param offset
	 *            The offset in the document to check at.
	 * @return Returns the Lexeme at the current offset.
	 */
	public Lexeme getLexemeFromDocumentOffset(int offset)
	{
		int index = getLexemeIndexFromDocumentOffset(offset);

		if (index > -1)
		{
			return lexemeList.get(index);
		}
		else
		{
			return null;
		}
	}

	/**
	 * Calculates the index and lexeme that the given offset is within and
	 * caches it. This accounts for whitespace areas by setting the result to
	 * the previous lexeme if available.
	 * 
	 * @param offset
	 */
	public void calculateCurrentLexeme(int offset)
	{
		// get rid of impossible offsets
		currentLexemeIndex = getLexemeIndexFromDocumentOffset(offset);
		if (currentLexemeIndex > -1)
		{
			currentLexeme = lexemeList.get(currentLexemeIndex);
		}
		else
		{
			currentLexeme = null;
		}
	}

	/**
	 * Returns the next IDENTIFIER Lexeme from the starting 'index' position
	 * 
	 * @param index
	 * @return Returns the next Ident lexeme
	 */
	public Lexeme getNextIdentifier(int index)
	{
		for (int i = index; i < lexemeList.size(); i++)
		{
			Lexeme lexeme = lexemeList.get(i);

			if(lexeme.typeIndex != TokenCategories.WHITESPACE &&
					lexeme.typeIndex != JSTokenTypes.IDENTIFIER)
			{
				return null;
			}
			
			if (lexeme.typeIndex == JSTokenTypes.IDENTIFIER)
			{
				return lexeme;
			}
		}

		return null;
	}
	
	/**
	 * getPreviousIdentifier
	 * 
	 * @param index
	 * @return Lexeme
	 */
	public Lexeme getPreviousIdentifier(int index)
	{
		for (int i = index; i >= 0; i--)
		{
			Lexeme lexeme = lexemeList.get(i);

			if(lexeme.typeIndex != TokenCategories.WHITESPACE &&
					lexeme.typeIndex != JSTokenTypes.IDENTIFIER)
			{
				return null;
			}
			
			if (lexeme.typeIndex == JSTokenTypes.IDENTIFIER)
			{
				return lexeme;
			}
		}

		return null;
	}	
	
	/**
	 * getNextTypeIdentifier
	 * 
	 * @param startIndex
	 * @return String
	 */
	public String getNextTypeIdentifier(int startIndex)
	{
		String name = ""; //$NON-NLS-1$
		int index = startIndex;
		int size = lexemeList.size();
		
		Lexeme lexeme = lexemeList.get(index);

		while(index < size && 
				(lexeme.typeIndex == JSTokenTypes.IDENTIFIER || lexeme.typeIndex == JSTokenTypes.DOT))
		{
			name += lexeme.getText();
	
			index++;

			if(index < size)
			{
				lexeme = lexemeList.get(index);
			}
		}
				
		return name.equals("") ? null : name; //$NON-NLS-1$
	}
	 
	/**
	 * Find 'foo' in a 'var a = new foo();' statement, but also account for comments.
	 * @param index
	 * @return Returns 'foo' in a 'var a = new foo();' statement, but also account for comments.
	 */
	public String getTypeAfterEqualNew(int index)
	{
		int equalsIndex = findNextTokenType(index, JSTokenTypes.EQUAL);
		
		if(equalsIndex == -1)
		{
			return null;
		}
		
		int newIndex = findNextTokenType(equalsIndex+1, JSTokenTypes.NEW);

		if(newIndex == -1)
		{
			return null;
		}

		int identifierIndex = findNextTokenType(equalsIndex+1, JSTokenTypes.IDENTIFIER);
		
		if(identifierIndex == -1)
		{
			return null;
		}

		return getNextTypeIdentifier(identifierIndex);
	}
	
	/**
	 * getIndexAfterTypeIdentifier
	 * 
	 * @param index
	 * @return int
	 */
	public int getIndexAfterTypeIdentifier(int index)
	{		
		// move to the next index
		index++;
		
		int equalsIndex = findNextTokenType(index, JSTokenTypes.EQUAL);
		
		if(equalsIndex == -1)
		{
			return -1;
		}
		
		int identifierIndex = findNextTokenType(equalsIndex+1, JSTokenTypes.IDENTIFIER);
		
		return identifierIndex;
	}
	
	/**
	 * findNextTokenType
	 * 
	 * @param startIndex
	 * @param typeIndex
	 * @return int
	 */
	public int findNextTokenType(int startIndex, int typeIndex)
	{
		int index = startIndex;
		int size = lexemeList.size();
		
		if(index >= size)
		{
			return -1;
		}
		
		Lexeme lexeme = lexemeList.get(index);

		while(index < size || lexeme.getCategoryIndex() == TokenCategories.WHITESPACE)
		{
			if(lexeme.typeIndex == typeIndex)
			{
				return index;
			}

			index++;

			if(index < size)
			{
				lexeme = lexemeList.get(index);
			}
			else
			{
				break;
			}
			
		}

		return -1;
	}
	
	/**
	 * isNextTokenType
	 * 
	 * @param startIndex
	 * @param typeIndex
	 * @return int
	 */
	public int isNextTokenType(int startIndex, int typeIndex)
	{
		int index = startIndex;
		int size = lexemeList.size();
		
		if(index >= size || index < 0)
		{
			return -1;
		}
		
		Lexeme lexeme = lexemeList.get(index);

		while(index < size && lexeme.getCategoryIndex() == TokenCategories.WHITESPACE)
		{
			index++;

			if(index < size)
			{
				lexeme = lexemeList.get(index);
			}
			else
			{
				break;
			}
		}
		
		if(lexeme.typeIndex == typeIndex)
		{
			return lexeme.offset;
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * isPrevTokenType
	 * 
	 * @param startIndex
	 * @param typeIndex
	 * @return int
	 */
	public int isPrevTokenType(int startIndex, int typeIndex)
	{
		int index = startIndex;
		int size = lexemeList.size();
		
		if(index >= size || index < 0)
		{
			return -1;
		}
		
		Lexeme lexeme = lexemeList.get(index);

		while(index <= 0 && lexeme.getCategoryIndex() == TokenCategories.WHITESPACE)
		{
			index--;

			if(index >= 0)
			{
				lexeme = lexemeList.get(index);
			}
			else
			{
				break;
			}
		}
		
		if(lexeme.typeIndex == typeIndex)
		{
			return lexeme.offset;
		}
		else
		{
			return -1;
		}
	}	
	
	/**
	 * Looks for the name of the function in the following formats: 
	 * 
	 * TYPE 1: function foo() {} 
	 * TYPE 2: foo = function() {} 
	 * TYPE 3: bar.foo = function() {} 
	 * TYPE 4: foo = { "bar" : function() {}, bar2 : function() {}, ... } 
	 * TYPE 5: function foo() { bar=function() {} }
	 * 
	 * @param currentIndex
	 *            The current index of the lexemes to look at.
	 * @return Returns the name of the function
	 */
	public JSFunctionInfo getFunctionInfo(int currentIndex)
	{
		return getFunctionInfo(currentIndex, false);
	}

	private JSFunctionInfo getFunctionInfo(int currentIndex, boolean recurse)
	{
		for (int i = currentIndex; i < lexemeList.size(); i++)
		{
			Lexeme lexeme = lexemeList.get(i);

			// TYPE 1
			if (lexeme.typeIndex == JSTokenTypes.IDENTIFIER)
			{
				String params = findParameters(i);

				String parent = null;
				if (!recurse)
				{
					parent = findParentFunction(i);
				}

				if (parent == null)
				{
					parent = ""; //$NON-NLS-1$
				}
				else
				{
					parent = parent + "."; //$NON-NLS-1$
				}

				JSFunctionInfo fi = new JSFunctionInfo(parent + lexeme.getText(), lexeme.offset, params);
				//fi.docOffset = findDocOffset(i-1);
				fi.nameOffset = lexeme.offset;
				return fi;
			}

			// TYPE 2,3,4
			if (lexeme.typeIndex == JSTokenTypes.LPAREN)
			{
				String params = findParameters(i - 1);

				int listIdx = currentIndex - 1;

				for (; listIdx > 0; --listIdx)
				{
					Lexeme lx1 = lexemeList.get(listIdx);
					
					// TYPE 2 & 3, Find '='
					if (lx1.typeIndex == JSTokenTypes.EQUAL)
					{
						String parent = null;
						if (!recurse)
						{
							parent = findParentFunction(listIdx);
						}
						JSFunctionInfo fi = findIdentifierBeforeEqual(listIdx);
						if (fi != null)
						{
							if (parent != null)
							{
								fi.name = parent + "." + fi.name; //$NON-NLS-1$
							}
							fi.params = params;
							//fi.docOffset = findDocOffset(getLexemeIndexFromDocumentOffset(fi.offset + 1));
							return fi;
						}
						else
						{
							return null;
						}
					}

					// TYPE 4: foo = { "bar" : function() {}, "bar2" :
					// function() {}, ... }
					else if (lx1.typeIndex == JSTokenTypes.COLON)
					{
						// Find the first identifier (going backwards)
						for (; listIdx > 0; --listIdx)
						{
							Lexeme lx2 = lexemeList.get(listIdx);
							
							if (lx2.typeIndex == JSTokenTypes.STRING || lx2.typeIndex == JSTokenTypes.IDENTIFIER)
							{
								String name = lx2.getText();
								if (lx2.typeIndex == JSTokenTypes.STRING)
								{
									name = name.substring(1, name.length() - 1);
								}
								String parentName = null;

								if (!recurse)
								{
									parentName = findParentFunction(listIdx, 1);
								}

								if (parentName != null)
								{
									JSFunctionInfo fi = new JSFunctionInfo(parentName + "." + name, lx2.offset, params); //$NON-NLS-1$
									//fi.docOffset = findDocOffset(getLexemeIndexFromDocumentOffset(lx2.offset)+1);
									fi.nameOffset = lx2.offset;
									return fi;
								}
								else
								{
									JSFunctionInfo fi = new JSFunctionInfo(name, lx2.offset, params); 
									//fi.docOffset = findDocOffset(getLexemeIndexFromDocumentOffset(lx2.offset)+1);
									fi.nameOffset = lx2.offset;
									return fi;
								}
							}
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * findDocOffset
	 * 
	 * @param currentIndex
	 * @return int
	 */
	public int findDocOffset(int currentIndex)
	{
		if(currentIndex > 0)
		{
			Lexeme lx = lexemeList.get(--currentIndex);
	
			if (lx.typeIndex == JSTokenTypes.DOCUMENTATION)
			{
				return lx.offset;
			}
		}
		
		return -1;
	}
	
//	public FunctionDocumentation getFunctionDocumentation(int index)
//	{
//		DocumentationManager dm = JSEnvironment.getDocumentationManager();
//		String src = getLexemeFromDocumentOffset(index+1).getText();
//		FunctionDocumentation doc = dm.parseJSFunctionDocumentation(src);
//        
//        return doc;
//	}

	/**
	 * Finds the parameter names of a function through lexeme lookup.
	 * 
	 * @param index
	 *            The index in the lexeme list to start looking from (should be
	 *            the open paren).
	 * @return Finds the parameter names of a function through lexeme lookup.
	 */
	private String findParameters(int index)
	{
		// todo: maybe this can now query the environment.
		String params = ""; //$NON-NLS-1$
		boolean found = false;

		for (int i = index; i < lexemeList.size(); i++)
		{
			Lexeme lexeme = lexemeList.get(i);
			if (lexeme.typeIndex == JSTokenTypes.LPAREN)
			{
				while (lexeme.typeIndex != JSTokenTypes.RPAREN && i < lexemeList.size())
				{
					lexeme = lexemeList.get(i++);
					params += lexeme.getText().trim();
					found = true;
				}

				if (found)
				{
					break;
				}
			}
		}

		if (!found || params.trim().length() == 0)
		{
			return "()"; //$NON-NLS-1$
		}
		else
		{
			return params;
		}
	}

	private String findParentFunction(int currentIndex)
	{
		// todo: maybe this can now query the environment.
		return findParentFunction(currentIndex, 0);
	}

	private String findParentFunction(int currentIndex, int startDepth)
	{
		// todo: maybe this can now query the environment.
		int level = startDepth;

		// Find 'foo' in --> foo = { "bar" : function() {}, "bar2" : function()
		// {}, ... }
		while (currentIndex > 0)
		{
			Lexeme lx = lexemeList.get(--currentIndex);

			if (lx.getLanguage().equals(JSMimeType.MimeType) == false)
			{
				continue;
			}
			
			if (lx.typeIndex == JSTokenTypes.RCURLY)
			{
				level++;
			}

			else if (lx.typeIndex == JSTokenTypes.LCURLY)
			{
				level--;
			}

			if (level < startDepth) // level == 0)
			{
				// Find '='
				for (; currentIndex > 0; --currentIndex)
				{
					Lexeme lx1 = lexemeList.get(currentIndex);
					
					if (lx.getLanguage().equals(JSMimeType.MimeType) == false)
					{
						continue;
					}
					
					if (lx1.typeIndex == JSTokenTypes.EQUAL)
					{
						String id = findIdentifierBeforeEqual(currentIndex).name;
						String parent = findParentFunction(currentIndex);
						if (parent != null)
						{
							return parent + "." + id; //$NON-NLS-1$
						}
						else
						{
							return id;
						}
					}
					else if (lx1.typeIndex == JSTokenTypes.FUNCTION)
					{
						String name = getFunctionInfo(currentIndex, true).name;
						String parent = findParentFunction(currentIndex);
						if (parent != null)
						{
							return parent + "." + name; //$NON-NLS-1$
						}
						else
						{
							return name;
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * findIdentifierBeforeEqual
	 * 
	 * @param currentIndex
	 * @return JSFunctionInfo
	 */
	public JSFunctionInfo findIdentifierBeforeEqual(int currentIndex)
	{
		// todo: maybe this can now query the environment.
		// Find the first identifier (going backwards)
		while (currentIndex >= 0)
		{
			Lexeme lx2 = lexemeList.get(currentIndex--);

			if (lx2.typeIndex == JSTokenTypes.IDENTIFIER)
			{
				String name = ""; //$NON-NLS-1$
				int offset = -1;
				while (lx2.typeIndex == JSTokenTypes.IDENTIFIER || lx2.typeIndex == JSTokenTypes.DOT
						|| lx2.typeIndex == JSTokenTypes.THIS)
				{
					if (lx2.typeIndex == JSTokenTypes.THIS)
					{
						name = name.substring(1); // remove the dot
					}
					else
					{
						name = lx2.getText() + name;
					}

					offset = lx2.offset;

					int index = currentIndex--;

					if(lx2.isAfterEOL())
					{
						break;
					}
					
					if (index >= 0)
					{
						lx2 = lexemeList.get(index);
					}
					else
					{
						break;
					}
				}

				JSFunctionInfo fi = new JSFunctionInfo(name, offset);
				fi.nameOffset = offset;
				return fi;
			}
		}

		return null;
	}

//	/**
//	 * Gets a doc object from a scriptdoc lexeme.
//	 * @param docLexeme
//	 * @return Returns a doc object from a scriptdoc lexeme.
//	 */
//	public IDocumentation getDoumenatationFromLexeme(Lexeme docLexeme)
//	{
//		if( !(docLexeme.typeIndex == JSTokenTypes.DOCUMENTATION) )
//		{
//			return null;
//		}
//		String docSrc = docLexeme.getText();
//		IDocumentation doc = JSEnvironment.getDocumentationManager().parseScriptDoc(docSrc);
//		return doc;
//	}
}
