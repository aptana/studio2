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
package com.aptana.ide.parsing.eclipse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.LexerPlugin;
import com.aptana.ide.lexer.TokenList;
import com.aptana.ide.lexer.matcher.MatcherLexerBuilder;
import com.aptana.ide.parsing.IParser;

/**
 * Maintains registry of token lists and parsers by language MIME type
 * 
 * @author Kevin Sawicki (ksawicki@aptana.com)
 * @author Kevin Lindsey
 * @author Chris Williams
 */
public abstract class ParsingRegistry
{
	private static final String TOKEN_LIST_ID = "com.aptana.ide.editors.tokenList"; //$NON-NLS-1$
	private static final String PARSER_ID = "com.aptana.ide.editors.parser"; //$NON-NLS-1$
	private static final String SCANNER_ID = "com.aptana.ide.editors.scanner"; //$NON-NLS-1$

	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
	private static final String ATTR_LANGUAGE = "language"; //$NON-NLS-1$	
	private static final String ATTR_RESOURCE = "resource"; //$NON-NLS-1$
	private static final String ATTR_EXTENSION = "extension"; //$NON-NLS-1$

	private static final String TAG_PARSER = "parser"; //$NON-NLS-1$
	private static final String TAG_SCANNER = "scanner"; //$NON-NLS-1$
	private static final String TAG_TOKEN_LIST = "tokenList"; //$NON-NLS-1$
	private static final String TAG_TEXT_MATCHER = "textMatcher"; //$NON-NLS-1$

	private static Map<String, InstanceCreator> languageTokenLists = new HashMap<String, InstanceCreator>();
	private static Map<String, InstanceCreator> extensionTokenLists = new HashMap<String, InstanceCreator>();
	private static Map<String, InstanceCreator> languageParsers = new HashMap<String, InstanceCreator>();
	private static Map<String, InstanceCreator> languageScanners = new HashMap<String, InstanceCreator>();

	/**
	 * static constructor
	 */
	static
	{
		loadAll();
	}

	public static void clearAll()
	{
		languageTokenLists.clear();
		extensionTokenLists.clear();
	}

	public static void loadAll()
	{
		loadTokenLists();
		loadParsers();
		loadScanners();
	}

	public static void reloadAll()
	{
		clearAll();
		loadAll();
	}

	/**
	 * Creates the parser for a language mime type
	 * 
	 * @param language
	 *            - language mime type
	 * @return ITokenList
	 */
	public static IParser getParser(String language)
	{
		if (!languageParsers.containsKey(language))
		{
			return null;
		}
		InstanceCreator creator = languageParsers.get(language);
		return (IParser) creator.getCachedInstance();
	}

	/**
	 * Creates the parser for a language mime type
	 * 
	 * @param language
	 *            - language mime type
	 * @return ITokenList
	 */
	public static IParser getScanner(String language)
	{
		if (!languageScanners.containsKey(language))
			return null;

		InstanceCreator creator = languageScanners.get(language);
		return (IParser) creator.getCachedInstance();
	}

	/**
	 * Creates the token list for a language mime type
	 * 
	 * @param language
	 *            - language mime type
	 * @return ITokenList
	 */
	public static TokenList getTokenList(String language)
	{
		if (!languageTokenLists.containsKey(language))
			return null;

		InstanceCreator creator = languageTokenLists.get(language);
		return (TokenList) creator.getCachedInstance();
	}

	/**
	 * getTokenListByExtension
	 * 
	 * @param extension
	 * @return - token list
	 */
	public static TokenList getTokenListByExtension(String extension)
	{
		if (extension == null || extension.length() <= 0)
			return null;

		if (extension.startsWith(".")) //$NON-NLS-1$
		{
			extension = extension.substring(1);
		}

		if (!extensionTokenLists.containsKey(extension))
			return null;

		InstanceCreator creator = extensionTokenLists.get(extension);
		return (TokenList) creator.getCachedInstance();
	}

	/**
	 * createParse
	 * 
	 * @param language
	 * @return IParser
	 */
	public static IParser createParser(String language)
	{
		if (!languageParsers.containsKey(language))
			return null;

		InstanceCreator creator = languageParsers.get(language);
		return (IParser) creator.createInstance();
	}

	/**
	 * createScanner
	 * 
	 * @param language
	 * @return IParser
	 */
	public static IParser createScanner(String language)
	{
		if (!languageScanners.containsKey(language))
			return null;

		InstanceCreator creator = languageScanners.get(language);
		return (IParser) creator.createInstance();
	}

	/**
	 * createTokenList
	 * 
	 * @param input
	 * @return TokenList or null
	 * @throws LexerException
	 */
	public static TokenList createTokenList(InputStream input) throws LexerException
	{
		if (input == null)
			return null;

		// create lexer builder
		MatcherLexerBuilder builder = new MatcherLexerBuilder();

		// read input stream
		builder.loadXML(input);

		// finalize lexer
		ILexer lexer = builder.buildLexer();

		// get a list of languages
		String[] languages = lexer.getLanguages();

		// grab the first (and only) language token list
		if (languages != null && languages.length > 0)
		{
			return (TokenList) lexer.getTokenList(languages[0]);
		}
		return null;
	}

	/**
	 * createTokenList
	 * 
	 * @param language
	 * @return TokenList
	 */
	public static TokenList createTokenList(String language)
	{
		if (!languageTokenLists.containsKey(language))
			return null;

		InstanceCreator creator = languageTokenLists.get(language);
		return (TokenList) creator.createInstance();
	}

	/**
	 * hasParser
	 * 
	 * @param language
	 * @return boolean
	 */
	public static boolean hasParser(String language)
	{
		return languageParsers.containsKey(language);
	}

	/**
	 * hasScanner
	 * 
	 * @param language
	 * @return boolean
	 */
	public static boolean hasScanner(String language)
	{
		return languageScanners.containsKey(language);
	}

	/**
	 * hasTokenList
	 * 
	 * @param language
	 * @return boolean
	 */
	public static boolean hasTokenList(String language)
	{
		return languageTokenLists.containsKey(language);
	}

	/**
	 * loadTokenLists
	 */
	private static void loadTokenLists()
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint ep = registry.getExtensionPoint(TOKEN_LIST_ID);

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
					InstanceCreator instance = null;
					String language = null;
					String fileExtension = null;

					if (!isActivityEnabled(element))
					{
						// Do nothing because this extension should not work.
						continue;
					}
					else if (elementName.equals(TAG_TOKEN_LIST) && LexerPlugin.useCodeBasedLexer())
					{
						String tokenListClass = element.getAttribute(ATTR_CLASS);

						language = element.getAttribute(ATTR_LANGUAGE);
						fileExtension = element.getAttribute(ATTR_EXTENSION);

						if (fileExtension != null && fileExtension.startsWith(".")) //$NON-NLS-1$
						{
							fileExtension = fileExtension.substring(1);
						}

						if (tokenListClass != null && language != null && language.length() > 0)
						{
							instance = new InstanceCreator(element, ATTR_CLASS);
						}
					}
					else if (elementName.equals(TAG_TEXT_MATCHER) && LexerPlugin.useMatcherLexer())
					{
						String resourceName = element.getAttribute(ATTR_RESOURCE);

						language = element.getAttribute(ATTR_LANGUAGE);
						fileExtension = element.getAttribute(ATTR_EXTENSION);

						if (fileExtension != null && fileExtension.startsWith(".")) //$NON-NLS-1$
						{
							fileExtension = fileExtension.substring(1);
						}

						if (resourceName != null && language != null && language.length() > 0)
						{
							instance = new InstanceCreator(element, resourceName)
							{
								public Object createInstance()
								{
									IExtension ext = element.getDeclaringExtension();
									String pluginId = ext.getNamespaceIdentifier();
									Bundle bundle = Platform.getBundle(pluginId);
									URL resource = bundle.getResource(this.attributeName);
									if (resource == null)
									{
										resource = bundle.getEntry(this.attributeName);
									}
									try
									{
										InputStream input = resource.openStream();
										return createTokenList(input);
									}
									catch (IOException e)
									{
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									catch (LexerException e)
									{
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									return null;
								}
							};
						}
					}

					if (instance != null)
					{
						languageTokenLists.put(language, instance);
						if (fileExtension != null)
						{
							extensionTokenLists.put(fileExtension, instance);
						}
					}
				}
			}
		}
	}

	private static boolean isActivityEnabled(IConfigurationElement element)
	{
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * loadParsers
	 */
	private static void loadParsers()
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint ep = registry.getExtensionPoint(PARSER_ID);

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
					else if (elementName.equals(TAG_PARSER))
					{
						String parserClass = element.getAttribute(ATTR_CLASS);
						String language = element.getAttribute(ATTR_LANGUAGE);

						if (parserClass != null && language != null && language.length() > 0)
						{
							InstanceCreator creator = new InstanceCreator(element, ATTR_CLASS);

							languageParsers.put(language, creator);
						}
					}
				}
			}
		}
	}

	/**
	 * loadScanners
	 */
	private static void loadScanners()
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint ep = registry.getExtensionPoint(SCANNER_ID);

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
					else if (elementName.equals(TAG_SCANNER))
					{
						String parserClass = element.getAttribute(ATTR_CLASS);
						String language = element.getAttribute(ATTR_LANGUAGE);

						if (parserClass != null && language != null && language.length() > 0)
						{
							InstanceCreator creator = new InstanceCreator(element, ATTR_CLASS);

							languageScanners.put(language, creator);
						}
					}
				}
			}
		}
	}

	/**
	 * registerParser
	 * 
	 * @param language
	 * @param parser
	 */
	public static void registerParser(String language, IParser parser)
	{
		if (languageParsers.containsKey(language) == false)
		{
			InstanceCreator creator = new InstanceCreator(null, null);
			creator.setCache(parser);
			languageParsers.put(language, creator);
		}
		else
		{
			InstanceCreator creator = languageParsers.get(language);
			creator.setCache(parser);
		}
	}

	/**
	 * registerScanner
	 * 
	 * @param language
	 * @param parser
	 */
	public static void registerScanner(String language, IParser parser)
	{
		if (languageScanners.containsKey(language) == false)
		{
			InstanceCreator creator = new InstanceCreator(null, null);
			creator.setCache(parser);
			languageScanners.put(language, creator);
		}
		else
		{
			InstanceCreator creator = languageScanners.get(language);
			creator.setCache(parser);
		}
	}

	/**
	 * registerTokenList
	 * 
	 * @param tokenList
	 */
	public static void registerTokenList(TokenList tokenList)
	{
		registerTokenList(tokenList, null);
	}

	/**
	 * registerTokenList
	 * 
	 * @param tokenList
	 * @param fileExtension
	 */
	public static void registerTokenList(TokenList tokenList, String fileExtension)
	{
		String language = tokenList.getLanguage();

		if (languageTokenLists.containsKey(language) == false)
		{
			InstanceCreator creator = new InstanceCreator(null, null);

			creator.setCache(tokenList);

			languageTokenLists.put(language, creator);
		}
		else
		{
			InstanceCreator creator = languageTokenLists.get(language);

			creator.setCache(tokenList);
		}

		if (fileExtension != null && fileExtension.length() > 0)
		{
			if (fileExtension.startsWith(".")) //$NON-NLS-1$
			{
				fileExtension = fileExtension.substring(1);
			}

			if (extensionTokenLists.containsKey(fileExtension) == false)
			{
				InstanceCreator creator = new InstanceCreator(null, null);

				creator.setCache(tokenList);

				extensionTokenLists.put(fileExtension, creator);
			}
			else
			{
				InstanceCreator creator = extensionTokenLists.get(fileExtension);
				creator.setCache(tokenList);
			}
		}
	}

	/**
	 * Unregisters a language parser
	 * 
	 * @param language
	 */
	public static void unregisterParser(String language)
	{
		languageParsers.remove(language);
	}

	/**
	 * Unregisters a language scanner
	 * 
	 * @param language
	 */
	public static void unregisterScanner(String language)
	{
		languageScanners.remove(language);
	}

	/**
	 * Clears the cached token list if it exists in the registry
	 * 
	 * @param language
	 */
	public static void clearTokenList(String language)
	{
		if (languageTokenLists.containsKey(language))
		{
			InstanceCreator creator = languageTokenLists.get(language);
			creator.setCache(null);
		}
	}

	/**
	 * unregisterTokenList
	 * 
	 * @param tokenList
	 */
	public static void unregisterTokenList(TokenList tokenList)
	{
		languageTokenLists.remove(tokenList.getLanguage());
	}
}
