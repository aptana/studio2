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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;

import org.eclipse.jface.preference.IPreferenceStore;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Interpreter;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptOrFnNode;
import org.mozilla.javascript.Scriptable;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.PluginUtils;
import com.aptana.ide.core.StringUtils;
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
public class JSLintValidator extends ValidatorBase
{
	/*
	 * Fields
	 */
	static String jsLintString = getResourceText("fulljslint.js"); //$NON-NLS-1$
	static Script JSLintScript = null;

	private static String LINE_DELIM = System.getProperty("line.separator", "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$

	/*
	 * Constructors
	 */

	/**
	 * MozillaJsValidator
	 */
	public JSLintValidator()
	{
		if (JSLintScript == null)
		{
			JSLintScript = getJSLintScript(jsLintString);
		}
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
	public IFileError[] parseForErrors(String path, String source, IFileSourceProvider sourceProvider,
			boolean collectErrors, boolean collectWarnings, boolean collectInfos)
	{
		IFileError[] err = new IFileError[0];

		UnifiedErrorReporter reporter = new UnifiedErrorReporter(sourceProvider);
		reporter.addErrors(err);
		ArrayList<Range> ranges = new ArrayList<Range>();
		Context cx = Context.enter();

		try
		{

			source = JSValidationUtils.filterPIInstructions(source, ranges);
			cx.setErrorReporter(reporter);

			if (collectErrors || collectWarnings)
			{
				lintScript(JSLintScript, cx, source, path, collectErrors, collectWarnings);
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
					if (offset >= source.length()
							|| ((offset <= source.length()) && LINE_DELIM.indexOf(source.charAt(offset)) != -1))
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
									((FileError) currError).setLineNumber(sourceProvider.getLineOfOffset(currError
											.getOffset()) + 1);
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
			// return JSValidationUtils.filterErrorsByNovalidate(errors, source);
			return errors;
		}
		catch (Throwable th)
		{
			IdeLog.logError(JSPlugin.getDefault(), "Novalidate filtering failed", th); //$NON-NLS-1$
			return errors;
		}
	}

	

	/**
	 * lintScript
	 * 
	 * @param JSLintScript
	 * @param cx
	 * @param script
	 * @param path
	 * @param collectErrors
	 * @param collectWarnings
	 */
	private void lintScript(Script JSLintScript, Context cx, String script, String path, boolean collectErrors,
			boolean collectWarnings)
	{
		Scriptable scope = cx.initStandardObjects();

		// JSLint jsl = new JSLint();
		// jsl.exec(cx, scope);

		JSLintScript.exec(cx, scope);

		Object fObj = scope.get("jslint", scope); //$NON-NLS-1$

		if (!(fObj instanceof Function))
		{
			IdeLog.logInfo(JSPlugin.getDefault(), Messages.JSErrorManager_JSLintUndefined);
		}
		else
		{
			Object[] functionArgs = { script, scope.get("aptanaOptions", scope) }; //$NON-NLS-1$

			Function f = (Function) fObj;

			// PC: we ignore the result, because i have found that with some versions, there might
			// be
			// errors but this function returned true (false == errors)
			f.call(cx, scope, scope, functionArgs).toString();

			Object errorObject = f.get("errors", scope); //$NON-NLS-1$

			if (errorObject != null && errorObject instanceof NativeArray)
			{
				NativeArray errorArray = (NativeArray) errorObject;
				Object[] ids = errorArray.getIds();
				if (ids.length == 0)
				{
					return;
				}

				boolean lastIsError = false;
				NativeObject last = (NativeObject) errorArray.get(Integer.parseInt(ids[ids.length - 1].toString()),
						scope);
				if (last == null)
				{
					lastIsError = true;
				}

				if (lastIsError == false && collectWarnings == false)
				{
					return;
				}

				for (int i = 0; i < ids.length; i++)
				{
					try
					{
						NativeObject v = (NativeObject) errorArray.get(Integer.parseInt(ids[i].toString()), scope);
						if (v != null)
						{
							int line = (int) Double.parseDouble(v.get("line", scope).toString()) + 1; //$NON-NLS-1$
							String reason = v.get("reason", scope).toString().trim(); //$NON-NLS-1$
							int character = (int) Double.parseDouble(v.get("character", scope).toString()); //$NON-NLS-1$
							String evidence = v.get("evidence", scope).toString().trim(); //$NON-NLS-1$

							UnifiedErrorReporter rep = (UnifiedErrorReporter) cx.getErrorReporter();

							if (i == ids.length - 2 && lastIsError)
							{
								if (collectErrors)
								{
									rep.error(reason, path, line, evidence, character);
								}
							}
							else if (collectWarnings)
							{
								rep.warning(reason, path, line, evidence, character);
							}

						}
					}
					catch (Exception e)
					{
						IdeLog.logInfo(JSPlugin.getDefault(), "Error running JavaScript lint script", e); //$NON-NLS-1$
					}
				}

			}
		}
	}

	/**
	 * getJSLintScript
	 * 
	 * @param jsLintSourceString
	 * @return Script
	 */
	private static Script getJSLintScript(String jsLintSourceString)
	{
		Context cx = Context.enter();

		try
		{
			CompilerEnvirons compilerEnv = new CompilerEnvirons();
			compilerEnv.initFromContext(cx);

			Parser p = new Parser(compilerEnv, cx.getErrorReporter());
			ScriptOrFnNode tree = p.parse(jsLintSourceString, "fulljslint.js", 1); //$NON-NLS-1$

			String encodedSource = p.getEncodedSource();

			Interpreter compiler = createCompiler();

			Object bytecode = compiler.compile(compilerEnv, tree, encodedSource, false);

			Script result = compiler.createScriptObject(bytecode, null);

			return result;

		}
		catch (org.mozilla.javascript.EvaluatorException e)
		{
			IdeLog.logInfo(JSPlugin.getDefault(), Messages.JSErrorManager_JSLintFailed, e);
		}
		catch (Exception e)
		{
			if (e instanceof InterruptedException == false)
			{
				IdeLog.logInfo(JSPlugin.getDefault(), Messages.JSErrorManager_JSLintFailed, e);
			}
		}
		finally
		{
			// Exit from the context.
			Context.exit();
		}

		return null;
	}

	private static Class<?> codegenClass = Kit.classOrNull("org.mozilla.javascript.optimizer.Codegen"); //$NON-NLS-1$

	private static Interpreter createCompiler()
	{
		int optimizationLevel = codegenClass != null ? 0 : -1;
		Interpreter result = null;

		if (optimizationLevel >= 0 && codegenClass != null)
		{
			result = (Interpreter) newInstanceOrNull(codegenClass);
		}
		if (result == null)
		{
			result = new Interpreter();
		}
		return result;
	}

	static Object newInstanceOrNull(Class<?> cl)
	{
		try
		{
			return cl.newInstance();
		}
		catch (SecurityException x)
		{
		}
		catch (LinkageError ex)
		{
		}
		catch (InstantiationException x)
		{
		}
		catch (IllegalAccessException x)
		{
		}
		return null;
	}

	private static String getResourceText(String name)
	{
		try
		{
			// get resource stream
			String fullName = "/com/aptana/ide/editor/js/" + name; //$NON-NLS-1$
			InputStream stream = com.aptana.ide.epl.Activator.class.getResourceAsStream(fullName);

			// create output buffer
			StringWriter sw = new StringWriter();

			// read contents into a string buffer
			try
			{
				// get buffered reader
				InputStreamReader isr = new InputStreamReader(stream);
				BufferedReader reader = new BufferedReader(isr);

				// create temporary buffer
				char[] buf = new char[1024];

				// fill buffer
				int numRead = reader.read(buf);

				// keep reading until the end of the stream
				while (numRead != -1)
				{
					// output temp buffer to output buffer
					sw.write(buf, 0, numRead);

					// fill buffer
					numRead = reader.read(buf);
				}
			}
			finally
			{
				if (stream != null)
				{
					stream.close();
				}
			}

			// return string buffer's content
			return sw.toString();
		}
		catch (Exception e)
		{
			if (e instanceof InterruptedException == false)
			{
				IdeLog.logInfo(JSPlugin.getDefault(), StringUtils.format(Messages.JSErrorManager_GetResourceTextFailed,
						name), e);
			}
			return null;
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
