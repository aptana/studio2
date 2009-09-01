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
package com.aptana.ide.parsing;

import java.util.Map;

import com.aptana.ide.lexer.IRange;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.parsing.nodes.IParseNodeFactory;

/**
 * Stores the current lexemes of the current file, as well as the parse results. If all else fails, this is the "most
 * correct" version of the file information. Everything a generic parser needs in order to parse a file.
 * 
 * @author Kevin Lindsey
 */
public interface IParseState
{
	/**
	 * Add a child parse state to this parse state
	 * 
	 * @param child
	 *            The child parse state to add
	 */
	void addChildState(IParseState child);

	/**
	 * Adds a comment region (GenericCommentNode) to this parse state
	 * 
	 * @param node
	 *            GenericCommentNode instance
	 */
	void addCommentRegion(IParseNode node);
	
	/**
	 * Add a range to be updated
	 * 
	 * @param range
	 */
	void addUpdateRegion(IRange range);

	/**
	 * Clears the comments regions
	 */
	void clearCommentRegions();
	
	/**
	 * Clear any previously calculated update regions
	 */
	void clearUpdateRegions();

	/**
	 * Remove the current edit while preserving the source
	 */
	void clearEditState();
	
	/**
	 * Get the child parse states for this parser
	 * 
	 * @return Returns the child parse states for this parser
	 */
	IParseState[] getChildren();

	/**
	 * Gets the comment regions currently in this parse state
	 * 
	 * @return - Array of GenericCommentNode nodes
	 */
	IParseNode[] getCommentRegions();
	
	/**
	 * Gets the source regions that need a forced update. This occurs when a scanning-only
	 * pass over the source cannot properly categorize its lexemes. The parsing pass is able
	 * to determine a refined token type based on context. Since this occurs after scanning
	 * and hence after redraw, we need to force an update of these lexemes for proper colorization
	 * 
	 * @return
	 */
	IRange[] getUpdateRegions();

	/**
	 * Get the file index associated with this parse state
	 * 
	 * @return Returns the associated file index
	 */
	int getFileIndex();

	/**
	 * Get the source code that has been inserted into the source document
	 * 
	 * @return Returns the source code that has been inserted into the source document
	 */
	char[] getInsertedText();

	/**
	 * Gets the language mime type for this parse state.
	 * 
	 * @return Returns the language mime type for this parse state.
	 */
	String getLanguage();

	/**
	 * Retrieve the lexeme list associated with this parse state
	 * 
	 * @return Returns this parse state's lexeme list
	 */
	LexemeList getLexemeList();

	/**
	 * Get the parent parse state for this parse state
	 * 
	 * @return Returns the parse state that owns this parse state
	 */
	IParseState getParent();

	/**
	 * Gets the factory that creates all parse nodes for a given language.
	 * 
	 * @return IParseNodeFactory
	 */
	IParseNodeFactory getParseNodeFactory();

	/**
	 * Gets the results of a parse.
	 * 
	 * @return Returns the results of a parse.
	 */
	IParseNode getParseResults();

	/**
	 * Get the child parse state of given language
	 * 
	 * @param language
	 *            the language mime type
	 * @return Returns the parse state that owns this parse state
	 */
	IParseState getParseState(String language);

	/**
	 * Return the amount of time in milliseconds that elapsed during parsing of the edit represented by this parse state
	 * 
	 * @return Returns the amount of time in milliseconds that elapsed during the last parse
	 */
	long getParseTime();

	/**
	 * Gets the length of the source code that has been deleted, if any.
	 * 
	 * @return Returns the length of the deleted text.
	 */
	int getRemoveLength();

	/**
	 * Get the top-most IParseState in this tree. This is typically needed for calls that need to propagate to all child
	 * parse states, like reset()
	 * 
	 * @return Returns the top-most IParseState in the tree
	 */
	IParseState getRoot();

	/**
	 * Retrieve the full source of the current document
	 * 
	 * @return Returns the source of the current document
	 */
	char[] getSource();

	/**
	 * Get the offset where the source code begins in the source file
	 * 
	 * @return Returns the source code's offset within its source file
	 */
	int getStartingOffset();

	/**
	 * @return Returns object that holds the environment properties that have been added by this parseState.
	 */
	Map<Object,Object> getUpdatedProperties();

	/**
	 * Called after the full parse happens (after parse, but not before parseComposite).
	 */
	void onAfterParse();

	/**
	 * Called before the full parse happens (before parse, but not before parseComposite).
	 */
	void onBeforeParse();

	/**
	 * Remove a child parse state from this parse state
	 * 
	 * @param child
	 *            The child parse state to remove
	 */
	void removeChildState(IParseState child);

	/**
	 * Reset all the state information associated with this parse context
	 */
	void reset();

	/**
	 * Setup this parse state with the next edit to the source document
	 * 
	 * @param source
	 *            The entire source of the document after this edit has been applied
	 * @param insertedSource
	 *            The new text inserted into the document
	 * @param offset
	 *            The offset where this edit occurred
	 * @param removeLength
	 *            The number of characters to remove before inserting the insertedSource
	 */
	void setEditState(String source, String insertedSource, int offset, int removeLength);

	/**
	 * Set the file index associate with this parse state
	 * 
	 * @param index
	 *            The new file index for this parse state
	 */
	void setFileIndex(int index);

	/**
	 * Sets the results of a parse.
	 * 
	 * @param results
	 */
	void setParseResults(IParseNode results);

	/**
	 * Set the total number of milliseconds that elapsed for the last parse
	 * 
	 * @param elapsedMilliseconds
	 *            The new elapsed time
	 */
	void setParseTime(long elapsedMilliseconds);

	/**
	 * Unloads any additions the parse of this parse state has added to the environment
	 */
	void unloadFromEnvironment();
}
