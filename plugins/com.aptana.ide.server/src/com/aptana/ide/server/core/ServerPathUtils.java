/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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
package com.aptana.ide.server.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Server path utils.
 * Parameters with spaces and/or slashes inside must be double-quoted.
 * @author Denis Denisenko
 */
public final class ServerPathUtils
{
	/**
	 * Gets file name by path with parameters.
	 * @param path - path.
	 * @return file name or null.
	 */
	public static String getFileNameByPathWithParameters(String path)
	{
		if (startsWithQuote(path))
		{
			String[] parts = parseCommand(path);
			if (parts == null || parts.length == 0)
			{
				return null;
			}
			
			return parts[1];
		}
		else
		{
			int fileNameEndPos = getFileEndPosition(path);
			if (fileNameEndPos == -1)
			{
				return path;
			}
			return path.substring(0, fileNameEndPos);
		}
	}
	
	/**
	 * Gets parameters.
	 * @param path - path.
	 * @return parameters
	 */
	public static String[] getParameters(String path)
	{
		if (startsWithQuote(path))
		{
			String[] parts = parseCommand(path);
			if (parts == null || parts.length == 0)
			{
				return new String[0];
			}
			
			String[] toReturn = new String[parts.length - 1];
			for (int i = 1; i < parts.length; i++)
			{
				toReturn[i-1] = parts[i];
			}
			
			return toReturn;
			//return Arrays.copyOfRange(parts, 1, parts.length);
		}
		else
		{
			int fileNameEndPos = getFileEndPosition(path);
			if (fileNameEndPos == -1)
			{
				return new String[0];
			}
			
			String parametersSubString = path.substring(fileNameEndPos, path.length());
			return parseCommand(parametersSubString);
		}
	}

	/**
	 * Gets file name end position.
	 * @param path - path.
	 * @return file name end position.
	 */
	private static int getFileEndPosition(String path)
	{
		String subPath = path;
		int firstQuotePos = path.indexOf('"');
		if (firstQuotePos != -1)
		{
			subPath = path.substring(0, firstQuotePos);
		}
		
		int lastSlashPos = subPath.lastIndexOf('/');
		int lastBackSlashPos = subPath.lastIndexOf('\\');
		
		int lastSeparator = 0;
		if (lastSlashPos > lastBackSlashPos)
		{
			lastSeparator = lastSlashPos;
		}
		else
		{
			lastSeparator = lastBackSlashPos;
		}
		
		if (lastSeparator <= 0)
		{
			return -1;
		}
		
		int fileNameEndPos = -1;
		for (int pos = lastSeparator; pos < path.length(); pos++)
		{
			if (Character.isWhitespace(path.charAt(pos)))
			{
				fileNameEndPos = pos;
			}
		}
		
		return fileNameEndPos;
	}
	
	/**
	 * Parses command.
	 * @param command - command to parse.
	 * @return parsed command.
	 */
	private static String[] parseCommand(String command)
	{
		List<String> result = new ArrayList<String>();
		
		char previousChar = 0;
		char ch = 0;
		
		StringBuilder currentPart = new StringBuilder();
		boolean insideQuotes = false;
		
		for (int i = 0; i < command.length(); i++)
		{
			previousChar = ch;
			ch = command.charAt(i);
			
			if (insideQuotes)
			{
				if (ch == '\'' && previousChar != '\\')
				{
					//adding current part
					result.add(currentPart.toString());
					
					//clearing current part buffer
					currentPart.replace(0, currentPart.length(), ""); //$NON-NLS-1$
				
					insideQuotes = false;
				}
				else
				{
					currentPart.append(ch);
				}
			}
			else
			{
				if (Character.isWhitespace(ch))
				{
					if (currentPart.length() != 0)
					{
						//adding current part
						result.add(currentPart.toString());
						
						//clearing current part buffer
						currentPart.replace(0, currentPart.length(), ""); //$NON-NLS-1$
					}
				}
				else if (ch == '\'' && previousChar != '\\')
				{
					insideQuotes = true;
				}
				else
				{
					currentPart.append(ch);
				}
			}
		}
		
		//adding current part if not empty
		if (currentPart.length() != 0)
		{
			//adding current part
			result.add(currentPart.toString());
		}
		
		String[] toReturn = new String[result.size()];
		return result.toArray(toReturn);
	}
	
	/**
	 * Gets whether string starts with quote.
	 * @return true if starts with quote, false otherwise. 
	 */
	static boolean startsWithQuote(String str)
	{
		if (str.length() == 0)
		{
			return false;
		}
		
		return str.trim().charAt(0) == '"';
	}
}
