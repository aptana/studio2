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
package com.aptana.ide.editor.xml.validator;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Locator;
import org.xml.sax.helpers.DefaultHandler;

import com.aptana.xml.IErrorHandler;

/**
 * @author Kevin Lindsey
 */
public class XMLParser extends DefaultHandler
{
	private static final Pattern PARSE_ERROR_LINE_NUMBER = Pattern.compile(" line (\\d+)"); //$NON-NLS-1$
	
	private IErrorHandler _errorHandler;
	private Locator _locator;

	/**
	 * XMLParser
	 */
	public XMLParser()
	{
	}

	/**
	 * setErrorHandler
	 * 
	 * @param errorHandler
	 */
	public void setErrorHandler(IErrorHandler errorHandler)
	{
		this._errorHandler = errorHandler;
	}

	/**
	 * sendError
	 * 
	 * @param message
	 */
	private void sendError(String message)
	{
		if (this._errorHandler != null && this._locator != null)
		{
			int line = this._locator.getLineNumber();
			int column = this._locator.getColumnNumber();

			if (line == -1)
			{
				Matcher m = PARSE_ERROR_LINE_NUMBER.matcher(message);

				if (m.find())
				{
					line = Integer.parseInt(m.group(1));
				}
				else
				{
					line = 0;
				}
			}

			this._errorHandler.handleError(line, column, message);
		}
	}

	// /**
	// * sendInfo
	// *
	// * @param message
	// */
	// private void sendInfo(String message)
	// {
	// if (this._errorHandler != null)
	// {
	// int line = this._locator.getLineNumber();
	// int column = this._locator.getColumnNumber();
	//			
	// this._errorHandler.handleInfo(line, column, message);
	// }
	// }

	// /**
	// * sendWarning
	// *
	// * @param message
	// */
	// private void sendWarning(String message)
	// {
	// if (this._errorHandler != null)
	// {
	// int line = this._locator.getLineNumber();
	// int column = this._locator.getColumnNumber();
	//
	// this._errorHandler.handleWarning(line, column, message);
	//		}
	//	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	public void setDocumentLocator(Locator locator)
	{
		super.setDocumentLocator(locator);

		this._locator = locator;
	}

	/**
	 * load
	 * 
	 * @param in
	 */
	public void parse(InputStream in)
	{
		try
		{
			// create a new SAX factory class
			SAXParserFactory factory = SAXParserFactory.newInstance();

			// make sure it generates namespace aware parsers
			factory.setNamespaceAware(true);

			// create the parser
			SAXParser saxParser = factory.newSAXParser();

			// parse the XML
			saxParser.parse(in, this);
		}
		catch (Exception e)
		{
			this.sendError(e.getMessage());
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{
				}
			}
		}
	}
}
