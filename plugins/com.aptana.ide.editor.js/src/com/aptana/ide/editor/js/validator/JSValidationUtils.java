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
package com.aptana.ide.editor.js.validator;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editor.js.lexing.JSTokenTypes;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.js.parsing.JSParser;
import com.aptana.ide.editor.js.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.editors.unified.errors.IFileError;
import com.aptana.ide.editors.unified.errors.UnifiedErrorReporter;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.lexer.LexerException;
import com.aptana.ide.lexer.Range;
import com.aptana.ide.parsing.IParseState;
import com.aptana.ide.parsing.IParser;
import com.aptana.ide.parsing.ParserInitializationException;

/**
 * Utils for JS validation.
 * 
 * @author munch
 */
public final class JSValidationUtils
{

	/**
	 * Java Script "//validate" comment.
	 */
	private static final String JS_VALIDATE_COMMENT = "validate"; //$NON-NLS-1$

	/**
	 * Java Script "//novalidate" comment.
	 */
	private static final String JS_NOVALIDATE_COMMENT = "novalidate"; //$NON-NLS-1$

	private static JSParser parser;
	private static IParseState parseState;

	/**
	 * Filters errors by "novalidate" comment areas.
	 * 
	 * @param errors -
	 *            error to filter. must be sorted by position in ascending order.
	 * @param source -
	 *            JS source code.
	 * @return filtered errors, sorted by position in ascending order.
	 * @throws ParserInitializationException
	 *             IF parsing throws this exception.
	 * @throws LexerException
	 *             IF parsing throws this exception.
	 */
	public static IFileError[] filterErrorsByNovalidate(IFileError[] errors, String source)
			throws ParserInitializationException, LexerException
	{
		// make sure we have a parser
		if (parser == null)
		{
			parser = new JSParser();
		}

		// make sure we have a parse state
		if (parseState == null)
		{
			parseState = parser.createParseState(null);
		}

		// apply edit
		parseState.setEditState(source, source, 0, 0);

		// parse
		parser.parse(parseState);

		// grab lexemes
		LexemeList lexemes = parseState.getLexemeList();

		return filterErrorsByNovalidate(errors, lexemes, source);
	}

	static ArrayList<ICleanup> cleanup = new ArrayList<ICleanup>();

	static
	{
		IConfigurationElement[] configurationElementsFor = Platform.getExtensionRegistry().getConfigurationElementsFor(
				"com.aptana.ide.editor.js.languageCleanup"); //$NON-NLS-1$
		for (IConfigurationElement e : configurationElementsFor)
		{
			try
			{
				ICleanup createExecutableExtension = (ICleanup) e.createExecutableExtension("class"); //$NON-NLS-1$
				cleanup.add(createExecutableExtension);
			}
			catch (CoreException e1)
			{
				IdeLog.log(JSPlugin.getDefault(), IStatus.ERROR, "unable to instantiate cleanup object", e1); //$NON-NLS-1$
			}
		}
	}

	/**
	 * @param source
	 * @param reporter
	 * @param ranges
	 * @return filtered errors
	 */
	protected static IFileError[] filterErrors(String source, UnifiedErrorReporter reporter, ArrayList<Range> ranges)
	{
		IFileError[] errors = reporter.getErrors();

		if (JSPlugin.getDefault().getPreferenceStore().getBoolean(IPreferenceConstants.ENABLE_NO_VALIDATE_COMMENT))
		{
			IParser ps = LanguageRegistry.getParser(JSMimeType.MimeType);
			IParseState createParseState = ps.createParseState(null);
			createParseState.setEditState(source, source, 0, 0);

			ArrayList<IFileError> ls = new ArrayList<IFileError>();
			try
			{
				ps.parse(createParseState);
			}
			catch (RuntimeException e1)
			{
				IdeLog.logError(JSPlugin.getDefault(), "parse exception while lexing cleared JS source", e1); //$NON-NLS-1$			
			}
			catch (LexerException e1)
			{
				IdeLog.logError(JSPlugin.getDefault(), "lexing exception while lexing cleared JS source", e1); //$NON-NLS-1$
			}
			catch (java.text.ParseException e)
			{
				IdeLog.logError(JSPlugin.getDefault(), "parse exception while lexing cleared JS source", e); //$NON-NLS-1$
			}
			LexemeList lexemeList = createParseState.getLexemeList();
			l2: for (IFileError e : errors)
			{
				for (Range r : ranges)
				{
					Lexeme ceilingLexeme = lexemeList.getCeilingLexeme(r.getEndingOffset());
					int le = ceilingLexeme.getEndingOffset() + 1;
					if (e.getOffset() < le + 1 && e.getOffset() > r.getStartingOffset())
					{
						continue l2;
					}
				}
				ls.add(e);
			}
			errors = new IFileError[ls.size()];
			ls.toArray(errors);
		}
		return errors;
	}

	/**
	 * @param source
	 * @param toFill
	 *            collection for storing not js ranges
	 * @return - String
	 * @throws LexerException
	 * @throws ParserInitializationException
	 */
	public static String filterPIInstructions(String source, ArrayList<Range> toFill) throws LexerException,
			ParserInitializationException
	{
		StringBuilder bld = new StringBuilder(source);
		for (ICleanup c : cleanup)
		{
			List<Range> notJsCode = c.getNotJsCode(source);
			for (Range r : notJsCode)
			{
				toFill.add(r);
				StringBuilder spaces = new StringBuilder();
				int startingOffset = r.getStartingOffset();
				int endingOffset = r.getEndingOffset();
				boolean found=false;
				for (int a=startingOffset;a>=0;a--){
					char ch=source.charAt(a);
					if (ch=='\r'||ch=='\n'){
						startingOffset=a;
						found=true;
						break;
					}
				}
				if (!found){
					startingOffset=0;
				}
				found=false;
				for (int a=endingOffset;a<source.length();a++){
					char ch=source.charAt(a);
					if (ch=='\r'||ch=='\n'){
						endingOffset=a;
						found=true;
						break;
					}
				}
				if (!found){
					endingOffset=source.length()-1;
				}
				for (int a = startingOffset; a < endingOffset; a++)
				{
					spaces.append(' ');
				}
				bld.replace(startingOffset, endingOffset, spaces.toString());
			}
		}
		return bld.toString();
	}

	/**
	 * Filters errors by "novalidate" comment areas.
	 * 
	 * @param errors -
	 *            error to filter. must be sorted by position in ascending order.
	 * @param lexemes -
	 *            lexemes list.
	 * @param source -
	 *            source.
	 * @return filtered errors, sorted by position in ascending order.
	 */
	public static IFileError[] filterErrorsByNovalidate(IFileError[] errors, LexemeList lexemes, String source)
	{
		if (lexemes.size() < 2)
		{
			return errors;
		}

		List<IFileError> filteredErrors = new LinkedList<IFileError>();

		// current unchecked error index
		int currentErrorIndex = 0;

		// validation state
		boolean validating = true;

		// start index of the area under validation
		int validateStartIndex = 0;

		for (int i = 0; i < lexemes.size() - 1; i++)
		{
			Lexeme currentLexeme = lexemes.get(i);
			Lexeme nextLexeme = lexemes.get(i + 1);
			if (currentLexeme.typeIndex == JSTokenTypes.COMMENT && nextLexeme.typeIndex == JSTokenTypes.CDO
					&& nextLexeme.getText() != null)
			{
				if (nextLexeme.getText().startsWith(JS_NOVALIDATE_COMMENT) && validating)
				{
					// entering novalidate area
					validating = false;

					// copying errors from the area under validation to the result list
					currentErrorIndex = copyErrorsFromArea(errors, filteredErrors, currentErrorIndex,
							validateStartIndex, currentLexeme.getStartingOffset());
				}
				else if (nextLexeme.getText().startsWith(JS_VALIDATE_COMMENT) && !validating)
				{
					// leaving novalidate area
					validating = true;

					// novalidate area ended, validate area started
					validateStartIndex = currentLexeme.getEndingOffset();

					// skipping all errors that belongs to the same line, current lexeme does
					// if line contains whitespaces only
					currentErrorIndex = skipEmptyLineErrors(currentErrorIndex, errors, nextLexeme, source);
				}
			}
		}

		if (validating)
		{
			// copying errors from the area under validation to the result list
			copyErrorsFromArea(errors, filteredErrors, currentErrorIndex, validateStartIndex, lexemes
					.getAffectedRegion().getEndingOffset());
		}

		IFileError[] toReturn = new IFileError[filteredErrors.size()];
		filteredErrors.toArray(toReturn);
		return toReturn;
	}

	/**
	 * Skipping all errors that belongs to the same line, current lexeme does if line contains whitespaces only.
	 * 
	 * @param errorIndex -
	 *            error index to start with.
	 * @param errors -
	 *            errors.
	 * @param lexeme -
	 *            current lexeme.
	 * @param source -
	 *            source
	 * @return new error index
	 */
	private static int skipEmptyLineErrors(int errorIndex, IFileError[] errors, Lexeme lexeme, String source)
	{

		int endLineOffset = getEndLineIndex(lexeme.getEndingOffset(), source);

		// checking if only whitespace symbols are between lexeme end and new line
		for (int i = lexeme.getEndingOffset(); i < endLineOffset; i++)
		{
			char ch = source.charAt(i);
			// non-whitespace met. getting out.
			if (!Character.isWhitespace(ch))
			{
				return errorIndex;
			}
		}

		// skipping errors that are between lexeme end and newline
		for (int i = errorIndex; i < errors.length; i++)
		{
			IFileError currentError = errors[i];
			if (currentError.getOffset() >= endLineOffset)
			{
				return i;
			}
		}
		return errors.length;
	}

	/**
	 * Gets closest new line of EOF offset.
	 * 
	 * @param startOffset -
	 *            offset to start search from.
	 * @param source -
	 *            source.
	 * @return closest new line of end of file offset.
	 */
	private static int getEndLineIndex(int startOffset, String source)
	{
		StringReader reader = new StringReader(source);
		try
		{
			reader.skip(startOffset);

			int currentChar;
			int currentOffset = 0;
			int lineNumber = 1;

			while ((currentChar = reader.read()) != -1)
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
						if (currentOffset + 1 < source.length())
						{
							return currentOffset + startOffset + 1;
						}
						lineNumber++;
						break;
					default:
						break;
				}

				currentOffset++;
			}
		}
		catch (IOException ex)
		{
			// should not happen
			IdeLog.logError(JSPlugin.getDefault(), "Exception searching for the line end", ex); //$NON-NLS-1$
		}

		return source.length();
	}

	/**
	 * Copies errors that belong to the area starting from the index specified.
	 * 
	 * @param from -
	 *            errors source.
	 * @param to -
	 *            errors destination.
	 * @param errorsStartIndex -
	 *            errors start index.
	 * @param areaStartIndex -
	 *            area start index.
	 * @param areaEndIndex -
	 *            area end index.
	 * @return index of the first error that was out of the area.
	 */
	private static int copyErrorsFromArea(IFileError[] from, List<IFileError> to, int errorsStartIndex, int areaStartIndex,
			int areaEndIndex)
	{
		if (errorsStartIndex < from.length)
		{
			int errorIndex = errorsStartIndex;
			for (; errorIndex < from.length; errorIndex++)
			{
				IFileError currentError = from[errorIndex];
				if (currentError.getOffset() >= areaStartIndex)
				{
					if (currentError.getOffset() <= areaEndIndex)
					{
						// adding error
						to.add(currentError);
					}
					else
					{
						// next error is out of scope
						break;
					}
				}
			}
			errorsStartIndex = errorIndex;
		}
		return errorsStartIndex;
	}

	/**
	 * JSValidationUtils private constructor.
	 */
	private JSValidationUtils()
	{
	}
}
