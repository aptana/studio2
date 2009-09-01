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
package com.aptana.ide.editor.html;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITypedRegion;

import com.aptana.ide.core.PluginUtils;
import com.aptana.ide.editor.css.CSSErrorManager;
import com.aptana.ide.editor.html.lexing.HTMLTokenTypes;
import com.aptana.ide.editor.html.parsing.HTMLMimeType;
import com.aptana.ide.editor.js.JSErrorManager;
import com.aptana.ide.editors.unified.FileService;
import com.aptana.ide.editors.unified.IFileLanguageService;
import com.aptana.ide.editors.unified.IFileSourceProvider;
import com.aptana.ide.editors.unified.errors.FileError;
import com.aptana.ide.editors.unified.errors.IFileError;
import com.aptana.ide.editors.unified.errors.UnifiedErrorManager;
import com.aptana.ide.editors.unified.errors.UnifiedErrorReporter;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.parsing.IOffsetMapper;

/**
 * @author Robin Debreuil
 */
public class HTMLErrorManager extends UnifiedErrorManager
{

	JSErrorManager jsErrorManager;
	CSSErrorManager cssErrorManager;

	/**
	 * HTMLErrorManager
	 * 
	 * @param fileService
	 */
	public HTMLErrorManager(FileService fileService)
	{
		super(fileService, HTMLMimeType.MimeType);

		jsErrorManager = new JSErrorManager(this.fileService);
		cssErrorManager = new CSSErrorManager(this.fileService);
	}

	/**
	 * @see com.aptana.ide.editors.unified.errors.UnifiedErrorManager#parseForErrors(java.lang.String, java.lang.String,
	 *      com.aptana.ide.editors.unified.IFileSourceProvider)
	 */
	public IFileError[] parseForErrors(String path, String source, IFileSourceProvider sourceProvider)
	{
		UnifiedErrorReporter reporter = new UnifiedErrorReporter(sourceProvider);

		String htmlSource = extractLanguage("text/html", source, this); //$NON-NLS-1$
		// Parent class has been instrcuted via constructor to handle html
		IFileError[] errs = super.parseForErrors(path, htmlSource, sourceProvider);
		reporter.addErrors(errs);

		String jsSource = extractLanguage("text/javascript", source, jsErrorManager); //$NON-NLS-1$
		IFileError[] err = jsErrorManager.parseForErrors(path, jsSource, sourceProvider);
		reporter.addErrors(err);

		String cssSource = extractLanguage("text/css", source, cssErrorManager); //$NON-NLS-1$
		err = cssErrorManager.parseForErrors(path, cssSource, sourceProvider);
		reporter.addErrors(err);

		err = reporter.getErrors();

		List validErrors = getValidErrors(err);
		err = (IFileError[]) validErrors.toArray(new IFileError[validErrors.size()]);

		// Sort the combined results by line number
		Arrays.sort(err, new Comparator()
		{
			public int compare(Object arg0, Object arg1)
			{
				FileError a = (FileError) arg0;
				FileError b = (FileError) arg1;
				return a.getLineNumber() - b.getLineNumber();
			}
		});

		// Sort the combined results by severity
		Arrays.sort(err, new Comparator()
		{
			public int compare(Object arg0, Object arg1)
			{
				FileError a = (FileError) arg0;
				FileError b = (FileError) arg1;
				return b.getSeverity() - a.getSeverity();
			}
		});

		return err;
	}

	/**
	 * This code removes invalid JS errors reported from HTML attributes. Currently only removes "invalid return" errors
	 * on attributes values such as onClick="return true;"
	 * 
	 * @param err -
	 *            raw error list
	 * @return - list of valid errors
	 */
	private List getValidErrors(IFileError[] err)
	{
		List validErrors = new ArrayList();
		for (int i = 0; i < err.length; i++)
		{
			IFileError fe = err[i];
			if (!"invalid return".equalsIgnoreCase(fe.getMessage())) //$NON-NLS-1$
			{
				validErrors.add(fe);
			}
			else
			{
				try
				{
					ITypedRegion region = fileService.getPartitionAtOffset(fe.getOffset());
					IFileLanguageService service = fileService.getLanguageService(HTMLMimeType.MimeType);
					if (service != null && service.getOffsetMapper() != null)
					{
						IOffsetMapper mapper = service.getOffsetMapper();
						int index = mapper.getLexemeIndexFromDocumentOffset(region.getOffset());
						if (index - 1 > 0 && index - 1 < mapper.getLexemeList().size())
						{
							Lexeme prev = mapper.getLexemeAtIndex(index - 1);
							// Only remove when previous lexeme is = and is HTML since that will signify the start of a
							// attribute
							if (!(prev.getLanguage().equals(HTMLMimeType.MimeType) && prev.getToken().getTypeIndex() == HTMLTokenTypes.EQUAL))
							{
								validErrors.add(fe);
							}
						}
					}
				}
				catch (Exception e)
				{
					// Leave as valid error if attempt to remove fails for any reason
					validErrors.add(fe);
				}
			}
		}
		return validErrors;
	}

	private String extractLanguage(String lang, String source, UnifiedErrorManager manager)
	{
		if (source == null || source == "") //$NON-NLS-1$
		{
			return ""; //$NON-NLS-1$
		}
		
		if (this.fileService != null)
		{
			ITypedRegion[] partitions = this.fileService.getPartitions();
			ArrayList strip = new ArrayList();

			if (partitions != null)
			{
				for (int i = 0; i < partitions.length; i++)
				{
					ITypedRegion partition = partitions[i];
					int start = partition.getOffset();
					int length = partition.getLength();
					if (partition.getType().equals(lang) == false)
					{
						if (start >= 0 && length > 0)
						{
							strip.add(partition);
						}
					}
					else
					{
						source = manager.processLanguagePartition(partition, source);
					}
				}
				char[] sourceArray=source.toCharArray();
				// Have to strip after the fact, as the lang partition may need to know
				// its context
				for (Iterator iter = strip.iterator(); iter.hasNext();)
				{
					ITypedRegion partition = (ITypedRegion) iter.next();
					int start = partition.getOffset();
					int length = partition.getLength();

					 stripCharsArray(sourceArray, start, length);
				}
				source=new String(sourceArray);
			}
		}

		return source;
	}
	
	
	protected void stripCharsArray(char[] source, int start, int length)
	{
		char[] c = source;

		int end = start + length;

		if (end > c.length)
		{
			end = c.length;
		}

		for (int i = start; i < end; i++)
		{

			if (c[i] != '\r' && c[i] != '\n')
			{
				c[i] = ' ';
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

		return super.filterMessage(message);

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
}
