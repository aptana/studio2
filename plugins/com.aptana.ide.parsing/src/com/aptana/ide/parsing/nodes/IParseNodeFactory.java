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
package com.aptana.ide.parsing.nodes;

import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.IParseState;

/**
 * @author Kevin Lindsey
 */
public interface IParseNodeFactory<T extends IParseNode>
{
	/**
	 * Get the parse state that owns this node factory
	 * 
	 * @return IParseState
	 */
	IParseState getOwningParseState();

	/**
	 * Create a new instance of a parse node of the given type
	 * 
	 * @param nodeType
	 *            The unique integer value of the type of node to create. Typically, string overloaded version of this
	 *            method will calculate the node type index and call this method.
	 * @param startingLexeme
	 *            The lexeme that starts this node
	 * @return Returns the resulting parse node
	 */
	T createParseNode(int nodeType, Lexeme startingLexeme);

	/**
	 * Create a root node for this language. Note that some languages can be nested in other languages, so this node
	 * would not be the root of the entire tree, but only the root for the transition into a new language.
	 * 
	 * @return IParseNode
	 */
	IParseNode createRootNode();
}
