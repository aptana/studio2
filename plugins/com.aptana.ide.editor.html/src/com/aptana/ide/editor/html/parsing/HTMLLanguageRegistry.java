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
package com.aptana.ide.editor.html.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.parsing.IParser;

/**
 * @author Kevin Lindsey
 */
public class HTMLLanguageRegistry
{
	private static String TAG_PARSER = "parser"; //$NON-NLS-1$
	private static String TAG_ELEMENT_LANGUAGE = "element-language"; //$NON-NLS-1$
	private static String TAG_PI_LANGUAGE = "pi-language"; //$NON-NLS-1$
	private static String TAG_ATTRIBUTE_LANGUAGE = "attribute-language"; //$NON-NLS-1$
	private static String TAG_VALUE = "value"; //$NON-NLS-1$

	// parser attribute
	private static String ATTR_LANGUAGE = "language"; //$NON-NLS-1$
	// private static String ATTR_PARSER = "parser"; //$NON-NLS-1$

	// element-language attributes
	private static String ATTR_ELEMENT_NAME = "element-name"; //$NON-NLS-1$
	private static String ATTR_ATTRIBUTE_NAME = "attribute-name"; //$NON-NLS-1$
	private static String ATTR_LANGUAGE_OWNS_ELEMENT = "language-owns-element"; //$NON-NLS-1$

	// pi-language attribute
	private static String ATTR_PI_NAME = "pi-name"; //$NON-NLS-1$
	private static String ATTR_HANDLES_EOF = "handles-eof"; //$NON-NLS-1$

	// value attribute
	private static String ATTR_VALUE = "value"; //$NON-NLS-1$

	private Map<String, IParser> _parsers;
	private Map<String, Boolean> _handlesEOF;
	private Map<String, Boolean> _languageOwnsElement;

	/**
	 * ElementRegistry
	 */
	public HTMLLanguageRegistry()
	{
		this._parsers = new HashMap<String, IParser>();
		this._handlesEOF = new HashMap<String, Boolean>();
		this._languageOwnsElement = new HashMap<String, Boolean>();
	}

	/**
	 * loadFromExtension
	 * 
	 * @param extension
	 * @return IParser[]
	 */
	public IParser[] loadFromExtension(IExtension extension)
	{
		IConfigurationElement[] elements = extension.getConfigurationElements();
		List<IParser> parsers = new ArrayList<IParser>();

		for (int i = 0; i < elements.length; i++)
		{
			IConfigurationElement element = elements[i];

			if (element.getName().equals(TAG_PARSER))
			{
				String language = element.getAttribute(ATTR_LANGUAGE);

				if (language != null && language.length() > 0)
				{
					// create parser instance
					// IParser parser = (IParser) element.createExecutableExtension(ATTR_PARSER);
					IParser parser = LanguageRegistry.createParser(language);

					// register all element-languages using this parser
					this.registerElementLanguages(element.getChildren(TAG_ELEMENT_LANGUAGE), parser);

					// register all pi-languages using this parser
					this.registerPILanguages(element.getChildren(TAG_PI_LANGUAGE), parser);

					// register all attribute-languages using this parser
					this.registerAttributeLanguages(element.getChildren(TAG_ATTRIBUTE_LANGUAGE), parser);

					// add parser for our return result
					parsers.add(parser);
				}
			}
		}

		return parsers.toArray(new IParser[parsers.size()]);
	}

	/**
	 * registerAttributeLanguages
	 * 
	 * @param elements
	 * @param parser
	 */
	private void registerAttributeLanguages(IConfigurationElement[] elements, IParser parser)
	{
		for (int i = 0; i < elements.length; i++)
		{
			// get element-language
			IConfigurationElement element = elements[i];

			// get list of attribute names
			IConfigurationElement[] attributeNames = element.getChildren(TAG_VALUE);

			for (int j = 0; j < attributeNames.length; j++)
			{
				String attributeName = attributeNames[j].getAttribute(ATTR_VALUE);

				if (attributeName != null)
				{
					// register transition
					this.setRegistryEntry(attributeName, StringUtils.EMPTY, StringUtils.EMPTY, parser);
				}
			}
		}
	}

	/**
	 * registerElementLanguage
	 * 
	 * @param element
	 * @param parsers
	 */
	private void registerElementLanguages(IConfigurationElement[] elements, IParser parser)
	{
		for (int i = 0; i < elements.length; i++)
		{
			// get element-language
			IConfigurationElement element = elements[i];

			// grab element name
			String elementName = element.getAttribute(ATTR_ELEMENT_NAME);

			// grab possible attribute name
			String attributeName = element.getAttribute(ATTR_ATTRIBUTE_NAME);

			if (attributeName != null)
			{
				// get a list of attribute values
				IConfigurationElement[] attributeValues = element.getChildren(TAG_VALUE);

				// add references
				for (int j = 0; j < attributeValues.length; j++)
				{
					String attributeValue = attributeValues[j].getAttribute(ATTR_VALUE);

					if (attributeValue == null)
					{
						attributeValue = StringUtils.EMPTY;
					}

					// register transition
					this.setRegistryEntry(elementName, attributeName, attributeValue, parser);
				}
			}
			else
			{
				// register element only
				this.setRegistryEntry(elementName, "", "", parser); //$NON-NLS-1$ //$NON-NLS-2$

				// determine who owns this element
				boolean languageOwnsElement = false;
				String ownsElement = element.getAttribute(ATTR_LANGUAGE_OWNS_ELEMENT);

				if (ownsElement != null)
				{
					languageOwnsElement = Boolean.parseBoolean(ownsElement);
				}

				this.setLanguageOwnsElement(elementName, StringUtils.EMPTY, StringUtils.EMPTY, languageOwnsElement);
			}
		}
	}

	/**
	 * registerPILanguage
	 * 
	 * @param element
	 * @param parsers
	 */
	private void registerPILanguages(IConfigurationElement[] elements, IParser parser)
	{
		for (int i = 0; i < elements.length; i++)
		{
			// get pi-language
			IConfigurationElement element = elements[i];

			// build full pi-name
			String piName = "<?" + element.getAttribute(ATTR_PI_NAME); //$NON-NLS-1$

			// register transition
			this.setRegistryEntry(piName, StringUtils.EMPTY, StringUtils.EMPTY, parser);

			// see if this language handles its own EOF
			boolean eof = false;
			String handlesEOF = element.getAttribute(ATTR_HANDLES_EOF);

			if (handlesEOF != null)
			{
				eof = Boolean.getBoolean(handlesEOF);
			}

			// store result
			this.setHandlesEOF(piName, StringUtils.EMPTY, StringUtils.EMPTY, eof);
		}
	}

	/**
	 * setHandlesEOF
	 * 
	 * @param piName
	 * @param attributeName
	 * @param attributeValue
	 * @param eof
	 */
	public void setHandlesEOF(String piName, String attributeName, String attributeValue, boolean eof)
	{
		String key = this.buildKey(piName, StringUtils.EMPTY, StringUtils.EMPTY);

		this._handlesEOF.put(key, eof);
	}

	/**
	 * setLanguageOwnsElement
	 * 
	 * @param elementName
	 * @param attributeName
	 * @param attributeValue
	 * @param eof
	 */
	public void setLanguageOwnsElement(String elementName, String attributeName, String attributeValue, boolean eof)
	{
		String key = this.buildKey(elementName, StringUtils.EMPTY, StringUtils.EMPTY);

		this._languageOwnsElement.put(key, eof);
	}

	/**
	 * setRegistryEntry
	 * 
	 * @param elementName
	 * @param attributeName
	 * @param attributeValue
	 * @param parser
	 */
	public void setRegistryEntry(String elementName, String attributeName, String attributeValue, IParser parser)
	{
		if (parser == null)
		{
			throw new IllegalArgumentException(Messages.LanguageRegistry_ParserMustBeDefined);
		}

		String key = this.buildKey(elementName, attributeName, attributeValue);

		this._parsers.put(key, parser);
	}

	/**
	 * getAttributeLanguage
	 * 
	 * @param attributeName
	 * @return IParser or null
	 */
	public IParser getAttributeLanguage(String attributeName)
	{
		IParser result = null;
		String key = this.buildKey(attributeName, StringUtils.EMPTY, StringUtils.EMPTY);

		if (this._parsers.containsKey(key))
		{
			result = this._parsers.get(key);
		}

		return result;
	}

	/**
	 * getElementLanguage
	 * 
	 * @param elementName
	 * @param attributeName
	 * @param attributeValue
	 * @return IParser or null
	 */
	public IParser getElementLanguage(String elementName, String attributeName, String attributeValue)
	{
		IParser result = null;
		String key = this.buildKey(elementName, attributeName, attributeValue);

		if (this._parsers.containsKey(key))
		{
			result = this._parsers.get(key);
		}

		return result;
	}

	/**
	 * getHandlesEOF
	 * 
	 * @param processInstructionName
	 * @return boolean
	 */
	public boolean getHandlesEOF(String processInstructionName)
	{
		String key = this.buildKey(processInstructionName, StringUtils.EMPTY, StringUtils.EMPTY);
		boolean result = false;

		if (this._handlesEOF.containsKey(key))
		{
			result = this._handlesEOF.get(key);
		}

		return result;
	}

	/**
	 * getLanguageOwnsElement
	 * 
	 * @param processInstructionName
	 * @return boolean
	 */
	public boolean getLanguageOwnsElement(String elementName)
	{
		String key = this.buildKey(elementName, StringUtils.EMPTY, StringUtils.EMPTY);
		boolean result = false;

		if (this._languageOwnsElement.containsKey(key))
		{
			result = this._languageOwnsElement.get(key);
		}

		return result;
	}

	/**
	 * getPercentLanguage
	 * 
	 * @param percentInstructionName
	 * @return IParser or null
	 */
	public IParser getPercentInstructionLanguage(String percentInstructionName)
	{
		IParser result = null;
		String key = this.buildKey(percentInstructionName, StringUtils.EMPTY, StringUtils.EMPTY);

		if (this._parsers.containsKey(key))
		{
			result = this._parsers.get(key);
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
		IParser result = null;
		String key = this.buildKey(processInstructionName, StringUtils.EMPTY, StringUtils.EMPTY);

		if (this._parsers.containsKey(key))
		{
			result = this._parsers.get(key);
		}

		return result;
	}

	/**
	 * buildKey
	 * 
	 * @param elementName
	 * @param attributeName
	 * @param attributeValue
	 * @return String
	 */
	private String buildKey(String elementName, String attributeName, String attributeValue)
	{
		if (elementName == null || elementName.length() == 0)
		{
			throw new IllegalArgumentException(Messages.LanguageRegistry_ElementNameMustBeDefined);
		}
		if (attributeName == null)
		{
			throw new IllegalArgumentException(Messages.LanguageRegistry_AttributeNameMustBeDefined);
		}
		if (attributeValue == null)
		{
			throw new IllegalArgumentException(Messages.LanguageRegistry_AttributeValueMustBeDefined);
		}

		return "[" + elementName.toLowerCase() + "]" + attributeName.toLowerCase() + "=" + attributeValue; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
