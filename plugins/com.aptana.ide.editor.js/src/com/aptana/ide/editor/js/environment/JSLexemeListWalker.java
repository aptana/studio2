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
package com.aptana.ide.editor.js.environment;

import com.aptana.ide.editor.js.environment.LexemeConsumerHelper.LexemeConsumerResult;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.js.parsing.JSParseState;
import com.aptana.ide.editor.js.runtime.Environment;
import com.aptana.ide.editor.js.runtime.IScope;
import com.aptana.ide.editor.scriptdoc.parsing.PropertyDocumentation;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.metadata.IDocumentation;
import com.aptana.ide.metadata.IDocumentationStore;
import com.aptana.ide.parsing.IParseState;

/**
 * Walks a list of JSLexemes, performing a function on each one
 */
public class JSLexemeListWalker extends LexemeListWalker
{
	Environment env;
	IScope currentScope;
	LexemeBasedEnvironmentLoader envLoader;
	LexemeConsumerHelper consumerHelper;
	boolean isVar;

	/**
	 * Creates a new instance of JSLexemeListWalker
	 * 
	 * @param env
	 *            The current JS environment
	 * @param initialScope
	 *            The initial scope
	 * @param envLoader
	 *            The current environment loader
	 */
	protected JSLexemeListWalker(Environment env, IScope initialScope, LexemeBasedEnvironmentLoader envLoader)
	{
		currentScope = initialScope;
		this.env = env;
		this.envLoader = envLoader;
	}

	/**
	 * @see LexemeListWalker#walkList(IParseState, int)
	 */
	public int walkList(IParseState parseState, int startIndex)
	{
		consumerHelper = new LexemeConsumerHelper(env, envLoader, parseState);
		int result = super.walkList(parseState, startIndex);

		// insure all scriptDoc-only declared objects have been added
		addScriptDocObjects(parseState);
		// System.out.println("***walked*** " + parseState.getFileIndex() + " : " + parseState.getFullPath());
		return result;
	}

	/**
	 * @see LexemeListWalker#onLexeme(Lexeme, int)
	 */
	protected void onLexeme(Lexeme lexeme, int index) throws AbortException
	{
		if (!(lexeme.getLanguage().equals(JSMimeType.MimeType)))
		{
			return;
		}

		try
		{
			LexemeConsumerResult result = consumerHelper.consumeStatements(index, currentScope);
			this.currentIndex = result.endIndex;
		}
		catch (EndOfFileException e)
		{
			throw new AbortException(this.llSize);
		}
	}

	/**
	 * Adds all scriptdoc-only objects
	 */
	private void addScriptDocObjects(IParseState parseState)
	{
		JSParseState jsps = (JSParseState) parseState.getParseState(JSMimeType.MimeType);
		if (jsps != null)
		{
			IDocumentationStore store = jsps.getDocumentationStore();
			if (store != null)
			{
				IDocumentation[] docs = store.getDocumentationObjects();
				for (int i = 0; i < docs.length; i++)
				{
					if (docs[i] instanceof PropertyDocumentation)
					{
						PropertyDocumentation doc = (PropertyDocumentation) docs[i];
						String[] aliases = doc.getAliases().getTypes();
						for (int j = 0; j < aliases.length; j++)
						{
							String alias = aliases[j];
							LexemeConsumerHelper.addDocHolderToEnvironment(alias, doc, parseState);
						}
					}
				}
			}
		}
	}
}
