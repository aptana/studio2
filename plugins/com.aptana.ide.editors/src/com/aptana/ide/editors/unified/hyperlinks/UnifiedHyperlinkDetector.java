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
package com.aptana.ide.editors.unified.hyperlinks;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;

import com.aptana.ide.editors.managers.EnvironmentManager;
import com.aptana.ide.editors.unified.IFileLanguageService;
import com.aptana.ide.lexer.Lexeme;
import com.aptana.ide.lexer.LexemeList;
import com.aptana.ide.parsing.ICodeLocation;
import com.aptana.ide.parsing.IOffsetMapper;
import com.aptana.ide.parsing.IRuntimeEnvironment;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 */
public class UnifiedHyperlinkDetector implements IHyperlinkDetector
{

	private static final IHyperlink[] EMPTY = null;

	private IFileLanguageService service;

	/**
	 * Creates a new hyperlink detector
	 * 
	 * @param service -
	 *            file language service
	 */
	public UnifiedHyperlinkDetector(IFileLanguageService service)
	{
		this.service = service;
	}

	/**
	 * @see org.eclipse.jface.text.hyperlink.IHyperlinkDetector#detectHyperlinks(org.eclipse.jface.text.ITextViewer,
	 *      org.eclipse.jface.text.IRegion, boolean)
	 */
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks)
	{
		if (this.service == null)
		{
			return EMPTY;
		}

		IOffsetMapper mapper = this.service.getOffsetMapper();
		if (mapper == null)
		{
			return EMPTY;
		}

		LexemeList lxList = mapper.getLexemeList();
		if (lxList == null)
		{
			return EMPTY;
		}
		Lexeme lx = lxList.getLexemeFromOffset(region.getOffset());
		if (lx == null)
		{
			return EMPTY;
		}

		String mimetype = lx.getLanguage();

		IRuntimeEnvironment env = EnvironmentManager.getEnvironment(mimetype);

		IFileLanguageService langService = this.service.getFileContext().getLanguageService(mimetype);
		if (langService == null || env == null)
		{
			return EMPTY;
		}

		IOffsetMapper langMapper = langService.getOffsetMapper();
		if (langMapper == null)
		{
			return EMPTY;
		}

		ICodeLocation loc = langMapper.findTarget(lx);
		if (loc == null)
		{
			return EMPTY;
		}
		IRegion linkRegion = new Region(lx.offset, lx.length);
		UnifiedSourceHyperlink link = new UnifiedSourceHyperlink(this.service, lx, linkRegion, lx.getText());
		return new IHyperlink[] { link };
	}
}
