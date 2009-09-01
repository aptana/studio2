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
package com.aptana.ide.logging.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.editors.unified.UnifiedColorManager;
import com.aptana.ide.editors.unified.colorizer.CategoryColorizer;
import com.aptana.ide.editors.unified.colorizer.ColorizationStyle;
import com.aptana.ide.editors.unified.colorizer.ColorizerReader;
import com.aptana.ide.editors.unified.colorizer.ColorizerWriter;
import com.aptana.ide.editors.unified.colorizer.LanguageColorizer;
import com.aptana.ide.editors.unified.colorizer.Region;
import com.aptana.ide.editors.unified.colorizer.TokenColorizer;
import com.aptana.ide.lexer.IEnumerationMap;
import com.aptana.ide.lexer.IToken;
import com.aptana.ide.lexer.ITokenList;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.TokenList;
import com.aptana.ide.logging.LoggingPlugin;
import com.aptana.ide.logging.LoggingPreferences;
import com.aptana.ide.logging.LoggingPreferences.Rule;
import com.aptana.ide.logging.coloring.TokenCategories;
import com.aptana.ide.logging.coloring.TokenTypes;
import com.aptana.sax.AttributeSniffer;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 * @author Denis Denisenko
 */
public class LoggingStructureProvider implements ITreeContentProvider, ITableLabelProvider
{
    
	/**
     * COLORIZATION_SAVED preference property name.
     */
    public static final String COLORIZATION_SAVED = "Colorization saved"; //$NON-NLS-1$

    private String hashToken(IToken token)
	{
		return token.getLanguage() + "::" + token.getCategory() + "::" + token.getType(); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Class representing a category
	 */
	public class Category
	{

		private List<IToken> tokens;
		private Map<String, ColorizationStyle> styles;
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
			tokens = new LinkedList<IToken>();
			styles = new HashMap<String, ColorizationStyle>();
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
		    if (!containsToken(token))
		    {
		        tokens.add(token);
		    }
		}
		
		/**
         * Adds a token
         * 
         * @param token - token to add
         * @param pos - position
         */
        public void addToken(IToken token, int pos)
        {
            if (!containsToken(token))
            {
                tokens.add(pos, token);
            }
        }
		
		/**
		 * Removes token.
		 * @param token - token to remove.
		 * @return removed token position, -1 if not found
		 */
		public int removeToken(IToken token)
		{
		    Iterator<IToken> it = tokens.iterator();
		    int i = 0;
            while(it.hasNext())
            {
                if (tokensEqual(it.next(), token))
                {
                    it.remove();
                    return i;
                }
                i++;
            }
            
            return -1;
		}

		/**
		 * Gets a token
		 * 
		 * @param hash
		 * @return the token
		 */
		public IToken getToken(String hash)
		{
			for(IToken currentToken : tokens)
			{
			    if (hashToken(currentToken).equals(hash))
			    {
			        return currentToken;
			    }
			}
			return null;
		}

		/**
		 * Gets the tokens
		 * 
		 * @return - collection of tokens
		 */
		public List<IToken> getTokens()
		{
			return tokens;
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
		
		public void clear()
		{
		    tokens.clear();
		    styles.clear();
		}
		
		/**
		 * Checks if category contains token.
		 * @param token - token to check.
		 * @return true if contains, false otherwise.
		 */
		private boolean containsToken(IToken token)
		{
		    for (IToken currentToken : tokens)
		    {
		        if (tokensEqual(currentToken, token))
		        {
		                return true;
		        }
		    }
		    
		    return false;
		}

	}

	/**
	 * Categories.
	 */
	private Map<String, Category> categories;
	
	/**
	 * Regions.
	 */
	private Map<IToken, HashMap> regions;
	
	/**
	 * Image map.
	 */
	private Map imageMap;
	
	/**
	 * Background color.
	 */
	private Color backgroundColor;
	
	/**
	 * Line hightlight color.
	 */
	private Color lineHighlightColor;

	/**
	 * Caret color.
	 */
	private Color caretColor;
	
	/**
	 * Selection foreground color.
	 */
	private Color selectionForegroundColor;
	
	/**
	 * Selection background color.
	 */
	private Color selectionBackgroundColor;
	
	/**
	 * Folding backgrtound color.
	 */
	private Color foldingBackgroundColor;
	
	/**
	 * Folding foreground color.
	 */
	private Color foldingForegroundColor;
	
	/**
	 * Black color for initialization.
	 */
    private RGB black;
    
    /**
     * Main text font.
     */
    private FontData[] font;
    //private List<RuleChange> changes = new ArrayList<RuleChange>();
    
    /**
     * Autobolding.
     */
    private Boolean autobolding = null;
    
    /**
     * Wrapping.
     */
    private Boolean wrapping = null;
    
    /**
     * Cursor line color.
     */
    private RGB cursorLineColor = null;
    
    /**
     * Text foreground color.
     */
    private RGB textForegroundColor = null;
    
    /**
     * Backloglines.
     */
    private Integer backlogLines;
    
    /**
     * Temporary rules copy.
     */
    private List<LoggingPreferences.Rule> tempRules = new ArrayList<Rule>();
    
    /**
     * Initial wrapping.
     */
    private boolean initialWrapping;
    
    /**
     * Read timeout.
     */
    private Integer readTimeout;
    
    /**
     * Read buffer.
     */
    private Integer readBuffer;

    /**
     * Default encoding.
     */
    private String defaultEncoding;

	/**
	 * Creates a new language provider
	 * 
	 * @param language
	 */
	public LoggingStructureProvider()
	{
		categories = new HashMap();
		imageMap = new HashMap();
		regions = new HashMap();
		backgroundColor = null;
		lineHighlightColor = null;
		caretColor = null;
		selectionForegroundColor = null;
		selectionBackgroundColor = null;
		black = new RGB(0, 0, 0);
		loadProvider();
		
		List<LoggingPreferences.Rule> rules = LoggingPlugin.getDefault().getLoggingPreferences().getRules();
		tempRules.addAll(rules);
	}
	
	/**
	 * Loads the provider with the content of the language colorizer and token list for this language.
	 */
	public void loadProvider()
	{
	    loadProvider(null, null);
	}

	/**
	 * Loads the provider with the content of the language colorizer and token list for this language.
	 * @param initialTokens - initial tokens list..
	 * @param colorizer - colorizer containing initial colorization. may be null, then language reistry is used.
	 */
	public void loadProvider(List<IToken> initialTokens, LanguageColorizer colorizer)
	{
	    List<IToken> tokens;
	    if (initialTokens == null)
	    {
    		ITokenList tl = TokenTypes.getTokenList();
    		tokens = new ArrayList<IToken>();
    		if (tl != null)
    		{
    			for (int i = 0; i < tl.size(); i++)
    			{
    				IToken curr = (IToken) tl.get(i);
    				this.addToken(curr);
    				tokens.add(curr);
    			}
    		}
	    }
	    else
	    {
            tokens = initialTokens;
            
            for (IToken curr : tokens)
            {
                this.addToken(curr);
            }
	    }
		LanguageColorizer lc = colorizer;
		if (lc == null)
		{
		    lc = LanguageRegistry.getLanguageColorizer(TokenTypes.LANGUAGE, new ColorizerReader());
		}
		if (lc != null)
		{
			initializeColoring(lc);
			
			//initializing styles if needed
			checkDefaultStyles(tokens);
			
			createInitialWrapping();
		}
	}

    /**
     * Initializes coloring from language colorizer.
     * @param lc - colorizer.
     */
    private void initializeColoring(LanguageColorizer lc)
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
	
	/**
	 * Clears everything.
	 */
	public void clear()
	{
	    clearStyles();
	    Iterator cats = categories.values().iterator();
        while (cats.hasNext())
        {
            Category cat = (Category) cats.next();
            cat.clear();
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
	public Collection<IToken> getTokens()
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
		if (regions.get(token) == null)
		{
		    regions.put(token, new HashMap());
		}
	}
	
	/**
     * Adds a token to this language
     * 
     * @param token - token to add.
     * @param pos - position.
     */
    public void addToken(IToken token, int pos)
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
            category.addToken(token, pos);
        }
        
        if (regions.get(token) == null)
        {
            regions.put(token, new HashMap());
        }
    }

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof Category)
		{
		    return ((Category) parentElement).getTokens().toArray();
//			List list = new ArrayList(((Category) parentElement).getTokens());
//			Collections.sort(list, new Comparator()
//			{
//
//				public int compare(Object o1, Object o2)
//				{
//					String s1 = ((IToken) o1).getDisplayName();
//					String s2 = ((IToken) o2).getDisplayName();
//					return s1.compareTo(s2);
//				}
//
//			});
//			return list.toArray();
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
		
		//searching for default category
		for (Category category : categories.values())
		{
		    if (category.getName().equals(TokenTypes.DEFAULT_CATEGORY))
		    {
		        return getChildren(category);
		    }
		}
		
		return new Object[]{};
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
			if (columnIndex == 0 && element instanceof IToken)
			{
				return UnifiedEditorsPlugin.getImage("icons/checked.gif"); //$NON-NLS-1$
			}
			else if (columnIndex == 2 && element instanceof IToken)
			{
				if (!getRegions((IToken) element).isEmpty())
				{
					return UnifiedEditorsPlugin.getImage("icons/region.gif"); //$NON-NLS-1$
				}
			}
			else if (columnIndex == 3)
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
			else if (columnIndex == 4)
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
			else if (columnIndex == 5)
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
			else if (columnIndex == 6)
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
			if (columnIndex == 0 && element instanceof IToken)
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
//		if (columnIndex == 0)
//		{
//			if (element instanceof Category)
//			{
//				return ((Category) element).getDisplayName();
//			}
//		}
//		else if (columnIndex == 2)
//		{
//			if (element instanceof IToken)
//			{
//				return ((IToken) element).getDisplayName();
//			}
//		}
	    if (columnIndex == 1)
        {
            if (element instanceof IToken)
            {
                //return ((IToken) element).getDisplayName();
                return ((IToken) element).getType();
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
		textForegroundColor = null;
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
	    buildLanguageColorizer(lc);
		try
		{
		    ColorizerWriter writer = 
		        new ColorizerWriter(); 
		    writer.buildColorizationPreference(lc, lc.getLanguage(), prefId);
			UnifiedEditorsPlugin.getDefault().getPreferenceStore().firePropertyChangeEvent(COLORIZATION_SAVED,
					COLORIZATION_SAVED, COLORIZATION_SAVED);
		}
		catch (LexerException e)
		{
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), e.getMessage());
		}
	}
	
	/**
     * Builds the language colorizer current in this provider.
     * 
     * @param lc - colorizer to build.
     */
    public void buildLanguageColorizer(LanguageColorizer lc)
    {
        filterRegionsByTokens();
        
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
     * Gets text foreground color
     * 
     * @return - text foreground color
     */
    public RGB getTextForegroundColor()
    {
        if (textForegroundColor == null)
        {
            textForegroundColor = LoggingPlugin.getDefault().
                getLoggingPreferences().getTextColorRGB();
        }
        
        return textForegroundColor;
    }

    /**
     * Sets the text foreground color
     * 
     * @param color -
     *            text foreground color
     */
    public void setTextForegroundColor(RGB color)
    {
        this.textForegroundColor = color;
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
		return TokenTypes.LANGUAGE;
		
	}

	/**
	 * Sets the language
	 * 
	 * @param language -
	 *            new language
	 */
	public void setLanguage(String language)
	{
		
	}

	/**
	 * Builds a colorization file
	 * 
	 * @param file
	 * @throws LexerException
	 */
	public void buildColorizationFile(File file) throws LexerException
	{
	    LanguageColorizer colorizer = new LanguageColorizer(TokenTypes.LANGUAGE);
	    buildLanguageColorizer(colorizer);
		LoggingColorizerWriter writer = 
            new LoggingColorizerWriter(tempRules);
		writer.buildColorizationFile(colorizer, getLanguage(), file);
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
			    //reading tokens (rules)
			    TokensReader tokensReader = new TokensReader(tempRules);
			    InputStream stream = new FileInputStream(file);
			    try
			    {
			        tokensReader.read(stream);
			    }
			    finally
			    {
			        stream.close();
			    }
			    
			    //creating tokens list
			    final List<IToken> newTokens = new ArrayList<IToken>();
                ITokenList tl = TokenTypes.getTokenList();
                
                for (LoggingPreferences.Rule rule : tempRules)
                {
                    IToken token = tl.createToken();
                    token.setCategory(TokenTypes.DEFAULT_CATEGORY);
                    token.setType(rule.getName());
                    newTokens.add(token);
                }
                
                TokenList newTokenList = tokenListByTokens(newTokens);
                
			    //reading color info
			    LoggingColorizerReader reader = new LoggingColorizerReader(newTokenList, false);
			    LanguageColorizer colorizer = reader.importColorization(file);
				this.clear();
				
				this.loadProvider(newTokens, colorizer);
			}
			else
			{
			    IdeLog.logError(LoggingPlugin.getDefault(), Messages.LoggingStructureProvider_ERR_Loading);
			}

		}
		catch (Throwable e)
		{
		    IdeLog.logError(LoggingPlugin.getDefault(), Messages.LoggingStructureProvider_ERR_Loading, e);
		}		
	}

	/**
	 * Resets this language to its defaults as obtained from the LanguageRegistry
	 */
	public void resetToLanguageDefaults()
	{
		if (getLanguage() != null)
		{
		    createInitialWrapping();
		    LoggingPreferences preferences = LoggingPlugin.getDefault().getLoggingPreferences();
		    
            font = preferences.getDefaultFont(); 
            autobolding = preferences.getDefaultAutoBolding();
            wrapping = preferences.getDefaultWrapping();
            cursorLineColor = preferences.getDefaultCursorLineColor();
            textForegroundColor = preferences.getDefaultTextColor();
            backlogLines = preferences.getDefaultBacklogLines();

            tempRules.clear();

            readTimeout = preferences.getDefaultReadTimeout();
            readBuffer = preferences.getDefaultReadBuffer();
            defaultEncoding = preferences.getDefaultDefaultEncoding();

            this.clearStyles();
            this.clear();
            preferences.fillDefaultRules(this);
        }
	}
	
	public void restoreGeneralDefaults()
	{
	    if (getLanguage() != null)
        {
            createInitialWrapping();
            LoggingPreferences preferences = LoggingPlugin.getDefault().getLoggingPreferences();
            
            font = preferences.getDefaultFont(); 
            autobolding = preferences.getDefaultAutoBolding();
            wrapping = preferences.getDefaultWrapping();
            cursorLineColor = preferences.getDefaultCursorLineColor();
            textForegroundColor = preferences.getDefaultTextColor();
            backlogLines = preferences.getDefaultBacklogLines();

            readTimeout = preferences.getDefaultReadTimeout();
            readBuffer = preferences.getDefaultReadBuffer();
            defaultEncoding = preferences.getDefaultDefaultEncoding();
        }
	}
	
	public void restoreColorizationDefautls()
	{
	    if (getLanguage() != null)
        {
	        LoggingPreferences preferences = LoggingPlugin.getDefault().getLoggingPreferences();

            tempRules.clear();

            this.clearStyles();
            this.clear();
            preferences.fillDefaultRules(this);
        }
	}

	/**
	 * Refreshes the tokens in this language
	 */
	public void refreshTokens()
	{
		ITokenList tl = TokenTypes.getTokenList();
		
		List<String> original = new ArrayList<String>();
		if (tl != null)
		{
			for (int i = 0; i < tl.size(); i++)
			{
				IToken curr = (IToken) tl.get(i);
				original.add(curr.getType());
				
				Category category = this.getCategory(curr.getCategory());
				
				if (category == null || category.getToken(curr.getType()) == null)
				{
					this.addToken(curr);
				}
			}
			
			List<IToken> toRemove = new ArrayList<IToken>(); 
			for (IToken token : getTokens())
			{
			    if (!original.contains(token.getType()))
			    {
			        toRemove.add(token);
			    }
			}
			for (IToken token : toRemove)
			{
			    removeToken(token);
			}
		}
		
		checkDefaultStyles(tl);
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
	
	/**
	 * Removes rule.
	 * @param rule - rule to remove.
	 */
	public void removeRule(IToken ruletoken)
	{
	    LoggingPreferences.Rule rule = new LoggingPreferences.Rule(ruletoken.getType(), null, true, true);
	    
	    IToken toRemove = null;
	    for(IToken token : getTokens())
	    {
	        if (token.getType().equals(rule.getName()))
	        {
	            toRemove = token;
	            break;
	        }
	    }
	    if (toRemove != null)
	    {
	        removeStyle(toRemove);
	        removeToken(toRemove);
	    }
	    
	    getCurrentRules().remove(rule);
	}
	
	
	
	/**
	 * Adds rule.
	 * @param name - rule name.
	 * @param content - rule content.
	 * @param isRegexp - whether rule is regexp.
	 * @param isCaseSensetive - whether rule is case sensitive.
	 */
	public void addRule(String name, String content, boolean isRegexp, boolean isCaseSensitive)
	{
	    LoggingPreferences.Rule rule = new LoggingPreferences.Rule(name, content, isRegexp, isCaseSensitive); 
//	    IToken token = TokenTypes.getTokenList().createToken();
//        token.setCategory(TokenTypes.DEFAULT_CATEGORY);
//        token.setType(rule.getName());
//        addToken(token);
//        checkDefaultStyle(token);
//        getCurrentRules().add(rule);
	    addRule(rule, null);
	}
	
	/**
     * Adds rule to the beginning.
     * @param name - rule name.
     * @param content - rule content.
     * @param isRegexp - whether rule is regexp.
     * @param isCaseSensetive - whether rule is case sensitive.
     */
    public void addRuleToBeginning(String name, String content, boolean isRegexp, boolean isCaseSensitive)
    {
        LoggingPreferences.Rule rule = new LoggingPreferences.Rule(name, content, isRegexp, isCaseSensitive); 
        addRule(rule, 0, null);
    }
	
	/**
     * Adds rule.
     * @param name - rule name.
     * @param content - rule content.
     * @param isRegexp - whether rule is regexp.
     * @param isCaseSensetive - whether rule is case sensitive.
     * @param color - color to associate with the rule.
     * @param bold - whether rule font should be bold.
     * @param bold - whether rule font should be italic.
     * @param bold - whether rule font should be underlined.
     */
    public void addRule(String name, String content, boolean isRegexp, boolean isCaseSensitive, RGB color,
            boolean bold, boolean italic, boolean underline)
    {
        LoggingPreferences.Rule rule = new LoggingPreferences.Rule(name, content, isRegexp, isCaseSensitive); 
//        IToken token = TokenTypes.getTokenList().createToken();
//        token.setCategory(TokenTypes.DEFAULT_CATEGORY);
//        token.setType(rule.getName());
//        addToken(token);
//        if(getStyle(token) == null)
//        {
//            ColorizationStyle style = new ColorizationStyle();
//            style.setForegroundColor(UnifiedColorManager.getInstance()
//                    .getColor(color));
//            style.setName(token.getCategory() + "_" + token.getType());
//            style.setBold(bold);
//            style.setItalic(italic);
//            style.setUnderline(underline);
//            addStyle(token, style);
//        }
//        getCurrentRules().add(rule);
        
        ColorizationStyle style = createStyle(name, color, bold, italic, underline);
        addRule(rule, style);
    }
    
	/**
     * Adds rule.
     * @param rule - rule to add.
     * @param pos - position to add rule to.
     */
    public void addRule(LoggingPreferences.Rule rule, int pos)
    {
//        IToken token = TokenTypes.getTokenList().createToken();
//        token.setCategory(TokenTypes.DEFAULT_CATEGORY);
//        token.setType(rule.getName());
//        addToken(token, pos);
//        checkDefaultStyle(token);
//        getCurrentRules().add(pos, rule);
        addRule(rule, pos, null);
    }
    
    /**
     * Adds rule.
     * @param rule - rule to add.
     * @param pos - position to add rule to.
     * @param style - style to apply, may be null.
     */
    public void addRule(LoggingPreferences.Rule rule, int pos, ColorizationStyle style)
    {
        IToken token = TokenTypes.getTokenList().createToken();
        token.setCategory(TokenTypes.DEFAULT_CATEGORY);
        token.setType(rule.getName());
        addToken(token, pos);
        checkDefaultStyle(token);
        getCurrentRules().add(pos, rule);
        if (style != null)
        {
            addStyle(token, style);
        }
    }
    
    /**
     * Adds rule.
     * @param rule - rule to add.
     * @param style - style to apply, may be null.
     */
    public void addRule(LoggingPreferences.Rule rule, ColorizationStyle style)
    {
        IToken token = TokenTypes.getTokenList().createToken();
        token.setCategory(TokenTypes.DEFAULT_CATEGORY);
        token.setType(rule.getName());
        addToken(token);
        checkDefaultStyle(token);
        getCurrentRules().add(rule);
        if (style != null)
        {
            addStyle(token, style);
        }
    }
	
	/**
	 * Gets current rules state. 
	 * @return rules state.
	 */
	public List<LoggingPreferences.Rule> getCurrentRules()
	{
	    return tempRules;
	}
	
	public void updateRule(IToken ruletoken, String newContent, boolean isRegexp,
	        boolean isCaseSensitive)
	{
	    LoggingPreferences.Rule rule = new LoggingPreferences.Rule(ruletoken.getType(), newContent, isRegexp,
	            isCaseSensitive);
	    
	    //removing
	    IToken toRemove = null;
        for(IToken token : getTokens())
        {
            if (token.getType().equals(rule.getName()))
            {
                toRemove = token;
                break;
            }
        }
        if (toRemove != null)
        {
            ColorizationStyle style = getStyle(toRemove);
            int removedIndex = removeToken(toRemove);
            
            getCurrentRules().remove(rule);
            
            //adding
//            IToken token = TokenTypes.getTokenList().createToken();
//            token.setCategory(TokenTypes.DEFAULT_CATEGORY);
//            token.setType(rule.getName());
//            addToken(token, removedIndex);
//            addStyle(token, style);
//            getCurrentRules().add(rule);
            
            IToken token = TokenTypes.getTokenList().createToken();
            token.setCategory(TokenTypes.DEFAULT_CATEGORY);
            token.setType(rule.getName());
            addToken(token, removedIndex);
            addStyle(token, style);
            checkDefaultStyle(token);
            getCurrentRules().add(removedIndex, rule);
        }
	}
	
	/**
	 * Moves rule down.
	 * @param token - token.
	 */
	public void moveRuleDown(IToken token)
	{
	    int pos = rulePositionByToken(token);
	    ColorizationStyle style = getStyleByToken(token);
	    if (pos == -1 || pos == tempRules.size() - 1)
	    {
	        return;
	    }
	    LoggingPreferences.Rule rule  = getCurrentRules().get(pos);
	    removeRule(token);
	    addRule(rule, pos + 1, style);
	}

	/**
	 * Moves rule up.
	 * @param token - token.
	 */
	public void moveRuleUp(IToken token)
    {
        int pos = rulePositionByToken(token);
        ColorizationStyle style = getStyleByToken(token);
        if (pos == -1 || pos == 0)
        {
            return;
        }
        LoggingPreferences.Rule rule  = getCurrentRules().get(pos);
        removeRule(token);
        addRule(rule, pos - 1, style);
    }
	
	/**
	 * Applies changes
	 */
	public void applyChanges()
	{
	   applyRuleChanges();
	   applyAutoBolding();
	   applyCursorLineColor();
	   applyTextForegroundColor();
	   applyFont();
	   applyReadTimeout();
	   applyReadBuffer();
	   applyDefaultEncoding();
	   applyBacklogLines();
	   applyWrapping();
	}
	
	/**
     * Gets whether "auto-bolding" on new data is on. 
     * @return whether "auto-bolding" on new data is on.
     */
    public boolean getAutoBolding()
    {
        if (autobolding == null)
        {
            autobolding = LoggingPlugin.getDefault().getLoggingPreferences().getAutoBolding();
        }
        
        return autobolding;
    }
    
    /**
     * Sets font value.
     * @param font - font to set.
     */
    public void setFont(FontData[] font)
    {
        this.font = font;
    }
    
    /**
     * Gets font. 
     * @return font.
     */
    public FontData[] getFont()
    {
        if (font == null)
        {
            font = LoggingPlugin.getDefault().getLoggingPreferences().getFontData();
        }
        
        return font;
    }
    
    /**
     * Sets read timeout.
     * @param timeout - timeout to set.
     */
    public void setReadTimeout(int timeout)
    {
        this.readTimeout = timeout;
    }
    
    /**
     * Gets read timeout. 
     * @return timeout.
     */
    public int getReadTimeot()
    {
        if (readTimeout == null)
        {
            readTimeout = LoggingPlugin.getDefault().getLoggingPreferences().getReadTimeout();
        }
        
        return readTimeout;
    }
    
    /**
     * Sets read buffer.
     * @param buffer - buffer to set.
     */
    public void setReadBuffer(int buffer)
    {
        this.readBuffer = buffer;
    }
    
    /**
     * Gets read buffer. 
     * @return buffer.
     */
    public int getReadBuffer()
    {
        if (readBuffer == null)
        {
            readBuffer = LoggingPlugin.getDefault().getLoggingPreferences().getReadBuffer();
        }
        
        return readBuffer;
    }
    
    /**
     * Sets autobolding value.
     * @param autobolding - autobolding to set.
     */
    public void setAutoBolding(boolean autobolding)
    {
        this.autobolding = autobolding;
    }
    
    /**
     * Gets whether "wrapping is on. 
     * @return whether wrapping is on.
     */
    public boolean getWrapping()
    {
        if (wrapping == null)
        {
            wrapping = LoggingPlugin.getDefault().getLoggingPreferences().getWrapping();
        }
        
        return wrapping;
    }

    
    /**
     * Sets wrapping value.
     * @param wrapping - wrapping to set.
     */
    public void setWrapping(boolean wrapping)
    {
        this.wrapping = wrapping;
    }
    
    /**
     * Gets backlog lines. 
     * @return backlog lines.
     */
    public int getBacklogLines()
    {
        if (backlogLines == null)
        {
            backlogLines = LoggingPlugin.getDefault().getLoggingPreferences().getBacklogLines();
        }
        
        return backlogLines;
    }

    
    /**
     * Sets backlog lines. 
     * @param lines - lines to set.
     */
    public void setBacklogLines(int lines)
    {
        this.backlogLines = lines;
    }
    
    /**
     * Gets cursor line color. 
     * @return cursor line color.
     */
    public RGB getCursorLineColor()
    {
        if (cursorLineColor == null)
        {
            cursorLineColor = LoggingPlugin.getDefault().
                getLoggingPreferences().getCursorLineColor();
        }
        
        return cursorLineColor;
    }
    
    /**
     * Sets cursor line color.
     * @param color - color to set.
     */
    public void setCursorLineColor(RGB color)
    {
        this.cursorLineColor = color;
    }
    
    /**
     * Checks whether tokens are equal.
     * @param token1 - token1.
     * @param token2 - token2.
     * @return true if equal, false otherwise
     */
    public boolean tokensEqual(IToken token1, IToken token2)
    {
        return token1 == null ? token2 == null :
            hashToken(token1).equals(hashToken(token2));
    }
    
    /**
     * Sets default encoding.
     * @param encoding - encoding to set.
     */
    public void setDefaultEncoding(String encoding)
    {
        defaultEncoding = encoding;
    }
    
    /**
     * Gets default encoding.
     * @return default encoding
     */
    public String getDefaultEncoding()
    {
        if (defaultEncoding == null)
        {
            defaultEncoding = LoggingPlugin.getDefault().
                getLoggingPreferences().getDefaultEncoding();
        }
        return defaultEncoding;
    }

	/**
	 * Initializes tokens with default style if needed.
     * @param tokens - tokens to checks.
     */
    private void checkDefaultStyles(ITokenList tokens)
    {
        for (int i = 0; i < tokens.size(); i++)
        {
            IToken token = tokens.get(i);
            checkDefaultStyle(token);
        }
    }
    
    /**
     * Initializes tokens with default style if needed.
     * @param tokens - tokens to checks.
     */
    private void checkDefaultStyles(List<IToken> tokens)
    {
        for (int i = 0; i < tokens.size(); i++)
        {
            IToken token = tokens.get(i);
            checkDefaultStyle(token);
        }
    }

    /**
     * Checks token style and initializes if needed.
     * @param token - token to check.
     */
    private void checkDefaultStyle(IToken token)
    {
        
        //if no style is set, performing default initialization
        if(getStyle(token) == null)
        {
            ColorizationStyle style = new ColorizationStyle();
            style.setForegroundColor(UnifiedColorManager.getInstance()
                    .getColor(black));
            style.setName(token.getCategory() + "_" + token.getType()); //$NON-NLS-1$
            addStyle(token, style);
        }
    }

    /**
     * Removes token.
     * @param token - token to remove.
     * @return removed token position or -1
     */
    private int removeToken(IToken token)
    {
        Category cat = getCategory(token.getCategory());
        int index = cat.removeToken(token);
        Map regionMap = (Map) regions.get(token);
        regionMap.clear();
        regions.remove(token);
        return index;
    }
    
    /**
     * Applies autobolding changes.
     */
    private void applyAutoBolding()
    {
        if (autobolding != null)
        {
            LoggingPlugin.getDefault().getLoggingPreferences().setAutoBolding(autobolding);
        }
    }
    
    /**
     * Applies font changes.
     */
    private void applyFont()
    {
        if (font != null)
        {
            LoggingPlugin.getDefault().getLoggingPreferences().setFontData(font);
        }
    }
    
    /**
     * Applies wrapping changes.
     */
    private void applyWrapping()
    {
        if (wrapping != null && !wrapping.equals(initialWrapping))
        {
            LoggingPlugin.getDefault().getLoggingPreferences().setWrapping(wrapping);
        }
    }
    
    /**
     * Applies wrapping changes.
     */
    private void applyBacklogLines()
    {
        if (backlogLines != null)
        {
            LoggingPlugin.getDefault().getLoggingPreferences().setBacklogLines(backlogLines);
        }
    }
    
    /**
     * Applies read timeout.
     */
    private void applyReadTimeout()
    {
        if (readTimeout != null)
        {
            LoggingPlugin.getDefault().getLoggingPreferences().setReadTimeout(readTimeout);
        }
    }
    
    /**
     * Applies read buffer.
     */
    private void applyReadBuffer()
    {
        if (readBuffer != null)
        {
            LoggingPlugin.getDefault().getLoggingPreferences().setReadBuffer(readBuffer);
        }
    }
    
    /**
     * Applies read buffer.
     */
    private void applyDefaultEncoding()
    {
        if (defaultEncoding != null)
        {
            LoggingPlugin.getDefault().getLoggingPreferences().setDefaultEncoding(defaultEncoding);
        }
    }
    
    /**
     * Applies cursor line color.
     */
    private void applyCursorLineColor()
    {
        if (cursorLineColor != null)
        {
            LoggingPlugin.getDefault().getLoggingPreferences().setCursorLineColor(cursorLineColor);
        }
    }
    
    /**
     * Applies cursor line color.
     */
    private void applyTextForegroundColor()
    {
        if (textForegroundColor != null)
        {
            LoggingPlugin.getDefault().getLoggingPreferences().setTextColor(textForegroundColor);
        }
    }

    /**
     * Applies stored rules changes.
     */
    void applyRuleChanges()
    {
        List<LoggingPreferences.Rule> rules = LoggingPlugin.getDefault().getLoggingPreferences().getRules();
        rules.clear();
        rules.addAll(tempRules);   
    }
    
    void filterRegionsByTokens()
    {
        Collection<IToken> tokens = getTokens();
        Map<IToken, HashMap> filteredRegions = new HashMap<IToken, HashMap>();
        for (IToken token : tokens)
        {
            if (regions.containsKey(token))
            {
                filteredRegions.put(token, regions.get(token));
            }
        }
        
        regions.clear();
        regions.putAll(filteredRegions);
    }
    
    /**
     * Creates initial wrapping value.
     */
    private void createInitialWrapping()
    {
        initialWrapping = LoggingPlugin.getDefault().getLoggingPreferences().getWrapping();
    }
    
    /**
     * Creates token list by tokens.
     * @param tokens - tokens
     * @return token list
     */
    private TokenList tokenListByTokens(final List<IToken> tokens)
    {
        TokenList newTokenList = new TokenList();
        newTokenList.setCategoryMap(new IEnumerationMap()
        {

            public int getIntValue(String name)
            {
                return TokenCategories.getIntValue(name);
            }

            public String getName(int index)
            {
                return TokenCategories.getName(index);
            }

            public String[] getNames()
            {
                return TokenCategories.getNames();
            }
            
        });
        newTokenList.setTypeMap(new IEnumerationMap()
        {
            public int getIntValue(String name)
            {
                for (int i = 0; i < tokens.size(); i++)
                {
                    IToken token = tokens.get(i);
                    if (token.getType().equals(name))
                    {
                        return i;
                    }
                }
                
                return -1;
            }

            public String getName(int index)
            {
                return tokens.get(index).getType();
            }

            public String[] getNames()
            {
                String[] names = new String[tokens.size()];
                tokens.toArray(names);
                return names;
            }
            
        });
        for (IToken token : tokens)
        {
            newTokenList.add(token);
        }
        return newTokenList;
    }
    
    /**
     * Gets rule by token.
     * @param token - token. 
     * @return rule or null
     */
    private LoggingPreferences.Rule ruleByToken(IToken token)
    {
        int pos = rulePositionByToken(token);
        if (pos != -1)
        {
            return tempRules.get(pos);        
        }
        
        return null;
    }
    
    /**
     * Gets token by rule.
     * @param rule - rule.
     * @return token or null.
     */
    private IToken tokenByRule(LoggingPreferences.Rule rule)
    {
        for (IToken token : getTokens())
        {
            if (token.getType().equals(rule.getName()))
            {
                return token;
            }
        }
        
        return null;
    }
    
    /**
     * Gets rule position by token.
     * @param token - token.
     * @return position or -1 if not found
     */
    private int rulePositionByToken(IToken token)
    {
        for (int i = 0; i < tempRules.size(); i++)
        {
            LoggingPreferences.Rule rule = tempRules.get(i);
            if (rule.getName().equals(token.getType()))
            {
                return i;
            }
        }
        
        return -1;
    }
    
    /**
     * Creates style.
     * @param tokenName - token name.
     * @param color - color.
     * @param bold - bold or not.
     * @param italic - italic or not.
     * @param underline - underlined or not.
     * @return style.
     */
    private ColorizationStyle createStyle(String tokenName, RGB color, boolean bold,
            boolean italic, boolean underline)
    {
        ColorizationStyle style;
        style = new ColorizationStyle();
        style.setForegroundColor(UnifiedColorManager.getInstance().getColor(
                color));
        style.setName(TokenTypes.DEFAULT_CATEGORY + "_" + tokenName); //$NON-NLS-1$
        style.setBold(bold);
        style.setItalic(italic);
        style.setUnderline(underline);
        return style;
    }
    
    /**
     * Gets token style.
     * @param token - token.
     * @return token style.
     */
    private ColorizationStyle getStyleByToken(IToken token)
    {
        Category cat = getCategory(token.getCategory());
        if (cat == null)
        {
            return null;
        }
        
        return cat.getTokenStyle(token);
    }
}
