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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.logging;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.editors.unified.colorizer.ColorizerReader;
import com.aptana.ide.editors.unified.colorizer.LanguageColorizer;
import com.aptana.ide.logging.coloring.TokenTypes;
import com.aptana.ide.logging.preferences.ILoggingPreferenceListener;
import com.aptana.ide.logging.preferences.LoggingStructureProvider;

/**
 * Logging preferences.
 * @author Denis Denisenko
 */
public class LoggingPreferences
{
    public static class Rule
    {
        /**
         * Rule name.
         */
        private String name;
        
        /**
         * Rule content.
         */
        private String rule;
        
        /**
         * Whether content is regexp.
         */
        private boolean isRegexp;
        
        /**
         * Whether rule is case insensitive.
         */
        private boolean isCaseInsensitive;

        /**
         * Rule constructor.
         * @param name - rule name.
         * @param rule - rule content.
         * @param isRegexp - whether rule is regexp.
         * @param isCaseInsensitive - whether rule is case insensetive.
         */
        public Rule(String name, String rule, boolean isRegexp, boolean isCaseInsensitive)
        {
            this.name = name;
            this.rule = rule;
            this.isRegexp = isRegexp;
            this.isCaseInsensitive = isCaseInsensitive;
        }

        /**
         * Gets rule name.
         * @return the name
         */
        public String getName()
        {
            return name;
        }

        /**
         * Gets rule content.
         * @return the rule
         */
        public String getRule()
        {
            return rule;
        }

        /**
         * Gets whether rule content is regexp.
         * @return the isRegexp
         */
        public boolean isRegexp()
        {
            return isRegexp;
        }
        
        /**
         * Sets new rule. Does not update any dependencies.
         * @param rule - rule to set.
         */
        public void setRule(String rule)
        {
            this.rule = rule;
        }
        
        /**
         * Gets whether rule is case insensitive.
         * @return whether rule is case insensitive.
         */
        public boolean isCaseInsensitive()
        {
            return isCaseInsensitive;
        }

        /**
          * {@inheritDoc}
          */
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        /**
          * {@inheritDoc}
          */
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final Rule other = (Rule) obj;
            if (name == null)
            {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            return true;
        }

        /**
          * {@inheritDoc}
          */
        @Override
        public String toString()
        {
            return name;
        }
        
        
    }
    
    /**
     * Modifiable rules list.
     * @author Denis Denisenko
     */
    private class RulesList extends AbstractList<Rule>
    {
        /**
         * Base.
         */
        private List<Rule> base;
        
        /**
         * RulesList constructor.
         * @param base - list to base on.
         */
        public RulesList(List<Rule> base)
        {
           this.base = base; 
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Rule get(int index)
        {
            return base.get(index);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int size()
        {
            return base.size();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void add(int index, Rule element)
        {
            if (base.contains(element))
            {
                return;
            }
            
            base.add(index, element);
            saveRules(base);
            notifyRulesChanged();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Rule remove(int index)
        {
            Rule toReturn = base.remove(index);
            saveRules(base);
            notifyRulesChanged();
            return toReturn;
        }

        /**
          * {@inheritDoc}
          */
        @Override
        public void clear()
        {
            base.clear();
            saveRules(base);
            notifyRulesChanged();
        }

        /**
          * {@inheritDoc}
          */
        @Override
        public boolean addAll(int index, Collection<? extends Rule> c)
        {
            boolean added = base.addAll(index, c);
            saveRules(base);
            return added;
        } 
    }
    
    
    /**
     * Colorizer ID.
     */
    public static final String COLORIZER_ID = "com.aptana.ide.logging.coloring.colorizer"; //$NON-NLS-1$
    
    /**
     * Base preference key.
     */
    private static final String BASE_KEY = "com.aptana.ide.logging.preferences.LoggingPreferences"; //$NON-NLS-1$
    
    /**
     * Default rules extension point ID.
     */
    public static final String DEFAULT_RULES_POINT_ID = "com.aptana.ide.logging.defaultRules"; //$NON-NLS-1$
    
    /**
     * Rules list length key.
     */
    private static final String LENGTH_KEY = BASE_KEY + "_length"; //$NON-NLS-1$
    
    /**
     * Rules list name key.
     */
    private static final String NAME_KEY = BASE_KEY + "_name"; //$NON-NLS-1$
    
    /**
     * Rules list content key.
     */
    private static final String CONTENT_KEY = BASE_KEY + "_content"; //$NON-NLS-1$
    
    /**
     * Rules list isRegexp key.
     */
    private static final String REGEXP_KEY = BASE_KEY + "_isregexp"; //$NON-NLS-1$
    
    /**
     * Rules list isRegexp key.
     */
    private static final String CASEINSENSITIVE_KEY = BASE_KEY + "_iscasesensitive"; //$NON-NLS-1$
    
    /**
     * Autobolding key.
     */
    private static final String AUTOBOLDING_KEY = BASE_KEY + "_autobolding"; //$NON-NLS-1$
    
    /**
     * Wrapping key.
     */
    private static final String WRAPPING_KEY = BASE_KEY + "_wrapping"; //$NON-NLS-1$
    
    /**
     * Key for main text font.
     */
    public static final String MAIN_TEXT_FONT_KEY = BASE_KEY + ".maintextfont"; //$NON-NLS-1$
    
    /**
     * Key for preference of whether to color cursor line.
     */
    public static final String CURSORLINE_KEY = BASE_KEY + ".cursorline"; //$NON-NLS-1$
    
    /**
     * Key for cursor line color.
     */
    public static final String CURSORLINE_COLOR_KEY = BASE_KEY + ".cursorlinecolor"; //$NON-NLS-1$
    
    /**
     * Key for text color.
     */
    public static final String TEXT_COLOR_KEY = BASE_KEY + ".textcolor"; //$NON-NLS-1$
    
    /**
     * Key for read timeout.
     */
    public static final String READ_TIMEOUT_KEY = BASE_KEY + ".readtimeout"; //$NON-NLS-1$
    
    /**
     * Key for read buffer.
     */
    public static final String READ_BUFFER_KEY = BASE_KEY + ".readbuffer"; //$NON-NLS-1$
    
    /**
     * Key for default encoding.
     */
    public static final String DEFAULT_ENCODING_KEY = BASE_KEY + ".defaultencoding"; //$NON-NLS-1$

    /**
     * Backlog lines key.
     */
    private static final String BACKLOG_LINES_KEY = BASE_KEY + ".backloglines"; //$NON-NLS-1$
    
    /**
     * Default font size for windows.
     */
    private static final int WINDOWS_DEFAULT_FONT_SIZE = 9;
    
    /**
     * Default font size for MacOS.
     */
    private static final int MAC_OS_DEFAULT_FONT_SIZE = 10;
    
    /**
     * Default font size for Linux.
     */
    private static final int LINUX_DEFAULT_FONT_SIZE = 10;
    
    /**
     * DEFAULT_FONT_FAMILY
     */
    private static final String DEFAULT_FONT_FAMILY = "Courier"; //$NON-NLS-1$

    /**
     * Rule element name.
     */
    private static final Object RULE_ELEMENT = "rule"; //$NON-NLS-1$

    /**
     * Rule name attribute.
     */
    private static final String NAME_ATTRIBVUTE = "name"; //$NON-NLS-1$

    /**
     * Rule content attribute.
     */
    private static final String CONTENT_ATTRIBUTE = "content"; //$NON-NLS-1$

    /**
     * Rule isRegexp attribute.
     */
    private static final String ISREGEXP_ATTRIBUTE = "regexp"; //$NON-NLS-1$

    /**
     * Rule isCaseInsensitive attribute.
     */
    private static final String ISCASEINSENSITIVE_ATTRIBUTE = "caseInsensitive"; //$NON-NLS-1$

    /**
     * Rule isBold attribute.
     */
    private static final String BOLD_ATTRIBUTE = "bold"; //$NON-NLS-1$

    /**
     * Rule isItalic attribute.
     */
    private static final String ITALIC_ATTRIBUTE = "italic"; //$NON-NLS-1$

    /**
     * Rule isUnderline attribute.
     */
    private static final String UNDERLINE_ATTRIBUTE = "underline"; //$NON-NLS-1$

    /**
     * Rule isColor attribute.
     */
    private static final String COLOR_ATTRIBUTE = "color"; //$NON-NLS-1$
    
    /**
     * Rules.
     */
    private RulesList rules;

    /**
     * Listeners.
     */
    private List<ILoggingPreferenceListener> listeners = new ArrayList<ILoggingPreferenceListener>(); 
    
    /**
     * Listeners, pending to be added.
     */
    private List<ILoggingPreferenceListener> pendingToAddListeners = new ArrayList<ILoggingPreferenceListener>();
    
    /**
     * Listeners, pending to be removed.
     */
    private List<ILoggingPreferenceListener> pendingToRemoveListeners = new ArrayList<ILoggingPreferenceListener>();
    
    /**
     * Autobolding.
     */
    private Boolean autoBolding;
    
    /**
     * Wrapping.
     */
    private Boolean wrapping;
    
    /**
     * Font registry.
     */
    private FontRegistry fontRegistry = new FontRegistry(Display.getCurrent());
    
    /**
     * Whether main font is initialized.
     */
    private boolean mainFontLoaded = false;
    
    /**
     * Color registry.
     */
    private ColorRegistry colorRegistry = new ColorRegistry(Display.getCurrent());
    
    /**
     * Read timeout.
     */
    private Integer readTimeout;
    
    /**
     * Read buffer.
     */
    private Integer readBuffer;

    /**
     * Defautl encoding.
     */
    private String defaultEncoding;

    /**
     * Backlog lines.
     */
    private Integer backLogLines;
    
    /**
     * Gets the modifiable list of the names of the regexp-based rules.
     * @return names of the regexp-based rules.
     */
    public List<Rule> getRules()
    {
        if (rules == null)
        {
            final List<Rule> base = loadRules();
            rules = new RulesList(base);
        }
        
        return rules;
    }
    
    /**
     * Initializes preferences.
     */
    public void initializePreferences()
    {
        IPreferenceStore store = getPreferenceStore();
        store.setDefault(WRAPPING_KEY, false);
        store.setDefault(AUTOBOLDING_KEY, true);
        store.setDefault(CURSORLINE_KEY, true);
        PreferenceConverter.setDefault(store, CURSORLINE_COLOR_KEY, new RGB(233, 233, 235));
        PreferenceConverter.setDefault(store, TEXT_COLOR_KEY, new RGB(128,128,128));
        store.setDefault(READ_TIMEOUT_KEY, 100);
        store.setDefault(READ_BUFFER_KEY, 1024 * 32);
        store.setDefault(DEFAULT_ENCODING_KEY, "UTF-8"); //$NON-NLS-1$
        store.setDefault(BACKLOG_LINES_KEY, 750);
        
        initializeDefaultFont();
        
        //if no colorization info is available, writting defaults
        if (store.getString(LENGTH_KEY).length() == 0)
        {
            initializeDefaultRules();
        }
    }

    /**
     * Gets whether "auto-bolding" on new data is on. 
     * @return whether "auto-bolding" on new data is on.
     */
    public boolean getAutoBolding()
    {
        if (autoBolding == null)
        {
            loadAutoBolding();
        }
        
        return autoBolding;
    }
    
    /**
     * Sets autobolding value.
     * @param autobolding - autobolding to set.
     */
    public void setAutoBolding(boolean autobolding)
    {
        this.autoBolding = autobolding;
        saveAutoBolding();   
    }
    
    /**
     * Gets read timeout.
     * @return read timeout.
     */
    public int getReadTimeout()
    {
        if (readTimeout == null)
        {
            loadReadTimeout();
        }
        
        return readTimeout;
    }
    
    /**
     * Sets autobolding value.
     * @param autobolding - autobolding to set.
     */
    public void setReadTimeout(int readtimeout)
    {
        this.readTimeout = readtimeout;
        saveReadTimeout();   
    }
    
    /**
     * Gets read timeout.
     * @return read timeout.
     */
    public int getReadBuffer()
    {
        if (readBuffer == null)
        {
            loadReadBuffer();
        }
        
        return readBuffer;
    }
    
    /**
     * Sets readbuffer value.
     * @param readbuffer - readbuffer to set.
     */
    public void setReadBuffer(int readbuffer)
    {
        this.readBuffer = readbuffer;
        saveReadBuffer();   
    }
    
    /**
     * Gets whether wrapping is on. 
     * @return whether wrapping is on.
     */
    public boolean getWrapping()
    {
        if (wrapping == null)
        {
            loadWrapping();
        }
        
        return wrapping;
    }
    
    
    /**
     * Sets wrap value.
     * @param wrapping - true if wrapping is on, false otherwise.
     */
    public void setWrapping(boolean wrapping)
    {
        this.wrapping = wrapping;
        saveWrapping();
        
        notifyWrappingChanged(wrapping);
    }
    
    /**
     * Gets backlog lines. 
     * @return
     */
    public int getBacklogLines()
    {
        if (backLogLines == null)
        {
            loadBacklogLines();
        }
        return backLogLines;
    }
    
    

    /**
     * Sets backlog lines.
     * @param lines - lines to set.
     */
    public void setBacklogLines(int lines)
    {
        backLogLines = lines;
        saveBacklogLines();
    }
    
    /**
     * Sets cursor line color.
     * @param rgb - RGB to set.
     */
    public void setCursorLineColor(RGB rgb)
    {
        PreferenceConverter.setValue(getPreferenceStore(), CURSORLINE_COLOR_KEY, rgb);
    }
    
    /**
     * Gets cursor line color.
     * @return cursor line color.
     */
    public RGB getCursorLineColor()
    {
        return PreferenceConverter.getColor(getPreferenceStore(), CURSORLINE_COLOR_KEY);
    }
    
    /**
     * Gets text color.
     * @return cursor line color.
     */
    public RGB getTextColorRGB()
    {
        RGB color = colorRegistry.getRGB(TEXT_COLOR_KEY);
        if (color == null)
        {
            RGB rgb = PreferenceConverter.getColor(getPreferenceStore(), TEXT_COLOR_KEY);
            colorRegistry.put(TEXT_COLOR_KEY, rgb);
            color = colorRegistry.getRGB(TEXT_COLOR_KEY);
        }
        
        return color;
    }
    
    /**
     * Gets text color.
     * @return cursor line color.
     */
    public Color getTextColor()
    {
        Color color = colorRegistry.get(TEXT_COLOR_KEY);
        if (color == null)
        {
            RGB rgb = PreferenceConverter.getColor(getPreferenceStore(), TEXT_COLOR_KEY);
            colorRegistry.put(TEXT_COLOR_KEY, rgb);
            color = colorRegistry.get(TEXT_COLOR_KEY);
        }
        
        return color;
    }
    
    /**
     * Sets text color.
     * @param rgb - RGB to set.
     */
    public void setTextColor(RGB rgb)
    {
        PreferenceConverter.setValue(getPreferenceStore(), TEXT_COLOR_KEY, rgb);
        colorRegistry.put(TEXT_COLOR_KEY, rgb);
        notifyForegroundColorChanged(colorRegistry.get(TEXT_COLOR_KEY));
    }
    
    /**
     * Gets font data. 
     * @return font data.
     */
    public FontData[] getFontData()
    {
        synchronized (fontRegistry)
        {
            loadMainFont();
            
            return fontRegistry.getFontData(MAIN_TEXT_FONT_KEY);
        }
    }
    
    /**
     * Gets font.
     * @return main font.
     */
    public Font getFont()
    {
        synchronized (fontRegistry)
        {
            loadMainFont();
               
            return fontRegistry.get(MAIN_TEXT_FONT_KEY);
        }
    }
    
    /**
     * Sets font data.
     * @param data - data to set.
     */
    public void setFontData(FontData[] data)
    {
        synchronized (fontRegistry)
        {
            PreferenceConverter.setValue(getPreferenceStore(), MAIN_TEXT_FONT_KEY, data);
            fontRegistry.put(MAIN_TEXT_FONT_KEY, data);
            notifyFontChanged(fontRegistry.get(MAIN_TEXT_FONT_KEY));
            mainFontLoaded = true;
        }
    }
    
    
    //////////////////////////
    /**
     * Gets whether "auto-bolding" on new data is on. 
     * @return whether "auto-bolding" on new data is on.
     */
    public boolean getDefaultAutoBolding()
    {
        IPreferenceStore store = getPreferenceStore();
        return store.getDefaultBoolean(AUTOBOLDING_KEY);
    }
    
    /**
     * Gets default backlog lines. 
     * @return default backlog lines.
     */
    public int getDefaultBacklogLines()
    {
        IPreferenceStore store = getPreferenceStore();
        return store.getDefaultInt(BACKLOG_LINES_KEY);
    }
    
    /**
     * Gets read timeout.
     * @return read timeout.
     */
    public int getDefaultReadTimeout()
    {
        IPreferenceStore store = getPreferenceStore();
        return store.getDefaultInt(READ_TIMEOUT_KEY);
    }
    
    /**
     * Gets read timeout.
     * @return read timeout.
     */
    public int getDefaultReadBuffer()
    {
        IPreferenceStore store = getPreferenceStore();
        return store.getDefaultInt(READ_BUFFER_KEY);
    }
    
    /**
     * Gets whether wrapping is on. 
     * @return whether wrapping is on.
     */
    public boolean getDefaultWrapping()
    {
        IPreferenceStore store = getPreferenceStore();
        return store.getDefaultBoolean(WRAPPING_KEY);
    }
    
    /**
     * Gets default cursor line color.
     * @return cursor line color.
     */
    public RGB getDefaultCursorLineColor()
    {
        return PreferenceConverter.getDefaultColor(getPreferenceStore(), CURSORLINE_COLOR_KEY);
    }
    
    /**
     * Gets default text color.
     * @return text color.
     */
    public RGB getDefaultTextColor()
    {
        return PreferenceConverter.getDefaultColor(getPreferenceStore(), TEXT_COLOR_KEY);
    }
    
    /**
     * Gets font data. 
     * @return font data.
     */
    public FontData[] getDefaultFont()
    {
        return PreferenceConverter.getDefaultFontDataArray(getPreferenceStore(), MAIN_TEXT_FONT_KEY);
    }
    
    /**
     * @return
     */
    public String getDefaultDefaultEncoding()
    {
        IPreferenceStore store = getPreferenceStore();
        return store.getDefaultString(DEFAULT_ENCODING_KEY);
    }
    //////////////////////////
    
    /**
     * Adds logging preference listener.
     * @param listener - listener to add. 
     */
    public void addPreferenceListener(ILoggingPreferenceListener listener)
    {
        pendingToAddListeners.add(listener);
    }
    
    /**
     * Removes logging preference listener.
     * @param listener - listener to remove.
     */
    public void removePreferenceListener(ILoggingPreferenceListener listener)
    {
        pendingToRemoveListeners.add(listener);
    }
    
    /**
     * @param defaultEncoding
     */
    public void setDefaultEncoding(String defaultEncoding)
    {
        this.defaultEncoding = defaultEncoding;
        saveDefaultEncoding();
    }

    /**
     * @return
     */
    public String getDefaultEncoding()
    {
        if (defaultEncoding == null)
        {
            loadDefaultEncoding();
        }
        
        return defaultEncoding;
    }
    
    /**
     * Creates language colorizer.
     */
    public static void createLanguageColorizer()
    {
        ColorizerReader reader = new ColorizerReader(TokenTypes.getTokenList());
        LanguageColorizer lc = reader.loadColorization(LoggingPreferences.COLORIZER_ID, true);
        if (lc != null)
        {
            LanguageRegistry.registerLanguageColorizer(TokenTypes.LANGUAGE, lc);
        }
        if (lc == null)
        {
            lc = new LanguageColorizer(TokenTypes.LANGUAGE);
            LanguageRegistry.registerLanguageColorizer(TokenTypes.LANGUAGE, lc);
            LanguageRegistry.setPreferenceId(TokenTypes.LANGUAGE, LoggingPreferences.COLORIZER_ID);
        }
    }
    
    /**
     * Gets maximum number of lines single regexp may match. 
     * @return max regexp lines.
     */
    public int getRegexpMaxLines()
    {
        return 1;
    }
    
    /**
     * Fills default coloring rules. 
     */
    public void initializeDefaultRules()
    {
        createLanguageColorizer();
        LoggingStructureProvider provider = new LoggingStructureProvider();
        
        fillDefaultRules(provider);
        
        provider.buildLanguageColorizer(
                LanguageRegistry.getLanguageColorizer(TokenTypes.LANGUAGE),
                LoggingPreferences.COLORIZER_ID);
        provider.applyChanges();
    }

    /**
     * Reads rules from preferences.
     * @return rules.
     */
    private List<Rule> loadRules()
    {
        IPreferenceStore store = getPreferenceStore();
        int length = store.getInt(LENGTH_KEY);
        
        List<Rule> result = new ArrayList<Rule>();
        
        for (int i = 0; i < length; i++)
        {
            String ruleName = store.getString(NAME_KEY + i);
            String ruleContent = store.getString(CONTENT_KEY + i);
            Boolean ruleIsRegexp = store.getBoolean(REGEXP_KEY + i);
            Boolean isCaseInsensitive = store.getBoolean(CASEINSENSITIVE_KEY + i);
            Rule rule = new Rule(ruleName, ruleContent, ruleIsRegexp, isCaseInsensitive);
            result.add(rule);
        }
        
        return result;
    }
    
    /**
     * Writes rules to preferences.
     * @param rules - rules to write.
     */
    private void saveRules(List<Rule> rules)
    {
        //clearing previous values
        clearList();
        
        //saving
        IPreferenceStore store = getPreferenceStore();
        
        store.setValue(LENGTH_KEY, rules.size());
        
        for (int i = 0; i < rules.size(); i++)
        {
            Rule rule = rules.get(i);
            store.setValue(NAME_KEY + i, rule.getName());
            store.setValue(CONTENT_KEY + i, rule.getRule());
            store.setValue(REGEXP_KEY + i, rule.isRegexp());
            store.setValue(CASEINSENSITIVE_KEY + i, rule.isCaseInsensitive());
        }
    }
    
    /**
     * Clears list in preferences.
     */
    private void clearList()
    {
        IPreferenceStore store = getPreferenceStore();
        int length = store.getDefaultInt(LENGTH_KEY);
        
        for (int i = 0; i < length; i++)
        {
            store.setValue(NAME_KEY + i, ""); //$NON-NLS-1$
            store.setValue(CONTENT_KEY + i, ""); //$NON-NLS-1$
            store.setValue(REGEXP_KEY + i, false);
            store.setValue(CASEINSENSITIVE_KEY + i, false);
        }
    }
    
    /**
     * Gets preference store.
     * @return preference store.
     */
    private IPreferenceStore getPreferenceStore()
    {
        return LoggingPlugin.getDefault().getPreferenceStore();
    }
    
    /**
     * Notifies listeners that rules are changed.
     */
    private void notifyRulesChanged()
    {
        for (ILoggingPreferenceListener listener : getListeners())
        {
            listener.rulesChanged();
        }
    }
    
    /**
     * Notifies listeners that wrapping mode is changed.
     * @param wrapping - new wrapping value.
     */
    private void notifyWrappingChanged(boolean wrapping)
    {
        for (ILoggingPreferenceListener listener : getListeners())
        {
            listener.wrappingChanged(wrapping);
        }
    }
    
    /**
     * Notifies listeners that font is changed.
     * @param font - new font value.
     */
    private void notifyFontChanged(Font font)
    {
        for (ILoggingPreferenceListener listener : getListeners())
        {
            listener.fontChanged(font);
        }
    }
    
    /**
     * Notifies listeners that text foreground color is changed.
     * @param color - new color value.
     */
    private void notifyForegroundColorChanged(Color color)
    {
        for (ILoggingPreferenceListener listener : getListeners())
        {
            listener.textForegroundColorChanged(color);
        }
    }
    
    /**
     * Gets current listeners.
     * @return current listeners.
     */
    private List<ILoggingPreferenceListener> getListeners()
    {
        listeners.addAll(pendingToAddListeners);
        listeners.removeAll(pendingToRemoveListeners);
        pendingToAddListeners.clear();
        pendingToRemoveListeners.clear();
        return listeners;
    }
    
    /**
     * Saves autobolding.
     */
    private void saveAutoBolding()
    {   
        if(autoBolding != null)
        {
            IPreferenceStore store = getPreferenceStore();
            store.setValue(AUTOBOLDING_KEY, autoBolding);
        }
    }
    
    /**
     * Loads autobolding.
     */
    private void loadAutoBolding()
    {
        IPreferenceStore store = getPreferenceStore();
        autoBolding = store.getBoolean(AUTOBOLDING_KEY);
    }
    
    /**
     * Saves read timeout.
     */
    private void saveReadTimeout()
    {   
        if(readTimeout != null)
        {
            IPreferenceStore store = getPreferenceStore();
            store.setValue(READ_TIMEOUT_KEY, readTimeout);
        }
    }
    
    /**
     * Loads read timeout.
     */
    private void loadReadTimeout()
    {
        IPreferenceStore store = getPreferenceStore();
        readTimeout = store.getInt(READ_TIMEOUT_KEY);
    }
    
   /**
    * Saves read buffer.
    */
   private void saveReadBuffer()
   {   
       if(readBuffer != null)
       {
           IPreferenceStore store = getPreferenceStore();
           store.setValue(READ_BUFFER_KEY, readBuffer);
       }
   }
   
   /**
    * Loads read buffer.
    */
   private void loadReadBuffer()
   {
       IPreferenceStore store = getPreferenceStore();
       readBuffer = store.getInt(READ_BUFFER_KEY);
   }
    
    /**
     * Saves wrapping.
     */
    private void saveWrapping()
    {   
        if(wrapping != null)
        {
            IPreferenceStore store = getPreferenceStore();
            store.setValue(WRAPPING_KEY, wrapping);
        }
    }
    
    /**
     * Loads wrapping.
     */
    private void loadWrapping()
    {
        IPreferenceStore store = getPreferenceStore();
        wrapping = store.getBoolean(WRAPPING_KEY);
    }
    
    /**
     * Saves default encoding.
     */
    private void saveDefaultEncoding()
    {
        IPreferenceStore store = getPreferenceStore();
        store.setValue(DEFAULT_ENCODING_KEY, defaultEncoding);
    }

    /**
     * Loads default encoding.
     */
    private void loadDefaultEncoding()
    {
        IPreferenceStore store = getPreferenceStore();
        defaultEncoding = store.getString(DEFAULT_ENCODING_KEY);
    }
    
    /**
     * Saves backlog lines.
     */
    private void saveBacklogLines()
    {
        IPreferenceStore store = getPreferenceStore();
        store.setValue(BACKLOG_LINES_KEY, backLogLines);
    }
    
    /**
     * Load backlog lines.
     */
    private void loadBacklogLines()
    {
        IPreferenceStore store = getPreferenceStore();
        backLogLines = store.getInt(BACKLOG_LINES_KEY);
    }

    /**
     * Fills default coloring rules.
     * @param provider - structure provider.
     */
    public void fillDefaultRules(LoggingStructureProvider provider)
    {        
        loadRulesFromExtensions(provider);
        
        //provider.addRule("Jaxer-TRACE", "^\d\d\:\d\d\:\d\d\ .{10}\ \[\s*\d+\]\[TRACE\]", true, true, new RGB(60,132,96), false, false, false);
        //provider.addRule("Jaxer-DEBUG", "^\d\d\:\d\d\:\d\d\ .{10}\ \[\s*\d+\]\[DEBUG\]", true, true, new RGB(0,0,255), false, false, false);
        //provider.addRule("Jaxer-INFO", "^\d\d\:\d\d\:\d\d\ .{10}\ \[\s*\d+\]\[INFO\]", true, true, new RGB(128,64,0), false, false, false);
        //provider.addRule("Jaxer-WARN", "^\\d\\d\\:\\d\\d\\:\\d\\d\\ .{10}\\ \\[\\s*\\d+\\]\\[WARNING\\]", true, true, new RGB(255,128,0), false, false, false);
        //provider.addRule("Jaxer-FATAL", "^\d\d\:\d\d\:\d\d\ .{10}\ \[\s*\d+\]\[FATAL\ ERROR\]", true, true, new RGB(255,0,0), true, false, false);
        //provider.addRule("Jaxer-ERROR", "^\d\d\:\d\d\:\d\d\ .{10}\ \[\s*\d+\]\[ERROR", true, true, new RGB(255, 0, 0), false, false, false);
        //provider.addRule("ApacheError-emerg", "^\\[(\\w|\\ |\\:)+\\]\\ \\[emerg\\]\\ ", true, true, new RGB(255, 0, 0), true, true, true);
        //provider.addRule("ApacheError-alert", "^\[(\w|\ |\:)+\]\ \[alert\]\ ", true, true, new RGB(255, 0, 0), true, true, false);
        //provider.addRule("ApacheError-crit", "^\[(\w|\ |\:)+\]\ \[crit\]\ ", true, true, new RGB(255, 0, 0), true, false, false);
        //provider.addRule("ApacheError-error", "^\[(\w|\ |\:)+\]\ \[error\]\ ", true, true, new RGB(255, 0, 0), false, false, false);
        //provider.addRule("ApacheError-warn", "^\[(\w|\ |\:)+\]\ \[warn\]\ ", true, true, new RGB(255, 128, 0), false, false, false);
        //provider.addRule("ApacheError-notice", "^\[(\w|\ |\:)+\]\ \[notice\]\ ", true, true, new RGB(128, 0, 255), false, false, false);
        //provider0.addRule("ApacheError-info", "^\[(\w|\ |\:)+\]\ \[info\]\ ", true, true, new RGB(128, 64, 64), false, false, false);
        //provider.addRule("ApacheError-debug", "^\[(\w|\ |\:)+\]\ \[debug\]\ ", true, true, new RGB(0, 0, 255), false, false, false);
        //provider.addRule("Default", ".*", true, true, new RGB(128,128,128), false, false, false);
    }

    /**
     * Loads rules from plug-in extensions.
     * @param provider - provider to contribute rules to.
     */
    private void loadRulesFromExtensions(LoggingStructureProvider provider)
    {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint ep = registry.getExtensionPoint(DEFAULT_RULES_POINT_ID);

        if (ep != null)
        {
            IExtension[] extensions = ep.getExtensions();

            for (int i = 0; i < extensions.length; i++)
            {
                IExtension extension = extensions[i];
                IConfigurationElement[] elements = extension.getConfigurationElements();

                for (int j = 0; j < elements.length; j++)
                {
                    IConfigurationElement element = elements[j];
                    String elementName = element.getName();

                    if (elementName.equals(RULE_ELEMENT))
                    {
                        String name = element.getAttribute(NAME_ATTRIBVUTE);
                        String content = element.getAttribute(CONTENT_ATTRIBUTE);
                        boolean isRegexp = getBooleanAttribute(element, ISREGEXP_ATTRIBUTE, true);
                        boolean isCaseSensitive = getBooleanAttribute(element, ISCASEINSENSITIVE_ATTRIBUTE, false); 
                        boolean isBold = getBooleanAttribute(element, BOLD_ATTRIBUTE, false);
                        boolean isItalic = getBooleanAttribute(element, ITALIC_ATTRIBUTE, false); 
                        boolean isUnderline = getBooleanAttribute(element, UNDERLINE_ATTRIBUTE, false); 
                        String colorString = element.getAttribute(COLOR_ATTRIBUTE);
                        RGB color = new RGB(0, 0, 0);
                        try 
                        {
                            color = getRGB(colorString);
                        }
                        catch (IllegalArgumentException ex)
                        {
                            IdeLog.logError(LoggingPlugin.getDefault(), Messages.getString("LoggingPreferences.ERR_WrongFormat") + colorString); //$NON-NLS-1$
                        }
                        
                        provider.addRule(name, content, isRegexp, isCaseSensitive, color, isBold, isItalic, isUnderline);
                    }
                }
            }
        }
    }
    
    /**
     * Gets RGB from string.
     * @param colorString - color string
     * @return RGB.
     * @throws IllegalArgumentException if string is not a legal RGB representation.
     */
    private RGB getRGB(String colorString) throws IllegalArgumentException
    {
        if (colorString == null) 
        {
            throw new IllegalArgumentException("Null is not a valid RGB"); //$NON-NLS-1$
        }
        StringTokenizer tokenizer = new StringTokenizer(colorString, ","); //$NON-NLS-1$

        try 
        {
            String red = tokenizer.nextToken().trim();
            String green = tokenizer.nextToken().trim();
            String blue = tokenizer.nextToken().trim();
            int rval = 0, gval = 0, bval = 0;
            
            rval = Integer.parseInt(red);
            gval = Integer.parseInt(green);
            bval = Integer.parseInt(blue);
            
            return new RGB(rval, gval, bval);
        } 
        catch (Throwable e) 
        {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Initializes default font.
     */
    private void initializeDefaultFont()
    {
        Font defaultFont = JFaceResources.getDefaultFont();
        
        FontData[] data = defaultFont.getFontData();
        
        String osName = System.getProperty("os.name", "Windows"); //$NON-NLS-1$ //$NON-NLS-2$
        
        int height = data[0].getHeight();
        if (data != null && data.length > 0)
        {
            if (osName.startsWith("Windows")) //$NON-NLS-1$
            {
                height = WINDOWS_DEFAULT_FONT_SIZE;
            }
            else if (osName.startsWith("Linux")) //$NON-NLS-1$
            {
                height = LINUX_DEFAULT_FONT_SIZE;
            }
            else if (osName.startsWith("Mac OS")) //$NON-NLS-1$
            {
                height = MAC_OS_DEFAULT_FONT_SIZE;
            }
        }
        data[0].setHeight(height);
        if (supports(DEFAULT_FONT_FAMILY, height))
        {
            data[0].setName(DEFAULT_FONT_FAMILY);
        }
        
        PreferenceConverter.setDefault(getPreferenceStore(), MAIN_TEXT_FONT_KEY, data);
    }
    
    /**
     * Cheks whether font is supported.
     * @param name - font name.
     * @param height - font size.
     * @return true if supports, false otherwise.
     */
    private boolean supports(String name, int height)
    {
        try
        {
            Font font = new Font(Display.getCurrent(), name, height, 0);
            font.dispose();
            return true;
        }
        catch(SWTError error)
        {
            return false;
        }
    }

    /**
     * Gets boolean attribute.
     * @param element - element.
     * @param name - attribute name.
     * @param defaultValue - default value to use.
     * @return attribute value.
     */
    private boolean getBooleanAttribute(IConfigurationElement element, String name, boolean defaultValue)
    {
        String attributeString = element.getAttribute(name);
        if (attributeString == null)
        {
            return defaultValue;
        }
        
        return Boolean.parseBoolean(attributeString);
    }
    
    /**
     * Loads main font.
     */
    private void loadMainFont()
    {
        if (!mainFontLoaded)
        {
            FontData[] data = PreferenceConverter.getFontDataArray(getPreferenceStore(),
                    MAIN_TEXT_FONT_KEY);
            fontRegistry.put(MAIN_TEXT_FONT_KEY, data);
            mainFontLoaded = true;
        }
    }
    
    /**
     * LoggingPreferences private constructor.
     */
    LoggingPreferences()
    {
    }    
}
