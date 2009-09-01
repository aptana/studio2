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
package com.aptana.ide.parsing.matcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import org.eclipse.core.runtime.Platform;

import com.aptana.ide.parsing.ParsingPlugin;
import com.aptana.xml.DocumentNode;
import com.aptana.xml.IErrorHandler;
import com.aptana.xml.INode;
import com.aptana.xml.NodeBase;
import com.aptana.xml.Parser;

/**
 * @author Kevin Lindsey
 */
public class MatcherParserBuilder implements IErrorHandler
{
	private static final String PARSER_NAMESPACE = "http://www.aptana.com/2007/parser/1.0"; //$NON-NLS-1$
	private static final String MATCHER_SUFFIX = "Matcher"; //$NON-NLS-1$

	private ParserMatcher _parser;

	/**
	 * MatcherParserBuilder
	 */
	public MatcherParserBuilder()
	{
	}

	/**
	 * createParserParser
	 * 
	 * @return Parser
	 */
	public static Parser createParserParser()
	{
		// create parser
		Parser parser = new Parser(PARSER_NAMESPACE);

		// add our bundle
		parser.addBundle(Platform.getBundle("com.aptana.ide.parsing")); //$NON-NLS-1$

		// add our packages for our model and matchers
		parser.addPackage(ILexemeMatcher.class.getPackage().getName()); // "com.aptana.ide.parser.matcher"

		// add our Element and Matcher suffixes
		parser.addSuffix(MATCHER_SUFFIX);

		// use NodeBase for our undefined element class
		parser.setUnknownElementClass(NodeBase.class);

		return parser;
	}

	/**
	 * getParser
	 * 
	 * @return ParserElement
	 */
	public ParserMatcher getParser()
	{
		return this._parser;
	}

	/**
	 * @see com.aptana.xml.IErrorHandler#handleError(int, int, java.lang.String)
	 */
	public void handleError(int line, int column, String message)
	{
		String msg = MessageFormat.format(
			Messages.getString("MatcherParserBuilder.Error_at_line_column"), //$NON-NLS-1$
			new Object[] {
				Integer.toString(line),
				Integer.toString(column),
				message
			}
		);

		ParsingPlugin.logError(msg);
	}

	/**
	 * @see com.aptana.xml.IErrorHandler#handleInfo(int, int, java.lang.String)
	 */
	public void handleInfo(int line, int column, String message)
	{
		String msg = MessageFormat.format(
			Messages.getString("MatcherParserBuilder.Info_at_line_column"), //$NON-NLS-1$
			new Object[] {
				Integer.toString(line),
				Integer.toString(column),
				message
			}
		);

		ParsingPlugin.logInfo(msg);
	}

	/**
	 * @see com.aptana.xml.IErrorHandler#handleWarning(int, int, java.lang.String)
	 */
	public void handleWarning(int line, int column, String message)
	{
		String msg = MessageFormat.format(
			Messages.getString("MatcherParserBuilder.Warning_at_line_column"), //$NON-NLS-1$
			new Object[] {
				Integer.toString(line),
				Integer.toString(column),
				message
			}
		);

		ParsingPlugin.logWarning(msg);
	}

	/**
	 * Load the specified binary grammar file
	 * 
	 * @param file
	 */
	public void loadXML(File file)
	{
		try
		{
			FileInputStream inputStream = new FileInputStream(file);

			this.loadXML(inputStream);
		}
		catch (FileNotFoundException e)
		{
			ParsingPlugin.logError(Messages.getString("MatcherParserBuilder.Error_reading_parser_file"), e); //$NON-NLS-1$
		}
	}

	/**
	 * load
	 * 
	 * @param in
	 */
	public void loadXML(InputStream in)
	{
		try
		{
			// create parser
			Parser parser = createParserParser();

			// associate error handler
			parser.setErrorHandler(this);

			// get the parse result
			DocumentNode result = parser.loadXML(in);

			// post-process the results
			if (result != null)
			{
				INode node = result.getRootNode();

				if (node != null && node instanceof ParserMatcher)
				{
					ParserMatcher parserElement = (ParserMatcher) node;

					parserElement.validate();
					parserElement.seal();

					this._parser = parserElement;
				}
			}
		}
		catch (Exception e)
		{
			ParsingPlugin.logError(Messages.getString("MatcherParserBuilder.Error_building_parser"), e); //$NON-NLS-1$
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
