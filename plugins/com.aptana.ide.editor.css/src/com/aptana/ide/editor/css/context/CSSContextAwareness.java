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
package com.aptana.ide.editor.css.context;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.css.lexing.CSSTokenTypes;
import com.aptana.ide.editor.css.parsing.CSSMimeType;
import com.aptana.ide.editors.unified.IFileService;
import com.aptana.ide.editors.unified.IUnifiedEditor;
import com.aptana.ide.editors.unified.context.ContextItem;
import com.aptana.ide.editors.unified.context.IContextAwareness;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;

/**
 * @author Robin Debreuil
 */
public class CSSContextAwareness implements IContextAwareness
{
	// private UnifiedEditor editor = null;
	private ContextItem fileContext = null;

	/**
	 * CSSContextAwareness
	 * 
	 * @param editor
	 */
	public CSSContextAwareness(IUnifiedEditor editor)
	{
		// this.editor = editor;
		this.fileContext = new ContextItem("global"); //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.ide.editors.unified.context.IContextAwareness#update(com.aptana.ide.editors.unified.IFileService)
	 */
	public void update(IFileService context)
	{
		ContextItem currentStyle = null;

		fileContext.clearAll();

		// Get lexemes for the current doc
		LexemeList lexemeList = context.getLexemeList(); // editor.getLanguageService().getLexemeList();
		if (lexemeList == null)
		{
			return;
		}

		int lexemeListSize = lexemeList.size();

		// Search for function definitions
		for (int lexc = 0; lexc < lexemeListSize; lexc++)
		{
			Lexeme lexeme = lexemeList.get(lexc);

			if (lexeme.getLanguage().equals(CSSMimeType.MimeType) == false)
			{
				continue;
			}

			if (lexeme.typeIndex == CSSTokenTypes.IDENTIFIER || lexeme.typeIndex == CSSTokenTypes.HASH
					|| lexeme.typeIndex == CSSTokenTypes.PROPERTY || lexeme.typeIndex == CSSTokenTypes.SELECTOR
					|| lexeme.typeIndex == CSSTokenTypes.CLASS)
			{
				currentStyle = new CSSContextItem();
				lexc = consumeName(lexemeList, lexc, currentStyle);
			}
			else if (lexeme.typeIndex == CSSTokenTypes.LCURLY)
			{
				lexc = consumeStyleElements(lexemeList, lexc + 1, currentStyle);
			}
		}
	}

	private int consumeName(LexemeList lexemeList, int lexc, ContextItem item)
	{
		Lexeme lexeme = lexemeList.get(lexc);

		String name = lexeme.getText() + StringUtils.SPACE;

		item.setOffset(lexeme.offset);
		item.setLength(lexeme.length);

		int lexemeListSize = lexemeList.size();

		while (lexeme.typeIndex != CSSTokenTypes.LCURLY && lexc < lexemeListSize)
		{
			lexc++;
			if (lexc < lexemeListSize)
			{
				lexeme = lexemeList.get(lexc);

				if (lexeme.getLanguage().equals(CSSMimeType.MimeType) == false)
				{
					continue;
				}

				if (lexeme.typeIndex != CSSTokenTypes.LCURLY && lexeme.typeIndex != CSSTokenTypes.COMMENT)
				{
					name += lexeme.getText();
				}
			}
		}

		lexc--;

		item.setName(name.trim());

		fileContext.addItem(item);

		return lexc;
	}

	private int consumeStyleElements(LexemeList lexemeList, int lexc, ContextItem item)
	{
		int lexemeListSize = lexemeList.size();

		String name = null;
		String value = StringUtils.EMPTY;

		for (; lexc < lexemeListSize; lexc++)
		{
			Lexeme lexeme = lexemeList.get(lexc);

			if (lexeme.getLanguage().equals(CSSMimeType.MimeType) == false)
			{
				continue;
			}

			if (lexeme.typeIndex == CSSTokenTypes.RCURLY)
			{
				if (name != null)
				{
					item.values.put(name, value);
				}
				return lexc++;
			}
			else if (name == null
					&& (lexeme.typeIndex == CSSTokenTypes.IDENTIFIER || lexeme.typeIndex == CSSTokenTypes.PROPERTY || lexeme.typeIndex == CSSTokenTypes.SELECTOR))
			{
				name = lexeme.getText();
			}
			else if (name != null && lexeme.typeIndex == CSSTokenTypes.COLON)
			{
				// Move past colon
				lexc++;

				if (lexc >= lexemeListSize)
				{
					return lexc;
				}

				lexeme = lexemeList.get(lexc);

				while (lexc < lexemeListSize && lexeme.typeIndex != CSSTokenTypes.RCURLY
						&& lexeme.typeIndex != CSSTokenTypes.SEMICOLON)
				{
					value += lexeme.getText();
					value += StringUtils.SPACE;

					lexc++;

					lexeme = lexemeList.get(lexc);
				}

				lexc--;

				item.values.put(name, value.trim());

				name = null;
				value = StringUtils.EMPTY;
			}

		}

		return lexc;
	}

	/**
	 * @see com.aptana.ide.editors.unified.context.IContextAwareness#getFileContext()
	 */
	public ContextItem getFileContext()
	{
		return this.fileContext;
	}

	private static CSSContextAwareness instance;

	/**
	 * getInstance
	 * 
	 * @param editor
	 * @return CSSContextAwareness
	 */
	public static CSSContextAwareness getInstance(IUnifiedEditor editor)
	{
		if (instance == null)
		{
			instance = new CSSContextAwareness(editor);
		}
		return instance;
	}
}
