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
package com.aptana.ide.lexer.matcher.model;

import com.aptana.ide.lexer.matcher.AbstractTextMatcher;
import com.aptana.ide.lexer.matcher.NameValueChangeListener;
import com.aptana.xml.INode;

/**
 * @author Kevin Lindsey
 */
public class UseElement extends MatcherElement implements NameValueChangeListener
{
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$
	
	private String _referenceName;
	private String _value;

	/**
	 * UseElement
	 */
	public UseElement()
	{
	}

	/**
	 * getName
	 * 
	 * @return String
	 */
	public String getRef()
	{
		return this._referenceName;
	}

	/**
	 * @see com.aptana.xml.NodeBase#getText()
	 */
	public String getText()
	{
		String result = EMPTY_STRING;
		
		if (this._value != null)
		{
			result = this._value;
		}
		
		return result;
	}

	/**
	 * @see com.aptana.xml.NodeBase#setParent(com.aptana.xml.INode)
	 */
	protected void setParent(INode parent)
	{
		super.setParent(parent);
		
		if (parent instanceof AbstractTextMatcher)
		{
			((AbstractTextMatcher) parent).addNameValueChangeListener(this);
		}
	}

	/**
	 * setName
	 * 
	 * @param name
	 */
	public void setRef(String name)
	{
		this._referenceName = name;
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.model.MatcherElement#validateLocal()
	 */
	protected void validateLocal()
	{
		if (this._referenceName == null || this._referenceName.length() == 0)
		{
			this.getDocument().sendError(Messages.UseElement_Missing_Ref, this);
		}
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.NameValueChangeListener#nameValueChanged(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void nameValueChanged(String name, String oldValue, String newValue)
	{
		if (this._referenceName.equals(name))
		{
			this._value = newValue;
		}
	}
}
