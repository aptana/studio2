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
package com.aptana.ide.editor.xml.formatting;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Stack;

import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.ide.editor.html.preferences.IPreferenceConstants;
import com.aptana.ide.editor.xml.lexing.XMLTokenTypes;
import com.aptana.ide.editor.xml.parsing.XMLParseState;
import com.aptana.ide.editors.unified.ParentOffsetMapper;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;

/**
 * XML formatting utils.
 * 
 * @author Denis Denisenko
 */
public final class XMLUtils
{

	/** is the tag "open" */
	public static final int TAG_OPEN = 1;

	/** is the tag "closed" */
	public static final int TAG_CLOSED = 2;

	/** is the tag "self-closed" */
	public static final int TAG_SELF_CLOSED = 4;

	/**
	 * Finds first lexeme in the list starting from the index specified. Found lexeme should be of the one of
	 * acceptedTypes type. Also the search breaks when meeting any lexeme the belongs to deniedTypes.
	 * 
	 * @param lexemeList -
	 *            lexemes.
	 * @param index -
	 *            index to start from.
	 * @param acceptedTypes -
	 *            accepted lexeme types.
	 * @param deniedTypes -
	 *            types that the search breaks on.
	 * @return found lexeme, or null if not found.
	 */
	public static Lexeme getFirstLexemeBreaking(LexemeList lexemeList, int index, int[] acceptedTypes, int[] deniedTypes)
	{
		if (index >= lexemeList.size())
		{
			return null;
		}

		for (int i = index; i < lexemeList.size(); i++)
		{
			Lexeme currentLexeme = lexemeList.get(i);

			// using row check is faster then creating hash sets here
			// usually these type arrays are just in cpu cache

			for (int j = 0; j < deniedTypes.length; j++)
			{
				if (currentLexeme.typeIndex == deniedTypes[j])
				{
					return null;
				}
			}

			for (int j = 0; j < acceptedTypes.length; j++)
			{
				if (currentLexeme.typeIndex == acceptedTypes[j])
				{
					return currentLexeme;
				}
			}
		}

		return null;
	}

	/**
	 * Finds first lexeme in the list starting from the index specified. Found lexeme should be of the one of
	 * acceptedTypes type. Also the search breaks when meeting any lexeme that does not belong to typesToSkip.
	 * 
	 * @param lexemeList -
	 *            lexemes.
	 * @param index -
	 *            index to start from.
	 * @param acceptedTypes -
	 *            accepted lexeme types.
	 * @param typesToSkip -
	 *            types that should be skipped.
	 * @return found lexeme, or null if not found.
	 */
	public static Lexeme getFirstLexemeSkipping(LexemeList lexemeList, int index, int[] acceptedTypes, int[] typesToSkip)
	{
		if (index >= lexemeList.size())
		{
			return null;
		}

		Main: for (int i = index; i < lexemeList.size(); i++)
		{
			Lexeme currentLexeme = lexemeList.get(i);

			// using row check is faster then creating hash sets here
			// usually these type arrays are just in cpu cache
			for (int j = 0; j < acceptedTypes.length; j++)
			{
				if (currentLexeme.typeIndex == acceptedTypes[j])
				{
					return currentLexeme;
				}
			}

			for (int j = 0; j < typesToSkip.length; j++)
			{
				if (currentLexeme.typeIndex == typesToSkip[j])
				{
					continue Main;
				}
			}

			return null;
		}

		return null;
	}

	/**
	 * Returns true if we are somewhere inside the opening tag definition i.e. &lt;tagname | &gt;
	 * 
	 * @param offset -
	 *            offset to check.
	 * @param lexemeList -
	 *            lexemes
	 * @return true if inside, false otherwise.
	 */
	public static boolean insideOpenTag(int offset, LexemeList lexemeList)
	{
		Lexeme startLexeme = getTagOpenLexeme(offset, lexemeList);

		return (startLexeme != null);

	}

	/**
	 * Get tag open lexeme.
	 * 
	 * @param offset -
	 *            offset to check.
	 * @param lexemeList -
	 *            lexemes.
	 * @return tag open lexeme, or null if not found.
	 */
	public static Lexeme getTagOpenLexeme(int offset, LexemeList lexemeList)
	{
		int index = ParentOffsetMapper.getLexemeIndexFromDocumentOffset(offset, lexemeList);
		if (index > -1)
		{
			Lexeme l = lexemeList.get(index);
			return getTagOpenLexeme(l, lexemeList);
		}
		else
		{
			return null;
		}
	}

	/**
	 * Gets tag start lexeme.
	 * 
	 * @param lexeme -
	 *            lexeme to start search from.
	 * @param lexemeList -
	 *            lexemes
	 * @return tag start lexeme, or null if not found.
	 */
	public static Lexeme getTagOpenLexeme(Lexeme lexeme, LexemeList lexemeList)
	{
		Lexeme startTag = null;

		int position = lexemeList.getLexemeIndex(lexeme);

		// backtrack over lexemes to find name - we are really just
		// searching for the last OPEN_ELEMENT
		while (position >= 0)
		{
			Lexeme curLexeme = lexemeList.get(position);

			if (curLexeme.typeIndex == XMLTokenTypes.END_TAG)
			{
				break;
			}

			if (curLexeme.typeIndex == XMLTokenTypes.SLASH_GREATER_THAN)
			{
				break;
			}

			if (curLexeme.typeIndex == XMLTokenTypes.GREATER_THAN)
			{
				break;
			}

			if (curLexeme.typeIndex == XMLTokenTypes.START_TAG)
			{
				startTag = curLexeme;
				break;
			}
			position--;
		}

		return startTag;
	}

	/**
	 * XMLUtils constructor.
	 */
	private XMLUtils()
	{
	}

	/**
	 * getTagCloseLexeme
	 * 
	 * @param offset
	 * @param lexemeList
	 * @return Lexeme
	 */
	public static Lexeme getTagCloseLexeme(int offset, LexemeList lexemeList)
	{
		Lexeme l = lexemeList.getCeilingLexeme(offset);
		return getTagCloseLexeme(l, lexemeList);
	}

	/**
	 * getTagCloseLexeme
	 * 
	 * @param startLexeme
	 * @param lexemeList
	 * @return Lexeme
	 */
	public static Lexeme getTagCloseLexeme(Lexeme startLexeme, LexemeList lexemeList)
	{
		return getNextLexemeOfType(startLexeme, new int[] { XMLTokenTypes.GREATER_THAN,
				XMLTokenTypes.SLASH_GREATER_THAN }, lexemeList);
	}

	/**
	 * getNextLexemeOfType
	 * 
	 * @param startLexeme
	 * @param lexemeTypes
	 * @param lexemeList
	 * @return Lexeme
	 */
	public static Lexeme getNextLexemeOfType(Lexeme startLexeme, int[] lexemeTypes, LexemeList lexemeList)
	{
		return getNextLexemeOfType(startLexeme, lexemeTypes, new int[0], lexemeList);
	}

	/**
	 * getNextLexemeOfType
	 * 
	 * @param startLexeme
	 * @param lexemeTypes
	 * @param lexemeTypesToBail
	 * @param lexemeList
	 * @return Lexeme
	 */
	public static Lexeme getNextLexemeOfType(Lexeme startLexeme, int[] lexemeTypes, int[] lexemeTypesToBail,
			LexemeList lexemeList)
	{
		Arrays.sort(lexemeTypes);
		Arrays.sort(lexemeTypesToBail);

		int index = lexemeList.getLexemeIndex(startLexeme);

		for (int i = index; i < lexemeList.size(); i++)
		{
			Lexeme l = lexemeList.get(i);

			if (Arrays.binarySearch(lexemeTypes, l.typeIndex) >= 0)
			{
				return l;
			}

			if (Arrays.binarySearch(lexemeTypesToBail, l.typeIndex) >= 0)
			{
				return null;
			}

		}

		return null;
	}

	/**
	 * Is the current tag "closed", i.e. it has an greater than at the end of it?
	 * 
	 * @param tag
	 * @param lexemeList
	 * @return isTagClosed
	 */
	public static int isTagClosed(Lexeme tag, LexemeList lexemeList)
	{
		// if(!isStartTag(tag) && isEndTag(tag))
		// return false;

		int position = lexemeList.getLexemeIndex(tag) + 1;
		if (position < 0)
		{
			return TAG_OPEN;
		}

		// move forward over lexemes to find the closing tag element
		while (position < lexemeList.size())
		{

			Lexeme curLexeme = lexemeList.get(position);

			// if it's an end tag or a start tag, no
			if (curLexeme.typeIndex == XMLTokenTypes.START_TAG || curLexeme.typeIndex == XMLTokenTypes.END_TAG)
			{
				return TAG_OPEN;
			}

			// if it's a slash greater than, we've self closed, so yes
			if (curLexeme.typeIndex == XMLTokenTypes.SLASH_GREATER_THAN)
			{
				return TAG_SELF_CLOSED;
			}

			if (curLexeme.typeIndex == XMLTokenTypes.GREATER_THAN)
			{
				return TAG_CLOSED;
			}

			position++;
		}

		return TAG_OPEN;
	}

	/**
	 * isStartTag
	 * 
	 * @param lexeme
	 * @return boolean
	 */
	public static boolean isStartTag(Lexeme lexeme)
	{
		return (lexeme.typeIndex == XMLTokenTypes.START_TAG || (lexeme.typeIndex == XMLTokenTypes.ERROR && lexeme
				.getText().equals("<"))); //$NON-NLS-1$
	}

	/**
	 * isEndTag
	 * 
	 * @param lexeme
	 * @return boolean
	 */
	public static boolean isEndTag(Lexeme lexeme)
	{
		return (lexeme.typeIndex == XMLTokenTypes.END_TAG || (lexeme.typeIndex == XMLTokenTypes.ERROR && lexeme
				.getText().equals("</"))); //$NON-NLS-1$
	}

	/**
	 * Gets the open name of the tag in question, but trimmed such that we only create the name from the part _before_
	 * the offset (assuming the offset is inside the lexeme)
	 * 
	 * @param tag
	 *            The name of the tag
	 * @param offset
	 * @return A string with the tag name encased in "</>"
	 */
	public static String getOpenTagName(Lexeme tag, int offset)
	{
		String lexemeText = tag.getText();

		if (tag.containsOffset(offset) && offset > tag.offset)
		{
			lexemeText = lexemeText.substring(0, offset - tag.offset);
		}

		String tempName = stripTagEndings(lexemeText);
		return tempName;
	}

	/**
	 * Removes the "<" and "</" from the beginning and end of a tag
	 * 
	 * @param tag
	 *            The tag text to strip
	 * @return A string with the item removed
	 */
	public static String stripTagEndings(String tag)
	{
		String tempName = tag.replaceAll("</", ""); //$NON-NLS-1$ //$NON-NLS-2$
		tempName = tempName.replaceAll(">", ""); //$NON-NLS-1$ //$NON-NLS-2$
		return tempName.replaceAll("<", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Creates a open tag for the tag name
	 * 
	 * @param tagName
	 *            The name of the tag
	 * @param close
	 * @return A string with the tag name encased in "<>"
	 */
	public static String createOpenTag(String tagName, boolean close)
	{
		String temp = "<" + stripTagEndings(tagName); //$NON-NLS-1$
		if (close)
		{
			return temp + ">"; //$NON-NLS-1$
		}
		else
		{
			return temp;
		}
	}

	/**
	 * Creates a open tag for the tag name taht self-closes
	 * 
	 * @param tagName
	 *            The name of the tag
	 * @return A string with the tag name encased in "< />"
	 */
	public static String createSelfClosedTag(String tagName)
	{
		String temp = "<" + stripTagEndings(tagName); //$NON-NLS-1$
		return temp + " />"; //$NON-NLS-1$
	}

	/**
	 * Creates a close tag for the tag name
	 * 
	 * @param tag
	 *            The opening lexeme
	 * @param close
	 * @return A string with the tag name encased in "</>"
	 */
	public static String createCloseTag(Lexeme tag, boolean close)
	{
		if (tag.typeIndex == XMLTokenTypes.START_TAG)
		{
			return createCloseTag(tag.getText().substring(1), close);
		}
		else
		{
			return null;
		}
	}

	/**
	 * Creates a close tag for the tag name
	 * 
	 * @param tagName
	 *            The name of the tag
	 * @param close
	 * @return A string with the tag name encased in "</>"
	 */
	public static String createCloseTag(String tagName, boolean close)
	{
		String temp = "</" + stripTagEndings(tagName); //$NON-NLS-1$
		if (close)
		{
			return temp + ">"; //$NON-NLS-1$
		}
		else
		{
			return temp;
		}

	}

	/**
	 * Creates a close tag for the tag name, but trimmed such that we only create the close tag from the part _before_
	 * the offset (assuming the offset is inside the lexeme)
	 * 
	 * @param tag
	 *            The name of the tag
	 * @param offset
	 * @param close
	 * @return A string with the tag name encased in "</>"
	 */
	public static String createCloseTag(Lexeme tag, int offset, boolean close)
	{
		String tempName = getOpenTagName(tag, offset);
		return createCloseTag(tempName, close);
	}

	/**
	 * Surrounds the item with quotes according to the current preferences
	 * 
	 * @param store
	 * @param value
	 * @return String
	 */
	public static String quoteAttributeValue(IPreferenceStore store, String value)
	{
		String quoteChar = ""; //$NON-NLS-1$		
		return quoteChar + value + quoteChar;
	}

	/**
	 * Do we insert an equals sign?
	 * 
	 * @param store
	 * @return String
	 */
	public static boolean insertEquals(IPreferenceStore store)
	{
		if (store != null)
		{
			return store.getBoolean(IPreferenceConstants.HTMLEDITOR_INSERT_EQUALS);
		}
		else
		{
			return false;
		}
	}

	/**
	 * Given an opening and a closing lexeme of an HTML open tag declaration it will grab all of the attributes inside
	 * as a hashtable
	 * 
	 * @param openTagLexeme
	 * @param closeTagLexeme
	 * @param lexemeList
	 * @return Hashtable
	 */
	public static Hashtable gatherAttributes(Lexeme openTagLexeme, Lexeme closeTagLexeme, LexemeList lexemeList)
	{
		Hashtable<String, String> h = new Hashtable<String, String>();
		if (openTagLexeme == null || closeTagLexeme == null)
		{
			return h;
		}

		int startIndex = lexemeList.getLexemeIndex(openTagLexeme);
		int endIndex = lexemeList.size() - 1;
		if (closeTagLexeme != null)
		{
			endIndex = lexemeList.getLexemeIndex(closeTagLexeme);
		}

		if (startIndex > endIndex)
		{
			throw new IndexOutOfBoundsException(""); //$NON-NLS-1$
		}

		String currentName = null;
		boolean foundEquals = false;
		boolean foundQuote = false;
		while (startIndex < endIndex)
		{
			startIndex++;
			Lexeme l = lexemeList.get(startIndex);
			if (l.typeIndex == XMLTokenTypes.NAME && !foundEquals)
			{
				currentName = l.getText();
				continue;
			}

			if (l.typeIndex == XMLTokenTypes.EQUAL)
			{
				foundEquals = true;
				continue;
			}

			else if (l.typeIndex == XMLTokenTypes.STRING || (foundEquals && l.typeIndex == XMLTokenTypes.NAME))
			{
				foundEquals = false;
				if (currentName != null && !h.containsKey(currentName))
				{
					h.put(currentName, l.getText());
				}
			}

			else if (l.typeIndex == XMLTokenTypes.STRING)
			{
				if (foundQuote == true)
				{
					foundQuote = false;
					foundEquals = false;
					if (currentName != null && !h.containsKey(currentName))
					{
						h.put(currentName, ""); //$NON-NLS-1$
					}
				}
				else
				{
					foundQuote = true;
				}
			}
		}

		return h;
	}

	/**
	 * Are we inside a lexeme where it is "quoted"
	 * 
	 * @param lexeme
	 *            The string text
	 * @param offset
	 *            The current cursor offset
	 * @return Yes if true, false if not
	 */
	public static boolean insideQuotedString(Lexeme lexeme, int offset)
	{
		String text = lexeme.getText();

		if ((text.startsWith("\"") || text.startsWith("'")) && lexeme.containsOffset(offset)) //$NON-NLS-1$ //$NON-NLS-2$
		{
			return true;
		}

		return false;
	}

	/**
	 * getPreviousUnclosedTag
	 * 
	 * @param lexeme
	 * @param lexemeList
	 * @param parseState
	 * @return Lexeme
	 */
	public static Lexeme getPreviousUnclosedTag(Lexeme lexeme, LexemeList lexemeList, XMLParseState parseState)
	{
		if (lexeme == null)
		{
			return null;
		}

		int position = lexemeList.getLexemeIndex(lexeme) - 1;

		Lexeme startTag = null;
		boolean selfClosed = false;

		Stack<Lexeme> tags = new Stack<Lexeme>();

		// backtrack over lexemes to find name - we are really just
		// searching for the last OPEN_ELEMENT
		while (position >= 0)
		{
			Lexeme curLexeme = lexemeList.get(position);

			if (curLexeme.typeIndex == XMLTokenTypes.END_TAG)
			{
				tags.push(curLexeme);
			}

			if (curLexeme.typeIndex == XMLTokenTypes.SLASH_GREATER_THAN)
			{
				selfClosed = true;
			}

			if (curLexeme.typeIndex == XMLTokenTypes.START_TAG)
			{
				// if the item was self-closed, just continue on
				if (selfClosed)
				{
					selfClosed = false;
					position--;
					continue;
				}

				if (tags.size() == 0)
				{

					return curLexeme;

				}

				Lexeme l = tags.pop();
				if (l.typeIndex == XMLTokenTypes.SLASH_GREATER_THAN)
				{
					position--;
					continue;
				}

				position--;
				continue;

			}
			position--;
		}

		return startTag;
	}

	/**
	 * Is the current start tag "balanced" by a later closing tag?
	 * 
	 * @param tag -
	 *            lexeme of the start tag to check
	 * @param lexemeList -
	 *            list of lexemes
	 * @param parseState -
	 *            parse state
	 * @return true if tag is balanced
	 */
	public static boolean isStartTagBalanced(Lexeme tag, LexemeList lexemeList, XMLParseState parseState)
	{
		// If we are the last lexeme in the list, there is no way we can be balanced.
		int index = lexemeList.getLexemeIndex(tag);
		if (index == lexemeList.size() - 1)
		{
			return false;
		}

		if (tag == null || lexemeList == null || parseState == null)
		{
			throw new IllegalArgumentException("null arguments are not accepted"); //$NON-NLS-1$
		}

		if (!isStartTag(tag))
		{
			// maybe IllegalArgumentException would be better @Denis
			return false;
		}

		// treating self-closed tags as always balanced
		if (isTagSelfClosed(tag, lexemeList))
		{
			return true;
		}

		String originalTagName = stripTagEndings(tag.getText());

		// tags that are able closing themselves are always balanced

		int balance = 0;

		// current state: whether we're tracking tag close or not
		boolean trackingClose = false;
		for (int i = 0; i < lexemeList.size(); i++)
		{
			Lexeme currentLexeme = lexemeList.get(i);

			if (isStartTag(currentLexeme))
			{
				// checking tag name
				String currenTagName = stripTagEndings(currentLexeme.getText());
				if (originalTagName.equals(currenTagName))
				{
					// entering trackingClose state
					trackingClose = true;
				}
			}
			else if (isEndTag(currentLexeme))
			{
				// if we met the end tag, we should always escape "trackingClose" state
				if (trackingClose)
				{
					balance++;
					trackingClose = false;
				}

				// checking tag name
				String currenTagName = stripTagEndings(currentLexeme.getText());
				if (originalTagName.equals(currenTagName))
				{
					balance--;
				}
			}
			// if we met some other lexeme and should track the close of the opened tag...
			else if (trackingClose)
			{
				// if it's an end tag or a start tag, no
				if (currentLexeme.typeIndex == XMLTokenTypes.START_TAG
						|| currentLexeme.typeIndex == XMLTokenTypes.END_TAG)
				{
					balance++;
					trackingClose = false;
				}
				// if it's a slash greater than, we've self closed, so yes
				else if (currentLexeme.typeIndex == XMLTokenTypes.SLASH_GREATER_THAN)
				{
					trackingClose = false;
				}
				else if (currentLexeme.typeIndex == XMLTokenTypes.GREATER_THAN)
				{
					balance++;
					trackingClose = false;
				}
			}

			// if (isStartTag(currentLexeme) && isTagClosed(currentLexeme, lexemeList) != TAG_SELF_CLOSED) {
			// String currenTagName = stripTagEndings(currentLexeme.getText());
			// if (originalTagName.equals(currenTagName))
			// {
			// balance++;
			// }
			// }
			// else if (isEndTag(currentLexeme))
			// {
			// String currenTagName = stripTagEndings(currentLexeme.getText());
			// if (originalTagName.equals(currenTagName))
			// {
			// balance--;
			// }
			// }
		}

		return balance <= 0;
	}

	/**
	 * Checks whether tag is self-closed
	 * 
	 * @param tag -
	 *            tag to check
	 * @param lexemeList -
	 *            list of lexemes
	 * @return whether tag is self-closed
	 */
	public static boolean isTagSelfClosed(Lexeme tag, LexemeList lexemeList)
	{
		if (!isStartTag(tag))
		{
			return false;
		}

		int position = lexemeList.getLexemeIndex(tag) + 1;
		if (position < 0)
		{
			return false;
		}

		// move forward over lexemes to find name - we are really just
		// searching for the next END_TAG
		while (position < lexemeList.size())
		{
			Lexeme curLexeme = lexemeList.get(position);

			// if it's a slash greater than, we've self closed
			if (curLexeme.typeIndex == XMLTokenTypes.SLASH_GREATER_THAN)
			{
				return true;
			}

			// If it's an end tag, we haven't
			if (curLexeme.typeIndex == XMLTokenTypes.END_TAG)
			{
				return false;
			}

			// Tag close sign found, we haven't
			if (curLexeme.typeIndex == XMLTokenTypes.GREATER_THAN)
			{
				return false;
			}

			// if it's a start tag, we haven't
			if (curLexeme.typeIndex == XMLTokenTypes.START_TAG)
			{
				return false;
			}
			position++;
		}

		return false;
	}
}
