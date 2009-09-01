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
package com.aptana.ide.editor.xml.validator;

import java.io.ByteArrayInputStream;

import com.aptana.ide.editors.unified.IFileSourceProvider;
import com.aptana.ide.editors.unified.colorizer.ColorizerReader;
import com.aptana.ide.editors.unified.errors.IFileError;
import com.aptana.ide.editors.unified.errors.UnifiedErrorReporter;
import com.aptana.ide.editors.validator.ValidatorBase;
import com.aptana.xml.IErrorHandler;

/**
 * @author Kevin Lindsey
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class ColorizationValidator extends ValidatorBase implements IErrorHandler
{
	private UnifiedErrorReporter _reporter;
	private String _path;
	private boolean _collectErrors;
	private boolean _collectWarnings;
	private boolean _collectInfos;

	/**
	 * LexerValidator
	 */
	public ColorizationValidator()
	{
	}

	/**
	 * @see com.aptana.ide.editors.validator.ValidatorBase#parseForErrors(java.lang.String, java.lang.String,
	 *      com.aptana.ide.editors.unified.IFileSourceProvider, boolean, boolean, boolean)
	 */
	public IFileError[] parseForErrors(String path, String source, IFileSourceProvider sourceProvider,
			boolean collectErrors, boolean collectWarnings, boolean collectInfos)
	{
		// save references
		this._path = path;
		this._collectErrors = collectErrors;
		this._collectWarnings = collectWarnings;
		this._collectInfos = collectInfos;

		// create unified error reporter
		this._reporter = new UnifiedErrorReporter(sourceProvider);

		// create colorizer reader
		ColorizerReader reader = new ColorizerReader();

		// set ourselves as the error handler so we can respond to various message types
		reader.setErrorHandler(this);

		// conver source into a stream
		ByteArrayInputStream in = new ByteArrayInputStream(source.getBytes());

		// parse source
		reader.loadLanguageColorizer(in, false);

		// return list of messages
		return this._reporter.getErrors();
	}

	/**
	 * @see IErrorHandler#handleError(int, int, java.lang.String)
	 */
	public void handleError(int line, int column, String message)
	{
		if (this._collectErrors)
		{
			this._reporter.error(message, this._path, line + 1, "", column); //$NON-NLS-1$
		}
	}

	/**
	 * @see IErrorHandler#handleInfo(int, int, java.lang.String)
	 */
	public void handleInfo(int line, int column, String message)
	{
		if (this._collectInfos)
		{
			this._reporter.info(message, this._path, line + 1, "", column); //$NON-NLS-1$
		}
	}

	/**
	 * @see IErrorHandler#handleWarning(int, int, java.lang.String)
	 */
	public void handleWarning(int line, int column, String message)
	{
		if (this._collectWarnings)
		{
			this._reporter.warning(message, this._path, line + 1, "", column); //$NON-NLS-1$
		}
	}
}
