/**
 * This file Copyright (c) 2005-2007 Aptana, Inc. This program is
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
package com.aptana.ide.core.model;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class TextNodeSniffer
{

	private InputStream stream;
	private String value;

	/**
	 * Site manager parser
	 * 
	 * @param stream
	 */
	public TextNodeSniffer(InputStream stream)
	{
		this.stream = stream;
		this.value = null;
	}

	/**
	 * Starts a parse of the configured input stream looking for the first occurrence of the text node under the
	 * specified element
	 * 
	 * @param element
	 * @return - element text node or null
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public String start(final String element) throws ParserConfigurationException, SAXException, IOException
	{
		this.value = null;
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		SAXParser saxParser = factory.newSAXParser();
		saxParser.parse(stream, new DefaultHandler()
		{
			StringBuffer buffer;

			public void characters(char ch[], int start, int length) throws SAXException
			{
				if (buffer != null)
				{
					buffer.append(ch, start, length);
				}
			}

			public void endElement(String uri, String localName, String qName) throws SAXException
			{
				if (element.equals(qName) && value == null)
				{
					value = buffer.toString();
				}
			}

			public void startElement(String uri, String localName, String qName, Attributes attributes)
					throws SAXException
			{
				buffer = new StringBuffer();
			}

		});
		return this.value;
	}
}
