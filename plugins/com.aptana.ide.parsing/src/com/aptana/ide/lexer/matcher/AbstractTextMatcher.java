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
package com.aptana.ide.lexer.matcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aptana.ide.lexer.IToken;
import com.aptana.ide.lexer.ITokenList;
import com.aptana.ide.lexer.matcher.model.CategoryGroupElement;
import com.aptana.ide.lexer.matcher.model.MatcherElement;
import com.aptana.ide.lexer.matcher.model.TokenGroupElement;
import com.aptana.xml.INode;

/**
 * @author Kevin Lindsey
 */
public abstract class AbstractTextMatcher extends MatcherElement implements ITextMatcher
{
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private IToken _matchedToken;
	private String _name;
	private Map<String,String> _valuesByName;
	private List<NameValueChangeListener> _nameValueChangeListeners;
	
	/**
	 * this matcher's token type
	 */
	protected IToken token;

	/**
	 * AbstractMatcher
	 */
	public AbstractTextMatcher()
	{
		this.addChildTypes();
	}

	/**
	 * accept
	 * 
	 * @param source
	 * @param startingOffset
	 * @param endingOffset
	 * @param token
	 */
	protected void accept(char[] source, int startingOffset, int endingOffset, IToken token)
	{
		if (token != null)
		{
			this.setMatchedToken(token);
		}

		if (this._name != null && this._name.length() > 0)
		{
			// get matched text
			String text = new String(source, startingOffset, endingOffset - startingOffset);

			this.setNameValue(this._name, text);
		}
	}

	/**
	 * addChildTypes
	 */
	public abstract void addChildTypes();

	/**
	 * addFirstCharacters
	 * 
	 * @param map
	 */
	public void addFirstCharacters(MatcherMap map)
	{
		this.addFirstCharacters(map, this);
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.ITextMatcher#addFirstCharacters(com.aptana.ide.lexer.matcher.MatcherMap,
	 *      com.aptana.ide.lexer.matcher.ITextMatcher)
	 */
	public void addFirstCharacters(MatcherMap map, ITextMatcher target)
	{
		map.addUncategorizedMatcher(target);
	}

	/**
	 * addNameValueChangeListener
	 * 
	 * @param listener
	 */
	public void addNameValueChangeListener(NameValueChangeListener listener)
	{
		if (listener != null)
		{
			AbstractTextMatcher top = this.getExpressionRoot();

			if (top._nameValueChangeListeners == null)
			{
				top._nameValueChangeListeners = new ArrayList<NameValueChangeListener>();
			}

			if (top._nameValueChangeListeners.contains(listener) == false)
			{
				top._nameValueChangeListeners.add(listener);
			}
		}
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.ITextMatcher#canMatchNothing()
	 */
	public boolean canMatchNothing()
	{
		return false;
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.model.MatcherElement#createToken(com.aptana.ide.lexer.ITokenList)
	 */
	protected void createToken(ITokenList tokenList)
	{
		String type = this.getType();

		if (type != null && type.length() > 0)
		{
			String group = this.getGroup();
			String category = this.getCategory();
			String newGroup = this.getSwitchTo();

			// create lexer token
			IToken token = tokenList.createToken();

			// set group, category, type, and new lexer group
			token.setLexerGroup(group);
			token.setCategory(category);
			token.setType(type);
			token.setNewLexerGroup(newGroup);

			try
			{
				// add to token list
				tokenList.add(token);

				// set token as matcher's return value
				this.token = token;
			}
			catch (IllegalArgumentException e)
			{
				this.getDocument().sendError(e.getMessage(), this);
			}
		}
	}

	/**
	 * fireNameValueChange
	 */
	private void fireNameValueChange(String name, String oldValue, String newValue)
	{
		AbstractTextMatcher top = this.getExpressionRoot();

		if (top._nameValueChangeListeners != null)
		{
			for (int i = 0; i < top._nameValueChangeListeners.size(); i++)
			{
				NameValueChangeListener listener = top._nameValueChangeListeners.get(i);

				listener.nameValueChanged(name, oldValue, newValue);
			}
		}
	}

	/**
	 * getExpressionRoot
	 * 
	 * @return AbstractMatcher
	 */
	protected AbstractTextMatcher getExpressionRoot()
	{
		// find top-most parent
		AbstractTextMatcher result = this;

		while (result != null)
		{
			INode parent = result.getParent();

			if (parent instanceof AbstractTextMatcher)
			{
				result = (AbstractTextMatcher) parent;
			}
			else
			{
				break;
			}
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.ITextMatcher#getMatchedToken()
	 */
	public IToken getMatchedToken()
	{
		return this._matchedToken;
	}

	/**
	 * getName
	 * 
	 * @return String or null
	 */
	public String getName()
	{
		return this._name;
	}

	/**
	 * getNameValue
	 * 
	 * @param name
	 * @return String
	 */
	protected String getNameValue(String name)
	{
		String result = EMPTY_STRING;
		AbstractTextMatcher top = this.getExpressionRoot();

		if (top._valuesByName != null && top._valuesByName.containsKey(name))
		{
			result = top._valuesByName.get(name);
		}

		return result;
	}

	/**
	 * getToken
	 * 
	 * @return IToken or null
	 */
	public IToken getToken()
	{
		return this.token;
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.ITextMatcher#match(char[], int, int)
	 */
	public abstract int match(char[] source, int offset, int eofOffset);

	/**
	 * removeNameValueChangeListener
	 * 
	 * @param listener
	 */
	public void removeNameValueChangeListener(NameValueChangeListener listener)
	{
		AbstractTextMatcher top = this.getExpressionRoot();

		if (top._nameValueChangeListeners != null)
		{
			top._nameValueChangeListeners.remove(listener);
		}
	}

	/**
	 * setMatchedToken
	 * 
	 * @param token
	 */
	protected void setMatchedToken(IToken token)
	{
		this._matchedToken = token;

		// propagate up the tree
		INode parent = this.getParent();

		if (parent != null && parent instanceof AbstractTextMatcher)
		{
			((AbstractTextMatcher) parent).setMatchedToken(token);
		}
	}

	/**
	 * setName
	 * 
	 * @param name
	 */
	public void setName(String name)
	{
		this._name = name;
	}

	/**
	 * setNameValue
	 * 
	 * @param name
	 * @param value
	 */
	protected void setNameValue(String name, String value)
	{
		// find top-most parent
		AbstractTextMatcher top = this.getExpressionRoot();

		// make sure we have a hash to populate
		if (top._valuesByName == null)
		{
			top._valuesByName = new HashMap<String,String>();
		}

		// locate the old value
		String oldValue = null;

		if (top._valuesByName.containsKey(name))
		{
			oldValue = top._valuesByName.get(name);
		}

		// set the new value
		top._valuesByName.put(name, value);

		// fire a change event
		this.fireNameValueChange(name, oldValue, value);
	}

	/**
	 * @see com.aptana.xml.NodeBase#setParent(com.aptana.xml.INode)
	 */
	protected void setParent(INode parent)
	{
		super.setParent(parent);

		// transfer name/value change listeners to expression root, if we're not the root
		if (this._nameValueChangeListeners != null)
		{
			AbstractTextMatcher top = this.getExpressionRoot();

			if (top != this)
			{
				while (this._nameValueChangeListeners.size() > 0)
				{
					// get listener
					NameValueChangeListener listener = this._nameValueChangeListeners.get(0);

					// add to root
					top.addNameValueChangeListener(listener);

					// NOTE: we can't use removeNameValueChangeListener below as that finds the expression root and
					// deletes from there. That would delete the listener we just added to the parent and cause an
					// infinite loop

					// remove from self
					this._nameValueChangeListeners.remove(listener);
				}

				// clear list
				this._nameValueChangeListeners = null;
			}
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		Class<?> thisClass = this.getClass();
		String fullName = thisClass.getName();
		String thisPackage = thisClass.getPackage().getName();
		String result;

		if (thisPackage != null && thisPackage.length() > 0)
		{
			result = fullName.substring(thisPackage.length() + 1);
		}
		else
		{
			result = fullName;
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.model.MatcherElement#validateLocal()
	 */
	protected void validateLocal()
	{
		INode parent = this.getParent();
		String type = this.getType();

		if (parent instanceof CategoryGroupElement)
		{
			// NOTE: CategoryGroup already tests for category, so we don't need to test for that here

			if (this.getTypeDefinedInSubtree() == false)
			{
				if (this.getChildCount() > 0)
				{
					this.getDocument().sendError(Messages.AbstractMatcher_No_Type_On_Self_Or_Descendants, this);
				}
				else
				{
					this.getDocument().sendError(Messages.AbstractMatcher_No_Type, this);
				}
			}
		}
		else if (parent instanceof TokenGroupElement)
		{
			String category = this.getCategory();

			if (type == null || type.length() == 0)
			{
				this.getDocument().sendError(Messages.AbstractMatcher_No_Type, this);
			}
			if (category == null || category.length() == 0)
			{
				this.getDocument().sendError(Messages.AbstractMatcher_No_Category, this);
			}
		}
		else
		{
			if (type == null || type.length() == 0)
			{
				// see if we're in a category group
				boolean insideCategoryGroup = false;

				while (parent != null)
				{
					if (parent instanceof CategoryGroupElement)
					{
						insideCategoryGroup = true;
						break;
					}
					else
					{
						parent = parent.getParent();
					}
				}

				// make sure we have a category and a group if we're not in a category group
				if (insideCategoryGroup == false)
				{
					String category = this.getCategory();

					if (category == null || category.length() == 0)
					{
						this.getDocument().sendError(Messages.AbstractMatcher_No_Category, this);
					}
				}
			}
		}
	}

	/**
	 * wrapChildrenInAndElement
	 */
	protected void wrapChildrenInAndElement()
	{
		// wrap multiple children in an <and> element
		if (this.getChildCount() > 1)
		{
			AndMatcher and = new AndMatcher();

			while (this.getChildCount() > 0)
			{
				and.appendChild(this.getChild(0));
			}

			this.appendChild(and);
		}
	}
}
