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
package com.aptana.ide.editors.unified.colorizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.UnifiedColorManager;
import com.aptana.ide.editors.unified.UnifiedColorizerBase;
import com.aptana.ide.lexer.IToken;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.IParseState;

/**
 * Language colorizer
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class LanguageColorizer extends UnifiedColorizerBase
{

	/**
	 * Hashes the token to store it internally
	 * 
	 * @param token
	 * @return - hash value, language + category + type
	 */
	public static String hashToken(IToken token)
	{
		return token.getLanguage() + "::" + token.getCategory() + "::" + token.getType(); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Fires an event noting that colorization settings have changed
	 */
	public static void fireColorizationEvent()
	{
		UnifiedEditorsPlugin.getDefault().getPreferenceStore().firePropertyChangeEvent("Colorization saved", //$NON-NLS-1$
				"Colorization saved", "Colorization saved"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private List<IColorizerHandler> handlers;
	private Map<String, TokenColorizer> tokenColorizers;
	private Map<String, CategoryColorizer> categoryColorizers;
	private TextAttribute DEFAULT;
	private String mimeType;
	private Color background;
	private Color selectionBg;
	private Color selectionFg;
	private Color caretColor;
	private Color lineHighlightColor;
	private Color foldingBg;
	private Color foldingFg;

	/**
	 * Creates a new language colorizer for a mime type
	 * 
	 * @param mimeType -
	 *            mime type of language
	 */
	public LanguageColorizer(String mimeType)
	{
		super(mimeType);
		this.mimeType = mimeType;
		tokenColorizers = new HashMap<String, TokenColorizer>();
		categoryColorizers = new HashMap<String, CategoryColorizer>();
		handlers = new ArrayList<IColorizerHandler>();
		RGB fgRGB = new RGB(0, 0, 0);
		Color fgColor = UnifiedColorManager.getInstance().getColor(fgRGB);
		DEFAULT = new TextAttribute(fgColor, null, 0);
		background = null;
		selectionBg = null;
		selectionFg = null;
		caretColor = null;
		lineHighlightColor = null;
	}

	/**
	 * Adds a handler to this language colorizer
	 * 
	 * @param handler
	 */
	public void addHandler(IColorizerHandler handler)
	{
		this.handlers.add(handler);
	}

	/**
	 * Gets the handlers for this language colorizer
	 * 
	 * @return - list of colorizer handlers
	 */
	public List<IColorizerHandler> getHandlers()
	{
		return this.handlers;
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedColorizerBase#getPluginPreferenceStore()
	 */
	protected IPreferenceStore getPluginPreferenceStore()
	{
		return UnifiedEditorsPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedColorizerBase#initializeColorTables()
	 */
	protected void initializeColorTables()
	{
	}

	
	/**
	 * @see com.aptana.ide.editors.unified.ILexemeColorMapper#createStyle(com.aptana.ide.parsing.IParseState,
	 *      com.aptana.ide.lexer.Lexeme, java.util.Vector)
	 */
	public void createStyle(IParseState parseState, Lexeme lx, Vector<StyleRange> styles)
	{
		IToken token = lx.getToken();
		TokenColorizer tc = getTokenColorizer(token);

		IColorizerHandler handler = null;
		StyleRange handlerStyleRange = null;
		for (int i = 0; i < handlers.size(); i++)
		{
			handler = handlers.get(i);
			handlerStyleRange = handler.getStyleRange(parseState, lx);
			if (handlerStyleRange != null)
			{
				break;
			}
		}
		if (handlerStyleRange != null)
		{
			styles.add(handlerStyleRange);
		}
		else if (tc != null)
		{
			tc.colorize(styles, lx.offset, lx.length);
			
		}
		else if (categoryColorizers.containsKey(token.getCategory()))
		{
			CategoryColorizer colorizer = (CategoryColorizer) categoryColorizers.get(token.getCategory());
			ColorizationStyle style = colorizer.getStyle();
			int bold = style.isBold() ? SWT.BOLD : 0;
			int italic = style.isItalic() ? SWT.ITALIC : 0;
			StyleRange sr = new StyleRange(lx.offset, lx.length, style.getForegroundColor(),
					style.getBackgroundColor(), bold | italic);
			sr.underline = style.isUnderline();
			styles.add(sr);
		}
		else
		{
			StyleRange sr = new StyleRange(lx.offset, lx.length, DEFAULT.getForeground(), DEFAULT.getBackground(),
					DEFAULT.getStyle());
			styles.add(sr);
		}
		for (int i = 0; i < handlers.size(); i++)
		{
			handler = handlers.get(i);
			if (handler instanceof IExtendedColorizerHandler)
			{
				IExtendedColorizerHandler cm = (IExtendedColorizerHandler) handler;
				StyleRange[] styleRanges = cm.getStyleRanges(parseState, lx);
				if (styleRanges != null)
				{
					styles.addAll(Arrays.asList(styleRanges));
				}
			}
		}
	}

	/**
	 * Adds a token colorizer to this language
	 * 
	 * @param colorizer -
	 *            token colorizer
	 */
	public void addTokenColorizer(TokenColorizer colorizer)
	{
		IToken token = colorizer.getToken();
		tokenColorizers.put(hashToken(token), colorizer);
	}

	/**
	 * Removes a token colorizer
	 * 
	 * @param token
	 * @return - removed colorizer
	 */
	public TokenColorizer removeTokenColorizer(IToken token)
	{
		TokenColorizer retVal = null;
		String key = hashToken(token);
		if (tokenColorizers.containsKey(key))
		{
			retVal = (TokenColorizer) tokenColorizers.remove(key);
		}
		return retVal;
	}

	/**
	 * Gets the token colorizer for a token
	 * 
	 * @param token -
	 *            token to get colorization for
	 * @return - token colorizer
	 */
	public TokenColorizer getTokenColorizer(IToken token)
	{
		if (tokenColorizers.containsKey(hashToken(token)))
		{
			return (TokenColorizer) tokenColorizers.get(hashToken(token));
		}

		return null;
	}

	/**
	 * getTokenColorizers
	 * 
	 * @return Collection
	 */
	public Collection getTokenColorizers()
	{
		return tokenColorizers.values();
	}

	/**
	 * addTokenColorizers
	 * 
	 * @param colorizers
	 */
	public void addTokenColorizers(Collection colorizers)
	{
		Iterator iter = colorizers.iterator();

		while (iter.hasNext())
		{
			addTokenColorizer((TokenColorizer) iter.next());
		}
	}

	/**
	 * Retains only the IToken instances found in the iterator parameter
	 * 
	 * @param tokens
	 */
	public void retainTokens(Iterator tokens)
	{
		List<String> keys = new ArrayList<String>();
		while (tokens.hasNext())
		{
			IToken token = (IToken) tokens.next();
			keys.add(hashToken(token));
		}
		tokenColorizers.keySet().retainAll(keys);
	}

	/**
	 * Adds a category level colorization
	 * 
	 * @param colorizer -
	 *            colorizer for category
	 */
	public void addCategoryColorizer(CategoryColorizer colorizer)
	{
		categoryColorizers.put(colorizer.getName(), colorizer);
	}

	/**
	 * Gets a category colorizer or null if one doesn't exist
	 * 
	 * @param category -
	 *            name of category
	 * @return - ColorizationStyle for this category
	 */
	public CategoryColorizer getCategoryColorizer(String category)
	{
		if (categoryColorizers.containsKey(category))
		{
			return (CategoryColorizer) categoryColorizers.get(category);
		}
		return null;
	}

	/**
	 * Gets the categories that have colorization
	 * 
	 * @return - Collection of String category names
	 */
	public Collection getCategories()
	{
		return categoryColorizers.keySet();
	}

	/**
	 * Gets the category level colorizers
	 * 
	 * @return - collection of CategoryColorizer objects
	 */
	public Collection getCategoryColorizers()
	{
		return categoryColorizers.values();
	}

	/**
	 * Sets the token colorizers
	 * 
	 * @param colorizer
	 */
	public void setTokenColorizers(LanguageColorizer colorizer)
	{
		this.tokenColorizers = colorizer.tokenColorizers;
	}

	/**
	 * Sets the category colorizers
	 * 
	 * @param colorizer
	 */
	public void setCategoryColorizers(LanguageColorizer colorizer)
	{
		this.categoryColorizers = colorizer.categoryColorizers;
	}

	/**
	 * Gets the language this colorizer is used for
	 * 
	 * @return - mime type of language
	 */
	public String getLanguage()
	{
		return mimeType;
	}

	/**
	 * Gets the background color
	 * 
	 * @return - bg color
	 */
	public Color getBackground()
	{
		return background;
	}

	/**
	 * Gets the background color taking into consideration an colorizer handlers registered that want to customize the
	 * background based on the lexeme
	 * 
	 * @param state
	 * @param lexeme
	 * @return - bg color
	 */
	public Color getBackground(IParseState state, Lexeme lexeme)
	{
		Color bg = null;
		IColorizerHandler handler = null;
		for (int i = 0; i < handlers.size(); i++)
		{
			handler = handlers.get(i);
			bg = handler.getBackground(state, lexeme);
			if (bg != null)
			{
				break;
			}
		}
		if (bg == null)
		{
			bg = this.background;
		}
		return bg;
	}

	/**
	 * Gets the empty line background color
	 * 
	 * @param state
	 * @param offset
	 * @return - bg color
	 */
	public Color getEmptyLineBackground(IParseState state, int offset)
	{
		Color bg = null;
		IColorizerHandler handler = null;
		for (int i = 0; i < handlers.size(); i++)
		{
			handler = handlers.get(i);
			bg = handler.getEmptyLineColor(state, offset);
			if (bg != null)
			{
				break;
			}
		}
		if (bg == null)
		{
			bg = this.background;
		}
		return bg;
	}

	/**
	 * Sets the background color
	 * 
	 * @param background -
	 *            new background color
	 */
	public void setBackground(Color background)
	{
		this.background = background;
	}

	/**
	 * Gets the caret color
	 * 
	 * @return - caret color
	 */
	public Color getCaretColor()
	{
		return caretColor;
	}

	/**
	 * Sets the caret color
	 * 
	 * @param caretColor -
	 *            new caret color
	 */
	public void setCaretColor(Color caretColor)
	{
		this.caretColor = caretColor;
	}

	/**
	 * Gets the selection background color
	 * 
	 * @return - selection background color
	 */
	public Color getSelectionBackground()
	{
		return selectionBg;
	}

	/**
	 * Sets the selection background color
	 * 
	 * @param selectionBg -
	 *            new selection background color
	 */
	public void setSelectionBackground(Color selectionBg)
	{
		this.selectionBg = selectionBg;
	}

	/**
	 * Gets the selection foreground color
	 * 
	 * @return - selection foreground color
	 */
	public Color getSelectionForeground()
	{
		return selectionFg;
	}

	/**
	 * Sets the selection foreground color
	 * 
	 * @param selectionFg -
	 *            new selection foreground color
	 */
	public void setSelectionForeground(Color selectionFg)
	{
		this.selectionFg = selectionFg;
	}

	/**
	 * Gets the line highlight color
	 * 
	 * @return - line highlight color
	 */
	public Color getLineHighlightColor()
	{
		return lineHighlightColor;
	}

	/**
	 * Sets the line highlight color
	 * 
	 * @param lineHighlightColor -
	 *            new line highlight color
	 */
	public void setLineHighlightColor(Color lineHighlightColor)
	{
		this.lineHighlightColor = lineHighlightColor;
	}

	/**
	 * @return the foldingBg
	 */
	public Color getFoldingBg()
	{
		return foldingBg;
	}

	/**
	 * @param foldingBg
	 *            the foldingBg to set
	 */
	public void setFoldingBg(Color foldingBg)
	{
		this.foldingBg = foldingBg;
	}

	/**
	 * @return the foldingFg
	 */
	public Color getFoldingFg()
	{
		return foldingFg;
	}

	/**
	 * @param foldingFg
	 *            the foldingFg to set
	 */
	public void setFoldingFg(Color foldingFg)
	{
		this.foldingFg = foldingFg;
	}
}
