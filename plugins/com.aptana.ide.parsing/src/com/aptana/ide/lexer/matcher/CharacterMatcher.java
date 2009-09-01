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

import com.aptana.ide.lexer.matcher.model.UseElement;

/**
 * @author Kevin Lindsey
 */
public class CharacterMatcher extends AbstractCharacterMatcher
{
	private char _character;
	private boolean _characterDefined;

	/**
	 * SingleCharacterMatcher
	 */
	public CharacterMatcher()
	{
	}
	
	/**
	 * SingleCharacterMatcher
	 * 
	 * @param c
	 */
	public CharacterMatcher(char c)
	{
		this._character = c;
	}
	
	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#addChildTypes()
	 */
	public void addChildTypes()
	{
		this.addChildType(UseElement.class);
	}
	
	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#addFirstCharacters(com.aptana.ide.lexer.matcher.MatcherMap, com.aptana.ide.lexer.matcher.ITextMatcher)
	 */
	public void addFirstCharacters(MatcherMap map, ITextMatcher target)
	{
		if (this.getChildCount() > 0)
		{
			map.addUncategorizedMatcher(target);
		}
		else
		{
			if (this.getNegate())
			{
				map.addNegatedCharacterMatcher(this._character, target);
			}
			else
			{
				map.addCharacterMatcher(this._character, target);
			}
		}
	}

	/**
	 * @see com.aptana.xml.NodeBase#appendText(java.lang.String)
	 */
	public void appendText(String text)
	{
		super.appendText(text);
		
		if (text != null && text.length() > 0)
		{
			char c = text.charAt(0);
			
			if (c == '\\' && text.length() > 1)
			{
				char c2 = text.charAt(1);
				
				switch (c2)
				{
					case 'f':
						c = '\f';
						break;
						
					case 'n':
						c = '\n';
						break;
						
					case 'r':
						c = '\r';
						break;
						
					case 't':
						c = '\t';
						break;
						
					case 'v':
						c = '\u000B';
						break;
						
					default:
						c = c2;
						break;
				}
				
				this._character = c;
			}
			else
			{
				this._character = c;
			}
			
			this._characterDefined = true;
		}
	}

	/**
	 * getCharacter
	 *
	 * @return char
	 */
	public char getCharacter()
	{
		char result;
		
		if (this.getChildCount() > 0)
		{
			UseElement use = (UseElement) this.getChild(0);
			String text = use.getText();
			
			if (text.length() > 0)
			{
				result = text.charAt(0);
			}
			else
			{
				result = '\0';
			}
		}
		else
		{
			result = this._character;
		}
		
		return result;
	}
	
	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractCharacterMatcher#matchCharacter(char)
	 */
	protected boolean matchCharacter(char c)
	{
		return c == this.getCharacter();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		String result;
		
		switch (this._character)
		{
			case '\r':
				result = "\r"; //$NON-NLS-1$
				break;
				
			case '\n':
				result = "\n"; //$NON-NLS-1$
				break;
				
			case '\t':
				result = "\t"; //$NON-NLS-1$
				break;
				
			default:
				result = Character.toString(this._character);
				break;
		}
		
		return result;
	}

	/**
	 * @see com.aptana.ide.lexer.matcher.AbstractTextMatcher#validateLocal()
	 */
	protected void validateLocal()
	{
		super.validateLocal();
		
		if (this._characterDefined == false && this.getChildCount() == 0)
		{
			this.getDocument().sendError(Messages.CharacterMatcher_No_Text_Or_Use_Element, this);
		}
		else if (this.getChildCount() > 1)
		{
			this.getDocument().sendWarning(Messages.CharacterMatcher_Only_Recognize_First_Child, this);
		}
	}
}
