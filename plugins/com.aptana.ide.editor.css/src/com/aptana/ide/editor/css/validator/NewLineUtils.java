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
package com.aptana.ide.editor.css.validator;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.css.CSSPlugin;

/**
 * Utility class that is used to match lines to offsets and backwards
 * @author Denis Denisenko
 */
public class NewLineUtils
{
    /**
     * Offsets of every line.
     */
    private Map<Integer, Integer> lineOffsets = new LinkedHashMap<Integer, Integer>();
    
    /**
     * Text.
     */
    private String text;

    /**
     * NewLineUtils constructor.
     * @param text - text to create use.
     */
    public NewLineUtils(String text) {
        this.text = text;
        lineOffsets.put(new Integer(0), new Integer(0));
        StringReader reader = new StringReader(text);
        
        int currentChar;
        int currentOffset = 0;
        int lineNumber = 1;
        try
        {
            while((currentChar = reader.read()) != -1)
            {
                switch (currentChar)
                {
                    case '\r':
                        reader.mark(1);
                        int nextChar = reader.read();
                        currentOffset++;
                        if (nextChar != '\n')
                        {
                            reader.reset();
                            currentOffset--;
                        }
                    case '\n':
                        if (currentOffset + 1 < text.length())
                        {
                            lineOffsets.put(new Integer(lineNumber), 
                                    new Integer(currentOffset + 1));
                        }
                        lineNumber++;
                        break;
                    default:
                        break;
                }
                
                currentOffset++;
            }
        } 
        catch (IOException e)
        {
            //impossible
            IdeLog.logError(CSSPlugin.getDefault(), 
                    "Exception while creating New Line Utils", e); //$NON-NLS-1$
        }
    }
    
    /**
     * Gets offset of the line.
     * @param lineNumber - line number.
     * @return line offset, or -1 if such line does not exist.
     */
    public int getLineOffset(int lineNumber)
    {
        Integer offset = lineOffsets.get(new Integer(lineNumber));
        if (offset == null)
        {
            return -1;
        }
        
        return offset.intValue();
    }
    
    /**
     * Gets the offset (from the beginning of the line) of the first non-whitespace character.
     * @param lineNumber - line number.
     * @return offset, or -1 if no line exist, or no non-whitespace character exist on that line.
     */
    public int getFirstNonWhitespaceCharacterOffset(int lineNumber)
    {
        int startOffset = getLineOffset(lineNumber);
        if (startOffset == -1) 
        {
            return -1;
        }
        
        int endOffset = getLineOffset(lineNumber + 1);
        if (endOffset == -1)
        {
            endOffset = text.length();
        }
        
        for (int i = startOffset; i < endOffset; i++)
        {
            int ch = text.charAt(i);
            if (!Character.isWhitespace(ch))
            {
                return i - startOffset;
            }
        }
        
        return -1;
    }
    
    public int getLineOfOffset(int offset)
    {
        if (lineOffsets.size() == 0)
        {
            return 0;
        }
        
        //TODO implement binary search here
        Collection<Entry<Integer, Integer>> values = lineOffsets.entrySet();
        Iterator<Entry<Integer, Integer>> it = values.iterator();
        
        Entry<Integer, Integer> entry;
        int lineNumber = 0;
        Integer currentOffset;
        while(it.hasNext())
        {
            entry = it.next();
            currentOffset = entry.getValue();
            if (currentOffset.intValue() > offset)
            {
                return lineNumber -1;
            }
            lineNumber++;
        }
        
        return lineNumber - 1;
    }
}
