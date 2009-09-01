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
package com.aptana.ide.editors.unified;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.StringUtils;
import com.aptana.ide.editors.UnifiedEditorsPlugin;
import com.aptana.ide.io.SourceWriter;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.IParser;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public abstract class BaseFormatter implements ICodeFormatter
{

	/**
	 * Display errors to the user
	 */
	protected boolean displayErrors = true;

	/**
	 * @see com.aptana.ide.editors.unified.ICodeFormatter#setDisplayErrors(boolean)
	 */
	public void setDisplayErrors(boolean display)
	{
		this.displayErrors = display;
	}

	/**
	 * @see com.aptana.ide.editors.unified.ICodeFormatter#shouldDisplayErrors()
	 */
	public boolean shouldDisplayErrors()
	{
		return this.displayErrors;
	}

	/**
	 * @param originalList
	 * @param parser
	 * @param original
	 * @param formatted
	 * @param ignorableSizeNodes
	 * @param ignorableCountNodes
	 * @return - true if formatting correct, false otherwise
	 */
	public boolean isFormattingCorrect(LexemeList originalList, IParser parser, String original, String formatted,
			int[] ignorableSizeNodes, int[] ignorableCountNodes)
	{
		boolean isCorrect = true;
		IParseState createParseState = parser.createParseState(null);

		createParseState.setEditState(formatted, formatted, 0, 0);
		try
		{
			parser.parse(createParseState);
		}
		catch (Exception e)
		{
			IdeLog.logInfo(UnifiedEditorsPlugin.getDefault(), Messages.BaseFormatter_INF_ErrorParsingFormattedContent, e);
			isCorrect = false;
			return isCorrect;
		}
		LexemeList modifiedList = createParseState.getLexemeList();
		if (ignorableCountNodes != null && ignorableCountNodes.length > 0)
		{
			for (int j = 0; j < modifiedList.size(); j++)
			{
				for (int i = 0; i < ignorableCountNodes.length; i++)
				{
					if (modifiedList.get(j).typeIndex == ignorableCountNodes[i])
					{
						modifiedList.remove(j);
						break;
					}
				}
			}
			for (int j = 0; j < originalList.size(); j++)
			{
				for (int i = 0; i < ignorableCountNodes.length; i++)
				{
					if (originalList.get(j).typeIndex == ignorableCountNodes[i])
					{
						originalList.remove(j);
						break;
					}
				}
			}
		}
		int modifiedSize = modifiedList.size();
		int originalSize = originalList.size();
		
		if (modifiedSize != originalSize)
		{
			IdeLog.logInfo(UnifiedEditorsPlugin.getDefault(), StringUtils.format(
					Messages.BaseFormatter_INF_NewLexemeListSizeMismatch, new Object[] {
							"" + modifiedSize, "" + originalSize })); //$NON-NLS-1$ //$NON-NLS-2$
			isCorrect = false;
		}

		for (int i = 0; i < modifiedList.size(); i++)
		{
			Lexeme oLexeme = originalList.get(i);
			String oText = oLexeme.getText();
			Lexeme mLexeme = modifiedList.get(i);
			String mText = mLexeme.getText();
			boolean ignore = false;
			if (ignorableSizeNodes != null && ignorableSizeNodes.length > 0)
			{
				for (int j = 0; j < ignorableSizeNodes.length; j++)
				{
					if (mLexeme.typeIndex == ignorableSizeNodes[j] && oLexeme.typeIndex == ignorableSizeNodes[j])
					{
						ignore = true;
						break;
					}
				}
			}
			if (!ignore && !textIsEqual(mText, oText))
			{
				IdeLog.logInfo(UnifiedEditorsPlugin.getDefault(), StringUtils.format(
						Messages.BaseFormatter_INF_NewLexemeMismatch, new Object[] { printReturns(stripSpace(mText)), printReturns(stripSpace(oText)) }));
				isCorrect = false;
				break;
			}
		}

		if (!isCorrect)
		{
			logError(original, formatted);
		}

		return isCorrect;
	}

	/**
	 * Compares the text of two nodes
	 * 
	 * @param text
	 * @param text2
	 * @return - true if equal, false otherwise
	 */
	private boolean textIsEqual(String text, String text2)
	{
		String newMText = StringUtils.replace(text, "\r\n", ""); //$NON-NLS-1$ //$NON-NLS-2$
		newMText = StringUtils.replace(newMText, "\r", ""); //$NON-NLS-1$ //$NON-NLS-2$
		newMText = StringUtils.replace(newMText, "\n", ""); //$NON-NLS-1$ //$NON-NLS-2$
		newMText = StringUtils.replace(newMText, " ", ""); //$NON-NLS-1$ //$NON-NLS-2$
		newMText = StringUtils.replace(newMText, "\t", ""); //$NON-NLS-1$ //$NON-NLS-2$

		String newOText = StringUtils.replace(text2, "\r\n", ""); //$NON-NLS-1$ //$NON-NLS-2$
		newOText = StringUtils.replace(newOText, "\r", ""); //$NON-NLS-1$ //$NON-NLS-2$
		newOText = StringUtils.replace(newOText, "\n", ""); //$NON-NLS-1$ //$NON-NLS-2$
		newOText = StringUtils.replace(newOText, " ", ""); //$NON-NLS-1$ //$NON-NLS-2$
		newOText = StringUtils.replace(newOText, "\t", ""); //$NON-NLS-1$ //$NON-NLS-2$

		return newMText.equals(newOText);
	}

	/**
	 * Strips carriage returns
	 * 
	 * @param text
	 * @return - normalizes carraige returns to \n
	 */
	public String normalizeCarriageReturns(String text)
	{
		String newMText = StringUtils.replace(text, "\r\n", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		return StringUtils.replace(newMText, "\r", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Strip the spaces from text
	 * 
	 * @param text
	 * @return - text with spaces and tabs removed
	 */
	private String stripSpace(String text)
	{
		String newText = StringUtils.replace(text, " ", "\\_"); //$NON-NLS-1$ //$NON-NLS-2$
		return StringUtils.replace(newText, "\t", "\\t"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Strip the spaces from text
	 * 
	 * @param text
	 * @return - text with spaces and tabs removed
	 */
	private String printReturns(String text)
	{
		String newText = StringUtils.replace(text, "\r", "\\r"); //$NON-NLS-1$ //$NON-NLS-2$
		return StringUtils.replace(newText, "\n", "\\n"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Logs an error
	 * 
	 * @param original
	 * @param formatted
	 */
	protected void logError(String original, String formatted)
	{
		original = printReturns(original);
		formatted = printReturns(formatted);
		IdeLog.logError(UnifiedEditorsPlugin.getDefault(), StringUtils.format(
				Messages.BaseFormatter_ERR_FormattingFailure, new String[] {
						stripSpace(original), stripSpace(formatted) }));
		if (displayErrors)
		{
			MessageDialog
					.openInformation(
							Display.getDefault().getActiveShell(),
							Messages.BaseFormatter_TTL_ErrorFormatting,
							Messages.BaseFormatter_INF_FormattingErrorDetails);
		}
	}

	/**
	 * Is the lexeme of the specified type?
	 * 
	 * @param lexeme
	 * @param type
	 * @return lexeme type
	 */
	public boolean isLexemeOfType(Lexeme lexeme, int type)
	{
		return lexeme != null && lexeme.typeIndex == type;
	}

	/**
	 * @param writer
	 * @param source2
	 * @param formatted
	 * @return - string
	 */
	protected String getStartLineBreaks(SourceWriter writer, String source2, String formatted)
	{
		String oldWhitespace = StringUtils.findStartWhitespace(source2);
		String newWhitespace = StringUtils.findStartWhitespace(formatted);
		int oldNewLines = StringUtils.getNumberOfNewlines(oldWhitespace);
		int newNewLines = StringUtils.getNumberOfNewlines(newWhitespace);

		String toAppend = ""; //$NON-NLS-1$
		if (oldNewLines > newNewLines)
		{
			int diff = oldNewLines - newNewLines;
			for (int i = 0; i < diff; i++)
			{
				toAppend += writer.getCurrentIndentationString() + writer.getLineDelimeter();
			}
		}

		return toAppend;
	}

	/**
	 * @param writer
	 * @param source2
	 * @param formatted
	 * @return - string
	 */
	protected String getEndLineBreaks(SourceWriter writer, String source2, String formatted)
	{
		String oldWhitespace = StringUtils.findEndWhitespace(source2);
		String newWhitespace = StringUtils.findEndWhitespace(formatted);
		int oldNewLines = StringUtils.getNumberOfNewlines(oldWhitespace);
		int newNewLines = StringUtils.getNumberOfNewlines(newWhitespace);

		String toAppend = ""; //$NON-NLS-1$
		if (oldNewLines > newNewLines)
		{
			int diff = oldNewLines - newNewLines;
			for (int i = 0; i < diff; i++)
			{
				toAppend += writer.getCurrentIndentationString() + writer.getLineDelimeter();
			}
		}

		return toAppend;
	}
}
