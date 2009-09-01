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
import java.util.List;
import java.util.Map;

import com.aptana.ide.lexer.IRange;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.parsing.nodes.IParseNode;
import com.aptana.ide.parsing.nodes.IParseNodeFactory;

/**
 * Adds lexemes to the parent parse state, or defers to the parent for the particular property.
 * 
 * @author Kevin Lindsey
 */
public class ParseStateChild implements IParseState
{
	private IParseState _parent;
	private List<IParseState> _children;
	private IParseNodeFactory _parseNodeFactory;
	private String language;

	/**
	 * Create a new instance of ParseStateChild
	 * 
	 * @param language -
	 *            language mime type
	 */
	public ParseStateChild(String language)
	{
		this(language, new ParseState());
	}

	/**
	 * Create a new instance of ParseStateChild
	 * 
	 * @param language -
	 *            language mime type
	 * @param parent
	 *            The parent IParseState
	 */
	public ParseStateChild(String language, IParseState parent)
	{
		if (parent == null)
		{
			throw new NullPointerException(Messages.ParseStateChild_ParseStateParentMustBeDefined);
		}

		// save reference to parent
		this._parent = parent;
		this.language = language;

		// add this as child or parent
		parent.addChildState(this);
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
		this._parent.addCommentRegion(node);
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#addUpdateRegion(com.aptana.ide.lexer.IRange)
	 */
	public void addUpdateRegion(IRange range)
	{
		this._parent.addUpdateRegion(range);
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#clearCommentRegions()
	 */
	public void clearCommentRegions()
	{
		this._parent.clearCommentRegions();
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#clearEditState()
	 */
	public void clearEditState()
	{
		this._parent.clearEditState();
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#clearUpdateRegions()
	 */
	public void clearUpdateRegions()
	{
		this._parent.clearUpdateRegions();
	}

	/**
	 * createParseNodeFactory
	 * 
	 * @return IParseNodeFactory
	 */
	protected IParseNodeFactory createParseNodeFactory()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getChildren()
	 */
	public IParseState[] getChildren()
	{
		if (this._children == null)
		{
			return null;
		}

		return this._children.toArray(new IParseState[0]);
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getCommentRegions()
	 */
	public IParseNode[] getCommentRegions()
	{
		return this._parent.getCommentRegions();
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getFileIndex()
	 */
	public int getFileIndex()
	{
		return this._parent.getFileIndex();
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getInsertedText()
	 */
	public char[] getInsertedText()
	{
		return this._parent.getInsertedText();
	}

	/**
	 * This method return the language specified when the ParseStateChild constructor is called.
	 * 
	 * @see com.aptana.ide.parsing.IParseState#getLanguage()
	 */
	public String getLanguage()
	{
		return language;
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getLexemeList()
	 */
	public LexemeList getLexemeList()
	{
		return this._parent.getLexemeList();
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getParent()
	 */
	public IParseState getParent()
	{
		return this._parent;
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getParseNodeFactory()
	 */
	public IParseNodeFactory getParseNodeFactory()
	{
		if (this._parseNodeFactory == null)
		{
			this._parseNodeFactory = this.createParseNodeFactory();
		}

		return this._parseNodeFactory;
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getParseResults()
	 */
	public IParseNode getParseResults()
	{
		return this._parent.getParseResults();
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getParseState(java.lang.String)
	 */
	public IParseState getParseState(String language)
	{
		if (this.getLanguage().equals(language))
		{
			return this;
		}
		else
		{
			return getParseState(language, this.getChildren());
		}
	}

	private IParseState getParseState(String language, IParseState[] children)
	{
		if (children == null || children.length == 0)
		{
			return null;
		}

		IParseState result = null;

		for (int i = 0; i < children.length; i++)
		{
			IParseState state = children[i];

			if (state.getLanguage().equals(language))
			{
				result = state;
				break;
			}
			else
			{
				result = getParseState(language, state.getChildren());

				if (result != null)
				{
					break;
				}
			}
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getParseTime()
	 */
	public long getParseTime()
	{
		return this._parent.getParseTime();
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getRemoveLength()
	 */
	public int getRemoveLength()
	{
		return this._parent.getRemoveLength();
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getRoot()
	 */
	public IParseState getRoot()
	{
		IParseState result = this;

		while (result.getParent() != null)
		{
			result = result.getParent();
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getSource()
	 */
	public char[] getSource()
	{
		return this._parent.getSource();
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getStartingOffset()
	 */
	public int getStartingOffset()
	{
		return this._parent.getStartingOffset();
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getUpdatedProperties()
	 */
	public Map<Object,Object> getUpdatedProperties()
	{
		return this._parent.getUpdatedProperties();
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#getUpdateRegions()
	 */
	public IRange[] getUpdateRegions()
	{
		return this._parent.getUpdateRegions();
	}

	/**
	 * Called after the full parse happens. This base class will iterate over the children.
	 * 
	 * @see com.aptana.ide.parsing.IParseState#onAfterParse()
	 */
	public void onAfterParse()
	{
		if (this._children != null)
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
		if (this._children != null)
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
	 * @see com.aptana.ide.parsing.IParseState#reset()
	 */
	public void reset()
	{
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
		this._parent.setEditState(source, insertedSource, offset, removeLength);
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#setFileIndex(int)
	 */
	public void setFileIndex(int index)
	{
		this._parent.setFileIndex(index);
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#setParseResults(com.aptana.ide.parsing.nodes.IParseNode)
	 */
	public void setParseResults(IParseNode results)
	{
		this._parent.setParseResults(results);
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#setParseTime(long)
	 */
	public void setParseTime(long elapsedMilliseconds)
	{
		this._parent.setParseTime(elapsedMilliseconds);
	}

	/**
	 * Return a string representation of the edit contained by this parse state
	 * 
	 * @return Returns a string representation of the underlying edit in this parse state
	 */
	public String toString()
	{
		return this._parent.toString();
	}

	/**
	 * @see com.aptana.ide.parsing.IParseState#unloadFromEnvironment()
	 */
	public void unloadFromEnvironment()
	{
		// do nothing until we are adding to the environment
		// in that case overload this method in the subclass.
	}
}
