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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editor.js.lexing.JSTokenTypes;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.js.parsing.JSParseState;
import com.aptana.ide.editor.js.parsing.nodes.JSBinaryOperatorAssignNode;
import com.aptana.ide.editor.js.parsing.nodes.JSBinaryOperatorNode;
import com.aptana.ide.editor.js.parsing.nodes.JSFunctionNode;
import com.aptana.ide.editor.js.parsing.nodes.JSLabelNode;
import com.aptana.ide.editor.js.parsing.nodes.JSNaryAndExpressionNode;
import com.aptana.ide.editor.js.parsing.nodes.JSNaryNode;
import com.aptana.ide.editor.js.parsing.nodes.JSParseNode;
import com.aptana.ide.editor.js.parsing.nodes.JSParseNodeTypes;
import com.aptana.ide.editor.js.parsing.nodes.JSPrimitiveNode;
import com.aptana.ide.editor.js.parsing.nodes.JSStringNode;
import com.aptana.ide.editor.js.parsing.nodes.JSTextNode;
import com.aptana.ide.editor.js.parsing.nodes.JSUnaryOperatorNode;
import com.aptana.ide.editor.js.parsing.nodes.JSVarNode;
import com.aptana.ide.editor.jscomment.lexing.JSCommentTokenTypes;
import com.aptana.ide.editors.unified.BaseFormatter;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.editors.unified.UnifiedEditor;
import com.aptana.ide.editors.unified.folding.GenericCommentNode;
import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.TokenCategories;
import com.aptana.ide.parsing.IParser;
import com.aptana.ide.parsing.nodes.IParseNode;

/**
 * JavaScript code formatter class
 * 
 * @author Pavel Petrochenko
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class JSCodeFormatter extends BaseFormatter
{

	private String source = null;
	private HashMap<IParseNode, IFormattingCallback> markedNodes = new HashMap<IParseNode, IFormattingCallback>();
	private int indentLevel = 0;
	private JSCodeFormatterOptions codeoptions;
	private boolean nextBlockNoNewLine;
	private boolean nextReturnNoNewLine;
	private boolean nextIfNoNewLine;
	private boolean nextBlockFunctionBody;
	private boolean nextBlockInCase;

	private LexemeList lexemes;
	private SourceWriter writer;
	private Lexeme currentLexeme;
	private List<CommentNode> commentNodes = new ArrayList<CommentNode>();
	private boolean shouldPrint = true;

	Pattern newLinePattern = Pattern.compile(".*(\r|\n|\r\n)*.*"); //$NON-NLS-1$

	/**
	 * Class to emcompass metadata about comment regions
	 * 
	 * @author Kevin Sawicki
	 */
	private class CommentNode
	{
		GenericCommentNode node;
		boolean isMultiline = false;
		boolean printed = false;
		int indentLevel;
		boolean isNoFormat = false;
		boolean isFormat = false;
	}

	/**
	 * Builds a list of comment nodes found in this lexeme list. This function finds the indent level of comments and
	 * whether they are multiline or single line
	 */
	private List<CommentNode> buildCommentNodes(String source, IParseNode[] nodes, LexemeList lexemes)
	{
		List<CommentNode> cNodes = new ArrayList<CommentNode>();

		for (int i = 0; i < nodes.length; i++)
		{
			GenericCommentNode curr = (GenericCommentNode) nodes[i];
			CommentNode cn = new CommentNode();
			cn.node = curr;
			String substring = source.substring(curr.getStartingOffset(), curr.getEndingOffset());
			if (substring.indexOf('\r') != -1 || substring.indexOf('\n') != -1)
			{
				cn.isMultiline = true;
			}
			else
			{
				cn.isMultiline = false;
				if ("//FORMAT".equalsIgnoreCase(substring.trim())) //$NON-NLS-1$
				{
					cn.isFormat = true;
				}
				else if ("//NOFORMAT".equalsIgnoreCase(substring.trim())) //$NON-NLS-1$
				{
					cn.isNoFormat = true;
				}
			}

			cNodes.add(cn);
		}
		int level = 0;
		for (int i = 0; i < lexemes.size(); i++)
		{
			Lexeme curr = lexemes.get(i);
			if (curr.typeIndex == JSTokenTypes.LCURLY)
			{
				level++;
				Lexeme next = UnifiedEditor.findBalancingLexeme(lexemes, i, JSMimeType.MimeType, curr.typeIndex,
						JSTokenTypes.RCURLY, 1);
				for (int j = 0; j < cNodes.size(); j++)
				{
					CommentNode cn = (CommentNode) cNodes.get(j);
					GenericCommentNode comment = cn.node;
					if (comment.getStartingOffset() > curr.getStartingOffset()
							&& (next != null && comment.getStartingOffset() < next.getStartingOffset()))
					{
						cn.indentLevel = level;
					}
				}
			}
			else if (curr.typeIndex == JSTokenTypes.RCURLY)
			{
				level--;
			}
		}

		return cNodes;
	}

	/**
	 * @param node
	 * @param cb
	 */
	public void addMarkedNode(IParseNode node, IFormattingCallback cb)
	{
		markedNodes.put(node, cb);
	}

	/**
	 * Configures the underlying source writer
	 * 
	 * @param level
	 * @param indentString
	 * @param indentSize
	 */
	public void configureWriter(int level, String indentString, int indentSize)
	{
		writer = new SourceWriter(level, indentString, indentSize);
	}

	/**
	 * @see com.aptana.ide.editors.unified.ICodeFormatter#format(java.lang.String, boolean, Map, IProject, String)
	 */
	public String format(String source, boolean isSelection, Map options, IProject project, String separator)
	{
		return format(source, options, project, separator);
	}

	/**
	 * @param source
	 * @param options
	 * @param project
	 * @param separator
	 * @return - formatted string
	 */
	public String format(String source, Map options, IProject project, String separator)
	{
		JSCodeFormatterOptions cOptions = new JSCodeFormatterOptions(options, project);
		if (!cOptions.doFormatting)
		{
			return source;
		}

		IParser parser = LanguageRegistry.getParser(JSMimeType.MimeType);
		JSParseState parseState = (JSParseState) parser.createParseState(null);
		parseState.setEditState(source, source, 0, 0);
		try
		{
			LexemeList lexemes = parseState.getLexemeList();
			IParseNode parseNode = parser.parse(parseState);
			IParseNode[] comments = parseState.getCommentRegions();
			return format(source, source, parseNode, lexemes, comments, cOptions, separator);
		}
		catch (LexerException e)
		{
			// Do nothing
		}
		catch (ParseException e)
		{
			// Do nothing
		}

		return source;
	}

	/**
	 * Format based on a root parse node, a list of lexemes, and a list of comments
	 * 
	 * @param wholeSource
	 * @param contentToFormat
	 * @param parseNode
	 * @param lexemes
	 * @param comments
	 * @param options
	 * @param separator
	 * @return - formatted string
	 */
	public String format(String wholeSource, String contentToFormat, IParseNode parseNode, LexemeList lexemes,
			IParseNode[] comments, JSCodeFormatterOptions options, String separator)
	{
		this.source = wholeSource;

		if (options == null)
		{
			this.codeoptions = new JSCodeFormatterOptions();
		}
		else
		{
			this.codeoptions = options;
		}

		if (!this.codeoptions.doFormatting)
		{
			return contentToFormat;
		}
		this.shouldPrint = true;
		this.lexemes = lexemes;
		this.commentNodes = this.buildCommentNodes(wholeSource, comments, lexemes);
		String formatted = formatGivenNode(contentToFormat, parseNode, separator);

		// Check formatted text against source to see if we've lost or transposed any content
		IParser parser = LanguageRegistry.getParser(JSMimeType.MimeType);
		if (!isFormattingCorrect(lexemes, parser, contentToFormat, formatted, new int[] { JSCommentTokenTypes.TEXT },
				null))
		{
			formatted = contentToFormat;
		}
		this.lastLineEndingAdjusted = null;
		this.commentNodes.clear();
		this.currentLexeme = null;
		this.noFormatEnd = null;
		this.noFormatStart = null;
		this.shouldPrint = true;
		markedNodes.clear();
		this.writer = null;
		return formatted;
	}

	/**
	 * @param source
	 * @param parse
	 * @param lineDelimeter
	 * @return formatted source
	 */
	public String formatGivenNode(String source, IParseNode parse, String lineDelimeter)
	{
		nextBlockNoNewLine = false;
		if (checkError(parse))
		{
			return doLexerBasedFormat(source);
		}
		if (lexemes == null)
		{
			return source;
		}
		for (int a = 0; a < lexemes.size(); a++)
		{
			Lexeme lexeme = lexemes.get(a);
			if (lexeme.getToken().getCategoryIndex() == JSTokenTypes.ERROR)
			{
				return source;
			}
		}

		if (writer == null)
		{
			String indent = codeoptions.formatterTabChar;
			if (indent.length() == 0)
			{
				indent = " "; //$NON-NLS-1$
			}
			if (indent.charAt(0) == ' ')
			{
				StringBuffer bf = new StringBuffer();
				for (int a = 0; a < codeoptions.tabSize; a++)
				{
					bf.append(' ');
				}
				indent = bf.toString();
			}
			configureWriter(indentLevel, indent, codeoptions.tabSize);
		}
		// writer = new SourceWriter(indentLevel, indent, codeoptions.tabSize);
		if (lineDelimeter != null)
		{
			writer.setLineDelimeter(lineDelimeter);
		}

		format(parse, writer);

		// Print all remaining comments no matter what
		for (int i = 0; i < commentNodes.size(); i++)
		{
			CommentNode cn = commentNodes.get(i);
			if (!cn.printed)
			{
				if (!shouldPrint)
				{
					writer.println();
				}
				this.shouldPrint = true;
				printComment(cn);
				cn.printed = true;
				if (!cn.isMultiline)
				{
					printRemainingCommentLineEnding();
				}
			}
		}

		markedNodes.clear();

		String formatted = writer.toString();

		if (codeoptions.preserveLineBreaks)
		{
			String start = getStartLineBreaks(writer, source, formatted);
			String end = getEndLineBreaks(writer, source, formatted);

			formatted = start + formatted + end;
		}

		return formatted;
	}

	private String doLexerBasedFormat(String source)
	{
		return source;
	}

	private boolean checkError(IParseNode parse)
	{
		if (parse.getTypeIndex() == JSParseNodeTypes.ERROR)
		{
			return true;
		}
		for (int a = 0; a < parse.getChildCount(); a++)
		{
			if (checkError(parse.getChild(a)))
			{
				return true;
			}
		}
		return false;
	}

	private void format(IParseNode node, SourceWriter writer)
	{
		if (!(node instanceof JSParseNode))
		{
			return;
		}
		JSParseNode jsnode = (JSParseNode) node;
		writeFormattedSource(jsnode, writer, true);
	}

	private void writeFormattedSource(JSParseNode jsnode, SourceWriter writer, boolean appendTrailing)
	{
		if (jsnode instanceof JSBinaryOperatorNode)
		{
			JSBinaryOperatorNode bnode = (JSBinaryOperatorNode) jsnode;
			writeBinaryNode(bnode, writer);
			return;
		}
		else if (jsnode instanceof JSFunctionNode)
		{
			JSFunctionNode lnode = (JSFunctionNode) jsnode;
			writeFunctionNode(lnode, writer);
			return;
		}
		else if (jsnode instanceof JSLabelNode)
		{
			JSLabelNode lnode = (JSLabelNode) jsnode;
			writeLabelNode(lnode, writer);
			return;
		}
		else if (jsnode instanceof JSNaryNode)
		{
			JSNaryNode nnode = (JSNaryNode) jsnode;
			writeNaryNode(nnode, writer);
			return;
		}
		else if (jsnode instanceof JSPrimitiveNode)
		{
			JSPrimitiveNode pnode = (JSPrimitiveNode) jsnode;
			writePrimitiveNode(pnode, writer);
			return;
		}
		else if (jsnode instanceof JSStringNode)
		{
			JSStringNode snode = (JSStringNode) jsnode;
			writeStringNode(snode, writer);
			return;
		}
		else if (jsnode instanceof JSTextNode)
		{
			JSTextNode tnode = (JSTextNode) jsnode;
			writeTextNode(tnode, writer);
			return;
		}
		else if (jsnode instanceof JSUnaryOperatorNode)
		{
			JSUnaryOperatorNode unode = (JSUnaryOperatorNode) jsnode;
			writeUnaryOperatorNode(unode, writer);
			return;
		}
		else if (jsnode instanceof JSVarNode)
		{
			JSVarNode vnode = (JSVarNode) jsnode;
			writeVarNode(vnode, writer);
			return;
		}
		else
		{
			if (jsnode.getTypeIndex() == JSParseNodeTypes.ERROR)
			{
				this.printLexeme(jsnode.getStartingLexeme());
				// writer.print(substring);
			}
			else
			{
				writeCommon(jsnode, writer, appendTrailing);
			}
		}
	}

	private Lexeme[] getLexemesBetweenNode(IParseNode first, IParseNode second)
	{
		return getLexemesBetweenLexemes(first.getEndingLexeme(), second.getStartingLexeme());
	}

	private Lexeme[] getLexemesBetweenLexemes(Lexeme first, Lexeme second)
	{
		List<Lexeme> between = new ArrayList<Lexeme>();
		int firstIndex = lexemes.getLexemeIndex(first);
		int secondIndex = lexemes.getLexemeIndex(second);
		if (secondIndex - 1 > firstIndex)
		{
			for (int i = firstIndex + 1; i < secondIndex; i++)
			{
				Lexeme curr = lexemes.get(i);
				if (curr.getLanguage().equals(JSMimeType.MimeType))
				{
					between.add(curr);
				}
			}

		}
		return between.toArray(new Lexeme[0]);
	}

	/**
	 * Gets the next lexeme in the list. This returns the next meaningful lexeme that is a JS lexeme and not whitespace
	 * 
	 * @return - next JS non-whitespace lexeme or null if none
	 */
	private Lexeme getNextLexeme()
	{
		return getNextLexeme(this.currentLexeme);
	}

	private Lexeme getNextLexeme(Lexeme lexeme)
	{
		Lexeme next = null;
		int index;
		boolean found = false;
		if (lexeme != null)
		{
			index = lexemes.getLexemeIndex(lexeme);
		}
		else
		{
			index = 0;
			if (lexemes.size() > 0)
			{
				next = lexemes.get(0);
				if (next != null && next.getLanguage().equals(JSMimeType.MimeType)
						&& next.getCategoryIndex() != TokenCategories.WHITESPACE)
				{
					found = true;
				}
			}
		}
		while (!found)
		{
			if (index > -1 && index + 1 < lexemes.size())
			{
				next = lexemes.get(index + 1);
				if (next != null && next.getLanguage().equals(JSMimeType.MimeType)
						&& next.getCategoryIndex() != TokenCategories.WHITESPACE)
				{
					found = true;
				}
				else
				{
					index++;
				}
			}
			else
			{
				found = true;
			}
		}
		return next;
	}

	/**
	 * Gets the previous lexeme in the list. This returns the previous meaningful lexeme that is a JS lexeme and not
	 * whitespace
	 * 
	 * @return - previous JS non-whitespace lexeme or null if none
	 */
	private Lexeme getPreviousLexeme()
	{
		Lexeme next = null;
		if (this.currentLexeme != null)
		{
			int index = lexemes.getLexemeIndex(this.currentLexeme);
			boolean found = false;
			while (!found)
			{
				if (index - 1 > 0)
				{
					next = lexemes.get(index - 1);
					if (next != null && next.getLanguage().equals(JSMimeType.MimeType)
							&& next.getCategoryIndex() != TokenCategories.WHITESPACE)
					{
						found = true;
					}
					else
					{
						index--;
					}
				}
				else
				{
					break;
				}
			}
			if (!found)
			{
				next = null;
			}
		}
		return next;
	}

	/**
	 * Gets the next lexeme if it is of the specified type
	 * 
	 * @param type -
	 *            type of lexeme you want to be next
	 * @return - lexeme of type if next, null otherwise
	 */
	private Lexeme getNextLexemeIfOfType(int type)
	{
		Lexeme next = getNextLexeme();
		if (next != null && next.typeIndex == type)
		{
			return next;
		}
		return null;
	}

	/**
	 * Prints the next lexeme if it is of a specificied type
	 * 
	 * @param type -
	 *            type of lexeme you want to print if next
	 */
	private void printNextLexemeIfOfType(int type)
	{
		Lexeme next = getNextLexemeIfOfType(type);
		if (next != null)
		{
			printLexeme(next);
		}
	}

	/**
	 * Prints a comment
	 * 
	 * @param node
	 */
	private void printComment(CommentNode node)
	{
		if (node != null)
		{
			if (node.isMultiline)
			{
				printMultilineComment(node.node);
			}
			else
			{
				printSinglelineComment(node.node);
			}
		}
	}

	/**
	 * Prints a single line comment as is
	 * 
	 * @param node
	 */
	private void printSinglelineComment(GenericCommentNode node)
	{
		try
		{
			String substring = source.substring(node.getStartingOffset(), node.getEndingOffset());
			print(substring);
		}
		catch (Exception e)
		{
			// Do nothing
		}
	}

	/**
	 * Prints a multi line comment by breaking the into individual lines and then adjusting the indent accordingly
	 * 
	 * @param node
	 */
	private void printMultilineComment(GenericCommentNode node)
	{
		String substring = source.substring(node.getStartingOffset(), node.getEndingOffset());
		String[] split = splitOnLines(substring);
		if (split.length > 1)
		{
			adjustCommentIndent();
			// if (!writer.toString().endsWith(writer.getIndentText()))
			// {
			// writer.printIndent();
			// }
			print(split[0]);
			printLineEnding(node, 1, false);
			for (int i = 1; i < split.length; i++)
			{
				printIndent();
				print(' ');
				print(split[i].trim());
				printLineEnding(node, 1, false);
			}
		}
		else
		{
			writer.print(substring);
		}
	}

	/**
	 * Since a comment may need an indent or not depending on what was printed before it this method is used to adjust
	 * the indent of the first line of a comment. It looks at the end of the current buffer and figures out if it is
	 * missing some need indentation.
	 */
	private void adjustCommentIndent()
	{
		if (!shouldPrint)
		{
			return;
		}
		String buffer = writer.toString();
		if (buffer.length() > 0)
		{
			String trailing = ""; //$NON-NLS-1$
			int index = buffer.length() - 1;
			char spacing = buffer.charAt(index);
			if (spacing == ' ' || spacing == '\t')
			{
				while (index > -1 && buffer.charAt(index) == spacing)
				{
					trailing += spacing;
					index--;
				}
				String ending = writer.getIndentText();
				if (ending.length() > trailing.length())
				{
					int diff = ending.length() - trailing.length();
					for (int i = 0; i < diff; i++)
					{
						writer.print(spacing);
					}
				}
			}
			else
			{
				writer.printIndent();
			}
		}
	}

	/**
	 * These lexemes are used to represent the lexeme gap when a //noformat //format block is found.
	 */
	private Lexeme noFormatStart;
	private Lexeme noFormatEnd;

	private void handleNoFormatting(CommentNode cn)
	{
		int endingOffset = source.length();
		this.noFormatStart = lexemes.getLexemeFromOffset(cn.node.getStartingOffset());
		this.noFormatEnd = lexemes.get(lexemes.size() - 1);
		for (int j = 0; j < commentNodes.size(); j++)
		{
			CommentNode format = commentNodes.get(j);
			if (format.isFormat && format.node.getStartingOffset() > cn.node.getStartingOffset())
			{
				endingOffset = format.node.getEndingOffset();
				this.noFormatEnd = lexemes.getFloorLexeme(format.node.getEndingOffset());
				break;
			}
		}
		String unformatted = source.substring(cn.node.getStartingOffset(), endingOffset);
		for (int j = 0; j < commentNodes.size(); j++)
		{
			CommentNode format = commentNodes.get(j);
			if (format.node.getStartingOffset() >= cn.node.getStartingOffset()
					&& format.node.getEndingOffset() <= endingOffset)
			{
				format.printed = true;
			}
		}
		int end = lexemes.getLexemeIndex(this.noFormatEnd);
		if (end + 1 < lexemes.size())
		{
			this.noFormatEnd = getNextLexeme(this.noFormatEnd);
		}
		else
		{
			this.noFormatEnd = null;
		}

		print(unformatted);
		this.shouldPrint = false;
	}

	private void printAdjustedIndent()
	{
		adjustCommentIndent();
	}

	private void print(char c)
	{
		if (shouldPrint)
		{
			writer.print(c);
		}
	}

	private void print(String string)
	{
		if (shouldPrint)
		{
			writer.print(string);
		}
	}

	private void printIndent()
	{
		if (shouldPrint)
		{
			writer.printIndent();
		}
	}

	private void printPreviousLineEnding(Lexeme lexeme, IParseNode node, int lineCount, boolean advance)
	{
		if (lexeme != null)
		{
			int index = lexemes.getLexemeIndex(lexeme);
			if (index > 0)
			{
				Lexeme format = lexemes.get(index - 1);
				printLineEnding(format, node, lineCount, advance);
			}
		}
	}

	/**
	 * Prints a lexeme and surrounding comments
	 * 
	 * @param lexeme -
	 *            lexeme to print
	 */
	private void printLexeme(Lexeme lexeme)
	{
		Lexeme nextCheck = getNextLexeme();
		if (this.noFormatEnd != null)
		{
			this.noFormatStart = lexeme;
		}
		if (this.currentLexeme == null || nextCheck != null && nextCheck.equals(lexeme))
		{
			this.currentLexeme = lexeme;
			if (this.noFormatStart != this.noFormatEnd)
			{
				return;
			}

			if (!shouldPrint && this.currentLexeme != null)
			{
				this.shouldPrint = true;
				printPreviousLineEnding(this.noFormatEnd, null, 0, false);
				printAdjustedIndent();
			}
			else
			{
				this.shouldPrint = true;
			}
			this.noFormatStart = null;
			this.noFormatEnd = null;

			Lexeme previous = getPreviousLexeme();
			Lexeme next = getNextLexeme();
			// Look before the current lexeme
			for (int i = 0; i < commentNodes.size(); i++)
			{
				CommentNode cn = commentNodes.get(i);
				GenericCommentNode comment = cn.node;
				if (cn.isMultiline && !cn.printed)
				{
					if (!cn.printed && comment.getStartingOffset() < this.currentLexeme.offset
							&& (previous == null || comment.getStartingOffset() > previous.offset))
					{
						int previousLevel = writer.getIndentLevel();
						writer.setCurrentIndentLevel(cn.indentLevel);
						printComment(cn);
						writer.setCurrentIndentLevel(previousLevel);
						writer.printIndent();
						cn.printed = true;
					}
				}
				else if (!cn.printed && comment.getStartingOffset() < this.currentLexeme.offset)
				{
					int previousLevel = writer.getIndentLevel();
					if (cn.isNoFormat)
					{
						handleNoFormatting(cn);
						if (this.noFormatEnd == lexeme)
						{
							this.shouldPrint = true;
							printPreviousLineEnding(this.noFormatEnd, null, 0, false);
							this.currentLexeme = lexeme;
							this.noFormatEnd = null;
							this.noFormatStart = null;
						}
					}
					else
					{
						writer.setCurrentIndentLevel(cn.indentLevel);
						adjustCommentIndent();
						printComment(cn);
						writer.setCurrentIndentLevel(previousLevel);
						Lexeme ending = lexemes.getLexemeFromOffset(comment.getEndingOffset() - 1);
						printLineEnding(ending, null, 1, false);
						printIndent();
					}
					cn.printed = true;

				}
			}

			// Print the lexeme
			print(lexeme.getText());

			// Look after the current lexeme and print any non-multiline comments
			for (int i = 0; i < commentNodes.size(); i++)
			{
				CommentNode cn = commentNodes.get(i);
				if (!cn.isMultiline)
				{
					GenericCommentNode comment = cn.node;
					if (!cn.printed && comment.getStartingOffset() > this.currentLexeme.offset
							&& (next == null || comment.getStartingOffset() < next.offset))
					{
						String between = source.substring(lexeme.getEndingOffset(), comment.getStartingOffset());
						if (between.indexOf('\n') == -1 && between.indexOf('\r') == -1)
						{
							int previousLevel = writer.getIndentLevel();
							writer.setCurrentIndentLevel(0);
							try
							{
								// If we have leading spaces that don't span lines than trim to a single space
								String substring = source.substring(this.currentLexeme.getStartingOffset(), comment
										.getStartingOffset());
								if (substring.indexOf('\n') == -1 && substring.indexOf('\r') == -1
										&& (substring.indexOf(' ') != -1 || substring.indexOf('\t') != -1))
								{
									print(' ');
								}
							}
							catch (Exception e)
							{
								// Do nothing and move on
							}
							if (cn.isNoFormat)
							{
								handleNoFormatting(cn);
							}
							else
							{
								printComment(cn);

								try
								{
									if (next == null)
									{
										printLineEnding(false);
									}
									else if (comment.getName().equals("JSCOMMENT")) //$NON-NLS-1$
									{
										try
										{
											String substring = source.substring(comment.getStartingOffset(), comment
													.getEndingOffset());
											if (substring.startsWith("//")) //$NON-NLS-1$
											{
												printLineEnding(true);
											}
										}
										catch (Exception e)
										{
											// Do nothing
										}

									}
								}
								catch (Exception e)
								{
									// Do nothing and move on
								}

							}
							writer.setCurrentIndentLevel(previousLevel);
							cn.printed = true;
						}
					}
				}
			}
		}
	}

	/**
	 * Prints an array of lexemes if they are the next ones in the list. Will iterate over the entire array and print
	 * each lexeme if getNextLexeme returns that lexeme
	 * 
	 * @param lexemes -
	 *            array of lexemes to print out
	 */
	private void printLexemes(Lexeme[] lexemes)
	{
		if (lexemes != null && lexemes.length > 0)
		{
			for (int i = 0; i < lexemes.length; i++)
			{
				Lexeme next = getNextLexeme();
				if (lexemes[i].equals(next))
				{
					printLexeme(lexemes[i]);
				}
			}
		}
	}

	private void printRemainingCommentLineEnding()
	{
		if (!shouldPrint)
		{
			return;
		}
		writer.println();
	}

	private void printLineEnding(boolean advance)
	{
		printLineEnding(null, 1, advance);
	}

	/**
	 * Print a line ending
	 * 
	 * @param node
	 */
	private void printLineEnding(IParseNode node)
	{
		printLineEnding(this.currentLexeme, node, 1, true);
	}

	private Lexeme lastLineEndingAdjusted = null;

	private void printLineEnding(IParseNode node, int defaultLineCount, boolean advance)
	{
		printLineEnding(this.currentLexeme, node, defaultLineCount, advance);
	}

	/**
	 * Print a line ending
	 * 
	 * @param node
	 */
	private void printLineEnding(Lexeme lexeme, IParseNode node, int defaultLineCount, boolean advance)
	{

		if (!shouldPrint)
		{
			return;
		}
		try
		{
			Lexeme previous = lexeme;
			if (previous == null)
			{
				writer.println();
				return;
			}
			int previousIndex = lexemes.getLexemeIndex(previous);
			if (previousIndex < 0)
			{
				writer.println();
				return;
			}

			int nextIndex = previousIndex + 1;
			if (nextIndex >= lexemes.size())
			{
				writer.println();
				return;
			}
			Lexeme next = lexemes.get(nextIndex);
			int newLines = getNumberOfNewlines(previous, next);

			int newLinesMax = defaultLineCount;
			if (codeoptions.preserveLineBreaks)
			{
				if (previous != lastLineEndingAdjusted || !advance)
				{
					if (advance)
					{
						lastLineEndingAdjusted = previous;
					}
					newLinesMax = Math.max(newLines, defaultLineCount);
				}
				else
				{
					newLinesMax = 0;
				}
			}

			for (int i = 0; i < newLinesMax; i++)
			{
				if (i > 0)
				{
					writer.printIndent();
				}
				writer.println();
			}
		}
		catch (Exception ex)
		{
			IdeLog.logError(JSPlugin.getDefault(), "Error grabbing new lines", ex); //$NON-NLS-1$
			writer.println();
		}
	}

	/**
	 * Number of new lines between two lexemes
	 * 
	 * @param previous
	 * @param next
	 * @return - number of lines
	 */
	int getNumberOfNewlines(Lexeme previous, Lexeme next)
	{
		if (previous == null || next == null)
		{
			return 1;
		}
		else
		{
			String sourceBit = source.substring(previous.getEndingOffset(), next.getStartingOffset());
			return StringUtils.getNumberOfNewlines(sourceBit);
		}
	}

	private void writeBinaryNode(JSBinaryOperatorNode bnode, SourceWriter writer)
	{
		if (bnode instanceof JSBinaryOperatorAssignNode)
		{
			JSBinaryOperatorAssignNode anode = (JSBinaryOperatorAssignNode) bnode;
			writeBinaryAssignNode(anode, writer);
			return;
		}

		switch (bnode.getTypeIndex())
		{
			case JSParseNodeTypes.GET_ELEMENT:
				JSParseNode object = (JSParseNode) bnode.getChild(0);
				JSParseNode index = (JSParseNode) bnode.getChild(1);
				writeFormattedSource(object, writer, true);
				printNextLexemeIfOfType(JSTokenTypes.LBRACKET);
				writeFormattedSource(index, writer, true);
				printNextLexemeIfOfType(JSTokenTypes.RBRACKET);
				break;

			case JSParseNodeTypes.GET_PROPERTY:
				JSParseNode left = (JSParseNode) bnode.getChild(0);
				JSParseNode identifier = (JSParseNode) bnode.getChild(1);
				writeFormattedSource(left, writer, true);
				printNextLexemeIfOfType(JSTokenTypes.DOT);
				writeFormattedSource(identifier, writer, true);
				break;

			case JSParseNodeTypes.EQUAL:
			case JSParseNodeTypes.GREATER_THAN:
			case JSParseNodeTypes.GREATER_THAN_OR_EQUAL:
			case JSParseNodeTypes.IDENTITY:
			case JSParseNodeTypes.IN:
			case JSParseNodeTypes.INSTANCE_OF:
			case JSParseNodeTypes.LESS_THAN:
			case JSParseNodeTypes.LESS_THAN_OR_EQUAL:
			case JSParseNodeTypes.LOGICAL_AND:
			case JSParseNodeTypes.LOGICAL_OR:
			case JSParseNodeTypes.NOT_EQUAL:
			case JSParseNodeTypes.NOT_IDENTITY:
			case JSParseNodeTypes.ADD:
			case JSParseNodeTypes.ARITHMETIC_SHIFT_RIGHT:
			case JSParseNodeTypes.BITWISE_AND:
			case JSParseNodeTypes.BITWISE_OR:
			case JSParseNodeTypes.BITWISE_XOR:
			case JSParseNodeTypes.DIVIDE:
			case JSParseNodeTypes.MOD:
			case JSParseNodeTypes.MULTIPLY:
			case JSParseNodeTypes.SHIFT_LEFT:
			case JSParseNodeTypes.SHIFT_RIGHT:
			case JSParseNodeTypes.SUBTRACT:
				writeSourceCommon(bnode, writer);
				break;

			default:
				break;
		}
	}

	private void writeBinaryAssignNode(JSBinaryOperatorAssignNode anode, SourceWriter writer)
	{
		JSParseNode left = (JSParseNode) anode.getChild(0);
		JSParseNode right = (JSParseNode) anode.getChild(1);
		writeFormattedSource(left, writer, true);
		print(' ');
		printLexemes(getLexemesBetweenLexemes(left.getEndingLexeme(), right.getStartingLexeme()));
		print(' ');
		writeFormattedSource(right, writer, true);
	}

	private void writeSourceCommon(JSBinaryOperatorNode bnode, SourceWriter writer)
	{
		JSParseNode left = (JSParseNode) bnode.getChild(0);
		JSParseNode right = (JSParseNode) bnode.getChild(1);

		writeFormattedSource(left, writer, true);
		Lexeme endingLexeme = right.getEndingLexeme();
		Lexeme startingLexeme = left.getStartingLexeme();
		boolean isNewLine = false;
		int ti = right.getTypeIndex();
		if (ti == JSParseNodeTypes.FUNCTION || ti == JSParseNodeTypes.OBJECT_LITERAL)
		{
			isNewLine = true;
		}
		else
		{
			for (int a = startingLexeme.getEndingOffset(); a < endingLexeme.getStartingOffset() && a < source.length(); a++)
			{
				char c = source.charAt(a);
				if (c == '\r' || c == '\n')
				{
					isNewLine = true;
					break;
				}
			}
		}
		if (!isNewLine)
		{
			print(' ');
			printLexemes(new Lexeme[] { left.getEndingLexeme() });
			printLexemes(getLexemesBetweenNode(left, right));
			print(' ');
		}
		else
		{
			print(' ');
			printLexemes(new Lexeme[] { left.getEndingLexeme() });
			printLexemes(getLexemesBetweenNode(left, right));
			printLineEnding(bnode);
			printAdjustedIndent();
		}
		writeFormattedSource(right, writer, true);
	}

	private void writeCommon(JSParseNode node, SourceWriter writer, boolean appendTrailing)
	{
		switch (node.getTypeIndex())
		{
			case JSParseNodeTypes.ASSIGN:
				writeFormattedSource((JSParseNode) node.getChild(0), writer, true); // left
				print(' ');
				printLexemes(getLexemesBetweenNode(node.getChild(0), node.getChild(1)));
				print(' ');
				writeFormattedSource((JSParseNode) node.getChild(1), writer, true);
				break;

			case JSParseNodeTypes.CATCH:
			{
				if (codeoptions.insertNewLineBeforeCatch && writer.getCurrentIndentLevel() != 0)
				{
					printLineEnding(node);
					printIndent();
				}
				printLexeme(node.getStartingLexeme());
				print(' ');

				// name
				JSParseNode child = (JSParseNode) node.getChild(0);
				printNextLexemeIfOfType(JSTokenTypes.LPAREN);
				writeFormattedSource(child, writer, true);
				Lexeme next = getNextLexemeIfOfType(JSTokenTypes.RPAREN);
				if (next != null)
				{
					printLexeme(next);
					print(' ');
				}

				// body
				writeFormattedSource((JSParseNode) node.getChild(1), writer, true);
				break;
			}
			case JSParseNodeTypes.CONDITIONAL:
			{
				writeFormattedSource((JSParseNode) node.getChild(0), writer, true);
				// condition
				Lexeme next = getNextLexemeIfOfType(JSTokenTypes.QUESTION);
				if (next != null)
				{
					print(' ');
					printLexeme(next);
					print(' ');
				}
				writeFormattedSource((JSParseNode) node.getChild(1), writer, true);
				next = getNextLexeme();
				if (next != null && next.typeIndex == JSTokenTypes.COLON)
				{
					print(' ');
					printLexeme(next);
					print(' ');
				}
				writeFormattedSource((JSParseNode) node.getChild(2), writer, true);
				break;
			}
			case JSParseNodeTypes.CONSTRUCT:
			{
				printLexeme(node.getStartingLexeme());
				print(' ');
				writeFormattedSource((JSParseNode) node.getChild(0), writer, true); // function
				printNextLexemeIfOfType(JSTokenTypes.LPAREN);
				writeFormattedSource((JSParseNode) node.getChild(1), writer, true); // args
				printNextLexemeIfOfType(JSTokenTypes.RPAREN);
				break;
			}
			case JSParseNodeTypes.DECLARATION:
			{
				writeFormattedSource((JSParseNode) node.getChild(0), writer, true); // name
				if (node.getChildCount() > 1 && node.getChild(1).getTypeIndex() != JSParseNodeTypes.EMPTY)
				{
					Lexeme next = getNextLexemeIfOfType(JSTokenTypes.EQUAL);
					if (next != null)
					{
						print(' ');
						printLexeme(next);
						print(' ');
					}
					writeFormattedSource((JSParseNode) node.getChild(1), writer, true); // assignment
				}
				break;
			}
			case JSParseNodeTypes.DO:
				writeDoNode(node, writer);
				break;

			case JSParseNodeTypes.EMPTY:
				printNextLexemeIfOfType(JSTokenTypes.SEMICOLON);
				break;

			case JSParseNodeTypes.FINALLY:
				if (codeoptions.insertNewLineBeforeFinally && writer.getCurrentIndentLevel() != 0)
				{
					printLineEnding(node);
					printIndent();

				}
				printLexeme(node.getStartingLexeme());
				print(' ');
				writeFormattedSource((JSParseNode) node.getChild(0), writer, true); // body
				break;

			case JSParseNodeTypes.FOR_IN:
			{
				writeForInNode(node, writer);
				break;
			}
			case JSParseNodeTypes.FOR:
				writeForNode(node, writer);
				break;

			case JSParseNodeTypes.FUNCTION:
				writeFunction(node, writer);
				break;

			case JSParseNodeTypes.IF:
				writeIf(node, writer);
				break;

			case JSParseNodeTypes.INVOKE:
				writeFormattedSource((JSParseNode) node.getChild(0), writer, true); // function
				printNextLexemeIfOfType(JSTokenTypes.LPAREN);
				writeFormattedSource((JSParseNode) node.getChild(1), writer, true); // arguments
				printLexeme(node.getEndingLexeme());

				break;

			case JSParseNodeTypes.LABELLED:
				writeFormattedSource((JSParseNode) node.getChild(0), writer, true); // label
				printLexemes(getLexemesBetweenNode(node.getChild(0), node.getChild(1)));
				print(' ');
				writeFormattedSource((JSParseNode) node.getChild(1), writer, true); // statement
				break;

			case JSParseNodeTypes.NAME_VALUE_PAIR:
				writeFormattedSource((JSParseNode) node.getChild(0), writer, true); // name
				printLexemes(getLexemesBetweenNode(node.getChild(0), node.getChild(1)));
				print(' ');
				writeFormattedSource((JSParseNode) node.getChild(1), writer, true); // value
				break;

			case JSParseNodeTypes.THIS:
				printNextLexemeIfOfType(JSTokenTypes.THIS);
				// writer.print("node"); //$NON-NLS-1$
				break;

			case JSParseNodeTypes.TRY:
				JSParseNode body = ((JSParseNode) node.getChild(0));
				JSParseNode catchNode = ((JSParseNode) node.getChild(1));
				JSParseNode finallyNode = ((JSParseNode) node.getChild(2));
				printLexeme(node.getStartingLexeme());
				print(' ');
				writeFormattedSource(body, writer, true);
				if (catchNode.isEmpty() == false)
				{
					if (writer.getCurrentIndentLevel() != 0)
					{
						print(' ');
					}
					writeFormattedSource(catchNode, writer, true);
				}

				if (finallyNode.isEmpty() == false)
				{
					if (!codeoptions.insertNewLineBeforeFinally && writer.getCurrentIndentLevel() != 0)
					{
						print(' ');
					}
					writeFormattedSource(finallyNode, writer, true);
				}
				break;

			case JSParseNodeTypes.WHILE:
				writeWhileNode(node, writer);
				break;

			case JSParseNodeTypes.WITH:
				printLexeme(node.getStartingLexeme());
				print(' ');
				JSParseNode child = (JSParseNode) node.getChild(0);
				printNextLexemeIfOfType(JSTokenTypes.LPAREN);
				writeFormattedSource(child, writer, true); // expression
				Lexeme next = getNextLexemeIfOfType(JSTokenTypes.RPAREN);
				if (next != null)
				{
					printLexeme(next);
					print(' ');
				}
				writeFormattedSource((JSParseNode) node.getChild(1), writer, true); // body
				break;

			case JSParseNodeTypes.ERROR:
				writer.print("ERROR"); //$NON-NLS-1$
				break;

			default:
				break;
		}
	}

	private void writeForNode(JSParseNode node, SourceWriter JSCodeFormatterOptions)
	{
		JSParseNode initializer = ((JSParseNode) node.getChild(0));

		JSParseNode condition = ((JSParseNode) node.getChild(1));
		JSParseNode advance = ((JSParseNode) node.getChild(2));
		JSParseNode body = ((JSParseNode) node.getChild(3));
		printLexeme(node.getStartingLexeme());
		Lexeme next = getNextLexemeIfOfType(JSTokenTypes.LPAREN);
		if (next != null)
		{
			print(' ');
			printLexeme(next);
		}

		if (initializer.isEmpty() == false)
		{
			writeFormattedSource(initializer, writer, true);
		}
		next = getNextLexeme();
		if (next != null && next.typeIndex == JSTokenTypes.SEMICOLON)
		{
			printLexeme(next);
		}

		if (condition.isEmpty() == false)
		{
			print(' ');
			writeFormattedSource(condition, writer, true);
		}
		next = getNextLexeme();
		if (next != null && next.typeIndex == JSTokenTypes.SEMICOLON)
		{
			printLexeme(next);
		}

		if (advance.isEmpty() == false)
		{
			print(' ');
			writeFormattedSource(advance, writer, true);
		}
		next = getNextLexeme();
		if (next != null && next.typeIndex == JSTokenTypes.RPAREN)
		{
			printLexeme(next);
			print(' ');
		}
		if (body.getTypeIndex() != JSParseNodeTypes.STATEMENTS)
		{
			printLineEnding(node);
			writer.increaseIndent();
			printIndent();
		}
		writeFormattedSource(body, writer, true);
		if (body.getTypeIndex() != JSParseNodeTypes.STATEMENTS)
		{
			writer.decreaseIndent();
		}
	}

	private void writeForInNode(JSParseNode node, SourceWriter writer)
	{
		JSParseNode initializer = ((JSParseNode) node.getChild(0));
		JSParseNode object = ((JSParseNode) node.getChild(1));
		JSParseNode body = ((JSParseNode) node.getChild(2));
		printLexeme(node.getStartingLexeme());
		Lexeme next = getNextLexemeIfOfType(JSTokenTypes.LPAREN);
		if (next != null)
		{
			print(' ');
			printLexeme(next);
		}
		writeFormattedSource(initializer, writer, true);
		next = getNextLexeme();
		if (next != null && next.typeIndex == JSTokenTypes.IN)
		{
			print(' ');
			printLexeme(next);
			print(' ');
		}
		writeFormattedSource(object, writer, true);
		next = getNextLexeme();
		if (next != null && next.typeIndex == JSTokenTypes.RPAREN)
		{
			printLexeme(next);
			print(' ');
		}
		if (body.getTypeIndex() != JSParseNodeTypes.STATEMENTS)
		{
			printLineEnding(node);
			writer.increaseIndent();
			printIndent();
		}
		writeFormattedSource(body, writer, true);
		if (body.getTypeIndex() != JSParseNodeTypes.STATEMENTS)
		{
			writer.decreaseIndent();
		}
	}

	private void writeDoNode(JSParseNode node, SourceWriter writer)
	{
		JSParseNode doBody = (JSParseNode) node.getChild(0);
		printLexeme(node.getStartingLexeme());
		print(' ');
		if (doBody.getTypeIndex() != JSParseNodeTypes.STATEMENTS)
		{
			printLineEnding(node);
			writer.increaseIndent();
			printIndent();
		}
		writeFormattedSource(doBody, writer, true);
		if (doBody.getTypeIndex() != JSParseNodeTypes.STATEMENTS)
		{
			writer.decreaseIndent();
			printNextLexemeIfOfType(JSTokenTypes.SEMICOLON);
		}
		JSParseNode cond = (JSParseNode) node.getChild(1);
		if (codeoptions.insertNewLineBeforeWhile)
		{
			printLineEnding(node);
			printIndent();
		}
		else
		{
			print(' ');
		}
		Lexeme next = getNextLexemeIfOfType(JSTokenTypes.WHILE);
		if (next != null)
		{
			printLexeme(next);
			print(' ');
		}
		next = getNextLexeme();
		if (next != null && next.typeIndex == JSTokenTypes.LPAREN)
		{
			printLexeme(next);
		}
		writeFormattedSource(cond, writer, true); // condition
		next = getNextLexeme();
		if (next != null && next.typeIndex == JSTokenTypes.RPAREN)
		{
			printLexeme(next);
		}
	}

	private void writeWhileNode(JSParseNode node, SourceWriter writer)
	{
		JSParseNode body;
		printLexeme(node.getStartingLexeme());
		print(' ');

		JSParseNode child = (JSParseNode) node.getChild(0);
		printNextLexemeIfOfType(JSTokenTypes.LPAREN);
		writeFormattedSource((JSParseNode) child, writer, true); // condition
		Lexeme next = getNextLexemeIfOfType(JSTokenTypes.RPAREN);
		if (next != null)
		{
			printLexeme(next);
			print(' ');
		}
		body = (JSParseNode) node.getChild(1);
		if (body.getTypeIndex() != JSParseNodeTypes.STATEMENTS)
		{
			printLineEnding(node);
			writer.increaseIndent();
			printIndent();
		}
		writeFormattedSource(body, writer, true); // body
		if (body.getTypeIndex() != JSParseNodeTypes.STATEMENTS)
		{
			writer.decreaseIndent();
		}
	}

	private void printConditionalSpace()
	{
		String last = writer.toString();
		int len = last.length();
		if (len > 0 && !Character.isWhitespace(last.charAt(len - 1)))
		{
			print(' ');
		}
	}

	private void writeIf(JSParseNode node, SourceWriter writer)
	{
		JSParseNode condition;
		condition = ((JSParseNode) node.getChild(0));
		JSParseNode trueCase = ((JSParseNode) node.getChild(1));
		JSParseNode falseCase = ((JSParseNode) node.getChild(2));
		if (codeoptions.insertNewLineBeforeIf && !nextIfNoNewLine)
		{
			printLineEnding(node);
		}
		printLexeme(node.getStartingLexeme());
		print(' ');
		printNextLexemeIfOfType(JSTokenTypes.LPAREN);
		writeFormattedSource(condition, writer, true);
		Lexeme next = getNextLexemeIfOfType(JSTokenTypes.RPAREN);
		if (next != null)
		{
			printLexeme(next);
			printConditionalSpace();
		}
		boolean incIndent = false;
		boolean nR = nextReturnNoNewLine;

		nextReturnNoNewLine = trueCase.getTypeIndex() == JSParseNodeTypes.RETURN;
		if (!codeoptions.keepThenSameLine)
		{
			if (trueCase.getTypeIndex() != JSParseNodeTypes.STATEMENTS)
			{

				if (nextReturnNoNewLine && codeoptions.insertNewLineBeforeReturn)
				{
					nextReturnNoNewLine = false;
					incIndent = true;
				}
				if ((!codeoptions.keepSimpleIfOnOneLine) || !falseCase.isEmpty())
				{
					printLineEnding(node);
					incIndent = true;
				}
			}
		}
		if (incIndent)
		{
			writer.increaseIndent();
			printIndent();
		}
		if (trueCase.getTypeIndex() == JSParseNodeTypes.STATEMENTS)
		{
			if (codeoptions.keepGuardianClauseOnOneLine)
			{
				this.nextBlockNoNewLine = true;
			}
		}
		writeFormattedSource(trueCase, writer, true);
		nextReturnNoNewLine = nR;
		if (incIndent)
		{
			writer.decreaseIndent();
		}
		if (falseCase.isEmpty() == false)
		{
			if (trueCase.getTypeIndex() != JSParseNodeTypes.STATEMENTS)
			{
				next = getNextLexeme();
				if (next != null && next.typeIndex == JSTokenTypes.SEMICOLON)
				{
					printLexeme(next);
				}
			}
			if (this.codeoptions.insertNewLineBeforeElse)
			{
				printLineEnding(node);
				printIndent();
				next = getNextLexeme();
				if (next != null && next.typeIndex == JSTokenTypes.ELSE)
				{
					printLexeme(next);
					print(' ');
				}
			}
			else
			{
				next = getNextLexeme();
				// fix for #6355
				boolean tc = trueCase.getTypeIndex() != JSParseNodeTypes.STATEMENTS;
				if (tc)
				{
					writer.println();
					writer.printIndent();
				}
				if (next != null && next.typeIndex == JSTokenTypes.ELSE)
				{
					if (!tc)
					{
						print(' ');
					}
					printLexeme(next);
					print(' ');
				}
			}
			incIndent = false;
			boolean isNotCompacting = (!codeoptions.compactElseIf || falseCase.getTypeIndex() != JSParseNodeTypes.IF);
			if (!this.codeoptions.keepElseStatementOnSameLine
					|| (this.codeoptions.insertNewLineBeforeIf && falseCase.getTypeIndex() == JSParseNodeTypes.IF))
			{
				if (falseCase.getTypeIndex() != JSParseNodeTypes.STATEMENTS && isNotCompacting)
				{
					printLineEnding(node);
					incIndent = true;
				}
			}
			if (falseCase.getTypeIndex() == JSParseNodeTypes.RETURN && codeoptions.insertNewLineBeforeReturn)
			{
				incIndent = true;
			}
			if (incIndent)
			{
				writer.increaseIndent();
				printIndent();
			}
			if (falseCase.getTypeIndex() == JSParseNodeTypes.STATEMENTS)
			{
				if (codeoptions.keepGuardianClauseOnOneLine)
				{
					this.nextBlockNoNewLine = true;
				}
			}
			nextIfNoNewLine = !isNotCompacting;
			writeFormattedSource(falseCase, writer, true);
			nextIfNoNewLine = false;
			if (incIndent)
			{
				writer.decreaseIndent();
			}
		}
	}

	private void writeFunction(JSParseNode node, SourceWriter writer)
	{
		JSParseNode body;
		JSParseNode parameters = ((JSParseNode) node.getChild(0));
		body = ((JSParseNode) node.getChild(1));

		// output keyword
		printLexeme(node.getStartingLexeme());
		// output name, if we have one
		Lexeme next = getNextLexemeIfOfType(JSTokenTypes.IDENTIFIER);
		if (next != null)
		{
			print(' ');
			printLexeme(next);
		}

		// open parameter list
		next = getNextLexeme();
		if (next != null && next.typeIndex == JSTokenTypes.LPAREN)
		{
			printLexeme(next);
		}

		// output parameters, if we have any
		if (parameters.isEmpty() == false)
		{
			writeFormattedSource(parameters, writer, true);
		}

		// close parameter list
		next = getNextLexeme();
		if (next != null && next.typeIndex == JSTokenTypes.RPAREN)
		{
			printLexeme(next);
			if (codeoptions.addSpaceAfterFunctionDeclaration
					&& codeoptions.formatterBracePositionForMethodDecl == JSCodeFormatterOptions.END_LINE)
			{
				writer.print(' ');
			}
		}
		// output body
		nextBlockFunctionBody = true;
		writeFormattedSource(body, writer, true);
		// after function *
		int inc = 0;
		if (node.getParent().getParent() == null)
		{
			inc = 1;
		}

		// we add one more newline on top of what we were planning on
		// adding already. If next item is a semicolon or , don't print a new line
		next = getNextLexeme();
		if (next != null
				&& (next.typeIndex == JSTokenTypes.COMMA || next.typeIndex == JSTokenTypes.SEMICOLON
						|| next.typeIndex == JSTokenTypes.RPAREN || next.typeIndex == JSTokenTypes.LPAREN))
		{
			return;
		}
		else
		{
			printLineEnding(node, codeoptions.blankLinesBeforeMethod + inc, true);
		}
	}

	private void writeFunctionNode(JSFunctionNode lnode, SourceWriter writer)
	{
		writeCommon(lnode, writer, false);
	}

	private void writeLabelNode(JSLabelNode lnode, SourceWriter writer)
	{
		switch (lnode.getTypeIndex())
		{
			case JSParseNodeTypes.BREAK:
				printLexeme(lnode.getStartingLexeme());
				break;

			case JSParseNodeTypes.CONTINUE:
				printLexeme(lnode.getStartingLexeme());
				break;

			default:
				break;
		}

		JSParseNode child = (JSParseNode) lnode.getChild(0);
		if (child.isEmpty() == false)
		{
			print(" "); //$NON-NLS-1$
			writeFormattedSource(child, writer, true);
		}
	}

	private void writeSourceCommon(JSNaryNode node, SourceWriter writer)
	{

		if (node.getChildCount() > 0)
		{
			JSParseNode element = (JSParseNode) node.getChild(0);

			writeFormattedSource(element, writer, true);

			for (int i = 1; i < node.getChildCount(); i++)
			{
				element = (JSParseNode) node.getChild(i);

				Lexeme next = getNextLexemeIfOfType(JSTokenTypes.COMMA);
				if (next != null)
				{
					printLexeme(next);
					print(' ');
				}
				if (writer.getCurrentIndentLevel() == 0)
				{
					printIndent();
				}
				writeFormattedSource(element, writer, true);
			}
		}
	}

	private void writeNaryExpressionNode(JSNaryAndExpressionNode nnode, SourceWriter writer)
	{
		JSParseNode expression = (JSParseNode) nnode.getChild(0);

		switch (nnode.getTypeIndex())
		{
			case JSParseNodeTypes.CASE:
				printLexeme(nnode.getStartingLexeme());
				print(' ');
				nextBlockInCase = true;
				JSParseNode child = (JSParseNode) nnode.getChild(0);
				writeFormattedSource(child, writer, true);
				printNextLexemeIfOfType(JSTokenTypes.COLON);
				boolean printLine = true;
				if (nnode.getChildCount() > 1)
				{
					if (nnode.getChild(1).getTypeIndex() == JSParseNodeTypes.STATEMENTS)
					{
						printLine = false;
					}
				}
				if (codeoptions.indentStatementsCompareToCases && printLine)
				{
					writer.increaseIndent();
				}

				if (printLine && writer.getCurrentIndentLevel() > 0)
				{
					printLineEnding(nnode);
				}

				for (int i = 1; i < nnode.getChildCount(); i++)
				{
					JSParseNode statement = (JSParseNode) nnode.getChild(i);
					if (statement.getTypeIndex() == JSParseNodeTypes.BREAK)
					{
						if (!codeoptions.indentBreaksCompareToCases && codeoptions.indentStatementsCompareToCases)
						{
							writer.decreaseIndent();
						}
						else if (codeoptions.indentBreaksCompareToCases && !codeoptions.indentStatementsCompareToCases)
						{
							writer.increaseIndent();
						}
					}
					if (printLine)
					{
						printIndent();
					}
					writeFormattedSource(statement, writer, true);
					if (statement.getTypeIndex() == JSParseNodeTypes.BREAK)
					{
						if (!codeoptions.indentBreaksCompareToCases && codeoptions.indentStatementsCompareToCases)
						{
							writer.increaseIndent();
						}
						else if (codeoptions.indentBreaksCompareToCases && !codeoptions.indentStatementsCompareToCases)
						{
							writer.decreaseIndent();
						}
					}

					if (statement.getIncludesSemicolon())
					{
						Lexeme next = getNextLexemeIfOfType(JSTokenTypes.SEMICOLON);
						if (next != null)
						{
							printLexeme(next);
							printLineEnding(nnode);
						}
					}
					else
					{
						printLineEnding(nnode);
					}
				}
				if (codeoptions.indentStatementsCompareToCases && printLine)
				{
					writer.decreaseIndent();
				}
				nextBlockInCase = false;
				break;

			case JSParseNodeTypes.SWITCH:
				printLexeme(nnode.getStartingLexeme());
				print(' ');
				printNextLexemeIfOfType(JSTokenTypes.LPAREN);
				writeFormattedSource(expression, writer, true);
				printNextLexemeIfOfType(JSTokenTypes.RPAREN);
				int bracePosition = codeoptions.formatterBracePositionForBlockInSwitch;
				Lexeme next = getNextLexemeIfOfType(JSTokenTypes.LCURLY);
				if (bracePosition == JSCodeFormatterOptions.END_LINE)
				{
					if (next != null)
					{
						print(' ');
						printIndentIfNewLine();
						printLexeme(next);
					}
					printLineEnding(nnode);
				}
				else if (bracePosition == JSCodeFormatterOptions.NEXT_LINE)
				{
					printLineEnding(nnode);
					printIndent();
					if (next != null)
					{
						printLexeme(next);
					}
					printLineEnding(nnode);
				}
				else if (bracePosition == JSCodeFormatterOptions.NEXT_LINE_SHIFTED)
				{
					printLineEnding(nnode);
					writer.increaseIndent();
					printIndent();
					if (next != null)
					{
						printLexeme(next);
					}
					printLineEnding(nnode);
				}
				if (codeoptions.indentStatementsCompareToSwitch)
				{
					writer.increaseIndent();
				}
				for (int i = 1; i < nnode.getChildCount(); i++)
				{
					JSParseNode statement = (JSParseNode) nnode.getChild(i);
					int currentIndentLevel = writer.getCurrentIndentLevel();
					// if
					// (statement.getTypeIndex()==JSParseNodeTypes.DEFAULT&&currentIndentLevel>0){
					// printLineEnding(nnode);
					// }
					if (currentIndentLevel == 0)
					{
						printIndent();
					}
					writeFormattedSource(statement, writer, true);

				}
				if (codeoptions.indentStatementsCompareToSwitch)
				{
					writer.decreaseIndent();
				}

				printIndent();
				printNextLexemeIfOfType(JSTokenTypes.RCURLY);
				if (bracePosition == JSCodeFormatterOptions.NEXT_LINE_SHIFTED)
				{
					writer.decreaseIndent();
				}
				break;

			default:
				break;
		}
	}

	private void writeNaryNode(JSNaryNode nnode, SourceWriter writer)
	{
		if (nnode instanceof JSNaryAndExpressionNode)
		{
			JSNaryAndExpressionNode nenode = (JSNaryAndExpressionNode) nnode;
			writeNaryExpressionNode(nenode, writer);
			return;
		}

		int bracePosition = codeoptions.formatterBracePositionForBlock;
		if (this.nextBlockFunctionBody)
		{
			bracePosition = codeoptions.formatterBracePositionForMethodDecl;
		}
		if (this.nextBlockInCase)
		{
			bracePosition = codeoptions.formatterBracePositionForBlockInCase;
		}
		switch (nnode.getTypeIndex())
		{
			case JSParseNodeTypes.ARGUMENTS:
				writeSourceCommon(nnode, writer);
				break;

			case JSParseNodeTypes.ARRAY_LITERAL:
				printNextLexemeIfOfType(JSTokenTypes.LBRACKET);
				writeSourceCommon(nnode, writer);
				printNextLexemeIfOfType(JSTokenTypes.RBRACKET);
				break;

			case JSParseNodeTypes.COMMA:
				writeSourceCommon(nnode, writer);
				break;

			case JSParseNodeTypes.DEFAULT:
				writeDefaultNode(nnode, writer);
				break;

			case JSParseNodeTypes.OBJECT_LITERAL:
				writeObjectLiteral(nnode, writer);
				break;

			case JSParseNodeTypes.PARAMETERS:

				writeSourceCommon(nnode, writer);
				break;

			case JSParseNodeTypes.STATEMENTS:
				writeStatements(nnode, writer, bracePosition);
				break;

			case JSParseNodeTypes.VAR:
				printLexeme(nnode.getStartingLexeme());
				print(" "); //$NON-NLS-1$
				writeSourceCommon(nnode, writer);
				break;

			default:
				break;
		}
	}

	/**
	 * Prints an indent if the last character is either a new line or spaces after a new line. This method was designed
	 * to adjust the indent when a curly should be on the same line as a statement but can't be because of a // comment
	 * at the end of that line
	 */
	private void printIndentIfNewLine()
	{
		String string = writer.toString();
		int len = string.length() - 1;
		while (len > 0)
		{
			char c = string.charAt(len);
			if (Character.isWhitespace(c))
			{
				len--;
				if (c == '\n' || c == '\r')
				{
					try
					{
						StringBuffer buffer = writer.getBuffer();
						buffer.replace(len + 2, buffer.length(), ""); //$NON-NLS-1$
						printAdjustedIndent();
					}
					catch (Exception e)
					{
						// Do nothing and return
					}
					return;
				}
			}
			else
			{
				return;
			}
		}
	}

	private void writeStatements(JSNaryNode nnode, SourceWriter writer, int bracePosition)
	{
		boolean ne = nextBlockNoNewLine;
		boolean notDec = false;
		if (nnode.getChildCount() > 0)
		{
			int typeIndex = nnode.getChild(0).getTypeIndex();
			if (codeoptions.keepGuardianClauseOnOneLine)
			{
				nextReturnNoNewLine = nnode.getChildCount() == 1
						&& (typeIndex == JSParseNodeTypes.RETURN || typeIndex == JSParseNodeTypes.RETURN) ? nextBlockNoNewLine
						: false;
			}
		}
		if (nnode.isTopLevel() == false)
		{
			if (bracePosition == JSCodeFormatterOptions.END_LINE)
			{
				// This is for the case if end line is not possible since the line ended with a // comment
				printIndentIfNewLine();
				printNextLexemeIfOfType(JSTokenTypes.LCURLY);
			}
			else if (bracePosition == JSCodeFormatterOptions.NEXT_LINE)
			{
				if (writer.getLength() != 0)
				{
					printLineEnding(nnode);
					printIndent();
					printNextLexemeIfOfType(JSTokenTypes.LCURLY);
				}
				else
				{
					printNextLexemeIfOfType(JSTokenTypes.LCURLY);
				}
			}
			else if (bracePosition == JSCodeFormatterOptions.NEXT_LINE_SHIFTED)
			{
				if (writer.getLength() != 0)
				{
					printLineEnding(nnode);
					writer.increaseIndent();
					printIndent();
					printNextLexemeIfOfType(JSTokenTypes.LCURLY);
				}
				else
				{
					printNextLexemeIfOfType(JSTokenTypes.LCURLY);
				}
			}
			for (int a = 0; a < codeoptions.blankLinesInStartOfMethodBody; a++)
			{
				printLineEnding(nnode);
				printIndent();
			}
			if (nnode.getChildCount() > 0)
			{
				if ((nextBlockNoNewLine && nnode.getChildCount() == 1)
						&& ((nnode.getChild(0).getTypeIndex() == JSParseNodeTypes.RETURN || nnode.getChild(0)
								.getTypeIndex() == JSParseNodeTypes.THROW)))
				{
					notDec = true;
					writer.print(' ');
					nextBlockNoNewLine = false;
				}
				else
				{
					nextBlockNoNewLine = false;
					printLineEnding(nnode);
					if (nextBlockFunctionBody)
					{
						if (codeoptions.indentStatementsCompareToBody)
						{
							writer.increaseIndent();
						}
					}
					else
					{
						if (codeoptions.indentStatementsCompareToBlock)
						{
							writer.increaseIndent();
						}
					}
				}
			}
		}
		boolean tempHolder = nextBlockFunctionBody;
		boolean tempCase = nextBlockInCase;
		nextBlockFunctionBody = false;
		nextBlockInCase = false;

		for (int i = 0; i < nnode.getChildCount(); i++)
		{
			IParseNode node = nnode.getChild(i);
			if (i > 0)
			{
				nextReturnNoNewLine = false;
			}
			if (node instanceof JSParseNode)
			{
				JSParseNode statement = (JSParseNode) nnode.getChild(i);
				if (!nextReturnNoNewLine)
				{
					printAdjustedIndent();
				}
				boolean nr = nextReturnNoNewLine;
				if (statement.getTypeIndex() != JSParseNodeTypes.RETURN)
				{
					nextReturnNoNewLine = false;
				}
				writeFormattedSource(statement, writer, true);
				nextReturnNoNewLine = nr;
				Lexeme next = getNextLexeme();
				if (statement.getIncludesSemicolon())
				{
					if (!notDec)
					{
						printNextLexemeIfOfType(JSTokenTypes.SEMICOLON);
						if (writer.getCurrentIndentLevel() != 0)
						{
							printLineEnding(nnode);
						}
					}
				}
				else if (next != null && next.typeIndex == JSTokenTypes.SEMICOLON)
				{
					printLexeme(next);
					printLineEnding(nnode);
				}
				else
				{
					if (!notDec)
					{
						if (writer.getCurrentIndentLevel() != 0)
						{
							if (i < nnode.getChildCount() - 1)
							{
								if (!nnode.getChild(i + 1).isEmpty())
								{
									printLineEnding(nnode);
								}
							}
							else
							{
								printLineEnding(nnode);
							}
						}
					}
					else
					{
						printLexeme(next);
					}
				}

			}
			else
			{
				writeFormattedSource((JSParseNode) node, writer, true);

			}
		}
		nextBlockFunctionBody = tempHolder;
		nextBlockInCase = tempCase;
		if (nnode.isTopLevel() == false)
		{
			writeRcurly(nnode, writer, bracePosition, notDec);
		}
		nextBlockFunctionBody = false;
		nextBlockNoNewLine = ne;
	}

	private void writeRcurly(JSNaryNode nnode, SourceWriter writer, int bracePosition, boolean notDec)
	{
		if (nnode.getChildCount() > 0)
		{
			if (!notDec)
			{
				if (nextBlockFunctionBody)
				{
					if (codeoptions.indentStatementsCompareToBody)
					{
						writer.decreaseIndent();
					}
				}
				else
				{
					if (codeoptions.indentStatementsCompareToBlock)
					{
						writer.decreaseIndent();
					}
				}
				Lexeme next = getNextLexemeIfOfType(JSTokenTypes.RCURLY);
				if (next != null)
				{
					printIndent();
					printLexeme(next);
				}
				if (bracePosition == JSCodeFormatterOptions.NEXT_LINE_SHIFTED)
				{
					writer.decreaseIndent();
				}
			}
			else
			{
				Lexeme next = getNextLexemeIfOfType(JSTokenTypes.RCURLY);
				if (next != null)
				{
					if (!nextReturnNoNewLine)
					{
						printIndent();
					}

					print(' ');
					printLexeme(next);
				}
				else
				{
					Lexeme nextLexeme = getNextLexeme();
					if (nextLexeme != null)
					{
						printNextLexemeIfOfType(JSTokenTypes.SEMICOLON);
						Lexeme nextLexeme2 = getNextLexeme();
						if (nextLexeme2 != null)
						{
							print(' ');
							printNextLexemeIfOfType(JSTokenTypes.RCURLY);
						}
					}
				}
				if (bracePosition == JSCodeFormatterOptions.NEXT_LINE_SHIFTED)
				{
					writer.decreaseIndent();
				}
			}
		}
		else
		{
			writeClosePartRCurly(nnode, writer, bracePosition);
		}
	}

	private void writeClosePartRCurly(JSNaryNode nnode, SourceWriter writer, int bracePosition)
	{
		if (writer.getCurrentIndentLevel() != 0)
		{
			printLineEnding(nnode);
		}
		Lexeme next = getNextLexemeIfOfType(JSTokenTypes.RCURLY);
		if (next != null)
		{
			printIndent();
			printLexeme(next);
		}
		if (bracePosition == JSCodeFormatterOptions.NEXT_LINE_SHIFTED)
		{
			writer.decreaseIndent();
		}
	}

	private void writeObjectLiteral(JSNaryNode nnode, SourceWriter writer)
	{
		int bracePosition;
		bracePosition = codeoptions.formatterBracePositionForArrayInitializer;
		if (nnode.getChildCount() == 0)
		{
			if (codeoptions.keepEmptyArrayInitializerOnOneLine)
			{
				bracePosition = JSCodeFormatterOptions.END_LINE;
			}
		}
		Lexeme next = getNextLexeme();
		if (bracePosition == JSCodeFormatterOptions.END_LINE)
		{
			printIndentIfNewLine();
			printNextLexemeIfOfType(JSTokenTypes.LCURLY);
		}
		else if (bracePosition == JSCodeFormatterOptions.NEXT_LINE)
		{
			printLineEnding(nnode);
			printIndent();
			printNextLexemeIfOfType(JSTokenTypes.LCURLY);
		}
		else if (bracePosition == JSCodeFormatterOptions.NEXT_LINE_SHIFTED)
		{
			printLineEnding(nnode);
			writer.increaseIndent();
			printIndent();
			printNextLexemeIfOfType(JSTokenTypes.LCURLY);
		}

		if (nnode.getChildCount() > 0)
		{

			printLineEnding(nnode);
			writer.increaseIndent();
			JSParseNode element = (JSParseNode) nnode.getChild(0);

			printIndent();

			writeFormattedSource(element, writer, true);
			for (int i = 1; i < nnode.getChildCount(); i++)
			{
				if (writer.getCurrentIndentationLevel() == 0)
				{
					printIndent();
				}
				next = getNextLexeme();
				if (next != null && next.typeIndex == JSTokenTypes.COMMA)
				{
					printLexeme(next);
				}
				if (writer.getCurrentIndentLevel() != 0)
				{
					printLineEnding(nnode);
				}

				element = (JSParseNode) nnode.getChild(i);
				printIndent();
				writeFormattedSource(element, writer, false);
			}
			next = getNextLexeme();
			if (next != null && next.typeIndex == JSTokenTypes.COMMA)
			{
				printLexeme(next);
			}

			writer.decreaseIndent();
			if (writer.getCurrentIndentLevel() != 0)
			{
				printLineEnding(nnode);
			}
			next = getNextLexeme();
			if (next != null && next.typeIndex == JSTokenTypes.RCURLY)
			{
				printIndent();
				printLexeme(next);
			}
			if (bracePosition == JSCodeFormatterOptions.NEXT_LINE_SHIFTED)
			{
				writer.decreaseIndent();
			}
		}
		else
		{
			next = getNextLexeme();
			if (next != null && next.typeIndex == JSTokenTypes.RCURLY)
			{
				printLexeme(next);
			}
			if (bracePosition == JSCodeFormatterOptions.NEXT_LINE_SHIFTED)
			{
				writer.decreaseIndent();
			}
		}
	}

	private void writeDefaultNode(JSNaryNode nnode, SourceWriter writer)
	{
		printLexeme(nnode.getStartingLexeme());
		printNextLexemeIfOfType(JSTokenTypes.COLON);
		if (codeoptions.indentStatementsCompareToCases)
		{
			writer.increaseIndent();
		}
		if (writer.getCurrentIndentLevel() > 0)
		{
			printLineEnding(nnode);
		}

		for (int i = 0; i < nnode.getChildCount(); i++)
		{
			JSParseNode statement = (JSParseNode) nnode.getChild(i);
			printIndent();
			writeFormattedSource(statement, writer, true);
			if (statement.getIncludesSemicolon())
			{
				Lexeme next = getNextLexemeIfOfType(JSTokenTypes.SEMICOLON);
				if (next != null)
				{
					printLexeme(next);
					printLineEnding(nnode);
				}
			}
			else
			{
				printLineEnding(nnode);
			}
		}

		if (codeoptions.indentStatementsCompareToCases)
		{
			writer.decreaseIndent();
		}
	}

	private String[] splitOnLines(String comment)
	{
		String[] result = null;
		ArrayList<String> lines = new ArrayList<String>();
		boolean lastSlash = false;
		int lastPos = 0;
		for (int a = 0; a < comment.length(); a++)
		{
			char c = comment.charAt(a);
			if (c == '\n' || c == '\r')
			{
				if (!lastSlash)
				{
					lastSlash = true;
					lines.add(comment.substring(lastPos, a));
					lastPos = a + 1;
				}
				continue;
			}
			lastSlash = false;
		}
		lines.add(comment.substring(lastPos, comment.length()));
		result = new String[lines.size()];
		lines.toArray(result);
		return result;
	}

	private void writePrimitiveNode(JSPrimitiveNode pnode, SourceWriter writer)
	{
		printLexeme(pnode.getStartingLexeme());
	}

	private void writeStringNode(JSStringNode snode, SourceWriter writer)
	{
		printLexeme(snode.getStartingLexeme());
	}

	private void writeTextNode(JSTextNode tnode, SourceWriter writer)
	{
		writeCommon(tnode, writer, true);
	}

	private void writeSourceUnary(JSUnaryOperatorNode onode, SourceWriter writer, boolean appendTrailing,
			boolean useSpace, boolean operatorFirst)
	{
		if (operatorFirst)
		{
			printLexeme(onode.getStartingLexeme());
		}

		if (useSpace)
		{
			print(' ');
		}
		JSParseNode operand = (JSParseNode) onode.getChild(0);
		writeFormattedSource(operand, writer, appendTrailing);
		if (!operatorFirst)
		{
			printLexeme(onode.getEndingLexeme());
		}
	}

	private void writeUnaryOperatorNode(JSUnaryOperatorNode unode, SourceWriter writer)
	{
		switch (unode.getTypeIndex())
		{
			case JSParseNodeTypes.GROUP:
				JSParseNode operand = (JSParseNode) unode.getChild(0);
				printNextLexemeIfOfType(JSTokenTypes.LPAREN);
				writeFormattedSource(operand, writer, true);
				printNextLexemeIfOfType(JSTokenTypes.RPAREN);
				break;

			case JSParseNodeTypes.RETURN:
				if (codeoptions.insertNewLineBeforeReturn && !nextReturnNoNewLine)
				{
					printLineEnding(unode);
					if (writer.getCurrentIndentationLevel() == 0)
					{
						writer.printIndent();
					}
				}
				printNextLexemeIfOfType(JSTokenTypes.RETURN);
				printNextLexemeIfOfType(JSTokenTypes.SEMICOLON);
				int previous = codeoptions.formatterBracePositionForArrayInitializer;
				codeoptions.formatterBracePositionForArrayInitializer = JSCodeFormatterOptions.END_LINE;
				if (unode.getChildCount() > 0)
				{
					operand = (JSParseNode) unode.getChild(0);
					if (operand.getTypeIndex() != JSParseNodeTypes.EMPTY)
					{
						print(" "); //$NON-NLS-1$
						writeFormattedSource(operand, writer, true);
					}
				}
				else
				{
					Lexeme next = getNextLexemeIfOfType(JSTokenTypes.SEMICOLON);
					if (next != null)
					{
						print(' ');
						printLexeme(next);
					}
				}
				codeoptions.formatterBracePositionForArrayInitializer = previous;
				break;

			case JSParseNodeTypes.TYPEOF:
				operand = (JSParseNode) unode.getChild(0);
				printNextLexemeIfOfType(JSTokenTypes.TYPEOF);

				if (operand.getTypeIndex() != JSParseNodeTypes.GROUP)
				{
					print(" "); //$NON-NLS-1$
				}
				writeFormattedSource(operand, writer, true);
				break;

			case JSParseNodeTypes.DELETE:
			case JSParseNodeTypes.THROW:
			case JSParseNodeTypes.VOID:
			case JSParseNodeTypes.BITWISE_NOT:
				writeSourceUnary(unode, writer, true, true, true);
				break;
			case JSParseNodeTypes.LOGICAL_NOT:
			case JSParseNodeTypes.NEGATE:
			case JSParseNodeTypes.POSITIVE:
			case JSParseNodeTypes.PRE_DECREMENT:
			case JSParseNodeTypes.PRE_INCREMENT:
				writeSourceUnary(unode, writer, true, false, true);
				break;

			case JSParseNodeTypes.POST_DECREMENT:
			case JSParseNodeTypes.POST_INCREMENT:
				writeSourceUnary(unode, writer, true, false, false);
				break;

			default:
				break;
		}
	}

	private void writeVarNode(JSVarNode vnode, SourceWriter writer)
	{
		writeCommon(vnode, writer, true);
	}

	/**
	 * @see com.aptana.ide.editors.unified.ICodeFormatter#createNestedMark()
	 */
	public String createNestedMark()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.ICodeFormatter#handlesNested()
	 */
	public boolean handlesNested()
	{
		return true;
	}

	/**
	 * @return indent level of writer
	 */
	public int getCurrentIndentationLevel()
	{
		int iLevel = 0;
		String currentIndentationString = writer.getCurrentIndentationString();
		for (int a = 0; a < currentIndentationString.length(); a++)
		{
			char c = currentIndentationString.charAt(a);
			if (c == ' ')
			{
				iLevel++;
			}
			else if (c == '\t')
			{
				iLevel += codeoptions.tabSize;
			}
		}
		return iLevel;
	}
}
