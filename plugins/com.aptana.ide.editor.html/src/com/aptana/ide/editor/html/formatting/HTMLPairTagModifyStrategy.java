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
package com.aptana.ide.editor.html.formatting;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.html.HTMLPairFinder;
import com.aptana.ide.editor.html.HTMLPlugin;
import com.aptana.ide.editor.html.lexing.HTMLTokenTypes;
import com.aptana.ide.editor.html.parsing.HTMLMimeType;
import com.aptana.ide.editor.html.parsing.HTMLParseState;
import com.aptana.ide.editor.html.parsing.HTMLUtils;
import com.aptana.ide.editor.html.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.IPreferenceClient;
import com.aptana.ide.editors.unified.PairMatch;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.parsing.IParseState;

/**
 * Auto-edit strategy responsible for modifying the pair tag.
 * @author Denis Denisenko
 */
public class HTMLPairTagModifyStrategy implements IAutoEditStrategy,
        IPreferenceClient
{
    
    /**
     * configuration
     */
    protected SourceViewerConfiguration configuration;

    /**
     * sourceViewer
     */
    protected ISourceViewer sourceViewer;

    /**
     * context
     */
    protected EditorFileContext context;
    
    /**
     * HTMLPairTagModifyStrategy
     * 
     * @param context - file context.
     * @param configuration - source viewer configuration.
     * @param sourceViewer - source viewer.
     */
    public HTMLPairTagModifyStrategy(EditorFileContext context, 
            SourceViewerConfiguration configuration, ISourceViewer sourceViewer)
    {
        this.context = context;
        this.configuration = configuration;
        this.sourceViewer = sourceViewer;
    }

    /**
     * {@inheritDoc}
     */
    public void customizeDocumentCommand(IDocument document,
            DocumentCommand command)
    {
        IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
        
        IParseState parseState = context.getParseState();
        HTMLParseState htmlParseState = (HTMLParseState) parseState.getParseState(HTMLMimeType.MimeType);
        
        //modifies pair tag
        modifyPairTag(document, command, htmlParseState, store);
    }

    /**
     * {@inheritDoc}
     */
    public IPreferenceStore getPreferenceStore()
    {
        return HTMLPlugin.getDefault().getPreferenceStore();
    }

    
    /**
     * Modifies pair tag if any.
     * @param document - document.
     * @param command - original document command.
     * @param parseState  - parse state.
     * @param store - preference store.
     */
    private void modifyPairTag(IDocument document, DocumentCommand command,
            IParseState parseState, IPreferenceStore store)
    {
        if (store == null || !store.getBoolean(IPreferenceConstants.AUTO_MODIFY_PAIR_TAG))
        {
            return;
        }
        
        Lexeme cursorLexeme = getTagLexeme(parseState, command.offset);
        if (cursorLexeme != null)
        {
            HTMLPairFinder finder = new HTMLPairFinder();
            PairMatch match = 
                finder.findPairMatch(command.offset, parseState, cursorLexeme, 2);
            
            if (match != null)
            {
                Lexeme pairLexeme;
                if (match.endStart > command.offset)
                {
                    pairLexeme = getTagLexeme(parseState, match.endStart);
                }
                else
                {
                    pairLexeme = getTagLexeme(parseState, match.beginStart);
                }

                //if pair lexeme is not found, match is invalid
                if (pairLexeme == null)
                {
                    return;
                }
                
                //checking the match
                if (!checkMatch(cursorLexeme, pairLexeme))
                {
                    return;
                }
                
                //checking if current change is full tag removal
                //if so we need to completely delete it's pair
                if(checkFullDelete(command, parseState.getLexemeList(), 
                        cursorLexeme, pairLexeme))
                {
                    return;
                }
                
                //checking if current change is going to destroy the tag.
                //such a change should not be propagated
                if(destroysTag(cursorLexeme, parseState.getLexemeList(),
                        command.offset, command.length))
                {
                    return;
                }
                
                //filtering replace length
                int replaceLength = filterReplaceLength(command, cursorLexeme);
                if (replaceLength == -1)
                {
                    return;
                }
                
                //filtering replace text
                String replaceText = filterReplaceText( command.text);
                if (replaceLength == 0 && replaceText.length() == 0)
                {
                    return;
                }
                
                try
                {
                    //saving caret position
                    int caretPosition = command.offset + command.length;
                    command.caretOffset = caretPosition;
                    command.shiftsCaret = true;
                    
                    int offsetDif = command.offset - cursorLexeme.offset;
                    
                    //checking if current match is upper or lower
                    if (match.endStart > command.offset)
                    {
                        //match is lower
                        command.addCommand(match.endStart + offsetDif + 1,
                                replaceLength, replaceText, command.owner);
                    }
                    else
                    {
                        //match is upper
                        command.addCommand(match.beginStart + offsetDif - 1,
                                replaceLength, replaceText, command.owner);
                    }
                
                    command.doit = false;
                } 
                catch (BadLocationException e)
                {
                    //can happen when user deletes both starting and closing tag simultaneously
                }
            }
        }
    }
    
    /**
     * Checks if current change is full tag removal and
     * modifies command to completely delete its pair if so.
     * @param command - document command.
     * @param lexemes - lexemes.
     * @param cursorLexeme - cursor lexeme.
     * @param pairLexeme - pair lexeme.
     * @return true if full removal, false otherwise.
     */
    private boolean checkFullDelete(DocumentCommand command,
            LexemeList lexemes, Lexeme cursorLexeme, Lexeme pairLexeme)
    {
        Lexeme cursorEndingLexeme = getTagEndingLexeme(cursorLexeme, lexemes);
        Lexeme pairEndingLexeme = getTagEndingLexeme(pairLexeme, lexemes);
        
        if (cursorEndingLexeme == null || pairEndingLexeme == null)
        {
            return false;
        }
        

        if (cursorLexeme.getStartingOffset() == command.offset
                && cursorEndingLexeme.getEndingOffset() == command.offset + command.length)
        {
            //removing the whole tag
            try
            {
                //saving caret position
                int caretPosition = command.offset + command.length;
                command.caretOffset = caretPosition;
                command.shiftsCaret = true;
                command.addCommand(pairLexeme.getStartingOffset(),
                        pairEndingLexeme.getEndingOffset() - pairLexeme.getStartingOffset(),
                        "", null); //$NON-NLS-1$
                command.doit = false;
                return true;
            } catch (BadLocationException e)
            {
                //should not happen
                IdeLog.logError(HTMLPlugin.getDefault(), 
                        Messages.HTMLPairTagModifyStrategy_ERR_TagRemoval, e);
            }
        }
        
        return false;
    }

    /**
     * Checks whether current change is going to destroy the tag.
     * @param startLexeme - tag start lexeme.
     * @param lexemes - lexemes.
     * @param offset - replace change offset.
     * @param length - replace change length.
     * @return true if destroys, false otherwise.
     */
    private boolean destroysTag(Lexeme startLexeme, LexemeList lexemes,
            int offset, int length)
    {
        Lexeme closing = getTagEndingLexeme(startLexeme, lexemes);
        
        //nothing to destroy, if closing lexeme (">") is not found.
        if (closing == null)
        {
            return false;
        }
        
        //if the replace change end is after the closing lexeme start, tag would be destroyed
        return offset + length > closing.getStartingOffset();
    }

    /**
     * Gest tag ending lexeme.
     * @param startLexeme - starting lexeme.
     * @param lexemes - lexemes.
     * @return tag ending lexeme, or null if not found.
     */
    private Lexeme getTagEndingLexeme(Lexeme startLexeme, LexemeList lexemes)
    {
        int startIndex = lexemes.getLexemeIndex(startLexeme);
        if ((startLexeme.typeIndex != HTMLTokenTypes.START_TAG &&
                startLexeme.typeIndex != HTMLTokenTypes.END_TAG) || startIndex < 0)
        {
            throw new IllegalArgumentException("Wrong start lexeme"); //$NON-NLS-1$
        }
        
        Lexeme closing = HTMLUtils.getFirstLexemeBreaking(lexemes, startIndex + 1, 
                new int[]{HTMLTokenTypes.GREATER_THAN},
                new int[]{HTMLTokenTypes.START_TAG,
                    HTMLTokenTypes.END_TAG,
                    HTMLTokenTypes.START_TAG,}
        );
        return closing;
    }

    /**
     * Checks if match is really correct.
     * 
     * @param firstLexeme - first lexeme in the match.
     * @param secondLexeme - second lexeme in the match.
     * 
     * @return true if match is correct, false otherwise
     */
    private boolean checkMatch(Lexeme firstLexeme, Lexeme secondLexeme)
    {
        int firstTagStart = getTagNameStartOffset(firstLexeme);
        if (firstTagStart == -1)
        {
            return false;
        }
        
        int secondTagStart = getTagNameStartOffset(secondLexeme);
        if (secondTagStart == -1)
        {
            return false;
        }
        
        String firstName = 
            firstLexeme.getText().substring(firstTagStart - firstLexeme.getStartingOffset());
        
        String secondName = 
            secondLexeme.getText().substring(secondTagStart - secondLexeme.getStartingOffset());
        
        return firstName.equalsIgnoreCase(secondName);
            
    }

    /**
     * Filters replace length to only allow the part that replaces the tag lexeme.
     * 
     * @param originalCommand - original command.
     * @param tagLexeme - tag lexeme.
     * 
     * @return allowed replace length, or -1 if replace is denied. 
     */
    private int filterReplaceLength(DocumentCommand originalCommand, Lexeme tagLexeme)
    {
        //checking command replace start. 
        
        //Replacing "<" and "</" symbols is not allowed. 
        int tagNameStartOffset = getTagNameStartOffset(tagLexeme);
        if (originalCommand.offset < tagNameStartOffset)
        {
            return -1;
        }
        
        //starting the replace after the tag start node is also denied
        if (originalCommand.offset > tagLexeme.getEndingOffset())
        {
            return -1;
        }
        
        //truncating replace length 
        int maxLength = tagLexeme.getEndingOffset() - originalCommand.offset;
        int resultLength = Math.min(originalCommand.length, maxLength);
        return resultLength;
    }
    
    private String filterReplaceText(String originalText)
    {
        //taking the allowed symbols till meeting the denied one
        int pos = 0;
        for(; pos < originalText.length(); pos++)
        {
            int ch = originalText.charAt(pos);
            if (!Character.isLetterOrDigit(ch))
            {
                break;
            }
        }
        
        return originalText.substring(0, pos);
    }
    
    /**
     * Get tag name start offset.
     * 
     * @param tagLexeme - tag lexeme.
     * 
     * @return name start offset.
     */
    private int getTagNameStartOffset(Lexeme tagLexeme)
    {
        if (tagLexeme.typeIndex == HTMLTokenTypes.START_TAG)
        {
            return tagLexeme.getStartingOffset() + 1;
        }
        else if (tagLexeme.typeIndex == HTMLTokenTypes.END_TAG)
        {
            return tagLexeme.getStartingOffset() + 2;
        }
        else
        {
            throw new IllegalArgumentException("Tag starting lexeme is excpected"); //$NON-NLS-1$
        }
    }

    /**
     * Gets tag start or tag end lexeme, if found 
     * @param state - parse state.
     * @param offset - offset to search in, or before.
     * @return lexeme, or null if not found
     */
    private Lexeme getTagLexeme(IParseState state, int offset)
    {
        LexemeList lexemeList = state.getLexemeList();
        
        Lexeme currentLexeme = lexemeList.getFloorLexeme(offset);
        if (currentLexeme != null && (currentLexeme.typeIndex == HTMLTokenTypes.START_TAG 
                || currentLexeme.typeIndex == HTMLTokenTypes.END_TAG))
        {
            return currentLexeme;
        }
        
        currentLexeme = lexemeList.getCeilingLexeme(offset);
        if (currentLexeme == null)
        {
            return null;
        }
        
        //checking current lexeme
        if (currentLexeme.typeIndex == HTMLTokenTypes.START_TAG 
                        || currentLexeme.typeIndex == HTMLTokenTypes.END_TAG)
        {
            return currentLexeme;
        }
        
        int currentLexemeIndex = lexemeList.getLexemeIndex(currentLexeme);
        if (currentLexemeIndex <= 0)
        {
            return null;
        }
        
        Lexeme previousLexeme = lexemeList.get(currentLexemeIndex - 1);
        
        if ((previousLexeme.typeIndex == HTMLTokenTypes.START_TAG 
                || previousLexeme.typeIndex == HTMLTokenTypes.END_TAG)
                && currentLexeme.typeIndex == HTMLTokenTypes.GREATER_THAN)
        {
            return previousLexeme;
        }
        
        return null;
    }
}
