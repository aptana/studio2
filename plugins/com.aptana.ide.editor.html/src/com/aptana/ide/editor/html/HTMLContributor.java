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

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.ide.editor.css.CSSContributor;
import com.aptana.ide.editor.html.contentassist.HTMLContentAssistProcessor;
import com.aptana.ide.editor.html.formatting.HTMLAutoIndentStrategy;
import com.aptana.ide.editor.html.formatting.HTMLBracketInserter;
import com.aptana.ide.editor.html.formatting.HTMLPairTagModifyStrategy;
import com.aptana.ide.editor.html.parsing.HTMLMimeType;
import com.aptana.ide.editor.js.JSContributor;
import com.aptana.ide.editors.formatting.IUnifiedBracketInserter;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.BaseContributor;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.IUnifiedEditorContributor;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.editors.unified.UnifiedReconcilingStrategy;
import com.aptana.ide.editors.unified.colorizer.LanguageColorizer;
import com.aptana.ide.editors.unified.hover.LexemeTextHover;

/**
 * @author Robin Debreuil
 */
public class HTMLContributor extends BaseContributor
{
	/**
	 * _childContributors
	 */
	protected IUnifiedEditorContributor[] childContributors;

	private boolean isDisposing = false;
	private IAutoEditStrategy[] _autoEditStrategy;
	private LexemeTextHover _textHover;
	private LanguageColorizer _colorizer;
	private IUnifiedBracketInserter _inserter;

	/**
	 * HTMLContributor
	 */
	public HTMLContributor()
	{
		this(HTMLMimeType.MimeType);
	}

	/**
	 * HTMLContributer
	 * 
	 * @param mimeType
	 */
	public HTMLContributor(String mimeType)
	{
		super(mimeType);

		this._colorizer = LanguageRegistry.getLanguageColorizer(HTMLMimeType.MimeType);

		this._colorizer = (this._colorizer == null) ? null : this._colorizer; // make compiler happy
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getChildContributors()
	 */
	public IUnifiedEditorContributor[] getChildContributors()
	{
		if (this.childContributors == null)
		{
			JSContributor jsc = new JSContributor();
			CSSContributor cssc = new CSSContributor();

			jsc.setParent(this);
			cssc.setParent(this);

			this.childContributors = new IUnifiedEditorContributor[] { jsc, cssc };
		}

		return this.childContributors;
	}

	/**
	 * Returns an instance of the TextHover class for the specified content type
	 * 
	 * @param sourceViewer
	 *            The current ISourceViewer
	 * @param contentType
	 *            The current content type (generally a mine type such as text/html)
	 * @return A new TextHover class, or null if not relevant.
	 */
	public ITextHover getLocalTextHover(ISourceViewer sourceViewer, String contentType)
	{
		if (contentType.equals(HTMLMimeType.MimeType))
		{
			_textHover = new LexemeTextHover(getFileContext());
			return _textHover;
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.BaseContributor#getLocalAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public IAutoEditStrategy[] getLocalAutoEditStrategies(ISourceViewer sourceViewer, String contentType)
	{
		if (contentType.equals(HTMLMimeType.MimeType))
		{
			IAutoEditStrategy autoIndentStrategy = new HTMLAutoIndentStrategy(this.getFileContext(), this
					.getParentConfiguration(), sourceViewer);

			IAutoEditStrategy pairTagModifyStrategy = new HTMLPairTagModifyStrategy(this.getFileContext(), this
					.getParentConfiguration(), sourceViewer);

			_autoEditStrategy = new IAutoEditStrategy[] { autoIndentStrategy, pairTagModifyStrategy };
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
		if (contentType.equals(HTMLMimeType.MimeType))
		{
			EditorFileContext context = getFileContext();
			if (context != null)
			{
				return new HTMLContentAssistProcessor(context);
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
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getReconcilingStrategy()
	 */
	public UnifiedReconcilingStrategy getReconcilingStrategy()
	{
		return new UnifiedReconcilingStrategy();
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

		if (childContributors != null)
		{
			for (int i = 0; i < childContributors.length; i++)
			{
				IUnifiedEditorContributor child = childContributors[i];
				if (child instanceof BaseContributor)
				{
					((BaseContributor) child).dispose();
				}
			}
		}
		childContributors = null;

		if (_colorizer != null)
		{
			_colorizer.dispose();
			_colorizer = null;
		}

		_autoEditStrategy = null;
		_textHover = null;
		_colorizer = null;

		super.dispose();
	}

	/**
	 * @see com.aptana.ide.editors.unified.BaseContributor#isAutoActivateContentAssist()
	 */
	public boolean isAutoActivateContentAssist()
	{
		return HTMLPlugin.getDefault().getPreferenceStore()
				.getBoolean(IPreferenceConstants.CODE_ASSIST_AUTO_ACTIVATION);
	}

	/**
	 * @see com.aptana.ide.editors.unified.BaseContributor#getLocalBracketInserter(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public IUnifiedBracketInserter getLocalBracketInserter(ISourceViewer sourceViewer, String contentType)
	{
		if (contentType.equals(HTMLMimeType.MimeType))
		{
			EditorFileContext context = getFileContext();
			if (context != null)
			{
				_inserter = new HTMLBracketInserter(sourceViewer, context);
				return _inserter;
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
}
