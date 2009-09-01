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
package com.aptana.ide.editor.js.validator;

import java.util.ArrayList;

import org.eclipse.jface.preference.IPreferenceStore;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Parser;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.PluginUtils;
import com.aptana.ide.editor.js.JSPlugin;
import com.aptana.ide.editor.js.Messages;
import com.aptana.ide.editors.unified.IFileSourceProvider;
import com.aptana.ide.editors.unified.errors.FileError;
import com.aptana.ide.editors.unified.errors.IFileError;
import com.aptana.ide.editors.unified.errors.UnifiedErrorReporter;
import com.aptana.ide.editors.validator.ValidatorBase;
import com.aptana.ide.lexer.Range;

/**
 * @author Paul Colton
 * @author Samir Joshi (Refactoring)
 */
public class MozillaJsValidator extends ValidatorBase
{
	private static String LINE_DELIM = System.getProperty("line.separator", "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * MozillaJsValidator
	 */
	public MozillaJsValidator()
	{
	}

	/**
	 * @param path
	 * @param source
	 * @param sourceProvider
	 * @param collectErrors
	 * @param collectWarnings
	 * @param collectInfos -
	 *            ignored for now
	 * @return errors
	 * @see com.aptana.ide.editors.validator.ValidatorBase#parseForErrors(String, String, IFileSourceProvider, boolean,
	 *      boolean, boolean)
	 */
	public IFileError[] parseForErrors(String path, String source, IFileSourceProvider sourceProvider, boolean collectErrors, boolean collectWarnings, boolean collectInfos)
	{
		IFileError[] err = new IFileError[0];

		UnifiedErrorReporter reporter = new UnifiedErrorReporter(sourceProvider);
		reporter.addErrors(err);

		Context cx = Context.enter();
		ArrayList<Range>ranges=new ArrayList<Range>();
		try
		{
			
			source = JSValidationUtils.filterPIInstructions(source,ranges);
			//TO WORKAROUND STU-792; TODO REVIEW IT LATER
			source=source.replace("=<", "= "); //$NON-NLS-1$ //$NON-NLS-2$
			cx.setErrorReporter(reporter);

			if (collectErrors)
			{
				CompilerEnvirons compilerEnv = new CompilerEnvirons();
				compilerEnv.initFromContext(cx);

				Parser p = new Parser(compilerEnv, reporter);

				try
				{
					// ScriptOrFnNode tree =
					p.parse(source, path, 1);
				}
				catch (org.mozilla.javascript.EvaluatorException e)
				{

				}
			}
		}
		catch (Exception e)
		{
			IdeLog.logInfo(JSPlugin.getDefault(), Messages.JSErrorManager_ParseForErrorsFailed, e);
		}
		finally
		{
			// Exit from the context.
			Context.exit();
		}

		IFileError[] errors = JSValidationUtils.filterErrors(source, reporter, ranges);
		
		// Error may be at source.length() which will cause error markers to not show up and the validation view
		// selection to be incorrect
		// Manually go through and check for this condition
		if (errors != null)
		{
			for (int i = 0; i < errors.length; i++)
			{
				// This is wrapped in a catch all block so that it doesn't impact the error reporting since the
				// post-processing logic shouldn't break the old functionality
				try
				{
					IFileError currError = errors[i];
					int offset = currError.getOffset();

					// This checks if the error is the last character of the offset of the error is being reported at an
					// area where a line delimiter is making it impossible to select and reveal that problem area
					if (offset >= source.length() || ((offset <= source.length()) && LINE_DELIM.indexOf(source.charAt(offset)) != -1))
					{
						if (currError instanceof FileError)
						{
							// Apparently JS line can return us an offset that is past the end of the source string, not
							// just at the end
							// The same procedure should be used where we just back up until we are at a valid position
							// to add the marker
							if (offset > source.length())
							{
								offset = source.length();
							}
							// Adjust to the first non line delimiter found so that we will always have a selectable
							// marker
							offset = offset == source.length() ? offset - 1 : offset;
							
							for (int j = offset; j >= 0; j--)
							{
								if (LINE_DELIM.indexOf(source.charAt(j)) == -1)
								{
									((FileError) currError).setOffset(j);
									// Adjust line number to place of new offset, +1 at the end comes from the behavior
									// in UnifiedErrorReporter
									((FileError) currError).setLineNumber(sourceProvider.getLineOfOffset(currError.getOffset()) + 1);
									break;
								}
							}
						}
					}
				}
				catch (Exception e)
				{
					IdeLog.logInfo(JSPlugin.getDefault(), "Error running JavaScript lint script", e); //$NON-NLS-1$
				}
			}
		}

		try
		{
//			return JSValidationUtils.filterErrorsByNovalidate(errors, source);
			return errors;
		}
		catch (Throwable th)
		{
			IdeLog.logError(JSPlugin.getDefault(), "Novalidate filtering failed", th); //$NON-NLS-1$
			return errors;
		}
	}

	/**
	 * Returns the preference store
	 * 
	 * @return IPreferenceStore
	 */
	protected IPreferenceStore getPreferenceStore()
	{
		if (PluginUtils.isPluginLoaded(JSPlugin.getDefault()))
		{
			return JSPlugin.getDefault().getPreferenceStore();
		}
		else
		{
			return null;
		}
	}

}
