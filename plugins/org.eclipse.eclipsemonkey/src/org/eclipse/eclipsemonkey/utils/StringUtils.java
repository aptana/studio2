/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.eclipse.eclipsemonkey.utils;


import java.text.MessageFormat;

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
		return MessageFormat.format(str, new Object[] { new Long(replacement) });
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
		return MessageFormat.format(str, new Object[] { new Integer(replacement) });
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
		return message + "..."; //$NON-NLS-1$
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

}

