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
package com.aptana.ide.editors.unified.errors;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.IMarker;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import com.aptana.ide.editors.unified.IFileSourceProvider;

/**
 * This is the default error reporter for JavaScript.
 * 
 * @author Norris Boyd
 */
public class UnifiedErrorReporter implements ErrorReporter
{
	IFileSourceProvider sourceProvider;
	ArrayList errors = new ArrayList();

	/**
	 * UnifiedErrorReporter
	 * 
	 * @param sourceProvider
	 */
	public UnifiedErrorReporter(IFileSourceProvider sourceProvider)
	{
		this.sourceProvider = sourceProvider;
	}

	/**
	 * @see org.mozilla.javascript.ErrorReporter#warning(java.lang.String, java.lang.String, int, java.lang.String, int)
	 */
	public void warning(String message, String sourceURI, int line, String lineText, int lineOffset)
	{
		add(IMarker.SEVERITY_WARNING, message, sourceURI, line, lineText, lineOffset);
	}

	/**
	 * @see org.mozilla.javascript.ErrorReporter#error(java.lang.String, java.lang.String, int, java.lang.String, int)
	 */
	public void error(String message, String sourceURI, int line, String lineText, int lineOffset)
	{
		add(IMarker.SEVERITY_ERROR, message, sourceURI, line, lineText, lineOffset);
	}

	
	/**
	 * Add informational message
	 * @param message 
	 * @param sourceURI 
	 * @param line 
	 * @param lineText 
	 * @param lineOffset 
	 */
	public void info(String message, String sourceURI, int line, String lineText, int lineOffset)
	{
		add(IMarker.SEVERITY_INFO, message, sourceURI, line, lineText, lineOffset);
	}
	
	/**
	 * @see org.mozilla.javascript.ErrorReporter#runtimeError(java.lang.String, java.lang.String, int, java.lang.String,
	 *      int)
	 */
	public EvaluatorException runtimeError(String message, String sourceURI, int line, String lineText, int lineOffset)
	{
		// display(message, sourceURI, line, lineText, lineOffset);

		return new EvaluatorException(message, sourceURI, line, lineText, lineOffset);
	}

	/**
	 * add
	 * 
	 * @param severity
	 * @param message
	 * @param sourceURI
	 * @param line
	 * @param lineText
	 * @param lineOffset
	 */
	protected void add(final int severity, String message, String sourceURI, int line, String lineText, int lineOffset)
	{
		FileError e = new FileError();

		message = cleanupMessage(message);

		int docLen = sourceProvider.getSourceLength();
		int totalLines = sourceProvider.getLineOfOffset(docLen) + 1;
		if (line > totalLines)
		{
			line = totalLines;
		}
		else if (line < 1)
		{
			line = 1;
		}
		int charLineOffset = sourceProvider.getLineOffset(line - 1);
		int lineOfOffset = sourceProvider.getLineOfOffset(charLineOffset);

		e.setLineNumber(lineOfOffset + 1); // LINE_NUMBER
		e.setOffset(charLineOffset + lineOffset); // CHAR_START
		e.setLength(1); // CHAR_END

		e.setSeverity(severity); // SEVERITY
		e.setMessage(message); // MESSAGE
		e.setFileName(sourceURI);

		errors.add(e);
	}

	/**
	 * cleanupMessage
	 * 
	 * @param message
	 * @return String
	 */
	private String cleanupMessage(String message)
	{
		if ("undefined labe".equals(message)) //$NON-NLS-1$
		{
			return message + "l"; //$NON-NLS-1$
		}
		else
		{
			return message;
		}
	}

	/**
	 * getErrors
	 * 
	 * @return IFileError[]
	 */
	public IFileError[] getErrors()
	{
		return (IFileError[]) errors.toArray(new IFileError[errors.size()]);
	}
	
	/**
	 * addErrors
	 * @param errs The array of errors to add
	 */
	public void addErrors(IFileError[] errs)
	{
		errors.addAll(Arrays.asList(errs));
	}
}
