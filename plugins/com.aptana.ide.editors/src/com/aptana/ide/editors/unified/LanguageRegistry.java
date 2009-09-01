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

import java.io.File;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.IActivityManager;
import org.eclipse.ui.activities.IIdentifier;
import org.eclipse.ui.activities.IIdentifierListener;
import org.eclipse.ui.activities.IdentifierEvent;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.editors.unified.colorizer.ColorizerReader;
import com.aptana.ide.editors.unified.colorizer.LanguageColorizer;
import com.aptana.ide.editors.unified.colorizer.Messages;
import com.aptana.ide.lexer.TokenList;
import com.aptana.ide.parsing.IParser;
import com.aptana.ide.parsing.eclipse.ParsingRegistry;

/**
 * Maintains registry of token lists, colorizers, pair finders, and parsers by language MIME type
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 * @author Kevin Lindsey
 */
public final class LanguageRegistry
{
	private static final String PAIRFINDER_ID = "com.aptana.ide.editors.pairfinder"; //$NON-NLS-1$
	private static final String FORMATTER_ID = "com.aptana.ide.editors.formatter"; //$NON-NLS-1$

	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
	private static final String ATTR_LANGUAGE = "language"; //$NON-NLS-1$

	private static final String TAG_PAIR_FINDER = "pairFinder"; //$NON-NLS-1$
	private static final String TAG_FORMATTER = "formatter"; //$NON-NLS-1$

	private static Map<String, LanguageColorizer> languageColorizers = new HashMap<String, LanguageColorizer>();
	private static Map<String, InstanceCreator> pairFinders = new HashMap<String, InstanceCreator>();
	private static Map<String, InstanceCreator> formatters = new HashMap<String, InstanceCreator>();
	private static ColorizerReader colorizerReader;
	private static final IActivityManager activityManager = PlatformUI.getWorkbench().getActivitySupport()
			.getActivityManager();

	private static IIdentifierListener identifierListener = new IIdentifierListener()
	{
		public void identifierChanged(IdentifierEvent identifierEvent)
		{
			if (identifierEvent.hasEnabledChanged())
			{
				IIdentifier identifier = identifierEvent.getIdentifier();
				if (identifier.isEnabled())
				{
					LanguageRegistry.reloadAll();
					// identifier.removeIdentifierListener(this);
				}
			}
		}
	};
	/**
	 * static constructor
	 */
	static
	{
		loadAll();
	}

	/**
	 * LanguageRegistry
	 */
	private LanguageRegistry()
	{
		// Does nothing
	}

	public static void clearAll()
	{
		languageColorizers.clear();
		pairFinders.clear();
		formatters.clear();
	}

	public static void loadAll()
	{
		loadColorizations();
		loadPairFinders();
		loadCodeFormatters();
	}

	public static void reloadAll()
	{
		clearAll();
		loadAll();
	}

	/**
	 * Get the colorizer for the specified language
	 * 
	 * @param language
	 * @return LanguageColorizer or null
	 */
	public static LanguageColorizer getLanguageColorizer(String language)
	{
		LanguageColorizer original = null;

		if (languageColorizers.containsKey(language))
		{
			original = languageColorizers.get(language);
		}
		else if (colorizerReader.getInstanceCreator(language) != null)
		{
			InstanceCreator creator = colorizerReader.getInstanceCreator(language);

			// This method call will also register the colorizer which is done
			// in ColorizerReader
			original = (LanguageColorizer) creator.getCachedInstance();
		}
		else
		{
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), StringUtils.format(
					Messages.LanguageRegistry_NO_COLORIZER, language));
		}

		return original;
	}

	/**
	 * Get the colorizer for the specified language
	 * 
	 * @param language
	 * @param reader
	 *            - reader to use if needed.
	 * @return LanguageColorizer or null
	 */
	public static LanguageColorizer getLanguageColorizer(String language, ColorizerReader reader)
	{
		LanguageColorizer original = null;

		if (languageColorizers.containsKey(language))
		{
			original = languageColorizers.get(language);
		}
		else if (reader.getInstanceCreator(language) != null)
		{
			InstanceCreator creator = reader.getInstanceCreator(language);

			// This method call will also register the colorizer which is done
			// in ColorizerReader
			original = (LanguageColorizer) creator.getCachedInstance();
		}
		else
		{
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), StringUtils.format(
					Messages.LanguageRegistry_NO_COLORIZER, language));
		}

		return original;
	}

	/**
	 * Gets a list of all the registered language colorizers
	 * 
	 * @return - list of LanguageColorzier objects
	 */
	public static List<LanguageColorizer> getLanguageColorizers()
	{
		return new ArrayList<LanguageColorizer>(languageColorizers.values());
	}

	/**
	 * @param language
	 * @return preference id for this language type
	 */
	public static String getPreferenceId(String language)
	{
		return colorizerReader.getPreferenceId(language);
	}

	/**
	 * Creates the parser for a language mime type
	 * 
	 * @param language
	 *            - language mime type
	 * @return ITokenList
	 * @deprecated use {@link ParsingRegistry#getParser(String)}
	 */
	public static IParser getParser(String language)
	{
		return ParsingRegistry.getParser(language);
	}

	/**
	 * Creates the parser for a language mime type
	 * 
	 * @param language
	 *            - language mime type
	 * @return ITokenList
	 * @deprecated use {@link ParsingRegistry#getScanner(String)}
	 */
	public static IParser getScanner(String language)
	{
		return ParsingRegistry.getScanner(language);
	}

	/**
	 * Creates the pair finder for a language mime type
	 * 
	 * @param language
	 *            - language mime type
	 * @return IPairFinder
	 */
	public static IPairFinder getPairFinder(String language)
	{
		IPairFinder result = null;

		if (pairFinders.containsKey(language))
		{
			InstanceCreator creator = pairFinders.get(language);
			result = (IPairFinder) creator.getCachedInstance();
		}
		else
		{
			String message = MessageFormat.format(Messages.LanguageRegistry_No_Associated_Finder,
					new Object[] { language });
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), message);
		}

		return result;
	}

	/**
	 * Creates the Code Formatter for a language mime type
	 * 
	 * @param language
	 *            - language mime type
	 * @return ICodeFormatter
	 */
	public static ICodeFormatter getCodeFormatter(String language)
	{
		ICodeFormatter result = null;

		if (formatters.containsKey(language))
		{
			InstanceCreator creator = formatters.get(language);
			result = (ICodeFormatter) creator.getCachedInstance();
		}
		else
		{
			String message = MessageFormat.format(Messages.LanguageRegistry_No_Associated_Formatter,
					new Object[] { language });
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), message);
		}

		return result;
	}

	/**
	 * Creates the token list for a language mime type
	 * 
	 * @param language
	 *            - language mime type
	 * @return ITokenList
	 * @deprecated use {@link ParsingRegistry#getTokenList(String)}
	 */
	public static TokenList getTokenList(String language)
	{
		return ParsingRegistry.getTokenList(language);
	}

	/**
	 * getTokenListByExtension
	 * 
	 * @param extension
	 * @return - token list
	 * @deprecated use {@link ParsingRegistry#getTokenListByExtension(String)}
	 */
	public static TokenList getTokenListByExtension(String extension)
	{
		return ParsingRegistry.getTokenListByExtension(extension);
	}

	/**
	 * createParse
	 * 
	 * @param language
	 * @return IParser
	 * @deprecated use {@link ParsingRegistry#createParser(String)}
	 */
	public static IParser createParser(String language)
	{
		return ParsingRegistry.createParser(language);
	}

	/**
	 * createScanner
	 * 
	 * @param language
	 * @return IParser
	 * @deprecated use {@link ParsingRegistry#createScanner(String)}
	 */
	public static IParser createScanner(String language)
	{
		return ParsingRegistry.createScanner(language);
	}

	/**
	 * createTokenList
	 * 
	 * @param input
	 * @return TokenList or null
	 * @deprecated use {@link ParsingRegistry#createTokenList(InputStream)}
	 */
	public static TokenList createTokenList(InputStream input)
	{
		try
		{
			return ParsingRegistry.createTokenList(input);

		}
		catch (Exception e)
		{
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.LanguageRegistry_Cannot_Create_Token_List, e);
		}
		return null;
	}

	/**
	 * createTokenList
	 * 
	 * @param language
	 * @return TokenList
	 * @deprecated use {@link ParsingRegistry#createTokenList(String)}
	 */
	public static TokenList createTokenList(String language)
	{
		return ParsingRegistry.createTokenList(language);
	}

	/**
	 * Checks if a colorizer exists for a language
	 * 
	 * @param language
	 * @return - true if colorizer exists for the language
	 */
	public static boolean hasLanguageColorizer(String language)
	{
		return languageColorizers.containsKey(language);
	}

	/**
	 * hasParser
	 * 
	 * @param language
	 * @return boolean
	 * @deprecated use {@link ParsingRegistry#hasParser(String)}
	 */
	public static boolean hasParser(String language)
	{
		return ParsingRegistry.hasParser(language);
	}

	/**
	 * hasScanner
	 * 
	 * @param language
	 * @return boolean
	 * @deprecated use {@link ParsingRegistry#hasScanner(String)}
	 */
	public static boolean hasScanner(String language)
	{
		return ParsingRegistry.hasScanner(language);
	}

	/**
	 * hasTokenList
	 * 
	 * @param language
	 * @return boolean
	 * @deprecated use {@link ParsingRegistry#hasTokenList(String)}
	 */
	public static boolean hasTokenList(String language)
	{
		return ParsingRegistry.hasTokenList(language);
	}

	/**
	 * Imports a colorization
	 * 
	 * @param file
	 *            - colorization file
	 * @param language
	 *            - mime type
	 */
	public static void importColorization(File file, String language)
	{
		colorizerReader.importColorization(file, language);
	}

	/**
	 * loadColorizations
	 */
	private static void loadColorizations()
	{
		colorizerReader = new ColorizerReader();
		colorizerReader.loadExtensionPointColorizers();
	}

	public static final boolean isActivityEnabled(IConfigurationElement element)
	{

		String extensionId = element.getAttribute("id"); //$NON-NLS-1$
		String extensionPluginId = element.getNamespaceIdentifier();
		String extensionString = null;
		if (extensionPluginId != null && extensionId != null && extensionPluginId.length() > 0
				&& extensionId.length() > 0)
		{
			extensionString = extensionPluginId + "/" + extensionId; //$NON-NLS-1$
		}
		else if (extensionPluginId != null && extensionPluginId.length() > 0)
		{
			extensionString = extensionPluginId + "/.*"; //$NON-NLS-1$
		}

		if (extensionString != null)
		{
			final IIdentifier id = activityManager.getIdentifier(extensionString);
			if (id != null)
			{
				boolean enabled = id.isEnabled();
				if (!id.isEnabled())
				{
					id.addIdentifierListener(identifierListener);
				}
				return enabled;
			}
		}
		return true;
	}

	/**
	 * loadPairFinders
	 */
	private static void loadPairFinders()
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint ep = registry.getExtensionPoint(PAIRFINDER_ID);
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

					if (!isActivityEnabled(element))
					{
						// Do nothing because this extension should not work.
						continue;
					}
					else if (elementName.equals(TAG_PAIR_FINDER))
					{
						String finderClass = element.getAttribute(ATTR_CLASS);
						String language = element.getAttribute(ATTR_LANGUAGE);

						// IdeLog.logInfo(UnifiedEditorsPlugin.getDefault(),
						// StringUtils.format("Attempting to register
						// parser with class name {0} and language {1}", new
						// String[] {parserClass, language}));

						if (finderClass != null && language != null && language.length() > 0)
						{
							InstanceCreator creator = new InstanceCreator(element, ATTR_CLASS);
							// IdeLog.logInfo(UnifiedEditorsPlugin.getDefault(),
							// StringUtils.format("Registered parser
							// for MIME type {0} in LanguageRegistry",
							// language));
							pairFinders.put(language, creator);
						}
					}
				}
			}
		}
	}

	/**
	 * loadCodeFormatters
	 */
	private static void loadCodeFormatters()
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint ep = registry.getExtensionPoint(FORMATTER_ID);
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

					if (!isActivityEnabled(element))
					{
						// DO NOTHING
						continue;
					}
					else if (elementName.equals(TAG_FORMATTER))
					{
						String finderClass = element.getAttribute(ATTR_CLASS);
						String language = element.getAttribute(ATTR_LANGUAGE);

						// IdeLog.logInfo(UnifiedEditorsPlugin.getDefault(),
						// StringUtils.format("Attempting to register
						// parser with class name {0} and language {1}", new
						// String[] {parserClass, language}));

						if (finderClass != null && language != null && language.length() > 0)
						{
							InstanceCreator creator = new InstanceCreator(element, ATTR_CLASS);
							// IdeLog.logInfo(UnifiedEditorsPlugin.getDefault(),
							// StringUtils.format("Registered parser
							// for MIME type {0} in LanguageRegistry",
							// language));
							formatters.put(language, creator);
						}
					}
				}
			}
		}
	}

	/**
	 * Registers a language colorizer.
	 * 
	 * @param language
	 * @param colorizer
	 */
	public static void registerLanguageColorizer(String language, LanguageColorizer colorizer)
	{
		if (!languageColorizers.containsKey(language))
		{
			languageColorizers.put(language, colorizer);
		}
		else
		{
			LanguageColorizer curr = languageColorizers.get(language);

			curr.setCategoryColorizers(colorizer);
			curr.setTokenColorizers(colorizer);
			curr.setBackground(colorizer.getBackground());
			curr.setCaretColor(colorizer.getCaretColor());
			curr.setLineHighlightColor(colorizer.getLineHighlightColor());
			curr.setSelectionBackground(colorizer.getSelectionBackground());
			curr.setSelectionForeground(colorizer.getSelectionForeground());
			curr.setFoldingBg(colorizer.getFoldingBg());
			curr.setFoldingFg(colorizer.getFoldingFg());
		}
	}

	/**
	 * registerParser
	 * 
	 * @param language
	 * @param parser
	 * @deprecated use {@link ParsingRegistry#registerParser(String, IParser)}
	 */
	public static void registerParser(String language, IParser parser)
	{
		ParsingRegistry.registerParser(language, parser);
	}

	/**
	 * registerScanner
	 * 
	 * @param language
	 * @param parser
	 * @deprecated use {@link ParsingRegistry#registerScanner(String, IParser)}
	 */
	public static void registerScanner(String language, IParser parser)
	{
		ParsingRegistry.registerScanner(language, parser);
	}

	/**
	 * registerTokenList
	 * 
	 * @param tokenList
	 * @deprecated use {@link ParsingRegistry#registerScanner(TokenList)}
	 */
	public static void registerTokenList(TokenList tokenList)
	{
		ParsingRegistry.registerTokenList(tokenList);
	}

	/**
	 * registerTokenList
	 * 
	 * @param tokenList
	 * @param fileExtension
	 * @deprecated use {@link ParsingRegistry#registerTokenList(TokenList, String)}
	 */
	public static void registerTokenList(TokenList tokenList, String fileExtension)
	{
		ParsingRegistry.registerTokenList(tokenList, fileExtension);
	}

	/**
	 * Restores a colorizer to its defaults
	 * 
	 * @param language
	 */
	public static void restoreDefaultColorization(String language)
	{
		colorizerReader.restoreDefault(language);
	}

	/**
	 * Sets the pref id
	 * 
	 * @param language
	 * @param preferenceId
	 */
	public static void setPreferenceId(String language, String preferenceId)
	{
		colorizerReader.setPreferenceId(language, preferenceId);
	}

	/**
	 * Unregisters a language colorizer
	 * 
	 * @param language
	 */
	public static void unregisterLanguageColorizer(String language)
	{
		LanguageColorizer curr = languageColorizers.remove(language);

		if (curr != null)
		{
			LanguageColorizer colorizer = new LanguageColorizer(curr.getLanguage());
			curr.setCategoryColorizers(colorizer);
			curr.setTokenColorizers(colorizer);
			curr.setBackground(colorizer.getBackground());
			curr.setCaretColor(colorizer.getCaretColor());
			curr.setLineHighlightColor(colorizer.getLineHighlightColor());
			curr.setSelectionBackground(colorizer.getSelectionBackground());
			curr.setSelectionForeground(colorizer.getSelectionForeground());
		}
	}

	/**
	 * Unregisters a language parser
	 * 
	 * @param language
	 * @deprecated use {@link ParsingRegistry#unregisterParser(String)}
	 */
	public static void unregisterParser(String language)
	{
		ParsingRegistry.unregisterParser(language);
	}

	/**
	 * Unregisters a language scanner
	 * 
	 * @param language
	 * @deprecated use {@link ParsingRegistry#unregisterScanner(String)}
	 */
	public static void unregisterScanner(String language)
	{
		ParsingRegistry.unregisterScanner(language);
	}

	/**
	 * Clears the cached token list if it exists in the registry
	 * 
	 * @param language
	 * @deprecated use {@link ParsingRegistry#clearTokenList(String)}
	 */
	public static void clearTokenList(String language)
	{
		ParsingRegistry.clearTokenList(language);
	}

	/**
	 * unregisterTokenList
	 * 
	 * @param tokenList
	 * @deprecated use {@link ParsingRegistry#unregisterTokenList(TokenList)}
	 */
	public static void unregisterTokenList(TokenList tokenList)
	{
		ParsingRegistry.unregisterTokenList(tokenList);
	}
}
