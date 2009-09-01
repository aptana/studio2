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
package com.aptana.ide.logging.coloring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.lexer.ILexer;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.logging.LoggingPlugin;
import com.aptana.ide.logging.LoggingPreferences;

/**
 * Lexeme manager that manages file lexemes.
 * @author Denis Denisenko
 */
public class LoggingLexemeManager
{

    /**
     * Lexemes.
     */
    private Map<Integer, List<Lexeme>> lexemes;
    
    /**
     * Document.
     */
    private final IDocument document;
    
    /**
     * Max number of regexp lines.
     */
    private final int regexpMaxLines;
    
    /**
     * Top lines.
     */
    private List<String> topLines = new ArrayList<String>();
    
    
    
    /**
     * LoggingLexemeManager constructor.
     * @param document - document.
     * @param preferences - logging preferences.
     */
    public LoggingLexemeManager(IDocument document,
            LoggingPreferences preferences)
    {
        lexemes = new HashMap<Integer, List<Lexeme>>();
        regexpMaxLines = preferences.getRegexpMaxLines();
        this.document = document;
    }
    
    public Lexeme[] getLexemes(int lineNumber)
    {
        List<Lexeme> cached = lexemes.get(lineNumber);
        if (cached == null)
        {
            synchronized (lexemes)
            {
                try
                {
                    prefetch(lineNumber);
                } catch (BadLocationException e)
                {
                    IdeLog.logError(LoggingPlugin.getDefault(), Messages.LoggingLexemeManager_ERR_Exception, e);
                    return new Lexeme[]{};
                }
                cached = lexemes.get(lineNumber);
                if (cached == null)
                {
                    return new Lexeme[]{};
                }
            }
        }
        
        Lexeme[] result = new Lexeme[cached.size()];
        cached.toArray(result);
        return result;
    }

    /**
     * Make a prefetch for current line and several nearby lines
     * @param lineNumber - line number.
     * @throws BadLocationException 
     */
    private void prefetch(int lineNumber) throws BadLocationException
    {
        //getting first line to start lexing from
        int lineToStart = lineNumber - regexpMaxLines + 1;
        if (lineToStart < -topLines.size())
        {
            lineToStart = 0;
        }
        
        //checking for cached lexemes.
        for (int i = lineToStart; i <= lineNumber; i++)
        {
            List<Lexeme> lineLexemes = lexemes.get(i);
            if (lineLexemes == null)
            {
                lineToStart = i;
                break;
            }
        }
        
        String toParse = buildContentToParse(lineToStart, lineNumber);
        clearLines(lineToStart, lineNumber);

        try
        {
            getLexer().setLanguage(TokenTypes.LANGUAGE);
        } catch (LexerException e1)
        {
            IdeLog.logError(LoggingPlugin.getDefault(), Messages.LoggingLexemeManager_ERR_Exception, e1);
        }
        getLexer().setSource(toParse);
        while(true)
        {
            Lexeme lexeme = getLexer().getNextLexeme();
            if (lexeme == null)
            {
                break;
            }
            
            addLexeme(lexeme, lineToStart);
        }
    }

    /**
     * Gets lexer.
     * @return lexer
     */
    private ILexer getLexer()
    {
        return TokenTypes.getLexerFactory().getLexer();
    }

    /**
     * Clears lines cache.
     * @param startLine - start line.
     * @param endLine - end line.
     */
    private void clearLines(int startLine, int endLine)
    {
        for (int i = startLine; i <= endLine; i++)
        {
            lexemes.put(i, null);
        }
    }

    /**
     * Builds content to parse.
     * @param firstLine - first line to parse.
     * @param lastLine - last line to parse.
     * @return built content.
     * @throws BadLocationException 
     */
    private String buildContentToParse(int firstLine, int lastLine) throws BadLocationException
    {
        int startLine = firstLine;
        StringBuilder builder = new StringBuilder();
        
        if (startLine < 0)
        {
            for (int i = startLine; i <= 0; i++)
            {
                builder.append(topLines.get(i + topLines.size()));
            }
            startLine = 0;
        }
        
        for (int lineNumber = startLine; lineNumber <= lastLine; lineNumber++)
        {
            int lineOffset = document.getLineOffset(lineNumber);
            int lineLength = document.getLineLength(lineNumber);
            builder.append(document.get(lineOffset, lineLength));
        }
        
        return builder.toString();
    }

    /**
      * {@inheritDoc}
      */
    public void dataAvailable(List<String> topLines)
    {
        synchronized (lexemes)
        {
            lexemes.clear();
            fillTopLines(topLines);
        }
    }
    
    /**
     * Clears lexeme cache.
     */
    public void clearCache()
    {
        lexemes.clear();
    }
 
    /**
     * Fills toplines info.
     * @param topLines - new toplines.
     */
    private void fillTopLines(List<String> topLines)
    {
        this.topLines.clear();
        this.topLines.addAll(topLines);
    }
    
    /**
     * Registers cached lexeme. 
     * @param lexeme - lexeme to register
     * @param baseLine - line, lexeme offset is counted beginning from. 
     * @throws BadLocationException 
     */
    private void addLexeme(Lexeme lexeme, int baseLine) throws BadLocationException
    {
        int effectiveLineOffset = 0;
        if (baseLine < 0)
        {
            effectiveLineOffset = getTopLineStartOffset(baseLine);
        }
        else
        {
            effectiveLineOffset = document.getLineOffset(baseLine);
        }
        
        lexeme.adjustOffset(effectiveLineOffset);
        
        int lexemeStartLine = getLineByOffset(lexeme.getStartingOffset());
        List<Lexeme> lst = getModifiableLineLexemes(lexemeStartLine);
        lst.add(lexeme);
        
        int lexemeEndLine = getLineByOffset(lexeme.getEndingOffset());
        if (lexemeEndLine != lexemeStartLine)
        {
            if (lexemeEndLine == lexemeStartLine + 1 && lexemeEndsWithNewLine(lexeme.getText()))
            {
                return;
            }
            lst = getModifiableLineLexemes(lexemeEndLine);
            lst.add(lexeme);
        }
    }
    
    /**
     * Checks whether lexeme ends with new line.
     * @param text - lexeme text.
     * @return true if ends with new line, false otherwsie.
     */
    private boolean lexemeEndsWithNewLine(String text)
    {
        return text.endsWith("\r") || text.endsWith("\n") || text.endsWith("\r\n");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    /**
     * Gets topline offset. 
     * @param line - line.
     * @return topline offset
     */
    private int getTopLineStartOffset(int line)
    {
        int result = 0;
        for (int i = line; i < 0; i++)
        {
            result += topLines.get(i).length();
        }
        
        return result;
    }

    private int getLineByOffset(int offset) throws BadLocationException
    {
        return document.getLineOfOffset(offset);
    }
    
    /**
     * Gets modifiable lexemes list for line.
     * @param lineNumber - line number.
     * @return list
     */
    private List<Lexeme> getModifiableLineLexemes(int lineNumber)
    {
        List<Lexeme> toReturn = lexemes.get(lineNumber);
        if (toReturn == null)
        {
            toReturn = new ArrayList<Lexeme>(1);
            lexemes.put(lineNumber, toReturn);
        }
        
        return toReturn;
    }
}
