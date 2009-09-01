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

import java.text.MessageFormat;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;

import com.aptana.ide.core.IdeLog;
import com.aptana.ide.core.ui.CoreUIUtils;
import com.aptana.ide.editors.UnifiedEditorsPlugin;

/**
 * This a source provider for a file currently open in the editor.
 * DocumentSourceProvider
 */
public class DocumentSourceProvider implements IFileSourceProvider
{
	/*
	 * Fields
	 */
	private IDocument document;
	private IEditorInput editorInput;

	/*
	 * Constructors
	 */
	
	/**
	 * DocumentSourceProvider
	 *
	 * @param doc
	 * @param editorInput
	 */
	public DocumentSourceProvider(IDocument doc, IEditorInput editorInput)
	{
		this.document = doc;
		this.editorInput = editorInput;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileSourceProvider#getSource()
	 */
	public String getSource()
	{
		return document.get();
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileSourceProvider#getSourceLength()
	 */
	public int getSourceLength()
	{
		return document.getLength();
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileSourceProvider#getLineOfOffset(int)
	 */
	public int getLineOfOffset(int offset)
	{
		try
		{
			return this.document.getLineOfOffset(offset);
		}
		catch (BadLocationException e)
		{
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), Messages.DocumentSourceProvider_Error, e);
			return 0;
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileSourceProvider#getLineOffset(int)
	 */
	public int getLineOffset(int line)
	{
		try
		{
			return this.document.getLineOffset(line);
		}
		catch (BadLocationException e)
		{
			String message = MessageFormat.format(Messages.DocumentSourceProvider_Error, new String[] { Integer.toString(line) });
			
			IdeLog.logError(UnifiedEditorsPlugin.getDefault(), message, e);
			
			return 0;
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IFileSourceProvider#getSourceURI()
	 */
	public String getSourceURI()
	{
		return CoreUIUtils.getURI(editorInput);
	}
	
	/**
	 * figure it out
	 * @param o 
	 * @return boolean
	 */ 
	public boolean equals(Object o)
	{
		if(o instanceof DocumentSourceProvider)
		{
			DocumentSourceProvider dsp = (DocumentSourceProvider) o;
			return this.document == dsp.document && this.editorInput == dsp.editorInput;
		}
		else
		{
			return super.equals(o);
		}
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return super.hashCode();
	}
}
