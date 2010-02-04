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
package com.aptana.ide.editor.js.formatting;

// import org.eclipse.jface.text.Assert;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.js.JSFileLanguageService;
import com.aptana.ide.editor.js.JSPairFinder;
import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editor.js.lexing.JSTokenTypes;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.js.parsing.JSParseState;
import com.aptana.ide.editor.js.parsing.nodes.JSParseNodeTypes;
import com.aptana.ide.editor.js.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.editors.unified.PairMatch;
import com.aptana.ide.editors.unified.UnifiedAutoIndentStrategy;
import com.aptana.ide.editors.unified.UnifiedConfiguration;
import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.parsing.IParser;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * A class for auto-insertion of items into JavaScript
 * 
 * @author Ingo Muschenetz
 */
public class JSAutoIndentStrategy extends UnifiedAutoIndentStrategy
{

	/**
	 * Creates a new instance of the JSAutoEditStrategy
	 * 
	 * @param context
	 * @param configuration
	 * @param sourceViewer
	 */
	public JSAutoIndentStrategy(EditorFileContext context, SourceViewerConfiguration configuration,
			ISourceViewer sourceViewer)
	{
		super(context, configuration, sourceViewer);
	}

	/**
	 * customizeDocumentCommand
	 * 
	 * @param document
	 * @param command
	 */
	public void customizeDocumentCommand(IDocument document, DocumentCommand command)
	{
		if (command.text == null || command.length > 0)
		{
			return;
		}
		String[] lineDelimiters = document.getLegalLineDelimiters();
		int index = TextUtilities.endsWith(lineDelimiters, command.text);
		if (index > -1)
		{
			// ends with line delimiter
			if (lineDelimiters[index].equals(command.text))
			{
				indentAfterNewLine(document, command);
			}
			return;
		}
		else if (command.text.equals("\t")) //$NON-NLS-1$
		{
			if (configuration instanceof UnifiedConfiguration)
			{
				UnifiedConfiguration uc = (UnifiedConfiguration) configuration;
				if (uc.useSpacesAsTabs())
				{
					command.text = uc.getTabAsSpaces();
				}
			}
		}
		else if (command.text.equals("}")) { //$NON-NLS-1$
			try
			{

				if (JSPlugin.getDefault().getPreferenceStore().getBoolean(
						IPreferenceConstants.AUTO_FORMAT_ON_CLOSE_CURLY))
				{
					LexemeList ll = getLexemeList();
					Lexeme lexeme = ll.getFloorLexeme(command.offset);

					if (lexeme != null && lexeme.getToken().getTypeIndex() != JSTokenTypes.STRING)
					{
						String unit = document.get();
						JSPairFinder finder = new JSPairFinder();
						unit = unit.substring(0, command.offset) + "}" //$NON-NLS-1$
								+ unit.substring(command.offset);

						IParser parser = LanguageRegistry.getParser(JSMimeType.MimeType);
						JSParseState createParseState = (JSParseState) parser.createParseState(null);
						JSCodeFormatter formatter = new JSCodeFormatter();
						createParseState.setEditState(unit, unit, 0, 0);

						try
						{
							parser.parse(createParseState);
						}
						catch (LexerException e1)
						{
							return;
						}

						PairMatch findPairMatch = finder.findPairMatch(command.offset, createParseState);
						if (findPairMatch == null)
						{
							return;
						}

						IParseNode ast = parser.parse(createParseState);

						final IParseNode formattedNode = findAstNode(ast, command.offset + 1);
						if (formattedNode != null)
						{
							IParseNode parentNode = formattedNode; // .getParent();
							// while (parentNode.getParent() != null
							// && (isOnSameLine(parentNode.getParent(), parentNode, unit) || isGoodNodeType(parentNode
							// .getParent())))
							// {
							// parentNode = parentNode.getParent();
							// }

							if (parentNode != null)
							{

								int emNodeEnd = parentNode.getEndingOffset();
								int emNodeStart = parentNode.getStartingOffset();
								if (emNodeStart >= emNodeEnd)
								{
									return;
								}
								String nodeContent = unit.substring(emNodeStart, emNodeEnd);
								IdeLog.logInfo(JSPlugin.getDefault(),
										"nodeContent: +++++++++++++++++++++++++++++++++\r\n" + nodeContent + ":"); //$NON-NLS-1$ //$NON-NLS-2$

								// Old style formatting based on string
								// nodeContent=unit;
								// JSParseState parseState = (JSParseState) parser.createParseState(null);
								// parseState.setEditState(nodeContent, nodeContent, 0, 0);

								JSParseState parseState = (JSParseState) createParseState
										.getParseState(JSMimeType.MimeType);
								formatter.addMarkedNode(formattedNode, new IFormattingCallback()
								{

									public void nodeIsBeingFormatted(IParseNode node, JSCodeFormatter formatter)
									{
										// no-op
									}

								});

								String lineSeparator = ""; //$NON-NLS-1$

								if (document instanceof IDocumentExtension4)
								{
									IDocumentExtension4 ext = (IDocumentExtension4) document;
									lineSeparator = ext.getDefaultLineDelimiter();
								}
								try
								{
									lineSeparator = document.getLineDelimiter(0);
								}
								catch (BadLocationException e1)
								{
									// silently ignore
								}

								IProject project = getProject();

								LexemeList lexList = getLexemes(parseState.getLexemeList(), emNodeStart, emNodeEnd);
								IParseNode[] comments = getComments(parseState, emNodeStart, emNodeEnd);

								JSCodeFormatterOptions options = new JSCodeFormatterOptions(null, project);
								// have to pass in original content, as offsets are based on that
								int indentation = getIndentationLevelAtOffset(document, emNodeStart);
								formatter.configureWriter(indentation, getIndentString(), options.tabSize);
								String formatted = formatter.format(unit, nodeContent, parentNode, lexList, comments,
										options, lineSeparator);

								// if no formatting, then don't worry about the replace
								if (formatted.equals(nodeContent))
								{
									return;
								}

								// old style formatting based on strings
								// String formatted = formatter.format(nodeContent, false, null, project,
								// lineSeparator);
								IdeLog.logInfo(JSPlugin.getDefault(),
										"Formatted: +++++++++++++++++++++++++++++++++\r\n" + formatted + ":"); //$NON-NLS-1$ //$NON-NLS-2$

								// String indented = indent(indentation, formatted, lineSeparator);
								String indented = formatted;
								indented = StringUtils.trimStart(indented);
								IdeLog.logInfo(JSPlugin.getDefault(), "Indented: +++++++++++++++++++++++++++++++++\r\n" //$NON-NLS-1$
										+ indented + ":"); //$NON-NLS-1$

								// trim last carriage return on formatted text of original doesn't have it;
								if (!nodeContent.endsWith("\r") && !nodeContent.endsWith("\n")) //$NON-NLS-1$ //$NON-NLS-2$
								{
									if (indented.endsWith("\r\n")) //$NON-NLS-1$
									{
										indented = indented.substring(0, indented.length() - 2);
									}
									else if (indented.endsWith("\n") || indented.endsWith("\r")) //$NON-NLS-1$ //$NON-NLS-2$
									{
										indented = indented.substring(0, indented.length() - 1);
									}
								}

								IdeLog.logInfo(JSPlugin.getDefault(),
										"Indented Trimmed: +++++++++++++++++++++++++++++++++\r\n" + indented + ":"); //$NON-NLS-1$ //$NON-NLS-2$
								command.offset = emNodeStart;
								command.length = (emNodeEnd - emNodeStart) - 1; // current document does not have '}'
								// inserted
								command.text = indented;
								command.shiftsCaret = true;
								return;
							}
						}
					}
				}
			}
			// These catches are intended to catch every possible error or exception that could occur and return so that
			// the } will be inserted no matter what
			catch (Exception e)
			{
				IdeLog.logError(JSPlugin.getDefault(), e.getMessage());
				return;
			}
			catch (Error e)
			{
				IdeLog.logError(JSPlugin.getDefault(), e.getMessage());
				return;
			}
		}

		// super.customizeDocumentCommand(document, command);
	}

	private IProject getProject()
	{
		IEditorInput editorInput = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor().getEditorInput();

		IProject project = null;
		if (editorInput instanceof IFileEditorInput)
		{
			IFileEditorInput fl = (IFileEditorInput) editorInput;
			project = fl.getFile().getProject();
		}
		return project;
	}

	private LexemeList getLexemes(LexemeList lexemes, int startOffset, int endOffset)
	{
		int start = lexemes.getLexemeIndex(startOffset);
		int end = lexemes.getLexemeIndex(endOffset - 1);
		Lexeme[] lexList = lexemes.copyRange(start, end);
		LexemeList ll = new LexemeList();
		for (int i = 0; i < lexList.length; i++)
		{
			Lexeme lexeme = lexList[i];
			ll.add(lexeme);
		}
		return ll;
	}

	private IParseNode[] getComments(JSParseState parseState, int startOffset, int endOffset)
	{
		List<IParseNode> list = new ArrayList<IParseNode>();
		IParseNode[] nodes = parseState.getCommentRegions();
		for (int i = 0; i < nodes.length; i++)
		{
			IParseNode node = nodes[i];
			if (node.getStartingOffset() >= startOffset && node.getEndingOffset() <= endOffset)
			{
				list.add(node);
			}
		}

		return (IParseNode[]) list.toArray(new IParseNode[0]);
	}

	private IParseNode findAstNode(IParseNode ast, int offset)
	{
		if (ast.getTypeIndex() == JSParseNodeTypes.IF)
		{
			if (ast.getChild(1).getEndingOffset() == offset)
			{
				return ast;
			}
		}
		if (ast.getTypeIndex() == JSParseNodeTypes.TRY)
		{
			if (ast.getChild(0).getEndingOffset() == offset)
			{
				return ast;
			}
			if (ast.getChild(1).getEndingOffset() == offset)
			{
				return ast;
			}
		}
		if (ast.getTypeIndex() == JSParseNodeTypes.DO)
		{
			if (ast.getChild(0).getEndingOffset() == offset)
			{
				return ast;
			}
			if (ast.getChild(1).getEndingOffset() == offset)
			{
				return ast;
			}
		}

		if (ast.getEndingOffset() == offset && ast.getParent() != null)
		{
			return ast;
		}
		for (int a = 0; a < ast.getChildCount(); a++)
		{
			IParseNode findAstNode = findAstNode(ast.getChild(a), offset);
			if (findAstNode != null)
			{
				return findAstNode;
			}
		}
		return null;
	}

	/**
	 * @param d
	 * @param offset
	 * @return indentation level
	 */
	protected int getIndentationLevelAtOffset(IDocument d, int offset)
	{
		try
		{
			int p = (offset == d.getLength() ? offset - 1 : offset);
			IRegion line = d.getLineInformationOfOffset(p);

			int lineOffset = line.getOffset();
			int firstNonWS = findEndOfWhiteSpace(d, lineOffset, offset);
			String lineIndent = null;
			try
			{
				lineIndent = d.get(lineOffset, firstNonWS - lineOffset);
			}
			catch (BadLocationException e1)
			{
				return 0;
			}
			if (lineIndent.equals(StringUtils.EMPTY))
			{
				return 0;
			}

			int indentSize = 0;
			int tabWidth = this.configuration.getTabWidth(sourceViewer);
			char[] indentChars = lineIndent.toCharArray();
			for (int i = 0; i < indentChars.length; i++)
			{
				char e = indentChars[i];
				if (e == '\t')
				{
					indentSize += tabWidth - (indentSize % tabWidth);
				}
				else
				{
					indentSize++;
				}
			}
			return indentSize;
		}
		catch (BadLocationException excp)
		{
			// stop work
		}
		return 0;
	}

	/**
	 * indents string
	 * 
	 * @param level
	 * @param languageContent
	 * @param lineSeparator
	 * @return indented string
	 */
	public String indent(int level, String languageContent, String lineSeparator)
	{
		if (lineSeparator == null)
		{
			return languageContent;
		}
		String[] split = languageContent.split(lineSeparator);
		int indentSize = 4;
		if (configuration instanceof UnifiedConfiguration)
		{
			UnifiedConfiguration uc = (UnifiedConfiguration) configuration;
			indentSize = uc.getTabWidth(sourceViewer);
		}
		SourceWriter result = new SourceWriter(level, getIndentString(), indentSize);
		if (split.length == 1)
		{
			result.printIndent();
			result.print(split[0]);
			return result.toString();
		}
		for (int a = 0; a < split.length; a++)
		{
			result.printlnWithIndent(split[a]);
		}
		return result.toString();
	}

	/**
	 * indentAfterNewLine
	 * 
	 * @param d
	 * @param c
	 */
	protected void indentAfterNewLine(IDocument d, DocumentCommand c)
	{
		// not using the following helper method until we commit to not using
		// other types of source viewer configuation files
		// this.configuration.getIndentString(this.sourceViewer,
		// JSMimeType.MimeType);

		String indentString = getIndentString();

		// nothing to add if nothing to add
		if (indentString.equals("")) //$NON-NLS-1$
		{
			return;
		}

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

			// find case of ml comments being indented with an extra space for
			// formatting
			int lineLen = d.getLineLength(d.getLineOfOffset(lineOffset)) - 2; // don't
			// include
			// return
			// char
			if (lineLen > 0 && lineOffset + lineLen < d.getLength())
			{
				String lineText = d.get(lineOffset, lineLen);
				if (lineText.endsWith(" */")) //$NON-NLS-1$
				{
					firstNonWS--;
				}
			}

			StringBuffer buf = new StringBuffer(c.text);

			String currentIndent = getIndentationString(d, lineOffset, firstNonWS);
			String newline = c.text;

			// now just add the indents
			buf.append(currentIndent);

			if (c.offset > 1 && d.getChar(c.offset - 1) == '{')
			{

				JSCodeFormatterOptions options = new JSCodeFormatterOptions(null, getProject());
				if (JSCodeFormatterOptions.END_LINE != options.formatterBracePositionForMethodDecl)
				{
					// move opening brace to next line!
					c.offset = c.offset - 1;
					buf = new StringBuffer();
					//newline, current indent, open brace
					buf.append(newline).append(currentIndent).append("{");
					buf.append(c.text); // newline
					buf.append(currentIndent); // current indent
					c.length += 1;
					if (shouldAutoIndent())
					{
						c.shiftsCaret = false;
						buf.append(indentString); // additional indent
						c.caretOffset = c.offset + buf.length(); // move caret here now
						if (d.getChar(c.offset + 1) == '}')
						{
							buf.append(newline); // newline
							buf.append(currentIndent); // open brace indent level
						}
					}
					else
					{
						c.shiftsCaret = false;
						c.caretOffset = c.offset + buf.length() - 1;
					}
				}
				else if (shouldAutoIndent())
				{
					buf.append(indentString);
					c.shiftsCaret = false;
					c.caretOffset = c.offset + buf.length();
					if (d.getChar(c.offset) == '}')
					{
						buf.append(newline);
						buf.append(currentIndent);
					}
				}
			}

			// move the caret behind the prefix, even if we do not have to
			// insert it.
			// if (lengthToAdd < firstNonWS)
			// c.caretOffset = offset - lengthToAdd;
			c.text = buf.toString();

		}
		catch (BadLocationException excp)
		{
			// stop work
		}

		return;
	}

	private boolean shouldAutoIndent()
	{
		return Platform.getPreferencesService().getBoolean(JSPlugin.ID,
				IPreferenceConstants.AUTO_INDENT_ON_CARRIAGE_RETURN, true, null);
	}

	/**
	 * @see UnifiedAutoIndentStrategy#getPreferenceStore()
	 */
	public IPreferenceStore getPreferenceStore()
	{
		return JSPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * @see com.aptana.ide.editors.unified.UnifiedAutoIndentStrategy#getLexemeList()
	 */
	protected LexemeList getLexemeList()
	{
		JSFileLanguageService ls = (JSFileLanguageService) context.getLanguageService(JSMimeType.MimeType);
		return ls.getFileContext().getLexemeList();
	}

	/**
	 * @param source
	 * @param pos
	 * @return current offset from nearest new line character
	 */
	public int getCurrentIndentLevel(String source, int pos)
	{
		int res = 0;
		for (int a = pos; a >= 0; a--)
		{

			char charAt = source.charAt(a);
			if (charAt == '\n' || charAt == '\r')
			{
				break;
			}
			res++;
			if (!Character.isWhitespace(charAt))
			{
				res = 0;
			}
		}
		return res;
	}
}
