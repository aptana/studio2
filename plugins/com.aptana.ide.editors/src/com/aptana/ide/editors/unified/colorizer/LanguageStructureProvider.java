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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.editors.unified.UnifiedColorManager;
import com.aptana.ide.lexer.IToken;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.TokenList;
import com.aptana.sax.AttributeSniffer;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class LanguageStructureProvider implements ITreeContentProvider, ITableLabelProvider
{

	private String hashToken(IToken token)
	{
		return token.getLanguage() + "::" + token.getCategory() + "::" + token.getType(); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Class representing a category
	 */
	public class Category
	{

		private Map tokens;
		private Map styles;
		private String name;
		private ColorizationStyle style;

		/**
		 * Creates a new category
		 * 
		 * @param name
		 */
		public Category(String name)
		{
			this.name = name;
			tokens = new HashMap();
			styles = new HashMap();
			this.style = null;
		}

		/**
		 * Sets the style
		 * 
		 * @param style
		 */
		public void setStyle(ColorizationStyle style)
		{
			this.style = style;
		}

		/**
		 * Gets the style
		 * 
		 * @return - style
		 */
		public ColorizationStyle getStyle()
		{
			return this.style;
		}

		/**
		 * Adds a token style
		 * 
		 * @param token
		 * @param style
		 */
		public void addTokenStyle(IToken token, ColorizationStyle style)
		{
			if (token != null)
			{
				styles.put(hashToken(token), style);
			}
		}

		/**
		 * Remove token style
		 * 
		 * @param token
		 */
		public void removeTokenStyle(IToken token)
		{
			if (token != null)
			{
				styles.remove(hashToken(token));
			}
		}

		/**
		 * Gets the token style
		 * 
		 * @param token
		 * @return - style
		 */
		public ColorizationStyle getTokenStyle(IToken token)
		{
			ColorizationStyle style = null;
			if (token != null)
			{
				style = (ColorizationStyle) styles.get(hashToken(token));
			}
			return style;
		}

		/**
		 * Adds a token
		 * 
		 * @param token
		 */
		public void addToken(IToken token)
		{
			tokens.put(hashToken(token), token);
		}

		/**
		 * Gets a token
		 * 
		 * @param hash
		 * @return the token
		 */
		public IToken getToken(String hash)
		{
			if (tokens.containsKey(hash))
			{
				return (IToken) tokens.get(hash);
			}
			return null;
		}

		/**
		 * Gets the tokens
		 * 
		 * @return - collection of tokens
		 */
		public Collection getTokens()
		{
			return tokens.values();
		}

		/**
		 * Gets the name of the category
		 * 
		 * @return - name of category
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * Gets the display name
		 * 
		 * @return - display name
		 */
		public String getDisplayName()
		{
			String display = name.toLowerCase();
			display = display.substring(0, 1).toUpperCase() + display.substring(1, display.length());
			return display;
		}

		/**
		 * Gets style
		 * 
		 * @return - styles
		 */
		public Map getStyles()
		{
			return styles;
		}

	}

	private Map categories;
	private Map regions;
	private Map imageMap;
	private Color backgroundColor;
	private Color lineHighlightColor;
	private Color caretColor;
	private Color selectionForegroundColor;
	private Color selectionBackgroundColor;
	private Color foldingBackgroundColor;
	private Color foldingForegroundColor;
	private String language;

	/**
	 * Creates a new language provider
	 * 
	 * @param language
	 */
	public LanguageStructureProvider(String language)
	{
		this.language = language;
		categories = new HashMap();
		imageMap = new HashMap();
		regions = new HashMap();
		backgroundColor = null;
		lineHighlightColor = null;
		caretColor = null;
		selectionForegroundColor = null;
		selectionBackgroundColor = null;
		loadProvider();
	}

	/**
	 * Loads the provider with the content of the language colorizer and token list for this language
	 */
	public void loadProvider()
	{
		TokenList tl = LanguageRegistry.getTokenList(language);
		if (tl != null)
		{
			for (int i = 0; i < tl.size(); i++)
			{
				IToken curr = (IToken) tl.get(i);
				this.addToken(curr);
			}
		}
		LanguageColorizer lc = LanguageRegistry.getLanguageColorizer(language);
		if (lc != null)
		{
			Iterator colorizers = lc.getTokenColorizers().iterator();
			while (colorizers.hasNext())
			{
				TokenColorizer curr = (TokenColorizer) colorizers.next();
				ColorizationStyle currStyle = curr.getBaseColorization();
				ColorizationStyle clone = new ColorizationStyle(currStyle);
				this.addStyle(curr.getToken(), clone);
				Iterator regions = curr.getRegions().iterator();
				Map regionMap = new HashMap();
				while (regions.hasNext())
				{
					Region region = (Region) regions.next();
					Region cloneRegion = new Region(region);
					ColorizationStyle regionStyle = region.getStyle();
					ColorizationStyle regionCloneStyle = new ColorizationStyle(regionStyle);
					cloneRegion.setStyle(regionCloneStyle);
					cloneRegion.setName(region.getName());
					regionMap.put(cloneRegion.getName(), cloneRegion);
					this.addRegion(curr.getToken(), cloneRegion);
				}
			}
			colorizers = lc.getCategoryColorizers().iterator();
			while (colorizers.hasNext())
			{
				CategoryColorizer curr = (CategoryColorizer) colorizers.next();
				ColorizationStyle clone = new ColorizationStyle(curr.getStyle());
				Category category = this.getCategory(curr.getName());
				category.setStyle(clone);
			}
			this.setBackgroundColor(lc.getBackground());
			this.setCaretColor(lc.getCaretColor());
			this.setLineHighlightColor(lc.getLineHighlightColor());
			this.setSelectionForegroundColor(lc.getSelectionForeground());
			this.setSelectionBackgroundColor(lc.getSelectionBackground());
			this.setFoldingBackgroundColor(lc.getFoldingBg());
			this.setFoldingForegroundColor(lc.getFoldingFg());
		}
	}

	/**
	 * Clears the styles in the provider
	 */
	public void clearStyles()
	{
		Iterator cats = categories.values().iterator();
		while (cats.hasNext())
		{
			Category cat = (Category) cats.next();
			cat.setStyle(null);
			cat.getStyles().clear();
		}
		Iterator regs = regions.values().iterator();
		while (regs.hasNext())
		{
			Map map = (Map) regs.next();
			map.clear();
		}
	}

	/**
	 * Adds a style
	 * 
	 * @param token
	 * @param style
	 */
	public void addStyle(IToken token, ColorizationStyle style)
	{
		Category cat = getCategory(token.getCategory());
		cat.addTokenStyle(token, style);
	}

	/**
	 * Remove style
	 * 
	 * @param token
	 */
	public void removeStyle(IToken token)
	{
		Category cat = getCategory(token.getCategory());
		cat.removeTokenStyle(token);
		Map regionMap = (Map) regions.get(token);
		regionMap.clear();
	}

	/**
	 * Remove region
	 * 
	 * @param token
	 * @param name
	 */
	public void removeRegion(IToken token, String name)
	{
		Map regionMap = (Map) regions.get(token);
		regionMap.remove(name);
	}

	/**
	 * Add region
	 * 
	 * @param token
	 * @param region
	 */
	public void addRegion(IToken token, Region region)
	{
		Map regionMap = (Map) regions.get(token);
		regionMap.put(region.getName(), region);
	}

	/**
	 * Gets regions
	 * 
	 * @param token
	 * @return - regions
	 */
	public Map getRegions(IToken token)
	{
		return ((Map) regions.get(token));
	}

	/**
	 * Get token styles
	 * 
	 * @return - token styles
	 */
	public Map getTokenStyles()
	{
		Map all = new HashMap();
		Iterator cats = categories.values().iterator();
		while (cats.hasNext())
		{
			Category cat = (Category) cats.next();
			all.putAll(cat.getStyles());
		}
		return all;
	}

	/**
	 * Gets the tokens
	 * 
	 * @return - tokens
	 */
	public Collection getTokens()
	{
		Collection all = new ArrayList();
		Iterator cats = categories.values().iterator();
		while (cats.hasNext())
		{
			Category cat = (Category) cats.next();
			all.addAll(cat.getTokens());
		}
		return all;
	}

	/**
	 * Get category styles
	 * 
	 * @return - category styles
	 */
	public Map getCategoryStyles()
	{
		Map all = new HashMap();
		Iterator cats = categories.values().iterator();
		while (cats.hasNext())
		{
			Category cat = (Category) cats.next();
			all.put(cat, cat.getStyle());
		}
		return all;
	}

	/**
	 * Gets a style
	 * 
	 * @param token
	 * @return - style
	 */
	public ColorizationStyle getStyle(IToken token)
	{
		Category cat = getCategory(token.getCategory());
		return cat.getTokenStyle(token);
	}

	/**
	 * Gets a category by group and name
	 * 
	 * @param category
	 * @return - category object
	 */
	public Category getCategory(String category)
	{
		Category cat = null;
		if (categories.containsKey(category))
		{
			cat = (Category) categories.get(category);
		}
		return cat;
	}

	/**
	 * Adds a token to this language
	 * 
	 * @param token
	 */
	public void addToken(IToken token)
	{
		Category category = null;
		if (!categories.containsKey(token.getCategory()))
		{
			category = new Category(token.getCategory());
			ColorizationStyle style = new ColorizationStyle();
			style.setForegroundColor(UnifiedColorManager.getInstance().getColor(new RGB(0, 0, 0)));
			style.setName(category.getName());
			category.setStyle(style);
			categories.put(token.getCategory(), category);
		}
		else
		{
			category = (Category) categories.get(token.getCategory());
		}
		if (category.getToken(hashToken(token)) == null)
		{
			category.addToken(token);
		}
		regions.put(token, new HashMap());
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof Category)
		{
			List list = new ArrayList(((Category) parentElement).getTokens());
			Collections.sort(list, new Comparator()
			{

				public int compare(Object o1, Object o2)
				{
					String s1 = ((IToken) o1).getDisplayName();
					String s2 = ((IToken) o2).getDisplayName();
					return s1.compareTo(s2);
				}

			});
			return list.toArray();
		}
		else
		{
			return new Object[0];
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element)
	{
		if (element instanceof IToken)
		{
			IToken token = (IToken) element;
			return getCategory(token.getCategory());
		}
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element)
	{
		return element instanceof Category;
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		List list = new ArrayList(categories.values());
		Collections.sort(list, new Comparator()
		{

			public int compare(Object o1, Object o2)
			{
				String s1 = ((Category) o1).getDisplayName();
				String s2 = ((Category) o2).getDisplayName();
				return s1.compareTo(s2);
			}

		});
		return list.toArray();
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose()
	{
		// Does nothing
	}

	/**
	 * Disposes the images used in this provider
	 */
	public void disposeImages()
	{
		Iterator iter = imageMap.values().iterator();
		while (iter.hasNext())
		{
			((Image) iter.next()).dispose();
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 *      java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{

	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener)
	{

	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex)
	{
		ColorizationStyle style = null;
		if (element instanceof IToken)
		{
			style = getStyle((IToken) element);
		}
		else if (element instanceof Category)
		{
			style = ((Category) element).getStyle();
		}
		if (style != null)
		{
			if (columnIndex == 1 && element instanceof IToken)
			{
				return UnifiedEditorsPlugin.getImage("icons/checked.gif"); //$NON-NLS-1$
			}
			else if (columnIndex == 3 && element instanceof IToken)
			{
				if (!getRegions((IToken) element).isEmpty())
				{
					return UnifiedEditorsPlugin.getImage("icons/region.gif"); //$NON-NLS-1$
				}
			}
			else if (columnIndex == 4)
			{
				if (style != null)
				{
					Color fg = style.getForegroundColor();
					Image img = null;
					if (!imageMap.containsKey(fg.getRGB()))
					{
						img = new Image(Display.getCurrent(), 16, 16);
						GC gc = new GC(img);
						gc.setBackground(fg);
						gc.fillRectangle(1, 1, 13, 13);
						gc.setForeground(UnifiedColorManager.getInstance().getColor(new RGB(0, 0, 0)));
						gc.drawRectangle(1, 1, 13, 13);
						gc.dispose();
						imageMap.put(fg.getRGB(), img);
					}
					else
					{
						img = (Image) imageMap.get(fg.getRGB());
					}
					return img;
				}
			}
			else if (columnIndex == 5)
			{
				if (style != null && style.isBold())
				{
					return UnifiedEditorsPlugin.getImage("icons/bold_on.gif"); //$NON-NLS-1$
				}
				else
				{
					return UnifiedEditorsPlugin.getImage("icons/bold_off.gif"); //$NON-NLS-1$
				}
			}
			else if (columnIndex == 6)
			{
				if (style != null && style.isItalic())
				{
					return UnifiedEditorsPlugin.getImage("icons/italic_on.gif"); //$NON-NLS-1$
				}
				else
				{
					return UnifiedEditorsPlugin.getImage("icons/italic_off.gif"); //$NON-NLS-1$
				}
			}
			else if (columnIndex == 7)
			{
				if (style != null && style.isUnderline())
				{
					return UnifiedEditorsPlugin.getImage("icons/underline_on.gif"); //$NON-NLS-1$
				}
				else
				{
					return UnifiedEditorsPlugin.getImage("icons/underline_off.gif"); //$NON-NLS-1$
				}
			}
		}
		else
		{
			if (columnIndex == 1 && element instanceof IToken)
			{
				return UnifiedEditorsPlugin.getImage("icons/unchecked.gif"); //$NON-NLS-1$
			}
		}
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex)
	{
		if (columnIndex == 0)
		{
			if (element instanceof Category)
			{
				return ((Category) element).getDisplayName();
			}
		}
		else if (columnIndex == 2)
		{
			if (element instanceof IToken)
			{
				return ((IToken) element).getDisplayName();
			}
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener)
	{

	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property)
	{
		return false;
	}

	/**
	 * 
	 */
	public void removeAll()
	{
		categories.clear();
		imageMap.clear();
		regions.clear();
		backgroundColor = null;
		lineHighlightColor = null;
		caretColor = null;
		selectionForegroundColor = null;
		selectionBackgroundColor = null;
	}

	/**
	 * Builds the language colorizer current in this provider and saves it out.
	 * 
	 * @param lc
	 * @param prefId
	 */
	public void buildLanguageColorizer(LanguageColorizer lc, String prefId)
	{
		Map tokenStyles = this.getTokenStyles();
		Iterator tokens = this.getTokens().iterator();
		while (tokens.hasNext())
		{
			IToken curr = (IToken) tokens.next();
			ColorizationStyle cloneStyle = (ColorizationStyle) tokenStyles.get(hashToken(curr));
			if (cloneStyle != null)
			{
				ColorizationStyle newStyle = new ColorizationStyle(cloneStyle);
				TokenColorizer colorizer = lc.getTokenColorizer(curr);
				if (colorizer == null)
				{
					colorizer = new TokenColorizer();
					colorizer.setToken(curr);
					lc.addTokenColorizer(colorizer);
				}
				colorizer.setBaseColorization(newStyle);
				Map regionMap = (Map) this.getRegions(curr);
				Iterator regions = regionMap.values().iterator();
				while (regions.hasNext())
				{
					Region region = (Region) regions.next();
					Region cloneRegion = new Region(region);
					cloneRegion.setName(region.getName());
					ColorizationStyle newRegionStyle = new ColorizationStyle(region.getStyle());
					newRegionStyle.setName(curr.getCategory() + "_" + curr.getType() + "_" + region.getName()); //$NON-NLS-1$ //$NON-NLS-2$
					cloneRegion.setStyle(newRegionStyle);
					colorizer.addColorization(cloneRegion);
				}
				Iterator existingRegions = colorizer.getRegions().iterator();
				while (existingRegions.hasNext())
				{
					Region currRegion = (Region) existingRegions.next();
					if (!regionMap.containsKey(currRegion.getName()))
					{
						existingRegions.remove();
					}
				}
			}
		}
		Iterator colorizers = lc.getTokenColorizers().iterator();
		while (colorizers.hasNext())
		{
			TokenColorizer curr = (TokenColorizer) colorizers.next();
			if (!tokenStyles.containsKey(hashToken(curr.getToken())))
			{
				colorizers.remove();
			}
		}
		Map categoryStyles = this.getCategoryStyles();
		Iterator styles = categoryStyles.keySet().iterator();
		while (styles.hasNext())
		{
			Category category = (Category) styles.next();
			ColorizationStyle curr = (ColorizationStyle) categoryStyles.get(category);
			ColorizationStyle newStyle = new ColorizationStyle(curr);
			CategoryColorizer colorizer = lc.getCategoryColorizer(category.getName());
			if (colorizer == null)
			{
				colorizer = new CategoryColorizer();
				colorizer.setName(category.getName());
				lc.addCategoryColorizer(colorizer);
			}
			colorizer.setStyle(newStyle);
		}
		colorizers = lc.getCategoryColorizers().iterator();
		while (colorizers.hasNext())
		{
			CategoryColorizer curr = (CategoryColorizer) colorizers.next();
			Category cat = this.getCategory(curr.getName());
			if (!categoryStyles.containsKey(cat))
			{
				colorizers.remove();
			}
		}
		lc.setBackground(this.getBackgroundColor());
		lc.setCaretColor(this.getCaretColor());
		lc.setLineHighlightColor(this.getLineHighlightColor());
		lc.setSelectionBackground(this.getSelectionBackgroundColor());
		lc.setSelectionForeground(this.getSelectionForegroundColor());
		lc.setFoldingBg(this.getFoldingBackgroundColor());
		lc.setFoldingFg(this.getFoldingForegroundColor());
		try
		{
		    (new ColorizerWriter()).buildColorizationPreference(lc, lc.getLanguage(), prefId);
			UnifiedEditorsPlugin.getDefault().getPreferenceStore().firePropertyChangeEvent("Colorization saved", //$NON-NLS-1$
					"Colorization saved", "Colorization saved"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		catch (LexerException e)
		{
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), e.getMessage());
		}
	}

	/**
	 * Gets the background color
	 * 
	 * @return - background color
	 */
	public Color getBackgroundColor()
	{
		return backgroundColor;
	}

	/**
	 * Sets the background color
	 * 
	 * @param backgroundColor -
	 *            new background color
	 */
	public void setBackgroundColor(Color backgroundColor)
	{
		this.backgroundColor = backgroundColor;
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
	 * Gets the selection background color
	 * 
	 * @return - selection background color
	 */
	public Color getSelectionBackgroundColor()
	{
		return selectionBackgroundColor;
	}

	/**
	 * Sets the selection background color
	 * 
	 * @param selectionBackgroundColor -
	 *            new selection background color
	 */
	public void setSelectionBackgroundColor(Color selectionBackgroundColor)
	{
		this.selectionBackgroundColor = selectionBackgroundColor;
	}

	/**
	 * Gets the selection foreground color
	 * 
	 * @return - selection foreground color
	 */
	public Color getSelectionForegroundColor()
	{
		return selectionForegroundColor;
	}

	/**
	 * Sets the selection foreground color
	 * 
	 * @param selectionForegroundColor -
	 *            new selection foreground color
	 */
	public void setSelectionForegroundColor(Color selectionForegroundColor)
	{
		this.selectionForegroundColor = selectionForegroundColor;
	}

	/**
	 * Gets the language
	 * 
	 * @return - language
	 */
	public String getLanguage()
	{
		return language;
	}

	/**
	 * Sets the language
	 * 
	 * @param language -
	 *            new language
	 */
	public void setLanguage(String language)
	{
		this.language = language;
	}

	/**
	 * Builds a colorization file
	 * 
	 * @param file
	 * @throws LexerException
	 */
	public void buildColorizationFile(File file) throws LexerException
	{
		if (getLanguage() == null)
		{
			// TODO throw error
			return;
		}
		LanguageColorizer colorizer = LanguageRegistry.getLanguageColorizer(getLanguage());
		(new ColorizerWriter()).buildColorizationFile(colorizer, getLanguage(), file);
	}

	/**
	 * Imports a colorization
	 * 
	 * @param file
	 */
	public void importColorization(File file)
	{
		AttributeSniffer sniffer = new AttributeSniffer("colorizer", "language"); //$NON-NLS-1$ //$NON-NLS-2$
		try
		{
			sniffer.read(new FileInputStream(file));
			if (getLanguage() != null && getLanguage().equals(sniffer.getMatchedValue()))
			{
				LanguageRegistry.importColorization(file, getLanguage());
				this.clearStyles();
				this.loadProvider();
			}
			else
			{
				// TODO throw error
			}

		}
		catch (Exception e)
		{
			// TODO throw error
		}

	}

	/**
	 * Resets this language to its defaults as obtained from the LanguageRegistry
	 */
	public void resetToLanguageDefaults()
	{
		if (getLanguage() != null)
		{
			this.clearStyles();
			LanguageRegistry.restoreDefaultColorization(getLanguage());
			this.loadProvider();
		}
	}

	/**
	 * Refreshes the tokens in this language
	 */
	public void refreshTokens()
	{
		TokenList tl = LanguageRegistry.getTokenList(language);
		if (tl != null)
		{
			for (int i = 0; i < tl.size(); i++)
			{
				IToken curr = (IToken) tl.get(i);
				Category category = this.getCategory(curr.getCategory());
				if (category == null || category.getToken(curr.getType()) == null)
				{
					this.addToken(curr);
				}
			}
		}
	}

	/**
	 * @return the foldingBackgroundColor
	 */
	public Color getFoldingBackgroundColor()
	{
		return foldingBackgroundColor;
	}

	/**
	 * @param foldingBackgroundColor
	 *            the foldingBackgroundColor to set
	 */
	public void setFoldingBackgroundColor(Color foldingBackgroundColor)
	{
		this.foldingBackgroundColor = foldingBackgroundColor;
	}

	/**
	 * @return the foldingForegroundColor
	 */
	public Color getFoldingForegroundColor()
	{
		return foldingForegroundColor;
	}

	/**
	 * @param foldingForegroundColor
	 *            the foldingForegroundColor to set
	 */
	public void setFoldingForegroundColor(Color foldingForegroundColor)
	{
		this.foldingForegroundColor = foldingForegroundColor;
	}

}
