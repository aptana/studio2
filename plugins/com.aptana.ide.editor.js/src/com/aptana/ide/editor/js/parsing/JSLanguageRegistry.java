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
package com.aptana.ide.editor.js.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.parsing.IParser;

/**
 * @author Kevin Lindsey
 */
public class JSLanguageRegistry
{
	private static String TAG_PARSER = "parser"; //$NON-NLS-1$
	private static String TAG_SCANNER = "scanner"; //$NON-NLS-1$
	private static String TAG_PI_LANGUAGE = "pi-language"; //$NON-NLS-1$
	private static String TAG_SINGLE_LINE_COMMENT_LANGUAGE = "single-line-comment-language"; //$NON-NLS-1$
	private static String TAG_MULTI_LINE_COMMENT_LANGUAGE = "multi-line-comment-language"; //$NON-NLS-1$
	private static String TAG_DOCUMENTATION_COMMENT_LANGUAGE = "documentation-comment-language"; //$NON-NLS-1$

	// parser attribute
	private static String ATTR_LANGUAGE = "language"; //$NON-NLS-1$
	//private static String ATTR_PARSER = "parser"; //$NON-NLS-1$

	// pi-language attribute
	private static String ATTR_PI_NAME = "pi-name"; //$NON-NLS-1$

	private static String SINGLE_LINE_COMMENT_KEY = "//"; //$NON-NLS-1$
	private static String MULTI_LINE_COMMENT_KEY = "/*"; //$NON-NLS-1$
	private static String DOCUMENTATION_COMMENT_KEY = "/**"; //$NON-NLS-1$

	private Map<String, IParser> _parserByMimeType;
	private Map<String, IParser> _scannerByMimeType;

	/**
	 * ElementRegistry
	 */
	public JSLanguageRegistry()
	{
		this._parserByMimeType = new HashMap<String, IParser>();
		this._scannerByMimeType = new HashMap<String, IParser>();
	}

	/**
	 * getDocumentationCommentParser
	 * 
	 * @return IParser
	 */
	public IParser getDocumentationCommentParser()
	{
		return this.getParser(DOCUMENTATION_COMMENT_KEY);
	}

	/**
	 * getDocumentationCommentScanner
	 * 
	 * @return IParser
	 */
	public IParser getDocumentationCommentScanner()
	{
		return this.getScanner(DOCUMENTATION_COMMENT_KEY);
	}

	/**
	 * getMultiLineCommentParser
	 * 
	 * @return IParser
	 */
	public IParser getMultiLineCommentParser()
	{
		return this.getParser(MULTI_LINE_COMMENT_KEY);
	}
	
	/**
	 * getMultiLineCommentScanner
	 * 
	 * @return IParser
	 */
	public IParser getMultiLineCommentScanner()
	{
		return this.getScanner(MULTI_LINE_COMMENT_KEY);
	}

	/**
	 * getParser
	 * 
	 * @param key
	 * @return IParser
	 */
	public IParser getParser(String key)
	{
		IParser result = null;

		if (this._parserByMimeType.containsKey(key))
		{
			result = this._parserByMimeType.get(key);
		}

		return result;
	}
	
	/**
	 * getPILanguage
	 * 
	 * @param processInstructionName
	 * @return IParser
	 */
	public IParser getProcessingInstructionLanguage(String processInstructionName)
	{
		return this.getParser(processInstructionName);
	}

	/**
	 * getProcessingInstructionScanner
	 * 
	 * @param processInstructionName
	 * @return IParser
	 */
	public IParser getProcessingInstructionScanner(String processInstructionName)
	{
		return this.getScanner(processInstructionName);
	}
	
	/**
	 * getScanner
	 * 
	 * @param key
	 * @return IParser
	 */
	public IParser getScanner(String key)
	{
		IParser result = null;
		
		if (this._scannerByMimeType.containsKey(key))
		{
			result = this._scannerByMimeType.get(key);
		}
		
		return result;
	}

	/**
	 * getSingleLineCommentParser
	 * 
	 * @return IParser
	 */
	public IParser getSingleLineCommentParser()
	{
		return this.getParser(SINGLE_LINE_COMMENT_KEY);
	}
	
	/**
	 * getSingleLineCommentScanner
	 * 
	 * @return IParser
	 */
	public IParser getSingleLineCommentScanner()
	{
		return this.getScanner(SINGLE_LINE_COMMENT_KEY);
	}

	/**
	 * loadFromExtension
	 * 
	 * @param extension
	 * @return IParser[]
	 */
	public IParser[] loadParsersFromExtension(IExtension extension)
	{
		IConfigurationElement[] elements = extension.getConfigurationElements();
		List<IParser> parsers = new ArrayList<IParser>();

		for (IConfigurationElement element : elements)
		{
			if (element.getName().equals(TAG_PARSER))
			{
				processElement(parsers, element, false);
			}
		}

		return parsers.toArray(new IParser[parsers.size()]);
	}
	
	/**
	 * loadScannersFromExtension
	 * 
	 * @param extension
	 * @return IParser[]
	 */
	public IParser[] loadScannersFromExtension(IExtension extension)
	{
		IConfigurationElement[] elements = extension.getConfigurationElements();
		List<IParser> parsers = new ArrayList<IParser>();
		
		for (IConfigurationElement element : elements)
		{
			if (element.getName().equals(TAG_SCANNER))
			{
				processElement(parsers, element, true);
			}
		}
		
		return parsers.toArray(new IParser[parsers.size()]);
	}
	
	/**
	 * processElement
	 * 
	 * @param parsers
	 * @param element
	 * @param isScanner
	 */
	private void processElement(List<IParser> parsers, IConfigurationElement element, boolean isScanner)
	{
		String language = element.getAttribute(ATTR_LANGUAGE);

		if (language != null && language.length() > 0)
		{
			// create parser instance
			//parser = (IParser) element.createExecutableExtension(ATTR_PARSER);
			IParser parser;
			
			if (isScanner)
			{
				parser = LanguageRegistry.createScanner(language);
			}
			else
			{
				parser = LanguageRegistry.createParser(language);
			}

			// register all pi-languages using this parser
			this.registerPILanguages(element.getChildren(TAG_PI_LANGUAGE), parser, isScanner);
			
			// register all single-line comment languages
			this.registerCommentLanguages(
				SINGLE_LINE_COMMENT_KEY,
				element.getChildren(TAG_SINGLE_LINE_COMMENT_LANGUAGE),
				parser,
				isScanner
			);
			
			// register all multi-line comment languages
			this.registerCommentLanguages(
				MULTI_LINE_COMMENT_KEY,
				element.getChildren(TAG_MULTI_LINE_COMMENT_LANGUAGE),
				parser,
				isScanner
			);
			
			// register all document comment languages
			this.registerCommentLanguages(
				DOCUMENTATION_COMMENT_KEY,
				element.getChildren(TAG_DOCUMENTATION_COMMENT_LANGUAGE),
				parser,
				isScanner
			);

			// add parser for our return result
			parsers.add(parser);
		}
	}

	/**
	 * registerPILanguage
	 * 
	 * @param element
	 * @param parsers
	 * @param isScanner
	 */
	private void registerCommentLanguages(String key, IConfigurationElement[] elements, IParser parser, boolean isScanner)
	{
		for (int i = 0; i < elements.length; i++)
		{
			// get pi-language
			IConfigurationElement element = elements[i];

			// build full pi-name
			String piName = "<?" + element.getAttribute(ATTR_PI_NAME); //$NON-NLS-1$

			// register transition
			if (isScanner)
			{
				this.registerScanner(piName, parser);
			}
			else
			{
				this.registerParser(piName, parser);
			}
		}
	}
	
	/**
	 * registerParser
	 * 
	 * @param key
	 * @param parser
	 */
	private void registerParser(String key, IParser parser)
	{
		this._parserByMimeType.put(key, parser);
	}

	/**
	 * registerPILanguage
	 * 
	 * @param element
	 * @param parsers
	 * @param isScanner
	 */
	private void registerPILanguages(IConfigurationElement[] elements, IParser parser, boolean isScanner)
	{
		for (int i = 0; i < elements.length; i++)
		{
			// get pi-language
			IConfigurationElement element = elements[i];

			// build full pi-name
			String piName = "<?" + element.getAttribute(ATTR_PI_NAME); //$NON-NLS-1$

			// register transition
			if (isScanner)
			{
				this.registerScanner(piName, parser);
			}
			else
			{
				this.registerParser(piName, parser);
			}
		}
	}
	
	/**
	 * registerScanner
	 * 
	 * @param key
	 * @param parser
	 */
	private void registerScanner(String key, IParser parser)
	{
		this._scannerByMimeType.put(key, parser);
	}
}
