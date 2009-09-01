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
package com.aptana.ide.core;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * @author Kevin Lindsey
 */
public class StringUtils
{
	/**
	 * LINE_DELIMITER
	 */
	public static final String LINE_DELIMITER = System.getProperty("line.separator", "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * BULLET
	 */
	public static final String BULLET = "\u2022 "; //$NON-NLS-1$

	/**
	 * EMPTY
	 */
	public static final String EMPTY = ""; //$NON-NLS-1$

	/**
	 * TAB
	 */
	public static final String TAB = "\t"; //$NON-NLS-1$

	/**
	 * SPACE
	 */
	public static final String SPACE = " "; //$NON-NLS-1$

	/**
	 * EMPTY
	 */
	public static final String COLON = ":"; //$NON-NLS-1$

	/**
	 * Protected constructor for utility class.
	 */
	protected StringUtils()
	{

	}

	/**
	 * Create a string by concatenating the elements of a string array using a delimited between each item
	 * 
	 * @param delimiter
	 *            The text to place between each element in the array
	 * @param items
	 *            The array of items to join
	 * @return The resulting string
	 */
	public static String join(String delimiter, String[] items)
	{
		if (items == null)
		{
			return null;
		}

		int length = items.length;
		String result = StringUtils.EMPTY;

		if (length > 0)
		{
			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < length - 1; i++)
			{
				sb.append(items[i]).append(delimiter);
			}

			sb.append(items[length - 1]);

			result = sb.toString();
		}

		return result;
	}

	/**
	 * Strips HTML tags from text
	 * 
	 * @param text
	 *            Text to strip
	 * @return the text, minus any tags
	 */
	public static String stripHTML(String text)
	{
		if (text == null)
		{
			return null;
		}

		String tempText = text.replaceAll("<p>", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		return tempText.replaceAll("\\<.*?\\>", StringUtils.EMPTY); //$NON-NLS-1$
	}
	
	/**
	 * Returns an empty string in place of a null value
	 * 
	 * @param input
	 * @return String
	 * @since 3.0.2
	 */
	public static String replaceNullWithEmpty(String input)
	{
		if (input == null)
		{
			return StringUtils.EMPTY;
		}
		return input;
	}

	/**
	 * This method encodes the URL, removes the spaces from the URL and replaces the same with <code>"%20"</code>.
	 * This method is required to fix Bug 77840.
	 * 
	 * @param input
	 * @return String
	 * @since 3.0.2
	 */
	public static String urlEncodeForSpaces(String input)
	{

		if (input == null)
		{
			return null;
		}

		return urlEncodeForSpaces(input.toCharArray());
	}

	/**
	 * urlEncodeKeyValuePair
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static String urlEncodeKeyValuePair(String key, String value)
	{
		String result = null;
		
		try
		{
			result = java.net.URLEncoder.encode(key, "UTF-8") + "=" + java.net.URLEncoder.encode(value, "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		catch (UnsupportedEncodingException e)
		{
		}
		
		return result;
	}
	
	/**
	 * This method encodes the URL, removes the spaces from the URL and replaces the same with <code>"%20"</code>.
	 * This method is required to fix Bug 77840.
	 * 
	 * @param input
	 * @return String
	 * @since 3.0.2
	 */
	public static String urlEncodeForSpaces(char[] input)
	{

		if (input == null)
		{
			return null;
		}

		StringBuffer retu = new StringBuffer(input.length);
		for (int i = 0; i < input.length; i++)
		{
			if (input[i] == ' ')
			{
				retu.append("%20"); //$NON-NLS-1$
			}
			else
			{
				retu.append(input[i]);
			}
		}
		return retu.toString();
	}

	/**
	 * This method encodes the URL, removes the spaces and brackets from the URL and replaces the same with
	 * <code>"%20"</code> and <code>"%5B" and "%5D"</code> and <code>"%7B" "%7D"</code>.
	 * 
	 * @param input
	 * @return String
	 * @since 3.0.2
	 */
	public static String urlEncodeFilename(char[] input)
	{

		if (input == null)
		{
			return null;
		}

		StringBuffer retu = new StringBuffer(input.length);
		for (int i = 0; i < input.length; i++)
		{
			if (input[i] == ' ')
			{
				retu.append("%20"); //$NON-NLS-1$
			}
			else if (input[i] == '[')
			{
				retu.append("%5B"); //$NON-NLS-1$
			}
			else if (input[i] == ']')
			{
				retu.append("%5D"); //$NON-NLS-1$
			}
			else if (input[i] == '{')
			{
				retu.append("%7B"); //$NON-NLS-1$
			}
			else if (input[i] == '}')
			{
				retu.append("%7D"); //$NON-NLS-1$
			}
			else if (input[i] == '`')
			{
				retu.append("%60"); //$NON-NLS-1$
			}
			else if (input[i] == '+')
			{
				retu.append("%2B"); //$NON-NLS-1$
			}
			else
			{
				retu.append(input[i]);
			}
		}
		return retu.toString();
	}

	/**
	 * This method encodes the URL, removes the spaces and brackets from the URL and replaces the same with
	 * <code>"%20"</code> and <code>"%5B" and "%5D"</code> and <code>"%7B" "%7D"</code>.
	 * 
	 * @param input
	 * @return String
	 * @since 3.0.2
	 */
	public static String urlDecodeFilename(char[] input)
	{
		if (input == null)
		{
			return null;
		}
		
		StringBuffer retu = new StringBuffer(input.length);
		for (int i = 0; i < input.length; i++)
		{
			if(input[i] != '%' || i + 2 >= input.length)
			{
				retu.append(input[i]);
			}
			else
			{
				String test = new String(input, i, 3);
				if (test.equals("%20")) //$NON-NLS-1$
				{
					retu.append(" "); //$NON-NLS-1$
					i += 2;
				}
				else if (test.equals("%5B")) //$NON-NLS-1$
				{
					retu.append("["); //$NON-NLS-1$
					i += 2;
				}
				else if (test.equals("%5D")) //$NON-NLS-1$
				{
					retu.append("]"); //$NON-NLS-1$
					i += 2;
				}
				else if (test.equals("%7B")) //$NON-NLS-1$
				{
					retu.append("{"); //$NON-NLS-1$
					i += 2;
				}
				else if (test.equals("%7D")) //$NON-NLS-1$
				{
					retu.append("}"); //$NON-NLS-1$
					i += 2;
				}
				else if (test.equals("%60")) //$NON-NLS-1$
				{
					retu.append("`"); //$NON-NLS-1$
					i += 2;
				}
				else if (test.equals("%2B")) //$NON-NLS-1$
				{
					retu.append("+"); //$NON-NLS-1$
					i += 2;
				}
				else
				{
					retu.append(input[i]);
				}
			}
		}
		return retu.toString();
	}
	
	/**
	 * Removes all carriage returns from a string
	 * 
	 * @param text
	 *            The string to strip of '\n'
	 * @return The string minus the carriage returns
	 */
	public static String stripCarriageReturns(String text)
	{
		if (text == null)
		{
			return null;
		}

		return text.replaceAll("\n", StringUtils.EMPTY); //$NON-NLS-1$
	}

	/**
	 * Removes all extra whitespace (multiple spaces or tabs) from a string
	 * 
	 * @param text
	 *            The string to strip of '\n'
	 * @return The string minus the whitespace
	 */
	public static String stripWhitespace(String text)
	{
		if (text == null)
		{
			return null;
		}

		return text.replaceAll("\\s+", " "); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Removes the HTML, extra whitespace and carriage returns from a string
	 * 
	 * @param text
	 *            The text to strip
	 * @return The new text, reformatted
	 */
	public static String formatAsPlainText(String text)
	{
		String tempText = StringUtils.stripCarriageReturns(text);
		tempText = StringUtils.stripWhitespace(tempText);
		tempText = StringUtils.replace(tempText, "</li>", StringUtils.EMPTY); //$NON-NLS-1$
		tempText = StringUtils.replace(tempText, "<li>", LINE_DELIMITER + "\t" + BULLET); //$NON-NLS-1$ //$NON-NLS-2$
		tempText = StringUtils.replace(tempText, "<p>", LINE_DELIMITER); //$NON-NLS-1$
		tempText = StringUtils.stripHTML(tempText);
		return tempText.trim();
	}

	/**
	 * Removes any leading or trailing " or ' quotes on the string. This is necessary as attribute values show as
	 * strings from the lexer, and need to be surrounded by StringUtils.EMPTY
	 * 
	 * @param stringToTrim
	 *            The string to trim
	 * @return String
	 */
	public static String trimStringQuotes(String stringToTrim)
	{

		if (stringToTrim == null)
		{
			return null;
		}

		String trimmed = stringToTrim.trim();

		if (trimmed.startsWith("\"") || trimmed.startsWith("'")) //$NON-NLS-1$ //$NON-NLS-2$
		{
			trimmed = trimmed.substring(1);
		}

		if (trimmed.endsWith("\"") || trimmed.endsWith("'")) //$NON-NLS-1$ //$NON-NLS-2$
		{
			trimmed = trimmed.substring(0, trimmed.length() - 1);
		}

		return trimmed;
	}

	/**
	 * Removes any leading or trailing [] on the string.
	 * 
	 * @param stringToTrim
	 *            The string to trim
	 * @return String
	 */
	public static String trimBrackets(String stringToTrim)
	{

		if (stringToTrim == null)
		{
			return null;
		}

		String trimmed = stringToTrim.trim();

		if (trimmed.startsWith("[")) //$NON-NLS-1$
		{
			trimmed = trimmed.substring(1);
		}

		if (trimmed.endsWith("]")) //$NON-NLS-1$
		{
			trimmed = trimmed.substring(0, trimmed.length() - 1);
		}

		return trimmed;
	}
	
	/**
	 * Trims the start of the string of whitespace. This functions
	 * identically to String.trim(), but works only at the
	 * beginning of a string
	 * @param stringToTrim
	 * @return
	 */
	public static String trimStart(String stringToTrim)
	{
		if(stringToTrim == null)
		{
			return null;
		}
		
		char[] chars = stringToTrim.toCharArray();
		int index = 0;
		int length = chars.length;
		
		while(index < length && chars[index] <= ' ')
		{
			index++;
		}
		
		if(index > 0)
		{
			return stringToTrim.substring(index);
		}
		else
		{
			return stringToTrim;
		}
	}
	
	/**
	 * Trims the end of the string of whitespace. This functions
	 * identically to String.trim(), but works only at the
	 * end of a string
	 * @param stringToTrim
	 * @return
	 */
	public static String trimEnd(String stringToTrim)
	{
		if(stringToTrim == null)
		{
			return null;
		}
		
		char[] chars = stringToTrim.toCharArray();
		int index = chars.length;
		
		while(index > 0 && chars[index - 1] < ' ')
		{
			index--;
		}
		
		if(index > 0)
		{
			return stringToTrim.substring(0, index);
		}
		else
		{
			return stringToTrim;
		}
	}

	/**
	 * Replace one string with another
	 * 
	 * @param str
	 * @param pattern
	 * @param replace
	 * @return String
	 */
	public static String replace(String str, String pattern, String replace)
	{

		int s = 0;
		int e = 0;
		StringBuffer result = new StringBuffer();

		while ((e = str.indexOf(pattern, s)) >= 0)
		{
			result.append(str.substring(s, e));
			result.append(replace);
			s = e + pattern.length();
		}
		result.append(str.substring(s));
		return result.toString();

	}

	/**
	 * Formats the string with replacement values
	 * 
	 * @param str
	 * @param replacement
	 * @return String
	 */
	public static String format(String str, long replacement)
	{
		return MessageFormat.format(str, new Object[] { Long.toString(replacement) });
	}

	/**
	 * Formats the string with replacement values
	 * 
	 * @param str
	 * @param replacement
	 * @return String
	 */
	public static String format(String str, int replacement)
	{
		return MessageFormat.format(str, new Object[] { Integer.toString(replacement) });
	}

	/**
	 * Formats the string with replacement values
	 * 
	 * @param str
	 * @param replacement
	 * @return String
	 */
	public static String format(String str, String replacement)
	{
		return MessageFormat.format(str, new Object[] { replacement });
	}

	/**
	 * Formats the string with replacement values
	 * 
	 * @param str
	 * @param replacement
	 * @return String
	 */
	public static String format(String str, Object replacement)
	{
		return MessageFormat.format(str, new Object[] { replacement.toString() });
	}

	/**
	 * Formats the string with replacement values
	 * 
	 * @param str
	 * @param replacements
	 * @return String
	 */
	public static String format(String str, Object[] replacements)
	{
		return MessageFormat.format(str, replacements);
	}

	/**
	 * Adds an ellipsis to the end of a string, generally indicating that this string leads to another choice (like a
	 * dialog)
	 * 
	 * @param message
	 * @return The ellipsif-ied string
	 */
	public static String ellipsify(String message)
	{
		return message + CoreStrings.ELLIPSIS;
	}
	
	/**
	 * Adds a colon to the end of the string, as if making a form label
	 * 
	 * @param message
	 * @return string + colon
	 */
	public static String makeFormLabel(String message)
	{
		return message + COLON;
	}
	
	/**
	 * Converts a filename extension wildcard string to a regular expression (i.e. *.js into a regular expression matching all .js file names)
	 * @param wildcardExpression the expression to convert
	 * @param caseInsensitive do we make the expression case-insensitive
	 * @return The modified expression
	 */
	public static String convertWildcardExpressionToRegex(String wildcardExpression, boolean caseInsensitive)
	{
		if(wildcardExpression == null)
		{
			return null;
		}
		
		if(wildcardExpression.startsWith("/") && wildcardExpression.endsWith("/"))  //$NON-NLS-1$//$NON-NLS-2$
		{
			return wildcardExpression.substring(1, wildcardExpression.length() - 1);
		}
		
		String string = wildcardExpression.replaceAll("\\.(?=[^\\*])", "\\\\."); //$NON-NLS-1$//$NON-NLS-2$
		string = string.replaceAll("\\*", ".*"); //$NON-NLS-1$ //$NON-NLS-2$

		if(caseInsensitive)
		{
			string = "(?i)" + string; //$NON-NLS-1$
		}
		return string;
	}

	/**
	 * A string of spaces of a particular length
	 * @param length 
	 * 
	 * @return The string of spaces, equal to the passed length;
	 */
	public static String getSpaces(int length)
	{
		String defaultIndent = "                                        "; //$NON-NLS-1$
		String indentString = defaultIndent;
		if (length < indentString.length())
		{
			indentString = defaultIndent.substring(0, length);
		}
		return indentString;
	}
	
	/**
	 * Returns a very specialized array. An array of strings spaces of 
	 * decreasing length, starting with a string of length "length"
	 * @param length
	 * @return String[]
	 */
	public static String[] getArrayOfSpaces(int length)
	{		
		ArrayList prefixes = new ArrayList();
		for(int i = length; i > 0; i--)
		{
			prefixes.add(StringUtils.getSpaces(i));
		}
		return (String[])prefixes.toArray(new String[0]);
	}
	
	/**
	 * Find the first non-whitespace character
	 * @param text
	 * @return
	 */
	public static String findStartWhitespace(String text)
	{
		
		if(text == null || text.length() == 0)
		{
			return text;
		}
		
		String[] s = text.split("\\S"); //$NON-NLS-1$
		if(s.length > 0)
		{
			return s[0];
		}
		else
		{
			return ""; //$NON-NLS-1$
		}
	}

	/**
	 * Find the last non-whitespace character
	 * @param text
	 * @return
	 */
	public static String findEndWhitespace(String text)
	{
		if(text == null || text.length() == 0)
		{
			return text;
		}
		
		String trimmed = trimEnd(text);
		int textLength = text.length();
		if(trimmed.length() == textLength)
		{
			return ""; //$NON-NLS-1$
		}
		else
		{
			return text.substring(trimmed.length());
		}
	}
	
	/**
	 * Number of new lines in source text
	 * 
	 * @param text string
	 * @return - number of lines
	 */
	public static int getNumberOfNewlines(String text)
	{
		if(text == null)
		{
			return 0;
		}
		
		int count = 0;
		String sourceBit = StringUtils.replace(text, "\r\n", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		sourceBit = StringUtils.replace(sourceBit, "\r", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		char[] split = sourceBit.toCharArray();
		for (int i = 0; i < split.length; i++)
		{
			char c = split[i];
			if (c == '\n')
			{
				count++;
			}
		}

		return count;
	}
	
	/**
	 * Compare version strings
	 * @param left
	 * @param right
	 * @return positive if left > right, zero if left == right, negative otherwise
	 */
	public static int compareVersions(String left, String right) {
		int result;
		String[] lparts = left.split("\\."); //$NON-NLS-1$
		String[] rparts = right.split("\\."); //$NON-NLS-1$
		for( int i = 0; i < lparts.length && i < rparts.length; ++i) {
			result = lparts[i].compareToIgnoreCase(rparts[i]);
			if(result != 0) {
				return result;
			}
		}
		return (lparts.length - rparts.length);
	}

	/**
	 * Replaces security-sensitive information (e.g. password and credit card) with proper text.
	 * 
	 * @param object
	 * @return a string that hides security-sensitive information
	 */
	public static String getPublishableMessage(Object object)
	{
		if (object == null)
		{
			return "null"; //$NON-NLS-1$
		}
		String text = object.toString();
		text = text.replaceAll("password=.+,", "PASSWORD,"); //$NON-NLS-1$ //$NON-NLS-2$
		text = text.replaceAll("password=.+}", "PASSWORD}"); //$NON-NLS-1$ //$NON-NLS-2$
		text = text.replaceAll("<password.*>.+</password>", "<password>PASSWORD</password>"); //$NON-NLS-1$ //$NON-NLS-2$
		text = text.replaceAll("<app_password.*>.+<.*>", "<app_password>PASSWORD</app_password>"); //$NON-NLS-1$ //$NON-NLS-2$
		text = text.replaceAll("number=[0-9]+,", "number=XXXXXXXXXXXXXXXX"); //$NON-NLS-1$ //$NON-NLS-2$
		text = text.replaceAll("<number.*>[0-9]+<.*>", "<number>XXXXXXXXXXXXXXXX</number>"); //$NON-NLS-1$ //$NON-NLS-2$

		return text;
	}

}
