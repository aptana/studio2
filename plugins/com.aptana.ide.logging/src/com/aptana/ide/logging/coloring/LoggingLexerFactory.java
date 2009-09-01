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
package com.aptana.ide.logging.coloring;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.XMLUtils;
import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.lexer.IEnumerationMap;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.matcher.AbstractTextMatcher;
import com.aptana.ide.lexer.matcher.MatcherLexerBuilder;
import com.aptana.ide.lexer.matcher.MatcherTokenList;
import com.aptana.ide.logging.LoggingPlugin;
import com.aptana.ide.logging.LoggingPreferences;
import com.aptana.ide.logging.impl.InLineMatcher;
import com.aptana.ide.logging.preferences.ILoggingPreferenceListener;
import com.aptana.xml.INode;

/**
 * Lexer factory.
 * 
 * @author Denis Denisenko
 */
public class LoggingLexerFactory
{
	/**
	 * Lexer.
	 */
	private ILexer lexer;

	public ILexer getLexer()
	{
		if (lexer != null)
		{
			return lexer;
		}

		// create lexer builder
		MatcherLexerBuilder builder = new MatcherLexerBuilder();

		String lexerXML = createColoringLexerXML();
		InputStream in = new ByteArrayInputStream(lexerXML.getBytes());
		// read input stream
		builder.loadXML(in, LoggingLexerFactory.class.getClassLoader());

		// finalize lexer
		try
		{
			lexer = builder.buildLexer();

			MatcherTokenList tokenList = (MatcherTokenList) lexer.getTokenList(TokenTypes.LANGUAGE);

			// [KEL] Temporary fix to force optimization
			if (tokenList.hasGroup(TokenTypes.START_GROUP_NAME))
			{
				tokenList.setCurrentGroup(TokenTypes.START_GROUP_NAME);
				AbstractTextMatcher topMatcher = tokenList.getCurrentMatcher();
				INode child = topMatcher.getChild(0);
				if (child instanceof InLineMatcher)
				{
					InLineMatcher inLineMatcher = (InLineMatcher) child;
					inLineMatcher.buildFirstCharacterMap();
				}
			}
			// [KEL] End temporary fix

			IEnumerationMap typeMap = tokenList.getTypeMap();
			lexer.setIgnoreSet(TokenTypes.LANGUAGE, new int[] { typeMap.getIntValue(TokenTypes.ERROR) });
		}
		catch (LexerException e)
		{
			IdeLog.logError(LoggingPlugin.getDefault(), Messages.LoggingLexerFactory_ERR_Exception, e);
		}

		return lexer;
	}

	/**
	 * LoggingLexerFactory private constructor.
	 */
	LoggingLexerFactory()
	{
		LoggingPreferences preferences = LoggingPlugin.getDefault().getLoggingPreferences();
		preferences.addPreferenceListener(new ILoggingPreferenceListener()
		{

			public void rulesChanged()
			{
				lexer = null;
			}

			public void wrappingChanged(boolean wrapping)
			{
			}

			public void fontChanged(Font font)
			{
			}

			public void textForegroundColorChanged(Color color)
			{
			}
		});
	}

	/**
	 * Creates coloring lexer XML.
	 * 
	 * @return xml content
	 */
	private String createColoringLexerXML()
	{
		SourceWriter writer = new SourceWriter();

		// write xml declaration
		writer.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>"); //$NON-NLS-1$

		// open lexer
		writer.println("<lexer").increaseIndent(); //$NON-NLS-1$
		writer.printlnWithIndent("xmlns=\"http://www.aptana.com/2007/lexer/1.2\""); //$NON-NLS-1$
		writer.printlnWithIndent("language=\"text/log\""); //$NON-NLS-1$
		writer.printlnWithIndent("category-enumeration=\"com.aptana.ide.logging.coloring.TokenCategories\""); //$NON-NLS-1$
		writer.printlnWithIndent("type-enumeration=\"com.aptana.ide.logging.coloring.TokenTypes\">"); //$NON-NLS-1$
		writer.printlnWithIndent("<bundle>com.aptana.ide.logging</bundle>"); //$NON-NLS-1$
		writer.printlnWithIndent("<package>com.aptana.ide.logging.impl</package>"); //$NON-NLS-1$

		// open default token group
		writer.printlnWithIndent("<token-group group=\"default\">").increaseIndent(); //$NON-NLS-1$

		// add user-defined patterns
		this.emitRules(writer);

		// close default token group
		writer.decreaseIndent().printlnWithIndent("</token-group>"); //$NON-NLS-1$

		// close lexer
		writer.decreaseIndent().printlnWithIndent("</lexer>"); //$NON-NLS-1$

		return writer.toString();
	}

	/**
	 * Convenience function to emit all rules in their lexer xml format
	 * 
	 * @param writer
	 */
	private void emitRules(SourceWriter writer)
	{
		List<LoggingPreferences.Rule> rules = LoggingPlugin.getDefault().getLoggingPreferences().getRules();

		if (rules.size() > 0)
		{
			writer.printlnWithIndent("<category-group category=\"" + TokenTypes.DEFAULT_CATEGORY + "\">") //$NON-NLS-1$ //$NON-NLS-2$
					.increaseIndent();
			writer.printlnWithIndent("<in-line>").increaseIndent(); //$NON-NLS-1$

			for (LoggingPreferences.Rule rule : rules)
			{
				writeRule(writer, rule);
			}

			// writing all-matching rule that returns ERROR type token
			writer.printlnWithIndent("<regex category=\"" + TokenTypes.SYSTEM + "\" type=\"" + TokenTypes.ERROR //$NON-NLS-1$ //$NON-NLS-2$
					+ "\">.</regex>"); //$NON-NLS-1$

			writer.decreaseIndent().printlnWithIndent("</in-line>"); //$NON-NLS-1$
			writer.decreaseIndent().printlnWithIndent("</category-group>"); //$NON-NLS-1$
		}
	}

	/**
	 * Writes rule.
	 * 
	 * @param writer -
	 *            writer to use.
	 * @param rule -
	 *            rule to write.
	 */
	private void writeRule(SourceWriter writer, LoggingPreferences.Rule rule)
	{
		String elementName = (rule.isRegexp()) ? "regex" : "string"; //$NON-NLS-1$ //$NON-NLS-2$
		String caseAttribute = (rule.isCaseInsensitive()) ? " case-insensitive=\"true\"" : ""; //$NON-NLS-1$ //$NON-NLS-2$
		String patternName = XMLUtils.entitize(rule.getName());
		String patternString = XMLUtils.entitize(rule.getRule());
		String element = MessageFormat.format("<{0} type=\"{1}\"{3}>{2}</{0}>", new Object[] { elementName, //$NON-NLS-1$
				patternName, patternString, caseAttribute });

		writer.printlnWithIndent(element);
	}
}
