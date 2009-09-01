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
package com.aptana.ide.editor.css.formatting;

// import org.eclipse.jface.text.Assert;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.aptana.ide.editor.css.CSSFileLanguageService;
import com.aptana.ide.editor.css.CSSPlugin;
import com.aptana.ide.editor.css.lexing.CSSTokenTypes;
import com.aptana.ide.editor.css.parsing.CSSMimeType;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.UnifiedAutoIndentStrategy;
import com.aptana.ide.editors.unified.UnifiedConfiguration;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;

/**
 * Handles the indenting of lines in the CSS editor
 * 
 * @author Ingo Muschenetz
 */
public class CSSAutoIndentStrategy extends UnifiedAutoIndentStrategy
{
	private String linePrefix;

	/**
	 * CSSAutoIndentStrategy
	 * 
	 * @param context
	 * @param configuration
	 * @param sourceViewer
	 */
	public CSSAutoIndentStrategy(EditorFileContext context, SourceViewerConfiguration configuration,
			ISourceViewer sourceViewer)
	{
		super(context, configuration, sourceViewer);
	}

	/**
	 * @see org.eclipse.jface.text.IAutoEditStrategy#customizeDocumentCommand(org.eclipse.jface.text.IDocument,
	 *      org.eclipse.jface.text.DocumentCommand)
	 */
	public void customizeDocumentCommand(IDocument document, DocumentCommand command)
	{
		if (command.text == null || command.length > 0)
		{
			return;
		}

		if (UnifiedConfiguration.isNewlineString(command.text))
		{
			if (isIndentComment(document,command)){
				indentCommentAfterNewLine(document, command);
			}
			else if (!indentAfterOpenBrace(document, command))
			{
				super.customizeDocumentCommand(document, command);
			}			
		}
		else
		{
			super.customizeDocumentCommand(document, command);
		}

	}

	private boolean isIndentComment(IDocument document, DocumentCommand command) {
		LexemeList lexemeList = getLexemeList();
		int lexemeFloorIndex = lexemeList.getLexemeFloorIndex(command.offset);
		if (lexemeFloorIndex==-1){
			return false;
		}
		Lexeme lexemeFromOffset = lexemeList.get(lexemeFloorIndex);
		if (lexemeFromOffset==null){
			return false;
		}
		int typeIndex = lexemeFromOffset.getToken().getTypeIndex();
		return typeIndex==CSSTokenTypes.COMMENT;
	}

	/**
	 * @see UnifiedAutoIndentStrategy#getPreferenceStore()
	 */
	public IPreferenceStore getPreferenceStore()
	{
		return CSSPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedAutoIndentStrategy#getLexemeList()
	 */
	
	protected LexemeList getLexemeList()
	{
		CSSFileLanguageService ls = (CSSFileLanguageService) context.getLanguageService(CSSMimeType.MimeType);

		return ls.getFileContext().getLexemeList();
	}
	
	/**
	 * getAutoInsertCharacters
	 * 
	 * @return char[]
	 */
	protected char[] getAutoInsertCharacters()
	{
		return new char[] { };
	}
	
	
	/**
	 * indentCloseToken
	 * 
	 * @param doc
	 * @param c
	 * @param offset
	 * @param lineOffset
	 * @param firstNonWS
	 * @return boolean
	 */
	protected boolean indentCloseToken(IDocument doc, DocumentCommand c, int offset, int lineOffset, int firstNonWS)
	{
		boolean isClose = false;
		if (doc.getLength() < 2 || offset < 2)
		{
			isClose = true;
		}
		else
		{
			try
			{
				if (doc.getChar(offset - 1) == '/' && doc.getChar(offset - 2) == '*')
				{
					isClose = true;
				}
			}
			catch (BadLocationException e)
			{
			}
		}

		if (isClose)
		{
			String append = getIndentationString(doc, lineOffset, firstNonWS);
			// multiline comments indent with "space *" after the first, so trim that if it is there
			if (append.endsWith(" ")) //$NON-NLS-1$
			{
				append = append.substring(0, append.length() - 1);
			}
			c.text += append;
			return true;
		}

		return false;
	}
	
	/**
	 * Copies the indentation of the previous line and adds a star. If the javadoc just started on
	 * this line add standard method tags and close the javadoc.
	 * 
	 * @param d
	 *            the document to work on
	 * @param c
	 *            the command to deal with
	 */
	protected void indentCommentAfterNewLine(IDocument d, DocumentCommand c)
	{

		int offset = c.offset;
		if (offset == -1 || d.getLength() == 0)
		{
			return;
		}

		try
		{
			int p = (offset == d.getLength() ? offset - 1 : offset);
			IRegion line = d.getLineInformationOfOffset(p);

			int lineOffset = line.getOffset();
			int firstNonWS = findEndOfWhiteSpace(d, lineOffset, offset);

			// find out if this is a return after a */ (in which case only add an indent, not a *)
			if (indentCloseToken(d, c, offset, lineOffset, firstNonWS))
			{
				return;
			}

			// find out if this is a // style single line comment
			if ((d.getLength() > firstNonWS + 1) && (d.getChar(firstNonWS + 1) == '/'))
			{
				super.indentAfterNewLine(d, c);
				return;
			}
			// get line prefix
			IPreferenceStore store = CSSPlugin.getDefault().getPreferenceStore();
			boolean useStar = true;
			if (store != null)
			{
				useStar = true;//store.getBoolean(IPreferenceConstants.PREFERENCE_COMMENT_INDENT_USE_STAR);
			}
			linePrefix = useStar ? "* " : " "; //$NON-NLS-1$ //$NON-NLS-2$
			StringBuffer buf = new StringBuffer(c.text);
			IRegion prefix = findPrefixRange(d, line);
			String indentation = d.get(prefix.getOffset(), prefix.getLength());
			// String indentation = getIndentationString(d, lineOffset, firstNonWS);
			// if(indentation == "")
			// return;

			int lengthToAdd = Math.min(offset - prefix.getOffset(), prefix.getLength());

			buf.append(indentation.substring(0, lengthToAdd));

			if (firstNonWS < offset)
			{
				if (d.getChar(firstNonWS) == '/')
				{
					// javadoc started on this line
					buf.append(" " + linePrefix); //$NON-NLS-1$

					if (isNewComment(d, offset))
					{
						c.shiftsCaret = false;
						c.caretOffset = c.offset + buf.length();
						String lineDelimiter = TextUtilities.getDefaultLineDelimiter(d);

						String endTag = lineDelimiter + indentation + " */"; //$NON-NLS-1$

						// guard for end of doc (multiline comment at very end of doc
						if (d.getLength() > firstNonWS + 2 && d.getChar(firstNonWS + 1) == '*')
						{
							// we need to close the comment
							d.replace(offset, 0, endTag);
						}
						else
						{
							buf.append(endTag);
						}
					}

				}
			}

			// move the caret behind the prefix, even if we do not have to insert it.
			if (lengthToAdd < prefix.getLength())
			{
				c.caretOffset = offset + prefix.getLength() - lengthToAdd;
			}
			c.text = buf.toString();

		}
		catch (BadLocationException excp)
		{
			// stop work
		}
	}
	
	/**
	 * Guesses if the command operates within a newly created javadoc comment or not. If in doubt,
	 * it will assume that the javadoc is new.
	 * 
	 * @param document
	 *            the document
	 * @param commandOffset
	 *            the command offset
	 * @return <code>true</code> if the comment should be closed, <code>false</code> if not
	 */
	protected boolean isNewComment(IDocument document, int commandOffset)
	{

		try
		{
			// Lexeme lx = lexemeList.get( lexemeList.getLexemeCeilingIndex(commandOffset) );
			// if( lx.getLanguage().equals(ScriptDocMimeType.MimeType) )
			// return false;

			int lineIndex = document.getLineOfOffset(commandOffset) + 1;
			if (lineIndex >= document.getNumberOfLines())
			{
				return true;
			}

			IRegion line = document.getLineInformation(lineIndex);

			ITypedRegion partition = TextUtilities.getPartition(document, UnifiedConfiguration.UNIFIED_PARTITIONING,
					commandOffset, false);
			int partitionEnd = partition.getOffset() + partition.getLength() - 1; // partitions
			// have overlaps
			// in eclipse
			if (line.getOffset() >= partitionEnd)
			{
				return true;
			}

			String comment =  this.getLexemeList().getLexemeFromOffset(commandOffset).getText();
			// comments that don't end with */ are certainly not closed
			if (!comment.endsWith("*/")) //$NON-NLS-1$
			{
				return true; 
			}

			int firstNewline = comment.indexOf('\n');

			// assume short comment always unclosed and guard for next test
			if (comment.length() < 4)
			{
				return true;
			}
			if (comment.startsWith("/**/")) //$NON-NLS-1$
			{
				return false; 
			}
			
			//This doesn't work because comment is actually pulling up to the next "*/"
//			/*If the comment line startsWith and endWith the appropriate comments */
//			if( comment.startsWith("/*") && comment.endsWith("*/")){
//				return false;
//			}


			if ( firstNewline > -1 && firstNewline <= comment.length() ){
				// comments that have * as the first non ws char on next line are probably closed
				String subComment = comment.substring(firstNewline).trim();
				if (subComment.startsWith("*")) //$NON-NLS-1$
				{
					return false;
				}

				// no extra lines means probably not closed (can be a */ line due to previous test)
				if (subComment.indexOf("\n") == -1) //$NON-NLS-1$
				{
					return true;
				}
			}

			
			if (comment.indexOf("/*", 2) != -1) //$NON-NLS-1$
			{
				return true; // enclosed another comment -> probably a new comment
			}

			return false;

		}
		catch (BadLocationException e)
		{
			return false;
		}
	}
	
	/**
	 * Returns the range of the multiline comment prefix on the given line in <code>document</code>.
	 * The prefix greedily matches the following regex pattern: <code>\w*\*\w*</code>, that is,
	 * any number of whitespace characters, followed by an asterix ('*'), followed by any number of
	 * whitespace characters.
	 * 
	 * @param document
	 *            the document to which <code>line</code> refers
	 * @param line
	 *            the line from which to extract the prefix range
	 * @return an <code>IRegion</code> describing the range of the prefix on the given line
	 * @throws BadLocationException
	 *             if accessing the document fails
	 */
	protected IRegion findPrefixRange(IDocument document, IRegion line) throws BadLocationException
	{
		int lineOffset = line.getOffset();
		int lineEnd = lineOffset + line.getLength();
		int indentEnd = findEndOfWhiteSpace(document, lineOffset, lineEnd);
		if (indentEnd < lineEnd && document.get(indentEnd, linePrefix.length()).equals(linePrefix))
		{
			indentEnd++;
			while (indentEnd < lineEnd && document.getChar(indentEnd) == ' ')
			{
				indentEnd++;
			}
		}
		return new Region(lineOffset, indentEnd - lineOffset);
	}
	
}
