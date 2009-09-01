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
package com.aptana.ide.editor.jscomment;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;

import com.aptana.ide.editor.js.lexing.JSTokenTypes;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.jscomment.lexing.JSCommentTokenTypes;
import com.aptana.ide.editor.jscomment.parsing.JSCommentMimeType;
import com.aptana.ide.editor.jscomment.parsing.JSCommentParser;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.editors.unified.colorizer.CategoryColorizer;
import com.aptana.ide.editors.unified.colorizer.ColorizationStyle;
import com.aptana.ide.editors.unified.colorizer.LanguageColorizer;
import com.aptana.ide.editors.unified.colorizer.TokenColorizer;
import com.aptana.ide.lexer.IToken;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.TokenList;
import com.aptana.ide.parsing.IParseState;

/**
 * @author Robin Debreuil
 */
public class JSCommentColorizer extends LanguageColorizer
{

	private LanguageColorizer jsColorizer;
	private TokenList jsList;
	private IToken multi;
	private IToken single;

	/**
	 * JSCommentColorizer
	 */
	public JSCommentColorizer()
	{
		super(JSCommentMimeType.MimeType);
		if (LanguageRegistry.hasLanguageColorizer(JSMimeType.MimeType))
		{
			jsColorizer = LanguageRegistry.getLanguageColorizer(JSMimeType.MimeType);
			jsList = LanguageRegistry.getTokenList(JSMimeType.MimeType);
			if (jsList != null)
			{
				for (int i = 0; i < jsList.size(); i++)
				{
					IToken token = jsList.get(i);
					if (token.getTypeIndex() == JSTokenTypes.START_MULTILINE_COMMENT)
					{
						multi = token;
					}
					else if (token.getTypeIndex() == JSTokenTypes.COMMENT)
					{
						single = token;
					}
				}
			}
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.colorizer.LanguageColorizer#createStyle(com.aptana.ide.parsing.IParseState,
	 *      com.aptana.ide.lexer.Lexeme, java.util.Vector)
	 */
	public void createStyle(IParseState parseState, Lexeme lx, Vector styles)
	{
		if (jsColorizer == null)
		{
			jsColorizer = LanguageRegistry.getLanguageColorizer(JSMimeType.MimeType);
		}
		if (jsList == null)
		{
			jsList = LanguageRegistry.getTokenList(JSMimeType.MimeType);
			for (int i = 0; i < jsList.size(); i++)
			{
				IToken token = jsList.get(i);
				if (token.getTypeIndex() == JSTokenTypes.START_MULTILINE_COMMENT)
				{
					multi = token;
				}
				else if (token.getTypeIndex() == JSTokenTypes.COMMENT)
				{
					single = token;
				}
			}
		}

		if (lx.getToken().getLexerGroup().equals(JSCommentParser.MULTI_LINE_GROUP)
				|| lx.getToken().getTypeIndex() == JSCommentTokenTypes.START_MULTILINE_COMMENT
				|| lx.getToken().getTypeIndex() == JSCommentTokenTypes.END_MULTILINE_COMMENT)
		{
			TokenColorizer colorizer = null;
			if (multi != null)
			{
				colorizer = jsColorizer.getTokenColorizer(multi);
			}
			if (colorizer != null)
			{
				colorizer.colorize(styles, lx.offset, lx.length);
			}
			else
			{
				CategoryColorizer catColorizer = jsColorizer.getCategoryColorizer("WHITESPACE"); //$NON-NLS-1$
				ColorizationStyle style = catColorizer.getStyle();
				int bold = style.isBold() ? SWT.BOLD : 0;
				int italic = style.isItalic() ? SWT.ITALIC : 0;
				StyleRange sr = new StyleRange(lx.offset, lx.length, style.getForegroundColor(), style
						.getBackgroundColor(), bold | italic);
				sr.underline = style.isUnderline();
				styles.add(sr);
			}
		}
		else if (lx.getToken().getLexerGroup().equals(JSCommentParser.SINGLE_LINE_GROUP)
				|| lx.getToken().getTypeIndex() == JSCommentTokenTypes.START_SINGLELINE_COMMENT)
		{
			TokenColorizer colorizer = null;
			if (single != null)
			{
				colorizer = jsColorizer.getTokenColorizer(single);
			}
			if (colorizer != null)
			{
				colorizer.colorize(styles, lx.offset, lx.length);
			}
			else
			{
				CategoryColorizer catColorizer = jsColorizer.getCategoryColorizer("WHITESPACE"); //$NON-NLS-1$
				ColorizationStyle style = catColorizer.getStyle();
				int bold = style.isBold() ? SWT.BOLD : 0;
				int italic = style.isItalic() ? SWT.ITALIC : 0;
				StyleRange sr = new StyleRange(lx.offset, lx.length, style.getForegroundColor(), style
						.getBackgroundColor(), bold | italic);
				sr.underline = style.isUnderline();
				styles.add(sr);
			}
		}
	}

}
