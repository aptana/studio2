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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.editor.xml.XMLPlugin;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.IFileSourceProvider;
import com.aptana.ide.editors.unified.errors.ErrorDescriptor;
import com.aptana.ide.editors.unified.errors.FileError;
import com.aptana.ide.editors.unified.errors.IFileError;
import com.aptana.ide.editors.unified.errors.UnifiedErrorReporter;
import com.aptana.ide.editors.validator.ValidatorBase;
import com.aptana.xml.IErrorHandler;

/**
 * @author Kevin Lindsey
 */
public class XMLValidator extends ValidatorBase implements IErrorHandler
{
	private XMLParser _xmlParser;
	private UnifiedErrorReporter _reporter;
	private String _path;

	/**
	 * LexerValidator
	 */
	public XMLValidator()
	{
	}

	/**
	 * getXMLParser
	 * 
	 * @return Parser
	 */
	public XMLParser getXMLParser()
	{
		if (this._xmlParser == null)
		{
			this._xmlParser = new XMLParser();
		}

		return this._xmlParser;
	}

	/**
	 * @see com.aptana.ide.editors.validator.ValidatorBase#parseForErrors(java.lang.String, java.lang.String,
	 *      com.aptana.ide.editors.unified.IFileSourceProvider, boolean, boolean, boolean)
	 */
	public IFileError[] parseForErrors(String path, String source, IFileSourceProvider sourceProvider,
			final boolean collectErrors, final boolean collectWarnings, final boolean collectInfos)
	{
		// create unified error reporter
		this._reporter = new UnifiedErrorReporter(sourceProvider);

		// save reference to file path
		this._path = path;
		String ignoreErrors = XMLPlugin.getDefault().getPreferenceStore().getString(
				IPreferenceConstants.IGNORE_PROBLEMS);
		ErrorDescriptor[] ds = ErrorDescriptor.deserializeErrorDescriptors(ignoreErrors);

		XMLParser xmlParser = this.getXMLParser();
		xmlParser.setErrorHandler(new IErrorHandler()
		{

			public void handleError(int line, int column, String message)
			{
				if (collectErrors)
				{
					XMLValidator.this.handleError(line, column, message);
				}
			}

			public void handleInfo(int line, int column, String message)
			{
				if (collectInfos)
				{
					XMLValidator.this.handleInfo(line, column, message);
				}
			}

			public void handleWarning(int line, int column, String message)
			{
				if (collectWarnings)
				{
					XMLValidator.this.handleWarning(line, column, message);
				}
			}

		});

		// finds the proper encoding of the file
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(_path));
		String encoding = CoreUIUtils.getFileEncoding(file);
		byte[] bytes;
		try {
            bytes = source.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            // falls back to the system default
            bytes = source.getBytes();
        }
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		xmlParser.parse(in);

		IFileError[] errors = this._reporter.getErrors();
		ArrayList result = new ArrayList();
		l2:for (int a = 0; a < errors.length; a++)
		{
			IFileError fileError = errors[a];
			if (fileError instanceof FileError)
			{
				for (int b = 0; b < ds.length; b++)
				{

					if (ds[b].matchesError((FileError) fileError))
					{
						continue l2;
					}
				}
				
			}
			result.add(fileError);
		}
		errors = new IFileError[result.size()];
		result.toArray(errors);
		return errors;
	}

	/**
	 * @see com.aptana.xml.IErrorHandler#handleError(int, int, java.lang.String)
	 */
	public void handleError(int line, int column, String message)
	{
		this._reporter.error(message, this._path, line + 1, "", column); //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.xml.IErrorHandler#handleInfo(int, int, java.lang.String)
	 */
	public void handleInfo(int line, int column, String message)
	{
		this._reporter.info(message, this._path, line + 1, "", column); //$NON-NLS-1$
	}

	/**
	 * @see com.aptana.xml.IErrorHandler#handleWarning(int, int, java.lang.String)
	 */
	public void handleWarning(int line, int column, String message)
	{
		this._reporter.warning(message, this._path, line + 1, "", column); //$NON-NLS-1$
	}
}
