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
package com.aptana.ide.editor.jscomment;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.source.ISourceViewer;

import com.aptana.ide.editor.jscomment.formatting.JSCommentAutoIndentStrategy;
import com.aptana.ide.editor.jscomment.parsing.JSCommentMimeType;
import com.aptana.ide.editors.unified.BaseContributor;
import com.aptana.ide.editors.unified.LanguageRegistry;
import com.aptana.ide.editors.unified.UnifiedReconcilingStrategy;

/**
 * @author Robin Debreuil
 */
public class JSCommentContributor extends BaseContributor
{
	private IAutoEditStrategy[] _autoEditStrategy;
	private boolean isDisposing = false;
	private JSCommentColorizer _colorizer;

	/**
	 * JSCommentContributor
	 */
	public JSCommentContributor()
	{
		super(JSCommentMimeType.MimeType);
		this._colorizer = new JSCommentColorizer();
		LanguageRegistry.registerLanguageColorizer(JSCommentMimeType.MimeType, this._colorizer);
		this._colorizer = (this._colorizer == null) ? null : this._colorizer; // make compiler happy
	}

	/**
	 * @see com.aptana.ide.editors.unified.IUnifiedEditorContributor#getReconcilingStrategy()
	 */
	public UnifiedReconcilingStrategy getReconcilingStrategy()
	{
		return null;
	}

	/**
	 * @see com.aptana.ide.editors.unified.BaseContributor#getLocalAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String)
	 */
	public IAutoEditStrategy[] getLocalAutoEditStrategies(ISourceViewer sourceViewer, String contentType)
	{
		if (contentType.equals(JSCommentMimeType.MimeType))
		{
			_autoEditStrategy = new IAutoEditStrategy[] { new JSCommentAutoIndentStrategy(this.getFileContext(), this.getParentConfiguration(), sourceViewer) };
			return _autoEditStrategy;
		}
		return null;
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

		super.dispose();
	}
}
