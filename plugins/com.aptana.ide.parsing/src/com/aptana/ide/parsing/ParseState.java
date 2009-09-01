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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aptana.ide.lexer.IRange;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.Range;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.parsing.nodes.IParseNodeFactory;

/**
 * @author Kevin Lindsey
 */
public class ParseState implements IParseState
{
	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final char[] NO_CHARS = new char[0];
	
	private List<IParseState> _children;
	private List<IParseNode> _comments = new ArrayList<IParseNode>();
	private List<IRange> _updateRegions = new ArrayList<IRange>();
	private LexemeList _lexemes;

	private int _fileIndex = -1;

	private char[] _source;
	private char[] _insertedText;
	private int _startingOffset;
	private int _removeLength;

	private long _parseTime;

	Map<Object,Object> updatedProperties = new HashMap<Object,Object>();
	
	/**
	 * Holds the parse results across languages as they are parsed.
	 */
	protected IParseNode parseResults;

	/**
	 * Create a new instance of ParseState
	 */
	public ParseState()
	{
		this._lexemes = new LexemeList();
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#addChildState(com.aptana.ide.parsing.IParseState)
	 */
	public void addChildState(IParseState child)
	{
		if (this._children == null)
		{
			this._children = new ArrayList<IParseState>();
		}

		this._children.add(child);
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#addCommentRegion(com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public void addCommentRegion(IParseNode node)
	{
		this._comments.add(node);
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#addUpdateRegion(com.aptana.ide.lexer.IRange)
	 */
	public void addUpdateRegion(IRange range)
	{
		this._updateRegions.add(range);
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#clearCommentRegions()
	 */
	public void clearCommentRegions()
	{
		// clear comment regions
		this._comments.clear();
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#clearEditState()
	 */
	public void clearEditState()
	{
		this._insertedText = NO_CHARS;
		this._removeLength = 0;
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#clearUpdateRegions()
	 */
	public void clearUpdateRegions()
	{
		this._updateRegions.clear();
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getChildren()
	 */
	public IParseState[] getChildren()
	{
		return this._children.toArray(new IParseState[0]);
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getCommentRegions()
	 */
	public IParseNode[] getCommentRegions()
	{
		return this._comments.toArray(new IParseNode[0]);
	}

	/**
	 * Find the last lexeme in the text that is being removed. If the deleted text touches the next lexeme, that next
	 * lexeme becomes the ending lexeme
	 * 
	 * @return Returns the last lexeme in the deleted text
	 */
	private Lexeme getEndingLexeme()
	{
		LexemeList lexemes = this.getLexemeList();
		int lexemeCount = lexemes.size();
		int endingOffset = this.getStartingOffset() + this.getRemoveLength();
		int index = lexemes.getLexemeIndex(endingOffset);
		Lexeme result = null;

		if (index < 0)
		{
			index = -(index + 1);

			if (index < lexemeCount)
			{
				Lexeme candidate = lexemes.get(index);

				if (candidate.offset == endingOffset)
				{
					result = candidate;
				}
			}
		}
		else
		{
			result = lexemes.get(index);
		}

		// check if next lexeme is touching this one and include it if it is
		if (result != null)
		{
			index++;

			if (index < lexemeCount)
			{
				Lexeme candidate = lexemes.get(index);

				if (result.getEndingOffset() == candidate.offset)
				{
					result = candidate;
				}
			}
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getFileIndex()
	 */
	public int getFileIndex()
	{
		return this._fileIndex;
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getInsertedText()
	 */
	public char[] getInsertedText()
	{
		char[] result = this._insertedText;
		
		if (result == null)
		{
			result = NO_CHARS;  
		}
		
		return result;
	}

	/**
	 * There is no default language for a parseState
	 * 
	 * @see com.aptana.ide.parsing.IParseState#getLanguage()
	 */
	public String getLanguage()
	{
		return EMPTY;
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getLexemeList()
	 */
	public LexemeList getLexemeList()
	{
		return this._lexemes;
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getParent()
	 */
	public IParseState getParent()
	{
		// ParseState does not have a parent
		return null;
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getParseNodeFactory()
	 */
	public IParseNodeFactory getParseNodeFactory()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getParseResults()
	 */
	public IParseNode getParseResults()
	{
		return this.parseResults;
	}

	/**
	 * The default parser is the current parser.
	 * 
	 * @see com.aptana.ide.parsing.IParseState#getParseState(java.lang.String)
	 */
	public IParseState getParseState(String language)
	{
		return this;
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getParseTime()
	 */
	public long getParseTime()
	{
		return this._parseTime;
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getRemoveLength()
	 */
	public int getRemoveLength()
	{
		return this._removeLength;
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getRoot()
	 */
	public IParseState getRoot()
	{
		// ParseStates do not have parents or ancestors
		return null;
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getSource()
	 */
	public char[] getSource()
	{
		char[] result = this._source;
		
		if (result == null)
		{
			result = NO_CHARS;
		}
		
		return result;
	}

	/**
	 * Find the first lexeme in the text that is being removed. If the deleted text touches the previous lexeme, that
	 * lexeme becomes the starting lexeme
	 * 
	 * @return Returns the first lexeme in the delete text
	 */
	private Lexeme getStartingLexeme()
	{
		LexemeList lexemes = this.getLexemeList();
		int startingOffset = this.getStartingOffset();
		int index = lexemes.getLexemeIndex(startingOffset);
		Lexeme result = null;

		if (index < 0)
		{
			index = -(index + 1);

			if (index > 0)
			{
				Lexeme candidate = lexemes.get(index - 1);

				if (candidate.getEndingOffset() == startingOffset)
				{
					result = candidate;
				}
			}
		}
		else
		{
			if (index > 0)
			{
				Lexeme candidate = lexemes.get(index - 1);

				if (candidate.getEndingOffset() == startingOffset)
				{
					result = candidate;
				}
				else
				{
					result = lexemes.get(index);
				}
			}
			else
			{
				result = lexemes.get(index);
			}
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getStartingOffset()
	 */
	public int getStartingOffset()
	{
		return this._startingOffset;
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getUpdatedProperties()
	 */
	public Map<Object,Object> getUpdatedProperties()
	{
		return updatedProperties;
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getUpdateRegions()
	 */
	public IRange[] getUpdateRegions()
	{
		return this._updateRegions.toArray(new IRange[this._updateRegions.size()]);
	}

	/**
	 * Perform any insertions on the source text. This will update the lexeme list and its affected range
	 */
	private void insertText()
	{
		int insertedLength = this.getInsertedText().length;

		// handle inserted text
		if (insertedLength > 0)
		{
			LexemeList lexemes = this.getLexemeList();
			Range affectedRegion = lexemes.getAffectedRegion();
			int resizeDelta = insertedLength - this.getRemoveLength();
			int startingOffset = this.getStartingOffset();

			// find lexeme containing starting offset
			int insertIndex = lexemes.getLexemeIndex(startingOffset + this.getRemoveLength());

			if (insertIndex < 0)
			{
				insertIndex = -(insertIndex + 1);
			}

			if (0 <= insertIndex && insertIndex < lexemes.size())
			{
				// get lexeme we're about to remove
				Lexeme lexeme = lexemes.get(insertIndex);

				// add it to our affected region
				if (lexeme.offset <= startingOffset)
				{
					affectedRegion.includeInRange(lexeme.offset);
				}
				affectedRegion.includeInRange(lexeme.getEndingOffset() + resizeDelta);

				// remove it
				lexemes.remove(insertIndex);
			}

			// find lexeme to the left
			insertIndex = lexemes.getLexemeIndex(startingOffset);

			if (insertIndex < 0)
			{
				insertIndex = -(insertIndex + 1);
			}

			if (insertIndex > 0)
			{
				// get lexeme we're about to remove
				Lexeme lexeme = lexemes.get(insertIndex - 1);

				if (lexeme.getEndingOffset() == startingOffset)
				{
					// add it to our affected region
					affectedRegion.includeInRange(lexeme);

					// remove it
					lexemes.remove(insertIndex - 1);
				}
			}
		}
	}

	/**
	 * Called after the full parse happens. This base class will iterate over the children.
	 * 
	 * @see com.aptana.ide.parsing.IParseState#onAfterParse()
	 */
	public void onAfterParse()
	{
		if (this._children != null && this._children.size() > 0)
		{
			for (int i = 0; i < this._children.size(); i++)
			{
				this._children.get(i).onAfterParse();
			}
		}
	}

	/**
	 * Called before the full parse happens. This base class will iterate over the children.
	 * 
	 * @see com.aptana.ide.parsing.IParseState#onBeforeParse()
	 */
	public void onBeforeParse()
	{
		if (this._children != null && this._children.size() > 0)
		{
			for (int i = 0; i < this._children.size(); i++)
			{
				this._children.get(i).onBeforeParse();
			}
		}
		
		this.clearCommentRegions();
		this.clearUpdateRegions();
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#removeChildState(com.aptana.ide.parsing.IParseState)
	 */
	public void removeChildState(IParseState child)
	{
		if (this._children != null)
		{
			this._children.remove(child);
		}
	}

	/**
	 * Perform any deletions on the source text. This will update the lexeme list and its affected range
	 */
	private void removeText()
	{
		int removeLength = this.getRemoveLength();

		// remove all lexemes (and possibly their neighbors) for the text that
		// was deleted
		if (removeLength > 0)
		{
			LexemeList lexemes = this.getLexemeList();
			Range affectedRegion = lexemes.getAffectedRegion();
			int startingOffset = this.getStartingOffset();
			int insertedLength = this.getInsertedText().length;
			int resizeDelta = insertedLength - removeLength;

			Lexeme startingLexeme = this.getStartingLexeme();
			Lexeme endingLexeme = this.getEndingLexeme();

			// remove range from lexeme list
			if (startingLexeme != null && endingLexeme != null)
			{
				// add lexeme range to our affected region
				affectedRegion.includeInRange(startingLexeme.offset);
				affectedRegion.includeInRange(endingLexeme.getEndingOffset() + resizeDelta);

				// remove range of lexemes
				lexemes.remove(startingLexeme, endingLexeme);
			}
			else
			{
				if (startingLexeme != null)
				{
					// we only need to add the starting lexeme to our range if
					// we edited text inside the lexeme itself.
					if (startingLexeme.containsOffset(startingOffset)
							|| startingLexeme.getEndingOffset() == startingOffset)
					{
						// add lexeme range to our affected region
						affectedRegion.includeInRange(startingLexeme.offset);

						lexemes.remove(startingLexeme);
					}
				}

				if (endingLexeme != null)
				{
					// NOTE: [KEL] I had to remove this optimization since
					// Ruby's embedded documents have lexemes that rely on
					// start-of-line tests. If we don't invalidate this
					// lexeme, then those lexemes will never get re-lexed.

					// if (endingLexeme.offset != startingOffset + removeLength)
					// {
					// // add lexeme range to our affected region
					// affectedRegion.includeInRange(endingLexeme.getEndingOffset() + resizeDelta);
					//
					// lexemes.remove(endingLexeme);
					// }

					// add lexeme range to our affected region
					affectedRegion.includeInRange(endingLexeme.getEndingOffset() + resizeDelta);

					lexemes.remove(endingLexeme);
				}

				int startingIndex = lexemes.getLexemeCeilingIndex(startingOffset);
				int endingIndex = lexemes.getLexemeFloorIndex(startingOffset + removeLength - 1);

				if (startingIndex != -1 && endingIndex != -1 && startingIndex <= endingIndex)
				{
					startingLexeme = lexemes.get(startingIndex);
					endingLexeme = lexemes.get(endingIndex);

					// add lexeme range to our affected region
					// affectedRegion.includeInRange(startingLexeme.offset);
					// affectedRegion.includeInRange(endingLexeme.getEndingOffset() + resizeDelta);

					// remove range of lexemes
					lexemes.remove(startingLexeme, endingLexeme);
				}
			}
		}
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#reset()
	 */
	public void reset()
	{
		// clear the lexeme cache
		// we need to sync this as there may be an onContentChanged parse going on
		LexemeList ll = this.getLexemeList();
		synchronized (ll)
		{
			ll.clear();
		}

		// let children perform any other reset functionality they may have
		if (this._children != null)
		{
			for (int i = 0; i < this._children.size(); i++)
			{
				IParseState child = this._children.get(i);

				child.reset();
			}
		}
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#setEditState(java.lang.String, java.lang.String, int, int)
	 */
	public void setEditState(String source, String insertedSource, int offset, int removeLength)
	{
		this._source = (source != null) ? source.toCharArray() : NO_CHARS;
		this._insertedText = (insertedSource != null) ? insertedSource.toCharArray() : NO_CHARS;
		this._startingOffset = offset;
		this._removeLength = removeLength;
		
		this.updateLexemeList();
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#setFileIndex(int)
	 */
	public void setFileIndex(int index)
	{
		// System.out.println("index " + this._fileIndex + " > " + index);
		// if(this._fileIndex > 0 && this._fileIndex != index)
		unloadFromEnvironment();

		this._fileIndex = index;
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#setParseResults(com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public void setParseResults(IParseNode results)
	{
		// save root node
		this.parseResults = results;
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#setParseTime(long)
	 */
	public void setParseTime(long elapsedMilliseconds)
	{
		this._parseTime = elapsedMilliseconds;
	}

	/**
	 * Return a string representation of the edit contained by this parse state
	 * 
	 * @return Returns a string representation of the underlying edit in this parse state
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		int insertedLength = this._insertedText.length;

		sb.append("@").append(this._startingOffset); //$NON-NLS-1$

		if (this._removeLength > 0 || insertedLength > 0)
		{
			if (this._removeLength > 0)
			{
				sb.append(":r").append(this._removeLength); //$NON-NLS-1$
			}

			if (insertedLength > 0)
			{
				sb.append(":i").append(insertedLength).append(":").append(this._insertedText); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
			{
				// output closing delimiter for proper parsing of the remove length
				sb.append(":"); //$NON-NLS-1$
			}
		}
		else
		{
			// output closing delimiter for proper parsing of the offset
			sb.append(":"); //$NON-NLS-1$
		}

		return sb.toString();
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#unloadFromEnvironment()
	 */
	public void unloadFromEnvironment()
	{
		for (int i = 0; i < _children.size(); i++)
		{
			IParseState child = this._children.get(i);
			
			child.unloadFromEnvironment();
		}
	}

	/**
	 * updateLexemeList
	 */
	protected void updateLexemeList()
	{
		LexemeList lexemes = this.getLexemeList();
		int startingOffset = this.getStartingOffset();
		int insertedLength = this.getInsertedText().length;

		// set initial changed region
		lexemes.getAffectedRegion().setRange(startingOffset, startingOffset + insertedLength);

		// perform any pending source deletes
		this.removeText();

		// perform any pending source insertions
		this.insertText();

		// update lexeme offsets
		this.updateLexemeOffsets();
	}

	/**
	 * Update the offsets of all lexemes at or following the edit point
	 */
	private void updateLexemeOffsets()
	{
		LexemeList lexemes = this.getLexemeList();
		char[] insertedText = this.getInsertedText();
		int insertedLength = (insertedText != null) ? insertedText.length : 0;
		int resizeDelta = insertedLength - this.getRemoveLength();

		// update lexeme offsets at and after the edit point
		if (resizeDelta != 0)
		{
			int insertIndex = lexemes.getLexemeIndex(this.getStartingOffset());

			if (insertIndex < 0)
			{
				insertIndex = -(insertIndex + 1);
			}

			lexemes.shiftLexemeOffsets(insertIndex, resizeDelta);
		}
	}
}
