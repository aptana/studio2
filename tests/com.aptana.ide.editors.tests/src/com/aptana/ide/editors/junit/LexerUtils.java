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
package com.aptana.ide.editors.junit;

import java.io.IOException;
import java.io.InputStream;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.matcher.MatcherLexerBuilder;

/**
 * LexerUtils
 * 
 * @author Ingo Muschenetz
 */
public class LexerUtils
{
	/**
	 * Protected constructor for utility class
	 */
	protected LexerUtils()
	{
	}

	/**
	 * createLexer
	 * 
	 * @param tokenResource
	 * @param language
	 * @param ignoreTokens
	 * @return ILexer
	 */
	public static ILexer createLexer(InputStream tokenResource, String language, int[] ignoreTokens)
	{
		MatcherLexerBuilder builder = new MatcherLexerBuilder();
		ILexer lexer = null;

		try
		{
			builder.loadXML(tokenResource);
		}
		finally
		{
			if (tokenResource != null)
			{
				try
				{
					tokenResource.close();
				}
				catch (IOException e)
				{
				}
			}
		}

		try
		{
			lexer = builder.buildLexer();
		}
		catch (LexerException e)
		{
			IdeLog.logError(EditorsJunitPlugin.getDefault(), "Error", e); //$NON-NLS-1$
		}

		// ignore whitespace
		lexer.setIgnoreSet(language, ignoreTokens);
		
		try
		{
			lexer.setLanguageAndGroup(language, "default"); //$NON-NLS-1$
		}
		catch (LexerException e)
		{
			IdeLog.logError(EditorsJunitPlugin.getDefault(), "Error", e); //$NON-NLS-1$
		}


		return lexer;
	}

	/**
	 * createLexemeList
	 * 
	 * @param lexer
	 * @param source
	 * @return LexemeList
	 */
	public static LexemeList createLexemeList(ILexer lexer, String source)
	{
		LexemeList ll = new LexemeList();
		lexer.setSource(source);
		while (!lexer.isEOS())
		{
			Lexeme currentLexeme = lexer.getNextLexeme();

			if (currentLexeme == null && lexer.isEOS() == false)
			{
				// Switch to error group.
				// NOTE: We want setGroup's exception to propagate since
				// that indicates an internal inconsistency when it
				// fails
				try
				{
					lexer.setGroup("error"); //$NON-NLS-1$
				}
				catch (LexerException e)
				{
					IdeLog.logError(EditorsJunitPlugin.getDefault(), "Error", e); //$NON-NLS-1$
				}
				currentLexeme = lexer.getNextLexeme();
			}

			if (currentLexeme != null)
			{
				ll.add(currentLexeme);
			}
		}
		return ll;
	}
}
