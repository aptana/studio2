/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL youelect, is prohibited.
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
package com.aptana.ide.editor.yml;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.ide.editor.yml.contentassist.YMLContentAssistProcessor;
import com.aptana.ide.editor.yml.formatting.YMLAutoIndentStrategy;
import com.aptana.ide.editors.unified.BaseContributor;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.editors.unified.UnifiedReconcilingStrategy;
import com.aptana.ide.editors.unified.colorizer.LanguageColorizer;

/**
 * YMLContributor
 */
public class YMLContributor extends BaseContributor
{

	private IAutoEditStrategy[] _autoEditStrategies;
	private YMLContentAssistProcessor _contentAssistProcessor;
	private boolean isDisposing = false;
	private LanguageColorizer _colorizer;

	/**
	 * YMLContributor
	 */
	public YMLContributor()
	{
		super(YMLMimeType.MimeType);
		this._colorizer = LanguageRegistry.getLanguageColorizer(YMLMimeType.MimeType);
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getLocalContentAssistProcessor(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public IContentAssistProcessor getLocalContentAssistProcessor(ISourceViewer sourceViewer, String contentType)
	{
		IContentAssistProcessor result = null;

		if (contentType.equals(YMLMimeType.MimeType))
		{
			EditorFileContext context = getFileContext();

			if (context != null)
			{
				this._contentAssistProcessor = new YMLContentAssistProcessor(context);// service);

				result = this._contentAssistProcessor;
			}
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getReconcilingStrategy()
	 */
	public UnifiedReconcilingStrategy getReconcilingStrategy()
	{
		return new UnifiedReconcilingStrategy();
	}

	/**
	 * @see com.aptana.ide.editors.unified.BaseContributor#getLocalAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public IAutoEditStrategy[] getLocalAutoEditStrategies(ISourceViewer sourceViewer, String contentType)
	{
		IAutoEditStrategy[] result = null;

		if (contentType.equals(YMLMimeType.MimeType))
		{
			this._autoEditStrategies = new IAutoEditStrategy[] { new YMLAutoIndentStrategy(this.getFileContext(), this
					.getParentConfiguration(), sourceViewer) };

			result = this._autoEditStrategies;
		}

		return result;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#dispose()
	 */
	public void dispose()
	{
		if (isDisposing == false)
		{
			isDisposing = true;

			if (this._colorizer != null)
			{
				this._colorizer.dispose();
				this._colorizer = null;
			}

			this._autoEditStrategies = null;
			this._colorizer = null;
			this._contentAssistProcessor = null;

			super.dispose();
		}
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#isValidIdentifier(char, int)
	 */
	public boolean isValidIdentifier(char c, int keyCode)
	{
		return ('a' <= keyCode && keyCode <= 'z') || c == '_' || c == '#' || c == '.';
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#isValidActivationCharacter(char, int)
	 */
	public boolean isValidActivationCharacter(char c, int keyCode)
	{
		return Character.isWhitespace(c) || c == ':';
	}

}
