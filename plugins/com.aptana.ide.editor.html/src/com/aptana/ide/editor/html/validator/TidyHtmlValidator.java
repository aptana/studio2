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
package com.aptana.ide.editor.html.validator;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.preference.IPreferenceStore;
import org.w3c.tidy.Tidy;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.PluginUtils;
import com.aptana.ide.editor.css.CSSErrorManager;
import com.aptana.ide.editor.html.HTMLPlugin;
import com.aptana.ide.editor.html.Messages;
import com.aptana.ide.editor.js.JSErrorManager;
import com.aptana.ide.editors.unified.IFileSourceProvider;
import com.aptana.ide.editors.unified.errors.IFileError;
import com.aptana.ide.editors.unified.errors.UnifiedErrorReporter;
import com.aptana.ide.editors.validator.ValidatorBase;

/**
 * @author Robin Debreuil
 * @author Samir Joshi (Refactored from HTMLErrorManager)
 */
public class TidyHtmlValidator extends ValidatorBase
{

	static Pattern pattern = Pattern
			.compile("\\s*line\\s+(\\d+)\\s*column\\s+(\\d+)\\s*-\\s*(Warning|Error):\\s*(.+)$"); //$NON-NLS-1$

	JSErrorManager jsErrorManager;
	CSSErrorManager cssErrorManager;

	/**
	 * HTMLErrorManager
	 */
	public TidyHtmlValidator()
	{

	}

	/**
	 * @param path
	 * @param source
	 * @param sourceProvider
	 * @param collectErrors
	 * @param collectWarnings
	 * @param collectInfos
	 *            - ignored for now
	 * @return errors
	 * @see com.aptana.ide.editors.validator.ValidatorBase#parseForErrors(String, String, IFileSourceProvider, boolean,
	 *      boolean, boolean)
	 */
	public IFileError[] parseForErrors(String path, String source, IFileSourceProvider sourceProvider,
			boolean collectErrors, boolean collectWarnings, boolean collectInfos)
	{
		String input = runThroughTidy(source);
		if (input == null || input.trim().length() == 0)
			return new IFileError[0];

		UnifiedErrorReporter reporter = new UnifiedErrorReporter(sourceProvider);
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new StringReader(input));
			String line;
			while ((line = br.readLine()) != null)
			{
				if (line.startsWith("line")) //$NON-NLS-1$
				{
					parseTidyOutput(reporter, path, line, collectErrors, collectWarnings);
				}
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(HTMLPlugin.getDefault(), Messages.HTMLErrorManager_ErrorParsingForErrors, e);
		}
		finally
		{
			if (br != null)
				try
				{
					br.close();
				}
				catch (IOException e)
				{
					// ignore
				}
		}
		return reporter.getErrors();
	}

	private String runThroughTidy(String source)
	{
		try
		{
			Tidy tidy = new Tidy();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			PrintWriter out = new PrintWriter(bout);
			tidy.setErrout(out);
			try
			{
				String patchedSource = patchSource(source);
				tidy.parse(new ByteArrayInputStream(patchedSource.getBytes()), null);
			}
			catch (NullPointerException e)
			{
				IdeLog.logError(HTMLPlugin.getDefault(), Messages.TidyHtmlValidator_Null_Pointer, e);
			}
			out.flush();

			return bout.toString();
		}
		catch (Throwable t)
		{
			return "";
		}
	}

	private void parseTidyOutput(UnifiedErrorReporter err, String filename, String source, boolean showErrors,
			boolean showWarnings)
	{
		Matcher matcher = pattern.matcher(source);

		while (matcher.find())
		{
			int line = Integer.parseInt(matcher.group(1));
			int column = Integer.parseInt(matcher.group(2));
			String type = matcher.group(3);
			String message = matcher.group(4);

			message = filterMessage(message);

			if (message != null)
			{
				if (type.startsWith("Error") && showErrors) //$NON-NLS-1$
				{
					err.error(message, filename, line, "", column); //$NON-NLS-1$
				}
				else if (type.startsWith("Warning") && showWarnings) //$NON-NLS-1$
				{
					err.warning(message, filename, line, "", column); //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * filter messages before they go out, return 'null' to have the message omitted
	 * 
	 * @param message
	 * @return String
	 */
	public String filterMessage(String message)
	{
		message = message.replaceFirst("discarding", "should discard"); //$NON-NLS-1$ //$NON-NLS-2$
		message = message.replaceFirst("inserting", "should insert"); //$NON-NLS-1$ //$NON-NLS-2$
		message = message.replaceFirst("trimming", "should trim"); //$NON-NLS-1$ //$NON-NLS-2$
		return message;
	}

	/**
	 * Returns the preference store
	 * 
	 * @return IPreferenceStore
	 */
	protected IPreferenceStore getPreferenceStore()
	{
		if (PluginUtils.isPluginLoaded(HTMLPlugin.getDefault()))
		{
			return HTMLPlugin.getDefault().getPreferenceStore();
		}
		else
		{
			return null;
		}
	}

	/**
	 * Patches source.
	 * 
	 * @param source
	 *            - source to patch.
	 * @return patched source.
	 */
	private String patchSource(String source)
	{
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < source.length(); i++)
		{
			char ch = source.charAt(i);
			boolean patchNeeded = false;

			if (ch == '<' && i + 1 < source.length() && Character.isLetter(source.charAt(i + 1)))
			{
				// checking for the wrong tag start case
				for (int j = i + 1; j < source.length(); j++)
				{
					char ch2 = source.charAt(j);
					if (Character.isLetterOrDigit(ch2) || ch2 == '_' || ch == ':' || ch == '-' || ch == '.')
					{
						// valid part of tag start, doing nothing
					}
					else if (Character.isWhitespace(ch2) || ch2 == '>' || ch2 == '/')
					{
						// valid end of tag start, breaking the check
						break;
					}
					else
					{
						// invalid end of tag start, so we have to replace the starting '<'
						// character with some "safe" character for W3C validator.
						patchNeeded = true;
					}
				}
			}

			if (patchNeeded)
			{
				// patching the start '<' character with 'L' character.
				builder.append('L');
			}
			else
			{
				builder.append(ch);
			}
		}

		return builder.toString();
	}
}
