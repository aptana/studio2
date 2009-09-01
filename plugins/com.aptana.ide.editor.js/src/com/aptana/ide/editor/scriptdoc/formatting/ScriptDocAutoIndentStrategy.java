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
package com.aptana.ide.editor.scriptdoc.formatting;

// import org.eclipse.jface.text.Assert;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editor.js.context.JSFunctionInfo;
import com.aptana.ide.editor.js.context.JSLexemeUtils;
import com.aptana.ide.editor.js.lexing.JSTokenTypes;
import com.aptana.ide.editor.js.preferences.IPreferenceConstants;
import com.aptana.ide.editor.jscomment.formatting.JSCommentAutoIndentStrategy;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;

/**
 * 
 */
public class ScriptDocAutoIndentStrategy extends JSCommentAutoIndentStrategy
{
	/**
	 * ScriptDocAutoIndentStrategy
	 * 
	 * @param context
	 * @param configuration
	 * @param sourceViewer
	 */
	public ScriptDocAutoIndentStrategy(EditorFileContext context, SourceViewerConfiguration configuration,
			ISourceViewer sourceViewer)
	{
		super(context, configuration, sourceViewer);
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
	protected void indentAfterNewLine(IDocument d, DocumentCommand c)
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

			// get line prefix
			IPreferenceStore store = JSPlugin.getDefault().getPreferenceStore();
			boolean useStar = true;
			if (store != null)
			{
				useStar = store.getBoolean(IPreferenceConstants.PREFERENCE_COMMENT_INDENT_USE_STAR);
			}
			linePrefix = useStar ? "* " : " "; //$NON-NLS-1$ //$NON-NLS-2$

			StringBuffer buf = new StringBuffer(c.text);
			IRegion prefix = findPrefixRange(d, line);
			String indentation = d.get(prefix.getOffset(), prefix.getLength());
			int lengthToAdd = Math.min(offset - prefix.getOffset(), prefix.getLength());

			buf.append(indentation.substring(0, lengthToAdd));

			if (firstNonWS < offset)
			{
				if (d.getChar(firstNonWS) == '/')
				{
					// javadoc started on this line
					buf.append(" " + linePrefix); //$NON-NLS-1$

					if (isNewComment(d, offset))
					// TODO: isPreferenceTrue(PreferenceConstants.EDITOR_CLOSE_JAVADOCS) && )
					{
						c.shiftsCaret = false;
						c.caretOffset = c.offset + buf.length();
						String lineDelimiter = TextUtilities.getDefaultLineDelimiter(d);

						String endTag = lineDelimiter + indentation + " */"; //$NON-NLS-1$

						// guard for end of doc (multiline comment at very end of doc
						if (d.getLength() > firstNonWS + 2 && d.getChar(firstNonWS + 1) == '*'
								&& d.getChar(firstNonWS + 2) == '*') // TODO:
																		// isPreferenceTrue(PreferenceConstants.EDITOR_ADD_JAVADOC_TAGS))
						{
							// we need to close the comment before computing
							// the correct tags in order to get the method
							d.replace(offset, 0, endTag);

							// evaluate method signature
							String string = createJSDocTags(offset, d, c, indentation, lineDelimiter);

							if (string != null)
							{
								buf.append(string);
							}
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
	 * Creates the Javadoc tags for newly inserted comments.
	 * 
	 * @param offset
	 *            the offset into the document where we're editing
	 * @param document
	 *            the document
	 * @param command
	 *            the command
	 * @param indentation
	 *            the base indentation to use
	 * @param lineDelimiter
	 *            the line delimiter to use
	 * @return the tags to add to the document
	 */
	private String createJSDocTags(int offset, IDocument document, DocumentCommand command, String indentation,
			String lineDelimiter)
	{
		LexemeList lexemeList = context.getLexemeList();
		if (lexemeList == null)
		{
			return null;
		}

		// todo: need to at least make a freindly method in lexemelist to not return a negative
		// number etc.
		int start = lexemeList.getLexemeIndex(offset);
		if (start < -1)
		{
			start = Math.abs(start + 1); // this binary search can return a negative number for
		}
											// spaces
		start++; // use the following lexeme

		if (start != -1) // still can be negative 1
		{
			boolean allowVar = true;
			// look a maximum of 20 lexemes ahead (ws and newlines are not included)
			int maxLookahead = Math.min(lexemeList.size(), start + 25);
			for (int lexc = start; lexc < maxLookahead; lexc++)
			{
				Lexeme lexeme = lexemeList.get(lexc);

				if (// lexeme.typeIndex == JSTokenTypes.VAR || // var fn = function is valid here,
					// so only allow one
				lexeme.typeIndex == JSTokenTypes.RCURLY || lexeme.typeIndex == JSTokenTypes.SEMICOLON
						|| lexeme.typeIndex == JSTokenTypes.MULTILINE_COMMENT
						|| lexeme.typeIndex == JSTokenTypes.DOCUMENTATION)
				{
					return null;
				}
				else if (lexeme.typeIndex == JSTokenTypes.VAR)
				{
					if (allowVar)
					{
						allowVar = false;
					}
					else
					{
						return null;
					}
				}
				else if (lexeme.typeIndex == JSTokenTypes.FUNCTION)
				{
					JSLexemeUtils utils = new JSLexemeUtils(lexemeList);
					JSFunctionInfo fi = utils.getFunctionInfo(lexc);
					if (fi == null)
					{
						return null;
					}
					String params = fi.params.substring(1, fi.params.length() - 1);
					String[] args = params.trim().split(","); //$NON-NLS-1$

					if (args.length == 1 && args[0].length() == 0)
					{
						return null;
					}

					String result = ""; //$NON-NLS-1$

					for (int i = 0; i < args.length; i++)
					{
						if (i == 0)
						{
							result += lineDelimiter;
						}
						result += indentation + " * @param {Object} " + args[i]; //$NON-NLS-1$
						if (i < args.length - 1)
						{
							result += lineDelimiter;
						}
					}

					return result;
				}
			}
		}

		return null;
	}

}
