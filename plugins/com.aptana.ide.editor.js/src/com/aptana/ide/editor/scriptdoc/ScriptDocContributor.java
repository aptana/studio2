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
package com.aptana.ide.editor.scriptdoc;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;

import com.aptana.ide.editor.scriptdoc.contentassist.ScriptDocContentAssistProcessor;
import com.aptana.ide.editor.scriptdoc.formatting.ScriptDocAutoIndentStrategy;
import com.aptana.ide.editor.scriptdoc.parsing.ScriptDocMimeType;
import com.aptana.ide.editors.unified.BaseContributor;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.editors.unified.UnifiedReconcilingStrategy;
import com.aptana.ide.editors.unified.colorizer.LanguageColorizer;

/**
 * @author Robin Debreuil
 */
public class ScriptDocContributor extends BaseContributor
{
	private ScriptDocReconcilingStrategy _reconcilingStrategy;
	private IAutoEditStrategy[] _autoEditStrategy;
	private IContentAssistProcessor _contentAssistProcessor;
	private boolean isDisposing = false;
	private LanguageColorizer _colorizer;

	/**
	 * ScriptDocContributor
	 */
	public ScriptDocContributor()
	{
		super(ScriptDocMimeType.MimeType);
		this._colorizer = LanguageRegistry.getLanguageColorizer(ScriptDocMimeType.MimeType);

		this._colorizer = (this._colorizer == null) ? null : this._colorizer; // make compiler happy
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getReconcilingStrategy()
	 */
	public UnifiedReconcilingStrategy getReconcilingStrategy()
	{
		_reconcilingStrategy = new ScriptDocReconcilingStrategy();
		return _reconcilingStrategy;
	}

	/**
	 * @see com.aptana.ide.editors.unified.BaseContributor#getLocalAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public IAutoEditStrategy[] getLocalAutoEditStrategies(ISourceViewer sourceViewer, String contentType)
	{
		if (contentType.equals(ScriptDocMimeType.MimeType))
		{
			_autoEditStrategy = new IAutoEditStrategy[] { new ScriptDocAutoIndentStrategy(this.getFileContext(), this.getParentConfiguration(), sourceViewer) };
			return _autoEditStrategy;
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getLocalContentAssistProcessor(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public IContentAssistProcessor getLocalContentAssistProcessor(ISourceViewer sourceViewer, String contentType)
	{
		if (contentType.equals(ScriptDocMimeType.MimeType))
		{
			EditorFileContext context = getFileContext();
			if (context != null)
			{
				_contentAssistProcessor = new ScriptDocContentAssistProcessor(context, (SourceViewer) sourceViewer);// service);
				return _contentAssistProcessor;
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#dispose()
	 */
	public void dispose()
	{
		if (isDisposing)
		{
			return;
		}
		isDisposing = true;

		if(_colorizer != null)
		{
			_colorizer.dispose();
			_colorizer = null;
		}
		
		_autoEditStrategy = null;
		_reconcilingStrategy = null;
		_contentAssistProcessor = null;

		super.dispose();
	}
}
