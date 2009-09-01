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
package com.aptana.ide.editor.js;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;

import com.aptana.ide.editor.js.contentassist.JSContentAssistProcessor;
import com.aptana.ide.editor.js.formatting.JSAutoIndentStrategy;
import com.aptana.ide.editor.js.formatting.JSBracketInserter;
import com.aptana.ide.editor.js.parsing.JSMimeType;
import com.aptana.ide.editor.jscomment.JSCommentContributor;
import com.aptana.ide.editor.scriptdoc.ScriptDocContributor;
import com.aptana.ide.editors.formatting.IUnifiedBracketInserter;
import com.aptana.ide.editors.preferences.IPreferenceConstants;
import com.aptana.ide.editors.unified.BaseContributor;
import com.aptana.ide.editors.unified.EditorFileContext;
import com.aptana.ide.editors.unified.IUnifiedEditorContributor;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.editors.unified.UnifiedDoubleClickStrategy;
import com.aptana.ide.editors.unified.UnifiedReconcilingStrategy;
import com.aptana.ide.editors.unified.colorizer.LanguageColorizer;
import com.aptana.ide.editors.unified.hover.LexemeTextHover;

/**
 * @author Robin Debreuil
 */
public class JSContributor extends BaseContributor
{
	private IUnifiedEditorContributor[] childContributors;
	private ITextDoubleClickStrategy _doubleClickStrategy;
	private JSBracketInserter _bracketInserter;
	private SourceViewer _sourceViewer;
	private IAutoEditStrategy[] _autoEditStrategy;
	private LexemeTextHover _lexemeTextHover;
	private UnifiedReconcilingStrategy _reconcilingStrategy;
	private JSContentAssistProcessor _contentAssistProcessor;
	private boolean isDisposing = false;
	private LanguageColorizer _colorizer;
	private JSBracketInserter _inserter;

	/**
	 * JSContributor
	 */
	public JSContributor()
	{
		super(JSMimeType.MimeType);
		this._colorizer = LanguageRegistry.getLanguageColorizer(JSMimeType.MimeType);

		this._colorizer = (this._colorizer == null) ? null : this._colorizer; // make compiler happy
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getChildContributors()
	 */
	public IUnifiedEditorContributor[] getChildContributors()
	{
		if (childContributors == null)
		{
			ScriptDocContributor sdc = new ScriptDocContributor();
			sdc.setParent(this);
			JSCommentContributor jcc = new JSCommentContributor();
			jcc.setParent(this);

			childContributors = new IUnifiedEditorContributor[] { sdc, jcc };
		}
		return childContributors;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getLocalContentAssistProcessor(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public IContentAssistProcessor getLocalContentAssistProcessor(ISourceViewer sourceViewer, String contentType)
	{
		if (contentType.equals(JSMimeType.MimeType))// && getFileLanguageService() instanceof IFileLanguageService)
		{
			_sourceViewer = (SourceViewer) sourceViewer;

			EditorFileContext context = getFileContext();
			// JSFileLanguageService langService =
			// (JSFileLanguageService)service.getLanguageService(JSMimeType.MimeType);
			if (context != null)
			{
				_contentAssistProcessor = new JSContentAssistProcessor(context);
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
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getLocalTextHover(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public ITextHover getLocalTextHover(ISourceViewer sourceViewer, String contentType)
	{
		if (contentType.equals(JSMimeType.MimeType))
		{
			_lexemeTextHover = new LexemeTextHover(getFileContext());
			return _lexemeTextHover;
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getReconcilingStrategy()
	 */
	public UnifiedReconcilingStrategy getReconcilingStrategy()
	{
		_reconcilingStrategy = new UnifiedReconcilingStrategy();
		return _reconcilingStrategy;
	}

	/**
	 * @see com.aptana.ide.editors.unified.BaseContributor#getLocalAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public IAutoEditStrategy[] getLocalAutoEditStrategies(ISourceViewer sourceViewer, String contentType)
	{
		if (contentType.equals(JSMimeType.MimeType))
		{
			_autoEditStrategy = new IAutoEditStrategy[] { new JSAutoIndentStrategy(this.getFileContext(), this
					.getParentConfiguration(), sourceViewer) };
			return _autoEditStrategy;
		}
		return null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getDoubleClickStrategy(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType)
	{

		if (_doubleClickStrategy == null)
		{
			_doubleClickStrategy = new UnifiedDoubleClickStrategy();
		}

		return _doubleClickStrategy;
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

		if (_bracketInserter != null)
		{
			_sourceViewer.removeVerifyKeyListener(_bracketInserter);
			_bracketInserter = null;
		}

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

		_contentAssistProcessor = null;
		_doubleClickStrategy = null;
		_autoEditStrategy = null;
		_sourceViewer = null;
		_reconcilingStrategy = null;
		_lexemeTextHover = null;

		super.dispose();
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#isValidIdentifier(char, int)
	 */
	public boolean isValidIdentifier(char c, int keyCode)
	{
		// TODO: Unicode issue
		return ('a' <= keyCode && keyCode <= 'z') || c == '_' || c == '$';
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#isValidActivationCharacter(char, int)
	 */
	public boolean isValidActivationCharacter(char c, int keyCode)
	{
		return Character.isWhitespace(c) || c == '(' || c == ',';
	}

	/**
	 * @see com.aptana.ide.editors.unified.BaseContributor#isAutoActivateContentAssist()
	 */
	public boolean isAutoActivateContentAssist()
	{
		return JSPlugin.getDefault().getPreferenceStore().getBoolean(IPreferenceConstants.CODE_ASSIST_AUTO_ACTIVATION);
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getLocalBracketInserter(ISourceViewer, String)
	 */
	public IUnifiedBracketInserter getLocalBracketInserter(ISourceViewer sourceViewer, String contentType) {
		if (contentType.equals(JSMimeType.MimeType))
		{
			EditorFileContext context = getFileContext();
			if (context != null)
			{
				_inserter = new JSBracketInserter(sourceViewer, context);
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
